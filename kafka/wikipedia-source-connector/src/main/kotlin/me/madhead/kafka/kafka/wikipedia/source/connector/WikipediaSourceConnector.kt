package me.madhead.kafka.kafka.wikipedia.source.connector

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.config.ConfigException
import org.apache.kafka.connect.source.SourceConnector

@Suppress("unused")
class WikipediaSourceConnector : SourceConnector() {
    private lateinit var topic: String

    override fun version() = VERSION

    override fun config(): ConfigDef = ConfigDef()
        .define(CONFIG_TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Destination topic")

    override fun taskClass() = WikipediaSourceTask::class.java

    override fun start(props: MutableMap<String, String>) {
        topic = props[CONFIG_TOPIC] ?: throw ConfigException("Missing $CONFIG_TOPIC configuration parameter")
    }

    override fun taskConfigs(maxTasks: Int): List<Map<String, String>> = listOf(
        mapOf(
            CONFIG_TOPIC to topic
        )
    )

    override fun stop() {}
}
