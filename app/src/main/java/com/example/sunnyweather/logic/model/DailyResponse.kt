package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class DailyResponse(val status: String, val result: Result) {
    //RealtimeResponse数据类中也有一个Result类，不过由于都是定义在数据类内部，所以不会发生冲突
    data class Result(val daily: Daily)

    data class Daily(val temperature: List<Temperature>, val skycon: List<Skycon>, @SerializedName("life_index") val lifeIndex : LifeIndex)
    data class Temperature(val max: Float, val min: Float)
    data class Skycon(val value: String, val date: Date)
    data class LifeIndex(val coldRisk: List<LifeDescription>, val carWashing: List<LifeDescription>, val ultraviolet: List<LifeDescription>, val dressing: List<LifeDescription>)
    data class LifeDescription(val desc: String)}

