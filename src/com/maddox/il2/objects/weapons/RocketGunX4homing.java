package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class RocketGunX4homing extends RocketGunX4
{

    static 
    {
        Class class1 = RocketGunX4homing.class;
        Property.set(class1, "bulletClass", (Object)RocketX4homing.class);
    }
}
