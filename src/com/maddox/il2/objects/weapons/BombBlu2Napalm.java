package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombBlu2Napalm extends Bomb
{
    static 
    {
        Class class1 = BombBlu2Napalm.class;
        Property.set(class1, "mesh", "3DO/Arms/Blu2/mono.sim");
        Property.set(class1, "radius", 77F);
        Property.set(class1, "power", 75F);
        Property.set(class1, "powerType", 2);
        Property.set(class1, "kalibr", 0.6F);
        Property.set(class1, "massa", 316F);
        Property.set(class1, "sound", "weapon.bomb_std");
    }
}
