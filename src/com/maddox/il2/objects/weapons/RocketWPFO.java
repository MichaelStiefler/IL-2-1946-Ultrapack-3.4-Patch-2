package com.maddox.il2.objects.weapons;

import java.util.Random;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.MsgExplosion;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Mission;
import com.maddox.il2.objects.ActorLand;
import com.maddox.il2.objects.effects.Explosions;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class RocketWPFO extends Rocket
{

    public RocketWPFO()
    {
        counter = 0;
        marked = false;
    }

    public void start(float f, int i)
    {
        super.start(f, i);
        drawing(false);
        t1 = Time.current() + 0x1c3a90L + World.Rnd().nextLong(0L, 850L);
        t2 = Time.current() + 0x1c3a90L + World.Rnd().nextLong(0L, 3800L);
        Eff3DActor.New(this, null, new Loc(), 1.0F, "Effects/Smokes/WPsmoke.eff", (float)(t1 - Time.current()) / 1000F);
    }

    public void msgCollision(Actor actor, String s, String s1)
    {
        if(actor instanceof ActorLand)
        {
            if(Time.current() <= (t2 + t1) / 2L)
            {
                Point3d point3d = new Point3d();
                this.pos.getTime(Time.current(), point3d);
                Class class1 = getClass();
                float f = Property.floatValue(class1, "power", 0.0F);
                int i = Property.intValue(class1, "powerType", 0);
                float f1 = Property.floatValue(class1, "radius", 1.0F);
                MsgExplosion.send(actor, s1, point3d, getOwner(), this.M, f, i, f1);
                Vector3d vector3d = new Vector3d();
                getSpeed(vector3d);
                vector3d.x *= 0.5D;
                vector3d.y *= 0.5D;
                vector3d.z = 1.0D;
                setSpeed(vector3d);
                if(counter > 200 && !marked)
                {
                    marked = true;
                    HUD.logCenter("                                                                             Splash!");
                    counter = 0;
                }
                Random random = new Random();
                int j = random.nextInt(10);
                j -= 5;
                if(marked && counter > 25 + j)
                {
                    int k = random.nextInt(150);
                    int l = k - 75;
                    point3d.x += l;
                    k = random.nextInt(150);
                    l = k - 75;
                    point3d.y += l;
                    Explosions.generate(actor, point3d, 25F, 0, 136F, !Mission.isNet());
                    MsgExplosion.send(actor, s, point3d, getOwner(), 0.0F, 25F, 0, 136F);
                    counter = 0;
                }
                counter++;
            }
        } else
        {
            super.msgCollision(actor, s, s1);
        }
    }

    private long t1;
    private long t2;
    private int counter;
    private boolean marked;

    static 
    {
        Class class1 = RocketWPFO.class;
        Property.set(class1, "mesh", "3DO/Arms/2-75inch/mono.sim");
        Property.set(class1, "sprite", "3DO/Effects/Rocket/rocketsmokewhite.eff");
        Property.set(class1, "flame", "3DO/Effects/Rocket/mono.sim");
        Property.set(class1, "smoke", "3DO/Effects/Rocket/rocketsmokewhite.eff");
        Property.set(class1, "emitColor", new Color3f(1.0F, 1.0F, 0.5F));
        Property.set(class1, "emitLen", 50F);
        Property.set(class1, "emitMax", 1.0F);
        Property.set(class1, "sound", "weapon.rocket_132");
        Property.set(class1, "radius", 0.0F);
        Property.set(class1, "timeLife", 100F);
        Property.set(class1, "timeFire", 4F);
        Property.set(class1, "force", 0.0F);
        Property.set(class1, "power", 0.0F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.08F);
        Property.set(class1, "massa", 10.1F);
        Property.set(class1, "massaEnd", 5.1F);
    }
}
