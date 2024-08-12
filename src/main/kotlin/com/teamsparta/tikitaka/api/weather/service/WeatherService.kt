package com.teamsparta.tikitaka.api.weather.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.teamsparta.tikitaka.api.weather.dto.ForecastValue
import com.teamsparta.tikitaka.api.weather.dto.WeatherData
import com.teamsparta.tikitaka.api.weather.dto.WeatherResponse
import com.teamsparta.tikitaka.api.weather.dto.WeatherResponseContainer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.DefaultUriBuilderFactory
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class WeatherService(private val webBuilder: WebClient.Builder) {

    @Value("\${kma.base_url}")
    lateinit var baseUrl: String

    @Value("\${kma.service_key}")
    lateinit var serviceKey: String

    private val objectMapper = ObjectMapper()

    fun searchWeather(nx: Int, ny: Int): Mono<WeatherData> {
        val factory = DefaultUriBuilderFactory(baseUrl).apply {
            encodingMode = DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY
        }

        val webClient = webBuilder
            .uriBuilderFactory(factory)
            .baseUrl(baseUrl)
            .build()

        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/getUltraSrtFcst")
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("pageNo", "1")
                    .queryParam("numOfRows", "1000")
                    .queryParam("dataType", "JSON")
                    .queryParam("base_date", getBaseDate())
                    .queryParam("base_time", getBaseTime(LocalTime.now()))
                    .queryParam("nx", nx)
                    .queryParam("ny", ny)
                    .build()
            }
            .header("Content-type", "application/json")
            .retrieve()
            .bodyToMono(String::class.java)
            .map { objectMapper.readValue(it, WeatherResponseContainer::class.java) }
            .map { extractRelevantData(it.response) }
    }

    private fun extractRelevantData(response: WeatherResponse?): WeatherData {
        val temperature = mutableListOf<ForecastValue>()
        val skyCondition = mutableListOf<ForecastValue>()
        val humidity = mutableListOf<ForecastValue>()
        val precipitationType = mutableListOf<ForecastValue>()

        response?.body?.items?.item?.forEach { item ->
            val forecastValue = ForecastValue(
                date = item.fcstDate ?: "",
                time = item.fcstTime ?: "",
                value = item.fcstValue ?: ""
            )

            when (item.category) {
                "T1H" -> temperature.add(forecastValue)
                "SKY" -> skyCondition.add(forecastValue)
                "REH" -> humidity.add(forecastValue)
                "PTY" -> precipitationType.add(forecastValue)
            }
        }

        return WeatherData(
            temperature = temperature,
            skyCondition = skyCondition,
            humidity = humidity,
            precipitationType = precipitationType
        )
    }

    private fun getBaseDate(): String {
        val curTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return curTime.format(formatter)
    }

    private fun getBaseTime(curTime: LocalTime): String {
        val hours = curTime.hour
        val minutes = curTime.minute

        val convertedHour: String = if (minutes < 30) {
            (hours - 1).toString().padStart(2, '0') + "00"
        } else {
            hours.toString().padStart(2, '0') + "00"
        }

        return when (convertedHour) {
            "0200", "0300", "0400" -> "0200"
            "0500", "0600", "0700" -> "0500"
            "0800", "0900", "1000" -> "0800"
            "1100", "1200", "1300" -> "1100"
            "1400", "1500", "1600" -> "1400"
            "1700", "1800", "1900" -> "1700"
            "2000", "2100", "2200" -> "2000"
            "2300", "2400", "0000", "0100" -> "2300"
            else -> "0000"
        }
    }
}
