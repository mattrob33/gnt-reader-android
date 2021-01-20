package com.mattrobertson.greek.reader.html

import com.mattrobertson.greek.reader.viewsettings.FontFamily
import com.mattrobertson.greek.reader.viewsettings.PageMargins
import com.mattrobertson.greek.reader.viewsettings.ViewSettings

fun wrapThemedHtml(
	htmlBody: String,
	viewSettings: ViewSettings,
	isDarkTheme: Boolean
): String {
	val bgColor = if (isDarkTheme) "#000000" else "#FFFFFF"
	val textColor = if (isDarkTheme) "#FFFFFF" else "#000000"

	val fontSize = "${viewSettings.fontSize}px"

	val lineSpacing = "${viewSettings.lineSpacing}%"

	val pageMargin = when (viewSettings.pageMargins) {
		PageMargins.NARROW -> "10px"
		PageMargins.MEDIUM -> "20px"
		PageMargins.WIDE -> "40px"
	}

	val fontFamily = when (viewSettings.fontFamily) {
		FontFamily.SBL -> "SblGreek"
		FontFamily.CARDO -> "Gentium"
		FontFamily.GENTIUM -> "Cardo"
	}

	return """<html>
	<head>
		<style type="text/css">
			@font-face {
				font-family: SblGreek;
				src: url("file:///android_asset/fonts/sblgreek.ttf")
			}
			@font-face {
				font-family: Gentium;
				src: url("file:///android_asset/fonts/gentium.ttf")
			}
			@font-face {
				font-family: Cardo;
				src: url("file:///android_asset/fonts/cardo.ttf")
			}
			body {
				background-color: $bgColor;
				color: $textColor;
				padding-left: $pageMargin;
				padding-right: $pageMargin;
				font-family: $fontFamily;
				font-size: $fontSize;
				line-height: $lineSpacing;
			}
			p {
				margin: 0;
				padding: 0;
			}
			.selectedWord {
				font-weight: bold;
			}
			sup, sub {
			  vertical-align: baseline;
			  position: relative;
			  top: -0.4em;
			}
			sub { 
			  top: 0.4em; 
			}
		</style>
		<script type="text/javascript">
	
			function scrollToAnchor(id) {
				window.location.hash = id;
			}
	
			function onWordClick(id, text, lexicalForm, codedParsing) {
				var selectedWords = document.getElementsByClassName('selectedWord');
				if (selectedWords.length > 0)
					selectedWords[0].removeAttribute('class');
				document.getElementById(id).setAttribute('class', 'selectedWord');
				ReaderApp.onWordClick(text, lexicalForm, codedParsing);
			}
	
		</script>
	</head>
	<body>
		$htmlBody
	</body>
</html>"""
}