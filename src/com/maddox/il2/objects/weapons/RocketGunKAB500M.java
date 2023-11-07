package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class RocketGunKAB500M extends RocketBombGun
{
    public void setRocketTimeLife(float f)
    {
        timeLife = 30F;
    }

    static 
    {
        Class class1 = RocketGunKAB500M.class;
        Property.set(class1, "bulletClass", (Object)KAB500M.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 2.0F);
        Property.set(class1, "sound", "weapon.bombgun");
    }
}
