package info.anodsplace.camtest.ftp;

import android.net.nsd.NsdServiceInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author alex
 * @date 2015-03-12
 */
public class FtpQueue {

    /** Used for generating monotonically-increasing sequence numbers for requests. */
    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    private FtpConnection mConnection;

    /**
     * The set of all requests currently being processed by this RequestQueue. A Request
     * will be in this set if it is waiting in any queue or currently being processed by
     * any dispatcher.
     */
    private final Set<FtpRequest> mCurrentRequests = new HashSet<FtpRequest>();
    /** The network dispatchers. */
    private QueueDispatcher[] mDispatchers;
    private final FtpDelivery mDelivery;

    public FtpQueue(FtpConnection connection, int threadPoolSize, FtpDelivery delivery) {
        mConnection = connection;
        mDispatchers = new QueueDispatcher[threadPoolSize];
        mDelivery = delivery;
    }

    /** The queue of requests that are actually going out to the network. */
    private final PriorityBlockingQueue<FtpRequest> mRequestQueue =
            new PriorityBlockingQueue<FtpRequest>();

    public void start() {

        stop();  // Make sure any currently running dispatchers are stopped.

        // Create network dispatchers (and corresponding threads) up to the pool size.
        for (int i = 0; i < mDispatchers.length; i++) {
            QueueDispatcher dispatcher = new QueueDispatcher(mRequestQueue, mConnection, mDelivery);
            mDispatchers[i] = dispatcher;
            dispatcher.start();
        }

    }

    public void add(FtpRequest request) {
        // Tag the request as belonging to this queue and add it to the set of current requests.
        request.setRequestQueue(this);
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }

        // Process requests in the order they are added.
        request.setSequence(getSequenceNumber());

        // If the request is uncacheable, skip the cache queue and go straight to the network.
        mRequestQueue.add(request);
    }

    public void cancelAll() {
        synchronized (mCurrentRequests) {
            for (FtpRequest request : mCurrentRequests) {
                request.cancel();
            }
        }
    }

    void finish(FtpRequest request) {
        // Remove from the set of requests currently being processed.
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }
    }

    public void stop()
    {
        for (int i = 0; i < mDispatchers.length; i++) {
            if (mDispatchers[i] != null) {
                mDispatchers[i].quit();
            }
        }
    }


    /**
     * Gets a sequence number.
     */
    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mConnection.disconnect();
            }
        }).start();
    }
}
