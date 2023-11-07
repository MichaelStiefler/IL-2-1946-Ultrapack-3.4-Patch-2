package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;

public abstract class TA_152NEW_WeaponLayout1 extends TA_152NEW {
    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
//      System.out.println("TA_152NEW_WeaponLayout1 prepareWeapons");
      _WeaponSlot[] weaponSlotsRegistered = Aircraft.getWeaponSlotsRegistered(aircraftClass, thisWeaponsName);
      hierMesh.chunkVisible("7mmCowl_D0", weaponSlotsRegistered[3] == null);
      TA_152NEW.prepareWeapons(aircraftClass, hierMesh, thisWeaponsName);
  }
    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        TA_152NEW_WeaponLayout1.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }
}
