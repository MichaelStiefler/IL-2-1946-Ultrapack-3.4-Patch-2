package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class FuelTankGun_TankFletcherL230gal extends FuelTankGun
{

    static 
    {
        Class localClass = FuelTankGun_TankFletcherL230gal.class;
        Property.set(localClass, "bulletClass", (Object)FuelTank_TankFletcherL230gal.class);
        Property.set(localClass, "bullets", 1);
        Property.set(localClass, "shotFreq", 0.25F);
        Property.set(localClass, "external", 1);
    }
}
