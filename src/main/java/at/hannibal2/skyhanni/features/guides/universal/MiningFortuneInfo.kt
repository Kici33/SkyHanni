package at.hannibal2.skyhanni.features.guides.universal

class MiningFortuneInfo : FortuneInfo {
    override fun getUniversal(): Int {
        return getSkillLevel() + getBestiary() + getGardenPlots() + getAnitaBuff() +
            getCommunityShop() + getCakeBuff() + getTotalArmor() + getTotalPet() + getTotalEquipment();
    }

    override fun getSkillLevel(): Int {
        TODO("Not yet implemented")
    }

    override fun getBestiary(): Int {
        TODO("Not yet implemented")
    }

    override fun getGardenPlots(): Int {
        TODO("Not yet implemented")
    }

    override fun getAnitaBuff(): Int {
        TODO("Not yet implemented")
    }

    override fun getCommunityShop(): Int {
        TODO("Not yet implemented")
    }

    override fun getCakeBuff(): Int {
        TODO("Not yet implemented")
    }

    override fun getTotalArmor(): Int {
        TODO("Not yet implemented")
    }

    override fun getBaseArmor(): Int {
        TODO("Not yet implemented")
    }

    override fun getArmorAbility(): Int {
        TODO("Not yet implemented")
    }

    override fun getArmorReforges(): Int {
        TODO("Not yet implemented")
    }

    override fun getArmorEnchants(): Int {
        TODO("Not yet implemented")
    }

    override fun getArmorGemstones(): Int {
        TODO("Not yet implemented")
    }

    override fun getTotalPet(): Int {
        TODO("Not yet implemented")
    }

    override fun getBasePet(): Int {
        TODO("Not yet implemented")
    }

    override fun getPetItem(): Int {
        TODO("Not yet implemented")
    }

    override fun getTotalEquipment(): Int {
        TODO("Not yet implemented")
    }

    override fun getBaseEquipment(): Int {
        TODO("Not yet implemented")
    }

    override fun getEquipmentAbility(): Int {
        TODO("Not yet implemented")
    }

    override fun getEquipmentReforges(): Int {
        TODO("Not yet implemented")
    }

    override fun getEquipmentEnchants(): Int {
        TODO("Not yet implemented")
    }

    override fun getEquipmentGemstones(): Int {
        TODO("Not yet implemented")
    }

}
