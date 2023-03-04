package com.example.blessingofshoes3.db

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "services")
data class Services(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idServices")
    var idServices: Int = 0,

    @ColumnInfo(name = "serviceName")
    var serviceName: String? = null,

    @ColumnInfo(name = "serviceMaterialPrice")
    var serviceMaterialPrice: Int? = null,

    @ColumnInfo(name = "serviceFinalPrice")
    var serviceFinalPrice: Int? = null,

    @ColumnInfo(name = "serviceProfit")
    var serviceProfit: Int? = null,

    @ColumnInfo(name = "estimatedTime")
    var estimatedTime: Int? = null,

    @ColumnInfo(name = "username")
    var username: String? = null,

    @ColumnInfo(name = "timeAdded")
    var timeAdded: String? = null,

) : Parcelable