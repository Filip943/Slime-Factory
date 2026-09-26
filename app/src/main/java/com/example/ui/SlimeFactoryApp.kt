package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.OfflineProgressDialog
import com.example.ui.components.TopBarStats
import com.example.ui.screens.FactoryScreen
import com.example.ui.screens.MutationLabScreen
import com.example.ui.screens.RebirthScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.ZonesScreen

enum class FactoryTab(val title: String, val iconEmoji: String, val testTagId: String) {
  FACTORY("Fabryka", "🏭", "nav_factory"),
  SHOP("Sklep", "🛒", "nav_shop"),
  LAB("Mutacje", "🧬", "nav_lab"),
  ZONES("Strefy", "🌎", "nav_zones"),
  REBIRTH("Rebirth", "♻️", "nav_rebirth")
}

@Composable
fun SlimeFactoryApp(
  viewModel: SlimeGameViewModel = viewModel()
) {
  val state by viewModel.uiState.collectAsState()
  var currentTab by remember { mutableIntStateOf(0) }

  // Offline earnings modal if returning after time away
  if (state.offlineResult != null) {
    OfflineProgressDialog(
      elapsedSeconds = state.offlineResult!!.elapsedSeconds,
      earnedGoo = state.offlineResult!!.earnedGoo,
      earnedCoins = state.offlineResult!!.earnedCoins,
      onDismiss = { viewModel.dismissOfflineDialog() }
    )
  }

  Scaffold(
    contentWindowInsets = WindowInsets(0.dp),
    topBar = {
      Box(modifier = Modifier.padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())) {
        TopBarStats(
          coins = state.coins,
          goo = state.currentGoo,
          prodRatePerSec = state.productionPerSecond,
          rebirthTokens = state.rebirthTokens,
          activeZone = state.activeZone,
          isSoundEnabled = state.isSoundEnabled,
          onToggleSound = { viewModel.toggleSound() },
          onZoneClick = { currentTab = FactoryTab.ZONES.ordinal }
        )
      }
    },
    bottomBar = {
      Box(modifier = Modifier.padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())) {
        NavigationBar(
          containerColor = Color(0xFF0F172A),
          tonalElevation = 8.dp,
          modifier = Modifier.fillMaxWidth().testTag("bottom_navigation_bar")
        ) {
          FactoryTab.entries.forEachIndexed { index, tab ->
            val isSelected = currentTab == index
            NavigationBarItem(
              selected = isSelected,
              onClick = { currentTab = index },
              icon = {
                Text(
                  text = tab.iconEmoji,
                  fontSize = if (isSelected) 22.sp else 18.sp
                )
              },
              label = {
                Text(
                  text = tab.title,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                  fontSize = 11.sp
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF4ADE80),
                selectedTextColor = Color(0xFF4ADE80),
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B),
                indicatorColor = Color(0xFF1E293B)
              ),
              modifier = Modifier.testTag(tab.testTagId)
            )
          }
        }
      }
    },
    containerColor = Color(state.activeZone.backgroundColor)
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(Color(state.activeZone.backgroundColor))
    ) {
      Crossfade(targetState = currentTab, label = "tab_crossfade") { tabIdx ->
        when (FactoryTab.entries[tabIdx]) {
          FactoryTab.FACTORY -> {
            FactoryScreen(
              state = state,
              onTapSlime = { viewModel.tapSlime() },
              onSellGoo = { viewModel.sellGoo() },
              onLevelUpSlime = { slotIdx -> viewModel.levelUpSlime(slotIdx) },
              onUnlockSlot = { slotIdx, cost -> viewModel.unlockSlot(slotIdx, cost) },
              onNavigateToShop = { currentTab = FactoryTab.SHOP.ordinal },
              onNavigateToLab = { currentTab = FactoryTab.LAB.ordinal }
            )
          }
          FactoryTab.SHOP -> {
            ShopScreen(
              state = state,
              onBuyUpgrade = { upgrade -> viewModel.buyFactoryUpgrade(upgrade) },
              onBuySlime = { slotIdx, slime -> viewModel.buySlimeForSlot(slotIdx, slime) }
            )
          }
          FactoryTab.LAB -> {
            MutationLabScreen(
              state = state,
              onSelectSlot = { slotIdx -> viewModel.selectChamberSlot(slotIdx) },
              onAttemptMutation = { viewModel.attemptMutation() }
            )
          }
          FactoryTab.ZONES -> {
            ZonesScreen(
              state = state,
              onSelectZone = { zone -> viewModel.selectZone(zone) }
            )
          }
          FactoryTab.REBIRTH -> {
            RebirthScreen(
              state = state,
              onPerformRebirth = { viewModel.performRebirth() },
              onBuyPerk = { perk -> viewModel.buyRebirthPerk(perk) }
            )
          }
        }
      }
    }
  }
}
