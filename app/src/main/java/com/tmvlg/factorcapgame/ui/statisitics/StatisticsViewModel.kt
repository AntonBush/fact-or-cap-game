package com.tmvlg.factorcapgame.ui.statisitics

import androidx.lifecycle.ViewModel
import com.tmvlg.factorcapgame.data.repository.user.UserRepositoryImpl

class StatisticsViewModel : ViewModel() {
    val repository = UserRepositoryImpl()
}
