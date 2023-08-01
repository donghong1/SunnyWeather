package com.example.sunnyweather.logic
import androidx.lifecycle.liveData
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

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }}

