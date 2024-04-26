package com.example.thenotesapp.API

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface GrammarApi {
    @Headers(
        "X-RapidAPI-Key: 4cc70e33femsh29fe8d0a5374c63p1ed6c9jsn84d195dfed83",
        "X-RapidAPI-Host: grammarbot-neural.p.rapidapi.com"
    )
    @POST("/check")
    fun checkGrammar(
        @Query("text") text: String,
        @Query("language") language: String?= "en"
    ): Call<GrammarResponse>
}
