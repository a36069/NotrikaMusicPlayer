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
import android.net.Uri
import android.os.Bundle
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.abdipor.music1.R
import com.abdipor.music1.activities.saf.SAFGuideActivity
import com.abdipor.music1.helper.MusicPlayerRemote
import com.abdipor.music1.model.Song
import com.abdipor.music1.util.MusicUtil
import com.abdipor.music1.util.PreferenceUtil
import com.abdipor.music1.util.SAFUtil
import com.afollestad.materialdialogs.MaterialDialog

class DeleteSongsDialog : DialogFragment() {
    @JvmField
    var currentSong: Song? = null
    @JvmField
    var songsToRemove: List<Song>? = null

    private var deleteSongsAsyncTask: DeleteSongsAsyncTask? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val songs: ArrayList<Song>? = requireArguments().getParcelableArrayList("songs")
        var title = 0
        var content: CharSequence = ""
        if (songs != null) {
            if (songs.size > 1) {
                title = R.string.delete_songs_title
                content = HtmlCompat.fromHtml(
                    getString(R.string.delete_x_songs, songs.size),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            } else {
                title = R.string.delete_song_title
                content = HtmlCompat.fromHtml(
                    getString(R.string.delete_song_x, songs[0].title),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }

        return MaterialDialog(requireContext()).show {
            title(title)
            message(text = content)
            negativeButton(android.R.string.cancel) {
                dismiss()
            }
            cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
            noAutoDismiss()
            positiveButton(R.string.action_delete) {
                if (songs != null) {
                    if ((songs.size == 1) && MusicPlayerRemote.isPlaying(songs[0])) {
                        MusicPlayerRemote.playNextSong()
                    }
                }

                songsToRemove = songs
                deleteSongsAsyncTask = DeleteSongsAsyncTask(this@DeleteSongsDialog)
                deleteSongsAsyncTask?.execute(DeleteSongsAsyncTask.LoadingInfo(songs, null))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SAFGuideActivity.REQUEST_CODE_SAF_GUIDE -> {
                SAFUtil.openTreePicker(this)
            }
            SAFUtil.REQUEST_SAF_PICK_TREE,
            SAFUtil.REQUEST_SAF_PICK_FILE -> {
                if (deleteSongsAsyncTask != null) {
                    deleteSongsAsyncTask?.cancel(true)
                }
                deleteSongsAsyncTask = DeleteSongsAsyncTask(this)
                deleteSongsAsyncTask?.execute(
                    DeleteSongsAsyncTask.LoadingInfo(
                        requestCode,
                        resultCode,
                        data
                    )
                )
            }
        }
    }

    fun deleteSongs(songs: List<Song>, safUris: List<Uri>?) {
        MusicUtil.deleteTracks(requireActivity(), songs, safUris) { this.dismiss() }
    }

    companion object {

        fun create(song: Song): DeleteSongsDialog {
            val list = ArrayList<Song>()
            list.add(song)
            return create(list)
        }

        fun create(songs: List<Song>): DeleteSongsDialog {
            val dialog = DeleteSongsDialog()
            val args = Bundle()
            args.putParcelableArrayList("songs", ArrayList(songs))
            dialog.arguments = args
            return dialog
        }
    }
}

