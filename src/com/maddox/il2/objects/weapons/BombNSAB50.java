package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.objects.ActorLand;
import com.maddox.il2.objects.air.Chute;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class BombNSAB50 extends Bomb
{

    public BombNSAB50()
    {
        chute = null;
        OnChute = false;
    }

    protected boolean haveSound()
    {
        return false;
    }

    public void start()
    {
        super.start();
        DelaySAB = Time.current() + 500L + World.Rnd().nextLong(0L, 850L);
    }

    public void destroy()
    {
        if(chute != null)
            chute.destroy();
        super.destroy();
    }

    public void deploychute()
    {
        chute = new Chute(this);
        chute.setMesh("3do/Arms/P85-ParaBrake/mono.sim");
        chute.collide(false);
        chute.mesh().setScale(0.2F);
        chute.pos.setRel(new Point3d(-0.35D, 0.0D, 0.05D), new Orient(0.0F, 90F, 0.0F));
    }

    public void interpolateTick()
    {
        this.curTm += Time.tickLenFs();
        super.interpolateTick();
        if(!OnChute && Time.current() > DelaySAB)
        {
            OnChute = true;
            setMesh("3do/Arms/P85-NoSAB50/mono_deploy.sim");
            deploychute();
            Eff3DActor.New(this, findHook("_NightFlare"), null, 1.0F, "3do/Effects/P85/P85_NightFlare.eff", -1F);
        }
        if(OnChute)
        {
            getSpeed(v3d);
            v3d.scale(0.97D);
            if(v3d.z < -2D)
                v3d.z += 1.1F * Time.tickConstLenFs();
            setSpeed(v3d);
        }
    }

    public void msgCollision(Actor actor, String s, String s1)
    {
        if((actor instanceof ActorLand) && chute != null)
            chute.landing();
        super.msgCollision(actor, s, s1);
    }

    private Chute chute;
    private boolean OnChute;
    private static Vector3d v3d = new Vector3d();
    private long DelaySAB;

    static 
    {
        Class class1 = BombNSAB50.class;
        Property.set(class1, "mesh", "3do/Arms/P85-NoSAB50/mono.sim");
        Property.set(class1, "emitColor", new Color3f(1.0F, 1.0F, 1.0F));
        Property.set(class1, "emitLen", 250F);
        Property.set(class1, "emitMax", 10F);
        Property.set(class1, "radius", 75F);
        Property.set(class1, "power", 0.0F);
        Property.set(class1, "powerType", 1);
        Property.set(class1, "kalibr", 0.2F);
        Property.set(class1, "massa", 24F);
        Property.set(class1, "sound", "weapon.bomb_std");
    }
}
