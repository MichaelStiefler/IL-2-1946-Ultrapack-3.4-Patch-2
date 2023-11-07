package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class Bomb100lbGP_AN_M30 extends BombProximity
{
    static 
    {
        Class class1 = Bomb100lbGP_AN_M30.class;
        Property.set(class1, "mesh", "3do/arms/100lbGP_AN_M30/mono.sim");
        Property.set(class1, "radius", 40F);
        Property.set(class1, "power", 25.882F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.21F);
        Property.set(class1, "massa", 52.21F);
        Property.set(class1, "sound", "weapon.bomb_mid");
        Property.set(class1, "fuze", ((Object) (new Object[] {
            Fuze_AN_M103.class, Fuze_M160.class, Fuze_M112.class, Fuze_M115.class, Fuze_T91.class
        })));
    }
}
