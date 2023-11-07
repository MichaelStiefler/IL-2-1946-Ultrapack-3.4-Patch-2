package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombGunIT150Kg_M extends BombGun
{
    static 
    {
        Class class1 = BombGunIT150Kg_M.class;
        Property.set(class1, "bulletClass", (Object)BombIT150Kg_M.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 3F);
        Property.set(class1, "external", 1);
        Property.set(class1, "sound", "weapon.bombgun");
    }
}
