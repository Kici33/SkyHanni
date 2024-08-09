package at.hannibal2.skyhanni.features.guides.farming

import at.hannibal2.skyhanni.features.guides.FortuneTypes
import at.hannibal2.skyhanni.utils.GuiRenderUtils

internal enum class FarmingFortuneInfos(
    val sumTo: FarmingFortuneInfos?,
    private val currentF: () -> Number,
    private val maxF: (FarmingFortuneInfos) -> Number,
) {
    UNIVERSAL(
        null, { FarmingFortuneData.totalBaseFF }, FortuneTypes.TOTAL,
        {
            val backupArmor = FarmingItems.currentArmor
            val backupEquip = FarmingItems.currentEquip
            FarmingItems.currentArmor = null
            FarmingItems.currentEquip = null
            val total = maxSumToThis(it)
            FarmingItems.currentArmor = backupArmor
            FarmingItems.currentEquip = backupEquip
            total
        },
    ),
    FARMING_LEVEL(UNIVERSAL, { FarmingFortuneData.baseFF }, FortuneTypes.SKILL_LEVEL, 240),
    BESTIARY(UNIVERSAL, { FarmingFortuneData.baseFF }, FortuneTypes.BESTIARY, 60),
    GARDEN_PLOTS(UNIVERSAL, { FarmingFortuneData.baseFF }, FortuneTypes.PLOTS, 72),
    ANITA_BUFF(UNIVERSAL, { FarmingFortuneData.baseFF }, FortuneTypes.ANITA, 60),
    COMMUNITY_SHOP(UNIVERSAL, { FarmingFortuneData.baseFF }, FortuneTypes.COMMUNITY_SHOP, 40),
    CAKE_BUFF(UNIVERSAL, { FarmingFortuneData.baseFF }, FortuneTypes.CAKE, 5),
    TOTAL_ARMOR(UNIVERSAL, { FarmingItems.currentArmor?.getFortuneData() ?: FarmingFortuneData.armorTotalFF }, FortuneTypes.TOTAL),
    BASE_ARMOR(
        TOTAL_ARMOR, { FarmingItems.currentArmor?.getFortuneData() ?: FarmingFortuneData.armorTotalFF }, FortuneTypes.BASE,
        {
            when (FarmingItems.currentArmor) {
                FarmingItems.HELMET -> 30
                FarmingItems.CHESTPLATE, FarmingItems.LEGGINGS -> 35
                FarmingItems.BOOTS -> if (FarmingFortuneData.usingSpeedBoots) 60 else 30
                else -> if (FarmingFortuneData.usingSpeedBoots) 160 else 130
            }
        },
    ),
    ABILITY_ARMOR(
        TOTAL_ARMOR, { FarmingItems.currentArmor?.getFortuneData() ?: FarmingFortuneData.armorTotalFF }, FortuneTypes.ABILITY,
        {
            when (FarmingItems.currentArmor) {
                FarmingItems.HELMET, FarmingItems.CHESTPLATE, FarmingItems.LEGGINGS -> if (FarmingFortuneData.usingSpeedBoots) 16.667 else 18.75
                FarmingItems.BOOTS -> if (FarmingFortuneData.usingSpeedBoots) 0 else 18.75
                else -> if (FarmingFortuneData.usingSpeedBoots) 50 else 75
            }
        },
    ),
    REFORGE_ARMOR(
        TOTAL_ARMOR, { FarmingItems.currentArmor?.getFortuneData() ?: FarmingFortuneData.armorTotalFF }, FortuneTypes.REFORGE,
        {
            when (FarmingItems.currentArmor) {
                FarmingItems.HELMET, FarmingItems.CHESTPLATE, FarmingItems.LEGGINGS -> 30
                FarmingItems.BOOTS -> if (FarmingFortuneData.usingSpeedBoots) 25 else 30
                else -> if (FarmingFortuneData.usingSpeedBoots) 115 else 120
            }
        },
    ),
    ENCHANT_ARMOR(
        sumTo = TOTAL_ARMOR,
        from = { FarmingItems.currentArmor?.getFortuneData() ?: FarmingFortuneData.armorTotalFF },
        what = FortuneTypes.ENCHANT,
        x4 = { FarmingItems.currentArmor == null },
        max = 5,
    ),
    GEMSTONE_ARMOR(
        TOTAL_ARMOR, { FarmingItems.currentArmor?.getFortuneData() ?: FarmingFortuneData.armorTotalFF }, FortuneTypes.GEMSTONE,
        {
            when (FarmingItems.currentArmor) {
                FarmingItems.HELMET, FarmingItems.CHESTPLATE, FarmingItems.LEGGINGS -> 20
                FarmingItems.BOOTS -> if (FarmingFortuneData.usingSpeedBoots) 16 else 20
                else -> if (FarmingFortuneData.usingSpeedBoots) 76 else 80
            }
        },
    ),
    TOTAL_PET(UNIVERSAL, { FarmingItems.currentPet.getFortuneData() }, FortuneTypes.TOTAL),
    PET_BASE(
        TOTAL_PET, { FarmingItems.currentPet.getFortuneData() }, FortuneTypes.BASE,
        {
            when (FarmingItems.currentPet) {
                FarmingItems.ELEPHANT -> 150
                FarmingItems.MOOSHROOM_COW -> 158
                FarmingItems.BEE -> 30
                FarmingItems.SLUG -> 100
                else -> 0
            }
        },
    ),
    PET_ITEM(TOTAL_PET, { FarmingItems.currentPet.getFortuneData() }, FortuneTypes.PET_ITEM, 60),
    TOTAL_EQUIP(
        sumTo = UNIVERSAL,
        from = { FarmingItems.currentEquip?.getFortuneData() ?: FarmingFortuneData.equipmentTotalFF },
        what = FortuneTypes.TOTAL,
    ),
    BASE_EQUIP(
        sumTo = TOTAL_EQUIP,
        from = { FarmingItems.currentEquip?.getFortuneData() ?: FarmingFortuneData.equipmentTotalFF },
        what = FortuneTypes.BASE,
        x4 = { FarmingItems.currentEquip == null },
        max = 5.0,
    ),
    ABILITY_EQUIP(
        sumTo = TOTAL_EQUIP,
        from = { FarmingItems.currentEquip?.getFortuneData() ?: FarmingFortuneData.equipmentTotalFF },
        what = FortuneTypes.ABILITY,
        x4 = { FarmingItems.currentEquip == null },
        max = 15.0,
    ),
    REFORGE_EQUIP(
        sumTo = TOTAL_EQUIP,
        from = { FarmingItems.currentEquip?.getFortuneData() ?: FarmingFortuneData.equipmentTotalFF },
        what = FortuneTypes.REFORGE,
        x4 = { FarmingItems.currentEquip == null },
        max = 15.0,
    ),
    ENCHANT_EQUIP(
        sumTo = TOTAL_EQUIP,
        from = { FarmingItems.currentEquip?.getFortuneData() ?: FarmingFortuneData.equipmentTotalFF },
        what = FortuneTypes.ENCHANT,
        x4 = { FarmingItems.currentEquip == null },
        max = { at.hannibal2.skyhanni.features.garden.GardenAPI.totalAmountVisitorsExisting.toDouble() / 4.0 },
    ),
    ;

    val current get() = currentF().toDouble()
    val max get() = maxF(this).toDouble()

    fun bar(label: String, tooltip: String, width: Int = 90) =
        GuiRenderUtils.getFortuneBar(label, tooltip, current, max, width)

    constructor(
        sumTo: FarmingFortuneInfos?,
        current: () -> Number,
        max: Number,
    ) : this(sumTo, current, { max })

    constructor(
        sumTo: FarmingFortuneInfos?,
        from: () -> Map<FortuneTypes, Double>,
        what: FortuneTypes,
        max: Number,
    ) : this(sumTo, { from()[what] ?: 0.0 }, { max })

    constructor(
        sumTo: FarmingFortuneInfos?,
        from: () -> Map<FortuneTypes, Double>,
        what: FortuneTypes,
        x4: () -> Boolean,
        max: Number,
    ) : this(sumTo, { from()[what] ?: 0.0 }, { if (x4()) max.toDouble() * 4 else max })

    constructor(
        sumTo: FarmingFortuneInfos?,
        from: () -> Map<FortuneTypes, Double>,
        what: FortuneTypes,
        x4: () -> Boolean,
        max: () -> Number,
    ) : this(sumTo, { from()[what] ?: 0.0 }, { if (x4()) max().toDouble() * 4 else max() })

    constructor(
        sumTo: FarmingFortuneInfos?,
        from: () -> Map<FortuneTypes, Double>,
        what: FortuneTypes,
        max: (FarmingFortuneInfos) -> Number,
    ) : this(sumTo, { from()[what] ?: 0.0 }, max)

    constructor(
        sumTo: FarmingFortuneInfos?,
        from: () -> Map<FortuneTypes, Double>,
        what: FortuneTypes,
    ) : this(sumTo, { from()[what] ?: 0.0 }, ::maxSumToThis)
}

private fun maxSumToThis(self: FarmingFortuneInfos): Double = FarmingFortuneInfos.entries.filter { it.sumTo == self }.sumOf { it.max }
