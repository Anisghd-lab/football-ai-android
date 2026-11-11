package com.footballai.app.data.mapper

import com.footballai.app.core.model.CouncilRoleType
import com.footballai.app.core.model.ProbabilityStatus
import com.footballai.app.core.network.dto.AgentRoleDto
import com.footballai.app.core.network.dto.PredictionResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CouncilQuarantineTest {

    @Test
    fun testUniformPlaceholderQuarantined() {
        val uniformDto = PredictionResponseDto(
            matchId = "test_match",
            homeWinProb = 0.333,
            drawProb = 0.334,
            awayWinProb = 0.333
        )

        val status = AgentMapper.evaluateProbabilityStatus(uniformDto)
        assertEquals(ProbabilityStatus.NON_PREDICTIVE_PLACEHOLDER, status)
    }

    @Test
    fun testExplicitNonPredictivePlaceholderQuarantined() {
        val explicitDto = PredictionResponseDto(
            matchId = "test_match",
            probabilityStatus = "NON_PREDICTIVE_PLACEHOLDER",
            homeWinProb = 0.50,
            drawProb = 0.30,
            awayWinProb = 0.20
        )

        val status = AgentMapper.evaluateProbabilityStatus(explicitDto)
        assertEquals(ProbabilityStatus.NON_PREDICTIVE_PLACEHOLDER, status)
    }

    @Test
    fun testRealCalibratedEstimateRecognized() {
        val realDto = PredictionResponseDto(
            matchId = "test_match",
            homeWinProb = 0.65,
            drawProb = 0.22,
            awayWinProb = 0.13,
            calibratedHomeProb = 0.62,
            calibratedDrawProb = 0.23,
            calibratedAwayProb = 0.15
        )

        val status = AgentMapper.evaluateProbabilityStatus(realDto)
        assertEquals(ProbabilityStatus.REAL_ESTIMATE, status)
    }

    @Test
    fun testMissingProbabilityReturnsDataInsufficient() {
        val missingDto = PredictionResponseDto(
            matchId = "test_match",
            homeWinProb = null,
            drawProb = null,
            awayWinProb = null
        )

        val status = AgentMapper.evaluateProbabilityStatus(missingDto)
        assertEquals(ProbabilityStatus.DATA_INSUFFICIENT, status)
    }

    @Test
    fun testSixAgentRolesMappedCorrectly() {
        val agentDtos = listOf(
            AgentRoleDto(role = "TACTICAL", name = "Tactical Analyst"),
            AgentRoleDto(role = "STATISTICAL", name = "Statistical Modeler"),
            AgentRoleDto(role = "NEWS_CONTEXT", name = "Context Evaluator"),
            AgentRoleDto(role = "INDEPENDENT", name = "Independent Auditor"),
            AgentRoleDto(role = "CONTRARIAN", name = "Contrarian Challenger"),
            AgentRoleDto(role = "SYNTHESIZER", name = "Master Synthesizer")
        )

        val domainRoles = AgentMapper.mapRoleListToDomain(agentDtos)
        assertEquals(6, domainRoles.size)

        assertEquals(CouncilRoleType.TACTICAL, domainRoles[0].roleType)
        assertFalse(domainRoles[0].isPrimarySynthesizer)

        assertEquals(CouncilRoleType.SYNTHESIZER, domainRoles[5].roleType)
        assertTrue(domainRoles[5].isPrimarySynthesizer)
    }
}
