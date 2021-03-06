package io.almayce.dev.app24awd.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.almayce.dev.app24awd.*
import io.almayce.dev.app24awd.model.docs.Doc
import java.io.FileNotFoundException
import java.util.*

/**
 * Created by almayce on 22.09.17.
 */
class DocsRecyclerViewAdpater(val context: Context, var list: ArrayList<Doc>) : RecyclerView.Adapter<DocsRecyclerViewAdpater.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var clickListener: ItemClickListener? = null
    private var longClickListener: ItemLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_doc, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the textview in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val target = list[position]
        val (title, endDate, photoPath) = target
        with(holder){
            tvTitle.text = title
            tvReplace.text = dateFormat(endDate)
            ivPhoto.setImageResource(R.color.colorAccent)
            if (photoPath != "") {
                try {
                    ivPhoto.setImageURI(Uri.parse(photoPath))
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }

    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    private fun dateFormat(replaceDate: Long): Str {
        val sdf = SDF("dd / MM / yy", Locale.ROOT)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(replaceDate)
    }

    override fun getItemCount(): Int = list.size

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val tvTitle: TV = itemView.findViewById(R.id.tvTitle)
        val ivPhoto: IV =itemView.findViewById(R.id.ivPhoto)
        val tvReplace: TV = itemView.findViewById(R.id.tvReplace)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(view: View) {
            if (clickListener != null) clickListener!!.onItemClick(view, adapterPosition)
        }

        override fun onLongClick(view: View): Bool {
            view.contentDescription = tvTitle.text.toString()
            if (longClickListener != null) longClickListener!!.onItemLongClick(view, adapterPosition)
            return true
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): Str? = null

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener) {
        this.clickListener = itemClickListener
    }

    fun setLongClickListener(itemLongClickListener: ItemLongClickListener) {
        this.longClickListener = itemLongClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface ItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }
}