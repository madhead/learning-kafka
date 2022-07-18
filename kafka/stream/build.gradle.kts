plugins {
    id("me.madhead.kotlin-common-conventions")
    application
}

dependencies {
    implementation(platform(libs.kotlinx.serialization.bom))
    implementation(platform(libs.log4j.bom))
    implementation(libs.kafka.streams)
    implementation(projects.wikipedia.model)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.log4j.slf4j.impl)
}

application {
    mainClass.set("me.madhead.kafka.kafka.stream.MainKt")
}
