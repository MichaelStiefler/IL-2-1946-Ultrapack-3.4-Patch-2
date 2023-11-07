package com.maddox.il2.objects.weapons;

import com.maddox.il2.engine.GunProperties;

public class MGunMK103_Ta152NAGki extends MGunMK103_Ta152NAGs
{
    public GunProperties createProperties()
    {
        GunProperties gunproperties = super.createProperties();
        gunproperties.bUseHookAsRel = true;
        gunproperties.shells = null;
        gunproperties.shotFreq = 7.083333F;
        gunproperties.maxDeltaAngle = 0.14F;
        gunproperties.shotFreqDeviation = 0.02F;
        return gunproperties;
    }
}
