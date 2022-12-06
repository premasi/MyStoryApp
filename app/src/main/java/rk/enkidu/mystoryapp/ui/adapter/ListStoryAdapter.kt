package rk.enkidu.mystoryapp.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rk.enkidu.mystoryapp.data.response.ListStoryItem
import rk.enkidu.mystoryapp.databinding.ListStoryItemBinding
import rk.enkidu.mystoryapp.ui.activity.DetailActivity

class ListStoryAdapter:
    PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemBinding = ListStoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if(data != null){
            holder.bind(data)
        }
    }

    class ListViewHolder(private val itemBinding: ListStoryItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(itemItem: ListStoryItem) {

            Glide.with(itemView.context)
                .load(itemItem.photoUrl) // URL Gambar
                .into(itemBinding.ivImage) // imageView mana yang akan diterapkan

            itemBinding.tvName.text = itemItem.name
            itemBinding.tvDate.text = itemItem.createdAt


            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_ID, itemItem.id)
                itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity)
                    .toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}