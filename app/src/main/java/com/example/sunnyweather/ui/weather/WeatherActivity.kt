package com.example.sunnyweather.ui.weather

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale


class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)
            //设置沉浸式状态栏
            /**
             * 从Intent中取出经纬度坐标和地区名称，并赋值到WeatherViewModel的相应变量中
             * 对weatherLiveData对象进行观察，当获取到服务器返回的天气数据时，就调用showWeatherInfo()进行解析与展示
             * 调用了WeatherViewModel的refreshWeather()方法来执行一次刷新天气的请求。
             **/
            if (viewModel.locationLng.isEmpty()) {
                viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
            }
            if (viewModel.locationLat.isEmpty()) {
                viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
            }
            if (viewModel.placeName.isEmpty()) {
                viewModel.placeName = intent.getStringExtra("place_name") ?: ""
            }
            viewModel.weatherLiveData.observe(this, Observer { result ->
                val weather = result.getOrNull()
                if (weather != null) {
                    showWeatherInfo(weather)
                } else {
                    Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                    result.exceptionOrNull()?.printStackTrace()
                }
            })
            //设置下拉刷新进度条的颜色

                refreshWeather()
            }
            //点击切换城市按钮，打开滑动菜单
        fun refreshWeather() {
            //调用刷新天气的方法
            viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
            //显示下拉刷新进度条
        }

        /**
         * 从Weather对象中获取数据，然后显示到相应控件上
         * 在未来几天天气预报部分，我们使用一个for-in循环来处理
         * 每天的天气信息，在循环中动态加载forecast_item.xml布局并设置相应的数据
         * 然后添加到父布局中
         **/
        private fun showWeatherInfo(weather: Weather) {
            val placeName : TextView =findViewById(R.id.placeName)
            placeName.text = viewModel.placeName
            val realtime = weather.realtime
            val daily = weather.daily
            val currentTempText = "${realtime.temperature.toInt()} °C"
            val currentTemp: TextView =findViewById(R.id.currentTemp)
            currentTemp.text = currentTempText
            val currentSky: TextView =findViewById(R.id.currentSky)
            currentSky.text = getSky(realtime.skycon).info
            val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
            val currentAQI: TextView =findViewById(R.id.currentAQI)
            currentAQI.text = currentPM25Text
            val nowLayout:RelativeLayout?=findViewById(R.id.nowLayout)
            nowLayout?.setBackgroundResource(getSky(realtime.skycon).bg)
            val forecastLayout: LinearLayout =findViewById(R.id.forecastLayout)
            forecastLayout.removeAllViews()
            val days = daily.skycon.size
            for (i in 0 until days) {
                val skycon = daily.skycon[i]
                val temperature = daily.temperature[i]
                val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
                val dateInfo = view.findViewById(R.id.dateInfo) as TextView
                val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
                val skyInfo = view.findViewById(R.id.skyInfo) as TextView
                val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateInfo.text = simpleDateFormat.format(skycon.date)
                val sky = getSky(skycon.value)
                skyIcon.setImageResource(sky.icon)
                skyInfo.text = sky.info
                val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} °C"
                temperatureInfo.text = tempText
                forecastLayout.addView(view)
            }
            val lifeIndex = daily.lifeIndex
            val coldRiskText: TextView =findViewById(R.id.coldRiskText)
            coldRiskText.text = lifeIndex.coldRisk[0].desc
            val dressingText: TextView =findViewById(R.id.dressingText)
            dressingText.text = lifeIndex.dressing[0].desc
            val ultravioletText: TextView =findViewById(R.id.ultravioletText)
            ultravioletText.text = lifeIndex.ultraviolet[0].desc
            val carWashingText: TextView =findViewById(R.id.carWashingText)
            carWashingText.text = lifeIndex.carWashing[0].desc
            val weatherLayout: ScrollView =findViewById(R.id.weatherLayout)
            weatherLayout.visibility = View.VISIBLE
        }

    }