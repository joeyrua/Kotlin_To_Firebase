package com.example.kotlin_to_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class Register : AppCompatActivity() {
    private lateinit var username:TextInputLayout
    private lateinit var phone:TextInputLayout
    private lateinit var email:TextInputLayout
    private lateinit var password:TextInputLayout
    private lateinit var login: Button
    private lateinit var register: Button
    private lateinit var database: DatabaseReference
    private lateinit var auth:FirebaseAuth
    private lateinit var userID:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        username = findViewById(R.id.input_username)
        phone = findViewById(R.id.input_Phone)
        email = findViewById(R.id.input_email_re)
        password = findViewById(R.id.input_password_re)
        login = findViewById(R.id.to_login_btn)
        register =findViewById(R.id.Register_btn)
        auth = Firebase.auth

        if(auth.currentUser != null){
            startActivity(Intent(this,UserProfile::class.java))
            finish()
        }
        login.setOnClickListener{
            startActivity(Intent(this,Login::class.java))
            finish()
        }

        register.setOnClickListener {
            if(!username_vaild() or !phone_vaild() or !email_vaild() or !password_vaild()){
                return@setOnClickListener
            }
            val username:String = username.editText?.text.toString()
            val phone:String = phone.editText?.text.toString()
            val email:String = email.editText?.text.toString()
            val password:String = password.editText?.text.toString()
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if(it.isSuccessful){
                    userID = auth.currentUser!!.uid
                    database = FirebaseDatabase.getInstance().getReference()
                    val helper = UserHelper(username = username,phone = phone,email = email,password = password)
                    database.child("users").child(userID).setValue(helper)
                    Toast.makeText(this,"註冊成功",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,UserProfile::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this,"註冊失敗",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private  fun username_vaild():Boolean{
        val userame_1:String = username.editText?.text.toString()
        if(userame_1.isEmpty()){
            username.error = "你沒有輸入本欄位"
            return false
        }
        else{
            username.error = null
            username.isErrorEnabled = false
            return true
        }
    }

    private  fun phone_vaild():Boolean{
        val phone_1:String = phone.editText?.text.toString()
        if(phone_1.isEmpty()){
            phone.error = "你沒有輸入本欄位"
            return false
        }
        else{
            phone.error = null
            phone.isErrorEnabled = false
            return true
        }
    }

    private  fun email_vaild():Boolean{
        val email_1:String = email.editText?.text.toString()
        if(email_1.isEmpty()){
            email.error = "你沒有輸入本欄位"
            return false
        }
        else{
            email.error = null
            email.isErrorEnabled = false
            return true
        }
    }

    private  fun password_vaild():Boolean{
        val password_1:String = password.editText?.text.toString()
        if(password_1.isEmpty()){
            password.error = "你沒有輸入本欄位"
            return false
        }
        else{
            password.error = null
            password.isErrorEnabled = false
            return true
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

}