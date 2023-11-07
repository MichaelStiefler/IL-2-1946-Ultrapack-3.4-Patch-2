package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class SHORT_StirlingIII extends SHORT {

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        if (this.thisWeaponsName.startsWith("16x") || this.thisWeaponsName.startsWith("H2S_16x")) this.FM.M.fuel -= 1395F;
        if (this.thisWeaponsName.startsWith("18x500") || this.thisWeaponsName.startsWith("H2S_18x500")) this.FM.M.fuel -= 1570F;
        if (this.thisWeaponsName.startsWith("12x") || this.thisWeaponsName.startsWith("H2S_12x")) this.FM.M.fuel -= 3475F;
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("H2S", thisWeaponsName.startsWith("H2S"));
        hierMesh.chunkVisible("RPC2", thisWeaponsName.startsWith("H2S"));
        hierMesh.chunkVisible("RPC", !thisWeaponsName.startsWith("H2S"));
    }
    
    static {
        Class class1 = SHORT_StirlingIII.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "StirlingIII");
        Property.set(class1, "meshName", "3DO/Plane/STIRLING/hierMkIII.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar02());
        Property.set(class1, "yearService", 1942F);
        Property.set(class1, "yearExpired", 1945F);
        Property.set(class1, "FlightModel", "FlightModels/ShortIII.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitStirling.class, CockpitStirling_Bombardier.class, CockpitStirling_NGunner.class, CockpitStirling_TGunner.class, CockpitStirling_AGunner.class });
        Property.set(class1, "LOSElevation", 1.0728F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 10, 10, 11, 11, 12, 12, 12, 12, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_MGUN05", "_MGUN06", "_MGUN07", "_MGUN08", "_BombSpawn01", "_BombSpawn02", "_BombSpawn03", "_BombSpawn04", "_BombSpawn05", "_BombSpawn06",
                "_BombSpawn07", "_BombSpawn08", "_BombSpawn09", "_BombSpawn10", "_BombSpawn11", "_BombSpawn12", "_BombSpawn13", "_BombSpawn14", "_BombSpawn15", "_BombSpawn16", "_BombSpawn17", "_BombSpawn18" });
    }
}
