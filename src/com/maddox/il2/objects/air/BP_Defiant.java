package com.maddox.il2.objects.air;

import com.maddox.il2.ai.War;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.fm.Turret;
import com.maddox.il2.game.HUD;
import com.maddox.il2.objects.weapons.MGunDummy;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.CommonTools;
import com.maddox.sas1946.il2.util.Reflection;

public class BP_Defiant extends DefiantRAF implements TypeTNBFighter {
    public boolean turretAngles(int i, float af[]) {
        boolean retVal = super.turretAngles(i, af);
        if (i != 0) {
            return retVal;
        }
        if (af[1] > 84F) {
            af[1] = 84F;
            retVal = false;
        } else if (af[1] < 0F) {
            af[1] = 0F;
            retVal = false;
        }
        float minaf1 = 0F;
        if ((Math.abs(af[0]) > 100F) && (Math.abs(af[0]) <= 175F)) {
            minaf1 = Math.max(af[1], CommonTools.smoothCvt(Math.abs(af[0]), 100F, 115F, 0F, Aircraft.cvt(this.FM.CT.getCockpitDoor(), 0.01F, 0.99F, 19F, 60F)));
        } else if (Math.abs(af[0]) > 175F) {
            minaf1 = Math.max(af[1], CommonTools.smoothCvt(Math.abs(af[0]), 175F, 179F, Aircraft.cvt(this.FM.CT.getCockpitDoor(), 0.01F, 0.99F, 19F, 60F), 19F));
        }
        if (af[1] < minaf1) {
            af[1] = minaf1;
            retVal = false;
        }
        return retVal;
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        for (int i = 1; i < 3; i++) {
            if (this.FM.getAltitude() < 3000F) {
                this.hierMesh().chunkVisible("HMask" + i + "_D0", false);
            } else {
                this.hierMesh().chunkVisible("HMask" + i + "_D0", this.hierMesh().isChunkVisible("Pilot" + i + "_D0"));
            }
        }

    }

    public void doKillPilot(int i) {
        switch (i) {
            case 1:
                this.FM.turret[0].bIsOperable = false;
                this.FM.turret[0].setHealth(0F);
                break;
        }
    }

    public void doMurderPilot(int i) {
        switch (i) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                break;

            case 1:
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("HMask2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                break;
        }
    }

    public void auxPressed(int i) {
        super.auxPressed(i);
        if (i != 20) {
            return;
        }
        this.turretMode++;
        if (this.turretMode > BP_Defiant.TURRET_MODE_MAX) {
            this.turretMode = BP_Defiant.TURRET_MODE_NORMAL;
        }
        HUD.log("Turret Mode: " + BP_Defiant.TURRET_MODES[this.turretMode]);
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.lastTurretActivationRequested = Long.MIN_VALUE;
    }

    public void update(float f) {
        super.update(f);
        this.updateTurretStatus(f);
        switch (this.turretMode) {
            case TURRET_MODE_HEAD_TRACKING:
            case TURRET_MODE_LOCKED_FWD:
                this.FM.CT.WeaponControl[10] = this.FM.CT.WeaponControl[0];
            case TURRET_MODE_LOCKED_AFT:
                this.FM.turret[0].bIsAIControlled = false;
                break;
            case TURRET_MODE_NORMAL:
            default:
                break;
        }
        if (this.FM.CT.Weapons[0][0] instanceof MGunDummy) {
            MGunDummy gun = (MGunDummy) this.FM.CT.Weapons[0][0];
            if (this.FM.CT.Weapons[10][0].haveBullets() && this.FM.turret[0].health > 0F) {
                gun.loadBullets();
            } else {
                gun.emptyGun();
            }
        }
    }

    float oldAzimuth   = 0F;
    float oldElevation = 0F;

    private void turretFollowsPilotHead(float tickLen) {
        if (!this.FM.turret[0].bIsOperable || (this != World.getPlayerAircraft())) {
            return;
        }
        if (!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) {
            return;
        }
        if (!Config.isUSE_RENDER()) {
            return;
        }
        this.updateRotation(this.viewAzimut + 180F, this.viewTangage, tickLen);
    }

    private void updateRotation(float azimut, float tangage, float tickLen) {
        this.FM.turret[0].tuLim[0] = azimut;
        this.FM.turret[0].tuLim[1] = tangage;
        this.turretAngles(0, this.FM.turret[0].tuLim);
        if (this.FM.turret[0].tuLim[0] < 0) {
            this.FM.turret[0].tuLim[0] += 360F;
        }
        Class[] paramTypes = { Turret.class, float.class };
        Object[] params = { this.FM.turret[0], new Float(tickLen) };
        Reflection.invokeMethod(this.FM, "updateRotation", paramTypes, params);
    }

    private void updateTurretStatus(float tickLen) {
        this.updateTurretLockStatus(tickLen);
        if (this.turretMode == BP_Defiant.TURRET_MODE_HEAD_TRACKING) {
            this.turretFollowsPilotHead(tickLen);
        }
    }

    private void updateTurretLockStatus(float tickLen) {
        switch (this.turretMode) {
            case TURRET_MODE_NORMAL:
                boolean activateTurret = !this.FM.turret[0].bIsAIControlled || (War.getNearestEnemy(this, 5000F) != null);
                if (activateTurret) {
                    this.lastTurretActivationRequested = Time.current();
                }
                float targetAnimation = Time.current() < (this.lastTurretActivationRequested + BP_Defiant.TURRET_ACTIVATED_DURATION) ? 0F : 1F;
                boolean lock = targetAnimation > 0.5F;
                if (lock) {
                    this.FM.turret[0].bIsOperable = false;
                    this.turretReadyToLock = ((Math.abs(this.FM.turret[0].tu[0]) < 0.01F) || (Math.abs(this.FM.turret[0].tu[0]) > 359.99F)) && ((Math.abs(this.FM.turret[0].tu[1]) < 0.01F) || (Math.abs(this.FM.turret[0].tu[1]) > 359.99F));
                    if (!this.turretReadyToLock) {
                        this.updateRotation(0F, 0F, tickLen);
                    } else {
                        if ((targetAnimation - this.turretLockPosition) > 0.01F) {
                            this.turretLockPosition += Math.min(0.01F, targetAnimation - this.turretLockPosition);
                            this.animateFairings(this.turretLockPosition);
                        }
                    }
                } else {
                    if (this.turretLockPosition > 0.01F) {
                        this.FM.turret[0].bIsOperable = false;
                        this.turretLockPosition -= Math.min(0.01F, this.turretLockPosition);
                        this.animateFairings(this.turretLockPosition);
                    } else {
                        this.FM.turret[0].bIsOperable = this.FM.turret[0].health > 0F;
                    }
                }
                break;
            case TURRET_MODE_LOCKED_AFT:
                this.FM.turret[0].bIsOperable = false;
                this.turretReadyToLock = ((Math.abs(this.FM.turret[0].tu[0]) < 0.01F) || (Math.abs(this.FM.turret[0].tu[0]) > 359.99F)) && ((Math.abs(this.FM.turret[0].tu[1]) < 0.01F) || (Math.abs(this.FM.turret[0].tu[1]) > 359.99F));
                if (!this.turretReadyToLock) {
                    if (this.turretLockPosition > 0.01F) {
                        this.turretLockPosition -= Math.min(0.01F, this.turretLockPosition);
                        this.animateFairings(this.turretLockPosition);
                    } else {
                        this.updateRotation(0F, 0F, tickLen);
                    }
                } else {
                    if ((1F - this.turretLockPosition) > 0.01F) {
                        this.turretLockPosition += Math.min(0.01F, 1F - this.turretLockPosition);
                        this.animateFairings(this.turretLockPosition);
                    }
                }
                break;
            case TURRET_MODE_LOCKED_FWD:
                this.FM.turret[0].bIsOperable = false;
                float azimuth = (this.FM.turret[0].tu[0] + 3600F) % 360F;
                this.turretReadyToLock = ((Math.abs(azimuth - 180F) < 0.01F)) && ((Math.abs(this.FM.turret[0].tu[1] - 19F) < 0.01F));
                if (!this.turretReadyToLock) {
                    if (this.turretLockPosition > 0.01F) {
                        this.turretLockPosition -= Math.min(0.01F, this.turretLockPosition);
                        this.animateFairings(this.turretLockPosition);
                    } else {
                        if (Math.abs(azimuth - 180F) > 5F) {
                            this.updateRotation(175F, 19F, tickLen);
                        } else {
                            this.updateRotation(180F, 19F, tickLen);
                        }
                    }
                } else {
                    if ((1F - this.turretLockPosition) > 0.01F) {
                        this.turretLockPosition += Math.min(0.01F, 1F - this.turretLockPosition);
                        this.animateFairings(this.turretLockPosition);
                    }
                }
                break;
            case TURRET_MODE_HEAD_TRACKING:
                this.lastTurretActivationRequested = Time.current();
                if (this.turretLockPosition > 0.01F) {
                    this.FM.turret[0].bIsOperable = false;
                    this.turretLockPosition -= Math.min(0.01F, this.turretLockPosition);
                    this.animateFairings(this.turretLockPosition);
                } else {
                    this.FM.turret[0].bIsOperable = this.FM.turret[0].health > 0F;
                }
                break;
            default:
                break;
        }
    }

    private void animateFairings(float lockPos) {
        this.hierMesh().chunkSetAngles("Fairing1_D0", 0.0F, 0.0F, CommonTools.smoothCvt(lockPos, 0F, 1F, -6F, 0F));
        this.hierMesh().chunkSetAngles("Fairing2_D0", 0.0F, 0.0F, CommonTools.smoothCvt(lockPos, 0F, 1F, 23F, 0F));
        this.resetYPRmodifier();
        Aircraft.xyz[1] = CommonTools.smoothCvt(lockPos, 0F, 1F, -0.14F, 0F);
        this.hierMesh().chunkSetLocate("Actuator2_D0", Aircraft.xyz, Aircraft.ypr);
    }

    public byte getTurretMode() {
        return this.turretMode;
    }

    public float getTurretLockPosition() {
        return this.turretLockPosition;
    }

    private boolean               turretReadyToLock             = true;
    private float                 turretLockPosition            = 1F;
    private long                  lastTurretActivationRequested = 0L;
    private static final long     TURRET_ACTIVATED_DURATION     = 30000L;
    static final byte             TURRET_MODE_NORMAL            = 0;
    private static final byte     TURRET_MODE_LOCKED_AFT        = 1;
    private static final byte     TURRET_MODE_LOCKED_FWD        = 2;
    private static final byte     TURRET_MODE_HEAD_TRACKING     = 3;
    private static final byte     TURRET_MODE_MAX               = BP_Defiant.TURRET_MODE_HEAD_TRACKING;
    private static final String[] TURRET_MODES                  = { "Normal (AI/Player)", "Disabled", "Locked Forward", "Head Tracking" };
    private byte                  turretMode                    = BP_Defiant.TURRET_MODE_NORMAL;

    static {
        Class class1 = BP_Defiant.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Defiant");
        Property.set(class1, "meshName", "3DO/Plane/BPDefiant(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar00());
        Property.set(class1, "yearService", 1938F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/DefiantMkI.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitDefiantF.class, CockpitDefiant_AGunner.class });
        Property.set(class1, "LOSElevation", 0.965F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 10, 10, 10, 10 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN00", "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04" });
    }
}
