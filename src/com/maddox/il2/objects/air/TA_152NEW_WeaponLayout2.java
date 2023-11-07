package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;

public abstract class TA_152NEW_WeaponLayout2 extends TA_152NEW {
    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
//        System.out.println("TA_152NEW_WeaponLayout2 prepareWeapons");
      _WeaponSlot[] weaponSlotsRegistered = Aircraft.getWeaponSlotsRegistered(aircraftClass, thisWeaponsName);
      hierMesh.chunkVisible("20mmL2_D0", weaponSlotsRegistered[3] != null);
      hierMesh.chunkVisible("20mmR2_D0", weaponSlotsRegistered[4] != null);
      TA_152NEW.prepareWeapons(aircraftClass, hierMesh, thisWeaponsName);
  }
    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        TA_152NEW_WeaponLayout2.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }
}
