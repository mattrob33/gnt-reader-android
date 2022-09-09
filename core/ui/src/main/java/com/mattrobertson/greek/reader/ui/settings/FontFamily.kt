package com.mattrobertson.greek.reader.ui.settings

sealed class FontFamily(val name: String) {

	object SblGreek: FontFamily("sbl-greek")
	object Cardo: FontFamily("cardo")
	object Gentium: FontFamily("gentium")

	companion object {
		fun fromName(name: String): FontFamily {
			return when (name) {
				SblGreek.name -> SblGreek
				Cardo.name -> Cardo
				Gentium.name -> Gentium
				else -> throw IllegalArgumentException("Invalid font: $name")
			}
		}

		val default = SblGreek
	}
}

