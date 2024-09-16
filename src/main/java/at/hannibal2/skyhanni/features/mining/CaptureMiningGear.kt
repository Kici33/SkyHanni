package at.hannibal2.skyhanni.features.mining

import at.hannibal2.skyhanni.config.storage.ProfileSpecificStorage
import at.hannibal2.skyhanni.data.PetAPI
import at.hannibal2.skyhanni.data.ProfileStorageData
import at.hannibal2.skyhanni.events.InventoryFullyOpenedEvent
import at.hannibal2.skyhanni.events.LorenzChatEvent
import at.hannibal2.skyhanni.events.MiningToolChangeEvent
import at.hannibal2.skyhanni.features.guides.mining.MiningFortuneData
import at.hannibal2.skyhanni.features.guides.mining.MiningFortuneDisplay
import at.hannibal2.skyhanni.features.guides.mining.MiningItems
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.InventoryUtils
import at.hannibal2.skyhanni.utils.ItemCategory
import at.hannibal2.skyhanni.utils.ItemUtils.getInternalName
import at.hannibal2.skyhanni.utils.ItemUtils.getItemCategoryOrNull
import at.hannibal2.skyhanni.utils.ItemUtils.getItemRarityOrNull
import at.hannibal2.skyhanni.utils.LorenzUtils
import at.hannibal2.skyhanni.utils.NumberUtil.romanToDecimalIfNecessary
import at.hannibal2.skyhanni.utils.RegexUtils.matchMatcher
import at.hannibal2.skyhanni.utils.SimpleTimeMark.Companion.fromNow
import at.hannibal2.skyhanni.utils.StringUtils.removeColor
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.days

@SkyHanniModule
object CaptureMiningGear {
    private val outdatedItems get() = ProfileStorageData.profileSpecific?.mining?.fortune?.outdatedItems

    private val patternGroup = RepoPattern.group("mining.fortuneguide.capture")

    private val miningLevelPattern by patternGroup.pattern(
        "mininglevel",
        "SKILL LEVEL UP Mining .*➜(?<level>.*)",
    )

    private val cakePattern by patternGroup.pattern(
        "cake",
        "(?:Big )?Yum! You (?:gain|refresh) [+]5☘ Mining Fortune for 48 hours!",
    )

    private val petLevelUpPattern by patternGroup.pattern(
        "petlevelup",
        "Your (?<pet>.*) leveled up to level .*!",
    )

    private val miningSets = arrayListOf(
        "SORROW", "DIVAN", "GOBLIN"
    )

    private val equipment = arrayListOf(
        "AMBER", "AMETHYST", "ANCIENT", "DWARVEN",
        "GLOWSTONE", "JADE", "MITHRIL", "PENDANT",
        "SAPPHIRE", "TITANIUM", "VANQUISHED"
    )

    // TODO update armor on equipment/wardrobe update as well
    fun captureMiningGear() {
        for (armor in InventoryUtils.getArmor()) {
            if (armor == null) continue
            val split = armor.getInternalName().asString().split("_")
            if (split.first() in miningSets) {
                val category = armor.getItemCategoryOrNull() ?: continue
                MiningItems.getFromItemCategoryOne(category)?.setItem(armor)
            }
        }
    }

    @SubscribeEvent
    fun onGardenToolChange(event: MiningToolChangeEvent) {
        captureMiningGear()
    }

    @SubscribeEvent
    fun onInventoryOpen(event: InventoryFullyOpenedEvent) {
        if (!LorenzUtils.inSkyBlock) return
        val storage = ProfileStorageData.profileSpecific?.mining?.fortune ?: return;
        val outdatedItems = outdatedItems ?: return
        val items = event.inventoryItems
        if (PetAPI.isPetMenu(event.inventoryName)) {
            pets(items, outdatedItems)
            return
        }
        when (event.inventoryName) {
            "Your Equipment and Stats" -> equipmentAndStats(items, outdatedItems)
            "Your Skills" -> skills(items, storage)
        }
    }

    private fun skills(
        items: Map<Int, ItemStack>,
        storage: ProfileSpecificStorage.MiningConfig.MiningFortune,
    ) {
        for ((_, item) in items) {
            if (item.displayName.contains("Mining ")) {
                storage.miningLevel = item.displayName.split(" ").last().romanToDecimalIfNecessary()
            }
        }
    }

    private fun pets(
        items: Map<Int, ItemStack>,
        outdatedItems: MutableMap<MiningItems, Boolean>,
    ) {
        // If they've 2 of same pet, one will be overwritten

        // setting to current saved level -1 to stop later pages saving low rarity pets
        var highestScathaRarity = (MiningItems.SCATHA.getItemOrNull()?.getItemRarityOrNull()?.id ?: -1) - 1
        var highestSnailRarity = (MiningItems.SNAIL.getItemOrNull()?.getItemRarityOrNull()?.id ?: -1) - 1
        var highestBalRarity = (MiningItems.BAL.getItemOrNull()?.getItemRarityOrNull()?.id ?: -1) - 1
        var highestGlaciteGolemRarity = (MiningItems.GLACITE_GOLEM.getItemOrNull()?.getItemRarityOrNull()?.id ?: -1) - 1

        for ((_, item) in items) {
            if (item.getItemCategoryOrNull() != ItemCategory.PET) continue
            val (name, rarity) = item.getInternalName().asString().split(";")
            if (name == "SCATHA" && rarity.toInt() > highestScathaRarity) {
                MiningItems.SCATHA.setItem(item)
                outdatedItems[MiningItems.SCATHA] = false
                highestScathaRarity = rarity.toInt()
            }
            if (name == "SNAIL" && rarity.toInt() > highestSnailRarity) {
                MiningItems.SNAIL.setItem(item)
                outdatedItems[MiningItems.SNAIL] = false
                highestSnailRarity = rarity.toInt()
            }
            if (name == "BAL" && rarity.toInt() > highestBalRarity) {
                MiningItems.BAL.setItem(item)
                outdatedItems[MiningItems.BAL] = false
                highestBalRarity = rarity.toInt()
            }
            if (name == "GLACITE_GOLEM" && rarity.toInt() > highestGlaciteGolemRarity) {
                MiningItems.GLACITE_GOLEM.setItem(item)
                outdatedItems[MiningItems.GLACITE_GOLEM] = false
                highestGlaciteGolemRarity = rarity.toInt()
            }
        }
    }

    private fun equipmentAndStats(
        items: Map<Int, ItemStack>,
        outdatedItems: MutableMap<MiningItems, Boolean>,
    ) {
        for ((_, slot) in items) {
            val split = slot.getInternalName().asString().split("_")
            val category = slot.getItemCategoryOrNull() ?: continue
            if (split.first() in equipment) {
                val item = MiningItems.getFromItemCategoryOne(category) ?: continue
                item.setItem(slot)
                outdatedItems[item] = false
                MiningFortuneDisplay.loadFortuneLineData(slot)
            }
        }
    }

    @SubscribeEvent
    fun onChat(event: LorenzChatEvent) {
        if (!LorenzUtils.inSkyBlock) return
        val storage = ProfileStorageData.profileSpecific?.mining?.fortune ?: return
        val outdatedItems = outdatedItems ?: return
        val msg = event.message.removeColor().trim()
        miningLevelPattern.matchMatcher(msg) {
            storage.miningLevel = group("level").romanToDecimalIfNecessary()
            return
        }
        petLevelUpPattern.matchMatcher(msg) {
            val pet = group("pet").uppercase().replace("✦", "").trim().replace(" ", "_")
            for (item in MiningItems.entries) {
                if (item.name.contains(pet)) {
                    outdatedItems[item] = true
                }
            }
            return
        }
        cakePattern.matchMatcher(msg) {
            MiningFortuneData.cakeExpireTime = 2.days.fromNow()
            return
        }
    }

}
