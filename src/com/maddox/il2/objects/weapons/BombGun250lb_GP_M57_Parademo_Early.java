package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombGun250lb_GP_M57_Parademo_Early extends BombGun
{
    static 
    {
        Class class1 = BombGun250lb_GP_M57_Parademo_Early.class;
        Property.set(class1, "bulletClass", (Object)Bomb250lb_GP_M57_Parademo_Early.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 3F);
        Property.set(class1, "external", 1);
        Property.set(class1, "sound", "weapon.bombgun");
    }
}
