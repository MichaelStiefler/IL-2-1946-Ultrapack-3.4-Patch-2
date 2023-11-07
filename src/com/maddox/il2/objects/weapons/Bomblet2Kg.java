package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class Bomblet2Kg extends Bomb
{
    protected boolean haveSound()
    {
        return index % 16 == 0;
    }

    static 
    {
        Class class1 = Bomblet2Kg.class;
        Property.set(class1, "mesh", "3do/arms/2KgBomblet/mono.sim");
        Property.set(class1, "radius", 4F);
        Property.set(class1, "power", 0.12F);
        Property.set(class1, "powerType", 1);
        Property.set(class1, "kalibr", 0.1F);
        Property.set(class1, "massa", 2.0F);
        Property.set(class1, "randomOrient", 1);
        Property.set(class1, "sound", "weapon.bomb_cassette");
    }
}
