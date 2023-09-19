package com.daydev.tesapps

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    private lateinit var responseData: MutableList<DataModel>
    private var dataAdapter: DataAdapter? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private var uid: String? = null
    private var tabs: TabLayout? = null
    private var cardAdd: CardView? = null

    private var buttonAddData: Button?=null
    private var buttonProfile: Button?=null
    private var buttonLogout: Button?=null

    private var userEmail: String? = null

    override fun onStart(){
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser != null){
            uid = currentUser.uid
            val email = currentUser.email
            //userEmail = "$uid:$email"
        }else{
            val intentSession = Intent(this, LoginActivity::class.java)
            startActivity(intentSession)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.supportActionBar!!.hide()

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        tabs = findViewById<TabLayout>(R.id.tabs)
        buttonAddData = findViewById<Button>(R.id.buttonAddData)
        buttonProfile = findViewById<Button>(R.id.buttonProfile)
        buttonLogout = findViewById<Button>(R.id.buttonLogout)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = GridLayoutManager(this, 1)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("app/data/")
        responseData = mutableListOf()

        dataAdapter = DataAdapter(responseData as ArrayList<DataModel>)
        recyclerView!!.adapter = dataAdapter
        bindingData()

        tabs!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab?.position){
                    0 -> return
                    1 -> Toast.makeText(this@MainActivity, "Coming Soon!", Toast.LENGTH_LONG).show()
                    2 -> Toast.makeText(this@MainActivity, "Coming Soon!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        cardAdd = findViewById<CardView>(R.id.cardAdd)
        cardAdd!!.setOnClickListener {
            val intentAddSession = Intent(this,AddActivity::class.java)
            startActivity(intentAddSession)
        }

        buttonAddData!!.setOnClickListener{
            val intentAddData = Intent(this,AddActivity::class.java)
            startActivity(intentAddData)
        }

        buttonLogout!!.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intentSession = Intent(this,LoginActivity::class.java)
            startActivity(intentSession)
        }

    }

    private fun showToast(msg: String) {
        Toast.makeText(this@MainActivity, "The day of the week is $msg", Toast.LENGTH_LONG).show()
    }
    private fun bindingData() {
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                responseData.add(snapshot.getValue(DataModel::class.java)!!)
                dataAdapter!!.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }

            override fun onChildRemoved(snapshot: DataSnapshot) { }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }

            override fun onCancelled(error: DatabaseError) { }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                var intent = Intent(this, Settings::class.java)
                startActivity(intent)
                true
            }

            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intentSession = Intent(this,LoginActivity::class.java)
                startActivity(intentSession)
                return true
            }
            else -> {super.onOptionsItemSelected(item)}
        }
    }
}