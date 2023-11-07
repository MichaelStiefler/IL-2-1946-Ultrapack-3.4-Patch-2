package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;

public class B_24J100 extends B_24 implements TypeBomber, TypeX4Carrier, TypeGuidedBombCarrier {
    public boolean        bToFire               = false;
    private float         deltaAzimuth          = 0.0F;
    private float         deltaTangage          = 0.0F;
    private boolean       isGuidingBomb         = false;
    private boolean       isMasterAlive;
    public static boolean bChangedPit           = false;
    private boolean       bSightAutomation      = false;
    private boolean       bSightBombDump        = false;
    public float          fSightCurDistance     = 0.0F;
    public float          fSightCurForwardAngle = 0.0F;
    public float          fSightCurSideslip     = 0.0F;
    public float          fSightCurAltitude     = 3000.0F;
    public float          fSightCurSpeed        = 200.0F;
    public float          fSightCurReadyness    = 0.0F;
    private float         calibDistance;
    static final float[]  calibrationScale      = { 0.0F, 0.2F, 0.4F, 0.66F, 0.86F, 1.05F, 1.2F, 1.6F };

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.FM.AS.wantBeaconsNet(true);
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("BatWingRackR_D0", thisWeaponsName.endsWith("Bat"));
        hierMesh.chunkVisible("BatWingRackL_D0", thisWeaponsName.endsWith("Bat"));
    }

    public boolean typeGuidedBombCisMasterAlive() {
        return this.isMasterAlive;
    }

    public void typeGuidedBombCsetMasterAlive(boolean bool) {
        this.isMasterAlive = bool;
    }

    public boolean typeGuidedBombCgetIsGuiding() {
        return this.isGuidingBomb;
    }

    public void typeGuidedBombCsetIsGuiding(boolean bool) {
        this.isGuidingBomb = bool;
    }

    public void typeX4CAdjSidePlus() {
        this.deltaAzimuth = 0.0020F;
    }

    public void typeX4CAdjSideMinus() {
        this.deltaAzimuth = -0.0020F;
    }

    public void typeX4CAdjAttitudePlus() {
        this.deltaTangage = 0.0020F;
    }

    public void typeX4CAdjAttitudeMinus() {
        this.deltaTangage = -0.0020F;
    }

    public void typeX4CResetControls() {
        this.deltaAzimuth = this.deltaTangage = 0.0F;
    }

    public float typeX4CgetdeltaAzimuth() {
        return this.deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage() {
        return this.deltaTangage;
    }

    protected boolean cutFM(int i, int i_0_, Actor actor) {
        switch (i) {
            case 19:
                this.killPilot(this, 4);
                /* fall through */
            default:
                return super.cutFM(i, i_0_, actor);
        }
    }

    public boolean turretAngles(int i, float[] fs) {
        boolean bool = super.turretAngles(i, fs);
        float f = -fs[0];
        float f_1_ = fs[1];
        switch (i) {
            default:
                break;
            case 0:
                if (f < -85.0F) {
                    f = -85.0F;
                    bool = false;
                }
                if (f > 85.0F) {
                    f = 85.0F;
                    bool = false;
                }
                if (f_1_ < -32.0F) {
                    f_1_ = -32.0F;
                    bool = false;
                }
                if (f_1_ > 46.0F) {
                    f_1_ = 46.0F;
                    bool = false;
                }
                break;
            case 1:
                if (f_1_ < -0.0F) {
                    f_1_ = -0.0F;
                    bool = false;
                }
                if (f_1_ > 20.0F) {
                    f_1_ = 20.0F;
                    bool = false;
                }
                break;
            case 2:
                if (f_1_ < -70.0F) {
                    f_1_ = -70.0F;
                    bool = false;
                }
                if (f_1_ > 7.0F) {
                    f_1_ = 7.0F;
                    bool = false;
                }
                break;
            case 3:
                if (f < -35.0F) {
                    f = -35.0F;
                    bool = false;
                }
                if (f > 64.0F) {
                    f = 64.0F;
                    bool = false;
                }
                if (f_1_ < -37.0F) {
                    f_1_ = -37.0F;
                    bool = false;
                }
                if (f_1_ > 50.0F) {
                    f_1_ = 50.0F;
                    bool = false;
                }
                break;
            case 4:
                if (f < -67.0F) {
                    f = -67.0F;
                    bool = false;
                }
                if (f > 34.0F) {
                    f = 34.0F;
                    bool = false;
                }
                if (f_1_ < -37.0F) {
                    f_1_ = -37.0F;
                    bool = false;
                }
                if (f_1_ > 50.0F) {
                    f_1_ = 50.0F;
                    bool = false;
                }
                break;
            case 5:
                if (f < -85.0F) {
                    f = -85.0F;
                    bool = false;
                }
                if (f > 85.0F) {
                    f = 85.0F;
                    bool = false;
                }
                if (f_1_ < -32.0F) {
                    f_1_ = -32.0F;
                    bool = false;
                }
                if (f_1_ > 46.0F) {
                    f_1_ = 46.0F;
                    bool = false;
                }
        }
        fs[0] = -f;
        fs[1] = f_1_;
        return bool;
    }

    protected void mydebug(String string) {
        System.out.println(string);
    }

    public void rareAction(float f, boolean bool) {
        super.rareAction(f, bool);
    }

    private static final float toMeters(float f) {
        return 0.3048F * f;
    }

    private static final float toMetersPerSecond(float f) {
        return 0.4470401F * f;
    }

    public boolean typeBomberToggleAutomation() {
        this.bSightAutomation = !this.bSightAutomation;
        this.bSightBombDump = false;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAutomation" + (this.bSightAutomation ? "ON" : "OFF"));
        return this.bSightAutomation;
    }

    public void typeBomberAdjDistanceReset() {
        this.fSightCurDistance = 0.0F;
        this.fSightCurForwardAngle = 0.0F;
    }

    public void typeBomberAdjDistancePlus() {
        this.fSightCurForwardAngle++;
        if (this.fSightCurForwardAngle > 85.0F) this.fSightCurForwardAngle = 85.0F;
        this.fSightCurDistance = toMeters(this.fSightCurAltitude) * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        if (!this.isGuidingBomb) HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) this.fSightCurForwardAngle) });
        if (this.bSightAutomation) this.typeBomberToggleAutomation();
    }

    public void typeBomberAdjDistanceMinus() {
        this.fSightCurForwardAngle--;
        if (this.fSightCurForwardAngle < 0.0F) this.fSightCurForwardAngle = 0.0F;
        this.fSightCurDistance = toMeters(this.fSightCurAltitude) * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        if (!this.isGuidingBomb) HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) this.fSightCurForwardAngle) });
        if (this.bSightAutomation) this.typeBomberToggleAutomation();
    }

    public void typeBomberAdjSideslipReset() {
        this.fSightCurSideslip = 0.0F;
    }

    public void typeBomberAdjSideslipPlus() {
        if (!this.isGuidingBomb) {
            this.fSightCurSideslip += 0.1F;
            if (this.fSightCurSideslip > 3.0F) this.fSightCurSideslip = 3.0F;
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Integer((int) (this.fSightCurSideslip * 10.0F)) });
        }
    }

    public void typeBomberAdjSideslipMinus() {
        if (!this.isGuidingBomb) {
            this.fSightCurSideslip -= 0.1F;
            if (this.fSightCurSideslip < -3.0F) this.fSightCurSideslip = -3.0F;
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Integer((int) (this.fSightCurSideslip * 10.0F)) });
        }
    }

    public void typeBomberAdjAltitudeReset() {
        this.fSightCurAltitude = 3000.0F;
    }

    public void typeBomberAdjAltitudePlus() {
        this.fSightCurAltitude += 50.0F;
        if (this.fSightCurAltitude > 50000.0F) this.fSightCurAltitude = 50000.0F;
        if (!this.isGuidingBomb) HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitudeft", new Object[] { new Integer((int) this.fSightCurAltitude) });
        this.fSightCurDistance = toMeters(this.fSightCurAltitude) * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
    }

    public void typeBomberAdjAltitudeMinus() {
        this.fSightCurAltitude -= 50.0F;
        if (this.fSightCurAltitude < 1000.0F) this.fSightCurAltitude = 1000.0F;
        if (!this.isGuidingBomb) HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitudeft", new Object[] { new Integer((int) this.fSightCurAltitude) });
        this.fSightCurDistance = toMeters(this.fSightCurAltitude) * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
    }

    public void typeBomberAdjSpeedReset() {
        this.fSightCurSpeed = 200.0F;
    }

    public void typeBomberAdjSpeedPlus() {
        this.fSightCurSpeed += 10.0F;
        if (this.fSightCurSpeed > 450.0F) this.fSightCurSpeed = 450.0F;
        if (!this.isGuidingBomb) HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeedMPH", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberAdjSpeedMinus() {
        this.fSightCurSpeed -= 10.0F;
        if (this.fSightCurSpeed < 100.0F) this.fSightCurSpeed = 100.0F;
        if (!this.isGuidingBomb) HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeedMPH", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberUpdate(float f) {
        if (Math.abs(this.FM.Or.getKren()) > 4.5) {
            this.fSightCurReadyness -= 0.0666666F * f;
            if (this.fSightCurReadyness < 0.0F) this.fSightCurReadyness = 0.0F;
        }
        if (this.fSightCurReadyness < 1.0F) this.fSightCurReadyness += 0.0333333F * f;
        else if (this.bSightAutomation) {
            this.fSightCurDistance -= toMetersPerSecond(this.fSightCurSpeed) * f;
            if (this.fSightCurDistance < 0.0F) {
                this.fSightCurDistance = 0.0F;
                this.typeBomberToggleAutomation();
            }
            this.fSightCurForwardAngle = (float) Math.toDegrees(Math.atan(this.fSightCurDistance / toMeters(this.fSightCurAltitude)));
            this.calibDistance = toMetersPerSecond(this.fSightCurSpeed) * floatindex(Aircraft.cvt(toMeters(this.fSightCurAltitude), 0.0F, 7000.0F, 0.0F, 7.0F), calibrationScale);
            if (this.fSightCurDistance < this.calibDistance + toMetersPerSecond(this.fSightCurSpeed) * Math.sqrt(toMeters(this.fSightCurAltitude) * (2F / 9.81F))) this.bSightBombDump = true;
            if (this.bSightBombDump) if (this.FM.isTick(3, 0)) {
                if (this.FM.CT.Weapons[3] != null && this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1] != null && this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1].haveBullets()) {
                    this.FM.CT.WeaponControl[3] = true;
                    HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightBombdrop");
                }
            } else this.FM.CT.WeaponControl[3] = false;
        }
    }

    public void typeBomberReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
        netmsgguaranted.writeByte((this.bSightAutomation ? 1 : 0) | (this.bSightBombDump ? 2 : 0));
        netmsgguaranted.writeFloat(this.fSightCurDistance);
        netmsgguaranted.writeByte((int) this.fSightCurForwardAngle);
        netmsgguaranted.writeByte((int) ((this.fSightCurSideslip + 3.0F) * 33.33333F));
        netmsgguaranted.writeFloat(this.fSightCurAltitude);
        netmsgguaranted.writeByte((int) (this.fSightCurSpeed / 2.5F));
        netmsgguaranted.writeByte((int) (this.fSightCurReadyness * 200.0F));
    }

    public void typeBomberReplicateFromNet(NetMsgInput netmsginput) throws IOException {
        int i = netmsginput.readUnsignedByte();
        this.bSightAutomation = (i & 0x1) != 0;
        this.bSightBombDump = (i & 0x2) != 0;
        this.fSightCurDistance = netmsginput.readFloat();
        this.fSightCurForwardAngle = netmsginput.readUnsignedByte();
        this.fSightCurSideslip = -3.0F + netmsginput.readUnsignedByte() / 33.33333F;
        this.fSightCurAltitude = netmsginput.readFloat();
        this.fSightCurSpeed = netmsginput.readUnsignedByte() * 2.5F;
        this.fSightCurReadyness = netmsginput.readUnsignedByte() / 200.0F;
    }

    static {
        Class var_class = B_24J100.class;
        new NetAircraft.SPAWN(var_class);
        Property.set(var_class, "iconFar_shortClassName", "B-24");
        Property.set(var_class, "meshName", "3DO/Plane/B-24J-100-CF(Multi1)/hier.him");
        Property.set(var_class, "PaintScheme", new PaintSchemeBMPar05());
        Property.set(var_class, "meshName_us", "3DO/Plane/B-24J-100-CF(USA)/hier.him");
        Property.set(var_class, "PaintScheme_us", new PaintSchemeFMPar06());
        Property.set(var_class, "noseart", 1);
        Property.set(var_class, "yearService", 1943.5F);
        Property.set(var_class, "yearExpired", 2800.9F);
        Property.set(var_class, "FlightModel", "FlightModels/B-24J.fmd");
        Property.set(var_class, "cockpitClass", new Class[] { CockpitB_24J100.class, CockpitB_24J100_Bombardier.class, CockpitB_24J100_FGunner.class, CockpitB_24J100_TGunner.class, CockpitB_24J100_AGunner.class, CockpitB_24J100_BGunner.class,
                CockpitB_24J100_RGunner.class, CockpitB_24J100_LGunner.class });
        weaponTriggersRegister(var_class, new int[] { 10, 10, 11, 11, 12, 12, 13, 14, 15, 15, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 });
        weaponHooksRegister(var_class, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_MGUN05", "_MGUN06", "_MGUN07", "_MGUN08", "_MGUN09", "_MGUN10", "_BombSpawn01", "_BombSpawn02", "_BombSpawn03", "_BombSpawn04", "_BombSpawn05",
                "_BombSpawn06", "_BombSpawn07", "_BombSpawn08", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04" });
    }
}
