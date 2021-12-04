package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.invite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.user.UserRepository

class InviteFragmentViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            modelClass.getConstructor(
                UserRepository::class.java
            ).newInstance(userRepository)
        } catch (e: ReflectiveOperationException) {
            val exception = IllegalArgumentException("Unknown view model class $modelClass")
            exception.addSuppressed(e)
            throw exception
        }
    }
}
