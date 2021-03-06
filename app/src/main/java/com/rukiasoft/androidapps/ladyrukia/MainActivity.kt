package com.rukiasoft.androidapps.ladyrukia

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val placePickerRequest = 1

    val db = FirebaseFirestore.getInstance()

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


    private val s = "cretinooooo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            val builder = PlacePicker.IntentBuilder()

            startActivityForResult(builder.build(this), placePickerRequest)
        }

        val mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build()

        try {
            val result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null)
            result.setResultCallback { likelyPlaces ->
                for (placeLikelihood in likelyPlaces) {
                    Log.i("cretinooooo", String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.place.name,
                            placeLikelihood.likelihood))
                }
                likelyPlaces.release()
            }
        }catch (e: SecurityException){}

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == placePickerRequest) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(this, data)
                val toastMsg = String.format("Place: %s", place.name)
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show()
                // Create a new user with a first, middle, and last name
                val placeMap = HashMap<String, Any>()
                placeMap["name"] = place.name
                placeMap["type"] = place.placeTypes
                placeMap["rating"] = place.rating
                placeMap["latitude"] = place.latLng.latitude
                placeMap["longitude"] = place.latLng.longitude
                placeMap["priceLevel"] = place.priceLevel

                // Add a new document with a generated ID
                db.document("places/${place.id}")
                        .set(placeMap)
                        .addOnCompleteListener {task ->
                            if(task.isSuccessful){
                                Log.d(s, "DocumentSnapshot added with ID: " + place.id)
                            }else{
                                Log.d(s, "Error adding document: " + task.exception?.message)
                            }
                        }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}









