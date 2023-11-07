package com.maddox.il2.ai.ground;

import java.util.List;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.VisCheck;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Accumulator;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.Paratrooper;
import com.maddox.il2.objects.ships.Ship.RwyTransp;
import com.maddox.il2.objects.ships.Ship.RwyTranspSqr;
import com.maddox.il2.objects.ships.Ship.RwyTranspWide;
import com.maddox.il2.objects.ships.TransparentTestRunway;
import com.maddox.il2.objects.vehicles.planes.Plane.GenericSpawnPointPlane;

public class NearestEnemies
    implements Accumulator
{

    public NearestEnemies()
    {
    }

    public static NearestEnemies enemies()
    {
        return enemies;
    }

    public static void set(int i)
    {
        set(i, 0.0F, 0.0F);
        useSpeed = false;
    }

    public static void set(int i, float f, float f1)
    {
        enemies.clear();
        usedWeaponsMask = i;
        minSpeed = f;
        maxSpeed = f1;
        useSpeed = true;
    }

    public static void resetGameClear()
    {
        for(int i = 0; i < nearAct.length; i++)
            nearAct[i] = null;

    }

    public void clear()
    {
        nearNUsed = 0;
    }

    public boolean add(Actor actor, double d)
    {
        if(!(actor instanceof Prey) || (((Prey)actor).HitbyMask() & usedWeaponsMask) == 0)
            return true;
        if(useSpeed)
        {
            float f = (float)actor.getSpeed(null);
            if(f < minSpeed || f > maxSpeed)
                return true;
        }
        int i;
        for(i = nearNUsed - 1; i >= 0 && d < nearDSq[i]; i--);
        if(++i >= nearNUsed)
        {
            if(nearNUsed < MAX_OBJECTS)
            {
                nearAct[nearNUsed] = actor;
                nearDSq[nearNUsed] = d;
                nearNUsed++;
            }
        } else
        {
            int j;
            if(nearNUsed < MAX_OBJECTS)
            {
                j = nearNUsed - 1;
                nearNUsed++;
            } else
            {
                j = nearNUsed - 2;
            }
            for(; j >= i; j--)
            {
                nearAct[j + 1] = nearAct[j];
                nearDSq[j + 1] = nearDSq[j];
            }

            nearAct[i] = actor;
            nearDSq[i] = d;
        }
        return true;
    }

    public static Actor getAFoundEnemy()
    {
        if(nearNUsed <= 0)
            return null;
        else
            return nearAct[nearNUsed != 1 ? World.Rnd().nextInt(nearNUsed) : 0];
    }

    public static Actor getAFoundEnemy(Point3d point3d, double d, int i)
    {
        return getAFoundEnemy(point3d, d, i, 5F);
    }

    public static Actor getAFoundEnemy(Point3d point3d, double d, int i, float f)
    {
        double d1 = d * d;
        List list = Engine.targets();
        int j = list.size();
        boolean flag = false;
        for(int k = 0; k < j; k++)
        {
            Actor actor = (Actor)list.get(k);
            if((actor instanceof RwyTransp) || (actor instanceof RwyTranspWide) || (actor instanceof RwyTranspSqr) || (actor instanceof TransparentTestRunway) || (actor instanceof GenericSpawnPointPlane) || (((Prey)actor).HitbyMask() & usedWeaponsMask) == 0)
                continue;
            int i1 = actor.getArmy();
            if(i1 == 0 || i1 == i)
                continue;
            Point3d point3d1 = actor.pos.getAbsPoint();
            double d2 = (point3d.x - point3d1.x) * (point3d.x - point3d1.x) + (point3d.y - point3d1.y) * (point3d.y - point3d1.y);
            if(d2 > d1)
                continue;
            if(useSpeed)
            {
                float f1 = (float)actor.getSpeed(null);
                if(f1 < minSpeed || f1 > maxSpeed)
                    continue;
            }
            if(actor instanceof Paratrooper)
            {
                d2 += 1.0D + d1;
                flag = true;
            }
            if(actor instanceof Aircraft)
            {
                tempPos.set(point3d);
                tempPos.z += f;
                if(!VisCheck.visCheck(tempPos, point3d1, null, (Aircraft)actor))
                    continue;
            }
            int j1;
            for(j1 = nearNUsed - 1; j1 >= 0 && d2 < nearDSq[j1]; j1--);
            if(++j1 >= nearNUsed)
            {
                if(nearNUsed < MAX_OBJECTS)
                {
                    nearAct[nearNUsed] = actor;
                    nearDSq[nearNUsed] = d2;
                    nearNUsed++;
                }
                continue;
            }
            int k1;
            if(nearNUsed < MAX_OBJECTS)
            {
                k1 = nearNUsed - 1;
                nearNUsed++;
            } else
            {
                k1 = nearNUsed - 2;
            }
            for(; k1 >= j1; k1--)
            {
                nearAct[k1 + 1] = nearAct[k1];
                nearDSq[k1 + 1] = nearDSq[k1];
            }

            nearAct[j1] = actor;
            nearDSq[j1] = d2;
        }

        if(nearNUsed <= 0)
            return null;
        if(!flag || (nearAct[0] instanceof Paratrooper))
            return nearAct[nearNUsed != 1 ? World.Rnd().nextInt(nearNUsed) : 0];
        int l;
        for(l = 1; l < nearNUsed && !(nearAct[l] instanceof Paratrooper); l++);
        return nearAct[l != 1 ? World.Rnd().nextInt(l) : 0];
    }

    public static Actor getAFoundFlyingPlane(Point3d point3d, double d, int i, float f)
    {
        double d1 = d * d;
        List list = Engine.targets();
        int j = list.size();
        for(int k = 0; k < j; k++)
        {
            Actor actor = (Actor)list.get(k);
            if(!(actor instanceof Aircraft))
                continue;
            int l = actor.getArmy();
            if(l == 0 || l == i || (((Prey)actor).HitbyMask() & usedWeaponsMask) == 0)
                continue;
            Point3d point3d1 = actor.pos.getAbsPoint();
            double d2 = (point3d.x - point3d1.x) * (point3d.x - point3d1.x) + (point3d.y - point3d1.y) * (point3d.y - point3d1.y) + (point3d.z - point3d1.z) * (point3d.z - point3d1.z);
            if(d2 > d1 || actor.getSpeed(null) < 10D || point3d1.z - World.land().HQ(point3d1.x, point3d1.y) < (double)f)
                continue;
            int i1;
            for(i1 = nearNUsed - 1; i1 >= 0 && d2 < nearDSq[i1]; i1--);
            if(++i1 >= nearNUsed)
            {
                if(nearNUsed < MAX_OBJECTS)
                {
                    nearAct[nearNUsed] = actor;
                    nearDSq[nearNUsed] = d2;
                    nearNUsed++;
                }
                continue;
            }
            int j1;
            if(nearNUsed < MAX_OBJECTS)
            {
                j1 = nearNUsed - 1;
                nearNUsed++;
            } else
            {
                j1 = nearNUsed - 2;
            }
            for(; j1 >= i1; j1--)
            {
                nearAct[j1 + 1] = nearAct[j1];
                nearDSq[j1 + 1] = nearDSq[j1];
            }

            nearAct[i1] = actor;
            nearDSq[i1] = d2;
        }

        if(nearNUsed <= 0)
            return null;
        else
            return nearAct[nearNUsed != 1 ? World.Rnd().nextInt(nearNUsed) : 0];
    }

    private static final int MAX_OBJECTS = 3;
    private static Actor nearAct[] = new Actor[MAX_OBJECTS];
    private static double nearDSq[] = new double[MAX_OBJECTS];
    private static int nearNUsed;
    private static NearestEnemies enemies = new NearestEnemies();
    private static int usedWeaponsMask;
    private static boolean useSpeed = false;
    private static float minSpeed;
    private static float maxSpeed;
    private static Point3d tempPos = new Point3d();

}
