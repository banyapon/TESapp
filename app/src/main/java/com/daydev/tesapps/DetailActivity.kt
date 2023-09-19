package com.daydev.tesapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import java.io.Serializable

class DetailActivity : AppCompatActivity() {
    var showText: TextView? = null
    var showDetail: TextView? = null
    var showWeight: TextView? = null
    var showLatLon: TextView? = null
    var showImg: ImageView? = null
    var showQR: ImageView? = null
    var imageGalA: ImageView? = null
    var imageGalB: ImageView? = null
    var imageGalC: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        showText = findViewById<TextView>(R.id.showText)
        showDetail = findViewById<TextView>(R.id.showDetail)
        showWeight = findViewById<TextView>(R.id.showWeight)
        showLatLon = findViewById<TextView>(R.id.showLatLon)
        showImg = findViewById<ImageView>(R.id.showImg)
        showQR = findViewById<ImageView>(R.id.showQR)
        imageGalA = findViewById<ImageView>(R.id.imageGalA)
        imageGalB = findViewById<ImageView>(R.id.imageGalB)
        imageGalC = findViewById<ImageView>(R.id.imageGalC)

        var intent = intent
        showText!!.text = ""+intent.getStringExtra("title")
        showDetail!!.text = ""+intent.getStringExtra("content")
        showWeight!!.text = ""+intent.getStringExtra("weight")+" kg."
        showLatLon!!.text = ""+intent.getStringExtra("location")
        val numberList = intent.getStringExtra("gallery")
        val fnumber = numberList!!.replace("[", "")
        val lnumber = fnumber!!.replace("]", "")
        Log.d("Gallery", lnumber.toString())
        val gal: List<String> = lnumber.split(", ")
        Picasso.get().load(intent.getStringExtra("thumbnail"))
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .into(showImg)
        var qrImg : String? = null
        qrImg = "https://chart.googleapis.com/chart?cht=qr&chl=https://tesprojectapp-default-rtdb.firebaseio.com/app/data/"+intent.getStringExtra("key")+".json&chs=177x177"

        Picasso.get().load(gal[0])
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .into(imageGalA)
        Picasso.get().load(gal[1])
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .into(imageGalB)
        Picasso.get().load(gal[2])
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .into(imageGalC)

        Picasso.get().load(qrImg)
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .into(showQR)
    }
}
