package info.anodsplace.camtest.ftp;

import com.android.volley.RequestQueue;

/**
 * @author alex
 * @date 2015-03-12
 */
public class FtpRequest implements Comparable<FtpRequest> {
    /** Sequence number of this request, used to enforce FIFO ordering. */
    private Integer mSequence;

    /** The request queue this request is associated with. */
    private FtpQueue mRequestQueue;

    /** Whether or not this request has been canceled. */
    private boolean mCanceled = false;
    private boolean mResponseDelivered = false;
    private String mLocal;
    private String mRemoteName;

    public FtpRequest(String local, String remote) {
        mLocal = local;
        mRemoteName = remote;
    }


    /**
     * Mark this request as canceled.  No callback will be delivered.
     */
    public void cancel() {
        mCanceled = true;
    }

    /**
     * Returns true if this request has been canceled.
     */
    public boolean isCanceled() {
        return mCanceled;
    }


    /**
     * Mark this request as having a response delivered on it.  This can be used
     * later in the request's lifetime for suppressing identical responses.
     */
    public void markDelivered() {
        mResponseDelivered = true;
    }

    /**
     * Returns true if this request has had a response delivered for it.
     */
    public boolean hasHadResponseDelivered() {
        return mResponseDelivered;
    }

    public void finish() {
        if (mRequestQueue != null) {
            mRequestQueue.finish(this);
        }
    }

    public String getLocal() {
        return mLocal;
    }

    public String getRemoteName() {
        return mRemoteName;
    }


    /**
     * Associates this request with the given queue. The request queue will be notified when this
     * request has finished.
     *
     * @return This Request object to allow for chaining.
     */
    public FtpRequest setRequestQueue(FtpQueue requestQueue) {
        mRequestQueue = requestQueue;
        return this;
    }

    /**
     * Sets the sequence number of this request.  Used by {@link RequestQueue}.
     *
     * @return This Request object to allow for chaining.
     */
    public final FtpRequest setSequence(int sequence) {
        mSequence = sequence;
        return this;
    }

    /**
     * Returns the sequence number of this request.
     */
    public final int getSequence() {
        if (mSequence == null) {
            throw new IllegalStateException("getSequence called before setSequence");
        }
        return mSequence;
    }

    @Override
    public String toString() {
        return (mCanceled ? "[X] " : "[ ] ") + mLocal+" --> "+ mRemoteName + " "+ mSequence;
    }

    @Override
    public int compareTo(FtpRequest other) {
        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
        return this.mSequence - other.mSequence;
    }


}
