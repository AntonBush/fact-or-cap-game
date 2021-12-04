package com.tmvlg.factorcapgame.ui.multiplayergame.scoreboard

import androidx.recyclerview.widget.DiffUtil
import com.tmvlg.factorcapgame.data.repository.firebase.Player

class PlayersDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem == newItem
    }
}
