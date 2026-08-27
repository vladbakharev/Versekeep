package com.vladbakharev.versekeep.presentation.navigation

object Screen {
    const val HOME = "home"
    const val LIBRARY = "library"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
    const val DETAILS = "details/{poemId}"
    const val EDITOR = "editor?poemId={poemId}"

    fun details(poemId: Long) = "details/$poemId"

    fun editor(poemId: Long? = null) = poemId?.let { "editor?poemId=$it" } ?: "editor"
}
