package io.almayce.dev.app24awd.global

import android.os.Environment
import io.almayce.dev.app24awd.*
import io.almayce.dev.app24awd.global.Serializer.FileName.*
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.docs.DocList
import io.almayce.dev.app24awd.view.location.Markers
import java.io.File
import java.io.FileNotFoundException
import java.io.InvalidClassException

/**
 * Created by almayce on 25.09.17.
 */
object Serializer {

    private const val APP_FOLDER = "/24awd"

    fun serialize(f: FileName) {
        val dir = File("${Environment.getExternalStorageDirectory()}$APP_FOLDER")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, f.fileName)
        if (!file.exists()) file.createNewFile()

        OOS(FOS(file)).use {
            when (f) {
                CARS -> it.writeObject(CarList)
                MARKERS -> it.writeObject(Markers)
                DOCS -> it.writeObject(DocList)
            }
        }
    }

    @Throws(InvalidClassException::class)
    fun deserialize(f: FileName) {
        val dir = File("${Environment.getExternalStorageDirectory()}$APP_FOLDER")
        val file = File(dir, f.fileName)
        try {
            OIS(FIS(file)).use {
                when (f) {
                    CARS -> CarList.addAll(it.readObject() as CarList)
                    MARKERS -> Markers.addAll(it.readObject() as Markers)
                    DOCS -> DocList.addAll(it.readObject() as DocList)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    enum class FileName(var fileName: Str) {
        CARS("cars.sr"),
        MARKERS("markers.sr"),
        DOCS("docs.sr");
    }
}