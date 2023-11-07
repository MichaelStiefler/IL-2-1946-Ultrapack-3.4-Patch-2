package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombGun500lbGP_AN_M43_Parademo extends BombGun
{
    static 
    {
        Class class1 = BombGun500lbGP_AN_M43_Parademo.class;
        Property.set(class1, "bulletClass", (Object)Bomb500lbGP_AN_M43_Parademo.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 3F);
        Property.set(class1, "external", 1);
        Property.set(class1, "sound", "weapon.bombgun");
    }
}
