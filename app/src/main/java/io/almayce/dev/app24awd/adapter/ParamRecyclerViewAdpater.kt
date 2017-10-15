package io.almayce.dev.app24awd.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.model.CarList
import io.almayce.dev.app24awd.model.CarTabParam
import io.almayce.dev.app24awd.model.SelectedCar
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by almayce on 22.09.17.
 */
class ParamRecyclerViewAdpater(val context: Context, var list: ArrayList<CarTabParam>) : RecyclerView.Adapter<ParamRecyclerViewAdpater.ViewHolder>() {

    private val inflater: LayoutInflater
    private var clickListener: ItemClickListener? = null
    private var longClickListener: ItemLongClickListener? = null

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_param, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the textview in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val target = list.get(position)
        holder.tvTitle.text = target.title

        holder.tvLimit.text = "${target.replaceLimit} км."
        holder.tvCost.text = "${target.replaceCost} руб."



//        Log.d("firstReplaceDate", target.firstReplaceDate.toString())
//        Log.d("firstReplaceMileage", target.firstReplaceMileage.toString())
//        Log.d("lastReplaceDate", target.lastReplaceDate.toString())
//        Log.d("lastReplaceMileage", target.lastReplaceMileage.toString())

        val car = CarList.get(SelectedCar.index)

        holder.tvReplace.text = " - "
        holder.tvNextMileageReplace.text = " - "
        holder.tvReplaceDays.text = " - "

        if (target.replaceMileage > 0) {
            holder.tvReplace.text =  "${dateFormat(target.replaceDate)} - ${target.replaceMileage} км."
            holder.tvNextMileageReplace.text = "${(target.replaceMileage + target.replaceLimit)} км."
        }

        if (target.replaceMileage >0 && car.replaceMileage >0) {

            val replaceMileage = (target.replaceMileage + target.replaceLimit) - car.replaceMileage
            Log.d("replaceMileage", replaceMileage.toString())

            val deltaDate = car.replaceDate - car.createDate
            Log.d("deltaDate", deltaDate.toString())

            val deltaMileage = car.replaceMileage - car.createMileage
            Log.d("deltaMileage", deltaMileage.toString())

            var deltaDays = TimeUnit.DAYS.convert(deltaDate, TimeUnit.MILLISECONDS)
            Log.d("deltaDays", deltaDays.toString())

            if (deltaDays < 1) deltaDays = 1
            var avgPerDay = deltaMileage / deltaDays
            if (avgPerDay < 1) avgPerDay = 1
            val daysLeft = replaceMileage / avgPerDay
            Log.d("daysLeft", daysLeft.toString())

            val replaceDate = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(daysLeft, TimeUnit.DAYS)
            val nextReplaceDate = dateFormat(replaceDate)

            if (Math.abs(System.currentTimeMillis() - replaceDate) < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
                    || daysLeft <= 0 || replaceMileage < 0) {
                holder.llNextReplace.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentSecondTrans))
                holder.tvNextReplace.text = "Произведите замену"
                holder.tvReplaceDays.text = "!!!"
            } else {
                holder.llNextReplace.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentTrans))
                holder.tvNextReplace.text = "Замена через:"
                holder.tvReplaceDays.text =
                        "${replaceMileage} км.\n" +
                                "${daysLeft} дн.\n" +
                                "(${nextReplaceDate})"
            }
        }
    }

//    fun avgPerDay(dateList: ArrayList<Long>, mileageList: ArrayList<Int>): Long {
//        val deltaDate = dateList.last() - dateList.get(1)
//        val deltaMileage = mileageList.last() - dateList.get(1)
//        val deltaDays = TimeUnit.DAYS.convert(deltaDate, TimeUnit.MILLISECONDS)
//        return (deltaMileage / deltaDays)
//    }

//    fun calculateReplaceDaysLeft(limit: Int, date: Long): Int {
//        val totalDays = limit / CarList.get(SelectedCar.index).dayMileage
//        val totalMillis = TimeUnit.MILLISECONDS.convert(totalDays.toLong(), TimeUnit.DAYS);
//        val currentMillis = System.currentTimeMillis()
//        val millis = totalMillis - (currentMillis - date)
//        return TimeUnit.DAYS.convert(millis, TimeUnit.MILLISECONDS).toInt()
//    }

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
        var tvReplace: TextView
        var tvNextReplace: TextView
        var llNextReplace: LinearLayout
        var tvCost: TextView
        var tvLimit: TextView
        var tvReplaceDays: TextView
        var tvNextMileageReplace: TextView

        init {
            tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
            tvReplace = itemView.findViewById<TextView>(R.id.tvReplace)
            tvNextReplace = itemView.findViewById<TextView>(R.id.tvNextReplace)
            llNextReplace = itemView.findViewById<LinearLayout>(R.id.llNextReplace)
            tvCost = itemView.findViewById<TextView>(R.id.tvCost)
            tvLimit = itemView.findViewById<TextView>(R.id.tvLimit)
            tvReplaceDays = itemView.findViewById<TextView>(R.id.tvReplaceDays)
            tvNextMileageReplace = itemView.findViewById<TextView>(R.id.tvNextMilleageReplace)
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