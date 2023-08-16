package global.x.weather.domain.models

import global.x.weather.infrastructure.util.StringUtil

data class SearchResultModel(
    val id: Int,
    val name: String,
    val region: String,
    val country: String,
) {
    fun getDisplayName( delimiter: String): String {
        return StringUtil.getDisplayName(name, region, country, delimiter)
    }
}