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
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class TorpedoLtfFiume extends Bomb {
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
            if (World.land().isWater(TorpedoLtfFiume.P.x, TorpedoLtfFiume.P.y)) {
                return;
            }
        }
        if ((this.impact < 5700L) || ((randf >= 53.2D) && (randf <= 53.9D))) {
            this.collide(false);
            return;
        }
        if ((randf >= 12.5D) && (randf <= 21.87D)) {
            this.destroy();
        } else {
            this.doExplosion(actor, string_0_);
        }
    }

    private void surface() {
        Class var_class = this.getClass();
        double randi = Math.random() * 100D;
        this.travelTime = (long) Property.floatValue(var_class, "traveltime", 1.0F) * 1000L;
        this.pos.getAbs(TorpedoLtfFiume.P, TorpedoLtfFiume.Or);
        this.flow = true;
        this.getSpeed(TorpedoLtfFiume.spd);
        if (World.land().isWater(TorpedoLtfFiume.P.x, TorpedoLtfFiume.P.y)) {
            if (TorpedoLtfFiume.spd.z < -0.12D) {
                Explosions.RS82_Water(TorpedoLtfFiume.P, 4F, 1.0F);
            }
            double d = TorpedoLtfFiume.spd.length();
            if (d > 0.001D) {
                d = TorpedoLtfFiume.spd.z / TorpedoLtfFiume.spd.length();
            } else {
                d = -1D;
            }
            if ((d > -0.57D) && (randi <= 1.03D)) {
                this.velocity = 0.5F;
            }
            if ((d > -0.57D) && (randi >= 38.1D) && (randi <= 41D)) {
                this.velocity = 0.02F;
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
        TorpedoLtfFiume.spd.z = 0.0D;
        this.setSpeed(TorpedoLtfFiume.spd);
        TorpedoLtfFiume.P.z = 0.0D;
        float fs[] = new float[3];
        TorpedoLtfFiume.Or.getYPR(fs);
        TorpedoLtfFiume.Or.setYPR(fs[0], 0.0F, fs[2]);
        this.pos.setAbs(TorpedoLtfFiume.P, TorpedoLtfFiume.Or);
        this.flags &= 0xffffffbf;
        this.drawing(false);
        Eff3DActor.New(this, null, null, 1.0F, "3DO/Effects/Tracers/533mmTorpedo/Line.eff", -1F);
        Eff3DActor.New(this, null, null, 1.0F, "3DO/Effects/Tracers/533mmTorpedo/wave.eff", -1F);
    }

    public void interpolateTick() {
        float f = Time.tickLenFs();
        this.pos.getAbs(TorpedoLtfFiume.P);
        if (TorpedoLtfFiume.P.z <= -0.1D) {
            this.surface();
        }
        if (!this.flow) {
            Ballistics.update(this, this.M, this.S);
        } else {
            this.getSpeed(TorpedoLtfFiume.spd);
            if (TorpedoLtfFiume.spd.length() > this.velocity) {
                TorpedoLtfFiume.spd.scale(0.99D);
            } else if (TorpedoLtfFiume.spd.length() < this.velocity) {
                TorpedoLtfFiume.spd.scale(1.01D);
            }
            this.setSpeed(TorpedoLtfFiume.spd);
            this.pos.getAbs(TorpedoLtfFiume.P);
            TorpedoLtfFiume.P.x += TorpedoLtfFiume.spd.x * f;
            TorpedoLtfFiume.P.y += TorpedoLtfFiume.spd.y * f;
            this.pos.setAbs(TorpedoLtfFiume.P);
            if ((Time.current() > (this.started + this.travelTime)) || !World.land().isWater(TorpedoLtfFiume.P.x, TorpedoLtfFiume.P.y)) {
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
        this.getSpeed(TorpedoLtfFiume.spd);
        this.pos.getAbs(TorpedoLtfFiume.P, TorpedoLtfFiume.Or);
        Vector3d vector3d = new Vector3d(Property.floatValue(var_class, "startingspeed", 0.0F), 0.0D, 0.0D);
        TorpedoLtfFiume.Or.transform(vector3d);
        TorpedoLtfFiume.spd.add(vector3d);
        this.setSpeed(TorpedoLtfFiume.spd);
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

    Actor           Other;
    String          OtherChunk;
    String          ThisChunk;
    boolean         flow;
    private float   velocity;
    private long    travelTime;
    private long    started;
    private long    impact;
    static Vector3d spd = new Vector3d();
    static Orient   Or  = new Orient();
    static Point3d  P   = new Point3d();

}
