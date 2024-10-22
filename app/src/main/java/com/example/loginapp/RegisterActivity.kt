package com.example.loginapp

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Get reference by text
        val account = findViewById<TextView>(R.id.alreadyHaveAnAccount)
        val accountText = getString(R.string.haveAnAccount)
        val spannableString = SpannableString(accountText)

        val registerBtn = findViewById<Button>(R.id.btnRegister)

        registerBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
        registerBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))

        //Create Clickable login
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Redirect to the LoginActivity when "Login" is clicked
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }

        }

        // Set the ClickableSpan to the word "Login"
        val loginStartIndex = accountText.indexOf("Login")
        spannableString.setSpan(
            clickableSpan,
            loginStartIndex,
            loginStartIndex + "Login".length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the spannable string to the TextView
        account.text = spannableString

        // Enable clicking on the text
        account.movementMethod = LinkMovementMethod.getInstance()

    }
}
