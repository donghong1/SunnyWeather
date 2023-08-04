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
import com.example.sunnyweather.ui.weather.WeatherActivity
import org.w3c.dom.Text

//适配器需要继承自RecyclerView.Adapter，泛型指定为PlaceAdapter.ViewHolder
 class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    //内部类ViewHolder需要继承自RecyclerView.ViewHolder
    /**在RecyclerView中，ViewHolder类用于保存每个子项的视图引用。通过将视图的引用保存在ViewHolder对象中，
    *可以避免在滚动列表时频繁地调用findViewById方法来查找视图。
     **/
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        //传入参数是RecyclerView子项的最外层布局place_item，这样可以通过findViewById来访问布局中的实例
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //将RecyclerView子项的布局进行解析，得到子项布局对应的View
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        //创建ViewHolder实例
        val holder = ViewHolder(view)
        /**给place_item.xml的最外层布局注册了一个点击事件监听器
         * 在点击事件中获取当前点击项的经纬度坐标和地区名称，并传入Intent中
         * 最后启动WeatherActivity
         **/
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]
            val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            fragment.startActivity(intent)
            fragment.activity?.finish()
            fragment.viewModel.savePlace(place)
        }
        return holder}
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }
    override fun getItemCount() = placeList.size
    }

