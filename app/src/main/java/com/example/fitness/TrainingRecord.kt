package com.example.fitness

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrainingRecord(
    @PrimaryKey(autoGenerate = true) val seq: Int,
    @ColumnInfo val date: String,
    @ColumnInfo val part: String,
    @ColumnInfo val name: String,
    @ColumnInfo val set: Int,
    @ColumnInfo val rep: Int,
    @ColumnInfo val wt: Int,
    @ColumnInfo val breakT: Int
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(seq)
        parcel.writeString(date)
        parcel.writeString(part)
        parcel.writeString(name)
        parcel.writeInt(set)
        parcel.writeInt(rep)
        parcel.writeInt(wt)
        parcel.writeInt(breakT)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrainingRecord> {
        override fun createFromParcel(parcel: Parcel): TrainingRecord {
            return TrainingRecord(parcel)
        }

        override fun newArray(size: Int): Array<TrainingRecord?> {
            return arrayOfNulls(size)
        }
    }
}
