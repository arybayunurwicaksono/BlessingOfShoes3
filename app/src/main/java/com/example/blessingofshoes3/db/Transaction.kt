package com.example.blessingofshoes3.db

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "transaction")
data class Transaction(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idTransaction")
    var idTransaction: Int = 0,

    @ColumnInfo(name = "totalTransaction")
    var totalTransaction: Int? = null,

    @ColumnInfo(name = "profitTransaction")
    var profitTransaction: Int? = null,

    @ColumnInfo(name = "moneyReceived")
    var moneyReceived: Int? = null,

    @ColumnInfo(name = "moneyChange")
    var moneyChange: Int? = null,

    @ColumnInfo(name = "totalItem")
    var totalItem: Int? = null,

    @ColumnInfo(name = "username")
    var username: String? = null,

    @ColumnInfo(name = "typePayment")
    var typePayment: String? = null,

    @ColumnInfo(name = "transactionDate")
    var transactionDate: String? = null,

    @ColumnInfo(name = "proofPhoto")
    val proofPhoto: Bitmap,

    @ColumnInfo(name = "transactionType")
    val transactionType: String? = null

) : Parcelable