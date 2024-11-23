package com.dexclassdemo.liuguangli.dexclassloaderdemo.dex;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.dexclassdemo.liuguangli.dexclassloaderdemo.dex.utils.DexUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DexOptimizerN implements IDexOptimizer {
    private static @NonNull ProcessBuilder getProcessBuilder(@NonNull File dexFile, File optimizedDexFile, String isa) {
        final List<String> commandAndParams = new ArrayList<>();
        commandAndParams.add("dex2oat");
        // for 7.1.1, duplicate class fix
        if (Build.VERSION.SDK_INT >= 24) {
            commandAndParams.add("--runtime-arg");
            commandAndParams.add("-classpath");
            commandAndParams.add("--runtime-arg");
            commandAndParams.add("&");
        }
        commandAndParams.add("--dex-file=" + dexFile.getAbsolutePath());
        commandAndParams.add("--oat-file=" + optimizedDexFile.getAbsolutePath());
        commandAndParams.add("--instruction-set=" + isa);
        commandAndParams.add("--compiler-filter=speed-profile");
        return new ProcessBuilder(commandAndParams);
    }

    @Override
    public void optimize(@NonNull Context context, @NonNull File dexFile, @NonNull DexOptimizeCallback callback) {
        File optimizedDexFile = DexUtils.getOptimizedDexFile(dexFile);
        String isa = DexUtils.getCurrentInstructionSet();
        if (optimizedDexFile == null || isa == null) {
            callback.onFailed(dexFile, "optimizedDexFile or isa is null");
            return;
        }
        final ProcessBuilder pb = getProcessBuilder(dexFile, optimizedDexFile, isa);
        try {
            Process dex2oatProcess = pb.start();
            final int ret = dex2oatProcess.waitFor();
            if (ret != 0) {
                callback.onFailed(dexFile, "dex2oat works unsuccessfully, exit code: " + ret);
            }
            callback.onSuccess(dexFile);
        } catch (Exception e) {
            callback.onFailed(dexFile, "cmd exe failed: " + e.getMessage());
        }
    }
}
