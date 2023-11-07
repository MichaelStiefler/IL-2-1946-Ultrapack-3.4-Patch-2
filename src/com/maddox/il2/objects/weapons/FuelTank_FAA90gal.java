package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class FuelTank_FAA90gal extends FuelTank
{
    public void start()
    {
        super.start();
        Delay = Time.current() + 600L + World.Rnd().nextLong(0L, 850L);
    }

    public void interpolateTick()
    {
        super.interpolateTick();
        if(Time.current() > Delay)
            rotAxis = new Vector3d(10D, 1.0D, 5D);
    }

    private long Delay;

    static 
    {
        Class class1 = FuelTank_FAA90gal.class;
        Property.set(class1, "mesh", "3do/Arms/FAA-90galDroptank/mono.sim");
        Property.set(class1, "kalibr", 0.4F);
        Property.set(class1, "massa", 341F);
    }
}
