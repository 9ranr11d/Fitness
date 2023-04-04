package com.example.fitness

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import com.example.fitness.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class CalendarFragment : Fragment() {
    private val TAG = javaClass.simpleName
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val utils = Utils()

    private var partsList = ArrayList<String>()

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
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCalendar()
        getPartsList()

        getRecordList()
    }

    //운동 부위 Spinner 쓰일 목록
    private fun getPartsList() {
        val sharedPreferences = this.requireActivity().getSharedPreferences(MainActivity.utilFileName, Activity.MODE_PRIVATE)
        val tempPartsList = sharedPreferences.getString("Parts_list", null)

        if(partsList.isEmpty()) {
            try {
                val jsonAry = JSONArray(tempPartsList)

                for(i: Int in 0 until jsonAry.length())
                    partsList.add(jsonAry.optString(i))

            }catch(jsonE: JSONException) {
                jsonE.stackTrace
            }
        }
    }

    //CalendarView 일정 표시
    private fun getRecordList() {
        lifecycle.coroutineScope.launch {
            recordListViewModel.allDate().collect {
                val datePartMap = setOrganizeByDate(it)
                val frequencyMap = getMaxIndex(datePartMap)

                val dateSet = HashSet<CalendarDay>()

                for(date in frequencyMap) {
                    val tempDate = date.key.split("-")
                    dateSet.add(CalendarDay.from(tempDate[0].toInt(), tempDate[1].toInt(), tempDate[2].toInt()))
                }

                binding.calendarCalendar.addDecorator(CalendarDecorator(0, dateSet))
            }
        }
    }

    //Key 저장된 날짜에서 가장 많이 한 운동 부위의 위치를 반환
    private fun getMaxIndex(datePartMap: HashMap<String, ArrayList<String>>): HashMap<String, Int> {
        val result = HashMap<String, Int>()

        for(target in datePartMap) {
            val frequencyList = ArrayList<Int>()

            for(part in partsList)
                frequencyList.add(Collections.frequency(target.value, part))

            result[target.key] = frequencyList.indexOf(Collections.max(frequencyList))
        }

        return result
    }

    //날짜, 부위 쌍의 기록들을 같은 날짜끼리 묶어서 반환
    private fun setOrganizeByDate(target: List<DatePart>): HashMap<String, ArrayList<String>> {
        val result = HashMap<String, ArrayList<String>>()

        for(one in target) {
            var onlyPartsList = ArrayList<String>()

            if(result.containsKey(one.date)) {
                onlyPartsList = result[one.date]!!
                onlyPartsList.add(one.part)
            }else
                onlyPartsList.add(one.part)

            result[one.date] = onlyPartsList
        }

        return result
    }

    //CalendarView 날짜 클릭 Listener
    private fun initCalendar() {
        binding.calendarCalendar.setTitleFormatter(MonthArrayTitleFormatter(resources.getTextArray(R.array.calendar_months)))
        binding.calendarCalendar.setTitleFormatter(TitleFormatter {
            return@TitleFormatter "${it.year} ${utils.intFullFormat(it.month, 2)}"
        })
        binding.calendarCalendar.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.calendar_days_of_week)))

        binding.calendarCalendar.setOnDateChangedListener { _, date, _ ->
            Log.i(TAG, "${date.date}")
        }
    }

    //Fragment ViewBinding 삭제
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}