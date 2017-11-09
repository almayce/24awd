package io.almayce.dev.app24awd.view.tabs

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.*
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import io.almayce.dev.app24awd.Bool
import io.almayce.dev.app24awd.LLM
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.Str
import io.almayce.dev.app24awd.adapter.ParamRecyclerViewAdpater
import io.almayce.dev.app24awd.global.Notificator
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.cars.CarTab
import io.almayce.dev.app24awd.presenter.TabsPresenter
import kotlinx.android.synthetic.main.app_bar_tab.*
import kotlinx.android.synthetic.main.content_tab.*

/**
 * Created by almayce on 22.09.17.
 */
class TabsActivity : MvpAppCompatActivity(),
        TabsView,
        ParamRecyclerViewAdpater.ItemClickListener,
        ParamRecyclerViewAdpater.ItemLongClickListener {

    @InjectPresenter
    lateinit var pr: TabsPresenter

private lateinit var control: TabsActivityControl
    private lateinit var notificator: Notificator
    private lateinit var paramAdapter: ParamRecyclerViewAdpater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)
        setSupportActionBar(toolbar)

        with(intent) {
            pr.carIndex = getIntExtra("carIndex", 0)
            pr.tabIndex = getIntExtra("tabIndex", 0)
        }

        val currentTab = CarList[pr.carIndex].tabs[pr.tabIndex]
        initActionBar(currentTab)
        initAdapter(currentTab)
        init()
        fabAdd.setOnClickListener({ control.addTabParamDialog() })
    }

    private fun init() {
        control = TabsActivityControl(this)
        notificator = Notificator()
    }

    private fun initActionBar(currentTab: CarTab) = with(supportActionBar!!) {
        setDisplayHomeAsUpEnabled(true)
        setDisplayShowHomeEnabled(true)
        title = "${pr.getSelectedCarModel()} - ${pr.getSelectedCarMileage()} км."
    }

    private fun initAdapter(currentTab: CarTab) {
        paramAdapter = ParamRecyclerViewAdpater(this, currentTab.params)
        with(paramAdapter) {
            setClickListener(this@TabsActivity)
            setLongClickListener(this@TabsActivity)
        }
        with(rvParams) {
            layoutManager = LLM(this@TabsActivity)
            adapter = paramAdapter
        }

    }

    private val CAMERA_PHOTO = 111
    private var path: Uri? = null
    private var pos = 0
    fun getPhoto(position: Int) {
        val intentCapture = Intent(ACTION_IMAGE_CAPTURE)
        val contentValues = ContentValues()
        contentValues.put(Images.Media.TITLE, "temp")
        path = contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
        pos = position
//        intentCapture.putExtra("position", position)
        intentCapture.putExtra(EXTRA_OUTPUT,
                path)

        startActivityForResult(intentCapture, CAMERA_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val currentParam = pr.getCurrentParam(pos)
//            val path = intent.getStringExtra("path")
            currentParam.photoPath = path.toString()
            val projection = arrayOf(Images.Media.DATA)
            val cursor = contentResolver.query(path, projection,
                    null,
                    null,
                    null)
            with(cursor) {
                val index = getColumnIndexOrThrow(Images.Media.DATA)
                moveToFirst()
                close()
                with(pr) {
                    saveTabParams()
                    transformBitmap(getString(index))
                }
            }
        }
    }

    fun showToast(text: Str) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    override fun onSupportNavigateUp(): Bool {
        onBackPressed()
        return true
    }

    override fun onItemClick(view: View, position: Int) {
        control.showPopupMenu(this, view, position)
    }

    override fun onItemLongClick(view: View, position: Int) {
        control.showPopupMenu(this, view, position)
    }

    override fun notifyDataSetChanged() = paramAdapter.notifyDataSetChanged()
}