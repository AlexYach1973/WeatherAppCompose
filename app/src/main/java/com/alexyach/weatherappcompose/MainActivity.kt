package com.alexyach.weatherappcompose

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.alexyach.weatherappcompose.data.WeatherModel
import com.alexyach.weatherappcompose.screens.DialogSearch
import com.alexyach.weatherappcompose.screens.MainCard
import com.alexyach.weatherappcompose.screens.TabLayout
import com.alexyach.weatherappcompose.ui.theme.WeatherAppComposeTheme
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

const val API_KEY = "3ddb30cce63b4ef8b5995500230904"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeatherAppComposeTheme {

                /** Состояния */
                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }
                val currentDay = remember {
                    mutableStateOf(
                        WeatherModel(
                            "",
                            "",
                            "0.0",
                            "",
                            "",
                            "0.0",
                            "0.0",
                            ""
                        )
                    )
                }
                val dialogState = remember {
                    mutableStateOf(false)
                }

                if (dialogState.value) {
                    DialogSearch(dialogState, onSubmit = {city ->
                        getData(city, this, daysList, currentDay)
                    })
                }

                // передали состояния
                getData("Kiev", this, daysList, currentDay)

                Image(
                    painter = painterResource(id = R.drawable.cloud4_600_1067),
                    contentDescription = "background",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.8f),
                    contentScale = ContentScale.FillBounds
                )
                Column {
                    MainCard(currentDay,
                        onClickSync = {
                            getData("Kiev", this@MainActivity, daysList, currentDay)
                        },
                        onClickSearch = {
                            dialogState.value = true
                        })
                    TabLayout(daysList, currentDay)
                }
            }
        }
    }
}

/** Volley */
private fun getData(
    city: String, context: Context,
    daysList: MutableState<List<WeatherModel>>,
    currentDay: MutableState<WeatherModel>
) {

    val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
            "&q=$city" +
            "&days=" +
            "3" +
            "&aqi=no&alerts=no"
    // создали очередь
    val queue = Volley.newRequestQueue(context)

    // Сам запрос
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val list = getWeatherByDays(response)
            daysList.value = list
            currentDay.value = list[0]

//            Log.d("myLogs", "Response: $response")
            Log.d("myLogs", "Response Thread: ${Thread.currentThread().name}")
        },
        { error ->
            Log.d("myLogs", "Error: $error")
        }
    )

    // Добавили запрос в очередь
    queue.add(sRequest)
}


private fun getWeatherByDays(response: String): List<WeatherModel> {
    if (response.isEmpty()) return emptyList()

    val list = ArrayList<WeatherModel>()

    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")

    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

    for (i in 0 until days.length()) {
        val item = days[i] as JSONObject

        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()
            )
        )
    }

    // для первого элемента добавить currentTemp и date
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
    )

    return list

}