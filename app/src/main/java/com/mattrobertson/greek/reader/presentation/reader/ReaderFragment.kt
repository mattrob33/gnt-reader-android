package com.mattrobertson.greek.reader.presentation.reader

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.presentation.BottomNavHostFragmentDirections
import com.mattrobertson.greek.reader.presentation.util.ScreenState
import com.mattrobertson.greek.reader.swipe.SwipeDetector
import com.mattrobertson.greek.reader.swipe.Swipeable
import com.mattrobertson.greek.reader.webview.ReaderJsInterface
import com.mattrobertson.greek.reader.webview.ScrollObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.reader.*


@AndroidEntryPoint
class ReaderFragment : Fragment() {

    private val viewModel by activityViewModels<ReaderViewModel>()

    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

    private lateinit var toolbarTitle: TextView

    private lateinit var loadingProgress: ProgressBar

    private lateinit var bottomSheet: NestedScrollView
    private lateinit var tvConcordance: TextView
    private lateinit var tvDef: TextView
    private lateinit var tvLex: TextView

    private var audioMenuItem: MenuItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.reader, container, false)
        setHasOptionsMenu(true)
        return root
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbarTitle = requireActivity().findViewById(R.id.toolbar_reader_title)

        loadingProgress = requireActivity().findViewById(R.id.loading_progress)

        bottomSheet = requireActivity().findViewById(R.id.bottomSheet)
        tvConcordance = requireActivity().findViewById(R.id.tvConcordance)
        tvDef = requireActivity().findViewById(R.id.tvDef)
        tvLex = requireActivity().findViewById(R.id.tvLex)

        bottomSheet.visibility = View.VISIBLE

        val swipeHandler = object: Swipeable {
            override fun onSwipeLeft() = viewModel.nextChapter()
            override fun onSwipeRight() = viewModel.prevChapter()
        }

        val gestureDetector = GestureDetector(requireContext(), SwipeDetector(swipeHandler))

        webview_reader.apply {
            setBackgroundColor(Color.argb(1, 0, 0, 0))
            addJavascriptInterface(ReaderJsInterface(viewModel), "ReaderApp")

            settings.javaScriptEnabled = true

            webViewClient = object: WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    loadingProgress.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    loadingProgress.visibility = View.GONE
                }
            }

            setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
            }

            observer = object: ScrollObserver {
                override fun onScroll() {
                    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }

        val nightModeFlags = requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        viewModel.isDarkTheme = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            if (viewModel.isDarkTheme) {
                WebSettingsCompat.setForceDark(webview_reader.settings, WebSettingsCompat.FORCE_DARK_ON)
            } else {
                WebSettingsCompat.setForceDark(webview_reader.settings, WebSettingsCompat.FORCE_DARK_OFF)
            }
        }

        tvConcordance.movementMethod = LinkMovementMethod.getInstance()

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        mBottomSheetBehavior.peekHeight = 400
        mBottomSheetBehavior.isHideable = true

        toolbarTitle.setOnClickListener {
            requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
                BottomNavHostFragmentDirections.toRefPicker()
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.navigateBack()
            }
        })

        subscribeUI()
    }

    override fun onResume() {
        super.onResume()
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_reader, menu)
        audioMenuItem = menu.findItem(R.id.menu_audio)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_audio -> viewModel.toggleAudioPlayback()
        }
        return true
    }

    private fun subscribeUI() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                ScreenState.LOADING -> {
                    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    loadingProgress.visibility = View.VISIBLE
                }
                ScreenState.READY -> {
                }
            }
        }

        viewModel.title.observe(viewLifecycleOwner) {
            toolbarTitle.text = it
        }

        viewModel.html.observe(viewLifecycleOwner) { html ->
            webview_reader.loadDataWithBaseURL("app:html", html, "text/html", "utf-8", null)
        }

        viewModel.glossInfo.observe(viewLifecycleOwner) { nullableGlossInfo ->
            nullableGlossInfo?.let { glossInfo ->
                var defEntry = glossInfo.gloss

                if (glossInfo.parsing.isNotBlank())
                    defEntry += "\n${glossInfo.parsing}"

                tvDef.text = defEntry
                tvLex.text = glossInfo.lex

                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        viewModel.concordanceInfo.observe(viewLifecycleOwner) { concordanceInfo ->
            tvConcordance.text = concordanceInfo ?: ""
        }

        viewModel.showConcordanceScreenForLex.observe(viewLifecycleOwner) { lex ->
            launchConcordanceScreen(lex)
        }

        viewModel.audioReady.observe(viewLifecycleOwner) { ready ->
            val icon = if(ready) {
                ResourcesCompat.getDrawable(resources, R.drawable.ic_audio_stop, requireContext().theme)
            }
            else {
                ResourcesCompat.getDrawable(resources, R.drawable.ic_audio_play, requireContext().theme)
            }

            icon!!.mutate().colorFilter = PorterDuffColorFilter(resources.getColor(R.color.textColor), PorterDuff.Mode.SRC_ATOP)

            audioMenuItem?.icon = icon
        }
    }

    private fun launchConcordanceScreen(lex: String) {
        requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
            ReaderFragmentDirections.toConcordance(lex)
        )
    }
}