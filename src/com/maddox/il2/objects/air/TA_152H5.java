package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class TA_152H5 extends TA_152NEW_WeaponLayout1 // TA_152H_Paulus
    implements TypeFighterAceMaker
{
    public void update(float f) {
        this.updateAfterburner();
        super.update(f);
    }

    static 
    {
        Class class1 = TA_152H5.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Ta.152");
        Property.set(class1, "meshName", "3DO/Plane/Ta-152H-5/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1945.5F);
        Property.set(class1, "yearExpired", 1948F);
        Property.set(class1, "FlightModel", "FlightModels/Ta-152H-5.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitTA_152H5.class
        });
        Property.set(class1, "LOSElevation", 0.764106F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 1, 1, 1, 1, 9, 9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON03", "_CANNON04", "_CANNON05", "_CANNON06", "_ExternalDev01", "_ExternalDev02"
        });
    }
}
