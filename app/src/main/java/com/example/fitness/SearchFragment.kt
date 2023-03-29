package com.example.fitness

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitness.databinding.FragmentSearchBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //ViewBinding
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recordRecycler = binding.recyclerSearchRecord
        recordRecycler.layoutManager = LinearLayoutManager(requireContext())

        val recordListAdapter = RecordListAdapter {
            Log.i(tag, "id : ${it.id},part : ${it.part}, name : ${it.name}, set : ${it.set}")
        }
        recordRecycler.adapter = recordListAdapter

        lifecycle.coroutineScope.launch {
            recordListViewModel.allRecord().collect() {
                recordListAdapter.submitList(it)
            }
        }
    }

    private val recordListViewModel: RecordListViewModel by activityViewModels {
        RecordListViewModelFactory(
            (activity?.application as RecordListApplication).dataBase.TrainingRecordDAO()
        )
    }
}