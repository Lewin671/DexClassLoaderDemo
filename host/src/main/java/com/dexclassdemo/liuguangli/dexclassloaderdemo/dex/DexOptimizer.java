package com.dexclassdemo.liuguangli.dexclassloaderdemo.dex;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.dexclassdemo.liuguangli.dexclassloaderdemo.dex.utils.DexUtils;

import java.io.File;

public class DexOptimizer implements IDexOptimizer {
    private static final String TAG = "DexOptimizer";
    // this is a singleton
    private static final DexOptimizer sInstance = new DexOptimizer();

    public static DexOptimizer getInstance() {
        return sInstance;
    }

    @Override
    public void optimize(@NonNull Context context, @NonNull File dexFile, @NonNull DexOptimizeCallback callback) {
        try {
            optimizeInternal(context, dexFile, callback);
        } catch (Throwable throwable) {
            callback.onFailed(dexFile, throwable.getMessage());
        }
    }

    private void optimizeInternal(@NonNull Context context, @NonNull File dexFile, @NonNull DexOptimizeCallback callback) {
        if (DexUtils.is32BitEnv()) {
            callback.onFailed(dexFile, "32bit not supported");
            return;
        }

        // only supported on android 8 and above
        if (!DexUtils.isNewerOrEqualThanVersion(Build.VERSION_CODES.N)) {
            callback.onFailed(dexFile, "only supported on android 8 and above, current version is " + Build.VERSION.SDK_INT);
            return;
        }

        // check dexFile is valid or not
        if (!DexUtils.isLegalFile(dexFile)) {
            callback.onFailed(dexFile, "dexFile is invalid");
            return;
        }

        // check if it's on android Q or above
        if (DexUtils.isNewerOrEqualThanVersion(Build.VERSION_CODES.Q)) {
            new DexOptimizerQ().optimize(context, dexFile, callback);
        } else {
            // check if it's on android R or above
            new DexOptimizerN().optimize(context, dexFile, callback);
        }
    }
}
