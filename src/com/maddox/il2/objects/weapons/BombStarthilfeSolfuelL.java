package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombStarthilfeSolfuelL extends BombStarthilfeSolfuel
{

//    public void interpolateTick()
//    {
//        super.interpolateTick();
//        if(!bOnChute && this.curTm > ttcurTM)
//        {
//            setMesh("3DO/Arms/StarthilfeSolfuelL/mono.sim");
//        }
//    }
    static 
    {
        Class var_class = BombStarthilfeSolfuelL.class;
        Property.set(var_class, "mesh", "3DO/Arms/StarthilfeSolfuelL/mono.sim");
        Property.set(var_class, "radius", 0.1F);
        Property.set(var_class, "power", 0.0F);
        Property.set(var_class, "powerType", 0);
        Property.set(var_class, "kalibr", 0.7F);
        Property.set(var_class, "massa", 0.9F);
        Property.set(var_class, "sound", "weapon.bomb_phball");
    }
}
