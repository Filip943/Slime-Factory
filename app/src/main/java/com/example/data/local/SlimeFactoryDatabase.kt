package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    GameStateEntity::class,
    SlimeSlotEntity::class,
    UpgradeLevelEntity::class,
    RebirthPerkEntity::class,
    ZoneUnlockEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class SlimeFactoryDatabase : RoomDatabase() {
  abstract fun slimeFactoryDao(): SlimeFactoryDao

  companion object {
    @Volatile
    private var INSTANCE: SlimeFactoryDatabase? = null

    fun getDatabase(context: Context): SlimeFactoryDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          SlimeFactoryDatabase::class.java,
          "slime_factory_db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
