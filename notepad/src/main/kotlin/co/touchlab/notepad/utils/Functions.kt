package co.touchlab.notepad.utils

import co.touchlab.multiplatform.architecture.db.sqlite.NativeOpenHelperFactory

expect fun currentTimeMillis():Long

expect fun <B> backgroundTask(backJob:()-> B, mainJob:(B) -> Unit)

expect fun backgroundTask(backJob:()->Unit)

expect fun initContext():NativeOpenHelperFactory

