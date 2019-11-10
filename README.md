# mapsnav (aka Wikimap)

Android app that shows Wikipedia articles near you on a map.

Due to time pressure, there are no unit tests. Ouch!
Nevertheless, the app is architected with testability in mind.

Technologies used:
* Dagger
* RxJava
* Retrofit (with Moshi)
* Kotlin

UX tips:
* Tap on the map to show/hide the bottom sheet

Issues:
* The Wikipedia images are mixed. It has photos and useless images intermixed. We filter out SVGs and PNGs to mitigate this partially until a better solution is found.
