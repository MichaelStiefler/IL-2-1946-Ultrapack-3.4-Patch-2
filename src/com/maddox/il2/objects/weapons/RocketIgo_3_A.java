package com.maddox.il2.objects.weapons;

import java.io.IOException;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.MsgExplosion;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorNet;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.Atmosphere;
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

public class RocketIgo_3_A extends RocketBomb {
    static class SPAWN implements NetSpawn {

        public void netSpawn(int i, NetMsgInput netmsginput) {
            NetObj netobj = netmsginput.readNetObj();
            if (netobj == null) {
                return;
            }
            try {
                Actor actor = (Actor) netobj.superObj();
                Point3d point3d = new Point3d(netmsginput.readFloat(), netmsginput.readFloat(), netmsginput.readFloat());
                Orient orient = new Orient(netmsginput.readFloat(), netmsginput.readFloat(), 0.0F);
                float f = netmsginput.readFloat();
                new RocketIgo_3_A(actor, netmsginput.channel(), i, point3d, orient, f);
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                exception.printStackTrace();
            }
        }

        SPAWN() {
        }
    }

    class Mirror extends ActorNet {

        public void msgNetNewChannel(NetChannel netchannel) {
            if (!Actor.isValid(this.actor())) {
                return;
            }
            if (netchannel.isMirrored(this)) {
                return;
            }
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
        }

        public boolean netInput(NetMsgInput netmsginput) throws IOException {
            if (netmsginput.isGuaranted()) {
                return false;
            }
            if (this.isMirrored()) {
                this.out.unLockAndSet(netmsginput, 0);
                this.postReal(Message.currentTime(true), this.out);
            }
            RocketIgo_3_A.v.x = netmsginput.readFloat();
            RocketIgo_3_A.v.y = netmsginput.readFloat();
            RocketIgo_3_A.v.z = netmsginput.readFloat();
            RocketIgo_3_A.this.setSpeed(RocketIgo_3_A.v);
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
            if (netchannel.isMirrored(this)) {
                return;
            }
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
        }

        public boolean netInput(NetMsgInput netmsginput) throws IOException {
            return false;
        }

        public void netUpdate() {
            try {
                this.out.unLockAndClear();
                RocketIgo_3_A.this.getSpeed(RocketIgo_3_A.v);
                this.out.writeFloat((float) RocketIgo_3_A.v.x);
                this.out.writeFloat((float) RocketIgo_3_A.v.y);
                this.out.writeFloat((float) RocketIgo_3_A.v.z);
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
        if ((this.tEStart > 0L) && (Time.current() > this.tEStart)) {
            this.tEStart = -1L;
            this.setThrust(17500F);
            if (Config.isUSE_RENDER()) {
                if (this.smoke != null) {
                    Eff3DActor.setIntesity(this.smoke, 1.0F);
                }
                if (this.sprite != null) {
                    Eff3DActor.setIntesity(this.sprite, 1.0F);
                }
                if (this.flame != null) {
                    this.flame.drawing(true);
                }
                this.light.light.setEmit(2.0F, 100F);
            }
        }
        if ((World.Rnd().nextFloat() > 0.8F) && (this.fl1 != null)) {
            Eff3DActor.setIntesity(this.fl1, World.Rnd().nextFloat(0.1F, 2.5F));
        }
        if ((World.Rnd().nextFloat() > 0.8F) && (this.fl2 != null)) {
            Eff3DActor.setIntesity(this.fl2, World.Rnd().nextFloat(0.1F, 2.5F));
        }
        float f = Time.tickLenFs();
        this.pos.getAbs(RocketIgo_3_A.p, RocketIgo_3_A.or);
        if (this.first) {
            this.first = false;
        }
        if (Actor.isValid(this.getOwner())) {
            int smoothFactor = SMOOTH_FACTOR;
            if (((this.getOwner() != World.getPlayerAircraft()) || !((RealFlightModel) this.fm).isRealMode()) && (this.fm instanceof Maneuver) && (this.target != null)) {
                smoothFactor /= 4;
                RocketIgo_3_A.pT.set(this.target.pos.getAbsPoint());
                Point3d point3d = new Point3d();
                point3d.x = RocketIgo_3_A.pT.x;
                point3d.y = RocketIgo_3_A.pT.y;
                point3d.z = RocketIgo_3_A.pT.z + 2.5D;
                point3d.sub(RocketIgo_3_A.p);
                RocketIgo_3_A.or.transformInv(point3d);
                Aircraft aircraft = (Aircraft) this.getOwner();
                boolean flag = aircraft.FM.isCapableOfACM() && !aircraft.FM.isReadyToDie() && !aircraft.FM.isTakenMortalDamage() && !aircraft.FM.AS.bIsAboutToBailout && !aircraft.FM.AS.isPilotDead(1);
                if ((point3d.x > -10D) && flag) {
                    double d = Aircraft.cvt(this.fm.Skill * aircraft.FM.AS.getPilotHealth(1), 0.0F, 3F, 10F, 2.0F);
                    if (point3d.y > d) {
                        ((TypeX4Carrier) this.fm.actor).typeX4CAdjSideMinus();
                    }
                    if (point3d.y < -d) {
                        ((TypeX4Carrier) this.fm.actor).typeX4CAdjSidePlus();
                    }
                    if (point3d.z < -d) {
                        ((TypeX4Carrier) this.fm.actor).typeX4CAdjAttitudeMinus();
                    }
                    if (point3d.z > d) {
                        ((TypeX4Carrier) this.fm.actor).typeX4CAdjAttitudePlus();
                    }
                }
            }
            this.getSpeed(RocketBomb.spd);
            float f1 = (float) RocketBomb.spd.length();
            Vector3d vector3d = new Vector3d(0.0D, 0.0D, 0.0D);
//            vector3d.y = -RocketIgo_3_A.azimuthControlScaleFact * f1 * ((TypeX4Carrier) this.fm.actor).typeX4CgetdeltaAzimuth();
//            vector3d.z = RocketIgo_3_A.tangageControlScaleFact * f1 * ((TypeX4Carrier) this.fm.actor).typeX4CgetdeltaTangage();
            this.deltaAzimuth = this.smoothAdjust(this.deltaAzimuth, ((TypeX4Carrier) this.fm.actor).typeX4CgetdeltaAzimuth(), smoothFactor);
            this.deltaTangage = this.smoothAdjust(this.deltaTangage, ((TypeX4Carrier) this.fm.actor).typeX4CgetdeltaTangage(), smoothFactor);
            vector3d.y = -azimuthControlScaleFact * f1 * this.deltaAzimuth;
            vector3d.z = tangageControlScaleFact * f1 * this.deltaTangage;
            if ((vector3d.y != 0.0D) || (vector3d.z != 0.0D)) {
                f1 *= 0.9992F;
            }
            this.pos.getAbs(RocketBomb.Or);
            RocketBomb.Or.transform(vector3d);
            vector3d.z += (f1 * 0.0018D * f * Atmosphere.g() * this.Minit) / this.M;
            if (this.isThrust) {
                vector3d.z += (0.032D * f * Atmosphere.g() * this.Minit) / this.M;
            }
            RocketBomb.spd.add(vector3d);
            float f2 = (float) RocketBomb.spd.length();
            float f3 = f1 / f2;
            RocketBomb.spd.scale(f3);
            this.setSpeed(RocketBomb.spd);
            ((TypeX4Carrier) this.fm.actor).typeX4CResetControls();
        }
        if (!Actor.isValid(this.getOwner()) || !(this.getOwner() instanceof Aircraft)) {
            this.doExplosionAir();
            this.postDestroy();
            this.collide(false);
            this.drawing(false);
            return true;
        } else {
            return true;
        }
    }

    public RocketIgo_3_A() {
        this.first = true;
        this.fm = null;
    }

    public RocketIgo_3_A(Actor actor, NetChannel netchannel, int i, Point3d point3d, Orient orient, float f) {
        this.first = true;
        this.fm = null;
        this.net = new Mirror(this, netchannel, i);
        this.pos.setAbs(point3d, orient);
        this.pos.reset();
        this.pos.setBase(actor, null, true);
        this.doStart(-1F);
        RocketIgo_3_A.v.set(1.0D, 0.0D, 0.0D);
        orient.transform(RocketIgo_3_A.v);
        RocketIgo_3_A.v.scale(f);
        this.setSpeed(RocketIgo_3_A.v);
        this.collide(false);
    }

    public void start(float f) {
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

    private void doStart(float f) {
        super.start(-1F);
        this.fm = ((Aircraft) this.getOwner()).FM;
        if (this.fm instanceof Maneuver) {
            this.maneuver = (Maneuver) this.fm;
            this.target = this.maneuver.target_ground;
        }
        if (Config.isUSE_RENDER()) {
            this.fl1 = Eff3DActor.New(this, this.findHook("_NavLightR"), null, 1.0F, "3DO/Effects/Fireworks/FlareBlue1.eff", -1F);
            this.fl2 = Eff3DActor.New(this, this.findHook("_NavLightG"), null, 1.0F, "3DO/Effects/Fireworks/FlareBlue1.eff", -1F);
            if (this.flame != null) {
                this.flame.drawing(false);
            }
            if (this.fl1 != null) {
                Eff3DActor.setIntesity(this.fl1, 10F);
            }
            com.maddox.il2.engine.Hook hook = null;
            hook = this.findHook("_SMOKE01");
            this.smoke01 = Eff3DActor.New(this, hook, null, 1.0F, "Effects/Smokes/SmokeBlack_planeTrail.eff", -1F);
            if (this.smoke01 != null) {
                this.smoke01.pos.changeHookToRel();
            }
        }
        this.noGDelay = -1L;
        this.tEStart = Time.current() + 1000L;
        if (Config.isUSE_RENDER()) {
            if (this.smoke != null) {
                Eff3DActor.setIntesity(this.smoke, 0.0F);
            }
            if (this.sprite != null) {
                Eff3DActor.setIntesity(this.sprite, 0.0F);
            }
            if (this.flame != null) {
                this.flame.drawing(false);
            }
            this.light.light.setEmit(0.0F, 0.0F);
        }
        this.pos.getAbs(RocketIgo_3_A.p, RocketIgo_3_A.or);
        RocketIgo_3_A.or.setYPR(RocketIgo_3_A.or.getYaw(), RocketIgo_3_A.or.getPitch(), 0.0F);
        this.pos.setAbs(RocketIgo_3_A.p, RocketIgo_3_A.or);
    }

    public void destroy() {
        if (this.isNet() && this.isNetMirror()) {
            this.doExplosionAir();
        }
        if (Config.isUSE_RENDER()) {
            Eff3DActor.finish(this.fl1);
            Eff3DActor.finish(this.fl2);
            if (this.smoke01 != null) {
                Eff3DActor.finish(this.smoke01);
            }
        }
        super.destroy();
    }

    protected void doExplosionAir() {
        this.pos.getTime(Time.current(), RocketIgo_3_A.p);
        MsgExplosion.send(null, null, RocketIgo_3_A.p, this.getOwner(), 45F, 2.0F, 1, 550F);
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

    protected void mydebug(String s) {
    }

    float smoothAdjust(float oldValue, float newValue, int smoothFactor) {
        return ((oldValue * (smoothFactor - 1F)) + newValue) / smoothFactor;
    }

    float             deltaAzimuth                 = 0.0F;
    float             deltaTangage                 = 0.0F;
    static int        SMOOTH_FACTOR                = 20;
    private FlightModel     fm;
    private Maneuver        maneuver;
    private Actor           target;
    private Eff3DActor      fl1;
    private Eff3DActor      fl2;
    private Eff3DActor      smoke01;
    private static Orient   or                      = new Orient();
    private static Point3d  p                       = new Point3d();
    private static Point3d  pT                      = new Point3d();
    private static Vector3d v                       = new Vector3d();
    private long            tEStart;
    private static double   azimuthControlScaleFact = 1.0D;
    private static double   tangageControlScaleFact = 1.0D;
    private boolean         first;

    static {
        Class class1 = RocketIgo_3_A.class;
        Property.set(class1, "mesh", "3do/Arms/Igo-3-A/mono.sim");
        Property.set(class1, "smoke", "3DO/Effects/Rocket/rocketsmokewhite.eff");
        Property.set(class1, "smokeStart", "3do/effects/rocket/rocketsmokewhitestart.eff");
        Property.set(class1, "smokeTile", "3do/effects/rocket/rocketsmokewhitetile.eff");
        Property.set(class1, "emitColor", new Color3f(1.0F, 1.0F, 0.5F));
        Property.set(class1, "emitLen", 50F);
        Property.set(class1, "emitMax", 0.5F);
        Property.set(class1, "sound", "weapon.bomb_std");
        Property.set(class1, "radius", 100F);
        Property.set(class1, "timeLife", 1000F);
        Property.set(class1, "timeFire", 12F);
        Property.set(class1, "force", 0.0F);
        Property.set(class1, "power", 800F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 1.1F);
        Property.set(class1, "massa", 1400F);
        Property.set(class1, "massaEnd", 1200F);
        Spawn.add(class1, new SPAWN());
    }

}
