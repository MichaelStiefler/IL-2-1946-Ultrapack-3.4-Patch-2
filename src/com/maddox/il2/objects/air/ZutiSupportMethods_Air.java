package com.maddox.il2.objects.air;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.RangeRandom;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.ZutiSupportMethods_AI;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.GunGeneric;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.AircraftState;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.Motor;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.fm.ZutiSupportMethods_FM;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.ZutiAircraft;
import com.maddox.il2.game.ZutiAircraftCrewManagement;
import com.maddox.il2.game.ZutiSupportMethods;
import com.maddox.il2.game.ZutiSupportMethods_Multicrew;
import com.maddox.il2.game.ZutiWeaponsManagement;
import com.maddox.il2.net.BornPlace;
import com.maddox.il2.net.NetUser;
import com.maddox.il2.net.ZutiSupportMethods_Net;
import com.maddox.il2.net.ZutiSupportMethods_NetSend;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.vehicles.planes.PlaneGeneric;
import com.maddox.il2.objects.weapons.BombGun;
import com.maddox.il2.objects.weapons.MissileGun;
import com.maddox.il2.objects.weapons.RocketGun;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.NetEnv;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.SectFile;
import com.maddox.rts.Time;
import com.maddox.util.NumberTokenizer;

public class ZutiSupportMethods_Air {
    private static BornPlace LAST_PROCESSED_BORN_PLACE = null;

    private static final int MSG_CHANGE_LOADOUT        = 90;
    private static final int MSG_RELOAD_WEAPONS        = 91;
    private static final int MSG_START_MISSILE_ENGINE  = 92;

    public static void resetClassVariables() {
        ZutiSupportMethods_Air.LAST_PROCESSED_BORN_PLACE = null;
    }

    /**
     * Method executes actions that as a result backup specified aircraft engines.
     *
     * @param aircraft
     */
    public static void backupAircraftEngines(Aircraft aircraft) {
        try {
            if (aircraft.zutiMotorsArray == null) aircraft.zutiMotorsArray = new ArrayList();

            aircraft.zutiMotorsArray.clear();

            int size = aircraft.FM.EI.engines.length;

            for (int i = 0; i < size; i++) {
                Motor motor = aircraft.FM.EI.engines[i];
                aircraft.zutiMotorsArray.add(ZutiSupportMethods_FM.createEngineBackup(motor));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method returns number of engines that aircraft has.
     *
     * @param aircraft
     * @return
     */
    public static int getNuberOfAircraftEngines(Aircraft aircraft) {
        if (aircraft.zutiMotorsArray == null) return 0;

        return aircraft.zutiMotorsArray.size();
    }

    /**
     * Method loads specified weapons load to the aircraft.
     *
     * @param aircraft
     * @param weapons
     * @throws Exception
     */
    public static void loadWeaponsForAircraft(Aircraft aircraft, String weapons) throws Exception {
        aircraft.weaponsLoad(weapons);
        aircraft.thisWeaponsName = weapons;
    }

    /**
     * Method returns the name of currently loaded weapons loadout.
     *
     * @param aircraft
     * @return
     */
    public static String getCurrentAircraftLoadoutName(Aircraft aircraft) {
        return aircraft.thisWeaponsName;
    }

    /**
     * Method returns specified engine backup class.
     *
     * @param aircraft
     * @param engineId
     * @return
     */
    public static Motor getAircraftMotorBackup(Aircraft aircraft, int engineId) {
        return (Motor) aircraft.zutiMotorsArray.get(engineId);
    }

    /**
     * Method makes sure that aircraft wings are extended.
     *
     * @param aircraft
     */
    public static void unfoldAircraftWings(Aircraft aircraft) {
        if (!aircraft.zutiWingsUnfolded) {
            // give time to things to synchronize
            if (Time.current() - aircraft.zutiLastUnfoldCheck < 2000) return;

            // System.out.println("Checking ..." + this.name());

            aircraft.zutiLastUnfoldCheck = Time.current();

            boolean result = ZutiSupportMethods_Air.isAircraftSpeedGreaterThanCarrierSpeed(aircraft);
            // System.out.println("Result: " + result);
            // System.out.println("Wings : " + FM.CT.wingControl);
            if (aircraft.FM.CT.wingControl > 0.0F && result) aircraft.FM.CT.wingControl = 0.0F;
            // System.out.println("Set to 0.0!");
            else aircraft.zutiUnfoldCheckRepeatCount--;

            if (aircraft.zutiUnfoldCheckRepeatCount == 0) aircraft.zutiWingsUnfolded = true;
            // System.out.println("Wings unfolding check completed!");
        }
    }

    /**
     * Method calculates difference between aircraft and aircraft carrier speeds.
     *
     * @param aircraft
     * @return
     */
    public static boolean isAircraftSpeedGreaterThanCarrierSpeed(Aircraft aircraft) {
        if (aircraft.FM.brakeShoeLastCarrier != null) if (aircraft.FM.brakeShoeLastCarrier instanceof BigshipGeneric) {
            Property property = Property.get(aircraft.FM.brakeShoeLastCarrier.getClass(), "speed");
            if (property == null) return false;

            double result = aircraft.FM.getSpeed() - property.doubleValue();
            // System.out.println("Speed diff: " + result);

            return Math.abs(result) > 20.0D ? true : false;
        }

        return false;
    }

    /**
     * Execute this method after aircraft was created.
     *
     * @param aircraft
     */
    public static void executeWhenAircraftIsCreated(Aircraft aircraft) {
        aircraft.FM.CT.zutiOwnerAircraftName = aircraft.name();

        // TODO: Limit Log output to player planes only // by Storebror
        if (aircraft.FM.CT.zutiOwnerAircraftName.endsWith("_0")) System.out.println("ZSM_Air - Spawning new AC. Owner name is >" + aircraft.FM.CT.zutiOwnerAircraftName + "<");

        // Remember the fuel value of loaded aircraft
        aircraft.zutiAircraftFuel = aircraft.FM.M.fuel;
        String acName = aircraft.name();
        // System.out.println("Aircraft: Current plane fuel capacity: " +
        // zutiAircraftFuel);

        // Make ac engines backup for later repairs
        ZutiSupportMethods_Air.backupAircraftEngines(aircraft);

        // Reset processing of cargo drops since player changed his plane
        ZutiWeaponsManagement.ZUTI_PROCESS_CARGO_DROPS = true;

        // If this is master add AC name as a pilot
        if (Main.cur() != null && Main.cur().netServerParams != null && Main.cur().netServerParams.isMaster()) {
            // OK, we are the net master
            if (acName.endsWith("_0")) {
                // Live name, pilot is owner
                String name = acName;
                name = name.substring(0, name.lastIndexOf("_0"));
                ZutiAircraftCrewManagement.getAircraftCrew(acName).setUserPosition(name, 0);

                // Send live plane owner his crew readout
                ZutiSupportMethods_NetSend.aircraftCrew(ZutiSupportMethods.getNetUser(name), acName);
            } else // AI Plane, it's name as pilot name
                ZutiAircraftCrewManagement.getAircraftCrew(aircraft.name()).setUserPosition(acName, 0);

            // System.out.println("Aircraft - crew printput for: " + acName);
            ZutiAircraftCrewManagement.getAircraftCrew(acName).mapPrintout();
            // System.out.println("----------------------------");
        }

        // Report resources to your side and adjust to available resources - only for
        // single player missions
        if (!Mission.isNet()) ZutiSupportMethods_NetSend.reportSpawnResources(aircraft);

//        System.out.println("executeWhenAircraftIsCreated() " + aircraft.getClass().getName() + ", z=" + aircraft.FM.Loc.z + ", Vwld.length()=" + aircraft.FM.Vwld.length());

    }

    /**
     * Call this method when aircraft is destroyed because in case it is multi-crew enabled aircraft actions need to be performed to synchronize users net positions.
     *
     * @param aircraft
     */
    public static void executeWhenAircraftWasDestroyed(Aircraft aircraft) {
        try {
            if (!aircraft.FM.AS.zutiIsPlaneMultiCrew()) return;
    
            // Request user new net place if plane is multi crew plane because removing it
            // would shuffle AC positions and as a result also netusers net places.
            ZutiSupportMethods_Multicrew.updateNetUsersCrewPlaces();
        } catch (Exception e) {}
    }

    /**
     * Method restores cockpit to it's original state.
     *
     * @param cockpit
     */
    public static void restoreCockpit(Cockpit cockpit) {
        Iterator iter = cockpit.zutiOriginalMeshesStates.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            // System.out.println("---------key: " + (String)entry.getKey() + ", value: " +
            // ((Boolean)entry.getValue()).booleanValue());
            cockpit.mesh.chunkVisible((String) entry.getKey(), ((Boolean) entry.getValue()).booleanValue());

            // System.out.println("Resroting mesh: " + (String)entry.getKey() + ",
            // isVisible: " + ((Boolean)entry.getValue()).booleanValue());
        }
    }

    /**
     * Method backups cockpit meshes.
     *
     * @param cockpit
     */
    public static void backupCockpit(Cockpit cockpit) {
        cockpit.zutiOriginalMeshesStates = new HashMap();

        int chunks = cockpit.mesh.chunks();
        for (int i = 0; i < chunks; i++) {
            cockpit.mesh.setCurChunk(i);
            String chunkName = cockpit.mesh.chunkName();
            boolean isVisible = cockpit.mesh.isChunkVisible();

            // Don't store external body part meshes as I do not repair them
            if (chunkName.startsWith("Wing")) continue;

            // System.out.println(i + " - Storing mesh: " + chunkName + ", isVisible: " +
            // isVisible);
            cockpit.zutiOriginalMeshesStates.put(chunkName, new Boolean(isVisible));
        }
    }

    /**
     * When reloading gunners guns, old owner is lost and it needs to be reset! If this method is not called, gunner can not fire gun any more.
     */
    public static void resetCockpitGunnerWeaponOwner(CockpitGunner cockpitGunner) {
        BulletEmitter[] abulletemitter = cockpitGunner.aircraft().FM.CT.Weapons[cockpitGunner.weaponControlNum()];
        if (abulletemitter != null) cockpitGunner.emitter = abulletemitter[0];
        // System.out.println("CockpitGunner: emitter reassigned to: " + emitter);
    }

    /**
     * Once player enters, leaves or is kicked from gunner position, set autopilot for that gunner position ON. If it is left OFF, sycing problems occurs and other players are unable to use that position effectively.
     */
    public static void setGunnerAutopilotOn() {
        try {
            HotKeyCmd.exec("misc", "cockpitRealOff" + Main3D.cur3D().cockpitCurIndx());
        } catch (Exception ex) {}
    }

    /**
     * Check if selected aircraft is available at net user selected born place. Null is returned if AC is not available. Method also decreases AC numbers if it is available.
     *
     * @param aircraft
     * @param netUser
     * @return
     */
    public static ZutiAircraft getAircraft(Aircraft aircraft, int homeBaseId) {

        // System.out.println("Spawning player name : " + ((AircraftNet)
        // netaircraft.net).netUser.uniqueName());
        // System.out.println("Spawning player bp id: " + ((AircraftNet)
        // netaircraft.net).netUser.getBornPlace());
        // System.out.println(((Aircraft)this).getClass());
        // System.out.println("NetAircraft spawning player name : " +
        // netUser.uniqueName());
        // System.out.println("NetAircraft spawning player bp id: " +
        // netUser.getBornPlace());
        String currentAcName = Property.stringValue(aircraft.getClass(), "keyName");
        // System.out.println("NetAircraft spawning player plane: " + currentAcName);

        return getAircraft(currentAcName, homeBaseId);
    }

    /**
     * Check if selected aircraft is available at net user selected born place. Null is returned if AC is not available. Method also decreases AC numbers if it is available.
     *
     * @param aircraft
     * @param netUser
     * @return
     */
    public static ZutiAircraft getAircraft(String currentAcName, int homeBaseId) {
        try {
            BornPlace zutiPilotsBornPlace = (BornPlace) World.cur().bornPlaces.get(homeBaseId);
            if (zutiPilotsBornPlace != null) // if we don't have limiting planes enabled for selected home base, return true
                // as all planes are available. Or, if that is enabled and plane is available...
                // also return true then.
                if (zutiPilotsBornPlace.zutiEnablePlaneLimits) return ZutiSupportMethods_Net.getAircraftAtBornPlace(zutiPilotsBornPlace, currentAcName);
                else return new ZutiAircraft();
            // System.out.println(((Aircraft)this).typedName());
            // String string = I18N.plane(Property.stringValue(((Aircraft)this).getClass(),
            // "keyName"));
            // String string = I18N.plane(Property.stringValue(((Aircraft)this).getClass(),
            // "cockpitClass"));
            // String string = Property.stringValue(((Aircraft)this).getClass(),
            // "cockpitClass");
            /*
             * String currentAcName = Property.stringValue(((Aircraft)this).getClass(), "keyName"); System.out.println("NetAircraft - releasing plane: " + currentAcName); zutiPilotsBornPlace.zutiReleaseAircraft(currentAcName);
             */
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Notify net players that specified aircraft executed loadout change command.
     *
     * @param aircraft
     * @param loadoutId
     * @param loadout
     * @return
     */
    public static boolean sendNetAircraftLoadoutChange(Aircraft aircraft, int loadoutId, String loadout) {
        if (!aircraft.isNet() || aircraft.net.countMirrors() == 0) return false;

        try {
            System.out.println("ZutiSupportMethods_Air: sending loadout change to >" + loadout + "< command for aircraft >" + aircraft.name() + "< to mirrors!");
            NetMsgGuaranted netMsgGuaranted = new NetMsgGuaranted();
            netMsgGuaranted.writeByte(MSG_CHANGE_LOADOUT);
            netMsgGuaranted.writeInt(loadoutId);
            netMsgGuaranted.write255(loadout);
            aircraft.net.post(netMsgGuaranted);
            // net.postTo(Main.cur().netServerParams.masterChannel(), netMsgGuaranted);
            return true;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Notify net players that specified aircraft executed RRR command.
     *
     * @param aircraft
     * @param weaponsId:   0=guns, 1=rockets, 2=bombs
     * @param amount:      used for bullets and rockets amount reporting
     * @param bombsAmount: used for reporting bombs amount
     * @return
     */
    public static boolean sendNetAircraftRearmOrdinance(Aircraft aircraft, int weaponsId, long amount, int[] bombsAmount) {
        // TODO: +++ RRR Bug hunting
        String bombsArray = "";
        if (bombsAmount == null) bombsArray = "null";
        else {
            bombsArray = "{ ";
            for (int bai = 0; bai < bombsAmount.length; bai++) {
                if (bai > 0) bombsArray += ", ";
                bombsArray += bombsAmount[bai];
            }
            bombsArray += " }";
        }
        ZutiWeaponsManagement.printDebugMessage(aircraft, "ZutiSupportMethods_Air sendNetAircraftRearmOrdinance(" + aircraft.getClass().getName() + ", " + weaponsId + ", " + amount + ", " + bombsArray + " )");
        // --- RRR Bug hunting
        if (!aircraft.isNet() || aircraft.net.countMirrors() == 0) return false;

        try {
//        	System.out.println("ZutiSupportMethods_Air: sending reload weapons >" + weaponsId + "< command for aircraft >" + aircraft.name() + "< to mirrors!");
            NetMsgGuaranted netMsgGuaranted = new NetMsgGuaranted();
            netMsgGuaranted.writeByte(MSG_RELOAD_WEAPONS);
            netMsgGuaranted.writeInt(weaponsId);

            if (bombsAmount == null) {
                netMsgGuaranted.writeLong(amount);
                // TODO: +++ RRR Bug hunting
                ZutiWeaponsManagement.printDebugMessage(aircraft, "ZutiSupportMethods_Air sendNetAircraftRearmOrdinance netMsgGuaranted has no bombs array");
                // --- RRR Bug hunting
            } else {
                netMsgGuaranted.writeInt(bombsAmount[0]);
                netMsgGuaranted.writeInt(bombsAmount[1]);
                netMsgGuaranted.writeInt(bombsAmount[2]);
                netMsgGuaranted.writeInt(bombsAmount[3]);
                netMsgGuaranted.writeInt(bombsAmount[4]);
                netMsgGuaranted.writeInt(bombsAmount[5]);
                // TODO: +++ RRR Bug hunting
                ZutiWeaponsManagement.printDebugMessage(aircraft, "ZutiSupportMethods_Air sendNetAircraftRearmOrdinance netMsgGuaranted has bombs array");
                // --- RRR Bug hunting
            }

            // TODO: +++ RRR Bug hunting
            ZutiWeaponsManagement.printDebugMessage(aircraft, "ZutiSupportMethods_Air sendNetAircraftRearmOrdinance sending netMsgGuaranted");
            // --- RRR Bug hunting
            aircraft.net.post(netMsgGuaranted);
            // TODO: +++ RRR Bug hunting
            ZutiWeaponsManagement.printDebugMessage(aircraft, "ZutiSupportMethods_Air sendNetAircraftRearmOrdinance netMsgGuaranted sent");
            // --- RRR Bug hunting
            // net.postTo(Main.cur().netServerParams.masterChannel(), netMsgGuaranted);
            return true;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Notify net players that specified aircraft started a missile engine in preparation for launch.
     *
     * @param aircraft
     * @param loadoutId
     * @param loadout
     * @return
     */
    public static boolean sendNetAircraftMissileEngineStart(Actor actor, MissileGun missileGun) {
//		System.out.println("ZutiSupportMethods_Air sendNetAircraftMissileEngineStart(" + actor.hashCode() + ", " + missileGun.hashCode()
//		+ ", actor.isNet()=" + actor.isNet() + ", actor.isNetMirror()=" + actor.isNetMirror() + ", actor.isNetMaster()=" + actor.isNetMaster()
//		+ " actor.net.countMirrors()=" + (actor.isNet()?actor.net.countMirrors():0));
        if (!Actor.isValid(actor) || !(actor instanceof Aircraft) || !(actor instanceof TypeGuidedMissileCarrier)) return false;
        Aircraft aircraft = (Aircraft) actor;
        if (!aircraft.isNet() || aircraft.net.countMirrors() == 0) return false;

        byte trigger = -1;
        byte index = -1;

        try {
            for (byte l = 0; l < aircraft.FM.CT.Weapons.length; l++)
                if (aircraft.FM.CT.Weapons[l] != null) for (byte l1 = 0; l1 < aircraft.FM.CT.Weapons[l].length; l1++)
                    if (aircraft.FM.CT.Weapons[l][l1] != null) if (aircraft.FM.CT.Weapons[l][l1].equals(missileGun)) {
                        trigger = l;
                        index = l1;
                        break;
                    }
        } catch (Exception exception) {
            EventLog.type("Exception in sendNetAircraftMissileEngineStart: " + exception.getMessage());
        }

        if (trigger < 0 || index < 0) return false;

        try {
//			System.out.println("ZutiSupportMethods_Air: sending missile engine start trigger >" + trigger
//					+ "< index >" + index + "< command for aircraft >" + aircraft.name() + "< to mirrors!");
            NetMsgGuaranted netMsgGuaranted = new NetMsgGuaranted();
            netMsgGuaranted.writeByte(MSG_START_MISSILE_ENGINE);
            netMsgGuaranted.writeByte(trigger);
            netMsgGuaranted.writeByte(index);
            aircraft.net.post(netMsgGuaranted);
            return true;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Call this method when net aircraft receives new net command.
     *
     * @param aircraft
     * @param i:          command id
     * @param netmsginput
     * @return
     */
    public static boolean processNetAircraftMirroredMessage(Aircraft aircraft, int i, NetMsgInput netmsginput) {
        try {
            switch (i) {
                case MSG_CHANGE_LOADOUT: {
                    // Loadout changed
                    int loadoutId = netmsginput.readInt();
                    String selectedLoadout = netmsginput.read255();

//					System.out.println("NetAircraft: received change loadout to >" + loadoutId + "< command!");

                    // TODO: +++ RRR Bug hunting
//					if( Config.isUSE_RENDER() ) // This if clause causes the net message not to populate across a dedicated server
//					{
                    // --- RRR Bug hunting

                    // Clear all ammo
                    ZutiWeaponsManagement.unloadLoadedWeapons(aircraft);
                    // Clear old pylons
                    ZutiWeaponsManagement.removePylons(aircraft);
                    // System.out.println("ZutiTimer_ChangeLoadout preparations complete!");
                    ZutiWeaponsManagement.preloadLoadOptions(aircraft, loadoutId, selectedLoadout);

                    ZutiWeaponsManagement.changeLoadout(aircraft, selectedLoadout, false);

                    // TODO: +++ RRR Bug hunting
//					}
                    // --- RRR Bug hunting

                    // Forward this message to remaining clients
                    ZutiSupportMethods_Air.sendNetAircraftLoadoutChange(aircraft, loadoutId, selectedLoadout);
                    return true;
                }
                case MSG_RELOAD_WEAPONS: {
                    int weaponsId = netmsginput.readInt();
                    // TODO: +++ RRR Bug hunting
                    ZutiWeaponsManagement.printDebugMessage(aircraft, "ZutiSupportMethods_Air processNetAircraftMirroredMessage(" + aircraft.getClass().getName() + ", MSG_RELOAD_WEAPONS, NetMsgInput), weaponsId = " + weaponsId);
                    // --- RRR Bug hunting

//					System.out.println("NetAircraft: received reload weapons >" + weaponsId + "< command!");
                    long bulletsRockets = -1;
                    int[] bombs = null;

                    // TODO: +++ RRR Bug hunting
//					if( Config.isUSE_RENDER() ) // This if clause causes the net message not to populate across a dedicated server
//					{
                    // --- RRR Bug hunting
                    switch (weaponsId) {
                        case 0:
                            bulletsRockets = netmsginput.readLong();
                            // TODO: +++ RRR Bug hunting
                            ZutiWeaponsManagement.printDebugMessage(aircraft, "ZutiSupportMethods_Air processNetAircraftMirroredMessage bulletsRockets = " + bulletsRockets);
                            // --- RRR Bug hunting
                            ZutiWeaponsManagement.rearmMGs_Cannons(aircraft, bulletsRockets);
                            break;
                        case 1:
                            bulletsRockets = netmsginput.readLong();
                            // TODO: +++ RRR Bug hunting
                            ZutiWeaponsManagement.printDebugMessage(aircraft, "ZutiSupportMethods_Air processNetAircraftMirroredMessage bulletsRockets = " + bulletsRockets);
                            // --- RRR Bug hunting
                            ZutiWeaponsManagement.rearmRockets(aircraft, bulletsRockets);
                            break;
                        case 2:
                            // TODO: +++ RRR Bug hunting
                            // bombs = new int[]{0,0,0,0,0,0,0};
                            bombs = new int[] { 0, 0, 0, 0, 0, 0 }; // Good morning Zuti, index 0-5 is 6 elements, not 7.
                            // --- RRR Bug hunting
                            bombs[0] = netmsginput.readInt();
                            bombs[1] = netmsginput.readInt();
                            bombs[2] = netmsginput.readInt();
                            bombs[3] = netmsginput.readInt();
                            bombs[4] = netmsginput.readInt();
                            bombs[5] = netmsginput.readInt();

                            // TODO: +++ RRR Bug hunting
                            String bombsArray = "";
                            bombsArray = "{ ";
                            for (int bai = 0; bai < bombs.length; bai++) {
                                if (bai > 0) bombsArray += ", ";
                                bombsArray += bombs[bai];
                            }
                            bombsArray += " }";
                            ZutiWeaponsManagement.printDebugMessage(aircraft, "ZutiSupportMethods_Air processNetAircraftMirroredMessage bombs = " + bombsArray);
//								ZutiWeaponsManagement.rearmBombsFTanksTorpedoes(aircraft, bombs);
                            ZutiWeaponsManagement.rearmBombsFTanksTorpedoes(aircraft, (int[]) bombs.clone()); // Objects like arrays are passed as reference, this would reduce the number of bombs to reload before the reload command is sent to other
                                                                                                              // clients!
                            // --- RRR Bug hunting
                            break;
                    }
                    // TODO: +++ RRR Bug hunting
//					}
                    // --- RRR Bug hunting
                    // Forward this message to remaining clients
                    // TODO: +++ RRR Bug hunting
                    String bombsArray = "";
                    if (bombs == null) bombsArray = "null";
                    else {
                        bombsArray = "{ ";
                        for (int bai = 0; bai < bombs.length; bai++) {
                            if (bai > 0) bombsArray += ", ";
                            bombsArray += bombs[bai];
                        }
                        bombsArray += " }";
                    }
                    ZutiWeaponsManagement.printDebugMessage(aircraft,
                            "ZutiSupportMethods_Air processNetAircraftMirroredMessage calling ZutiSupportMethods_Air.sendNetAircraftRearmOrdinance(" + aircraft.getClass().getName() + ", " + weaponsId + ", " + bulletsRockets + ", " + bombsArray + ")");
                    // --- RRR Bug hunting
                    ZutiSupportMethods_Air.sendNetAircraftRearmOrdinance(aircraft, weaponsId, bulletsRockets, bombs);
                    // TODO: +++ RRR Bug hunting
                    ZutiWeaponsManagement.printDebugMessage(aircraft, "ZutiSupportMethods_Air processNetAircraftMirroredMessage ZutiSupportMethods_Air.sendNetAircraftRearmOrdinance called");
                    // --- RRR Bug hunting
                    return true;
                }
                case MSG_START_MISSILE_ENGINE: {
                    byte trigger = netmsginput.readByte();
                    byte index = netmsginput.readByte();
//				System.out.println("ZutiSupportMethods_Air MSG_START_MISSILE_ENGINE trigger=" + trigger + ", index=" + index);
                    if (!(aircraft.FM.CT.Weapons[trigger][index] instanceof MissileGun)) return true;
                    MissileGun missileGun = (MissileGun) aircraft.FM.CT.Weapons[trigger][index];
                    missileGun.netStartEngine();
                    ZutiSupportMethods_Air.sendNetAircraftMissileEngineStart(aircraft, missileGun);
                    return true;
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        return false;
    }

    /**
     * Align aircraft based on the terrain it is spawning on.
     *
     * @param aircraft
     */
    public static void alignAircraftToLandscape(NetAircraft aircraft) {
        Point3d p = new Point3d();
        Vector3d n = new Vector3d();
        Orient o = new Orient();

        aircraft.pos.getAbs(p, o);
        p.z = Engine.land().HQ(p.x, p.y) + aircraft.FM.Gears.H;
        o.increment(0.0F, -aircraft.FM.Gears.Pitch, 0.0F);
        Engine.land().N(p.x, p.y, n);
        o.orient(n);
        o.increment(0.0F, aircraft.FM.Gears.Pitch, 0.0F);

        long l = (long) (p.x % 2.3D * 221D + p.y % 3.4D * 211D * 211D);
        RangeRandom rangerandom = new RangeRandom(l);
        p.z += rangerandom.nextFloat(0.1F, 0.4F);

        aircraft.pos.setAbs(p, o);
    }

    /**
     * Method empties all resources from specified aircraft. Fuel is set to 0.
     *
     * @param ac
     */
    public static void emptyAircraft(Aircraft aircraft) {
        if (aircraft == null) return;

        aircraft.FM.M.fuel = 0;

        BulletEmitter[][] weapons = aircraft.FM.CT.Weapons;

        for (int i = 0; i < weapons.length; i++) {
            if (weapons[i] == null) continue;

            try {
                for (int j = 0; j < weapons[i].length; j++)
                    if (weapons[i][j] instanceof GunGeneric) ((GunGeneric) weapons[i][j]).loadBullets(0);
                    else if (weapons[i][j] instanceof RocketGun) ((RocketGun) weapons[i][j]).loadBullets(0);
                    else if (weapons[i][j] instanceof BombGun) ((BombGun) weapons[i][j]).loadBullets(0);
            } catch (Exception ex) {}
        }
    }

    /**
     * Method determines if aircraft has landed undamaged or not.
     *
     * @param fm
     * @return
     */
    public static boolean isAircraftOnTheGroundAndUndamaged(FlightModel fm) {
        if (fm.Gears.isAnyDamaged() && fm.getSpeedKMH() < 1 || !fm.isCapableOfBMP() || !fm.isCapableOfTaxiing() || fm.isSentControlsOutNote()) return false;

        return true;
    }

    /**
     * Method checks if bailing was done on ground/water.
     *
     * @param flightmodel
     * @return
     */
    public static boolean isBailedOnGroundOrDeck(Aircraft aircraft) {
        if (aircraft == null) return false;

        Point3d pos = aircraft.pos.getAbsPoint();
        if (World.land().isWater(pos.x, pos.y)) {
            // Carrier rules apply for this case
            if (aircraft.FM.getSpeedKMH() < ZutiSupportMethods_AI.MAX_SPEED_FOR_DECK_BAILOUT && aircraft.FM.getAltitude() < ZutiSupportMethods_AI.MAX_HEIGHT_FOR_DECK_BAILOUT) return true;
        } else if (aircraft.FM.getSpeedKMH() < 10.0D && aircraft.FM.getVertSpeed() < 2.0D) return true;
        return false;
    }

    /**
     * Method indicates if aircraft is still able to fly or not.
     *
     * @param fm
     * @return
     */
    public static boolean isAircraftUsable(FlightModel fm) {
        /*
         * System.out.println("  isAnyDamaged: " + fm.Gears.isAnyDamaged()); System.out.println("  fm.getSpeedKMH(): " + fm.getSpeedKMH()); System.out.println("  fm.isCapableOfBMP(): " + fm.isCapableOfBMP());
         * System.out.println("  fm.isCapableOfTaxiing(): " + fm.isCapableOfTaxiing()); System.out.println("  fm.isSentControlsOutNote(): " + fm.isSentControlsOutNote());
         */
        if (!fm.Gears.isAnyDamaged() && fm.getSpeedKMH() < 1 && fm.isCapableOfBMP() && fm.isCapableOfTaxiing() && !fm.isSentControlsOutNote()) return true;

        return false;
    }

    /**
     * Method returns aircraft name from its actor name (string between $ and @ chars in actor.toString()).
     *
     * @param value
     * @return
     */
    public static String getStaticAcNameFromActor(Actor actor) {
        if (actor == null) return "";

        String value = actor.toString();

        try {
            return value.substring(value.indexOf("$") + 1, value.indexOf("@"));
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * This method returns wing position only if that wing is still on the ground! If it is not, null is returned as a result.
     *
     * @param sectfile
     * @param string
     * @return
     */
    public static Point3d getWingTakeoffLocation(SectFile sectfile, String string) {
        String wingName = string + "_Way";

        int index = sectfile.sectionIndex(wingName);
        if (index < 0) return null;

        int variables = sectfile.vars(index);
        if (variables < 0) return null;

        String line = sectfile.var(index, 0);
        if (line.equalsIgnoreCase("TAKEOFF")) {
            NumberTokenizer numbertokenizer = new NumberTokenizer(sectfile.value(index, 0));
            Point3d point = new Point3d();
            point.x = numbertokenizer.next(0.0F, -1000000.0F, 1000000.0F);
            point.y = numbertokenizer.next(0.0F, -1000000.0F, 1000000.0F);

            return point;
        }

        return null;
    }

    /**
     * Methdo creates default zuti aircraft objects. It's settings include: default number of aircraft, max fuel selection at 100%, "Default" loadout.
     *
     * @param acName
     * @return
     */
    public static ZutiAircraft createDefaultZutiAircraft(String acName) {
        ZutiAircraft zac = new ZutiAircraft();

        zac.setAcName(acName);
        zac.setMaxFuelSelection(9);
        ArrayList loadouts = new ArrayList();
        loadouts.add("Default");
        zac.setSelectedWeapons(loadouts);

        return zac;
    }

    /**
     * Call this method when a stationary plane was destroyed. Method will find born place that this plane belonged to (based on location of the plane and born place radius) and decrease plane number for that plane, if born place counts stationary planes
     * as valid planes for overall plane numbers.
     *
     * @param stationaryPlane
     */
    public static void decreaseBornPlacePlaneCounter(PlaneGeneric stationaryPlane) {
        String acName = ZutiSupportMethods.getAircraftName_I18N(ZutiSupportMethods_Air.getStaticAcNameFromActor(stationaryPlane));

        // System.out.println(" Destroyed AC >" + acName + "<"); Patch Pack 107, reduce
        // logging

        Point3d point3d = stationaryPlane.pos.getAbsPoint();
        double x = point3d.x;
        double y = point3d.y;

        try {
            boolean processed = false;
            if (LAST_PROCESSED_BORN_PLACE != null) {
                // let's check first if destroyed static AC is in last bp area...
                double distance = Math.sqrt(Math.pow(LAST_PROCESSED_BORN_PLACE.place.x - x, 2) + Math.pow(LAST_PROCESSED_BORN_PLACE.place.y - y, 2));
                if (distance <= LAST_PROCESSED_BORN_PLACE.r) {
                    // jup, inside this bp destroyed ac was, alter that plane number
                    ZutiSupportMethods_Net.removeAircraftAtBornPlace(LAST_PROCESSED_BORN_PLACE, acName);
                    // System.out.println("WORLD1 ------------------------ " + acName);
                    processed = true;
                }
            }

            if (!processed) {
                List bornPlaces = World.cur().bornPlaces;
                if (bornPlaces == null) return;

                BornPlace bp = null;
                for (int i = 0; i < bornPlaces.size(); i++) {
                    bp = (BornPlace) bornPlaces.get(i);

                    // If bornplace does not allow static planes affect on plane limitations, skip
                    // it and move to other ones
                    if (!bp.zutiIncludeStaticPlanes) continue;

                    // check if destroyed plane was inside correct born place radius
                    double distance = Math.pow(bp.place.x - x, 2) + Math.pow(bp.place.y - y, 2);
                    if (distance <= bp.r * bp.r) {
                        // jup, inside this bp destroyed ac was, alter that plane number
                        ZutiSupportMethods_Net.removeAircraftAtBornPlace(bp, acName);
                        // System.out.println("WORLD2 ------------------------ " + acName);
                        LAST_PROCESSED_BORN_PLACE = bp;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method returns aircraft name.
     *
     * @param aircraft
     * @return
     */
    public static String getAircraftI18NName(Class aircraftClass) {
        if (aircraftClass != null) return Property.stringValue(aircraftClass, "keyName");

        return null;
    }

    /**
     * Call this method when glider aircraft type landed. Based on its type certain amount of paratroopers is loaded to home base in which glider landed.
     *
     * @param aircraft
     */
    public static void processGliderLanding(Aircraft aircraft) {
        if (aircraft == null || !(aircraft instanceof TypeGlider)) return;

        if (Main.cur().netServerParams == null || !Main.cur().netServerParams.isMaster()) return;

        Point3d pos = aircraft.pos.getAbsPoint();
        BornPlace bp = ZutiSupportMethods.getParatrooperAreaBornPlace(pos.x, pos.y, aircraft.getArmy());

        if (bp != null) {
            int paratroopers = 0;
            // Determine number of paratroopers based on glider type
            if (aircraft instanceof ME_321) paratroopers = 130;
            else if (aircraft instanceof G_11) paratroopers = 11;

            bp.zutiParatroopersInsideHomeBaseArea += paratroopers;
            System.out.println("ZSM_A - Glider landed... number of hostile paratroopers inside home base at x=" + bp.place.x + " and y=" + bp.place.y + " is: " + bp.zutiParatroopersInsideHomeBaseArea + ". Needed: " + bp.zutiCapturingRequiredParatroopers);

            ZutiSupportMethods.isBornPlaceOverrunByPara(bp, aircraft.getArmy());
        }
    }

    /**
     * Call this method to destroy aircraft by directly calling .destroy on it or by bailing the pilot (set bail to true).
     *
     * @param bail
     */
    public static void destroyPlayerAircraft(boolean bail) {
        ZutiSupportMethods_FM.UPDATE_DECK_TIMER = false;

        ZutiSupportMethods_AI.collectPoints();
        ZutiSupportMethods_NetSend.ejectPlayer((NetUser) NetEnv.host());
        if (World.getPlayerFM() instanceof RealFlightModel) ZutiSupportMethods_NetSend.ejectAircraftCrew(World.getPlayerAircraft());

        ZutiSupportMethods_Multicrew.ejectPlayerGunner();

        Main.cur().resetUser();
        Aircraft aircraft = World.getPlayerAircraft();
        if (aircraft != null && aircraft.FM instanceof RealFlightModel) {
            ZutiSupportMethods_NetSend.releaseCarrierSpawnPlace((NetUser) NetEnv.host());
            ZutiSupportMethods_FM.UPDATE_VULNERABILITY_TIMER = false;
            ZutiSupportMethods_FM.UPDATE_DECK_TIMER = false;

            if (!bail) aircraft.destroy();
            else {
                AircraftState.bCheckPlayerAircraft = false;
                aircraft.hitDaSilk();
                AircraftState.bCheckPlayerAircraft = true;
            }
        }
    }
}
