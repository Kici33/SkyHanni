package at.hannibal2.skyhanni.features.guides.universal

import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.features.garden.CropType
import at.hannibal2.skyhanni.features.guides.farming.FFStats
import at.hannibal2.skyhanni.features.guides.farming.FarmingItems
import at.hannibal2.skyhanni.features.guides.farming.FortuneUpgrades
import at.hannibal2.skyhanni.features.guides.universal.pages.FortuneOverviewPage
import at.hannibal2.skyhanni.utils.guide.GuideGUI
import at.hannibal2.skyhanni.utils.guide.GuideTab
import at.hannibal2.skyhanni.utils.renderables.Renderable
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

class UniversalGuideGUI constructor(guideType: GuideType) : GuideGUI<UniversalGuideGUI.FortuneGuidePage>(UniversalGuideGUI.FortuneGuidePage.OVERVIEW) {

    override val sizeX = 360
    override val sizeY = 200

    companion object {

        fun isInGui() = Minecraft.getMinecraft().currentScreen is UniversalGuideGUI

        fun open(guideType: GuideType) {
            SkyHanniMod.screenToOpen = UniversalGuideGUI(guideType)
        }

        fun updateDisplay() {
            with(Minecraft.getMinecraft().currentScreen) {
                if (this !is UniversalGuideGUI) return
                this.refreshPage()
            }
        }
    }

    /** Value for which crop page is active */
    private var currentCrop: CropType? = null

    init {
        FFStats.loadFFData()
        FortuneUpgrades.generateGenericUpgrades()

        FarmingItems.setDefaultPet()

        pageList = mapOf(
            FortuneGuidePage.OVERVIEW to FortuneOverviewPage(guideType, sizeX, sizeY),
//             FortuneGuidePage.SPECIFIC to SpecificPage({ currentCrop!! }, sizeX, sizeY),
//             FortuneGuidePage.UPGRADES to FortuneUpgradePage(guideType, { currentCrop }, sizeX, sizeY - 2),
        )
        verticalTabs = listOf(
            vTab(ItemStack(Items.gold_ingot), Renderable.string("§eBreakdown")) {
                currentPage = if (currentCrop == null) FortuneGuidePage.OVERVIEW else FortuneGuidePage.SPECIFIC
            },
            vTab(ItemStack(Items.map), Renderable.string("§eUpgrades")) {
                currentPage = FortuneGuidePage.UPGRADES
            })
        horizontalTabs = buildList {
            add(
                hTab(ItemStack(Blocks.grass), Renderable.string("§eOverview")) {
                    currentCrop = null

                    it.pageSwitchHorizontal()
                }
            )
            for (crop in CropType.entries) {
                add(
                    hTab(crop.icon, Renderable.string("§e${crop.cropName}")) {
                        currentCrop = crop

                        it.pageSwitchHorizontal()
                    }
                )
            }
        }
        horizontalTabs.firstOrNull()?.fakeClick()
        verticalTabs.firstOrNull()?.fakeClick()
    }

    private fun GuideTab.pageSwitchHorizontal() {
        if (isSelected()) {
            verticalTabs.first { it != lastVerticalTabWrapper.tab }.fakeClick() // Double Click Logic
        } else {
            lastVerticalTabWrapper.tab?.fakeClick() // First Click Logic
        }
    }

    enum class FortuneGuidePage {
        OVERVIEW,
        SPECIFIC,
        UPGRADES,
    }

}
