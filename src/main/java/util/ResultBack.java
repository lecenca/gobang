package util;

import java.util.concurrent.locks.LockSupport;

public class ResultBack {

    private Thread thread;

    public ResultBack(){}

    public void await(){
        thread = Thread.currentThread();
        LockSupport.park(thread);
    }

    public void signal(){
        LockSupport.unpark(thread);
    }
}
