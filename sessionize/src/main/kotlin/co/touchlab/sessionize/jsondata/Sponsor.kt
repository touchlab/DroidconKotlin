package co.touchlab.sessionize.jsondata

data class Sponsor(val name:String, val url:String, val icon:String)

data class SponsorGroup(val groupName:String, val sponsors:List<Sponsor>)