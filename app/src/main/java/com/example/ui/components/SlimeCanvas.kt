package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.MutationType
import com.example.model.SlimeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

data class FloatingParticle(
  val id: Long,
  val text: String,
  val startX: Float,
  val startY: Float,
  val color: Color
)

@Composable
fun SlimeCanvas(
  slimeType: SlimeType,
  mutationType: MutationType = MutationType.NONE,
  modifier: Modifier = Modifier,
  sizeDp: Dp = 160.dp,
  interactive: Boolean = true,
  onSlimeTapped: (() -> Unit)? = null
) {
  val coroutineScope = rememberCoroutineScope()

  // Squish spring animations
  val squishScaleX = remember { Animatable(1f) }
  val squishScaleY = remember { Animatable(1f) }

  // Idle gentle breathing & floating transition
  val infiniteTransition = rememberInfiniteTransition(label = "slime_idle")
  val breathScale by infiniteTransition.animateFloat(
    initialValue = 0.97f,
    targetValue = 1.03f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breath"
  )
  val floatOffsetY by infiniteTransition.animateFloat(
    initialValue = -4f,
    targetValue = 4f,
    animationSpec = infiniteRepeatable(
      animation = tween(1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "float"
  )

  // Rainbow color cycling if Rainbow mutation
  val rainbowHue by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rainbow"
  )

  // Blinking eyes logic
  val blinkState = remember { Animatable(1f) }
  LaunchedEffect(Unit) {
    while (true) {
      delay((2000..4500).random().toLong())
      blinkState.animateTo(0.1f, tween(80))
      blinkState.animateTo(1f, tween(100))
    }
  }

  val baseColor1 = if (mutationType == MutationType.RAINBOW) {
    Color.hsv(rainbowHue, 0.75f, 0.95f)
  } else {
    Color(slimeType.primaryColor)
  }

  val baseColor2 = if (mutationType == MutationType.RAINBOW) {
    Color.hsv((rainbowHue + 40) % 360f, 0.85f, 0.75f)
  } else {
    Color(slimeType.secondaryColor)
  }

  val auraColor = when (mutationType) {
    MutationType.NONE -> Color(slimeType.glowColor)
    MutationType.CRYSTAL -> Color(0x9938BDF8)
    MutationType.VOID -> Color(0x99A855F7)
    MutationType.RAINBOW -> Color(0x99F59E0B)
    MutationType.QUANTUM -> Color(0xAA34D399)
  }

  Box(
    modifier = modifier
      .size(sizeDp)
      .pointerInput(interactive) {
        if (!interactive) return@pointerInput
        detectTapGestures(
          onTap = {
            onSlimeTapped?.invoke()
            coroutineScope.launch {
              // Bouncy squash & stretch
              launch {
                squishScaleX.animateTo(1.28f, tween(70, easing = FastOutSlowInEasing))
                squishScaleX.animateTo(0.92f, tween(90, easing = FastOutSlowInEasing))
                squishScaleX.animateTo(1.0f, tween(120, easing = FastOutSlowInEasing))
              }
              launch {
                squishScaleY.animateTo(0.72f, tween(70, easing = FastOutSlowInEasing))
                squishScaleY.animateTo(1.12f, tween(90, easing = FastOutSlowInEasing))
                squishScaleY.animateTo(1.0f, tween(120, easing = FastOutSlowInEasing))
              }
            }
          }
        )
      },
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val cx = w / 2f
      val cy = (h / 2f) + floatOffsetY

      val currentScaleX = squishScaleX.value * breathScale
      val currentScaleY = squishScaleY.value / breathScale

      // Draw Slime Shadow
      val shadowWidth = w * 0.65f * currentScaleX
      val shadowHeight = h * 0.14f * currentScaleY
      drawOval(
        color = Color(0x33000000),
        topLeft = Offset(cx - shadowWidth / 2f, h * 0.85f),
        size = Size(shadowWidth, shadowHeight)
      )

      // Draw Mutation Aura Glow if mutated
      if (mutationType != MutationType.NONE) {
        val glowRadius = w * 0.48f * currentScaleX
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(auraColor, Color.Transparent),
            center = Offset(cx, cy),
            radius = glowRadius * 1.3f
          ),
          radius = glowRadius * 1.3f,
          center = Offset(cx, cy)
        )
      }

      // Draw Slime Teardrop / Blob Body
      val bodyPath = Path().apply {
        val bodyW = (w * 0.38f) * currentScaleX
        val bodyH = (h * 0.36f) * currentScaleY

        // Start at bottom center
        moveTo(cx, cy + bodyH)

        // Bottom right curve
        cubicTo(
          cx + bodyW * 0.85f, cy + bodyH,
          cx + bodyW * 1.15f, cy + bodyH * 0.5f,
          cx + bodyW * 0.95f, cy
        )

        // Top right tapering toward peak
        cubicTo(
          cx + bodyW * 0.8f, cy - bodyH * 0.6f,
          cx + bodyW * 0.3f, cy - bodyH * 1.05f,
          cx, cy - bodyH * 1.12f // Crown point
        )

        // Top left tapering down
        cubicTo(
          cx - bodyW * 0.3f, cy - bodyH * 1.05f,
          cx - bodyW * 0.8f, cy - bodyH * 0.6f,
          cx - bodyW * 0.95f, cy
        )

        // Bottom left curve
        cubicTo(
          cx - bodyW * 1.15f, cy + bodyH * 0.5f,
          cx - bodyW * 0.85f, cy + bodyH,
          cx, cy + bodyH
        )
        close()
      }

      // Gradient Fill for Slime Body
      drawPath(
        path = bodyPath,
        brush = Brush.verticalGradient(
          colors = listOf(baseColor1, baseColor2),
          startY = cy - h * 0.4f,
          endY = cy + h * 0.4f
        ),
        style = Fill
      )

      // Rim outline for juicy cartoon feel
      drawPath(
        path = bodyPath,
        color = baseColor2.copy(alpha = 0.5f),
        style = Stroke(width = 3.dp.toPx())
      )

      // Glossy Top-Left Highlight
      val glossRadius = (w * 0.14f) * currentScaleX
      val glossCenter = Offset(cx - w * 0.16f * currentScaleX, cy - h * 0.16f * currentScaleY)
      drawCircle(
        color = Color.White.copy(alpha = 0.45f),
        radius = glossRadius,
        center = glossCenter
      )
      drawCircle(
        color = Color.White.copy(alpha = 0.75f),
        radius = glossRadius * 0.4f,
        center = glossCenter.copy(x = glossCenter.x - glossRadius * 0.2f, y = glossCenter.y - glossRadius * 0.2f)
      )

      // Eyes positions
      val eyeSpacing = w * 0.15f * currentScaleX
      val eyeY = cy + h * 0.02f * currentScaleY
      val eyeW = (w * 0.055f) * currentScaleX
      val eyeH = (h * 0.08f) * currentScaleY * blinkState.value

      // Left & Right Eye Outer
      val eyeColor = if (mutationType == MutationType.VOID) Color(0xFFFFD54F) else Color(0xFF1E293B)

      // Left Eye
      drawOval(
        color = eyeColor,
        topLeft = Offset(cx - eyeSpacing - eyeW, eyeY - eyeH / 2f),
        size = Size(eyeW * 2f, eyeH)
      )
      // Right Eye
      drawOval(
        color = eyeColor,
        topLeft = Offset(cx + eyeSpacing - eyeW, eyeY - eyeH / 2f),
        size = Size(eyeW * 2f, eyeH)
      )

      // Sparkles in Eyes (if eyes are open)
      if (blinkState.value > 0.4f) {
        val sparkleRadius = eyeW * 0.45f
        drawCircle(
          color = Color.White,
          radius = sparkleRadius,
          center = Offset(cx - eyeSpacing - eyeW * 0.3f, eyeY - eyeH * 0.25f)
        )
        drawCircle(
          color = Color.White,
          radius = sparkleRadius,
          center = Offset(cx + eyeSpacing - eyeW * 0.3f, eyeY - eyeH * 0.25f)
        )
      }

      // Cute Smile
      val smileY = cy + h * 0.12f * currentScaleY
      val smilePath = Path().apply {
        moveTo(cx - w * 0.08f * currentScaleX, smileY)
        quadraticTo(
          cx, smileY + h * 0.06f * currentScaleY,
          cx + w * 0.08f * currentScaleX, smileY
        )
      }
      drawPath(
        path = smilePath,
        color = eyeColor,
        style = Stroke(width = 3.5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
      )

      // Rosy Cheeks
      val cheekY = cy + h * 0.08f * currentScaleY
      val cheekRadius = (w * 0.045f) * currentScaleX
      val cheekColor = Color(0xFFFF8A80).copy(alpha = 0.4f)
      drawCircle(
        color = cheekColor,
        radius = cheekRadius,
        center = Offset(cx - eyeSpacing * 1.5f, cheekY)
      )
      drawCircle(
        color = cheekColor,
        radius = cheekRadius,
        center = Offset(cx + eyeSpacing * 1.5f, cheekY)
      )

      // If Mutation is Crystal: Draw little floating diamond shards
      if (mutationType == MutationType.CRYSTAL) {
        for (i in 0..2) {
          val crystalAngle = (rainbowHue + i * 120) * (Math.PI / 180.0)
          val cX = cx + (w * 0.32f * cos(crystalAngle)).toFloat()
          val cY = cy + (h * 0.28f * sin(crystalAngle)).toFloat()
          drawCircle(
            color = Color(0xFFBAE6FD),
            radius = 5.dp.toPx(),
            center = Offset(cX, cY)
          )
        }
      }
    }
  }
}
