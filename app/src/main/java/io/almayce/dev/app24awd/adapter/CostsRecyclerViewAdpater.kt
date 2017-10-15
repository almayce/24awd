package io.almayce.dev.app24awd.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.model.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by almayce on 22.09.17.
 */
class CostsRecyclerViewAdpater(val context: Context, var list: ArrayList<CarCost>) : RecyclerView.Adapter<CostsRecyclerViewAdpater.ViewHolder>() {

    private val inflater: LayoutInflater
    private var clickListener: ItemClickListener? = null
    private var longClickListener: ItemLongClickListener? = null

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_costs, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the textview in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val target = list.get(position)
        holder.tvTitle.text = target.title
        holder.tvMileage.text = "${target.mileage} км."
        holder.tvComment.text = target.comment
        holder.tvDate.text = dateFormat(target.date)

//        val replaceMileage = (target.mileage + target.limit) - CarList.get(SelectedCar.index).replaceMileage
//        val replaceDays = replaceMileage / CarList.get(SelectedCar.index).dayMileage

//        if (target.limit > 0)
//            holder.tvReplace.text = "Замена через ${replaceDays} дн. (${target.mileage + target.limit} км.)"
//        else holder.tvReplace.text = ""
        holder.tvPrice.text = "Цена ${target.price} руб."
    }

//    fun calculateReplaceDaysLeft(limit: Int, date: Long): Int {
//        val totalDays = (limit / CarList.get(SelectedCar.index).dayMileage)
//        val totalMillis = TimeUnit.MILLISECONDS.convert(totalDays.toLong(), TimeUnit.DAYS);
//        val currentMillis = System.currentTimeMillis()
//        val millis = totalMillis - (currentMillis - date)
//        return TimeUnit.DAYS.convert(millis, TimeUnit.MILLISECONDS).toInt()
//    }

    fun dateFormat(replaceDate: Long): String {
        val sdf = SimpleDateFormat("dd / MM / yy", Locale.ROOT)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(replaceDate)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    override fun getItemCount(): Int = list.size

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        var tvTitle: TextView
        var tvMileage: TextView
        var tvComment: TextView
        var tvDate: TextView
//        var tvReplace: TextView
        var tvPrice: TextView

        init {
            tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
            tvMileage = itemView.findViewById<TextView>(R.id.tvMileage)
            tvComment = itemView.findViewById<TextView>(R.id.tvComment)
            tvDate = itemView.findViewById<TextView>(R.id.tvDate)
//            tvReplace = itemView.findViewById<TextView>(R.id.tvReplace)
            tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
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