package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class RocketGunR4MPB2_DZZ extends RocketGun
{
    public void setConvDistance(float f, float f1)
    {
        super.setConvDistance(f, f1 + 2.81F);
    }

    static 
    {
        Class class1 = RocketGunR4MPB2_DZZ.class;
        Property.set(class1, "bulletClass", (Object)RocketR4MPB2_DZZ.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 0.25F);
        Property.set(class1, "sound", "weapon.rocketgun_132");
    }
}
