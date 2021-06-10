package com.learning.happyplace.happyplace.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.learning.happyplace.happyplace.models.Happyplacemodel

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME="HappyPlacesDatabase"
        private const val DATABASE_VERSION=1
        private const val TABLE_NAME="HappyPlaceTable"


        private const val KEY_ID="id"
        private const val KEY_TITLE="title"
        private const val KEY_IMAGE="image"
        private const val KEY_DESCRIP="description"
        private const val KEY_DATE="date"
        private const val KEY_LATITUDE="latitude"
        private const val KEY_LOCATION="location"
        private const val KEY_LONGITUDE="longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createQuery=("CREATE TABLE "+ TABLE_NAME+"("
                + KEY_ID+" INTEGER PRIMARY KEY,"
                + KEY_TITLE+" TEXT,"
                + KEY_DESCRIP+" TEXT,"
                + KEY_IMAGE+" TEXT,"
                + KEY_DATE+" TEXT,"
                + KEY_LOCATION+" TEXT,"
                + KEY_LATITUDE+" TEXT,"
                + KEY_LONGITUDE+" TEXT)")
        db?.execSQL(createQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS"+ TABLE_NAME)
        onCreate(db)
    }

    fun addHappyPlace(hp:Happyplacemodel) : Long{
        val db=this.writableDatabase
        val contentValues =ContentValues()
       // contentValues.put(KEY_ID,hp.id)
        contentValues.put(KEY_TITLE,hp.title)
        contentValues.put(KEY_DESCRIP,hp.descrip)
        contentValues.put(KEY_IMAGE,hp.image)
        contentValues.put(KEY_DATE,hp.date)
        contentValues.put(KEY_LOCATION,hp.location)
        contentValues.put(KEY_LATITUDE,hp.latitude)
        contentValues.put(KEY_LONGITUDE,hp.longitude)
        val result = db.insert(TABLE_NAME,null,contentValues)
        db.close()
        return result
    }

    fun getdata():ArrayList<Happyplacemodel>{
        val hpl = ArrayList<Happyplacemodel>()
        val quer="SELECT * FROM $TABLE_NAME"
        val db=this.readableDatabase
        try{
            val cursor:Cursor=db.rawQuery(quer,null)
            if(cursor.moveToFirst()){
                do{
                    hpl.add(Happyplacemodel(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIP)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE))))
                }while (cursor.moveToNext())
            }
        }catch (e:SQLiteException){
            db.execSQL(quer)
            return ArrayList()
        }
        return hpl
    }

    fun EditHappyPlace(hp: Happyplacemodel): Int {
        val db=this.writableDatabase
        val contentValues =ContentValues()
        Log.i("error",hp.toString())
        contentValues.put(KEY_ID,hp.id)
        contentValues.put(KEY_TITLE,hp.title)
        contentValues.put(KEY_DESCRIP,hp.descrip)
        contentValues.put(KEY_IMAGE,hp.image)
        contentValues.put(KEY_DATE,hp.date)
        contentValues.put(KEY_LOCATION,hp.location)
        contentValues.put(KEY_LATITUDE,hp.latitude)
        contentValues.put(KEY_LONGITUDE,hp.longitude)
        val result = db.update(TABLE_NAME,contentValues, KEY_ID+"="+hp.id,null)
        db.close()
        return result
    }

    fun deleteHappyPlace(id:Int):Int
    {
        val db=this.writableDatabase
        return db.delete(TABLE_NAME, KEY_ID+"="+id,null)
        db.close()
    }





}