package global.x.weather.data.source.models

import com.google.gson.annotations.SerializedName

data class LocationApiModel(
    val name:String,
    val region: String,
    val country: String,
    @SerializedName("localtime")
    val localTime: String,
    @SerializedName("localtime_epoch")
    val localTimeEpoch: Long
)