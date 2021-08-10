package com.mattrobertson.greek.reader.compose.settings

data class ViewSettings(
	var showVerseNumbers: Boolean = true,
	var showVersesNewLines: Boolean = false,
	var useDarkTheme: Boolean = true,
	var pageMargins: PageMargins = PageMargins.MEDIUM,
	var fontFamily: FontFamily = FontFamily.CARDO,
	var fontSize: Int = 14,
	var lineSpacing: Int = 180
) {

	override fun equals(other: Any?): Boolean {
		if (other !is ViewSettings) return false

		return (showVerseNumbers == other.showVerseNumbers) &&
			(showVersesNewLines == other.showVersesNewLines) &&
			(useDarkTheme == other.useDarkTheme) &&
			(pageMargins == other.pageMargins) &&
			(fontFamily == other.fontFamily) &&
			(fontSize == other.fontSize) &&
			(lineSpacing == other.lineSpacing)

	}

	override fun hashCode(): Int {
		var result = showVerseNumbers.hashCode()
		result = 31 * result + showVersesNewLines.hashCode()
		result = 31 * result + useDarkTheme.hashCode()
		result = 31 * result + pageMargins.hashCode()
		result = 31 * result + fontFamily.hashCode()
		result = 31 * result + fontSize.hashCode()
		result = 31 * result + lineSpacing.hashCode()
		return result
	}

}