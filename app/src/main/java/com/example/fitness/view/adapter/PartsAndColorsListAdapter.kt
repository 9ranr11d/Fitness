package com.example.fitness.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.util.ItemTouchHelperListener
import com.example.fitness.data.PartColor
import com.example.fitness.databinding.ItemRecyclerPartAndColorBinding

class PartsAndColorsListAdapter(private val localList: ArrayList<PartColor>, private val onItemTouch: (String) -> Unit)
    : RecyclerView.Adapter<PartsAndColorsListAdapter.ViewHolder>(),
    ItemTouchHelperListener {
    //한 줄에 쓸 View 가져오기
    class ViewHolder(private val binding: ItemRecyclerPartAndColorBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(partColor: PartColor) {
            binding.textIPartAndColorPart.text = partColor.part
            binding.textIPartAndColorColor.text = partColor.color
            binding.viewIPartAndColorColor.setBackgroundColor(partColor.color.toLong(16).toInt())
        }

        val editLay = binding.relativeIPartAndColorEdit
        val delLay = binding.relativeIPartAndColorDel
    }

    //한 줄에 onCreate 메서드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecyclerPartAndColorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //목록 개수
    override fun getItemCount(): Int = localList.size

    //한 줄 안에 View 조작
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(localList[position])
    }

    //Drag
    override fun onItemMove(from: Int, to: Int): Boolean {
        val item: PartColor = localList[from]
        localList.removeAt(from)
        localList.add(to, item)
        notifyItemMoved(from, to)

        return true
    }

    //Swipe
    override fun onItemSwipe(position: Int, direction: Int) {
        onItemTouch("${direction}_${localList[position]}_$position")
    }

    //추가
    fun setInsert(target: PartColor) {
        localList.add(target)
        notifyItemInserted(localList.size)
    }

    //수정
    fun setUpdate(target: PartColor, position: Int) {
        localList[position] = target
        notifyItemChanged(position)
    }

    //삭제
    fun setDelete(position: Int) {
        localList.removeAt(position)
        notifyItemRemoved(position)
    }

    //리스트 반환
    fun getAll(): ArrayList<PartColor> {
        return localList
    }
}