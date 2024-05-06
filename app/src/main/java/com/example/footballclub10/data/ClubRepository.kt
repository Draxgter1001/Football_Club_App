package com.example.footballclub10.data

class ClubRepository(private val clubDao: ClubDAO) {
    suspend fun searchClubs(query: String): List<Club> {
        return clubDao.searchClubs(query)
    }
}