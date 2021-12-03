package com.tmvlg.factorcapgame.data.auth

data class User(
    val name: String,
    val idToken: String
) {
    fun toMap(): Map<String, String> {
        return mapOf(
            "name" to name,
            "token" to idToken
        )
    }
}