package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class Bomb1000lbGP_Mk12 extends Bomb
{

    static 
    {
        Class class1 = Bomb1000lbGP_Mk12.class;
        Property.set(class1, "mesh", "3do/arms/1000lbGP_Mk12/mono.sim");
        Property.set(class1, "radius", 71F);
        Property.set(class1, "power", 370F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.42F);
        Property.set(class1, "massa", 454F);
        Property.set(class1, "sound", "weapon.bomb_mid");
        Property.set(class1, "fuze", ((Object) (new Object[] {
            Fuze_Pistol_No76.class, Fuze_Pistol_No78.class
        })));
    }
}
