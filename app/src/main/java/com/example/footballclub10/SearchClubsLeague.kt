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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.footballclub10.ui.theme.FootBallClub10Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
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
                    SearchClubsLeagueContent()
                }
            }
        }
    }
}

@Composable
fun SearchClubsLeagueContent(){
    var clubInfoDisplay by remember{ mutableStateOf("") }
    var keyword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier.padding(5.dp),
            placeholder = { Text(text = "Enter League Name") },
            value = keyword,
            onValueChange = { keyword = it }
        )

        Spacer(modifier = Modifier.padding(10.dp))

        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ){
            Button(
                onClick =
                {
                    scope.launch {
                        clubInfoDisplay = fetchClubs(keyword, context)
                    }
                },
                modifier = Modifier.padding(end = 20.dp)
                ) {
                Text(text = "Retrieve Clubs")
            }

            Button(
                onClick = { /*TODO*/ },
            ) {
                Text(text = "Save clubs to Database")
            }
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Text(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            text = clubInfoDisplay
        )
    }
}

private suspend fun fetchClubs(keyword: String, context: Context): String{
    val urlString = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=$keyword"
    val url = URL(urlString)
    val con: HttpURLConnection = url.openConnection() as HttpURLConnection

    val stb = StringBuilder()

    withContext(Dispatchers.IO){
        val bf = BufferedReader(InputStreamReader(con.inputStream))
        var line: String? = bf.readLine()
        while(line != null){
            stb.append(line + "\n")
            line = bf.readLine()
        }
    }

    return withContext(Dispatchers.Main) {
        parseJSON(stb, context)
    }
}

private fun parseJSON(stb: StringBuilder, context: Context): String{

    try {
        val json = JSONObject(stb.toString())
        var allClubs = StringBuilder()
        var jsonArray: JSONArray = json.getJSONArray("teams")

        for (i in 0 until jsonArray.length()) {
            val team: JSONObject = jsonArray[i] as JSONObject

            val idTeam = team["idTeam"] as String
            val strTeam = team["strTeam"] as String
            val strTeamShort = team["strTeamShort"] as String
            val strAlternate = team["strAlternate"] as String
            val intFormedYear = team["intFormedYear"] as String
            val strLeague = team["strLeague"] as String
            val idLeague = team["idLeague"] as String
            val strStadium = team["strStadium"] as String
            val strKeywords = team["strKeywords"] as String
            val strStadiumThumb = team["strStadiumThumb"] as String
            val strStadiumLocation = team["strStadiumLocation"] as String
            val intStadiumCapacity = team["intStadiumCapacity"] as String
            val strWebsite = team["strWebsite"] as String
            val strTeamJersey = team["strTeamJersey"] as String
            val strTeamLogo = team["strTeamLogo"] as String

            allClubs.append("  Team ID: $idTeam\n  Name: $strTeam\n" +
                    "  Team Short: $strTeamShort\n  Alternate: $strAlternate\n  Formed Year: $intFormedYear\n " +
                    "  League: $strLeague\n " +
                    "  League ID: $idLeague\n  Stadium: $strStadium\n " +
                    "  Keywords: $strKeywords\n  Stadium Thumb: $strStadiumThumb\n " +
                    "  Stadium Location: $strStadiumLocation\n  Stadium Capacity: $intStadiumCapacity\n " +
                    "  Website: $strWebsite\n  TeamJersey: $strTeamJersey\n  TeamLogo: $strTeamLogo\n\n")
        }

        return allClubs.append("\n\n").toString()
    } catch (e: JSONException) {
        Toast.makeText(context, "No teams found for the given league.", Toast.LENGTH_SHORT).show()
        return ""
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to retrieve clubs: ${e.message}", Toast.LENGTH_SHORT).show()
        return ""
    }
}
