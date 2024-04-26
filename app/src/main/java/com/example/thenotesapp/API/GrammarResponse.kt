package com.example.thenotesapp.API



data class GrammarResponse(

    val matches: List<Match>?
)
data class Match(
    val message: String,
    val replacements: List<Replacement>
)

data class Replacement(
    val value: String
)

data class LanguageDetail(
    val detectedLanguage: String,
    val language: String
)