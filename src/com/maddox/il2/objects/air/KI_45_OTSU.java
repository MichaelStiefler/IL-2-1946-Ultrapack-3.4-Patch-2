package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class KI_45_OTSU extends KI_45 implements TypeJazzPlayer {

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        if (this.thisWeaponsName.startsWith("2xHo103_slanted")) {
            this.hasJazzMusic = true;
            if (this.pit != null) this.pit.showJazzSight();
            if (this.gunner != null) this.gunner.showJazz();
        }
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("JazzGuns", thisWeaponsName.startsWith("2xHo103_slanted"));
        hierMesh.chunkVisible("CFTank", !thisWeaponsName.startsWith("2xHo103_slanted"));
    }
    
    public boolean hasCourseWeaponBullets() {
        return this.FM.CT.Weapons[0] != null && this.FM.CT.Weapons[0][0] != null && this.FM.CT.Weapons[0][0].countBullets() != 0;
    }

    public boolean hasSlantedWeaponBullets() {
        if (this.hasJazzMusic) return this.FM.CT.Weapons[1] != null && this.FM.CT.Weapons[1][0] != null && this.FM.CT.Weapons[1][1] != null && this.FM.CT.Weapons[1][0].countBullets() != 0 || this.FM.CT.Weapons[1][1].countBullets() != 0;
        else return false;
    }

    public Vector3d getAttackVector() {
        return ATTACK_VECTOR;
    }

    private static final Vector3d ATTACK_VECTOR = new Vector3d(-190D, 0.0D, -300D);

    static {
        Class class1 = KI_45_OTSU.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Ki-45");
        Property.set(class1, "meshName", "3do/plane/Ki-45(ja)/OTSU_hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFCSPar05());
        Property.set(class1, "yearService", 1941F);
        Property.set(class1, "yearExpired", 1945F);
        Property.set(class1, "FlightModel", "FlightModels/Ki-45-Tei.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitKI_45_KoOtsu.class, CockpitKI_45_Gunner.class });
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 0, 1, 1, 1, 3, 3, 9, 9, 10 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN02", "_MGUN03", "_CANNON05", "_CANNON05", "_CANNON03", "_CANNON04", "_ExternalBomb01", "_ExternalBomb02", "_ExternalDev01", "_ExternalDev02", "_MGUN01" });
    }
}
