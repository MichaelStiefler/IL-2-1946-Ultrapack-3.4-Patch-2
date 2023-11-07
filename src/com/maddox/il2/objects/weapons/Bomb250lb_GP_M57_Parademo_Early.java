package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.objects.ActorLand;
import com.maddox.il2.objects.air.Chute;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class Bomb250lb_GP_M57_Parademo_Early extends Bomb
{

    public Bomb250lb_GP_M57_Parademo_Early()
    {
        chute = null;
        chute1 = null;
        bOnChute = false;
    }

    protected boolean haveSound()
    {
        return false;
    }

    public void start()
    {
        super.start();
        ttcurTM = World.Rnd().nextFloat(0.5F, 1.75F);
    }

    public void destroy()
    {
        if(chute != null)
            chute.destroy();
        if(chute1 != null)
            chute1.destroy();
        super.destroy();
    }

    public void interpolateTick()
    {
        super.interpolateTick();
        if(bOnChute)
        {
            this.pos.getAbs(Bomb.P, Bomb.Or);
            getSpeed(v3d);
            v3d.scale(0.97D);
            if(v3d.z < -2D)
                v3d.z += 1.1F * Time.tickConstLenFs();
            setSpeed(v3d);
            if((double)this.curTm > (double)ttcurTM + 2D)
            {
                Bomb.Or.getTangage();
                float f = 3.5F * Time.tickConstLenFs();
                if(Bomb.Or.getTangage() > -90F)
                {
                    Bomb.Or.increment(0.0F, -f, 0.0F);
                } else
                {
                    Bomb.Or.increment(0.0F, 0.0F, 0.0F);
                    Bomb.Or.set(0.0F, -90F, 0.0F);
                }
                v3d.set(1.0D, 0.0D, 0.0D);
                Bomb.Or.transform(v3d);
                this.pos.setUpdateEnable(true);
                this.pos.setAbs(Bomb.P, Bomb.Or);
                this.pos.reset();
                if(Bomb.Or.getTangage() == -90F)
                {
                    Vector3d localVector3d1 = new Vector3d();
                    Vector3d localVector3d2 = new Vector3d();
                    getSpeed(localVector3d2);
                    localVector3d1.set(1.0D, 0.0D, 0.0D);
                    Bomb.Or.transform(localVector3d1);
                    localVector3d1.add(localVector3d2);
                    this.pos.setAbs(Bomb.P, Bomb.Or);
                    this.pos.reset();
                    this.pos.setUpdateEnable(true);
                    setSpeed(localVector3d1);
                }
            }
        } else
        if(this.curTm > ttcurTM)
        {
            bOnChute = true;
            chute = new Chute(this);
            chute.collide(false);
            chute.mesh().setScale(0.5F);
            chute.pos.setRel(new Point3d(-0.05D, -0.115D, 0.0D), new Orient(20F, 90F, 0.0F));
            chute1 = new Chute(this);
            chute1.collide(false);
            chute1.mesh().setScale(0.5F);
            chute1.pos.setRel(new Point3d(-0.05D, 0.115D, 0.0D), new Orient(-20F, 90F, 0.0F));
        }
    }

    public void msgCollision(Actor paramActor, String paramString1, String paramString2)
    {
        if((paramActor instanceof ActorLand) && chute != null)
            chute.landing();
        if((paramActor instanceof ActorLand) && chute1 != null)
            chute1.landing();
        super.msgCollision(paramActor, paramString1, paramString2);
    }

    private Chute chute;
    private Chute chute1;
    private boolean bOnChute;
    private static Vector3d v3d = new Vector3d();
    private float ttcurTM;

    static 
    {
        Class class1 = Bomb250lb_GP_M57_Parademo_Early.class;
        Property.set(class1, "mesh", "3do/arms/250lb_GP_M57_Parademo_Early/mono.sim");
        Property.set(class1, "radius", 40F);
        Property.set(class1, "power", 25.882F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.21F);
        Property.set(class1, "massa", 52.21F);
        Property.set(class1, "sound", "weapon.bomb_mid");
        Property.set(class1, "fuze", ((Object) (new Object[] {
            Fuze_AN_M103.class, Fuze_M160.class, Fuze_M112.class, Fuze_M115.class
        })));
    }
}
