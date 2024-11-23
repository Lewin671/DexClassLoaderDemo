package com.dexclassdemo.liuguangli.dexclassloaderdemo.dex.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DexUtils {
    private static final String TAG = "DexUtils";
    private static String currentInstructionSet = null;
    public static final String ODEX_SUFFIX = ".odex";
    public static final String VDEX_SUFFIX = ".vdex";

    public static boolean isNewerOrEqualThanVersion(int apiLevel) {
        return Build.VERSION.SDK_INT >= apiLevel;
    }

    public static String getCurrentInstructionSet() {
        if (currentInstructionSet != null) {
            return currentInstructionSet;
        }

        try {
            Class<?> clazz = Class.forName("dalvik.system.VMRuntime");
            Method currentGet = clazz.getDeclaredMethod("getCurrentInstructionSet");
            currentGet.setAccessible(true);
            currentInstructionSet = (String) currentGet.invoke(null);
        } catch (Throwable ignored) {
            switch (Build.CPU_ABI) {
                case "armeabi":
                case "armeabi-v7a":
                    currentInstructionSet = "arm";
                    break;
                case "arm64-v8a":
                    currentInstructionSet = "arm64";
                    break;
                case "x86":
                    currentInstructionSet = "x86";
                    break;
                case "x86_64":
                    currentInstructionSet = "x86_64";
                    break;
                case "mips":
                    currentInstructionSet = "mips";
                    break;
                case "mips64":
                    currentInstructionSet = "mips64";
                    break;
                default:
                    Log.e(TAG, "Unsupported abi: " + Build.CPU_ABI);
                    currentInstructionSet = "unknown";
            }
        }
        LogUtils.i(TAG, "getCurrentInstructionSet:" + currentInstructionSet);
        return currentInstructionSet;
    }

    public static boolean is32BitEnv() {
        final String currISA = getCurrentInstructionSet();
        return "arm".equals(currISA) || "x86".equals(currISA) || "mips".equals(currISA);
    }

    public static boolean isLegalFile(@Nullable File file) {
        return file != null && file.exists() && file.canRead() && file.isFile() && file.length() > 0;
    }

    @Nullable
    public static File getOptimizedDexFile(@NonNull File dexFile) {
        // dex_location = /xxx/test.jar
        // odex_location = /xxx/oat/<isa>/test.odex

        String currentInstructionSet = getCurrentInstructionSet();
        if (currentInstructionSet == null) {
            return null;
        }

        File parentFile = dexFile.getParentFile();
        if (parentFile == null) {
            return null;
        }

        String fileName = dexFile.getName();
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            fileName = fileName.substring(0, index);
        }

        File oatDir = new File(parentFile, "oat");
        File instructionSetDir = new File(oatDir, currentInstructionSet);
        return new File(instructionSetDir, fileName + ODEX_SUFFIX);
    }
}
