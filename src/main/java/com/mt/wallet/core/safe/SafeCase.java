package com.mt.wallet.core.safe;

import android.support.annotation.Keep;

/**
 * Created by sai on 2018/6/11.
 */
@Keep
public class SafeCase {

    private final int id = sequence; //never change field name and type, 'cause jni gonna need them
    private static int sequence;

    static{
        sequence = 0;
        System.loadLibrary("SafeCase");
    }

    public SafeCase(){

        sequence += 1;
        initialize();
    }

    @Override
    protected void finalize() throws Throwable {
        uninitialize();
        super.finalize();
    }

    public synchronized String value(char[] idxAry){
        return valueJni(idxAry);
    }

    public synchronized void delete(int idx){
        deleteJni(idx);
    }

    public synchronized int put(char c){
        return putJni(c);
    }

    public synchronized void initialize(){
        initializeJni();
    }

    public synchronized void uninitialize(){
        uninitializeJni();
    }


    public native String valueJni(char[] idxAry);

    public native void deleteJni(int idx);

    public native int putJni(char c);

    public native void initializeJni();

    public native void uninitializeJni();


}
