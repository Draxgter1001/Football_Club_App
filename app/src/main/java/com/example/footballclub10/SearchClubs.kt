package com.example.footballclub10

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.footballclub10.data.Club
import com.example.footballclub10.data.ClubRepository
import com.example.footballclub10.ui.theme.FootBallClub10Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class SearchClubs : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FootBallClub10Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SearchClubsContent()
                }
            }
        }
    }
}

@Composable
fun SearchClubsContent(){
    var keyword by rememberSaveable {  mutableStateOf("") }
    var searchResults by rememberSaveable { mutableStateOf<List<Club>>(emptyList()) }
    val clubRepository = remember { ClubRepository(db.getClubDao()) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
        )
    {
        TextField(
            modifier = Modifier.padding(top = 20.dp),
            placeholder = {Text(text = "Enter Club or League name")}, 
            value = keyword, 
            onValueChange = { keyword = it }
        )

        Spacer(modifier = Modifier.padding(10.dp))

        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick =
                {
                    try{
                        scope.launch(Dispatchers.IO) {
                            val results = clubRepository.searchClubs(keyword)
                            withContext(Dispatchers.Main){
                                searchResults = results
                            }
                        }
                    }catch (e: Exception){
                        Toast.makeText(context, "Failed to retrieve clubs or leagues, try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            {
                Text(text = "Search", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.padding(10.dp))

        if(searchResults.isNotEmpty()){
            Text(text = "Clubs with the following keyword '$keyword':", modifier = Modifier.padding(bottom = 10.dp))
            ShowClubList(clubs = searchResults)
        }
    }
}

@Composable
fun loadImageBitmap(imageUrl: String): ImageBitmap? {
    return remember(imageUrl) {
        var image: ImageBitmap? = null
        kotlinx.coroutines.runBlocking {
            withContext(Dispatchers.IO) {
                try {
                    val url = URL(imageUrl)
                    val result = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    image = result.asImageBitmap()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        image
    }
}

@Composable
fun ShowClubList(clubs: List<Club>) {
    LazyColumn {
        items(clubs) { club ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val imageBitmap = loadImageBitmap(club.strTeamLogo)
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Club Logo",
                        modifier = Modifier.size(64.dp)
                    )
                }
                Text(
                    text = club.strTeam,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}