package com.example.footballclub10

import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.footballclub10.data.Club
import com.example.footballclub10.data.ClubDAO
import com.example.footballclub10.ui.theme.FootBallClub10Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class SearchClubsLeague : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FootBallClub10Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SearchClubsLeagueContent(club_dao)
                }
            }
        }
    }
}

@Composable
fun SearchClubsLeagueContent(clubDao: ClubDAO) { // Pass ClubDao as a parameter to the composable function
    var clubInfoDisplay by rememberSaveable { mutableStateOf("") }
    var keyword by rememberSaveable { mutableStateOf("") }
    var saveToDatabase by rememberSaveable { mutableStateOf(false) }
    var isClicked by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier.padding(top = 20.dp),
            placeholder = { Text(text = "Enter League Name") },
            value = keyword,
            onValueChange = { keyword = it }
        )

        Spacer(modifier = Modifier.padding(10.dp))

        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    isClicked = true
                    saveToDatabase = false
                    scope.launch {
                        try {
                            val stb = getClubs(keyword)
                            clubInfoDisplay = parseJSON(stb, context, saveToDatabase, clubDao)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to retrieve clubs: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.padding(end = 20.dp)
            ) {
                Text(text = "Retrieve Clubs", color = Color.Black)
            }

            Button(
                enabled = isClicked && keyword.isNotEmpty(),
                onClick = {
                    isClicked = false
                    saveToDatabase = true
                    scope.launch {
                        try {
                            val stb = getClubs(keyword)
                            clubInfoDisplay = parseJSON(stb, context, saveToDatabase, clubDao)
                            Toast.makeText(context, "Clubs saved to database", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to save clubs: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            saveToDatabase = false
                        }
                    }
                }
            ) {
                Text(text = "Save clubs to Database", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Text(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            text = clubInfoDisplay
        )
    }
}

private suspend fun getClubs(keyword: String): StringBuilder {
    val urlString = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=$keyword"
    val url = URL(urlString)
    val con: HttpURLConnection = url.openConnection() as HttpURLConnection

    val stb = StringBuilder()

    withContext(Dispatchers.IO) {
        val bf = BufferedReader(InputStreamReader(con.inputStream))
        var line: String? = bf.readLine()
        while (line != null) {
            stb.append(line + "\n")
            line = bf.readLine()
        }
    }

    return stb
}

private suspend fun parseJSON(stb: StringBuilder, context: Context, saveToDatabase: Boolean, clubDao: ClubDAO): String {
    val clubs = mutableListOf<Club>()
    try {
        val json = JSONObject(stb.toString())
        val allClubs = StringBuilder()
        val jsonArray = json.getJSONArray("teams")

        for (i in 0 until jsonArray.length()) {
            val team = jsonArray.getJSONObject(i)

            val club = Club(
                idTeam = team.optString("idTeam", ""),
                strTeam = team.optString("strTeam", ""),
                strTeamShort = team.optString("strTeamShort", ""),
                strAlternate = team.optString("strAlternate", ""),
                intFormedYear = team.optString("intFormedYear", ""),
                strLeague = team.optString("strLeague", ""),
                idLeague = team.optString("idLeague", ""),
                strStadium = team.optString("strStadium", ""),
                strKeywords = team.optString("strKeywords", ""),
                strStadiumThumb = team.optString("strStadiumThumb", ""),
                strStadiumLocation = team.optString("strStadiumLocation", ""),
                intStadiumCapacity = team.optString("intStadiumCapacity", ""),
                strWebsite = team.optString("strWebsite", ""),
                strTeamJersey = team.optString("strTeamJersey", ""),
                strTeamLogo = team.optString("strTeamLogo", "")
            )

            clubs.add(club)
            allClubs.append("  Team ID: ${club.idTeam}\n  Name: ${club.strTeam}\n" +
                    "  Team Short: ${club.strTeamShort}\n  Alternate: ${club.strAlternate}\n  Formed Year: ${club.intFormedYear}\n " +
                    "  League: ${club.strLeague}\n " +
                    "  League ID: ${club.idLeague}\n  Stadium: ${club.strStadium}\n " +
                    "  Keywords: ${club.strKeywords}\n  Stadium Thumb: ${club.strStadiumThumb}\n " +
                    "  Stadium Location: ${club.strStadiumLocation}\n  Stadium Capacity: ${club.intStadiumCapacity}\n " +
                    "  Website: ${club.strWebsite}\n  TeamJersey: ${club.strTeamJersey}\n  TeamLogo: ${club.strTeamLogo}\n\n")
        }

        if (saveToDatabase) {
            clubDao.insertClubs(clubs)
        }

        return allClubs.toString()
    } catch (e: JSONException) {
        Toast.makeText(context, "No clubs found for the given league.", Toast.LENGTH_SHORT).show()
        return ""
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to retrieve clubs: ${e.message}", Toast.LENGTH_SHORT).show()
        return ""
    }
}
