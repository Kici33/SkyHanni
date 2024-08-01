package at.hannibal2.skyhanni.features.guides

import at.hannibal2.skyhanni.features.guides.farming.FarmingFortuneInfo
import at.hannibal2.skyhanni.features.guides.mining.MiningFortuneInfo

enum class GuideType(
    private val fortuneInfo: FortuneInfo? = null,
    private val universalLabel: String,
    private val universalTooltip: String
) {

    FARMING(
        FarmingFortuneInfo(),
        "§6Universal Farming Fortune",
        "§7§2Farming fortune in that is\n§2applied to every crop\n§eNot the same as tab FF\n§eSee on the grass block page"
    ),

    MINING(
        MiningFortuneInfo(),
        "§6Universal Mining Fortune",
        "§7§2Mining fortune in that is\n§2applied to every ore\n§eNot the same as tab FF\n§eSee on the iron pickaxe page",
    );

    fun getFortuneInfo(): FortuneInfo? {
        return fortuneInfo
    }

    fun getUniversalLabel(): String {
        return universalLabel
    }

    fun getUniversalTooltip(): String {
        return universalTooltip
    }

}
