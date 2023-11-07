package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombGun1000lbsE_MC extends BombGun {
    static {
        Class class1 = BombGun1000lbsE_MC.class;
        Property.set(class1, "bulletClass", (Object) Bomb1000lbsE_MC.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 1.0F);
        Property.set(class1, "external", 1);
        Property.set(class1, "sound", "weapon.bombgun");
    }
}
