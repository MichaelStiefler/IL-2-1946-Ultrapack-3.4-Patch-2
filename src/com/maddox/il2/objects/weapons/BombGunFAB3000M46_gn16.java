package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombGunFAB3000M46_gn16 extends BombGun {
    static {
        Class class1 = BombGunFAB3000M46_gn16.class;
        Property.set(class1, "bulletClass", (Object) BombFAB3000M46_gn16.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 1.0F);
        Property.set(class1, "external", 1);
        Property.set(class1, "sound", "weapon.bombgun");
    }
}
