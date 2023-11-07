package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class RocketGunHVAR_5inch_Mk5_6_HE extends RocketGun
{
    public void setRocketTimeLife(float f)
    {
        this.timeLife = -1F;
    }

    static 
    {
        Class class1 = RocketGunHVAR_5inch_Mk5_6_HE.class;
        Property.set(class1, "bulletClass", (Object)RocketHVAR_5inch_Mk5_6_HE.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 1.0F);
        Property.set(class1, "sound", "weapon.rocketgun_132");
    }
}
