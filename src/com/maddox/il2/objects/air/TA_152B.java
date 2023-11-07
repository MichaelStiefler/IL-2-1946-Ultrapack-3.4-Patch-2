package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class TA_152B extends TA_152NEW_WeaponLayout1 {

    static {
        Class class1 = TA_152B.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Ta.152");
        Property.set(class1, "meshName", "3DO/Plane/Ta-152B/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(class1, "yearService", 1944.6F);
        Property.set(class1, "yearExpired", 1948F);
        Property.set(class1, "FlightModel", "FlightModels/Ta-152B (Ultrapack).fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitTA_152B.class });
        Property.set(class1, "LOSElevation", 0.755F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_CANNON01", "_CANNON03", "_CANNON04", "_CANNON05", "_CANNON06", "_CANNON07", "_CANNON08", "_CANNON09", "_CANNON10", "_CANNON11", "_CANNON12", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06", "_ExternalDev07", "_ExternalDev08" });
    }
}
