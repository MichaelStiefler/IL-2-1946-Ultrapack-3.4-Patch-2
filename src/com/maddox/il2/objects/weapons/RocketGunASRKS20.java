package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class RocketGunASRKS20 extends RocketGun
{
    public void setRocketTimeLife(float f)
    {
        timeLife = 30F;
    }

    static 
    {
        Class class1 = RocketGunASRKS20.class;
        Property.set(class1, "bulletClass", (Object)ASRKS20.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 0.25F);
        Property.set(class1, "sound", "weapon.rocketgun_132");
    }
}
