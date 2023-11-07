package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class La_15 extends La15
{
    static 
    {
        Class class1 = La_15.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "La-15");
        Property.set(class1, "meshName", "3DO/Plane/La-15/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(class1, "yearService", 1949.9F);
        Property.set(class1, "yearExpired", 1960.3F);
        Property.set(class1, "FlightModel", "FlightModels/MiG-15F.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitLa_15.class
        });
        Property.set(class1, "LOSElevation", 0.725F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            1, 0, 0, 9, 9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02", "_CANNON03", "_ExternalDev01", "_ExternalDev02"
        });
    }
}
