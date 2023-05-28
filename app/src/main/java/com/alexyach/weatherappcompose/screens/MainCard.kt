package com.alexyach.weatherappcompose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alexyach.weatherappcompose.R
import com.alexyach.weatherappcompose.data.WeatherModel
import com.alexyach.weatherappcompose.ui.theme.BlueWait
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


@Composable
fun MainCard(
    currentDay: MutableState<WeatherModel>,
    onClickSync: () -> Unit,
    onClickSearch: () -> Unit
) {

    Column(
        modifier = Modifier
//            .fillMaxSize()
            .padding(4.dp)
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = BlueWait,
            elevation = 8.dp,
            shape = RoundedCornerShape(8.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                        text = currentDay.value.time,
                        style = TextStyle(fontSize = 18.sp),
                        color = Color.White
                    )
                    AsyncImage(
                        model = "https:${currentDay.value.icon}",
                        contentDescription = "weather",
                        modifier = Modifier
                            .size(35.dp)
                            .padding(end = 8.dp, top = 4.dp)
                    )
                }

                // City name
                Text(
                    text = currentDay.value.city,
                    style = TextStyle(fontSize = 28.sp),
                    color = Color.White
                )
                // Temperature
                Text(
                    text = if (currentDay.value.currentTemp.isNotEmpty()) {
                        "${currentDay.value.currentTemp.toFloat().toInt()}°C"
                    } else {
                        "${currentDay.value.maxTemp}°C/${currentDay.value.minTemp}°C"
                    },
                    style = TextStyle(fontSize = 56.sp),
                    color = Color.White
                )
                Text(
                    text = currentDay.value.condition,
                    style = TextStyle(fontSize = 18.sp),
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    IconButton(
                        onClick = {
                            onClickSearch.invoke()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "icon",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Text(
                        text = "${currentDay.value.maxTemp.toFloat().toInt()}°C/" +
                                "${currentDay.value.minTemp.toFloat().toInt()}°C",
                        style = TextStyle(fontSize = 18.sp),
                        color = Color.White
                    )

                    IconButton(
                        onClick = {
                            onClickSync.invoke()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cloud_sync),
                            contentDescription = "search",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(
    daysList: MutableState<List<WeatherModel>>,
    currentDay: MutableState<WeatherModel>
) {
    val tabList = listOf("HOURS", "DAYS")
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    // Для анимации
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {

        /** Кнопки выбора "HOURS"/"DAYS" */
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = { position ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, position)
                )
            },
            backgroundColor = BlueWait,
            contentColor = Color.White
        ) {
            tabList.forEachIndexed { index, text ->
                Tab(
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(text = text)
                    }
                )
            }
        }

        /** Сами вкладки */
        HorizontalPager(
            count = tabList.size,
            state = pagerState,
            modifier = Modifier.weight(1.0f)
        ) { index ->
            val list = when (index) {
                0 -> getWeatherByHours(currentDay.value.hours)
                1 -> daysList.value
                else -> daysList.value
            }

            MainList(list, currentDay)
        }
    }
}

private fun getWeatherByHours(hours: String): List<WeatherModel> {
    if (hours.isEmpty()) return listOf()

    val list = ArrayList<WeatherModel>()
    val hoursArray = JSONArray(hours)

    for (i in 0 until hoursArray.length()) {
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "",
                item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString() + "°C",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "",
                "",
                ""
            )
        )
    }

    return list
}