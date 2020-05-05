package co.touchlab.sessionize

import android.util.Log
import co.touchlab.sessionize.api.NetworkRepo
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageHandler : FirebaseMessagingService() {
/*
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d(TAG, "From: ${remoteMessage?.from}")

        remoteMessage?.data?.isNotEmpty()?.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        NetworkRepo.dataCalls()
    }

    override fun onNewToken(token: String?) {
    }

    companion object {
        val TAG = FirebaseMessageHandler::class.java.simpleName
        fun init() {
            FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener {
                if (!it.isSuccessful) {
                    print("Error subscribing to topic")
                }else{
                    print("Success subscribing to topic")
                }
            }
        }
    }*/
}