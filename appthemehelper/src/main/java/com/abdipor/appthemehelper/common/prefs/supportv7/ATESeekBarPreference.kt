package com.abdipor.appthemehelper.common.prefs.supportv7

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.preference.PreferenceViewHolder
import androidx.preference.SeekBarPreference
import com.abdipor.appthemehelper.R
import com.abdipor.appthemehelper.ThemeStore
import com.abdipor.appthemehelper.util.ATHUtil
import com.abdipor.appthemehelper.util.TintHelper

class ATESeekBarPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
    defStyleRes: Int = -1
) : SeekBarPreference(context, attrs, defStyleAttr, defStyleRes) {

    init {
        icon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ATHUtil.resolveColor(
                context,
                android.R.attr.colorControlNormal
            ), BlendModeCompat.SRC_IN
        )
    }

    override fun onBindViewHolder(view: PreferenceViewHolder) {
        super.onBindViewHolder(view)
        val seekBar = view.findViewById(R.id.seekbar) as SeekBar
        TintHelper.setTintAuto(seekBar, ThemeStore.accentColor(context), false)
    }
}
