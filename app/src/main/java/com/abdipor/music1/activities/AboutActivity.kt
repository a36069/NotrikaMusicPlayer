package com.abdipor.music1.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdipor.appthemehelper.util.ATHUtil
import com.abdipor.appthemehelper.util.ToolbarContentTintHelper
import com.abdipor.music1.App
import com.abdipor.music1.Constants.APP_INSTAGRAM_LINK
import com.abdipor.music1.Constants.APP_TELEGRAM_LINK
import com.abdipor.music1.Constants.APP_TWITTER_LINK
import com.abdipor.music1.Constants.FAQ_LINK
import com.abdipor.music1.Constants.GITHUB_PROJECT
import com.abdipor.music1.Constants.MEDIUM
import com.abdipor.music1.Constants.RATE_ON_GOOGLE_PLAY
import com.abdipor.music1.Constants.TELEGRAM_CHANGE_LOG
import com.abdipor.music1.R
import com.abdipor.music1.activities.base.AbsBaseActivity
import com.abdipor.music1.adapter.ContributorAdapter
import com.abdipor.music1.model.Contributor
import com.abdipor.music1.util.NavigationUtil
import com.abdipor.music1.util.PreferenceUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.card_credit.*
import kotlinx.android.synthetic.main.card_other.*
import kotlinx.android.synthetic.main.card_notrika_info.*
import kotlinx.android.synthetic.main.card_social.*
import java.io.IOException
import java.nio.charset.StandardCharsets

class AboutActivity : AbsBaseActivity(), View.OnClickListener {

    private val contributorsJson: String?
        get() {
            val json: String
            try {
                val inputStream = assets.open("contributors.json")
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                json = String(buffer, StandardCharsets.UTF_8)
            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
            }
            return json
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        setDrawUnderStatusBar()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setStatusbarColorAuto()
        setNavigationbarColorAuto()
        setLightNavigationBar(true)

        val toolbarColor = ATHUtil.resolveColor(this, R.attr.colorSurface)
        toolbar.setBackgroundColor(toolbarColor)
        ToolbarContentTintHelper.colorBackButton(toolbar)
        setSupportActionBar(toolbar)
        version.setSummary(getAppVersion())
        setUpView()
        loadContributors()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun setUpView() {
        appGithub.setOnClickListener(this)
        faqLink.setOnClickListener(this)
        telegramLink.setOnClickListener(this)
        appRate.setOnClickListener(this)
        appShare.setOnClickListener(this)
        instagramLink.setOnClickListener(this)
        twitterLink.setOnClickListener(this)
        mediumtLink.setOnClickListener(this)
        bugReportLink.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mediumtLink -> openUrl(MEDIUM)
            R.id.faqLink -> openUrl(FAQ_LINK)
            R.id.telegramLink -> openUrl(APP_TELEGRAM_LINK)
            R.id.appGithub -> openUrl(GITHUB_PROJECT)
            R.id.appRate -> openUrl(RATE_ON_GOOGLE_PLAY)
            R.id.appShare -> shareApp()
            R.id.instagramLink -> openUrl(APP_INSTAGRAM_LINK)
            R.id.twitterLink -> openUrl(APP_TWITTER_LINK)
            R.id.bugReportLink -> NavigationUtil.bugReport(this)
        }
    }

    private fun showChangeLogOptions() {
        MaterialDialog(this).show {
            cornerRadius(PreferenceUtil.getInstance(this@AboutActivity).dialogCorner)
            listItems(items = listOf("Telegram Channel", "App")) { _, position, _ ->
                if (position == 0) {
                    openUrl(TELEGRAM_CHANGE_LOG)
                } else {
                    NavigationUtil.gotoWhatNews(this@AboutActivity)
                }
            }
        }
    }

    private fun getAppVersion(): String {
        return try {
            val isPro = if (App.isProVersion()) "Pro" else "Free"
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            "${packageInfo.versionName} $isPro"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "0.0.0"
        }
    }

    private fun shareApp() {
        ShareCompat.IntentBuilder.from(this).setType("text/plain")
            .setChooserTitle(R.string.share_app)
            .setText(String.format(getString(R.string.app_share), packageName)).startChooser()
    }

    private fun loadContributors() {
        val type = object : TypeToken<List<Contributor>>() {

        }.type
        val contributors = Gson().fromJson<List<Contributor>>(contributorsJson, type)

        val contributorAdapter = ContributorAdapter(contributors)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = contributorAdapter
    }
}
