package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3f;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.rts.IniFile;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.CommonTools;
import com.maddox.sound.SoundFX;

public class P_47ModPack extends P_47 {

    public P_47ModPack() {
        this.bFlaps = false;
        this.bFlapsEnd = false;
        this.pTailSway = new Point3f(4.0F, 0F, 0F);
        this.bExtTank = false;
        this.hasRwr = false;
        this.oldBreakControl = false;
        // try {
        // this.aircraftLHclass = Class.forName("com.maddox.il2.objects.air.AircraftLH");
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }

    // private Class aircraftLHclass;

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.isRwrActive = this.hasRwr;
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
        if (!Mission.isNet()) {
            this.FM.CT.bHasCockpitDoorControl = true;
            this.FM.CT.dvCockpitDoor = 1F;
        }
        // this.FM.AS.setCockpitState(this, this.FM.AS.astateCockpitState | 0xFF); // for Cockpit damage testing
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        if (hierMesh.chunkFindCheck("ETank_D0") >= 0) hierMesh.chunkVisible("ETank_D0", false);
        boolean bCenterRackVisible = thisWeaponsName.equalsIgnoreCase("tank") || thisWeaponsName.equalsIgnoreCase("tank2x500") || thisWeaponsName.equalsIgnoreCase("tank6x45") || thisWeaponsName.equalsIgnoreCase("tank2x5006x45")
                || thisWeaponsName.equalsIgnoreCase("1x1000") || thisWeaponsName.equalsIgnoreCase("1x10002x500") || thisWeaponsName.equalsIgnoreCase("1x10006x45") || thisWeaponsName.equalsIgnoreCase("1x10002x5006x45")
                || thisWeaponsName.indexOf("c_") != -1;
        boolean bWingRacksVisible = thisWeaponsName.equalsIgnoreCase("tank2x500") || thisWeaponsName.equalsIgnoreCase("tank2x5006x45") || thisWeaponsName.equalsIgnoreCase("2x500") || thisWeaponsName.equalsIgnoreCase("2x5006x45")
                || thisWeaponsName.equalsIgnoreCase("1x10002x500") || thisWeaponsName.equalsIgnoreCase("1x10002x5006x45") || thisWeaponsName.equalsIgnoreCase("2x1000") || thisWeaponsName.indexOf("w_") != -1;
        if (hierMesh.chunkFindCheck("Rack_D0") >= 0) hierMesh.chunkVisible("Rack_D0", bCenterRackVisible);
        if (hierMesh.chunkFindCheck("RackL_D0") >= 0) hierMesh.chunkVisible("RackL_D0", bWingRacksVisible);
        if (hierMesh.chunkFindCheck("RackR_D0") >= 0) hierMesh.chunkVisible("RackR_D0", bWingRacksVisible);
    }
    
    public void update(float f) {
        super.update(f);
        if (this.hasRwr && this == World.getPlayerAircraft()) try {
            if (this.FM.CT.BrakeControl == 1.0F) {
                if (!this.oldBreakControl) {
                    this.oldBreakControl = true;
                    if (!this.FM.Gears.onGround()) {
                        this.isRwrActive = !this.isRwrActive;
                        HUD.log("RWR " + (this.isRwrActive ? "ON" : "OFF"));
                    }
                }
            } else this.oldBreakControl = false;
        } catch (Exception exception) {}
    }

    public void sfxFlaps(boolean flag) {
        if (flag) {
            this.bFlaps = true;
            this.bFlapsEnd = false;
            printDebugMessage("*** Flaps sound starts");
        }
        if (this.bFlaps && !flag) {
            this.bFlaps = false;
            this.bFlapsEnd = true;
            printDebugMessage("*** Flaps sound ends");
        }
    }

    public void sfxGear(boolean flag) {
        if (this.soundGearUp != null) this.soundGearUp.setPlay(flag);
    }

    public void sfxWheels() {
        if (this.FM.getSpeedKMH() > 0.0F) {
            if (this.soundWheels != null) this.soundWheels.setPlay(true);
            printDebugMessage("*** Wheels sound used");
        }
    }

    public void destroy() {
        if (this.isDestroyed()) return;
        // System.out.println("P_47ModPack destroy 01");
        super.destroy();
        // System.out.println("P_47ModPack destroy 02");
        if (this.soundWheels != null) this.soundWheels.cancel();
        if (this.soundGearDn != null) this.soundGearDn.cancel();
        if (this.soundGearUp != null) this.soundGearUp.cancel();
    }

    public void moveCockpitDoor(float f) {
        this.resetYPRmodifier();
        if (this instanceof P_47B1 || this instanceof P_47B15 || this instanceof P_47BDT || this instanceof P_47C5 || this instanceof P_47D10 || this instanceof P_47D22 || this instanceof ThunderboltMkI) {
            Aircraft.xyz[1] = CommonTools.smoothCvt(f, 0.01F, 0.99F, 0.0F, 0.85F);
            Aircraft.xyz[2] = -CommonTools.smoothCvt(f, 0.01F, 0.99F, 0.0F, 0.025F);
        } else if (this instanceof P_47D27 || this instanceof P_47D || this instanceof ThunderboltMkII) {
            Aircraft.xyz[2] = -CommonTools.smoothCvt(f, 0.01F, 0.99F, 0.0F, 0.65F);
            Aircraft.xyz[1] = CommonTools.smoothCvt(f, 0.01F, 0.99F, 0.0F, 0.025F);
        } else Aircraft.xyz[2] = -CommonTools.smoothCvt(f, 0.01F, 0.99F, 0.0F, 0.65F);
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if (Config.isUSE_RENDER()) {
            if (Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null) Main3D.cur3D().cockpits[0].onDoorMoved(f);
            this.setDoorSnd(f);
        }
    }

    protected void moveFan(float f) {
        if (!Config.isUSE_RENDER()) return;
        int i = this.FM.EI.engines[0].getStage();
        if (i > 0 && i < 6) f = 0.005F * i;
        super.moveFan(f);
        this.hierMesh().chunkSetAngles(Aircraft.Props[0][0], 0.0F, -this.propPos[0] + 45F, 0.0F);
        // if (!BaseGameVersion.is411orLater()) this.hierMesh().chunkSetAngles("Head1_D0", 0.0F, Aircraft.cvt(-this.FM.CT.getRudder(), -1.0F, 1.0F, -60F, 60F), 0.0F);
    }

    protected void moveAirBrake(float f) {
        float f1 = 30F * f;
        this.hierMesh().chunkSetAngles("Brake1_D0", 0.0F, 0.0F, f1);
        this.hierMesh().chunkSetAngles("Brake2_D0", 0.0F, 0.0F, f1);
    }

    void bubbleTopTailSway() {
        float fGunFactor = this.FM.CT.WeaponControl[0] || this.FM.CT.WeaponControl[1] ? 1.1F : 1F;
        Vector3f theTailSway = new Vector3f(0F, (float) Math.sin(Time.current() / 1000F * Math.PI * fGunFactor), 0F);
        float fSwayFactor = this.FM.getSpeed() * this.FM.getSpeed() * fGunFactor * fGunFactor / 30F;
        theTailSway.scale(fSwayFactor);
        Vector3f theTailSwayMomentum = new Vector3f();
        theTailSwayMomentum.cross(this.pTailSway, theTailSway);
        this.FM.producedAM.z += theTailSwayMomentum.z;
    }

    private Point3f        pTailSway;
    public boolean         bExtTank;
    public SoundFX         soundWheels;
    public SoundFX         soundGearDn;
    public SoundFX         soundGearUp;
    public boolean         bFlaps;
    public boolean         bFlapsEnd;
    private static int     flightModelsConfIniValue = -1;
    boolean                hasRwr;
    public boolean         isRwrActive;
    private boolean        oldBreakControl;

    private static boolean _DEBUG                   = false;

    protected static void printDebugMessage(String theMessage) {
        if (_DEBUG) System.out.println(theMessage);
    }

    static boolean useStockFlightModels() {
        try {
            if (!Config.isUSE_RENDER()) return true;
            if (flightModelsConfIniValue == -1) {
                IniFile inifile = new IniFile("conf.ini", 0);
                flightModelsConfIniValue = inifile.get("Mods", "P47PackNewFM", 0);
            }
            return flightModelsConfIniValue == 0;
        } catch (Exception e) {
            return true;
        }
    }
}
