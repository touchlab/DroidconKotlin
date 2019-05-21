package co.touchlab.sessionize

import android.content.Context
import android.util.Log
import co.touchlab.sessionize.api.NetworkRepo
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageHandler : FirebaseMessagingService() {

    private var token:String? = null

    fun requestToken(){
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener OnCompleteListener@{ task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "getInstanceId failed", task.exception)
                return@OnCompleteListener
            }
            token = task.result?.token
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d(TAG, "From: ${remoteMessage?.from}")

        remoteMessage?.data?.isNotEmpty()?.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        NetworkRepo.dataCalls()
    }
    override fun onNewToken(token: String?) {
        this.token = token
    }

    companion object {
        val TAG = FirebaseMessageHandler::class.java.simpleName
        fun initFirebaseApp(context: Context){
            FirebaseApp.initializeApp(context)
        }
    }
}