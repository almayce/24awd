package io.almayce.dev.app24awd.view

import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import io.almayce.dev.app24awd.Bool
import io.almayce.dev.app24awd.R
import kotlinx.android.synthetic.main.app_bar_dtp.*

/**
 * Created by almayce on 26.09.17.
 */

class DtpActivity : MvpAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dtp)
        setSupportActionBar(toolbar)
            initActionBar()
    }

    private fun initActionBar() = with(supportActionBar!!) {
        setDisplayHomeAsUpEnabled(true)
        setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Bool {
        onBackPressed()
        return true
    }
}