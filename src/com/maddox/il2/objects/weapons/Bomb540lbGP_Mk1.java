package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class Bomb540lbGP_Mk1 extends Bomb
{

    static 
    {
        Class class1 = Bomb540lbGP_Mk1.class;
        Property.set(class1, "mesh", "3do/arms/540lbGP_Mk1/mono.sim");
        Property.set(class1, "radius", 83F);
        Property.set(class1, "power", 197F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.33F);
        Property.set(class1, "massa", 312F);
        Property.set(class1, "sound", "weapon.bomb_mid");
        Property.set(class1, "fuze", ((Object) (new Object[] {
            Fuze_Pistol_No78.class
        })));
    }
}
