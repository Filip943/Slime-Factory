package com.example.ui.screens

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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FactoryUpgradeType
import com.example.model.SlimeType
import com.example.ui.GameUiState
import com.example.ui.components.SlimeCanvas
import com.example.util.NumberFormatter

@Composable
fun ShopScreen(
  state: GameUiState,
  onBuyUpgrade: (FactoryUpgradeType) -> Boolean,
  onBuySlime: (Int, SlimeType) -> Boolean,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var targetSlotIndex by remember { mutableIntStateOf(0) }
  val haptic = LocalHapticFeedback.current

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("shop_screen")
  ) {
    // Top Tabs: Ulepszenia vs Nowe Slime'y
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color(0xFF0F172A),
      contentColor = Color(0xFF4ADE80),
      modifier = Modifier.fillMaxWidth()
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Text(
            text = "⚡ ULEPSZENIA MASZYN",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        },
        modifier = Modifier.testTag("tab_upgrades")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            text = "🧪 NOWE SLIME'Y",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        },
        modifier = Modifier.testTag("tab_slimes")
      )
    }

    if (selectedTab == 0) {
      // Upgrades List
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(FactoryUpgradeType.entries) { upgrade ->
          val currentLevel = state.upgrades[upgrade] ?: 0
          val cost = upgrade.calculateCost(currentLevel)
          val isMax = currentLevel >= upgrade.maxLevel
          val canAfford = state.coins >= cost && !isMax

          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
              .testTag("upgrade_card_${upgrade.id}")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = upgrade.emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = upgrade.titlePl,
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
                        text = if (isMax) "MAX" else "Poziom $currentLevel",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontWeight = FontWeight.Bold,
                          color = if (isMax) Color(0xFFFBBF24) else Color(0xFF38BDF8)
                        )
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = upgrade.descriptionPl,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                  )
                }
              }

              Spacer(modifier = Modifier.width(8.dp))

              Button(
                onClick = {
                  haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                  onBuyUpgrade(upgrade)
                },
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF22C55E),
                  contentColor = Color.Black,
                  disabledContainerColor = Color(0xFF334155),
                  disabledContentColor = Color(0xFF64748B)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier.testTag("buy_upgrade_${upgrade.id}_button")
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = if (isMax) "MAX" else "KUP",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                  )
                  if (!isMax) {
                    Text(
                      text = "${NumberFormatter.formatCoins(cost.toDouble())} 💰",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    } else {
      // Slimes Adoption Market
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Target slot picker
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "🎯 Wybierz stanowisko docelowe:",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              )
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                state.slimeSlots.filter { it.isUnlocked }.forEach { slot ->
                  val isSelected = targetSlotIndex == slot.slotIndex
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) Color(0xFF22C55E) else Color(0xFF1E293B))
                      .border(
                        1.dp,
                        if (isSelected) Color.White else Color(0xFF334155),
                        RoundedCornerShape(8.dp)
                      )
                      .clickable { targetSlotIndex = slot.slotIndex }
                      .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "#${slot.slotIndex + 1} ${slot.slimeType.emoji}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.Black else Color.White
                      )
                    )
                  }
                }
              }
            }
          }
        }

        // List of all Slime Breeds
        items(SlimeType.entries) { slime ->
          val canAfford = state.coins >= slime.baseCost
          val currentSlimeInSlot = state.slimeSlots.find { it.slotIndex == targetSlotIndex }?.slimeType

          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, Color(slime.primaryColor).copy(alpha = 0.4f), RoundedCornerShape(18.dp))
              .testTag("slime_market_card_${slime.id}")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              SlimeCanvas(
                slimeType = slime,
                sizeDp = 64.dp,
                interactive = false
              )

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "${slime.emoji} ${slime.displayNamePl}",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = Color.White
                    )
                  )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = "⚡ +${NumberFormatter.formatGoo(slime.baseProduction)} Goo/s",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                  )
                )

                Text(
                  text = slime.description,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                  ),
                  maxLines = 2
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              Button(
                onClick = {
                  haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                  onBuySlime(targetSlotIndex, slime)
                },
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF22C55E),
                  contentColor = Color.Black,
                  disabledContainerColor = Color(0xFF334155),
                  disabledContentColor = Color(0xFF64748B)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.testTag("buy_slime_${slime.id}_button")
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = if (currentSlimeInSlot == slime) "POSIADASZ" else "UMIEŚĆ",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
                  )
                  Text(
                    text = "${NumberFormatter.formatCoins(slime.baseCost.toDouble())} 💰",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
