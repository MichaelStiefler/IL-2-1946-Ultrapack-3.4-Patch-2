package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class FuelTank_Venom extends FuelTank {
    static {
        Class class1 = FuelTank_Venom.class;
        Property.set(class1, "mesh", "3DO/Arms/VenomTank/mono.sim");
        Property.set(class1, "kalibr", 0.6F);
        Property.set(class1, "massa", 156F);
    }
}
