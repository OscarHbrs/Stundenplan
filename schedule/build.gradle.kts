import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
}

// The timetable built into the apps (offline, first start); the deploy workflow publishes newer ones.
val generateBuiltInSchedule by tasks.registering {
    val input = layout.projectDirectory.file("schedules.json")
    val outputDir = layout.buildDirectory.dir("generated/builtInSchedule")
    inputs.file(input)
    outputs.dir(outputDir)
    doLast {
        // Split, because the JVM limits string constants to 64 KB.
        val chunks = input.asFile.readText().chunked(16_000).joinToString(",\n") { chunk ->
            val escaped = chunk.replace("\\", "\\\\").replace("\"", "\\\"").replace("$", "\\$")
                .replace("\n", "\\n").replace("\r", "")
            "    \"$escaped\""
        }
        val file = outputDir.get().file("io/github/oscarhbrs/stundenplan/schedule/BuiltInScheduleJson.kt").asFile
        file.parentFile.mkdirs()
        file.writeText(
            "package io.github.oscarhbrs.stundenplan.schedule\n\n" +
                "internal val BUILT_IN_SCHEDULE_JSON: String = listOf(\n$chunks\n).joinToString(\"\")\n"
        )
    }
}

kotlin {
    android {
        namespace = "io.github.oscarhbrs.stundenplan.schedule"
        compileSdk = 37
        minSdk = 26
    }

    jvm {
        mainRun {
            mainClass.set("io.github.oscarhbrs.stundenplan.schedule.ExportKt")
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(generateBuiltInSchedule)
        }
        commonMain.dependencies {
            api(libs.kotlinx.datetime)
            api(libs.kotlinx.serialization.json)
            implementation(libs.ksoup)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
