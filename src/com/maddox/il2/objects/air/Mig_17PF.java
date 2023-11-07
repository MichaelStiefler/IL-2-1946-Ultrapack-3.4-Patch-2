package com.maddox.il2.objects.air;

import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.fm.Controls;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.objects.weapons.ActiveMissile;
import com.maddox.il2.objects.weapons.GuidedMissileUtils;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class Mig_17PF extends Mig_17 implements TypeGuidedMissileCarrier, TypeCountermeasure, TypeThreatDetector, TypeGSuit {

    public Mig_17PF() {
        this.hasChaff = false;
        this.hasFlare = false;
        this.lastChaffDeployed = 0L;
        this.lastFlareDeployed = 0L;
        this.lastCommonThreatActive = 0L;
        this.intervalCommonThreat = 1000L;
        this.lastRadarLockThreatActive = 0L;
        this.intervalRadarLockThreat = 1000L;
        this.lastMissileLaunchThreatActive = 0L;
        this.intervalMissileLaunchThreat = 1000L;
        this.bToFire = false;
        this.mixedPairs = false;
        this.launchTwinTime = -1L;
        this.needsTwinLaunchIndex = -1;
        this.lastTwinLaunchIndex = -1;
        this.guidedMissileUtils = new GuidedMissileUtils(this);
    }

    public GuidedMissileUtils getGuidedMissileUtils() {
        return this.guidedMissileUtils;
    }

    public long getChaffDeployed() {
        if (this.hasChaff) return this.lastChaffDeployed;
        else return 0L;
    }

    public long getFlareDeployed() {
        if (this.hasFlare) return this.lastFlareDeployed;
        else return 0L;
    }

    public void setCommonThreatActive() {
        long curTime = Time.current();
        if (curTime - this.lastCommonThreatActive > this.intervalCommonThreat) {
            this.lastCommonThreatActive = curTime;
            this.doDealCommonThreat();
        }
    }

    public void setRadarLockThreatActive() {
        long curTime = Time.current();
        if (curTime - this.lastRadarLockThreatActive > this.intervalRadarLockThreat) {
            this.lastRadarLockThreatActive = curTime;
            this.doDealRadarLockThreat();
        }
    }

    public void setMissileLaunchThreatActive() {
        long curTime = Time.current();
        if (curTime - this.lastMissileLaunchThreatActive > this.intervalMissileLaunchThreat) {
            this.lastMissileLaunchThreatActive = curTime;
            this.doDealMissileLaunchThreat();
        }
    }

    private void doDealCommonThreat() {
    }

    private void doDealRadarLockThreat() {
    }

    private void doDealMissileLaunchThreat() {
    }

    public void getGFactors(TypeGSuit.GFactors theGFactors) {
        theGFactors.setGFactors(NEG_G_TOLERANCE_FACTOR, NEG_G_TIME_FACTOR, NEG_G_RECOVERY_FACTOR, POS_G_TOLERANCE_FACTOR, POS_G_TIME_FACTOR, POS_G_RECOVERY_FACTOR);
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        if (this.FM.isPlayers()) {
            this.FM.CT.bHasCockpitDoorControl = true;
            this.FM.CT.dvCockpitDoor = 0.5F;
        }
        if (this.thisWeaponsName.startsWith("K5M+R55") || this.thisWeaponsName.startsWith("K5Mf+R55f")) this.mixedPairs = true;
        this.guidedMissileUtils.onAircraftLoaded();
    }

    public void checkPairLaunch() {
        if (!this.mixedPairs) return;
//		for (int j=0; j<this.FM.CT.Weapons[2].length; j++) {
//			if (this.FM.CT.Weapons[2][j] instanceof MissileGun) {
//				MissileGun theMissileGun = (MissileGun) this.FM.CT.Weapons[2][j];
//				if (theMissileGun.haveBullets()) {
//					Class theBulletClass = theMissileGun.bulletClass();
//					if (Missile.class.isAssignableFrom(theBulletClass)) { // We've found a missile!
//						this.guidedMissileUtils.changeMissileClass(theBulletClass);
//					}
//				}
//			}
//		}
        if (this.isNetMirror()) return;
        if (!this.FM.AS.isMaster()) return;
//        if (!this.FM.isPlayers()) return;
        if (this.FM.isPlayers() && this.FM instanceof RealFlightModel && ((RealFlightModel) this.FM).isRealMode() && this.FM.CT.getRocketFireMode() != Controls.defaultFire) return;
//		System.out.print("Weapons on Trigger 2: ");
//		for (int i=0; i<this.FM.CT.Weapons[2].length; i++) {
//			System.out.print("" + this.FM.CT.Weapons[2][i].getClass().getName() + "(" + this.FM.CT.Weapons[2][i].countBullets() + ") ");
//		}
//		System.out.println();

        if (this.needsTwinLaunchIndex != -1) this.lastTwinLaunchIndex = this.needsTwinLaunchIndex;

        if (this.launchTwinTime == -1L) {
            if (this.needsTwinLaunchIndex != -1) {
                this.needsTwinLaunchIndex = -1;
                return;
            }
            for (int i = 0; i < 5; i += 4)
                if (this.FM.CT.Weapons[2][i] != null && this.FM.CT.Weapons[2][i + 2] != null && !this.FM.CT.Weapons[2][i].haveBullets() && this.FM.CT.Weapons[2][i + 2].haveBullets()) {
                    this.needsTwinLaunchIndex = i + 2;
//					System.out.println("this.needsTwinLaunchIndex = " + this.needsTwinLaunchIndex + ", this.lastTwinLaunchIndex = " + this.lastTwinLaunchIndex);
                    if (this.lastTwinLaunchIndex == this.needsTwinLaunchIndex) this.needsTwinLaunchIndex = -1;
//					System.out.print("Weapons on Trigger 2: ");
//					for (int j=0; j<this.FM.CT.Weapons[2].length; j++) {
//						System.out.print("" + this.FM.CT.Weapons[2][j].getClass().getName() + "(" + this.FM.CT.Weapons[2][j].countBullets() + ") ");
//					}
//					System.out.println();
                    break;
                }
        }

        if (this.needsTwinLaunchIndex > 0) if (this.launchTwinTime == -1L) this.launchTwinTime = Time.current() + PAIR_LAUNCH_DELAY;
//				System.out.println("this.launchTwinTime = " + this.launchTwinTime);
        else if (Time.current() > this.launchTwinTime) {
//				System.out.println("Releasing Missile " + this.needsTwinLaunchIndex);
            this.guidedMissileUtils.setStartLastMissile(0L);
            // this.FM.CT.Weapons[2][this.needsTwinLaunchIndex].shots(1);
            this.guidedMissileUtils.shootNextMissile();
//				this.FM.CT.WeaponControl[2] = true;
            this.launchTwinTime = -1L;
            if (this.FM instanceof RealFlightModel && ((RealFlightModel) this.FM).isRealMode() || !(this.FM instanceof Pilot)) return;

//				if (this.FM.AP instanceof AutopilotAI) ((AutopilotAI) this.FM.AP).setOverrideMissileControl(this.FM.CT, true);
            ActiveMissile twinMissile = null;
            for (int activeMissileIndex = 0; activeMissileIndex < GuidedMissileUtils.getActiveMissilesSize(); activeMissileIndex++) {
                ActiveMissile theActiveMissile = GuidedMissileUtils.getActiveMissile(activeMissileIndex);
                if (this == theActiveMissile.getOwner()) twinMissile = theActiveMissile;
            }
            if (twinMissile != null) this.guidedMissileUtils.setMissileTarget(twinMissile.getVictim());
//					System.out.println("Target set:" + (twinMissile.getVictim() == null ? "null" : twinMissile.getVictim().getClass().getName()));
        }
    }

    public boolean isPlayer() {
        return this.FM instanceof RealFlightModel && this.FM.isPlayers() && ((RealFlightModel) this.FM).isRealMode();
    }

    public void update(float f) {
        Actor overrideMissileTarget = !this.isNetMirror() && !this.isPlayer() && this.launchTwinTime == -1 && this.needsTwinLaunchIndex != -1 ? this.guidedMissileUtils.getMissileTarget() : null;
        this.guidedMissileUtils.update();
        if (overrideMissileTarget != null) this.guidedMissileUtils.setMissileTarget(overrideMissileTarget);
        super.update(f);
        this.checkPairLaunch();
        this.typeFighterAceMakerRangeFinder();
        if (this.FM.AS.isMaster() && Config.isUSE_RENDER()) if (this.FM.EI.engines[0].getPowerOutput() > 0.5F && this.FM.EI.engines[0].getStage() == 6) {
            if (this.FM.EI.engines[0].getPowerOutput() > 0.5F) if (this.FM.EI.engines[0].getPowerOutput() > 1.001F) this.FM.AS.setSootState(this, 0, 5);
            else this.FM.AS.setSootState(this, 0, 3);
        } else this.FM.AS.setSootState(this, 0, 0);
    }

    private GuidedMissileUtils guidedMissileUtils;
    private boolean            hasChaff;
    private boolean            hasFlare;
    private long               lastChaffDeployed;
    private long               lastFlareDeployed;
    private long               lastCommonThreatActive;
    private long               intervalCommonThreat;
    private long               lastRadarLockThreatActive;
    private long               intervalRadarLockThreat;
    private long               lastMissileLaunchThreatActive;
    private long               intervalMissileLaunchThreat;
    private static final float NEG_G_TOLERANCE_FACTOR = 1F;
    private static final float NEG_G_TIME_FACTOR      = 1F;
    private static final float NEG_G_RECOVERY_FACTOR  = 1F;
    private static final float POS_G_TOLERANCE_FACTOR = 1.8F;
    private static final float POS_G_TIME_FACTOR      = 1.5F;
    private static final float POS_G_RECOVERY_FACTOR  = 1F;
    public boolean             bToFire;
    private boolean            mixedPairs;
    private long               launchTwinTime;
    private int                needsTwinLaunchIndex;
    private int                lastTwinLaunchIndex;
    private static final long  PAIR_LAUNCH_DELAY      = 300L;

    static {
        Class var_class = Mig_17PF.class;
        new NetAircraft.SPAWN(var_class);
        Property.set(var_class, "iconFar_shortClassName", "MiG-17PF");
        Property.set(var_class, "meshName_ru", "3DO/Plane/MiG-17PF(Multi1)/hier.him");
        Property.set(var_class, "PaintScheme_ru", new PaintSchemeFCSPar1956());
        Property.set(var_class, "meshName_sk", "3DO/Plane/MiG-17PF(Multi1)/hier.him");
        Property.set(var_class, "PaintScheme_sk", new PaintSchemeFMPar1956());
        Property.set(var_class, "meshName_ro", "3DO/Plane/MiG-17PF(Multi1)/hier.him");
        Property.set(var_class, "PaintScheme_ro", new PaintSchemeFMPar1956());
        Property.set(var_class, "meshName_hu", "3DO/Plane/MiG-17PF(Multi1)/hier.him");
        Property.set(var_class, "PaintScheme_hu", new PaintSchemeFMPar1956());
        Property.set(var_class, "meshName", "3DO/Plane/MiG-17PF(Multi1)/hier.him");
        Property.set(var_class, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(var_class, "yearService", 1952.11F);
        Property.set(var_class, "yearExpired", 1960.3F);
        Property.set(var_class, "FlightModel", "FlightModels/MiG-17PF.fmd");
        Property.set(var_class, "cockpitClass", new Class[] { CockpitMig_17PF.class, CockpitMig_17Radar.class });
        Property.set(var_class, "LOSElevation", 0.725F);
        Aircraft.weaponTriggersRegister(var_class, new int[] { 0, 0, 0, 9, 9, 9, 9, 9, 9, 2, 2, 2, 2, 2, 2, 2, 2, 9, 9, 2, 2, 2, 2, 9, 9, 9, 9, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 9, 9, 9, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 });
        Aircraft.weaponHooksRegister(var_class,
                new String[] { "_CANNON01", "_CANNON02", "_CANNON03", "_ExternalDev03", "_ExternalDev04", "_ExternalDev01", "_ExternalDev02", "_ExternalDev05", "_ExternalDev06", "_ExternalRock01", "_ExternalRock01", "_ExternalRock02", "_ExternalRock02",
                        "_ExternalRock03", "_ExternalRock03", "_ExternalRock04", "_ExternalRock04", "_ExternalDev07", "_ExternalDev08", "_ExternalRock05", "_ExternalRock05", "_ExternalRock06", "_ExternalRock06", "_ExternalDev09", "_ExternalDev10",
                        "_ExternalDev11", "_ExternalDev12", "_ExternalRock07", "_ExternalRock08", "_ExternalRock09", "_ExternalRock10", "_ExternalRock11", "_ExternalRock12", "_ExternalRock13", "_ExternalRock14", "_ExternalRock15", "_ExternalRock16",
                        "_ExternalRock17", "_ExternalRock18", "_ExternalRock19", "_ExternalRock20", "_ExternalRock21", "_ExternalRock22", "_ExternalRock23", "_ExternalRock24", "_ExternalRock25", "_ExternalRock26", "_ExternalRock27", "_ExternalRock28",
                        "_ExternalRock29", "_ExternalRock30", "_ExternalRock31", "_ExternalRock32", "_ExternalRock33", "_ExternalRock34", "_ExternalRock35", "_ExternalRock36", "_ExternalRock37", "_ExternalRock38", "_ExternalDev17", "_ExternalDev13",
                        "_ExternalDev14", "_ExternalRock39", "_ExternalRock40", "_ExternalRock41", "_ExternalRock42", "_ExternalRock43", "_ExternalRock44", "_ExternalRock45", "_ExternalRock46", "_ExternalRock47", "_ExternalRock48", "_ExternalRock49",
                        "_ExternalRock50", "_ExternalRock51", "_ExternalRock52", "_ExternalRock53", "_ExternalRock54", "_ExternalRock55", "_ExternalRock56", "_ExternalRock57", "_ExternalRock58", "_ExternalRock59", "_ExternalRock60", "_ExternalRock61",
                        "_ExternalRock62", "_ExternalRock63", "_ExternalRock64", "_ExternalRock65", "_ExternalRock66", "_ExternalRock67", "_ExternalRock68", "_ExternalRock69", "_ExternalRock70" });
    }
}
