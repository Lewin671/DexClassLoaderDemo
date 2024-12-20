package com.dexclassdemo.liuguangli.dexclassloaderdemo;


import android.os.Bundle;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dexclassdemo.liuguangli.R;

import java.io.File;

import java.io.FileOutputStream;
import java.io.InputStream;


import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {
    private static final String DIR_NAME = "plugins";
    //    private static final String FILE_NAME = "bundle-debug.apk";
    private static final String FILE_NAME = "test.apk";

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
        Toast.makeText(this, "类加载" + (success ? "成功" : " 失败") + ", 耗时: " + cost + "ms", Toast.LENGTH_SHORT).show();
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
