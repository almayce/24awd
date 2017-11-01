package io.almayce.dev.app24awd.view

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import com.arellomobile.mvp.MvpAppCompatActivity
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
            if (path == null) send(false) else send(true)
        })
btAddPhoto.setOnClickListener({
    getPhoto()
})
    }

    fun initActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private val CAMERA_PHOTO = 111
    private var path: Uri? = null

    fun getPhoto() {
        var intentCapture = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "temp")
        path = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                path)

        startActivityForResult(intentCapture, CAMERA_PHOTO);
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

    fun send(withPhoto: Boolean) {
        var email = "info@24awd.com"
        var subject = "Заявка: ${etName.text.toString()}"
        var text = "Телефон: ${etPhone.text.toString()}\n" +
                "Автомобиль: ${CarList.get(SelectedCar.index).model}\n" +
                "VIN: ${CarList.get(SelectedCar.index).vin}\n" +
                "Объем двигателя: ${CarList.get(SelectedCar.index).engineCapacity}\n" +
                "Мощность: ${CarList.get(SelectedCar.index).enginePower}\n" +
                "Год выпуска: ${CarList.get(SelectedCar.index).year}\n" +
                "Общий пробег: ${CarList.get(SelectedCar.index).replaceMileage}\n\n" +
                "Сообщение: ${etMessage.text.toString()}"


        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "application/image"
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf<String>(email))
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text)
        if (withPhoto) emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        startActivity(Intent.createChooser(emailIntent, "Send to $email via:"))
    }
}