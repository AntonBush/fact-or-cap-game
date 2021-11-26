package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find.outdated

import android.util.SparseBooleanArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class SelectableAdapter<S, T : SelectableAdapter.ViewHolder>(
    var clickListener: ViewHolder.ClickListener? = null,
    data: List<S> = emptyList()
) : RecyclerView.Adapter<T>() {
    var data: List<S> = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private var selectedItems = SparseBooleanArray()

    override fun getItemCount(): Int {
        return data.size
    }

    fun isSelected(position: Int): Boolean {
        return selectedItems[position]
    }

    fun toggleSelection(position: Int) {
        if (selectedItems[position, false]) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    fun getSelectedItems(): IntArray {
        val selectedCount = getSelectedItemsCount()
        val selected = IntArray(selectedCount)
        for (i in 0 until selectedCount) {
            selected[i] = selectedItems.keyAt(i)
        }
        return selected
    }

    fun clearSelection() {
        val selectedCount = getSelectedItemsCount()
        val selected = getSelectedItems()
        selectedItems.clear()
        selected.forEach { notifyItemChanged(it) }
    }

    fun getSelectedItemsCount(): Int {
        return selectedItems.size();
    }

    fun deleteSelectedItems() {
        val selected = getSelectedItems()
        selected.sort()

        for (i in selected.size - 1 downTo 0) {
            val position = selected[i]
//            data.
//            data.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    open class ViewHolder(
        itemView: View,
        private val clickListener: ClickListener?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        init {
            itemView.setOnClickListener(::onClick)
            itemView.setOnLongClickListener(::onLongClick)
        }

        override fun onClick(v: View?) {
            clickListener?.onItemClicked(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            return clickListener?.onItemLongClicked(adapterPosition) ?: false
        }

        interface ClickListener {
            fun onItemClicked(position: Int)
            fun onItemLongClicked(position: Int): Boolean
        }
    }
}
