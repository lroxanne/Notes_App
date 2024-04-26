package com.example.thenotesapp.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://grammarbot-neural.p.rapidapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val grammarApi: GrammarApi = retrofit.create(GrammarApi::class.java)
}
