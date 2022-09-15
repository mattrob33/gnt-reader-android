package com.mattrobertson.greek.reader.tutorial

import android.os.Build.VERSION.SDK_INT
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.LocalImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.mattrobertson.greek.reader.ui.lib.MaxSizeBox
import com.mattrobertson.greek.reader.ui.lib.MaxSizeColumn
import com.mattrobertson.greek.reader.ui.lib.VSpacer
import com.mattrobertson.greek.reader.ui.lib.sideMargins
import com.mattrobertson.greek.reader.ui.theme.Fonts

@OptIn(ExperimentalPagerApi::class)
@Composable fun TutorialScreen(
    onDismiss: () -> Unit
) {

    val pages = remember {
        listOf(
            TutorialPageData(
                title = "Word Insights",
                description = "Tap on a word to see its gloss and parsing, plus a list of all occurrences.",
                gif = R.drawable.tutorial_gloss
            ),
            TutorialPageData(
                title = "Table of Contents",
                description = "Quickly navigate to other chapters with the table of contents.",
                gif = R.drawable.tutorial_contents
            ),
            TutorialPageData(
                title = "Vocabulary",
                description = "View all vocabulary for the current chapter, by frequency within the GNT.",
                gif = R.drawable.tutorial_vocab
            ),
            TutorialPageData(
                title = "Listen",
                description = "Listen to audio narration with erasmian or modern pronunciation.",
                gif = R.drawable.tutorial_audio
            ),
            TutorialPageData(
                title = "Settings",
                description = "Customize to your liking with 20 fonts to choose from.",
                gif = R.drawable.tutorial_settings
            )
        )
    }

    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == pagerState.pageCount - 1) {
            onDismiss()
        }
    }

    MaxSizeBox {
        HorizontalPager(
            count = pages.size + 2,
            state = pagerState,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 20.dp)
        ) { pageNum ->
            when (pageNum) {
                0 -> TutorialStartPage()
                pages.size -> TutorialEndPage()
                pages.size + 1 -> FinishTutorial()
                else -> TutorialPage(pages[pageNum])
            }
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .height(70.dp)
                .width(80.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Dismiss",
                tint = MaterialTheme.colors.onBackground
            )
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = MaterialTheme.colors.primary,
            inactiveColor = Color.Gray,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )
    }
}

@Composable private fun TutorialStartPage() {
    MaxSizeColumn(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .padding(horizontal = sideMargins() * 3)
    ) {

        Text(
            text = "GNT",
            fontFamily = Fonts.Cardo,
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground
        )

        Text(
            text = "Reader",
            fontFamily = Fonts.Cardo,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground
        )

        VSpacer(100.dp)

        Text(
            text = buildAnnotatedString {
                append("Meet the new and improved GNT Reader.\n\nSwipe to learn more.")
                withStyle(
                    SpanStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                ) {
                    append("→")
                }
            },
            fontFamily = FontFamily.Default,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground
        )
    }
}

@Composable private fun TutorialEndPage() {
    MaxSizeColumn(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .padding(horizontal = sideMargins() * 3)
    ) {
        Text(
            text = buildAnnotatedString {
                append("Read")
                withStyle(
                    SpanStyle(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black
                    )
                ) {
                    append(" →")
                }
            },
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(
                top = 0.dp,
                bottom = 8.dp,
                start = 16.dp,
                end = 8.dp
            )
        )
    }
}


@Composable private fun FinishTutorial() {
    MaxSizeColumn(
        modifier = Modifier.background(MaterialTheme.colors.background)
    ) {}
}

@Composable private fun TutorialPage(
    data: TutorialPageData
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(horizontal = 20.dp)
    ) {
        val (title, image, description) = createRefs()

        Title(
            title = data.title,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top, margin = 60.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Image(
            drawable = data.gif,
            modifier = Modifier.constrainAs(image) {
                top.linkTo(title.bottom, margin = 20.dp)
                bottom.linkTo(description.top, margin = 20.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                height = Dimension.fillToConstraints
            }
        )

        Description(
            text = data.description,
            modifier = Modifier.constrainAs(description) {
                bottom.linkTo(parent.bottom, margin = 40.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

@Composable private fun Title(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontFamily = FontFamily.Serif,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.onBackground,
        modifier = modifier
    )
}

@Composable private fun Description(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontFamily = FontFamily.Default,
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.onBackground,
        modifier = modifier
    )
}

@Composable private fun Image(
    @DrawableRes drawable: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    CompositionLocalProvider(LocalImageLoader provides imageLoader) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(drawable)
                .build(),
            contentDescription = null,
            modifier = modifier.then(
                Modifier
                    .background(
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(20.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        )
    }
}

private data class TutorialPageData(
    val title: String,
    val description: String,
    @DrawableRes val gif: Int
)