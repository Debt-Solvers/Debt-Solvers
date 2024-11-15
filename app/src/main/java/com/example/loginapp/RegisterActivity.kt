package com.example.loginapp
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.loginapp.viewmodel.RegisterViewModel


class RegisterActivity : AppCompatActivity() {

    //This just creates properties only usable in class RegisterActivity
    // It also defines the name and you can initialize it later in the code.
    // Used to reference the properties in the layout.xml files.

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var accountTextView: TextView
    private lateinit var errorText: TextView

    private val registerViewModel : RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firstNameEditText = findViewById(R.id.etFirstName)
        lastNameEditText = findViewById(R.id.etLastName)
        emailEditText = findViewById(R.id.etEmail)
        passwordEditText = findViewById(R.id.etPassword)
        registerButton = findViewById(R.id.btnRegister)
        accountTextView = findViewById(R.id.alreadyHaveAnAccount)
        errorText = findViewById(R.id.tvErrorMessage)

        // Set up the clickable Login Text span
        clickableLoginText()

        // Observe registration status
        registerViewModel.registrationStatus.observe(this, Observer { result ->
            when (result) {
                is RegisterViewModel.RegistrationResult.Success -> {
//                    Log.d("RegisterActivity", "Registration successful: ${result.response}")
                    errorText.visibility = View.GONE
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is RegisterViewModel.RegistrationResult.Error -> {
//                    Log.e("RegisterActivity", "Registration error: ${result.message}")
//                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                    errorText.apply {
                        text = result.message
                        visibility = View.VISIBLE
                    }
                }
            }
        })



        //Handles what happens after we click the register button

        registerButton.setOnClickListener{

            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            //Need to do validation to make sure the sections aren't empty.
            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                //Calls the method register which handles the process
                registerViewModel.register(firstName, lastName, email, password)
            } else {
                Log.d("RegisterActivity", "Please fill in all fields")
            }
        }
    }

    // Handles what happens when you click the login text at the bottom.
    private fun clickableLoginText() {

        val loginText = getString(R.string.haveAnAccount)
        val spannableString = SpannableString(loginText)

        //Create Clickable login
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Redirect to the LoginActivity when "Login" is clicked
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        // Set the ClickableSpan to the word "Login"
        val loginStartIndex = loginText.indexOf("Login")
        spannableString.setSpan(
            clickableSpan,
            loginStartIndex,
            loginStartIndex + "Login".length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Set the spannable string to the TextView
        accountTextView.text = spannableString
        // Enable the text to be clickable
        accountTextView.movementMethod = LinkMovementMethod.getInstance()
    }
}
