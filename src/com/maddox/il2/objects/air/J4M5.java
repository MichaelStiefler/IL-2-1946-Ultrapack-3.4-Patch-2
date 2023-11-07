package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class J4M5 extends J4Mx
    implements TypeStormovik
{
    static 
    {
        Class class1 = J4M5.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "J4M");
        Property.set(class1, "meshName", "3DO/Plane/J4M4/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar01());
        Property.set(class1, "meshName_ja", "3DO/Plane/J4M4/hier.him");
        Property.set(class1, "PaintScheme_ja", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1945F);
        Property.set(class1, "yearExpired", 1947F);
        Property.set(class1, "FlightModel", "FlightModels/J4M5.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitJ4M4.class
        });
        Property.set(class1, "LOSElevation", 1.0151F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 1, 3, 3, 3, 3, 3, 3, 2, 
            2, 2, 2, 2, 2, 2, 2, 2, 2, 9, 
            9, 9, 9, 3, 3, 3, 3, 3, 3, 3, 
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
            3, 9, 9, 1, 1, 0, 0, 0, 0, 9, 
            9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_Cannon01", "_Cannon02", "_Cannon03", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06", "_ExternalRock07", 
            "_ExternalRock08", "_ExternalRock09", "_ExternalRock10", "_ExternalRock11", "_ExternalRock12", "_ExternalRock13", "_ExternalRock14", "_ExternalRock15", "_ExternalRock16", "_ExternalDev01", 
            "_ExternalDev02", "_ExternalDev11", "_ExternalDev12", "_ExternalBomb07", "_ExternalBomb08", "_ExternalBomb09", "_ExternalBomb10", "_ExternalBomb11", "_ExternalBomb12", "_ExternalBomb13", 
            "_ExternalBomb14", "_ExternalBomb15", "_ExternalBomb16", "_ExternalBomb17", "_ExternalBomb18", "_ExternalBomb19", "_ExternalBomb20", "_ExternalBomb21", "_ExternalBomb22", "_ExternalBomb23", 
            "_ExternalBomb24", "_ExternalDev03", "_ExternalDev04", "_Cannon04", "_Cannon05", "_MGun01", "_MGun02", "_MGun03", "_MGun04", "_ExternalDev05", 
            "_ExternalDev06"
        });
    }
}
