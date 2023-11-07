package com.maddox.il2.objects.air;

import java.security.SecureRandom;

import com.maddox.il2.ai.RangeRandom;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;

/**
 * @author SAS~Skylla, SAS~Storebror
 * @see CockpitMIG_7.java
 *
 *
 * air.ini: 
 *  MiG-7             air.MIG_7 1                           NOINFO  r01   SUMMER
 * 
 * plane.properties:
 * 
 *  MiG-7         MiG-7 '44
 * 
 * weapons.properties:
 * 
 *  #####################################################################
 *  # MiG-7
 *  #####################################################################
 *  MiG-7.default         Default (2x 20mm ShVak)
 *  MiG-7.none            None
 *
 *
 *      -----------------------------------------------------------------------------------------
 *
 *      TODO ([-] pending, [x] done):
 *
 *      > [x] Cockpitinstrumente
 *      > [x] move Cockpit Door
 *      > [x] Fahrwerkanimation korrigieren
 *      > [x] Loadouts in cod Format
 *      > [x] FlightModel
 *      > [x] fix the jump-on-ground-start bug.
 *
 *      Die Liste wird noch auf übersehene Baustellen erweitert!
 *
 *      ------------------------------------------------------------------------------------------
**/

public class MIG_7 extends MIG_3 {
	
	private float [] rndgear = {0.0F, 0.0F, 0.0F};
    private static float [] rndgearnull = {0.0F, 0.0F, 0.0F}; // Used for Plane Land Pose calculation when Aircraft.setFM calls static gear methods
	
	public MIG_7() {
		//this will randomize the gear animation for every new spawned plane. 
		//Why? Cause the difference between flight sims and reality are many unecessary details .. (and death, of course :P)
	    
	    // Kurze Anmerkung zum Thema "Zufällige Fahrwerks-Animation" und "...wenn die Realität neu erfunden wird":
	    // Ich habe lange experimentiert, um eventuelle "Zufalls-Unfälle" durch eine reproduzierbare "Pseudo-Zufälligkeit" zu umgehen.
	    // Es gibt theoretisch zwei Ansätze:
	    // 1.) Aircraft-Hash-Wert als Seed für den Zufallszahlen-Generator nehmen.
	    //     Klingt verlockend, ist aber scheiße.
	    //     Viele Konstellationen führen dabei zu einem subjektiven Fehlen der Zufälligkeit
	    // 2.) Zufallswerte in den Netstream schreiben.
	    //     Wäre die perfekte Lösung, wenn es einen Punkt gäbe, indem die Net-Replikation für alle Flugzeuge abgegriffen werden kann.
	    //     Gibt es aber nicht.
	    //     Man müsste aus der MiG einen Pseudo-Bomber oder einen Pseudo-"Dockable" Client machen, dann müssen aber alle abstrakten Funktionen
	    //     implementiert werden, auch wenn's einfach nur Platzhalter sind.
	    //     Außerdem würde die KI mit einem solchen Pseudo-Bomber Bombenangriffe in Erwägung ziehen, oder versuchen, sich mit dem Pseudo-"Dockable" Client
	    //     z.B. unter eine TB-3 zu hängen.
	    // Fazit: Scheiß IL-2 Netcode-Implementation!
	    //        Das ist den Stress nicht wert.
	    //        Also bleibt der Zufall dem Zufall überlassen.
	    
	    // Seed Pseudo-Random Generator with really random hash.
	    SecureRandom secRandom = new SecureRandom();
	    secRandom.setSeed(System.currentTimeMillis());
	    RangeRandom rr = new RangeRandom(secRandom.nextLong());
	    for (int i=0; i<rndgear.length; i++)
	        rndgear[i] = rr.nextFloat(0.0F, 0.15F);
	    
//		rndgear[2] = (float)(Math.round(Math.random() * 15) / 100.0);
//		rndgear[0] = Math.abs((float)(Math.round(Math.random() * 15) / 100.0)-rndgear[2]);
//		rndgear[1] = Math.abs((float)(Math.round(Math.random() * 15) / 100.0)-rndgear[0]);
	}
	
	// Static Helper Method to initialize the static YPR and XYZ modifiers of the Aircraft class.
	// In IL-2 base game only instance method for this purpose exists, but this instance method cannot be called from static methods.
	private static void myResetYPRmodifier() {
	    Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0.0F;
	}

    // New Gear Animation Code
    public static void moveGear(HierMesh hiermesh, float leftGearPos, float rightGearPos, float tailWheelPos, float [] rnd) {
        
        // Always set limits for input value to 0.01F / 0.99F instead of 0F/1F.
        // It's not safe to assume that 0F/1F will ever be reached.
        // Therefore you need to leave a little "safety gap" otherwise your gears will leave that gap for you :))
        
        // Always initialize YPR and XYZ before using them, otherwise you will face nasty surprises!
        myResetYPRmodifier();
        Aircraft.ypr[1] = Aircraft.cvt(leftGearPos, 0.01F + rnd[0], 0.915F + rnd[0], 0.0F, 88F);
        if (leftGearPos <= (rnd[0]+0.07F)) {
            Aircraft.xyz[0] = Aircraft.cvt(leftGearPos, 0.01F + rnd[0], 0.07F + rnd[0], 0.0F, 0.05F);
        } else if (leftGearPos > (rnd[0]+0.07F)) {
            Aircraft.xyz[0] = Aircraft.cvt(leftGearPos, 0.12F + rnd[0], 0.22F + rnd[0], 0.05F, 0.0F);
        }
//        hiermesh.chunkSetLocate("GearL2_D0", Aircraft.xyz, Aircraft.ypr);
        hiermesh.chunkSetLocate("GearL2_BASE", Aircraft.xyz, Aircraft.ypr);
        hiermesh.chunkSetAngles("GearL2_D0", Aircraft.cvt(leftGearPos, 0.21F + rnd[0], 0.915F + rnd[0], 0.0F, 20F), 0.0F, Aircraft.cvt(rightGearPos, 0.21F + rnd[1], 0.915F + rnd[1], 0.0F, 6.05F));
        hiermesh.chunkSetAngles("GearL3_BASE", 0.0F, Aircraft.cvt(leftGearPos, 0.01F + rnd[0], 0.915F + rnd[0], 0.0F, 82F), 0.0F);
        hiermesh.chunkSetAngles("GearL3_D0", Aircraft.cvt(leftGearPos, 0.21F + rnd[0], 0.915F + rnd[0], 0.0F, -15F), 0.0F, 0.0F);
        
        Aircraft.ypr[1] = Aircraft.cvt(rightGearPos, 0.01F + rnd[1], 0.915F + rnd[1], 0.0F, -88F);
        if (rightGearPos <= (rnd[1]+0.07F)) {
            Aircraft.xyz[0] = Aircraft.cvt(rightGearPos, 0.01F + rnd[1], 0.07F + rnd[1], 0.0F, -0.05F);
        } else if (rightGearPos > (rnd[1]+0.07F)) {
            Aircraft.xyz[0] = Aircraft.cvt(rightGearPos, 0.12F + rnd[1], 0.22F + rnd[1], -0.05F, 0.0F);
        }
//        hiermesh.chunkSetLocate("GearR2_D0", Aircraft.xyz, Aircraft.ypr);
        hiermesh.chunkSetLocate("GearR2_BASE", Aircraft.xyz, Aircraft.ypr);
        hiermesh.chunkSetAngles("GearR2_D0", Aircraft.cvt(rightGearPos, 0.21F + rnd[1], 0.915F + rnd[1], 0.0F, -20F), 0.0F, Aircraft.cvt(rightGearPos, 0.21F + rnd[1], 0.915F + rnd[1], 0.0F, 6.05F));
        hiermesh.chunkSetAngles("GearR3_BASE", 0.0F, Aircraft.cvt(rightGearPos, 0.01F + rnd[1], 0.915F + rnd[1], 0.0F, -82F), 0.0F);
        hiermesh.chunkSetAngles("GearR3_D0", Aircraft.cvt(rightGearPos, 0.21F + rnd[1], 0.915F + rnd[1], 0.0F, 15F), 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, Aircraft.cvt(tailWheelPos, 0.01F + rnd[2], 0.85F + rnd[2], 0.0F, 70F), 0.0F);
        hiermesh.chunkSetAngles("GearC3_D0", 0.0F, Aircraft.cvt(tailWheelPos, 0.01F + rnd[2], 0.47F + rnd[2], 0.0F, -80F), 0.0F);
        hiermesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(tailWheelPos, 0.01F + rnd[2], 0.47F + rnd[2], 0.0F, 80F), 0.0F);
    }

    protected void moveGear(float leftGearPos, float rightGearPos, float tailWheelPos) {
        moveGear(this.hierMesh(), leftGearPos, rightGearPos, tailWheelPos, rndgear);
    }
    
    // ************************************************************************************************
    // Static function for plane land pose calculations
    public static void moveGear(HierMesh hiermesh, float leftGearPos, float rightGearPos, float tailWheelPos) {
        moveGear(hiermesh, leftGearPos, rightGearPos, tailWheelPos, rndgearnull);
    }
    // ************************************************************************************************

    // ************************************************************************************************
    // Gear code for backward compatibility, older base game versions don't indepently move their gears
    public static void moveGear(HierMesh hiermesh, float gearPos) {
        moveGear(hiermesh, gearPos, gearPos, gearPos, rndgearnull); // re-route old style function calls to new code
    }

    protected void moveGear(float gearPos) {
        moveGear(this.hierMesh(), gearPos, gearPos, gearPos, rndgear);
    }
    // ************************************************************************************************

    public void moveCockpitDoor(float f) {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.55F);
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if (Config.isUSE_RENDER()) {
            if ((Main3D.cur3D().cockpits != null) && (Main3D.cur3D().cockpits[0] != null)) {
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            }
            this.setDoorSnd(f);
        }
    }

    static {
        final Class aircraftClass = MIG_7.class;
        new SPAWN(aircraftClass);
        Property.set(aircraftClass, "iconFar_shortClassName", "MiG-7");
        Property.set(aircraftClass, "meshName", "3DO/Plane/MiG-7/hier.him");
        Property.set(aircraftClass, "PaintScheme", new PaintSchemeFMPar01());
        // This is the Mikojan-Gurewitsch I-222, maiden flight was on 7 may 1944
        // In contrast to 1946 plane types, _IF_ this plane ever went into service, it would have done so in 1944.
        // The reason for this plane _NOT_ to enter service wasn't an unfinished plane, but a dissolved task.
        Property.set(aircraftClass, "yearService", 1944.0f);
        // Would have been used in Korea as well ..
        Property.set(aircraftClass, "yearExpired", 1953.5f);
        Property.set(aircraftClass, "FlightModel", "FlightModels/MiG-7.fmd");
        Property.set(aircraftClass, "cockpitClass", new Class[] { CockpitMIG_7.class });
        Property.set(aircraftClass, "LOSElevation", 0.906f);
        Aircraft.weaponTriggersRegister(aircraftClass, new int[] { 0, 0 });
        Aircraft.weaponHooksRegister(aircraftClass, new String[] { "_MGUN01", "_MGUN02" });
    }

}
