package com.example.kotlin_to_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class UserProfile : AppCompatActivity() {
    private lateinit var username: TextInputLayout
    private lateinit var phone: TextInputLayout
    private lateinit var email: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var logout:Button
    private lateinit var update:Button
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user_id:String
    private lateinit var _Username:String
    private lateinit var _Phone:String
    private lateinit var _email:String
    private lateinit var _password:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        username = findViewById(R.id.input_username_profile)
        phone = findViewById(R.id.input_Phone_profile)
        email = findViewById(R.id.input_email_profile)
        password = findViewById(R.id.input_password_profile)
        logout = findViewById(R.id.logout_btn)
        update = findViewById(R.id.Update_btn)
        auth = FirebaseAuth.getInstance()
        user_id = auth.currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("users")



        showAllUserData()

        update.setOnClickListener {
            if (!isUsernameChange() or !isPhoneChange() or !isEmailChange() or !isPasswordChange()){
                return@setOnClickListener
            }
            else{
                Toast.makeText(this,"沒有更新資料",Toast.LENGTH_SHORT).show()
            }
        }

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,Login::class.java))
            finish()
        }



    }

    private fun showAllUserData() {
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                _Username = snapshot.child(user_id).child("username").getValue<String>() as String
                _Phone = snapshot.child(user_id).child("phone").getValue<String>() as String
                _email = snapshot.child(user_id).child("email").getValue<String>() as String
                _password = snapshot.child(user_id).child("password").getValue<String>() as String
                username.editText?.setText(_Username)
                phone.editText?.setText(_Phone)
                email.editText?.setText(_email)
                password.editText?.setText(_password)

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun isUsernameChange():Boolean{
        if(!_Username.equals(username.editText?.text.toString())){
            database.child(user_id).child("username").setValue(username.editText?.text.toString())
            _Username = username.editText?.text.toString()
            return true
        }
        else{
            return false
        }
    }

    private fun isPhoneChange():Boolean{
        if(!_Phone.equals(phone.editText?.text.toString())){
            database.child(user_id).child("phone").setValue(phone.editText?.text.toString())
            _Phone = phone.editText?.text.toString()
            return true
        }
        else{
            return false
        }
    }

    private fun isEmailChange():Boolean{
        if(!_email.equals(email.editText?.text.toString())){
            database.child(user_id).child("email").setValue(email.editText?.text.toString())
            _email = email.editText?.text.toString()
            auth.currentUser!!.updateEmail(email.editText?.text.toString()).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this,"Email Update Success",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"Email Update Fail",Toast.LENGTH_SHORT).show()
                }
            }
            return true
        }
        else{
            return false
        }
    }

    private fun isPasswordChange():Boolean{
        if(!_password.equals(password.editText?.text.toString())){
            database.child(user_id).child("password").setValue(password.editText?.text.toString())
            _password = password.editText?.text.toString()
            auth.currentUser!!.updatePassword(password.editText?.text.toString()).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this,"Password Update Success",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"Password Update Failed",Toast.LENGTH_SHORT).show()
                }
            }

            return true
        }
        else{
            return false
        }
    }


}