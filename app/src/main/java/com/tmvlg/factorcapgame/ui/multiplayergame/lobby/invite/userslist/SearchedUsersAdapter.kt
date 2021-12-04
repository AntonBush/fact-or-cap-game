package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.invite.userslist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.data.repository.firebase.Player
import com.tmvlg.factorcapgame.databinding.FoundUserBinding

class SearchedUsersAdapter :
    ListAdapter<Player, SearchedUsersViewHolder>(SearchedUsersDiffCallback()) {

    var onSearchedUserClickListener: ((Player) -> Unit)? = null

    var context: Context? = null

    var currentSelection = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedUsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context!!
        val binding = FoundUserBinding.inflate(inflater, parent, false)
        return SearchedUsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchedUsersViewHolder, position: Int) {
        val item = getItem(position)

        with(holder) {

            val borderResId = if (position == currentSelection)
                R.drawable.green_border
            else
                0

            binding.userLayout.setBackgroundResource(borderResId)

            binding.usernameTextview.text = item.name

            binding.userLayout.setOnClickListener {
                notifyItemChanged(currentSelection)
                currentSelection = layoutPosition
                notifyItemChanged(currentSelection)

                onSearchedUserClickListener?.invoke(getItem(currentSelection))
            }
        }
    }
}
