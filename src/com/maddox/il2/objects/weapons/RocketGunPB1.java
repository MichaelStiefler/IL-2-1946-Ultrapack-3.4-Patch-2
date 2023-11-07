package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class RocketGunPB1 extends RocketGun {
    public void setRocketTimeLife(float f) {
        this.timeLife = -1F;
    }

    static {
        Class class1 = RocketGunPB1.class;
        Property.set(class1, "bulletClass", (Object) RocketPB1.class);
        Property.set(class1, "bullets", 1);
        Property.set(class1, "shotFreq", 0.25F);
        Property.set(class1, "sound", "weapon.rocketgun_132");
    }
}
