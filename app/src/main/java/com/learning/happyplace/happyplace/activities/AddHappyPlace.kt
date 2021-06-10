 package com.learning.happyplace.happyplace.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.learning.happyplace.R
import com.learning.happyplace.happyplace.databases.DatabaseHandler
import com.learning.happyplace.happyplace.models.Happyplacemodel
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Error
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlace : AppCompatActivity(),View.OnClickListener {

    companion object{
        private const val GALLERY=1
        private const val CAMERA=2
        private const val Image_dir="HappyPlaceImages"

    }
    private val D=Calendar.getInstance()
    private lateinit var dateSetlistener:DatePickerDialog.OnDateSetListener
    private var saveImagetoInternalStorage :Uri?=null
    private var mLatitude : Double=0.0
    private var mLongitude :Double=0.0

    private var mHappyPlace: Happyplacemodel?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        if(intent.hasExtra("edit_detail")){
            mHappyPlace=intent.getSerializableExtra("edit_detail") as Happyplacemodel
        }

        dateSetlistener=DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            D.set(Calendar.YEAR,year)
            D.set(Calendar.MONTH,month)
            D.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDate()
        }
        updateDate()

        if(mHappyPlace!=null){
            date.setText(mHappyPlace!!.date)
            name.setText(mHappyPlace!!.title)
            description.setText(mHappyPlace!!.descrip)
            saveImagetoInternalStorage=Uri.parse(mHappyPlace!!.image)
            iv_image.setImageURI(saveImagetoInternalStorage)
            location.setText(mHappyPlace!!.location)
            btn_save.setText("UPDATE")
        }


        date.setOnClickListener(this)
        add_image.setOnClickListener(this)
        btn_save.setOnClickListener(this)
    }

    private fun updateDate(){
        val myformat="dd.MM.yyyy"
        val sdf=SimpleDateFormat(myformat,Locale.getDefault())
        date.setText(sdf.format(D.time).toString())
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.date ->{
                DatePickerDialog(this,dateSetlistener,D.get(Calendar.YEAR),D.get(Calendar.MONTH),D.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.add_image ->{
                val picturedialog=AlertDialog.Builder(this)
                picturedialog.setTitle("select action")
                val picturedialogitems= arrayOf("Select picture from device","Capture picture from camera")
                picturedialog.setItems(picturedialogitems){
                    dialog,which->
                    when(which){
                        0-> choosePhotoFromGallery()
                        1-> clickPicture()
                    }
                }
                picturedialog.create().show()

            }
            R.id.btn_save->{
                when{
                    name.text.isNullOrEmpty()->{
                          Toast.makeText(this,"Enter Title",Toast.LENGTH_SHORT).show()
                    }
                    description.text.isNullOrEmpty()->{
                        Toast.makeText(this,"Enter Description",Toast.LENGTH_SHORT).show()
                    }
                    date.text.isNullOrEmpty()->{
                        Toast.makeText(this,"Enter Date",Toast.LENGTH_SHORT).show()
                    }
                    location.text.isNullOrEmpty()->{
                        Toast.makeText(this,"Enter Location",Toast.LENGTH_SHORT).show()
                    }
                    saveImagetoInternalStorage==null ->{
                        Toast.makeText(this,"Please select an Image",Toast.LENGTH_SHORT).show()
                    }
                    else-> {
                        var hp = Happyplacemodel(0,
                                name.text.toString(),
                                saveImagetoInternalStorage.toString(),
                                description.text.toString(),
                                date.text.toString(),
                                location.text.toString(),
                                mLongitude, mLatitude)
                        if (mHappyPlace == null) {
                            val dbhandle = DatabaseHandler(this)
                            val result = dbhandle.addHappyPlace(hp)
                            if (result > 0) {
                                setResult(Activity.RESULT_OK)
                                Toast.makeText(this, "Successfully entered", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            else {
                                Toast.makeText(this, "Could not save", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            hp.id= mHappyPlace!!.id
                            val dbhandle = DatabaseHandler(this)
                            val result = dbhandle.EditHappyPlace(hp)
                            if (result > 0) {
                                setResult(Activity.RESULT_OK)
                                Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Log.i("Error",result.toString())
                                Toast.makeText(this, "Could not update", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }
            }
            R.id.btn_cancel->{
                finish()
            }
        }
    }

    private fun clickPicture() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport)
            {
                if(report.areAllPermissionsGranted()){
                    val gallery_intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(gallery_intent, CAMERA)
                }
            }
            override fun onPermissionRationaleShouldBeShown(p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?)
            {
                permissionRationalDialog()
            }
        }).onSameThread().check()

    }

    @Suppress("DEPRECATION")
    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object: MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport)
            {
                if(report.areAllPermissionsGranted()){
                    val gallery_intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(gallery_intent, GALLERY)
                }
            }
            override fun onPermissionRationaleShouldBeShown(p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?)
            {
                permissionRationalDialog()
            }
        }).onSameThread().check()
    }
    @Suppress("DEPRECATION")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if(data !=null){
                    val content=data.data
                    try{
                        val selectedBitmap= MediaStore.Images.Media.getBitmap(this.contentResolver,content)
                        saveImagetoInternalStorage=SaveImagetoDevice(selectedBitmap)
                        iv_image.setImageBitmap(selectedBitmap)
                    }catch (e:Error){
                        e.printStackTrace()
                        Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if(requestCode== CAMERA){
                if(data!=null){
                    val content=data.data
                    try{
                        val thumbnail:Bitmap=data!!.extras!!.get("data") as Bitmap
                        iv_image.setImageBitmap(thumbnail)
                        SaveImagetoDevice(thumbnail)
                        saveImagetoInternalStorage=SaveImagetoDevice(thumbnail)
                    }catch (e:Error){
                        e.printStackTrace()
                        Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun permissionRationalDialog(){
        AlertDialog.Builder(this).setMessage("You have turned off the permission required for this feature. It can be enabled in settings.")
                .setPositiveButton("Go to settings"){
                    _,_->
                    try{
                        val intent= Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri=Uri.fromParts("package",packageName,null)
                        intent.data=uri
                        startActivity(intent)
                    }catch (e:ActivityNotFoundException){
                        e.printStackTrace()
                    }
        }.setNegativeButton("No thanks"){
            dialog,_->
                    dialog.dismiss()
                }.show()
    }

    private fun SaveImagetoDevice(bitmap: Bitmap):Uri{
        val wrapper=ContextWrapper(applicationContext)
        var f=wrapper.getDir(Image_dir,Context.MODE_PRIVATE)
        f= File(f,"${UUID.randomUUID()}.jpg")
        try {
            val stream:OutputStream=FileOutputStream(f)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }
        return Uri.parse(f.absolutePath)
    }





}