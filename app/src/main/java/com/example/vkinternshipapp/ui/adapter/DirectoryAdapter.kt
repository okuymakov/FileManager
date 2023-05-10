package com.example.vkinternshipapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.vkinternshipapp.R
import com.example.vkinternshipapp.core.directoryName

class DirectoryAdapter(
    private val onClick: (String) -> Unit,
) : RecyclerView.Adapter<DirectoryAdapter.ViewHolder>() {
    private val data = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_directory, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], onClick)
    }

    fun submitData(newData: List<String>) {
        val diffCallback = DiffUtilCallback(data, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data.clear()
        data.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
        private val name: TextView = root.findViewById(R.id.directory_name)
        fun bind(dirPath: String, onClick: (String) -> Unit) {
            name.text = dirPath.directoryName(root.context)
            root.setOnClickListener { onClick(dirPath) }
        }
    }

    class DiffUtilCallback(private val oldList: List<String>, private val newList: List<String>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] === newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
