// Source File Name: GuidedMissileUtils.java
// Author:           Storebror
package com.maddox.il2.objects.weapons;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Geom;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.AutopilotAI;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.ai.ground.TgtFlak;
import com.maddox.il2.ai.ground.TgtShip;
import com.maddox.il2.ai.ground.TgtTank;
import com.maddox.il2.ai.ground.TgtTrain;
import com.maddox.il2.ai.ground.TgtVehicle;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.EnginesInterface;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.fm.Mass;
import com.maddox.il2.fm.Motor;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.Selector;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.sounds.SndAircraft;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.rts.Finger;
import com.maddox.rts.KryptoInputFilter;
import com.maddox.rts.Property;
import com.maddox.rts.SFSInputStream;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.Conversion;
import com.maddox.sas1946.il2.util.TrueRandom;
import com.maddox.sound.Sample;
import com.maddox.sound.SoundFX;
import com.maddox.util.NumberTokenizer;

public class GuidedMissileUtils {

    private class MissileDataForPk {
        private int   triggerNum;
        private float maxLaunchLoad;
        private float maxAngleToTarget;
        private float maxAngleFromTargetAft;
        private float minDist;
        private float optDist;
        private float maxDist;

        public int getTriggerNum() {
            return this.triggerNum;
        }

        public void setTriggerNum(int triggerNum) {
            this.triggerNum = triggerNum;
        }

        public float getMaxLaunchLoad() {
            return this.maxLaunchLoad;
        }

        public void setMaxLaunchLoad(float maxLaunchLoad) {
            this.maxLaunchLoad = maxLaunchLoad;
        }

        public float getMaxAngleToTarget() {
            return this.maxAngleToTarget;
        }

        public void setMaxAngleToTarget(float maxAngleToTarget) {
            this.maxAngleToTarget = maxAngleToTarget;
        }

        public float getMaxAngleFromTargetAft() {
            return this.maxAngleFromTargetAft;
        }

        public void setMaxAngleFromTargetAft(float maxAngleFromTargetAft) {
            this.maxAngleFromTargetAft = maxAngleFromTargetAft;
        }

        public float getMinDist() {
            return this.minDist;
        }

        public void setMinDist(float minDist) {
            this.minDist = minDist;
        }

        public float getOptDist() {
            return this.optDist;
        }

        public void setOptDist(float optDist) {
            this.optDist = optDist;
        }

        public float getMaxDist() {
            return this.maxDist;
        }

        public void setMaxDist(float maxDist) {
            this.maxDist = maxDist;
        }

        private MissileDataForPk() {
            this.triggerNum = 0;
            this.maxLaunchLoad = 99.9F;
            this.maxAngleToTarget = 99.9F;
            this.maxAngleFromTargetAft = 99.9F;
            this.minDist = 99.9F;
            this.optDist = 99.9F;
            this.maxDist = 99.9F;
        }
    }

    public static float angleActorBetween(Actor actorFrom, Actor actorTo) {
        float angleRetVal = 180.1F;
        double angleDoubleTemp = 0.0D;
        Loc angleActorLoc = new Loc();
        Point3d angleActorPos = new Point3d();
        Point3d angleTargetPos = new Point3d();
        Vector3d angleTargRayDir = new Vector3d();
        Vector3d angleNoseDir = new Vector3d();
        actorFrom.pos.getAbs(angleActorLoc);
        angleActorLoc.get(angleActorPos);
        actorTo.pos.getAbs(angleTargetPos);
        angleTargRayDir.sub(angleTargetPos, angleActorPos);
        angleDoubleTemp = angleTargRayDir.length();
        angleTargRayDir.scale(1.0D / angleDoubleTemp);
        angleNoseDir.set(1.0D, 0.0D, 0.0D);
        angleActorLoc.transform(angleNoseDir);
        angleDoubleTemp = angleNoseDir.dot(angleTargRayDir);
        angleRetVal = Geom.RAD2DEG((float) Math.acos(angleDoubleTemp));
        return angleRetVal;
    }

    public static float angleBetween(Actor actorFrom, Actor actorTo) {
        float angleRetVal = 180.1F;
        double angleDoubleTemp = 0.0D;
        Loc angleActorLoc = new Loc();
        Point3d angleActorPos = new Point3d();
        Point3d angleTargetPos = new Point3d();
        Vector3d angleTargRayDir = new Vector3d();
        Vector3d angleNoseDir = new Vector3d();
        actorFrom.pos.getAbs(angleActorLoc);
        angleActorLoc.get(angleActorPos);
        actorTo.pos.getAbs(angleTargetPos);
        angleTargRayDir.sub(angleTargetPos, angleActorPos);
        angleDoubleTemp = angleTargRayDir.length();
        angleTargRayDir.scale(1.0D / angleDoubleTemp);
        angleNoseDir.set(1.0D, 0.0D, 0.0D);
        angleActorLoc.transform(angleNoseDir);
        angleDoubleTemp = angleNoseDir.dot(angleTargRayDir);
        angleRetVal = Geom.RAD2DEG((float) Math.acos(angleDoubleTemp));
        return angleRetVal;
    }

    public static float angleBetween(Actor actorFrom, Vector3d targetVector) {
        Vector3d theTargetVector = new Vector3d();
        theTargetVector.set(targetVector);
        double angleDoubleTemp = 0.0D;
        Loc angleActorLoc = new Loc();
        Point3d angleActorPos = new Point3d();
        Vector3d angleNoseDir = new Vector3d();
        actorFrom.pos.getAbs(angleActorLoc);
        angleActorLoc.get(angleActorPos);
        angleDoubleTemp = theTargetVector.length();
        theTargetVector.scale(1.0D / angleDoubleTemp);
        angleNoseDir.set(1.0D, 0.0D, 0.0D);
        angleActorLoc.transform(angleNoseDir);
        angleDoubleTemp = angleNoseDir.dot(theTargetVector);
        return Geom.RAD2DEG((float) Math.acos(angleDoubleTemp));
    }

    public static float angleBetween(Actor actorFrom, Vector3f targetVector) {
        return angleBetween(actorFrom, new Vector3d(targetVector));
    }

    public static double distanceBetween(Actor actorFrom, Actor actorTo) {
        double distanceRetVal = 99999.999D;
        if (!Actor.isValid(actorFrom) || !Actor.isValid(actorTo)) return distanceRetVal;
        Loc distanceActorLoc = new Loc();
        Point3d distanceActorPos = new Point3d();
        Point3d distanceTargetPos = new Point3d();
        actorFrom.pos.getAbs(distanceActorLoc);
        distanceActorLoc.get(distanceActorPos);
        actorTo.pos.getAbs(distanceTargetPos);
        distanceRetVal = distanceActorPos.distance(distanceTargetPos);
        return distanceRetVal;
    }

    public static void LocalLog(Actor logActor, int i, String logLine) {
        if (logActor == World.getPlayerAircraft() && !logActor.isNetMirror()) HUD.log(i, logLine);
    }

    public static void LocalLog(Actor logActor, String logLine) {
        if (logActor == World.getPlayerAircraft() && !logActor.isNetMirror()) HUD.log(logLine);
    }

    public GuidedMissileUtils(Actor owner) {
        this.initParams(owner);
    }

    public GuidedMissileUtils(Actor theOwner, float theMissileMaxSpeedMeterPerSecond, float theLeadPercent, float theMaxG, float theStepsForFullTurn, float thePkMaxAngle, float thePkMaxAngleAft, float thePkMinDist, float thePkOptDist, float thePkMaxDist,
            float thePkMaxG, float theMaxPOVfrom, float theMaxPOVto, float theMaxDistance, float theMinPkForAttack, long theMillisecondsBetweenMissileLaunchAI, long theTargetType, boolean theAttackDecisionByAI, boolean theMultiTrackingCapable,
            boolean theCanTrackSubs, String theLockFx, String theNoLockFx, String theLockSmpl, String theNoLockSmpl) {
        this.initParams(theOwner, theMissileMaxSpeedMeterPerSecond, theLeadPercent, theMaxG, theStepsForFullTurn, thePkMaxAngle, thePkMaxAngleAft, thePkMinDist, thePkOptDist, thePkMaxDist, thePkMaxG, theMaxPOVfrom, theMaxPOVto, theMaxDistance,
                theMinPkForAttack, theMillisecondsBetweenMissileLaunchAI, theTargetType, theAttackDecisionByAI, theMultiTrackingCapable, theCanTrackSubs, theLockFx, theNoLockFx, theLockSmpl, theNoLockSmpl);
    }

    private static boolean actorIsAI(Actor theActor) {
        if (!(theActor instanceof Aircraft)) return true;
        if (((Aircraft) theActor).FM == null) return true;
        if ((theActor != World.getPlayerAircraft() || !((RealFlightModel) ((Aircraft) theActor).FM).isRealMode()) && ((Aircraft) theActor).FM instanceof Pilot) return true;
        return false;
    }

    public void cancelMissileGrowl() {
        if (this.missileOwner != World.getPlayerAircraft()) return;
        if (this.fxMissileToneLock != null) this.fxMissileToneLock.cancel();
        if (this.fxMissileToneNoLock != null) this.fxMissileToneNoLock.cancel();
    }

    public void changeMissileClass(Class theNewMissileClass) { // new function to switch missile types
        this.cancelMissileGrowl(); // stop
        this.missilesList.clear();
        this.myMissileClass = theNewMissileClass;
        this.lockTonesInitialized = false;
        this.createMissileList(this.missilesList, theNewMissileClass);
        this.setMissileGrowl(0);
        this.iMissileLockState = 0;
    }

    private void changeMissileGrowl(int iMode) {
        if (this.missileOwner != World.getPlayerAircraft()) return;
        this.setMissileGrowl(iMode);
        switch (iMode) {
            case 1:
                this.playMissileGrowlLock(false);
                break;
            case 2:
                this.playMissileGrowlLock(true);
                break;
            default:
                this.cancelMissileGrowl();
                break;
        }
    }

    private void checkAIlaunchMissile() {
//        System.out.println("00");
        if (!(this.missileOwner instanceof Aircraft)) return;
        if (!this.attackDecisionByAI) return;
        Aircraft ownerAircraft = (Aircraft) this.missileOwner;
//        if (ownerAircraft == World.getPlayerAircraft()) System.out.println("01");
        if (ownerAircraft.FM instanceof RealFlightModel && ((RealFlightModel) ownerAircraft.FM).isRealMode() || !(ownerAircraft.FM instanceof Pilot)) return;
        if (this.missilesList.isEmpty()) return;
//        if (ownerAircraft == World.getPlayerAircraft()) System.out.println("02");

        Pilot pilot = (Pilot) ownerAircraft.FM;

        if (pilot.target != null) {
            this.trgtAI = pilot.target.actor;

            GuidedMissileUtils.checkAllActiveMissilesValidity();
            for (int activeMissileIndex = 0; activeMissileIndex < GuidedMissileUtils.getActiveMissilesSize(); activeMissileIndex++) {
                ActiveMissile theActiveMissile = GuidedMissileUtils.getActiveMissile(activeMissileIndex);
                if (theActiveMissile.isAI()) if (ownerAircraft.getArmy() == theActiveMissile.getOwnerArmy()) if (theActiveMissile.getVictim() == this.trgtAI) {
                    this.trgtAI = null;
                    break;
                }
            }
        } else this.trgtAI = null;

        if (this.trgtAI == null) this.trgtAI = this.getMissileTarget();
        if (this.trgtAI != null) if (ownerAircraft.getArmy() == this.trgtAI.getArmy()) {
            this.trgtAI = null;
            this.trgtPk = 0F;
            return;
        }
//        if (ownerAircraft == World.getPlayerAircraft()) System.out.println("03");

        if (this.trgtAI instanceof Aircraft || this.trgtAI instanceof MissileInterceptable) // this.setMissileTarget(this.trgtAI);
            this.trgtPk = this.getMissilePk(this.trgtAI);
//			if (this.getMissileOwner() == World.getPlayerAircraft()) HUD.training("" + this.trgtPk + "/" + this.getMinPkForAttack());
        else this.trgtPk = 0F;
        // return;

        Actor missileTarget = this.getMissileTarget();
//		System.out.println("missileTarget=" + (missileTarget==null?"null":missileTarget.getClass().getName()));
        if (Missile.actorHasPos(missileTarget) && ownerAircraft.getArmy() != missileTarget.getArmy()) {
            float altTrgtPk = this.getMissilePk(missileTarget);
            if (altTrgtPk > this.trgtPk) {
                this.trgtPk = altTrgtPk;
                this.trgtAI = missileTarget;
            }
//			if (this.getMissileOwner() == World.getPlayerAircraft()) HUD.training("A " + this.trgtPk + "/" + this.getMinPkForAttack());
        }

        if (ownerAircraft.FM.AP instanceof AutopilotAI) ((AutopilotAI) ownerAircraft.FM.AP).setOverrideMissileControl(ownerAircraft.FM.CT, false);

//        if (ownerAircraft == World.getPlayerAircraft()) HUD.training("Pk=" + this.trgtPk + "/" + this.getMinPkForAttack() + ", t=" + (Time.current() - this.tMissilePrev - this.getMillisecondsBetweenMissileLaunchAI()));
        if (this.trgtPk > this.getMinPkForAttack() && Actor.isValid(this.trgtAI) && this.trgtAI.getArmy() != ownerAircraft.FM.actor.getArmy() && Time.current() > this.tMissilePrev + this.getMillisecondsBetweenMissileLaunchAI()
                && GuidedMissileUtils.noLaunchSince(minTimeBetweenAIMissileLaunch, ownerAircraft.FM.actor.getArmy()) && missilesLeft(ownerAircraft.FM.CT.Weapons[ownerAircraft.FM.CT.rocketHookSelected])) {
            if (isTargetHandledByAi(ownerAircraft.FM.actor.getArmy(), this.trgtAI)) return;
//            if (ownerAircraft == World.getPlayerAircraft()) System.out.println("trgtPk=" + this.trgtPk + ", minPk=" + this.getMinPkForAttack() + ", tDiff=" + Time.current() + "/" + (this.tMissilePrev + this.getMillisecondsBetweenMissileLaunchAI()) + ", noLaunchSince=" + GuidedMissileUtils.noLaunchSince(minTimeBetweenAIMissileLaunch, ownerAircraft.FM.actor.getArmy()) + ", missilesLeft=" + missilesLeft(ownerAircraft.FM.CT.Weapons[ownerAircraft.FM.CT.rocketHookSelected]));
            addTargetHandledByAi(ownerAircraft.FM.actor.getArmy(), this.trgtAI);
            this.tMissilePrev = Time.current();
            ownerAircraft.FM.CT.WeaponControl[ownerAircraft.FM.CT.rocketHookSelected] = true;
            if (ownerAircraft.FM.AP instanceof AutopilotAI) ((AutopilotAI) ownerAircraft.FM.AP).setOverrideMissileControl(ownerAircraft.FM.CT, true);
        }
    }

    public void checkLockStatus() {
        int iOldLockState = this.iMissileLockState;
        boolean bEnemyInSight = false;
        boolean bSidewinderLocked = false;
        boolean bNoEnemyTimeout = false;
        try {
            if (((Aircraft) this.missileOwner).FM.CT.BrakeControl == 1.0F) {
                if (!this.oldBreakControl) {
                    this.oldBreakControl = true;
                    if (!((Aircraft) this.missileOwner).FM.Gears.onGround()) {
                        this.engageMode--;
                        if (this.engageMode < this.ENGAGE_OFF) this.engageMode = this.ENGAGE_ON;
                        switch (this.engageMode) {
                            case ENGAGE_OFF:
                                if (this.missileName == null) {} else LocalLog(this.missileOwner, this.missileName + " Engagement OFF");
                                break;
                            case ENGAGE_AUTO:
                                if (this.missileName == null) {} else LocalLog(this.missileOwner, this.missileName + " Engagement AUTO");
                                break;
                            case ENGAGE_ON:
                                if (this.missileName == null) {} else LocalLog(this.missileOwner, this.missileName + " Engagement ON");
                                break;
                        }
                    }
                }
            } else this.oldBreakControl = false;
        } catch (Exception exception) {}
        try {
            if (this.missileOwner != World.getPlayerAircraft()) {
                if (this.iMissileLockState != 0) this.iMissileLockState = 0;
                return;
            }
            if (!this.hasMissiles()) {
                if (this.iMissileLockState != 0) {
                    this.changeMissileGrowl(0);
                    LocalLog(this.missileOwner, this.missileName + " missiles depleted");
                    this.iMissileLockState = 0;
                }
                return;
            }
            if (this.engageMode == this.ENGAGE_OFF) {
                if (this.iMissileLockState != 0) {
                    this.changeMissileGrowl(0);
                    LocalLog(this.missileOwner, this.missileName + " disengaged");
                    this.iMissileLockState = 0;
                }
                return;
            }

            // Actor potentialTarget = this.lookForGuidedMissileTarget(this.missileOwner, this.getMaxPOVfrom(), this.getMaxPOVto(), this.getPkMaxDist(), this.targetType);
//			if (Actor.isValid(potentialTarget)) {
//				bSidewinderLocked = true;
//				if (potentialTarget.getArmy() != World.getPlayerAircraft().getArmy()) {
//					bEnemyInSight = true;
//				}
//			}

            if (Actor.isValid(this.getMissileTarget())) {
                bSidewinderLocked = true;
                if (this.getMissileTarget().getArmy() != World.getPlayerAircraft().getArmy()) bEnemyInSight = true;
            }

            if (Actor.isValid(Main3D.cur3D().viewActor())) if (Main3D.cur3D().viewActor() == this.missileOwner) {
                Actor theEnemy = Selector.look(true, false, Main3D.cur3D()._camera3D[Main3D.cur3D().getRenderIndx()], World.getPlayerAircraft().getArmy(), -1, World.getPlayerAircraft(), false);
                if (Actor.isValid(theEnemy)) bEnemyInSight = true;
            }

            if (bEnemyInSight) this.tLastSeenEnemy = Time.current();
            else if (Time.current() - this.tLastSeenEnemy > 10000) bNoEnemyTimeout = true;

            if (bSidewinderLocked) {
                if (bEnemyInSight) this.iMissileLockState = 2;
                else if (this.engageMode == this.ENGAGE_ON) this.iMissileLockState = 2;
                else if (bNoEnemyTimeout) this.iMissileLockState = 0;
                else this.iMissileLockState = 2;
            } else if (bNoEnemyTimeout) this.iMissileLockState = 0;
            else this.iMissileLockState = 1;

            if (this.engageMode == this.ENGAGE_ON && this.iMissileLockState == 0) this.iMissileLockState = 1;
            if (((Aircraft) this.missileOwner).FM.getOverload() > this.fPkMaxG && this.iMissileLockState == 2) this.iMissileLockState = 1;

            switch (this.iMissileLockState) {
                case 1: {
                    if (iOldLockState != 1) this.changeMissileGrowl(1);
                    if (iOldLockState == 0) LocalLog(this.missileOwner, this.missileName + " engaged");
                    break;
                }
                case 2: {
                    if (iOldLockState != 2) this.changeMissileGrowl(2);
                    if (iOldLockState == 0) LocalLog(this.missileOwner, this.missileName + " engaged");
                    break;
                }
                case 0: {
                    if (iOldLockState != 0) {
                        this.changeMissileGrowl(0);
                        LocalLog(this.missileOwner, this.missileName + " disengaged");
                    }
                    break;
                }
                default: {
                    if (iOldLockState != 0) {
                        this.changeMissileGrowl(0);
                        LocalLog(this.missileOwner, this.missileName + " disengaged");
                    }
                    break;
                }
            }

        } catch (Exception exception) {}
    }

    private void checkPendingMissiles() {
        // TODO: Fix by SAS~Storebror: Iterate through the full missile list instead of just checking for first pending missile.
        // This is necessary because missiles with delay might be launched in salvo mode.
        try {
            for (int rocketsListIndex = 0; rocketsListIndex < this.missilesList.size(); rocketsListIndex++) {
                Object o = this.missilesList.get(rocketsListIndex);
                if (o instanceof MissileGunWithDelay) ((MissileGunWithDelay) o).checkPendingWeaponRelease();
            }
        } catch (IndexOutOfBoundsException ioobe) {}
    }

    public void createMissileList(ArrayList theMissileList) { // default missile init selects first available missile
        this.createMissileList(theMissileList, null);
    }

    // TODO: ++ Added/changed Code Multiple Missile Type Selection ++
    public void createMissileList(ArrayList theMissileList, Class theMissileClass) {
//		System.out.println("createMissileList " + (theMissileClass==null?"null":theMissileClass.getClass().getName()));
        Aircraft theMissileCarrier = (Aircraft) this.missileOwner;
        try {
            for (int l = 0; l < theMissileCarrier.FM.CT.Weapons.length; l++)
                if (theMissileCarrier.FM.CT.Weapons[l] != null) for (int l1 = 0; l1 < theMissileCarrier.FM.CT.Weapons[l].length; l1++)
                    if (theMissileCarrier.FM.CT.Weapons[l][l1] != null) if (theMissileCarrier.FM.CT.Weapons[l][l1] instanceof MissileGun) {
                        MissileGun theMissileGun = (MissileGun) theMissileCarrier.FM.CT.Weapons[l][l1];
                        if (theMissileGun.haveBullets()) {
                            Class theBulletClass = theMissileGun.bulletClass();
//									if (theMissileClass != null) {
//										if (!theBulletClass.getName().equals(theMissileClass.getName())) {
//											continue; // Not the type of missile we're searching for.
//										}
//									}
                            if (Missile.class.isAssignableFrom(theBulletClass)) { // We've found a missile!
                                if (theMissileClass == null) theMissileClass = theBulletClass;
                                theMissileList.add(theMissileCarrier.FM.CT.Weapons[l][l1]);
                            }
                        }
                    }
        } catch (Exception exception) {
            EventLog.type("Exception in createMissileList: " + exception.getMessage());
        }
        if (theMissileClass == null) return;
        this.getMissileProperties(theMissileClass);
    }

    private static int engineHeatSpreadType(Actor theActor) {
        if (!(theActor instanceof Aircraft)) return HEAT_SPREAD_360;
        EnginesInterface checkEI = ((FlightModelMain) ((SndAircraft) theActor).FM).EI;
        int iRetVal = HEAT_SPREAD_NONE;
        for (int i = 0; i < checkEI.getNum(); i++) {
            int iMotorType = checkEI.engines[i].getType();
            if (iMotorType == Motor._E_TYPE_JET || iMotorType == Motor._E_TYPE_ROCKET || iMotorType == Motor._E_TYPE_ROCKETBOOST || iMotorType == Motor._E_TYPE_PVRD) iRetVal |= HEAT_SPREAD_AFT;
            if (iMotorType == Motor._E_TYPE_INLINE || iMotorType == Motor._E_TYPE_RADIAL || iMotorType == Motor._E_TYPE_HELO_INLINE || iMotorType == Motor._E_TYPE_UNIDENTIFIED) iRetVal |= HEAT_SPREAD_360;
        }
        return iRetVal;
    }

    private static float enginePowerLevel(Actor theActor) {
        if (!(theActor instanceof Aircraft)) return 1F;
        EnginesInterface checkEI = ((FlightModelMain) ((Aircraft) theActor).FM).EI;
        float fRetVal = 1F;
        for (int i = 0; i < checkEI.getNum(); i++)
            fRetVal *= checkEI.engines[i].getControlThrottle() * (checkEI.engines[i].getControlAfterburner() ? checkEI.engines[i].afterburnerCompressorFactor : 1F);
        return fRetVal;
    }

    private static int engineHeatSpreadType(Motor theMotor) {
        int iRetVal = HEAT_SPREAD_NONE;
        int iMotorType = theMotor.getType();
        if (iMotorType == Motor._E_TYPE_JET || iMotorType == Motor._E_TYPE_ROCKET || iMotorType == Motor._E_TYPE_ROCKETBOOST || iMotorType == Motor._E_TYPE_PVRD) iRetVal |= HEAT_SPREAD_AFT;
        if (iMotorType == Motor._E_TYPE_INLINE || iMotorType == Motor._E_TYPE_RADIAL || iMotorType == Motor._E_TYPE_HELO_INLINE || iMotorType == Motor._E_TYPE_UNIDENTIFIED) iRetVal |= HEAT_SPREAD_360;
        return iRetVal;
    }

    public boolean getAttackDecisionByAI() {
        return this.attackDecisionByAI;
    }

    // TODO: For Countermeasures:
    public int getDetectorType() {
        int lockType = 0;
        if (this.iDetectorMode == Missile.DETECTOR_TYPE_INFRARED) lockType = 1;
        else if (this.iDetectorMode == Missile.DETECTOR_TYPE_RADAR_BEAMRIDING || this.iDetectorMode == Missile.DETECTOR_TYPE_RADAR_HOMING || this.iDetectorMode == Missile.DETECTOR_TYPE_RADAR_TRACK_VIA_MISSILE) lockType = 2;
        return lockType;
    }

    public boolean getCanTrackSubs() {
        return this.canTrackSubs;
    }

    public SoundFX getFxMissileToneLock() {
        return this.fxMissileToneLock;
    }

    public SoundFX getFxMissileToneNoLock() {
        return this.fxMissileToneNoLock;
    }

    public float getLeadPercent() {
        return this.fLeadPercent;
    }

    public float getMaxDistance() {
        return this.fMaxDistance;
    }

    public float getMaxG() {
        return this.fMaxG;
    }

    public float getMaxPOVfrom() {
        return this.fMaxPOVfrom;
    }

    public float getMaxPOVto() {
        return this.fMaxPOVto;
    }

    public long getMillisecondsBetweenMissileLaunchAI() {
        return this.millisecondsBetweenMissileLaunchAI;
    }

    public float getMinPkForAttack() {
        return this.minPkForAttack;
    }

    public Missile getMissileFromMissileGun(MissileGun theMissileGun) {
        return (Missile) theMissileGun.rocket;
    }

    public int getMissileGrowl() {
        return this.iMissileTone;
    }

    public int getMissileLockState() {
        return this.iMissileLockState;
    }

    public void setMissileLockState(int theLockState) {
        this.iMissileLockState = theLockState;
    }

    public float getMissileMaxSpeedKmh() {
        return this.fMissileMaxSpeedKmh;
    }

    public String getMissileName() {
        return this.missileName;
    }

    public Actor getMissileOwner() {
        return this.missileOwner;
    }

    private float getMissilePk(Actor theTarget) {
        float thePk = 0.0F;
        if (Actor.isValid(theTarget)) thePk = this.Pk(this.missileOwner, theTarget);

        return thePk;
    }

    public void getMissileProperties(Class theMissileClass) { // separate properties from missile list creation
        this.fPkMaxG = Property.floatValue(theMissileClass, "maxLockGForce", 99.9F);
        this.fMaxPOVfrom = Property.floatValue(theMissileClass, "maxFOVfrom", 99.9F);
        this.fMaxPOVto = Property.floatValue(theMissileClass, "maxFOVto", 99.9F);
        this.fPkMaxAngle = Property.floatValue(theMissileClass, "PkMaxFOVfrom", 99.9F);
        this.fPkMaxAngleAft = Property.floatValue(theMissileClass, "PkMaxFOVto", 99.9F);
        this.fPkMinDist = Property.floatValue(theMissileClass, "PkDistMin", 99.9F);
        this.fPkOptDist = Property.floatValue(theMissileClass, "PkDistOpt", 99.9F);
        this.fPkMaxDist = Property.floatValue(theMissileClass, "PkDistMax", 99.9F);
        this.fMissileMaxSpeedKmh = Property.floatValue(theMissileClass, "maxSpeed", 99.9F);
        this.fLeadPercent = Property.floatValue(theMissileClass, "leadPercent", 99.9F);
        this.fMaxG = Property.floatValue(theMissileClass, "maxGForce", 99.9F);
        this.iDetectorMode = Property.intValue(theMissileClass, "detectorType", Missile.DETECTOR_TYPE_MANUAL);
        this.attackDecisionByAI = Property.intValue(theMissileClass, "attackDecisionByAI", 0) != 0;
        this.canTrackSubs = Property.intValue(theMissileClass, "canTrackSubs", 0) != 0;
        this.multiTrackingCapable = Property.intValue(theMissileClass, "multiTrackingCapable", 0) != 0;
        this.minPkForAttack = Property.floatValue(theMissileClass, "minPkForAI", 25.0F);
        this.millisecondsBetweenMissileLaunchAI = Property.longValue(theMissileClass, "timeForNextLaunchAI", 10000L);
        this.targetType = Property.longValue(theMissileClass, "targetType", Missile.TARGET_AIR);
        this.missileName = Property.stringValue(theMissileClass, "friendlyName", "Missile");
        this.fSunRayAngle = Property.floatValue(theMissileClass, "sunRayAngle", 0F);
        this.bSingleTone = Property.intValue(theMissileClass, "singleTone", 0) != 0;
        this.fxLockPitch = Property.floatValue(theMissileClass, "fxLockPitch", 1F);
        this.fxNoLockPitch = Property.floatValue(theMissileClass, "fxNoLockPitch", 1F);
        this.fxLockVolume = Property.floatValue(theMissileClass, "fxLockVolume", 1F);
        this.fxNoLockVolume = Property.floatValue(theMissileClass, "fxNoLockVolume", 1F);
        this.iGrowlStyle = Property.intValue(theMissileClass, "growlStyle", 0);
        this.myMissileClass = theMissileClass;
        this.initLockTones();
    }

    public Actor getMissileTarget() {
        return this.trgtMissile;
    }

    public Point3f getMissileTargetOffset() {
        return this.getSelectedActorOffset();
    }

    public boolean getMultiTrackingCapable() {
        return this.multiTrackingCapable;
    }

    public float getPkMaxAngle() {
        return this.fPkMaxAngle;
    }

    public float getPkMaxAngleAft() {
        return this.fPkMaxAngleAft;
    }

    public float getPkMaxDist() {
        return this.fPkMaxDist;
    }

    public float getPkMaxG() {
        return this.fPkMaxG;
    }

    public float getPkMinDist() {
        return this.fPkMinDist;
    }

    public float getPkOptDist() {
        return this.fPkOptDist;
    }

    public Point3f getSelectedActorOffset() {
        return this.selectedActorOffset;
    }

    public Sample getSmplMissileLock() {
        return this.smplMissileLock;
    }

    public Sample getSmplMissileNoLock() {
        return this.smplMissileNoLock;
    }

    public long getStartLastMissile() {
        return this.tStartLastMissile;
    }

    public float getStepsForFullTurn() {
        return this.fStepsForFullTurn;
    }

    public long getTargetType() {
        return this.targetType;
    }

    public boolean hasMissiles() {
        return !this.missilesList.isEmpty();
    }

    private void initCommon() {
        this.selectedActorOffset = new Point3f();
        this.engageMode = this.ENGAGE_AUTO;
        this.iMissileLockState = 0;
        this.iMissileTone = 0;
        this.tLastSeenEnemy = Time.current() - 20000;
        this.oldBreakControl = true;
        this.missilesList = new ArrayList();
        this.tMissilePrev = 0L;
        this.attackDecisionByAI = false;
        this.minPkForAttack = 25.0F;
        this.millisecondsBetweenMissileLaunchAI = 10000L;
    }

    private void initLockTones() {
        if (this.lockTonesInitialized) return;
        if (this.myMissileClass == null) return;
        if (this.missileOwner == World.getPlayerAircraft()) {
            this.setFxMissileToneLock(Property.stringValue(this.myMissileClass, "fxLock", null), Property.floatValue(this.myMissileClass, "fxLockVolume", 1.0F));
            this.setFxMissileToneNoLock(Property.stringValue(this.myMissileClass, "fxNoLock", null), Property.floatValue(this.myMissileClass, "fxNoLockVolume", 1.0F));
        }
        this.lockTonesInitialized = true;
    }

    private void initParams(Actor theOwner) {
        if (theOwner instanceof Aircraft) this.missileOwner = theOwner;
        this.initCommon();
    }

    private void initParams(Actor theOwner, float theMissileMaxSpeedMeterPerSecond, float theLeadPercent, float theMaxG, float theStepsForFullTurn, float thePkMaxAngle, float thePkMaxAngleAft, float thePkMinDist, float thePkOptDist, float thePkMaxDist,
            float thePkMaxG, float theMaxPOVfrom, float theMaxPOVto, float theMaxDistance, float theMinPkForAttack, long theMillisecondsBetweenMissileLaunchAI, long theTargetType, boolean theAttackDecisionByAI, boolean theMultiTrackingCapable,
            boolean theCanTrackSubs, String theLockFx, String theNoLockFx, String theLockSmpl, String theNoLockSmpl) {
        this.initCommon();
        this.missileOwner = theOwner;
        this.fMissileMaxSpeedKmh = theMissileMaxSpeedMeterPerSecond;
        this.fLeadPercent = theLeadPercent;
        this.fMaxG = theMaxG;
        this.fStepsForFullTurn = theStepsForFullTurn;
        this.fPkMaxAngle = thePkMaxAngle;
        this.fPkMaxAngleAft = thePkMaxAngleAft;
        this.fPkMinDist = thePkMinDist;
        this.fPkOptDist = thePkOptDist;
        this.fPkMaxDist = thePkMaxDist;
        this.fPkMaxG = thePkMaxG;
        this.fMaxPOVfrom = theMaxPOVfrom;
        this.fMaxPOVto = theMaxPOVto;
        this.fMaxDistance = theMaxDistance;
        this.attackDecisionByAI = theAttackDecisionByAI;
        this.minPkForAttack = theMinPkForAttack;
        this.millisecondsBetweenMissileLaunchAI = theMillisecondsBetweenMissileLaunchAI;
        this.targetType = theTargetType;
        this.canTrackSubs = theCanTrackSubs;
        this.multiTrackingCapable = theMultiTrackingCapable;
        if (theLockFx == null) this.fxMissileToneLock = null;
        else this.fxMissileToneLock = this.missileOwner.newSound(theLockFx, false);
        if (theNoLockFx == null) this.fxMissileToneNoLock = null;
        else this.fxMissileToneNoLock = this.missileOwner.newSound(theNoLockFx, false);
    }

    public void lookForGuidedMissileTarget(Actor actor, float maxFOVfrom, float maxFOVto, double maxDistance) {
        this.lookForGuidedMissileTargetAircraft(actor, maxFOVfrom, maxFOVto, maxDistance);
    }

    public void lookForGuidedMissileTarget(Actor actor, float maxFOVfrom, float maxFOVto, double maxDistance, long targetType) {
        if ((targetType & Missile.TARGET_AIR) != 0) this.lookForGuidedMissileTargetAircraft(actor, maxFOVfrom, maxFOVto, maxDistance);
        else if ((targetType & Missile.TARGET_GROUND) != 0) this.lookForGuidedMissileTargetGround(actor, maxFOVfrom, maxFOVto, maxDistance);
        else if ((targetType & Missile.TARGET_SHIP) != 0) this.lookForGuidedMissileTargetShip(actor, maxFOVfrom, maxFOVto, maxDistance);
        if (this.getMissileTarget() == null) return;
        if (!(actor instanceof Aircraft)) return;
        Aircraft aircraft = (Aircraft) actor;
        if (aircraft.FM instanceof RealFlightModel && ((RealFlightModel) aircraft.FM).isRealMode() || !(aircraft.FM instanceof Pilot)) return;
        if (actor.getArmy() == this.getMissileTarget().getArmy()) this.setMissileTarget(null);
    }

    public void lookForGuidedMissileTargetAircraft(Actor actor, float maxFOVfrom, float maxFOVto, double maxDistance) {
        if (this.iDetectorMode == Missile.DETECTOR_TYPE_MANUAL) {
            this.setMissileTarget(null);
            this.trgtInSight = null;
            return;
        }
        double targetDistance = 0.0D;
        float targetAngle = 0.0F;
        float targetAngleAft = 0.0F;
        float targetBait = 0.0F;
        float maxTargetBait = 0.0F;
        Actor selectedActor = null;
        Actor visibleActor = null;
        float visibleFactor = 2F;
        Point3f theSelectedActorOffset = new Point3f(0.0F, 0.0F, 0.0F);

        try {
            List list = Engine.targets();
            int k = list.size();
            for (int i1 = 0; i1 < k; i1++) {
                Actor theTarget = (Actor) list.get(i1);
                if (theTarget instanceof Aircraft || theTarget instanceof MissileInterceptable) {
                    targetDistance = distanceBetween(actor, theTarget);
                    targetAngle = angleBetween(actor, theTarget);
                    targetAngleAft = 180.0F - angleBetween(theTarget, actor);

                    if (targetDistance > maxDistance * visibleFactor) continue;
                    if (targetAngle > maxFOVfrom * visibleFactor) continue;

                    switch (this.iDetectorMode) {
                        case Missile.DETECTOR_TYPE_INFRARED: {
                            float maxEngineForce = 0.0F;
                            int maxEngineForceEngineNo = 0;
                            EnginesInterface theEI = null;
                            if (theTarget instanceof Aircraft) {
                                theEI = ((FlightModelMain) ((SndAircraft) theTarget).FM).EI;
                                int iNumEngines = theEI.getNum();
                                for (int i = 0; i < iNumEngines; i++) {
                                    Motor theMotor = theEI.engines[i];
                                    float theEngineForce = theMotor.getEngineForce().length();
                                    if (engineHeatSpreadType(theMotor) == HEAT_SPREAD_NONE) theEngineForce = 0F;
                                    if (engineHeatSpreadType(theMotor) == HEAT_SPREAD_360) theEngineForce /= 10F;
                                    if (theEngineForce > maxEngineForce) {
                                        maxEngineForce = theEngineForce;
                                        maxEngineForceEngineNo = i;
                                    }
                                }
                            } else if (theTarget instanceof MissileInterceptable) maxEngineForce = Property.floatValue(theTarget.getClass(), "force", 1000F);
                            targetBait = maxEngineForce / targetAngle / (float) (targetDistance * targetDistance);
                            if (!theTarget.isAlive()) targetBait /= 10F;
                            if (targetBait > maxTargetBait) {
                                maxTargetBait = targetBait;
                                visibleActor = theTarget;
                                if (targetAngleAft > maxFOVto && (engineHeatSpreadType(theTarget) & HEAT_SPREAD_360) == 0) continue;
                                if (targetDistance > maxDistance) continue;
                                if (targetAngle > maxFOVfrom) continue;
                                selectedActor = theTarget;
                                if (theTarget instanceof Aircraft) theSelectedActorOffset = theEI.engines[maxEngineForceEngineNo].getEnginePos();
                            }
                            break;
                        }
                        case Missile.DETECTOR_TYPE_RADAR_BEAMRIDING:
                        case Missile.DETECTOR_TYPE_RADAR_HOMING:
                        case Missile.DETECTOR_TYPE_RADAR_TRACK_VIA_MISSILE: {
                            float fACMass = 0.0F;
                            if (theTarget instanceof Aircraft) {
                                Mass theM = ((FlightModelMain) ((SndAircraft) theTarget).FM).M;
                                fACMass = theM.getFullMass();
                            } else if (theTarget instanceof MissileInterceptable) fACMass = Property.floatValue(theTarget.getClass(), "massa", 1000F);
                            targetBait = fACMass / targetAngle / (float) (targetDistance * targetDistance);
                            if (!theTarget.isAlive()) targetBait /= 10F;

                            if (targetBait > maxTargetBait) {
                                maxTargetBait = targetBait;
                                visibleActor = theTarget;
                                if (targetDistance > maxDistance) continue;
                                if (targetAngle > maxFOVfrom) continue;
                                selectedActor = theTarget;
                                if (theTarget instanceof Aircraft) theSelectedActorOffset.set(0, 0, 0);
                            }
                            break;
                        }
                        default:
                            break;
                    }

                }
            }
        } catch (Exception e) {
            EventLog.type("Exception in selectedActor");
            EventLog.type(e.toString());
            EventLog.type(e.getMessage());
        }

        if (actor instanceof Aircraft && selectedActor != null) {
            Aircraft ownerAircraft = (Aircraft) actor;
            if (!ownerAircraft.FM.isPlayers() || !(ownerAircraft.FM instanceof RealFlightModel) || !((RealFlightModel) ownerAircraft.FM).isRealMode()) {
                GuidedMissileUtils.checkAllActiveMissilesValidity();
                for (int activeMissileIndex = 0; activeMissileIndex < GuidedMissileUtils.getActiveMissilesSize(); activeMissileIndex++) {
                    ActiveMissile theActiveMissile = GuidedMissileUtils.getActiveMissile(activeMissileIndex);
                    if (theActiveMissile.isAI()) if (ownerAircraft.FM.actor.getArmy() == theActiveMissile.getOwnerArmy()) if (theActiveMissile.getVictim() == selectedActor) {
                        selectedActor = null;
                        break;
                    }
                }
            }
        }

        this.selectedActorOffset.set(theSelectedActorOffset);
        this.setMissileTarget(selectedActor);
        this.trgtInSight = visibleActor;
    }

    public void lookForGuidedMissileTargetGround(Actor actor, float maxFOVfrom, float maxFOVto, double maxDistance) {
        if (this.iDetectorMode == Missile.DETECTOR_TYPE_MANUAL) {
            this.setMissileTarget(null);
            this.trgtInSight = null;
            return;
        }
        double targetDistance = 0.0D;
        float targetAngle = 0.0F;
        float targetBait = 0.0F;
        float maxTargetBait = 0.0F;
        Actor selectedActor = null;
        Point3f theSelectedActorOffset = new Point3f(0.0F, 0.0F, 0.0F);

        try {
            List list = Engine.targets();
            int k = list.size();
            for (int i1 = 0; i1 < k; i1++) {
                Actor theTarget1 = (Actor) list.get(i1);
                if (theTarget1 instanceof TgtFlak || theTarget1 instanceof TgtTank || theTarget1 instanceof TgtTrain || theTarget1 instanceof TgtVehicle) {
                    targetDistance = distanceBetween(actor, theTarget1);
                    if (targetDistance > maxDistance) continue;
                    targetAngle = angleBetween(actor, theTarget1);
                    if (targetAngle > maxFOVfrom) continue;

                    targetBait = 1 / targetAngle / (float) (targetDistance * targetDistance);
                    if (!theTarget1.isAlive()) targetBait /= 10.0F;
                    GuidedMissileUtils.checkAllActiveMissilesValidity();
                    for (int activeMissileIndex = 0; activeMissileIndex < GuidedMissileUtils.getActiveMissilesSize(); activeMissileIndex++) {
                        ActiveMissile theActiveMissile = GuidedMissileUtils.getActiveMissile(activeMissileIndex);
                        if (actorIsAI(actor)) if (actor.getArmy() == theActiveMissile.getOwnerArmy()) if (theActiveMissile.getVictim() == theTarget1) {
                            targetBait = 0.0F;
                            break;
                        }
                        if (this.iDetectorMode != Missile.DETECTOR_TYPE_INFRARED) if (!this.multiTrackingCapable) if (theActiveMissile.getOwner() == actor) if (theActiveMissile.getVictim() != null) if (actorIsAI(actor)) return;
                        else {
                            this.setMissileTarget(theActiveMissile.getVictim());
                            return;
                        }
                    }

                    if (targetBait <= maxTargetBait) continue;
                    maxTargetBait = targetBait;
                    selectedActor = theTarget1;
                    theSelectedActorOffset.set(0.0F, 0.0F, 0.0F);

                }

            }

        } catch (Exception e) {
            EventLog.type("Exception in selectedActor");
            EventLog.type(e.toString());
            EventLog.type(e.getMessage());
        }
        this.selectedActorOffset.set(theSelectedActorOffset);
        this.setMissileTarget(selectedActor);
    }

    public void lookForGuidedMissileTargetShip(Actor actor, float maxFOVfrom, float maxFOVto, double maxDistance) {
        double targetDistance = 0.0D;
        float targetAngle = 0.0F;
        float targetBait = 0.0F;
        float maxTargetBait = 0.0F;
        Actor selectedActor = null;
        this.selectedActorOffset.set(new Point3f(0.0F, 0.0F, 0.0F));

        if (!(actor instanceof Aircraft) || this.iDetectorMode == Missile.DETECTOR_TYPE_MANUAL) {
            this.setMissileTarget(null);
            this.trgtInSight = null;
            return;
        }

        try {
            List list = Engine.targets();
            int k = list.size();
            for (int i1 = 0; i1 < k; i1++) {
                Actor theTarget1 = (Actor) list.get(i1);
                if (theTarget1 instanceof TgtShip) {
                    targetDistance = distanceBetween(actor, theTarget1);
                    if (targetDistance > maxDistance) continue;
                    targetAngle = angleBetween(actor, theTarget1);
                    if (targetAngle > maxFOVfrom) continue;
                    if (theTarget1.pos.getAbsPoint().z < -3D) if (!this.canTrackSubs) continue;

                    targetBait = 1 / targetAngle / (float) (targetDistance * targetDistance);
                    if (!theTarget1.isAlive()) targetBait /= 10.0F;
                    GuidedMissileUtils.checkAllActiveMissilesValidity();
                    for (int activeMissileIndex = 0; activeMissileIndex < GuidedMissileUtils.getActiveMissilesSize(); activeMissileIndex++) {
                        ActiveMissile theActiveMissile = GuidedMissileUtils.getActiveMissile(activeMissileIndex);
                        if (actorIsAI(actor)) if (actor.getArmy() == theActiveMissile.getOwnerArmy()) if (theActiveMissile.getVictim() == theTarget1) {
//									System.out.println("blanked target " + theTarget1.getClass().getName() + " (already tracked)");
                            targetBait = 0.0F;
                            continue;
                        }
                        if (this.iDetectorMode != Missile.DETECTOR_TYPE_INFRARED) if (!this.multiTrackingCapable) if (theActiveMissile.getOwner() == actor) if (theActiveMissile.getVictim() != null) if (actorIsAI(actor)) {
                            this.setMissileTarget(null);
                            this.trgtInSight = null;
                            return;
                        } else {
                            if (theActiveMissile.getVictim().pos.getAbsPoint().z < 5D) if (!this.canTrackSubs) // HUD.log("Offset added");
                                this.selectedActorOffset.set(new Point3f(0.0F, 0.0F, 5.0F));
                            this.setMissileTarget(theActiveMissile.getVictim());
                            this.trgtInSight = null;
                            return;
                        }
                    }

                    if (targetBait <= maxTargetBait) continue;
                    maxTargetBait = targetBait;
                    selectedActor = theTarget1;
                }

            }

        } catch (Exception e) {
            EventLog.type("Exception in selectedActor");
            EventLog.type(e.toString());
            EventLog.type(e.getMessage());
        }
        if (selectedActor != null) if (selectedActor.pos.getAbsPoint().z < 5D) if (!this.canTrackSubs) this.selectedActorOffset.set(new Point3f(0.0F, 0.0F, 5.0F));
//		System.out.println("lookForGuidedMissileTargetShip " + (selectedActor == null?"null":selectedActor.getClass().getName()));
        this.setMissileTarget(selectedActor);
        this.trgtInSight = null;
        // return selectedActor;
    }

    public void onAircraftLoaded() {
        this.missilesList.clear();
        this.createMissileList(this.missilesList);
        this.setGunNullOwner();
    }

    private void generateMissileDataForPk() {
        if (!(this.missileOwner instanceof Aircraft)) {
            this.missileDataForPk = null;
            return;
        }
        if (this.missileDataForPk == null) this.missileDataForPk = new ArrayList();
        else this.missileDataForPk.clear();

        Aircraft ownerAircraft = (Aircraft) this.missileOwner;
        BulletEmitter ownerAircraftEmitters[][] = ownerAircraft.FM.CT.Weapons;
        for (int weaponTrigger = 0; weaponTrigger < ownerAircraftEmitters.length; weaponTrigger++) {
            if (weaponTrigger < 2) continue;
            if (weaponTrigger > 7) continue;
            if (ownerAircraftEmitters[weaponTrigger] == null) continue;
            if (ownerAircraft.FM.isPlayers() && ownerAircraft.FM instanceof RealFlightModel && ((RealFlightModel) ownerAircraft.FM).isRealMode()) if (weaponTrigger != ownerAircraft.FM.CT.rocketHookSelected) continue;

            for (int weaponIndex = 0; weaponIndex < ownerAircraftEmitters[weaponTrigger].length; weaponIndex++) {
                if (ownerAircraftEmitters[weaponTrigger][weaponIndex] == null) continue;
                if (!ownerAircraftEmitters[weaponTrigger][weaponIndex].haveBullets()) continue;
                if (!(ownerAircraftEmitters[weaponTrigger][weaponIndex] instanceof MissileGun)) continue;
                MissileGun theMissileGun = (MissileGun) ownerAircraftEmitters[weaponTrigger][weaponIndex];
                Class theMissileClass = theMissileGun.bulletClass();
                MissileDataForPk theMissileDataForPk = new MissileDataForPk();
                theMissileDataForPk.setTriggerNum(weaponTrigger);
                theMissileDataForPk.setMaxLaunchLoad(Property.floatValue(theMissileClass, "maxLockGForce", 99.9F));
                theMissileDataForPk.setMaxAngleToTarget(Property.floatValue(theMissileClass, "PkMaxFOVfrom", 99.9F));
                theMissileDataForPk.setMaxAngleFromTargetAft(Property.floatValue(theMissileClass, "PkMaxFOVto", 99.9F));
                theMissileDataForPk.setMinDist(Property.floatValue(theMissileClass, "PkDistMin", 99.9F));
                theMissileDataForPk.setOptDist(Property.floatValue(theMissileClass, "PkDistOpt", 99.9F));
                theMissileDataForPk.setMaxDist(Property.floatValue(theMissileClass, "PkDistMax", 99.9F));
                this.missileDataForPk.add(theMissileDataForPk);
                break;
            }
        }
    }

    public float irDetectorScan() {
        if (this.missileOwner != World.getPlayerAircraft()) return 0F;
        if (!(this.missileOwner instanceof Aircraft)) return 0F;

        float toleranceFactor = 2F;
        float maxAngleToTarget = this.getMaxPOVto() * toleranceFactor;
        float maxAngleFromTargetAft = this.getMaxPOVfrom() * toleranceFactor;
        float maxDist = this.getPkMaxDist() * toleranceFactor;

        // Actor target = this.lookForGuidedMissileTargetAircraft(this.missileOwner, 360F, maxAngleToTarget, maxDist);

        if (!Actor.isValid(this.trgtInSight)) return 0F;
        float angleFromTarget = angleBetween(this.missileOwner, this.trgtInSight);
        float angleToTarget = 180.0F - angleBetween(this.trgtInSight, this.missileOwner);
        float distanceToTarget = (float) distanceBetween(this.missileOwner, this.trgtInSight);

        if (distanceToTarget > maxDist || angleToTarget > maxAngleToTarget || angleFromTarget > maxAngleFromTargetAft) return 0F;

        float fDetectorScan = 1.0F;
        float fTemp = distanceToTarget / maxDist;
        fDetectorScan *= Math.exp(-1F * fTemp * fTemp);

        fTemp = angleToTarget / maxAngleToTarget;
        fDetectorScan *= Math.exp(-1F * fTemp * fTemp);

        if (engineHeatSpreadType(this.trgtInSight) == HEAT_SPREAD_AFT) {
            fTemp = angleFromTarget / maxAngleFromTargetAft;
            fDetectorScan *= Math.exp(-1F * fTemp * fTemp);
        }

        fDetectorScan *= enginePowerLevel(this.trgtInSight);

        if (this.fSunRayAngle > 0F) {
            float sunAngle = GuidedMissileUtils.angleBetween(this.missileOwner, World.Sun().ToSun);
            if (sunAngle < this.fSunRayAngle) {
                fTemp = angleFromTarget / maxAngleFromTargetAft;
                float fDetectorSun = (float) Math.exp(-1F * fTemp * fTemp);
                if (fDetectorSun > fDetectorScan) fDetectorScan = fDetectorSun;
            }
        }

        return fDetectorScan;
    }

    public float Pk(Actor actorFrom, Actor actorTo) {
        float fPkRet = 0.0F;
        if (!(this.missileOwner instanceof Aircraft)) return fPkRet;
        this.generateMissileDataForPk();
        float angleToTarget = angleBetween(actorFrom, actorTo);
        float angleFromTarget = 180.0F - angleBetween(actorTo, actorFrom);
        float distanceToTarget = (float) distanceBetween(actorFrom, actorTo);
        float gForce = ((Aircraft) actorFrom).FM.getOverload();
        float launchLoadFactor = 1.0F;
        if (actorFrom instanceof Aircraft) if (!(((Aircraft) actorFrom).FM instanceof RealFlightModel) || !((RealFlightModel) ((Aircraft) actorFrom).FM).isRealMode()) launchLoadFactor = 1.5F; // enhance Max. Launch load for AI.

        Aircraft ownerAircraft = (Aircraft) this.missileOwner;
        int bestMissileTrigger = ownerAircraft.FM.CT.rocketHookSelected;

        for (int missileIndex = 0; missileIndex < this.missileDataForPk.size(); missileIndex++) {
            float fPkRetTemp = 100.0F;
            float fTemp = 0.0F;
            MissileDataForPk theMissileDataForPk = (MissileDataForPk) this.missileDataForPk.get(missileIndex);
            if (distanceToTarget > theMissileDataForPk.getMaxDist() || distanceToTarget < theMissileDataForPk.getMinDist() || angleToTarget > theMissileDataForPk.getMaxAngleToTarget() || angleFromTarget > theMissileDataForPk.getMaxAngleFromTargetAft()
                    || gForce > theMissileDataForPk.getMaxLaunchLoad() * launchLoadFactor)
                break;
            if (distanceToTarget > theMissileDataForPk.getOptDist()) {
                fTemp = distanceToTarget - theMissileDataForPk.getOptDist();
                fTemp /= theMissileDataForPk.getMaxDist() - theMissileDataForPk.getOptDist();
                fPkRetTemp -= fTemp * fTemp * 20;
            } else {
                fTemp = theMissileDataForPk.getOptDist() - distanceToTarget;
                fTemp /= theMissileDataForPk.getOptDist() - theMissileDataForPk.getMinDist();
                fPkRetTemp -= fTemp * fTemp * 60;
            }
            fTemp = angleToTarget / theMissileDataForPk.getMaxAngleToTarget();
            fPkRetTemp -= fTemp * fTemp * 30;
            fTemp = angleFromTarget / theMissileDataForPk.getMaxAngleFromTargetAft();
            fPkRetTemp -= fTemp * fTemp * 50;
            fTemp = gForce / theMissileDataForPk.getMaxLaunchLoad() / launchLoadFactor;
            fPkRetTemp -= fTemp * fTemp * 30;
            if (fPkRetTemp < 0.0F) fPkRetTemp = 0.0F;
            if (fPkRetTemp > fPkRet) {
                bestMissileTrigger = theMissileDataForPk.getTriggerNum();
                fPkRet = fPkRetTemp;
            }
        }

        if (bestMissileTrigger != ownerAircraft.FM.CT.rocketHookSelected) ownerAircraft.FM.CT.doSetRocketHook(bestMissileTrigger);

        return fPkRet;
    }

    public void playMissileGrowlLock(boolean isLocked) {
        if (this.bSingleTone) {
            if (this.fxMissileToneLock != null && !this.fxMissileToneLock.isPlaying()) this.fxMissileToneLock.play();
            return;
        }
        if (isLocked) {
            if (this.fxMissileToneNoLock != null) this.fxMissileToneNoLock.cancel();
            if (this.fxMissileToneLock != null && !this.fxMissileToneLock.isPlaying()) {
                this.fxMissileToneLock.play();
                this.fxMissileToneLock.setVolume(this.fxLockVolume);
            }
        } else {
            if (this.fxMissileToneLock != null) this.fxMissileToneLock.cancel();
            if (this.fxMissileToneNoLock != null && !this.fxMissileToneNoLock.isPlaying()) {
                this.fxMissileToneNoLock.play();
                this.fxMissileToneLock.setVolume(this.fxNoLockVolume);
            }
        }
    }

    public void setAttackDecisionByAI(boolean theAttackDecisionByAI) {
        this.attackDecisionByAI = theAttackDecisionByAI;
    }

    public void setCanTrackSubs(boolean theCanTrackSubs) {
        this.canTrackSubs = theCanTrackSubs;
    }

    public void setFxMissileToneLock(String value) {
        if (this.missileOwner != World.getPlayerAircraft()) return;
        if (value == null) {
            this.fxMissileToneLock = null;
            return;
        }
        this.fxMissileToneLock = this.missileOwner.newSound(value, false);
    }

    public void setFxMissileToneLock(String fxName, float theVolume) {
        if (this.missileOwner != World.getPlayerAircraft()) return;
        this.setFxMissileToneLock(fxName);
        this.setFxMissileToneLockVolume(theVolume);
    }

    public void setFxMissileToneLockVolume(float value) {
        if (this.missileOwner != World.getPlayerAircraft()) return;
        if (this.fxMissileToneLock == null) return;
        this.fxMissileToneLock.setVolume(value);
    }

    // TODO: -- Added/changed Code Multiple Missile Type Selection --

    public void setFxMissileToneNoLock(String value) {
        if (this.missileOwner != World.getPlayerAircraft()) return;
        if (value == null) {
            this.fxMissileToneNoLock = null;
            return;
        }
        this.fxMissileToneNoLock = this.missileOwner.newSound(value, false);
    }

    public void setFxMissileToneNoLock(String fxName, float theVolume) {
        if (this.missileOwner != World.getPlayerAircraft()) return;
        this.setFxMissileToneNoLock(fxName);
        this.setFxMissileToneNoLockVolume(theVolume);
    }

    public void setFxMissileToneNoLockVolume(float value) {
        if (this.missileOwner != World.getPlayerAircraft()) return;
        if (this.fxMissileToneNoLock == null) return;
        this.fxMissileToneNoLock.setVolume(value);
    }

    public void setGunNullOwner() {
        if (!(this.missileOwner instanceof Aircraft)) return;
        Aircraft ownerAircraft = (Aircraft) this.missileOwner;
        try {
            for (int l = 0; l < ownerAircraft.FM.CT.Weapons.length; l++)
                if (ownerAircraft.FM.CT.Weapons[l] != null) for (int l1 = 0; l1 < ownerAircraft.FM.CT.Weapons[l].length; l1++) {
                    if (ownerAircraft.FM.CT.Weapons[l][l1] == null || !(ownerAircraft.FM.CT.Weapons[l][l1] instanceof GunNull)) continue;
                    Actor gunNullOwner = ((GunNull) ownerAircraft.FM.CT.Weapons[l][l1]).getOwner();
//						System.out.println("setGunNullOwner owner=" + (gunNullOwner == ownerAircraft?"ownerAircraft":""+gunNullOwner));
                    if (gunNullOwner != ownerAircraft) ((GunNull) ownerAircraft.FM.CT.Weapons[l][l1]).setOwner(ownerAircraft);
                }
        } catch (Exception localException) {}
    }

    public void setLeadPercent(float value) {
        this.fLeadPercent = value;
    }

    public void setLockTone(String theLockPrs, String theNoLockPrs, String theLockWav, String theNoLockWav) {
        if (this.missileOwner != World.getPlayerAircraft()) return;
        this.setFxMissileToneLock(theLockPrs);
        this.setFxMissileToneNoLock(theNoLockPrs);
    }

    public void setMaxDistance(float value) {
        this.fMaxDistance = value;
    }

    public void setMaxG(float value) {
        this.fMaxG = value;
    }

    public void setMaxPOVfrom(float value) {
        this.fMaxPOVfrom = value;
    }

    public void setMaxPOVto(float value) {
        this.fMaxPOVto = value;
    }

    public void setMillisecondsBetweenMissileLaunchAI(long theMillisecondsBetweenMissileLaunchAI) {
        this.millisecondsBetweenMissileLaunchAI = theMillisecondsBetweenMissileLaunchAI;
    }

    public void setMinPkForAttack(float theMinPkForAttack) {
        this.minPkForAttack = theMinPkForAttack;
    }

    public void setMissileGrowl(int growl) {
        this.iMissileTone = growl;
    }

    public void setMissileMaxSpeedKmh(float value) {
        this.fMissileMaxSpeedKmh = value;
    }

    public void setMissileName(String theMissileName) {
        this.missileName = theMissileName;
    }

    public void setMissileOwner(Actor value) {
        this.missileOwner = value;
        this.initLockTones();
    }

    public void setMissileTarget(Actor theTarget) {
        this.trgtMissile = theTarget;
//		System.out.println("setMissileTarget this.trgtMissile=" + this.trgtMissile);
    }

    public void setMultiTrackingCapable(boolean theMultiTrackingCapable) {
        this.multiTrackingCapable = theMultiTrackingCapable;
    }

    public void setPkMaxAngle(float value) {
        this.fPkMaxAngle = value;
    }

    public void setPkMaxAngleAft(float value) {
        this.fPkMaxAngleAft = value;
    }

    public void setPkMaxDist(float value) {
        this.fPkMaxDist = value;
    }

    public void setPkMaxG(float value) {
        this.fPkMaxG = value;
    }

    public void setPkMinDist(float value) {
        this.fPkMinDist = value;
    }

    public void setPkOptDist(float value) {
        this.fPkOptDist = value;
    }

    public void setStartLastMissile(long theStartTime) {
        this.tStartLastMissile = theStartTime;
        if (this.missileOwner != null && this.missileOwner.isNetMirror()) this.tStartLastMissile = 0L;
    }

    public void setStepsForFullTurn(float value) {
        this.fStepsForFullTurn = value;
    }

    public void setTargetType(long theTargetType) {
        this.targetType = theTargetType;
    }

    public void shootNextMissile() {
        this.shootNextMissile(false);
    }

    public void shootNextMissile(boolean isValidForNetMirrors) {
//		System.out.println("shootRocket 1");
        if (this.missilesList.isEmpty()) return;
//		System.out.println("shootRocket 2");
        int missileToShoot = 0;
        MissileGun theMissileGun = null;
        for (; missileToShoot < this.missilesList.size(); missileToShoot++) {
            theMissileGun = (MissileGun) this.missilesList.get(missileToShoot);
            if (!theMissileGun.isShots()/* && !theMissileGun.isEngineWarmupRunning() */) break;
        }

        if (missileToShoot >= this.missilesList.size()) {
            System.out.println("### ERROR in shootNextMissile(" + isValidForNetMirrors + "): No available MissileGun found!");
            return;
        }

        theMissileGun.shots(1, isValidForNetMirrors);

//		((MissileGun) this.missilesList.get(0)).shots(1, isValidForNetMirrors);
    }

    public void shotMissile(MissileGun theMissileGun) {
//		System.out.println("shotMissile 1 " + theMissileGun.hashCode());
        if (!(this.missileOwner instanceof Aircraft)) return;
//		System.out.println("shotMissile 2");
        Aircraft ownerAircraft = (Aircraft) this.missileOwner;
        if (this.hasMissiles()) {
//			if (NetMissionTrack.isPlaying() || Mission.isNet()) {
//				if ((!(ownerAircraft.FM instanceof RealFlightModel)) || (!((RealFlightModel) ownerAircraft.FM).isRealMode())) {
////					if (!this.rocketsList.isEmpty() && this.rocketsList.get(0) != null)
////						((MissileGun) this.rocketsList.get(0)).loadBullets(0);
//				} else if (World.cur().diffCur.Limited_Ammo && this.missileOwner.isNetMirror()) {
////					if (!this.rocketsList.isEmpty() && this.rocketsList.get(0) != null)
////						((MissileGun) this.rocketsList.get(0)).loadBullets(0);
//				}
//			}

//			System.out.println("shotMissile 3");
            if (World.cur().diffCur.Limited_Ammo || ownerAircraft != World.getPlayerAircraft()) // System.out.println("shotMissile 4");
                if (!this.missilesList.isEmpty() && this.missilesList.contains(theMissileGun)) {
//					System.out.println("shotMissile 5");
                    if (theMissileGun.isEngineWarmupRunning()) return;
//					System.out.println("shotMissile 6");
                    this.missilesList.remove(theMissileGun);
                }
            if (ownerAircraft != World.getPlayerAircraft()) // System.out.println("shotMissile 7");
                Voice.speakAttackByRockets(ownerAircraft);
        }
    }

    public void update() {
        if (!this.hasMissiles()) { 
            if (this.iMissileLockState != 0) {
                this.changeMissileGrowl(0);
                LocalLog(this.missileOwner, this.missileName + " missiles depleted");
                this.iMissileLockState = 0;
            }
            return;
        }
        Aircraft ownerAircraft = (Aircraft) this.missileOwner;
        if (ownerAircraft != null) if (ownerAircraft.FM.CT.Weapons[ownerAircraft.FM.CT.rocketHookSelected] == null) ownerAircraft.FM.CT.toggleRocketHook();

//		this.setMissileTarget(this.lookForGuidedMissileTarget(this.missileOwner, this.getMaxPOVfrom(), this.getMaxPOVto(), this.getPkMaxDist(), this.targetType));
        this.lookForGuidedMissileTarget(this.missileOwner, this.getMaxPOVfrom(), this.getMaxPOVto(), this.getPkMaxDist(), this.targetType);
        this.trgtPk = this.getMissilePk(this.getMissileTarget());
        this.checkAIlaunchMissile();
        this.checkPendingMissiles();
        this.checkLockStatus();

        // FIXME: Missile scan tone modulation anyone?
        if (this.getDetectorType() == Missile.DETECTOR_TYPE_INFRARED) switch (this.iGrowlStyle) {
            case 1:
                this.updateSideWinderIrDetector();
                break;
            default:
                break;
        }

        this.checkActiveMissiles();
        this.checkGrowlState();
    }

    private void updateSideWinderIrDetector() {
        float fDetectorScan = this.irDetectorScan();

        if (this.fxMissileToneLock != null) switch (this.getMissileGrowl()) {
            case 1:
                float detectorVolume = Aircraft.cvt(fDetectorScan, 0F, 0.5F, 0.6F, 1.0F) * this.fxNoLockVolume;
                float detectorMaxPitch = Aircraft.cvt(fDetectorScan, 0.2F, 0.8F, 1.0F, 2.0F) * this.fxNoLockPitch;

                if (this.lastTime == 0L) this.curTimeIndex = 0D;
                else this.curTimeIndex += (Time.currentReal() - this.lastTime) / 50D * (TrueRandom.nextDouble() + 0.5D);
                this.lastTime = Time.currentReal();

                double timeIndex = this.curTimeIndex % (Math.PI * 2D);
                float timeSin = (float) Math.sin(timeIndex);
                float detectorPitch = timeSin > 0.9F ? Aircraft.cvt(timeSin, -1F, 1F, 1F, detectorMaxPitch) : 1F;
                this.fxMissileToneLock.setVolume(detectorVolume);
                this.fxMissileToneLock.setPitch(detectorPitch);
                // HUD.training("ti:" + df.format(timeIndex) + ", dmp:" + df.format(detectorMaxPitch) + ", dv:" + df.format(detectorVolume));
                break;
            case 2:
                this.fxMissileToneLock.setVolume(this.fxLockVolume);
                this.fxMissileToneLock.setPitch(this.fxLockPitch);
                // HUD.training("lock Pitch=" + df.format(this.fxLockPitch));
                break;
            default:
                break;
        }
    }

    private void checkActiveMissiles() {
        for (int i = 0; i < getActiveMissilesSize(); i++) {
            ActiveMissile am = (ActiveMissile) getActiveMissiles().get(i);
            if (!Actor.isValid(am.getMissile()) || !Actor.isValid(am.getOwner()) || !Actor.isValid(am.getVictim())) getActiveMissiles().remove(am);
        }
    }

    private void checkGrowlState() {
        if (this.getMissileOwner() != World.getPlayerAircraft()) return;
        switch (this.getMissileGrowl()) {
            case 1:
                this.playMissileGrowlLock(false);
                break;
            case 2:
                this.playMissileGrowlLock(true);
                break;
            default:
                this.cancelMissileGrowl();
                break;
        }

    }

    private static boolean checkActiveMissileInit() {
        if (activeMissiles == null) {
            curMission = Mission.cur();
            activeMissiles = new ArrayList();
            return true;
        }
        if (Mission.cur() != curMission) {
            curMission = Mission.cur();
            if (activeMissiles != null) activeMissiles.clear();
            activeMissiles = new ArrayList();
            return true;
        }
        return false;
    }

    public static ArrayList getActiveMissiles() {
        checkActiveMissileInit();
        return activeMissiles;
    }

    public static void setActiveMissiles(ArrayList activeMissiles) {
        GuidedMissileUtils.activeMissiles = activeMissiles;
    }

    public static void addActiveMissile(ActiveMissile theNewActiveMissile) {
        checkActiveMissileInit();
        addTargetHandledByAi(theNewActiveMissile.getOwnerArmy(), theNewActiveMissile.getVictim());
        activeMissiles.add(theNewActiveMissile);
    }

    public static boolean removeActiveMissile(ActiveMissile theActiveMissile) {
        if (checkActiveMissileInit()) return true;
        if (!activeMissiles.contains(theActiveMissile)) return false;
        addTargetHandledByAi(theActiveMissile.getOwnerArmy(), theActiveMissile.getVictim());
        return activeMissiles.remove(theActiveMissile);
    }

    public static int getActiveMissilesSize() {
        if (checkActiveMissileInit()) return 0;
        return activeMissiles.size();
    }

    public static ActiveMissile getActiveMissile(int index) {
        if (checkActiveMissileInit()) return null;
        if (index >= activeMissiles.size()) return null;
        return (ActiveMissile) activeMissiles.get(index);
    }

    public static boolean checkAllActiveMissilesValidity() {
        if (activeMissiles == null) {
            curMission = Mission.cur();
            activeMissiles = new ArrayList();
            return true;
        }
        boolean retVal = true;
        for (int index = 0; index < activeMissiles.size(); index++) {
            ActiveMissile theActiveMissile = (ActiveMissile) activeMissiles.get(index);
            if (!theActiveMissile.isValidMissile()) {
                activeMissiles.remove(index);
                retVal = false;
            }
        }
        return retVal;
    }

    public static boolean noLaunchSince(long timeMilliseconds, int army) {
        if (checkActiveMissileInit()) return true;
        checkAllActiveMissilesValidity();
        for (int index = 0; index < activeMissiles.size(); index++) {
            ActiveMissile theActiveMissile = (ActiveMissile) activeMissiles.get(index);
            if (theActiveMissile.getOwnerArmy() == army) if (Time.current() - theActiveMissile.getLaunchTime() < timeMilliseconds) return false;
        }
        return true;
    }

    public static boolean missilesLeft(BulletEmitter[] missileHook) {
        for (int index = 0; index < missileHook.length; index++)
            if (missileHook[index] != null) if (missileHook[index] instanceof MissileGun) if (missileHook[index].haveBullets()) return true;
        return false;
    }

    private static boolean checkTargetsHandledByAi() {
        if (targetsHandledByAiAlready == null) {
            targetsHandledByAiAlready = new HashMap();
            return true;
        }
        return false;
    }

    public static boolean isTargetHandledByAi(int LauncherArmy, Actor victim) {
        if (victim == null) return true;
        checkTargetsHandledByAi();
        int victimHash = victim.hashCode();
        Long hashKey = new Long(Conversion.longFromTwoInts(LauncherArmy, victimHash));
        long curTime = Time.current();
        if (targetsHandledByAiAlready.containsKey(hashKey)) {
            long hashValue = ((Long) targetsHandledByAiAlready.get(hashKey)).longValue();
            if (curTime < hashValue || curTime > hashValue + minTimeBetweenAIMissileLaunch) {
                targetsHandledByAiAlready.remove(hashKey);
                return false;
            }
            return true;
        }
        return false;
    }

    public static long addTargetHandledByAi(int LauncherArmy, Actor victim) {
        if (victim == null) return 0;
        checkTargetsHandledByAi();
        int victimHash = victim.hashCode();
        Long hashKey = new Long(Conversion.longFromTwoInts(LauncherArmy, victimHash));
        Long curTime = new Long(Time.current());
        Long retVal = (Long) targetsHandledByAiAlready.put(hashKey, curTime);
        if (retVal == null) return 0;
        return retVal.longValue();
    }

    private static int[] getSwTbl(int i) {
        if (i < 0) i = -i;
        int j = i % 16 + 11;
        int k = i % Finger.kTable.length;
        if (j < 0) j = -j % 16;
        if (j < 10) j = 10;
        if (k < 0) k = -k % Finger.kTable.length;
        int ai[] = new int[j];
        for (int l = 0; l < j; l++)
            ai[l] = Finger.kTable[(k + l) % Finger.kTable.length];

        return ai;
    }

    public static boolean parseMissilePropertiesFile(Class missileClass) {
        if (Property.intValue(missileClass, "IgnoreMissilePropertiesFile", 0) == 1) return true;
        String line = "N/A";
        String parsedLine = "N/A";
        try {
            int i = Finger.Int("sa" + missileClass.getName() + "s1");
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new KryptoInputFilter(new SFSInputStream(Finger.LongFN(0L, "missiles/" + Finger.incInt(i, "946"))), getSwTbl(i))));
            do {
                if ((line = bufferedreader.readLine()) == null) break;
                parsedLine = line.trim();
                if (parsedLine.startsWith("//") || parsedLine.startsWith(";") || parsedLine.startsWith("*") || parsedLine.startsWith("#")) continue;
                if (parsedLine.indexOf("//") != -1) parsedLine = parsedLine.substring(0, line.indexOf("//")).trim();
                StringTokenizer stringtokenizer = new StringTokenizer(parsedLine, ",");
                if (stringtokenizer.countTokens() != 3) {
                    System.out.println("GuidedMissileUtils Error in parseMissilePropertiesFile while parsing line: " + line + " (parsed: " + parsedLine + ")");
                    System.out.println("Number of tokens in this line doesn't meet expectation (is: " + stringtokenizer.countTokens() + ", expected: 3)");
                    continue;
                }
                String propertyName = stringtokenizer.nextToken();
                String propertyType = stringtokenizer.nextToken();
                if (propertyType.equalsIgnoreCase("i")) { // Integer
                    String value = stringtokenizer.nextToken();
                    if (value.equals("MAX")) Property.set(missileClass, propertyName, Integer.MAX_VALUE);
                    else if (value.equals("MIN")) Property.set(missileClass, propertyName, Integer.MIN_VALUE);
                    else Property.set(missileClass, propertyName, Integer.parseInt(value));
                } else if (propertyType.equalsIgnoreCase("l")) { // Long
                    String value = stringtokenizer.nextToken();
                    if (value.equals("MAX")) Property.set(missileClass, propertyName, Long.MAX_VALUE);
                    else if (value.equals("MIN")) Property.set(missileClass, propertyName, Long.MIN_VALUE);
                    else Property.set(missileClass, propertyName, Long.parseLong(value));
                } else if (propertyType.equalsIgnoreCase("f")) { // Float
                    String value = stringtokenizer.nextToken();
                    if (value.equals("MAX")) Property.set(missileClass, propertyName, Float.MAX_VALUE);
                    else if (value.equals("MIN")) Property.set(missileClass, propertyName, Float.MIN_VALUE);
                    else Property.set(missileClass, propertyName, Float.parseFloat(value));
                } else if (propertyType.equalsIgnoreCase("c")) { // Color
                    NumberTokenizer numbertokenizer = new NumberTokenizer(stringtokenizer.nextToken(), " ");
                    if (numbertokenizer.countTokens() != 3) {
                        System.out.println("GuidedMissileUtils Error in parseMissilePropertiesFile while parsing line: " + line + " (parsed: " + parsedLine + ")");
                        System.out.println("Number of numeric tokens in this line doesn't meet expectation (is: " + numbertokenizer.countTokens() + ", expected: 3)");
                        continue;
                    }
                    Property.set(missileClass, propertyName, new Color3f(numbertokenizer.nextFloat(), numbertokenizer.nextFloat(), numbertokenizer.nextFloat()));
                } else if (propertyType.equalsIgnoreCase("s")) { // String
                    String value = stringtokenizer.nextToken();
                    if (value.equals("null")) value = null;
                    Property.set(missileClass, propertyName, value);
                } else { // unknown
                    System.out.println("GuidedMissileUtils Error in parseMissilePropertiesFile while parsing line: " + line + " (parsed: " + parsedLine + ")");
                    System.out.println("Property \"" + propertyType + "\" doesn't meet expectations (valid property identifiers are i,l,f,c and s)");
                    continue;
                }
            } while (true);
            bufferedreader.close();
        } catch (FileNotFoundException filenotfoundexception) {
            filenotfoundexception.printStackTrace();
        } catch (Exception exception) {
            System.out.println("GuidedMissileUtils Exception in parseMissilePropertiesFile while parsing line: " + line + " (parsed: " + parsedLine + ")");
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    private static Mission    curMission                         = null;
    private static ArrayList  activeMissiles                     = null;
    private static HashMap    targetsHandledByAiAlready          = null;

    public static int         HEAT_SPREAD_360                    = 2; // Engine emits heat in all directions, i.e. Piston engines
    public static int         HEAT_SPREAD_AFT                    = 1; // Engine emits heat aft of A/C, i.e. Jet/Rocket engines
    public static int         HEAT_SPREAD_NONE                   = 0; // Engine produces no heat, i.e. Tow Gliders

    private boolean           attackDecisionByAI                 = false;
    private boolean           canTrackSubs                       = false;
    protected double          d                                  = 0.0D;
    protected float           deltaAzimuth                       = 0.0F;
    protected float           deltaTangage                       = 0.0F;
    private final int         ENGAGE_AUTO                        = 0;
    private final int         ENGAGE_OFF                         = -1;
    private final int         ENGAGE_ON                          = 1;
    private int               engageMode                         = 0;
    protected Eff3DActor      fl1                                = null;
    protected Eff3DActor      fl2                                = null;
    private float             fLeadPercent                       = 0.0F; // 0 means tail chasing, 100 means full lead tracking
    protected FlightModel     fm                                 = null;
    private float             fMaxDistance                       = 4500.0F; // maximum Distance for lockon
    private float             fMaxG                              = 12F; // maximum G-Force during flight
    private float             fMaxPOVfrom                        = 25.0F; // maximum Angle (from launching A/C POV) for lockon
    private float             fMaxPOVto                          = 60.0F; // maximum Angle (from target back POV) for lockon
    protected float           fMissileBaseSpeedKmh               = 0.0F;
    protected float           fMissileMaxSpeedKmh                = 2000.0F;
    private float             fPkMaxAngle                        = 30.0F; // maximum Angle (from launching A/C POV) for Pk calculation
    private float             fPkMaxAngleAft                     = 70.0F; // maximum Angle (from target back POV) for Pk calculation
    private float             fPkMaxDist                         = 4500.0F; // maximum Distance for Pk calculation
    private float             fPkMaxG                            = 2.0F; // maximum G-Force for Pk calculation
    private float             fPkMinDist                         = 400.0F; // minimum Distance for Pk calculation
    private float             fPkOptDist                         = 1500.0F; // optimum Distance for Pk calculation
    private float             fStepsForFullTurn                  = 10F; // update steps for maximum control surface output, higher value means slower reaction and smoother flight
    private float             fSunRayAngle                       = 0F; // max. Angle at which the missile will track Sun Ray, zero disables Sun Ray tracking (only valid for IR detector missiles)
    private boolean           bSingleTone                        = false;
    private float             fxLockPitch                        = 1F;
    private float             fxNoLockPitch                      = 1F;
    private float             fxLockVolume                       = 1F;
    private float             fxNoLockVolume                     = 1F;
    private int               iGrowlStyle                        = 0;
    private SoundFX           fxMissileToneLock                  = null;
    private SoundFX           fxMissileToneNoLock                = null;
    private int               iDetectorMode                      = 0;
    private int               iMissileLockState                  = 0;
    private int               iMissileTone                       = 0;
    protected double          launchKren                         = 0.0D;
    protected double          launchPitch                        = 0.0D;
    protected double          launchYaw                          = 0.0D;
    private boolean           lockTonesInitialized               = false;
    private long              millisecondsBetweenMissileLaunchAI = 10000L;
    private float             minPkForAttack                     = 25.0F;
    private String            missileName                        = null;
    private Actor             missileOwner                       = null;
    private boolean           multiTrackingCapable               = true;
    private Class             myMissileClass                     = null;
    private boolean           oldBreakControl                    = false;
    protected float           oldDeltaAzimuth                    = 0.0F;
    protected float           oldDeltaTangage                    = 0.0F;
    protected Orient          or                                 = new Orient();
    protected Orient          orVictimOffset                     = null;
    protected Point3d         p                                  = new Point3d();
    protected float           prevd                              = 0.0F;
    protected Point3d         pT                                 = null;
    protected Point3f         pVictimOffset                      = null;
    public int                rocketSelected                     = 2;
    private ArrayList         missilesList                       = null;
    private Point3f           selectedActorOffset                = null;
    private Sample            smplMissileLock                    = null;
    private Sample            smplMissileNoLock                  = null;
    private long              targetType                         = Missile.TARGET_AIR;
    private long              tLastSeenEnemy                     = 0L;
    private long              tMissilePrev                       = 0L;

    private Actor             trgtAI                             = null;
    private Actor             trgtMissile                        = null;
    private Actor             trgtInSight                        = null;

    private float             trgtPk                             = 0.0F;

    protected long            tStartLastMissile                  = 0L;

    protected Vector3d        v                                  = null;

    protected Actor           victim                             = null;

    protected Vector3d        victimSpeed                        = null;

    private ArrayList         missileDataForPk                   = null;

    private static final long minTimeBetweenAIMissileLaunch      = 1000L;

    private long              lastTime                           = 0L;
    private double            curTimeIndex                       = 0D;

    DecimalFormat             df                                 = new DecimalFormat("0.00");

}
