package com.example.fitness.util

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.view.adapter.PartsAndColorsListAdapter

class ItemTouchCallback(private val listener: ItemTouchHelperListener): ItemTouchHelper.Callback() {
    private var isEdit = false
    //화면 drag, swipe 감지 후 반환
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

        return makeMovementFlags(dragFlags, swipeFlags)
    }

    //Drag
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return listener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
    }

    //Swipe
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onItemSwipe(viewHolder.adapterPosition, direction)
    }

    //Swipe 시 뒷 배경 설정
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            isEdit = dX > 0
            val view =
                if(isEdit)
                    (viewHolder as PartsAndColorsListAdapter.ViewHolder).editLay
                else
                    (viewHolder as PartsAndColorsListAdapter.ViewHolder).delLay

            getDefaultUIUtil().onDraw(c, recyclerView, view, dX, dY, actionState, isCurrentlyActive)
        }
    }

    //Swipe 끝나고 원상복구
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        if(isEdit)
            getDefaultUIUtil().clearView((viewHolder as PartsAndColorsListAdapter.ViewHolder).editLay)
        else
            getDefaultUIUtil().clearView((viewHolder as PartsAndColorsListAdapter.ViewHolder).delLay)
    }
}