package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.databinding.LobbyBinding
import mva3.adapter.ItemBinder
import mva3.adapter.ItemViewHolder
import mva3.adapter.MultiViewAdapter

class LobbyBinder(
    private val onLobbySelectedListener: OnLobbySelectedListener?
) : ItemBinder<Lobby, LobbyBinder.ViewHolder>() {

    override fun createViewHolder(parent: ViewGroup): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LobbyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun bindViewHolder(holder: ViewHolder, lobby: Lobby) {
        with(holder) {
            binding.lobby.text = lobby.name
            binding.host.text = lobby.hostName
            binding.playersNumber.text = lobby.players.size.toString()
            onLobbySelectedListener?.onLobbySelected(binding, isItemSelected)
        }
    }

    override fun canBindData(item: Any?): Boolean {
        return item is Lobby
    }

    class ViewHolder(
        val binding: LobbyBinding
    ) : ItemViewHolder<Lobby>(binding.root) {
        init {
            binding.root.setOnClickListener {
                Log.d("LobbyBinder.ViewHolder", "click")
                toggleItemSelection()
            }
        }
    }

    interface OnLobbySelectedListener {
        fun onLobbySelected(binding: LobbyBinding, isSelected: Boolean)
    }
}

fun LobbyListAdapter(
    onLobbySelectedListener: LobbyBinder.OnLobbySelectedListener? = null
): MultiViewAdapter {
    return MultiViewAdapter().apply {
        // Register Binders
        registerItemBinders(LobbyBinder(onLobbySelectedListener))
    }
}
