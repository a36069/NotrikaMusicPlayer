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
import androidx.fragment.app.DialogFragment
import com.abdipor.music1.R
import com.abdipor.music1.loaders.PlaylistLoader
import com.abdipor.music1.model.Song
import com.abdipor.music1.util.PlaylistsUtil
import com.abdipor.music1.util.PreferenceUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems

class AddToPlaylistDialog : DialogFragment() {

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {
        val playlists = PlaylistLoader.getAllPlaylists(requireContext())
        val playlistNames: MutableList<String> = mutableListOf()
        playlistNames.add(requireContext().resources.getString(R.string.action_new_playlist))
        for (p in playlists) {
            playlistNames.add(p.name)
        }

        return MaterialDialog(requireContext()).show {
            title(R.string.add_playlist_title)
            cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
            listItems(items = playlistNames) { dialog, index, _ ->
                val songs =
                    requireArguments().getParcelableArrayList<Song>("songs") ?: return@listItems
                if (index == 0) {
                    dialog.dismiss()
                    activity?.supportFragmentManager?.let {
                        CreatePlaylistDialog.create(songs).show(it, "ADD_TO_PLAYLIST")
                    }
                } else {
                    dialog.dismiss()
                    PlaylistsUtil.addToPlaylist(
                        requireContext(),
                        songs,
                        playlists[index - 1].id,
                        true
                    )
                }
            }
        }
    }

    companion object {

        fun create(song: Song): AddToPlaylistDialog {
            val list = ArrayList<Song>()
            list.add(song)
            return create(list)
        }

        fun create(songs: List<Song>): AddToPlaylistDialog {
            val dialog = AddToPlaylistDialog()
            val args = Bundle()
            args.putParcelableArrayList("songs", ArrayList(songs))
            dialog.arguments = args
            return dialog
        }
    }
}