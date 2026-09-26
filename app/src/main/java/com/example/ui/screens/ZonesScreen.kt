package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ZoneType
import com.example.ui.GameUiState
import com.example.util.NumberFormatter

@Composable
fun ZonesScreen(
  state: GameUiState,
  onSelectZone: (ZoneType) -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("zones_screen"),
    contentPadding = PaddingValues(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.5.dp, Color(0xFF38BDF8), RoundedCornerShape(20.dp))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🌎", fontSize = 28.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Biomy & Strefy Świata",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
              )
              Text(
                text = "Odblokowuj nowe lokacje ze specjalnymi premiami",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
              )
            }
          }
        }
      }
    }

    items(ZoneType.entries) { zone ->
      val isUnlocked = state.unlockedZoneIds.contains(zone.id)
      val isActive = state.activeZone == zone
      val canAfford = state.coins >= zone.unlockCost

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(zone.surfaceColor)),
        modifier = Modifier
          .fillMaxWidth()
          .border(
            width = if (isActive) 2.dp else 1.dp,
            color = if (isActive) Color(zone.accentColor) else Color(0xFF334155),
            shape = RoundedCornerShape(18.dp)
          )
          .testTag("zone_card_${zone.id}")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = zone.emoji, fontSize = 32.sp)
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = zone.namePl,
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = Color.White
                    )
                  )
                  if (isActive) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(zone.accentColor))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = "AKTYWNA",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontWeight = FontWeight.Black,
                          color = Color.Black
                        )
                      )
                    }
                  }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = zone.descriptionPl,
                  style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Zone Perks Box
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF0F172A).copy(alpha = 0.7f))
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "Produkcja",
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
              )
              Text(
                text = "x${zone.prodMultiplier}",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF4ADE80)
                )
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "Cena Goo",
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
              )
              Text(
                text = "x${zone.valueMultiplier}",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Black,
                  color = Color(0xFFFBBF24)
                )
              )
            }

            if (zone.mutationBonusPct > 0.0) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "Mutacje",
                  style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                )
                Text(
                  text = "+${zone.mutationBonusPct.toInt()}%",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFA855F7)
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Action Button
          Button(
            onClick = {
              haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
              onSelectZone(zone)
            },
            enabled = isActive || isUnlocked || canAfford,
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isActive) Color(0xFF334155) else if (isUnlocked) Color(0xFF0284C7) else Color(0xFF22C55E),
              contentColor = if (isActive) Color(0xFF94A3B8) else if (isUnlocked) Color.White else Color.Black,
              disabledContainerColor = Color(0xFF1E293B),
              disabledContentColor = Color(0xFF64748B)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("zone_action_${zone.id}_button")
          ) {
            Text(
              text = when {
                isActive -> "Obecnie wybrany biom"
                isUnlocked -> "Podróżuj do tego biomu 🚀"
                else -> "Odblokuj biom za ${NumberFormatter.formatCoins(zone.unlockCost.toDouble())} 💰"
              },
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
              )
            )
          }
        }
      }
    }
  }
}
