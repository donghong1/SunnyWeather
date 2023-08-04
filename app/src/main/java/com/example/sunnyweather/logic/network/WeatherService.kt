package com.example.sunnyweather.logic.network

import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.DailyResponse
import com.example.sunnyweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
    @GET("v2.6/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String):
        Call<RealtimeResponse>

    //获取未来天气信息
    @GET("v2.6/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily?dailysteps=5")
    //使用@Path注解来向请求接口动态传入经纬度的坐标
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<DailyResponse>

}