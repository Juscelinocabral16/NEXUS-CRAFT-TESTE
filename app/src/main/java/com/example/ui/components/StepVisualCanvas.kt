package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StepIllustrationType
import com.example.data.model.TutorialStep

@Composable
fun StepVisualCanvas(
    step: TutorialStep,
    onZoomClick: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF141C16))
            .testTag("step_visual_container_${step.stepNumber}")
    ) {
        if (step.imageRes != null) {
            Image(
                painter = painterResource(id = step.imageRes),
                contentDescription = "Foto do passo: ${step.title}",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = onZoomClick != null) {
                        onZoomClick?.invoke(step.imageRes)
                    },
                contentScale = ContentScale.Crop
            )

            if (onZoomClick != null) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clickable { onZoomClick(step.imageRes) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = "Ampliar foto",
                            tint = Color.White
                        )
                    }
                }
            }
        } else {
            // Draw interactive Minecraft blueprint canvas diagram based on step type!
            BlueprintGridCanvas(step = step)
        }

        // Layer badge
        if (step.layerInfo != null) {
            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(bottomEnd = 10.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = step.layerInfo,
                    color = Color(0xFF81C784),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun BlueprintGridCanvas(step: TutorialStep) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Background subtle blueprint grid
        val gridSpacing = 24f
        var x = 0f
        while (x < w) {
            drawLine(
                color = Color(0x22388E3C),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
            )
            x += gridSpacing
        }
        var y = 0f
        while (y < h) {
            drawLine(
                color = Color(0x22388E3C),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
            y += gridSpacing
        }

        // Center isometric/2D block representation
        val centerX = w / 2f
        val centerY = h / 2f

        when (step.illustrationType) {
            StepIllustrationType.FOUNDATION_GRID -> {
                // Draw 2D block grid foundation
                val blockSize = 26f
                val rows = 5
                val cols = 7
                val startX = centerX - (cols * blockSize) / 2
                val startY = centerY - (rows * blockSize) / 2

                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val isCorner = (r == 0 || r == rows - 1) && (c == 0 || c == cols - 1)
                        val isBorder = r == 0 || r == rows - 1 || c == 0 || c == cols - 1
                        val color = when {
                            isCorner -> Color(0xFF6D4C41) // Wood pillar base
                            isBorder -> Color(0xFF78909C) // Stone bricks
                            else -> Color(0xFF546E7A) // Floor
                        }
                        drawRect(
                            color = color,
                            topLeft = Offset(startX + c * blockSize, startY + r * blockSize),
                            size = Size(blockSize - 2f, blockSize - 2f)
                        )
                    }
                }
            }

            StepIllustrationType.PILLARS_FRAME -> {
                // Draw isometric pillars
                val p1 = Offset(centerX - 120f, centerY + 30f)
                val p2 = Offset(centerX + 120f, centerY + 30f)
                val p3 = Offset(centerX - 40f, centerY + 70f)
                val p4 = Offset(centerX + 40f, centerY + 70f)

                listOf(p1, p2, p3, p4).forEach { pt ->
                    // Pillar
                    drawRect(
                        color = Color(0xFF8D6E63),
                        topLeft = Offset(pt.x - 14f, pt.y - 100f),
                        size = Size(28f, 100f)
                    )
                    // Pillar top cap
                    drawRect(
                        color = Color(0xFFA1887F),
                        topLeft = Offset(pt.x - 16f, pt.y - 105f),
                        size = Size(32f, 10f)
                    )
                }
                // Connecting beam
                drawLine(
                    color = Color(0xFFBCAAA4),
                    start = Offset(p1.x, p1.y - 95f),
                    end = Offset(p2.x, p2.y - 95f),
                    strokeWidth = 10f
                )
            }

            StepIllustrationType.WALLS_WINDOWS -> {
                // Glass & walls
                drawRect(
                    color = Color(0xFF37474F),
                    topLeft = Offset(centerX - 130f, centerY - 60f),
                    size = Size(260f, 120f)
                )
                // Windows
                drawRect(
                    color = Color(0xCC80DEEA),
                    topLeft = Offset(centerX - 90f, centerY - 40f),
                    size = Size(80f, 80f)
                )
                drawRect(
                    color = Color(0xCC80DEEA),
                    topLeft = Offset(centerX + 10f, centerY - 40f),
                    size = Size(80f, 80f)
                )
                // Frame dividers
                drawLine(
                    color = Color(0xFFFFFFFF),
                    start = Offset(centerX - 50f, centerY - 40f),
                    end = Offset(centerX - 50f, centerY + 40f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color(0xFFFFFFFF),
                    start = Offset(centerX + 50f, centerY - 40f),
                    end = Offset(centerX + 50f, centerY + 40f),
                    strokeWidth = 3f
                )
            }

            StepIllustrationType.ROOF_ARCH -> {
                // Triangular roof rafters
                val path = Path().apply {
                    moveTo(centerX, centerY - 80f)
                    lineTo(centerX + 140f, centerY + 50f)
                    lineTo(centerX - 140f, centerY + 50f)
                    close()
                }
                drawPath(path = path, color = Color(0xFF5D4037))

                // Inner warm glow
                drawRect(
                    color = Color(0xFFFFB300),
                    topLeft = Offset(centerX - 30f, centerY - 10f),
                    size = Size(60f, 60f)
                )
            }

            StepIllustrationType.FARM_PLOTS -> {
                // Farmland rows
                val rowHeight = 18f
                val rowWidth = 240f
                val startX = centerX - rowWidth / 2
                val startY = centerY - 50f

                for (i in 0 until 5) {
                    val isWater = i == 2
                    val color = if (isWater) Color(0xFF1E88E5) else Color(0xFF3E2723)
                    drawRect(
                        color = color,
                        topLeft = Offset(startX, startY + i * (rowHeight + 4f)),
                        size = Size(rowWidth, rowHeight)
                    )
                    // Crops on dirt
                    if (!isWater) {
                        for (c in 0 until 8) {
                            drawCircle(
                                color = Color(0xFF7CB342),
                                radius = 6f,
                                center = Offset(startX + 18f + c * 28f, startY + i * (rowHeight + 4f) + rowHeight / 2)
                            )
                        }
                    }
                }
            }

            StepIllustrationType.TOWER_BATTLEMENT -> {
                // Battlements crenellations
                val bW = 200f
                val startX = centerX - bW / 2
                // Base
                drawRect(
                    color = Color(0xFF616161),
                    topLeft = Offset(startX, centerY - 20f),
                    size = Size(bW, 80f)
                )
                // Teeth
                val teethCount = 5
                val toothW = bW / (teethCount * 2 - 1)
                for (t in 0 until teethCount) {
                    drawRect(
                        color = Color(0xFF757575),
                        topLeft = Offset(startX + t * toothW * 2, centerY - 50f),
                        size = Size(toothW, 30f)
                    )
                }
            }

            StepIllustrationType.INTERIOR_LIGHTS, StepIllustrationType.GENERIC_VOXEL -> {
                // Isometric 3D block
                val bSize = 60f
                // Top face
                val topPath = Path().apply {
                    moveTo(centerX, centerY - bSize)
                    lineTo(centerX + bSize, centerY - bSize / 2)
                    lineTo(centerX, centerY)
                    lineTo(centerX - bSize, centerY - bSize / 2)
                    close()
                }
                drawPath(topPath, Color(0xFF4CAF50)) // Grass top

                // Left face
                val leftPath = Path().apply {
                    moveTo(centerX - bSize, centerY - bSize / 2)
                    lineTo(centerX, centerY)
                    lineTo(centerX, centerY + bSize)
                    lineTo(centerX - bSize, centerY + bSize / 2)
                    close()
                }
                drawPath(leftPath, Color(0xFF5D4037)) // Dirt left

                // Right face
                val rightPath = Path().apply {
                    moveTo(centerX, centerY)
                    lineTo(centerX + bSize, centerY - bSize / 2)
                    lineTo(centerX + bSize, centerY + bSize / 2)
                    lineTo(centerX, centerY + bSize)
                    close()
                }
                drawPath(rightPath, Color(0xFF4E342E)) // Dirt right
            }
        }
    }
}
