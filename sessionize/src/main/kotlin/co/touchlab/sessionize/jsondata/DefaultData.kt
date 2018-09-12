package co.touchlab.sessionize.jsondata

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTreeParser

object DefaultData{
    fun parseSchedule(scheduleJson:String):List<Session> {
        val sessionDataList = ArrayList<Session>()

        val json = JsonTreeParser(scheduleJson).readFully()
        (json as JsonArray).content.forEach {
            (it as JsonObject).getArray("rooms").forEach {
                (it as JsonObject).getArray("sessions").forEach {
                    val session = it as JsonObject

                    val speakersList = ArrayList<SessionSpeaker>()

                    val jsonSpeakers = session.getArray("speakers")
                    jsonSpeakers.content.forEach {
                        val spakerJson = it as JsonObject
                        speakersList.add(SessionSpeaker(
                                spakerJson.getPrimitive("id").content,
                                spakerJson.getPrimitive("name").content
                        ))
                    }

                    val sessionData = Session(
                            session.getPrimitive("id").content,
                            session.getPrimitive("title").content,
                            session.getPrimitive("description").content,
                            session.getPrimitive("startsAt").content,
                            session.getPrimitive("endsAt").content,
                            session.getPrimitive("isServiceSession").boolean,
                            speakersList,
                            session.getPrimitive("roomId").intt,
                            session.getPrimitive("room").content
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
            val linksJson = speakerJson.getArray("links")
            val links = ArrayList<SpeakerLink>()
            linksJson.forEach {
                val linkJson = (it as JsonObject)
                links.add(SpeakerLink(
                        linkJson.getPrimitive("title").content,
                        linkJson.getPrimitive("url").content,
                        linkJson.getPrimitive("linkType").content
                ))
            }
            val speaker = Speaker(
                    speakerJson.getPrimitive("id").content,
                    speakerJson.getPrimitive("firstName").content,
                    speakerJson.getPrimitive("lastName").content,
                    speakerJson.getPrimitive("fullName").content,
                    speakerJson.getPrimitive("bio").content,
                    speakerJson.getPrimitive("tagLine").content,
                    speakerJson.getPrimitive("profilePicture").content,
                    links
            )
            speakerList.add(speaker)
        }

        return speakerList
    }

    fun parseSponsors(sponsorJsonString:String):List<SponsorGroup>{
        val json = JsonTreeParser(sponsorJsonString).readFully()
        val sponsorGroups = ArrayList<SponsorGroup>()
        (json as JsonArray).forEach {
            val sponsorGroupJson = it as JsonObject
            val groupName = sponsorGroupJson.getPrimitive("groupName").content
            val sponsorsJson = sponsorGroupJson.getArray("sponsors")
            val sponsorList = ArrayList<Sponsor>()

            sponsorsJson.forEach {
                val sponsorJson = it as JsonObject
                sponsorList.add(Sponsor(
                        sponsorJson.getPrimitive("name").content,
                        sponsorJson.getPrimitive("url").content,
                        sponsorJson.getPrimitive("icon").content
                ))
            }

            sponsorGroups.add(SponsorGroup(groupName, sponsorList))
        }

        return sponsorGroups
    }
}