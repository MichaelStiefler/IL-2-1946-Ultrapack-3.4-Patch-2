package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class KI_109_I extends KI_109
    implements TypeFighter
{

    static 
    {
        Class class1 = KI_109_I.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Ki-109");
        Property.set(class1, "meshName", "3DO/Plane/Ki-109(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar00());
        Property.set(class1, "meshName_ja", "3DO/Plane/Ki-109(ja)/hier.him");
        Property.set(class1, "PaintScheme_ja", new PaintSchemeBCSPar01());
        Property.set(class1, "yearService", 1944F);
        Property.set(class1, "yearExpired", 1948F);
        Property.set(class1, "FlightModel", "FlightModels/Ki109.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitKI_109.class, CockpitKI_109_TGunner.class
        });
        Property.set(class1, "LOSElevation", 1.4078F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 10
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_MGUN01"
        });
    }
}
