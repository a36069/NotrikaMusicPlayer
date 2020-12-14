package com.abdipor.music1.util.theme

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.PowerManager
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import com.abdipor.music1.R
import com.abdipor.music1.util.PreferenceUtil
import com.abdipor.music1.util.theme.ThemeMode.*

/**
 * @author Paolo Valerdi
 */
object ThemeManager {

    @StyleRes
    fun getThemeResValue(
        context: Context
    ): Int = when (context.generalThemeValue) {
        LIGHT -> R.style.Theme_NotrikaMusic_Light
        DARK -> R.style.Theme_NotrikaMusic_Base
        BLACK -> R.style.Theme_NotrikaMusic_Black
        AUTO -> R.style.Theme_NotrikaMusic_FollowSystem
    }

    fun getNightMode(
        context: Context
    ): Int = when (context.generalThemeValue) {
        LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        DARK,
        BLACK -> AppCompatDelegate.MODE_NIGHT_YES
        AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    private fun isSystemDarkModeEnabled(context: Context): Boolean {
        val isBatterySaverEnabled =
            (context.getSystemService(Context.POWER_SERVICE) as PowerManager?)?.isPowerSaveMode
                ?: false
        val isDarkModeEnabled =
            (context.resources.configuration.uiMode and UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES

        return isBatterySaverEnabled or isDarkModeEnabled
    }

}

val Context.generalThemeValue: ThemeMode
    get() = PreferenceUtil.getInstance(this).generalThemeValue