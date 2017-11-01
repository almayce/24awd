package io.almayce.dev.app24awd.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.model.cars.CarTab

/**
 * Created by almayce on 21.09.17.
 */
class TabGridViewAdapter(context: Context, var list: ArrayList<CarTab>) : BaseAdapter() {

    override fun getCount(): Int = list.size
    override fun getItem(position: Int): Any = list.get(position)
    override fun getItemId(position: Int): Long = position.toLong()

    var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view =  inflater.inflate(R.layout.item_tab, parent, false)
        view.findViewById<TextView>(R.id.tvItem).setText(list.get(position).title)
        view.findViewById<ImageView>(R.id.ivItem).setImageResource(list.get(position).icon)
        if (position == list.size-1) {
            view = inflater.inflate(R.layout.item_add_tab, parent, false)
            view.contentDescription = "add"
        }

        return view
    }
}