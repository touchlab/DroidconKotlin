package co.touchlab.sessionize.platform

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


var token:String? = null

fun firebaseInit(context: Context){
    FirebaseApp.initializeApp(context)
}
fun firebaseRequestToken(){
    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener OnCompleteListener@{ task ->
        if (!task.isSuccessful) {
            Log.w("TAG", "getInstanceId failed", task.exception)
            return@OnCompleteListener
        }
        token = task.result?.token
        Log.i("TOKEN",token)
    }
}


class FirebaseMessageHandler : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d("TAG", "From: ${remoteMessage?.from}")

        // Check if message contains a data payload.
        remoteMessage?.data?.isNotEmpty()?.let {
            Log.d("TAG", "Message data payload: " + remoteMessage.data)
        }
        // Check if message contains a notification payload.
        remoteMessage?.notification?.let {
            Log.d("TAG", "Message Notification Body: ${it.body}")
        }
    }
    override fun onNewToken(token: String?) {
        Log.d("TAG", "Refreshed token: $token")
    }
}