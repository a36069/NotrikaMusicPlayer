package com.abdipor.music1.adapter

import android.app.ActivityOptions
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.abdipor.appthemehelper.ThemeStore
import com.abdipor.music1.R
import com.abdipor.music1.adapter.base.MediaEntryViewHolder
import com.abdipor.music1.glide.AlbumGlideRequest
import com.abdipor.music1.glide.ArtistGlideRequest
import com.abdipor.music1.helper.MusicPlayerRemote
import com.abdipor.music1.helper.menu.SongMenuHelper
import com.abdipor.music1.loaders.PlaylistSongsLoader
import com.abdipor.music1.model.*
import com.abdipor.music1.model.smartplaylist.AbsSmartPlaylist
import com.abdipor.music1.util.MusicUtil
import com.abdipor.music1.util.NavigationUtil
import com.bumptech.glide.Glide
import android.util.Pair as UtilPair

class SearchAdapter(
    private val activity: AppCompatActivity,
    private var dataSet: List<Any>?
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    fun swapDataSet(dataSet: MutableList<Any>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (dataSet!![position] is Album) return ALBUM
        if (dataSet!![position] is Artist) return ARTIST
        if (dataSet!![position] is Genre) return GENRE
        if (dataSet!![position] is Playlist) return PLAYLIST
        return if (dataSet!![position] is Song) SONG else HEADER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == HEADER) ViewHolder(
            LayoutInflater.from(activity).inflate(
                R.layout.sub_header,
                parent,
                false
            ), viewType
        )
        else
            ViewHolder(
                LayoutInflater.from(activity).inflate(R.layout.item_list, parent, false),
                viewType
            )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ALBUM -> {
                val album = dataSet?.get(position) as Album
                holder.title?.text = album.title
                holder.text?.text = album.artistName
                AlbumGlideRequest.Builder.from(Glide.with(activity), album.safeGetFirstSong())
                    .checkIgnoreMediaStore(activity).build().into(holder.image)
            }
            ARTIST -> {
                val artist = dataSet?.get(position) as Artist
                holder.title?.text = artist.name
                holder.text?.text = MusicUtil.getArtistInfoString(activity, artist)
                ArtistGlideRequest.Builder.from(Glide.with(activity), artist).build()
                    .into(holder.image)
            }
            SONG -> {
                val song = dataSet?.get(position) as Song
                holder.title?.text = song.title
                holder.text?.text = song.albumName
            }
            GENRE -> {
                val genre = dataSet?.get(position) as Genre
                holder.title?.text = genre.name
            }
            PLAYLIST -> {
                val playlist = dataSet?.get(position) as Playlist
                holder.title?.text = playlist.name
                holder.text?.text = MusicUtil.getPlaylistInfoString(activity, getSongs(playlist))
            }
            else -> {
                holder.title?.text = dataSet?.get(position).toString()
                holder.title?.setTextColor(ThemeStore.accentColor(activity))
            }
        }
    }

    private fun getSongs(playlist: Playlist): java.util.ArrayList<Song> {
        val songs = java.util.ArrayList<Song>()
        if (playlist is AbsSmartPlaylist) {
            songs.addAll(playlist.getSongs(activity))
        } else {
            songs.addAll(PlaylistSongsLoader.getPlaylistSongList(activity, playlist.id))
        }
        return songs
    }

    override fun getItemCount(): Int {
        return dataSet!!.size
    }

    inner class ViewHolder(itemView: View, itemViewType: Int) : MediaEntryViewHolder(itemView) {
        init {
            itemView.setOnLongClickListener(null)

            if (itemViewType == SONG) {
                menu?.visibility = View.VISIBLE
                menu?.setOnClickListener(object : SongMenuHelper.OnClickSongMenu(activity) {
                    override val song: Song
                        get() = dataSet!![layoutPosition] as Song
                })
            } else {
                menu?.visibility = View.GONE
            }

            when (itemViewType) {
                ALBUM -> setImageTransitionName(activity.getString(R.string.transition_album_art))
                ARTIST -> setImageTransitionName(activity.getString(R.string.transition_artist_image))
                else -> {
                    val container = itemView.findViewById<View>(R.id.imageContainer)
                    container?.visibility = View.GONE
                }
            }
        }

        override fun onClick(v: View?) {
            val item = dataSet!![layoutPosition]
            when (itemViewType) {
                ALBUM -> {
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        activity,
                        UtilPair.create(image, activity.getString(R.string.transition_album_art))
                    )
                    NavigationUtil.goToAlbumOptions(activity, (item as Album).id, options)
                }
                ARTIST -> {
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        activity,
                        UtilPair.create(image, activity.getString(R.string.transition_artist_image))
                    )
                    NavigationUtil.goToArtistOptions(activity, (item as Artist).id, options)
                }
                GENRE -> {
                    NavigationUtil.goToGenre(activity, item as Genre)
                }
                PLAYLIST -> {
                    NavigationUtil.goToPlaylistNew(activity, item as Playlist)
                }
                SONG -> {
                    val playList = ArrayList<Song>()
                    playList.add(item as Song)
                    MusicPlayerRemote.openQueue(playList, 0, true)
                }
            }
        }
    }

    companion object {
        private const val HEADER = 0
        private const val ALBUM = 1
        private const val ARTIST = 2
        private const val SONG = 3
        private const val GENRE = 4
        private const val PLAYLIST = 5
    }
}
