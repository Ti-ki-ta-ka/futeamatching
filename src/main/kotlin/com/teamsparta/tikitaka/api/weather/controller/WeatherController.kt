package com.teamsparta.tikitaka.api.weather.controller

import com.teamsparta.tikitaka.api.weather.dto.WeatherData
import com.teamsparta.tikitaka.api.weather.service.WeatherService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v3/weather")
class WeatherController(
    private val weatherService: WeatherService
) {

    @GetMapping("/current")
    fun getCurrentWeather(
        @RequestParam("nx") nx: Int,
        @RequestParam("ny") ny: Int
    ): Mono<WeatherData> {
        return weatherService.searchWeather(nx, ny)
    }
}