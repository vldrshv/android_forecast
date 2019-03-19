package com.example.vldrshv.forecast.animations

import android.os.Handler

class MainAnimation {
    var delay: Long = 100
    
    fun start(func: () -> Unit) =
            startAfterDelay(delay, func)
    
    fun start(_delay: Long, func: () -> Unit) =
            startAfterDelay(_delay, func)
    
    private fun startAfterDelay(delay: Long, func: () -> Unit) {
        Handler().postDelayed({
            try {
                func()
            } catch (e: Exception) {
            }
        }, delay)
    }
    
}