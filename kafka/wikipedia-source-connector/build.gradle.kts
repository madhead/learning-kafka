plugins {
    id("me.madhead.kotlin-common-conventions")
    id("me.madhead.shadow-conventions")
}

dependencies {
    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(platform(libs.kotlinx.serialization.bom))
    shadow(libs.connect.api)
    implementation(projects.wikipedia.client)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
}
