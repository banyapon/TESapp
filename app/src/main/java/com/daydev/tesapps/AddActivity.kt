package com.daydev.tesapps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.security.SecureRandom


class AddActivity : AppCompatActivity() {
    var chooseImg: Button? = null
    var uploadImg:Button? = null
    var addContent:EditText?= null
    var addTitle:EditText?= null
    var imgView: ImageView? = null
    var PICK_IMAGE_REQUEST = 111
    var filePath: Uri? = null
    lateinit var mAuth: FirebaseAuth
    lateinit var database: FirebaseDatabase

    private val LOCATION_PERMISSION_REQ_CODE = 1000;
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    var storage: FirebaseStorage = FirebaseStorage.getInstance()
    var storageRef: StorageReference = storage.getReferenceFromUrl("gs://tesprojectapp.appspot.com")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        chooseImg = findViewById<Button>(R.id.chooseImg)
        uploadImg = findViewById<Button>(R.id.uploadImg)
        imgView = findViewById<ImageView>(R.id.imgView)
        addTitle = findViewById<EditText>(R.id.addTitle)
        addContent = findViewById<EditText>(R.id.addContent)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        chooseImg!!.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                PICK_IMAGE_REQUEST
            )
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        uploadImg!!.setOnClickListener {
            var newName: String?=null
            newName = randomString(13)
            getCurrentLocation()
            if (filePath != null) {
                val childRef = storageRef.child(""+newName+".jpg")
                //uploading the image
                val uploadTask = childRef.putFile(filePath!!)
                uploadTask.addOnSuccessListener {
                    val user = mAuth.currentUser
                    val databaseReference = database.reference.child("app/data/").push()
                    databaseReference.child("title").setValue(addTitle!!.text.toString())
                    databaseReference.child("content").setValue(addContent!!.text.toString())
                    databaseReference.child("thumbnail").setValue("https://firebasestorage.googleapis.com/v0/b/tesprojectapp.appspot.com/o/"+newName+".jpg?alt=media&token=cef97ac8-85b7-4fb7-8c03-34858978baab")
                    databaseReference.child("location").setValue("$latitude,$longitude")
                    databaseReference.child("uid").setValue(user!!.uid)
                    Toast.makeText(this@AddActivity, "Upload successful", Toast.LENGTH_SHORT).show()
                    var mainIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                }.addOnFailureListener { e ->
                    Toast.makeText(this@AddActivity, "Upload Failed -> $e", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@AddActivity, "Select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getCurrentLocation() {
        // checking location permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE);
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // getting the last known or current location
                latitude = location.latitude
                longitude = location.longitude
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed on getting current location",
                    Toast.LENGTH_SHORT).show()
            }
    }
    fun randomString(len: Int): String {
        val random = SecureRandom()
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()
        return (1..len).map { chars[random.nextInt(chars.size)] }.joinToString("")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        filePath = data!!.data
        try {
            //getting image from gallery
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            //Setting image to ImageView
            imgView!!.setImageBitmap(bitmap)
            Toast.makeText(this@AddActivity, "Upload เรียบร้อย!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}