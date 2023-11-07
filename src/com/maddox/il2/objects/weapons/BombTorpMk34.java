package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombTorpMk34 extends Torpedo {

    static {
        Class class1 = BombTorpMk34.class;
        Property.set(class1, "mesh", "3DO/Arms/Mk34_Torpedo/mono.sim");
        Property.set(class1, "radius", 90.8F);
        Property.set(class1, "power", 160F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.569F);
        Property.set(class1, "massa", 522.1F);
        Property.set(class1, "sound", "weapon.torpedo");
        Property.set(class1, "velocity", 17F);
        Property.set(class1, "traveltime", 150F);
        Property.set(class1, "startingspeed", 0.0F);
        Property.set(class1, "impactAngleMin", 20.0F);
        Property.set(class1, "impactAngleMax", 42.0F);
        Property.set(class1, "impactSpeed", 170.0F);
        Property.set(class1, "armingTime", 4.0F);
        Property.set(class1, "dropAltitude", 200.0F);
        Property.set(class1, "dropSpeed", 400.0F);
    }
}
