package com.example.sunnyweather.logic
import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * 仓库层，主要任务是判断调用方请求的数据应该是从本地数据源中获取还是从网络数据源中获取
 * 并将获取到的数据返回给调用方
 **/

object Repository {

    /**** -------------------- 获取网络数据源中的数据，返回给调用方 ---------------------------- ****/
    //将liveData()函数的线程参数类型指定成Dispatchers.IO,因为Android不允许在主线程中进行网络请求
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        //调用网络层的searchPlaces()函数搜索城市数据
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        //如果服务器返回的状态时ok，说明数据获取成功
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            //使用Kotlin内置的Result.success()方法来包装获取的城市数据列表
            Result.success(places)
        } else {
            //如果数据获取失败，使用Kotlin内置的Result.failure()方法来包装异常信息
            Result.failure(java.lang.RuntimeException("response status is ${placeResponse.status}"))
        }
    }
    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            /**
             * 获取实时天气信息和获取未来天气信息这2个请求是没有先后顺序的，可以让他们并发执行提高效率
             * 但是要在同时得到他们的响应结果后才能进一步执行程序
             * 只需要分别在两个async函数中发起网络请求，然后再分别调用它们的await()方法
             * 就可以保证只有在两个网络请求都成功响应之后，才会进一步执行程序
             **/
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            //如果2个请求的响应状态都是ok
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                //则封装到一个Weather对象中
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                //使用Result.success()方法来包装这个Weather对象
                Result.success(weather)
            } else {
                //若不是2个请求的响应都成功，则用Result.failure()方法包装一个异常信息
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" + "daily response status is ${dailyResponse.status}"
                    )
                )
            }
            }
        }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
    }

