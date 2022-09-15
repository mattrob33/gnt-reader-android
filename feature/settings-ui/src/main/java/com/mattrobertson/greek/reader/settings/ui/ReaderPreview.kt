package com.mattrobertson.greek.reader.settings.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.settings.Settings
import com.mattrobertson.greek.reader.ui.settings.getComposeFontFamily

@Composable fun ReaderPreview(
    settings: Settings
) {
    val text = getPreviewText(settings)

    Text(
        text = text,
        modifier = Modifier
            .height(250.dp)
            .padding(horizontal = 20.dp)
            .border(0.5.dp, MaterialTheme.colors.onBackground)
            .padding(horizontal = 20.dp)
            .padding(vertical = 10.dp)
    )
}

@Composable private fun getPreviewText(settings: Settings): AnnotatedString {

    val color = MaterialTheme.colors.onBackground

    return buildAnnotatedString {
        withStyle(ParagraphStyle(lineHeight = settings.fontSize * settings.lineSpacing)) {
            withStyle(
                SpanStyle(
                    color = color,
                    fontFamily = settings.font.getComposeFontFamily(),
                    fontSize = settings.fontSize,
                )
            ) {
                for (verse in previewVerses) {
                    if (settings.showVerseNumbers) {
                        withStyle(SpanStyle(fontSize = 16.sp, baselineShift = BaselineShift.Superscript)) {
                            append("${verse.verseNum} ")
                        }
                    }

                    append(verse.text)

                    if (settings.versesOnNewLines) {
                        append("\n")
                    }
                    else {
                        append(" ")
                    }
                }
            }
        }
    }
}

private data class PreviewVerse(
    val verseNum: Int,
    val text: String
)

private val previewVerses = listOf(
    PreviewVerse(1, "Ἐν ἀρχῇ ἦν ὁ λόγος, καὶ ὁ λόγος ἦν πρὸς τὸν θεόν, καὶ θεὸς ἦν ὁ λόγος."),
    PreviewVerse(2, "οὗτος ἦν ἐν ἀρχῇ πρὸς τὸν θεόν."),
    PreviewVerse(3, "πάντα δι’ αὐτοῦ ἐγένετο, καὶ χωρὶς αὐτοῦ ἐγένετο οὐδὲ ἕν. ὃ γέγονεν"),
    PreviewVerse(4, "ἐν αὐτῷ ζωὴ ἦν, καὶ ἡ ζωὴ ἦν τὸ φῶς τῶν ἀνθρώπων ·"),
    PreviewVerse(5, "καὶ τὸ φῶς ἐν τῇ σκοτίᾳ φαίνει, καὶ ἡ σκοτία αὐτὸ οὐ κατέλαβεν."),
    PreviewVerse(6, "Ἐγένετο ἄνθρωπος ἀπεσταλμένος παρὰ θεοῦ, ὄνομα αὐτῷ Ἰωάννης ·"),
    PreviewVerse(7, "οὗτος ἦλθεν εἰς μαρτυρίαν, ἵνα μαρτυρήσῃ περὶ τοῦ φωτός, ἵνα πάντες πιστεύσωσιν δι’ αὐτοῦ."),
    PreviewVerse(8, "οὐκ ἦν ἐκεῖνος τὸ φῶς, ἀλλ’ ἵνα μαρτυρήσῃ περὶ τοῦ φωτός."),
    PreviewVerse(9, "ἦν τὸ φῶς τὸ ἀληθινὸν ὃ φωτίζει πάντα ἄνθρωπον ἐρχόμενον εἰς τὸν κόσμον."),
    PreviewVerse(10, "Ἐν τῷ κόσμῳ ἦν, καὶ ὁ κόσμος δι’ αὐτοῦ ἐγένετο, καὶ ὁ κόσμος αὐτὸν οὐκ ἔγνω."),
    PreviewVerse(11, "εἰς τὰ ἴδια ἦλθεν, καὶ οἱ ἴδιοι αὐτὸν οὐ παρέλαβον."),
    PreviewVerse(12, "ὅσοι δὲ ἔλαβον αὐτόν, ἔδωκεν αὐτοῖς ἐξουσίαν τέκνα θεοῦ γενέσθαι, τοῖς πιστεύουσιν εἰς τὸ ὄνομα αὐτοῦ,"),
    PreviewVerse(13, "οἳ οὐκ ἐξ αἱμάτων οὐδὲ ἐκ θελήματος σαρκὸς οὐδὲ ἐκ θελήματος ἀνδρὸς ἀλλ’ ἐκ θεοῦ ἐγεννήθησαν."),
    PreviewVerse(14, "Καὶ ὁ λόγος σὰρξ ἐγένετο καὶ ἐσκήνωσεν ἐν ἡμῖν, καὶ ἐθεασάμεθα τὴν δόξαν αὐτοῦ, δόξαν ὡς μονογενοῦς παρὰ πατρός, πλήρης χάριτος καὶ ἀληθείας ·"),
    PreviewVerse(15, "Ἰωάννης μαρτυρεῖ περὶ αὐτοῦ καὶ κέκραγεν λέγων · Οὗτος ἦν ὃν εἶπον ⸃· Ὁ ὀπίσω μου ἐρχόμενος ἔμπροσθέν μου γέγονεν, ὅτι πρῶτός μου ἦν ·)"),
    PreviewVerse(16, "ὅτι ἐκ τοῦ πληρώματος αὐτοῦ ἡμεῖς πάντες ἐλάβομεν, καὶ χάριν ἀντὶ χάριτος ·"),
    PreviewVerse(17, "ὅτι ὁ νόμος διὰ Μωϋσέως ἐδόθη, ἡ χάρις καὶ ἡ ἀλήθεια διὰ Ἰησοῦ Χριστοῦ ἐγένετο."),
    PreviewVerse(18, "θεὸν οὐδεὶς ἑώρακεν πώποτε · μονογενὴς θεὸς ⸃ ὁ ὢν εἰς τὸν κόλπον τοῦ πατρὸς ἐκεῖνος ἐξηγήσατο.")
)