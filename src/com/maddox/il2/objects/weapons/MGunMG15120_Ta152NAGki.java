package com.maddox.il2.objects.weapons;

import com.maddox.il2.engine.GunProperties;

public class MGunMG15120_Ta152NAGki extends MGunMG15120_Ta152NAGs
{
    public GunProperties createProperties()
    {
        GunProperties gunproperties = super.createProperties();
        gunproperties.bUseHookAsRel = true;
        gunproperties.shells = null;
        gunproperties.shotFreq = 10.2F;
        gunproperties.maxDeltaAngle = 0.21F;
        return gunproperties;
    }
}
