package com.example.data.model

import androidx.annotation.DrawableRes

enum class TutorialCategory(val displayName: String, val iconName: String) {
    ALL("Todas", "grid_view"),
    HOUSES("Casas", "home"),
    FARMS("Fazendas", "eco"),
    CASTLES("Castelos & Torres", "fort"),
    SURVIVAL("Sobrevivência", "shield"),
    DECORATION("Decoração", "palette"),
    NETHER("Nether & End", "whatshot")
}

enum class BuildDifficulty(val displayName: String, val colorHex: Long) {
    BEGINNER("Iniciante", 0xFF4CAF50),
    INTERMEDIATE("Intermediário", 0xFFFFB300),
    ADVANCED("Avançado", 0xFFFF7043),
    MASTER("Mestre", 0xFFE53935)
}

data class MaterialItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val iconHint: String = "block",
    val category: String = "Blocos"
) {
    val stacksDescription: String
        get() {
            val stacks = quantity / 64
            val remainder = quantity % 64
            return when {
                stacks > 0 && remainder > 0 -> "$stacks packs + $remainder blocos"
                stacks > 0 -> "$stacks packs (64)"
                else -> "$remainder blocos"
            }
        }
}

enum class StepIllustrationType {
    FOUNDATION_GRID,
    PILLARS_FRAME,
    WALLS_WINDOWS,
    ROOF_ARCH,
    INTERIOR_LIGHTS,
    FARM_PLOTS,
    TOWER_BATTLEMENT,
    GENERIC_VOXEL
}

data class TutorialStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val builderTip: String? = null,
    val requiredBlocks: List<String> = emptyList(),
    val layerInfo: String? = null,
    val illustrationType: StepIllustrationType = StepIllustrationType.GENERIC_VOXEL,
    @DrawableRes val imageRes: Int? = null
)

data class Tutorial(
    val id: String,
    val title: String,
    val shortDescription: String,
    val fullDescription: String,
    val category: TutorialCategory,
    val difficulty: BuildDifficulty,
    val estimatedTime: String,
    val dimensions: String,
    @DrawableRes val heroImageRes: Int? = null,
    val materials: List<MaterialItem>,
    val steps: List<TutorialStep>,
    val isFeatured: Boolean = false,
    val tags: List<String> = emptyList()
)
