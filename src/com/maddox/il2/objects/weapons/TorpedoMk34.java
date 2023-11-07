package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.il2.engine.LightPointWorld;
import com.maddox.il2.engine.MsgCollision;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.objects.ActorLand;
import com.maddox.il2.objects.effects.Explosions;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.ShipGeneric;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class TorpedoMk34 extends Bomb {

    public TorpedoMk34() {
        this.victim = null;
        this.torpedoUtils = null;
        this.pT = null;
        this.deltaAzimuth = 0.0F;
        this.v = null;
        this.torpedoUtils = new TorpedoAcusticUtils(this);
        this.pT = new Point3d();
        this.v = new Vector3d();
    }

    private void getTorpedoTarget() {
        Actor theTarget = this.torpedoUtils.lookTorpedo(this, 45F, 12000D);
        if ((theTarget instanceof ShipGeneric) || (theTarget instanceof BigshipGeneric)) {
            this.victim = theTarget;
        } else {
            this.victim = null;
        }
    }

    public void msgCollision(Actor actor, String string, String string_0_) {
        double randf = Math.random() * 100D;
        this.impact = Time.current() - this.started;
        this.Other = actor;
        this.OtherChunk = string_0_;
        if (actor instanceof ActorLand) {
            if (this.flow) {
                this.doExplosion(actor, string_0_);
                return;
            }
            this.surface();
            if (World.land().isWater(TorpedoMk34.P.x, TorpedoMk34.P.y)) {
                return;
            }
        }
        if ((this.impact < 6200L) || ((randf >= 53.2D) && (randf < 55.6D))) {
            this.collide(false);
            return;
        }
        if ((randf >= 12.5D) && (randf < 19.8D)) {
            this.destroy();
        } else {
            this.doExplosion(actor, string_0_);
        }
    }

    private void surface() {
        if (this.victim == null) {
            this.getTorpedoTarget();
        }
        Class var_class = this.getClass();
        double randi = Math.random() * 100D;
        this.travelTime = (long) Property.floatValue(var_class, "traveltime", 1.0F) * 1000L;
        this.pos.getAbs(TorpedoMk34.P, TorpedoMk34.Or);
        this.flow = true;
        this.getSpeed(TorpedoMk34.spd);
        if (World.land().isWater(TorpedoMk34.P.x, TorpedoMk34.P.y)) {
            if (TorpedoMk34.spd.z < -0.12D) {
                Explosions.RS82_Water(TorpedoMk34.P, 4F, 1.0F);
            }
            double d = TorpedoMk34.spd.length();
            if (d > 0.001D) {
                d = TorpedoMk34.spd.z / TorpedoMk34.spd.length();
            } else {
                d = -1D;
            }
            if ((d > -0.57D) && (randi >= 73D) && (randi <= 75.7D)) {
                this.travelTime = 18400L;
            }
            if ((d > -0.57D) && (randi <= 1.7D)) {
                this.velocity = 0.02F;
                this.destroy();
            }
            if ((d > -0.57D) && (randi >= 38.1D) && (randi <= 42.3D)) {
                this.destroy();
            }
            if (d < -0.57D) {
                this.velocity = 0.02F;
                this.destroy();
            }
        }
        if (this.isDestroyed()) {
            return;
        }
        TorpedoMk34.spd.z = 0.0D;
        this.setSpeed(TorpedoMk34.spd);
        TorpedoMk34.P.z = 0.0D;
        float fs[] = new float[3];
        TorpedoMk34.Or.getYPR(fs);
        TorpedoMk34.Or.setYPR(fs[0], 0.0F, fs[2]);
        this.pos.setAbs(TorpedoMk34.P, TorpedoMk34.Or);
        this.flags &= 0xffffffbf;
        this.drawing(false);
        Eff3DActor.New(this, null, null, 1.0F, "3DO/Effects/Tracers/533mmTorpedo/Line.eff", -1F);
        Eff3DActor.New(this, null, null, 1.0F, "3DO/Effects/Tracers/533mmTorpedo/wave.eff", -1F);
    }

    public void interpolateTick() {
        float f = Time.tickLenFs();
        this.pos.getAbs(TorpedoMk34.P, TorpedoMk34.or);
        if (TorpedoMk34.P.z <= -0.1D) {
            this.surface();
        }
        if (!this.flow) {
            Ballistics.update(this, this.M, this.S);
        } else {
            float f1 = (float) this.getSpeed(null);
            this.getSpeed(TorpedoMk34.spd);
            if (TorpedoMk34.spd.length() > this.velocity) {
                f1 *= 0.99F;
            } else if (TorpedoMk34.spd.length() < this.velocity) {
                f1 *= 1.01F;
            }
            this.pos.getAbs(TorpedoMk34.P, TorpedoMk34.or);
            this.v.set(1.0D, 0.0D, 0.0D);
            TorpedoMk34.or.transform(this.v);
            this.v.scale(f1);
            this.setSpeed(this.v);
            TorpedoMk34.P.x += this.v.x * f;
            TorpedoMk34.P.y += this.v.y * f;
            if (this.victim != null) {
                this.pT = this.victim.pos.getAbsPoint();
                this.pT.sub(TorpedoMk34.P);
                TorpedoMk34.or.transformInv(this.pT);
                double angleAzimuth = Math.toDegrees(Math.atan(this.pT.y / this.pT.x));
                this.deltaAzimuth = 0.0F;
                if (angleAzimuth > 2D) {
                    this.deltaAzimuth = -1F;
                }
                if (angleAzimuth < -2D) {
                    this.deltaAzimuth = 1.0F;
                }
                this.deltaAzimuth *= 5F * f;
                TorpedoMk34.or.increment(this.deltaAzimuth, 0.0F, 0.0F);
                TorpedoMk34.or.setYPR(TorpedoMk34.or.getYaw(), 0.0F, 0.0F);
            }
            this.pos.setAbs(TorpedoMk34.P, TorpedoMk34.or);
            if ((Time.current() > (this.started + this.travelTime)) || !World.land().isWater(TorpedoMk34.P.x, TorpedoMk34.P.y)) {
                this.sendexplosion();
            }
        }
        this.updateSound();
    }

    private void sendexplosion() {
        MsgCollision.post(Time.current(), this, this.Other, null, this.OtherChunk);
    }

    public void start() {
        Class var_class = this.getClass();
        this.init(Property.floatValue(var_class, "kalibr", 1.0F), Property.floatValue(var_class, "massa", 1.0F));
        this.started = Time.current();
        this.velocity = Property.floatValue(var_class, "velocity", 1.0F);
        this.travelTime = (long) Property.floatValue(var_class, "traveltime", 1.0F) * 1000L;
        this.setOwner(this.pos.base(), false, false, false);
        this.pos.setBase(null, null, true);
        this.pos.setAbs(this.pos.getCurrent());
        this.getSpeed(TorpedoMk34.spd);
        this.pos.getAbs(TorpedoMk34.P, TorpedoMk34.Or);
        Vector3d vector3d = new Vector3d(Property.floatValue(var_class, "startingspeed", 0.0F), 0.0D, 0.0D);
        TorpedoMk34.Or.transform(vector3d);
        TorpedoMk34.spd.add(vector3d);
        this.setSpeed(TorpedoMk34.spd);
        this.collide(true);
        this.interpPut(new Bomb.Interpolater(), null, Time.current(), null);
        this.drawing(true);
        if (Property.containsValue(var_class, "emitColor")) {
            LightPointActor lightpointactor = new LightPointActor(new LightPointWorld(), new Point3d());
            lightpointactor.light.setColor((Color3f) Property.value(var_class, "emitColor", new Color3f(1.0F, 1.0F, 0.5F)));
            lightpointactor.light.setEmit(Property.floatValue(var_class, "emitMax", 1.0F), Property.floatValue(var_class, "emitLen", 50F));
            this.draw.lightMap().put("light", lightpointactor);
        }
        this.sound = this.newSound(Property.stringValue(var_class, "sound", null), false);
        if (this.sound != null) {
            this.sound.play();
        }
    }

    Actor                       Other;
    String                      OtherChunk;
    String                      ThisChunk;
    boolean                     flow;
    private float               velocity;
    private long                travelTime;
    private long                started;
    private long                impact;
    static Vector3d             spd = new Vector3d();
    static Orient               Or  = new Orient();
    static Point3d              P   = new Point3d();
    private Actor               victim;
    private TorpedoAcusticUtils torpedoUtils;
    private Point3d             pT;
    private static Orient       or  = new Orient();
    private float               deltaAzimuth;
    private Vector3d            v;

}
