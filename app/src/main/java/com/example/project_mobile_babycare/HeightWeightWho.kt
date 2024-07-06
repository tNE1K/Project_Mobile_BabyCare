package com.example.project_mobile_babycare

import android.os.Parcel
import android.os.Parcelable

data class HeightWeightWho(
    val months: Int,
    val height: Double,
    val weight: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(months)
        parcel.writeDouble(height)
        parcel.writeDouble(weight)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HeightWeightWho> {
        override fun createFromParcel(parcel: Parcel): HeightWeightWho {
            return HeightWeightWho(parcel)
        }

        override fun newArray(size: Int): Array<HeightWeightWho?> {
            return arrayOfNulls(size)
        }
    }
}
