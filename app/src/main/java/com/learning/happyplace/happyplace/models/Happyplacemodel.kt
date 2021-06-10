package com.learning.happyplace.happyplace.models

import java.io.Serializable

data class Happyplacemodel(
        var id:Int,
        val title:String,
        val image:String,
        val descrip:String,
        val date: String,
        val location:String,
        val longitude:Double,
        val latitude:Double):Serializable

//We can also make it serializable using parceble.....need to implement extra method and make all strings null
//Also change getting value to getparcebleExtra
