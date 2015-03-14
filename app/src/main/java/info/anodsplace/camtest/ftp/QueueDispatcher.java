/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.anodsplace.camtest.ftp;

import android.os.Process;

import org.apache.commons.net.ftp.FTPClient;

import java.util.concurrent.BlockingQueue;


public class QueueDispatcher extends Thread {
    /** The queue of requests to service. */
    private final BlockingQueue<FtpRequest> mQueue;
    /** The network interface for processing requests. */
    private final FtpConnection mFtpConnection;
    /** For posting responses and errors. */
    private final FtpDelivery mDelivery;
    /** Used for telling us to die. */
    private volatile boolean mQuit = false;

    public QueueDispatcher(BlockingQueue<FtpRequest> queue,
                           FtpConnection connection,
                           FtpDelivery delivery) {
        mQueue = queue;
        mFtpConnection = connection;
        mDelivery = delivery;
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            //long startTimeMs = SystemClock.elapsedRealtime();
            FtpRequest request;
            try {
                // Take a request from the queue.
                request = mQueue.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
                // If the request was cancelled already, do not perform the
                // network request.
                if (request.isCanceled()) {
                    request.finish();
                    continue;
                }

//                TrafficStats.setThreadStatsTag(request.getTrafficStatsTag());

                // Perform the network request.
                mFtpConnection.upload(request);

                // Post the response back.
                request.markDelivered();
                mDelivery.postDelivered(request);
            } catch (Exception e) {
                FtpError error = new FtpError(e.toString(), e);
                mDelivery.postError(request, error);
            }
        }
    }

}
