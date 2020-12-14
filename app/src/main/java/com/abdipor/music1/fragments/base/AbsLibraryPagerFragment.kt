package com.abdipor.music1.fragments.base

import android.os.Bundle
import com.abdipor.music1.activities.MainActivity

open class AbsLibraryPagerFragment : AbsMusicServiceFragment() {

    val mainActivity: MainActivity
        get() = requireActivity() as MainActivity

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }
}
