package co.touchlab.sessionize

import co.touchlab.sessionize.jsondata.Sponsor
import co.touchlab.sessionize.jsondata.SponsorGroup
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.orderBy
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object SponsorsModel : BaseModel(ServiceRegistry.coroutinesDispatcher) {

    suspend fun loadSponsors(
            proc: (sponsors: List<SponsorGroup>) -> Unit,
            error: (ex: Throwable) -> Unit
    ) {
        try {
            sponsorGroupsFrom(
                    Firebase.firestore
                            .collection("sponsors-sf-2019")
                            .orderBy("displayOrder")
                            .get()
                            .documents
            )
        } catch (e: Throwable) {
            ServiceRegistry.softExceptionCallback(e, "loadSponsorsFromServer failed")
            error(e)
        }
    }

    private fun sponsorGroupsFrom(documentSnapshots: List<DocumentSnapshot>): List<SponsorGroup> {
        return documentSnapshots.map {
            sponsorGroupFrom(it)
        }
    }

    private fun sponsorGroupFrom(documentSnapshot: DocumentSnapshot): SponsorGroup {
        val groupName = documentSnapshot.id.capitalize()
        val level: Map<String, Any?> = documentSnapshot.data()

        @Suppress("UNCHECKED_CAST")
        val sponsorsList = level["sponsors"] as List<Map<String, String>>
        val sponsors = sponsorsList.map {
            Sponsor(
                    name = it["name"] as String,
                    groupName = groupName,
                    url = it["url"] as String,
                    icon = it["icon"] as String,
                    sponsorId = it["sponsorId"]
            )
        }

        return SponsorGroup(groupName, sponsors)
    }
}

fun sponsorClicked(sponsor: Sponsor) {
    ServiceRegistry.analyticsApi.logEvent("sponsor_clicked", mapOf(Pair("id", sponsor.sponsorId.toString()), Pair("name", sponsor.name)))
}
