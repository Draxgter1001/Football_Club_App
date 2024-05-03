package com.example.footballclub10

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.footballclub10.ui.theme.FootBallClub10Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ClubsJersey : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FootBallClub10Theme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ClubsJerseyContent()
                }
            }
        }
    }
}

@Composable
fun ClubsJerseyContent() {
    var leagueName by rememberSaveable { mutableStateOf("") }
    var clubSubstring by rememberSaveable { mutableStateOf("") }
    val jerseys = remember { mutableStateListOf<Pair<String, Bitmap?>>() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = leagueName,
            onValueChange = { leagueName = it },
            label = { Text("Enter a League Name") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = clubSubstring,
            onValueChange = { clubSubstring = it },
            label = { Text("Enter a Club Name") },
            singleLine = true,
            enabled = leagueName.isNotBlank()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                scope.launch {
                    jerseys.clear()
                    try {
                        fetchJerseys(leagueName, clubSubstring, jerseys, context)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to fetch data: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            enabled = leagueName.isNotBlank() && clubSubstring.isNotBlank()
        ) {
            Text("Fetch Jerseys")
        }
        Spacer(modifier = Modifier.height(10.dp))
        jerseys.forEachIndexed { index, (year, bitmap) ->
            bitmap?.let {
                Image(bitmap = it.asImageBitmap(), contentDescription = "Jersey from $year")
                Spacer(modifier = Modifier.height(10.dp))
                if (index < jerseys.size - 1) {
                    Text("Year: $year", modifier = Modifier.padding(horizontal = 8.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

suspend fun fetchJerseys(leagueName: String, clubSubstring: String, jerseys: MutableList<Pair<String, Bitmap?>>, context: android.content.Context) {
    withContext(Dispatchers.IO) {
        try {
            val teamIds = fetchTeamIds(leagueName, clubSubstring, context)
            teamIds.forEach { teamId ->
                try {
                    val jerseysUrl = URL("https://www.thesportsdb.com/api/v1/json/3/lookupequipment.php?id=$teamId")
                    val connection = jerseysUrl.openConnection() as HttpsURLConnection
                    connection.inputStream.bufferedReader().use { reader ->
                        val result = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            result.append(line)
                        }
                        val json = JSONObject(result.toString())
                        val equipments = json.getJSONArray("equipment")
                        for (i in 0 until equipments.length()) {
                            val equipment = equipments.getJSONObject(i)
                            val jerseyUrl = equipment.getString("strEquipment")
                            val year = equipment.getString("strSeason")
                            if (jerseyUrl.isNotEmpty()) {
                                val bitmap = loadBitmap(jerseyUrl)
                                jerseys.add(year to bitmap)
                            }
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error loading jerseys: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: JSONException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error parsing data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun loadBitmap(url: String): Bitmap? {
    return try {
        val inputStream: InputStream = URL(url).openStream()
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        null
    }
}

suspend fun fetchTeamIds(leagueName: String, clubSubstring: String, context: android.content.Context): List<String> {
    val urlString = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=$leagueName"
    val url = URL(urlString)
    val connection = url.openConnection() as HttpsURLConnection
    val teamIds = mutableListOf<String>()
    try {
        connection.inputStream.bufferedReader().use { reader ->
            val result = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result.append(line)
            }

            val json = JSONObject(result.toString())
            val teams = json.getJSONArray("teams")
            for (i in 0 until teams.length()) {
                val team = teams.getJSONObject(i)
                val teamName = team.getString("strTeam")
                if (teamName.contains(clubSubstring, ignoreCase = true)) {
                    teamIds.add(team.getString("idTeam"))
                }
            }
        }
    } catch (e: JSONException) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error parsing team data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    } catch (e: IOException) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    return teamIds
}
