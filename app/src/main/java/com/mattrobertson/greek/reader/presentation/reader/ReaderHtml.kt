package com.mattrobertson.greek.reader.presentation.reader

val readerHtmlHead = """
<head>
	<style type="text/css">
		@font-face {
			font-family: SblGreek;
			src: url("file:///android_asset/fonts/sblgreek.ttf")
		}
		body {
			padding-left: 20px;
			padding-right: 20px;
			font-family: SblGreek;
			font-size: x-large;
			line-height: 180%;
		}
		p {
			margin: 0;
			padding: 0;
		}
		.selectedWord {
			font-weight: bold;
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
"""