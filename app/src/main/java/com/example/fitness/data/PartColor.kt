package com.example.fitness.data

data class PartColor(val part: String, val color: String) {
    override fun toString(): String {
        val result = StringBuilder()
        result
            .append(part)
            .append("_")
            .append(color)

        return result.toString()
    }
}
