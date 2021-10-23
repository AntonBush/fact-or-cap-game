package com.tmvlg.factorcapgame.ui.menu

import androidx.lifecycle.ViewModel
import com.tmvlg.factorcapgame.data.repository.user.UserRepositoryImpl

class MenuViewModel : ViewModel() {
    val repository = UserRepositoryImpl()
}
