package com.maddox.il2.objects.air;

import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class MOSQUITOtsts extends MOSQUITO implements TypeFighter, TypeStormovik {

    public MOSQUITOtsts() {
        this.phase = 0;
        this.disp = 0.0F;
        this.oldbullets = 0;
        this.g1 = null;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
        if (this.FM.CT.Weapons[1] != null) this.g1 = this.FM.CT.Weapons[1][0];
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("Rack1_D0", thisWeaponsName.startsWith("2x") || thisWeaponsName.startsWith("L2x"));
        hierMesh.chunkVisible("Rack2_D0", thisWeaponsName.startsWith("2x") || thisWeaponsName.startsWith("L2x"));
        hierMesh.chunkVisible("MGBar1", !thisWeaponsName.startsWith("Less") && !thisWeaponsName.startsWith("L2x"));
        hierMesh.chunkVisible("MGBar2", !thisWeaponsName.startsWith("Less") && !thisWeaponsName.startsWith("L2x"));
    }
    
    public void update(float f) {
        if (this.g1 != null && this.oldbullets != this.g1.countBullets()) switch (this.phase) {
            default:
                break;

            case 0:
                if (this.g1.isShots()) {
                    this.oldbullets = this.g1.countBullets();
                    this.phase = 1;
                    this.disp = 0.0F;
                }
                break;

            case 1:
                this.disp += 12.6F * f;
                this.resetYPRmodifier();
                Aircraft.xyz[1] = this.disp;
                this.hierMesh().chunkSetLocate("Cannon_D0", Aircraft.xyz, Aircraft.ypr);
                if (this.disp >= 0.7F) this.phase = 2;
                break;

            case 2:
                this.disp -= 1.2F * f;
                this.resetYPRmodifier();
                Aircraft.xyz[1] = this.disp;
                this.hierMesh().chunkSetLocate("Cannon_D0", Aircraft.xyz, Aircraft.ypr);
                if (this.disp <= 0.0F) this.phase = 3;
                break;

            case 3:
                this.phase = 0;
                break;
        }
        super.update(f);
    }

    private int           phase;
    private float         disp;
    private int           oldbullets;
    private BulletEmitter g1;

    static {
        Class class1 = MOSQUITOtsts.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Mosquito");
        Property.set(class1, "meshName", "3DO/Plane/Mosquito_FB_MkVI_Tse(Multi1)/TseTse_hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar04());
        Property.set(class1, "meshName_gb", "3DO/Plane/Mosquito_FB_MkVI_Tse(GB)/TseTse_hier.him");
        Property.set(class1, "PaintScheme_gb", new PaintSchemeFMPar04());
        Property.set(class1, "yearService", 1943F);
        Property.set(class1, "yearExpired", 1948.5F);
        Property.set(class1, "FlightModel", "FlightModels/Mosquito-tsts.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitMosquitoTseTse.class });
        Property.set(class1, "LOSElevation", 0.6731F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 0, 0, 1, 3, 3, 3, 3 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_CANNON01", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04" });
    }
}
