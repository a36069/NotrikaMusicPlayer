package com.abdipor.music1.fragments.player.color

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.abdipor.appthemehelper.util.ATHUtil
import com.abdipor.appthemehelper.util.ColorUtil
import com.abdipor.appthemehelper.util.MaterialValueHelper
import com.abdipor.appthemehelper.util.ToolbarContentTintHelper
import com.abdipor.music1.R
import com.abdipor.music1.extensions.getSuitableColorFor
import com.abdipor.music1.fragments.base.AbsPlayerFragment
import com.abdipor.music1.glide.NotrikaMusicColoredTarget
import com.abdipor.music1.glide.SongGlideRequest.Builder
import com.abdipor.music1.glide.palette.BitmapPaletteWrapper
import com.abdipor.music1.helper.MusicPlayerRemote
import com.abdipor.music1.model.Song
import com.abdipor.music1.util.NavigationUtil
import com.abdipor.music1.util.NotrikaColorUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import kotlinx.android.synthetic.main.fragment_color_player.*

class ColorFragment : AbsPlayerFragment() {

    private var lastColor: Int = 0
    private var backgroundColor: Int = 0
    private lateinit var playbackControlsFragment: ColorPlaybackControlsFragment
    private var valueAnimator: ValueAnimator? = null

    override fun playerToolbar(): Toolbar {
        return playerToolbar
    }

    override val paletteColor: Int
        get() = backgroundColor

    override fun onColorChanged(color: Int) {

    }

    override fun onFavoriteToggled() {
        //toggleFavorite(MusicPlayerRemote.currentSong)
    }

    override fun onShow() {
        playbackControlsFragment.show()
    }

    override fun onHide() {
        playbackControlsFragment.hide()
        onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun toolbarIconColor(): Int {
        return lastColor
    }

    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (valueAnimator != null) {
            valueAnimator!!.cancel()
            valueAnimator = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_color_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSubFragments()
        setUpPlayerToolbar()
        playerImage.setOnClickListener {
            NavigationUtil.goToLyrics(requireActivity())
        }
    }

    private fun setUpSubFragments() {
        playbackControlsFragment =
            childFragmentManager.findFragmentById(R.id.playbackControlsFragment) as ColorPlaybackControlsFragment
    }

    private fun setUpPlayerToolbar() {
        playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
            setOnMenuItemClickListener(this@ColorFragment)
            ToolbarContentTintHelper.colorizeToolbar(
                this,
                ATHUtil.resolveColor(requireContext(), R.attr.colorControlNormal),
                requireActivity()
            )
        }
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateSong()
    }

    private fun updateSong() {
        Builder.from(Glide.with(requireActivity()), MusicPlayerRemote.currentSong)
            .checkIgnoreMediaStore(requireContext())
            .generatePalette(requireContext())
            .build()
            .into(object : NotrikaMusicColoredTarget(playerImage) {
                override fun onColorReady(color: Int) {

                }

                override fun onResourceReady(
                    resource: BitmapPaletteWrapper?,
                    glideAnimation: GlideAnimation<in BitmapPaletteWrapper>?
                ) {
                    super.onResourceReady(resource, glideAnimation)
                    resource?.let {
                        val palette = resource.palette
                        val swatch = NotrikaColorUtil.getSwatch(palette)

                        val textColor = NotrikaColorUtil.getTextColor(palette)
                        val backgroundColor = getSuitableColorFor(
                            palette,
                            ATHUtil.resolveColor(requireContext(), R.attr.colorSurface),
                            Color.BLACK
                        )
                        if (ATHUtil.isWindowBackgroundDark(requireContext())) {
                            ColorUtil.desaturateColor(backgroundColor, 0.5f)
                        }

                        setColors(backgroundColor, textColor)
                    }

                }

                override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                    super.onLoadFailed(e, errorDrawable)
                    val backgroundColor = defaultFooterColor
                    val textColor =
                        if (ColorUtil.isColorLight(defaultFooterColor)) MaterialValueHelper.getPrimaryTextColor(
                            requireContext(),
                            true
                        )
                        else MaterialValueHelper.getPrimaryTextColor(requireContext(), false)
                    setColors(backgroundColor, textColor)
                }
            })
    }

    private fun setColors(backgroundColor: Int, componentsColor: Int) {
        this.lastColor = componentsColor
        this.backgroundColor = backgroundColor
        playbackControlsFragment.setDark(componentsColor, backgroundColor)
        colorGradientBackground?.setBackgroundColor(backgroundColor)
        playerActivity?.setLightNavigationBar(ColorUtil.isColorLight(backgroundColor))
        callbacks?.onPaletteColorChanged()
        ToolbarContentTintHelper.colorizeToolbar(playerToolbar, componentsColor, requireActivity())
    }

    companion object {
        fun newInstance(): ColorFragment {
            val args = Bundle()
            val fragment = ColorFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
