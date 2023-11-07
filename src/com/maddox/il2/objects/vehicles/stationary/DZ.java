package com.maddox.il2.objects.vehicles.stationary;

public abstract class DZ
{
    public static class DZUnit extends DZGeneric {
//        public int getDzRadius() {
//            return 1000;
//        }
    }

//    public static class DZUnit10km extends DZGeneric {
//        public int getDzRadius() {
//            return 10000;
//        }
//    }
//    public static class DZUnit100km extends DZGeneric {
//        public int getDzRadius() {
//            return 100000;
//        }
//    }

    static 
    {
        new DZGeneric.SPAWN(DZUnit.class);
//        new DZGeneric.SPAWN(DZUnit10km.class);
//        new DZGeneric.SPAWN(DZUnit100km.class);
    }
}
