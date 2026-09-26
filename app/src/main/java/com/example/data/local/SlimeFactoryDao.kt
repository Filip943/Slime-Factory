package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SlimeFactoryDao {
  @Query("SELECT * FROM game_state WHERE id = 1 LIMIT 1")
  fun getGameStateFlow(): Flow<GameStateEntity?>

  @Query("SELECT * FROM game_state WHERE id = 1 LIMIT 1")
  suspend fun getGameStateOnce(): GameStateEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateGameState(state: GameStateEntity)

  @Query("SELECT * FROM slime_slots ORDER BY slotIndex ASC")
  fun getAllSlimeSlotsFlow(): Flow<List<SlimeSlotEntity>>

  @Query("SELECT * FROM slime_slots ORDER BY slotIndex ASC")
  suspend fun getAllSlimeSlotsOnce(): List<SlimeSlotEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateSlimeSlot(slot: SlimeSlotEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSlimeSlots(slots: List<SlimeSlotEntity>)

  @Query("SELECT * FROM upgrade_levels")
  fun getAllUpgradesFlow(): Flow<List<UpgradeLevelEntity>>

  @Query("SELECT * FROM upgrade_levels")
  suspend fun getAllUpgradesOnce(): List<UpgradeLevelEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateUpgrade(upgrade: UpgradeLevelEntity)

  @Query("SELECT * FROM rebirth_perks")
  fun getAllRebirthPerksFlow(): Flow<List<RebirthPerkEntity>>

  @Query("SELECT * FROM rebirth_perks")
  suspend fun getAllRebirthPerksOnce(): List<RebirthPerkEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateRebirthPerk(perk: RebirthPerkEntity)

  @Query("SELECT * FROM zone_unlocks")
  fun getAllZoneUnlocksFlow(): Flow<List<ZoneUnlockEntity>>

  @Query("SELECT * FROM zone_unlocks")
  suspend fun getAllZoneUnlocksOnce(): List<ZoneUnlockEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateZone(zone: ZoneUnlockEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertZones(zones: List<ZoneUnlockEntity>)
}
