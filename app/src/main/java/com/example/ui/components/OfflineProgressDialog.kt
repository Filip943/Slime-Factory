package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.util.NumberFormatter

@Composable
fun OfflineProgressDialog(
  elapsedSeconds: Long,
  earnedGoo: Double,
  earnedCoins: Double,
  onDismiss: () -> Unit
) {
  val hours = elapsedSeconds / 3600
  val minutes = (elapsedSeconds % 3600) / 60
  val seconds = elapsedSeconds % 60
  val timeFormatted = when {
    hours > 0 -> "${hours}h ${minutes}m"
    minutes > 0 -> "${minutes}m ${seconds}s"
    else -> "${seconds}s"
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
      modifier = Modifier
        .fillMaxWidth()
        .border(2.dp, Color(0xFF4ADE80), RoundedCornerShape(20.dp))
        .padding(4.dp)
        .testTag("offline_progress_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = "🏭", fontSize = 48.sp)

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Witaj z powrotem!",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Black,
            color = Color.White
          ),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Twoja fabryka ciężko pracowała podczas Twojej nieobecności przez $timeFormatted.",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = Color(0xFF94A3B8)
          ),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Earnings box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            .padding(16.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🧪", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Wyprodukowane Goo:",
                  style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )
              }
              Text(
                text = "+${NumberFormatter.formatGoo(earnedGoo)}",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF4ADE80)
                )
              )
            }

            if (earnedCoins > 0.0) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = "💰", fontSize = 18.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Auto-sprzedaż monet:",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                  )
                }
                Text(
                  text = "+${NumberFormatter.formatCoins(earnedCoins)}",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFBBF24)
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF22C55E),
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("collect_offline_earnings_button")
        ) {
          Text(
            text = "ODBIERZ ZYSKI! 🚀",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            )
          )
        }
      }
    }
  }
}
