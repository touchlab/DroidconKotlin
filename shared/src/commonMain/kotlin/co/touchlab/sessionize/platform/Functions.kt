package co.touchlab.sessionize.platform

/**
 * Current time in millis. Like Java's System.currentTimeMillis()
 */
expect fun currentTimeMillis(): Long

/**
 * Generates a unique string for use in tracking this user anonymously
 */
expect fun createUuid(): String

//This stuff is just till ktor catches up
expect fun simpleGet(url:String):String