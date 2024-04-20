package com.example.footballclub10.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClubDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClubs(clubs: List<Club>)

    @Query("select * from club where strTeam like '%' || :query || '%' or strLeague like '%' || :query || '%'")
    suspend fun searchClubs(query: String): List<Club>
}