package com.mattrobertson.greek.reader.ui.settings

import com.mattrobertson.greek.reader.ui.settings.FontFamily.*
import com.mattrobertson.greek.reader.ui.theme.Fonts

sealed class FontFamily(
	val id: String,
	val displayName: String
) {

	object SblGreek: FontFamily("sbl-greek", "SBL Greek")
	object Cardo: FontFamily("cardo", "Cardo")
	object Gentium: FontFamily("gentium", "Gentium")
	object Lato: FontFamily("lato", "Lato")
	object Brill: FontFamily("brill", "Brill")

	object Andika: FontFamily("andika", "Andika")
	object Charis: FontFamily("charis", "Charis")
	object DidotClassic: FontFamily("didot-classic", "Didot Classic")
	object Doulos: FontFamily("doulos", "Doulos")
	object Galatia: FontFamily("galatia", "Galatia")

	object Heraklit: FontFamily("heraklit", "Heraklit")
	object Porson: FontFamily("porson", "Porson")
	object Bodoni: FontFamily("bodoni", "Bodoni")
	object Olga: FontFamily("olga", "Olga")
	object NeoHellenic: FontFamily("neo-hellenic", "NeoHellenic")
	object Artemisia: FontFamily("artemisia", "Artemisia")
	object Elpis: FontFamily("elpis", "Elpis")
	object Galatea: FontFamily("galatea", "Galatea")
	object Orpheus: FontFamily("orpheus", "Orpheus")
	object Philostratos: FontFamily("philostratos", "Philostratos")

	companion object {
		val default = Cardo

		val allFonts = listOf(
			Andika,
			Artemisia,
			Bodoni,
			Brill,
			Charis,
			DidotClassic,
			Doulos,
			Elpis,
			Galatea,
			Galatia,
			Gentium,
			Heraklit,
			Lato,
			NeoHellenic,
			Olga,
			Orpheus,
			Philostratos,
			Porson,
			SblGreek,
		)

		fun fromId(id: String): FontFamily {
			allFonts.forEach { font ->
				if (font.id == id) {
					return font
				}
			}
			throw IllegalArgumentException("Invalid font: $id")
		}
	}
}

fun FontFamily.getComposeFontFamily(): androidx.compose.ui.text.font.FontFamily {
	return when (this) {
		Andika -> Fonts.Andika
		Artemisia -> Fonts.Artemisia
		Bodoni -> Fonts.Bodoni
		Brill -> Fonts.Brill
		Cardo -> Fonts.Cardo
		Charis -> Fonts.Charis
		DidotClassic -> Fonts.DidotClassic
		Doulos -> Fonts.Doulos
		Elpis -> Fonts.Elpis
		Galatea -> Fonts.Galatea
		Galatia -> Fonts.Galatia
		Gentium -> Fonts.Gentium
		Heraklit -> Fonts.Heraklit
		Lato -> Fonts.Lato
		NeoHellenic -> Fonts.NeoHellenic
		Olga -> Fonts.Olga
		Orpheus -> Fonts.Orpheus
		Philostratos -> Fonts.Philostratos
		Porson -> Fonts.Porson
		SblGreek -> Fonts.SblGreek
	}
}