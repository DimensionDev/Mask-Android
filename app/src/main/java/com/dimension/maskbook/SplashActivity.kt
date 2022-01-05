package com.dimension.maskbook

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dimension.maskbook.util.getSettings

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        startActivity(Intent(this, ComposeActivity::class.java))
        if (!getSettings("is_intro_shown", false)) {
            startActivity(Intent(this, IntroActivity::class.java))
        } else {
            startActivity(Intent(this, GeckoViewActivity::class.java))
        }
        finish()
    }
}
