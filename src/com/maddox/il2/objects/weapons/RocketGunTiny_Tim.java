package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class RocketGunTiny_Tim extends RocketGun
{
    public void setRocketTimeLife(float f)
    {
        this.timeLife = -1F;
    }

    static 
    {
        Class class1 = RocketGunTiny_Tim.class;
        Property.set(class1, "bulletClass", (Object)RocketTiny_Tim.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 4F);
        Property.set(class1, "sound", "weapon.rocketgun_132");
    }
}
