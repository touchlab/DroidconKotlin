package com.google.codelabs.mdc.kotlin.shrine

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Editable
import android.support.design.button.MaterialButton
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.KeyEvent

/**
 * Fragment representing the login screen for Shrine.
 */
class LoginFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.shr_login_fragment, container, false)
        val passwordTextInput = view.findViewById<TextInputLayout>(R.id.password_text_input)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.password_edit_text)
        val nextButton = view.findViewById<MaterialButton>(R.id.next_button)

        // Set an error if the password is less than 8 characters.
        nextButton.setOnClickListener {
            if (!isPasswordValid(passwordEditText.text)) {
                passwordTextInput.error = getString(R.string.shr_error_password)
            } else {
                passwordTextInput.error = null // Clear the error
                (activity as NavigationHost).navigateTo(ProductGridFragment(), false) // Navigate to the next Fragment
            }
        }

        // Clear the error once more than 8 characters are typed.
        passwordEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(view: View, i: Int, keyEvent: KeyEvent): Boolean {
                if (isPasswordValid(passwordEditText.text)) {
                    passwordTextInput.error = null //Clear the error
                }

                return false
            }
        })

        return view
    }

    private fun isPasswordValid(text: Editable?): Boolean {
        return text != null && text.length >= 8
    }
}
