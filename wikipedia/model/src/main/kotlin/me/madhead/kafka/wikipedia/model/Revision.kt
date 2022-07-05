package me.madhead.kafka.wikipedia.model

import kotlinx.serialization.Serializable

@Serializable
data class Revision(
    val old: Int?,
    val new: Int?,
)
