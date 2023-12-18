package com.matnik.game.fxcoursework.client.module;

import java.io.Serializable;

public enum HeroType implements Serializable {
    Mage(12, 6, 0, 3),
    Knight(15, 2, 3, 3),
    Archer(10, 7, 0, 0);

    private final int baseHp;
    private final int baseDamage;
    private final int baseArmor;
    private final int baseMagicResistance;

    HeroType(int baseHp, int baseDamage, int baseArmor, int baseMagicResistance) {
        this.baseHp = baseHp;
        this.baseDamage = baseDamage;
        this.baseArmor = baseArmor;
        this.baseMagicResistance = baseMagicResistance;
    }

    public int getBaseHp() {
        return baseHp;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getBaseArmor() {
        return baseArmor;
    }

    public int getBaseMagicResistance() {
        return baseMagicResistance;
    }
}
