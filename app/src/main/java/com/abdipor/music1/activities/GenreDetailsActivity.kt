package com.abdipor.music1.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abdipor.appthemehelper.util.ATHUtil
import com.abdipor.music1.App
import com.abdipor.music1.R
import com.abdipor.music1.activities.base.AbsSlidingMusicPanelActivity
import com.abdipor.music1.adapter.song.ShuffleButtonSongAdapter
import com.abdipor.music1.helper.menu.GenreMenuHelper
import com.abdipor.music1.interfaces.CabHolder
import com.abdipor.music1.model.Genre
import com.abdipor.music1.model.Song
import com.abdipor.music1.mvp.presenter.GenreDetailsPresenter
import com.abdipor.music1.mvp.presenter.GenreDetailsView
import com.abdipor.music1.util.DensityUtil
import com.abdipor.music1.util.NotrikaColorUtil
import com.afollestad.materialcab.MaterialCab
import kotlinx.android.synthetic.main.activity_playlist_detail.*
import java.util.*
import javax.inject.Inject

/**
 * @author Hemanth S (a36069).
 */

class GenreDetailsActivity : AbsSlidingMusicPanelActivity(), CabHolder, GenreDetailsView {

    @Inject
    lateinit var genreDetailsPresenter: GenreDetailsPresenter

    private lateinit var genre: Genre
    private lateinit var songAdapter: ShuffleButtonSongAdapter
    private var cab: MaterialCab? = null

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    private fun checkIsEmpty() {
        checkForPadding()
        emptyEmoji.text = getEmojiByUnicode(0x1F631)
        empty?.visibility = if (songAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun checkForPadding() {
        val height = DensityUtil.dip2px(this, 52f)
        recyclerView.setPadding(0, 0, 0, (height))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setDrawUnderStatusBar()
        super.onCreate(savedInstanceState)
        setStatusbarColorAuto()
        setNavigationbarColorAuto()
        setTaskDescriptionColorAuto()
        setLightNavigationBar(true)
        toggleBottomNavigationView(true)

        if (intent.extras != null) {
            genre = intent?.extras?.getParcelable(EXTRA_GENRE_ID)!!
        } else {
            finish()
        }

        setUpToolBar()
        setupRecyclerView()

        App.musicComponent.inject(this)
        genreDetailsPresenter.attachView(this)
    }

    private fun setUpToolBar() {
        toolbar.setBackgroundColor(ATHUtil.resolveColor(this, R.attr.colorSurface))
        setSupportActionBar(toolbar)
        title = genre.name
    }

    override fun onResume() {
        super.onResume()
        genreDetailsPresenter.loadGenreSongs(genre.id)
    }

    override fun onDestroy() {
        super.onDestroy()
        genreDetailsPresenter.detachView()
    }

    override fun createContentView(): View {
        return wrapSlidingMusicPanel(R.layout.activity_playlist_detail)
    }

    override fun showEmptyView() {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_genre_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return GenreMenuHelper.handleMenuClick(this, genre, item)
    }

    private fun setupRecyclerView() {
        songAdapter = ShuffleButtonSongAdapter(this, ArrayList(), R.layout.item_list, this)
        recyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this@GenreDetailsActivity)
            adapter = songAdapter
        }
        songAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIsEmpty()
            }
        })
    }

    override fun songs(songs: List<Song>) {
        songAdapter.swapDataSet(songs)
    }

    override fun openCab(menuRes: Int, callback: MaterialCab.Callback): MaterialCab {
        if (cab != null && cab!!.isActive) cab?.finish()
        cab = MaterialCab(this, R.id.cab_stub).setMenu(menuRes)
            .setCloseDrawableRes(R.drawable.ic_close_white_24dp)
            .setBackgroundColor(
                NotrikaColorUtil.shiftBackgroundColorForLightText(
                    ATHUtil.resolveColor(
                        this,
                        R.attr.colorSurface
                    )
                )
            ).start(callback)
        return cab!!
    }

    override fun onBackPressed() {
        if (cab != null && cab!!.isActive) cab!!.finish()
        else {
            recyclerView!!.stopScroll()
            super.onBackPressed()
        }
    }

    override fun onMediaStoreChanged() {
        super.onMediaStoreChanged()
        genreDetailsPresenter.loadGenreSongs(genre.id)
    }

    companion object {
        const val EXTRA_GENRE_ID = "extra_genre_id"
    }
}
