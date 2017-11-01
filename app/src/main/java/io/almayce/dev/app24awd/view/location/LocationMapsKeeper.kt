package io.almayce.dev.app24awd.view.location

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder


/**
 * Created by almayce on 27.10.17.
 */
class LocationMapsKeeper(var activity: LocationActivity) {

    var pref: SharedPreferences
    var gb: GsonBuilder
    var gson: Gson

    init {
        pref = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        gb = GsonBuilder()
        gson = gb.create()
    }

    fun keep(c: Any, key: String): Boolean {
        try {
            val json = gson.toJson(c)
            val editor = pref.edit()
            editor.putString(key, json)
            editor.apply()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun load(key: String): Any {
        val json = pref.getString(key, "")
        if (json != "")
            return gson.fromJson(json, Any::class.java)
        else throw KotlinNullPointerException()
    }
}