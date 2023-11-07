package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class DH116 extends DH116xyz
{
    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("HydraMagazine", thisWeaponsName.endsWith("10xHydra"));
        hierMesh.chunkVisible("Hydraports", thisWeaponsName.endsWith("10xHydra"));
        hierMesh.chunkVisible("Mk82Clamps", thisWeaponsName.indexOf("2xOC") == -1 && (thisWeaponsName.indexOf("6xMk") != -1 || thisWeaponsName.indexOf("12xMk") != -1 || thisWeaponsName.indexOf("6xCB") != -1 || thisWeaponsName.indexOf("12xCB") != -1));
        hierMesh.chunkVisible("Mk83Clamps", thisWeaponsName.indexOf("2xOC") != -1);
        for (int i=1; i<7; i++)
            hierMesh.chunkVisible("WingPylon" + i, (thisWeaponsName.indexOf("AIM") == -1) && (thisWeaponsName.indexOf("12x") != -1 || thisWeaponsName.indexOf("+6xCB") != -1 || thisWeaponsName.indexOf("+6xMk") != -1 || (thisWeaponsName.indexOf("2xOC") != -1 && (thisWeaponsName.indexOf("6xCB") != -1 || thisWeaponsName.indexOf("6xMk") != -1))));
        
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        DH116.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }
    
//    private static Aircraft._WeaponSlot[] defaultSlot() {
//        byte len = 36;
//        Aircraft._WeaponSlot[] a_lweaponslot = new Aircraft._WeaponSlot[len];
//        try
//        {
//            a_lweaponslot[0] = new Aircraft._WeaponSlot(0, "CannonAden30mm", 300);
//            a_lweaponslot[1] = new Aircraft._WeaponSlot(0, "CannonAden30mm", 300);
//        }
//        catch(Exception exception) { }
//        for(int i = 2; i < len; i++)
//            a_lweaponslot[i] = null;
//        return a_lweaponslot;
//    }

    static 
    {
        Class class1 = DH116.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "DH-116");
        Property.set(class1, "meshName", "3DO/Plane/DeHavilland116/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(class1, "yearService", 1946F);
        Property.set(class1, "yearExpired", 1948.5F);
        Property.set(class1, "FlightModel", "FlightModels/DH116.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitDH116.class
        });
        Property.set(class1, "LOSElevation", 1.1058F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 
            2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
            9, 9, 9, 9, 9, 9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_MGUN01", "_MGUN02", "_ExternalRock01", "_ExternalRock01", "_ExternalRock02", "_ExternalRock02", "_ExternalRock03", "_ExternalRock03", "_ExternalRock04", "_ExternalRock04",
            "_ExternalRock05", "_ExternalRock05", "_ExternalRock06", "_ExternalRock06", "_ExternalRock13", "_ExternalRock14", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", 
            "_ExternalBomb05", "_ExternalBomb06", "_ExternalBomb07", "_ExternalBomb08", "_ExternalBomb09", "_ExternalBomb10", "_ExternalBomb11", "_ExternalBomb12", "_ExternalBomb13", "_ExternalBomb14", 
            "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06"
        });

//    
//        try
//        {
//            ArrayList arraylist = new ArrayList();
//            Property.set(class1, "weaponsList", arraylist);
//            HashMapInt hashmapint = new HashMapInt();
//            Property.set(class1, "weaponsMap", hashmapint);
//            
//            String s = "default";
//            Aircraft._WeaponSlot[] a_lweaponslot = defaultSlot();
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "2xAIM-9B";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "4xAIM-9B";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//
//            s = "6xAIM-9B";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[32] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[33] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "2xAIM-9D";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "4xAIM-9D";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//
//            s = "6xAIM-9D";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[32] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[33] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "6xMk82";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "12xMk82";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[24] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[25] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[26] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[27] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[28] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[29] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "6xMk82+2xAIM-9B";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "6xMk82+4xAIM-9B";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "6xMk82+6xAIM-9B";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[32] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[33] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "6xMk82+2xAIM-9D";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "6xMk82+4xAIM-9D";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "6xMk82+6xAIM-9D";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[32] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[33] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "6xCBU";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "12xCBU";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[24] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[25] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[26] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[27] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[28] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[29] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//
//            
//            
//            
//            s = "6xCBU+2xAIM-9B";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "6xCBU+4xAIM-9B";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//
//            s = "6xCBU+6xAIM-9B";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[32] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[33] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "6xCBU+2xAIM-9D";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "6xCBU+4xAIM-9D";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//
//            s = "6xCBU+6xAIM-9D";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[32] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[33] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            s = "2xOCMk83";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[22] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[23] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//
//
//            s = "2xOCMk83+2xAIM-9B";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[22] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[23] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "2xOCMk83+4xAIM-9B";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[22] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[23] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//
//            s = "2xOCMk83+6xAIM-9B";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[22] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[23] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[32] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[33] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "2xOCMk83+2xAIM-9D";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[22] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[23] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "2xOCMk83+4xAIM-9D";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[22] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[23] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//
//            s = "2xOCMk83+6xAIM-9D";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[22] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[23] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9D", 1);
//            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
//            a_lweaponslot[30] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[31] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[32] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[33] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[34] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            a_lweaponslot[35] = new Aircraft._WeaponSlot(9, "PylonF86_Sidewinder", -12345);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "2xOCMk83+6xMk82";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[22] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[23] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[24] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[25] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[26] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[27] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[28] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[29] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "2xOCMk83+6xCBU";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[22] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[23] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
//            a_lweaponslot[24] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[25] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[26] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[27] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[28] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[29] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            
//            
//            s = "6xCBU+6xMk82";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
//            a_lweaponslot[24] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[25] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[26] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[27] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[28] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            a_lweaponslot[29] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            
//            //            s = "4xR60_8xMk82";
////            a_lweaponslot = defaultSlot();
////             a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[22] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[23] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            arraylist.add(s);
////            hashmapint.put(Finger.Int(s), a_lweaponslot);
////            
////            s = "6xR60_4xCBU24";
////            a_lweaponslot = defaultSlot();
////            a_lweaponslot[0] = new Aircraft._WeaponSlot(0, "CannonAden30mm", 300);
////            a_lweaponslot[1] = new Aircraft._WeaponSlot(0, "CannonAden30mm", 300);
////            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[14] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[15] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[26] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[27] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[28] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[29] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            arraylist.add(s);
////            hashmapint.put(Finger.Int(s), a_lweaponslot);
////            
////            s = "4xR60_6xCBU24";
////            a_lweaponslot = defaultSlot();
////            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[24] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[25] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[26] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[27] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[28] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[29] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            arraylist.add(s);
////            hashmapint.put(Finger.Int(s), a_lweaponslot);
////            
////            s = "6xR60_2xOCMk83";
////            a_lweaponslot = defaultSlot();
////            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[12] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[13] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[14] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[15] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[38] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
////            a_lweaponslot[39] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
////            arraylist.add(s);
////            hashmapint.put(Finger.Int(s), a_lweaponslot);
////            
////            s = "4xR60_2xMk82_2xOCMk83";
////            a_lweaponslot = defaultSlot();
////            a_lweaponslot[4] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[5] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[6] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[7] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[8] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[9] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[10] = new Aircraft._WeaponSlot(2, "RocketGunAIM9B", 1);
////            a_lweaponslot[11] = new Aircraft._WeaponSlot(2, "RocketGunNull", 1);
////            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[38] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
////            a_lweaponslot[39] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
////            arraylist.add(s);
////            hashmapint.put(Finger.Int(s), a_lweaponslot);
////            
////            s = "8xMk82_10xHydra";
////            a_lweaponslot = defaultSlot();
////            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunHYDRA", 10);
////            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunHYDRA", 10);
////            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[18] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[19] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[20] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[21] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[22] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[23] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            arraylist.add(s);
////            hashmapint.put(Finger.Int(s), a_lweaponslot);
////            
////            s = "6xCBU24_10xHydra";
////            a_lweaponslot = defaultSlot();
////            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunHYDRA", 10);
////            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunHYDRA", 10);
////            a_lweaponslot[24] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[25] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[26] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[27] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[28] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[29] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            arraylist.add(s);
////            hashmapint.put(Finger.Int(s), a_lweaponslot);
////            
////            s = "2xMk82_2xOCMk83_10xHydra";
////            a_lweaponslot = defaultSlot();
////            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunHYDRA", 10);
////            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunHYDRA", 10);
////            a_lweaponslot[16] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[17] = new Aircraft._WeaponSlot(3, "BombGunMk82", 1);
////            a_lweaponslot[38] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
////            a_lweaponslot[39] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
////            arraylist.add(s);
////            hashmapint.put(Finger.Int(s), a_lweaponslot);
////            
////            s = "2xCBU24_2xOCMk83_10xHydra";
////            a_lweaponslot = defaultSlot();
////            a_lweaponslot[2] = new Aircraft._WeaponSlot(2, "RocketGunHYDRA", 10);
////            a_lweaponslot[3] = new Aircraft._WeaponSlot(2, "RocketGunHYDRA", 10);
////            a_lweaponslot[24] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[25] = new Aircraft._WeaponSlot(3, "BombGunCBU24", 1);
////            a_lweaponslot[38] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
////            a_lweaponslot[39] = new Aircraft._WeaponSlot(3, "BombGunOCMk83", 1);
////            arraylist.add(s);
////            hashmapint.put(Finger.Int(s), a_lweaponslot);
//            
//            s = "none";
//            a_lweaponslot = defaultSlot();
//            a_lweaponslot[0] = null;
//            a_lweaponslot[1] = null;
//            arraylist.add(s);
//            hashmapint.put(Finger.Int(s), a_lweaponslot);
//        }
//        catch(Exception exception) { }
//    
//    
    
    
    }
}
