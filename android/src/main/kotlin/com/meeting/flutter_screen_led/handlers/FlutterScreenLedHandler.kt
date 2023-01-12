package com.meeting.flutter_screen_led.handlers

import android.content.Context

object FlutterScreenLedHandler {
    private var context: Context? = null

    fun setContext(context: Context?) {
        FlutterScreenLedHandler.context = context
    }

    fun getContext(): Context? {
        return context
    }
}