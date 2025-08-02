package ru.otusevildi.themealdbclient.ui.categories.categoriestab

import android.annotation.SuppressLint
import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.otusevildi.themealdbclient.data.RecipeCategory
import ru.otusevildi.themealdbclient.databinding.ListItemCategoryBinding


class CategoriesListAdapter(private val context: Context,
                            private val onCategoryClick: (String?) -> Unit,
                            private val onInfoClick: ((View?) -> Unit)?):
    RecyclerView.Adapter<CategoriesListAdapter.CategoryListItemViewHolder>() {

    private var data: List<RecipeCategory> = emptyList()

    class CategoryListItemViewHolder(var binding: ListItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCategoryBinding.inflate(inflater, parent, false)
        return CategoryListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryListItemViewHolder, position: Int) {
        if (position < data.size) {
            val item = data[position]
            with(holder.binding) {
                Glide.with(context)
                    .load(item.tLink)
                    .centerCrop()
                    .into(image)
                root.setOnClickListener { onCategoryClick(item.name) }
                title.text = item.name
                infoSign.setOnClickListener(onInfoClick)
                infoText.movementMethod = ScrollingMovementMethod()
                infoText.text = item.description
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<RecipeCategory>?) {
        if (data != null) {
            this.data = data
            notifyDataSetChanged()
        }
    }
}
