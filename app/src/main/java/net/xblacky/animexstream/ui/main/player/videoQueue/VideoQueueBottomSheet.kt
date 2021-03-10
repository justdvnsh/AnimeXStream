package net.xblacky.animexstream.ui.main.player.videoQueue

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_animeinfo.*
import kotlinx.android.synthetic.main.fragment_animeinfo.view.*
import kotlinx.android.synthetic.main.loading.view.*
import kotlinx.android.synthetic.main.video_queue_bottom_sheet.*
import kotlinx.android.synthetic.main.video_queue_bottom_sheet.view.*
import net.xblacky.animexstream.R
import net.xblacky.animexstream.ui.main.animeinfo.AnimeInfoFragmentArgs
import net.xblacky.animexstream.ui.main.animeinfo.AnimeInfoViewModel
import net.xblacky.animexstream.ui.main.animeinfo.AnimeInfoViewModelFactory
import net.xblacky.animexstream.ui.main.animeinfo.epoxy.AnimeInfoController
import net.xblacky.animexstream.utils.ItemOffsetDecoration
import net.xblacky.animexstream.utils.Tags.GenreTags
import net.xblacky.animexstream.utils.Utils
import net.xblacky.animexstream.utils.model.AnimeInfoModel

class VideoQueueBottomSheet: BottomSheetDialogFragment() {

    companion object {
        const val TAG = "VideoQueueBottomSheet"
        @JvmStatic
        fun newInstance(bundle: Bundle): VideoQueueBottomSheet {
            val fragment = VideoQueueBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var rootView: View
    private lateinit var viewModelFactory: AnimeInfoViewModelFactory
    private lateinit var viewModel: AnimeInfoViewModel
    private lateinit var episodeController: AnimeInfoController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.video_queue_bottom_sheet, container, false)
        Log.d("MYSELF ANIMEURL -> ", formulateUrl())
        viewModelFactory = AnimeInfoViewModelFactory(formulateUrl())
        viewModel = ViewModelProvider(this, viewModelFactory).get(AnimeInfoViewModel::class.java)
        setupRecyclerView()
        setObserver()
        return rootView
    }

    private fun formulateUrl(): String {
        val url = requireArguments().getString("URL")
        val catUrl = url!!.split("-")
        val u = catUrl.subList(0, catUrl.size - 2).joinToString(separator = "-")
        return "/category$u"
    }

    private fun setObserver() {

        viewModel.animeInfoModel.observe(viewLifecycleOwner, Observer {
            it?.let {
                episodeController.setAnime(it.animeTitle)
            }
        })

        viewModel.episodeList.observe(viewLifecycleOwner, Observer {
            it?.let {
                episodeController.setData(it)
            }
        })
    }

    private fun setupRecyclerView() {
        episodeController = AnimeInfoController()
        episodeController.spanCount = Utils.calculateNoOfColumns(requireContext(), 150f)
        rootView.videoQueueBottomSheet.adapter = episodeController.adapter
        rootView.videoQueueBottomSheet.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        dialog?.show()
    }

    override fun onResume() {
        super.onResume()
        if(episodeController.isWatchedHelperUpdated()){
            episodeController.setData(viewModel.episodeList.value)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
            val behaviour = BottomSheetBehavior.from(sheet)
            behaviour.isHideable = false
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            behaviour.setPeekHeight(126, true)
            behaviour.isDraggable = true
            behaviour.isFitToContents = true
            behaviour.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            behaviour.setPeekHeight(0)
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                    TODO("Not yet implemented")
                }

            })
        }
        return dialog
    }
}