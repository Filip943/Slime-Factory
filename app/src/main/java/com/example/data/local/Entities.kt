package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_state")
data class GameStateEntity(
  @PrimaryKey val id: Int = 1,
  val coins: Double = 100.0,
  val currentGoo: Double = 0.0,
  val rebirthTokens: Int = 0,
  val totalRebirths: Int = 0,
  val activeZoneId: String = "grassland",
  val totalCoinsEarned: Double = 0.0,
  val totalGooProduced: Double = 0.0,
  val totalTaps: Long = 0L,
  val totalMutationsDiscovered: Int = 1,
  val discoveredMutationsCsv: String = "basic_none",
  val lastSavedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "slime_slots")
data class SlimeSlotEntity(
  @PrimaryKey val slotIndex: Int,
  val slimeTypeId: String,
  val mutationTypeId: String = "none",
  val level: Int = 1,
  val isUnlocked: Boolean = false
)

@Entity(tableName = "upgrade_levels")
data class UpgradeLevelEntity(
  @PrimaryKey val upgradeId: String,
  val level: Int = 0
)

@Entity(tableName = "rebirth_perks")
data class RebirthPerkEntity(
  @PrimaryKey val perkId: String,
  val level: Int = 0
)

@Entity(tableName = "zone_unlocks")
data class ZoneUnlockEntity(
  @PrimaryKey val zoneId: String,
  val isUnlocked: Boolean = false
)
