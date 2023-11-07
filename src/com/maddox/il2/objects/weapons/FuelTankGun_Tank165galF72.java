package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class FuelTankGun_Tank165galF72 extends FuelTankGun
{
    static 
    {
        Class class1 = FuelTankGun_Tank165galF72.class;
        Property.set(class1, "bulletClass", (Object)FuelTank_Tank165galF72.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 1.0F);
        Property.set(class1, "external", 1);
    }
}
