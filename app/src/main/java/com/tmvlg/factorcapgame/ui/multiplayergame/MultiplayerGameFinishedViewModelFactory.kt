package com.tmvlg.factorcapgame.ui.multiplayergame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseGameRepository

class MultiplayerGameFinishedViewModelFactory(
    private val firebaseGameRepository: FirebaseGameRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            modelClass.getConstructor(
                FirebaseGameRepository::class.java
            ).newInstance(firebaseGameRepository)
        } catch (e: ReflectiveOperationException) {
            val exception = IllegalArgumentException("Unknown view model class $modelClass")
            exception.addSuppressed(e)
            throw exception
        }
    }
}
