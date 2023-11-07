package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.MsgExplosion;
import com.maddox.il2.ai.MsgShot;
import com.maddox.il2.ai.RangeRandom;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorMesh;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.Interpolate;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.il2.engine.LightPointWorld;
import com.maddox.il2.engine.MeshShared;
import com.maddox.il2.engine.MsgCollisionListener;
import com.maddox.il2.engine.MsgCollisionRequestListener;
import com.maddox.il2.game.Mission;
import com.maddox.il2.net.Chat;
import com.maddox.il2.objects.ActorLand;
import com.maddox.il2.objects.ActorSimpleMesh;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.effects.Explosions;
import com.maddox.rts.Message;
import com.maddox.rts.ObjState;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class Rocket extends ActorMesh implements MsgCollisionRequestListener, MsgCollisionListener {
    class Interpolater extends Interpolate {

        public boolean tick() {
            if ((this.timeBegin + Rocket.this.timeLife) < Time.current()) {
                Rocket.this.doExplosionAir();
                Rocket.this.postDestroy();
                Rocket.this.collide(false);
                Rocket.this.drawing(false);
                return false;
            }
            if ((this.timeBegin + Rocket.this.timeFire) < Time.current()) {
                Rocket.this.endSmoke();
                Rocket.this.P = 0.0F;
            } else {
                Rocket.this.M -= Rocket.this.DM;
            }
            if (Rocket.this.interpolateStep()) {
                Ballistics.update(this.actor, Rocket.this.M, Rocket.this.S, Rocket.this.P, (this.timeBegin + Rocket.this.noGDelay) < Time.current());
            }
            return true;
        }

        Interpolater() {
        }
    }

    public void msgCollisionRequest(Actor actor, boolean aflag[]) {
        if (actor == this.getOwner()) {
            aflag[0] = false;
        }
    }

    public void msgCollision(Actor actor, String s, String s1) {
        if ((this.getOwner() == World.getPlayerAircraft()) && !(actor instanceof ActorLand)) {
            World.cur().scoreCounter.rocketsHit++;
            if (Mission.isNet() && (actor instanceof Aircraft) && ((Aircraft) actor).isNetPlayer()) {
                Chat.sendLogRnd(3, "gore_rocketed", (Aircraft) this.getOwner(), (Aircraft) actor);
            }
        }
        this.doExplosion(actor, s1);
    }

    protected void doExplosion(Actor actor, String s) {
        this.pos.getTime(Time.current(), Rocket.p);
        Class class1 = this.getClass();
        float f = Property.floatValue(class1, "power", 1000F);
        int i = Property.intValue(class1, "powerType", 0);
        float f1 = Property.floatValue(class1, "radius", 0.0F);
        this.getSpeed(this.speed);
        Vector3f vector3f = new Vector3f(this.speed);
        if (f1 <= 0.0F) {
            MsgShot.send(actor, s, Rocket.p, vector3f, this.M, this.getOwner(), f, 1, 0.0D);
        } else {
            MsgShot.send(actor, s, Rocket.p, vector3f, this.M, this.getOwner(), (float) (0.5F * this.M * this.speed.lengthSquared()), 0, 0.0D);
            MsgExplosion.send(actor, s, Rocket.p, this.getOwner(), this.M, f, i, f1);
        }
        Explosions.generateRocket(actor, Rocket.p, f, i, f1);
        this.destroy();
    }

    protected void doExplosionAir() {
        this.pos.getTime(Time.current(), Rocket.p);
        Class class1 = this.getClass();
        float f = Property.floatValue(class1, "power", 1000F);
        int i = Property.intValue(class1, "powerType", 0);
        float f1 = Property.floatValue(class1, "radius", 150F);
        MsgExplosion.send(null, null, Rocket.p, this.getOwner(), this.M, f, i, f1);
        Explosions.AirFlak(Rocket.p, 0);
    }

    public boolean interpolateStep() {
        return true;
    }

    protected void endSmoke() {
        if (this.endedSmoke) {
            return;
        }
        this.endedSmoke = true;
        if (this.light != null) {
            this.light.light.setEmit(0.0F, 1.0F);
        }
        Eff3DActor.finish(this.smoke);
        Eff3DActor.finish(this.sprite);
        ObjState.destroy(this.flame);
        this.stopSounds();
    }

    public void destroy() {
        this.endSmoke();
        super.destroy();
        this.smoke = null;
        this.sprite = null;
        this.flame = null;
        this.light = null;
        this.soundName = null;
    }

    protected void setThrust(float f) {
        this.P = f;
    }

    public double getSpeed(Vector3d vector3d) {
        if (vector3d != null) {
            vector3d.set(this.speed);
        }
        return this.speed.length();
    }

    public void setSpeed(Vector3d vector3d) {
        this.speed.set(vector3d);
    }

    protected void init(float f, float f1, float f2, float f3, float f4, float f5) {
        if (Actor.isValid(this.getOwner()) && (World.getPlayerAircraft() == this.getOwner())) {
            this.setName("_rocket_");
        }
        super.getSpeed(this.speed);
        if (World.cur().diffCur.Wind_N_Turbulence) {
            Point3d point3d = new Point3d();
            Vector3d vector3d = new Vector3d();
            this.pos.getAbs(point3d);
            World.wind().getVectorWeapon(point3d, vector3d);
            this.speed.add(-vector3d.x, -vector3d.y, 0.0D);
        }
        this.S = (float) ((3.1415926535897931D * f * f) / 4D);
        this.M = f1;
        if (f3 > 0.0F) {
            this.DM = (f1 - f2) / (f3 / Time.tickConstLenFs());
        } else {
            this.DM = 0.0F;
        }
        this.P = f4;
        this.timeFire = (long) ((f3 * 1000F) + 0.5D);
        this.timeLife = (long) ((f5 * 1000F) + 0.5D);
    }

    public void start(float f) {
        this.start(f, 0);
    }

    public boolean isRocket() {
        if (this instanceof RocketNull) return false;
        if (Property.floatValue(this.getClass(), "power", 0F) < 0.1F) return false;
        return true;
    }

    public void start(float f, int i) {
        Class class1 = this.getClass();
        float f1 = Property.floatValue(class1, "kalibr", 0.082F);
        if (f <= 0.0F) {
            f = Property.floatValue(class1, "timeLife", 45F);
        }
        RangeRandom rangerandom = new RangeRandom(i);
        float f2 = -1F + (2.0F * rangerandom.nextFloat());
        f2 *= f2 * f2;
        float f3 = -1F + (2.0F * rangerandom.nextFloat());
        f3 *= f3 * f3;
        this.init(f1, Property.floatValue(class1, "massa", 6.8F), Property.floatValue(class1, "massaEnd", 2.52F), Property.floatValue(class1, "timeFire", 4F) / (1.0F + (0.1F * f2)), Property.floatValue(class1, "force", 500F) * (1.0F + (0.1F * f2)), f + (f3 * 0.1F));
        this.setOwner(this.pos.base(), false, false, false);
        this.pos.setBase(null, null, true);
        this.pos.setAbs(this.pos.getCurrent());
        this.pos.getAbs(Aircraft.tmpOr);
        float f4 = 0.68F * Property.floatValue(class1, "maxDeltaAngle", 3F);
        f2 = -1F + (2.0F * rangerandom.nextFloat());
        f3 = -1F + (2.0F * rangerandom.nextFloat());
        f2 *= f2 * f2 * f4;
        f3 *= f3 * f3 * f4;
        Aircraft.tmpOr.increment(f2, f3, 0.0F);
        this.pos.setAbs(Aircraft.tmpOr);
        this.pos.getRelOrient().transformInv(this.speed);
        this.speed.z /= 3D;
        this.speed.x += 200D;
        this.pos.getRelOrient().transform(this.speed);
        this.collide(true);
        this.interpPut(new Interpolater(), null, Time.current(), null);
        if (this.getOwner() == World.getPlayerAircraft() && this.isRocket())
            World.cur().scoreCounter.rocketsFire++;
        if (!Config.isUSE_RENDER()) return;
        Hook hook = null;
        String s = Property.stringValue(class1, "sprite", null);
        if (s != null) {
            if (hook == null) {
                hook = this.findHook("_SMOKE");
            }
            this.sprite = Eff3DActor.New(this, hook, null, f1, s, -1F);
            if (this.sprite != null) {
                this.sprite.pos.changeHookToRel();
            }
        }
        s = Property.stringValue(class1, "flame", null);
        if (s != null) {
            if (hook == null) {
                hook = this.findHook("_SMOKE");
            }
            this.flame = new ActorSimpleMesh(s);
            if (this.flame != null) {
                ((ActorSimpleMesh) this.flame).mesh().setScale(f1);
                this.flame.pos.setBase(this, hook, false);
                this.flame.pos.changeHookToRel();
                this.flame.pos.resetAsBase();
            }
        }
        s = Property.stringValue(class1, "smoke", null);
        if (s != null) {
            if (hook == null) {
                hook = this.findHook("_SMOKE");
            }
            this.smoke = Eff3DActor.New(this, hook, null, 1.0F, s, -1F);
            if (this.smoke != null) {
                this.smoke.pos.changeHookToRel();
            }
        }
        this.light = new LightPointActor(new LightPointWorld(), new Point3d());
        this.light.light.setColor((Color3f) Property.value(class1, "emitColor", new Color3f(1.0F, 1.0F, 0.5F)));
        this.light.light.setEmit(Property.floatValue(class1, "emitMax", 1.0F), Property.floatValue(class1, "emitLen", 50F));
        this.draw.lightMap().put("light", this.light);
        this.soundName = Property.stringValue(class1, "sound", null);
        if (this.soundName != null) {
            this.newSound(this.soundName, true);
        }
    }

    public Object getSwitchListener(Message message) {
        return this;
    }

    public Rocket() {
        this.noGDelay = 1000L;
        this.endedSmoke = false;
        this.speed = new Vector3d();
        this.noGDelay = 0L;
        this.endedSmoke = false;
        this.setMesh(MeshShared.get(Property.stringValue(this.getClass(), "mesh", null)));
        this.flags |= 0xe0;
        this.collide(false);
        this.drawing(true);
    }

    protected long            noGDelay;
    private static Point3d    p = new Point3d();
    private boolean           endedSmoke;
    protected Eff3DActor      smoke;
    protected Eff3DActor      sprite;
    protected Actor           flame;
    protected LightPointActor light;
    protected String          soundName;
    protected long            timeFire;
    protected long            timeLife;
    protected Vector3d        speed;
    private float             S;
    protected float           M;
    private float             DM;
    private float             P;
}
