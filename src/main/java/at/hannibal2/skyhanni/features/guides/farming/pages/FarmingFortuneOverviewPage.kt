package at.hannibal2.skyhanni.features.guides.farming.pages

import at.hannibal2.skyhanni.features.guides.FortuneTypes
import at.hannibal2.skyhanni.features.guides.farming.FarmingFortuneData
import at.hannibal2.skyhanni.features.guides.farming.FarmingFortuneInfos
import at.hannibal2.skyhanni.features.guides.farming.FarmingItems
import at.hannibal2.skyhanni.utils.RenderUtils
import at.hannibal2.skyhanni.utils.TimeUnit
import at.hannibal2.skyhanni.utils.TimeUtils.format
import at.hannibal2.skyhanni.utils.guide.GuideTablePage
import at.hannibal2.skyhanni.utils.renderables.Renderable

class FarmingFortuneOverviewPage(sizeX: Int, sizeY: Int, paddingX: Int = 15, paddingY: Int = 7, footerSpacing: Int = 6) :
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
        val timeUntilCakes = FarmingFortuneData.cakeExpireTime.timeUntil().format(TimeUnit.HOUR, maxUnits = 1)

        content.addTable(
            0,
            FarmingFortuneInfos.UNIVERSAL.bar(
                "§6Universal Farming Fortune",
                "§7§2Farming fortune in that is\n§2applied to every crop\n§eNot the same as tab FF\n" +
                    "§eSee on the grass block page",
            ),
        )

        content.addTable(
            1,
            FarmingFortuneInfos.FARMING_LEVEL.bar(
                "§2Farming Level",
                if (FortuneTypes.SKILL_LEVEL.notSaved()) "§cFarming level not saved\n§eOpen /skills to set it!"
                else "§7§2Fortune for levelling your farming skill\n§2You get 4☘ per farming level",
            ),
        )

        content.addTable(
            2,
            FarmingFortuneInfos.BESTIARY.bar(
                "§2Bestiary",
                if (FortuneTypes.BESTIARY.notSaved()) "§cBestiary fortune not saved\n§eOpen /bestiary to set it!"
                else "§7§2Fortune for killing pests\n§2You get 0.4☘ per bestiary milestone per pest",
            ),
        )

        content.addTable(
            3,
            FarmingFortuneInfos.GARDEN_PLOTS.bar(
                "§2Garden Plots",
                if (FortuneTypes.PLOTS.notSaved()) "§cUnlocked plot count not saved\n" +
                    "§eOpen /desk and view your plots to set it!"
                else "§7§2Fortune for unlocking garden plots\n§2You get 3☘ per plot unlocked",
            ),
        )

        content.addTable(
            4,
            FarmingFortuneInfos.ANITA_BUFF.bar(
                "§2Anita Buff",
                if (FortuneTypes.ANITA.notSaved()) "§cAnita buff not saved\n§eVisit Anita to set it!"
                else "§7§2Fortune for levelling your Anita extra crops\n§2You get 4☘ per buff level",
            ),
        )

        content.addTable(
            5,
            FarmingFortuneInfos.COMMUNITY_SHOP.bar(
                "§2Community upgrades",
                if (FortuneTypes.COMMUNITY_SHOP.notSaved()) "§cCommunity upgrade level not saved\n" +
                    "§eVisit Elizabeth to set it!"
                else "§7§2Fortune for community shop upgrades\n§2You get 4☘ per upgrade tier",
            ),
        )

        content.addTable(
            6,
            FarmingFortuneInfos.CAKE_BUFF.bar(
                "§2Cake Buff",
                when {
                    FarmingFortuneData.cakeExpireTime.isFarPast() ->
                        "§eYou have not eaten a cake since\n§edownloading this update, assuming the\n§ebuff is active!"

                    FarmingFortuneData.cakeExpireTime.isInPast() ->
                        "§cYour cake buff has run out\nGo eat some cake!"

                    else ->
                        "§7§2Fortune for eating cake\n§2You get 5☘ for eating cake\n" +
                            "§2Time until cake buff runs out: $timeUntilCakes"
                },
            ),
        )

        val moreInfo = "§2Select a piece for more info"
        val wordArmor = if (FarmingItems.currentArmor == null) "Armor" else "Piece"
        val armorName = FarmingItems.currentArmor?.getItem()?.displayName ?: ""

        content.addTable(
            1,
            FarmingFortuneInfos.TOTAL_ARMOR.bar(
                "§2Total $wordArmor Fortune",
                if (FarmingItems.currentArmor == null) "§7§2Total fortune from your armor\n$moreInfo"
                else "§7§2Total fortune from your\n$armorName",
            ),
        )

        content.addTable(
            2,
            FarmingFortuneInfos.BASE_ARMOR.bar(
                "§2Base $wordArmor Fortune",
                if (FarmingItems.currentArmor == null) "§7§2The base fortune from your armor\n$moreInfo"
                else "§7§2Base fortune from your\n$armorName",
            ),
        )

        content.addTable(
            3,
            FarmingFortuneInfos.ABILITY_ARMOR.bar(
                "§2$wordArmor Ability",
                if (FarmingItems.currentArmor == null) "§7§2The fortune from your armor's ability\n$moreInfo"
                else "§7§2Ability fortune from your\n$armorName",
            ),
        )

        content.addTable(
            4,
            FarmingFortuneInfos.REFORGE_ARMOR.bar(
                "§2$wordArmor Reforge",
                if (FarmingItems.currentArmor == null) "§7§2The fortune from your armor's reforge\n$moreInfo"
                else "§7§2Reforge fortune from your\n$armorName",
            ),
        )

        content.addTable(
            5,
            FarmingFortuneInfos.ENCHANT_ARMOR.bar(
                "§2$wordArmor Enchantment",
                if (FarmingItems.currentArmor == null) "§7§2The fortune from your armor's enchantments\n$moreInfo"
                else "§7§2Enchantment fortune from your\n$armorName",
            ),
        )

        content.addTable(
            6,
            FarmingFortuneInfos.GEMSTONE_ARMOR.bar(
                "§2$wordArmor Gemstones",
                if (FarmingItems.currentArmor == null) "§7§2The fortune from your armor's gemstones\n$moreInfo"
                else "§7§2Gemstone fortune from your\n$armorName",
            ),
        )

        val wordEquip = if (FarmingItems.currentEquip == null) "Equipment" else "Piece"

        val equipmentName = FarmingItems.currentEquip?.getItem()?.displayName ?: ""

        content.addTable(
            1,
            FarmingFortuneInfos.TOTAL_EQUIP.bar(
                "§2Total $wordEquip Fortune",
                if (FarmingItems.currentEquip == null) "§7§2Total fortune from your equipment\n$moreInfo"
                else "§7§2Total fortune from your\n$equipmentName",
            ),
        )

        content.addTable(
            2,
            FarmingFortuneInfos.BASE_EQUIP.bar(
                "§2$wordEquip Base Fortune",
                if (FarmingItems.currentEquip == null) "§7§2The base fortune from your equipment\n$moreInfo"
                else "§7§2Base fortune from your\n$equipmentName",
            ),
        )

        content.addTable(
            3,
            FarmingFortuneInfos.ABILITY_EQUIP.bar(
                "§2$wordEquip Ability",
                if (FarmingItems.currentEquip == null) "§7§2The fortune from your equipment's abilities\n$moreInfo"
                else "§7§2Ability fortune from your\n$equipmentName",
            ),
        )

        content.addTable(
            4,
            FarmingFortuneInfos.REFORGE_EQUIP.bar(
                "§2$wordEquip Reforge",
                if (FarmingItems.currentEquip == null) "§7§2The fortune from your equipment's reforges\n$moreInfo"
                else "§7§2Reforge fortune from your\n$equipmentName",
            ),
        )

        content.addTable(
            5,
            FarmingFortuneInfos.ENCHANT_EQUIP.bar(
                "§2$wordEquip Enchantment",
                if (FarmingItems.currentEquip == null) "§7§2The fortune from your equipment's enchantments\n$moreInfo"
                else "§7§2Enchantment fortune from your\n$equipmentName",
            ),
        )

        footer.add(
            Renderable.horizontalContainer(
                FarmingItems.getPetsDisplay(true),
                4,
                horizontalAlign = RenderUtils.HorizontalAlignment.CENTER,
                verticalAlign = RenderUtils.VerticalAlignment.CENTER,
            ),
        )

        footer.add(
            FarmingFortuneInfos.TOTAL_PET.bar(
                "§2Total Pet Fortune",
                "§7§2The total fortune from your pet and its item",
                72,
            ),
        )

        footer.add(
            FarmingFortuneInfos.PET_BASE.bar(
                "§2Base Pet Fortune",
                "§7§2The base fortune from your pet",
                72,
            ),
        )

        footer.add(
            FarmingFortuneInfos.PET_ITEM.bar(
                "§2Pet Item",
                when (FarmingFortuneData.currentPetItem) {
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
                FarmingItems.getArmorDisplay(true),
                4,
                horizontalAlign = RenderUtils.HorizontalAlignment.CENTER,
                verticalAlign = RenderUtils.VerticalAlignment.CENTER,
            ),
        )
        content.addTable(
            0,
            Renderable.horizontalContainer(
                FarmingItems.getEquipmentDisplay(true),
                4,
                horizontalAlign = RenderUtils.HorizontalAlignment.CENTER,
                verticalAlign = RenderUtils.VerticalAlignment.CENTER,
            ),
        )

        return content to footer
    }

    private fun FortuneTypes.notSaved(): Boolean = FarmingFortuneData.baseFF[this]?.let {
        it < 0.0
    } ?: true
}

private fun MutableList<MutableList<Renderable>>.addTable(row: Int, r: Renderable) {
    this.getOrNull(row)?.add(r) ?: mutableListOf(r).let { this.add(row, it) }
}
