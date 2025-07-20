package ru.otusevildi.themealdbclient.ui.container.favorites

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.otusevildi.themealdbclient.data.Recipe
import ru.otusevildi.themealdbclient.databinding.ListItemFavoriteBinding

class FavoritesListAdapter(private val context: Context, private val onRecipeClick: (String?) -> Unit):
    RecyclerView.Adapter<FavoritesListAdapter.RecentItemViewHolder>() {

    private var data: List<Recipe> = emptyList()

    class RecentItemViewHolder(var binding: ListItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = data.size // Количество элементов в списке данных

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemFavoriteBinding.inflate(inflater, parent, false)

        return RecentItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentItemViewHolder, position: Int) {
        if (position < data.size) {
            val item = data[position]
            with(holder.binding) {
                Glide.with(context)
                    .load(item.tLink)
                    .centerCrop()
                    .into(image)
                root.setOnClickListener { onRecipeClick(item.id) }
                title.text = item.name
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

