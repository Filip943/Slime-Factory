package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.GameStateEntity
import com.example.data.local.RebirthPerkEntity
import com.example.data.local.SlimeFactoryDatabase
import com.example.data.local.SlimeSlotEntity
import com.example.data.local.UpgradeLevelEntity
import com.example.data.local.ZoneUnlockEntity
import com.example.model.FactoryUpgradeType
import com.example.model.MutationType
import com.example.model.RebirthPerkType
import com.example.model.SlimeType
import com.example.model.ZoneType
import com.example.sound.SoundSynth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

data class SlimeSlotData(
  val slotIndex: Int,
  val slimeType: SlimeType,
  val mutationType: MutationType = MutationType.NONE,
  val level: Int = 1,
  val isUnlocked: Boolean = false
) {
  val baseProduction: Double
    get() = slimeType.baseProduction * level * mutationType.multiplier

  val levelUpCost: Long
    get() = (slimeType.baseCost * 0.6 * Math.pow(1.35, (level - 1).toDouble())).toLong()
}

data class OfflineResult(
  val elapsedSeconds: Long,
  val earnedGoo: Double,
  val earnedCoins: Double
)

data class GameUiState(
  val coins: Double = 100.0,
  val currentGoo: Double = 0.0,
  val rebirthTokens: Int = 0,
  val totalRebirths: Int = 0,
  val activeZone: ZoneType = ZoneType.GRASSLAND,
  val unlockedZoneIds: Set<String> = setOf("grassland"),
  val slimeSlots: List<SlimeSlotData> = emptyList(),
  val upgrades: Map<FactoryUpgradeType, Int> = emptyMap(),
  val rebirthPerks: Map<RebirthPerkType, Int> = emptyMap(),
  val discoveredMutations: Set<String> = setOf("basic_none"),
  val totalCoinsEarned: Double = 0.0,
  val totalGooProduced: Double = 0.0,
  val totalTaps: Long = 0L,
  val isSoundEnabled: Boolean = true,
  val offlineResult: OfflineResult? = null,
  val selectedChamberSlotIndex: Int = 0,
  val mutationMessage: String? = null
) {
  val speedUpgradeLevel: Int get() = upgrades[FactoryUpgradeType.SPEED] ?: 0
  val storageUpgradeLevel: Int get() = upgrades[FactoryUpgradeType.STORAGE] ?: 0
  val sellPriceUpgradeLevel: Int get() = upgrades[FactoryUpgradeType.SELL_PRICE] ?: 0
  val mutationUpgradeLevel: Int get() = upgrades[FactoryUpgradeType.MUTATION_CHANCE] ?: 0
  val autoSellerUpgradeLevel: Int get() = upgrades[FactoryUpgradeType.AUTO_SELLER] ?: 0

  val permProdLevel: Int get() = rebirthPerks[RebirthPerkType.PERM_PRODUCTION] ?: 0
  val permMutationLevel: Int get() = rebirthPerks[RebirthPerkType.PERM_MUTATION] ?: 0
  val permValueLevel: Int get() = rebirthPerks[RebirthPerkType.PERM_VALUE] ?: 0
  val permStorageLevel: Int get() = rebirthPerks[RebirthPerkType.STORAGE_BOOST] ?: 0
  val headstartLevel: Int get() = rebirthPerks[RebirthPerkType.HEADSTART_COINS] ?: 0

  // Total max capacity of storage tank
  val maxGooCapacity: Double
    get() {
      val base = 100.0 + (storageUpgradeLevel * 250.0)
      return base * (1.0 + permStorageLevel * 0.50)
    }

  // Goo production per second
  val productionPerSecond: Double
    get() {
      val sumSlotBases = slimeSlots.filter { it.isUnlocked }.sumOf { it.baseProduction }
      val speedMultiplier = 1.0 + (speedUpgradeLevel * 0.15)
      val rebirthMultiplier = 1.0 + (permProdLevel * 0.25)
      return sumSlotBases * speedMultiplier * activeZone.prodMultiplier * rebirthMultiplier
    }

  // Sell value per 1 Goo
  val sellPricePerGoo: Double
    get() {
      val base = 1.0 + (sellPriceUpgradeLevel * 0.20)
      val rebirthMul = 1.0 + (permValueLevel * 0.30)
      return base * activeZone.valueMultiplier * rebirthMul
    }

  // Total mutation chance in percent (e.g. 5.0 = 5%)
  val totalMutationChancePercent: Double
    get() {
      val base = 5.0
      val upgradeBonus = mutationUpgradeLevel * 2.0
      val zoneBonus = activeZone.mutationBonusPct
      val rebirthBonus = permMutationLevel * 5.0
      return (base + upgradeBonus + zoneBonus + rebirthBonus).coerceIn(1.0, 95.0)
    }

  // Tokens earned if player rebirthed right now
  val potentialRebirthTokens: Int
    get() {
      if (coins < 1_000_000.0) return 0
      return floor(sqrt(coins / 1_000_000.0) * 10).toInt()
    }
}

class SlimeGameViewModel(application: Application) : AndroidViewModel(application) {
  private val dao = SlimeFactoryDatabase.getDatabase(application).slimeFactoryDao()

  private val _uiState = MutableStateFlow(GameUiState())
  val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

  init {
    loadInitialData()
    startTickerLoop()
  }

  private fun loadInitialData() {
    viewModelScope.launch(Dispatchers.IO) {
      val savedState = dao.getGameStateOnce()
      val savedSlots = dao.getAllSlimeSlotsOnce()
      val savedUpgrades = dao.getAllUpgradesOnce()
      val savedPerks = dao.getAllRebirthPerksOnce()
      val savedZones = dao.getAllZoneUnlocksOnce()

      val now = System.currentTimeMillis()

      val defaultSlots = if (savedSlots.isEmpty()) {
        val initialSlots = listOf(
          SlimeSlotEntity(slotIndex = 0, slimeTypeId = "basic", mutationTypeId = "none", level = 1, isUnlocked = true),
          SlimeSlotEntity(slotIndex = 1, slimeTypeId = "water", mutationTypeId = "none", level = 1, isUnlocked = false),
          SlimeSlotEntity(slotIndex = 2, slimeTypeId = "basic", mutationTypeId = "none", level = 1, isUnlocked = false),
          SlimeSlotEntity(slotIndex = 3, slimeTypeId = "basic", mutationTypeId = "none", level = 1, isUnlocked = false),
          SlimeSlotEntity(slotIndex = 4, slimeTypeId = "basic", mutationTypeId = "none", level = 1, isUnlocked = false),
          SlimeSlotEntity(slotIndex = 5, slimeTypeId = "basic", mutationTypeId = "none", level = 1, isUnlocked = false)
        )
        dao.insertSlimeSlots(initialSlots)
        initialSlots
      } else {
        savedSlots
      }

      val defaultZones = if (savedZones.isEmpty()) {
        val zones = listOf(
          ZoneUnlockEntity(zoneId = "grassland", isUnlocked = true),
          ZoneUnlockEntity(zoneId = "frozen", isUnlocked = false),
          ZoneUnlockEntity(zoneId = "volcano", isUnlocked = false),
          ZoneUnlockEntity(zoneId = "toxic_zone", isUnlocked = false),
          ZoneUnlockEntity(zoneId = "space", isUnlocked = false),
          ZoneUnlockEntity(zoneId = "void", isUnlocked = false)
        )
        dao.insertZones(zones)
        zones
      } else {
        savedZones
      }

      val slotsData = defaultSlots.map {
        SlimeSlotData(
          slotIndex = it.slotIndex,
          slimeType = SlimeType.fromId(it.slimeTypeId),
          mutationType = MutationType.fromId(it.mutationTypeId),
          level = it.level,
          isUnlocked = it.isUnlocked
        )
      }

      val upgradesMap = savedUpgrades.associate {
        (FactoryUpgradeType.fromId(it.upgradeId) ?: FactoryUpgradeType.SPEED) to it.level
      }

      val perksMap = savedPerks.associate {
        (RebirthPerkType.fromId(it.perkId) ?: RebirthPerkType.PERM_PRODUCTION) to it.level
      }

      val unlockedZones = defaultZones.filter { it.isUnlocked }.map { it.zoneId }.toSet()
      val activeZone = ZoneType.fromId(savedState?.activeZoneId ?: "grassland")

      val discoveredMutations = (savedState?.discoveredMutationsCsv ?: "basic_none")
        .split(",")
        .filter { it.isNotBlank() }
        .toSet()

      var currentCoins = savedState?.coins ?: 100.0
      var currentGoo = savedState?.currentGoo ?: 0.0
      var offlineResult: OfflineResult? = null

      // Calculate offline earnings if saved state exists
      if (savedState != null) {
        val elapsedSec = min((now - savedState.lastSavedTimestamp) / 1000L, 86400L) // capped at 24h
        if (elapsedSec > 10L) {
          val tempState = GameUiState(
            coins = currentCoins,
            currentGoo = currentGoo,
            rebirthTokens = savedState.rebirthTokens,
            totalRebirths = savedState.totalRebirths,
            activeZone = activeZone,
            unlockedZoneIds = unlockedZones,
            slimeSlots = slotsData,
            upgrades = upgradesMap,
            rebirthPerks = perksMap
          )

          val rate = tempState.productionPerSecond
          val autoLevel = tempState.autoSellerUpgradeLevel
          val totalProd = rate * elapsedSec
          val maxCap = tempState.maxGooCapacity

          var offlineCoins = 0.0
          var offlineGoo = 0.0

          if (autoLevel > 0) {
            // Auto seller converts produced goo into coins
            offlineCoins = totalProd * tempState.sellPricePerGoo * 0.75
            offlineGoo = (currentGoo + totalProd * 0.25).coerceAtMost(maxCap) - currentGoo
          } else {
            val spaceLeft = (maxCap - currentGoo).coerceAtLeast(0.0)
            offlineGoo = totalProd.coerceAtMost(spaceLeft)
          }

          currentGoo = (currentGoo + offlineGoo).coerceIn(0.0, maxCap)
          currentCoins += offlineCoins

          offlineResult = OfflineResult(
            elapsedSeconds = elapsedSec,
            earnedGoo = offlineGoo,
            earnedCoins = offlineCoins
          )
        }
      }

      _uiState.update {
        it.copy(
          coins = currentCoins,
          currentGoo = currentGoo,
          rebirthTokens = savedState?.rebirthTokens ?: 0,
          totalRebirths = savedState?.totalRebirths ?: 0,
          activeZone = activeZone,
          unlockedZoneIds = unlockedZones,
          slimeSlots = slotsData,
          upgrades = upgradesMap,
          rebirthPerks = perksMap,
          discoveredMutations = discoveredMutations,
          totalCoinsEarned = savedState?.totalCoinsEarned ?: 0.0,
          totalGooProduced = savedState?.totalGooProduced ?: 0.0,
          totalTaps = savedState?.totalTaps ?: 0L,
          offlineResult = offlineResult
        )
      }
    }
  }

  private fun startTickerLoop() {
    viewModelScope.launch {
      var saveCounter = 0
      while (true) {
        delay(200L) // 5 ticks per second for smooth progression
        val dt = 0.2 // 0.2 seconds

        _uiState.update { state ->
          val rate = state.productionPerSecond
          val produced = rate * dt
          val newGoo = (state.currentGoo + produced).coerceAtMost(state.maxGooCapacity)

          var coins = state.coins
          var currentGooAfterAutoSell = newGoo
          val autoSeller = state.autoSellerUpgradeLevel

          // Automated selling
          if (autoSeller > 0 && currentGooAfterAutoSell > 0.0) {
            val sellFractionPerSecond = 0.12 * autoSeller
            val sellAmount = currentGooAfterAutoSell * (sellFractionPerSecond * dt)
            val coinEarned = sellAmount * state.sellPricePerGoo
            currentGooAfterAutoSell = (currentGooAfterAutoSell - sellAmount).coerceAtLeast(0.0)
            coins += coinEarned
          }

          state.copy(
            coins = coins,
            currentGoo = currentGooAfterAutoSell,
            totalGooProduced = state.totalGooProduced + produced
          )
        }

        saveCounter++
        if (saveCounter >= 25) { // Auto-save to Room every ~5 seconds
          saveCounter = 0
          saveCurrentState()
        }
      }
    }
  }

  fun dismissOfflineDialog() {
    _uiState.update { it.copy(offlineResult = null) }
  }

  fun toggleSound() {
    _uiState.update {
      val next = !it.isSoundEnabled
      SoundSynth.isSoundEnabled = next
      it.copy(isSoundEnabled = next)
    }
  }

  // Tap on the main slime for instant manual Goo & squish!
  fun tapSlime(): Double {
    val state = _uiState.value
    // Tap power scales with highest slime and tap bonus
    val mainSlime = state.slimeSlots.firstOrNull { it.isUnlocked }
    val baseGain = (mainSlime?.baseProduction ?: 1.0) * 1.5
    val manualGoo = baseGain.coerceAtLeast(1.0)

    SoundSynth.playSlimeSquish()

    _uiState.update {
      val nextGoo = (it.currentGoo + manualGoo).coerceAtMost(it.maxGooCapacity)
      it.copy(
        currentGoo = nextGoo,
        totalTaps = it.totalTaps + 1,
        totalGooProduced = it.totalGooProduced + manualGoo
      )
    }
    return manualGoo
  }

  // Sell all stored Goo for Coins!
  fun sellGoo() {
    val state = _uiState.value
    if (state.currentGoo <= 0.0) return

    val totalCoinValue = state.currentGoo * state.sellPricePerGoo
    SoundSynth.playCoinSound()

    _uiState.update {
      it.copy(
        coins = it.coins + totalCoinValue,
        currentGoo = 0.0,
        totalCoinsEarned = it.totalCoinsEarned + totalCoinValue
      )
    }
    saveCurrentState()
  }

  // Buy or level up a factory upgrade
  fun buyFactoryUpgrade(upgradeType: FactoryUpgradeType): Boolean {
    val state = _uiState.value
    val currentLevel = state.upgrades[upgradeType] ?: 0
    if (currentLevel >= upgradeType.maxLevel) return false

    val cost = upgradeType.calculateCost(currentLevel)
    if (state.coins < cost) return false

    SoundSynth.playUpgradeSound()

    _uiState.update {
      val newUpgrades = it.upgrades.toMutableMap()
      newUpgrades[upgradeType] = currentLevel + 1

      // If slot expansion upgrade, unlock next slot!
      val updatedSlots = if (upgradeType == FactoryUpgradeType.SLOT_EXPANSION) {
        val nextSlotToUnlock = it.slimeSlots.indexOfFirst { slot -> !slot.isUnlocked }
        if (nextSlotToUnlock != -1) {
          it.slimeSlots.mapIndexed { idx, slot ->
            if (idx == nextSlotToUnlock) slot.copy(isUnlocked = true) else slot
          }
        } else it.slimeSlots
      } else it.slimeSlots

      it.copy(
        coins = it.coins - cost,
        upgrades = newUpgrades,
        slimeSlots = updatedSlots
      )
    }
    saveCurrentState()
    return true
  }

  // Buy new slime for a slot
  fun buySlimeForSlot(slotIndex: Int, slimeType: SlimeType): Boolean {
    val state = _uiState.value
    if (state.coins < slimeType.baseCost) return false

    SoundSynth.playUpgradeSound()

    _uiState.update {
      val updatedSlots = it.slimeSlots.map { slot ->
        if (slot.slotIndex == slotIndex) {
          slot.copy(
            slimeType = slimeType,
            level = 1,
            mutationType = MutationType.NONE,
            isUnlocked = true
          )
        } else slot
      }

      val newDiscovered = it.discoveredMutations + "${slimeType.id}_none"

      it.copy(
        coins = it.coins - slimeType.baseCost,
        slimeSlots = updatedSlots,
        discoveredMutations = newDiscovered
      )
    }
    saveCurrentState()
    return true
  }

  // Level up a slime inside a slot
  fun levelUpSlime(slotIndex: Int): Boolean {
    val state = _uiState.value
    val slot = state.slimeSlots.find { it.slotIndex == slotIndex } ?: return false
    val cost = slot.levelUpCost
    if (state.coins < cost) return false

    SoundSynth.playUpgradeSound()

    _uiState.update {
      val updatedSlots = it.slimeSlots.map { s ->
        if (s.slotIndex == slotIndex) s.copy(level = s.level + 1) else s
      }
      it.copy(
        coins = it.coins - cost,
        slimeSlots = updatedSlots
      )
    }
    saveCurrentState()
    return true
  }

  // Unlock next factory slot
  fun unlockSlot(slotIndex: Int, cost: Long): Boolean {
    val state = _uiState.value
    if (state.coins < cost) return false

    SoundSynth.playUpgradeSound()

    _uiState.update {
      val updatedSlots = it.slimeSlots.map { s ->
        if (s.slotIndex == slotIndex) s.copy(isUnlocked = true) else s
      }
      it.copy(
        coins = it.coins - cost,
        slimeSlots = updatedSlots
      )
    }
    saveCurrentState()
    return true
  }

  // Mutation Lab selection & synthesis
  fun selectChamberSlot(slotIndex: Int) {
    _uiState.update { it.copy(selectedChamberSlotIndex = slotIndex, mutationMessage = null) }
  }

  // Mutate Slime in the Chamber!
  fun attemptMutation(): Boolean {
    val state = _uiState.value
    val slot = state.slimeSlots.find { it.slotIndex == state.selectedChamberSlotIndex } ?: return false
    if (!slot.isUnlocked) return false

    // Catalyst cost scales with slime tier
    val catalystCost = (slot.slimeType.baseCost * 0.4).toLong().coerceAtLeast(150L)
    if (state.coins < catalystCost) return false

    val chance = state.totalMutationChancePercent
    val roll = (0..10000).random() / 100.0 // 0.0 to 100.0

    val isSuccess = roll <= chance

    val newMutation = if (isSuccess) {
      // Pick mutation tier according to weights
      val tierRoll = (0..100).random()
      when {
        tierRoll > 96 -> MutationType.QUANTUM
        tierRoll > 85 -> MutationType.RAINBOW
        tierRoll > 60 -> MutationType.VOID
        else -> MutationType.CRYSTAL
      }
    } else {
      slot.mutationType
    }

    if (isSuccess && newMutation != slot.mutationType) {
      SoundSynth.playMutationSound()
    } else {
      SoundSynth.playSlimeSquish()
    }

    _uiState.update { curState ->
      val updatedSlots = curState.slimeSlots.map { s ->
        if (s.slotIndex == state.selectedChamberSlotIndex) {
          s.copy(mutationType = newMutation)
        } else s
      }

      val mutationKey = "${slot.slimeType.id}_${newMutation.id}"
      val newDiscovered = if (isSuccess) curState.discoveredMutations + mutationKey else curState.discoveredMutations

      val msg = if (isSuccess && newMutation != slot.mutationType) {
        "🧬 SUKCES! Slime zmutował w formę: ${newMutation.displayNamePl} (${newMutation.rarityNamePl})!"
      } else {
        "💨 Eksperyment niestabilny! DNA slime'a nie zmieniło się tym razem."
      }

      curState.copy(
        coins = curState.coins - catalystCost,
        slimeSlots = updatedSlots,
        discoveredMutations = newDiscovered,
        mutationMessage = msg
      )
    }
    saveCurrentState()
    return isSuccess
  }

  // Change or unlock Biomes / Zones
  fun selectZone(zone: ZoneType) {
    val state = _uiState.value
    if (!state.unlockedZoneIds.contains(zone.id)) {
      // Try to unlock zone
      if (state.coins < zone.unlockCost) return
      SoundSynth.playRebirthSound()
      _uiState.update {
        it.copy(
          coins = it.coins - zone.unlockCost,
          unlockedZoneIds = it.unlockedZoneIds + zone.id,
          activeZone = zone
        )
      }
    } else {
      // Switch active zone
      _uiState.update { it.copy(activeZone = zone) }
    }
    saveCurrentState()
  }

  // Prestiż / Rebirth
  fun performRebirth(): Boolean {
    val state = _uiState.value
    val tokens = state.potentialRebirthTokens
    if (tokens <= 0) return false

    SoundSynth.playRebirthSound()

    _uiState.update { curState ->
      val headstartCoins = 100.0 + (curState.headstartLevel * 1000.0)

      // Reset regular slimes to initial Basic Slime
      val resetSlots = listOf(
        SlimeSlotData(slotIndex = 0, slimeType = SlimeType.BASIC, level = 1, mutationType = MutationType.NONE, isUnlocked = true),
        SlimeSlotData(slotIndex = 1, slimeType = SlimeType.BASIC, level = 1, mutationType = MutationType.NONE, isUnlocked = false),
        SlimeSlotData(slotIndex = 2, slimeType = SlimeType.BASIC, level = 1, mutationType = MutationType.NONE, isUnlocked = false),
        SlimeSlotData(slotIndex = 3, slimeType = SlimeType.BASIC, level = 1, mutationType = MutationType.NONE, isUnlocked = false),
        SlimeSlotData(slotIndex = 4, slimeType = SlimeType.BASIC, level = 1, mutationType = MutationType.NONE, isUnlocked = false),
        SlimeSlotData(slotIndex = 5, slimeType = SlimeType.BASIC, level = 1, mutationType = MutationType.NONE, isUnlocked = false)
      )

      curState.copy(
        coins = headstartCoins,
        currentGoo = 0.0,
        rebirthTokens = curState.rebirthTokens + tokens,
        totalRebirths = curState.totalRebirths + 1,
        activeZone = ZoneType.GRASSLAND,
        unlockedZoneIds = setOf("grassland"),
        slimeSlots = resetSlots,
        upgrades = emptyMap(), // factory upgrades reset, rebirth perks stay permanent!
        mutationMessage = "🌟 REBIRTH ZAKOŃCZONY! Otrzymano $tokens Rebirth Tokens!"
      )
    }
    saveCurrentState()
    return true
  }

  // Buy Permanent Rebirth Perk
  fun buyRebirthPerk(perk: RebirthPerkType): Boolean {
    val state = _uiState.value
    val currentLevel = state.rebirthPerks[perk] ?: 0
    if (currentLevel >= perk.maxLevel) return false
    if (state.rebirthTokens < perk.costPerLevel) return false

    SoundSynth.playUpgradeSound()

    _uiState.update { curState ->
      val newPerks = curState.rebirthPerks.toMutableMap()
      newPerks[perk] = currentLevel + 1
      curState.copy(
        rebirthTokens = curState.rebirthTokens - perk.costPerLevel,
        rebirthPerks = newPerks
      )
    }
    saveCurrentState()
    return true
  }

  private fun saveCurrentState() {
    val state = _uiState.value
    viewModelScope.launch(Dispatchers.IO) {
      val entity = GameStateEntity(
        id = 1,
        coins = state.coins,
        currentGoo = state.currentGoo,
        rebirthTokens = state.rebirthTokens,
        totalRebirths = state.totalRebirths,
        activeZoneId = state.activeZone.id,
        totalCoinsEarned = state.totalCoinsEarned,
        totalGooProduced = state.totalGooProduced,
        totalTaps = state.totalTaps,
        totalMutationsDiscovered = state.discoveredMutations.size,
        discoveredMutationsCsv = state.discoveredMutations.joinToString(","),
        lastSavedTimestamp = System.currentTimeMillis()
      )
      dao.insertOrUpdateGameState(entity)

      // Save slots
      val slotEntities = state.slimeSlots.map {
        SlimeSlotEntity(
          slotIndex = it.slotIndex,
          slimeTypeId = it.slimeType.id,
          mutationTypeId = it.mutationType.id,
          level = it.level,
          isUnlocked = it.isUnlocked
        )
      }
      dao.insertSlimeSlots(slotEntities)

      // Save upgrades
      state.upgrades.forEach { (type, level) ->
        dao.insertOrUpdateUpgrade(UpgradeLevelEntity(upgradeId = type.id, level = level))
      }

      // Save rebirth perks
      state.rebirthPerks.forEach { (perk, level) ->
        dao.insertOrUpdateRebirthPerk(RebirthPerkEntity(perkId = perk.id, level = level))
      }

      // Save zones
      state.unlockedZoneIds.forEach { zoneId ->
        dao.insertOrUpdateZone(ZoneUnlockEntity(zoneId = zoneId, isUnlocked = true))
      }
    }
  }
}
