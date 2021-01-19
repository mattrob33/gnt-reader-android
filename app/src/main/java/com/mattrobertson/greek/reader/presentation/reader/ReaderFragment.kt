package com.mattrobertson.greek.reader.presentation.reader

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.mattrobertson.greek.reader.webview.ReaderJsInterface
import com.mattrobertson.greek.reader.swipe.SwipeDetector
import com.mattrobertson.greek.reader.swipe.Swipeable
import com.mattrobertson.greek.reader.util.getNavigationResult
import com.mattrobertson.greek.reader.webview.ScrollObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.reader.*


@AndroidEntryPoint
class ReaderFragment : Fragment() {

    private val viewModel by activityViewModels<ReaderViewModel>()

    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

    private lateinit var toolbarTitle: TextView

    private lateinit var bottomSheet: NestedScrollView
    private lateinit var tvConcordance: TextView
    private lateinit var tvDef: TextView
    private lateinit var tvLex: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.reader, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbarTitle = requireActivity().findViewById(R.id.toolbar_reader_title)

        bottomSheet = requireActivity().findViewById(R.id.bottomSheet)
        tvConcordance = requireActivity().findViewById(R.id.tvConcordance)
        tvDef = requireActivity().findViewById(R.id.tvDef)
        tvLex = requireActivity().findViewById(R.id.tvLex)

        bottomSheet.visibility = View.VISIBLE

        webview_reader.settings.apply {
            javaScriptEnabled = true
        }

        val nightModeFlags = requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            if (isNightMode) {
                WebSettingsCompat.setForceDark(webview_reader.settings, WebSettingsCompat.FORCE_DARK_ON)
            } else {
                WebSettingsCompat.setForceDark(webview_reader.settings, WebSettingsCompat.FORCE_DARK_OFF)
            }
        }

        webview_reader.apply {
            setBackgroundColor(Color.argb(1, 0, 0, 0))
            addJavascriptInterface(ReaderJsInterface(viewModel), "ReaderApp")
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

        val swipeHandler = object: Swipeable {
            override fun onSwipeLeft() {
                viewModel.nextChapter()
            }

            override fun onSwipeRight() {
                viewModel.prevChapter()
            }
        }

        val swipeDetector = SwipeDetector(swipeHandler)

        val gestureDetector = GestureDetector(requireContext(), swipeDetector)

        webview_reader.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }

        webview_reader.addScrollObserver(object: ScrollObserver {
            override fun onScroll() {
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun subscribeUI() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                ScreenState.LOADING -> {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
                    // TODO : show progress indicator
                }
                ScreenState.READY -> {
                    // TODO : hide progress indicator
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

        getNavigationResult("book")?.observe(viewLifecycleOwner) { book ->
            val chapter = getNavigationResult("chapter")?.value ?: 0

            Toast.makeText(requireContext(), "Ref is $book, $chapter", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchConcordanceScreen(lex: String) {
        requireActivity().findNavController(R.id.core_nav_host_fragment).navigate(
            ReaderFragmentDirections.toConcordance(lex)
        )
    }
}