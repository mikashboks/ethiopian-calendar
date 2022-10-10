package com.mkb.ethiopian.sample

import android.app.Application
import com.yariksoffice.lingver.Lingver

class CustomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Lingver.init(this)
    }
}