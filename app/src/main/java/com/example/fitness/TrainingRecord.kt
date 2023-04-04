package com.example.fitness

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrainingRecord(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val date: String,
    @ColumnInfo val time: String,
    @ColumnInfo val part: String,
    @ColumnInfo val name: String,
    @ColumnInfo val set: Int,
    @ColumnInfo val rep: String,
    @ColumnInfo val wt: String,
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
