package co.touchlab.sessionize.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Fragment extension function which allows automatic handling of ViewBinding Lifecycle.
 *
 * @param onCleanup option function which would be called just before the binding object gets cleaned up.
 *
 * @return ViewBinding object
 *
 * @sample
 *
 * class MyFragment : Fragment() {
 *
 *     private var binding: MyViewBinding by viewBindingLifecycle()
 *     /** viewBindingLifecycle(onCleanUp: (binding) -> Unit) recieves an optional parameter onCleanUp
 *     *  which be called just before binding object is cleaned up.
 *     *
 *     *  Sample Usage:
 *     *
 *     *  private var binding: MyViewBinding by viewBindingLifecycle { binding ->
 *     *      // Do your cleanup here
 *     *  }
 *     */
 *
 *     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
 *         binding = MyViewBinding.inflate(inflater, container, false)
 *         return binding.root
 *     }
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *
 *     super.onViewCreated(view, savedInstanceState)
 *        // you can now use you binding object here **
 *     }
 *
 *     override fun onStop() {
 *         super.onStop()
 *         // You can only use your binding until here..
 *         // binding object will be cleared beyond this lifecycle event
 *     }
 *
 *     override fun onDestroy()  {
 *         super.onDestroy()
 *         // binding == null at this point
 *     }
 *
 * }
 */
inline fun <reified T : ViewBinding> Fragment.viewBindingLifecycle(
        noinline onCleanup: ((T) -> Unit)? = null
): ReadWriteProperty<Fragment, T> =
        object : ReadWriteProperty<Fragment, T>, DefaultLifecycleObserver {

            // A backing property to hold our value
            private var binding: T? = null

            init {
                var viewLifecycleOwner: LifecycleOwner? = null
                this@viewBindingLifecycle
                        .viewLifecycleOwnerLiveData
                        .observe(this@viewBindingLifecycle) { newLifecycleOwner ->
                            viewLifecycleOwner?.lifecycle?.removeObserver(this)

                            viewLifecycleOwner = newLifecycleOwner.also {
                                it.lifecycle.addObserver(this)
                            }
                        }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                onCleanup?.invoke(binding!!)
                binding = null
            }

            override fun getValue(
                    thisRef: Fragment,
                    property: KProperty<*>
            ): T {
                return this.binding!!
            }

            override fun setValue(
                    thisRef: Fragment,
                    property: KProperty<*>,
                    value: T
            ) {
                this.binding = value
            }
        }