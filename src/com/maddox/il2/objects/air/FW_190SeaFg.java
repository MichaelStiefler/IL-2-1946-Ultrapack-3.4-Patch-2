package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class FW_190SeaFg extends FW_190Sea
{
    static 
    {
        Class var_class = FW_190SeaFg.class;
        new NetAircraft.SPAWN(var_class);
        Property.set(var_class, "iconFar_shortClassName", "FW190");
        Property.set(var_class, "meshName", "3DO/Plane/Fw-190A-4T/hier.him");
        Property.set(var_class, "PaintScheme", new PaintSchemeFMPar01());
        Property.set(var_class, "yearService", 1942.6F);
        Property.set(var_class, "yearExpired", 1948F);
        Property.set(var_class, "FlightModel", "FlightModels/Fw-190A-4N (Ultrapack).fmd");
        Property.set(var_class, "cockpitClass", new Class[] { CockpitFW_190A4T.class });
        Property.set(var_class, "LOSElevation", 0.764106F);
        Aircraft.weaponTriggersRegister(var_class, new int[] {
            0, 0, 1, 1, 1, 1, 9, 9, 9, 9, 
            9, 9, 2, 2, 9, 9, 3
        });
        Aircraft.weaponHooksRegister(var_class, new String[] {
            "_MGUN01", "_MGUN02", "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06", 
            "_ExternalDev07", "_ExternalDev08", "_ExternalRock01", "_ExternalRock02", "_ExternalDev01", "_ExternalDev02", "_ExternalBomb01"
        });
    }
}
