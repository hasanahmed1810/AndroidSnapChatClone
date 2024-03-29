package com.hasan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChooseUserActivity : AppCompatActivity() {

    var chooseUserListView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var keys: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        supportActionBar?.title = "Send Snap"

        chooseUserListView = findViewById(R.id.chooseUserListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,emails)
        chooseUserListView?.adapter = adapter

        Firebase.database.reference.child("users").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val email = snapshot.child("email").value as String
                emails.add(email)
                keys.add(snapshot.key!!)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })

        chooseUserListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val snapMap: Map<String, String> = mapOf("from" to Firebase.auth.currentUser!!.email!!, "imageName" to intent.getStringExtra("imageName"), "imageURL" to intent.getStringExtra("imageURL"), "message" to intent.getStringExtra("message"))
            Firebase.database.reference.child("users").child(keys.get(position)).child("snaps").push().setValue(snapMap)
            Toast.makeText(this, "Snap Sent", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SnapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}