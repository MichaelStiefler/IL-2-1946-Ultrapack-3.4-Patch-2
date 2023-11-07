package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class RocketGunX4R extends RocketGunX4
{

    static 
    {
        Class class1 = RocketGunX4R.class;
        Property.set(class1, "bulletClass", (Object)RocketX4R.class);
    }
}
