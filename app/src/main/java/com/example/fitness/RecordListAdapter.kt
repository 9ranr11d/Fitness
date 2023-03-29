package com.example.fitness

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.databinding.ItemRecyclerViewRecordBinding
import java.util.*

class RecordListAdapter(private val onItemClicked: (TrainingRecord) -> Unit): ListAdapter<TrainingRecord, RecordListAdapter.ViewHolder>(DiffCallback) {

    companion object {
        private val utils = Utils()
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
    class ViewHolder(private val binding: ItemRecyclerViewRecordBinding): RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("SimpleDateFormat")
        fun bind(trainingRecord: TrainingRecord) {
            val dateTime = trainingRecord.date.split("_")
            binding.textRecyclerDate.text = "${dateTime[0]}\n${dateTime[1]}"
            binding.textRecyclerPart.text = trainingRecord.part
            binding.textRecyclerName.text = trainingRecord.name
            binding.textRecyclerSet.text = "${trainingRecord.set}"
            binding.textRecyclerRepAndWt.text = utils.crossStr(trainingRecord.set, trainingRecord.rep, trainingRecord.wt)
        }
    }

    //한 줄에 onCreate 메서드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            ItemRecyclerViewRecordBinding.inflate(
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