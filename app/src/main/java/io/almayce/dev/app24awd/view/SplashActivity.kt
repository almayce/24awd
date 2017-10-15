package io.almayce.dev.app24awd.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.view.main.MainActivity
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by almayce on 21.09.17.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        launch(UI) {
            delay(2000L)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }
    }
}