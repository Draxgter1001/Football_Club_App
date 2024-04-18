package com.example.footballclub10

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.footballclub10.data.AppDatabase
import com.example.footballclub10.data.League
import com.example.footballclub10.data.LeagueDao
import com.example.footballclub10.ui.theme.FootBallClub10Theme
import kotlinx.coroutines.launch

lateinit var db: AppDatabase
lateinit var league_dao: LeagueDao

class AddLeagues : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(this, AppDatabase::class.java, "league_database").build()
        league_dao = db.getDao()
        setContent {
            FootBallClub10Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AddLeaguesContent()
                }
            }
        }
    }
}

@Composable
fun AddLeaguesContent(){
    var strLeague by remember { mutableStateOf("") }
    var strSport by remember { mutableStateOf("") }
    var strLeagueAlternate by remember{ mutableStateOf("") }

    var context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(Alignment.TopCenter)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                modifier = Modifier.padding(5.dp),
                placeholder = { Text(text = "League Name") },
                value = strLeague,
                onValueChange = { newText -> strLeague = newText }
            )

            TextField(
                modifier = Modifier.padding(5.dp),
                placeholder = { Text(text = "League Sport") },
                value = strSport,
                onValueChange = { newText -> strSport = newText }
            )

            TextField(
                modifier = Modifier.padding(5.dp),
                placeholder = { Text(text = "League Alternate") },
                value = strLeagueAlternate,
                onValueChange = { newText -> strLeagueAlternate = newText }
            )
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    try{
                        val league = strLeague
                        val sport = strSport
                        val leagueAlternate = strLeagueAlternate

                        scope.launch {
                            league_dao.insertLeague(
                                League(strLeague = league, strSport = sport, strLeagueAlternate = leagueAlternate)
                            )
                            Toast.makeText(context, "League added successfully", Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: Exception){
                        Toast.makeText(context, "Failed to add league: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(end = 30.dp)
            ) {
                Text(text = "Add new League", color = Color.Black)
            }

            Button(onClick = { /*TODO*/ }) {
                Text(text = "Retrieve Leagues", color = Color.Black)
            }
        }
    }
}