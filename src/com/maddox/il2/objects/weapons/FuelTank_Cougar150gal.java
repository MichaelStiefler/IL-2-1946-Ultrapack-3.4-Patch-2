package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class FuelTank_Cougar150gal extends FuelTank
{
    static 
    {
        Class class1 = FuelTank_Cougar150gal.class;
        Property.set(class1, "mesh", "3DO/Arms/Cougar150gal/mono.sim");
        Property.set(class1, "kalibr", 0.6F);
        Property.set(class1, "massa", 540F);
    }
}
