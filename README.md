# GNT Reader
A digital reader's edition of the Greek New Testament for Android.

#### Major Features

 - Glosses
 - Concordance
 - Vocabulary
 - Audio
 - Reading plans with daily reminders

## Reading plans (v8.1.5)

The six reading plans from v7 are available again. Start a plan, then open Settings for its daily reminder toggle, notification time, and reset action. Android may ask for notification permission (Android 13+) and exact alarm access (Android 12+). If exact alarms are not allowed, Android may deliver the reminder a little later than the selected time.

Version 8.1.1 also fixes a launch crash in optimized builds by preserving the field names required by generated protobuf messages.

Use **Read today's plan** to read all chapters assigned for the current day in one reader view. **Mark day complete** advances the plan from that view. Tapping an individual chapter in the schedule still opens that chapter in the regular reader. Words tapped in a plan reading now open the concordance sheet, just as they do in the regular reader.

The annual plan reviews only the previous day's reading. The single Reading view setting controls both the main reader and the daily-plan reader, switching each between continuous scrolling and paginated reading; paginated mode omits the page counter. The reading screen also uses a system-aware status bar so Android's status icons remain visible with current themes.

Pagination safely ignores trailing empty lines after the end of a daily reading, preventing a crash when opening longer assigned readings.

## Download

[Download the latest APK from GitHub Releases](https://github.com/claviger-pc/gnt-reader-android/releases/latest).

The v8.1.5 APK is a minified release build with unused resources removed. It is signed with a local development certificate because this fork does not have a release keystore configured; it installs as a fresh app, but cannot update an installation signed by Google Play or another key.

## App size

The v8 rewrite bundles about 20 selectable Greek fonts; v7 bundled one. The packaged database also grew from 7,979,008 bytes to 15,794,176 bytes. These app assets account for much of the larger v8 package. The initial 41 MB APK from this checkout was an unshrunk debug build. The optimized v8.1.1 release APK is about 21 MB with R8 code shrinking and resource shrinking enabled.

The app is [available on the Google Play Store](https://play.google.com/store/apps/details?id=com.mattrobertson.greek.reader).
