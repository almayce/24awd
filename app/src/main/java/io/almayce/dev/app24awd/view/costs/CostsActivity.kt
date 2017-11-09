package io.almayce.dev.app24awd.view.costs

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import io.almayce.dev.app24awd.Bool
import io.almayce.dev.app24awd.LLM
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.Str
import io.almayce.dev.app24awd.adapter.CostsRecyclerViewAdpater
import io.almayce.dev.app24awd.presenter.CostsPresenter
import kotlinx.android.synthetic.main.app_bar_addcar.*
import kotlinx.android.synthetic.main.content_costs.*


/**
 * Created by almayce on 26.09.17.
 */
class CostsActivity : MvpAppCompatActivity(), CostsView, CostsRecyclerViewAdpater.ItemClickListener, CostsRecyclerViewAdpater.ItemLongClickListener {

    @InjectPresenter
    lateinit var pr: CostsPresenter
    private lateinit var costsAdapter: CostsRecyclerViewAdpater
    private val control = CostsActivityControl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_costs)
        setSupportActionBar(toolbar)
        initActionBar()
        getAdapter()
        fabAdd.setOnClickListener({ control.dialogAddCost() })
    }

    override fun onCreateOptionsMenu(menu: Menu): Bool {
        menuInflater.inflate(R.menu.cost, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Bool {
        with(control) {
            when (item.itemId) {
                R.id.action_cost -> dialogFilterCost()
                R.id.action_mileage -> dialogFilterCostByMileage()
                else -> onBackPressed()
            }
        }
        return true
    }

    override fun notifyAdapter() = costsAdapter.notifyDataSetChanged()

    private fun initActionBar() = with(supportActionBar!!) {
        setDisplayHomeAsUpEnabled(true)
        setDisplayShowHomeEnabled(true)
        title = "${pr.getSelectedCarModel()} - ${pr.getSelectedCarMileage()} км."
    }

    override fun onItemClick(view: View, position: Int) {
        control.showCostsPopupMenu(view, position)
    }

    override fun onItemLongClick(view: View, position: Int) {
        control.showCostsPopupMenu(view, position)
    }

    fun showToast(text: Str) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    override fun onSupportNavigateUp(): Bool {
        onBackPressed()
        return true
    }

    private fun getAdapter() {
        costsAdapter = pr.getCostsRecyclerViewAdpater(this)
        initAdapter()
    }

    fun getFilteredByMileageCostsRecyclerViewAdapter(mileage: Int) {
        costsAdapter = pr.getFilteredByMileageCostsRecyclerViewAdapter(this@CostsActivity, mileage)
        initAdapter()
    }

    fun getFilteredCostsRecyclerViewAdapter(timeInMillis: Long) {
        costsAdapter = pr.getFilteredCostsRecyclerViewAdapter(this@CostsActivity, timeInMillis)
        initAdapter()
    }

    private fun initAdapter() {
        costsAdapter.setClickListener(this)
        with(rvCosts) {
            layoutManager = LLM(this@CostsActivity)
            adapter = costsAdapter
        }
    }

}