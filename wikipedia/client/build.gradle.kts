plugins {
    id("me.madhead.kotlin-common-conventions")
}

dependencies {
    api(projects.wikipedia.model)
    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(platform(libs.kotlinx.serialization.bom))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.okhttp.sse)
    implementation(libs.kotlinx.serialization.json)
}
