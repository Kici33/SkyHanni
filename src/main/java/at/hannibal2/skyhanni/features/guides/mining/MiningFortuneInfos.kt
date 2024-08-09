package at.hannibal2.skyhanni.features.guides.mining

import at.hannibal2.skyhanni.features.guides.FortuneTypes
import at.hannibal2.skyhanni.utils.GuiRenderUtils

internal enum class MiningFortuneInfos(
    val sumTo: MiningFortuneInfos?,
    private val currentF: () -> Number,
    private val maxF: (MiningFortuneInfos) -> Number,
) {
    UNIVERSAL(
        null, { MiningFortuneData.totalBaseMF }, FortuneTypes.TOTAL,
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
    MINING_LEVEL(UNIVERSAL, { MiningFortuneData.baseMF }, FortuneTypes.SKILL_LEVEL, 240),
    CAKE_BUFF(UNIVERSAL, { MiningFortuneData.baseMF }, FortuneTypes.CAKE, 5),
    HOTM(UNIVERSAL, { MiningFortuneData.baseMF }, FortuneTypes.HOTM, 550),
    TOTAL_ARMOR(UNIVERSAL, { MiningItems.currentArmor?.getFortuneData() ?: MiningFortuneData.armorTotalMF },
        FortuneTypes.TOTAL
    ),
    BASE_ARMOR(
        TOTAL_ARMOR, { MiningItems.currentArmor?.getFortuneData() ?: MiningFortuneData.armorTotalMF }, FortuneTypes.BASE,
        {
            when (MiningItems.currentArmor) {
                MiningItems.HELMET,  MiningItems.CHESTPLATE, MiningItems.LEGGINGS, MiningItems.BOOTS -> 30
                else -> 120
            }
        },
    ),
    REFORGE_ARMOR(
        TOTAL_ARMOR, { MiningItems.currentArmor?.getFortuneData() ?: MiningFortuneData.armorTotalMF },
        FortuneTypes.REFORGE,
        {
            when (MiningItems.currentArmor) {
                MiningItems.HELMET, MiningItems.CHESTPLATE, MiningItems.LEGGINGS, MiningItems.BOOTS -> 30
                else -> 120
            }
        },
    ),
    GEMSTONE_ARMOR(
        TOTAL_ARMOR, { MiningItems.currentArmor?.getFortuneData() ?: MiningFortuneData.armorTotalMF },
        FortuneTypes.GEMSTONE,
        {
            when (MiningItems.currentArmor) {
                MiningItems.HELMET, MiningItems.CHESTPLATE, MiningItems.LEGGINGS, MiningItems.BOOTS -> 100
                else -> 400
            }
        },
    ),
    TOTAL_PET(UNIVERSAL, { MiningItems.currentPet.getFortuneData() }, FortuneTypes.TOTAL),
    PET_BASE(
        TOTAL_PET, { MiningItems.currentPet.getFortuneData() }, FortuneTypes.BASE,
        {
            when (MiningItems.currentPet) {
                MiningItems.SCATHA -> 125
//                 MiningItems.SNAIL ->
//                 MiningItems.BAL ->
                else -> 0
            }
        },
    ),
    PET_ITEM(TOTAL_PET, { MiningItems.currentPet.getFortuneData() }, FortuneTypes.PET_ITEM, 50),
    TOTAL_EQUIP(
        sumTo = UNIVERSAL,
        from = { MiningItems.currentEquip?.getFortuneData() ?: MiningFortuneData.equipmentTotalMF },
        what = FortuneTypes.TOTAL,
    ),
    BASE_EQUIP(
        sumTo = TOTAL_EQUIP,
        from = { MiningItems.currentEquip?.getFortuneData() ?: MiningFortuneData.equipmentTotalMF },
        what = FortuneTypes.BASE,
        x4 = { MiningItems.currentEquip == null },
        max = 10,
    ),
    ABILITY_EQUIP(
        sumTo = TOTAL_EQUIP,
        from = { MiningItems.currentEquip?.getFortuneData() ?: MiningFortuneData.equipmentTotalMF },
        what = FortuneTypes.ABILITY,
        x4 = { MiningItems.currentEquip == null },
        max = 6.25,
    ),
    REFORGE_EQUIP(
        sumTo = TOTAL_EQUIP,
        from = { MiningItems.currentEquip?.getFortuneData() ?: MiningFortuneData.equipmentTotalMF },
        what = FortuneTypes.REFORGE,
        x4 = { MiningItems.currentEquip == null },
        max = 6,
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


