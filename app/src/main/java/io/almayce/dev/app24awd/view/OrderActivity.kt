package io.almayce.dev.app24awd.view

import android.content.ContentValues
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.*
import com.arellomobile.mvp.MvpAppCompatActivity
import io.almayce.dev.app24awd.Bool
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.cars.SelectedCar
import kotlinx.android.synthetic.main.app_bar_order.*
import kotlinx.android.synthetic.main.content_order.*

/**
 * Created by almayce on 26.09.17.
 */

class OrderActivity : MvpAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        setSupportActionBar(toolbar)
        initActionBar()
        initUI()
    }

    private fun initUI() {
        fabConfirm.setOnClickListener({
            if (path == null) send(false)
            else send(true)
        })
        btAddPhoto.setOnClickListener({
            getPhoto()
        })
    }

    private fun initActionBar() = with(supportActionBar!!) {
        setDisplayHomeAsUpEnabled(true)
        setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Bool {
        onBackPressed()
        return true
    }

    private val CAMERA_PHOTO = 111
    private var path: Uri? = null

    private fun getPhoto() {
        val intentCapture = Intent(ACTION_IMAGE_CAPTURE)
        val contentValues = ContentValues()
        contentValues.put(Images.Media.TITLE, "temp")
        path = contentResolver.insert(
                Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
        intentCapture.putExtra(EXTRA_OUTPUT,
                path)

        startActivityForResult(intentCapture, CAMERA_PHOTO)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            var projection = arrayOf<String>(MediaStore.Images.Media.DATA);
//            var cursor = getContentResolver().query(path, projection, null, null, null);
//            var index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            cursor.moveToFirst()
//            var capturedImageFilePath = cursor.getString(index);
//            cursor.close()
//            pr.transformBitmap(capturedImageFilePath)
//        }
//    }

    private fun send(withPhoto: Bool) {
        val target = CarList[SelectedCar.index]
        val (model, vin, engineCapacity, enginePower,
                year, createDate, replaceDate,
                createMileage, replaceMileage,
                tabs, costs) = target

        val email = "info@24awd.com"
        val subject = "Заявка: ${etName.text}"
        val text = "Телефон: ${etPhone.text}\n" +
                "Автомобиль: $model\n" +
                "VIN: $vin\n" +
                "Объем двигателя: $engineCapacity\n" +
                "Мощность: $enginePower\n" +
                "Год выпуска: $year\n" +
                "Общий пробег: $replaceMileage\n\n" +
                "Сообщение: ${etMessage.text}"

        val emailIntent = Intent(ACTION_SEND)
        with(emailIntent) {
            type = "application/image"
            putExtra(EXTRA_EMAIL, arrayOf(email))
            putExtra(EXTRA_SUBJECT, subject)
            putExtra(EXTRA_TEXT, text)
            if (withPhoto) putExtra(EXTRA_STREAM, path)
        }
        startActivity(createChooser(
                emailIntent, "Send to $email via:"))
    }
}