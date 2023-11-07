package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class RocketTiny_Tim extends Rocket
{

    public RocketTiny_Tim()
    {
        tOrient = new Orient();
        super.flags &= 0xffffffdf;
    }

    public boolean interpolateStep()
    {
        if(tEStart > 0L)
            if(Time.current() > tEStart)
            {
                tEStart = -1L;
                setThrust(222000F);
                if(Config.isUSE_RENDER())
                {
                    newSound(this.soundName, true);
                    Eff3DActor.setIntesity(this.smoke, 1.0F);
                    Eff3DActor.setIntesity(this.sprite, 1.0F);
                    this.flame.drawing(true);
                    this.light.light.setEmit(2.0F, 100F);
                }
            } else
            {
                Ballistics.update(this, this.M, 0.07068583F, 0.0F, true);
                this.pos.setAbs(tOrient);
                return false;
            }
        return super.interpolateStep();
    }

    public void start(float f, int i)
    {
        super.start(-1F, i);
        FlightModel flightmodel = ((Aircraft)getOwner()).FM;
        tOrient.set(flightmodel.Or);
        this.speed.set(flightmodel.Vwld);
        this.noGDelay = -1L;
        tEStart = Time.current() + World.Rnd().nextLong(900L, 1100L);
        if(Config.isUSE_RENDER())
        {
            breakSounds();
            Eff3DActor.setIntesity(this.smoke, 0.0F);
            Eff3DActor.setIntesity(this.sprite, 0.0F);
            this.flame.drawing(false);
            this.light.light.setEmit(0.0F, 0.0F);
        }
    }

    private long tEStart;
    private Orient tOrient;

    static 
    {
        Class class1 = RocketTiny_Tim.class;
        Property.set(class1, "mesh", "3DO/Arms/Tiny_Tim/mono.sim");
        Property.set(class1, "sprite", "3DO/Effects/Rocket/firesprite.eff");
        Property.set(class1, "flame", "3DO/Effects/Rocket/mono.sim");
        Property.set(class1, "smoke", "3DO/Effects/Rocket/rocketsmokewhite.eff");
        Property.set(class1, "smokeStart", "3do/effects/rocket/rocketsmokewhitestart.eff");
        Property.set(class1, "smokeTile", "3do/effects/rocket/rocketsmokewhitetile.eff");
        Property.set(class1, "emitColor", new Color3f(1.0F, 1.0F, 0.5F));
        Property.set(class1, "emitLen", 50F);
        Property.set(class1, "emitMax", 2.0F);
        Property.set(class1, "sound", "weapon.rocket_132");
        Property.set(class1, "radius", 176F);
        Property.set(class1, "timeLife", 1000000F);
        Property.set(class1, "timeFire", 33F);
        Property.set(class1, "force", 2500F);
        Property.set(class1, "power", 68.4F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.298F);
        Property.set(class1, "massa", 582F);
        Property.set(class1, "massaEnd", 270F);
    }
}
