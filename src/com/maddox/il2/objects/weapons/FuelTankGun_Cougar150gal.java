package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class FuelTankGun_Cougar150gal extends FuelTankGun
{
    static 
    {
        Class var_class = FuelTankGun_Cougar150gal.class;
        Property.set(var_class, "bulletClass", (Object)FuelTank_Cougar150gal.class);
        Property.set(var_class, "bullets", 1);
        Property.set(var_class, "shotFreq", 1.0F);
        Property.set(var_class, "external", 1);
    }
}
