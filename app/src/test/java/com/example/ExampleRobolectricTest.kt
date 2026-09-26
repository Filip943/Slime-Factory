package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.FactoryUpgradeType
import com.example.model.MutationType
import com.example.model.SlimeType
import com.example.model.ZoneType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Slime Factory", appName)
  }

  @Test
  fun `verify slime types and production base calculations`() {
    assertEquals(1.0, SlimeType.BASIC.baseProduction, 0.001)
    assertEquals(4.0, SlimeType.WATER.baseProduction, 0.001)
    assertEquals(15.0, SlimeType.FIRE.baseProduction, 0.001)
    assertEquals(70.0, SlimeType.TOXIC.baseProduction, 0.001)
    assertEquals(350.0, SlimeType.GOLDEN.baseProduction, 0.001)

    assertEquals(3.0, MutationType.CRYSTAL.multiplier, 0.001)
    assertEquals(10.0, MutationType.VOID.multiplier, 0.001)
    assertEquals(50.0, MutationType.RAINBOW.multiplier, 0.001)
    assertEquals(200.0, MutationType.QUANTUM.multiplier, 0.001)
  }

  @Test
  fun `verify factory upgrades cost progression`() {
    val speedBase = FactoryUpgradeType.SPEED.calculateCost(0)
    val speedLevel1 = FactoryUpgradeType.SPEED.calculateCost(1)
    assertTrue(speedLevel1 > speedBase)
  }

  @Test
  fun `verify biomes and multipliers`() {
    assertEquals(1.0, ZoneType.GRASSLAND.prodMultiplier, 0.001)
    assertTrue(ZoneType.VOLCANO.prodMultiplier > ZoneType.GRASSLAND.prodMultiplier)
  }
}
