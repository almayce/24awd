package io.almayce.dev.app24awd.adapter

import android.content.Context
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.almayce.dev.app24awd.*
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.cars.CarTabParam
import io.almayce.dev.app24awd.model.cars.SelectedCar
import java.io.FileNotFoundException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by almayce on 22.09.17.
 */
class ParamRecyclerViewAdpater(val context: Context, var list: ArrayList<CarTabParam>) : RecyclerView.Adapter<ParamRecyclerViewAdpater.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var clickListener: ItemClickListener? = null
    private var longClickListener: ItemLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_param, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the textview in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val target = list[position]
        val (title, photoPath, replaceDate, replaceMileage, replaceCost, replaceLimit) = target
        val (model, vin, engineCapacity, enginePower, year, createDate, carReplaceDate, carCreateMileage, carReplaceMileage, tabs, costs) = CarList[SelectedCar.index]

        with(holder) {
            tvTitle.text = title
            tvLimit.text = "$replaceLimit км."
            tvCost.text = "$replaceCost руб."
            tvReplace.text = " - "
            tvNextMileageReplace.text = " - "
            tvReplaceDays.text = " - "

            if (photoPath != "")
                try {
                    ivPhoto.setImageURI(Uri.parse(photoPath))
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

            if (replaceMileage > 0) {
                tvReplace.text = "${dateFormat(replaceDate)} - $replaceMileage км."
                tvNextMileageReplace.text = "${(replaceMileage + replaceLimit)} км."
            }

            if (replaceMileage > 0 && carReplaceMileage > 0) {

                val replaceMileage = (replaceMileage + replaceLimit) - carReplaceMileage
                Log.d("replaceMileage", replaceMileage.toString())

                val deltaDate = carReplaceDate - createDate
                Log.d("deltaDate", deltaDate.toString())

                val deltaMileage = carReplaceMileage - carCreateMileage
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

                llNextReplace.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentTrans))
                tvNextReplace.text = "Замена через:"
                tvReplaceDays.text = "$replaceMileage км.\n" +
                                "$daysLeft дн.\n" +
                                "($nextReplaceDate)"

                val deltaMillis = Math.abs(System.currentTimeMillis() - replaceDate)
                val dayInMillis = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
                if (deltaMillis < dayInMillis || daysLeft <= 0)
                    tvReplaceDays.text = "$replaceMileage км."

                if (replaceMileage < 500) {
                    llNextReplace.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentSecondTrans))
                    tvNextReplace.text = "Произведите замену"
                    tvReplaceDays.text = "!!!"
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
        val ivPhoto: IV = itemView.findViewById(R.id.ivPhoto)
        val tvReplace: TV = itemView.findViewById(R.id.tvReplace)
        val tvNextReplace: TV = itemView.findViewById(R.id.tvNextReplace)
        val llNextReplace: LinearLayout = itemView.findViewById(R.id.llNextReplace)
        val tvCost: TV = itemView.findViewById(R.id.tvCost)
        val tvLimit: TV = itemView.findViewById(R.id.tvLimit)
        val tvReplaceDays: TV = itemView.findViewById(R.id.tvReplaceDays)
        val tvNextMileageReplace: TV = itemView.findViewById(R.id.tvNextMilleageReplace)

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