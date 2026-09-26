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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.model.RebirthPerkType
import com.example.ui.GameUiState
import com.example.util.NumberFormatter

@Composable
fun RebirthScreen(
  state: GameUiState,
  onPerformRebirth: () -> Boolean,
  onBuyPerk: (RebirthPerkType) -> Boolean,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  var showConfirmDialog by remember { mutableStateOf(false) }

  val targetCoins = 1_000_000.0
  val progress = (state.coins / targetCoins).coerceIn(0.0, 1.0).toFloat()
  val tokensToEarn = state.potentialRebirthTokens
  val canRebirth = tokensToEarn > 0

  if (showConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showConfirmDialog = false },
      title = {
        Text(
          text = "♻️ Czy na pewno chcesz wykonać Rebirth?",
          fontWeight = FontWeight.Black,
          color = Color.White
        )
      },
      text = {
        Text(
          text = "Zresetujesz swoje monety, stan magazynu Goo oraz podstawowe stanowiska.\n\n" +
            "W zamian otrzymasz +$tokensToEarn Rebirth Tokens, które odblokowują potężne, STAŁE ulepszenia!",
          color = Color(0xFFCBD5E1)
        )
      },
      containerColor = Color(0xFF1E293B),
      confirmButton = {
        Button(
          onClick = {
            showConfirmDialog = false
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            onPerformRebirth()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7), contentColor = Color.White),
          modifier = Modifier.testTag("confirm_rebirth_button")
        ) {
          Text(text = "POTWIERDZAM REBIRTH! 🚀", fontWeight = FontWeight.Black)
        }
      },
      dismissButton = {
        TextButton(onClick = { showConfirmDialog = false }) {
          Text(text = "Anuluj", color = Color(0xFF94A3B8))
        }
      }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("rebirth_screen"),
    contentPadding = PaddingValues(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Rebirth Portal Header Card
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1065)),
        modifier = Modifier
          .fillMaxWidth()
          .border(2.dp, Color(0xFFA855F7), RoundedCornerShape(22.dp))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(text = "♻️", fontSize = 42.sp)

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Kwantowy Rebirth Fabryki",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              color = Color.White
            )
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Wykonano Rebirthów: ${state.totalRebirths} • Twoje Tokeny: ${state.rebirthTokens} 🟣",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color(0xFFE9D5FF)
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Progress to 1,000,000 coins
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Cel na Rebirth:",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD8B4FE))
              )
              Text(
                text = "${NumberFormatter.formatCoins(state.coins)} / 1.00M",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFFBBF24)
                )
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
              color = Color(0xFFC084FC),
              trackColor = Color(0xFF3B0764)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Rebirth Action Button
          Button(
            onClick = {
              if (canRebirth) showConfirmDialog = true
            },
            enabled = canRebirth,
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFA855F7),
              contentColor = Color.White,
              disabledContainerColor = Color(0xFF3B0764),
              disabledContentColor = Color(0xFF7E22CE)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("rebirth_action_button")
          ) {
            Text(
              text = if (canRebirth) "WYKONAJ REBIRTH (+${tokensToEarn} TOKENÓW) 🚀" else "WYMAGA 1 000 000 MONET 🔒",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
            )
          }
        }
      }
    }

    // 2. Permanent Rebirth Skill Tree
    item {
      Text(
        text = "🔮 Stałe Drzewko Prestiżu (Rebirth Perks)",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Black,
          color = Color.White
        )
      )
    }

    items(RebirthPerkType.entries) { perk ->
      val currentLevel = state.rebirthPerks[perk] ?: 0
      val isMax = currentLevel >= perk.maxLevel
      val canAfford = state.rebirthTokens >= perk.costPerLevel && !isMax

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, Color(0xFF475569), RoundedCornerShape(16.dp))
          .testTag("rebirth_perk_card_${perk.id}")
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
            Text(text = perk.emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = perk.titlePl,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF3B0764))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "Lvl $currentLevel / ${perk.maxLevel}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFE9D5FF)
                    )
                  )
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = perk.descriptionPl,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = {
              haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
              onBuyPerk(perk)
            },
            enabled = canAfford,
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFA855F7),
              contentColor = Color.White,
              disabledContainerColor = Color(0xFF334155),
              disabledContentColor = Color(0xFF64748B)
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            modifier = Modifier.testTag("buy_perk_${perk.id}_button")
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = if (isMax) "MAX" else "KUP",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
              )
              if (!isMax) {
                Text(
                  text = "${perk.costPerLevel} 🟣",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                )
              }
            }
          }
        }
      }
    }

    // 3. Stats Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, Color(0xFF334155), RoundedCornerShape(18.dp))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "📊 Statystyki Kariery",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              color = Color.White
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          StatRow("Całkowite zarobione monety:", "${NumberFormatter.formatCoins(state.totalCoinsEarned)} 💰")
          StatRow("Całkowicie wyprodukowane Goo:", "${NumberFormatter.formatGoo(state.totalGooProduced)} 🧪")
          StatRow("Wyciśnięte ręcznie slime'y (Tapnięcia):", "${state.totalTaps} 👆")
          StatRow("Odkryte formy ewolucji:", "${state.discoveredMutations.size} 🧬")
        }
      }
    }
  }
}

@Composable
private fun StatRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8)))
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    )
  }
}
