package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import androidx.recyclerview.widget.DiffUtil
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby

class LobbiesDiffCallback : DiffUtil.ItemCallback<Lobby>() {
    override fun areItemsTheSame(oldItem: Lobby, newItem: Lobby): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Lobby, newItem: Lobby): Boolean {
        return oldItem == newItem
    }
}