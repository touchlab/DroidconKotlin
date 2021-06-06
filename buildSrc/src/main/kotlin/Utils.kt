import org.gradle.api.Project
import java.io.File
import java.util.Properties

fun Project.signingKeyPassword(): String {
    val default = ""
    val propertiesFile: File = file("local.properties" as Object)
    if (propertiesFile.exists()) {
        val properties = Properties()
        try {
            properties.load(propertiesFile.inputStream())
            return properties.getProperty("releasePassword", default)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return default
}

fun Project.isReleaseEnabled(): Boolean {
    val file: File = file("./release.jks" as Object)
    return file.exists()
}

fun Project.isFirebaseEnabled(): Boolean {
    val file: File = file("./google-services.json" as Object)
    return file.exists()
}