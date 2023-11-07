package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombGunCBU24 extends BombGun
{
    static 
    {
        Class class1 = BombGunCBU24.class;
        Property.set(class1, "bulletClass", (Object)BombCBU24.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 0.25F);
        Property.set(class1, "external", 1);
        Property.set(class1, "sound", "weapon.bombgun");
    }
}
