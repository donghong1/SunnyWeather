package com.example.sunnyweather.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Place
import org.w3c.dom.Text

//适配器需要继承自RecyclerView.Adapter，泛型指定为PlaceAdapter.ViewHolder
 class PlaceAdapter(private val fragment: Fragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    //内部类ViewHolder需要继承自RecyclerView.ViewHolder
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        //传入参数是RecyclerView子项的最外层布局place_item，这样可以通过findViewById来访问布局中的实例
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //将RecyclerView子项的布局进行解析，得到子项布局对应的View
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        //创建ViewHolder实例
        return ViewHolder(view)}
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }
    override fun getItemCount() = placeList.size
    }

