package at.hannibal2.skyhanni.features.guides

import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.features.garden.CropType
import at.hannibal2.skyhanni.features.guides.farming.FarmingFortuneData
import at.hannibal2.skyhanni.features.guides.farming.FarmingFortuneUpgrades
import at.hannibal2.skyhanni.features.guides.farming.FarmingItems
import at.hannibal2.skyhanni.features.guides.farming.pages.CropPage
import at.hannibal2.skyhanni.features.guides.farming.pages.FarmingFortuneOverviewPage
import at.hannibal2.skyhanni.features.guides.farming.pages.FarmingFortuneUpgradePage
import at.hannibal2.skyhanni.utils.guide.GuideGUI
import at.hannibal2.skyhanni.utils.guide.GuideTab
import at.hannibal2.skyhanni.utils.renderables.Renderable
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

class UniversalGuideGUI constructor(guideType: GuideType) : GuideGUI<UniversalGuideGUI.FortuneGuidePage>(FortuneGuidePage.OVERVIEW) {

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

    init {

        FarmingItems.setDefaultPet()

        when (guideType) {
            GuideType.FARMING -> {
                FarmingFortuneData.loadFFData()
                FarmingFortuneUpgrades.generateGenericUpgrades()

                 var currentCrop: CropType? = null

                pageList = mapOf(
                    FortuneGuidePage.OVERVIEW to FarmingFortuneOverviewPage(sizeX, sizeY),
                    FortuneGuidePage.SPECIFIC to CropPage({ currentCrop!! }, sizeX, sizeY),
                    FortuneGuidePage.UPGRADES to FarmingFortuneUpgradePage({ currentCrop }, sizeX, sizeY - 2),
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
            }

            GuideType.MINING -> {
                pageList = mapOf(
//                     FortuneGuidePage.OVERVIEW to MiningFortuneOverviewPage(sizeX, sizeY)
//                     FortuneGuidePage.SPECIFIC to OrePage(sizeX, sizeY),
//                     FortuneGuidePage.UPGRADES to MiningFortuneUpgradePage(guideType, sizeX, sizeY - 2),
                )
            }

            GuideType.FORAGING -> TODO()
            GuideType.FISHING -> TODO()
            GuideType.MAGIC_FIND -> TODO()
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
