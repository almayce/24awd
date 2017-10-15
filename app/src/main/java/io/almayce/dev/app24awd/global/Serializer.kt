package io.almayce.dev.app24awd.global

import android.os.Environment
import io.almayce.dev.app24awd.model.CarList
import java.io.*

/**
 * Created by almayce on 25.09.17.
 */
object Serializer {

    fun serialize() {
        val dir = File("${Environment.getExternalStorageDirectory()}/24awd")
        if (!dir.exists())
            dir.mkdirs()

        val file = File(dir, "ser.out")
        if (!file.exists())
            file.createNewFile()

        val oos = ObjectOutputStream(FileOutputStream(file))
        oos.use { oos.writeObject(CarList) }
    }

    @Throws (InvalidClassException::class)
    fun deserialize()  {
        val dir = File("${Environment.getExternalStorageDirectory()}/24awd")
        val file = File(dir, "ser.out")
        val ois = ObjectInputStream(FileInputStream(file))
        CarList.addAll(ois.readObject() as CarList)
        println(CarList.size)
        ois.close()
    }
}