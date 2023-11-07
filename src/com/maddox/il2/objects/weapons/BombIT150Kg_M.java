package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombIT150Kg_M extends Bomb
{
    static 
    {
        Class class1 = BombIT150Kg_M.class;
        Property.set(class1, "mesh", "3do/arms/IT150Kg_M/mono.sim");
        Property.set(class1, "radius", 50F);
        Property.set(class1, "power", 21F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.25F);
        Property.set(class1, "massa", 151F);
        Property.set(class1, "sound", "weapon.bomb_mid");
    }
}
