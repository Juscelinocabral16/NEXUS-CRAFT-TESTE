package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fort
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.BuildDifficulty
import com.example.data.model.TutorialCategory

@Composable
fun CategoryChips(
    selectedCategory: TutorialCategory?,
    onSelectCategory: (TutorialCategory?) -> Unit,
    onlyFavorites: Boolean,
    onToggleFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Favorite Filter Chip
        FilterChip(
            selected = onlyFavorites,
            onClick = onToggleFavorites,
            label = { Text("Favoritos") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (onlyFavorites) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("filter_favorites")
        )

        TutorialCategory.values().forEach { category ->
            val isSelected = selectedCategory == category
            val icon = getCategoryIcon(category)

            FilterChip(
                selected = isSelected,
                onClick = { onSelectCategory(if (isSelected) null else category) },
                label = { Text(category.displayName) },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("filter_category_${category.name.lowercase()}")
            )
        }
    }
}

@Composable
fun DifficultyFilterRow(
    selectedDifficulty: BuildDifficulty?,
    onSelectDifficulty: (BuildDifficulty?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BuildDifficulty.values().forEach { diff ->
            val isSelected = selectedDifficulty == diff
            FilterChip(
                selected = isSelected,
                onClick = { onSelectDifficulty(diff) },
                label = { Text(diff.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(diff.colorHex).copy(alpha = 0.85f),
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.testTag("filter_difficulty_${diff.name.lowercase()}")
            )
        }
    }
}

private fun getCategoryIcon(category: TutorialCategory): ImageVector {
    return when (category) {
        TutorialCategory.ALL -> Icons.Default.GridView
        TutorialCategory.HOUSES -> Icons.Default.Home
        TutorialCategory.FARMS -> Icons.Default.Eco
        TutorialCategory.CASTLES -> Icons.Default.Fort
        TutorialCategory.SURVIVAL -> Icons.Default.Shield
        TutorialCategory.DECORATION -> Icons.Default.Palette
        TutorialCategory.NETHER -> Icons.Default.Whatshot
    }
}
