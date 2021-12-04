package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.invite.userslist

import androidx.recyclerview.widget.DiffUtil
import com.tmvlg.factorcapgame.data.repository.firebase.Player

class SearchedUsersDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem == newItem
    }
}
