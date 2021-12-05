package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.firebase.Player
import com.tmvlg.factorcapgame.databinding.LobbyUserBinding

class LobbyUserListAdapter(
    isHost: Boolean = false,
    hostName: String? = null,
    onClickListener: OnClickListener? = null
) : ListAdapter<Player, LobbyUserListAdapter.LobbyUserViewHolder>(LobbyUserDiffCallback()) {
    var isHost: Boolean = isHost
        set(value) {
            if (field != value) {
                field = value
                notifyItemRangeChanged(0, itemCount)
            }
        }
    var hostName = hostName
        set(value) {
            if (field != value) {
                field = value
                notifyItemRangeChanged(0, itemCount)
            }
        }
    var onClickListener = onClickListener
        set(value) {
            if (field != value) {
                field = value
                notifyItemRangeChanged(0, itemCount)
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyUserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LobbyUserBinding.inflate(inflater, parent, false)
        return LobbyUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LobbyUserViewHolder, position: Int) {
        val player = getItem(position)
        with(holder) {
            binding.userName.text = player.name
            if (!isHost
                || hostName == null
                || hostName == player.name
            ) {
                binding.removeButton.isEnabled = false
                binding.removeButton.setOnClickListener(null)
                binding.removeButton.visibility = View.INVISIBLE
            } else {
                binding.removeButton.visibility = View.VISIBLE
                binding.removeButton.setOnClickListener {
                    onClickListener?.onClick(player)
                }
                binding.removeButton.isEnabled = true
            }
        }
    }

    class LobbyUserViewHolder(
        val binding: LobbyUserBinding
    ) : RecyclerView.ViewHolder(binding.root)

    class LobbyUserDiffCallback : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.name == newItem.name
        }
    }

    interface OnClickListener {
        fun onClick(player: Player)
    }
}
