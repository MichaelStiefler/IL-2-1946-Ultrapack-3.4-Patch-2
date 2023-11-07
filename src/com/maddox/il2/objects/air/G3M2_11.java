package com.maddox.il2.objects.air;

import com.maddox.il2.ai.World;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class G3M2_11 extends G3M implements TypeBomber {

    public G3M2_11() {
        this.fGunPos = 1.0F;
        this.iGunPos = 1;
        this.btme = -1L;
    }

    public void update(float f) {
        super.update(f);
        if (this.FM.CT.getGear() > 0.9F) {
            this.resetYPRmodifier();
        }
        if (this.iGunPos == 0) {
            if (this.fGunPos > 0.0F) {
                this.fGunPos -= 0.2F * f;
                this.resetYPRmodifier();
                Aircraft.xyz[1] = -0.635F + (0.635F * this.fGunPos);
                this.hierMesh().chunkSetLocate("Turret1C_D0", Aircraft.xyz, Aircraft.ypr);
                this.hierMesh().chunkSetLocate("Turret2C_D0", Aircraft.xyz, Aircraft.ypr);
                this.hierMesh().chunkSetLocate("Turret3C_D0", Aircraft.xyz, Aircraft.ypr);
            }
        } else if ((this.iGunPos == 1) && (this.fGunPos < 1.0F)) {
            this.fGunPos += 0.2F * f;
            this.resetYPRmodifier();
            Aircraft.xyz[1] = -0.635F + (0.635F * this.fGunPos);
            this.hierMesh().chunkSetLocate("Turret1C_D0", Aircraft.xyz, Aircraft.ypr);
            this.hierMesh().chunkSetLocate("Turret2C_D0", Aircraft.xyz, Aircraft.ypr);
            this.hierMesh().chunkSetLocate("Turret3C_D0", Aircraft.xyz, Aircraft.ypr);
            if (this.fGunPos > 0.8F) {
                if (this.fGunPos < 0.9F) {

                }
            }
        }
        if (this.FM.AS.isMaster()) {
            if ((this.FM.turret[2].target != null) && (this.FM.AS.astatePilotStates[6] < 90)) {
                this.iGunPos = 1;
            }
            if (Time.current() > this.btme) {
                this.btme = Time.current() + World.Rnd().nextLong(5000L, 12000L);
                if (this.FM.turret[2].target == null) {
                    this.iGunPos = 0;
                }
            }
        }
        if ((this == World.getPlayerAircraft()) && (Main3D.cur3D().cockpitCur instanceof CockpitGunner)) {
            this.iGunPos = 1;
        }
    }

    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        switch (i) {
            default:
                break;

            case 0:
                if (f < -85F) {
                    f = -85F;
                    flag = false;
                }
                if (f > 85F) {
                    f = 85F;
                    flag = false;
                }
                if (f1 < -32F) {
                    f1 = -32F;
                    flag = false;
                }
                if (f1 > 46F) {
                    f1 = 46F;
                    flag = false;
                }
                break;

            case 1:
                if (f1 < -0F) {
                    f1 = -0F;
                    flag = false;
                }
                if (f1 > 20F) {
                    f1 = 20F;
                    flag = false;
                }
                break;

            case 2:
                if (f1 > 89F) {
                    f1 = 89F;
                    flag = false;
                }
                float f2 = Math.abs(f);
                if (f1 < Aircraft.cvt(f2, 140F, 180F, -1F, 25F)) {
                    f1 = Aircraft.cvt(f2, 140F, 180F, -1F, 25F);
                }
                break;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    public void doWoundPilot(int i, float f) {
        if (this.FM.turret != null && (i > 0) && (i < this.FM.turret.length)) {
            this.FM.turret[i].setHealth(f);
        }
    }

    private float fGunPos;
    private int   iGunPos;
    private long  btme;

    static {
        Class class1 = G3M2_11.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "G3M");
        Property.set(class1, "meshName", "3DO/Plane/G3M2_11(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar00());
        Property.set(class1, "meshName_ja", "3DO/Plane/G3M2_11(ja)/hier.him");
        Property.set(class1, "PaintScheme_ja", new PaintSchemeBCSPar01());
        Property.set(class1, "yearService", 1938F);
        Property.set(class1, "yearExpired", 1945F);
        Property.set(class1, "FlightModel", "FlightModels/G3M2-11.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitG3M2.class, CockpitG3M2_Bombardier.class, CockpitG3M1_TGunner.class, CockpitG3M1_BGunner.class, CockpitG3M1_AGunner.class });
        Property.set(class1, "LOSElevation", 1.4078F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 10, 11, 12, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9, 3, 9 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_ExternalBomb01", "_ExternalDev01", "_ExternalBomb02", "_ExternalDev02", "_ExternalBomb03", "_ExternalDev03", "_ExternalBomb04", "_ExternalDev04", "_ExternalBomb05", "_ExternalDev05", "_ExternalBomb06", "_ExternalDev06", "_ExternalBomb07", "_ExternalDev07", "_ExternalBomb08", "_ExternalDev08", "_ExternalBomb09", "_ExternalDev09", "_ExternalBomb10", "_ExternalDev10", "_ExternalBomb11", "_ExternalDev11", "_ExternalBomb12", "_ExternalDev12" });
    }
}
