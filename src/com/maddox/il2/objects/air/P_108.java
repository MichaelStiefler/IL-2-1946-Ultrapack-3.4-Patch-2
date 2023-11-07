package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class P_108 extends P_108xyz
{
    static 
    {
        Class class1 = P_108.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "P-108B");
        Property.set(class1, "meshName", "3DO/Plane/P-108/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar03());
        Property.set(class1, "yearService", 1941F);
        Property.set(class1, "yearExpired", 1945F);
        Property.set(class1, "FlightModel", "FlightModels/P108.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitP_108.class, CockpitP_108_Bombardier.class
        });
        Aircraft.weaponTriggersRegister(class1, new int[] {
            10, 11, 11, 12, 12, 13, 14, 15, 3, 3, 
            3, 3, 3, 3, 3, 3, 3
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_MGUN05", "_MGUN06", "_MGUN07", "_MGUN08", "_BombSpawn01", "_BombSpawn02", 
            "_BombSpawn03", "_BombSpawn04", "_BombSpawn05", "_BombSpawn06", "_BombSpawn07", "_BombSpawn08", "_BombSpawn09"
        });
    }
}
