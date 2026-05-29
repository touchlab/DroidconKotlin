package co.touchlab.droidcon.util

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

actual fun initializeFirebase() {
    Firebase.initialize(
        options = FirebaseOptions(
            applicationId = "1:1091975587304:web:droidcon",
            apiKey = "AIzaSyDJfGdSS15YDDg7CZCaAISCVv7YhzimvVA",
            projectId = "droidcon-148cc",
            databaseUrl = "https://droidcon-148cc.firebaseio.com",
            storageBucket = "droidcon-148cc.appspot.com",
            gcmSenderId = "1091975587304",
            authDomain = "droidcon-148cc.firebaseapp.com",
        ),
    )
}
