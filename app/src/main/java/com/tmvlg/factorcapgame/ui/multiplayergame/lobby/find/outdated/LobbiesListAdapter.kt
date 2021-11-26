package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find.outdated

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.databinding.LobbyBinding

class LobbiesListAdapter : ListAdapter<Lobby, LobbyViewHolder>(LobbiesDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LobbyBinding.inflate(inflater, parent, false)
        return LobbyViewHolder(binding)
//        return ViewHolder(binding, clickListener = null)
    }

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        val lobby = getItem(position)
        with (holder) {
            binding.lobby.text = lobby.roomName
            binding.host.text = lobby.host
            binding.playersNumber.text = lobby.players.size.toString()
        }
    }

    class ViewHolder(
        val binding: LobbyBinding,
        clickListener: ClickListener?
    ) : SelectableAdapter.ViewHolder(
        binding.root,
        clickListener
    )
}