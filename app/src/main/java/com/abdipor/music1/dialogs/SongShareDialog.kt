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
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.abdipor.music1.R
import com.abdipor.music1.activities.ShareInstagramStory
import com.abdipor.music1.model.Song
import com.abdipor.music1.util.MusicUtil
import com.abdipor.music1.util.PreferenceUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems

class SongShareDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val song: Song? = requireArguments().getParcelable("song")
        val currentlyListening: String =
            getString(R.string.currently_listening_to_x_by_x, song?.title, song?.artistName)

        return MaterialDialog(requireContext())
            .title(R.string.what_do_you_want_to_share)
            .show {
                cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
                listItems(
                    items = listOf(
                        getString(com.abdipor.music1.R.string.the_audio_file),
                        "\u201C" + currentlyListening + "\u201D",
                        getString(R.string.social_stories)
                    )
                ) { _, index, _ ->
                    when (index) {
                        0 -> {
                            startActivity(Intent.createChooser(song?.let {
                                MusicUtil.createShareSongFileIntent(
                                    it,
                                    context
                                )
                            }, null))
                        }
                        1 -> {
                            startActivity(
                                Intent.createChooser(
                                    Intent()
                                        .setAction(Intent.ACTION_SEND)
                                        .putExtra(Intent.EXTRA_TEXT, currentlyListening)
                                        .setType("text/plain"),
                                    null
                                )
                            )
                        }
                        2 -> {
                            if (song != null) {
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        ShareInstagramStory::class.java
                                    ).putExtra(
                                        ShareInstagramStory.EXTRA_SONG,
                                        song
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }

    companion object {

        fun create(song: Song): SongShareDialog {
            val dialog = SongShareDialog()
            val args = Bundle()
            args.putParcelable("song", song)
            dialog.arguments = args
            return dialog
        }
    }
}
