package io.almayce.dev.app24awd.view.tabs

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.adapter.ParamRecyclerViewAdpater
import io.almayce.dev.app24awd.global.Notificator
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
lateinit var control: TabsActivityControl
    lateinit var notificator: Notificator
    private lateinit var adapter: ParamRecyclerViewAdpater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)
        setSupportActionBar(toolbar)
        pr.carIndex = intent.getIntExtra("carIndex", 0)
        pr.tabIndex = intent.getIntExtra("tabIndex", 0)
        val currentTab = CarList.get(pr.carIndex).tabs.get(pr.tabIndex)
        initActionBar(currentTab)
        initAdapter(currentTab)
        init()
        fabAdd.setOnClickListener({ control.addTabParamDialog() })
    }

    private fun init() {
        control = TabsActivityControl(this)
        notificator = Notificator()
    }

    fun initActionBar(currentTab: CarTab) {
        supportActionBar?.title = "${pr.getSelectedCarModel()} - ${pr.getSelectedCarMileage()} км."
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }

    fun initAdapter(currentTab: CarTab) {
        rvParams.layoutManager = LinearLayoutManager(this)
        adapter = ParamRecyclerViewAdpater(this, currentTab.params)
        adapter.setClickListener(this)
        adapter.setLongClickListener(this)
        rvParams.adapter = adapter
    }

    fun showPopupMenu(context: Context, v: View, position: Int): Boolean {

        val popupMenu = PopupMenu(context, v)
        popupMenu.inflate(R.menu.popupmenu_param)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {

            override fun onMenuItemClick(item: MenuItem): Boolean {
                when (item.getItemId()) {
                    R.id.menu_edit -> control.editTabParamDialog(position)
                    R.id.menu_photo -> getPhoto(position)
                    R.id.menu_remind -> showToast(pr.setAlarm(context, position))
                    R.id.menu_remove -> control.removeTabParamDialog(position)
                    else -> return false
                }
                return false
            }
        })
        popupMenu.show()
        return true
    }


    private val CAMERA_PHOTO = 111
    private var path: Uri? = null
    private var pos = 0
    fun getPhoto(position: Int) {
        var intentCapture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "temp")
        path = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
        pos = position
//        intentCapture.putExtra("position", position)
        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                path)

        startActivityForResult(intentCapture, CAMERA_PHOTO);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val currentParam = pr.getCurrentParam(pos)
//            val path = intent.getStringExtra("path")
            currentParam.photoPath = path.toString()
            var projection = arrayOf<String>(MediaStore.Images.Media.DATA);
            var cursor = getContentResolver().query(path, projection, null, null, null);
            var index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            var capturedImageFilePath = cursor.getString(index);
            cursor.close()
            pr.saveTabParams()
            pr.transformBitmap(capturedImageFilePath)
        }
    }

    fun showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onItemClick(view: View, position: Int) {
        showPopupMenu(this, view, position)
    }

    override fun onItemLongClick(view: View, position: Int) {
        showPopupMenu(this, view, position)
    }

    override fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
    }
}