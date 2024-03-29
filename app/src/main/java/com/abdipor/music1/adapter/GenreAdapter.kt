package com.abdipor.music1.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdipor.music1.R
import com.abdipor.music1.adapter.base.MediaEntryViewHolder
import com.abdipor.music1.model.Genre
import com.abdipor.music1.util.NavigationUtil
import java.util.*

/**
 * @author Hemanth S (a36069).
 */

class GenreAdapter(
    private val activity: Activity,
    var dataSet: List<Genre>,
    private val mItemLayoutRes: Int
) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(activity).inflate(mItemLayoutRes, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = dataSet[position]
        holder.title?.text = genre.name
        holder.text?.text = String.format(
            Locale.getDefault(),
            "%d %s",
            genre.songCount,
            if (genre.songCount > 1) activity.getString(R.string.songs) else activity.getString(R.string.song)
        )
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun swapDataSet(list: List<Genre>) {
        dataSet = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {
        override fun onClick(v: View?) {
            super.onClick(v)
            val genre = dataSet[layoutPosition]
            NavigationUtil.goToGenre(activity, genre)
        }
    }
}
