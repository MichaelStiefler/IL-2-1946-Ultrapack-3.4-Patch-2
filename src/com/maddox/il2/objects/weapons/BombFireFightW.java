package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class BombFireFightW extends Bomb
{
    public void msgCollision(Actor actor, String s, String s1)
    {
        doExplosion(actor, s1);
    }

    public void start()
    {
        super.start();
        Disperse = Time.current() + 6000L;
    }

    public void destroy()
    {
        super.destroy();
    }

    public void interpolateTick()
    {
        super.interpolateTick();
        Eff3DActor.New(this, findHook("_Extinguish"), null, 1.0F, "3do/Effects/ZOK/ZOK_FireFightW.eff", -1F);
        getSpeed(v3d);
        v3d.scale(0.99D);
        if(v3d.z < -2D)
            v3d.z += 1.1F * Time.tickConstLenFs();
        setSpeed(v3d);
        if(Time.current() > Disperse)
            destroy();
    }

    private static Vector3d v3d = new Vector3d();
    private long Disperse;

    static 
    {
        Class class1 = BombFireFightW.class;
        Property.set(class1, "mesh", "3do/Arms/ZOK-FireFight/mono.sim");
        Property.set(class1, "radius", 1.0F);
        Property.set(class1, "power", 0.1F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.45F);
        Property.set(class1, "massa", 502F);
    }
}
