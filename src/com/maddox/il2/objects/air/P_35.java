package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class P_35 extends P_35xyz {

    public void missionStarting() {
        super.missionStarting();
        if (this.FM.isStationedOnGround()) {
            this.FM.AS.setCockpitDoor(this.FM.actor, 1);
            this.FM.CT.cockpitDoorControl = 1.0F;
        }
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        if (this.thisWeaponsName.startsWith("P35A")) {
            this.FM.EI.engines[0].load(this.FM, "FlightModels/PW_R-1830_Series.emd", "R-1830-45", 0);
        }
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("WingGunL_D0", thisWeaponsName.startsWith("P35A"));
        hierMesh.chunkVisible("WingGunR_D0", thisWeaponsName.startsWith("P35A"));
    }
    
    static {
        Class class1 = P_35.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "P_35");
        Property.set(class1, "meshName", "3DO/Plane/P-35/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar02());
        Property.set(class1, "yearService", 1937F);
        Property.set(class1, "yearExpired", 1951F);
        Property.set(class1, "FlightModel", "FlightModels/P-35.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitP_35.class });
        Property.set(class1, "LOSElevation", 0.9119F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 0, 0, 3, 3, 3 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_ExternalBomb01", "_ExternalBomb01", "_ExternalBomb02" });
    }
}
