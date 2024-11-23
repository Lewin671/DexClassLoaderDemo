package com.dexclassdemo.liuguangli.dexclassloaderdemo.dex;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;

public interface IDexOptimizer {

    /**
     * @param context  application context
     * @param dexFile  dex file to be optimized
     * @param callback callback for optimization progress
     */
    void optimize(@NonNull Context context, @NonNull File dexFile, @NonNull DexOptimizeCallback callback);
}
