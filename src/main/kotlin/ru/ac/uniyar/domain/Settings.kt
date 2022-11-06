package ru.ac.uniyar.domain

import org.http4k.format.Jackson
import java.nio.file.Path
import kotlin.io.path.isReadable

class Settings(settingsPath: Path) {
    val salt: String

    init {
        if (!settingsPath.isReadable()) throw SettingsFileError("Configuration file $settingsPath is not readable")

        val file = settingsPath.toFile()
        val jsonDocument = file.readText()
        val node = Jackson.parse(jsonDocument)

        if (!node.hasNonNull("salt")) throw SettingsFileError("Configuration file does not exist")

        salt = node["salt"].asText()
    }

}
class SettingsFileError(message: String) : RuntimeException(message)