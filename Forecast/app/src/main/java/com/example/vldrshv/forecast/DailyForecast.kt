package com.example.vldrshv.forecast

import com.google.gson.annotations.SerializedName

class DailyForecast {
    @SerializedName("DailyForecasts")
    var forecast: List<Weather> = listOf()

    override fun toString(): String {
        return "DailyForecast(forecast=$forecast)"
    }
}
class Weather{
    @SerializedName("Date")
    var date: String = ""
    @SerializedName("Sun")
    var sun: SpaceObj = SpaceObj()
    @SerializedName("Moon")
    var moon: SpaceObj = SpaceObj()
    @SerializedName("Temperature")
    var temperature: Temperature = Temperature()
    @SerializedName("RealFeelTemperature")
    var realFeelTemperature: Temperature = Temperature()
    @SerializedName("RealFeelTemperatureShade")
    var realFeelTemperatureShade: Temperature = Temperature()
    @SerializedName("HoursOfSun")
    var hoursOfSun: Float? = 0f

    @SerializedName("AirAndPollen")
    var airAndPollen: List<AirAndPollen> = listOf(AirAndPollen())
    @SerializedName("Day")
    var day: ExternalInfo = ExternalInfo()
    @SerializedName("Night")
    var night: ExternalInfo = ExternalInfo()
}

class SpaceObj {
    @SerializedName("Rise")
    var riseTime: String = ""
    @SerializedName("Set")
    var setTime: String = ""
}
class Temperature {
    @SerializedName("Minimum")
    var minimum: Value = Value()
    @SerializedName("Maximum")
    var maximum: Value = Value()
}
class Value {
    @SerializedName("Value")
    var value: Float = 0f
    @SerializedName("Unit")
    var unit: String = ""
    @SerializedName("UnitType")
    var unitType: Int = 0
}
class AirAndPollen {
    @SerializedName("Name")
    var name: String = ""
    @SerializedName("Value")
    var value: Int = 0
    @SerializedName("Category")
    var category: String = ""
    @SerializedName("CategoryValue")
    var categoryValue: Int = 0
    @SerializedName("Type")
    var type: String = ""
}
class ExternalInfo {
    @SerializedName("Icon")
    var iconId: Int = 0
    @SerializedName("IconPhrase")
    var iconPhrase: String = ""
    @SerializedName("ShortPhrase")
    var shortPhrase: String = ""
    @SerializedName("PrecipitationProbability")
    var precipitationPropability: Int = 0
    @SerializedName("ThunderstormProbability")
    var thunderstormProbability: Int = 0
    @SerializedName("RainProbability")
    var rainProbability: Int = 0
    @SerializedName("SnowProbability")
    var snowProbability: Int = 0
    @SerializedName("IceProbability")
    var iceProbability: Int = 0
    @SerializedName("Wind")
    var wind: Wind = Wind()
    @SerializedName("WindGust")
    var windGust: Wind = Wind()
    @SerializedName("TotalLiquid")
    var totalLiquid: Value = Value()
    @SerializedName("Rain")
    var rain: Value = Value()
    @SerializedName("Snow")
    var snow: Value = Value()
    @SerializedName("Ice")
    var ice: Value = Value()
}
class Wind {
    @SerializedName("Speed")
    var speed: Value = Value()
    @SerializedName("Direction")
    var direction: Direction = Direction()
}
class Direction {
    @SerializedName("Value")
    var value: Int = 0
    @SerializedName("Unit")
    var unit: String = ""
    @SerializedName("UnitType")
    var unitType: Int = 0
}