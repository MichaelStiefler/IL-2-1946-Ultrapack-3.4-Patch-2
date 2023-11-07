package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;

public class P_61A_10 extends P_61X {
    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
        if (this.thisWeaponsName.toLowerCase().endsWith("turret_as_gunpod") || !this.thisWeaponsName.toLowerCase().endsWith("turret")) {
            Cockpit[] newCockpits = new Cockpit[Main3D.cur3D().cockpits.length - 1];
            System.arraycopy(Main3D.cur3D().cockpits, 0, newCockpits, 0, Main3D.cur3D().cockpits.length - 1);
            Main3D.cur3D().cockpits = newCockpits;
            if (this.FM.turret.length > 0) this.FM.turret[0].bIsAIControlled = false;
        }
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("Turret1A_D0", thisWeaponsName.endsWith("gunpod"));
        hierMesh.chunkVisible("Turret1B_D0", thisWeaponsName.endsWith("gunpod"));
        hierMesh.chunkVisible("PylonDTK_L2", thisWeaponsName.startsWith("2x"));
        hierMesh.chunkVisible("PylonDTK_R2", thisWeaponsName.startsWith("2x"));
    }
    
    static {
        Class class1 = P_61A_10.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "P-61");
        Property.set(class1, "meshName", "3DO/Plane/P-61A/hierA.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(class1, "yearService", 1944F);
        Property.set(class1, "yearExpired", 1946.5F);
        Property.set(class1, "FlightModel", "FlightModels/P-61A.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitP_61B.class, CockpitP61_TGunner.class });
        Property.set(class1, "LOSElevation", 0.69215F);
        Property.set(class1, "Handicap", 1.0F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 9, 9, 9, 9, 10, 10, 10, 10, 9, 9, 2, 2, 2, 2, 2, 2, 9, 9, 9, 9, 9, 9, 9, 9, 2, 2, 2, 2, 2, 2, 2, 2 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06", "_ExternalBomb07", "_ExternalBomb08", "_ExternalBomb09", "_ExternalBomb10", "_ExternalBomb11", "_ExternalBomb12", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_ExternalDev05", "_ExternalDev06", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalDev07", "_ExternalDev08", "_ExternalDev09", "_ExternalDev10", "_ExternalDev11", "_ExternalDev12", "_ExternalDev13", "_ExternalDev14", "_ExternalRock07", "_ExternalRock08", "_ExternalRock09", "_ExternalRock10", "_ExternalRock11", "_ExternalRock12", "_ExternalRock13", "_ExternalRock14" });
    }
}
