import org.gradle.api.internal.FeaturePreviews

enableFeaturePreview(FeaturePreviews.Feature.TYPESAFE_PROJECT_ACCESSORS.name)

rootProject.name = "learning-kafka"

include(":wikipedia:model")
include(":wikipedia:client")
include(":kafka:wikipedia-source-connector")
