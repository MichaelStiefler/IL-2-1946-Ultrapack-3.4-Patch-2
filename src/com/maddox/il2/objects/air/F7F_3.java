package com.maddox.il2.objects.air;

import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;
import com.maddox.sas1946.il2.util.AircraftTools;

public class F7F_3 extends F7F implements TypeFighter, TypeBNZFighter, TypeStormovik {

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("HardpointC_D0", thisWeaponsName.indexOf("1x") != -1 || thisWeaponsName.indexOf("3x") != -1);
        hierMesh.chunkVisible("HardpointR_D0", thisWeaponsName.indexOf("2x") != -1 || thisWeaponsName.indexOf("3x") != -1);
        hierMesh.chunkVisible("HardpointL_D0", thisWeaponsName.indexOf("2x") != -1 || thisWeaponsName.indexOf("3x") != -1);
    }
    
    public static void moveGear(HierMesh hiermesh, float f, float f1, float f2, boolean bDown) {
        if (bDown) {
            hiermesh.chunkSetAngles("GearC33_D0", 0.0F, 0.0F, 0.0F);
            hiermesh.chunkSetAngles("GearC2_D0", 0.0F, Aircraft.cvt(f, 0.2F, 0.35F, 0.0F, -105F), 0.0F);
            if (f < 0.3F) {
                hiermesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(f, 0.15F, 0.2F, 0.0F, -90F), 0.0F);
                hiermesh.chunkSetAngles("GearC6_D0", 0.0F, Aircraft.cvt(f, 0.15F, 0.2F, 0.0F, -90F), 0.0F);
            } else {
                hiermesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(f, 0.33F, 0.4F, -90F, 0.0F), 0.0F);
                hiermesh.chunkSetAngles("GearC6_D0", 0.0F, Aircraft.cvt(f, 0.33F, 0.4F, -90F, 0.0F), 0.0F);
            }
            hiermesh.chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.8F, 0.0F, 116.5F), 0.0F);
            hiermesh.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.8F, 0.0F, -135F), 0.0F);
            hiermesh.chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(f, 0.2F, 1.0F, 0.0F, 116.5F), 0.0F);
            hiermesh.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(f, 0.2F, 1.0F, 0.0F, -135F), 0.0F);
            if (f < 0.5F) {
                hiermesh.chunkSetAngles("GearL5_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.1F, 0.0F, -70F), 0.0F);
                hiermesh.chunkSetAngles("GearL6_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.1F, 0.0F, -72.5F), 0.0F);
                hiermesh.chunkSetAngles("GearR5_D0", 0.0F, Aircraft.cvt(f, 0.2F, 0.3F, 0.0F, -70F), 0.0F);
                hiermesh.chunkSetAngles("GearR6_D0", 0.0F, Aircraft.cvt(f, 0.2F, 0.3F, 0.0F, -72.5F), 0.0F);
            } else {
                hiermesh.chunkSetAngles("GearL5_D0", 0.0F, Aircraft.cvt(f, 0.7F, 0.8F, -70F, -60F), 0.0F);
                hiermesh.chunkSetAngles("GearL6_D0", 0.0F, Aircraft.cvt(f, 0.7F, 0.8F, -72.5F, -62.5F), 0.0F);
                hiermesh.chunkSetAngles("GearR5_D0", 0.0F, Aircraft.cvt(f, 0.9F, 1.0F, -70F, -60F), 0.0F);
                hiermesh.chunkSetAngles("GearR6_D0", 0.0F, Aircraft.cvt(f, 0.9F, 1.0F, -72.5F, -62.5F), 0.0F);
            }
        } else {
            hiermesh.chunkSetAngles("GearC33_D0", 0.0F, 0.0F, 0.0F);
            hiermesh.chunkSetAngles("GearC2_D0", 0.0F, Aircraft.cvt(f, 0.65F, 0.8F, 0.0F, -105F), 0.0F);
            if (f > 0.7F) {
                hiermesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(f, 0.78F, 0.83F, -90F, 0.0F), 0.0F);
                hiermesh.chunkSetAngles("GearC6_D0", 0.0F, Aircraft.cvt(f, 0.78F, 0.83F, -90F, 0.0F), 0.0F);
            } else {
                hiermesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(f, 0.6F, 0.65F, 0.0F, -90F), 0.0F);
                hiermesh.chunkSetAngles("GearC6_D0", 0.0F, Aircraft.cvt(f, 0.6F, 0.65F, 0.0F, -90F), 0.0F);
            }
            hiermesh.chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(f, 0.2F, 1.0F, 0.0F, 116.5F), 0.0F);
            hiermesh.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(f, 0.2F, 1.0F, 0.0F, -135F), 0.0F);
            hiermesh.chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.8F, 0.0F, 116.5F), 0.0F);
            hiermesh.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.8F, 0.0F, -135F), 0.0F);
            if (f < 0.5F) {
                hiermesh.chunkSetAngles("GearL5_D0", 0.0F, Aircraft.cvt(f, 0.2F, 0.3F, 0.0F, -70F), 0.0F);
                hiermesh.chunkSetAngles("GearL6_D0", 0.0F, Aircraft.cvt(f, 0.2F, 0.3F, 0.0F, -72.5F), 0.0F);
                hiermesh.chunkSetAngles("GearR5_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.1F, 0.0F, -70F), 0.0F);
                hiermesh.chunkSetAngles("GearR6_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.1F, 0.0F, -72.5F), 0.0F);
            } else {
                hiermesh.chunkSetAngles("GearL5_D0", 0.0F, Aircraft.cvt(f, 0.9F, 1.0F, -70F, -60F), 0.0F);
                hiermesh.chunkSetAngles("GearL6_D0", 0.0F, Aircraft.cvt(f, 0.9F, 1.0F, -72.5F, -62.5F), 0.0F);
                hiermesh.chunkSetAngles("GearR5_D0", 0.0F, Aircraft.cvt(f, 0.7F, 0.8F, -70F, -60F), 0.0F);
                hiermesh.chunkSetAngles("GearR6_D0", 0.0F, Aircraft.cvt(f, 0.7F, 0.8F, -72.5F, -62.5F), 0.0F);
            }
        }
    }

    public static void moveGear(HierMesh hiermesh, float f, float f1, float f2) {
        moveGear(hiermesh, f, f1, f2, true);
    }

    protected void moveGear(float f, float f1, float f2) {
        moveGear(this.hierMesh(), f, f1, f2, this.FM.CT.GearControl > 0.5F);
    }

    public static void moveGear(HierMesh hiermesh, float f, boolean bDown) {
        moveGear(hiermesh, f, f, f, bDown);
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        moveGear(hiermesh, f, f, f, true);
    }

    protected void moveGear(float f) {
        moveGear(this.hierMesh(), f, this.FM.CT.GearControl > 0.5F);
    }

    public void doRemoveBodyFromPlane(int i) {
        super.doRemoveBodyFromPlane(i);
        this.hierMesh().chunkVisible("Pilot1_D0", false);
        this.hierMesh().chunkVisible("Pilot1_D1", false);
        this.hierMesh().chunkVisible("Head1_D0", false);
        this.hierMesh().chunkVisible("HMask1_D0", false);
    }

    public void doMurderPilot(int i) {
        switch (i) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                break;
        }
    }

    protected void moveWingFold(HierMesh hiermesh, float f) {
        hiermesh.chunkSetAngles("WingLOut_D0", 0.0F, 130F * f, 0.0F);
        hiermesh.chunkSetAngles("WingROut_D0", 0.0F, -130F * f, 0.0F);
    }

    public void moveWingFold(float f) {
//        if (f < 0.001F) {
//            this.setGunPodsOn(true);
//            this.hideWingWeapons(false);
//        } else {
//            this.setGunPodsOn(false);
//            this.FM.CT.WeaponControl[0] = false;
//            this.hideWingWeapons(true);
//        }
        this.moveWingFold(this.hierMesh(), f);
        AircraftTools.updateExternalWeaponHooks(this);
    }

    public void moveArrestorHook(float f) {
        this.hierMesh().chunkSetAngles("Hook1_D0", 0.0F, 55F * f, 0.0F);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (this.FM.getAltitude() < 3000F) this.hierMesh().chunkVisible("HMask1_D0", false);
        else this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
        if (this.FM.AS.astateEngineStates[0] > 3 && World.Rnd().nextFloat() < 0.0023F) this.FM.AS.hitTank(this, 0, 1);
        if (this.FM.AS.astateEngineStates[1] > 3 && World.Rnd().nextFloat() < 0.0023F) this.FM.AS.hitTank(this, 1, 1);
        if (this.FM.AS.astateEngineStates[2] > 3 && World.Rnd().nextFloat() < 0.0023F) this.FM.AS.hitTank(this, 2, 1);
        if (this.FM.AS.astateEngineStates[3] > 3 && World.Rnd().nextFloat() < 0.0023F) this.FM.AS.hitTank(this, 3, 1);
    }

    static {
        Class class1 = F7F_3.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "F7F");
        Property.set(class1, "meshName", "3DO/Plane/F7F-3(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar05());
        Property.set(class1, "yearService", 1943F);
        Property.set(class1, "yearExpired", 1965.5F);
        Property.set(class1, "FlightModel", "FlightModels/F7F-3.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitF7F_3.class });
        Property.set(class1, "LOSElevation", 0.92575F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 0, 0, 1, 1, 1, 1, 9, 9, 9, 9, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 });
        Aircraft.weaponHooksRegister(class1,
                new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03",
                        "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06", "_ExternalBomb07", "_ExternalBomb08", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalRock07",
                        "_ExternalRock08", "_ExternalRock09", "_ExternalRock10" });
    }
}
