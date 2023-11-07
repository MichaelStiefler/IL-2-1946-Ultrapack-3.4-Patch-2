//***********************************
//This class is for client side players only
//***********************************
package com.maddox.il2.game;

import com.maddox.il2.ai.World;
import com.maddox.il2.ai.ZutiSupportMethods_AI;
import com.maddox.il2.engine.Config;
import com.maddox.il2.fm.Motor;
import com.maddox.il2.fm.ZutiSupportMethods_FM;
import com.maddox.il2.net.BornPlace;
import com.maddox.il2.net.NetUser;
import com.maddox.il2.net.ZutiSupportMethods_NetSend;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.rts.NetEnv;
import com.maddox.rts.Time;

public class ZutiRepairEngine {
    private long      startTime  = 0;
    private float     repairTime = 0;
    private int       engineId   = 0;
    private Motor     motorBkp   = null;
    private Aircraft  playerAC   = null;
    private BornPlace bornPlace  = null;
    private int       engines;

    public ZutiRepairEngine(float repairPenalty, int engines, int inEngineId, Motor originalMotorState) {
        this.engines = engines;
        this.playerAC = World.getPlayerAircraft();
        this.bornPlace = ZutiSupportMethods.isPilotLandedOnBPWithOwnRRRSettings(this.playerAC.FM);
        this.engineId = inEngineId;
        this.motorBkp = originalMotorState;

        int engineRepairTime = Mission.MDS_VARIABLES().zutiReload_EngineRepairSeconds;
        if (this.bornPlace != null) engineRepairTime = this.bornPlace.zutiEngineRepairSeconds;
        if (Config.isUSE_RENDER() && Config.cur.ini.get("Mods", "offlineRRR", 0) == 1 && Mission.isSingle()) engineRepairTime = 1;

        this.repairTime = engineRepairTime * repairPenalty;
        System.out.println("Engine Repair Time: " + engineRepairTime + "s.");
        System.out.println("Penalty Multiplier: x" + repairPenalty);
        System.out.println("--------------------------------------");
        System.out.println("Calculated Repair Time: " + this.repairTime + "s.");

        HUD.log("mds.engineRepairingTime", new java.lang.Object[] { new Integer(this.engineId + 1), new Integer(Math.round(this.repairTime)) });

        engineRepairTime *= 1000;
        this.repairTime *= 1000;

        this.startTime = Time.current();
    }

    /**
     * if -1 is returned abort execution
     *
     * @return
     */
    public int updateTimer() {
        if (!ZutiSupportMethods.allowRRR(this.playerAC)) {
            this.cancelTimer();
            return -1;
        }

        try {
            if (Time.current() - this.startTime > this.repairTime) {
                this.zutiEngineRestore();
                // Collect earned points
                ZutiSupportMethods_AI.collectPoints();

                HUD.log("mds.engineRepairingDone", new java.lang.Object[] { new Integer(this.engineId + 1) });
                System.out.println("AC Engine " + (this.engineId + 1) + " repairs completed.");

                String userData = ZutiSupportMethods.getAircraftCompleteName(this.playerAC);
                String userLocation = ZutiSupportMethods.getPlayerLocation();
                ZutiSupportMethods_NetSend.logMessage((NetUser) NetEnv.host(), userData + " repaired engine >" + (this.engineId + 1) + "< at " + userLocation);
                return -1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.startTime += 1;
        return 0;
    }

    private void cancelTimer() {
        // Return engines if you did not use them
        ZutiSupportMethods_NetSend.returnRRRResources_Engine(this.engines, this.playerAC.pos.getAbsPoint());

        HUD.log("mds.engineRepairingAborted", new java.lang.Object[] { new Integer(this.engineId + 1) });
        System.out.println("AC Engine " + (this.engineId + 1) + " repairs aborted!!!");
    }

    private void zutiEngineRestore() {
        try {
            Motor motor = this.playerAC.FM.EI.engines[this.engineId];

            Aircraft ac = World.getPlayerAircraft();

            // This part is executed when prop is bent/damaged
            /*
             * if(isChunkAnyDamageVisible("Prop" + (engineId + 1)) || isChunkAnyDamageVisible("PropRot" + (engineId + 1))) { ac.hierMesh().chunkVisible(Props[engineId][0], false); ac.hierMesh().chunkVisible(Props[engineId][1], false);
             * ac.hierMesh().chunkVisible(Props[engineId][2], true); }
             */

            // Repair also the prop, in case it was damaged
            ac.hierMesh().chunkVisible(Aircraft.Props[this.engineId][0], true);
            ac.hierMesh().chunkVisible(Aircraft.Props[this.engineId][1], false);
            ac.hierMesh().chunkVisible(Aircraft.Props[this.engineId][2], false);

            // Reset oldProp array to all zeros
            for (int i = 0; i < ac.oldProp.length; i++)
                ac.oldProp[i] = 0;

            // System.out.println("ZutiTimer_RepairEngine prop control: " + motor.isHasControlProp());

            if (this.motorBkp != null) ZutiSupportMethods_FM.restoreMotor(motor, this.motorBkp);

            // call official repairEngine method on this engine, just to send relavant data across the network... and pray it works fine
            this.playerAC.FM.AS.repairEngine(this.engineId);
        } catch (Exception ex) {
            System.out.println("ZutiTimer_RepairEngine error, ID_02: " + ex.toString());
            ex.printStackTrace();
        }
    }
}