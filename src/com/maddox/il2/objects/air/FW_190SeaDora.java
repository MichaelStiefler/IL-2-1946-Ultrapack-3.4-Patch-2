package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class FW_190SeaDora extends FW_190Sea
{

    public FW_190SeaDora()
    {
        kangle = 0.0F;
    }

    public void update(float f)
    {
        for(int k = 1; k < 13; k++)
            hierMesh().chunkSetAngles("Water" + k + "_D0", 0.0F, -10F * kangle, 0.0F);

        kangle = 0.95F * kangle + 0.05F * this.FM.EI.engines[0].getControlRadiator();
        super.update(f);
    }

    private float kangle;

    static 
    {
        Class var_class = FW_190SeaDora.class;
        new NetAircraft.SPAWN(var_class);
        Property.set(var_class, "iconFar_shortClassName", "FW190");
        Property.set(var_class, "meshName", "3DO/Plane/Fw-190D-13T/hier.him");
        Property.set(var_class, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(var_class, "yearService", 1943.11F);
        Property.set(var_class, "yearExpired", 1948F);
        Property.set(var_class, "FlightModel", "FlightModels/Fw-190D-13N (Ultrapack).fmd");
        Property.set(var_class, "cockpitClass", new Class[] {
            CockpitFW_190D11Sea.class
        });
        Property.set(var_class, "LOSElevation", 0.764106F);
        Aircraft.weaponTriggersRegister(var_class, new int[] {
            1, 1, 0, 0, 9, 3, 9, 9, 9, 1, 
            1, 9, 9, 1, 1, 1, 1, 9, 9, 1, 
            1, 9, 9, 2, 2, 9, 9, 1, 1
        });
        Aircraft.weaponHooksRegister(var_class, new String[] {
            "_MGUN01", "_MGUN02", "_CANNON01", "_CANNON02", "_ExternalDev01", "_ExternalBomb01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_CANNON03", 
            "_CANNON04", "_ExternalDev05", "_ExternalDev06", "_CANNON05", "_CANNON06", "_CANNON07", "_CANNON08", "_ExternalDev07", "_ExternalDev08", "_CANNON09", 
            "_CANNON10", "_ExternalDev09", "_ExternalDev10", "_ExternalRock01", "_ExternalRock02", "_ExternalDev11", "_ExternalDev12", "_CANNON11", "_CANNON12"
        });
    }
}
