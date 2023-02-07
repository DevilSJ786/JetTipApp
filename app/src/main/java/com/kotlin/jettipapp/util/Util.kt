package com.kotlin.jettipapp.util

fun calculateTotalTip(totalBill: Double, tipPercentage: Int): Double {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty()) (totalBill * tipPercentage) / 100
    else 0.0
}

fun calculateTotalBill(tBill: Double, tPercentage: Int,splitBy:Int): Double{
    val bill= calculateTotalTip(tBill, tPercentage )+tBill
    return (bill/splitBy)
}