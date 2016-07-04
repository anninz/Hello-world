package com.thq.pat;

import android.util.Log;

public abstract class MyThread extends Thread {  

    private boolean suspend = false;  

    private String mLock = ""; // 只是需要一个对象而已，这个对象没有实际意义  

    public void setSuspend(boolean suspend) {  
        if (!suspend) {  
            synchronized (mLock) {  
                mLock.notifyAll();  
            }  
        }  
        this.suspend = suspend;  
    }  

    public boolean isSuspend() {  
        return this.suspend;  
    }
    
    public String getLock() {
        return mLock;
    }
    
    public void checkSuspend() {
        synchronized (getLock()) {
            if (isSuspend()) {  
                try {
                    Log.e("FxService", "wait");
                    getLock().wait();  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
            }  
        }
    }

/*    public void run() {  
        while (true) {  
            synchronized (mLock) {  
                if (suspend) {  
                    try {
                        Log.e("FxService", "wait");
                        control.wait();  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
            this.runPersonelLogic();  
        }  
    }  

    protected abstract void runPersonelLogic();*/
}
