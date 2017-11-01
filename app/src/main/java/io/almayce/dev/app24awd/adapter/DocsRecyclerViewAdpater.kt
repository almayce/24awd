package io.almayce.dev.app24awd.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.model.docs.Doc
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by almayce on 22.09.17.
 */
class DocsRecyclerViewAdpater(val context: Context, var list: ArrayList<Doc>) : RecyclerView.Adapter<DocsRecyclerViewAdpater.ViewHolder>() {

    private val inflater: LayoutInflater
    private var clickListener: ItemClickListener? = null
    private var longClickListener: ItemLongClickListener? = null

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_doc, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the textview in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val target = list.get(position)
        holder.tvTitle.text = target.title
        holder.tvReplace.text = dateFormat(target.endDate)
        holder.ivPhoto.setImageResource(R.color.colorAccent)
        if (target.photoPath != "") {
            try {
                holder.ivPhoto.setImageURI(Uri.parse(target.photoPath))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    fun dateFormat(replaceDate: Long): String {
        val sdf = SimpleDateFormat("dd / MM / yy", Locale.ROOT)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(replaceDate)
    }

    override fun getItemCount(): Int = list.size

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        var tvTitle: TextView
        var ivPhoto: ImageView
        var tvReplace: TextView

        init {
            tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
            ivPhoto = itemView.findViewById<ImageView>(R.id.ivPhoto)
            tvReplace = itemView.findViewById<TextView>(R.id.tvReplace)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(view: View) {
            if (clickListener != null) clickListener!!.onItemClick(view, adapterPosition)
        }

        override fun onLongClick(view: View): Boolean {
            view.contentDescription = tvTitle.text.toString()
            if (longClickListener != null) longClickListener!!.onItemLongClick(view, adapterPosition)
            return true
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): String? = null

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