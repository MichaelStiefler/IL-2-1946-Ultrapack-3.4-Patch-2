package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class Bomb55Gal_Napalm extends Bomb
{
    static 
    {
        Class class1 = Bomb55Gal_Napalm.class;
        Property.set(class1, "mesh", "3do/arms/55Gal_Napalm/mono.sim");
        Property.set(class1, "radius", 83F);
        Property.set(class1, "power", 144F);
        Property.set(class1, "powerType", 2);
        Property.set(class1, "kalibr", 0.61F);
        Property.set(class1, "massa", 164F);
        Property.set(class1, "sound", "weapon.bomb_mid");
        Property.set(class1, "fuze", ((Object) (new Object[] {
            Fuze_M157.class
        })));
    }
}
