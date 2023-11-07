package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.NearestTargets;
import com.maddox.il2.engine.*;
import com.maddox.il2.objects.ships.WeakBody;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class Bomb4512homing extends Bomb4512
{

    public Bomb4512homing()
    {
        deltaAzimuth = 0.0F;
        victim = null;
        ship = null;
    }

    public void start()
    {
        super.start();
        Class class1 = getClass();
        started = Time.current();
        velocity = Property.floatValue(class1, "velocity", 1.0F);
        travelTime = (long)Property.floatValue(class1, "traveltime", 1.0F) * 1000L;
    }

    private void sendexplosion()
    {
        MsgCollision.post(Time.current(), this, Other, (String)null, OtherChunk);
    }

    public void interpolateTick()
    {
        float f = Time.tickLenFs();
        pos.getAbs(P, Or);
        if(!flow)
        {
            Ballistics.update(this, M, S);
        } else
        {
            float f1 = (float)getSpeed((Vector3d)null);
            f1 += (velocity - f1) * 0.99F * f;
            spd.set(1.0D, 0.0D, 0.0D);
            Or.transform(spd);
            spd.scale(f1);
            setSpeed(spd);
            P.x += spd.x * (double)f;
            P.y += spd.y * (double)f;
            if(isNet() && isNetMirror())
            {
                pos.setAbs(P, Or);
                updateSound();
                return;
            }
            if(Actor.isValid(victim))
            {
                victim.pos.getAbs(pT);
                pT.sub(P);
                Or.transformInv(pT);
                if(pT.y > 1.0D)
                    deltaAzimuth = -1F;
                if(pT.y < -1D)
                    deltaAzimuth = 1.0F;
                Or.increment(5F * f * deltaAzimuth, 0.0F, 0.0F);
                deltaAzimuth = 0.0F;
                ship = NearestTargets.getEnemy(6, -1, P, 550D, 0);
                if(Actor.isValid(ship) && !(ship instanceof WeakBody) && P.distance(victim.pos.getAbsPoint()) > P.distance(ship.pos.getAbsPoint()))
                    victim = ship;
            } else
            {
                victim = NearestTargets.getEnemy(6, -1, P, 550D, 0);
                if(!Actor.isValid(victim) || (victim instanceof WeakBody))
                    victim = null;
            }
            pos.setAbs(P, Or);
            if(Time.current() > started + travelTime || !World.land().isWater(P.x, P.y))
                sendexplosion();
        }
        updateSound();
    }

    private float velocity;
    private long travelTime;
    private long started;
    private static Point3d pT = new Point3d();
    private float deltaAzimuth;
    private Actor victim;
    private Actor ship;

    static 
    {
        Class class1 = Bomb4512homing.class;
        Property.set(class1, "velocity", 13F);
        Property.set(class1, "traveltime", 810.6F);
    }
}
