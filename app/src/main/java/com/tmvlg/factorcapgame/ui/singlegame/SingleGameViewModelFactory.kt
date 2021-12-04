package com.tmvlg.factorcapgame.ui.singlegame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.game.GameRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository

// this class needed to pass arguments to the viewmodel
class SingleGameViewModelFactory(
    private val gameRepository: GameRepository,
    private val factRepository: FactRepository,
    private val userRepository: UserRepository,
    private val fragment: SingleGameFragment
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            modelClass.getConstructor(
                GameRepository::class.java,
                FactRepository::class.java,
                UserRepository::class.java,
                SingleGameFragment::class.java
            ).newInstance(
                gameRepository,
                factRepository,
                userRepository,
                fragment
            )
        } catch (e: ReflectiveOperationException) {
            val exception = IllegalArgumentException("Unknown view model class $modelClass")
            exception.addSuppressed(e)
            throw exception
        }
    }
}
