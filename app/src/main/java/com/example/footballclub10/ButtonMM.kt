package com.example.footballclub10

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenuButtons(context: Context){
    val fontSize by remember { mutableStateOf(24.sp) }

    Column(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)) {

        Button(onClick = { val intent = Intent(context, AddLeagues::class.java)
            context.startActivity(intent)}, modifier = Modifier
            .size(width = 350.dp, height = 80.dp)
            .padding(top = 4.dp)) {
            Text(text = "Add Leagues to DB", style = TextStyle(fontSize = fontSize), color = Color.Black)
        }

        Button(onClick = { val intent = Intent(context, SearchClubsLeague::class.java)
            context.startActivity(intent)}, modifier = Modifier
            .size(width = 350.dp, height = 100.dp)
            .padding(top = 30.dp)) {
            Text(text = "Search for Clubs By League", style = TextStyle(fontSize = fontSize), color = Color.Black)
        }

        Button(onClick = { val intent = Intent(context, SearchClubs::class.java)
            context.startActivity(intent) }, modifier = Modifier
            .size(width = 350.dp, height = 100.dp)
            .padding(top = 30.dp)) {
            Text(text = "Search for Clubs", style = TextStyle(fontSize = fontSize), color = Color.Black)
        }
    }
}