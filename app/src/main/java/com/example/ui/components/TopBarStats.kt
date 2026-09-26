package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ZoneType
import com.example.util.NumberFormatter

@Composable
fun TopBarStats(
  coins: Double,
  goo: Double,
  prodRatePerSec: Double,
  rebirthTokens: Int,
  activeZone: ZoneType,
  isSoundEnabled: Boolean,
  onToggleSound: () -> Unit,
  onZoneClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = Color(0xFF0F172A).copy(alpha = 0.95f),
    tonalElevation = 6.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Active Zone Pill
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(activeZone.surfaceColor))
            .border(1.dp, Color(activeZone.accentColor).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .clickable { onZoneClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("zone_indicator_pill"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = activeZone.emoji, fontSize = 14.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = activeZone.namePl,
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Rebirth Tokens pill if > 0
          if (rebirthTokens > 0) {
            Row(
              modifier = Modifier
                .padding(end = 8.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF3B0764))
                .border(1.dp, Color(0xFFC084FC), RoundedCornerShape(14.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "♻️", fontSize = 12.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "$rebirthTokens",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFE9D5FF)
                )
              )
            }
          }

          // Sound Toggle Button
          IconButton(
            onClick = onToggleSound,
            modifier = Modifier
              .size(36.dp)
              .testTag("sound_toggle_button")
          ) {
            Icon(
              imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
              contentDescription = "Dźwięk",
              tint = if (isSoundEnabled) Color(0xFF4ADE80) else Color(0xFF64748B)
            )
          }
        }
      }

      Spacer(modifier = Modifier.size(8.dp))

      // Currencies Stat Row: Coins & Goo
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Coins Card
        StatChip(
          icon = "💰",
          label = "Monety",
          value = NumberFormatter.formatCoins(coins),
          accentColor = Color(0xFFFBBF24),
          subtext = null,
          modifier = Modifier.weight(1f).testTag("coins_stat_chip")
        )

        // Goo Card
        StatChip(
          icon = "🧪",
          label = "Goo",
          value = NumberFormatter.formatGoo(goo),
          accentColor = Color(0xFF4ADE80),
          subtext = "+${NumberFormatter.formatGoo(prodRatePerSec)}/s",
          modifier = Modifier.weight(1f).testTag("goo_stat_chip")
        )
      }
    }
  }
}

@Composable
private fun StatChip(
  icon: String,
  label: String,
  value: String,
  accentColor: Color,
  subtext: String?,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF1E293B))
      .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
      .padding(horizontal = 10.dp, vertical = 7.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(text = icon, fontSize = 20.sp)
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall.copy(
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
          )
        )
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
              color = accentColor,
              fontWeight = FontWeight.Black,
              fontSize = 15.sp
            )
          )
          if (subtext != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = subtext,
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF86EFAC),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            )
          }
        }
      }
    }
  }
}
