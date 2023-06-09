package com.example.fitness.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitness.*
import com.example.fitness.view.model.RecordListApplication
import com.example.fitness.view.model.RecordListViewModel
import com.example.fitness.view.model.RecordListViewModelFactory
import com.example.fitness.data.TrainingRecord
import com.example.fitness.databinding.FragmentSearchBinding
import com.example.fitness.util.Utils
import com.example.fitness.view.activity.OneRecordActivity
import com.example.fitness.view.adapter.RecordListAdapter
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
//    private val TAG = javaClass.simpleName
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val utils = Utils()

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var recordListAdapter: RecordListAdapter

    private val recordListViewModel: RecordListViewModel by activityViewModels {
        RecordListViewModelFactory(
            (activity?.application as RecordListApplication).dataBase.TrainingRecordDAO()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //ViewBinding
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        setRecord()
        initSpinner()
        initSearch()
        setBtnListener()
        getLauncherResult()
    }

    private fun setBtnListener() {
        binding.btnSearchAdd.setOnClickListener {
            val tempDateTime = utils.getDateTime("yyyy-MM-dd HH:mm").split(" ")
            val addRecord = TrainingRecord(0, tempDateTime[0], tempDateTime[1], "", "", 0, "", "")
            val goOneRecord = Intent(requireContext(), OneRecordActivity::class.java)
            goOneRecord.putExtra("Target", addRecord)
            goOneRecord.putExtra("Is_edit", false)

            launcher.launch(goOneRecord)
        }
    }

    //SearchView
    private fun initSearch() {
        binding.searchSearch.isSubmitButtonEnabled = true   //검색 완료 버튼
        binding.searchSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(str: String?): Boolean {     //검색 완료 버튼 클릭 시
                if(binding.spinnerSearchDivision.selectedItem.toString() == "이름")   //검색할 대상을 '이름'
                    setSearchRecord(true, str!!)
                else                                                                 //'부위'로 검색
                    setSearchRecord(false, str!!)

                return true
            }

            override fun onQueryTextChange(str: String?): Boolean {     //검색어가 변경될 시
                if(str == "")       //검색어가 없을 때 전체 목록 표시
                    setRecord()

                return true
            }
        })
    }

    //Spinner
    private fun initSpinner() {
        binding.spinnerSearchDivision.adapter = utils.setSpinnerAdapter(
            requireContext(), resources.getStringArray(R.array.search_division).toCollection(ArrayList())
        )
    }

    //RecyclerView
    private fun initRecycler() {
        binding.recyclerSearchRecord.layoutManager = LinearLayoutManager(requireContext())

        //RecyclerView 목록 클릭 시
        recordListAdapter = RecordListAdapter {
            val goOneRecord = Intent(context, OneRecordActivity::class.java)
            goOneRecord.putExtra("Target", it)
            goOneRecord.putExtra("Is_edit", true)

            launcher.launch(goOneRecord)
        }

        binding.recyclerSearchRecord.adapter = recordListAdapter
        binding.recyclerSearchRecord.addItemDecoration(DividerItemDecoration(view?.context, 1))
    }

    //RecyclerView 전체 데이터 삽입
    private fun setRecord() {
        lifecycle.coroutineScope.launch {
            recordListViewModel.allRecord().collect {
                recordListAdapter.submitList(it)
            }
        }
    }

    //RecyclerView 검색된 데이터 삽입
    private fun setSearchRecord(isName: Boolean, keyword: String) {
        lifecycle.coroutineScope.launch {
            recordListViewModel.allRecord().collect {
                val searchRecords = mutableListOf<TrainingRecord>()

                //Keyword 포함한 기록 차출
                for(one in it) {
                    if(isName) {
                        if(one.name.lowercase().contains(keyword.lowercase()))
                            searchRecords += one
                    }else {
                        if (one.part.lowercase().contains(keyword.lowercase()))
                            searchRecords += one
                    }
                }

                recordListAdapter.submitList(searchRecords)
            }
        }
    }

    //Launcher Result
    private fun getLauncherResult() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Utils.RESULT_INSERT -> utils.makeToast(requireContext(), "기록에 성공했습니다.")
                Utils.RESULT_UPDATE -> utils.makeToast(requireContext(), "기록 갱신에 성공했습니다.")
                Utils.RESULT_DELETE -> utils.makeToast(requireContext(), "기록 삭제에 성공했습니다.")
                Activity.RESULT_CANCELED -> utils.makeToast(requireContext(), "취소되었습니다.")
            }
        }
    }

    //Fragment ViewBinding 삭제
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}