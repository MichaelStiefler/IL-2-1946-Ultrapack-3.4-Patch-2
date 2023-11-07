package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class FuelTank_TankFletcherL230gal extends FuelTank
{

    static 
    {
        Class localClass = FuelTank_TankFletcherL230gal.class;
        Property.set(localClass, "mesh", "3DO/Arms/FletcherLeftTank/mono.sim");
        Property.set(localClass, "kalibr", 0.6F);
        Property.set(localClass, "massa", 870.64F);
    }
}
