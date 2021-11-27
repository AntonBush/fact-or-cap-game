package com.tmvlg.factorcapgame.ui.leaderboard

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tmvlg.factorcapgame.R
import kotlin.collections.ArrayList

class PlayerListAdapter:
    RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder>() {

    private val allScores = ArrayList<PlayerScore>()

    inner class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val place: TextView = view.findViewById(R.id.place)
        val username: TextView = view.findViewById(R.id.username)
        val score: TextView = view.findViewById(R.id.username_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val viewHolder = PlayerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.player_score_leaderboard_list_item, parent, false)
        )
        return viewHolder
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        Log.d("Notification", "datachanging")
        val item = allScores[position]
        holder.score.text = item.score
        holder.place.text = item.place
        holder.username.text = item.username
    }

    override fun getItemCount(): Int {
        return allScores.size
    }

    /* To tell recycler view about the change of data due to viewModel*/
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<PlayerScore>) {
        Log.d("Notification", newList.toString())
        allScores.clear()
        allScores.addAll(newList)
        notifyDataSetChanged()
    }
}
