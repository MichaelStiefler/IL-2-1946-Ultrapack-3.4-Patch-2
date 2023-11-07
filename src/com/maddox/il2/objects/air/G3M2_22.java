package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class G3M2_22 extends G3M implements TypeBomber {

    public void doWoundPilot(int i, float f) {
        switch (i) {
            case 3:
                this.setTurretHealth(2, f);
                break;

            case 4:
                this.setTurretHealth(4, f);
                break;

            case 5:
                this.setTurretHealth(3, f);
                break;

            case 6:
                this.setTurretHealth(1, f);
                break;
        }
    }

    static {
        Class class1 = G3M2_22.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "G3M");
        Property.set(class1, "meshName", "3DO/Plane/G3M2_22(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar00());
        Property.set(class1, "meshName_ja", "3DO/Plane/G3M2_22(ja)/hier.him");
        Property.set(class1, "PaintScheme_ja", new PaintSchemeBCSPar01());
        Property.set(class1, "yearService", 1936F);
        Property.set(class1, "yearExpired", 1945F);
        Property.set(class1, "FlightModel", "FlightModels/G3M2-22.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitG3M2.class, CockpitG3M2_Bombardier.class, CockpitG3M2_AGunner.class, CockpitG3M2_TGunner.class, CockpitG3M2_RGunner.class, CockpitG3M2_LGunner.class });
        Property.set(class1, "LOSElevation", 1.4078F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 10, 11, 12, 13, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_CANNON01", "_MGUN03", "_ExternalBomb01", "_ExternalDev01", "_ExternalBomb02", "_ExternalDev02", "_ExternalBomb03", "_ExternalDev03", "_ExternalBomb04", "_ExternalDev04", "_ExternalBomb05", "_ExternalDev05", "_ExternalBomb06", "_ExternalDev06", "_ExternalBomb07", "_ExternalDev07", "_ExternalBomb08", "_ExternalDev08", "_ExternalBomb09", "_ExternalDev09", "_ExternalBomb10", "_ExternalDev10", "_ExternalBomb11", "_ExternalDev11", "_ExternalBomb12", "_ExternalDev12" });
    }
}
