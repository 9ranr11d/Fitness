package com.example.fitness

import androidx.room.ColumnInfo

data class DatePart(
    @ColumnInfo val date: String,
    @ColumnInfo val part: String
) {
    override fun toString(): String {
        val result = StringBuilder()
        result
            .append(date)
            .append(" ")
            .append(part)

        return result.toString()
    }
}
