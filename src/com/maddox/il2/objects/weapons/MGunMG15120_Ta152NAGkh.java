package com.maddox.il2.objects.weapons;

import com.maddox.il2.engine.GunProperties;

public class MGunMG15120_Ta152NAGkh extends MGunMG15120_Ta152NAGs
{
    public GunProperties createProperties()
    {
        GunProperties gunproperties = super.createProperties();
        gunproperties.bUseHookAsRel = true;
        gunproperties.shells = null;
        gunproperties.shotFreq = 10.5F;
        gunproperties.maxDeltaAngle = 0.21F;
        return gunproperties;
    }
}
