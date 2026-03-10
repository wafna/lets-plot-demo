plugins {
    id("kotlin-web-common")
}

val letsPlotKotlinVersion: String by project

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        @Suppress("unused")
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.lets-plot:lets-plot-kotlin-js:4.12.1")
            }
        }
    }
}
