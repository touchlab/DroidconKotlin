package com.google.codelabs.mdc.kotlin.shrine.application

import android.app.Application
import android.content.Context
import android.support.v7.app.AppCompatDelegate

class ShrineApplication : Application() {
    companion object {
        lateinit var instance: ShrineApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

}