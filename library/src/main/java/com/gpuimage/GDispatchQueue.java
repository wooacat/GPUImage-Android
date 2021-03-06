package com.gpuimage;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by j on 17/12/2017.
 */

public class GDispatchQueue {
    private HandlerThread mThread;
    private Handler mHandler;

    public GDispatchQueue(String name) {
        mThread = new HandlerThread(name);
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    public long getThreadId() {
        return mThread.getId();
    }

    public void release() {
        mThread.quit();
    }

    public void dispatch(Runnable runnable) {
        mHandler.post(runnable);
    }

    public void dispatchSyn(Runnable runnable) {
        new BlockingRunnable(runnable).postAndWait(mHandler);
    }

    private static final class BlockingRunnable implements Runnable {
        private final Runnable mTask;
        private boolean mDone;

        public BlockingRunnable(Runnable task) {
            mTask = task;
        }

        @Override
        public void run() {
            try {
                mTask.run();
            } finally {
                synchronized (this) {
                    mDone = true;
                    notifyAll();
                }
            }
        }

        public boolean postAndWait(Handler handler) {
            if (!handler.post(this)) {
                return false;
            }

            synchronized (this) {
                while (!mDone) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
            return true;
        }
    }

    public static void runAsynchronouslyOnVideoProcessingQueue(Runnable runnable) {
        GDispatchQueue videoProcessingQueue = GPUImageContext.sharedContextQueue();
        if (Thread.currentThread().getId() == videoProcessingQueue.getThreadId()) {
            runnable.run();
        } else {
            videoProcessingQueue.dispatch(runnable);
        }
    }

    public static void runSynchronouslyOnVideoProcessingQueue(Runnable runnable) {
        GDispatchQueue videoProcessingQueue = GPUImageContext.sharedContextQueue();
        if (Thread.currentThread().getId() == videoProcessingQueue.getThreadId()) {
            runnable.run();
        } else {
            videoProcessingQueue.dispatchSyn(runnable);
        }
    }

    public static void runAsynchronouslyOnContextQueue(GPUImageContext context, Runnable runnable) {
        GDispatchQueue videoProcessingQueue = context.contextQueue();
        if (Thread.currentThread().getId() == videoProcessingQueue.getThreadId()) {
            runnable.run();
        } else {
            videoProcessingQueue.dispatch(runnable);
        }
    }

    public static void runSynchronouslyOnContextQueue(GPUImageContext context, Runnable runnable) {
        GDispatchQueue videoProcessingQueue = context.contextQueue();
        if (Thread.currentThread().getId() == videoProcessingQueue.getThreadId()) {
            runnable.run();
        } else {
            videoProcessingQueue.dispatchSyn(runnable);
        }
    }
}
