package io.github.oscarhbrs.stundenplan.mensa

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import kotlinx.datetime.LocalDate

data class MensaPage(
    val canteenUid: String,
    val dates: List<LocalDate>,
    val openingHours: List<OpeningHours>,
    val address: String?
)

object MensaParser {

    // Diet markers that the website also lists as "allergens".
    private val nonAllergenCodes = setOf("(Veg)", "(V)")

    fun parsePage(html: String): MensaPage {
        val doc = Ksoup.parse(html)
        val dates = doc.select("select.select--single--date option")
            .mapNotNull { runCatching { LocalDate.parse(it.attr("value")) }.getOrNull() }
        val openingHours = doc.select(".canteen__info__body__ohg__openinghours").mapNotNull { block ->
            val days = block.selectFirst(".openinghours--days") ?: return@mapNotNull null
            OpeningHours(
                place = days.selectFirst("span")?.cleanText().orEmpty(),
                days = days.ownText().normalize(),
                time = block.selectFirst(".openinghours--time")?.cleanText().orEmpty()
            )
        }
        val address = doc.selectFirst(".canteen__info__body__address")?.let { element ->
            Ksoup.parse(element.html().replace(Regex("<br\\s*/?>"), ", ")).text().normalize().replace(" ,", ",")
        }
        return MensaPage(
            canteenUid = doc.selectFirst("#selectedCanteenUid")?.attr("value")?.ifBlank { null } ?: "1",
            dates = dates,
            openingHours = openingHours,
            address = address?.ifBlank { null }
        )
    }

    fun parseDay(date: LocalDate, html: String): MensaDay {
        val doc = Ksoup.parse(html)
        val categories = doc.select(".menus__container h2").mapNotNull { heading ->
            val results = heading.nextElementSibling()?.takeIf { it.hasClass("menus__results") }
                ?: return@mapNotNull null
            val meals = results.select(".menus__result").map(::parseMeal)
            if (meals.isEmpty()) null else MealCategory(heading.cleanText(), meals)
        }
        if (categories.isEmpty()) return MensaDay(date, emptyList(), emptyList())

        val notices = doc.select(".menus-notification").map { notice ->
            MensaNotice(
                title = notice.selectFirst(".menus-notification__mainline")?.cleanText().orEmpty(),
                text = notice.selectFirst(".menus-notification__subline")?.cleanText()?.ifBlank { null }
            )
        }
        return MensaDay(date, notices.filter { it.title.isNotBlank() }, categories)
    }

    private fun parseMeal(element: Element): Meal {
        val popoverSections = element.select(".menus__result__buttons .popover-content > div")
        fun section(title: String): List<String> = popoverSections
            .firstOrNull { it.selectFirst("strong")?.cleanText() == title }
            ?.select("p")?.map { it.cleanText() }
            .orEmpty()

        return Meal(
            name = element.selectFirst("h5")?.cleanText().orEmpty(),
            studentPrice = element.select("tr.menus__result__price")
                .firstOrNull { it.selectFirst("th")?.cleanText() == "Stud." }
                ?.selectFirst("td")?.cleanText(),
            labels = element.select(".menus__result_additives span[title]").map { it.attr("title").normalize() },
            allergens = section("Allergene").filterNot { entry -> nonAllergenCodes.any { entry.endsWith(it) } },
            additives = section("Zusatzstoffe"),
            co2 = element.selectFirst(".menus__result__co2")?.let(::parseCo2)
        )
    }

    private fun parseCo2(element: Element): Co2Info? {
        val grams = element.selectFirst("h3")?.text()
            ?.let { Regex("(\\d+)\\s*g").find(it)?.groupValues?.get(1)?.toIntOrNull() }
            ?: return null
        val icon = element.selectFirst("img")?.attr("src").orEmpty()
        val rating = when {
            "co2_green" in icon -> Co2Rating.GREEN
            "co2_orange" in icon -> Co2Rating.ORANGE
            "co2_red" in icon -> Co2Rating.RED
            else -> Co2Rating.UNKNOWN
        }
        val body = element.selectFirst(".popover-content-body")
        return Co2Info(
            grams = grams,
            rating = rating,
            headline = body?.selectFirst("strong")?.cleanText()?.ifBlank { null },
            description = body?.ownText()?.normalize()?.ifBlank { null }
        )
    }

    private fun Element.cleanText(): String = text().normalize()

    private fun String.normalize(): String = replace(' ', ' ').replace(Regex("\\s+"), " ").trim()
}
