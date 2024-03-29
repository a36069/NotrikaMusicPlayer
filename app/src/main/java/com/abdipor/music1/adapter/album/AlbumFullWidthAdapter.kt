/*
 * Copyright (C) 2017. Alexander Bilchuk <a.bilchuk@sandrlab.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abdipor.music1.adapter.album

import android.app.Activity
import android.app.ActivityOptions
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abdipor.music1.R
import com.abdipor.music1.glide.AlbumGlideRequest
import com.abdipor.music1.glide.NotrikaMusicColoredTarget
import com.abdipor.music1.helper.MusicPlayerRemote
import com.abdipor.music1.model.Album
import com.abdipor.music1.util.NavigationUtil
import com.abdipor.music1.views.MetalRecyclerViewPager
import com.bumptech.glide.Glide

class AlbumFullWidthAdapter(
    private val activity: Activity,
    private val dataSet: List<Album>,
    metrics: DisplayMetrics
) : MetalRecyclerViewPager.MetalAdapter<AlbumFullWidthAdapter.FullMetalViewHolder>(metrics) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullMetalViewHolder {
        return FullMetalViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.pager_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FullMetalViewHolder, position: Int) {
        // don't forget about calling supper.onBindViewHolder!
        super.onBindViewHolder(holder, position)
        val album = dataSet[position]
        holder.title?.text = getAlbumTitle(album)
        holder.text?.text = getAlbumText(album)
        holder.playSongs?.setOnClickListener {
            album.songs?.let { songs ->
                MusicPlayerRemote.openQueue(
                    songs,
                    0,
                    true
                )
            }
        }
        loadAlbumCover(album, holder)
    }

    private fun getAlbumTitle(album: Album): String? {
        return album.title
    }

    private fun getAlbumText(album: Album): String? {
        return album.artistName
    }

    private fun loadAlbumCover(album: Album, holder: FullMetalViewHolder) {
        if (holder.image == null) {
            return
        }
        AlbumGlideRequest.Builder.from(Glide.with(activity), album.safeGetFirstSong())
            .checkIgnoreMediaStore(activity)
            .generatePalette(activity)
            .build()
            .into(object : NotrikaMusicColoredTarget(holder.image!!) {
                override fun onColorReady(color: Int) {
                }
            })
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class FullMetalViewHolder(itemView: View) :
        MetalRecyclerViewPager.MetalViewHolder(itemView) {

        override fun onClick(v: View?) {
            val activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                imageContainerCard ?: image,
                "${activity.getString(R.string.transition_album_art)}_${dataSet[layoutPosition].id}"
            )
            NavigationUtil.goToAlbumOptions(activity, dataSet[layoutPosition].id, activityOptions)
        }
    }
}