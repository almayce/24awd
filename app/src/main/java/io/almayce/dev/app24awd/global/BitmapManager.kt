package io.almayce.dev.app24awd.global

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import io.almayce.dev.app24awd.FOS
import io.almayce.dev.app24awd.Str
import io.reactivex.subjects.PublishSubject
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by almayce on 18.10.17.
 */
class BitmapManager {

    val onTransformedBitmapObservable: PublishSubject<Bitmap>? = PublishSubject.create<Bitmap>()

    fun transformBitmap(path: Str) {
        var preview: Bitmap
        val options = BitmapFactory.Options()

        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        options.inSampleSize = calculateInSampleSize(options, 200, 200)
        options.inJustDecodeBounds = false
        preview = BitmapFactory.decodeFile(path, options)

        var file = File(path)
        if (file.exists()) {
            file.delete()
            file = File(path)
        }

        val stream = FOS(file)
        preview = rotateImage(preview, 90F)
        preview.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.flush()
        stream.close()
        onTransformedBitmapObservable!!.onNext(preview)
    }

    fun getBitmapFromURL(src: Str?): Bitmap? = try {
        val url = URL(src)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input = connection.inputStream
        BitmapFactory.decodeStream(input)
    } catch (e: IOException) {
        null
    }

    private fun calculateInSampleSize(
            options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }

        return inSampleSize
    }

    private fun rotateImage(src: Bitmap, degree: Float): Bitmap {
        // create new matrix
        val matrix = Matrix()
        // setup rotation degree
        matrix.postRotate(degree)
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    fun getBitmapFromPath(path: Str): Bitmap = try {
       BitmapFactory.decodeFile(path)
   } catch (e: IllegalStateException) {
       throw FileNotFoundException()
   }
}