package com.maddox.il2.objects.weapons;

import java.util.List;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.MsgExplosion;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.objects.ActorLand;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.TypeFighter;
import com.maddox.il2.objects.air.TypeScout;
import com.maddox.il2.objects.air.TypeStormovik;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class RocketWPFAC extends Rocket
{

    public RocketWPFAC()
    {
        marked = false;
        counter = 0;
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
                counter++;
                if(counter == 1000)
                    ((Maneuver) (pilot)).Group.setGroupTask(1);
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
                if(!marked)
                {
                    List list = Engine.targets();
                    int j = list.size();
                    for(int k = 0; k < j; k++)
                    {
                        Actor actor1 = (Actor)list.get(k);
                        Aircraft aircraft = (Aircraft)actor1;
                        if(((actor1 instanceof TypeStormovik) || (actor1 instanceof TypeFighter)) && !(actor1 instanceof TypeScout) && actor1.pos.getAbsPoint().distance(point3d) < 15000D)
                        {
                            pilot = (Pilot)((Aircraft)actor1).FM;
                            ((Maneuver) (pilot)).Group.setGroupTask(4);
                            ((Maneuver) (pilot)).Group.setGTargMode(0);
                            ((Maneuver) (pilot)).Group.setGTargMode(point3d, 100F);
                            Voice.speakOk(aircraft);
                        }
                        marked = true;
                    }

                }
            }
        } else
        {
            super.msgCollision(actor, s, s1);
        }
    }

    private long t1;
    private long t2;
    private boolean marked;
    private int counter;
    private static Pilot pilot = null;

    static 
    {
        Class class1 = RocketWPFAC.class;
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
