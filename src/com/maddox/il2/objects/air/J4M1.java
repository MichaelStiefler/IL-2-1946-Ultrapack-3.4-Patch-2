package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class J4M1 extends J4Mx
{
    static 
    {
        Class class1 = J4M1.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "J4M1");
        Property.set(class1, "meshName", "3DO/Plane/J4M1/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar01());
        Property.set(class1, "meshName_ja", "3DO/Plane/J4M1/hier.him");
        Property.set(class1, "PaintScheme_ja", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1945F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/J4M1.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitJ4M1.class
        });
        Property.set(class1, "LOSElevation", 1.0151F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 1, 3, 3, 3, 3, 9, 9, 9, 
            9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_Cannon01", "_Cannon02", "_Cannon03", "_Externalbomb01", "_Externalbomb02", "_Externalbomb03", "_Externalbomb04", "_Externaldev01", "_Externaldev02", "_ExternalBomb05", 
            "_ExternalBomb06"
        });
    }
}
