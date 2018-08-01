package co.touchlab.sessionize.data

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTreeParser

object DefaultData{
    fun parseAll(scheduleJson:String):List<Session> {
        val sessionDataList = ArrayList<Session>()

        val json = JsonTreeParser(scheduleJson).readFully()
        (json as JsonArray).content.forEach {
            (it as JsonObject).getAsArray("rooms").forEach {
                (it as JsonObject).getAsArray("sessions").forEach {
                    val session = it as JsonObject

                    val speakersList = ArrayList<SessionSpeaker>()

                    val jsonSpeakers = session.getAsArray("speakers")
                    jsonSpeakers.content.forEach {
                        val spakerJson = it as JsonObject
                        speakersList.add(SessionSpeaker(
                                spakerJson.getAsValue("id").content,
                                spakerJson.getAsValue("name").content
                        ))
                    }

                    val sessionData = Session(
                            session.getAsValue("id").content,
                            session.getAsValue("title").content,
                            session.getAsValue("description").content,
                            session.getAsValue("startsAt").content,
                            session.getAsValue("endsAt").content,
                            session.getAsValue("isServiceSession").asBoolean,
                            speakersList,
                            session.getAsValue("roomId").asInt,
                            session.getAsValue("room").content
                    )

                    sessionDataList.add(sessionData)
                }
            }
        }

        return sessionDataList
    }

    fun parseSpeakers(speakerJson:String):List<Speaker>{
        val speakerList = ArrayList<Speaker>()

        val json = JsonTreeParser(speakerJson).readFully()

        (json as JsonArray).forEach {
            val speakerJson = it as JsonObject
            val linksJson = speakerJson.getAsArray("links")
            val links = ArrayList<SpeakerLink>()
            linksJson.forEach {
                val linkJson = (it as JsonObject)
                links.add(SpeakerLink(
                        linkJson.getAsValue("title").content,
                        linkJson.getAsValue("url").content,
                        linkJson.getAsValue("linkType").content
                ))
            }
            val speaker = Speaker(
                    speakerJson.getAsValue("id").content,
                    speakerJson.getAsValue("firstName").content,
                    speakerJson.getAsValue("lastName").content,
                    speakerJson.getAsValue("fullName").content,
                    speakerJson.getAsValue("bio").content,
                    speakerJson.getAsValue("tagLine").content,
                    speakerJson.getAsValue("profilePicture").content,
                    links
            )
            speakerList.add(speaker)
        }

        return speakerList
    }


}