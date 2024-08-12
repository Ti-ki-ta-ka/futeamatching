package com.teamsparta.tikitaka.api.weather.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class WeatherResponseContainer @JsonCreator constructor(
    @JsonProperty("response") val response: WeatherResponse?
)

data class WeatherResponse @JsonCreator constructor(
    @JsonProperty("header") val header: Header?,
    @JsonProperty("body") val body: Body?
)

data class Header @JsonCreator constructor(
    @JsonProperty("resultCode") val resultCode: String?,
    @JsonProperty("resultMsg") val resultMsg: String?
)

data class Body @JsonCreator constructor(
    @JsonProperty("dataType") val dataType: String?,
    @JsonProperty("items") val items: Items?,
    @JsonProperty("numOfRows") val numOfRows: Int?,
    @JsonProperty("pageNo") val pageNo: Int?,
    @JsonProperty("totalCount") val totalCount: Int?
)

data class Items @JsonCreator constructor(
    @JsonProperty("item") val item: List<Item>?
)

data class Item @JsonCreator constructor(
    @JsonProperty("baseDate") val baseDate: String?,
    @JsonProperty("baseTime") val baseTime: String?,
    @JsonProperty("category") val category: String?,
    @JsonProperty("fcstDate") val fcstDate: String?,
    @JsonProperty("fcstTime") val fcstTime: String?,
    @JsonProperty("fcstValue") val fcstValue: String?,
    @JsonProperty("nx") val nx: Int?,
    @JsonProperty("ny") val ny: Int?
)

data class WeatherData(
    val temperature: List<ForecastValue>,
    val skyCondition: List<ForecastValue>,
    val humidity: List<ForecastValue>,
    val precipitationType: List<ForecastValue>
)

data class ForecastValue(
    val date: String,
    val time: String,
    val value: String
)
