package com.tmvlg.factorcapgame.ui.multiplayergame.scoreboard


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.tmvlg.factorcapgame.data.repository.firebase.Player
import com.tmvlg.factorcapgame.databinding.PlayerScoreboardBinding

class PlayersScoreboardAdapter : ListAdapter<Player, PlayerViewHolder>(PlayersDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlayerScoreboardBinding.inflate(inflater, parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val item = getItem(position)
        with (holder) {
            binding.memberUsername.text = item.playerName
            binding.memberResult.text = item.score.toString()
        }
    }
}