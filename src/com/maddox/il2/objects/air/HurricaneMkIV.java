package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class HurricaneMkIV extends Hurricane implements TypeStormovik, TypeStormovikArmored {

    public HurricaneMkIV() {
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("pod_L", thisWeaponsName.startsWith("Vicker"));
        hierMesh.chunkVisible("inner_barrel_L", thisWeaponsName.startsWith("Vicker"));
        hierMesh.chunkVisible("outer_barrel_L", thisWeaponsName.startsWith("Vicker"));
        hierMesh.chunkVisible("pod_R", thisWeaponsName.startsWith("Vicker"));
        hierMesh.chunkVisible("inner_barrel_R", thisWeaponsName.startsWith("Vicker"));
        hierMesh.chunkVisible("outer_barrel_R", thisWeaponsName.startsWith("Vicker"));
        hierMesh.chunkVisible("PylonL_D0", thisWeaponsName.indexOf("1x") != -1 || thisWeaponsName.indexOf("2x") != -1);
        hierMesh.chunkVisible("PylonR_D0", thisWeaponsName.indexOf("2x") != -1);
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        HurricaneMkIV.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
        this.FM.CT.bHasCockpitDoorControl = true;
        this.FM.CT.dvCockpitDoor = 1.0F;
    }
    
//    public void onAircraftLoaded() {
//        super.onAircraftLoaded();
//        if (!(this.getGunByHookName("_MGUN03") instanceof GunEmpty)) {
//            this.hierMesh().chunkVisible("pod_L", true);
//            this.hierMesh().chunkVisible("inner_barrel_L", true);
//            this.hierMesh().chunkVisible("outer_barrel_L", true);
//        }
//        if (!(this.getGunByHookName("_MGUN04") instanceof GunEmpty)) {
//            this.hierMesh().chunkVisible("pod_R", true);
//            this.hierMesh().chunkVisible("inner_barrel_R", true);
//            this.hierMesh().chunkVisible("outer_barrel_R", true);
//        }
//        if (this.getBulletEmitterByHookName("_ExternalDev01") instanceof GunEmpty && this.getBulletEmitterByHookName("_ExternalBomb01") instanceof GunEmpty) this.hierMesh().chunkVisible("PylonL_D0", false);
//        if (this.getBulletEmitterByHookName("_ExternalDev02") instanceof GunEmpty && this.getBulletEmitterByHookName("_ExternalBomb02") instanceof GunEmpty) this.hierMesh().chunkVisible("PylonR_D0", false);
//        super.onAircraftLoaded();
//        if (this.FM.isPlayers()) {
//            this.FM.CT.bHasCockpitDoorControl = true;
//            this.FM.CT.dvCockpitDoor = 1.0F;
//        }
//    }

    static {
        Class class1 = HurricaneMkIV.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "HurriMkIV");
        Property.set(class1, "meshName", "3DO/Plane/HurricaneMkIV(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar04());
        Property.set(class1, "yearService", 1942F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/HurricaneMkIV.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitHURRII.class });
        Property.set(class1, "LOSElevation", 0.66895F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 1, 1, 1, 1, 9, 9, 9, 9, 3, 3, 9, 9, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 });
        Aircraft.weaponHooksRegister(class1,
                new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalBomb01", "_ExternalBomb02", "_ExternalDev05", "_ExternalDev06", "_ExternalRock01",
                        "_ExternalRock01", "_ExternalRock02", "_ExternalRock02", "_ExternalRock03", "_ExternalRock03", "_ExternalRock06", "_ExternalRock06", "_ExternalRock05", "_ExternalRock05", "_ExternalRock04", "_ExternalRock04", "_ExternalRock07",
                        "_ExternalRock07", "_ExternalRock08", "_ExternalRock08" });
    }
}
