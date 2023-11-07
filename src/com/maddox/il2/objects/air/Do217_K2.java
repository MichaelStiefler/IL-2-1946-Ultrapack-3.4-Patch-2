package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.rts.Property;

public class Do217_K2 extends Do217 implements TypeX4Carrier, TypeGuidedBombCarrier {

    public Do217_K2() {
        this.bToFire = false;
        this.deltaAzimuth = 0.0F;
        this.deltaTangage = 0.0F;
        this.isGuidingBomb = false;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("WingRackR_D0", thisWeaponsName.startsWith("1xFritzX") || thisWeaponsName.startsWith("2xFritzX"));
        hierMesh.chunkVisible("WingRackL_D0", thisWeaponsName.startsWith("1xFritzX") || thisWeaponsName.startsWith("2xFritzX"));
        hierMesh.chunkVisible("WingRackR1_D0", thisWeaponsName.startsWith("1xHS293") || thisWeaponsName.startsWith("2xHS293"));
        hierMesh.chunkVisible("WingRackL1_D0", thisWeaponsName.startsWith("1xHS293") || thisWeaponsName.startsWith("2xHS293"));
    }
    
    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        if (i == 5) {
            if (f1 > 5F) {
                f1 = 5F;
                flag = false;
            }
            if (f1 < -5F) {
                f1 = -5F;
                flag = false;
            }
            if (f > 5F) {
                f = 5F;
                flag = false;
            }
            if (f < -5F) {
                f = -5F;
                flag = false;
            }
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    public boolean typeGuidedBombCisMasterAlive() {
        return this.isMasterAlive;
    }

    public void typeGuidedBombCsetMasterAlive(boolean flag) {
        this.isMasterAlive = flag;
    }

    public boolean typeGuidedBombCgetIsGuiding() {
        return this.isGuidingBomb;
    }

    public void typeGuidedBombCsetIsGuiding(boolean flag) {
        this.isGuidingBomb = flag;
    }

    public void typeX4CAdjSidePlus() {
        this.deltaAzimuth = 0.002F;
    }

    public void typeX4CAdjSideMinus() {
        this.deltaAzimuth = -0.002F;
    }

    public void typeX4CAdjAttitudePlus() {
        this.deltaTangage = 0.002F;
    }

    public void typeX4CAdjAttitudeMinus() {
        this.deltaTangage = -0.002F;
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

    public void typeBomberAdjDistancePlus() {
        this.fSightCurForwardAngle++;
        if (this.fSightCurForwardAngle > 85F) this.fSightCurForwardAngle = 85F;
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        if (!this.isGuidingBomb) HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) this.fSightCurForwardAngle) });
        if (this.bSightAutomation) this.typeBomberToggleAutomation();
    }

    public void typeBomberAdjDistanceMinus() {
        this.fSightCurForwardAngle--;
        if (this.fSightCurForwardAngle < 0.0F) this.fSightCurForwardAngle = 0.0F;
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        if (!this.isGuidingBomb) HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) this.fSightCurForwardAngle) });
        if (this.bSightAutomation) this.typeBomberToggleAutomation();
    }

    public void typeBomberAdjSideslipPlus() {
        if (!this.isGuidingBomb) {
            this.fSightCurSideslip += 0.1F;
            if (this.fSightCurSideslip > 3F) this.fSightCurSideslip = 3F;
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Integer((int) (this.fSightCurSideslip * 10F)) });
        }
    }

    public void typeBomberAdjSideslipMinus() {
        if (!this.isGuidingBomb) {
            this.fSightCurSideslip -= 0.1F;
            if (this.fSightCurSideslip < -3F) this.fSightCurSideslip = -3F;
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Integer((int) (this.fSightCurSideslip * 10F)) });
        }
    }

    public void typeBomberAdjAltitudePlus() {
        if (!this.isGuidingBomb) {
            this.fSightCurAltitude += 10F;
            if (this.fSightCurAltitude > 10000F) this.fSightCurAltitude = 10000F;
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) this.fSightCurAltitude) });
            this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        }
    }

    public void typeBomberAdjAltitudeMinus() {
        if (!this.isGuidingBomb) {
            this.fSightCurAltitude -= 10F;
            if (this.fSightCurAltitude < 850F) this.fSightCurAltitude = 850F;
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) this.fSightCurAltitude) });
            this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        }
    }

    protected void mydebug(String s) {
    }

    public boolean  bToFire;
    private float   deltaAzimuth;
    private float   deltaTangage;
    private boolean isGuidingBomb;
    private boolean isMasterAlive;

    static {
        Class class1 = Do217_K2.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Do-217");
        Property.set(class1, "meshName", "3do/plane/Do217_K2/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar02());
        Property.set(class1, "yearService", 1943F);
        Property.set(class1, "yearExpired", 1945F);
        Property.set(class1, "FlightModel", "FlightModels/Do217K-2.fmd");
        Property.set(class1, "cockpitClass",
                new Class[] { CockpitDo217_K1.class, CockpitDo217_Bombardier.class, CockpitDo217_NGunner.class, CockpitDo217_TGunner.class, CockpitDo217_BGunner.class, CockpitDo217_LGunner.class, CockpitDo217_RGunner.class, CockpitDo217_PGunner.class });
        Aircraft.weaponTriggersRegister(class1, new int[] { 10, 10, 11, 12, 13, 14, 15, 15, 15, 15, 3, 3, 3, 3, 3, 3, 3, 3, 9, 9 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN10", "_MGUN02", "_MGUN03", "_MGUN04", "_MGUN05", "_MGUN06", "_MGUN07", "_MGUN08", "_MGUN09", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04",
                "_ExternalBomb05", "_ExternalBomb06", "_ExternalBomb07", "_ExternalBomb08", "_ExternalDev01", "_ExternalDev02" });
    }
}
