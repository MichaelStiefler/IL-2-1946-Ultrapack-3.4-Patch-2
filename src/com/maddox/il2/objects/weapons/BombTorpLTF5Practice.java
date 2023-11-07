package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombTorpLTF5Practice extends Torpedo {

    static {
        Class class1 = BombTorpLTF5Practice.class;
        Property.set(class1, "mesh", "3do/arms/Practice/mono.sim");
        Property.set(class1, "radius", 0.2F);
        Property.set(class1, "power", 1.0F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.45F);
        Property.set(class1, "massa", 725);
        Property.set(class1, "sound", "weapon.torpedo");
        Property.set(class1, "velocity", 20.58F);
        Property.set(class1, "traveltime", 98.17F);
        Property.set(class1, "startingspeed", 0.0F);
        Property.set(class1, "impactAngleMin", 18.6F);
        Property.set(class1, "impactAngleMax", 44.0F);
        Property.set(class1, "impactSpeed", 77.5F);
        Property.set(class1, "armingTime", 2.0F);
        Property.set(class1, "dropAltitude", 40.0F);
        Property.set(class1, "dropSpeed", 260.0F);
    }
}
