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
package com.abdipor.appthemehelper.common.prefs.supportv7

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference
import com.abdipor.appthemehelper.R.layout

class ATEListPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
    defStyleRes: Int = -1
) : ListPreference(context, attrs, defStyleAttr, defStyleRes) {

    init {
        layoutResource = layout.ate_preference_custom_support
        if (summary == null || summary.toString().trim { it <= ' ' }.isEmpty()) {
            summary = "%s"
        }
    }
}