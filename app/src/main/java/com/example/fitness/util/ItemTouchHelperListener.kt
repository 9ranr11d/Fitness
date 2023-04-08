package com.example.fitness.util

interface ItemTouchHelperListener {
    fun onItemMove(from: Int, to: Int): Boolean
    fun onItemSwipe(position: Int, direction: Int)
}