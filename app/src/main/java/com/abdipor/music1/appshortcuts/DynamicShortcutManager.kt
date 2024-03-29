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

package com.abdipor.music1.appshortcuts

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import com.abdipor.music1.appshortcuts.shortcuttype.LastAddedShortcutType
import com.abdipor.music1.appshortcuts.shortcuttype.SearchShortCutType
import com.abdipor.music1.appshortcuts.shortcuttype.ShuffleAllShortcutType
import com.abdipor.music1.appshortcuts.shortcuttype.TopTracksShortcutType
import java.util.*

@TargetApi(Build.VERSION_CODES.N_MR1)
class DynamicShortcutManager(private val context: Context) {
    private val shortcutManager: ShortcutManager =
        this.context.getSystemService(ShortcutManager::class.java)

    private val defaultShortcuts: List<ShortcutInfo>
        get() = Arrays.asList(
            SearchShortCutType(context).shortcutInfo,
            ShuffleAllShortcutType(context).shortcutInfo,
            TopTracksShortcutType(context).shortcutInfo,
            LastAddedShortcutType(context).shortcutInfo

        )

    fun initDynamicShortcuts() {
        //if (shortcutManager.dynamicShortcuts.size == 0) {
        shortcutManager.dynamicShortcuts = defaultShortcuts
        //}
    }

    fun updateDynamicShortcuts() {
        shortcutManager.updateShortcuts(defaultShortcuts)
    }

    companion object {

        fun createShortcut(
            context: Context,
            id: String,
            shortLabel: String,
            longLabel: String,
            icon: Icon,
            intent: Intent
        ): ShortcutInfo {
            return ShortcutInfo.Builder(context, id).setShortLabel(shortLabel)
                .setLongLabel(longLabel).setIcon(icon).setIntent(intent).build()
        }

        fun reportShortcutUsed(context: Context, shortcutId: String) {
            context.getSystemService(ShortcutManager::class.java).reportShortcutUsed(shortcutId)
        }
    }
}
