package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;

public class Potez542 extends Potez54x implements TypeBomber, TypeTransport {
    public void update(float f) {
        super.update(f);
        if (this.FM.isPlayers()) {
            if (!Main3D.cur3D().isViewOutside()) {
                this.hierMesh().chunkVisible("CF_D0", false);
                this.hierMesh().chunkVisible("Cabin_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D0", false);
            } else {
                this.hierMesh().chunkVisible("CF_D0", true);
                this.hierMesh().chunkVisible("Cabin_D0", true);
                this.hierMesh().chunkVisible("Head1_D0", true);
                this.hierMesh().chunkVisible("HMask1_D0", true);
                this.hierMesh().chunkVisible("Pilot1_D0", true);
            }
        }
        if (this.FM.isPlayers()) {
            if (!Main3D.cur3D().isViewOutside()) {
                this.hierMesh().chunkVisible("CF_D1", false);
            }
            this.hierMesh().chunkVisible("CF_D2", false);
            this.hierMesh().chunkVisible("CF_D3", false);
            this.hierMesh().chunkVisible("Pilot1_D1", false);
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
                if (f < -90F) {
                    f = -90F;
                    flag = false;
                }
                if (f > 90F) {
                    f = 90F;
                    flag = false;
                }
                if (f1 < -15F) {
                    f1 = -15F;
                    flag = false;
                }
                if (f1 > 30F) {
                    f1 = 30F;
                    flag = false;
                }
                break;

            case 1:
                if (f < -120F) {
                    f = -120F;
                    flag = false;
                }
                if (f > 120F) {
                    f = 120F;
                    flag = false;
                }
                if (f1 > 30F) {
                    f1 = 30F;
                    flag = false;
                }
                if (f1 < 0.0F) {
                    f1 = 0.0F;
                    flag = false;
                }
                break;

            case 2:
                if (f < -30F) {
                    f = -30F;
                    flag = false;
                }
                if (f > 30F) {
                    f = 30F;
                    flag = false;
                }
                if (f1 < -30F) {
                    f1 = -30F;
                    flag = false;
                }
                if (f1 > 0.0F) {
                    f1 = 0.0F;
                    flag = false;
                }
                break;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    public boolean typeBomberToggleAutomation() {
        return false;
    }

    public void typeBomberAdjDistanceReset() {
        this.fSightCurForwardAngle = 0.0F;
    }

    public void typeBomberAdjDistancePlus() {
        this.fSightCurForwardAngle += 0.4F;
        if (this.fSightCurForwardAngle > 75F) {
            this.fSightCurForwardAngle = 75F;
        }
    }

    public void typeBomberAdjDistanceMinus() {
        this.fSightCurForwardAngle -= 0.4F;
        if (this.fSightCurForwardAngle < -15F) {
            this.fSightCurForwardAngle = -15F;
        }
    }

    public void typeBomberAdjSideslipReset() {
        this.fSightCurSideslip = 0.0F;
    }

    public void typeBomberAdjSideslipPlus() {
        this.fSightCurSideslip += 0.5D;
        if (this.thisWeaponsName.startsWith("1x")) {
            if (this.fSightCurSideslip > 40F) {
                this.fSightCurSideslip = 40F;
            }
            HUD.log(AircraftHotKeys.hudLogWeaponId, "Torpedo Angle  " + this.fSightCurSideslip);
        } else {
            if (this.fSightCurSideslip > 10F) {
                this.fSightCurSideslip = 10F;
            }
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip " + this.fSightCurSideslip);
        }
    }

    public void typeBomberAdjSideslipMinus() {
        this.fSightCurSideslip -= 0.5D;
        if (this.thisWeaponsName.startsWith("1x")) {
            if (this.fSightCurSideslip < -40F) {
                this.fSightCurSideslip = -40F;
            }
            HUD.log(AircraftHotKeys.hudLogWeaponId, "Torpedo Angle  " + this.fSightCurSideslip);
        } else {
            if (this.fSightCurSideslip < -10F) {
                this.fSightCurSideslip = -10F;
            }
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip " + this.fSightCurSideslip);
        }
    }

    public void typeBomberAdjAltitudeReset() {
        this.fSightCurAltitude = 300F;
    }

    public void typeBomberAdjAltitudePlus() {
        this.fSightCurAltitude += 10F;
        if (this.fSightCurAltitude > 6000F) {
            this.fSightCurAltitude = 6000F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) this.fSightCurAltitude) });
    }

    public void typeBomberAdjAltitudeMinus() {
        this.fSightCurAltitude -= 10F;
        if (this.fSightCurAltitude < 300F) {
            this.fSightCurAltitude = 300F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) this.fSightCurAltitude) });
    }

    public void typeBomberAdjSpeedReset() {
        this.fSightCurSpeed = 50F;
    }

    public void typeBomberAdjSpeedPlus() {
        this.fSightCurSpeed += 5F;
        if (this.fSightCurSpeed > 650F) {
            this.fSightCurSpeed = 650F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberAdjSpeedMinus() {
        this.fSightCurSpeed -= 5F;
        if (this.fSightCurSpeed < 50F) {
            this.fSightCurSpeed = 50F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberUpdate(float f) {
        double d = (this.fSightCurSpeed / 3.6D) * Math.sqrt((double) this.fSightCurAltitude * (2F / 9.81F));
        d -= this.fSightCurAltitude * this.fSightCurAltitude * 1.419E-005D;
        this.fSightSetForwardAngle = (float) Math.atan(d / this.fSightCurAltitude);
    }

    public void typeBomberReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
        netmsgguaranted.writeFloat(this.fSightCurAltitude);
        netmsgguaranted.writeFloat(this.fSightCurSpeed);
        netmsgguaranted.writeFloat(this.fSightCurForwardAngle);
        netmsgguaranted.writeFloat(this.fSightCurSideslip);
    }

    public void typeBomberReplicateFromNet(NetMsgInput netmsginput) throws IOException {
        this.fSightCurAltitude = netmsginput.readFloat();
        this.fSightCurSpeed = netmsginput.readFloat();
        this.fSightCurForwardAngle = netmsginput.readFloat();
        this.fSightCurSideslip = netmsginput.readFloat();
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        if (thisWeaponsName.equals("12x10kgP")) {
            hierMesh.chunkVisible("RackD_Door_D0", true);
            hierMesh.chunkVisible("RackF_Door_D0", false);
            hierMesh.chunkVisible("RackTypeF_D0", true);
            hierMesh.chunkVisible("RackTypeD_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU_L_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU_R_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU2_L_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU2_R_D0", false);
            return;
        }
        if (thisWeaponsName.equals("8x50kgA") || thisWeaponsName.equals("32x10kgA")) {
            hierMesh.chunkVisible("RackD_Door_D0", false);
            hierMesh.chunkVisible("RackF_Door_D0", true);
            hierMesh.chunkVisible("RackTypeD_D0", true);
            hierMesh.chunkVisible("RackTypeF_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU_L_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU_R_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU2_L_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU2_R_D0", false);
            return;
        }
        if (thisWeaponsName.equals("4x250kgCatalana")) {
            hierMesh.chunkVisible("RackD_Door_D0", true);
            hierMesh.chunkVisible("RackF_Door_D0", true);
            hierMesh.chunkVisible("RackTypeD_D0", false);
            hierMesh.chunkVisible("RackTypeF_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU_L_D0", true);
            hierMesh.chunkVisible("RackTypeTGPU_R_D0", true);
            hierMesh.chunkVisible("RackTypeTGPU2_L_D0", true);
            hierMesh.chunkVisible("RackTypeTGPU2_R_D0", true);
            return;
        }
        if (thisWeaponsName.equals("4x200kgMod1930+12x10kgP")) {
            hierMesh.chunkVisible("RackD_Door_D0", true);
            hierMesh.chunkVisible("RackF_Door_D0", false);
            hierMesh.chunkVisible("RackTypeD_D0", false);
            hierMesh.chunkVisible("RackTypeF_D0", true);
            hierMesh.chunkVisible("RackTypeTGPU_L_D0", true);
            hierMesh.chunkVisible("RackTypeTGPU_R_D0", true);
            hierMesh.chunkVisible("RackTypeTGPU2_L_D0", true);
            hierMesh.chunkVisible("RackTypeTGPU2_R_D0", true);
            return;
        }
        if (thisWeaponsName.equals("2x500kgCatalana")) {
            hierMesh.chunkVisible("RackD_Door_D0", true);
            hierMesh.chunkVisible("RackF_Door_D0", true);
            hierMesh.chunkVisible("RackTypeD_D0", false);
            hierMesh.chunkVisible("RackTypeF_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU_L_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU_R_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU2_L_D0", true);
            hierMesh.chunkVisible("RackTypeTGPU2_R_D0", true);
            return;
        }
        if (thisWeaponsName.equals("2x500kgMod1930")) {
            hierMesh.chunkVisible("RackD_Door_D0", true);
            hierMesh.chunkVisible("RackF_Door_D0", true);
            hierMesh.chunkVisible("RackTypeD_D0", false);
            hierMesh.chunkVisible("RackTypeF_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU_L_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU_R_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU2_L_D0", true);
            hierMesh.chunkVisible("RackTypeTGPU2_R_D0", true);
            return;
        } else {
            hierMesh.chunkVisible("RackD_Door_D0", true);
            hierMesh.chunkVisible("RackF_Door_D0", true);
            hierMesh.chunkVisible("RackTypeD_D0", false);
            hierMesh.chunkVisible("RackTypeF_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU_L_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU_R_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU2_L_D0", false);
            hierMesh.chunkVisible("RackTypeTGPU2_R_D0", false);
            return;
        }
    }

    public boolean bSightAutomation;
    public float   fSightCurDistance;
    public float   fSightCurForwardAngle;
    public float   fSightCurSideslip;
    public float   fSightCurAltitude;
    public float   fSightCurSpeed;
    public float   fSightCurReadyness;
    public float   fSightSetForwardAngle;

    static {
        Class class1 = Potez542.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Potez542");
        Property.set(class1, "meshName", "3DO/Plane/Potez540/hier542.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar01());
        Property.set(class1, "yearService", 1936.8F);
        Property.set(class1, "yearExpired", 1943.5F);
        Property.set(class1, "FlightModel", "FlightModels/Potez542.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitPotez540.class, CockpitPotez542_Bombardier.class });
        Property.set(class1, "LOSElevation", 0.76315F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 10, 11, 12, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 });
        Aircraft.weaponHooksRegister(class1,
                new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_BombSpawn01", "_BombSpawn02", "_BombSpawn03", "_BombSpawn04", "_BombSpawn05", "_BombSpawn06", "_BombSpawn07", "_BombSpawn08", "_BombSpawn09", "_BombSpawn10", "_BombSpawn11", "_BombSpawn12", "_BombSpawn13", "_BombSpawn14", "_BombSpawn15", "_BombSpawn16", "_BombSpawn17", "_BombSpawn18", "_BombSpawn19", "_BombSpawn20", "_BombSpawn21", "_BombSpawn22", "_BombSpawn23", "_BombSpawn24", "_BombSpawn25", "_BombSpawn26", "_BombSpawn27", "_BombSpawn28", "_BombSpawn29", "_BombSpawn30", "_BombSpawn31", "_BombSpawn32", "_BombSpawn33", "_BombSpawn34", "_BombSpawn35", "_BombSpawn36", "_BombSpawn37", "_BombSpawn38", "_BombSpawn39", "_BombSpawn40", "_BombSpawn41", "_BombSpawn42", "_BombSpawn43", "_BombSpawn44", "_BombSpawn45", "_BombSpawn46", "_BombSpawn47", "_BombSpawn48", "_BombSpawn49", "_BombSpawn50", "_BombSpawn51", "_BombSpawn52", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06" });
    }
}
