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

package com.abdipor.music1.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.abdipor.music1.R
import com.abdipor.music1.model.smartplaylist.AbsSmartPlaylist
import com.abdipor.music1.util.PreferenceUtil
import com.afollestad.materialdialogs.MaterialDialog


class ClearSmartPlaylistDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val playlist = requireArguments().getParcelable<AbsSmartPlaylist>("playlist")
        val title = R.string.clear_playlist_title

        val content = HtmlCompat.fromHtml(
            getString(R.string.clear_playlist_x, playlist!!.name),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        return MaterialDialog(requireContext()).show {
            title(title)
            cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
            message(text = content)
            positiveButton(R.string.clear_action) {
                if (activity == null) {
                    return@positiveButton
                }
                playlist.clear(requireActivity())
            }
            negativeButton { (android.R.string.cancel) }
        }
    }

    companion object {

        fun create(playlist: AbsSmartPlaylist): ClearSmartPlaylistDialog {
            val dialog = ClearSmartPlaylistDialog()
            val args = Bundle()
            args.putParcelable("playlist", playlist)
            dialog.arguments = args
            return dialog
        }
    }
}