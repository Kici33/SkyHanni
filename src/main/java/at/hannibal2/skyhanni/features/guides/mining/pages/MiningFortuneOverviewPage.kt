package at.hannibal2.skyhanni.features.guides.mining.pages

import at.hannibal2.skyhanni.features.guides.FortuneTypes
import at.hannibal2.skyhanni.features.guides.mining.MiningFortuneData
import at.hannibal2.skyhanni.features.guides.mining.MiningFortuneInfos
import at.hannibal2.skyhanni.features.guides.mining.MiningItems
import at.hannibal2.skyhanni.utils.RenderUtils
import at.hannibal2.skyhanni.utils.TimeUnit
import at.hannibal2.skyhanni.utils.TimeUtils.format
import at.hannibal2.skyhanni.utils.guide.GuideTablePage
import at.hannibal2.skyhanni.utils.renderables.Renderable

class MiningFortuneOverviewPage(sizeX: Int, sizeY: Int, paddingX: Int = 15, paddingY: Int = 7, footerSpacing: Int = 6) :
    GuideTablePage(
        sizeX, sizeY, paddingX, paddingY, footerSpacing,
    ) {

    override fun onEnter() {
        val (content, footer) = getPage()
        update(content, footer)
    }

    //TODO split up this 240 lines function
    fun getPage(): Pair<List<List<Renderable>>, List<Renderable>> {
        val content = mutableListOf<MutableList<Renderable>>()
        val footer = mutableListOf<Renderable>()
        val timeUntilCakes = MiningFortuneData.cakeExpireTime.timeUntil().format(TimeUnit.HOUR, maxUnits = 1)

        content.addTable(
            0,
            MiningFortuneInfos.UNIVERSAL.bar(
                "§6Universal Mining Fortune",
                "§7§2Mining fortune in that is\n§2applied to every ore\n" +
                    "§eSee on the iron pickaxe page",
            ),
        )

        content.addTable(
            1,
            MiningFortuneInfos.MINING_LEVEL.bar(
                "§2Mining Level",
                if (FortuneTypes.SKILL_LEVEL.notSaved()) "§cMining level not saved\n§eOpen /skills to set it!"
                else "§7§2Fortune for levelling your farming skill\n§2You get 4☘ per farming level",
            ),
        )

        content.addTable(
            6,
            MiningFortuneInfos.CAKE_BUFF.bar(
                "§2Cake Buff",
                when {
                    MiningFortuneData.cakeExpireTime.isFarPast() ->
                        "§eYou have not eaten a cake since\n§edownloading this update, assuming the\n§ebuff is active!"

                    MiningFortuneData.cakeExpireTime.isInPast() ->
                        "§cYour cake buff has run out\nGo eat some cake!"

                    else ->
                        "§7§2Fortune for eating cake\n§2You get 5☘ for eating cake\n" +
                            "§2Time until cake buff runs out: $timeUntilCakes"
                },
            ),
        )

        val moreInfo = "§2Select a piece for more info"
        val wordArmor = if (MiningItems.currentArmor == null) "Armor" else "Piece"
        val armorName = MiningItems.currentArmor?.getItem()?.displayName ?: ""

        content.addTable(
            1,
            MiningFortuneInfos.TOTAL_ARMOR.bar(
                "§2Total $wordArmor Fortune",
                if (MiningItems.currentArmor == null) "§7§2Total fortune from your armor\n$moreInfo"
                else "§7§2Total fortune from your\n$armorName",
            ),
        )

        content.addTable(
            2,
            MiningFortuneInfos.BASE_ARMOR.bar(
                "§2Base $wordArmor Fortune",
                if (MiningItems.currentArmor == null) "§7§2The base fortune from your armor\n$moreInfo"
                else "§7§2Base fortune from your\n$armorName",
            ),
        )

        content.addTable(
            3,
            MiningFortuneInfos.ABILITY_ARMOR.bar(
                "§2$wordArmor Ability",
                if (MiningItems.currentArmor == null) "§7§2The fortune from your armor's ability\n$moreInfo"
                else "§7§2Ability fortune from your\n$armorName",
            ),
        )

        content.addTable(
            4,
            MiningFortuneInfos.REFORGE_ARMOR.bar(
                "§2$wordArmor Reforge",
                if (MiningItems.currentArmor == null) "§7§2The fortune from your armor's reforge\n$moreInfo"
                else "§7§2Reforge fortune from your\n$armorName",
            ),
        )

        content.addTable(
            5,
            MiningFortuneInfos.ENCHANT_ARMOR.bar(
                "§2$wordArmor Enchantment",
                if (MiningItems.currentArmor == null) "§7§2The fortune from your armor's enchantments\n$moreInfo"
                else "§7§2Enchantment fortune from your\n$armorName",
            ),
        )

        content.addTable(
            6,
            MiningFortuneInfos.GEMSTONE_ARMOR.bar(
                "§2$wordArmor Gemstones",
                if (MiningItems.currentArmor == null) "§7§2The fortune from your armor's gemstones\n$moreInfo"
                else "§7§2Gemstone fortune from your\n$armorName",
            ),
        )

        val wordEquip = if (MiningItems.currentEquip == null) "Equipment" else "Piece"

        val equipmentName = MiningItems.currentEquip?.getItem()?.displayName ?: ""

        content.addTable(
            1,
            MiningFortuneInfos.TOTAL_EQUIP.bar(
                "§2Total $wordEquip Fortune",
                if (MiningItems.currentEquip == null) "§7§2Total fortune from your equipment\n$moreInfo"
                else "§7§2Total fortune from your\n$equipmentName",
            ),
        )

        content.addTable(
            2,
            MiningFortuneInfos.BASE_EQUIP.bar(
                "§2$wordEquip Base Fortune",
                if (MiningItems.currentEquip == null) "§7§2The base fortune from your equipment\n$moreInfo"
                else "§7§2Base fortune from your\n$equipmentName",
            ),
        )

        content.addTable(
            3,
            MiningFortuneInfos.ABILITY_EQUIP.bar(
                "§2$wordEquip Ability",
                if (MiningItems.currentEquip == null) "§7§2The fortune from your equipment's abilities\n$moreInfo"
                else "§7§2Ability fortune from your\n$equipmentName",
            ),
        )

        content.addTable(
            4,
            MiningFortuneInfos.REFORGE_EQUIP.bar(
                "§2$wordEquip Reforge",
                if (MiningItems.currentEquip == null) "§7§2The fortune from your equipment's reforges\n$moreInfo"
                else "§7§2Reforge fortune from your\n$equipmentName",
            ),
        )

        content.addTable(
            5,
            MiningFortuneInfos.ENCHANT_EQUIP.bar(
                "§2$wordEquip Enchantment",
                if (MiningItems.currentEquip == null) "§7§2The fortune from your equipment's enchantments\n$moreInfo"
                else "§7§2Enchantment fortune from your\n$equipmentName",
            ),
        )

        footer.add(
            Renderable.horizontalContainer(
                MiningItems.getPetsDisplay(true),
                4,
                horizontalAlign = RenderUtils.HorizontalAlignment.CENTER,
                verticalAlign = RenderUtils.VerticalAlignment.CENTER,
            ),
        )

        footer.add(
            MiningFortuneInfos.TOTAL_PET.bar(
                "§2Total Pet Fortune",
                "§7§2The total fortune from your pet and its item",
                72,
            ),
        )

        footer.add(
            MiningFortuneInfos.PET_BASE.bar(
                "§2Base Pet Fortune",
                "§7§2The base fortune from your pet",
                72,
            ),
        )

        footer.add(
            MiningFortuneInfos.PET_ITEM.bar(
                "§2Pet Item",
                when (MiningFortuneData.currentPetItem) {
                    "GREEN_BANDANA" -> "§7§2The fortune from your pet's item\n§2Grants 4☘ per garden level"
                    "YELLOW_BANDANA" -> "§7§2The fortune from your pet's item"
                    "MINOS_RELIC" -> "§cGreen Bandana is better for fortune than minos relic!"
                    else -> "No fortune boosting pet item"
                },
                72,
            ),
        )

        // Displays

        content.addTable(
            0,
            Renderable.horizontalContainer(
                MiningItems.getArmorDisplay(true),
                4,
                horizontalAlign = RenderUtils.HorizontalAlignment.CENTER,
                verticalAlign = RenderUtils.VerticalAlignment.CENTER,
            ),
        )

        content.addTable(
            0,
            Renderable.horizontalContainer(
                MiningItems.getEquipmentDisplay(true),
                4,
                horizontalAlign = RenderUtils.HorizontalAlignment.CENTER,
                verticalAlign = RenderUtils.VerticalAlignment.CENTER,
            ),
        )

        return content to footer
    }

    private fun FortuneTypes.notSaved(): Boolean = MiningFortuneData.baseFF[this]?.let {
        it < 0.0
    } ?: true
}

private fun MutableList<MutableList<Renderable>>.addTable(row: Int, r: Renderable) {
    this.getOrNull(row)?.add(r) ?: mutableListOf(r).let { this.add(row, it) }
}
