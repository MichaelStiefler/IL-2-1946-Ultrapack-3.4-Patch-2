package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class Bomb500lbGP_AN_M64 extends BombProximity
{

    static 
    {
        Class class1 = Bomb500lbGP_AN_M64.class;
        Property.set(class1, "mesh", "3do/arms/500lbGP_AN_M64/mono.sim");
        Property.set(class1, "radius", 84F);
        Property.set(class1, "power", 121.22F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.36F);
        Property.set(class1, "massa", 238.35F);
        Property.set(class1, "sound", "weapon.bomb_mid");
        Property.set(class1, "fuze", ((Object) (new Object[] {
            Fuze_AN_M103.class, Fuze_M116.class, Fuze_AN_MK_230.class, Fuze_MK_243.class, Fuze_T92.class
        })));
    }
}
