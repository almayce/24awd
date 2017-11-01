package io.almayce.dev.app24awd.view.docs

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
import io.almayce.dev.app24awd.adapter.DocsRecyclerViewAdpater
import io.almayce.dev.app24awd.model.docs.DocList
import io.almayce.dev.app24awd.presenter.DocsPresenter
import kotlinx.android.synthetic.main.app_bar_docs.*
import kotlinx.android.synthetic.main.content_docs.*

/**
 * Created by almayce on 30.10.17.
 */
class DocsActivity : MvpAppCompatActivity(),
        DocsView,
        DocsRecyclerViewAdpater.ItemClickListener,
        DocsRecyclerViewAdpater.ItemLongClickListener {

    @InjectPresenter
    lateinit var pr: DocsPresenter
    lateinit var control: DocsActivityControl

    private lateinit var adapter: DocsRecyclerViewAdpater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docs)
        setSupportActionBar(toolbar)
        initActionBar()
        initAdapter()
        init()
        fabAdd.setOnClickListener({ control.addDocDialog() })
    }

    private fun init() {
        control = DocsActivityControl(this)
    }

    fun initActionBar() {
        supportActionBar?.title = "Документы"
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }

    fun initAdapter() {
        rvDocs.layoutManager = LinearLayoutManager(this)
        adapter = DocsRecyclerViewAdpater(this, DocList)
        adapter.setClickListener(this)
        adapter.setLongClickListener(this)
        rvDocs.adapter = adapter
    }

    fun showPopupMenu(context: Context, v: View, position: Int): Boolean {

        val popupMenu = PopupMenu(context, v)
        popupMenu.inflate(R.menu.popupmenu_param)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {

            override fun onMenuItemClick(item: MenuItem): Boolean {
                when (item.getItemId()) {
//                    R.id.menu_replace -> replaceTabParamDialog(position)
                    R.id.menu_edit -> control.editDocDialog(position)
                    R.id.menu_photo -> getPhoto(position)
                    R.id.menu_remove -> control.removeDocDialog(position)
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
            val currentParam = pr.getCurrentDoc(pos)
//            val path = intent.getStringExtra("path")
            currentParam.photoPath = path.toString()
            var projection = arrayOf<String>(MediaStore.Images.Media.DATA);
            var cursor = getContentResolver().query(path, projection, null, null, null);
            var index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            var capturedImageFilePath = cursor.getString(index);
            cursor.close()
            pr.serialize()
            pr.transformBitmap(capturedImageFilePath)
        }
    }

    fun showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    override fun onItemClick(view: View, position: Int) {
        showPopupMenu(this, view, position)
    }

    override fun onItemLongClick(view: View, position: Int) {
        showPopupMenu(this, view, position)
    }

    override fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}