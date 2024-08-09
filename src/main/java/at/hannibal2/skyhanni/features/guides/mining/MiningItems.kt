package at.hannibal2.skyhanni.features.guides.mining

import at.hannibal2.skyhanni.data.ProfileStorageData
import at.hannibal2.skyhanni.features.guides.FortuneTypes
import at.hannibal2.skyhanni.features.guides.UniversalGuideGUI
import at.hannibal2.skyhanni.utils.ItemCategory
import at.hannibal2.skyhanni.utils.RenderUtils
import at.hannibal2.skyhanni.utils.SoundUtils
import at.hannibal2.skyhanni.utils.renderables.Renderable
import net.minecraft.client.gui.GuiScreen
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack

enum class MiningItems(
    val itemCategory: ItemCategory,
    private val ffCalculation: (ItemStack?) -> Map<FortuneTypes, Double> = { emptyMap() },
) {
    HELMET(ItemCategory.HELMET, MiningFortuneData::getArmorMFData),
    CHESTPLATE(ItemCategory.CHESTPLATE, MiningFortuneData::getArmorMFData),
    LEGGINGS(ItemCategory.LEGGINGS, MiningFortuneData::getArmorMFData),
    BOOTS(ItemCategory.BOOTS, MiningFortuneData::getArmorMFData),

    NECKLACE(ItemCategory.NECKLACE, MiningFortuneData::getEquipmentMFData),
    CLOAK(ItemCategory.CLOAK, MiningFortuneData::getEquipmentMFData),
    BELT(ItemCategory.BELT, MiningFortuneData::getEquipmentMFData),
    BRACELET(ItemCategory.BRACELET, MiningFortuneData::getEquipmentMFData),

    SCATHA(ItemCategory.PET, MiningFortuneData::getPetMFData),
    GLACITE_GOLEM(ItemCategory.PET, MiningFortuneData::getPetMFData),
    BAL(ItemCategory.PET, MiningFortuneData::getPetMFData),
    SNAIL(ItemCategory.PET, MiningFortuneData::getPetMFData)
    ;

    var selectedState = false

    fun getItem() = getItemOrNull() ?: fallbackItem

    private val fallbackItem: ItemStack by lazy {
        val name = "§cNo saved ${name.lowercase().replace("_", " ")}"
        ItemStack(Blocks.barrier).setStackDisplayName(name)
    }

    fun getItemOrNull() = ProfileStorageData.profileSpecific?.mining?.fortune?.miningItems?.get(this)
    fun setItem(value: ItemStack) = ProfileStorageData.profileSpecific?.mining?.fortune?.miningItems?.set(this, value)

    private fun onClick(): () -> Unit = when (this) {
        in armor -> {
            {
                SoundUtils.playClickSound()
                currentArmor = if (selectedState) null else this
                armor.forEach {
                    it.selectedState = it == currentArmor
                }
                UniversalGuideGUI.updateDisplay()
            }
        }

        in equip -> {
            {
                SoundUtils.playClickSound()
                currentEquip = if (selectedState) null else this
                equip.forEach {
                    it.selectedState = it == currentEquip
                }
                UniversalGuideGUI.updateDisplay()
            }
        }

        in pets -> {
            {
                val prev = currentPet
                currentPet = if (selectedState) lastEquippedPet else this
                if (prev != currentPet) {
                    SoundUtils.playClickSound()
                }
                pets.forEach {
                    it.selectedState = it == currentPet
                }
                MiningFortuneData.getTotalMF()
            }
        }

        else -> {
            {}
        }
    }

    fun getDisplay(clickEnabled: Boolean = false) = object : Renderable {

        val content = Renderable.clickable(
            Renderable.itemStackWithTip(
                getItem(), 1.0, 0, 0, false,
            ),
            onClick = onClick(),
            condition = { clickEnabled },
        )

        override val width = content.width
        override val height = content.height
        override val horizontalAlign = RenderUtils.HorizontalAlignment.CENTER
        override val verticalAlign = RenderUtils.VerticalAlignment.CENTER

        override fun render(posX: Int, posY: Int) {
            GuiScreen.drawRect(
                0,
                0,
                width,
                height,
                if (selectedState) 0xFFB3FFB3.toInt() else 0xFF43464B.toInt(),
            )
            content.render(posX, posY)
        }
    }

    private var ffData: Map<FortuneTypes, Double>? = null

    fun getFortuneData() = ffData ?: run {
        val data = ffCalculation(getItemOrNull())
        ffData = data
        data
    }

    companion object {

        // TODO
        var lastEquippedPet = SCATHA

        var currentPet: MiningItems = lastEquippedPet
        var currentArmor: MiningItems? = null
        var currentEquip: MiningItems? = null

        val armor = listOf(HELMET, CHESTPLATE, LEGGINGS, BOOTS)
        val equip = listOf(NECKLACE, CLOAK, BELT, BRACELET)
        val pets = listOf(SCATHA, BAL)

        fun getArmorDisplay(clickEnabled: Boolean = false): List<Renderable> = armor.map { it.getDisplay(clickEnabled) }

        fun getEquipmentDisplay(clickEnabled: Boolean = false): List<Renderable> =
            equip.map { it.getDisplay(clickEnabled) }

        fun getPetsDisplay(clickEnabled: Boolean = false): List<Renderable> = pets.map { it.getDisplay(clickEnabled) }
        fun resetClickState() {
            entries.filterNot { pets.contains(it) }.forEach { it.selectedState = false }
        }

        fun resetMFData() {
            entries.forEach { it.ffData = null }
        }

        fun setDefaultPet(): MiningItems {
            currentPet = lastEquippedPet
            pets.forEach {
                it.selectedState = it == currentPet
            }
            return lastEquippedPet
        }

        fun getFromItemCategory(category: ItemCategory) = entries.filter { it.itemCategory == category }
        fun getFromItemCategoryOne(category: ItemCategory) = entries.firstOrNull { it.itemCategory == category }
    }
}
