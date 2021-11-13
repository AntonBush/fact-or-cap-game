package com.tmvlg.factorcapgame.ui.singlegame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.game.GameRepositoryImpl
import com.tmvlg.factorcapgame.data.repository.user.UserRepository

class SingleGameViewModelFactory(
    private val gameRepository: GameRepositoryImpl,
    private val factRepository: FactRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SingleGameViewModel::class.java))
            return SingleGameViewModel(gameRepository, factRepository, userRepository) as T
        throw IllegalArgumentException("Unknown view model class $modelClass")
    }
}
