package com.hasan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.net.HttpURLConnection
import java.net.URL

class ViewSnapActivity : AppCompatActivity() {

    var messageTextView: TextView? = null
    var snapImageView: ImageView? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        supportActionBar?.title = "Snap"

        auth = Firebase.auth

        messageTextView = findViewById(R.id.messageTextView)
        snapImageView = findViewById(R.id.snapImageView)

        messageTextView?.text = intent.getStringExtra("message")

        val downloadTask = ImageDownloader()
        val myImage: Bitmap
        try {
            myImage = downloadTask.execute(intent.getStringExtra("imageURL")).get()
            snapImageView?.setImageBitmap(myImage)
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Firebase.database.reference.child("users").child(auth.currentUser!!.uid).child("snaps").child(intent.getStringExtra("snapKey")!!).removeValue()
        Firebase.storage.reference.child("images").child(intent.getStringExtra("imageName")!!).delete()
        Toast.makeText(this, "Snap Deleted", Toast.LENGTH_SHORT).show()
    }

    fun imageClicked(view: View){
        onBackPressed()
    }

    fun messageClicked(view: View){
        onBackPressed()
    }
}

class ImageDownloader : AsyncTask<String, Void, Bitmap>(){
    override fun doInBackground(vararg urls: String?): Bitmap? {
        try {
            val url = URL(urls[0])
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream = connection.inputStream
            return BitmapFactory.decodeStream(inputStream)
        }
        catch (e: Exception){
            e.printStackTrace()
            return null
        }
    }
}