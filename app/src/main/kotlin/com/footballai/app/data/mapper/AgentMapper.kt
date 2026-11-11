package com.footballai.app.data.mapper

import com.footballai.app.core.model.AgentRole
import com.footballai.app.core.model.AiProvider
import com.footballai.app.core.model.CouncilRoleType
import com.footballai.app.core.model.ProbabilityStatus
import com.footballai.app.core.network.dto.AgentRoleDto
import com.footballai.app.core.network.dto.PredictionResponseDto
import com.footballai.app.core.network.dto.ProviderDto
import kotlin.math.abs

object AgentMapper {

    fun mapRoleToDomain(dto: AgentRoleDto): AgentRole {
        val roleType = when (dto.role?.uppercase()?.trim()) {
            "TACTICAL" -> CouncilRoleType.TACTICAL
            "STATISTICAL" -> CouncilRoleType.STATISTICAL
            "NEWS_CONTEXT" -> CouncilRoleType.NEWS_CONTEXT
            "INDEPENDENT" -> CouncilRoleType.INDEPENDENT
            "CONTRARIAN" -> CouncilRoleType.CONTRARIAN
            "SYNTHESIZER" -> CouncilRoleType.SYNTHESIZER
            else -> CouncilRoleType.UNKNOWN
        }

        return AgentRole(
            roleType = roleType,
            name = dto.name ?: dto.role ?: "Analyste IA",
            description = dto.description ?: "Rôle analytique spécialisé au sein du Conseil IA",
            isPrimarySynthesizer = roleType == CouncilRoleType.SYNTHESIZER
        )
    }

    fun mapRoleListToDomain(dtos: List<AgentRoleDto>?): List<AgentRole> {
        return dtos?.map { mapRoleToDomain(it) } ?: emptyList()
    }

    fun mapProviderListToDomain(dtos: List<ProviderDto>?): List<AiProvider> {
        return dtos?.map { dto ->
            AiProvider(
                name = dto.providerName ?: "Fournisseur Inconnu",
                isActive = dto.isActive == true,
                apiBase = dto.apiBase ?: "N/A"
            )
        } ?: emptyList()
    }

    /**
     * Enforce strict adversarial quarantine:
     * Uniform probabilities around 0.333/0.334/0.333 or explicit NON_PREDICTIVE_PLACEHOLDER status
     * must never be considered valid predictive estimates.
     */
    fun evaluateProbabilityStatus(dto: PredictionResponseDto?): ProbabilityStatus {
        if (dto == null) return ProbabilityStatus.UNKNOWN
        if (dto.probabilityStatus == "NON_PREDICTIVE_PLACEHOLDER") {
            return ProbabilityStatus.NON_PREDICTIVE_PLACEHOLDER
        }

        val h = dto.calibratedHomeProb ?: dto.homeWinProb
        val d = dto.calibratedDrawProb ?: dto.drawProb
        val a = dto.calibratedAwayProb ?: dto.awayWinProb

        if (h == null || d == null || a == null) {
            return ProbabilityStatus.DATA_INSUFFICIENT
        }

        // Check if probabilities are technical uniform placeholders (e.g. 0.333, 0.334, 0.333)
        val isUniform = abs(h - 0.3333) < 0.005 && abs(d - 0.3333) < 0.005 && abs(a - 0.3333) < 0.005
        return if (isUniform) {
            ProbabilityStatus.NON_PREDICTIVE_PLACEHOLDER
        } else {
            ProbabilityStatus.REAL_ESTIMATE
        }
    }
}
