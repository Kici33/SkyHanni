package at.hannibal2.skyhanni.features.guides.farming

import at.hannibal2.skyhanni.data.CropAccessoryData
import at.hannibal2.skyhanni.data.GardenCropUpgrades.getUpgradeLevel
import at.hannibal2.skyhanni.data.ProfileStorageData
import at.hannibal2.skyhanni.features.garden.CropType
import at.hannibal2.skyhanni.features.garden.FarmingFortuneDisplay
import at.hannibal2.skyhanni.features.garden.GardenAPI
import at.hannibal2.skyhanni.features.guides.FortuneTypes
import at.hannibal2.skyhanni.features.guides.UniversalGuideGUI
import at.hannibal2.skyhanni.utils.ItemUtils.getInternalName
import at.hannibal2.skyhanni.utils.SimpleTimeMark
import at.hannibal2.skyhanni.utils.SkyBlockItemModifierUtils.getFarmingForDummiesCount
import at.hannibal2.skyhanni.utils.SkyBlockItemModifierUtils.getPetItem
import at.hannibal2.skyhanni.utils.SkyBlockItemModifierUtils.getPetLevel
import net.minecraft.item.ItemStack
import kotlin.math.floor

object FarmingFortuneData {
    private val mathCrops by lazy {
        listOf(CropType.WHEAT, CropType.CARROT, CropType.POTATO, CropType.SUGAR_CANE, CropType.NETHER_WART)
    }
    private val dicerCrops by lazy { listOf(CropType.PUMPKIN, CropType.MELON) }

    private val farmingBoots = arrayListOf("RANCHERS_BOOTS", "FARMER_BOOTS")

    var cakeExpireTime
        get() = GardenAPI.storage?.fortune?.cakeExpiring ?: SimpleTimeMark.farPast()
        set(value) {
            GardenAPI.storage?.fortune?.cakeExpiring = value
        }

    var equipmentTotalFF = mapOf<FortuneTypes, Double>()

    var armorTotalFF = mapOf<FortuneTypes, Double>()
    var usingSpeedBoots = false

    var currentPetItem = ""

    var baseFF = mapOf<FortuneTypes, Double>()

    var totalBaseFF = mapOf<FortuneTypes, Double>()

    fun loadFFData() {
        equipmentTotalFF = FarmingItems.equip.getFFData()

        armorTotalFF = FarmingItems.armor.getFFData()
        usingSpeedBoots = FarmingItems.BOOTS.getItem().getInternalName().asString() in farmingBoots

        baseFF = getGenericFF()

        getTotalFF()
    }

    fun getCropStats(crop: CropType, tool: ItemStack?) {
        FarmingFortuneStats.reset()

        FarmingFortuneStats.BASE.set(FarmingFortuneInfos.UNIVERSAL.current, FarmingFortuneInfos.UNIVERSAL.max)
        FarmingFortuneStats.CROP_UPGRADE.set((crop.getUpgradeLevel()?.toDouble() ?: 0.0) * 5.0, 45.0)
        FarmingFortuneStats.ACCESSORY.set(CropAccessoryData.cropAccessory.getFortune(crop), 30.0)
        FarmingFortuneStats.FFD.set((tool?.getFarmingForDummiesCount() ?: 0).toDouble(), 5.0)
        FarmingFortuneStats.TURBO.set(FarmingFortuneDisplay.getTurboCropFortune(tool, crop), 25.0)
        FarmingFortuneStats.DEDICATION.set(FarmingFortuneDisplay.getDedicationFortune(tool, crop), 92.0)
        FarmingFortuneStats.CULTIVATING.set(FarmingFortuneDisplay.getCultivatingFortune(tool), 20.0)

        FarmingFortuneDisplay.loadFortuneLineData(tool, 0.0)

        when (crop) {
            in mathCrops -> {
                FarmingFortuneStats.BASE_TOOL.set(FarmingFortuneDisplay.getToolFortune(tool), 50.0)
                FarmingFortuneStats.COUNTER.set(FarmingFortuneDisplay.getCounterFortune(tool), 96.0)
                FarmingFortuneStats.HARVESTING.set(FarmingFortuneDisplay.getHarvestingFortune(tool), 75.0)
                FarmingFortuneStats.COLLECTION.set(FarmingFortuneDisplay.getCollectionFortune(tool), 48.0)
                FarmingFortuneStats.REFORGE.set(FarmingFortuneDisplay.reforgeFortune, 20.0)
                FarmingFortuneStats.GEMSTONE.set(FarmingFortuneDisplay.gemstoneFortune, 30.0)
            }

            in dicerCrops -> {
                FarmingFortuneStats.SUNDER.set(FarmingFortuneDisplay.getSunderFortune(tool), 75.0)
                FarmingFortuneStats.REFORGE.set(FarmingFortuneDisplay.reforgeFortune, 20.0)
                FarmingFortuneStats.GEMSTONE.set(FarmingFortuneDisplay.gemstoneFortune, 20.0)
            }

            CropType.MUSHROOM -> {
                FarmingFortuneStats.BASE_TOOL.set(FarmingFortuneDisplay.getToolFortune(tool), 30.0)
                FarmingFortuneStats.HARVESTING.set(FarmingFortuneDisplay.getHarvestingFortune(tool), 75.0)
                FarmingFortuneStats.REFORGE.set(FarmingFortuneDisplay.reforgeFortune, 16.0)
                FarmingFortuneStats.GEMSTONE.set(FarmingFortuneDisplay.gemstoneFortune, 16.0)
            }

            CropType.COCOA_BEANS -> {
                FarmingFortuneStats.BASE_TOOL.set(FarmingFortuneDisplay.getToolFortune(tool), 20.0)
                FarmingFortuneStats.SUNDER.set(FarmingFortuneDisplay.getSunderFortune(tool), 75.0)
                FarmingFortuneStats.REFORGE.set(FarmingFortuneDisplay.reforgeFortune, 16.0)
                FarmingFortuneStats.GEMSTONE.set(FarmingFortuneDisplay.gemstoneFortune, 16.0)
            }

            CropType.CACTUS -> {
                FarmingFortuneStats.HARVESTING.set(FarmingFortuneDisplay.getHarvestingFortune(tool), 75.0)
                FarmingFortuneStats.REFORGE.set(FarmingFortuneDisplay.reforgeFortune, 16.0)
                FarmingFortuneStats.GEMSTONE.set(FarmingFortuneDisplay.gemstoneFortune, 16.0)
            }

            else -> {}
        }
        CarrolynTable.getByCrop(crop)?.let {
            val ff = if (it.get()) 12.0 else 0.0
            FarmingFortuneStats.CARROLYN.set(ff, 12.0)
        }

        FarmingFortuneStats.CROP_TOTAL.set(FarmingFortuneStats.getTotal())
    }

    fun getEquipmentFFData(item: ItemStack?): Map<FortuneTypes, Double> = buildMap {
        FarmingFortuneDisplay.loadFortuneLineData(item, 0.0)
        this[FortuneTypes.BASE] = FarmingFortuneDisplay.itemBaseFortune
        this[FortuneTypes.REFORGE] = FarmingFortuneDisplay.reforgeFortune
        this[FortuneTypes.ENCHANT] = FarmingFortuneDisplay.greenThumbFortune
        this[FortuneTypes.ABILITY] = FarmingFortuneDisplay.getAbilityFortune(item)
        this[FortuneTypes.TOTAL] = this.values.sum()
    }

    fun getArmorFFData(item: ItemStack?): Map<FortuneTypes, Double> = buildMap {
        FarmingFortuneDisplay.loadFortuneLineData(item, 0.0)
        this[FortuneTypes.BASE] = FarmingFortuneDisplay.itemBaseFortune
        this[FortuneTypes.REFORGE] = FarmingFortuneDisplay.reforgeFortune
        this[FortuneTypes.GEMSTONE] = FarmingFortuneDisplay.gemstoneFortune
        this[FortuneTypes.ENCHANT] = FarmingFortuneDisplay.pesterminatorFortune
        this[FortuneTypes.ABILITY] = FarmingFortuneDisplay.getAbilityFortune(item)
        this[FortuneTypes.TOTAL] = this.values.sum()
    }

    fun getPetFFData(item: ItemStack?): Map<FortuneTypes, Double> = buildMap {
        val gardenLvl = GardenAPI.getGardenLevel(overflow = false)
        this[FortuneTypes.BASE] = getPetFF(item)
        this[FortuneTypes.PET_ITEM] = when (item?.getPetItem()) {
            "GREEN_BANDANA" -> 4.0 * gardenLvl
            "YELLOW_BANDANA" -> 30.0
            "MINOS_RELIC" -> (this[FortuneTypes.BASE] ?: 0.0) * .33
            else -> 0.0
        }
        this[FortuneTypes.TOTAL] = this.values.sum()
    }

    private fun getGenericFF(): Map<FortuneTypes, Double> = buildMap {
        val storage = GardenAPI.storage?.fortune ?: return emptyMap()
        println(storage);

        this[FortuneTypes.SKILL_LEVEL] = storage.farmingLevel.toDouble() * 4
        this[FortuneTypes.BESTIARY] = storage.bestiary
        this[FortuneTypes.PLOTS] = storage.plotsUnlocked.toDouble() * 3
        this[FortuneTypes.ANITA] = storage.anitaUpgrade.toDouble() * 4
        this[FortuneTypes.COMMUNITY_SHOP] = (ProfileStorageData.playerSpecific?.gardenCommunityUpgrade ?: -1).toDouble() * 4
        if (cakeExpireTime.isInFuture() || cakeExpireTime.isFarPast()) {
            this[FortuneTypes.CAKE] = 5.0
        } else {
            this[FortuneTypes.CAKE] = 0.0
        }
        this[FortuneTypes.TOTAL] = this.values.sum()
    }

    fun getTotalFF() {
        currentPetItem = FarmingItems.currentPet.getItem().getPetItem().toString()

        totalBaseFF = combineFFData(
            baseFF, armorTotalFF, equipmentTotalFF, FarmingItems.currentPet.getFFData(),
        )

        UniversalGuideGUI.updateDisplay()
    }

    fun List<FarmingItems>.getFFData(): Map<FortuneTypes, Double> = combineFFData(this.map { it.getFFData() })

    fun combineFFData(vararg value: Map<FortuneTypes, Double>) = combineFFData(value.toList())
    fun combineFFData(value: List<Map<FortuneTypes, Double>>) =
        value.map { it.toList() }.flatten().groupBy({ it.first }, { it.second })
            .mapValues { (_, values) -> values.sum() }

    private fun getPetFF(pet: ItemStack?): Double {
        if (pet == null) return 0.0
        val petLevel = pet.getPetLevel()
        val strength = (GardenAPI.storage?.fortune?.farmingStrength)
        if (strength != null) {
            val rawInternalName = pet.getInternalName()
            return when {
                rawInternalName.contains("ELEPHANT;4") -> 1.5 * petLevel
                rawInternalName.contains("MOOSHROOM_COW;4") -> {
                    (10 + petLevel).toDouble() + floor(floor(strength / (40 - petLevel * .2)) * .7)
                }

                rawInternalName.contains("MOOSHROOM") -> (10 + petLevel).toDouble()
                rawInternalName.contains("BEE;2") -> 0.2 * petLevel
                rawInternalName.contains("BEE;3") || rawInternalName.contains("BEE;4") -> 0.3 * petLevel
                rawInternalName.contains("SLUG;4") -> 1.0 * petLevel
                else -> 0.0
            }
        }
        return 0.0
    }
}
