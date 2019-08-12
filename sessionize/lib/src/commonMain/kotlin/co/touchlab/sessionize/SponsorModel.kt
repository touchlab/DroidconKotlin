package co.touchlab.sessionize

import co.touchlab.firebase.firestore.DocumentSnapshot
import co.touchlab.firebase.firestore.Source
import co.touchlab.firebase.firestore.addSuccessListener
import co.touchlab.firebase.firestore.collection
import co.touchlab.firebase.firestore.data_
import co.touchlab.firebase.firestore.documents_
import co.touchlab.firebase.firestore.getFirebaseInstance
import co.touchlab.firebase.firestore.get_
import co.touchlab.firebase.firestore.id
import co.touchlab.sessionize.jsondata.SponsorGroup
import kotlin.native.concurrent.ThreadLocal
import co.touchlab.sessionize.jsondata.Sponsor

@ThreadLocal
object SponsorsModel : BaseModel(ServiceRegistry.coroutinesDispatcher) {
    fun loadSponsorsFromCache(proc: (sponsors: List<SponsorGroup>) -> Unit) {
        getFirebaseInstance()
                .collection("sponsors")
                .get_(Source.CACHE)
                .addListeners(successListener = {
                    if (it.documents_.isNullOrEmpty()) {
                        loadSponsorsFromServer(proc)
                    } else {
                        proc(sponsorGroupsFrom(it.documents_))
                    }
                }, failureListener = {
                    loadSponsorsFromServer(proc)
                    println("Firestore cache exception: ${it.message}")
                })
    }

    fun loadSponsorsFromServer(proc: (sponsors: List<SponsorGroup>) -> Unit) {
        getFirebaseInstance()
                .collection("sponsors")
                .get_(Source.SERVER)
                .addSuccessListener {
                    proc(sponsorGroupsFrom(it.documents_))
                }
    }

    private fun sponsorGroupsFrom(documentSnapshots: List<DocumentSnapshot>): List<SponsorGroup> {
        return documentSnapshots.map {
            sponsorGroupFrom(it)
        }
    }

    private fun sponsorGroupFrom(documentSnapshot: DocumentSnapshot): SponsorGroup {
        val groupName = documentSnapshot.id
        val level = documentSnapshot.data_()

        @Suppress("UNCHECKED_CAST")
        val sponsorsList = level?.get("sponsors") as ArrayList<Map<String, String>>
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

fun sponsorClicked(sponsor: Sponsor){
    ServiceRegistry.analyticsApi.logEvent("sponsor_clicked", mapOf(Pair("id", sponsor.sponsorId.toString()), Pair("name", sponsor.name)))
}