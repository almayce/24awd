package io.almayce.dev.app24awd.view.docs

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.*
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import io.almayce.dev.app24awd.Bool
import io.almayce.dev.app24awd.LLM
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.Str
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
    private lateinit var control: DocsActivityControl
    private lateinit var docsAdapter: DocsRecyclerViewAdpater

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

    private fun initActionBar() = with(supportActionBar!!) {
        setDisplayHomeAsUpEnabled(true)
        setDisplayShowHomeEnabled(true)
        title = "Документы"
    }

    private fun initAdapter() {
        docsAdapter = DocsRecyclerViewAdpater(this@DocsActivity, DocList)
        with(docsAdapter) {
            setClickListener(this@DocsActivity)
            setLongClickListener(this@DocsActivity)
        }
        with(rvDocs) {
            layoutManager = LLM(this@DocsActivity)
            adapter = docsAdapter
        }
    }

    private fun showPopupMenu(context: Context, v: View, position: Int): Bool {

        val popupMenu = PopupMenu(context, v)
        popupMenu.inflate(R.menu.popupmenu_param)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_edit -> control.editDocDialog(position)
                R.id.menu_photo -> getPhoto(position)
                R.id.menu_remove -> control.removeDocDialog(position)
                else -> return@OnMenuItemClickListener false
            }
            false
        })
        popupMenu.show()
        return true
    }

    private val CAMERA_PHOTO = 111
    private var path: Uri? = null
    private var pos = 0

    private fun getPhoto(position: Int) {
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
            val currentParam = pr.getCurrentDoc(pos)
            currentParam.photoPath = path.toString()
            val projection = arrayOf(Images.Media.DATA)
            val cursor = contentResolver.query(path, projection, null, null, null)
            with(cursor) {
                val index = getColumnIndexOrThrow(Images.Media.DATA)
                moveToFirst()
                val capturedImageFilePath = getString(index)
                close()
                with(pr) {
                    serialize()
                    transformBitmap(capturedImageFilePath)
                }
            }
        }
    }

    fun showToast(text: Str) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    override fun onItemClick(view: View, position: Int) {
        showPopupMenu(this, view, position)
    }

    override fun onItemLongClick(view: View, position: Int) {
        showPopupMenu(this, view, position)
    }

    override fun notifyDataSetChanged() = docsAdapter.notifyDataSetChanged()

    override fun onSupportNavigateUp(): Bool {
        onBackPressed()
        return true
    }
}