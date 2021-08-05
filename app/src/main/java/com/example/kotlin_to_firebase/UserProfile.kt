package com.example.kotlin_to_firebase

import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class UserProfile : AppCompatActivity() {
    private lateinit var username: TextInputLayout
    private lateinit var phone: TextInputLayout
    private lateinit var email: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var logout:Button
    private lateinit var update:Button
    private lateinit var Select_Image:Button
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user_id:String
    private lateinit var _Username:String
    private lateinit var _Phone:String
    private lateinit var _email:String
    private lateinit var _password:String
    private lateinit var image:ImageView
    private lateinit var image_id:String
    private lateinit var network: TextView
    private lateinit var storageReference: StorageReference
    private var contentUri:Uri?=null
    private val Gallery_Image_Code=120
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    val PositiveButton_Click = {
        dialog: DialogInterface,which:Int->
        System.exit(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        username = findViewById(R.id.input_username_profile)
        phone = findViewById(R.id.input_Phone_profile)
        email = findViewById(R.id.input_email_profile)
        password = findViewById(R.id.input_password_profile)
        logout = findViewById(R.id.logout_btn)
        update = findViewById(R.id.Update_btn)
        image = findViewById(R.id.image_view_show)
        Select_Image = findViewById(R.id.select_image)
        swipeRefreshLayout = findViewById(R.id.refresh_data)
        network = findViewById(R.id.network_test)
        auth = FirebaseAuth.getInstance()
        user_id = auth.currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("users")
        storageReference = FirebaseStorage.getInstance().getReference("Images/")

        if(!isNetworkConnect()){
            username.visibility = View.GONE
            phone.visibility = View.GONE
            email.visibility = View.GONE
            password.visibility = View.GONE
            update.visibility = View.GONE
            logout.visibility = View.GONE
            Select_Image.visibility = View.GONE
            image.visibility = View.GONE
            network.visibility = View.VISIBLE

            return
        }
        else{
            username.visibility = View.VISIBLE
            phone.visibility = View.VISIBLE
            email.visibility = View.VISIBLE
            password.visibility = View.VISIBLE
            update.visibility = View.VISIBLE
            logout.visibility = View.VISIBLE
            Select_Image.visibility = View.VISIBLE
            image.visibility = View.VISIBLE
            network.visibility = View.GONE
            showAllUserData()
        }

        /*swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing=false
            update.isClickable = true
        }*/








        update.setOnClickListener {
            if (!isUsernameChange() or !isPhoneChange() or !isEmailChange() or !isPasswordChange() or !isImageChange()){
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

        Select_Image.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(gallery, Gallery_Image_Code)
        }

    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Gallery_Image_Code && resultCode== RESULT_OK && data != null && data.data!=null){
            contentUri = data.data!!
            image.setImageURI(contentUri)
        }
    }

    private fun getExtension(uri:Uri):String{
        val cr = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri))!!
    }



    private fun showAllUserData() {
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                _Username = snapshot.child(user_id).child("username").getValue<String>() as String
                _Phone = snapshot.child(user_id).child("phone").getValue<String>() as String
                _email = snapshot.child(user_id).child("email").getValue<String>() as String
                _password = snapshot.child(user_id).child("password").getValue<String>() as String
                image_id = snapshot.child(user_id).child("imgId").getValue<String>() as String
                username.editText?.setText(_Username)
                phone.editText?.setText(_Phone)
                email.editText?.setText(_email)
                password.editText?.setText(_password)

                val imageRef = storageReference.child(image_id)
                imageRef.downloadUrl.addOnSuccessListener {
                        Glide.with(this@UserProfile)
                            .load(it)
                            .into(image)

                    }.addOnFailureListener{
                        Toast.makeText(this@UserProfile,"No Find Image",Toast.LENGTH_SHORT).show()
                      }

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

    private fun isImageChange():Boolean{
        if(contentUri!=null){
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_"+timestamp+"."+getExtension(contentUri!!)
            if (!image_id.equals(imageFileName)){
                update.isClickable = false //disable=>1.java:setEnable(false) 2.Kotlin:isClickable=false
                storageReference.child(imageFileName).putFile(contentUri!!)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Image Update Successfully",Toast.LENGTH_SHORT).show()
                        database.child(user_id).child("imgId").setValue(imageFileName)
                        image_id = imageFileName
                }.addOnFailureListener{
                    Toast.makeText(this,"Image Update Failed",Toast.LENGTH_SHORT).show()
                    }
                return true
            }
        }
        else{
            Toast.makeText(this,"No Image Update",Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun isNetworkConnect():Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.getActiveNetworkInfo()
        if(networkInfo != null && networkInfo.isConnected){
            return true
        }
        else{
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setMessage("你網路沒有連線喔!!")
            alertDialog.setPositiveButton("確定",null)
            alertDialog.setCancelable(false)
            alertDialog.show()
            return false
        }
    }






    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            ConfirmExit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun ConfirmExit(){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage("你要離開嗎?")
        alertDialog.setPositiveButton("是",PositiveButton_Click)
        alertDialog.setNegativeButton("否",null)
        alertDialog.setCancelable(false)
        alertDialog.show()
    }



}