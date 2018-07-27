package co.touchlab.notepad

import co.touchlab.droidcon.db.UserAccount
import co.touchlab.notepad.db.QueryLiveData
import co.touchlab.notepad.utils.goFreeze
import com.squareup.sqldelight.Query

class SpeakerModel(val speakerId:String){

    val speakerLiveData:SpeakerLiveData

    init {
        val query = goFreeze(AppContext.dbHelper.queryWrapper.userAccountQueries.selectById(speakerId))
        speakerLiveData = SpeakerLiveData(query)
    }

    fun shutDown(){
        speakerLiveData.removeListener()
    }

    class SpeakerLiveData(q: Query<UserAccount>) : QueryLiveData<UserAccount>(q){
        override fun extractData(q: Query<*>): UserAccount = q.executeAsOne() as UserAccount
    }
}