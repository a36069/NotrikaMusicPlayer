package com.abdipor.music1.activities

import android.content.Intent
import android.graphics.Paint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abdipor.appthemehelper.ThemeStore
import com.abdipor.appthemehelper.util.ATHUtil
import com.abdipor.appthemehelper.util.MaterialUtil
import com.abdipor.appthemehelper.util.TintHelper
import com.abdipor.appthemehelper.util.ToolbarContentTintHelper
import com.abdipor.music1.BuildConfig
import com.abdipor.music1.R
import com.abdipor.music1.activities.base.AbsBaseActivity
import com.abdipor.music1.dialogs.UpiPaymentBottomSheetDialogFragment
import com.abdipor.music1.extensions.textColorPrimary
import com.abdipor.music1.extensions.textColorSecondary
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.SkuDetails
import com.anjlab.android.iab.v3.TransactionDetails
import kotlinx.android.synthetic.main.activity_about.toolbar
import kotlinx.android.synthetic.main.activity_donation.*
import java.lang.ref.WeakReference
import java.util.*

class SupportDevelopmentActivity : AbsBaseActivity(), BillingProcessor.IBillingHandler {

    companion object {
        val TAG: String = SupportDevelopmentActivity::class.java.simpleName
        const val DONATION_PRODUCT_IDS = R.array.donation_ids
        private const val TEZ_REQUEST_CODE = 123
    }

    var billingProcessor: BillingProcessor? = null
    private var skuDetailsLoadAsyncTask: AsyncTask<*, *, *>? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun donate(i: Int) {
        val ids = resources.getStringArray(DONATION_PRODUCT_IDS)
        billingProcessor?.purchase(this, ids[i])
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation)

        setStatusbarColorAuto()
        setNavigationbarColorAuto()
        setTaskDescriptionColorAuto()
        setLightNavigationBar(true)

        setupToolbar()

        billingProcessor = BillingProcessor(this, BuildConfig.GOOGLE_PLAY_LICENSING_KEY, this)
        TintHelper.setTint(progress, ThemeStore.accentColor(this))
        donation.setTextColor(ThemeStore.accentColor(this))

        MaterialUtil.setTint(upiClick)
        upiClick.setOnClickListener {
            UpiPaymentBottomSheetDialogFragment().show(
                supportFragmentManager,
                UpiPaymentBottomSheetDialogFragment.TAG
            )
        }
    }

    private fun setupToolbar() {
        val toolbarColor = ATHUtil.resolveColor(this, R.attr.colorSurface)
        toolbar.setBackgroundColor(toolbarColor)
        ToolbarContentTintHelper.colorBackButton(toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onBillingInitialized() {
        loadSkuDetails()
    }

    private fun loadSkuDetails() {
        if (skuDetailsLoadAsyncTask != null) {
            skuDetailsLoadAsyncTask!!.cancel(false)
        }
        skuDetailsLoadAsyncTask = SkuDetailsLoadAsyncTask(this).execute()
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        //loadSkuDetails();
        Toast.makeText(this, R.string.thank_you, Toast.LENGTH_SHORT).show()
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Log.e(TAG, "Billing error: code = $errorCode", error)
    }

    override fun onPurchaseHistoryRestored() {
        //loadSkuDetails();
        Toast.makeText(this, R.string.restored_previous_purchases, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!billingProcessor!!.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
        if (requestCode == TEZ_REQUEST_CODE) {
            // Process based on the data in response.
            Log.d("result", data!!.getStringExtra("Status"))
        }
    }

    override fun onDestroy() {
        billingProcessor?.release()
        skuDetailsLoadAsyncTask?.cancel(true)
        super.onDestroy()
    }
}

private class SkuDetailsLoadAsyncTask internal constructor(supportDevelopmentActivity: SupportDevelopmentActivity) :
    AsyncTask<Void, Void, List<SkuDetails>>() {

    private val weakReference: WeakReference<SupportDevelopmentActivity> = WeakReference(
        supportDevelopmentActivity
    )

    override fun onPreExecute() {
        super.onPreExecute()
        val supportDevelopmentActivity = weakReference.get() ?: return

        supportDevelopmentActivity.progressContainer.visibility = View.VISIBLE
        supportDevelopmentActivity.recyclerView.visibility = View.GONE
    }

    override fun doInBackground(vararg params: Void): List<SkuDetails>? {
        val dialog = weakReference.get()
        if (dialog != null) {
            val ids =
                dialog.resources.getStringArray(SupportDevelopmentActivity.DONATION_PRODUCT_IDS)
            return dialog.billingProcessor!!.getPurchaseListingDetails(ArrayList(Arrays.asList(*ids)))
        }
        cancel(false)
        return null
    }

    override fun onPostExecute(skuDetails: List<SkuDetails>?) {
        super.onPostExecute(skuDetails)
        val dialog = weakReference.get() ?: return

        if (skuDetails == null || skuDetails.isEmpty()) {
            dialog.progressContainer.visibility = View.GONE
            return
        }

        dialog.progressContainer.visibility = View.GONE
        dialog.recyclerView.itemAnimator = DefaultItemAnimator()
        dialog.recyclerView.layoutManager = GridLayoutManager(dialog, 2)
        dialog.recyclerView.adapter = SkuDetailsAdapter(dialog, skuDetails)
        dialog.recyclerView.visibility = View.VISIBLE
    }
}

class SkuDetailsAdapter(
    private var donationsDialog: SupportDevelopmentActivity, objects: List<SkuDetails>
) : RecyclerView.Adapter<SkuDetailsAdapter.ViewHolder>() {

    private var skuDetailsList: List<SkuDetails> = ArrayList()

    init {
        skuDetailsList = objects
    }

    private fun getIcon(position: Int): Int {
        return when (position) {
            0 -> R.drawable.ic_cookie_white_24dp
            1 -> R.drawable.ic_take_away_white_24dp
            2 -> R.drawable.ic_take_away_coffe_white_24dp
            3 -> R.drawable.ic_beer_white_24dp
            4 -> R.drawable.ic_fast_food_meal_white_24dp
            5 -> R.drawable.ic_popcorn_white_24dp
            6 -> R.drawable.ic_card_giftcard_white_24dp
            7 -> R.drawable.ic_food_croissant_white_24dp
            else -> R.drawable.ic_card_giftcard_white_24dp
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(donationsDialog).inflate(
                LAYOUT_RES_ID,
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val skuDetails = skuDetailsList[i]
        viewHolder.title.text = skuDetails.title.replace("(Notrika Music Player)", "")
            .trim { it <= ' ' }
        viewHolder.text.text = skuDetails.description
        viewHolder.text.visibility = View.GONE
        viewHolder.price.text = skuDetails.priceText
        viewHolder.image.setImageResource(getIcon(i))

        val purchased = donationsDialog.billingProcessor!!.isPurchased(skuDetails.productId)
        val titleTextColor = if (purchased) ATHUtil.resolveColor(
            donationsDialog,
            android.R.attr.textColorHint
        ) else textColorPrimary(donationsDialog)
        val contentTextColor =
            if (purchased) titleTextColor else textColorSecondary(donationsDialog)

        viewHolder.title.setTextColor(titleTextColor)
        viewHolder.text.setTextColor(contentTextColor)
        viewHolder.price.setTextColor(titleTextColor)

        strikeThrough(viewHolder.title, purchased)
        strikeThrough(viewHolder.text, purchased)
        strikeThrough(viewHolder.price, purchased)

        viewHolder.itemView.setOnTouchListener { _, _ -> purchased }
        viewHolder.itemView.setOnClickListener { donationsDialog.donate(i) }
    }

    override fun getItemCount(): Int {
        return skuDetailsList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.itemTitle)
        var text: TextView = view.findViewById(R.id.itemText)
        var price: TextView = view.findViewById(R.id.itemPrice)
        var image: AppCompatImageView = view.findViewById(R.id.itemImage)
    }

    companion object {
        @LayoutRes
        private val LAYOUT_RES_ID = R.layout.item_donation_option

        private fun strikeThrough(textView: TextView, strikeThrough: Boolean) {
            textView.paintFlags =
                if (strikeThrough) textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}
