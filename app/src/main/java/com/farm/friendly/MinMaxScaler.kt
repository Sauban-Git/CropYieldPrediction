package com.farm.friendly

class MinMaxScaler (private val min: Double, private val max: Double) {
    fun scale(value: Double, featureMin: Double,featureMax: Double):Double {
        return (value - featureMin) / (featureMax - featureMin) * (max - min) + min
    }
}