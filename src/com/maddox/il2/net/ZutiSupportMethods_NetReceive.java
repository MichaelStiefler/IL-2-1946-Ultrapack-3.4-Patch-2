package com.maddox.il2.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.DataFormatException;

import com.maddox.gwindow.GWindowMessageBox;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.Front;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.fm.ZutiSupportMethods_FM;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.ZutiAircraft;
import com.maddox.il2.game.ZutiAircraftCrew;
import com.maddox.il2.game.ZutiAircraftCrewManagement;
import com.maddox.il2.game.ZutiSupportMethods;
import com.maddox.il2.game.ZutiSupportMethods_Multicrew;
import com.maddox.il2.game.ZutiSupportMethods_ResourcesManagement;
import com.maddox.il2.gui.ZutiSupportMethods_GUI;
import com.maddox.il2.objects.ActorCrater;
import com.maddox.il2.objects.ActorSnapToLand;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.Cockpit;
import com.maddox.il2.objects.air.CockpitPilot;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.air.NetGunner;
import com.maddox.il2.objects.air.TypeBomber;
import com.maddox.il2.objects.air.ZutiSupportMethods_Air;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.vehicles.artillery.ArtilleryGeneric;
import com.maddox.il2.objects.vehicles.tanks.TankGeneric;
import com.maddox.rts.NetEnv;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.RTSConf;

public class ZutiSupportMethods_NetReceive {
    /**
     * Call this method from NetUser class whenever we receive new message
     *
     * @param netuser:    netuser that send message. You can fetch username from this class.
     * @param netmsginput
     * @param messageId
     * @return
     */
    public static boolean processReceivedMessage(NetUser sender, NetMsgInput netmsginput, byte messageId) {
        try {
            if (messageId < ZutiSupportMethods_NetSend.TEST_MESSAGE) return false;

            if (sender == null || netmsginput == null) return false;

            switch (messageId) {
                case ZutiSupportMethods_NetSend.TEST_MESSAGE:
                    if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend.testMessage(sender);
                    else testMessage();
                    return true;
                case ZutiSupportMethods_NetSend.USER_PENALIZED:
                    if (Main.cur().netServerParams.isMaster()) setUserTimePenalty(sender, netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.AIRCRAFT_NOT_AVAILABLE:
                    aircraftNotAvailable();
                    return true;
                case ZutiSupportMethods_NetSend.REMOVE_TARGET:
                    if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend.removeTargets(sender);
                    else removeTarget(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.AWARDED_TINSPECT:
                    awardedForInspectingArea();
                    return true;
                case ZutiSupportMethods_NetSend.AIRCRAFT_LIST:
                    if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend.forwardAircraftList(sender);
                    else aircraftList(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.AIRCRAFT_LIST_COMPRESSED:
                    if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend.forwardAircraftList(sender);
                    else aircraftListCompressed(netmsginput, true);
                    return true;
                case ZutiSupportMethods_NetSend.EJECT_CREW_MEMBER:
                    ejectPlayer(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.PARA_CAPTURED_HB:
                    if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend.paraCapturedHomeBases(sender);
                    else paraCapturedHomeBase(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.BOMBARDIER_RELEASED_ORDINANCE:
                    if (Main.cur().netServerParams.isMaster()) {
                        String acName = netmsginput.read255();
                        boolean bool = netmsginput.readBoolean();
                        boolean hasBombBayDoors = netmsginput.readBoolean();

                        ZutiSupportMethods_NetSend_ToClients.bombardierReleasedOrdinance_ToClient(acName, bool, hasBombBayDoors);
                    } else bombardierReleasedOrdinance(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.BOMB_BAY_DOORS_STATUS:
                    if (Main.cur().netServerParams.isMaster()) {
                        String acName = netmsginput.read255();
                        byte status = netmsginput.readByte();

                        ZutiSupportMethods_NetSend_ToClients.bombBayDoorsStatus_ToClient(acName, status);
                    } else bombBayDoorsStatus(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.AIRCRAFT_WITH_RELEASED_ORDINANCE:
                    if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend.aircraftWithReleasedOrdinance(sender);
                    else aircraftWithReleasedOrdinance(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.BOMBARDIER_INSTRUMENTS_CHANGED:
                    if (Main.cur().netServerParams.isMaster()) {
                        String acName = netmsginput.read255();
                        int distance = netmsginput.readByte();
                        int sideslip = netmsginput.readByte();
                        int altitude = netmsginput.readByte();
                        int speed = netmsginput.readByte();
                        int gunsightAutomation = netmsginput.readByte();

                        ZutiSupportMethods_NetSend_ToClients.bombardierInstrumentsChanged_ToClient(sender, acName, distance, sideslip, altitude, speed, gunsightAutomation);

                        if (Config.isUSE_RENDER()) {
                            netmsginput.reset();
                            bombardierInstrumentsChanged(netmsginput);
                        }
                    } else bombardierInstrumentsChanged(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.SYNC_CREW_SCORE:
                    if (Main.cur().netServerParams.isMaster()) {
                        String acName = netmsginput.read255();
                        String actorName = netmsginput.read255();
                        boolean damagedOnly = netmsginput.readBoolean();
                        double explosionPower = netmsginput.readDouble();

                        ZutiSupportMethods_NetSend_ToClients.syncCrewScore_ToClient(sender, acName, actorName, damagedOnly, explosionPower);
                    } else syncCrewScore(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.TRANSFER_CONTROLS: {
                    String username = netmsginput.read255();
                    if (Main.cur().netServerParams.isMaster()) {
                        if (isCommandMeantForUs(username)) transferControls(username);
                        else ZutiSupportMethods_NetSend_ToClients.transferControls_ToClient(sender, username);
                    } else transferControls(username);
                    return true;
                }
                case ZutiSupportMethods_NetSend.AIRCRAFT_CONTROLS_MOVED:
                    if (Main.cur().netServerParams.isMaster()) {
                        String username = netmsginput.read255();
                        int eventId = netmsginput.readByte();
                        float controlState = netmsginput.readFloat();

                        ZutiSupportMethods_NetSend_ToClients.aircraftControlsMoved_ToClient(username, eventId, controlState);
                        /*
                         * if( Config.isUSE_RENDER() ) { netmsginput.reset(); aircraftControlsMoved(netmsginput); }
                         */
                    } else aircraftControlsMoved(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.AIRCRAFT_CONTROLS_CHANGED:
                    if (Main.cur().netServerParams.isMaster()) {
                        String acName = netmsginput.read255();
                        int eventId = netmsginput.readByte();
                        boolean weaponControl = netmsginput.readBoolean();

                        ZutiSupportMethods_NetSend_ToClients.aircraftControlsChanged_ToClient(acName, eventId, weaponControl);
                        /*
                         * if( Config.isUSE_RENDER() ) { netmsginput.reset(); aircraftControlsChanged(netmsginput); }
                         */
                    } else aircraftControlsChanged(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.COCKPIT_CHANGE:
                    if (Main.cur().netServerParams.isMaster()) {
                        String acName = netmsginput.read255();
                        // TODO: Storebror: Cockpit Change Bugfix
                        int requestedPosition = netmsginput.readInt(); // .readByte(); // .readInt();
                        boolean isPilot = netmsginput.readBoolean();

                        ZutiSupportMethods_NetSend.processCockpitChangeRequest(sender, acName, requestedPosition, isPilot);
                    } else {
                        String username = netmsginput.read255();
                        int newCockpitNr = netmsginput.readByte();

                        changeCockpit(username, newCockpitNr);
                    }
                    return true;
                case ZutiSupportMethods_NetSend.AIRCRAFT_CREW:
                    if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend.aircraftCrew(sender, netmsginput);
                    else aircraftCrew(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.CRATERS:
                    if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend.cratersList(sender);
                    else cratersList(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.LOG_MESSAGE:
                    String message = netmsginput.read255();
                    EventLog.type(message);
                    return true;
                case ZutiSupportMethods_NetSend.RRR_REFUEL: {
                    float fuel = netmsginput.readFloat();
                    if (Main.cur().netServerParams.isMaster()) {
                        String user = netmsginput.read255();
                        ZutiSupportMethods_NetSend_ToClients.refuelAircraft_ToClient(fuel, user);
                    } else ZutiSupportMethods_FM.startRefueling(World.getPlayerAircraft(), fuel);
                    return true;
                }
                case ZutiSupportMethods_NetSend.RRR_RETURN_FUEL: {
                    float unusedFuel = netmsginput.readFloat();
                    int bornPlaceId = netmsginput.readInt();
                    int army = netmsginput.readInt();

                    returnResources_Fuel(bornPlaceId, army, unusedFuel);
                    return true;
                }
                case ZutiSupportMethods_NetSend.RRR_BULLETS: {
                    long bullets = netmsginput.readLong();
                    if (Main.cur().netServerParams.isMaster()) {
                        String user = netmsginput.read255();
                        ZutiSupportMethods_NetSend_ToClients.reloadGuns_ToClient(bullets, user);
                    } else ZutiSupportMethods_FM.startRearming_Cannons(World.getPlayerAircraft(), bullets);
                    return true;
                }
                case ZutiSupportMethods_NetSend.RRR_RETURN_BULLETS: {
                    long unusedBullets = netmsginput.readLong();
                    int bornPlaceId = netmsginput.readInt();
                    int army = netmsginput.readInt();

                    returnResources_Bullets(bornPlaceId, army, unusedBullets);
                    return true;
                }
                case ZutiSupportMethods_NetSend.RRR_BOMBS: {
                    int[] bombs = new int[] { 0, 0, 0, 0, 0, 0 };
                    bombs[0] = netmsginput.readInt();
                    bombs[1] = netmsginput.readInt();
                    bombs[2] = netmsginput.readInt();
                    bombs[3] = netmsginput.readInt();
                    bombs[4] = netmsginput.readInt();
                    bombs[5] = netmsginput.readInt();
                    if (Main.cur().netServerParams.isMaster()) {
                        String user = netmsginput.read255();
                        ZutiSupportMethods_NetSend_ToClients.reloadBombs_ToClient(bombs, user);
                    } else ZutiSupportMethods_FM.startRearming_Bombs(World.getPlayerAircraft(), bombs);
                    return true;
                }
                case ZutiSupportMethods_NetSend.RRR_RETURN_BOMBS: {
                    int[] unusedBombs = new int[] { 0, 0, 0, 0, 0, 0 };
                    unusedBombs[0] = netmsginput.readInt();
                    unusedBombs[1] = netmsginput.readInt();
                    unusedBombs[2] = netmsginput.readInt();
                    unusedBombs[3] = netmsginput.readInt();
                    unusedBombs[4] = netmsginput.readInt();
                    unusedBombs[5] = netmsginput.readInt();

                    int bornPlaceId = netmsginput.readInt();
                    int army = netmsginput.readInt();

                    returnResources_Bombs(bornPlaceId, army, unusedBombs);
                    return true;
                }
                case ZutiSupportMethods_NetSend.RRR_ROCKETS: {
                    long rockets = netmsginput.readLong();
                    if (Main.cur().netServerParams.isMaster()) {
                        String user = netmsginput.read255();
                        ZutiSupportMethods_NetSend_ToClients.reloadRockets_ToClient(rockets, user);
                    } else ZutiSupportMethods_FM.startRearming_Rockets(World.getPlayerAircraft(), rockets);
                    return true;
                }
                case ZutiSupportMethods_NetSend.RRR_RETURN_ROCKETS: {
                    long unusedRockets = netmsginput.readLong();
                    int bornPlaceId = netmsginput.readInt();
                    int army = netmsginput.readInt();

                    returnResources_Rockets(bornPlaceId, army, unusedRockets);
                    return true;
                }
                case ZutiSupportMethods_NetSend.RRR_ENGINE: {
                    int engines = netmsginput.readInt();
                    int engineId = netmsginput.readInt();
                    if (Main.cur().netServerParams.isMaster()) {
                        String user = netmsginput.read255();
                        ZutiSupportMethods_NetSend_ToClients.repairEngine_ToClient(engines, engineId, user);
                    } else ZutiSupportMethods_FM.startEngineRepairing(World.getPlayerAircraft(), engines, engineId);
                    return true;
                }
                case ZutiSupportMethods_NetSend.RRR_RETURN_ENGINE: {
                    long unusedEngines = netmsginput.readLong();
                    int bornPlaceId = netmsginput.readInt();
                    int army = netmsginput.readInt();

                    returnResources_Engines(bornPlaceId, army, unusedEngines);
                    return true;
                }
                case ZutiSupportMethods_NetSend.RRR_REPAIR_KIT: {
                    if (Main.cur().netServerParams.isMaster()) {
                        long repairKits = netmsginput.readLong();
                        String user = netmsginput.read255();
                        ZutiSupportMethods_NetSend_ToClients.repairKit_ToClient(repairKits, user);
                    } else ZutiSupportMethods_FM.startRepairing(World.getPlayerAircraft());
                    return true;
                }
                case ZutiSupportMethods_NetSend.RRR_RETURN_REPAIR_KIT: {
                    long unusedRepairKits = netmsginput.readLong();
                    int bornPlaceId = netmsginput.readInt();
                    int army = netmsginput.readInt();

                    returnResources_RepairKits(bornPlaceId, army, unusedRepairKits);
                    return true;
                }
                case ZutiSupportMethods_NetSend.RRR_SPAWN_RESOURCES: {
                    // Read bullets data
                    long bullets = netmsginput.readLong();

                    // Read rockets data
                    long rockets = netmsginput.readLong();

                    // Read bombs data
                    int[] bombs = new int[] { 0, 0, 0, 0, 0, 0 };
                    bombs[0] = netmsginput.readInt();
                    bombs[1] = netmsginput.readInt();
                    bombs[2] = netmsginput.readInt();
                    bombs[3] = netmsginput.readInt();
                    bombs[4] = netmsginput.readInt();
                    bombs[5] = netmsginput.readInt();

                    // Read fuel data
                    float fuel = netmsginput.readFloat();

                    // Read cargo data
                    long cargo = netmsginput.readLong();

                    // Read user data
                    String user = netmsginput.read255();

                    if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend_ToClients.spawnResources_ToClient(bullets, bombs, rockets, fuel, cargo, user);
                    else {
                        printDebugMessage("RECEIVED SPAWN RESOURCES DATA!!!!!!!!!!!!!!!!!");
                        ZutiSupportMethods_FM.updateSpawnResources(bullets, bombs, rockets, fuel, cargo);
                    }
                    return true;
                }
                case ZutiSupportMethods_NetSend.AIRCRAFT_AVAILABILITY: {
                    String acName = netmsginput.read255();
                    int bornPlaceId = netmsginput.readInt();

                    if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend_ToClients.aircraftAvailability_ToClient(acName, bornPlaceId, sender);
                    else // We are targeted client. BornPlace holds info if ac is available or not
                         // If it is, bornPlaceId is 0, else it is -1.
                        if (bornPlaceId > -1) // Aircraft available, spawn!
                            ZutiSupportMethods_GUI.spawn_ClientGUI();
                        else new GWindowMessageBox(Main3D.cur3D().guiManager.root, 20.0F, true, ZutiSupportMethods_GUI.GAME_STATE.i18n("mds.info.aircraftAvailability"), ZutiSupportMethods_GUI.GAME_STATE.i18n("mds.info.aircraftNotAvailable"), 3, 0.0F);
                    return true;
                }
                case ZutiSupportMethods_NetSend.CARRIER_SPAWN_PLACE: {
                    if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend_ToClients.carrierStayPlace_ToClient(sender);
                    else {
                        int spawnPlace = netmsginput.readInt();
                        ((NetUser) NetEnv.host()).zutiSetAirdomeStay(spawnPlace);
                    }

                    return true;
                }
                case ZutiSupportMethods_NetSend.RELEASE_CARRIER_SPAWN_PLACE: {
                    // Only server can receive this message
                    int bornPlaceId = netmsginput.readInt();
                    int stayPlaceId = netmsginput.readInt();
                    ZutiSupportMethods_Net.releaseStayPlace(sender.uniqueName(), bornPlaceId, stayPlaceId);
                    return true;
                }
                case ZutiSupportMethods_NetSend.EJECT_CREW: {
                    // Only server can receive this message
                    String acName = netmsginput.read255();
                    ZutiSupportMethods_Multicrew.ejectGunnersForAircraft(acName);
                    return true;
                }
                case ZutiSupportMethods_NetSend.UPDATE_NET_PLACE: {
                    if (Main.cur().netServerParams.isMaster()) {
                        // Update crew net places here, then force clients to update themselves, just to be sure.
                        ZutiSupportMethods_Multicrew.updateNetUsersCrewPlaces();
                        // Forward the message to users
                        ZutiSupportMethods_NetSend_ToClients.updateNetPlace_ToClients(sender);
                    } else // We are a client... update net place
                        ZutiSupportMethods_Multicrew.updateNetUsersCrewPlaces();
                    return true;
                }
                case ZutiSupportMethods_NetSend.FRONT_MARKER:
                    if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend.frontMarkers(sender);
                    else frontMarker(netmsginput);
                    return true;
                case ZutiSupportMethods_NetSend.FRONT_REFRESH:
                    // Only client can receive this
                    if (!Main.cur().netServerParams.isMaster()) frontRefresh();
                    return true;
                // TODO: Add next case here...
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Server responded to our test message. Log it.
     */
    private static void testMessage() {
        printDebugMessage("RECEIVED TEST MESSAGE FROM SERVER!");
    }

    /**
     * Received information that player is banned/can not fly.
     *
     * @param netuser
     * @param netmsginput
     */
    private static void setUserTimePenalty(NetUser netuser, NetMsgInput netmsginput) {
        try {
            long penalty = netmsginput.readLong();
            String IP = netuser.masterChannel().remoteAddress().getHostAddress();

            ZutiSupportMethods.setUserTimePenalty(netuser.uniqueName(), IP, penalty);

            printDebugMessage("setUserTimePenalty set time penalty of >" + penalty + "< for user >" + netuser.uniqueName() + " [" + IP + "]<.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Received information that players selected plane is not available.
     */
    public static void aircraftNotAvailable() {
        try {
            NetAircraft netAc = World.getPlayerAircraft();

            if (netAc == null) return;

            netAc.destroy();
            NetAircraft.ZUTI_REFLY_OWERRIDE = true;

            HUD.log("mds.netCommand.aircraftNotAvailable", new java.lang.Object[] { Property.stringValue(((Aircraft) netAc).getClass(), "keyName") });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Received information that target is completed and needs to be removed.
     */
    private static void removeTarget(NetMsgInput netmsginput) {
        try {
            String targetDesignation = netmsginput.read255();
            ZutiSupportMethods_GUI.removeTargetPoint(targetDesignation);

            printDebugMessage("removeTarget >" + targetDesignation + "< received.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Received notification that user was the first to inspect designated area.
     */
    public static void awardedForInspectingArea() {
        try {
            // TInspect target removed by user, add 250 award points!
            com.maddox.il2.ai.ScoreCounter scorecounter = com.maddox.il2.ai.World.cur().scoreCounter;
            // 9=StaticAir, 250=points
            scorecounter.enemyItems.add(new com.maddox.il2.ai.ScoreItem(8, 250));
            com.maddox.il2.game.HUD.log("mds.netCommand.completedTInspect", new java.lang.Object[] { "250" });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // TODO: +++ Changed by SAS~Storebror: Compress excessively long aircraft lines.
    private static void aircraftList(NetMsgInput netmsginput) {
        aircraftListCompressed(netmsginput, false);
    }
    
    /**
     * Received aircraft list for home base.
     */
    private static void aircraftListCompressed(NetMsgInput netmsginput, boolean isCompressed) {
        // TODO: --- Changed by SAS~Storebror: Compress excessively long aircraft lines.
        try {
            ArrayList aircraftList = new ArrayList();
            double x = netmsginput.readDouble();
            double y = netmsginput.readDouble();
            
            // TODO: +++ Changed by SAS~Storebror: Compress excessively long aircraft lines.
//            StringTokenizer acTokenizer = new StringTokenizer(netmsginput.read255(), ";");
            String aircraftLine = "";
            if (isCompressed) {
                int compressedLength = netmsginput.readUnsignedByte();
                byte[] buf = new byte[compressedLength];
                for (int i = 0; i < buf.length; i++)
                    buf[i] = (byte) netmsginput.readUnsignedByte();
                try {
                    byte[] inflated = NetAircraft.decompress(buf);
                    aircraftLine = new String(inflated);
//                    System.out.println("Inflated aircraftLine=" + aircraftLine);
                } catch (DataFormatException e) {
                    e.printStackTrace();
                }
            } else {
                aircraftLine = netmsginput.read255();
            }
            StringTokenizer acTokenizer = new StringTokenizer(aircraftLine, ";");
            // TODO: --- Changed by SAS~Storebror: Compress excessively long aircraft lines.
            
            StringTokenizer acPropertiesTokenizer = null;
            int index = 0;
            String nextAcToken = null;
            ZutiAircraft zac = null;
            ArrayList selectedLoadouts = new ArrayList();
            while (acTokenizer.hasMoreTokens()) {
                nextAcToken = acTokenizer.nextToken();
                try {
                    printDebugMessage("Parsing: " + nextAcToken);
                    acPropertiesTokenizer = new StringTokenizer(nextAcToken, " ");
                    selectedLoadouts.clear();
                    zac = new ZutiAircraft();
                    index = 0;
                    // acName, currentNumberOfAc, maxFuel, loadouts
                    while (acPropertiesTokenizer.hasMoreTokens()) {
                        switch (index) {
                            case 0:
                                zac.setAcName(acPropertiesTokenizer.nextToken().trim());
                                break;
                            case 1:
                                zac.setNumberOfAircraft(new Integer(acPropertiesTokenizer.nextToken()).intValue());
                                break;
                            case 2:
                                zac.setMaxFuelSelection(new Integer(acPropertiesTokenizer.nextToken()).intValue());
                                break;
                            default:
                                selectedLoadouts.add(acPropertiesTokenizer.nextToken());
                                break;
                        }
                        index++;
                    }
                    zac.setSelectedWeapons(selectedLoadouts);
                    aircraftList.add(zac);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    printDebugMessage("Problem in parsing line >" + nextAcToken + "<");
                }
            }
            ZutiSupportMethods_Net.setAircraftListForHomeBase(aircraftList, x, y);

            printDebugMessage("unavailableAircraftList data received!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Received information about para captured home base.
     */
    private static void paraCapturedHomeBase(NetMsgInput netmsginput) {
        try {
            double x = netmsginput.readDouble();
            double y = netmsginput.readDouble();
            int army = netmsginput.readByte();

            BornPlace bp = ZutiSupportMethods_Net.getBornPlace(x, y);
            ZutiSupportMethods.paraCapturedBornPlace(bp, army);

            printDebugMessage("paraCapturedHomeBases data received!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Received notification net user was removed from a plane.
     */
    private static void ejectPlayer(NetMsgInput netmsginput) {
        try {
            String username = netmsginput.read255();

            NetUser netuser = ZutiSupportMethods.getNetUser(username);

            if (netuser == null) return;

            NetGunner netgunner = netuser.findGunner();
            if (netgunner == null) return;

            String acName = netgunner.getAircraftName();

            if (acName == null || acName.trim().length() < 1) return;

            String hostname = ((NetUser) NetEnv.host()).uniqueName();

            // System.out.println("Host name: " + hostname + " vs username: " + username);

            // If message is meant for us execute eject sequence
            if (username.equals(hostname)) {
                ZutiSupportMethods_Multicrew.ejectPlayerGunner();
                HUD.logCenter("mds.netCommand.kicked");
            } else {
                // If not, just show log message
                // Aircraft ac = (Aircraft)Actor.getByName(acName);
                // ZutiSupportMethods.ejectCrewFromCockpit(ac, netgunner.zutiGetCockpitNum(), false, false);
                Aircraft playerAc = World.getPlayerAircraft();
                if (playerAc != null && playerAc.name().equals(acName)) {
                    HUD.log("mds.netCommand.crewLeave", new java.lang.Object[] { username });
                    printDebugMessage("Ejecting: Gunner AC=" + acName + ", player AC=" + World.getPlayerAircraft().name());
                }

                if (Main.cur().netServerParams.isMaster()) {
                    // Send data about user to be disconnected to all users
                    NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                    netmsgguaranted.writeByte(ZutiSupportMethods_NetSend.EJECT_CREW_MEMBER);
                    netmsgguaranted.write255(username);
                    NetEnv.host().post(netmsgguaranted);
                }
            }

            // Reset old connections
            netgunner.zutiSetAircraftAndCockpitNum(null, -1);
            netuser.requestPlace(-1);

            ZutiAircraftCrew positions = ZutiAircraftCrewManagement.getAircraftCrew(acName);
            positions.setUserPosition(username, -1);

            printDebugMessage("ejectPlayer >" + netuser.uniqueName() + "< command received!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * AC released its bomb load.
     *
     * @param netmsginput
     */
    public static void bombardierReleasedOrdinance(NetMsgInput netmsginput) {
        try {
            if (netmsginput == null) return;

            String acName = netmsginput.read255();
            boolean bool = netmsginput.readBoolean();
            boolean hasBombBayDoors = netmsginput.readBoolean();

            Aircraft ac = (Aircraft) Actor.getByName(acName);
            if (ac == null) return;

            ac.FM.CT.bHasBayDoors = hasBombBayDoors;
            if (ac.FM instanceof RealFlightModel) {// Live aircraft execution code - this is aircraft owner and can release bombs!
                ac.FM.CT.WeaponControl[3] = bool;
                printDebugMessage("bombardierReleasedOrdinance executed on LIVE AC >" + acName + "<.");
            } else if (!ac.name().endsWith("_0")) {// Bombardier on AI controlled aircraft dropped bombs... drop at client too
                if (bool) ZutiSupportMethods_FM.executeDropBombs(ac.FM);
                printDebugMessage("bombardierReleasedOrdinance on AI AC >" + acName + "<.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * AC changed its bomb bay doors status.
     *
     * @param netmsginput
     */
    public static void bombBayDoorsStatus(NetMsgInput netmsginput) {
        try {
            if (netmsginput == null) return;

            String acName = netmsginput.read255();
            byte status = netmsginput.readByte();

            Aircraft ac = (Aircraft) Actor.getByName(acName);
            if (ac == null) return;

            if (ac.FM instanceof RealFlightModel && ac.name().endsWith("_0")) {// Live aircraft execution code - this is aircraft owner and change bomb bay doors!
                // 21 = event ID for bomb bay doors
                Main3D.cur3D().aircraftHotKeys.doCmdPilot(21, true);
                printDebugMessage("bombBayDoorsStatus executed on LIVE AC >" + acName + "<.");
            } else {// Bombardier on AI controlled aircraft changed bomb bay doors, change on client side too.
                ac.FM.CT.BayDoorControl = status;
                printDebugMessage("bombBayDoorsStatus executed on AI AC >" + acName + "<.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Received information about AC with no bombs.
     */
    private static void aircraftWithReleasedOrdinance(NetMsgInput netmsginput) {
        try {
            if (netmsginput == null) return;

            String acName = netmsginput.read255();
            String weaponReleasesLine = netmsginput.read255();
            String entry = null;

            StringTokenizer stringtokenizer = new StringTokenizer(weaponReleasesLine);
            StringTokenizer entrytokenizer = null;

            int index = 0;
            int weaponId = -1;
            int weaponBay = -1;
            int weaponRelease = -1;

            while (stringtokenizer.hasMoreTokens()) {
                index = 0;

                entry = stringtokenizer.nextToken();
                entrytokenizer = new StringTokenizer(entry, ",");
                while (entrytokenizer.hasMoreTokens()) {
                    switch (index) {
                        case 0:
                            weaponId = Integer.parseInt(entrytokenizer.nextToken());
                            break;
                        case 1:
                            weaponBay = Integer.parseInt(entrytokenizer.nextToken());
                            break;
                        case 2:
                            weaponRelease = Integer.parseInt(entrytokenizer.nextToken());
                            break;
                    }
                    index++;
                }

                ZutiSupportMethods_Multicrew.clearAircraftOrdinance(acName, weaponId, weaponBay, weaponRelease);
            }

            printDebugMessage("acWithEmptyBombLoadList data received! AcName=" + acName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Received information about aircraft bombardier instrument status
     *
     * @param
     */
    public static void bombardierInstrumentsChanged(String acName, int distance, int sideslip, int altitude, int speed, int gunsightAutomation) {
        try {
            if (acName == null) return;

            Aircraft aircraft = (Aircraft) Actor.getByName(acName);

            if (aircraft == null) return;

            TypeBomber bomber = null;
            if (aircraft != null && aircraft instanceof TypeBomber) {
                bomber = (TypeBomber) aircraft;
                if (distance > 0) bomber.typeBomberAdjDistancePlus();
                else if (distance < 0) bomber.typeBomberAdjDistancePlus();

                if (sideslip > 0) bomber.typeBomberAdjSideslipPlus();
                else if (sideslip < 0) bomber.typeBomberAdjSideslipMinus();

                if (altitude > 0) bomber.typeBomberAdjAltitudePlus();
                else if (altitude < 0) bomber.typeBomberAdjAltitudeMinus();

                if (speed > 0) bomber.typeBomberAdjSpeedPlus();
                else if (speed < 0) bomber.typeBomberAdjSpeedMinus();

                if (gunsightAutomation > 0) bomber.typeBomberToggleAutomation();

                printDebugMessage("bombardierInstrumentsChanged data received for ac >" + acName + "<.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Received information about aircraft bombardier instrument status
     *
     * @param netmsginput
     */
    private static void bombardierInstrumentsChanged(NetMsgInput netmsginput) {
        try {
            if (netmsginput == null) return;

            String acName = netmsginput.read255();
            int distance = netmsginput.readByte();
            int sideslip = netmsginput.readByte();
            int altitude = netmsginput.readByte();
            int speed = netmsginput.readByte();
            int gunsightAutomation = netmsginput.readByte();

            bombardierInstrumentsChanged(acName, distance, sideslip, altitude, speed, gunsightAutomation);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * We have received data that our crew member successfully killed valid actor.
     *
     * @param netmsginput
     */
    public static void syncCrewScore(NetMsgInput netmsginput) {
        try {
            // if we are the AC owner, we already synced the score as we were the initiator!
            NetUser netuser = (NetUser) NetEnv.host();
            if (netuser != null) {
                Aircraft playerAc = World.getPlayerAircraft();
                if (playerAc != null) if (playerAc.name().equals(netuser.uniqueName() + "_0")) return;
            }

            if (netmsginput == null) return;

            String actorName = netmsginput.read255();
            actorName = netmsginput.read255();
            boolean damagedOnly = netmsginput.readBoolean();

            Actor actor = Actor.getByName(actorName);

            if (!damagedOnly) {
                if (actor != null && netuser != null) if (actor.getArmy() != netuser.getArmy()) World.cur().scoreCounter.enemyDestroyed(actor);
                else World.cur().scoreCounter.friendDestroyed(actor);
            } else if (actor != null && netuser != null) {
                double explosionPower = netmsginput.readDouble();

                if (actor.getArmy() != netuser.getArmy()) World.cur().scoreCounter.enemyDamaged(actor, explosionPower);
                else World.cur().scoreCounter.friendDamaged(actor, explosionPower);
            }

            printDebugMessage("syncCrewScore data received for actor >" + actorName + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Received notification that pilot of our plane enabled or disables someone ability to control the plane.
     */
    private static void transferControls(String username) {
        try {
            NetUser netuser = (NetUser) NetEnv.host();

            if (netuser.uniqueName().equals(username) && !ZutiSupportMethods.IS_ACTING_INSTRUCTOR) {
                com.maddox.rts.HotKeyEnv.enable("pilot", true);
                RTSConf.cur.joy.setEnable(true);
                ZutiSupportMethods.IS_ACTING_INSTRUCTOR = true;
                HUD.log("mds.instructor.enabled");
                printDebugMessage("INSTRUCTOR MODE: " + ZutiSupportMethods.IS_ACTING_INSTRUCTOR);
                printDebugMessage("user >" + username + "< is now instructor. Joy control is >" + RTSConf.cur.joy.isEnable() + "<.");
            } else if (ZutiSupportMethods.IS_ACTING_INSTRUCTOR) {
                if (Main3D.cur3D() != null) {
                    Cockpit currentCockpit = Main3D.cur3D().cockpitCur;
                    if (!(currentCockpit instanceof CockpitPilot)) {
                        // Player is gunner at best, disable pilot controls and enable gunner ones back
                        com.maddox.rts.HotKeyEnv.enable("pilot", false);
                        com.maddox.rts.HotKeyEnv.enable("gunner", true);
                    }
                }
                // We received this command once again, this means that we are no longer instructor!
                RTSConf.cur.joy.setEnable(false);
                ZutiSupportMethods.IS_ACTING_INSTRUCTOR = false;
                printDebugMessage("INSTRUCTOR MODE: " + ZutiSupportMethods.IS_ACTING_INSTRUCTOR);
                HUD.log("mds.instructor.disabled");
                printDebugMessage("user >" + username + "< is no longer instructor. Joy control is >" + RTSConf.cur.joy.isEnable() + "<.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Received information about plane movement.
     *
     * @param netmsginput
     */
    private static void aircraftControlsMoved(NetMsgInput netmsginput) {
        try {
            if (netmsginput == null) return;

            int eventId = netmsginput.readByte();
            float controlState = netmsginput.readFloat();

            Main3D.cur3D().aircraftHotKeys.doCmdPilotMove(eventId, controlState);

            printDebugMessage("aircraftControlsMove received!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Received information about plane action.
     *
     * @param netmsginput
     */
    private static void aircraftControlsChanged(NetMsgInput netmsginput) {
        try {
            if (netmsginput == null) return;

            int eventId = netmsginput.readByte();
            boolean weaponControl = netmsginput.readBoolean();

            Main3D.cur3D().aircraftHotKeys.doCmdPilot(eventId, weaponControl);

            printDebugMessage("aircraftControlsChanged received!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Received information about assigned cockpit position.
     *
     * @param username
     * @param newCockpitNr
     */
    public static void changeCockpit(String username, int newCockpitNr) {
        try {
            if (username == null || newCockpitNr < 0) {
                printDebugMessage("changeCockpit: can not change cockpit position at this time! Try again later.");
                ZutiSupportMethods_Multicrew.CAN_REQUEST_COCKPIT_CHANGE = true;
                return;
            }

            if (World.getPlayerAircraft() == null) return;

            String acName = World.getPlayerAircraft().name();
            ZutiAircraftCrew positions = ZutiAircraftCrewManagement.getAircraftCrew(acName);

            // Set user position in crew map
            positions.setUserPosition(username, newCockpitNr);

            // Check if this data was meant for us. If so, switch cockpit.
            NetUser netuser = (NetUser) NetEnv.host();
            if (netuser != null && netuser.uniqueName().equals(username)) {
                // Turn AI on for current cockpit (old one, before switching to new one).
                ZutiSupportMethods_Air.setGunnerAutopilotOn();

                Main3D.cur3D().aircraftHotKeys.switchToCockpit(newCockpitNr);

                NetGunner gunner = netuser.findGunner();
                if (gunner != null && newCockpitNr > 0) gunner.zutiSetAircraftAndCockpitNum(acName, newCockpitNr);
                printDebugMessage("Set new gunner cockpit id and plane for user >" + netuser.uniqueName() + "< (US)!");

                // We received requested cockpit change. We can again request another one.
                ZutiSupportMethods_Multicrew.CAN_REQUEST_COCKPIT_CHANGE = true;
                // Turn AI on for new cockpit...
                ZutiSupportMethods_Air.setGunnerAutopilotOn();
            } else {
                // Cockpit change data was not meant for us. Get that user and change his data
                netuser = ZutiSupportMethods.getNetUser(username);
                if (netuser != null) {
                    NetGunner gunner = netuser.findGunner();
                    if (gunner != null && newCockpitNr > 0) gunner.zutiSetAircraftAndCockpitNum(acName, newCockpitNr);
                    printDebugMessage("Set new gunner cockpit id and plane for user >" + netuser.uniqueName() + "<!");
                }
            }

            printDebugMessage("changeCockpit for user >" + username + "< and cockpit >" + newCockpitNr + "< received!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void aircraftCrew(NetMsgInput netmsginput) {
        try {
            if (netmsginput == null) return;

            String crewLine = netmsginput.read255();
            String entry = null;

            printDebugMessage("received crew line: " + crewLine);

            StringTokenizer stringtokenizer = new StringTokenizer(crewLine);
            StringTokenizer entrytokenizer = null;

            int index = 0;
            int position = -1;
            String username = null;

            if (World.getPlayerAircraft() == null) return;

            ZutiAircraftCrew positions = ZutiAircraftCrewManagement.getAircraftCrew(World.getPlayerAircraft().name());
            positions.clearCrewMap();

            while (stringtokenizer.hasMoreTokens()) {
                index = 0;

                entry = stringtokenizer.nextToken();
                entrytokenizer = new StringTokenizer(entry, ",");
                while (entrytokenizer.hasMoreTokens()) {
                    switch (index) {
                        case 0:
                            position = Integer.parseInt(entrytokenizer.nextToken());
                            break;
                        case 1:
                            username = entrytokenizer.nextToken();
                            break;
                    }
                    index++;
                }

                positions.setUserPosition(username, position);
            }

            printDebugMessage("aircraftCrew data received! crew line=" + crewLine);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method will return true if: we are the host we are the receiver
     *
     * @return
     */
    public static boolean isCommandMeantForUs(String targetUsername) {
        if (Config.isUSE_RENDER() && Main.cur().netServerParams.isMaster()) {
            String hostName = ((NetUser) NetEnv.host()).uniqueName();
            if (targetUsername.equals(hostName)) return true;
        }

        return false;
    }

    /**
     * Received information about craters that are alive on the map.
     */
    private static void cratersList(NetMsgInput netmsginput) {
        try {
            long expirationTime = netmsginput.readLong();

            if (expirationTime < 0) {
                printDebugMessage("Received expired crater data!");
                return;
            }

            float size = netmsginput.readFloat();
            float x = netmsginput.readFloat();
            float y = netmsginput.readFloat();
            float z = netmsginput.readFloat();

            Loc l = new Loc(x, y, z, 0.0F, 0.0F, 0.0F);

            new ActorSnapToLand("3do/Effects/Explosion/Crater.sim", true, l, 0.2F, size, size + 2.0F, 1.0F, 0.0F, expirationTime);
            int calcSize;
            for (calcSize = 64; calcSize >= 2; calcSize /= 2)
                if (size >= calcSize) break;
            if (calcSize >= 2) new ActorCrater("3do/Effects/Explosion/Crater" + calcSize + "/Live.sim", l, expirationTime);

            printDebugMessage("crater data received: remaining live time = " + expirationTime + "s, size = " + size + ", x = " + x + ", y = " + y + ", z = " + z);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void returnResources_Bombs(int bornPlaceId, int army, int[] unusedBombs) {
        if (bornPlaceId > 0) {
            BornPlace bp = (BornPlace) World.cur().bornPlaces.get(bornPlaceId);
            bp.zutiBombsSupply[0] += unusedBombs[0];
            bp.zutiBombsSupply[1] += unusedBombs[1];
            bp.zutiBombsSupply[2] += unusedBombs[2];
            bp.zutiBombsSupply[3] += unusedBombs[3];
            bp.zutiBombsSupply[4] += unusedBombs[4];
            bp.zutiBombsSupply[5] += unusedBombs[5];

            ZutiSupportMethods_ResourcesManagement.printOutResourcesForHomeBase(bp);
        } else if (army > 0) {
            switch (army) {
                case 1:
                    Mission.MDS_VARIABLES().zutiBombsSupply_Red[0] += unusedBombs[0];
                    Mission.MDS_VARIABLES().zutiBombsSupply_Red[1] += unusedBombs[1];
                    Mission.MDS_VARIABLES().zutiBombsSupply_Red[2] += unusedBombs[2];
                    Mission.MDS_VARIABLES().zutiBombsSupply_Red[3] += unusedBombs[3];
                    Mission.MDS_VARIABLES().zutiBombsSupply_Red[4] += unusedBombs[4];
                    Mission.MDS_VARIABLES().zutiBombsSupply_Red[5] += unusedBombs[5];
                    break;
                case 2:
                    Mission.MDS_VARIABLES().zutiBombsSupply_Blue[0] += unusedBombs[0];
                    Mission.MDS_VARIABLES().zutiBombsSupply_Blue[1] += unusedBombs[1];
                    Mission.MDS_VARIABLES().zutiBombsSupply_Blue[2] += unusedBombs[2];
                    Mission.MDS_VARIABLES().zutiBombsSupply_Blue[3] += unusedBombs[3];
                    Mission.MDS_VARIABLES().zutiBombsSupply_Blue[4] += unusedBombs[4];
                    Mission.MDS_VARIABLES().zutiBombsSupply_Blue[5] += unusedBombs[5];
                    break;
            }

            ZutiSupportMethods_ResourcesManagement.printOutResourcesForSide(army);
        }
    }

    private static void returnResources_Bullets(int bornPlaceId, int army, long unusedBullets) {
        if (bornPlaceId > 0) {
            BornPlace bp = (BornPlace) World.cur().bornPlaces.get(bornPlaceId);
            bp.zutiFuelSupply += unusedBullets;

            ZutiSupportMethods_ResourcesManagement.printOutResourcesForHomeBase(bp);
        } else if (army > 0) {
            switch (army) {
                case 1:
                    Mission.MDS_VARIABLES().zutiBulletsSupply_Red += unusedBullets;
                    break;
                case 2:
                    Mission.MDS_VARIABLES().zutiBulletsSupply_Blue += unusedBullets;
                    break;
            }

            ZutiSupportMethods_ResourcesManagement.printOutResourcesForSide(army);
        }
    }

    private static void returnResources_Rockets(int bornPlaceId, int army, long unusedRockets) {
        if (bornPlaceId > 0) {
            BornPlace bp = (BornPlace) World.cur().bornPlaces.get(bornPlaceId);
            bp.zutiRocketsSupply += unusedRockets;

            ZutiSupportMethods_ResourcesManagement.printOutResourcesForHomeBase(bp);
        } else if (army > 0) {
            switch (army) {
                case 1:
                    Mission.MDS_VARIABLES().zutiRocketsSupply_Red += unusedRockets;
                    break;
                case 2:
                    Mission.MDS_VARIABLES().zutiRocketsSupply_Blue += unusedRockets;
                    break;
            }

            ZutiSupportMethods_ResourcesManagement.printOutResourcesForSide(army);
        }
    }

    private static void returnResources_Engines(int bornPlaceId, int army, long unusedEngines) {
        if (bornPlaceId > 0) {
            BornPlace bp = (BornPlace) World.cur().bornPlaces.get(bornPlaceId);
            bp.zutiEnginesSupply += unusedEngines;

            ZutiSupportMethods_ResourcesManagement.printOutResourcesForHomeBase(bp);
        } else if (army > 0) {
            switch (army) {
                case 1:
                    Mission.MDS_VARIABLES().zutiEnginesSupply_Red += unusedEngines;
                    break;
                case 2:
                    Mission.MDS_VARIABLES().zutiEnginesSupply_Blue += unusedEngines;
                    break;
            }

            ZutiSupportMethods_ResourcesManagement.printOutResourcesForSide(army);
        }
    }

    private static void returnResources_RepairKits(int bornPlaceId, int army, long unusedRepairKits) {
        if (bornPlaceId > 0) {
            BornPlace bp = (BornPlace) World.cur().bornPlaces.get(bornPlaceId);
            bp.zutiRepairKitsSupply += unusedRepairKits;

            ZutiSupportMethods_ResourcesManagement.printOutResourcesForHomeBase(bp);
        } else if (army > 0) {
            switch (army) {
                case 1:
                    Mission.MDS_VARIABLES().zutiRepairKitsSupply_Red += unusedRepairKits;
                    break;
                case 2:
                    Mission.MDS_VARIABLES().zutiRepairKitsSupply_Blue += unusedRepairKits;
                    break;
            }

            ZutiSupportMethods_ResourcesManagement.printOutResourcesForSide(army);
        }
    }

    private static void returnResources_Fuel(int bornPlaceId, int army, float unusedFuel) {
        if (bornPlaceId > 0) {
            BornPlace bp = (BornPlace) World.cur().bornPlaces.get(bornPlaceId);
            bp.zutiFuelSupply += unusedFuel;

            ZutiSupportMethods_ResourcesManagement.printOutResourcesForHomeBase(bp);
        } else if (army > 0) {
            switch (army) {
                case 1:
                    Mission.MDS_VARIABLES().zutiFuelSupply_Red += unusedFuel;
                    break;
                case 2:
                    Mission.MDS_VARIABLES().zutiFuelSupply_Blue += unusedFuel;
                    break;
            }

            ZutiSupportMethods_ResourcesManagement.printOutResourcesForSide(army);
        }
    }

    /**
     * Received information about front markers.
     */
    private static void frontMarker(NetMsgInput netmsginput) {
        try {
            double x = netmsginput.readDouble();
            double y = netmsginput.readDouble();
            int army = netmsginput.readByte();
            String carrier = netmsginput.read255();

            Front.Marker marker = new Front.Marker();
            marker.x = x;
            marker.y = y;
            marker.army = army;
            marker.zutiMarkerCarrierName = carrier;
            marker._armyMask = 1 << army - 1;

            Front.markers().add(marker);

            Actor actor = Actor.getByName(marker.zutiMarkerCarrierName);
            if (actor instanceof ArtilleryGeneric && actor.isAlive()) {
                ArtilleryGeneric aaa = (ArtilleryGeneric) actor;
                aaa.zutiResetFrontMarkers();
                aaa.zutiAddFrontMarker(marker);
            } else if (actor instanceof TankGeneric && actor.isAlive()) {
                TankGeneric tank = (TankGeneric) actor;
                tank.zutiResetFrontMarkers();
                tank.zutiAddFrontMarker(marker);
            } else if (actor instanceof BigshipGeneric && actor.isAlive()) {
                BigshipGeneric ship = (BigshipGeneric) actor;
                ship.zutiResetFrontMarkers();
                ship.zutiAddFrontMarker(marker);
                printDebugMessage("Marker assigned to: " + carrier);
            }
            // TODO: Limit Log output // by Storebror
            printDebugMessage("frontMarker data received! Markers count: " + Front.markers().size());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void frontRefresh() {
        Front.setMarkersChanged();
        Front.preRender(false);
        Front.render(false);
        ZutiSupportMethods.checkIfAnyBornPlaceWasOverrun();
        printDebugMessage("frontRefresh data received! Total markers in the list: " + Front.markers().size());
    }

    private static int       debugLevel    = Integer.MIN_VALUE;
    private static final int DEBUG_DEFAULT = 0;

    private static int curDebugLevel() {
        if (debugLevel == Integer.MIN_VALUE) debugLevel = Config.cur.ini.get("Mods", "DEBUG_ZSM_NETRECEIVE", DEBUG_DEFAULT);
        return debugLevel;
    }

    public static void printDebugMessage(String theMessage) {
        if (curDebugLevel() == 0) return;
        System.out.println("ZutiSupportMethods_NetReceive - " + theMessage);
    }
}