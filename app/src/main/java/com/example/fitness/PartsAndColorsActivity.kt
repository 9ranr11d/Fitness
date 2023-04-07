package com.example.fitness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitness.databinding.ActivityPartsAndColorsBinding
import com.example.fitness.databinding.DialogEditBinding

class PartsAndColorsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityPartsAndColorsBinding
    private lateinit var editBinding: DialogEditBinding
    private val utils = Utils()

    private lateinit var partsAndColorsListAdapter: PartsAndColorsListAdapter

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
        binding.btnPartsAndColorsSave.setOnClickListener(this)
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
                    ?.setMessage("${receiveList[1]} - ${receiveList[2]} \n해당 부위를 삭제하는 것이 맞습니까?")
                    ?.setPositiveButton("삭제") { _, _ ->
                        partsAndColorsListAdapter.setDelete(receiveList[3].toInt())
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
                    ?.setPositiveButton("수정") { _, _ ->
                        partsAndColorsListAdapter.setUpdate(
                            PartColor(
                                editBinding.editDEditPart.text.toString(),
                                editBinding.editDEditColor.text.toString()
                            ),
                            receiveList[3].toInt()
                        )

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
        editBinding.editDEditPart.setText(partColor.part)
        editBinding.editDEditColor.setText(partColor.color)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_parts_and_colors_back -> {
                setResult(RESULT_CANCELED)
                finish()
            }
            R.id.btn_parts_and_colors_add -> partsAndColorsListAdapter.setInsert(PartColor("X", "X"))
            R.id.btn_parts_and_colors_save -> {
                val partsList = ArrayList<String>()
                val colorsList = ArrayList<String>()

                partsAndColorsListAdapter.getAll().map {
                    partsList.add(it.part)
                    colorsList.add(it.color)
                }

                utils.setPrefList(this, "Parts_list", partsList)
                utils.setPrefList(this, "Colors_list", colorsList)
            }
        }
    }
}