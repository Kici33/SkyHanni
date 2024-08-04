package at.hannibal2.skyhanni.features.guides.farming.pages

import at.hannibal2.skyhanni.features.garden.CropType
import at.hannibal2.skyhanni.features.guides.farming.FarmingFortuneUpgrade
import at.hannibal2.skyhanni.features.guides.farming.FarmingFortuneUpgrades
import at.hannibal2.skyhanni.features.guides.farming.FarmingItems
import at.hannibal2.skyhanni.utils.ItemUtils.itemName
import at.hannibal2.skyhanni.utils.NEUItems.getItemStack
import at.hannibal2.skyhanni.utils.NumberUtil.shortFormat
import at.hannibal2.skyhanni.utils.RenderUtils
import at.hannibal2.skyhanni.utils.guide.GuideScrollPage
import at.hannibal2.skyhanni.utils.renderables.Renderable
import at.hannibal2.skyhanni.utils.RenderUtils.HorizontalAlignment
import at.hannibal2.skyhanni.utils.RenderUtils.VerticalAlignment
import java.text.DecimalFormat

class FarmingFortuneUpgradePage(val crop0: () -> CropType?, sizeX: Int, sizeY: Int, paddingX: Int = 15, paddingY: Int = 7) :
    GuideScrollPage(
        sizeX,
        sizeY,
        paddingX,
        paddingY,
        marginY = 10,
        hasHeader = true,
    ) {

    val crop get() = crop0()

    override fun onEnter() {
        crop?.let {
            FarmingFortuneUpgrades.getCropSpecific(it.farmingItem.getItemOrNull())
        } ?: {
            FarmingFortuneUpgrades.getCropSpecific(null) // TODO
        }

        FarmingItems.resetClickState()
        update(
            content = buildList {
                add(header())
                val upgradeList = if (crop == null)
                    FarmingFortuneUpgrades.genericUpgrades
                else
                    FarmingFortuneUpgrades.cropSpecificUpgrades
                addAll(upgradeList.map { upgrade -> upgrade.print() })
            }
        )
    }

    private fun header() = listOf("Upgrade", "", "Item", "FF", "Cost/FF", "Total").map {
        Renderable.string(
            it,
            0.9,
            horizontalAlign = RenderUtils.HorizontalAlignment.CENTER
        )
    }

    private fun FarmingFortuneUpgrade.print() = buildList {
        add(
            Renderable.wrappedString(
                description,
                136,
                0.75,
                verticalAlign = VerticalAlignment.CENTER
            )
        )
        add(
            Renderable.itemStackWithTip(
                requiredItem.getItemStack(),
                8.0 / 9.0,
                verticalAlign = VerticalAlignment.CENTER
            )
        )
        add(
            Renderable.wrappedString(
                requiredItem.itemName.let { if (itemQuantity == 1) it else "$it §fx$itemQuantity" }, // TODO wtf
                70,
                0.75,
                verticalAlign = VerticalAlignment.CENTER
            )
        )
        add(
            Renderable.string(
                "§a${DecimalFormat("0.##").format(fortuneIncrease)}",
                horizontalAlign = HorizontalAlignment.CENTER,
                verticalAlign = VerticalAlignment.CENTER
            )
        ) // TODO cleaner formating
        add(
            Renderable.string(
                "§6" + costPerFF?.shortFormat(),
                horizontalAlign = HorizontalAlignment.CENTER,
                verticalAlign = VerticalAlignment.CENTER
            )
        )
        add(
            Renderable.string(
                "§6" + cost?.shortFormat(),
                horizontalAlign = HorizontalAlignment.CENTER,
                verticalAlign = VerticalAlignment.CENTER
            )
        )
    }
}
