package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class FuelTankGun_TankNJgalR extends FuelTankGun
{

    static 
    {
        Class class1 = FuelTankGun_TankNJgalR.class;
        Property.set(class1, "bulletClass", (Object)FuelTank_TankNJgalR.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 1.0F);
        Property.set(class1, "external", 1);
    }
}
