package co.touchlab.sessionize.platform

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


var token:String? = null
var apiKey:String = ""

fun initApp(context: Context){
    var builder = FirebaseOptions.Builder().setApiKey(apiKey)
    FirebaseApp.initializeApp(context, builder.build())
}
fun requestInstanceId(){
    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener OnCompleteListener@{ task ->
        if (!task.isSuccessful) {
            Log.w("TAG", "getInstanceId failed", task.exception)
            return@OnCompleteListener
        }
        token = task.result?.token
    }
}


class Messager : FirebaseMessagingService() {
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