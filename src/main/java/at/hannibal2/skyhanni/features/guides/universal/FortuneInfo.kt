package at.hannibal2.skyhanni.features.guides.universal

import at.hannibal2.skyhanni.utils.GuiRenderUtils
import at.hannibal2.skyhanni.utils.renderables.Renderable

interface FortuneInfo {

    fun getUniversal(): Int;
    fun getSkillLevel(): Int;
    fun getBestiary(): Int;
    fun getGardenPlots(): Int;
    fun getAnitaBuff(): Int;
    fun getCommunityShop(): Int;
    fun getCakeBuff(): Int;
    fun getTotalArmor(): Int;
    fun getBaseArmor(): Int;
    fun getArmorAbility(): Int;
    fun getArmorReforges(): Int;
    fun getArmorEnchants(): Int;
    fun getArmorGemstones(): Int;
    fun getTotalPet(): Int;
    fun getBasePet(): Int;
    fun getPetItem(): Int;
    fun getTotalEquipment(): Int;
    fun getBaseEquipment(): Int;
    fun getEquipmentAbility(): Int;
    fun getEquipmentReforges(): Int;
    fun getEquipmentEnchants(): Int;
    fun getEquipmentGemstones(): Int;

    fun bar(label: String, toolTip: String, width: Int = 90): Renderable = GuiRenderUtils.getFortuneBar(
        label,
        toolTip,
        getUniversal().toDouble(),
        100.0,
        width
    )

}
