package co.touchlab.notepad.data

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTreeParser

object DefaultData{
    fun parseAll():List<Session> {
        val sessionDataList = ArrayList<Session>()

        val json = JsonTreeParser(defaultJson).readFully()
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

    fun parseSpeakers():List<Speaker>{
        val speakerList = ArrayList<Speaker>()

        val json = JsonTreeParser(defaultSpeakerJson).readFully()

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

    val defaultSpeakerJson = """[
  {
    "id": "69f4c795-e73e-4839-894a-9160152f8c54",
    "firstName": "Zakeel",
    "lastName": "Muhammad",
    "fullName": "Zakeel Muhammad",
    "bio": "Was born the same year as Windows 95 was released. Mobile Enthusiast. PM @ Microsoft. ",
    "tagLine": "PM @ Microsoft ",
    "profilePicture": "https://sessionize.com/image?f=6893f725d4022f2f5824f3d5ed687b66,200,200,True,False,95-e73e-4839-894a-9160152f8c54.3b1a7859-ab2d-4d53-8eec-f4d925a232ad.jpg",
    "sessions": [
      {
        "id": 57078,
        "name": "Lighting Fast Update to your Android App with React Native"
      }
    ],
    "isTopSpeaker": true,
    "links": [
      {
        "title": "Twitter",
        "url": "https://twitter.com/Zakeelmsft",
        "linkType": "Twitter"
      },
      {
        "title": "Company Website",
        "url": "http://microsoft.com",
        "linkType": "Company_Website"
      }
    ]
  },
  {
    "id": "b97dd341-5743-4f33-b57d-848ed79751b5",
    "firstName": "Keerthana",
    "lastName": "Kumar",
    "fullName": "Keerthana Kumar",
    "bio": null,
    "tagLine": null,
    "profilePicture": "https://sessionize.com/image?f=1af5bc42082f29b8afc512cbf9c40552,200,200,True,False,2860f5ce-1af6-4a82-8e41-f2f2af2a77a5.jpg",
    "sessions": [
      {
        "id": 57078,
        "name": "Lighting Fast Update to your Android App with React Native"
      }
    ],
    "isTopSpeaker": true,
    "links": []
  }

]"""

    val defaultJson = """[
  {
    "date": "2018-08-28T00:00:00",
    "rooms": [
      {
        "id": 1858,
        "name": "Room 2",
        "sessions": [
          {
            "id": "57078",
            "title": "Lighting Fast Update to your Android App with React Native",
            "description": "Waiting to get your updated source code on devices is now a thing of the past. By simply integrating React Native into your Android App, you can start to instantly deploy updates to the devices of your end users. By leveraging CodePush for React Native, you can eliminate some of the steps in your release cycle.",
            "startsAt": "2018-08-28T09:00:00",
            "endsAt": "2018-08-28T09:40:00",
            "isServiceSession": false,
            "isPlenumSession": false,
            "speakers": [
              {
                "id": "69f4c795-e73e-4839-894a-9160152f8c54",
                "name": "Zakeel Muhammad"
              },
              {
                "id": "b97dd341-5743-4f33-b57d-848ed79751b5",
                "name": "Keerthana Kumar"
              }
            ],
            "categories": [],
            "roomId": 1858,
            "room": "Room 2"
          }
        ],
        "hasOnlyPlenumSessions": false
      }
    ],
    "timeSlots": [
      {
        "slotStart": "09:00:00",
        "rooms": [
          {
            "id": 1858,
            "name": "Room 2",
            "session": {
              "id": "57078",
              "title": "Lighting Fast Update to your Android App with React Native",
              "description": "Waiting to get your updated source code on devices is now a thing of the past. By simply integrating React Native into your Android App, you can start to instantly deploy updates to the devices of your end users. By leveraging CodePush for React Native, you can eliminate some of the steps in your release cycle.",
              "startsAt": "2018-08-28T09:00:00",
              "endsAt": "2018-08-28T09:40:00",
              "isServiceSession": false,
              "isPlenumSession": false,
              "speakers": [
                {
                  "id": "69f4c795-e73e-4839-894a-9160152f8c54",
                  "name": "Zakeel Muhammad"
                },
                {
                  "id": "b97dd341-5743-4f33-b57d-848ed79751b5",
                  "name": "Keerthana Kumar"
                }
              ],
              "categories": [],
              "roomId": 1858,
              "room": "Room 2"
            },
            "index": 2
          }
        ]
      }
    ]
  }
]""".trimIndent()
}