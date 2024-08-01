package at.hannibal2.skyhanni.features.guides.farming.pages

import at.hannibal2.skyhanni.features.guides.GuideType
import at.hannibal2.skyhanni.utils.RenderUtils
import at.hannibal2.skyhanni.utils.guide.GuideTablePage
import at.hannibal2.skyhanni.utils.renderables.Renderable

class FortuneOverviewPage(private val guideType: GuideType, sizeX: Int, sizeY: Int, paddingX: Int = 15, paddingY: Int = 7, footerSpacing: Int = 6) :
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

        content.addTable(
            0,
            guideType.getFortuneInfo()?.bar(
                guideType.getUniversalLabel(),
                guideType.getUniversalTooltip(),
            ),
        )

        content.addTable(
            1,
            FInfos.SKILL_LEVEL.bar(
                guideType,
                guideType.getSkillLevel(),
                guideType.getSkillTooltip(),
            ),
        )


        content.addTable(
            0,
            Renderable.horizontalContainer(
                FarmingItems.getArmorDisplay(true),
                4,
                horizontalAlign = RenderUtils.HorizontalAlignment.CENTER,
                verticalAlign = RenderUtils.VerticalAlignment.CENTER,
            )
        )

        return content to footer
    }

}
