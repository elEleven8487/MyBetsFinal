package com.example.mybets.core.api

data class ApiSportsResponse(
    val response: List<MatchData>
)

data class MatchData(
    val fixture: Fixture,
    val league: League,
    val teams: Teams
)

data class League(
    val name: String,
    val logo: String
)

data class Fixture(
    val id: Int,
    val date: String
)

data class Teams(
    val home: TeamDetail,
    val away: TeamDetail
)

data class TeamDetail(
    val name: String,
    val logo: String
)