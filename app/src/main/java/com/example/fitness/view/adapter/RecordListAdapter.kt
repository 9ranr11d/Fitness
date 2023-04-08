package com.example.fitness.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.data.TrainingRecord
import com.example.fitness.util.Utils
import com.example.fitness.databinding.ItemRecyclerRecordBinding

class RecordListAdapter(private val onItemClicked: (TrainingRecord) -> Unit): ListAdapter<TrainingRecord, RecordListAdapter.ViewHolder>(
    DiffCallback
) {
    companion object {
        private val utils = Utils()

        //목록 내용이 바뀔 시 감지 후 변경
        private val DiffCallback = object: DiffUtil.ItemCallback<TrainingRecord>() {
            override fun areItemsTheSame(oldItem: TrainingRecord, newItem: TrainingRecord): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TrainingRecord, newItem: TrainingRecord): Boolean {
                return oldItem == newItem
            }
        }
    }

    //한 줄에 쓸 View 가져오기
    class ViewHolder(private val binding: ItemRecyclerRecordBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(trainingRecord: TrainingRecord) {
            binding.textIRecordDate.text = "${trainingRecord.date}\n${trainingRecord.time}"
            binding.textIRecordPart.text = trainingRecord.part
            binding.textIRecordName.text = trainingRecord.name
            binding.textIRecordSet.text = "${trainingRecord.set}"
            binding.textIRecordRepAndWt.text = utils.crossStr(trainingRecord.set, trainingRecord.rep, trainingRecord.wt)
        }
    }

    //한 줄에 onCreate 메서드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            ItemRecyclerRecordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }

        return viewHolder
    }

    //한 줄 안에 View 조작
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}