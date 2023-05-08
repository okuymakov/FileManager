package com.example.vkinternshipapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vkinternshipapp.R
import com.example.vkinternshipapp.core.format
import com.example.vkinternshipapp.core.formatFileSize
import com.example.vkinternshipapp.models.FileModel
import com.example.vkinternshipapp.core.toIconRes

class FileAdapter(private val onClick: (FileModel) -> Unit) :
    RecyclerView.Adapter<FileAdapter.ViewHolder>() {
    private val data = mutableListOf<FileModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], onClick)
    }

    fun submitData(newData: List<FileModel>) {
        if (data == newData) return
        val oldSize = data.size
        data.clear()
        notifyItemRangeRemoved(0, oldSize)
        data += newData
        notifyItemRangeInserted(0, newData.size)
    }

    class ViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
        private val name: TextView = root.findViewById(R.id.file_name)
        private val icon: ImageView = root.findViewById(R.id.file_icon)
        private val size: TextView = root.findViewById(R.id.file_size_or_count)
        private val createdAt: TextView = root.findViewById(R.id.file_date)

        fun bind(file: FileModel, onClick: (FileModel) -> Unit) {
            name.text = if (file.isDirectory) {
                file.name
            } else root.context.getString(R.string.file_name_with_ext, file.name, file.type)
            size.text = if (file.isDirectory) {
                root.context.resources.getQuantityString(
                    R.plurals.dir_items_count,
                    file.itemsCount,
                    file.itemsCount
                )
            } else file.size.formatFileSize(root.context)
            createdAt.text = file.createdAt.format("dd MMM yyyy")
            val iconRes = if (file.isDirectory) R.drawable.ic_dir else file.type.toIconRes()
            icon.setImageDrawable(ContextCompat.getDrawable(root.context, iconRes))
            root.setOnClickListener { onClick(file) }
        }
    }
}

