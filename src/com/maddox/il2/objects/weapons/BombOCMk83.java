package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombOCMk83 extends Bomb
{
    static 
    {
        Class class1 = BombOCMk83.class;
        Property.set(class1, "mesh", "3DO/Arms/OCMk83/mono.sim");
        Property.set(class1, "radius", 100F);
        Property.set(class1, "power", 250F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.32F);
        Property.set(class1, "massa", 227F);
        Property.set(class1, "sound", "weapon.bomb_big");
    }
}
