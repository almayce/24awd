package io.almayce.dev.app24awd.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.almayce.dev.app24awd.*
import io.almayce.dev.app24awd.model.cars.CarCost
import java.util.*

/**
 * Created by almayce on 22.09.17.
 */
class CostsRecyclerViewAdpater(val context: Context, var list: ArrayList<CarCost>) : RecyclerView.Adapter<CostsRecyclerViewAdpater.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var clickListener: ItemClickListener? = null
    private var longClickListener: ItemLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_costs, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val target = list[position]
        val (title, mileage, date, limit, price , comment) = target
        with(holder) {
            tvTitle.text = title
            tvMileage.text = "$mileage км."
            tvComment.text = comment
            tvDate.text = dateFormat(date)
            tvPrice.text = "Цена $price руб."
        }
    }

    private fun dateFormat(replaceDate: Long): Str {
        val sdf = SDF("dd / MM / yy", Locale.ROOT)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(replaceDate)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val tvTitle: TV = itemView.findViewById(R.id.tvTitle)
        val tvMileage: TV = itemView.findViewById(R.id.tvMileage)
        val tvComment: TV = itemView.findViewById(R.id.tvComment)
        val tvDate: TV = itemView.findViewById(R.id.tvDate)
        val tvPrice: TV = itemView.findViewById(R.id.tvPrice)

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