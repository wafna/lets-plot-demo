group = "lets-plot-demo"
version = "1.1"

plugins {
    id("com.github.ben-manes.versions") version "0.53.0" apply false
}

subprojects {
    apply(plugin = "com.github.ben-manes.versions")
}

repositories {
    mavenCentral()
}
