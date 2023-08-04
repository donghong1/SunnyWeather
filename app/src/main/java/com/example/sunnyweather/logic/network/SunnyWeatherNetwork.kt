package com.example.sunnyweather.logic.network
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
object SunnyWeatherNetwork {

    /****--------------------对WeatherService接口进行封装--------------------****/
    private val weatherService = ServiceCreator.create(WeatherService::class.java)
    suspend fun getDailyWeather(lng: String, lat: String) = weatherService.getDailyWeather(lng, lat).await()
    suspend fun getRealtimeWeather(lng: String, lat: String) = weatherService.getRealtimeWeather(lng, lat).await()
    /****--------------------对PlaceService接口进行封装--------------------****/
    private val placeService = ServiceCreator.create(PlaceService::class.java)
    suspend fun searchPlaces(query: String)  = placeService.searchPlaces(query).await()

    //定义成Call<T>的拓展函数，这样所有返回值是Call类型的Retrofit网络请求接口就可以直接调用该函数
    private suspend fun <T> Call<T>.await(): T {
        /** suspendCoroutine函数必须在协程作用域或挂起函数中才能调用，主要作用是将当前协程立即挂起 ，
         *  然后在一个普通的线程中执行Lambda表达式中的代码
         **/
        return suspendCoroutine { continuation ->
            enqueue(object: Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    //如果请求成功，就恢复被挂起的协程，并传入服务器响应的数据
                    if (body != null) continuation.resume(body)
                    //如果请求失败就恢复被挂起的协程，并传入具体的异常原因
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    //如果请求失败就恢复被挂起的协程，并传入具体的异常原因
                    continuation.resumeWithException(t)
                }
            })
        }
}}