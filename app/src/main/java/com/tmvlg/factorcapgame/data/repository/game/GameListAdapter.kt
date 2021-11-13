package com.tmvlg.factorcapgame.data.repository.game

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tmvlg.factorcapgame.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GameListAdapter :
    RecyclerView.Adapter<GameListAdapter.GameViewHolder>(){

    private val allGames = ArrayList<Game>()

    inner class GameViewHolder(view: View): RecyclerView.ViewHolder(view){

        val score: TextView = view.findViewById(R.id.score)
        val duration: TextView = view.findViewById(R.id.duration)
        val date : TextView = view.findViewById(R.id.date)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {

        val viewHolder = GameViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.game_statistics_list_item, parent, false))

        return viewHolder
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {

        val currentGame = allGames[position]
        holder.score.text = currentGame.score.toString()
        holder.duration.text = currentGame.duration.toString()
        val sdf = SimpleDateFormat("dd-MM-yy")
        val resultdate = sdf.format(Date(currentGame.date))

        holder.date.text = resultdate.toString()
    }

    override fun getItemCount(): Int {
        return allGames.size
    }

    /* To tell recycler view about the change of data due to viewModel*/
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Game>){
        allGames.clear()
        allGames.addAll(newList)
        notifyDataSetChanged()
    }

}