package com.alexyach.weatherappcompose.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alexyach.weatherappcompose.data.WeatherModel
import com.alexyach.weatherappcompose.ui.theme.*

@Composable
fun MainList(list: List<WeatherModel>, currentDay: MutableState<WeatherModel>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(list)
        { index, item ->
            ListItem(item, currentDay)
        }

    }

}

//@Preview(showBackground = true)
@Composable
fun ListItem(item: WeatherModel, currentDay: MutableState<WeatherModel>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .clickable {
                if (item.hours.isEmpty()) return@clickable
                currentDay.value = item
            },
        backgroundColor = BlueWait,
        elevation = 4.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        start = 8.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    )
            ) {
                Text(
                    text = item.time,
                    color = Purple700,
                    style = TextStyle(fontStyle = FontStyle.Italic)
                )
                Text(
                    text = item.condition,
                    color = Color.White
                )
            }

            Text(
                text = item.currentTemp.ifEmpty { "${item.maxTemp}/${item.minTemp}" },
                color = Color.White,
                style = TextStyle(fontSize = 24.sp)
            )

            AsyncImage(
                model = "https:${item.icon}",
                contentDescription = "image",
                modifier = Modifier
                    .size(45.dp)
                    .padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun DialogSearch(dialogState: MutableState<Boolean>, onSubmit: (String) -> Unit) {

    val dialogText = remember {
        mutableStateOf("")
    }

    AlertDialog(
//        modifier = Modifier.background(Color.White, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Blue100,
        contentColor = Purple500,
        onDismissRequest = {
            dialogState.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                dialogState.value = false
            }) {
                Text(text = "Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                dialogState.value = false
            }) {
                Text(text = "Cancel")
            }
        },
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Введите название города",
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = dialogText.value,
                    onValueChange = {
                        dialogText.value = it
                    })
            }
        }

    )
}
