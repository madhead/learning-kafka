plugins {
    id("me.madhead.kotlin-common-conventions")
    id("me.madhead.kotlin-serialization-conventions")
}

dependencies {
    implementation(platform(libs.kotlin.bom))
    implementation(platform(libs.kotlinx.serialization.bom))
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlinx.serialization.core)
}
