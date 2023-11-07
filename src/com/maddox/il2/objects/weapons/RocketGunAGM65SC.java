package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class RocketGunAGM65SC extends RocketGun
{

    public void setRocketTimeLife(float f)
    {
        timeLife = 30F;
    }

    static 
    {
        Class class1 = RocketGunAGM65SC.class;
        Property.set(class1, "bulletClass", (Object)AGM65SC.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 0.25F);
        Property.set(class1, "sound", "weapon.rocketgun_132");
    }
}
