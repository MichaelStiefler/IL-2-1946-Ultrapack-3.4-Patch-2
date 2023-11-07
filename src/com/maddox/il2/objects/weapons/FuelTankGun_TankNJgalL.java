package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class FuelTankGun_TankNJgalL extends FuelTankGun
{

    static 
    {
        Class class1 = FuelTankGun_TankNJgalL.class;
        Property.set(class1, "bulletClass", (Object)FuelTank_TankNJgalL.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 1.0F);
        Property.set(class1, "external", 1);
    }
}
