package com.maddox.il2.objects.air;

import com.maddox.il2.ai.War;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;

public class HampdenMkI extends HAMPDEN {

    public HampdenMkI() {
        this.pilot3kill = false;
        this.BlisTurOpen = false;
    }

    public void moveCockpitDoor(float f) {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, -0.625F);
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if (Config.isUSE_RENDER()) {
            if (Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null) Main3D.cur3D().cockpits[0].onDoorMoved(f);
            this.setDoorSnd(f);
        }
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("Bay3_D0", thisWeaponsName.startsWith("1 x"));
        hierMesh.chunkVisible("Bay4_D0", thisWeaponsName.startsWith("1 x"));
        hierMesh.chunkVisible("PilonTorp_D0", thisWeaponsName.startsWith("1 x"));
        hierMesh.chunkVisible("Bay1_D0", !thisWeaponsName.startsWith("1 x"));
        hierMesh.chunkVisible("Bay2_D0", !thisWeaponsName.startsWith("1 x"));
    }
    
    public void rareAction(float f, boolean flag) {
        Aircraft aircraft = War.getNearestEnemy(this, 6000F);
        if (!this.pilot3kill && aircraft != null && !this.FM.AS.bIsAboutToBailout) {
            this.hierMesh().chunkVisible("Blister3_D0", false);
            this.hierMesh().chunkVisible("BlisturretOpen_D0", true);
            this.BlisTurOpen = true;
        }
        if (!this.pilot3kill && aircraft == null && !this.FM.AS.bIsAboutToBailout && this.BlisTurOpen) {
            this.hierMesh().chunkVisible("Blister3_D0", true);
            this.hierMesh().chunkVisible("BlisturretOpen_D0", false);
            this.BlisTurOpen = false;
        }
    }

    public void update(float f) {
        super.update(f);
        if (!this.FM.isPlayers() && this.FM.Gears.onGround()) if (this.FM.EI.engines[1].getRPM() < 100F) this.FM.CT.cockpitDoorControl = 1.0F;
        else this.FM.CT.cockpitDoorControl = 0.0F;
        if (this.FM.AS.bIsAboutToBailout && this.BlisTurOpen && !this.pilot3kill) {
            this.hierMesh().chunkVisible("BlisturretOpen_D0", false);
            this.hierMesh().chunkVisible("Blister3_D0", true);
            this.BlisTurOpen = false;
        }
    }

    public void doKillPilot(int i) {
        switch (i) {
            case 2:
                this.FM.turret[0].bIsOperable = false;
                break;

            case 3:
                this.FM.turret[1].bIsOperable = false;
                break;
        }
    }

    public void doMurderPilot(int i) {
        switch (i) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                break;

            case 1:
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                this.hierMesh().chunkVisible("HMask2_D0", false);
                break;

            case 2:
                this.pilot3kill = true;
                this.hierMesh().chunkVisible("Pilot3_D0", false);
                this.hierMesh().chunkVisible("Pilot3_D1", true);
                this.hierMesh().chunkVisible("HMask3_D0", false);
                break;

            case 3:
                this.hierMesh().chunkVisible("Pilot4_D0", false);
                this.hierMesh().chunkVisible("Pilot4_D1", true);
                this.hierMesh().chunkVisible("HMask4_D0", false);
                break;

            case 4:
                this.hierMesh().chunkVisible("Pilot5_D0", false);
                this.hierMesh().chunkVisible("Pilot5_D1", true);
                this.hierMesh().chunkVisible("HMask5_D0", false);
                break;
        }
    }

    private boolean pilot3kill;
    private boolean BlisTurOpen;

    static {
        Class class1 = HampdenMkI.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Hampden");
        Property.set(class1, "meshName", "3DO/Plane/Hampden-MkI/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar00());
        Property.set(class1, "yearService", 1936F);
        Property.set(class1, "yearExpired", 1945F);
        Property.set(class1, "FlightModel", "FlightModels/HAMP.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitHampdenMkI.class, CockpitHampdenMkI_Bombardier.class, CockpitHampden_TGunner.class, CockpitHampden_BGunner.class });
        Property.set(class1, "LOSElevation", 0.7394F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 10, 10, 11, 11, 3, 3, 3, 3, 2, 9, 9, 3, 3, 3, 3, 3, 3 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_MGUN05", "_BombSpawn01", "_BombSpawn02", "_BombSpawn03", "_BombSpawn04", "_BombSpawn05", "_ExternalDev01", "_ExternalDev02", "_ExternalBomb01",
                "_ExternalBomb02", "_ExternalBomb04", "_ExternalBomb03", "_ExternalBomb05", "_ExternalBomb06" });
    }
}
