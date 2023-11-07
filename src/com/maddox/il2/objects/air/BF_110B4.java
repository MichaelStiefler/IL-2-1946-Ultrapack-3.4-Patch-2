package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class BF_110B4 extends BF_110
{
    public static boolean bChangedPit = false;

    static 
    {
        Class class1 = BF_110B4.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Bf-110");
        Property.set(class1, "meshName", "3DO/Plane/Bf-110B/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar01());
        Property.set(class1, "yearService", 1940F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/Bf-110B-4.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitBF_110B4.class, CockpitBF_110Early_Gunner.class
        });
        Property.set(class1, "LOSElevation", 0.66895F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 0, 0, 1, 1, 10
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_CANNON01", "_CANNON02", "_MGUN05"
        });
    }
}
