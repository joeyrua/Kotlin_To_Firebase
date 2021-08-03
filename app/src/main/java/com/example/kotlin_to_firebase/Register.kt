package com.example.kotlin_to_firebase


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.KeyEvent
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

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
    private lateinit var imageView: ImageView
    private lateinit var select_image_btn:Button
    private lateinit var currentPhotoPah:String
    private var contentUri: Uri?=null
    val REQUEST_IMAGE_CAPTURE = 110
    val Gallery_IMAGE_CODE = 120
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        username = findViewById(R.id.input_username)
        phone = findViewById(R.id.input_Phone)
        email = findViewById(R.id.input_email_re)
        password = findViewById(R.id.input_password_re)
        login = findViewById(R.id.to_login_btn)
        register =findViewById(R.id.Register_btn)
        imageView = findViewById(R.id.image_view)
        select_image_btn = findViewById(R.id.select_image)
        auth = Firebase.auth
        storageReference = FirebaseStorage.getInstance().getReference("Images")

        if(auth.currentUser != null){
            startActivity(Intent(this,UserProfile::class.java))
            finish()
        }
        login.setOnClickListener{
            startActivity(Intent(this,Login::class.java))
            finish()
        }

        register.setOnClickListener {
            if(!username_vaild() or !phone_vaild() or !email_vaild() or !password_vaild() or !_Img() ){
                return@setOnClickListener
            }
            val username:String = username.editText?.text.toString()
            val phone:String = phone.editText?.text.toString()
            val email:String = email.editText?.text.toString()
            val password:String = password.editText?.text.toString()
            val timeStamp:String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFilename:String = "JPEG_"+timeStamp+"."+getExtension(contentUri!!)

            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                    userID = auth.currentUser!!.uid
                    database = FirebaseDatabase.getInstance().getReference()
                    val helper = UserHelper(username = username,phone = phone,email = email,password = password,ImgId = imageFilename)
                    database.child("users").child(userID).setValue(helper)

                    val image = storageReference.child(imageFilename)
                    image.putFile(contentUri!!).addOnFailureListener{
                        Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener {
                        Toast.makeText(this,"註冊成功",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,UserProfile::class.java))
                        finish()
                    }
            }.addOnFailureListener{
                Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
            }
        }
        select_image_btn.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(gallery,Gallery_IMAGE_CODE)
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

    private fun _Img():Boolean{
        if(contentUri==null){
            Toast.makeText(this,"請務必選擇圖片喔!!!",Toast.LENGTH_SHORT).show()
            return false
        }else{
            return true
        }
    }







    private fun createImageFile():File{//capture_2
        val timestamp:String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir:File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPah = absolutePath
        }
    }


    private fun dispatchTakePicture(){//capture_2
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                takePictureIntent->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile:File?=try {
                    createImageFile()
                }catch (ex :IOException){
                    null
                }
                photoFile?.also {
                    val photoURI:Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.file_provider_",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI)
                    startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE)
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//capture_3 and choose image_2
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val f =File(currentPhotoPah)
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            contentUri = Uri.fromFile(f)
            mediaScanIntent.setData(contentUri)
            this.sendBroadcast(mediaScanIntent)
            imageView.setImageURI(contentUri)
            //UploadImageToFirebase(f.name,contentUri)
        }
        else if(requestCode==Gallery_IMAGE_CODE && resultCode == RESULT_OK && data != null && data.data!=null){
            contentUri = data.data!!

            imageView.setImageURI(contentUri)
            //UploadImageToFirebase(imageFilename,contentUri)
        }
    }

    private fun UploadImageToFirebase(name: String, contentUri: Uri?) {
        val image = storageReference.child(name)
        image.putFile(contentUri!!).addOnFailureListener{
            Toast.makeText(this,"Upload Image Failed",Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this,"Image Upload Successful",Toast.LENGTH_SHORT).show()
        }
    }


    private fun getExtension(uri: Uri):String{//choose_image_1
        val cr = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri))!!
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

}