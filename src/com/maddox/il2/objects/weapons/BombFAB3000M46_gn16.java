package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombFAB3000M46_gn16 extends Bomb {
    static {
        Class class1 = BombFAB3000M46_gn16.class;
        Property.set(class1, "mesh", "3DO/Arms/FAB3000M46_gn16/mono.sim");
        Property.set(class1, "power", 1310F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.806F);
        Property.set(class1, "massa", 3040F);
        Property.set(class1, "sound", "weapon.bomb_big");
        Property.set(class1, "fuze", ((new Object[] { Fuze_APUV.class, Fuze_APUV_M.class, Fuze_APUV_1.class, Fuze_AV_1du.class, Fuze_AV_1.class, Fuze_AV_87.class })));
        Property.set(class1, "dragCoefficient", 0.4F);
    }
}
