package at.hannibal2.skyhanni.features.guides.farming.pages

import at.hannibal2.skyhanni.features.garden.CropType
import at.hannibal2.skyhanni.features.guides.farming.FarmingFortuneData
import at.hannibal2.skyhanni.features.guides.farming.FarmingFortuneStats
import at.hannibal2.skyhanni.features.guides.farming.FarmingItems
import at.hannibal2.skyhanni.utils.CollectionUtils.split
import at.hannibal2.skyhanni.utils.GuiRenderUtils
import at.hannibal2.skyhanni.utils.RenderUtils
import at.hannibal2.skyhanni.utils.guide.GuideTablePage
import at.hannibal2.skyhanni.utils.renderables.Renderable

class CropPage(val crop0: () -> CropType, sizeX: Int, sizeY: Int, paddingX: Int = 15, paddingY: Int = 7) :
    GuideTablePage(
        sizeX, sizeY, paddingX, paddingY,
    ) {

    val crop get() = crop0()

    override fun onEnter() {
        val item = crop.farmingItem
        FarmingFortuneData.getCropStats(crop, item.getItemOrNull())

        FarmingItems.resetClickState()
        val toolLines = toolLines().split().map { Renderable.verticalContainer(it, 2) }
        update(
            listOf(
                header(),
                listOf(
                    toolLines[0],
                    equipDisplay(),
                    toolLines[1],
                ),
            ),
            emptyList(),
        )
    }

    private fun header(): List<Renderable> = buildList {
        add(FarmingFortuneStats.BASE.getFarmingBar())
        add(FarmingFortuneStats.CROP_TOTAL.getFarmingBar(110))
        add(FarmingFortuneStats.CROP_UPGRADE.getFarmingBar())
    }

    private fun FarmingFortuneStats.getFarmingBar(
        width: Int = 90,
    ) = Renderable.clickable(
        GuiRenderUtils.getFortuneBar(label(crop), tooltip(crop), current, max, width), { onClick(crop) },
    )

    private fun toolLines(): List<Renderable> =
        FarmingFortuneStats.entries.filter { it.isActive() && it !in headers }.map { it.getFarmingBar() }

    private fun equipDisplay(): Renderable =
        Renderable.fixedSizeColumn(
            Renderable.verticalContainer(
                listOf(
                    crop.farmingItem.getDisplay(),
                    Renderable.horizontalContainer(
                        listOf(
                            Renderable.verticalContainer(FarmingItems.getArmorDisplay(), 2),
                            Renderable.verticalContainer(FarmingItems.getEquipmentDisplay(), 2),
                        ),
                        2,
                        horizontalAlign = RenderUtils.HorizontalAlignment.CENTER,
                    ),
                    Renderable.horizontalContainer(FarmingItems.getPetsDisplay(true), 2),
                ),
                2,
                verticalAlign = RenderUtils.VerticalAlignment.BOTTOM,
            ),
            164,
            horizontalAlign = RenderUtils.HorizontalAlignment.CENTER,
            verticalAlign = RenderUtils.VerticalAlignment.BOTTOM,
        )

    companion object {
        private val headers = setOf(FarmingFortuneStats.BASE, FarmingFortuneStats.CROP_TOTAL, FarmingFortuneStats.CROP_UPGRADE)
    }
}
