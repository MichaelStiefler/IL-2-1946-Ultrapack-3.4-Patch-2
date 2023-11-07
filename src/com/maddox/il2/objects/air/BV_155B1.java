package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class BV_155B1 extends BV_155B1xyz
    implements TypeFighter
{

    public BV_155B1()
    {
        kangle = 0.0F;
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("30mmL", thisWeaponsName.equals("3xMk103_30mm"));
        hierMesh.chunkVisible("30mmR", thisWeaponsName.equals("3xMk103_30mm"));
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        BV_155B1.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }
    
    public void update(float f)
    {
        for(int i = 1; i < 9; i++)
            hierMesh().chunkSetAngles("Water" + i + "_D0", 0.0F, -20F * kangle, 0.0F);

        kangle = 0.95F * kangle + 0.05F * this.FM.EI.engines[0].getControlRadiator();
        if(this.FM.Loc.z > 9000D)
        {
            if(!this.FM.EI.engines[0].getControlAfterburner())
                this.FM.EI.engines[0].setAfterburnerType(2);
        } else
        if(!this.FM.EI.engines[0].getControlAfterburner())
            this.FM.EI.engines[0].setAfterburnerType(1);
        super.update(f);
    }

    private float kangle;

    static 
    {
        Class class1 = BV_155B1.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "BV-155B1");
        Property.set(class1, "meshName", "3DO/Plane/BV-155B1(multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1945.5F);
        Property.set(class1, "yearExpired", 1948F);
        Property.set(class1, "FlightModel", "FlightModels/BV-155B1.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitBV_155B1.class
        });
        Property.set(class1, "LOSElevation", 0.764106F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 0, 0, 0, 0, 9, 9, 9, 9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_CANNON05", "_CANNON06", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04"
        });
    }
}
