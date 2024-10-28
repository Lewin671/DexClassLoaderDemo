package com.dexclassdemo.liuguangli.apkbeloaded;

import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

/**
 * Created by liuguangli on 16/2/13.
 */
public class ClassToBeLoad {
    public void method() {
        Log.v("ClassToBeLoad", "called method of class " + ClassToBeLoad.class.getName());
        Log.v("ClassToBeLoad", DiskLruCache.class.getName());
    }
}
