package com.dexclassdemo.liuguangli.dexclassloaderdemo.dex;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dexclassdemo.liuguangli.dexclassloaderdemo.dex.utils.LogUtils;
import com.dexclassdemo.liuguangli.dexclassloaderdemo.dex.utils.ReflectUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.util.Objects;

public class DexOptimizerQ implements IDexOptimizer {
    private static final String TAG = "DexOptimizerQ";
    /**
     * IBinder protocol transaction code: execute a shell command.
     *
     * @hide
     */
    private static final int SHELL_COMMAND_TRANSACTION = ('_' << 24) | ('C' << 16) | ('M' << 8) | 'D';
    private static IBinder sPMBinder = null;
    private static PackageManager sPackageManager = null;

    @Nullable
    private static IBinder getPMBinder() {
        IBinder binder = sPMBinder;
        if (binder != null && binder.isBinderAlive()) {
            return binder;
        }

        try {
            Class<?> clazz = ReflectUtils.findClass("android.os.ServiceManager");
            Object service = ReflectUtils.callStaticMethod(clazz, "getService", new Object[]{"package"}, new Class[]{String.class});
            if (service instanceof IBinder) {
                binder = (IBinder) service;
                sPMBinder = binder;
            }
            return binder;
        } catch (Exception e) {
            LogUtils.e(TAG, "getPMBinder", e);
        }
        return null;

    }

    private static PackageManager getPackageManager(Context context) {
        PackageManager packageManager = sPackageManager;
        if (packageManager != null) {
            return packageManager;
        }

        try {
            final IBinder b = getPMBinder();
            if (b == null) {
                return null;
            }

            // reference: android.app.ActivityThread.getPackageManager
            Class<?> stubClazz = ReflectUtils.findClass("android.content.pm.IPackageManager$Stub");
            Object iPackageManager = ReflectUtils.callStaticMethod(stubClazz, "asInterface", new Object[]{b}, new Class[]{IBinder.class});

            // reference: android.app.ContextImpl.getPackageManager
            // new ApplicationPackageManager(this, pm));
            Class<?> contextImplClazz = ReflectUtils.findClass("android.app.ContextImpl");
            Class<?> applicationPackageManagerClazz = ReflectUtils.findClass("android.app.ApplicationPackageManager");
            Class<?> ipmClazz = ReflectUtils.findClass("android.content.pm.IPackageManager");
            Context contextImpl = context;
            if (context instanceof ContextWrapper) {
                contextImpl = ((ContextWrapper) context).getBaseContext();
            }
            packageManager = (PackageManager) ReflectUtils.newInstance(applicationPackageManagerClazz, new Object[]{contextImpl, iPackageManager}, new Class<?>[]{contextImplClazz, ipmClazz});
            sPackageManager = packageManager;
            return packageManager;
        } catch (Exception e) {
            LogUtils.e(TAG, "getPackageManager", e);
        }

        return null;
    }

    private static boolean registerDexModule(@NonNull Context context, @NonNull File dexFile) {
        PackageManager packageManager = getPackageManager(context);
        if (packageManager == null) {
            return false;
        }

        try {
            // call android.app.ApplicationPackageManager.registerDexModule
            // public void registerDexModule(@NonNull String dexModule,
            //            @Nullable DexModuleRegisterCallback callback)
            Class<?> callbackClazz = ReflectUtils.findClass("android.content.pm.PackageManager$DexModuleRegisterCallback");
            ReflectUtils.callInstanceMethod(packageManager, "registerDexModule", new Object[]{dexFile.getAbsolutePath(), null}, new Class[]{String.class, callbackClazz});
            return true;
        } catch (Exception e) {
            LogUtils.e(TAG, "registerDexModule", e);
        }

        return false;
    }

    private static boolean performDexOpt(@NonNull Context context) {
        String[] args = new String[]{"compile", "-f", "--secondary-dex", "-m", "speed", context.getPackageName()};
        return executeShellCommand(args);
    }

    private static boolean reconcile(@NonNull Context context) {
        // cmd package reconcile-secondary-dex-files
        String[] args = new String[]{"reconcile", context.getPackageName()};
        return executeShellCommand(args);
    }

    // reference: android.os.BinderProxy.shellCommand
    // android.content.pm.IPackageManager.Stub.onTransact
    private static boolean executeShellCommand(String[] args) {
        long lastIdentity = Binder.clearCallingIdentity();
        Parcel data = null;
        Parcel reply = null;
        try {
            data = Parcel.obtain();
            reply = Parcel.obtain();
            data.writeFileDescriptor(FileDescriptor.in);
            data.writeFileDescriptor(FileDescriptor.out);
            data.writeFileDescriptor(FileDescriptor.err);
            data.writeStringArray(args);
            // callback = null
            data.writeStrongBinder(null);
            data.writeStrongBinder(null);
            Objects.requireNonNull(getPMBinder()).transact(SHELL_COMMAND_TRANSACTION, data, reply, 0);
            reply.readException();
            return true;
        } catch (Exception e) {
            LogUtils.e(TAG, "executeShellCommand", e);
        } finally {
            if (data != null) {
                data.recycle();
            }
            if (reply != null) {
                reply.recycle();
            }
            Binder.restoreCallingIdentity(lastIdentity);
        }
        return false;
    }

    @Override
    public void optimize(@NonNull Context context, @NonNull File dexFile, @NonNull DexOptimizeCallback callback) {
        boolean success = reconcile(context); //registerDexModule(context, dexFile);
        if (success) {
            if (performDexOpt(context)) {
                callback.onSuccess(dexFile);
            } else {
                callback.onFailed(dexFile, "performDexOpt failed");
            }
        } else {
            callback.onFailed(dexFile, "registerDexFile failed");
        }
    }
}
