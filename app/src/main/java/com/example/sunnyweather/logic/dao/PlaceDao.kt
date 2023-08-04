package com.example.sunnyweather.logic.dao


import com.example.sunnyweather.logic.model.Place
import android.content.Context

import androidx.core.content.edit
import com.example.sunnyweather.SunnyWeatherApplication
import com.google.gson.Gson


object PlaceDao {

    //将地点存储在本地
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            //调用Gson库，将Place对象转化为Json字符串
            putString("place", Gson().toJson(place))
        }
    }

    //从本地读取保存地点
    fun getSavedPlace(): Place {
        //若取不到，则用空字符串作为默认值
        val placeJson = sharedPreferences().getString("place", "")
        //调用Gson库，将Json字符串转化为Place对象
        return Gson().fromJson(placeJson, Place::class.java)
    }
    fun isPlaceSaved() = sharedPreferences().contains("place")
    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)}


