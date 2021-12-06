package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.databinding.LobbyBinding

class LobbyListAdapter(
    var onLobbySelectedListener: OnLobbySelectedListener? = null
) : ListAdapter<Lobby, LobbyListAdapter.LobbyViewHolder>(LobbyDiffCallback()) {
    var selectedId: String? = null
        private set
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LobbyBinding.inflate(inflater, parent, false)
        return LobbyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        val lobby = getItem(position)
        with(holder) {
            binding.lobby.text = lobby.name
            binding.host.text = lobby.hostName
            binding.playersNumber.text = lobby.players.size.toString()
            binding.root.setOnClickListener {
                val oldPositionSelection = selectedPosition
                selectedPosition = holder.bindingAdapterPosition
                selectedId = lobby.id
                notifyItemChanged(holder.bindingAdapterPosition)
                if (oldPositionSelection != selectedPosition) {
                    notifyItemChanged(oldPositionSelection)
                }
            }
            onLobbySelectedListener?.onLobbySelected(binding, selectedId == lobby.id)
        }
    }

    class LobbyViewHolder(
        val binding: LobbyBinding
    ) : RecyclerView.ViewHolder(binding.root)

    class LobbyDiffCallback : DiffUtil.ItemCallback<Lobby>() {
        override fun areItemsTheSame(oldItem: Lobby, newItem: Lobby): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Lobby, newItem: Lobby): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.hostName == newItem.hostName
                    && oldItem.players.size == newItem.players.size
        }
    }

    fun interface OnLobbySelectedListener {
        fun onLobbySelected(binding: LobbyBinding, isSelected: Boolean)
    }
}
