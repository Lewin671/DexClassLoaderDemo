package com.dexclassdemo.liuguangli.dexclassloaderdemo;


import android.os.Bundle;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dexclassdemo.liuguangli.R;
import com.dexclassdemo.liuguangli.dexclassloaderdemo.dex.DexOptimizeCallback;
import com.dexclassdemo.liuguangli.dexclassloaderdemo.dex.DexOptimizer;
import com.dexclassdemo.liuguangli.dexclassloaderdemo.dex.utils.DexUtils;
import com.dexclassdemo.liuguangli.dexclassloaderdemo.dex.utils.LogUtils;


import java.io.File;

import java.io.FileOutputStream;
import java.io.InputStream;


import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {
    private static final String DIR_NAME = "plugins";
    //    private static final String FILE_NAME = "bundle-debug.apk";
    private static final String FILE_NAME = "core.jar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String apkPath = null;
                    try {
                        apkPath = getApkFilePath();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    loadApk(apkPath);

                    String finalApkPath = apkPath;
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            DexOptimizer.getInstance().optimize(MainActivity.this, new File(finalApkPath), new DexOptimizeCallback() {
                                @Override
                                public void onFailed(@NonNull File dexFile, @Nullable String errorMessage) {
                                    super.onFailed(dexFile, errorMessage);
                                    LogUtils.e("MainActivity", "onFailed: " + errorMessage);
                                }

                                @Override
                                public void onSuccess(@NonNull File dexFile) {
                                    super.onSuccess(dexFile);
                                    LogUtils.e("MainActivity", "dexopt onSuccess: " + dexFile.getAbsolutePath());
                                }
                            });

                        }
                    }.start();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String getApkFilePath() throws Exception {
        File dir = new File(this.getFilesDir(), DIR_NAME);
        File apkFile = new File(dir, FILE_NAME);
        if (apkFile.exists()) {
            apkFile.delete();
        }

        // 确保 dir 目录存在
        dir.mkdirs();

        try (InputStream inputStream = this.getAssets().open(DIR_NAME + "/" + FILE_NAME)) {
            try (FileOutputStream outputStream = new FileOutputStream(apkFile)) {
                byte[] buffer = new byte[2048];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
            }
        }
        return apkFile.getAbsolutePath();
    }

    public void loadApk(String apkPath) {
        Log.v("loadDexClasses", "Dex Preparing to loadDexClasses!");
        long start = SystemClock.uptimeMillis();
        File dexOpt = this.getDir("dexOpt", MODE_PRIVATE);
        final DexClassLoader classloader = new DexClassLoader(apkPath, dexOpt.getAbsolutePath(), null, this.getClassLoader());
        boolean success = false;
        try {
            Class<?> DiskLruCache = (Class<?>) classloader.loadClass("org.chromium.android_webview.AwContents");
            success = true;
        } catch (ClassNotFoundException ignored) {

        }

        long cost = SystemClock.uptimeMillis() - start;
        File optimizedDexFile = DexUtils.getOptimizedDexFile(new File(apkPath));
        boolean exists = optimizedDexFile != null && optimizedDexFile.exists();
        Toast.makeText(this, "类加载" + (success ? "成功" : " 失败") + ", 耗时: " + cost + "ms," + "optFileExists: " + exists, Toast.LENGTH_SHORT).show();
        Log.v("loadDexClasses", "Searching for class costs: " + cost + "ms");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
