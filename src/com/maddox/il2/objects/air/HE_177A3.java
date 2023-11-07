package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.objects.weapons.ToKGUtils;
import com.maddox.rts.Property;

public class HE_177A3 extends HE_177_TD implements TypeX4Carrier, TypeGuidedBombCarrier, TypeHasToKG {

    public HE_177A3() {
        this.deltaAzimuth = 0.0F;
        this.deltaTangage = 0.0F;
        this.isGuidingBomb = false;
        this.hasToKG = false;
        this.spreadAngle = 0;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        float f = 0.0F;
        float f1 = this.FM.M.fuel / this.FM.M.maxFuel;
        if (this.thisWeaponsName.startsWith("RustB")) {
            f = 7952.295F;
            this.iRust = 1;
        } else if (this.thisWeaponsName.startsWith("RustC")) {
            f = 9382.67F;
            this.iRust = 2;
        } else if (this.thisWeaponsName.startsWith("A5")) {
            f = 10813.045F;
            this.iRust = 3;
            this.FM.CT.bHasBayDoorControl = false;
        } else {
            f = 6521.92F;
            this.iRust = 0;
        }
        com.maddox.il2.ai.BulletEmitter abulletemitter[][] = this.FM.CT.Weapons;
        if (abulletemitter[3] == null) return;
        this.FM.M.fuel = this.FM.M.maxFuel = f;
        this.FM.M.computeParasiteMass(abulletemitter);
        this.FM.M.requestNitro(0.0F);
        float f2 = this.FM.M.getFullMass();
        if (f2 > 31000F) {
            float f4 = f2 - 31000F;
            if (f > f4) f -= f4;
        }
        this.FM.M.fuel = f1 * f;
        this.FM.M.maxFuel = f;
        this.FM.M.computeParasiteMass(abulletemitter);
        this.FM.M.requestNitro(0.0F);
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("PylonHs293C", false);
        hierMesh.chunkVisible("PylonHs293L", false);
        hierMesh.chunkVisible("PylonHs293R", false);
        hierMesh.chunkVisible("PylonFritzXC", false);
        hierMesh.chunkVisible("PylonFritzXL", false);
        hierMesh.chunkVisible("PylonFritzXR", false);
        hierMesh.chunkVisible("PylonLT50L", false);
        hierMesh.chunkVisible("PylonLT50R", false);
        hierMesh.chunkVisible("PylonLT50C1", false);
        hierMesh.chunkVisible("PylonLT50C2", false);
        hierMesh.chunkVisible("PylonSC2500C", false);
        hierMesh.chunkVisible("PylonC", false);
        hierMesh.chunkVisible("PylonL", false);
        hierMesh.chunkVisible("PylonR", false);
        if (thisWeaponsName.startsWith("A5_1") || thisWeaponsName.startsWith("A5_3") || thisWeaponsName.startsWith("A5_4")) {
            hierMesh.chunkVisible("PylonHs293C", thisWeaponsName.indexOf("293") != -1);
            hierMesh.chunkVisible("PylonFritzXC", thisWeaponsName.indexOf("FritzX") != -1);
            hierMesh.chunkVisible("PylonLT50L", thisWeaponsName.indexOf("LT50") != -1);
            hierMesh.chunkVisible("PylonLT50R", thisWeaponsName.indexOf("LT50") != -1);
            hierMesh.chunkVisible("PylonLT50C1", thisWeaponsName.indexOf("LT50") != -1);
            hierMesh.chunkVisible("PylonLT50C2", thisWeaponsName.indexOf("LT50") != -1);
            hierMesh.chunkVisible("PylonSC2500C", thisWeaponsName.indexOf("SC2500") != -1);
            hierMesh.chunkVisible("PylonC", thisWeaponsName.indexOf("293") == -1 && thisWeaponsName.indexOf("FritzX") == -1 && thisWeaponsName.indexOf("LT50") == -1 && thisWeaponsName.indexOf("SC2500") == -1);
        }
        if (thisWeaponsName.startsWith("A5_2") || thisWeaponsName.startsWith("A5_3")) {
            hierMesh.chunkVisible("PylonHs293L", thisWeaponsName.indexOf("293") != -1);
            hierMesh.chunkVisible("PylonHs293R", thisWeaponsName.indexOf("293") != -1);
            hierMesh.chunkVisible("PylonFritzXL", thisWeaponsName.indexOf("FritzX") != -1 || thisWeaponsName.indexOf("SC") != -1);
            hierMesh.chunkVisible("PylonFritzXR", thisWeaponsName.indexOf("FritzX") != -1 || thisWeaponsName.indexOf("SC") != -1);
            hierMesh.chunkVisible("PylonLT50C1", thisWeaponsName.indexOf("LT50") != -1);
            hierMesh.chunkVisible("PylonLT50C2", thisWeaponsName.indexOf("LT50") != -1);
            hierMesh.chunkVisible("PylonL", thisWeaponsName.indexOf("293") == -1 && thisWeaponsName.indexOf("FritzX") == -1 && thisWeaponsName.indexOf("LT50") == -1 && thisWeaponsName.indexOf("SC") == -1);
            hierMesh.chunkVisible("PylonR", thisWeaponsName.indexOf("293") == -1 && thisWeaponsName.indexOf("FritzX") == -1 && thisWeaponsName.indexOf("LT50") == -1 && thisWeaponsName.indexOf("SC") == -1);
        }
    }
    
    public int getBombTrainMaxAmount() {
        if (this.thisWeaponsName.startsWith("RustA_12x")) return 12;
        if (this.thisWeaponsName.startsWith("RustB_8x")) return 8;
        if (this.thisWeaponsName.startsWith("RustA_48x") || this.thisWeaponsName.startsWith("RustA_6x") || this.thisWeaponsName.startsWith("RustA_2xSC1800+4xSC250")) return 6;
        return !this.thisWeaponsName.startsWith("RustA_4x") && !this.thisWeaponsName.startsWith("RustA_2xSC1800") && !this.thisWeaponsName.startsWith("RustB_32x") && !this.thisWeaponsName.startsWith("RustB_4x")
                && !this.thisWeaponsName.startsWith("RustC_4x") ? 2 : 4;
    }

    protected void moveBayDoor(float f) {
        if (this.iRust < 1) {
            this.hierMesh().chunkSetAngles("Bay1_D0", 0.0F, -74F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Bay2_D0", 0.0F, -90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Bay3_D0", 0.0F, -74F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Bay4_D0", 0.0F, -90F * f, 0.0F);
        }
        if (this.iRust < 2) {
            this.hierMesh().chunkSetAngles("Bay5_D0", 0.0F, -74F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Bay6_D0", 0.0F, -90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Bay7_D0", 0.0F, -74F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Bay8_D0", 0.0F, -90F * f, 0.0F);
        }
        if (this.iRust < 3) {
            this.hierMesh().chunkSetAngles("Bay9_D0", 0.0F, -74F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Bay10_D0", 0.0F, -90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Bay11_D0", 0.0F, -74F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Bay12_D0", 0.0F, -90F * f, 0.0F);
        }
    }

    public boolean isSalvo() {
        return this.thisWeaponsName.indexOf("spread") == -1;
    }

    public void typeBomberAdjAltitudePlus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjAttitudePlus();
            return;
        }
        if (this.hasToKG) {
            this.fShipSpeed++;
            if (this.fShipSpeed > 35F) this.fShipSpeed = 35F;
            ToKGUtils.setTorpedoGyroAngle(this.FM, this.fAOB, this.fShipSpeed);
            HUD.log(AircraftHotKeys.hudLogWeaponId, "TOKGSpeed", new Object[] { new Integer((int) this.fShipSpeed) });
            return;
        }
        super.typeBomberAdjAltitudePlus();
    }

    public void typeBomberAdjAltitudeMinus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjAttitudeMinus();
            return;
        }
        if (this.hasToKG) {
            this.fShipSpeed--;
            if (this.fShipSpeed < 0.0F) this.fShipSpeed = 0.0F;
            ToKGUtils.setTorpedoGyroAngle(this.FM, this.fAOB, this.fShipSpeed);
            HUD.log(AircraftHotKeys.hudLogWeaponId, "TOKGSpeed", new Object[] { new Integer((int) this.fShipSpeed) });
            return;
        }
        super.typeBomberAdjAltitudeMinus();
    }

    public void typeBomberAdjSideslipPlus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjSidePlus();
            return;
        }
        if (this.hasToKG) {
            this.fAOB++;
            if (this.fAOB > 180F) this.fAOB = 180F;
            ToKGUtils.setTorpedoGyroAngle(this.FM, this.fAOB, this.fShipSpeed);
            HUD.log(AircraftHotKeys.hudLogWeaponId, "TOKGAOB", new Object[] { new Integer((int) this.fAOB) });
            return;
        }
        super.typeBomberAdjSideslipPlus();
//        this.fSightCurSideslip += 0.05F;
//        if (this.fSightCurSideslip > 3F) this.fSightCurSideslip = 3F;
//        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Float(this.fSightCurSideslip * 10F) });
    }

    public void typeBomberAdjSideslipMinus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjSideMinus();
            return;
        }
        if (this.hasToKG) {
            this.fAOB--;
            if (this.fAOB < -180F) this.fAOB = -180F;
            ToKGUtils.setTorpedoGyroAngle(this.FM, this.fAOB, this.fShipSpeed);
            HUD.log(AircraftHotKeys.hudLogWeaponId, "TOKGAOB", new Object[] { new Integer((int) this.fAOB) });
            return;
        }
        super.typeBomberAdjSideslipMinus();
    }

    public void typeBomberAdjDistancePlus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjAttitudePlus();
            return;
        }
        if (this.hasToKG) {
            this.fShipSpeed++;
            if (this.fShipSpeed > 35F) this.fShipSpeed = 35F;
            ToKGUtils.setTorpedoGyroAngle(this.FM, this.fAOB, this.fShipSpeed);
            HUD.log(AircraftHotKeys.hudLogWeaponId, "TOKGSpeed", new Object[] { new Integer((int) this.fShipSpeed) });
            return;
        }
        super.typeBomberAdjDistancePlus();
    }

    public void typeBomberAdjDistanceMinus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjAttitudeMinus();
            return;
        }
        if (this.hasToKG) {
            this.fShipSpeed--;
            if (this.fShipSpeed < 0.0F) this.fShipSpeed = 0.0F;
            ToKGUtils.setTorpedoGyroAngle(this.FM, this.fAOB, this.fShipSpeed);
            HUD.log(AircraftHotKeys.hudLogWeaponId, "TOKGSpeed", new Object[] { new Integer((int) this.fShipSpeed) });
            return;
        }
        super.typeBomberAdjDistanceMinus();
    }

    public void typeBomberAdjSpeedPlus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjAttitudePlus();
            return;
        }
        if (this.hasToKG) {
            this.fShipSpeed++;
            if (this.fShipSpeed > 35F) this.fShipSpeed = 35F;
            ToKGUtils.setTorpedoGyroAngle(this.FM, this.fAOB, this.fShipSpeed);
            HUD.log(AircraftHotKeys.hudLogWeaponId, "TOKGSpeed", new Object[] { new Integer((int) this.fShipSpeed) });
            return;
        }
        super.typeBomberAdjSpeedPlus();
    }

    public void typeBomberAdjSpeedMinus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjAttitudeMinus();
            return;
        }
        if (this.hasToKG) {
            this.fShipSpeed--;
            if (this.fShipSpeed < 0.0F) this.fShipSpeed = 0.0F;
            ToKGUtils.setTorpedoGyroAngle(this.FM, this.fAOB, this.fShipSpeed);
            HUD.log(AircraftHotKeys.hudLogWeaponId, "TOKGSpeed", new Object[] { new Integer((int) this.fShipSpeed) });
            return;
        }
        super.typeBomberAdjSpeedMinus();
    }

    public boolean typeGuidedBombCisMasterAlive() {
        return this.isMasterAlive;
    }

    public void typeGuidedBombCsetMasterAlive(boolean flag) {
        this.isMasterAlive = flag;
    }

    public boolean typeGuidedBombCgetIsGuiding() {
        return this.isGuidingBomb;
    }

    public void typeGuidedBombCsetIsGuiding(boolean flag) {
        this.isGuidingBomb = flag;
    }

    public void typeX4CAdjSidePlus() {
        this.deltaAzimuth = 0.002F;
    }

    public void typeX4CAdjSideMinus() {
        this.deltaAzimuth = -0.002F;
    }

    public void typeX4CAdjAttitudePlus() {
        this.deltaTangage = 0.002F;
    }

    public void typeX4CAdjAttitudeMinus() {
        this.deltaTangage = -0.002F;
    }

    public void typeX4CResetControls() {
        this.deltaAzimuth = this.deltaTangage = 0.0F;
    }

    public float typeX4CgetdeltaAzimuth() {
        return this.deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage() {
        return this.deltaTangage;
    }

    // public boolean bToFire;
    private float   deltaAzimuth;
    private float   deltaTangage;
    private boolean isGuidingBomb;
    private boolean isMasterAlive;

    protected float fAOB;
    protected float fShipSpeed;
    public boolean  hasToKG;
    protected int   spreadAngle;

    static {
        Class class1 = HE_177A3.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "He-177");
        Property.set(class1, "meshName", "3DO/Plane/He-177A-3/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar02());
        Property.set(class1, "yearService", 1942F);
        Property.set(class1, "yearExpired", 1945F);
        Property.set(class1, "FlightModel", "FlightModels/He-177A-3.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitHE_177A3.class, CockpitHE_177A3_Bombardier.class, CockpitHE_177A3_NGunner.class, CockpitHE_177A3_TGunner.class, CockpitHE_177A3_FGunner.class, CockpitHE_177A3_BGunner.class,
                CockpitHE_177A3_TGunner2.class, CockpitHE_177A3_HGunner.class });
        Property.set(class1, "LOSElevation", 1.0976F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 10, 11, 12, 13, 13, 14, 15, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 });
        Aircraft.weaponHooksRegister(class1,
                new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_MGUN05", "_MGUN06", "_MGUN07", "_BombSpawn00", "_BombSpawn68", "_BombSpawn69", "_BombSpawn01", "_BombSpawn02", "_BombSpawn03", "_BombSpawn04", "_BombSpawn05", "_BombSpawn06",
                        "_BombSpawn07", "_BombSpawn08", "_BombSpawn09", "_BombSpawn10", "_BombSpawn11", "_BombSpawn12", "_BombSpawn13", "_BombSpawn14", "_BombSpawn15", "_BombSpawn16", "_BombSpawn17", "_BombSpawn18", "_BombSpawn19", "_BombSpawn20",
                        "_BombSpawn21a", "_BombSpawn22a", "_BombSpawn23a", "_BombSpawn24a", "_BombSpawn25a", "_BombSpawn26a", "_BombSpawn27a", "_BombSpawn28", "_BombSpawn29a", "_BombSpawn30a", "_BombSpawn31a", "_BombSpawn32a", "_BombSpawn33a",
                        "_BombSpawn34a", "_BombSpawn35a", "_BombSpawn68", "_BombSpawn69", "_BombSpawn07", "_BombSpawn36", "_BombSpawn37a", "_BombSpawn38a", "_BombSpawn39a", "_BombSpawn40a", "_BombSpawn41a", "_BombSpawn42a", "_BombSpawn43a",
                        "_BombSpawn44", "_BombSpawn45a", "_BombSpawn46a", "_BombSpawn47a", "_BombSpawn48a", "_BombSpawn49a", "_BombSpawn50a", "_BombSpawn51a", "_BombSpawn52", "_BombSpawn53a", "_BombSpawn54a", "_BombSpawn55a", "_BombSpawn56a",
                        "_BombSpawn57a", "_BombSpawn58a", "_BombSpawn59a", "_BombSpawn60", "_BombSpawn61a", "_BombSpawn62a", "_BombSpawn63a", "_BombSpawn64a", "_BombSpawn65a", "_BombSpawn66a", "_BombSpawn67a", "_ExternalBomb01", "_ExternalBomb01",
                        "_ExternalBomb02", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb03", "_ExternalBomb11", "_ExternalBomb11", "_ExternalBomb12", "_ExternalBomb12", "_ExternalBomb13", "_ExternalBomb13", "_ExternalBomb21", "_ExternalBomb22",
                        "_ExternalBomb21", "_ExternalBomb22", "_ExternalBomb23", "_ExternalBomb24", "_ExternalBomb23", "_ExternalBomb24", "_ExternalBomb31" });
    }
}
