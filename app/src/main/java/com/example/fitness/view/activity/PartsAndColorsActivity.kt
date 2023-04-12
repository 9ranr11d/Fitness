package com.example.fitness.view.activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitness.*
import com.example.fitness.data.PartColor
import com.example.fitness.view.model.RecordListApplication
import com.example.fitness.view.model.RecordListViewModel
import com.example.fitness.view.model.RecordListViewModelFactory
import com.example.fitness.databinding.ActivityPartsAndColorsBinding
import com.example.fitness.databinding.DialogEditBinding
import com.example.fitness.util.ItemTouchCallback
import com.example.fitness.util.Utils
import com.example.fitness.view.adapter.PartsAndColorsListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PartsAndColorsActivity : AppCompatActivity(), View.OnClickListener {
//    private val TAG = javaClass.simpleName
    private lateinit var binding: ActivityPartsAndColorsBinding
    private lateinit var editBinding: DialogEditBinding
    private val utils = Utils()

    private lateinit var partsAndColorsListAdapter: PartsAndColorsListAdapter

    private val recordListViewModel: RecordListViewModel by viewModels {
        RecordListViewModelFactory(
            (application as RecordListApplication).dataBase.TrainingRecordDAO()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivityPartsAndColorsBinding.inflate(layoutInflater)
        editBinding = DialogEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()
        setBtnListener()
    }

    //Btn Listener
    private fun setBtnListener() {
        binding.btnPartsAndColorsBack.setOnClickListener(this)
        binding.btnPartsAndColorsAdd.setOnClickListener(this)
    }

    //RecyclerView 설정
    private fun initRecycler() {
        binding.recyclerPartsAndColors.layoutManager = LinearLayoutManager(this)

        val partsColorsList = ArrayList<PartColor>(
            utils.getPrefList(this, "Parts_list")
                .zip(utils.getPrefList(this, "Colors_list"))
                .map { PartColor(it.first, it.second) }
        )

        partsAndColorsListAdapter = PartsAndColorsListAdapter(partsColorsList) {
            val receiveList = it.split("_")

            if(receiveList[0].toInt() == ItemTouchHelper.LEFT) {
                val delDialog = utils.initDialog(this, "삭제")
                    ?.setMessage("${receiveList[1]} - ${receiveList[2]} \n해당 부위를 포함한 모든 기록도 같이 삭제됩니다.")
                    ?.setPositiveButton("삭제") { dialog, _ ->
                        dialog.dismiss()
                        partsAndColorsListAdapter.setDelete(receiveList[3].toInt())

                        setDelPart(receiveList[1])
                    }
                    ?.setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                        partsAndColorsListAdapter.notifyItemChanged(receiveList[3].toInt())
                        utils.makeToast(this, "취소되었습니다.")
                    }
                    ?.create()

                delDialog?.show()
            }else {
                val editLay = editBinding.root
                setEditLay(PartColor(receiveList[1], receiveList[2]))

                val editDialog = utils.initDialog(this, "수정")
                    ?.setView(editLay)
                    ?.setPositiveButton("수정") { dialog, _ ->
                        dialog.dismiss()
                        partsAndColorsListAdapter.setUpdate(
                            PartColor(
                                editBinding.editDEditPart.text.toString(),
                                editBinding.textDEditColor.text.toString()
                            ),
                            receiveList[3].toInt()
                        )

                        setUpdatePart(receiveList[1], editBinding.editDEditPart.text.toString())

                        if (editLay.parent != null)
                            (editLay.parent as ViewGroup).removeView(editLay)
                    }
                    ?.setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                        partsAndColorsListAdapter.notifyItemChanged(receiveList[3].toInt())
                        utils.makeToast(this, "취소되었습니다.")

                        if (editLay.parent != null)
                            (editLay.parent as ViewGroup).removeView(editLay)
                    }
                    ?.create()

                editDialog?.show()
            }
        }

        binding.recyclerPartsAndColors.adapter = partsAndColorsListAdapter
        binding.recyclerPartsAndColors.addItemDecoration(DividerItemDecoration(this, 1))

        val itemTouchHelper = ItemTouchHelper(ItemTouchCallback(partsAndColorsListAdapter))
        itemTouchHelper.attachToRecyclerView(binding.recyclerPartsAndColors)
    }

    //Dialog edit Layout
    private fun setEditLay(partColor: PartColor) {
        //초기 설정
        var isEdit = true
        editBinding.textDEditColor.visibility = View.VISIBLE
        editBinding.editDEditColor.visibility = View.INVISIBLE
        editBinding.btnDEditColorChange.background = ContextCompat.getDrawable(this, R.drawable.img_edit)
        editBinding.editDEditPart.setText(partColor.part)
        editBinding.textDEditColor.text = partColor.color

        //Edit 버튼
        editBinding.btnDEditColorChange.setOnClickListener {
            if(isEdit) {
                it.background = ContextCompat.getDrawable(this, R.drawable.img_check)
                setLayEnabled(true)

                editBinding.textDEditColor.visibility = View.INVISIBLE
                editBinding.editDEditColor.visibility = View.VISIBLE
                editBinding.editDEditColor.setText("${editBinding.textDEditColor.text}")

                //포커스
                editBinding.editDEditColor.requestFocus()
                editBinding.editDEditColor.setSelection(editBinding.editDEditColor.length())

                isEdit = false
            }else {
                setKeyBordHide(editBinding.editDEditColor.windowToken)
                setLayEnabled(false)

                it.background = ContextCompat.getDrawable(this, R.drawable.img_edit)

                if("${editBinding.editDEditColor.text}".isNotEmpty())
                    setColorPicker("${editBinding.editDEditColor.text}", "${editBinding.textDEditColor.text}")

                editBinding.editDEditColor.visibility = View.INVISIBLE
                editBinding.textDEditColor.visibility = View.VISIBLE
                editBinding.textDEditColor.text = "${editBinding.editDEditColor.text}"

                isEdit = true
            }
        }

        initColorPicker(partColor.color)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setLayEnabled(isEnabled: Boolean) {
        for(i: Int in 0 until editBinding.linearDEditPicker.childCount)
            editBinding.linearDEditPicker.getChildAt(i).setOnTouchListener { _, _ -> isEnabled }
    }

    private fun setKeyBordHide(windowToken: IBinder) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun setColorPicker(color: String, reset: String) {
        try {
            editBinding.cPickerDEdit.color = color.toLong(16).toInt()
        }catch(numE: java.lang.NumberFormatException) {
            editBinding.editDEditColor.setText(reset)
            utils.makeToast(this, "잘못된 형식입니다.")
        }
    }

    private fun initColorPicker(color: String) {
        editBinding.cPickerDEdit.addSVBar(editBinding.svbarDEdit)
        editBinding.cPickerDEdit.addOpacityBar(editBinding.opacityDEdit)
        editBinding.cPickerDEdit.addSaturationBar(editBinding.saturationDEdit)
        editBinding.cPickerDEdit.addValueBar(editBinding.valueDEdit)

        editBinding.cPickerDEdit.color = color.toLong(16).toInt()
        editBinding.cPickerDEdit.oldCenterColor = color.toLong(16).toInt()
        editBinding.cPickerDEdit.setOnColorChangedListener {
            editBinding.textDEditColor.text = Integer.toHexString(it)
        }
    }

    //해당 부위에 대한 모든 기록 삭제
    private fun setDelPart(part: String) {
        CoroutineScope(Dispatchers.IO).launch {
            recordListViewModel.delPart(part)
        }
    }

    //해당 부위의 바뀐 이름을 모든 기록에 반영
    private fun setUpdatePart(fromPart: String, toPart: String) {
        CoroutineScope(Dispatchers.IO).launch {
            recordListViewModel.updatePart(fromPart, toPart)
        }
    }

    //PartColor 객체를 나누어서 sharedPreferences 저장
    private fun setPrefList() {
        val partsList = ArrayList<String>()
        val colorsList = ArrayList<String>()

        partsAndColorsListAdapter.getAll().map {
            partsList.add(it.part)
            colorsList.add(it.color)
        }

        utils.setPrefList(this, "Parts_list", partsList)
        utils.setPrefList(this, "Colors_list", colorsList)
    }

    //부위 순서 변경 반영
    override fun onStop() {
        super.onStop()
        setPrefList()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_parts_and_colors_back -> {
                setResult(RESULT_CANCELED)
                finish()
            }
            R.id.btn_parts_and_colors_add -> {
                val insertLay = editBinding.root
                setEditLay(PartColor("", "ff000000"))

                val insertDialog = utils.initDialog(this, "추가")
                    ?.setView(insertLay)
                    ?.setPositiveButton("추가") { dialog, _ ->
                        dialog.dismiss()
                        partsAndColorsListAdapter.setInsert(
                            PartColor("${editBinding.editDEditPart.text}", "${editBinding.editDEditColor.text}")
                        )

                        if (insertLay.parent != null)
                            (insertLay.parent as ViewGroup).removeView(insertLay)
                    }
                    ?.setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                        utils.makeToast(this, "취소되었습니다.")

                        if (insertLay.parent != null)
                            (insertLay.parent as ViewGroup).removeView(insertLay)
                    }
                    ?.create()

                insertDialog?.show()
            }
        }
    }
}