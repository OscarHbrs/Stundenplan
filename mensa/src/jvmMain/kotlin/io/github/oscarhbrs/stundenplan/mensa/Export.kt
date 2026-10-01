package io.github.oscarhbrs.stundenplan.mensa

import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking

/** Writes the current menu as JSON, e.g. `./gradlew :mensa:jvmRun --args=path/to/mensa.json`. */
fun main(args: Array<String>) {
    val output = File(args.firstOrNull() ?: "mensa.json")
    val client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .build()

    val plan = runBlocking {
        MensaScraper.loadPlan { url ->
            val request = HttpRequest.newBuilder(URI(url)).timeout(Duration.ofSeconds(30)).build()
            val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await()
            check(response.statusCode() == 200) { "HTTP ${response.statusCode()} for $url" }
            response.body()
        }
    }
    check(plan.days.isNotEmpty()) { "No days found on ${MensaScraper.PAGE_URL}" }

    output.absoluteFile.parentFile.mkdirs()
    output.writeText(MensaJson.encodeToString(MensaPlan.serializer(), plan))
    println("Wrote ${plan.days.size} days, ${plan.days.sumOf { day -> day.categories.sumOf { it.meals.size } }} meals to $output")
}
