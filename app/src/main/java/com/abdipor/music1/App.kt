/*
 * Copyright (c) 2019 mohamamd hussin abdi.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.abdipor.music1

import android.widget.Toast
import androidx.multidex.MultiDexApplication
import com.abdipor.appthemehelper.ThemeStore
import com.abdipor.appthemehelper.util.VersionUtils
import com.abdipor.music1.appshortcuts.DynamicShortcutManager
import com.abdipor.music1.dagger.DaggerMusicComponent
import com.abdipor.music1.dagger.MusicComponent
import com.abdipor.music1.dagger.module.AppModule
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails

class App : MultiDexApplication() {

    lateinit var billingProcessor: BillingProcessor

    override fun onCreate() {
        super.onCreate()
        instance = this
        musicComponent = initDagger(this)

        // default theme
        if (!ThemeStore.isConfigured(this, 3)) {
            ThemeStore.editTheme(this)
                .accentColorRes(R.color.md_blue_A400)
                .coloredNavigationBar(true)
                .commit()
        }

        if (VersionUtils.hasNougatMR())
            DynamicShortcutManager(this).initDynamicShortcuts()

        // automatically restores purchases
        billingProcessor = BillingProcessor(this, BuildConfig.GOOGLE_PLAY_LICENSING_KEY,
            object : BillingProcessor.IBillingHandler {
                override fun onProductPurchased(productId: String, details: TransactionDetails?) {}

                override fun onPurchaseHistoryRestored() {
                    Toast.makeText(
                        this@App,
                        R.string.restored_previous_purchase_please_restart,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

                override fun onBillingError(errorCode: Int, error: Throwable?) {}

                override fun onBillingInitialized() {}
            })
    }

    private fun initDagger(app: App): MusicComponent =
        DaggerMusicComponent.builder()
            .appModule(AppModule(app))
            .build()

    override fun onTerminate() {
        super.onTerminate()
        billingProcessor.release()
    }

    companion object {
        private var instance: App? = null

        fun getContext(): App {
            return instance!!
        }

        fun isProVersion(): Boolean {
//            return BuildConfig.DEBUG || instance?.billingProcessor!!.isPurchased(
//                PRO_VERSION_PRODUCT_ID )
            return true
        }

        lateinit var musicComponent: MusicComponent

        const val PRO_VERSION_PRODUCT_ID = "pro_version"
    }
}
