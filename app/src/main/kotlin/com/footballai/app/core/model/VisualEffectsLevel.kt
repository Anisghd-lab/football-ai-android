package com.footballai.app.core.model

/**
 * Visual effects level configurable by user to balance aesthetics and hardware performance.
 */
enum class VisualEffectsLevel(val displayName: String, val description: String) {
    LIQUID(
        displayName = "Liquid Glass",
        description = "Translucidité avancée, reflets directionnels et plaques stabilisatrices"
    ),
    SOFT_GLASS(
        displayName = "Soft Glass",
        description = "Translucidité réduite sans dégradés complexes, économe en batterie"
    ),
    SOLID(
        displayName = "Solid (Opaque)",
        description = "Surfaces opaques haute lisibilité, zéro calcul de transparence"
    )
}
