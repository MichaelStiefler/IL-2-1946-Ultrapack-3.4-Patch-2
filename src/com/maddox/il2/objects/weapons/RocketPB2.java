package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.rts.Property;

public class RocketPB2 extends Rocket {
    static {
        Class class1 = RocketPB2.class;
        Property.set(class1, "mesh", "3DO/Arms/PB2-CLOSED/mono.sim");
        Property.set(class1, "sprite", "3DO/Effects/Tracers/GuidedRocket/Black.eff");
        Property.set(class1, "flame", "3DO/Effects/Rocket/mono.sim");
        Property.set(class1, "smoke", "3do/effects/rocket/rocketsmokewhite.eff");
        Property.set(class1, "emitColor", new Color3f(1.0F, 1.0F, 0.5F));
        Property.set(class1, "emitLen", 50F);
        Property.set(class1, "emitMax", 1.0F);
        Property.set(class1, "sound", "weapon.rocket_132");
        Property.set(class1, "radius", 0.0F);
        Property.set(class1, "timeLife", 999.999F);
        Property.set(class1, "timeFire", 4F);
        Property.set(class1, "force", 1300F);
        Property.set(class1, "power", 2.1F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.132F);
        Property.set(class1, "massa", 6.7F);
        Property.set(class1, "massaEnd", 2.9F);
    }
}
