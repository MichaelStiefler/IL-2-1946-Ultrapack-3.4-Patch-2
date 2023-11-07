package com.maddox.il2.objects.weapons;

import java.io.IOException;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.MsgExplosion;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.NearestTargets;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorNet;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.TypeX4Carrier;
import com.maddox.rts.Message;
import com.maddox.rts.NetChannel;
import com.maddox.rts.NetMsgFiltered;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.NetMsgSpawn;
import com.maddox.rts.NetObj;
import com.maddox.rts.NetSpawn;
import com.maddox.rts.NetUpdate;
import com.maddox.rts.Property;
import com.maddox.rts.Spawn;
import com.maddox.rts.Time;

public class RocketX4 extends Rocket {
    static class SPAWN implements NetSpawn {
        private Class spawner = null;
        public void netSpawn(int i, NetMsgInput netmsginput) {
            try {
                NetObj netobj = netmsginput.readNetObj();
                if (netobj == null) return;
                Class[] cArg = {Actor.class, NetChannel.class, int.class, Point3d.class, Orient.class, float.class};
                this.spawner.getDeclaredConstructor(cArg).newInstance(new Object[] {(Actor) netobj.superObj(), netmsginput.channel(), new Integer(i), new Point3d(netmsginput.readFloat(), netmsginput.readFloat(), netmsginput.readFloat()), new Orient(netmsginput.readFloat(), netmsginput.readFloat(), 0.0F), new Float(netmsginput.readFloat())});
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        SPAWN(Class spawner) {
            this.spawner = spawner;
        }
    }

    class Mirror extends ActorNet {

        public void msgNetNewChannel(NetChannel netchannel) {
            if (!Actor.isValid(this.actor())) {
                return;
            }
            try {
                if (netchannel.isMirrored(this)) {
                    return;
                }
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
            if (netmsginput.isGuaranted()) {
                return false;
            }
            if (this.isMirrored()) {
                this.out.unLockAndSet(netmsginput, 0);
                this.postReal(Message.currentTime(true), this.out);
            }
            p.x = netmsginput.readFloat();
            p.y = netmsginput.readFloat();
            p.z = netmsginput.readFloat();
            int i = netmsginput.readShort();
            int j = netmsginput.readShort();
            float f = -((i * 180F) / 32000F);
            float f1 = (j * 90F) / 32000F;
            or.set(f, f1, 0.0F);
            RocketX4.this.pos.setAbs(p, or);
            return true;
        }

        NetMsgFiltered out;

        public Mirror(Actor actor, NetChannel netchannel, int i) {
            super(actor, netchannel, i);
            this.out = new NetMsgFiltered();
        }
    }

    class Master extends ActorNet implements NetUpdate {

        public void msgNetNewChannel(NetChannel netchannel) {
            if (!Actor.isValid(this.actor())) {
                return;
            }
            try {
                if (netchannel.isMirrored(this)) {
                    return;
                }
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
                RocketX4.this.pos.getAbs(p, or);
                this.out.writeFloat((float) p.x);
                this.out.writeFloat((float) p.y);
                this.out.writeFloat((float) p.z);
                or.wrap();
                int i = (int) ((or.getYaw() * 32000F) / 180F);
                int j = (int) ((or.tangage() * 32000F) / 90F);
                this.out.writeShort(i);
                this.out.writeShort(j);
                this.post(Time.current(), this.out);
            } catch (Exception exception) {
                NetObj.printDebug(exception);
            }
        }

        NetMsgFiltered out;

        public Master(Actor actor) {
            super(actor);
            this.out = new NetMsgFiltered();
        }
    }

    public boolean interpolateStep() {
        float f = Time.tickLenFs();
        float f1 = (float) this.getSpeed(null);
        f1 += (320F - f1) * 0.1F * f;
        this.pos.getAbs(p, or);
        v.set(1.0D, 0.0D, 0.0D);
        or.transform(v);
        v.scale(f1);
        this.setSpeed(v);
        p.x += v.x * f;
        p.y += v.y * f;
        p.z += v.z * f;
        if (this.isNet() && this.isNetMirror()) {
            this.pos.setAbs(p, or);
            return false;
        }
        if (Actor.isValid(this.getOwner())) {
            int smoothFactor = SMOOTH_FACTOR;
            if (((this.getOwner() != World.getPlayerAircraft()) || !((RealFlightModel) this.fm).isRealMode()) && (this.fm instanceof Pilot)) {
                smoothFactor /= 4;
                Pilot pilot = (Pilot) this.fm;
                if (pilot.target != null) {
                    pilot.target.Loc.get(pT);
                    pT.sub(p);
                    or.transformInv(pT);
                    if (pT.x > -10D) {
                        this.stepBeamRider(((Pilot)this.fm).target.actor);
                        deltaAzimuth = deltaTangage = 0.0F;
                    }
                }
            } else {
                this.deltaAzimuth = this.smoothAdjust(this.deltaAzimuth, ((TypeX4Carrier) this.fm.actor).typeX4CgetdeltaAzimuth(), smoothFactor);
                this.deltaTangage = this.smoothAdjust(this.deltaTangage, ((TypeX4Carrier) this.fm.actor).typeX4CgetdeltaTangage(), smoothFactor);
                or.increment(50F * f * this.deltaAzimuth, 50F * f * this.deltaTangage, 0.0F);
            }
            or.setYPR(or.getYaw(), or.getPitch(), 0.0F);
            ((TypeX4Carrier) this.fm.actor).typeX4CResetControls();
        }
        this.pos.setAbs(p, or);
        if (Time.current() > (this.tStart + 500L)) {
            Actor actor = NearestTargets.getEnemy(0, -1, p, 800D, 0);
            if (Actor.isValid(actor)) {
                float f2 = (float) p.distance(actor.pos.getAbsPoint());
                if ((actor instanceof Aircraft) && (this.prevd != 1000F)) {
                    Point3d proximityFuseDetonationPoint = Fuze_Proximity.checkBlowProximityFuze(this, actor, p, this.lastPos, 40D);
                    if (proximityFuseDetonationPoint != null) {
                        this.pos.setAbs(proximityFuseDetonationPoint, or);
                        this.doExplosionAir();
                        this.postDestroy();
                        this.collide(false);
                        this.drawing(false);
                    }
                }
                this.prevd = f2;
            } else {
                this.prevd = 1000F;
            }
            this.lastPos.set(p);
        }
        if (!Actor.isValid(this.getOwner()) || !(this.getOwner() instanceof Aircraft)) {
            this.doExplosionAir();
            this.postDestroy();
            this.collide(false);
            this.drawing(false);
        }
        return false;
    }

    public boolean stepBeamRider(Actor target) {
        float missileSpeed = (float)this.getSpeed((Vector3d) null);
        
        Point3d p3dLaunchAC = new Point3d(this.getOwner().pos.getAbsPoint());
        Point3d p3dTarget = new Point3d(target.pos.getAbsPoint());
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

    private void computeMissilePath(float missileSpeed, float hTurn, float vTurn, float azimuth, float tangage) {
        float turnStepMax = MissilePhysics.getDegPerSec(missileSpeed, maxG) * Time.tickLenFs(); // turn limit, higher altitude means less turn capability (app. 1/10th from ground to 10km)
        float turnStepMaxSpeedFactor = 1F;
        float turnStepMaxAltitudeFactor = MissilePhysics.getAirDensityFactor((float) this.pos.getAbsPoint().z);
        if (missileSpeed < 340F) turnStepMaxSpeedFactor = missileSpeed / 340F;
        else {
            turnStepMaxAltitudeFactor *= Math.sqrt(missileSpeed / 340F);
            if (turnStepMaxAltitudeFactor > 1F) turnStepMaxAltitudeFactor = 1F;
        }

        turnStepMax *= turnStepMaxSpeedFactor * turnStepMaxAltitudeFactor;

        float newTurnDiffMax = turnStepMax / stepsForFullTurn; // turn rate change limit, smoothen the turns.
        if (newTurnDiffMax > this.turnDiffMax) this.turnDiffMax = (this.turnDiffMax * stepsForFullTurn + newTurnDiffMax) / (stepsForFullTurn + 1.0F);

            do {
                if (Math.abs(azimuth) > 90) break; // Target outside Radar coverage
                if (Math.abs(tangage) > 90) break; // Target outside Radar coverage

                if (hTurn * this.oldDeltaAzimuth < 0.0F) this.deltaAzimuth = hTurn * this.turnDiffMax;
                else {
                    this.deltaAzimuth = this.oldDeltaAzimuth + hTurn * this.turnDiffMax;
                    if (this.deltaAzimuth < -turnStepMax) this.deltaAzimuth = -turnStepMax;
                    if (this.deltaAzimuth > turnStepMax) this.deltaAzimuth = turnStepMax;
                }

                if (vTurn * this.oldDeltaTangage < 0.0F) this.deltaTangage = vTurn * this.turnDiffMax;
                else {
                    this.deltaTangage = this.oldDeltaTangage + vTurn * this.turnDiffMax;
                    if (this.deltaTangage < -turnStepMax) this.deltaTangage = -turnStepMax;
                    if (this.deltaTangage > turnStepMax) this.deltaTangage = turnStepMax;
                }

            } while (false);
            this.oldDeltaAzimuth = this.deltaAzimuth;
            this.oldDeltaTangage = this.deltaTangage;
        float sinRoll = (float) Math.sin(Math.toRadians(this.pos.getAbsOrient().getKren()));
        float cosRoll = (float) Math.cos(Math.toRadians(this.pos.getAbsOrient().getKren()));
        float rotDeltaAzimuth = cosRoll * this.deltaAzimuth - sinRoll * this.deltaTangage;
        float rotDeltaTangage = sinRoll * this.deltaAzimuth + cosRoll * this.deltaTangage;
        or.increment(rotDeltaAzimuth, rotDeltaTangage, 0.0F);
    }
    
    public RocketX4() {
        this.fm = null;
        this.tStart = 0L;
        this.prevd = 1000F;
    }

    public RocketX4(Actor actor, NetChannel netchannel, int i, Point3d point3d, Orient orient, float f) {
        this.fm = null;
        this.tStart = 0L;
        this.prevd = 1000F;
        this.net = new Mirror(this, netchannel, i);
        this.pos.setAbs(point3d, orient);
        this.pos.reset();
        this.pos.setBase(actor, null, true);
        this.doStart(-1F);
        v.set(1.0D, 0.0D, 0.0D);
        orient.transform(v);
        v.scale(f);
        this.setSpeed(v);
        this.collide(false);
    }

    public void start(float f, int i) {
        Actor actor = this.pos.base();
        if (Actor.isValid(actor) && (actor instanceof Aircraft)) {
            if (actor.isNetMirror()) {
                this.destroy();
                return;
            }
            this.net = new Master(this);
        }
        this.doStart(f);
    }

    void doStart(float f) {
        super.start(-1F, 0);
        this.fm = ((Aircraft) this.getOwner()).FM;
        this.tStart = Time.current();
        if (Config.isUSE_RENDER()) {
            this.fl1 = Eff3DActor.New(this, this.findHook("_NavLightR"), null, 1.0F, "3DO/Effects/Fireworks/FlareRed.eff", -1F);
            this.fl2 = Eff3DActor.New(this, this.findHook("_NavLightG"), null, 1.0F, "3DO/Effects/Fireworks/FlareGreen.eff", -1F);
            this.flame.drawing(true);
        }
        this.pos.getAbs(p, or);
        or.setYPR(or.getYaw(), or.getPitch(), 0.0F);
        this.pos.setAbs(p, or);
        this.lastPos.set(p);
    }

    public void destroy() {
        if (this.isNet() && this.isNetMirror()) {
            this.doExplosionAir();
        }
        if (Config.isUSE_RENDER()) {
            Eff3DActor.finish(this.fl1);
            Eff3DActor.finish(this.fl2);
        }
        super.destroy();
    }

    protected void doExplosion(Actor actor, String s) {
        this.pos.getTime(Time.current(), p);
        MsgExplosion.send(actor, s, p, this.getOwner(), 45F, 2.0F, 1, 550F);
        super.doExplosion(actor, s);
    }

    protected void doExplosionAir() {
        this.pos.getTime(Time.current(), p);
        MsgExplosion.send(null, null, p, this.getOwner(), 45F, 2.0F, 1, 550F);
        super.doExplosionAir();
    }

    public NetMsgSpawn netReplicate(NetChannel netchannel) throws IOException {
        NetMsgSpawn netmsgspawn = super.netReplicate(netchannel);
        netmsgspawn.writeNetObj(this.getOwner().net);
        Point3d point3d = this.pos.getAbsPoint();
        netmsgspawn.writeFloat((float) point3d.x);
        netmsgspawn.writeFloat((float) point3d.y);
        netmsgspawn.writeFloat((float) point3d.z);
        Orient orient = this.pos.getAbsOrient();
        netmsgspawn.writeFloat(orient.azimut());
        netmsgspawn.writeFloat(orient.tangage());
        float f = (float) this.getSpeed(null);
        netmsgspawn.writeFloat(f);
        return netmsgspawn;
    }

    float smoothAdjust(float oldValue, float newValue, int smoothFactor) {
        return ((oldValue * (smoothFactor - 1F)) + newValue) / smoothFactor;
    }

    FlightModel       fm;
    Eff3DActor        fl1;
    Eff3DActor        fl2;
    Orient     or                           = new Orient();
    Point3d    p                            = new Point3d();
    Point3d    pT                           = new Point3d();
    Vector3d   v                            = new Vector3d();
    Actor           victim = null;
    long              tStart;
    float             prevd;
    Point3d           lastPos                      = new Point3d();
    float             deltaAzimuth                 = 0.0F;
    float             deltaTangage                 = 0.0F;
    static int        SMOOTH_FACTOR                = 20;
    float turnDiffMax = 0.0F;
    float oldDeltaAzimuth = 0.0F;
    float oldDeltaTangage = 0.0F;
    static float stepsForFullTurn = 10F;
    static float maxG = 12F;

    static {
        Class class1 = RocketX4.class;
        Property.set(class1, "mesh", "3do/arms/X-4/mono.sim");
        Property.set(class1, "sprite", "3DO/Effects/Tracers/GuidedRocket/Black.eff");
        Property.set(class1, "flame", "3do/effects/rocket/mono.sim");
        Property.set(class1, "smoke", "3DO/Effects/Tracers/GuidedRocket/White.eff");
        Property.set(class1, "emitColor", new Color3f(1.0F, 1.0F, 0.5F));
        Property.set(class1, "emitLen", 50F);
        Property.set(class1, "emitMax", 1.0F);
        Property.set(class1, "sound", "weapon.rocket_132");
        Property.set(class1, "radius", 40F);
        Property.set(class1, "timeLife", 30F);
        Property.set(class1, "timeFire", 33F);
        Property.set(class1, "force", 15712F);
        Property.set(class1, "power", 2.0F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.22F);
        Property.set(class1, "massa", 60F);
        Property.set(class1, "massaEnd", 45F);
        Spawn.add(class1, new SPAWN(class1));
    }

}
