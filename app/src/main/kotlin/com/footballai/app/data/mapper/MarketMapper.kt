package com.footballai.app.data.mapper

import com.footballai.app.core.model.MarketCalibrationMetric
import com.footballai.app.core.model.MarketCategoriesInfo
import com.footballai.app.core.network.dto.MarketCalibrationItemDto
import com.footballai.app.core.network.dto.MarketCategoriesDto

object MarketMapper {

    fun mapCategoriesToDomain(dto: MarketCategoriesDto?): MarketCategoriesInfo {
        if (dto == null) {
            return MarketCategoriesInfo(
                categories = emptyList(),
                totalCategories = 0,
                primaryMarkets = emptyList()
            )
        }

        return MarketCategoriesInfo(
            categories = dto.categories ?: emptyList(),
            totalCategories = dto.totalCategories ?: dto.categories?.size ?: 0,
            primaryMarkets = dto.primaryMarkets ?: emptyList()
        )
    }

    fun mapCalibrationListToDomain(dtos: List<MarketCalibrationItemDto>?): List<MarketCalibrationMetric> {
        return dtos?.map { dto ->
            MarketCalibrationMetric(
                marketType = dto.marketType ?: "Inconnu",
                sampleCount = dto.sampleCount ?: 0,
                brierScore = dto.brierScore ?: 0.0,
                logLoss = dto.logLoss ?: 0.0,
                expectedCalibrationError = dto.expectedCalibrationError ?: 0.0,
                optimalTemperature = dto.optimalTemperature ?: 1.0
            )
        } ?: emptyList()
    }
}
