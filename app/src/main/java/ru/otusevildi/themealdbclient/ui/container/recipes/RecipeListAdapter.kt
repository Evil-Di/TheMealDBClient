package ru.otusevildi.themealdbclient.ui.container.recipes

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.otusevildi.themealdbclient.data.RecipeShort
import ru.otusevildi.themealdbclient.databinding.ListItemRecipeBinding

class RecipeListAdapter(private val context: Context,
                        private val onRecipeClick: (String?) -> Unit
    ) : RecyclerView.Adapter<RecipeListAdapter.RecipeListItemViewHolder>() {

    private var data: List<RecipeShort> = emptyList()

    class RecipeListItemViewHolder(var binding: ListItemRecipeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = (data.size-1)/3 + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeListItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemRecipeBinding.inflate(inflater, parent, false)
        return RecipeListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeListItemViewHolder, position: Int) {
        if (position < data.size) {
            with(holder.binding) {
                card1.visibility = View.INVISIBLE
                card2.visibility = View.INVISIBLE
                card3.visibility = View.INVISIBLE
                for (i in 0 until 3) {
                    val pos = 3 * position + i
                    if (pos < data.size) {
                        val item = data[pos]
                        when (i) {
                            0 -> Glide.with(context).apply {
                                load(item.tLink).centerCrop().into(image1).also {
                                    name1.text = item.name
                                    card1.visibility = View.VISIBLE
                                    card1.setOnClickListener { onRecipeClick(item.id) }
                                }
                            }

                            1 -> Glide.with(context).apply {
                                load(item.tLink).centerCrop().into(image2).also {
                                    name2.text = item.name
                                    card2.visibility = View.VISIBLE
                                    card2.setOnClickListener { onRecipeClick(item.id) }
                                }
                            }

                            2 -> Glide.with(context).apply {
                                load(item.tLink).centerCrop().into(image3).also {
                                    name3.text = item.name
                                    card3.visibility = View.VISIBLE
                                    card3.setOnClickListener { onRecipeClick(item.id) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<RecipeShort>?) {
        if (data != null) {
            this.data = data
            notifyDataSetChanged()
        }
    }
}