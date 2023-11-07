package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.NearestTargets;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.MsgCollision;
import com.maddox.il2.objects.ActorLand;
import com.maddox.il2.objects.effects.Explosions;
import com.maddox.il2.objects.ships.WeakBody;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class BombTorpRAT57N extends Torpedo
{

    public BombTorpRAT57N()
    {
        deltaAzimuth = 0.0F;
        victim = null;
        ship = null;
        booster = 0;
        boosterFireOutTime = -1L;
    }

    public void start()
    {
        super.start();
        Class class1 = getClass();
        started = Time.current();
        DelayRAT = Time.current() + 800L + World.Rnd().nextLong(0L, 850L);
        velocity = Property.floatValue(class1, "velocity", 1.0F);
        travelTime = (long)Property.floatValue(class1, "traveltime", 1.0F) * 1000L;
    }

    public void msgCollision(Actor actor, String s, String s1)
    {
        double d = Math.random() * 100D;
        impact = Time.current() - started;
        Other = actor;
        OtherChunk = s1;
        if(actor instanceof ActorLand)
        {
            if(flow)
            {
                doExplosion(actor, s1);
                return;
            }
            surface();
            if(World.land().isWater(Torpedo.P.x, Torpedo.P.y))
                return;
        }
        if(impact < 6200L)
        {
            collide(false);
            return;
        }
        if(d >= 0.5D && d < 4.9D)
            destroy();
        else
            doExplosion(actor, s1);
    }

    private void surface()
    {
        Class class1 = getClass();
        double d = Math.random() * 100D;
        travelTime = (long)Property.floatValue(class1, "traveltime", 1.0F) * 1000L;
        this.pos.getAbs(Torpedo.P, Torpedo.Or);
        flow = true;
        getSpeed(Torpedo.spd);
        if(World.land().isWater(Torpedo.P.x, Torpedo.P.y))
        {
            if(Torpedo.spd.z < -0.12D)
                Explosions.RS82_Water(Torpedo.P, 4F, 1.0F);
            double d1 = Torpedo.spd.length();
            if(d1 > 0.001D)
                d1 = Torpedo.spd.z / Torpedo.spd.length();
            else
                d1 = -1D;
            if(d1 > -0.57D && d >= 73D && d <= 75.7D)
                travelTime = 18400L;
            if(d1 > -0.57D && d <= 1.7D)
            {
                velocity = 0.02F;
                destroy();
            }
            if(d1 > -0.57D && d >= 38.1D && d <= 42.3D)
                destroy();
            if(d1 < -0.57D)
            {
                velocity = 0.02F;
                destroy();
            }
        }
        Torpedo.spd.z = 0.0D;
        setSpeed(Torpedo.spd);
        Torpedo.P.z = 0.0D;
        float af[] = new float[3];
        Torpedo.Or.getYPR(af);
        Torpedo.Or.setYPR(af[0], 0.0F, af[2]);
        this.pos.setAbs(Torpedo.P, Torpedo.Or);
        this.flags &= 0xffffffbf;
        drawing(false);
        Eff3DActor.New(this, null, null, 1.0F, "3do/Effects/Tracers/533mmTorpedo/Line.eff", -1F);
        Eff3DActor.New(this, null, null, 1.0F, "3do/Effects/Tracers/533mmTorpedo/wave.eff", -1F);
        if(booster == 1)
        {
            booster = 2;
            Eff3DActor.finish(Flame);
            Eff3DActor.finish(Smoke);
        }
    }

    private void sendexplosion()
    {
        MsgCollision.post(Time.current(), this, Other, (String)null, OtherChunk);
    }

    public void interpolateTick()
    {
        if(booster == 0 && Time.current() > DelayRAT)
        {
            booster = 1;
            boosterFireOutTime = Time.current() + 4000L;
            Flame = Eff3DActor.New(this, findHook("_Flame"), null, 1.0F, "3do/Effects/P85/P85_Rato.eff", -1F);
            Smoke = Eff3DActor.New(this, findHook("_Smoke"), null, 1.0F, "3do/Effects/P85/P85_RocketSmoke.eff", -1F);
        }
        if(booster == 1 && Time.current() > boosterFireOutTime)
        {
            booster = 2;
            Eff3DActor.finish(Flame);
            Eff3DActor.finish(Smoke);
        }
        float f = Time.tickLenFs();
        pos.getAbs(Torpedo.P, Torpedo.Or);
        if(!flow)
        {
            Ballistics.update(this, M, S);
        } else
        {
            float f1 = (float)getSpeed((Vector3d)null);
            f1 += (velocity - f1) * 0.99F * f;
            Torpedo.spd.set(1.0D, 0.0D, 0.0D);
            Torpedo.Or.transform(Torpedo.spd);
            Torpedo.spd.scale(f1);
            setSpeed(Torpedo.spd);
            Torpedo.P.x += Torpedo.spd.x * (double)f;
            Torpedo.P.y += Torpedo.spd.y * (double)f;
            if(isNet() && isNetMirror())
            {
                pos.setAbs(Torpedo.P, Torpedo.Or);
                updateSound();
                return;
            }
            if(Actor.isValid(victim))
            {
                victim.pos.getAbs(pT);
                pT.sub(Torpedo.P);
                Torpedo.Or.transformInv(pT);
                if(pT.y > 1.0D)
                    deltaAzimuth = -1F;
                if(pT.y < -1D)
                    deltaAzimuth = 1.0F;
                Torpedo.Or.increment(5F * f * deltaAzimuth, 0.0F, 0.0F);
                deltaAzimuth = 0.0F;
                ship = NearestTargets.getEnemy(6, -1, Torpedo.P, 550D, 0);
                if(Actor.isValid(ship) && !(ship instanceof WeakBody) && Torpedo.P.distance(victim.pos.getAbsPoint()) > Torpedo.P.distance(ship.pos.getAbsPoint()))
                    victim = ship;
            } else
            {
                victim = NearestTargets.getEnemy(6, -1, Torpedo.P, 550D, 0);
                if(!Actor.isValid(victim) || (victim instanceof WeakBody))
                    victim = null;
            }
            pos.setAbs(Torpedo.P, Torpedo.Or);
            if(Time.current() > started + travelTime || !World.land().isWater(Torpedo.P.x, Torpedo.P.y))
                sendexplosion();
        }
        updateSound();
    }

    private float velocity;
    private long travelTime;
    private long started;
    private long impact;
    private static Point3d pT = new Point3d();
    private float deltaAzimuth;
    private Actor victim;
    private Actor ship;
    private long DelayRAT;
    protected long boosterFireOutTime;
    private int booster;
    private Eff3DActor Flame;
    private Eff3DActor Smoke;

    static 
    {
        Class class1 = BombTorpRAT57N.class;
        Property.set(class1, "mesh", "3do/Arms/P85-RAT57N/mono.sim");
        Property.set(class1, "radius", 80F);
        Property.set(class1, "power", 239F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.455F);
        Property.set(class1, "massa", 627F);
        Property.set(class1, "sound", "weapon.torpedo");
        Property.set(class1, "velocity", 21F);
        Property.set(class1, "traveltime", 400F);
        Property.set(class1, "startingspeed", 0.0F);
    }
}
