package com.daydev.tesapps

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*


class AddActivity : AppCompatActivity() {
    var chooseImg: Button? = null
    var buttonCamera:Button? = null
    var uploadImg:Button? = null
    var addContent:EditText?= null
    var addTitle:EditText?= null
    var editTextTime:EditText?= null
    var editTextWeight:EditText?= null
    var imgView: ImageView? = null
    var galleryA: ImageView? = null
    var galleryB: ImageView? = null
    var galleryC: ImageView? = null
    var showLocation: TextView?= null
    var myLayout: LinearLayout? =null

    var filePath: Uri? = null
    lateinit var mAuth: FirebaseAuth
    lateinit var database: FirebaseDatabase

    private var arrImage = ArrayList<String>()
    private var picID: Int = 123

    private val LOCATION_PERMISSION_REQ_CODE = 1000
    val REQUEST_CODE = 200
    val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    var storage: FirebaseStorage = FirebaseStorage.getInstance()
    var storageRef: StorageReference = storage.getReferenceFromUrl("gs://tesprojectapp.appspot.com")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        chooseImg = findViewById<Button>(R.id.chooseImg)
        buttonCamera = findViewById<Button>(R.id.buttonCamera)
        uploadImg = findViewById<Button>(R.id.uploadImg)
        imgView = findViewById<ImageView>(R.id.imgView)
        addTitle = findViewById<EditText>(R.id.addTitle)
        showLocation = findViewById<TextView>(R.id.showLocation)
        addContent = findViewById<EditText>(R.id.addContent)
        editTextTime = findViewById<EditText>(R.id.editTextTime)
        editTextWeight = findViewById<EditText>(R.id.editTextWeight)
        myLayout = findViewById<LinearLayout>(R.id.myLayout)
        galleryA = findViewById<ImageView>(R.id.galleryA)
        galleryB = findViewById<ImageView>(R.id.galleryB)
        galleryC = findViewById<ImageView>(R.id.galleryC)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val sdf = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
        val currentDate = sdf.format(Date())

        System.out.println(" C DATE is  $currentDate")
        editTextTime!!.setText("$currentDate")

        chooseImg!!.setOnClickListener {
            openGalleryForImages()
        }

        buttonCamera!!.setOnClickListener {
            openCameraInterface()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        uploadImg!!.setOnClickListener {

            getCurrentLocation()

            if(TextUtils.isEmpty(addTitle!!.text.toString())){
                Toast.makeText(applicationContext, "กรุณากรอกชื่อกสถานที่!", Toast.LENGTH_SHORT).show()
            }else{
                val user = mAuth.currentUser
                val databaseReference = database.reference.child("app/data/").push()
                databaseReference.child("title").setValue(addTitle!!.text.toString())
                databaseReference.child("weight").setValue(editTextWeight!!.text.toString())
                databaseReference.child("datetime").setValue("$currentDate")
                databaseReference.child("content").setValue(addContent!!.text.toString())
                databaseReference.child("thumbnail").setValue(""+arrImage[0])
                databaseReference.child("location").setValue("$latitude,$longitude")
                databaseReference.child("gallery").setValue("$arrImage")
                databaseReference.child("uid").setValue(user!!.uid)
                Toast.makeText(this@AddActivity, "Upload successful", Toast.LENGTH_SHORT).show()
                var mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            }
        }
    }

    private fun openCameraInterface() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Toast.makeText(this, "Please check your permission!",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGalleryForImages() {

        if (Build.VERSION.SDK_INT < 19) {
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Pictures")
                , REQUEST_CODE
            )
        }
        else { // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        }

    }

    private fun getCurrentLocation() {
        // checking location permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE)
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

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){

            // if multiple images are selected
            if (data.clipData != null) {

                var count = data.clipData?.itemCount
                for (i in 0 until count!!) {
                    var imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                    Log.d("images",""+data.clipData?.getItemAt(i)!!.uri)
                    if (imageUri != null) {
                        var newMulName: String?=null
                        newMulName = randomString(13)
                        val childRef = storageRef.child("$newMulName.jpg")
                        //uploading the image
                        val uploadTask = childRef.putFile(imageUri!!)
                        uploadTask.addOnSuccessListener {

                            arrImage.add("https://firebasestorage.googleapis.com/v0/b/tesprojectapp.appspot.com/o/"+newMulName+".jpg?alt=media&token=cef97ac8-85b7-4fb7-8c03-34858978baab")

                        }.addOnFailureListener { e ->
                            Toast.makeText(this@AddActivity, "Upload Failed -> $e", Toast.LENGTH_SHORT).show()
                        }
                    }
                    if(i == 0){
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.clipData?.getItemAt(i)!!.uri)
                        imgView!!.setImageBitmap(bitmap)
                    }
                    if(i == 1){
                        val bitmapA = MediaStore.Images.Media.getBitmap(contentResolver, data.clipData?.getItemAt(i)!!.uri)
                        galleryA!!.setImageBitmap(bitmapA)
                    }
                    if(i == 2){
                        val bitmapB = MediaStore.Images.Media.getBitmap(contentResolver, data.clipData?.getItemAt(i)!!.uri)
                        galleryB!!.setImageBitmap(bitmapB)
                    }
                    if(i == 3){
                        val bitmapC = MediaStore.Images.Media.getBitmap(contentResolver, data.clipData?.getItemAt(i)!!.uri)
                        galleryC!!.setImageBitmap(bitmapC)
                    }

                    var newView: ImageView = ImageView(this)

                    myLayout!!.addView(newView)
                    newView.layoutParams.height = 50
                    newView.layoutParams.width = 50
                    newView.x = 60F
                    newView.y = 70F
                    newView.setImageURI(imageUri)
                    //     iv_image.setImageURI(imageUri) Here you can assign your Image URI to the ImageViews
                }

            } else if (data.data != null) {
                // if single image is selected

                var imageUri: Uri = data.data!!
                //   iv_image.setImageURI(imageUri) Here you can assign the picked image uri to your imageview

            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            var imageUri: Uri = getImageUri(this@AddActivity,imageBitmap)
            val formatDate = SimpleDateFormat("dd-mm-yyyy-HH-mm-ss")
            val newImageDate = formatDate.format(Date())
            val childRef = storageRef.child("$newImageDate.jpg")
            //uploading the image
            val uploadTask = childRef.putFile(imageUri!!)
            uploadTask.addOnSuccessListener {
                arrImage.add("https://firebasestorage.googleapis.com/v0/b/tesprojectapp.appspot.com/o/"+newImageDate+".jpg?alt=media&token=cef97ac8-85b7-4fb7-8c03-34858978baab")
                imgView!!.setImageBitmap(imageBitmap)
            }.addOnFailureListener { e ->
                Toast.makeText(this@AddActivity, "Upload Failed -> $e", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }
}