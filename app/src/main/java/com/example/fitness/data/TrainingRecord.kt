package com.example.fitness.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrainingRecord(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo var date: String,
    @ColumnInfo var time: String,
    @ColumnInfo var part: String,
    @ColumnInfo var name: String,
    @ColumnInfo var set: Int,
    @ColumnInfo var rep: String,
    @ColumnInfo var wt: String,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(part)
        parcel.writeString(name)
        parcel.writeInt(set)
        parcel.writeString(rep)
        parcel.writeString(wt)
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
