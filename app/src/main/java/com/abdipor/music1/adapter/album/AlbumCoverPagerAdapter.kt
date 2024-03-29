package com.abdipor.music1.adapter.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.abdipor.music1.R
import com.abdipor.music1.fragments.AlbumCoverStyle
import com.abdipor.music1.glide.NotrikaMusicColoredTarget
import com.abdipor.music1.glide.SongGlideRequest
import com.abdipor.music1.misc.CustomFragmentStatePagerAdapter
import com.abdipor.music1.model.Song
import com.abdipor.music1.util.NavigationUtil
import com.abdipor.music1.util.PreferenceUtil
import com.bumptech.glide.Glide

class AlbumCoverPagerAdapter(
    fragmentManager: FragmentManager,
    private val dataSet: List<Song>
) : CustomFragmentStatePagerAdapter(fragmentManager) {

    private var currentColorReceiver: AlbumCoverFragment.ColorReceiver? = null
    private var currentColorReceiverPosition = -1

    override fun getItem(position: Int): Fragment {
        return AlbumCoverFragment.newInstance(dataSet[position])
    }

    override fun getCount(): Int {
        return dataSet.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val o = super.instantiateItem(container, position)
        if (currentColorReceiver != null && currentColorReceiverPosition == position) {
            receiveColor(currentColorReceiver!!, currentColorReceiverPosition)
        }
        return o
    }

    /**
     * Only the latest passed [AlbumCoverFragment.ColorReceiver] is guaranteed to receive a
     * response
     */
    fun receiveColor(colorReceiver: AlbumCoverFragment.ColorReceiver, position: Int) {

        if (getFragment(position) is AlbumCoverFragment) {
            val fragment = getFragment(position) as AlbumCoverFragment
            currentColorReceiver = null
            currentColorReceiverPosition = -1
            fragment.receiveColor(colorReceiver, position)
        } else {
            currentColorReceiver = colorReceiver
            currentColorReceiverPosition = position
        }
    }

    class AlbumCoverFragment : Fragment() {

        lateinit var albumCover: ImageView
        private var isColorReady: Boolean = false
        private var color: Int = 0
        private lateinit var song: Song
        private var colorReceiver: ColorReceiver? = null
        private var request: Int = 0

        private val layout: Int
            get() {
                return when (PreferenceUtil.getInstance(requireContext()).albumCoverStyle) {
                    AlbumCoverStyle.NORMAL -> R.layout.fragment_album_cover
                    AlbumCoverStyle.FLAT -> R.layout.fragment_album_flat_cover
                    AlbumCoverStyle.CIRCLE -> R.layout.fragment_album_circle_cover
                    AlbumCoverStyle.CARD -> R.layout.fragment_album_card_cover
                    AlbumCoverStyle.MATERIAL -> R.layout.fragment_album_material_cover
                    AlbumCoverStyle.FULL -> R.layout.fragment_album_full_cover
                    AlbumCoverStyle.FULL_CARD -> R.layout.fragment_album_full_card_cover
                    else -> R.layout.fragment_album_cover
                }
            }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (arguments != null) {
                song = requireArguments().getParcelable(SONG_ARG)!!
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val finalLayout = when {
                PreferenceUtil.getInstance(requireContext())
                    .carouselEffect() -> R.layout.fragment_album_carousel_cover
                else -> layout
            }
            val view = inflater.inflate(finalLayout, container, false)
            albumCover = view.findViewById(R.id.player_image)
            albumCover.setOnClickListener {
                NavigationUtil.goToLyrics(requireActivity())
            }
            return view
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            loadAlbumCover()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            colorReceiver = null
        }

        private fun loadAlbumCover() {
            SongGlideRequest.Builder.from(Glide.with(requireContext()), song)
                .checkIgnoreMediaStore(requireContext())
                .generatePalette(requireContext()).build()
                .into(object : NotrikaMusicColoredTarget(albumCover) {
                    override fun onColorReady(color: Int) {
                        setColor(color)
                    }
                })
        }

        private fun setColor(color: Int) {
            this.color = color
            isColorReady = true
            if (colorReceiver != null) {
                colorReceiver!!.onColorReady(color, request)
                colorReceiver = null
            }
        }

        internal fun receiveColor(colorReceiver: ColorReceiver, request: Int) {
            if (isColorReady) {
                colorReceiver.onColorReady(color, request)
            } else {
                this.colorReceiver = colorReceiver
                this.request = request
            }
        }

        interface ColorReceiver {
            fun onColorReady(color: Int, request: Int)
        }

        companion object {

            private const val SONG_ARG = "song"

            fun newInstance(song: Song): AlbumCoverFragment {
                val frag = AlbumCoverFragment()
                val args = Bundle()
                args.putParcelable(SONG_ARG, song)
                frag.arguments = args
                return frag
            }
        }
    }

    companion object {
        val TAG: String = AlbumCoverPagerAdapter::class.java.simpleName
    }
}

