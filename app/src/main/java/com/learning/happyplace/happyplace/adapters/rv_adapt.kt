package com.learning.happyplace.happyplace.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learning.happyplace.R
import com.learning.happyplace.happyplace.activities.AddHappyPlace
import com.learning.happyplace.happyplace.databases.DatabaseHandler
import com.learning.happyplace.happyplace.models.Happyplacemodel
import kotlinx.android.synthetic.main.item.view.*

class rv_adapt(
    private val context: Context,
    private val list:ArrayList<Happyplacemodel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onclickListener : OnclickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return myViewHolder(
           LayoutInflater.from(context).inflate(R.layout.item,parent,false)
       )
    }

    fun setOnClickListener(onclickListener: OnclickListener){
        this.onclickListener=onclickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){
        val model=list[position]
        if(holder is myViewHolder){
            holder.itemView.iv_image_place.setImageURI(Uri.parse(model.image))
            holder.itemView.title.setText(model.title)
            holder.itemView.tv_Description.setText(model.descrip)
            holder.itemView.setOnClickListener({
                if (onclickListener!=null){
                    onclickListener!!.onClick(position,model)
                }
            })
        }
    }

    interface OnclickListener{
        fun onClick(position: Int,model:Happyplacemodel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class myViewHolder(view: View) :RecyclerView.ViewHolder(view)

    fun notifyEditItem(activity:Activity,position: Int,requestCode:Int){
        val intent=Intent(context,AddHappyPlace::class.java)
        intent.putExtra("edit_detail",list[position])
        activity.startActivityForResult(intent,requestCode)
        notifyItemChanged(position)
    }

    fun deleteitem(position: Int){
        val dbhandle = DatabaseHandler(context)
        if(dbhandle.deleteHappyPlace(list[position].id)>0){
            list.removeAt(position)
        notifyItemRemoved(position)}

    }


}