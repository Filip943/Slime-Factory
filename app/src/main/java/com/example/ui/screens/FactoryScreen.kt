package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MutationType
import com.example.ui.GameUiState
import com.example.ui.SlimeSlotData
import com.example.ui.components.FloatingParticle
import com.example.ui.components.GooTankMeter
import com.example.ui.components.SlimeCanvas
import com.example.util.NumberFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FactoryScreen(
  state: GameUiState,
  onTapSlime: () -> Double,
  onSellGoo: () -> Unit,
  onLevelUpSlime: (Int) -> Unit,
  onUnlockSlot: (Int, Long) -> Unit,
  onNavigateToShop: () -> Unit,
  onNavigateToLab: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  val coroutineScope = rememberCoroutineScope()
  val floatingParticles = remember { mutableStateListOf<FloatingParticle>() }

  val mainSlime = state.slimeSlots.firstOrNull { it.isUnlocked }
  val sellValue = state.currentGoo * state.sellPricePerGoo
  val canSell = state.currentGoo > 0.0

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("factory_screen"),
    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Interactive Slime Hero Podium
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(state.activeZone.surfaceColor)),
        modifier = Modifier
          .fillMaxWidth()
          .border(
            width = 1.5.dp,
            brush = Brush.verticalGradient(
              listOf(Color(state.activeZone.accentColor), Color(0xFF334155))
            ),
            shape = RoundedCornerShape(24.dp)
          )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Factory pipeline banner
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF0F172A).copy(alpha = 0.6f))
              .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "🏭 STANOWISKO GŁÓWNE",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(state.activeZone.accentColor),
                letterSpacing = 0.5.sp
              )
            )
            Text(
              text = "Dotknij, aby wycisnąć Goo! 👆",
              style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Big bouncy slime container with floating particles
          Box(
            modifier = Modifier
              .size(190.dp)
              .testTag("main_slime_tap_target"),
            contentAlignment = Alignment.Center
          ) {
            if (mainSlime != null) {
              SlimeCanvas(
                slimeType = mainSlime.slimeType,
                mutationType = mainSlime.mutationType,
                sizeDp = 180.dp,
                interactive = true,
                onSlimeTapped = {
                  haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                  val gain = onTapSlime()

                  val newId = System.currentTimeMillis()
                  val p = FloatingParticle(
                    id = newId,
                    text = "+${NumberFormatter.formatGoo(gain)} Goo",
                    startX = (-40..40).random().toFloat(),
                    startY = (-20..20).random().toFloat(),
                    color = Color(mainSlime.slimeType.primaryColor)
                  )
                  floatingParticles.add(p)
                  coroutineScope.launch {
                    delay(800)
                    floatingParticles.removeAll { it.id == newId }
                  }
                }
              )
            }

            // Render floating text particles
            floatingParticles.forEach { particle ->
              FloatingTextEffect(particle = particle)
            }
          }

          // Main Slime Info Badge
          if (mainSlime != null) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.padding(top = 4.dp)
            ) {
              Text(
                text = "${mainSlime.slimeType.emoji} ${mainSlime.slimeType.displayNamePl}",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
              )
              if (mainSlime.mutationType != MutationType.NONE) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(mainSlime.mutationType.rarityColor))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = mainSlime.mutationType.displayNamePl,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Black,
                      color = Color.Black
                    )
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Goo Tank Meter
          GooTankMeter(
            currentGoo = state.currentGoo,
            maxCapacity = state.maxGooCapacity,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Big Juicy SELL GOO Button
          Button(
            onClick = {
              if (canSell) {
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onSellGoo()
              }
            },
            enabled = canSell,
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFF59E0B),
              disabledContainerColor = Color(0xFF334155),
              contentColor = Color(0xFF1E293B),
              disabledContentColor = Color(0xFF64748B)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(54.dp)
              .testTag("sell_goo_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(text = "💰", fontSize = 22.sp)
              Spacer(modifier = Modifier.width(10.dp))
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "SPRZEDAJ WSZYSTKO",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                  )
                )
                Text(
                  text = if (canSell) "+${NumberFormatter.formatCoins(sellValue)} Coins" else "Brak Goo do sprzedaży",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold
                  )
                )
              }
            }
          }

          if (state.autoSellerUpgradeLevel > 0) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(top = 2.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF4ADE80))
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "🤖 Auto-Sprzedaż aktywna (${state.autoSellerUpgradeLevel * 12}%/s)",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color(0xFF86EFAC),
                  fontWeight = FontWeight.SemiBold
                )
              )
            }
          }
        }
      }
    }

    // 2. Factory Workstations Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "🏭 Stanowiska Produkcyjne",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              color = Color.White
            )
          )
          Text(
            text = "${state.slimeSlots.count { it.isUnlocked }} / ${state.slimeSlots.size} stanowisk aktywnych",
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
          )
        }

        Button(
          onClick = onNavigateToShop,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0284C7),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("shop_shortcut_button")
        ) {
          Text(
            text = "+ Nowy Slime",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }

    // 3. Workstation Cards List
    items(state.slimeSlots) { slot ->
      WorkstationCard(
        slot = slot,
        canAffordLevelUp = state.coins >= slot.levelUpCost,
        canAffordUnlock = state.coins >= (slot.slotIndex * 1500L + 1000L),
        onLevelUp = { onLevelUpSlime(slot.slotIndex) },
        onUnlock = { onUnlockSlot(slot.slotIndex, (slot.slotIndex * 1500L + 1000L)) },
        onMutateClick = onNavigateToLab,
        onShopClick = onNavigateToShop
      )
    }
  }
}

@Composable
private fun FloatingTextEffect(particle: FloatingParticle) {
  val animY = remember { Animatable(0f) }
  val animAlpha = remember { Animatable(1f) }

  androidx.compose.runtime.LaunchedEffect(particle.id) {
    launch {
      animY.animateTo(-60f, tween(750))
    }
    launch {
      delay(300)
      animAlpha.animateTo(0f, tween(450))
    }
  }

  Text(
    text = particle.text,
    color = particle.color.copy(alpha = animAlpha.value),
    style = MaterialTheme.typography.titleMedium.copy(
      fontWeight = FontWeight.Black,
      fontSize = 18.sp
    ),
    modifier = Modifier.offset {
      IntOffset(
        x = particle.startX.dp.roundToPx(),
        y = (particle.startY + animY.value).dp.roundToPx()
      )
    }
  )
}

@Composable
private fun WorkstationCard(
  slot: SlimeSlotData,
  canAffordLevelUp: Boolean,
  canAffordUnlock: Boolean,
  onLevelUp: () -> Unit,
  onUnlock: () -> Unit,
  onMutateClick: () -> Unit,
  onShopClick: () -> Unit
) {
  val unlockCost = (slot.slotIndex * 1500L + 1000L)

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (slot.isUnlocked) Color(0xFF1E293B) else Color(0xFF0F172A)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = if (slot.isUnlocked) Color(0xFF334155) else Color(0xFF1E293B),
        shape = RoundedCornerShape(18.dp)
      )
      .testTag("workstation_card_${slot.slotIndex}")
  ) {
    if (!slot.isUnlocked) {
      // Locked Slot Card
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Zablokowany",
              tint = Color(0xFF64748B)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Stanowisko #${slot.slotIndex + 1}",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
              )
            )
            Text(
              text = "Odblokuj za ${NumberFormatter.formatCoins(unlockCost.toDouble())} monet",
              style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
            )
          }
        }

        Button(
          onClick = onUnlock,
          enabled = canAffordUnlock,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF22C55E),
            contentColor = Color.Black,
            disabledContainerColor = Color(0xFF334155),
            disabledContentColor = Color(0xFF64748B)
          ),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
          modifier = Modifier.testTag("unlock_slot_${slot.slotIndex}_button")
        ) {
          Text(
            text = "Kup 💰",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    } else {
      // Unlocked Active Slot Card
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Slime avatar canvas
            SlimeCanvas(
              slimeType = slot.slimeType,
              mutationType = slot.mutationType,
              sizeDp = 58.dp,
              interactive = false
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = slot.slimeType.displayNamePl,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF334155))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "Lvl ${slot.level}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF86EFAC)
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(2.dp))

              // Rate and mutation
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "+${NumberFormatter.formatGoo(slot.baseProduction)} Goo/s",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                  )
                )
                if (slot.mutationType != MutationType.NONE) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "• ${slot.mutationType.displayNamePl}",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(slot.mutationType.rarityColor)
                    )
                  )
                }
              }
            }
          }

          // Level Up Button
          Button(
            onClick = onLevelUp,
            enabled = canAffordLevelUp,
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF3B82F6),
              contentColor = Color.White,
              disabledContainerColor = Color(0xFF334155),
              disabledContentColor = Color(0xFF64748B)
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("upgrade_slot_${slot.slotIndex}_button")
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "ULEPSZ ⚡",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
              )
              Text(
                text = "${NumberFormatter.formatCoins(slot.levelUpCost.toDouble())} 💰",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Secondary actions: Change Slime in Shop, or Mutate in Lab
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = onMutateClick,
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF6D28D9),
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.weight(1f).height(36.dp).testTag("slot_${slot.slotIndex}_mutate_btn")
          ) {
            Text(
              text = "🧬 Mutuj w Lab",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
          }

          Button(
            onClick = onShopClick,
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF334155),
              contentColor = Color(0xFFE2E8F0)
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.weight(1f).height(36.dp).testTag("slot_${slot.slotIndex}_replace_btn")
          ) {
            Text(
              text = "🔄 Zmień Slime'a",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }
  }
}
