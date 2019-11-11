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
* The Wikipedia API returns all kinds of images of an article. It has photos and useless images intermixed. We filter out SVGs and PNGs to mitigate this partially until a better solution is found.
* The Wikipedia API has a limit of 10km radius limit for querying articles around a location. This means that if you zoom out the map  you'll see a large concentration of markers around the center. https://en.wikipedia.org/w/api.php?action=help&modules=query%2Bgeosearch
