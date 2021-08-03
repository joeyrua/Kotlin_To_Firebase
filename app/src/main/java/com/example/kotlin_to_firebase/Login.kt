package com.example.kotlin_to_firebase

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException

class Login : AppCompatActivity() {
    private lateinit var email: TextInputLayout
    private lateinit var password:TextInputLayout
    private lateinit var forget_password:Button
    private lateinit var register:Button
    private lateinit var ok:Button
    private lateinit var googleSignIn:SignInButton//googleSignIn 按鈕不能使用Button，而是用SignInButton

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    val positiveButtonClick= {
        dialog: DialogInterface,which: Int->
        System.exit(0)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.input_email)
        password = findViewById(R.id.input_password)
        forget_password = findViewById(R.id.forget_password_btn)
        ok = findViewById(R.id.login_btn)
        register = findViewById(R.id.to_register_btn)
        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("users")

        ok.setOnClickListener{
            if(!email_vaild() or !password_vaild()){
                return@setOnClickListener
            }else{
                auth.signInWithEmailAndPassword(email.editText?.text.toString(),password.editText?.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(this,"Login Complete",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,UserProfile::class.java))
                        finish()
                    }
                    else if(!Network_Connect()){
                        return@addOnCompleteListener
                    }
                }
            }
        }

        register.setOnClickListener{
            if(!Network_Connect()){
                return@setOnClickListener
            }else{
                startActivity(Intent(this,Register::class.java))
                finish()
            }

        }
    }
    private fun Network_Connect():Boolean{//判斷是否網路有連線
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo  = connectivityManager.getActiveNetworkInfo()
        if(networkInfo != null && networkInfo.isConnected()){
            return true
        }
        else{
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setMessage("你沒有連到網路喔!!!!")
            alertDialog.setPositiveButton("確定",null)
            alertDialog.setCancelable(false)
            alertDialog.show()
            return false
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
        if(keyCode==KeyEvent.KEYCODE_BACK){
            ConfirmExit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun ConfirmExit(){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage("是否要離開???")
        alertDialog.setPositiveButton("是",positiveButtonClick)
        alertDialog.setNegativeButton("否",null)
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    public override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(this,UserProfile::class.java))
            finish()
        }
    }
}