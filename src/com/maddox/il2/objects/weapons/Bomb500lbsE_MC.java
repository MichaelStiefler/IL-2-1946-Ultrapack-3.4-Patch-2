package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class Bomb500lbsE_MC extends Bomb {
    static {
        Class class1 = Bomb500lbsE_MC.class;
        Property.set(class1, "mesh", "3DO/Arms/500LbsBombE_MC/mono.sim");
        Property.set(class1, "radius", 170.2F);
        Property.set(class1, "power", 105.23F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.327F);
        Property.set(class1, "massa", 235.868F);
        Property.set(class1, "sound", "weapon.bomb_big");
    }
}
