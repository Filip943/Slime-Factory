package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.NumberFormatter
import kotlin.math.sin

@Composable
fun GooTankMeter(
  currentGoo: Double,
  maxCapacity: Double,
  modifier: Modifier = Modifier,
  gooColor: Color = Color(0xFF22C55E)
) {
  val fillFraction = (currentGoo / maxCapacity).coerceIn(0.0, 1.0).toFloat()
  val percentInt = (fillFraction * 100).toInt()

  val infiniteTransition = rememberInfiniteTransition(label = "goo_wave")
  val wavePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = (2 * Math.PI).toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wave"
  )

  Column(modifier = modifier) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "🧪 Zbiornik Goo",
        style = MaterialTheme.typography.labelLarge.copy(
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      )
      Spacer(modifier = Modifier.weight(1f))
      Text(
        text = "${NumberFormatter.formatGoo(currentGoo)} / ${NumberFormatter.formatGoo(maxCapacity)} ($percentInt%)",
        style = MaterialTheme.typography.bodySmall.copy(
          fontWeight = FontWeight.Medium,
          color = if (fillFraction >= 0.95f) Color(0xFFF87171) else Color(0xFF94A3B8)
        )
      )
    }

    // Glowing glass tube
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(28.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(Color(0xFF0F172A))
        .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
    ) {
      Canvas(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        val w = size.width
        val h = size.height
        val fillWidth = w * fillFraction

        if (fillWidth > 0f) {
          val path = Path().apply {
            moveTo(0f, h)
            lineTo(0f, 0f)

            // Dynamic sine wave at the edge
            val waveAmp = if (fillFraction in 0.05f..0.95f) 4.dp.toPx() else 1.dp.toPx()
            val points = 30
            val step = fillWidth / points
            for (i in 0..points) {
              val x = i * step
              val y = waveAmp * sin(wavePhase + (i * 0.4f))
              lineTo(x, y + 4.dp.toPx())
            }

            lineTo(fillWidth, h)
            close()
          }

          drawPath(
            path = path,
            brush = Brush.horizontalGradient(
              colors = listOf(
                gooColor.copy(alpha = 0.85f),
                gooColor,
                if (fillFraction >= 0.9f) Color(0xFFFACC15) else Color(0xFF86EFAC)
              )
            )
          )

          // Light reflection tube highlight
          drawRect(
            color = Color.White.copy(alpha = 0.25f),
            topLeft = Offset(4f, 2f),
            size = Size(fillWidth - 8f, 3.dp.toPx())
          )
        }
      }

      if (fillFraction >= 0.98f) {
        Text(
          text = "MAGAZYN PEŁEN! SPRZEDAJ GOO!",
          modifier = Modifier.align(Alignment.Center),
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 1.sp
          )
        )
      }
    }
  }
}
