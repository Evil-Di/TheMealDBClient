package ru.otusevildi.themealdbclient.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.otusevildi.themealdbclient.data.Recipe
import ru.otusevildi.themealdbclient.databinding.ListItemSearchBinding

class SearchListAdapter(private val onRecipeClick: (String?) -> Unit
    ) : RecyclerView.Adapter<SearchListAdapter.ListItemSearchViewHolder>() {

    private var data: List<Recipe> = emptyList()

    class ListItemSearchViewHolder(var binding: ListItemSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemSearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemSearchBinding.inflate(inflater, parent, false)
        return ListItemSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListItemSearchViewHolder, position: Int) {
        if (position < data.size) {
            val item = data[position]
            with(holder.binding) {
                root.setOnClickListener { onRecipeClick(item.id) }
                name.text = item.name
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Recipe>?) {
        if (data != null) {
            this.data = data
            notifyDataSetChanged()
        }
    }
}