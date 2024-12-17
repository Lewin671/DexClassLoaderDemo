package com.dexclassdemo.liuguangli.dexclassloaderdemo

import android.util.Log

object Test {
    @JvmStatic
    fun finishTest() {
        JIniterface { msg ->
            Log.e("Test", "hello $msg")
            throw Exception("unknown error")
        }.finish111("world")
    }
}