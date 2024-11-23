package com.dexclassdemo.liuguangli.dexclassloaderdemo.dex;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

public class DexOptimizeCallback {

    /**
     * Called when the optimization may succeed
     *
     * @param dexFile the dex file to be optimized
     */
    public void onSuccess(@NonNull File dexFile) {
    }

    /**
     * Called when the optimization fails
     *
     * @param dexFile      the dex file to be optimized
     * @param errorMessage the error message when the optimization fails
     */
    public void onFailed(@NonNull File dexFile, @Nullable String errorMessage) {
    }
}
