package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.rts.Property;

public class RocketR4MPB2_DZZ extends Rocket
{
    public void start(float f)
    {
        setMesh("3DO/Arms/BRS-82/mono.sim");
        super.start(f);
        speed.normalize();
        speed.scale(525D);
        noGDelay = -1L;
    }

    static 
    {
        Class class1 = RocketR4MPB2_DZZ.class;
        Property.set(class1, "mesh", "3DO/Arms/BRS-82/mono.sim");
        Property.set(class1, "sprite", "3DO/Effects/Tracers/GuidedRocket/Black.eff");
        Property.set(class1, "flame", "3DO/Effects/Rocket/mono.sim");
        Property.set(class1, "smoke", "3DO/Effects/Tracers/GuidedRocket/White.eff");
        Property.set(class1, "emitColor", new Color3f(1.0F, 1.0F, 0.5F));
        Property.set(class1, "emitLen", 50F);
        Property.set(class1, "emitMax", 1.0F);
        Property.set(class1, "sound", "weapon.rocket_132");
        Property.set(class1, "radius", 5F);
        Property.set(class1, "timeLife", 60F);
        Property.set(class1, "timeFire", 60F);
        Property.set(class1, "force", 200F);
        Property.set(class1, "power", 1.52F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.0825F);
        Property.set(class1, "massa", 7F);
        Property.set(class1, "massaEnd", 5F);
        Property.set(class1, "maxDeltaAngle", 1.75F);
    }
}
