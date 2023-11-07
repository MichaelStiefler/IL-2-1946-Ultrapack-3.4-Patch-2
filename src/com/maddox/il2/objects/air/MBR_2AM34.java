package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Regiment;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Mission;
import com.maddox.rts.Property;

public class MBR_2AM34 extends MBR_2xyz implements TypeSeaPlane {

    public MBR_2AM34() {
        this.tmpp = new Point3d();
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        if (this.thisWeaponsName.endsWith("RS82")) {
            this.bRSArmed = true;
        }
       prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("BombRackInL_D0", !thisWeaponsName.endsWith("RS82") && !thisWeaponsName.endsWith("default") && !thisWeaponsName.endsWith("none"));
        hierMesh.chunkVisible("BombRackInR_D0", !thisWeaponsName.endsWith("RS82") && !thisWeaponsName.endsWith("default") && !thisWeaponsName.endsWith("none"));
        hierMesh.chunkVisible("RSRackL_D0", thisWeaponsName.endsWith("RS82"));
        hierMesh.chunkVisible("RSRackR_D0", thisWeaponsName.endsWith("RS82"));
    }
    
    public void update(float f) {
        super.update(f);
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 2; j++)
                if (this.FM.Gears.clpGearEff[i][j] != null) {
                    this.tmpp.set(this.FM.Gears.clpGearEff[i][j].pos.getAbsPoint());
                    this.tmpp.z = 0.01D;
                    this.FM.Gears.clpGearEff[i][j].pos.setAbs(this.tmpp);
                    this.FM.Gears.clpGearEff[i][j].pos.reset();
                }

    }

    public static String getSkinPrefix(String s, Regiment regiment) {
        if (regiment == null || regiment.country() == null) return "";
        if (regiment.country().equals(PaintScheme.countryRussia)) {
            int i = Mission.getMissionDate(true);
            if (i > 0) if (i > 0x1282fd5) return "41_";
            else return "";
        }
        if (regiment.country().equals(PaintScheme.countryFinland) || regiment.country().equals(PaintScheme.countryGermany) || regiment.country().equals(PaintScheme.countryHungary) || regiment.country().equals(PaintScheme.countryItaly)
                || regiment.country().equals(PaintScheme.countryJapan) || regiment.country().equals(PaintScheme.countryRomania) || regiment.country().equals(PaintScheme.countrySlovakia))
            return "fi_";
        else return "";
    }

    private Point3d tmpp;

    static {
        Class class1 = MBR_2AM34.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "MBR-2");
        Property.set(class1, "meshName", "3DO/Plane/MBR-2-AM-34(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar02());
        Property.set(class1, "meshName_ru", "3DO/Plane/MBR-2-AM-34/hier.him");
        Property.set(class1, "PaintScheme_ru", new PaintSchemeFCSPar01());
        Property.set(class1, "originCountry", PaintScheme.countryRussia);
        Property.set(class1, "yearService", 1937F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/MBR-2-AM-34.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitMBR_2AM34.class, CockpitMBR_2AM34_Bombardier.class, CockpitMBR_2AM34_FGunner.class, CockpitMBR_2AM34_TGunner.class });
        Aircraft.weaponTriggersRegister(class1, new int[] { 10, 11, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06", "_ExternalBomb07", "_ExternalBomb08", "_ExternalRock01",
                "_ExternalRock02", "_ExternalRock03", "_ExternalRock04" });
    }
}
