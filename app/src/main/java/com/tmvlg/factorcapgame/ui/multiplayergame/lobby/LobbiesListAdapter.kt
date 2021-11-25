package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.databinding.LobbyBinding
import com.tmvlg.factorcapgame.ui.multiplayergame.scoreboard.PlayerViewHolder

class LobbiesListAdapter : ListAdapter<Lobby, LobbyViewHolder>(LobbiesDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LobbyBinding.inflate(inflater, parent, false)
        return LobbyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        val item = getItem(position)
        with (holder) {
            binding.lobby.text = item.roomName
            binding.host.text = item.host
            binding.playersNumber.text = item.players.size.toString()
        }
    }

}