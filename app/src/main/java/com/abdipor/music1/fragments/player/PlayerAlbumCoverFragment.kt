package com.abdipor.music1.fragments.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.abdipor.music1.R
import com.abdipor.music1.adapter.album.AlbumCoverPagerAdapter
import com.abdipor.music1.adapter.album.AlbumCoverPagerAdapter.AlbumCoverFragment
import com.abdipor.music1.fragments.NowPlayingScreen.*
import com.abdipor.music1.fragments.base.AbsMusicServiceFragment
import com.abdipor.music1.helper.MusicPlayerRemote
import com.abdipor.music1.transform.CarousalPagerTransformer
import com.abdipor.music1.transform.ParallaxPagerTransformer
import com.abdipor.music1.util.PreferenceUtil
import kotlinx.android.synthetic.main.fragment_player_album_cover.*

class PlayerAlbumCoverFragment : AbsMusicServiceFragment(), ViewPager.OnPageChangeListener {
    private var callbacks: Callbacks? = null
    private var currentPosition: Int = 0
    private val colorReceiver = object : AlbumCoverFragment.ColorReceiver {
        override fun onColorReady(color: Int, request: Int) {
            if (currentPosition == request) {
                notifyColorChange(color)
            }
        }
    }

    fun removeSlideEffect() {
        val transformer = ParallaxPagerTransformer(R.id.player_image)
        transformer.setSpeed(0.3f)
        viewPager.setPageTransformer(true, transformer)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player_album_cover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.addOnPageChangeListener(this)
        val nowPlayingScreen = PreferenceUtil.getInstance(requireContext()).nowPlayingScreen

        if (PreferenceUtil.getInstance(requireContext()).carouselEffect() &&
            !((nowPlayingScreen == FULL) || (nowPlayingScreen == ADAPTIVE) || (nowPlayingScreen == FIT))
        ) {
            viewPager.clipToPadding = false
            viewPager.setPadding(40, 40, 40, 0)
            viewPager.pageMargin = 0
            viewPager.setPageTransformer(false, CarousalPagerTransformer(requireContext()))
        } else {
            viewPager.offscreenPageLimit = 2
            viewPager.setPageTransformer(
                true,
                PreferenceUtil.getInstance(requireContext()).albumCoverTransform
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager.removeOnPageChangeListener(this)
    }

    override fun onServiceConnected() {
        updatePlayingQueue()
    }

    override fun onPlayingMetaChanged() {
        viewPager.currentItem = MusicPlayerRemote.position
    }

    override fun onQueueChanged() {
        updatePlayingQueue()
    }

    private fun updatePlayingQueue() {
        viewPager.apply {
            adapter = AlbumCoverPagerAdapter(childFragmentManager, MusicPlayerRemote.playingQueue)
            viewPager.adapter!!.notifyDataSetChanged()
            viewPager.currentItem = MusicPlayerRemote.position
            onPageSelected(MusicPlayerRemote.position)
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        currentPosition = position
        if (viewPager.adapter != null) {
            (viewPager.adapter as AlbumCoverPagerAdapter).receiveColor(colorReceiver, position)
        }
        if (position != MusicPlayerRemote.position) {
            MusicPlayerRemote.playSongAt(position)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    private fun notifyColorChange(color: Int) {
        callbacks?.onColorChanged(color)
    }

    fun setCallbacks(listener: Callbacks) {
        callbacks = listener
    }

    fun removeEffect() {
        viewPager.setPageTransformer(false, null)
    }

    interface Callbacks {

        fun onColorChanged(color: Int)

        fun onFavoriteToggled()
    }

    companion object {
        val TAG: String = PlayerAlbumCoverFragment::class.java.simpleName
    }
}
