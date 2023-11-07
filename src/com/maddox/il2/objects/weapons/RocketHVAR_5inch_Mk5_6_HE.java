package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.rts.Property;

public class RocketHVAR_5inch_Mk5_6_HE extends Rocket
{
    static 
    {
        Class class1 = RocketHVAR_5inch_Mk5_6_HE.class;
        Property.set(class1, "mesh", "3DO/Arms/5inch_HVAR_Mk5-6_HE/mono.sim");
        Property.set(class1, "sprite", "3DO/Effects/Rocket/firesprite.eff");
        Property.set(class1, "flame", "3DO/Effects/Rocket/mono.sim");
        Property.set(class1, "smoke", "3DO/Effects/Rocket/rocketsmokewhite.eff");
        Property.set(class1, "smokeStart", "3do/effects/rocket/rocketsmokewhitestart.eff");
        Property.set(class1, "smokeTile", "3do/effects/rocket/rocketsmokewhitetile.eff");
        Property.set(class1, "emitColor", new Color3f(1.0F, 1.0F, 0.5F));
        Property.set(class1, "emitLen", 50F);
        Property.set(class1, "emitMax", 1.0F);
        Property.set(class1, "sound", "weapon.rocket_132");
        Property.set(class1, "radius", 78F);
        Property.set(class1, "timeLife", 999.999F);
        Property.set(class1, "timeFire", 4F);
        Property.set(class1, "force", 1500F);
        Property.set(class1, "power", 23.6F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.127F);
        Property.set(class1, "massa", 63.5F);
        Property.set(class1, "massaEnd", 24.1F);
    }
}
