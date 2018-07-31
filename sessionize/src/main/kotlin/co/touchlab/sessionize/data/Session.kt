package co.touchlab.sessionize.data

data class Session(
        val id:String,
        val title:String,
        val description:String,
        val startsAt:String,
        val endsAt:String,
        val serviceSession:Boolean,
        val speakers:List<SessionSpeaker>,
        val roomId:Int,
        val room:String
)

data class SessionSpeaker(
        val id:String,
        val name:String
)

data class Speaker(
        val id:String,
        val firstName:String,
        val lastName:String,
        val fullName:String,
        val bio:String,
        val tagLine:String,
        val profilePicture:String,
        val links:List<SpeakerLink>
)

data class SpeakerLink(
        val title:String,
        val url:String,
        val linkType:String
)

/*
"title": "Twitter",
        "url": "https://twitter.com/ykro",
        "linkType": "Twitter"
 */