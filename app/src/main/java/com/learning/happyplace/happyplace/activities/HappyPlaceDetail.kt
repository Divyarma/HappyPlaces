package com.learning.happyplace.happyplace.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.learning.happyplace.R
import com.learning.happyplace.happyplace.models.Happyplacemodel
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import kotlinx.android.synthetic.main.activity_happy_place_detail.*

class HappyPlaceDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_detail)
        setSupportActionBar(toolbar_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_detail.setNavigationOnClickListener {
            onBackPressed()
        }

        var hpm:Happyplacemodel?=null
        if(intent.hasExtra("model to display")){
            hpm=intent.getSerializableExtra("model to display") as Happyplacemodel
        }

        if(hpm!=null){
            supportActionBar!!.title=hpm.title
            tv_title_detail.setText(hpm.title)
            iv_details.setImageURI(Uri.parse(hpm.image))
            tv_Description_detail.setText(hpm.descrip)
            tv_location_detail.setText(hpm.location)
        }
    }
}