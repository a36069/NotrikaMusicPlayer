package com.abdipor.music1.adapter.song

import android.app.ActivityOptions
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.abdipor.music1.R
import com.abdipor.music1.adapter.base.AbsMultiSelectAdapter
import com.abdipor.music1.adapter.base.MediaEntryViewHolder
import com.abdipor.music1.extensions.hide
import com.abdipor.music1.extensions.show
import com.abdipor.music1.glide.NotrikaMusicColoredTarget
import com.abdipor.music1.glide.SongGlideRequest
import com.abdipor.music1.helper.MusicPlayerRemote
import com.abdipor.music1.helper.SortOrder
import com.abdipor.music1.helper.menu.SongMenuHelper
import com.abdipor.music1.helper.menu.SongsMenuHelper
import com.abdipor.music1.interfaces.CabHolder
import com.abdipor.music1.model.Song
import com.abdipor.music1.util.MusicUtil
import com.abdipor.music1.util.NavigationUtil
import com.abdipor.music1.util.PreferenceUtil
import com.afollestad.materialcab.MaterialCab
import com.bumptech.glide.Glide
import me.zhanghai.android.fastscroll.PopupTextProvider
import java.util.*

/**
 * Created by hemanths on 13/08/17.
 */

open class SongAdapter(
    protected val activity: AppCompatActivity,
    var dataSet: MutableList<Song>,
    protected var itemLayoutRes: Int,
    cabHolder: CabHolder?,
    showSectionName: Boolean = true
) : AbsMultiSelectAdapter<SongAdapter.ViewHolder, Song>(
    activity,
    cabHolder,
    R.menu.menu_media_selection
), MaterialCab.Callback, PopupTextProvider {

    private var showSectionName = true

    init {
        this.showSectionName = showSectionName
        this.setHasStableIds(true)
    }

    open fun swapDataSet(dataSet: List<Song>) {
        this.dataSet = dataSet.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return dataSet[position].id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false)
        return createViewHolder(view)
    }

    protected open fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = dataSet[position]
        val isChecked = isChecked(song)
        holder.itemView.isActivated = isChecked
        if (isChecked) {
            holder.menu?.hide()
        } else {
            holder.menu?.show()
        }
        holder.title?.text = getSongTitle(song)
        holder.text?.text = getSongText(song)
        loadAlbumCover(song, holder)
    }

    private fun setColors(color: Int, holder: ViewHolder) {
        if (holder.paletteColorContainer != null) {
            holder.paletteColorContainer?.setBackgroundColor(color)
        }
    }

    protected open fun loadAlbumCover(song: Song, holder: ViewHolder) {
        if (holder.image == null) {
            return
        }
        SongGlideRequest.Builder.from(Glide.with(activity), song)
            .checkIgnoreMediaStore(activity)
            .generatePalette(activity).build()
            .into(object : NotrikaMusicColoredTarget(holder.image!!) {
                override fun onLoadCleared(placeholder: Drawable?) {
                    super.onLoadCleared(placeholder)
                    setColors(defaultFooterColor, holder)
                }

                override fun onColorReady(color: Int) {
                    setColors(color, holder)
                }
            })
    }

    private fun getSongTitle(song: Song): String? {
        return song.title
    }

    private fun getSongText(song: Song): String? {
        return song.artistName
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getIdentifier(position: Int): Song? {
        return dataSet[position]
    }

    override fun getName(song: Song): String {
        return song.title
    }

    override fun onMultipleItemAction(menuItem: MenuItem, selection: ArrayList<Song>) {
        SongsMenuHelper.handleMenuClick(activity, selection, menuItem.itemId)
    }

    override fun getPopupText(position: Int): String {
        val sectionName: String? = when (PreferenceUtil.getInstance(activity).songSortOrder) {
            SortOrder.SongSortOrder.SONG_A_Z, SortOrder.SongSortOrder.SONG_Z_A -> dataSet[position].title
            SortOrder.SongSortOrder.SONG_ALBUM -> dataSet[position].albumName
            SortOrder.SongSortOrder.SONG_ARTIST -> dataSet[position].artistName
            SortOrder.SongSortOrder.SONG_YEAR -> return MusicUtil.getYearString(dataSet[position].year)
            SortOrder.SongSortOrder.COMPOSER -> dataSet[position].composer
            else -> {
                return ""
            }
        }

        return MusicUtil.getSectionName(sectionName)
    }

    open inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {
        protected open var songMenuRes = SongMenuHelper.MENU_RES
        protected open val song: Song
            get() = dataSet[layoutPosition]

        init {
            setImageTransitionName(activity.getString(R.string.transition_album_art))
            menu?.setOnClickListener(object : SongMenuHelper.OnClickSongMenu(activity) {
                override val song: Song
                    get() = this@ViewHolder.song

                override val menuRes: Int
                    get() = songMenuRes

                override fun onMenuItemClick(item: MenuItem): Boolean {
                    return onSongMenuItemClick(item) || super.onMenuItemClick(item)
                }
            })
        }

        protected open fun onSongMenuItemClick(item: MenuItem): Boolean {
            if (image != null && image!!.visibility == View.VISIBLE) {
                when (item.itemId) {
                    R.id.action_go_to_album -> {
                        val activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                            activity,
                            imageContainerCard ?: image,
                            "${activity.getString(R.string.transition_album_art)}_${song.albumId}"
                        )
                        NavigationUtil.goToAlbumOptions(activity, song.albumId, activityOptions)
                        return true
                    }
                }
            }
            return false
        }

        override fun onClick(v: View?) {
            if (isInQuickSelectMode) {
                toggleChecked(layoutPosition)
            } else {
                MusicPlayerRemote.openQueue(dataSet, layoutPosition, true)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            return toggleChecked(layoutPosition)
        }
    }

    companion object {

        val TAG: String = SongAdapter::class.java.simpleName
    }
}
