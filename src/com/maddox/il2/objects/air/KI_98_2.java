package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class KI_98_2 extends KI_98x
    implements TypeFighter, TypeStormovik
{
    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("cannon3_D0", !thisWeaponsName.startsWith("Extra"));
    }
        
    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        KI_98_2.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
        if(thisWeaponsName.startsWith("Extra")) this.FM.M.massEmpty -= 100F;
    }


    static 
    {
        Class class1 = KI_98_2.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "ki-98-II");
        Property.set(class1, "meshName", "3DO/Plane/ki-98-II/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar01());
        Property.set(class1, "meshName_ja", "3DO/Plane/ki-98-II/hier.him");
        Property.set(class1, "PaintScheme_ja", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1945F);
        Property.set(class1, "yearExpired", 1946F);
        Property.set(class1, "FlightModel", "FlightModels/Ki_98_II.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitKi98_2.class
        });
        Property.set(class1, "LOSElevation", 1.0151F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 1, 3, 3, 3, 3, 3, 3, 3, 
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
            3, 3, 3, 3, 3, 3, 3
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_Cannon01", "_Cannon02", "_Cannon03", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06", "_ExternalBomb07", 
            "_ExternalBomb08", "_ExternalBomb09", "_ExternalBomb10", "_ExternalBomb11", "_ExternalBomb12", "_ExternalBomb13", "_ExternalBomb14", "_ExternalBomb15", "_ExternalBomb16", "_ExternalBomb17", 
            "_ExternalBomb18", "_ExternalBomb19", "_ExternalBomb20", "_ExternalBomb21", "_ExternalBomb22", "_ExternalBomb23", "_ExternalBomb24"
        });
    }
}
