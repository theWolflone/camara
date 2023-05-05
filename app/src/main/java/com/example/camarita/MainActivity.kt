package com.example.camarita

import android.Manifest.permission.*
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.service.voice.VoiceInteractionSession.ActivityId
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.w3c.dom.Text
import java.net.URL

class MainActivity : AppCompatActivity() {
    data class foto(
        var ejex:String,
        var ejey:String,
        var imagencruda:String,
        var inputNombre: String
    )
        var listadefotos: MutableList<foto> = ArrayList()
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private var locationManager : LocationManager? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var image_uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        var  btncaptura = findViewById<Button>(R.id.btnCamara)
        btncaptura.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){

                    val permission = arrayOf(android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    requestPermissions(permission, PERMISSION_CODE)

                }
                else{
                    openCamera()
                }
            }
            else{
                openCamera()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (!checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions()
            }
        }
        else {

        }
    }

    private fun requestPermissions(){
        val  shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale){
            Log.i("","Displayiing permission rationale to provide additional context.")
            showSnackbar("Location  permission is needed for  core functioonality",
            "Okay",
            View.OnClickListener{
                startLocationPermissionRequest()
            })
        }else{
            Log.i("","Requestting perrmission")
            startLocationPermissionRequest()
        }

    }

    private  fun startLocationPermissionRequest(){
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),  34
        )
    }

    private fun openCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        var cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    private fun showSnackbar(
        mainTextStringId: String, actionStringId: String,
        listener: View.OnClickListener
    ) {
        Toast.makeText(this@MainActivity, mainTextStringId, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//called when user presses ALLOW or DENY from Permission Request Popup
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
//permission from popup was granted
                    openCamera()
                }
                else{
//permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    lateinit var mRecyclerView: RecyclerView
    val mAdapter: Adaptador = Adaptador()
    fun setUpRecyclerView(){
        mRecyclerView = findViewById(R.id.recycler) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.adaptador(listadefotos, this)
        mRecyclerView.adapter = mAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK){
//set image captured to image view
            var img_foto =  findViewById<ImageView>(R.id.imgAvatar)
            var texto = findViewById<TextView>(R.id.name)
            img_foto.setImageURI(image_uri)
            listadefotos.add(foto("","",image_uri.toString(), texto.text.toString()))
        setUpRecyclerView()
        }
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
    private fun getLastLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
// TODO: Consider calling
// ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
// public void onRequestPermissionsResult(int requestCode, String[] permissions,
// int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient?.lastLocation!!.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                var lastLocation = task.result
                var texto = findViewById<TextView>(R.id.name)


                listadefotos.add(foto((lastLocation)!!.latitude.toString(),(lastLocation)!!.longitude.toString(),image_uri.toString(), texto.text.toString()))



                setUpRecyclerView()

            }
            else {
                Log.w("", "getLastLocation:exception", task.exception)
                Toast.makeText(this,"Sin permisos",Toast.LENGTH_SHORT).show()
                var texto = findViewById<TextView>(R.id.name)


                listadefotos.add(foto("9.9254272","-84.0695808",image_uri.toString(), texto.text.toString()))



                setUpRecyclerView()
            }
        }
    }
}