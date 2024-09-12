plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
}

allprojects {
    version = findProperty("VERSION_NAME").toString()
    group = findProperty("GROUP").toString()
}

val clean by tasks.registering(type = Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
