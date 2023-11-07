package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombFAB100M46_gn16 extends Bomb {
    static {
        Class class1 = BombFAB100M46_gn16.class;
        Property.set(class1, "mesh", "3DO/Arms/FAB100M46_gn16/mono.sim");
        Property.set(class1, "radius", 28F);
        Property.set(class1, "power", 52F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.31F);
        Property.set(class1, "massa", 100F);
        Property.set(class1, "sound", "weapon.bomb_std");
        Property.set(class1, "fuze", ((new Object[] { Fuze_APUV.class, Fuze_APUV_M.class, Fuze_APUV_1.class, Fuze_AV_1du.class, Fuze_AV_1.class, Fuze_AV_87.class })));
        Property.set(class1, "dragCoefficient", 0.4F);
    }
}
