package at.hannibal2.skyhanni.features.guides.mining

import at.hannibal2.skyhanni.config.ConfigUpdaterMigrator
import at.hannibal2.skyhanni.data.MiningAPI
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.ItemUtils.getLore
import at.hannibal2.skyhanni.utils.RegexUtils.groupOrNull
import at.hannibal2.skyhanni.utils.RegexUtils.matchMatcher
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@SkyHanniModule
object MiningFortuneDisplay {
    private val config get() = MiningAPI.config.miningFortunes

    private val patternGroup = RepoPattern.group("mining.fortunedisplay")

    private val tooltipFortunePattern by patternGroup.pattern(
        "tooltip.new",
        "^§7Mining Fortune: §a\\+(?<display>[\\d.]+)(?: §2\\(\\+\\d\\))?(?: §9\\(\\+(?<reforge>\\d+)\\))?(?: §d\\(\\+(?<gemstone>\\d+)\\))?\$"
    )

    var displayedFortune = 0.0
    var reforgeFortune = 0.0
    var gemstoneFortune = 0.0
    var itemBaseFortune = 0.0

    fun loadFortuneLineData(tool: ItemStack?) {
        itemBaseFortune = 0.0
        reforgeFortune = 0.0
        gemstoneFortune = 0.0

        // TODO code cleanup (after ff rework)

        val lore = tool?.getLore() ?: return
        for (line in lore) {
            tooltipFortunePattern.matchMatcher(line) {
                displayedFortune = group("display")?.toDouble() ?: 0.0
                reforgeFortune = groupOrNull("reforge")?.toDouble() ?: 0.0
                gemstoneFortune = groupOrNull("gemstone")?.toDouble() ?: 0.0
                itemBaseFortune = displayedFortune - reforgeFortune - gemstoneFortune
            } ?: continue
        }
    }

    @SubscribeEvent
    fun onConfigFix(event: ConfigUpdaterMigrator.ConfigFixEvent) {
        event.move(3, "mining.miningFortuneDisplay", "mining.miningFortunes.display")
        event.move(3, "mining.miningFortuneDropMultiplier", "mining.miningFortunes.dropMultiplier")
        event.move(3, "mining.miningFortunePos", "mining.miningFortunes.pos")
    }
}
