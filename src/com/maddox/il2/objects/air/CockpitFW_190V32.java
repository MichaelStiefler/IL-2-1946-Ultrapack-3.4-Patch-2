package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class CockpitFW_190V32 extends CockpitFW_190V
{
    public CockpitFW_190V32()
    {
        super("3DO/Cockpit/FW-190V32/hier.him", "bf109");
        this.cockpitNightMats = (new String[] {
            "A4GP1", "A4GP2", "A4GP3", "A4GP4", "A4GP5", "A4GP6", "A5GP3Km", "CoolantGauge", "EQpt5"
        });
        setNightMats(false);
    }

    static 
    {
        Property.set(CockpitFW_190V32.class, "normZN", 0.72F);
        Property.set(CockpitFW_190V32.class, "gsZN", 0.66F);
    }

}
