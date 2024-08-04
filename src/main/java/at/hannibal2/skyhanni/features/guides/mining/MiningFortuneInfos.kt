package at.hannibal2.skyhanni.features.guides.mining

import at.hannibal2.skyhanni.features.guides.FortuneTypes
import at.hannibal2.skyhanni.features.guides.farming.FarmingFortuneInfos
import at.hannibal2.skyhanni.utils.GuiRenderUtils

internal enum class MiningFortuneInfos(
    val sumTo: MiningFortuneInfos?,
    private val currentF: () -> Number,
    private val maxF: (MiningFortuneInfos) -> Number,
) {
    UNIVERSAL(
        null, { MiningFortuneData.totalBaseFF }, FortuneTypes.TOTAL,
        {
            val backupArmor = MiningItems.currentArmor
            val backupEquip = MiningItems.currentEquip
            MiningItems.currentArmor = null
            MiningItems.currentEquip = null
            val total = maxSumToThis(it)
            MiningItems.currentArmor = backupArmor
            MiningItems.currentEquip = backupEquip
            total
        },
    ),
    MINING_LEVEL(UNIVERSAL, { MiningFortuneData.baseFF }, FortuneTypes.SKILL_LEVEL, 240),
    CAKE_BUFF(UNIVERSAL, { MiningFortuneData.baseFF }, FortuneTypes.CAKE, 5),
    TOTAL_ARMOR(UNIVERSAL, { MiningItems.currentArmor?.getFFData() ?: MiningFortuneData.armorTotalFF },
        FortuneTypes.TOTAL
    ),
    BASE_ARMOR(
        TOTAL_ARMOR, { MiningItems.currentArmor?.getFFData() ?: MiningFortuneData.armorTotalFF }, FortuneTypes.BASE,
        {
            when (MiningItems.currentArmor) {
                MiningItems.HELMET -> 30
                MiningItems.CHESTPLATE, MiningItems.LEGGINGS -> 35
                MiningItems.BOOTS -> if (MiningFortuneData.usingSpeedBoots) 60 else 30
                else -> if (MiningFortuneData.usingSpeedBoots) 160 else 130
            }
        },
    ),
    ABILITY_ARMOR(
        TOTAL_ARMOR, { MiningItems.currentArmor?.getFFData() ?: MiningFortuneData.armorTotalFF },
        FortuneTypes.ABILITY,
        {
            when (MiningItems.currentArmor) {
                MiningItems.HELMET, MiningItems.CHESTPLATE, MiningItems.LEGGINGS -> if (MiningFortuneData.usingSpeedBoots) 16.667 else 18.75
                MiningItems.BOOTS -> if (MiningFortuneData.usingSpeedBoots) 0 else 18.75
                else -> if (MiningFortuneData.usingSpeedBoots) 50 else 75
            }
        },
    ),
    REFORGE_ARMOR(
        TOTAL_ARMOR, { MiningItems.currentArmor?.getFFData() ?: MiningFortuneData.armorTotalFF },
        FortuneTypes.REFORGE,
        {
            when (MiningItems.currentArmor) {
                MiningItems.HELMET, MiningItems.CHESTPLATE, MiningItems.LEGGINGS -> 30
                MiningItems.BOOTS -> if (MiningFortuneData.usingSpeedBoots) 25 else 30
                else -> if (MiningFortuneData.usingSpeedBoots) 115 else 120
            }
        },
    ),
    ENCHANT_ARMOR(
        sumTo = TOTAL_ARMOR,
        from = { MiningItems.currentArmor?.getFFData() ?: MiningFortuneData.armorTotalFF },
        what = FortuneTypes.ENCHANT,
        x4 = { MiningItems.currentArmor == null },
        max = 5,
    ),
    GEMSTONE_ARMOR(
        TOTAL_ARMOR, { MiningItems.currentArmor?.getFFData() ?: MiningFortuneData.armorTotalFF },
        FortuneTypes.GEMSTONE,
        {
            when (MiningItems.currentArmor) {
                MiningItems.HELMET, MiningItems.CHESTPLATE, MiningItems.LEGGINGS -> 20
                MiningItems.BOOTS -> if (MiningFortuneData.usingSpeedBoots) 16 else 20
                else -> if (MiningFortuneData.usingSpeedBoots) 76 else 80
            }
        },
    ),
    TOTAL_PET(UNIVERSAL, { MiningItems.currentPet.getFFData() }, FortuneTypes.TOTAL),
    PET_BASE(
        TOTAL_PET, { MiningItems.currentPet.getFFData() }, FortuneTypes.BASE,
        {
            when (MiningItems.currentPet) {
                MiningItems.ELEPHANT -> 150
                MiningItems.MOOSHROOM_COW -> 158
                MiningItems.BEE -> 30
                MiningItems.SLUG -> 100
                else -> 0
            }
        },
    ),
    PET_ITEM(TOTAL_PET, { MiningItems.currentPet.getFFData() }, FortuneTypes.PET_ITEM, 60),
    TOTAL_EQUIP(
        sumTo = UNIVERSAL,
        from = { MiningItems.currentEquip?.getFFData() ?: MiningFortuneData.equipmentTotalFF },
        what = FortuneTypes.TOTAL,
    ),
    BASE_EQUIP(
        sumTo = TOTAL_EQUIP,
        from = { MiningItems.currentEquip?.getFFData() ?: MiningFortuneData.equipmentTotalFF },
        what = FortuneTypes.BASE,
        x4 = { MiningItems.currentEquip == null },
        max = 5.0,
    ),
    ABILITY_EQUIP(
        sumTo = TOTAL_EQUIP,
        from = { MiningItems.currentEquip?.getFFData() ?: MiningFortuneData.equipmentTotalFF },
        what = FortuneTypes.ABILITY,
        x4 = { MiningItems.currentEquip == null },
        max = 15.0,
    ),
    REFORGE_EQUIP(
        sumTo = TOTAL_EQUIP,
        from = { MiningItems.currentEquip?.getFFData() ?: MiningFortuneData.equipmentTotalFF },
        what = FortuneTypes.REFORGE,
        x4 = { MiningItems.currentEquip == null },
        max = 15.0,
    ),
    ENCHANT_EQUIP(
        sumTo = TOTAL_EQUIP,
        from = { MiningItems.currentEquip?.getFFData() ?: MiningFortuneData.equipmentTotalFF },
        what = FortuneTypes.ENCHANT,
        x4 = { MiningItems.currentEquip == null },
        max = { at.hannibal2.skyhanni.features.garden.GardenAPI.totalAmountVisitorsExisting.toDouble() / 4.0 },
    ),
    ;

    val current get() = currentF().toDouble()
    val max get() = maxF(this).toDouble()

    fun bar(label: String, tooltip: String, width: Int = 90) =
        GuiRenderUtils.getFortuneBar(label, tooltip, current, max, width)

    constructor(
        sumTo: MiningFortuneInfos?,
        current: () -> Number,
        max: Number,
    ) : this(sumTo, current, { max })

    constructor(
        sumTo: MiningFortuneInfos?,
        from: () -> Map<FortuneTypes, Double>,
        what: FortuneTypes,
        max: Number,
    ) : this(sumTo, { from()[what] ?: 0.0 }, { max })

    constructor(
        sumTo: MiningFortuneInfos?,
        from: () -> Map<FortuneTypes, Double>,
        what: FortuneTypes,
        x4: () -> Boolean,
        max: Number,
    ) : this(sumTo, { from()[what] ?: 0.0 }, { if (x4()) max.toDouble() * 4 else max })

    constructor(
        sumTo: MiningFortuneInfos?,
        from: () -> Map<FortuneTypes, Double>,
        what: FortuneTypes,
        x4: () -> Boolean,
        max: () -> Number,
    ) : this(sumTo, { from()[what] ?: 0.0 }, { if (x4()) max().toDouble() * 4 else max() })

    constructor(
        sumTo: MiningFortuneInfos?,
        from: () -> Map<FortuneTypes, Double>,
        what: FortuneTypes,
        max: (MiningFortuneInfos) -> Number,
    ) : this(sumTo, { from()[what] ?: 0.0 }, max)

    constructor(
        sumTo: MiningFortuneInfos?,
        from: () -> Map<FortuneTypes, Double>,
        what: FortuneTypes,
    ) : this(sumTo, { from()[what] ?: 0.0 }, ::maxSumToThis)
}

private fun maxSumToThis(self: MiningFortuneInfos): Double = MiningFortuneInfos.entries.filter { it.sumTo == self }.sumOf { it.max }


