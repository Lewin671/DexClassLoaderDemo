package com.dexclassdemo.liuguangli.dexclassloaderdemo;

import com.tencent.tinker.loader.app.TinkerApplication;

public class MyApplication extends TinkerApplication {
    protected MyApplication(int tinkerFlags) {
        super(tinkerFlags);
    }

    public MyApplication() {
        this(1);
    }
}
