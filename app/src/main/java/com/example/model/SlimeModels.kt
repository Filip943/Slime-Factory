package com.example.model

import androidx.compose.ui.graphics.Color

enum class SlimeType(
  val id: String,
  val displayNamePl: String,
  val displayNameEn: String,
  val emoji: String,
  val baseProduction: Double, // Goo per second
  val baseCost: Long,
  val primaryColor: Long,
  val secondaryColor: Long,
  val glowColor: Long,
  val description: String
) {
  BASIC(
    id = "basic",
    displayNamePl = "Zwykły Slime",
    displayNameEn = "Basic Slime",
    emoji = "🟢",
    baseProduction = 1.0,
    baseCost = 100L,
    primaryColor = 0xFF4ADE80,
    secondaryColor = 0xFF16A34A,
    glowColor = 0x664ADE80,
    description = "Początkowy, uroczy zielony glutek. Produkuje stabilne ilości zielonego Goo."
  ),
  WATER(
    id = "water",
    displayNamePl = "Wodny Slime",
    displayNameEn = "Water Slime",
    emoji = "🔵",
    baseProduction = 4.0,
    baseCost = 750L,
    primaryColor = 0xFF38BDF8,
    secondaryColor = 0xFF0284C7,
    glowColor = 0x6638BDF8,
    description = "Krystalicznie czysty i chłodny. Nawadnia maszyny i daje 4x więcej Goo."
  ),
  FIRE(
    id = "fire",
    displayNamePl = "Ognisty Slime",
    displayNameEn = "Fire Slime",
    emoji = "🔴",
    baseProduction = 15.0,
    baseCost = 5_000L,
    primaryColor = 0xFFF87171,
    secondaryColor = 0xFFDC2626,
    glowColor = 0x66F87171,
    description = "Gorący i dynamiczny. Podgrzewa rury fabryczne, generując 15 Goo/s."
  ),
  TOXIC(
    id = "toxic",
    displayNamePl = "Toksyczny Slime",
    displayNameEn = "Toxic Slime",
    emoji = "🟣",
    baseProduction = 70.0,
    baseCost = 50_000L,
    primaryColor = 0xFFA855F7,
    secondaryColor = 0xFF7E22CE,
    glowColor = 0x66A855F7,
    description = "Świeci w ciemności radioaktywnym fioletem. Generuje 70 Goo/s."
  ),
  GOLDEN(
    id = "golden",
    displayNamePl = "Złoty Slime",
    displayNameEn = "Golden Slime",
    emoji = "🟡",
    baseProduction = 350.0,
    baseCost = 500_000L,
    primaryColor = 0xFFFBBF24,
    secondaryColor = 0xFFD97706,
    glowColor = 0x66FBBF24,
    description = "Legendarny ociekający złotem glut. Wytwarza aż 350 czystego luksusowego Goo/s."
  ),
  FROST(
    id = "frost",
    displayNamePl = "Lodowy Slime",
    displayNameEn = "Frost Slime",
    emoji = "❄️",
    baseProduction = 1_800.0,
    baseCost = 2_500_000L,
    primaryColor = 0xFFE0F2FE,
    secondaryColor = 0xFF7DD3FC,
    glowColor = 0x66BAE6FD,
    description = "Zamrożony rdzeń sub-zero. Generuje 1 800 skrystalizowanego Goo/s."
  ),
  COSMIC(
    id = "cosmic",
    displayNamePl = "Kosmiczny Slime",
    displayNameEn = "Cosmic Slime",
    emoji = "🌌",
    baseProduction = 10_000.0,
    baseCost = 20_000_000L,
    primaryColor = 0xFFE879F9,
    secondaryColor = 0xFF4338CA,
    glowColor = 0x66C084FC,
    description = "Materia z odległych galaktyk. Generuje 10 000 czystego gwiezdnego Goo/s."
  );

  companion object {
    fun fromId(id: String): SlimeType = entries.find { it.id == id } ?: BASIC
  }
}

enum class MutationType(
  val id: String,
  val displayNamePl: String,
  val displayNameEn: String,
  val multiplier: Double,
  val rarityNamePl: String,
  val rarityColor: Long,
  val auraColor: Long,
  val description: String
) {
  NONE(
    id = "none",
    displayNamePl = "Standard",
    displayNameEn = "Standard",
    multiplier = 1.0,
    rarityNamePl = "Pospolity",
    rarityColor = 0xFF94A3B8,
    auraColor = 0x00000000,
    description = "Zwykły genom bez mutacji."
  ),
  CRYSTAL(
    id = "crystal",
    displayNamePl = "Kryształowy",
    displayNameEn = "Crystal",
    multiplier = 3.0,
    rarityNamePl = "Rzadki (x3)",
    rarityColor = 0xFF38BDF8,
    auraColor = 0x8838BDF8,
    description = "Błyszczące kryształy na skórze. Zwiększa produkcję trzykrotnie!"
  ),
  VOID(
    id = "void",
    displayNamePl = "Otchłani (Void)",
    displayNameEn = "Void",
    multiplier = 10.0,
    rarityNamePl = "Epicki (x10)",
    rarityColor = 0xFFA855F7,
    auraColor = 0x88A855F7,
    description = "Ciemna materia i tajemnicza poświata. Zwiększa produkcję aż 10-krotnie!"
  ),
  RAINBOW(
    id = "rainbow",
    displayNamePl = "Tęczowy (Rainbow)",
    displayNameEn = "Rainbow",
    multiplier = 50.0,
    rarityNamePl = "Mityczny (x50)",
    rarityColor = 0xFFF43F5E,
    auraColor = 0x88F59E0B,
    description = "Pulsowanie wszystkimi barwami tęczy. Ekstremalny mnożnik x50 produkcji!"
  ),
  QUANTUM(
    id = "quantum",
    displayNamePl = "Kwantowy (Quantum)",
    displayNameEn = "Quantum",
    multiplier = 200.0,
    rarityNamePl = "Boski (x200)",
    rarityColor = 0xFFFBBF24,
    auraColor = 0x8834D399,
    description = "Przeskakuje między wymiarami czasoprzestrzeni. Niewiarygodny bonus x200!"
  );

  companion object {
    fun fromId(id: String): MutationType = entries.find { it.id == id } ?: NONE
  }
}

enum class FactoryUpgradeType(
  val id: String,
  val titlePl: String,
  val descriptionPl: String,
  val emoji: String,
  val baseCost: Long,
  val costMultiplier: Double,
  val maxLevel: Int = 100
) {
  SPEED(
    id = "speed",
    titlePl = "Szybkość Fabryki",
    descriptionPl = "+15% prędkości produkcji Goo ze wszystkich stanowisk",
    emoji = "⚡",
    baseCost = 150L,
    costMultiplier = 1.6
  ),
  STORAGE(
    id = "storage",
    titlePl = "Zbiornik na Goo",
    descriptionPl = "+250 pojemności magazynowej na wyprodukowane Goo",
    emoji = "📦",
    baseCost = 200L,
    costMultiplier = 1.7
  ),
  SELL_PRICE(
    id = "sell_price",
    titlePl = "Wartość Rynkowa Goo",
    descriptionPl = "+20% wartości monet przy każdej sprzedaży",
    emoji = "💰",
    baseCost = 300L,
    costMultiplier = 1.8
  ),
  MUTATION_CHANCE(
    id = "mutation_chance",
    titlePl = "Katalizator Mutacji",
    descriptionPl = "+2% szansy na ewolucję rzadkich form slime'a",
    emoji = "🧪",
    baseCost = 1_000L,
    costMultiplier = 2.2,
    maxLevel = 25
  ),
  AUTO_SELLER(
    id = "auto_seller",
    titlePl = "Taśmociąg & Auto-Sprzedaż",
    descriptionPl = "Automatycznie sprzedaje 15% magazynu Goo co sekundę!",
    emoji = "🤖",
    baseCost = 2_500L,
    costMultiplier = 2.5,
    maxLevel = 10
  ),
  SLOT_EXPANSION(
    id = "slot_expansion",
    titlePl = "Nowe Stanowisko Fabryki",
    descriptionPl = "Odblokowuje dodatkowe miejsce na kolejnego slime'a",
    emoji = "🏭",
    baseCost = 1_000L,
    costMultiplier = 4.0,
    maxLevel = 8
  );

  fun calculateCost(currentLevel: Int): Long {
    return (baseCost * Math.pow(costMultiplier, currentLevel.toDouble())).toLong()
  }

  companion object {
    fun fromId(id: String): FactoryUpgradeType? = entries.find { it.id == id }
  }
}

enum class ZoneType(
  val id: String,
  val namePl: String,
  val emoji: String,
  val unlockCost: Long,
  val prodMultiplier: Double,
  val valueMultiplier: Double,
  val mutationBonusPct: Double,
  val backgroundColor: Long,
  val surfaceColor: Long,
  val accentColor: Long,
  val descriptionPl: String
) {
  GRASSLAND(
    id = "grassland",
    namePl = "Zielona Łąka",
    emoji = "🌱",
    unlockCost = 0L,
    prodMultiplier = 1.0,
    valueMultiplier = 1.0,
    mutationBonusPct = 0.0,
    backgroundColor = 0xFF0B1910,
    surfaceColor = 0xFF142E1F,
    accentColor = 0xFF4ADE80,
    descriptionPl = "Łagodna, zielona kraina idealna na start fabryki."
  ),
  FROZEN(
    id = "frozen",
    namePl = "Mroźna Fabryka",
    emoji = "❄️",
    unlockCost = 25_000L,
    prodMultiplier = 1.25,
    valueMultiplier = 1.5,
    mutationBonusPct = 2.0,
    backgroundColor = 0xFF081C2E,
    surfaceColor = 0xFF113250,
    accentColor = 0xFF38BDF8,
    descriptionPl = "Krystaliczny lód podwaja trwałość i wartość sprzedawanego Goo."
  ),
  VOLCANO(
    id = "volcano",
    namePl = "Wulkan Magmy",
    emoji = "🌋",
    unlockCost = 250_000L,
    prodMultiplier = 2.0,
    valueMultiplier = 1.2,
    mutationBonusPct = 5.0,
    backgroundColor = 0xFF240A0A,
    surfaceColor = 0xFF401212,
    accentColor = 0xFFF87171,
    descriptionPl = "Gorąca energia wulkanu podwaja szybkość produkcji wszystkich slime'ów."
  ),
  TOXIC_ZONE(
    id = "toxic_zone",
    namePl = "Strefa Skażenia",
    emoji = "☣️",
    unlockCost = 1_500_000L,
    prodMultiplier = 2.5,
    valueMultiplier = 1.8,
    mutationBonusPct = 15.0,
    backgroundColor = 0xFF1C0A26,
    surfaceColor = 0xFF341346,
    accentColor = 0xFFA855F7,
    descriptionPl = "Radioaktywna mgła dramatycznie zwiększa szanse na rzadkie mutacje (+15%)!"
  ),
  SPACE(
    id = "space",
    namePl = "Stacja Kosmiczna",
    emoji = "🌌",
    unlockCost = 10_000_000L,
    prodMultiplier = 5.0,
    valueMultiplier = 3.0,
    mutationBonusPct = 25.0,
    backgroundColor = 0xFF090A1A,
    surfaceColor = 0xFF171936,
    accentColor = 0xFFC084FC,
    descriptionPl = "Stan nieważkości i gwiezdna energia przyspieszają produkcję 5-krotnie."
  ),
  VOID(
    id = "void",
    namePl = "Wymiar Otchłani",
    emoji = "🕳️",
    unlockCost = 100_000_000L,
    prodMultiplier = 12.0,
    valueMultiplier = 5.0,
    mutationBonusPct = 40.0,
    backgroundColor = 0xFF050508,
    surfaceColor = 0xFF12121A,
    accentColor = 0xFFF43F5E,
    descriptionPl = "Kres rzeczywistości. Ekstremalna potęga antymaterii i boskie slime'y."
  );

  companion object {
    fun fromId(id: String): ZoneType = entries.find { it.id == id } ?: GRASSLAND
  }
}

enum class RebirthPerkType(
  val id: String,
  val titlePl: String,
  val descriptionPl: String,
  val emoji: String,
  val costPerLevel: Int,
  val maxLevel: Int = 10
) {
  PERM_PRODUCTION(
    id = "perm_production",
    titlePl = "Wieczna Wydajność",
    descriptionPl = "+25% stałej produkcji Goo ze wszystkich slime'ów na poziom",
    emoji = "⚡",
    costPerLevel = 1
  ),
  PERM_MUTATION(
    id = "perm_mutation",
    titlePl = "Mutacyjny Gen",
    descriptionPl = "+5% bazowej szansy na rzadkie mutacje",
    emoji = "🧬",
    costPerLevel = 2
  ),
  PERM_VALUE(
    id = "perm_value",
    titlePl = "Złote Receptury",
    descriptionPl = "+30% do ceny sprzedaży każdego Goo",
    emoji = "💰",
    costPerLevel = 1
  ),
  HEADSTART_COINS(
    id = "headstart_coins",
    titlePl = "Kapitał Początkowy",
    descriptionPl = "+1 000 Coins na start po każdym Rebirth",
    emoji = "🚀",
    costPerLevel = 1
  ),
  STORAGE_BOOST(
    id = "storage_boost",
    titlePl = "Kwantowy Magazyn",
    descriptionPl = "+50% bazowej pojemności zbiornika Goo",
    emoji = "📦",
    costPerLevel = 1
  );

  companion object {
    fun fromId(id: String): RebirthPerkType? = entries.find { it.id == id }
  }
}
