package io.github.oscarhbrs.stundenplan.mensa

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate

class MensaParserTest {

    private fun resource(name: String): String =
        checkNotNull(javaClass.classLoader.getResource(name)) { "Missing test resource $name" }.readText()

    private val date = LocalDate(2026, 10, 1)

    @Test
    fun parsesCategoriesInPageOrder() {
        val day = MensaParser.parseDay(date, resource("day-with-meals.html"))
        assertEquals(
            listOf("Hauptgericht", "Beilagen", "Suppe & Eintopf", "Buffet"),
            day.categories.map { it.name }
        )
    }

    @Test
    fun parsesMealDetails() {
        val day = MensaParser.parseDay(date, resource("day-with-meals.html"))
        val meal = day.categories.first().meals.first()

        assertEquals("Blumenkohl-Möhren-Paprika-Pfanne mit Zartweizen", meal.name)
        assertEquals("2,15 €", meal.studentPrice)
        assertEquals(listOf("Knoblauch", "Vegan", "fleischlose Kost"), meal.labels)
        assertEquals(listOf("Knoblauch (Kno)", "Gluten (40)", "Soja (45)", "Weizen (40a)"), meal.allergens)
        assertEquals(emptyList(), meal.additives)
        assertEquals(
            Co2Info(
                grams = 287,
                rating = Co2Rating.GREEN,
                headline = "Mindestens 50% besser als der Durchschnitt.",
                description = "Liegt im Ziel einer klimafreundlichen Ernährung."
            ),
            meal.co2
        )
    }

    @Test
    fun parsesAdditivesAndOtherRatings() {
        val day = MensaParser.parseDay(date, resource("day-with-meals.html"))
        val soup = day.categories.first { it.name == "Suppe & Eintopf" }.meals.first()
        assertEquals(listOf("konserviert (2)"), soup.additives)

        val pork = day.categories.first().meals[1]
        assertEquals(Co2Rating.ORANGE, pork.co2?.rating)
        assertTrue("Schweinefleisch" in pork.labels)
    }

    @Test
    fun parsesNotices() {
        val day = MensaParser.parseDay(date, resource("day-with-meals.html"))
        assertEquals("Mensa-Deal der Woche", day.notices.first().title)
        assertEquals(
            "Vegane/Vegetarische Hauptkomponente mit einem Softgetränk kombinieren und 15% Rabatt sichern!",
            day.notices.first().text
        )
        assertEquals(1, day.notices.size)
    }

    @Test
    fun parsesSpecialCategory() {
        val day = MensaParser.parseDay(date, resource("day-special.html"))
        assertTrue(day.categories.any { it.name == "Unser Spezial" })
    }

    @Test
    fun closedDayHasNoMealsAndNoNotices() {
        val day = MensaParser.parseDay(date, resource("day-closed.html"))
        assertEquals(emptyList(), day.categories)
        assertEquals(emptyList(), day.notices)
    }

    @Test
    fun parsesPage() {
        val page = MensaParser.parsePage(resource("page.html"))
        assertEquals("1", page.canteenUid)
        assertEquals(LocalDate(2026, 9, 30), page.dates.first())
        assertEquals(10, page.dates.size)
        assertEquals(
            listOf(
                OpeningHours("Mensa", "Montag - Freitag", "11:30 - 14:00"),
                OpeningHours("Café koffe-In", "Montag - Freitag", "08:00 - 14:00")
            ),
            page.openingHours
        )
        assertEquals("Grantham-Allee 20, 53757 Sankt Augustin", page.address)
    }

    @Test
    fun planSurvivesJsonRoundTrip() {
        val plan = MensaPlan(
            fetchedAtEpochMillis = 1L,
            openingHours = emptyList(),
            address = null,
            days = listOf(MensaParser.parseDay(date, resource("day-with-meals.html")))
        )
        val json = MensaJson.encodeToString(MensaPlan.serializer(), plan)
        assertEquals(plan, MensaJson.decodeFromString(MensaPlan.serializer(), json))
    }
}
