package io.github.oscarhbrs.stundenplan.mensa

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.LocalDate

object MensaScraper {
    const val PAGE_URL = "https://www.studierendenwerk-bonn.de/essen-trinken/mensen-cafes/mensa-sankt-augustin"

    // The endpoint the website itself calls via AJAX to fill in the menu of one day.
    private fun dayUrl(canteenUid: String, date: LocalDate) =
        "https://www.studierendenwerk-bonn.de/?type=1732731666" +
            "&tx_festwb_mealsajax%5Bdate%5D=$date" +
            "&tx_festwb_mealsajax%5Bcanteen%5D=$canteenUid" +
            "&tx_festwb_mealsajax%5Blanguage%5D=0"

    @OptIn(ExperimentalTime::class)
    suspend fun loadPlan(fetch: suspend (url: String) -> String): MensaPlan = coroutineScope {
        val page = MensaParser.parsePage(fetch(PAGE_URL))
        val days = page.dates
            .map { date -> async { MensaParser.parseDay(date, fetch(dayUrl(page.canteenUid, date))) } }
            .awaitAll()
        MensaPlan(
            fetchedAtEpochMillis = Clock.System.now().toEpochMilliseconds(),
            openingHours = page.openingHours,
            address = page.address,
            days = days
        )
    }
}
