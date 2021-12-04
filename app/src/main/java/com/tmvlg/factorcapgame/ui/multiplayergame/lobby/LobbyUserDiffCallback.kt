package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import androidx.recyclerview.widget.DiffUtil
import com.tmvlg.factorcapgame.data.repository.firebase.Player

class LobbyUserDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem.name == newItem.name
    }
}
