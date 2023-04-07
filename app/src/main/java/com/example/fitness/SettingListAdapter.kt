package com.example.fitness

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.databinding.ItemRecyclerSettingBinding

class SettingListAdapter(private val menuList: Array<String>, private val onItemClicked: (String) -> Unit): RecyclerView.Adapter<SettingListAdapter.ViewHolder>() {
    //한 줄에 쓸 View 가져오기
    class ViewHolder(private val binding: ItemRecyclerSettingBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.textISettingTitle.text = title
        }
    }

    //한 줄에 onCreate 메서드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            ItemRecyclerSettingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(menuList[position])
        }

        return viewHolder
    }

    //목록 개수
    override fun getItemCount(): Int = menuList.size

    //한 줄 안에 View 조작
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menuList[position])
    }
}