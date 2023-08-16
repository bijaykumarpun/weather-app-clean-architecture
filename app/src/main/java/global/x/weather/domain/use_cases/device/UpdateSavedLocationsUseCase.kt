package global.x.weather.domain.use_cases.device

import global.x.weather.domain.models.FavoriteLocationModel
import javax.inject.Inject

class UpdateSavedLocationsUseCase @Inject constructor(private val deviceRepository: DeviceRepository) {
    operator fun invoke(favoriteLocations: List<FavoriteLocationModel>) {
        return deviceRepository.updateSavedLocations(favoriteLocations.map { it.toSavedLocationModel() })
    }
}