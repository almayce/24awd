package io.almayce.dev.app24awd.global

import android.os.Environment
import io.almayce.dev.app24awd.model.docs.DocList
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.view.location.Markers
import java.io.*

/**
 * Created by almayce on 25.09.17.
 */
object Serializer {

    const val APP_FOLDER = "/24awd"

    fun serialize(f: FileName) {
        val dir = File("${Environment.getExternalStorageDirectory()}$APP_FOLDER")
        if (!dir.exists())
            dir.mkdirs()

        val file = File(dir, f.fileName)
        if (!file.exists())
            file.createNewFile()

        val oos = ObjectOutputStream(FileOutputStream(file))
        oos.use {
            when (f) {
                FileName.CARS -> oos.writeObject(CarList)
                FileName.MARKERS -> oos.writeObject(Markers)
                FileName.DOCS -> oos.writeObject(DocList)
            }
        }
    }

    @Throws(InvalidClassException::class)
    fun deserialize(f: FileName) {
        val dir = File("${Environment.getExternalStorageDirectory()}$APP_FOLDER")
        val file = File(dir, f.fileName)
        try {
            val fis = FileInputStream(file)
            val ois = ObjectInputStream(fis)
            ois.use {
                when (f) {
                    FileName.CARS -> CarList.addAll(ois.readObject() as CarList)
                    FileName.MARKERS -> Markers.addAll(ois.readObject() as Markers)
                    FileName.DOCS -> DocList.addAll(ois.readObject() as DocList)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    enum class FileName(var fileName: String) {
        CARS("cars.sr"),
        MARKERS("markers.sr"),
        DOCS("docs.sr");
    }
}