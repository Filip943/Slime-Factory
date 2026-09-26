package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MutationType
import com.example.ui.GameUiState
import com.example.ui.components.SlimeCanvas
import com.example.util.NumberFormatter

@Composable
fun MutationLabScreen(
  state: GameUiState,
  onSelectSlot: (Int) -> Unit,
  onAttemptMutation: () -> Boolean,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  val selectedSlot = state.slimeSlots.find { it.slotIndex == state.selectedChamberSlotIndex }
    ?: state.slimeSlots.firstOrNull { it.isUnlocked }

  val catalystCost = selectedSlot?.let { (it.slimeType.baseCost * 0.4).toLong().coerceAtLeast(150L) } ?: 200L
  val canAfford = state.coins >= catalystCost && selectedSlot != null

  val infiniteTransition = rememberInfiniteTransition(label = "chamber_pulse")
  val pulseGlow by infiniteTransition.animateFloat(
    initialValue = 0.98f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("mutation_lab_screen"),
    contentPadding = PaddingValues(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Lab Header & Slot Picker
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.5.dp, Color(0xFF818CF8), RoundedCornerShape(20.dp))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🧬", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Komora Mutacyjna DNA",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
              )
              Text(
                text = "Umieść slime'a w inkubatorze i poddaj go syntezie",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFC7D2FE))
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Wybierz obiekt testowy:",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Color(0xFF94A3B8)
            )
          )

          Spacer(modifier = Modifier.height(6.dp))

          // Slots row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            state.slimeSlots.filter { it.isUnlocked }.forEach { slot ->
              val isSelected = selectedSlot?.slotIndex == slot.slotIndex
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) Color(0xFF6366F1) else Color(0xFF0F172A))
                  .border(
                    1.dp,
                    if (isSelected) Color.White else Color(0xFF334155),
                    RoundedCornerShape(10.dp)
                  )
                  .clickable { onSelectSlot(slot.slotIndex) }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = slot.slimeType.emoji, fontSize = 16.sp)
                  Text(
                    text = "#${slot.slotIndex + 1}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) Color.White else Color(0xFF94A3B8)
                    )
                  )
                }
              }
            }
          }
        }
      }
    }

    // 2. High-Tech Glass Chamber with Slime
    item {
      if (selectedSlot != null) {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF0B0F19)),
          modifier = Modifier
            .fillMaxWidth()
            .border(
              width = 2.dp,
              brush = Brush.verticalGradient(
                listOf(Color(0xFF818CF8), Color(0xFF312E81), Color(0xFF0F172A))
              ),
              shape = RoundedCornerShape(24.dp)
            )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Chamber Slime Canvas with gentle pulse
            Box(
              modifier = Modifier
                .size(160.dp)
                .scale(pulseGlow),
              contentAlignment = Alignment.Center
            ) {
              SlimeCanvas(
                slimeType = selectedSlot.slimeType,
                mutationType = selectedSlot.mutationType,
                sizeDp = 150.dp,
                interactive = false
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current Status Box
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B))
                .padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${selectedSlot.slimeType.displayNamePl} • ",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              )
              Text(
                text = selectedSlot.mutationType.displayNamePl,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = Color(selectedSlot.mutationType.rarityColor)
                )
              )
              if (selectedSlot.mutationType != MutationType.NONE) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "(${selectedSlot.mutationType.multiplier}x)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFBBF24)
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mutation Probability Stats
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1E1B4B).copy(alpha = 0.6f))
                .border(1.dp, Color(0xFF4338CA), RoundedCornerShape(14.dp))
                .padding(12.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "Całkowita szansa na mutację:",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFC7D2FE))
                  )
                  Text(
                    text = String.format(java.util.Locale.US, "%.1f%%", state.totalMutationChancePercent),
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Black,
                      color = Color(0xFF38BDF8)
                    )
                  )
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "Koszt katalizatora:",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                  )
                  Text(
                    text = "${NumberFormatter.formatCoins(catalystCost.toDouble())} 💰",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFFBBF24)
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Feedback Message
            AnimatedVisibility(visible = state.mutationMessage != null) {
              Text(
                text = state.mutationMessage ?: "",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (state.mutationMessage?.contains("SUKCES") == true) Color(0xFF4ADE80) else Color(0xFFFCA5A5)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 10.dp)
              )
            }

            // Big MUTATE Button
            Button(
              onClick = {
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onAttemptMutation()
              },
              enabled = canAfford,
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B5CF6),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF334155),
                disabledContentColor = Color(0xFF64748B)
              ),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("mutate_slime_button")
            ) {
              Text(
                text = "⚡ MUTUJ SLIME'A 🧬",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                )
              )
            }
          }
        }
      }
    }

    // 3. Mutation Bestiary / Codex
    item {
      Text(
        text = "📖 Księga Odkrytych Mutacji",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Black,
          color = Color.White
        ),
        modifier = Modifier.padding(top = 8.dp)
      )
    }

    items(MutationType.entries.filter { it != MutationType.NONE }) { mutation ->
      val isDiscovered = state.discoveredMutations.any { it.endsWith(mutation.id) }

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isDiscovered) Color(0xFF1E293B) else Color(0xFF0F172A)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .border(
            1.dp,
            if (isDiscovered) Color(mutation.rarityColor).copy(alpha = 0.6f) else Color(0xFF1E293B),
            RoundedCornerShape(16.dp)
          )
          .testTag("bestiary_card_${mutation.id}")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isDiscovered) Color(mutation.rarityColor).copy(alpha = 0.2f) else Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (isDiscovered) "✨" else "❓",
              fontSize = 22.sp
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = if (isDiscovered) mutation.displayNamePl else "Nieznana Mutacja",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = if (isDiscovered) Color.White else Color(0xFF64748B)
                )
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isDiscovered) mutation.rarityNamePl else "???",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isDiscovered) Color(mutation.rarityColor) else Color(0xFF64748B)
                )
              )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = if (isDiscovered) mutation.description else "Poddawaj slime'y mutacjom w laboratorium, aby odkryć tę formę.",
              style = MaterialTheme.typography.bodySmall.copy(
                color = if (isDiscovered) Color(0xFF94A3B8) else Color(0xFF475569)
              )
            )
          }
        }
      }
    }
  }
}
