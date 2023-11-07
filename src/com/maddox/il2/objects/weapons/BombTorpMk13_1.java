package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombTorpMk13_1 extends Torpedomk13_1 {

    static {
        Class class1 = BombTorpMk13_1.class;
        Property.set(class1, "mesh", "3DO/Arms/Mk13_1/mono.sim");
        Property.set(class1, "radius", 90.8F);
        Property.set(class1, "power", 181.9F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.569F);
        Property.set(class1, "massa", 874.1F);
        Property.set(class1, "sound", "weapon.torpedo");
        Property.set(class1, "velocity", 17.25F);
        Property.set(class1, "traveltime", 333.9536F);
        Property.set(class1, "startingspeed", 0.0F);
        Property.set(class1, "impactAngleMin", 14.0F);
        Property.set(class1, "impactAngleMax", 24.0F);
        Property.set(class1, "impactSpeed", 74.0F);
        Property.set(class1, "armingTime", 3.0F);
        Property.set(class1, "dropAltitude", 30.0F);
        Property.set(class1, "dropSpeed", 205.0F);
    }
}
