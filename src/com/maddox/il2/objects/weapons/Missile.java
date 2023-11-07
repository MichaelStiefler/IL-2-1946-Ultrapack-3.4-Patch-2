// Source File Name: Missile.java
// Author:           Storebror
package com.maddox.il2.objects.weapons;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Geom;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorNet;
import com.maddox.il2.engine.BulletProperties;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.il2.engine.LightPointWorld;
import com.maddox.il2.engine.MsgCollisionListener;
import com.maddox.il2.engine.MsgCollisionRequestListener;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Selector;
import com.maddox.il2.objects.ActorSimpleMesh;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.TypeFighter;
import com.maddox.il2.objects.air.TypeGuidedMissileCarrier;
import com.maddox.rts.Message;
import com.maddox.rts.NetChannel;
import com.maddox.rts.NetMsgFiltered;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.NetMsgSpawn;
import com.maddox.rts.NetObj;
import com.maddox.rts.NetSpawn;
import com.maddox.rts.NetUpdate;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.TrueRandom;

public abstract class Missile extends Rocket implements MsgCollisionRequestListener, MsgCollisionListener {

    class Master extends ActorNet implements NetUpdate {

        NetMsgFiltered out;
        Orient         theMissileOrient  = new Orient();
        Point3d        theMissilePoint3d = new Point3d();

        public Master(Actor actor) {
            super(actor);
            this.out = new NetMsgFiltered();
        }

        public void msgNetNewChannel(NetChannel netchannel) {
            if (!Actor.isValid(this.actor())) return;
            try {
                if (netchannel.isMirrored(this)) return;
                if (netchannel.userState == 0) {
                    NetMsgSpawn netmsgspawn = this.actor().netReplicate(netchannel);
                    if (netmsgspawn != null) {
                        this.postTo(netchannel, netmsgspawn);
                        this.actor().netFirstUpdate(netchannel);
                    }
                }
            } catch (Exception exception) {
                NetObj.printDebug(exception);
            }
        }

        public boolean netInput(NetMsgInput netmsginput) throws IOException {
            return false;
        }

        public void netUpdate() {
            try {
                this.out.unLockAndClear();
                this.actor().pos.getAbs(this.theMissilePoint3d, this.theMissileOrient);
                this.out.writeFloat((float) this.theMissilePoint3d.x);
                this.out.writeFloat((float) this.theMissilePoint3d.y);
                this.out.writeFloat((float) this.theMissilePoint3d.z);
                this.theMissileOrient.wrap();
                short sYaw = (short) (this.theMissileOrient.getYaw() * 182F);
                short sPitch = (short) (this.theMissileOrient.getTangage() * 364F);
                short sRoll = (short) (this.theMissileOrient.getKren() * 182F);
                this.out.writeShort(sYaw);
                this.out.writeShort(sPitch);
                this.out.writeShort(sRoll);
                float f = (float) this.actor().getSpeed(null);
                this.out.writeFloat(f);
                this.post(Time.current(), this.out);
            } catch (Exception exception) {
                NetObj.printDebug(exception);
            }
        }
    }

    class Mirror extends ActorNet {

        NetMsgFiltered out;
        Orient         theMissileOrient      = new Orient();
        Point3d        theMissilePoint3d     = new Point3d();
        Vector3d       theTrajectoryVector3d = new Vector3d();

        public Mirror(Actor actor, NetChannel netchannel, int i) {
            super(actor, netchannel, i);
            this.out = new NetMsgFiltered();
        }

        public void msgNetNewChannel(NetChannel netchannel) {
            if (!Actor.isValid(this.actor())) return;
            if (netchannel.isMirrored(this)) return;
            try {
                if (netchannel.userState == 0) {
                    NetMsgSpawn netmsgspawn = this.actor().netReplicate(netchannel);
                    if (netmsgspawn != null) {
                        this.postTo(netchannel, netmsgspawn);
                        this.actor().netFirstUpdate(netchannel);
                    }
                }
            } catch (Exception exception) {
                NetObj.printDebug(exception);
            }
            return;
        }

        public boolean netInput(NetMsgInput netmsginput) throws IOException {
            if (netmsginput.isGuaranted()) return false;
            if (this.isMirrored()) {
                this.out.unLockAndSet(netmsginput, 0);
                this.postReal(Message.currentTime(true), this.out);
            }
            if (netmsginput.available() >= 22) {
                // new UP3.2 Style Missile Data

                this.actor().pos.getAbs(this.theMissilePoint3d);
                float newPosX = netmsginput.readFloat();
                float newPosY = netmsginput.readFloat();
                float newPosZ = netmsginput.readFloat();
                int smoothFactor = 10;
                this.theMissilePoint3d.set((this.theMissilePoint3d.x * (smoothFactor - 1) + newPosX) / smoothFactor, (this.theMissilePoint3d.y * (smoothFactor - 1) + newPosY) / smoothFactor,
                        (this.theMissilePoint3d.z * (smoothFactor - 1) + newPosZ) / smoothFactor);
                short sYaw = netmsginput.readShort();
                short sPitch = netmsginput.readShort();
                short sRoll = netmsginput.readShort();
                float fYaw = -(sYaw / 182F);
                float fPitch = sPitch / 364F;
                float fRoll = sRoll / 182F;
                float fSpeed = netmsginput.readFloat();
                this.theMissileOrient.set(fYaw, fPitch, fRoll);
                this.theMissileOrient.wrap();
                this.theTrajectoryVector3d.set(1.0D, 0.0D, 0.0D);
                this.theMissileOrient.transform(this.theTrajectoryVector3d);
                this.theTrajectoryVector3d.scale(fSpeed);
                this.actor().pos.setAbs(this.theMissilePoint3d, this.theMissileOrient);
                this.actor().setSpeed(this.theTrajectoryVector3d);
            } else {
                // old UP3 RC4 Style Missile Data
                this.theMissilePoint3d.set(netmsginput.readFloat(), netmsginput.readFloat(), netmsginput.readFloat());
                short sYaw = netmsginput.readShort();
                short sPitch = netmsginput.readShort();
                float fYaw = -(sYaw * 180F / 32000F);
                float fPitch = sPitch * 90F / 32000F;
                this.theMissileOrient.set(fYaw, fPitch, 0.0F);
                this.theMissileOrient.wrap();
                this.actor().pos.setAbs(this.theMissilePoint3d, this.theMissileOrient);
            }
            return true;
        }
    }

    /**
     * Helper Class for a chained list of active Nav Lights for a particular Missile. This class is necessary since missiles can have different numbers of active Nav Lights.
     */
    private class MissileNavLight {
        public MissileNavLight nextNavLight; // Pointer to the next chain element
        public Eff3DActor      theNavLight; // The Nav Light Object in the Chain

        public MissileNavLight(Eff3DActor theEff3DActor) { // Element constructor.
            this.theNavLight = theEff3DActor;
            this.nextNavLight = null;
        }
    }

    static class SPAWN implements NetSpawn {

        SPAWN() {
        }

        public void doSpawn(Actor actor, NetChannel netchannel, int i, Point3d point3d, Orient orient, float f) {
        }

        public void netSpawn(int i, NetMsgInput netmsginput) {
            NetObj netobj;
            netobj = netmsginput.readNetObj();
            if (netobj == null) return;
            try {
                Actor actor = (Actor) netobj.superObj();
                Point3d point3d = new Point3d();
                Orient orient = new Orient();
                float f = 0F;
                if (netmsginput.available() >= 28) {
                    // new UP3.2 Style Missile Data
                    point3d.set(netmsginput.readFloat(), netmsginput.readFloat(), netmsginput.readFloat());
                    orient.set(netmsginput.readFloat(), netmsginput.readFloat(), netmsginput.readFloat());
                    f = netmsginput.readFloat();
                } else {
                    // old UP3 RC4 Style Missile Data
                    point3d.set(netmsginput.readFloat(), netmsginput.readFloat(), netmsginput.readFloat());
                    orient.set(netmsginput.readFloat(), netmsginput.readFloat(), 0F);
                    f = netmsginput.readFloat();
                }
                this.doSpawn(actor, netmsginput.channel(), i, point3d, orient, f);
                if (actor instanceof TypeGuidedMissileCarrier) ((TypeGuidedMissileCarrier) actor).getGuidedMissileUtils().shootNextMissile(true);
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                exception.printStackTrace();
            }
            return;
        }
    }

    public Missile() {
        this.targetPoint3d = new Point3d();
        this.trajectoryVector3d = new Vector3d();
        this.victimSpeed = new Vector3d();
        this.victimOffsetOrient = new Orient();
        this.victimOffsetPoint3f = new Point3f();
        this.flags |= 0xE0;
        this.MissileInit();
        return;
    }

    public Missile(Actor actor, NetChannel netchannel, int i, Point3d point3d, Orient orient, float f) {
        this.flags |= 0xE0;
        this.MissileInit(actor, netchannel, i, point3d, orient, f);
    }

    public float AttackMaxDistance() {
        return 5000F;
    }

    public int chooseBulletType(BulletProperties abulletproperties[]) {
        if (abulletproperties.length == 1) return 0;
        if (abulletproperties.length <= 0) return -1;
        if (abulletproperties[0].power <= 0.0F) return 1;
        if (abulletproperties[1].power <= 0.0F) return 0;
        if (abulletproperties[0].powerType == 1) return 0;
        if (abulletproperties[1].powerType == 1) return 1;
        if (abulletproperties[0].powerType == 0) return 0;
        if (abulletproperties[1].powerType == 0) return 1;
        return abulletproperties[0].powerType != 2 ? 0 : 1;
    }

    public int chooseShotpoint(BulletProperties bulletproperties) {
        return -1;
    }

//    private boolean computeFuzeState() {
//        if (Time.current() > this.startTime + 2 * this.trackDelay) if (actorHasPos(this.victim)) {
//            Point3d futureVictimPos = new Point3d();
//            this.victim.pos.getTime(Time.tickNext(), futureVictimPos);
//            Point3d futureMissilePos = new Point3d();
//            this.pos.getTime(Time.tickNext(), futureMissilePos);
//            float distance = (float) this.missilePoint3d.distance(this.victim.pos.getAbsPoint());
//            float distanceFuture = (float) futureMissilePos.distance(futureVictimPos); // - 5F;
//            if (distanceFuture < 0F) distanceFuture = 0F;
//            if (this.fuzeRadius > 0F)
//                if ((this.victim instanceof Aircraft || this.victim instanceof MissileInterceptable) && (distanceFuture > distance * 5F || distance > this.previousDistance) && this.previousDistance != 1000F) if (distance < this.fuzeRadius) {
//                    this.doExplosionAir();
//                    this.postDestroy();
//                    this.collide(false);
//                    this.drawing(false);
//                }
//            this.previousDistance = distance;
//        } else this.previousDistance = 1000F;
//        this.safeVictimOffset.set(this.victimOffsetPoint3f);
//        return false;
//    }

    private boolean computeFuzeState() {
        if (Time.current() > this.startTime + 2 * this.trackDelay) if (actorHasPos(this.victim)) {
            float distance = (float) this.missilePoint3d.distance(this.victim.pos.getAbsPoint());
            if (this.fuzeRadius > 0F) {
                if ((this.victim instanceof Aircraft || this.victim instanceof MissileInterceptable) && this.previousDistance != 1000F) {
                    Point3d proximityFuseDetonationPoint = Fuze_Proximity.checkBlowProximityFuze(this, this.victim, this.missilePoint3d, this.lastPos, this.fuzeRadius);
                    if (proximityFuseDetonationPoint != null) {
                        this.pos.setAbs(proximityFuseDetonationPoint, this.missileOrient);
                        this.doExplosionAir();
                        this.postDestroy();
                        this.collide(false);
                        this.drawing(false);
                    }
                }
            }
            this.previousDistance = distance;
        } else this.previousDistance = 1000F;
        this.pos.getAbs(this.lastPos);
        this.safeVictimOffset.set(this.victimOffsetPoint3f);
        return false;
    }

    private float alignValue(float valueToAlign, float referenceValue, float alignFactor) {
        return (valueToAlign * alignFactor + referenceValue) / (1F + alignFactor);
    }

    private void alignOrient(Orient theOrientToAlign, Orient theReferenceOrient, float min, float max, float current, boolean alignYaw, boolean alignPitch, boolean alignRoll) {
        float alignFactor = Aircraft.cvt(current, min, max, 20F, 0F);
        theReferenceOrient.wrap();
        theOrientToAlign.wrap();
//		System.out.print("alignFactor=" + alignFactor + ", reference pitch=" + theReferenceOrient.getPitch() + ", pitch before=" + theOrientToAlign.getPitch());
        theOrientToAlign.setYPR(alignYaw ? this.alignValue(theOrientToAlign.getYaw(), theReferenceOrient.getYaw(), alignFactor) : theOrientToAlign.getYaw(),
                alignPitch ? this.alignValue(theOrientToAlign.getPitch(), theReferenceOrient.getPitch(), alignFactor) : theOrientToAlign.getPitch(),
                alignRoll ? this.alignValue(theOrientToAlign.getRoll(), theReferenceOrient.getRoll(), alignFactor) : theOrientToAlign.getRoll());
//		System.out.println("pitch after=" + theOrientToAlign.getPitch());
    }

    private float computeMissileAccelleration() {
        this.victimOffsetPoint3f.set(this.safeVictimOffset);
        float missileSpeed = (float) this.getSpeed(null);
        if (this.getFailState() == FAIL_TYPE_ENGINE) this.rocketMotorOperationTime = 0.0F;
        this.pos.getAbs(this.missilePoint3d, this.missileOrient);
        this.missileOrient.wrap();

        if (this.rollFactor > 0F) {
            float newRollRate = missileSpeed * this.rollFactor * Time.tickLenFs();
            this.currentRollRate = (this.currentRollRate * 14F + newRollRate) / 15F;
//			System.out.println("newRollRate=" + newRollRate + ", currentRollRate=" + currentRollRate);
            this.missileOrient.increment(0F, 0F, this.currentRollRate);
        }

        float theForce = this.missileForce;
        float millisecondsFromStart = Time.current() - this.startTime;

        if (millisecondsFromStart > this.rocketMotorOperationTime + this.rocketMotorSustainedOperationTime) {
            this.flameActive = false;
            this.smokeActive = false;
            this.spriteActive = false;
            this.endSmoke();
            this.missileMass = this.massaEnd;
            theForce = this.missileForce = 0.0F;
        } else if (millisecondsFromStart > this.rocketMotorOperationTime) {
            this.enterSustainedMode();
            theForce = this.missileForce = this.missileSustainedForce;
        } else {
            if (this.missileForceRunUpTime > 0.001F) if (millisecondsFromStart < this.missileForceRunUpTime) {
                float runUpTimeFactor = millisecondsFromStart / this.missileForceRunUpTime;
                if (runUpTimeFactor > 1.0F) runUpTimeFactor = 1.0F;
                this.setAllSmokeIntensities(runUpTimeFactor);
                this.setAllSpriteIntensities(runUpTimeFactor);
                this.setAllFlameScale(runUpTimeFactor);
                theForce *= (this.initialMissileForce + (100.0F - this.initialMissileForce) * runUpTimeFactor) / 100.0F;
            }
            this.missileMass -= this.massLossPerTick;
        }
        float forceAzimuth = MissilePhysics.getGForce(missileSpeed, this.oldDeltaAzimuth / Time.tickLenFs());
        float forceTangage = MissilePhysics.getGForce(missileSpeed, this.oldDeltaTangage / Time.tickLenFs());
        float turnForce = (float) Math.sqrt(forceAzimuth * forceAzimuth + forceTangage * forceTangage) * MissilePhysics.G_CONST * this.missileMass * ((float) Math.sqrt(2.0D) - 1.0F);
        turnForce *= this.dragCoefficient;

        float resForce = (float) Math.sqrt(Math.abs(theForce * theForce - turnForce * turnForce));
        if (turnForce > theForce) resForce *= -1F;
        float accelForce = resForce - MissilePhysics.getDragInGravity(this.frontSquare, this.dragCoefficient, (float) this.missilePoint3d.z, missileSpeed, this.missileOrient.getTangage(), this.missileMass);
        float accelleration = accelForce / this.missileMass;
        missileSpeed += accelleration * Time.tickLenFs();

        if (missileSpeed < 3F) this.failState = FAIL_TYPE_WARHEAD; // let missile detonate when speed is too low

        this.trajectoryVector3d.set(1.0D, 0.0D, 0.0D);
        if (Time.current() < this.startTime + this.trackDelay && this.launchType == Missile.LAUNCH_TYPE_DROP) // recover from launch pitch even if there's no target.
        {
            this.dropFlightPathOrient.transform(this.trajectoryVector3d);
            this.alignOrient(this.missileOrient, this.dropFlightPathOrient, this.startTime, this.startTime + this.trackDelay, Time.current(), true, true, false);
            // this.missileOrient.set(this.dropFlightPathOrient);
        } else this.missileOrient.transform(this.trajectoryVector3d);
        this.trajectoryVector3d.scale(missileSpeed);
        this.setSpeed(this.trajectoryVector3d);
        this.missilePoint3d.x += this.trajectoryVector3d.x * Time.tickLenFs();
        this.missilePoint3d.y += this.trajectoryVector3d.y * Time.tickLenFs();
        this.missilePoint3d.z += this.trajectoryVector3d.z * Time.tickLenFs();
        if (this.isNet() && this.isNetMirror()) {
            this.pos.setAbs(this.missilePoint3d, this.missileOrient);
            return -1F;
        }
        if (this.getFailState() == FAIL_TYPE_WARHEAD) {
            this.doExplosionAir();
            this.postDestroy();
            this.collide(false);
            this.drawing(false);
            return -1F;
        }
        return missileSpeed;
    }

    private void computeMissilePath(float missileSpeed, float hTurn, float vTurn, float azimuth, float tangage) {
        this.turnStepMax = MissilePhysics.getDegPerSec(missileSpeed, this.maxG) * Time.tickLenFs(); // turn limit, higher altitude means less turn capability (app. 1/10th from ground to 10km)
        float turnStepMaxSpeedFactor = 1F;
        float turnStepMaxAltitudeFactor = MissilePhysics.getAirDensityFactor((float) this.missilePoint3d.z);
        if (missileSpeed < 340F) turnStepMaxSpeedFactor = missileSpeed / 340F;
        else {
            turnStepMaxAltitudeFactor *= Math.sqrt(missileSpeed / 340F);
            if (turnStepMaxAltitudeFactor > 1F) turnStepMaxAltitudeFactor = 1F;
        }

        this.turnStepMax *= turnStepMaxSpeedFactor * turnStepMaxAltitudeFactor;

        float newTurnDiffMax = this.turnStepMax / this.stepsForFullTurn; // turn rate change limit, smoothen the turns.
        if (newTurnDiffMax > this.turnDiffMax) this.turnDiffMax = (this.turnDiffMax * this.stepsForFullTurn + newTurnDiffMax) / (this.stepsForFullTurn + 1.0F);

        if (this.getFailState() == FAIL_TYPE_IVAN) {
//			System.out.println("IRRER IVAN!");
            if (this.ivanTimeLeft < Time.tickLenFs()) {
                if (TrueRandom.nextFloat() < 0.5F) {
                    if (TrueRandom.nextFloat() < 0.5F) this.deltaAzimuth = this.turnStepMax;
                    else this.deltaAzimuth = -this.turnStepMax;
                    this.deltaTangage = TrueRandom.nextFloat(-this.turnStepMax, this.turnStepMax);
                } else {
                    if (TrueRandom.nextFloat() < 0.5F) this.deltaTangage = this.turnStepMax;
                    else this.deltaTangage = -this.turnStepMax;
                    this.deltaAzimuth = TrueRandom.nextFloat(-this.turnStepMax, this.turnStepMax);
                }
                this.ivanTimeLeft = TrueRandom.nextFloat(IVAN_TIME_MIN, IVAN_TIME_MAX);
            } else {
                this.deltaAzimuth = this.oldDeltaAzimuth;
                this.deltaTangage = this.oldDeltaTangage;
                this.ivanTimeLeft -= Time.tickLenFs();
                if (this.ivanTimeLeft < Time.tickLenFs()) {
                    this.failState = FAIL_TYPE_NONE;
                    this.ivanTimeLeft = 0.0F;
                }
            }
        } else if (this.getFailState() == FAIL_TYPE_MIRROR) {
            if (TrueRandom.nextFloat() < 0.5F) this.deltaAzimuth = this.turnStepMax;
            else this.deltaAzimuth = -this.turnStepMax;
            if (TrueRandom.nextFloat() < 0.5F) this.deltaTangage = this.turnStepMax;
            else this.deltaTangage = -this.turnStepMax;
        } else if (this.getFailState() == FAIL_TYPE_CONTROL_BLOCKED) {
            this.deltaAzimuth = this.oldDeltaAzimuth;
            this.deltaTangage = this.oldDeltaTangage;
        } else {

            if (this.stepMode == STEP_MODE_HOMING) {
                if (this.targetPoint3d.x > -10D) { // don't track if target has been passed
                    this.deltaAzimuth = -azimuth;
                    if (this.deltaAzimuth > this.turnStepMax) this.deltaAzimuth = this.turnStepMax; // limit turn
                    if (this.deltaAzimuth < -this.turnStepMax) this.deltaAzimuth = -this.turnStepMax; // limit turn
                    this.deltaTangage = tangage;
                    if (this.deltaTangage > this.turnStepMax) this.deltaTangage = this.turnStepMax; // limit turn
                    if (this.deltaTangage < -this.turnStepMax) this.deltaTangage = -this.turnStepMax; // limit turn
                }
                if (Math.abs(this.oldDeltaAzimuth - this.deltaAzimuth) > this.turnDiffMax) if (this.oldDeltaAzimuth < this.deltaAzimuth) this.deltaAzimuth = this.oldDeltaAzimuth + this.turnDiffMax;
                else this.deltaAzimuth = this.oldDeltaAzimuth - this.turnDiffMax;
                if (Math.abs(this.oldDeltaTangage - this.deltaTangage) > this.turnDiffMax) if (this.oldDeltaTangage < this.deltaTangage) this.deltaTangage = this.oldDeltaTangage + this.turnDiffMax;
                else this.deltaTangage = this.oldDeltaTangage - this.turnDiffMax;

            } else if (this.stepMode == STEP_MODE_BEAMRIDER) do {
                if (Math.abs(azimuth) > 90) break; // Target outside Radar coverage
                if (Math.abs(tangage) > 90) break; // Target outside Radar coverage

                if (hTurn * this.oldDeltaAzimuth < 0.0F) this.deltaAzimuth = hTurn * this.turnDiffMax;
                else {
                    this.deltaAzimuth = this.oldDeltaAzimuth + hTurn * this.turnDiffMax;
                    if (this.deltaAzimuth < -this.turnStepMax) this.deltaAzimuth = -this.turnStepMax;
                    if (this.deltaAzimuth > this.turnStepMax) this.deltaAzimuth = this.turnStepMax;
                }

                if (vTurn * this.oldDeltaTangage < 0.0F) this.deltaTangage = vTurn * this.turnDiffMax;
                else {
                    this.deltaTangage = this.oldDeltaTangage + vTurn * this.turnDiffMax;
                    if (this.deltaTangage < -this.turnStepMax) this.deltaTangage = -this.turnStepMax;
                    if (this.deltaTangage > this.turnStepMax) this.deltaTangage = this.turnStepMax;
                }

            } while (false);
            this.oldDeltaAzimuth = this.deltaAzimuth;
            this.oldDeltaTangage = this.deltaTangage;
        }
        float sinRoll = (float) Math.sin(Math.toRadians(this.missileOrient.getKren()));
        float cosRoll = (float) Math.cos(Math.toRadians(this.missileOrient.getKren()));
        float rotDeltaAzimuth = cosRoll * this.deltaAzimuth - sinRoll * this.deltaTangage;
        float rotDeltaTangage = sinRoll * this.deltaAzimuth + cosRoll * this.deltaTangage;
        this.missileOrient.increment(rotDeltaAzimuth, rotDeltaTangage, 0.0F);
//		this.missileOrient.increment(this.deltaAzimuth, this.deltaTangage, 0.0F);
    }

    private void computeNoTrackPath() {
        if (this.launchType == Missile.LAUNCH_TYPE_DROP) // this.dropFlightPathOrient.setYPR((float) this.getLaunchYaw(), (float) this.getLaunchPitch(), this.getRoll());
            this.dropFlightPathOrient.setYPR((float) this.getLaunchYaw(), (float) this.getLaunchPitch(), 0F);
        else // fly straight if no launch pitch.
        {
            if (this.missileOffset != null && actorHasPos(this.getOwner())) {
                this.p3dTemp.set(this.missileOffset); // Offset of missile hook from carrier plane
                this.getOwner().pos.getAbsOrient().transform(this.p3dTemp); // Adjust the offset to the carrier's current orientation
                this.p3dTemp.add(this.getOwner().pos.getAbsPoint()); // Add carrier position. Now p3dTemp is the real world coordinate of the current missile's hook on the carrier plane.
                double distanceFromLaunch = this.missilePoint3d.distance(this.p3dTemp); // distance between missile hook and current position, i.e. the length it traveled on the rail already.
                if (distanceFromLaunch < 5D) { // We're on or near the rail. Make sure that missile doesn't travel through airplane if launched from rail
                    this.v3dTemp.set(distanceFromLaunch, 0D, 0D);
                    this.missileOrient.set(this.getOwner().pos.getAbsOrient());
                    this.missileOrient.transform(this.v3dTemp);
                    this.p3dTemp.add(this.v3dTemp);
                    this.missilePoint3d.set(this.p3dTemp);
                    this.deltaAzimuth = this.deltaTangage = this.oldDeltaAzimuth = this.oldDeltaTangage = 0F;
                    return;
                }
            }
            if (!this.deltaAngleApplied) {
                float fMaxDeltaAngle = Property.floatValue(this.getClass(), "maxDeltaAngle", 0.5F);
                float fDeltaAngleX = (float) Math.pow(TrueRandom.nextFloat_Dome(-1F, 1F), 2F) * fMaxDeltaAngle;
                float fDeltaAngleY = (float) Math.pow(TrueRandom.nextFloat_Dome(-1F, 1F), 2F) * fMaxDeltaAngle;
                this.missileOrient.increment(fDeltaAngleX, fDeltaAngleY, 0.0F);
                this.deltaAngleApplied = true;
            }
            float newYaw = (this.missileOrient.getYaw() * 5F + (float) this.launchYaw) / 6F;
            float newPitch = (this.missileOrient.getPitch() * 5F + (float) this.launchPitch) / 6F;
            this.missileOrient.setYPR(newYaw, newPitch, this.getRoll());
        }
    }

    private void createAdditionalFlames() {
        this.flames = new Actor[this.exhausts];
        this.flames[0] = this.flame;
        if (this.simFlame == null) return;
        Hook theHook = null;
        for (int i = 1; i < this.exhausts; i++) {
            theHook = this.findHook("_SMOKE" + i);
            this.flames[i] = new ActorSimpleMesh(this.simFlame);
            if (this.flames[i] != null) {
                ((ActorSimpleMesh) this.flames[i]).mesh().setScale(1);
                this.flames[i].pos.setBase(this, theHook, false);
                this.flames[i].pos.changeHookToRel();
                this.flames[i].pos.resetAsBase();
            }
        }
    }

    private void createAdditionalSmokes() {
        this.smokes = new Eff3DActor[this.exhausts];
        this.smokes[0] = this.smoke;
        if (this.effSmoke == null) return;
        Hook theHook = null;
        for (int i = 1; i < this.exhausts; i++) {
            theHook = this.findHook("_SMOKE" + i);
            if (theHook == null) {
                this.smokes[i] = null;
                continue;
            }
            this.smokes[i] = Eff3DActor.New(this, theHook, null, 1.0F, this.effSmoke, -1F);
            if (this.smokes[i] != null) this.smokes[i].pos.changeHookToRel();
        }
    }

    private void createAdditionalSprites() {
        this.sprites = new Eff3DActor[this.exhausts];
        this.sprites[0] = this.sprite;
        if (this.effSprite == null) return;
        Hook theHook = null;
        for (int i = 1; i < this.exhausts; i++) {
            theHook = this.findHook("_SMOKE" + i);
            if (theHook == null) {
                this.sprites[i] = null;
                continue;
            }
            this.sprites[i] = Eff3DActor.New(this, theHook, null, this.missileKalibr, this.effSprite, -1F);
            if (this.sprites[i] != null) this.sprites[i].pos.changeHookToRel();
            else {}
        }
    }

    /**
     * Creates The Nav Lights according to Base Hook Name and Effect Name
     *
     * @param theNavLightHookName
     * @param theEffectName
     */
    private void createNamedNavLights(String theNavLightHookName, String theEffectName) {
        int numNavLights = this.getNumNavLights(theNavLightHookName);
        if (numNavLights == 0) return;
        Hook theHook = null;
        for (int i = 0; i < numNavLights; i++) {
            if (i == 0) theHook = this.findHook(theNavLightHookName);
            else theHook = this.findHook(theNavLightHookName + i);
            MissileNavLight theNavLight = new MissileNavLight(Eff3DActor.New(this, theHook, null, 1.0F, theEffectName, -1.0F));
            if (this.firstNavLight == null) {
                this.firstNavLight = theNavLight;
                this.lastNavLight = theNavLight;
            } else {
                this.lastNavLight.nextNavLight = theNavLight;
                this.lastNavLight = theNavLight;
            }
        }
    }

    /**
     * Creates Nav Lights according to the missile's mesh. Three Nav Light Types exist: "_NavLightR" : Red Flare "_NavLightG" : Green Flare "_NavLightW" : White Flare (very bright one) "_NavLightP" : Black Ball (currently useless)
     *
     * First Nav Light has no index, following Nav Lights start with Index "1".
     *
     * Example: A Green and a Red Nav Light are set through following Hooks: _NavLightG _NavLightR
     *
     * Example2: 2 Green Flares and 3 Black Balls are set through following Hooks: _NavLightG _NavLightG1 _NavLightP _NavLightP1 _NavLightP2
     */
    private void createNavLights() {
        this.createNamedNavLights("_NavLightR", "3DO/Effects/Fireworks/FlareRed.eff");
        this.createNamedNavLights("_NavLightG", "3DO/Effects/Fireworks/FlareGreen.eff");
        this.createNamedNavLights("_NavLightW", "3DO/Effects/Fireworks/FlareWhite.eff");
        this.createNamedNavLights("_NavLightP", "3DO/Effects/Fireworks/PhosfourousBall.eff");
    }

    public void destroy() {
        if (this.isNet() && this.isNetMirror()) this.doExplosionAir();
        this.endNavLights();
        this.flameActive = false;
        this.smokeActive = false;
        this.spriteActive = false;
        this.endSmoke();
        this.victim = null;
        this.startTime = 0L;
        this.previousDistance = 1000F;
        if (this.myActiveMissile != null) {
            GuidedMissileUtils.removeActiveMissile(this.myActiveMissile);
            this.myActiveMissile = null;
        }
        if (this instanceof MissileInterceptable) Engine.targets().remove(this);
        // TODO: For RWR
        try {
            Engine.missiles().remove(this);
        } catch (Exception e) {}
        super.destroy();
    }

    private void doStart(float f) {
        this.startEngineDone();
        this.startMissile(-1F, 0);
        this.setMissileEffects();
        this.setMissileStartParams();
        if (this.isNet() && this.isNetMirror()) return;
        this.setMissileDropParams();
        this.setMissileVictim();
        this.myActiveMissile = new ActiveMissile(this, this.getOwner(), this.victim, this.getOwner() == null ? Integer.MAX_VALUE : this.getOwner().getArmy(), this.victim == null ? Integer.MAX_VALUE : this.victim.getArmy(), this.ownerIsAI());
        GuidedMissileUtils.addActiveMissile(this.myActiveMissile);
        this.pos.getAbs(this.lastPos);
    }

    private void endAllFlame() {
        if (this.endedFlame) return;
        if (this.exhausts < 2) {
            if (this.flame != null) {
                this.flame.drawing(false);
                this.flame.destroy();
            }
        } else if (this.flames != null) for (int i = 0; i < this.exhausts; i++) {
            if (this.flames[i] == null) continue;
            this.flames[i].destroy();
        }
        if (this.light != null) this.light.light.setEmit(0.0F, 1.0F);
        this.stopSounds();
        this.endedFlame = true;
    }

    private void setAllFlameScale(float scale) {
        if (this.endedFlame) return;
        if (this.exhausts < 2) {
            if (this.flame != null) ((ActorSimpleMesh) this.flame).mesh().setScale(scale);
        } else if (this.flames != null) for (int i = 0; i < this.exhausts; i++) {
            if (this.flames[i] == null) continue;
            ((ActorSimpleMesh) this.flames[i]).mesh().setScale(scale);
        }
    }

    private void endAllSmoke() {
        if (this.endedSmoke) return;
        if (this.exhausts < 2) {
            if (this.smoke != null) Eff3DActor.finish(this.smoke);
        } else if (this.smokes != null) for (int i = 0; i < this.exhausts; i++) {
            if (this.smokes[i] == null) continue;
            Eff3DActor.finish(this.smokes[i]);
        }
        this.endedSmoke = true;
    }

    private void endAllSprites() {
        if (this.endedSprite) return;
        if (this.exhausts < 2) {
            if (this.sprite != null) Eff3DActor.finish(this.sprite);
        } else if (this.sprites != null) for (int i = 0; i < this.exhausts; i++) {
            if (this.sprites[i] == null) continue;
            Eff3DActor.finish(this.sprites[i]);
        }
        this.endedSprite = true;
    }

    /**
     * Finish all Nav Lights in the chained list. No action required if there are no Nav Lights.
     */
    private void endNavLights() {
        MissileNavLight theNavLight = this.firstNavLight;
        while (theNavLight != null) {
            Eff3DActor.finish(theNavLight.theNavLight);
            theNavLight = theNavLight.nextNavLight;
        }
    }

    protected void endSmoke() {
        if (this.isSmokeEnded) return;
        this.missileForce = 0F;
//		System.out.println("endSmoke this.smokeActive=" + this.smokeActive
//				+ ", this.spriteActive=" + this.spriteActive
//				+ ", this.flameActive=" + this.flameActive
//				+ ", this.endedSmoke=" + this.endedSmoke
//				+ ", this.endedSprite=" + this.endedSprite
//				+ ", this.endedFlame=" + this.endedFlame
//				);
//
//		System.out.println("millisecondsFromStart=" + (Time.current() - this.startTime)
//				+ ", rocketMotorOperationTime=" + rocketMotorOperationTime
//				+ ", rocketMotorSustainedOperationTime=" + rocketMotorSustainedOperationTime);
        if (!this.smokeActive && !this.endedSmoke) this.endAllSmoke();
        if (!this.spriteActive && !this.endedSprite) this.endAllSprites();
        if (!this.flameActive && !this.endedFlame) this.endAllFlame();
        if (this.endedSmoke && this.endedSprite && this.endedFlame) this.isSmokeEnded = true;
    }

    protected void enterSustainedMode() {
        if (this.isSustained) return;
        this.missileForce = this.missileSustainedForce;
        if (this.smokeActive && !this.endedSmoke) if (this.smokeSustain == 0F) {
            this.smokeActive = false;
            this.endAllSmoke();
        } else this.setAllSmokeIntensities(this.smokeSustain);
        if (this.spriteActive && !this.endedSprite) if (this.spriteSustain == 0F) {
            this.spriteActive = false;
            this.endAllSprites();
        } else this.setAllSpriteIntensities(this.spriteSustain);
        this.flameActive = false;
        if (!this.endedFlame) this.endAllFlame();
        this.isSustained = true;
    }

    public int getArmy() {
        return this.getOwner() == null ? 0 : this.getOwner().getArmy();
    }

    public int getFailState() {
        if (this.timeToFailure == 0L) return FAIL_TYPE_NONE;
        long millisecondsFromStart = Time.current() - this.startTime;
        if (millisecondsFromStart < this.timeToFailure) return FAIL_TYPE_NONE;
        if (this.myActiveMissile != null) {
            GuidedMissileUtils.removeActiveMissile(this.myActiveMissile);
            this.myActiveMissile = null;
        }
        if (this.failState == FAIL_TYPE_ELECTRONICS) {
            float fRand = TrueRandom.nextFloat();
            if (fRand < 0.01) return FAIL_TYPE_WARHEAD;
            if (fRand < 0.02) return FAIL_TYPE_REFLEX;
            if (fRand < 0.2) return FAIL_TYPE_MIRROR;
            if (fRand < 0.5) return FAIL_TYPE_CONTROL_BLOCKED;
            return FAIL_TYPE_IVAN;
        }
        return this.failState;
    }

    private FlightModel getFM() {
        if (!this.ownerIsAircraft()) return null;
        return ((Aircraft) this.getOwner()).FM;
    }

    public double getLaunchPitch() {
        double launchPitchTimeFactor = this.getLaunchTimeFactor();
        double theLaunchPitch = 1D + 2.0D * (Math.cos(launchPitchTimeFactor) - 1D + launchPitchTimeFactor / 5.0D);
//		System.out.println("getLaunchPitch launchPitchTimeFactor=" + launchPitchTimeFactor + ", theLaunchPitch=" + theLaunchPitch + ", this.launchPitch=" + this.launchPitch);
        theLaunchPitch *= Math.cos(this.launchKren);
        theLaunchPitch += this.launchPitch;
        while (theLaunchPitch > 180F)
            theLaunchPitch -= 360F;
        while (theLaunchPitch < -180F)
            theLaunchPitch += 360F;
        return theLaunchPitch;
    }

    public double getLaunchTimeFactor() {
        return (Time.current() - this.startTime + (this.engineDelayTime < 0L ? this.engineDelayTime : 0L)) / (double) this.trackDelay * 6.0D;
    }

    public double getLaunchYaw() {
        double launchYawTimeFactor = this.getLaunchTimeFactor();
        double theLaunchYaw = 1D + 2.0D * (Math.cos(launchYawTimeFactor) - 1D + launchYawTimeFactor / 5.0D);
        theLaunchYaw *= Math.sin(this.launchKren);
        theLaunchYaw += this.launchYaw;
        while (theLaunchYaw > 180F)
            theLaunchYaw -= 360F;
        while (theLaunchYaw < -180F)
            theLaunchYaw += 360F;
        return theLaunchYaw;
    }

    private void getMissileProperties() {
        Class localClass = this.getClass();
        this.launchType = Property.intValue(localClass, "launchType", 0);
        this.stepMode = Property.intValue(localClass, "stepMode", 0);
        this.maxFOVfrom = Property.floatValue(localClass, "maxFOVfrom", 180F);
        this.maxLaunchG = Property.floatValue(localClass, "maxLockGForce", 99.9F);
        this.maxG = Property.floatValue(localClass, "maxGForce", 12F);
        this.stepsForFullTurn = Property.floatValue(localClass, "stepsForFullTurn", 10);
        this.rocketMotorOperationTime = Property.floatValue(localClass, "timeFire", 2.2F) * 1000F;
        this.missileTimeLife = Property.floatValue(localClass, "timeLife", 30F) * 1000F;
        this.rocketMotorSustainedOperationTime = Property.floatValue(localClass, "timeSustain", 0F) * 1000F;
        this.timeFire = (long) (this.rocketMotorOperationTime + this.rocketMotorSustainedOperationTime);
        this.timeLife = (long) this.missileTimeLife;
        this.engineDelayTime = Property.longValue(this.getClass(), "engineDelayTime", 0L);
        this.missileForce = Property.floatValue(localClass, "force", 18712F);
        this.missileSustainedForce = Property.floatValue(localClass, "forceSustain", 0F);
        this.leadPercent = Property.floatValue(localClass, "leadPercent", 0.0F);
        this.missileKalibr = Property.floatValue(localClass, "kalibr", 0.2F);
        this.missileMass = Property.floatValue(localClass, "massa", 86.2F);
        this.massaEnd = Property.floatValue(localClass, "massaEnd", 80.0F);
        this.sunRayAngle = Property.floatValue(localClass, "sunRayAngle", 0.0F);
        this.groundTrackFactor = Property.floatValue(localClass, "groundTrackFactor", 0.0F);
        this.flareLockTime = Property.longValue(localClass, "flareLockTime", 1000L);
        this.trackDelay = Property.longValue(localClass, "trackDelay", 1000L);
        this.frontSquare = (float) Math.PI * this.missileKalibr * this.missileKalibr / 4.0F;
        this.missileForceRunUpTime = Property.floatValue(localClass, "forceT1", 0.0F) * 1000F;
        this.initialMissileForce = Property.floatValue(localClass, "forceP1", 0.0F);
        this.dragCoefficient = Property.floatValue(localClass, "dragCoefficient", 0.3F);
        this.exhausts = this.getNumExhausts();
        this.effSmoke = Property.stringValue(localClass, "smoke", null);
        this.effSprite = Property.stringValue(localClass, "sprite", null);
        this.smokeSustain = Property.floatValue(localClass, "smokeSustain", 0F);
        this.effSprite = Property.stringValue(localClass, "sprite", null);
        this.spriteSustain = Property.floatValue(localClass, "spriteSustain", 0F);
        this.simFlame = Property.stringValue(localClass, "flame", null);
        this.fuzeRadius = Property.floatValue(localClass, "fuzeRadius", 0F);
        this.fireAndForget = Property.intValue(localClass, "fireAndForget", 0) != 0;
        this.needIllumination = Property.intValue(localClass, "needIllumination", 0) != 0;
        this.rollFactor = Property.floatValue(localClass, "rollFactor", 0F);
        float failureRate = Property.floatValue(localClass, "failureRate", 10.0F);
        if (TrueRandom.nextFloat(0, 100.0F) < failureRate) {
            this.setFailState();
            float randFail = TrueRandom.nextFloat();
            long baseFailTime = this.trackDelay;
            if (this.failState == FAIL_TYPE_WARHEAD) baseFailTime += baseFailTime;
            this.timeToFailure = baseFailTime + (long) ((this.missileTimeLife - baseFailTime) * randFail);
        } else {
            this.failState = FAIL_TYPE_NONE;
            this.timeToFailure = 0L;
        }
        if (this.rocketMotorOperationTime > 0.0F) this.massLossPerTick = (this.missileMass - this.massaEnd) / (this.rocketMotorOperationTime / 1000F / Time.tickConstLenFs());
        else this.massLossPerTick = 0.0F;
    }

    private int getNumExhausts() {
        if (this.mesh.hookFind("_SMOKE") == -1) return 0;
        int retVal = 1;
        while (this.mesh.hookFind("_SMOKE" + retVal) != -1)
            retVal++;
        return retVal;
    }

    /**
     * Counts Number of Nav Lights for Base Hook Name
     *
     * @param theNavLightHookName
     * @return Number of Nav Lights for Base Hook Name
     */
    private int getNumNavLights(String theNavLightHookName) {
        if (this.mesh.hookFind(theNavLightHookName) == -1) return 0;
        int retVal = 1;
        while (this.mesh.hookFind(theNavLightHookName + retVal) != -1)
            retVal++;
        return retVal;
    }

    public float getRoll() {
        if (Time.current() - this.releaseTime > this.trackDelay) return 0F;
        float fRollCalcAbscissa = (float) (Time.current() - this.startTime) / (float) this.trackDelay * (float) Math.PI;
        float fRollCalcOrdinate = ((float) Math.cos(fRollCalcAbscissa) + 1F) * 0.5F;
        float fRet = 360F - fRollCalcOrdinate * (360F - this.oldRoll);
        while (fRet > 180F)
            fRet -= 360F;
        while (fRet < -180F)
            fRet += 360F;
        return fRet;
    }

    public boolean getShotpointOffset(int i, Point3d point3d) {
        if (i != 0) return false;
        if (point3d != null) point3d.set(0.0D, 0.0D, 0.0D);
        return true;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public int HitbyMask() {
        return -1;
    }

    public boolean interpolateStep() {
        if (this.engineDelayTime > 0L) if (!this.engineRunning) {
            if (Time.current() > this.releaseTime + this.engineDelayTime) this.startEngine();
            this.startTime = Time.current();
        }
        if (this.isNet() && this.isNetMirror()) return true;
        if (Time.current() > this.startTime + this.trackDelay && (this.isSunTracking() || this.isGroundTracking())) this.victim = null;
        if (!this.isFireAndForget() && !Actor.isAlive(this.getOwner())) this.victim = null;
        if (this.isNeedIllumination() && !this.isTargetIlluminated()) this.victim = null;

        if (!this.computeSpecialStepBefore()) return false;
        switch (this.stepMode) {
            case STEP_MODE_HOMING:
                if (!this.stepTargetHoming()) return false;
                break;
            case STEP_MODE_BEAMRIDER:
                if (!this.stepBeamRider()) return false;
                break;
            default:
                break;
        }
        if (!this.computeSpecialStepAfter()) return false;

        this.deltaAzimuth = this.deltaTangage = 0.0F;
        this.pos.setAbs(this.missilePoint3d, this.missileOrient);
//		System.out.println("Speed: " + (int)(this.pos.speed(null) * 3.6D) + " (Mach " + (this.pos.speed(null) * 3.6D / MissilePhysics.getMachForAlt((float)this.missilePoint3d.z)) + ")");
        return this.computeFuzeState();
    }

    public boolean computeSpecialStepBefore() {
        return true;
    }

    public boolean computeSpecialStepAfter() {
        return true;
    }

    private boolean isGroundTracking() {
        if (this.groundTrackFactor == 0.0F) return false; // No Ground Clutter Tracking possible
        this.pos.getAbs(this.missilePoint3d, this.missileOrient);
        this.missileOrient.wrap();
        float ttG = this.missileOrient.getTangage() * -1.0F;
        float missileAlt = (float) (this.missilePoint3d.z - Engine.land().HQ_Air(this.missilePoint3d.x, this.missilePoint3d.y));
        missileAlt /= 1000.0F;
        float groundFactor = ttG / (missileAlt * missileAlt);
        long lTimeCurrent = Time.current();
        if (this.lastTimeNoFlare == 0L) this.lastTimeNoFlare = lTimeCurrent;
        if (groundFactor > this.groundTrackFactor) {
            if (lTimeCurrent < this.lastTimeNoFlare + this.flareLockTime) return false;
            return true;
        }
        this.lastTimeNoFlare = lTimeCurrent;
        return false;
    }

    public boolean isReleased() {
        return this.releaseTime != 0L;
    }

    private boolean isSunTracking() {
        if (this.sunRayAngle == 0.0F) return false; // No Sun Ray Tracking possible
        float sunAngle = GuidedMissileUtils.angleBetween(this, World.Sun().ToSun);
        if (sunAngle < this.sunRayAngle) return true;
        return false;
    }

    public final void MissileInit() {
        this.victim = null;
        this.startTime = 0L;
        this.previousDistance = 1000F;
        this.deltaAzimuth = 0.0F;
        this.deltaTangage = 0.0F;
        this.oldDeltaAzimuth = 0.0F;
        this.oldDeltaTangage = 0.0F;
        this.endedFlame = false;
        this.endedSmoke = false;
        this.endedSprite = false;
        this.flameActive = true;
        this.smokeActive = true;
        this.spriteActive = true;
        this.collide(false);
        this.getMissileProperties();
    }

    public final void MissileInit(Actor actor, NetChannel netchannel, int i, Point3d point3d, Orient orient, float fSpeed) {
        if (Actor.isValid(actor)) if (!Actor.isValid(this.getOwner())) this.setOwner(actor);
        this.victim = null;
        this.startTime = 0L;
        this.previousDistance = 1000F;
        this.oldDeltaAzimuth = 0.0F;
        this.oldDeltaTangage = 0.0F;
        this.endedFlame = false;
        this.endedSmoke = false;
        this.endedSprite = false;
        this.flameActive = true;
        this.smokeActive = true;
        this.spriteActive = true;
        this.net = new Mirror(this, netchannel, i);
        this.pos.setAbs(point3d, orient);
        this.pos.reset();
        this.pos.setBase(actor, null, true);
        this.doStart(-1F);
        this.trajectoryVector3d.set(1.0D, 0.0D, 0.0D);
        orient.transform(this.trajectoryVector3d);
        this.trajectoryVector3d.scale(fSpeed);
        this.setSpeed(this.trajectoryVector3d);
        this.collide(true);
        this.getMissileProperties();
        this.startTime = Time.current();
        this.releaseTime = Time.current();
        if (this.engineDelayTime < 0L) this.startTime += this.engineDelayTime;
        if (this instanceof MissileInterceptable) {
            // TODO: For RWR
            try {
                Engine.missiles().add(this);
            } catch (Exception e) {}
            Engine.targets().add(this);
        }
    }

    public NetMsgSpawn netReplicate(NetChannel netchannel) throws IOException {
        NetMsgSpawn netmsgspawn = super.netReplicate(netchannel);
        netmsgspawn.writeNetObj(this.getOwner().net);

        Point3d futurePos = new Point3d();
        this.pos.getTime(Time.tickNext(), futurePos);
        netmsgspawn.writeFloat((float) futurePos.x);
        netmsgspawn.writeFloat((float) futurePos.y);
        netmsgspawn.writeFloat((float) futurePos.z);
        Orient orient = this.pos.getAbsOrient();
        netmsgspawn.writeFloat(orient.azimut());
        netmsgspawn.writeFloat(orient.tangage());
        netmsgspawn.writeFloat(orient.kren());
        float f = (float) this.getSpeed(null);
        netmsgspawn.writeFloat(f);
        return netmsgspawn;
    }

    private boolean ownerIsAI() {
        if (this.getFM() == null) return true;
        if ((this.getOwner() != World.getPlayerAircraft() || !((RealFlightModel) this.getFM()).isRealMode()) && this.getFM() instanceof Pilot) return true;
        return false;
    }

    private boolean ownerIsAircraft() {
        if (this.getOwner() instanceof Aircraft) return true;
        return false;
    }

    public void runupEngine() {
        float millisecondsFromStart = Time.current() - this.startTime;
        float runUpTimeFactor = millisecondsFromStart / this.missileForceRunUpTime;
        if (runUpTimeFactor > 1.0F) runUpTimeFactor = 1.0F;
        this.setAllSmokeIntensities(runUpTimeFactor);
        this.setAllSpriteIntensities(runUpTimeFactor);
        this.setAllFlameScale(runUpTimeFactor);
    }

    private void setAllSmokeIntensities(float theIntensity) {
        if (!this.engineRunning) return;
        if (this.exhausts < 2) {
            if (this.smoke != null) Eff3DActor.setIntesity(this.smoke, theIntensity);
        } else if (this.smokes != null) for (int i = 0; i < this.exhausts; i++) {
            if (this.smokes[i] == null) continue;
            Eff3DActor.setIntesity(this.smokes[i], theIntensity);
        }
    }

    private void setAllSpriteIntensities(float theIntensity) {
        if (!this.engineRunning) return;
        if (this.exhausts < 2) {
            if (this.sprite != null) Eff3DActor.setIntesity(this.sprite, theIntensity);
        } else if (this.sprites != null) for (int i = 0; i < this.exhausts; i++) {
            if (this.sprites[i] == null) continue;
            Eff3DActor.setIntesity(this.sprites[i], theIntensity);
        }
    }

    private void setFailState() {
        if (this.failState == FAIL_TYPE_NONE) this.failState = TrueRandom.nextInt(1, FAIL_TYPE_NUMBER);
    }

    private void setMissileDropParams() {
        switch (this.launchType) {
            default:
            case LAUNCH_TYPE_STRAIGHT: // simply ignite and leave rail
            {
                this.oldRoll = this.missileOrient.getRoll();
                break;
            }
            case LAUNCH_TYPE_QUICK: // "swoosh" off rocket rail
            {
                this.missileBaseSpeed += 5.0F;
                this.trajectoryVector3d.set(1.0D, 0.0D, 0.0D);
                this.missileOrient.transform(this.trajectoryVector3d);
                this.trajectoryVector3d.scale(this.missileBaseSpeed);
                this.setSpeed(this.trajectoryVector3d);
                this.launchKren = Math.toRadians(this.missileOrient.getRoll());
                this.launchYaw = this.missileOrient.getYaw();
                this.launchPitch = this.missileOrient.getPitch();
                this.oldRoll = this.missileOrient.getRoll();
                this.pos.setAbs(this.missilePoint3d, this.missileOrient);
                break;
            }
            case LAUNCH_TYPE_DROP: // drop pattern
            {
                this.launchKren = Math.toRadians(this.getOwner().pos.getAbsOrient().getRoll());
                if (this.ownerIsAircraft()) {
                    this.launchYaw = this.missileOrient.getYaw() - this.getFM().getAOA() * (float) Math.sin(this.launchKren);
                    this.launchPitch = this.missileOrient.getPitch() - this.getFM().getAOA() * (float) Math.cos(this.launchKren);
//					this.launchYaw = this.getOwner().pos.getCurrentOrient().getYaw() - this.getFM().getAOA() * (float) Math.sin(this.launchKren);
//					this.launchPitch = this.getOwner().pos.getCurrentOrient().getPitch() - this.getFM().getAOA() * (float) Math.cos(this.launchKren);
                    this.oldRoll = this.missileOrient.getRoll();
                } else { // if not launched from Aircraft, start in owner's current direction
                    this.launchYaw = this.getOwner().pos.getCurrentOrient().getYaw();
                    this.launchPitch = this.getOwner().pos.getCurrentOrient().getPitch();
                    this.oldRoll = this.getOwner().pos.getCurrentOrient().getRoll();
                }
                this.dropFlightPathOrient.setYPR((float) this.launchYaw /* + 0.5F * (float) Math.sin(this.launchKren) */, (float) this.launchPitch /*- 0.5F * (float) Math.cos(this.launchKren)*/, this.oldRoll);
                break;
            }
        }
    }

    public void setMissileEffects() {
        if (this.engineDelayTime <= 0L) this.setSmokeSpriteFlames();
        this.firstNavLight = null;
        this.lastNavLight = null;
        if (Config.isUSE_RENDER()) this.createNavLights();
        if (this.flame != null) ((ActorSimpleMesh) this.flame).mesh().setScale(1);
        if (Config.isUSE_RENDER() && this.flame != null) this.flame.drawing(true);
    }

    private void setMissileStartParams() {
        this.previousDistance = 1000.0F;
        this.pos.getRelOrient().transformInv(this.speed);
        this.pos.getRelOrient().transform(this.speed);
        this.setStartTime();
        this.pos.getAbs(this.missilePoint3d, this.missileOrient);

        this.missileBaseSpeed = (float) this.getSpeed((Vector3d) null);
    }

    private void setMissileVictim() {
        this.victim = null;
        try {
            if (this.ownerIsAI()) {
                if (this.getOwner() instanceof TypeGuidedMissileCarrier) {
                    this.victim = ((TypeGuidedMissileCarrier) this.getOwner()).getGuidedMissileUtils().getMissileTarget();
                    this.victimOffsetPoint3f = ((TypeGuidedMissileCarrier) this.getOwner()).getGuidedMissileUtils().getMissileTargetOffset();
                    this.safeVictimOffset.set(this.victimOffsetPoint3f);
                } else if (this.getOwner() instanceof TypeFighter) if (this.getFM() != null) this.victim = ((Pilot) this.getFM()).target.actor;
            } else {
                if (this.getFM() != null) if (this.getFM().getOverload() > this.maxLaunchG) {
                    this.victim = null;
                    return;
                }
                if (this.getOwner() instanceof TypeGuidedMissileCarrier) {
                    this.victim = ((TypeGuidedMissileCarrier) this.getOwner()).getGuidedMissileUtils().getMissileTarget();
                    this.victimOffsetPoint3f = ((TypeGuidedMissileCarrier) this.getOwner()).getGuidedMissileUtils().getMissileTargetOffset();
                    this.safeVictimOffset.set(this.victimOffsetPoint3f);
                } else {
                    this.victim = Selector.look(true, false, Main3D.cur3D()._camera3D[Main3D.cur3D().getRenderIndx()], World.getPlayerAircraft().getArmy(), -1, World.getPlayerAircraft(), false);
                    if (this.victim == null) this.victim = Main3D.cur3D().getViewPadlockEnemy();
                }
            }
        } catch (Exception exception) {}
//		System.out.println("setMissileVictim this.victim=" + this.victim);
    }

    private void setSmokeSpriteFlames() {
        if (this.exhausts > 1) {
            if (this.smoke != null) this.createAdditionalSmokes();
            if (this.sprite != null) this.createAdditionalSprites();
            if (this.flame != null) this.createAdditionalFlames();
        }
        this.engineRunning = true;
    }

    public void setStartTime() {
        if (this.startTimeIsSet) return;
        this.startTime = Time.current();
        this.startTimeIsSet = true;
    }

    public void start(float f) {
        this.start(f, 0);
    }

    public void start(float f, int paramInt) {
        Actor actor = this.pos.base();
        this.missileOffset = new Point3d(this.pos.getRelPoint());
        try {
            if (Actor.isValid(actor)) {
                if (actor.isNetMirror()) {
                    this.destroy();
                    return;
                }
                this.net = new Master(this);
            }
        } catch (Exception exception) {
            GuidedMissileUtils.LocalLog(this.getOwner(), "Missile launch cancelled (system error):" + exception.getMessage());
            this.destroy();
        }
        this.doStart(f);
    }

    public void startEngine() {
        if (this.engineRunning) return;
        Class localClass = this.getClass();
        Hook localHook = null;
        String str = Property.stringValue(localClass, "sprite", null);
        if (str != null) {
            if (localHook == null) localHook = this.findHook("_SMOKE");
            this.sprite = Eff3DActor.New(this, localHook, null, 1.0F, str, -1.0F);
            if (this.sprite != null) this.sprite.pos.changeHookToRel();
        }
        this.createAdditionalSprites();
        str = Property.stringValue(localClass, "flame", null);
        if (str != null) {
            if (localHook == null) localHook = this.findHook("_SMOKE");
            this.flame = new ActorSimpleMesh(str);
            if (this.flame != null) {
                ((ActorSimpleMesh) this.flame).mesh().setScale(1.0F);
                this.flame.pos.setBase(this, localHook, false);
                this.flame.pos.changeHookToRel();
                this.flame.pos.resetAsBase();
            }
        }
        this.createAdditionalFlames();
        str = Property.stringValue(localClass, "smoke", null);
        if (str != null) {
            if (localHook == null) localHook = this.findHook("_SMOKE");
            this.smoke = Eff3DActor.New(this, localHook, null, 1.0F, str, -1.0F);
            if (this.smoke != null) this.smoke.pos.changeHookToRel();
        }
        this.createAdditionalSmokes();
        this.soundName = Property.stringValue(localClass, "sound", null);
        if (this.soundName != null) this.newSound(this.soundName, true);
        this.engineRunning = true;
    }

    public void startEngineDone() {
        this.releaseTime = Time.current();
        if (this instanceof MissileInterceptable) Engine.targets().add(this);
        // TODO: For RWR
        try {
            Engine.missiles().add(this);
        } catch (Exception e) {}
    }

    public void startMissile(float paramFloat, int paramInt) {
        Class localClass = this.getClass();
        float f1 = Property.floatValue(localClass, "kalibr", 0.082F);
        if (paramFloat <= 0.0F) paramFloat = Property.floatValue(localClass, "timeLife", 45.0F);

        float f2 = -1.0F + 2.0F * TrueRandom.nextFloat();
        f2 *= f2 * f2;
        float f3 = -1.0F + 2.0F * TrueRandom.nextFloat();
        f3 *= f3 * f3;

        this.init(f1, Property.floatValue(localClass, "massa", 6.8F), Property.floatValue(localClass, "massaEnd", 2.52F), Property.floatValue(localClass, "timeFire", 4.0F) / (1.0F + 0.1F * f2),
                Property.floatValue(localClass, "force", 500.0F) * (1.0F + 0.1F * f2), paramFloat + f3 * 0.1F);

        this.setOwner(this.pos.base(), false, false, false);
        this.pos.setBase(null, null, true);
        this.pos.setAbs(this.pos.getCurrent());

        if (this.launchType == LAUNCH_TYPE_DROP) {
            this.pos.getAbs(Aircraft.tmpOr);
            float f4 = Property.floatValue(localClass, "maxDeltaAngle", 3.0F);
            f2 = TrueRandom.nextFloat(-1F, 1F);
            f3 = TrueRandom.nextFloat(-1F, 1F);
            f2 *= f2 * f2 * f4;
            f3 *= f3 * f3 * f4;
            Aircraft.tmpOr.increment(f2, f3, 0.0F);
            this.pos.setAbs(Aircraft.tmpOr);
            this.deltaAngleApplied = true;
        } else this.deltaAngleApplied = false;

        this.pos.getRelOrient().transformInv(this.speed);

        this.speed.z /= 3.0D;
        this.pos.getRelOrient().transform(this.speed);
        this.collide(true);
        this.interpPut(new Interpolater(), null, Time.current(), null);
        if (this.getOwner() == World.getPlayerAircraft()) World.cur().scoreCounter.rocketsFire += 1;
        if (!Config.isUSE_RENDER()) return;
        if (this.engineDelayTime <= 0L) this.startEngine();
        this.light = new LightPointActor(new LightPointWorld(), new Point3d());
        this.light.light.setColor((Color3f) Property.value(localClass, "emitColor", new Color3f(1.0F, 1.0F, 0.5F)));
        this.light.light.setEmit(Property.floatValue(localClass, "emitMax", 1.0F), Property.floatValue(localClass, "emitLen", 50.0F));
        this.draw.lightMap().put("light", this.light);
    }

//    public static Point3d trackPoint(Point3d p3dMissile, Point3d p3dTarget, Point3d p3dLaunchAC) {
//        Vector3d dvLaunchAC_Target = new Vector3d(p3dTarget);                                                  // Current Target position in Vector notation
//        dvLaunchAC_Target.sub(p3dLaunchAC);                                                                    // Subtract the launch AC's position, result is Target Ray Vector "AC"
//        Vector3d dvLaunchAC_Missile = new Vector3d(p3dMissile);                                                // Missile's current position in Vector notation
//        dvLaunchAC_Missile.sub(p3dLaunchAC);                                                                   // Subtract the launch AC's position, result is Missile Vector "AB"
//        double scalar = dvLaunchAC_Missile.dot(dvLaunchAC_Target) / dvLaunchAC_Target.dot(dvLaunchAC_Target);  // dot Product Equation (2) gets us scalar "t".
//        Point3d trackPoint = new Point3d(dvLaunchAC_Target);                                                   // Point according to Target Ray Vector with Offset (0, 0, 0)
//        trackPoint.scale(scalar);                                                                              // Scale that point (i.e. the ray vector) according to scalar "t", this is "t(C - A)"
//        trackPoint.add(p3dLaunchAC);                                                                           // Use Launch AC position as new offset, i.e. add "A"
//        // +++ DEBUG OUTPUT +++
//        if (Config.cur.ini.get("Mods", "debugTrackPoint", 0) == 1) {
//            System.out.println("trackPointDistance:" + p3dMissile.distance(trackPoint));
//            System.out.println("Launch AC Pos:" + p3dLaunchAC.toString());
//            System.out.println("Target Pos:" + p3dTarget.toString());
//            System.out.println("Missile Pos:" + p3dMissile.toString());
//            System.out.println("trackPoint Pos:" + trackPoint.toString());
//        }
//        // --- DEBUG OUTPUT ---
//        return trackPoint;                                                                                     // return the track point
//    }
    
    public boolean stepBeamRider() {
        float missileSpeed = this.computeMissileAccelleration();
        if (missileSpeed == -1F) return false;
        if (this.getOwner() == null || (this.victim != null && GuidedMissileUtils.angleBetween(this.getOwner(), this.victim) > this.maxFOVfrom)) this.victim = null;
        if (Time.current() < this.startTime + this.trackDelay) {
            this.computeNoTrackPath();
            return true;
        }
        if (this.victim == null) return true;
        
        Point3d p3dLaunchAC = new Point3d(this.getOwner().pos.getAbsPoint());
        Point3d p3dTarget = new Point3d(this.victim.pos.getAbsPoint());
        Point3d p3dMissile = new Point3d(this.pos.getAbsPoint());

        Vector3d dvLaunchAC_Target = new Vector3d(p3dTarget);                                                  // Current Target position in Vector notation
        dvLaunchAC_Target.sub(p3dLaunchAC);                                                                    // Subtract the launch AC's position, result is Target Ray Vector "AC"
        Vector3d dvLaunchAC_Missile = new Vector3d(p3dMissile);                                                // Missile's current position in Vector notation
        dvLaunchAC_Missile.sub(p3dLaunchAC);                                                                   // Subtract the launch AC's position, result is Missile Vector "AB"
        double scalar = dvLaunchAC_Missile.dot(dvLaunchAC_Target) / dvLaunchAC_Target.dot(dvLaunchAC_Target);  // dot Product Equation (2) gets us scalar "t".
        Point3d p3dTrack = new Point3d(dvLaunchAC_Target);                                                     // Point according to Target Ray Vector with Offset (0, 0, 0)
        p3dTrack.scale(scalar);                                                                                // Scale that point (i.e. the ray vector) according to scalar "t", this is "t(C - A)"
        p3dTrack.add(p3dLaunchAC);                                                                             // Use Launch AC position as new offset, i.e. add "A"
        // +++ DEBUG OUTPUT +++
        if (Config.cur.ini.get("Mods", "debugTrackPoint", 0) == 1) {
            System.out.println("scalar:" + scalar);
            System.out.println("missileSpeed:" + missileSpeed);
            System.out.println("trackPointDistance:" + p3dMissile.distance(p3dTrack));
            System.out.println("Launch AC Pos:" + p3dLaunchAC.toString());
            System.out.println("Target Pos:" + p3dTarget.toString());
            System.out.println("Missile Pos:" + p3dMissile.toString());
            System.out.println("trackPoint Pos:" + p3dTrack.toString());
        }
        // --- DEBUG OUTPUT ---
        Vector3d dvMissile_Track = new Vector3d(p3dTrack);                                                      // Track Point position in Vector notation
        dvMissile_Track.sub(p3dMissile);                                                                        // Subtract the Missile's current position, result is Missile to Track Vector "BD"
        
        dvLaunchAC_Target.normalize();
        dvLaunchAC_Target.scale(missileSpeed);
        if (p3dMissile.distance(p3dTrack) > missileSpeed / 5D)
            dvMissile_Track.scale(missileSpeed / p3dMissile.distance(p3dTrack));
        else
            dvMissile_Track.scale(5D);
        Vector3d dvMissileVector = new Vector3d(dvMissile_Track);
        dvMissileVector.add(dvLaunchAC_Target);
//        dvMissileVector.normalize();
        
        Orient missileOrient = new Orient();
        missileOrient.set(this.pos.getAbsOrient());
        missileOrient.setRoll(0F);
        missileOrient.transformInv(dvMissileVector);
        
        float missileAzimuth = (float) Math.toDegrees(Math.atan(-dvMissileVector.y / dvMissileVector.x));
        float missileElevation = (float) Math.toDegrees(Math.atan(dvMissileVector.z / dvMissileVector.x));
        
        p3dTarget.sub(p3dLaunchAC);
        this.getOwner().pos.getAbsOrient().transformInv(p3dTarget);
        float targetAzimuth = (float) Math.toDegrees(Math.atan(-p3dTarget.y / p3dTarget.x));
        float targetElevation = (float) Math.toDegrees(Math.atan(p3dTarget.z / p3dTarget.x));

        
        if (this.getFailState() == FAIL_TYPE_REFLEX) {
            missileAzimuth += 180F;
            missileElevation += 180F;
            if (missileAzimuth > 180F) missileAzimuth = 180F - missileAzimuth;
            if (missileElevation > 180F) missileElevation = 180F - missileElevation;
        }
        
        float turnNormal = 1.0F;
        float hTurn = 0.0F;
        float vTurn = 0.0F;

        if (missileAzimuth < 0) { // left of beam
            hTurn = -turnNormal; // turn right
        } else { 
            hTurn = turnNormal; // turn left
        }

        if (missileElevation < 0) { // below beam
            vTurn = -turnNormal; // turn up
        } else {
            vTurn = turnNormal; // turn down
        }
        
        // +++ DEBUG OUTPUT +++
        if (Config.cur.ini.get("Mods", "debugTrackPoint", 0) == 1) {
            System.out.println("missileAzimuth:" + missileAzimuth);
            System.out.println("missileElevation:" + missileElevation);
            System.out.println("targetAzimuth:" + targetAzimuth);
            System.out.println("targetElevation:" + targetElevation);
            System.out.println("hTurn:" + hTurn);
            System.out.println("vTurn:" + vTurn);
        }
        // --- DEBUG OUTPUT ---
        this.computeMissilePath(missileSpeed, hTurn, vTurn, targetAzimuth, targetElevation);
        
        return true;
    }

//    public boolean stepBeamRider2() {
//        float missileSpeed = this.computeMissileAccelleration();
//        if (missileSpeed == -1F) return false;
//        Actor myOwner = this.getOwner();
//        if (this.victim != null && GuidedMissileUtils.angleBetween(myOwner, this.victim) > this.maxFOVfrom) this.victim = null;
//        if (Time.current() < this.startTime + this.trackDelay) {
//            this.computeNoTrackPath();
//            return true;
//        }
//        if (this.victim == null || this.getOwner() == null) return true;
//        float hTurn = 0.0F;
//        float vTurn = 0.0F;
//
//        Point3d pointAC = new Point3d();
//        pointAC.set(myOwner.pos.getAbsPoint());
//        Orient orientAC = new Orient();
//        orientAC.set(myOwner.pos.getAbsOrient());
//        Point3d pointTarget = new Point3d();
//        pointTarget.set(this.victim.pos.getAbsPoint());
//        Vector3d vectorOffset = new Vector3d();
//        Orient orientTarget = new Orient();
//        orientTarget.set(this.victim.pos.getAbsOrient());
//        vectorOffset.set(this.victimOffsetPoint3f); // take victim Offset into account
//        orientTarget.transform(vectorOffset); // take victim Offset into account
//        pointTarget.add(vectorOffset); // take victim Offset into account
//
//        Point3d pointMissile = new Point3d();
//        pointMissile.set(this.pos.getAbsPoint());
//
//        Point3d pointMissileAft = new Point3d();
//        pointMissileAft.set(pointMissile);
//        Orient orientMissileAft = new Orient();
//        orientMissileAft.set(this.pos.getAbsOrient());
//        Point3d pointACAft = new Point3d();
//        pointACAft.set(pointAC);
//
//        pointTarget.sub(pointAC);
//        orientAC.transformInv(pointTarget);
//        float targetAzimuth = (float) Math.toDegrees(Math.atan(-pointTarget.y / pointTarget.x));
//        float targetElevation = (float) Math.toDegrees(Math.atan(pointTarget.z / pointTarget.x));
//        if (this.getFailState() == FAIL_TYPE_REFLEX) {
//            targetAzimuth += 180F;
//            targetElevation += 180F;
//            if (targetAzimuth > 180F) targetAzimuth = 180F - targetAzimuth;
//            if (targetElevation > 180F) targetElevation = 180F - targetElevation;
//        }
//
//        pointMissile.sub(pointAC);
//        orientAC.transformInv(pointMissile);
//        float missileAzimuth = (float) Math.toDegrees(Math.atan(-pointMissile.y / pointMissile.x));
//        float missileElevation = (float) Math.toDegrees(Math.atan(pointMissile.z / pointMissile.x));
//
//        float missileOffsetAzimuth = missileAzimuth - targetAzimuth;
//        float missileOffsetElevation = missileElevation - targetElevation;
//
//        pointACAft.sub(pointMissileAft);
//        orientMissileAft.transformInv(pointACAft);
//        float missileAzimuthAft = (float) Math.toDegrees(Math.atan(-pointACAft.y / pointACAft.x));
//        float missileElevationAft = (float) Math.toDegrees(Math.atan(pointACAft.z / pointACAft.x));
//
//        float missileTrackOffsetAzimuth = missileOffsetAzimuth - missileAzimuthAft;
//        float missileTrackOffsetElevation = missileOffsetElevation - missileElevationAft;
//
//        float closingFactor = -5.0F;
//        float maxClosing = 6.0F; //40.0F;
//        float fastClosingMax = 10.0F; //3.0F;
//
//        float turnNormal = 1.0F;
//        float turnQuick = 1.5F;
//        float turnSharp = 2.0F;
//
//        if (missileOffsetAzimuth < 0) { // left of beam
//            if (missileTrackOffsetAzimuth < 0) hTurn = turnSharp; // turn right sharp
//            else if (missileTrackOffsetAzimuth > maxClosing) hTurn = -turnNormal; // turn left
//            else if (missileTrackOffsetAzimuth > fastClosingMax && missileTrackOffsetAzimuth > closingFactor * missileOffsetAzimuth) hTurn = -turnQuick; // turn left quick
//            else hTurn = turnNormal; // turn right
//        } else { 
//            if (missileTrackOffsetAzimuth > 0) hTurn = -turnSharp; // turn left sharply
//            else if (missileTrackOffsetAzimuth < -maxClosing) hTurn = turnNormal; // turn right
//            else if (missileTrackOffsetAzimuth < -fastClosingMax && missileTrackOffsetAzimuth < closingFactor * missileOffsetAzimuth) hTurn = turnQuick; // turn right quick
//            else hTurn = -turnNormal; // turn left
//        }
//
//        if (missileOffsetElevation < 0) { // below beam
//            if (missileTrackOffsetElevation < 0) vTurn = turnSharp; // turn up sharp
//            else if (missileTrackOffsetElevation > maxClosing) vTurn = -turnNormal; // turn down
//            else if (missileTrackOffsetElevation > fastClosingMax && missileTrackOffsetElevation > closingFactor * missileOffsetElevation) vTurn = -turnQuick; // turn down quick
//            else vTurn = turnNormal; // turn up
//        } else {
//            if (missileTrackOffsetElevation > 0) vTurn = -turnSharp; // turn down sharp
//            else if (missileTrackOffsetElevation < -maxClosing) vTurn = turnNormal; // turn up
//            else if (missileTrackOffsetElevation < -fastClosingMax && missileTrackOffsetElevation < closingFactor * missileOffsetElevation) vTurn = turnQuick; // turn up quick
//            else vTurn = -turnNormal; // turn down
//        }
//        
////        System.out.println("moA=" + missileOffsetAzimuth + ", mtoA=" + missileTrackOffsetAzimuth + ", moE=" + missileOffsetElevation + ", mtoE=" + missileTrackOffsetElevation + ", hT=" + hTurn + ", vT=" + vTurn);
//
//        this.computeMissilePath(missileSpeed, hTurn, vTurn, targetAzimuth, targetElevation);
//
//        return true;
//    }

    public boolean stepTargetHoming() {
        float missileSpeed = this.computeMissileAccelleration();
        if (missileSpeed == -1F) return false;

        if (Time.current() < this.startTime + this.trackDelay) {
            this.computeNoTrackPath();
            return true;
        }
        if (this.victim == null) // if (this.failState != FAIL_TYPE_CONTROL_BLOCKED) System.out.println("victim = null, failState=" + this.failState);
//			if (this.timeToFailure != -1L) {
//				this.failState = FAIL_TYPE_CONTROL_BLOCKED;
//				this.turnStepMax = MissilePhysics.getDegPerSec(missileSpeed, this.maxG) * Time.tickLenFs();
//				this.deltaAzimuth = (this.oldDeltaAzimuth = TrueRandom.nextFloat_DomeInv(-this.turnStepMax, this.turnStepMax));
//				this.deltaTangage = (this.oldDeltaTangage = TrueRandom.nextFloat_DomeInv(-this.turnStepMax, this.turnStepMax));
//				this.timeToFailure = -1L;
//				System.out.println("CONTROLS BLOCKED AT azimuth=" + this.deltaAzimuth + ", tangage=" + this.deltaTangage);
//			}
//			this.computeMissilePath(missileSpeed, 0.0F, 0.0F, 0F, 0F);
            return true;

        this.checkChaffFlareLock();
        this.victim.pos.getAbs(this.targetPoint3d, this.victimOffsetOrient);
        // Calculate future victim position
        this.victim.getSpeed(this.victimSpeed); // target movement vector
        double victimDistance = GuidedMissileUtils.distanceBetween(this, this.victim); // distance missile -> target
        double theVictimSpeed = this.victimSpeed.length(); // target speed
        if (theVictimSpeed > 10D) {
            double speedRel = missileSpeed / theVictimSpeed; // relation missile speed / target speed
            double gamma = GuidedMissileUtils.angleActorBetween(this.victim, this); // angle offset missile vector -> target vector
            double alpha = Geom.RAD2DEG((float) Math.asin(Math.sin(Geom.DEG2RAD((float) gamma)) / speedRel)); // angle offset target vector -> target interception path vector
            double beta = 180.0D - gamma - alpha; // angle offset missile vector -> target interception path vector
            double victimAdvance = victimDistance * Math.sin(Geom.DEG2RAD((float) alpha)) / Math.sin(Geom.DEG2RAD((float) beta)); // track made good by target until impact
            victimAdvance -= 5.0D; // impact point 10m aft of engine (track exhaust).
            double timeToTarget = victimAdvance / theVictimSpeed; // time until calculated impact

            this.victimSpeed.scale(timeToTarget * (this.leadPercent / 100.0F));
            this.targetPoint3d.add(this.victimSpeed);
        }
//			Orient orientTarget = new Orient();
//			orientTarget.set(this.victim.pos.getAbsOrient());
        this.oTemp.set(this.victim.pos.getAbsOrient());
        this.trajectoryVector3d.set(this.victimOffsetPoint3f); // take victim Offset into account
        this.oTemp.transform(this.trajectoryVector3d); // take victim Offset into account
//			orientTarget.transform(this.trajectoryVector3d); // take victim Offset into account
        this.targetPoint3d.add(this.trajectoryVector3d); // take victim Offset into account

        this.targetPoint3d.sub(this.missilePoint3d); // relative Position to target
        this.oTemp.setYPR(this.missileOrient.getYaw(), this.missileOrient.getPitch(), 0F);
        this.oTemp.transformInv(this.targetPoint3d); // set coordinate system according to A/C POV
        // this.missileOrient.transformInv(this.targetPoint3d); // set coordinate system according to A/C POV

        // Calculate angle to target.
        // This is required in order to respect the IR Seeker FOV.
        float angleAzimuth = (float) Math.toDegrees(Math.atan(this.targetPoint3d.y / this.targetPoint3d.x));
        float angleTangage = (float) Math.toDegrees(Math.atan(this.targetPoint3d.z / this.targetPoint3d.x));

        if (this.getFailState() == FAIL_TYPE_REFLEX) {
            angleAzimuth += 180F;
            angleTangage += 180F;
            if (angleAzimuth > 180F) angleAzimuth = 180F - angleAzimuth;
            if (angleTangage > 180F) angleTangage = 180F - angleTangage;
        }

        this.computeMissilePath(missileSpeed, 0.0F, 0.0F, angleAzimuth, angleTangage);
        return true;
    }

    public int WeaponsMask() {
        return -1;
    }

    // TODO: For Countermeasures
    public Actor getMissileTarget() {
        return this.victim;
    }

    private void checkChaffFlareLock() {
        List theCountermeasures = null;
        try {
            theCountermeasures = Engine.countermeasures();
        } catch (Exception e) {}

        if (theCountermeasures == null) return;
        int lockType = ((TypeGuidedMissileCarrier) this.getOwner()).getGuidedMissileUtils().getDetectorType();
        int lockTime = TrueRandom.nextInt((int) this.flareLockTime + 1000); // World.rnd().nextInt((int) (flareLockTime + 1000));
        double flareDistance = 0.0D;
        int counterMeasureSize = theCountermeasures.size();
        for (int counterMeasureIndex = 0; counterMeasureIndex < counterMeasureSize; counterMeasureIndex++) {
            Actor flarechaff = (Actor) theCountermeasures.get(counterMeasureIndex);
            flareDistance = GuidedMissileUtils.distanceBetween(this, flarechaff);
            if (lockType == DETECTOR_TYPE_INFRARED && flarechaff instanceof RocketFlare && this.flareLockTime < lockTime && flareDistance < 200D && (double) GuidedMissileUtils.angleActorBetween(this.victim, this) > 30) this.victim = flarechaff;
            else if (flarechaff instanceof RocketChaff && this.flareLockTime < lockTime && flareDistance < 500D && (double) GuidedMissileUtils.angleActorBetween(this.victim, this) > 30) this.victim = flarechaff;
        }
    }

    public float getDeltaAzimuth() {
        return this.deltaAzimuth;
    }

    public void setDeltaAzimuth(float deltaAzimuth) {
        this.deltaAzimuth = deltaAzimuth;
    }

    public float getDeltaTangage() {
        return this.deltaTangage;
    }

    public void setDeltaTangage(float deltaTangage) {
        this.deltaTangage = deltaTangage;
    }

    public Orient getMissileOrient() {
        return this.missileOrient;
    }

    public void setMissileOrient(Orient missileOrient) {
        this.missileOrient = missileOrient;
    }

    public Point3d getMissilePoint3d() {
        return this.missilePoint3d;
    }

    public void setMissilePoint3d(Point3d missilePoint3d) {
        this.missilePoint3d = missilePoint3d;
    }

    public Actor getVictim() {
        return this.victim;
    }

    public void setVictim(Actor victim) {
        this.victim = victim;
    }

    public Point3f getVictimOffsetPoint3f() {
        return this.victimOffsetPoint3f;
    }

    public void setVictimOffsetPoint3f(Point3f victimOffsetPoint3f) {
        this.victimOffsetPoint3f.set(victimOffsetPoint3f);
        this.safeVictimOffset.set(victimOffsetPoint3f);
    }

    public void setVictimOffsetPoint3f(float x, float y, float z) {
        this.victimOffsetPoint3f.set(x, y, z);
        this.safeVictimOffset.set(x, y, z);
    }

    public void setVictimOffsetZ(float victimOffsetZ) {
        this.victimOffsetPoint3f.z = victimOffsetZ;
        this.safeVictimOffset.z = victimOffsetZ;
    }

    public float getTurnStepMax() {
        return this.turnStepMax;
    }

    public void setTurnStepMax(float turnStepMax) {
        this.turnStepMax = turnStepMax;
    }

    public boolean isFireAndForget() {
        return this.fireAndForget;
    }

    public void setFireAndForget(boolean fireAndForget) {
        this.fireAndForget = fireAndForget;
    }

    public boolean isNeedIllumination() {
        return this.needIllumination;
    }

    public void setNeedIllumination(boolean needIllumination) {
        this.needIllumination = needIllumination;
    }

    public boolean isTargetIlluminated() {
        if (this.isFireAndForget() || !this.isNeedIllumination()) return true;
        if (!actorHasPos(this.getOwner()) || !actorHasPos(this.victim)) return false;
        return GuidedMissileUtils.angleBetween(this.getOwner(), this.victim) <= this.maxFOVfrom;
    }

    public float getSmokeSustain() {
        return this.smokeSustain;
    }

    public void setSmokeSustain(float smokeSustain) {
        this.smokeSustain = smokeSustain;
    }

    public float getSpriteSustain() {
        return this.spriteSustain;
    }

    public void setSpriteSustain(float spriteSustain) {
        this.spriteSustain = spriteSustain;
    }

    public static boolean actorHasPos(Actor actor) {
        if (actor == null) return false;
        return actor.pos != null;
    }

    protected static final int DETECTOR_TYPE_INFRARED                = 1;
    protected static final int DETECTOR_TYPE_MANUAL                  = 0;
    protected static final int DETECTOR_TYPE_RADAR_BEAMRIDING        = 3;
    protected static final int DETECTOR_TYPE_RADAR_HOMING            = 2;
    protected static final int DETECTOR_TYPE_RADAR_TRACK_VIA_MISSILE = 4;
    protected static final int FAIL_TYPE_CONTROL_BLOCKED             = 6;
    protected static final int FAIL_TYPE_ELECTRONICS                 = 1;
    protected static final int FAIL_TYPE_ENGINE                      = 3;
    protected static final int FAIL_TYPE_IVAN                        = 5;
    protected static final int FAIL_TYPE_MIRROR                      = 2;
    protected static final int FAIL_TYPE_NONE                        = 0;
    protected static final int FAIL_TYPE_NUMBER                      = 7;
    protected static final int FAIL_TYPE_REFLEX                      = 4;
    protected static final int FAIL_TYPE_WARHEAD                     = 7;
    private static final float IVAN_TIME_MAX                         = 2.0F;
    private static final float IVAN_TIME_MIN                         = 1.0F;
    protected static final int LAUNCH_TYPE_DROP                      = 2;
    protected static final int LAUNCH_TYPE_QUICK                     = 1;
    protected static final int LAUNCH_TYPE_STRAIGHT                  = 0;
    protected static final int STEP_MODE_BEAMRIDER                   = 1;
    protected static final int STEP_MODE_HOMING                      = 0;
    protected static final int TARGET_AIR                            = 1;
    protected static final int TARGET_GROUND                         = 2;
    protected static final int TARGET_SHIP                           = 4;
    private float              deltaAzimuth                          = 0.0F;
    private float              deltaTangage                          = 0.0F;
    private float              dragCoefficient                       = 0.3F;
    private Orient             dropFlightPathOrient                  = new Orient();
    private String             effSmoke                              = null;
    private String             effSprite                             = null;
    private boolean            endedFlame                            = false;
    private boolean            endedSmoke                            = false;
    private boolean            endedSprite                           = false;
    private long               engineDelayTime                       = 0L;
    private boolean            engineRunning                         = false;
    private int                exhausts                              = 1;
    private int                failState                             = FAIL_TYPE_NONE;
    private MissileNavLight    firstNavLight                         = null; // First Nav Light Element in the chained list of Nav Lights.
    private boolean            flameActive                           = true;
    private Actor[]            flames                                = null;
    private long               flareLockTime                         = 1000L;
    private float              frontSquare                           = 1.0F;
    private float              groundTrackFactor                     = 0.0F;
    private float              initialMissileForce                   = 0F;
    private float              ivanTimeLeft                          = 0.0F;
    private MissileNavLight    lastNavLight                          = null; // Last Nav Light Element in the chained list of Nav Lights.
    private long               lastTimeNoFlare                       = 0L;
    private double             launchKren                            = 0.0D;
    private double             launchPitch                           = 0.0D;
    private int                launchType                            = 0;
    private double             launchYaw                             = 0.0D;
    private float              leadPercent                           = 0.0F; // 0 means tail chasing, 100 means full lead tracking
    private float              massaEnd                              = 86.2F;
    private float              massLossPerTick                       = 0F;
    private float              maxFOVfrom                            = 0.0F;
    private float              maxG                                  = 12F; // maximum G-Force during flight
    private float              maxLaunchG                            = 2.0F;
    private float              missileBaseSpeed                      = 0.0F;
    private float              missileForce                          = 18712F;
    private float              missileForceRunUpTime                 = 0F;
    private float              missileKalibr                         = 0.2F;
    private float              missileMass                           = 86.2F;
    private float              fuzeRadius                            = 50F;
    private Orient             missileOrient                         = new Orient();
    private Point3d            missilePoint3d                        = new Point3d();
    private float              missileSustainedForce                 = 0F;
    private float              missileTimeLife                       = 30F;
    private ActiveMissile      myActiveMissile                       = null;
    private float              oldDeltaAzimuth;
    private float              oldDeltaTangage;
    private float              oldRoll                               = 0F;
    private float              previousDistance                      = 0.0F;
    private long               releaseTime                           = 0L;
    private float              rocketMotorOperationTime              = 2.2F;
    private float              rocketMotorSustainedOperationTime     = 0F;
    private Vector3d           safeVictimOffset                      = new Vector3d();
    private String             simFlame                              = null;
    private boolean            smokeActive                           = true;
    private Eff3DActor[]       smokes                                = null;
    private boolean            spriteActive                          = true;
    private Eff3DActor[]       sprites                               = null;
    private long               startTime                             = 0L;
    private boolean            startTimeIsSet                        = false;
    private int                stepMode                              = 0;
    private float              stepsForFullTurn                      = 10; // update steps for maximum control surface output, higher value means slower reaction and smoother flight, lower value means higher agility
    private float              sunRayAngle                           = 0.0F;
    private Point3d            targetPoint3d                         = null;
    private long               timeToFailure                         = 0L;
    private long               trackDelay                            = 1000L;
    private Vector3d           trajectoryVector3d                    = null;
    private float              turnDiffMax                           = 0F;
    DecimalFormat              twoPlaces                             = new DecimalFormat("+000.00;-000.00"); // only required for debugging
    private Point3d            missileOffset                         = null;
    private Orient             oTemp                                 = new Orient();
    private Point3d            p3dTemp                               = new Point3d();
    private Vector3d           v3dTemp                               = new Vector3d();
    private Actor              victim                                = null;
    private boolean            deltaAngleApplied                     = false;
    private Orient             victimOffsetOrient                    = null;
    private Point3f            victimOffsetPoint3f                   = null;
    private Vector3d           victimSpeed                           = null;
    private float              turnStepMax                           = 0F;
    private boolean            fireAndForget                         = false;
    private boolean            needIllumination                      = false;
    private float              smokeSustain                          = 0F;
    private float              spriteSustain                         = 0F;
    private boolean            isSmokeEnded                          = false;
    private boolean            isSustained                           = false;
    private float              rollFactor                            = 0F;
    private float              currentRollRate                       = 0F;
    private Point3d            lastPos                               = new Point3d();
}
