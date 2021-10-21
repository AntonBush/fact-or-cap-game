package com.tmvlg.factorcapgame.data.repository.user

class UserRepositoryImpl(val username : String,
                         val total_games : Int,
                         val highest_score : Int,
                         val last_score : Int,
                         val average_score : Int,
                         val all_scores : Int): UserRepository{
}