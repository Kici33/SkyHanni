package at.hannibal2.skyhanni.features.guides.mining

import at.hannibal2.skyhanni.api.HotmAPI
import at.hannibal2.skyhanni.api.SkillAPI
import at.hannibal2.skyhanni.config.storage.PlayerSpecificStorage
import at.hannibal2.skyhanni.data.MiningAPI
import at.hannibal2.skyhanni.data.ProfileStorageData
import at.hannibal2.skyhanni.data.SkillExperience
import at.hannibal2.skyhanni.features.garden.CropType
import at.hannibal2.skyhanni.features.garden.GardenAPI
import at.hannibal2.skyhanni.features.guides.FortuneTypes
import at.hannibal2.skyhanni.features.guides.UniversalGuideGUI
import at.hannibal2.skyhanni.features.skillprogress.SkillType
import at.hannibal2.skyhanni.utils.CollectionUtils.sumAllValues
import at.hannibal2.skyhanni.utils.ItemUtils.getInternalName
import at.hannibal2.skyhanni.utils.SimpleTimeMark
import at.hannibal2.skyhanni.utils.SkyBlockItemModifierUtils.getPetItem
import at.hannibal2.skyhanni.utils.SkyBlockItemModifierUtils.getPetLevel
import io.github.moulberry.notenoughupdates.options.seperateSections.Mining
import net.minecraft.item.ItemStack
import kotlin.math.floor

object MiningFortuneData {

    var cakeExpireTime
        get() = GardenAPI.storage?.fortune?.cakeExpiring ?: SimpleTimeMark.farPast()
        set(value) {
            GardenAPI.storage?.fortune?.cakeExpiring = value
        }

    var equipmentTotalMF = mapOf<FortuneTypes, Double>()

    var armorTotalMF = mapOf<FortuneTypes, Double>()
    var usingSpeedBoots = false

    var currentPetItem = ""

    var baseMF = mapOf<FortuneTypes, Double>()

    var totalBaseMF = mapOf<FortuneTypes, Double>()

    fun loadMFData() {
        equipmentTotalMF = MiningItems.equip.getMFData()

        armorTotalMF = MiningItems.armor.getMFData()

        baseMF = getGenericMF()

        getTotalMF()
    }

    fun getEquipmentMFData(item: ItemStack?): Map<FortuneTypes, Double> = buildMap {
        MiningFortuneDisplay.loadFortuneLineData(item)
        this[FortuneTypes.BASE] = MiningFortuneDisplay.itemBaseFortune
        this[FortuneTypes.REFORGE] = MiningFortuneDisplay.reforgeFortune
        this[FortuneTypes.TOTAL] = this.values.sum()
    }

    fun getArmorMFData(item: ItemStack?): Map<FortuneTypes, Double> = buildMap {
        MiningFortuneDisplay.loadFortuneLineData(item)
        this[FortuneTypes.BASE] = MiningFortuneDisplay.itemBaseFortune
        this[FortuneTypes.REFORGE] = MiningFortuneDisplay.reforgeFortune
        this[FortuneTypes.GEMSTONE] = MiningFortuneDisplay.gemstoneFortune
        this[FortuneTypes.TOTAL] = this.values.sum()
    }

    fun getPetMFData(item: ItemStack?): Map<FortuneTypes, Double> = buildMap {
        this[FortuneTypes.BASE] = getPetMF(item)
        this[FortuneTypes.PET_ITEM] = when (item?.getPetItem()) {
            "QUICK_CLAW" -> (item.getPetLevel()/2).toDouble()
            "BEJEWELED_COLLAR" -> 10.0
            else -> 0.0
        }
        this[FortuneTypes.TOTAL] = this.values.sum()
    }

    private fun getGenericMF(): Map<FortuneTypes, Double> = buildMap {
        val storage = ProfileStorageData.profileSpecific?.mining?.fortune ?: return emptyMap()
        this[FortuneTypes.SKILL_LEVEL] = storage.miningLevel.times(4.0);

        val tree = HotmAPI.copyCurrentTree()
        var hotmFortune = 0.0

        tree?.perks?.get("MINING_FORTUNE")?.level?.let {
            hotmFortune = it.times(5).toDouble()
        }

        tree?.perks?.get("MINING_MADNESS")?.isUnlocked?.let {
            if (it) {
                hotmFortune += 50.0
            }
        }

        tree?.perks?.get("MINING_FORTUNE_II")?.level?.let {
            hotmFortune += it.times(5).toDouble()
        }

        this[FortuneTypes.HOTM] = hotmFortune
        if (cakeExpireTime.isInFuture() || cakeExpireTime.isFarPast()) {
            this[FortuneTypes.CAKE] = 5.0
        } else {
            this[FortuneTypes.CAKE] = 0.0
        }
        this[FortuneTypes.TOTAL] = this.values.sum()
    }

    fun getTotalMF() {
        currentPetItem = MiningItems.currentPet.getItem().getPetItem().toString()

        totalBaseMF = combineMFData(
            baseMF, armorTotalMF, equipmentTotalMF, MiningItems.currentPet.getFortuneData(),
        )

        UniversalGuideGUI.updateDisplay()
    }

    fun List<MiningItems>.getMFData(): Map<FortuneTypes, Double> = combineMFData(this.map { it.getFortuneData() })

    fun combineMFData(vararg value: Map<FortuneTypes, Double>) = combineMFData(value.toList())
    fun combineMFData(value: List<Map<FortuneTypes, Double>>) =
        value.map { it.toList() }.flatten().groupBy({ it.first }, { it.second })
            .mapValues { (_, values) -> values.sum() }

    private fun getPetMF(pet: ItemStack?): Double {
        if (pet == null) return 0.0
        val petLevel = pet.getPetLevel()
        val rawInternalName = pet.getInternalName()
        return when {
            rawInternalName.contains("SCATHA;2") -> 1.0 * petLevel
            rawInternalName.contains("SCATHA;3") -> 1.25 * petLevel
            rawInternalName.contains("SCATHA;4") -> 1.25 * petLevel
            else -> 0.0
        }
    }
}
