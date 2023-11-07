package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombOCNapalm extends Bomb {
    static {
        Class class1 = BombOCNapalm.class;
        Property.set(class1, "mesh", "3DO/Arms/OCNapalm/mono.sim");
        Property.set(class1, "radius", 177F);
        Property.set(class1, "power", 175F);
        Property.set(class1, "powerType", 2);
        Property.set(class1, "kalibr", 0.6F);
        Property.set(class1, "massa", 340F);
        Property.set(class1, "sound", "weapon.bomb_std");
    }
}
