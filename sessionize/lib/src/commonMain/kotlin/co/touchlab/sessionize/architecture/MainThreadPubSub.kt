package co.touchlab.sessionize.architecture

import co.touchlab.sessionize.lateValue
import co.touchlab.sessionize.platform.backToFront
import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.freeze

class MainThreadPubSub<T> : BasePub<T>(), Sub<T> {
    private val subSetLocal = ThreadLocalRef<MutableCollection<Sub<T>>>()

    init {
        subSetLocal.set(mutableSetOf())
    }

    override fun subs(): MutableCollection<Sub<T>> = subSetLocal.lateValue

    override fun onNext(next: T) {
        backToFront({ next.freeze() }) {
            applyNextValue(it)
        }
    }

    override fun onError(t: Throwable) {
        backToFront({ t.freeze() }) {
            applyError(it)
        }
    }
}