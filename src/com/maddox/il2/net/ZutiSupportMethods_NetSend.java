package com.maddox.il2.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.maddox.JGP.Point3d;
import com.maddox.gwindow.GWindowMessageBox;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.Front;
import com.maddox.il2.ai.Target;
import com.maddox.il2.ai.World;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.ZutiAcWithReleasedOrdinance;
import com.maddox.il2.game.ZutiAircraftCrew;
import com.maddox.il2.game.ZutiAircraftCrewManagement;
import com.maddox.il2.game.ZutiSupportMethods;
import com.maddox.il2.game.ZutiSupportMethods_Multicrew;
import com.maddox.il2.game.ZutiSupportMethods_ResourcesManagement;
import com.maddox.il2.game.order.ZutiSupportMethods_GameOrder;
import com.maddox.il2.gui.ZutiSupportMethods_GUI;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.air.NetGunner;
import com.maddox.il2.objects.air.ZutiSupportMethods_Air;
import com.maddox.il2.objects.effects.ZutiSupportMethods_Effects;
import com.maddox.rts.NetEnv;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Time;

public class ZutiSupportMethods_NetSend {
    public static boolean    REQUEST_PARA_CAPTURING_HB                = true;
    public static boolean    REQUEST_HB_AC_LIST                       = true;
    public static boolean    REQUEST_FRONT_MARKERS                    = true;
    public static boolean    REQUEST_AIRCRAFT_WITH_RELEASED_ORDINANCE = false;

    public static final byte TEST_MESSAGE                             = 40;
    public static final byte USER_PENALIZED                           = 41;
    public static final byte AIRCRAFT_NOT_AVAILABLE                   = 42;
    public static final byte REMOVE_TARGET                            = 43;
    public static final byte AWARDED_TINSPECT                         = 44;
    public static final byte AIRCRAFT_LIST                            = 45;
    public static final byte EJECT_CREW_MEMBER                        = 46;
    public static final byte PARA_CAPTURED_HB                         = 47;
    public static final byte BOMBARDIER_RELEASED_ORDINANCE            = 48;
    public static final byte BOMB_BAY_DOORS_STATUS                    = 49;
    public static final byte AIRCRAFT_WITH_RELEASED_ORDINANCE         = 50;
    public static final byte BOMBARDIER_INSTRUMENTS_CHANGED           = 53;
    public static final byte SYNC_CREW_SCORE                          = 54;
    public static final byte TRANSFER_CONTROLS                        = 55;
    public static final byte AIRCRAFT_CONTROLS_MOVED                  = 56;
    public static final byte AIRCRAFT_CONTROLS_CHANGED                = 57;
    public static final byte COCKPIT_CHANGE                           = 58;
    public static final byte AIRCRAFT_CREW                            = 59;
    public static final byte CRATERS                                  = 60;
    public static final byte LOG_MESSAGE                              = 61;
    public static final byte RRR_REFUEL                               = 62;
    public static final byte RRR_RETURN_FUEL                          = 63;
    public static final byte RRR_BULLETS                              = 64;
    public static final byte RRR_RETURN_BULLETS                       = 65;
    public static final byte RRR_BOMBS                                = 66;
    public static final byte RRR_RETURN_BOMBS                         = 67;
    public static final byte RRR_ROCKETS                              = 68;
    public static final byte RRR_RETURN_ROCKETS                       = 69;
    public static final byte RRR_ENGINE                               = 70;
    public static final byte RRR_RETURN_ENGINE                        = 71;
    public static final byte RRR_REPAIR_KIT                           = 72;
    public static final byte RRR_RETURN_REPAIR_KIT                    = 73;
    public static final byte RRR_SPAWN_RESOURCES                      = 74;
    public static final byte AIRCRAFT_AVAILABILITY                    = 75;
    public static final byte CARRIER_SPAWN_PLACE                      = 76;
    public static final byte RELEASE_CARRIER_SPAWN_PLACE              = 77;
    public static final byte EJECT_CREW                               = 78;
    public static final byte UPDATE_NET_PLACE                         = 79;
    public static final byte FRONT_MARKER                             = 80;
    public static final byte FRONT_REFRESH                            = 81;
    public static final byte AIRCRAFT_LIST_COMPRESSED                 = -45;

    /**
     * Reset class variables
     */
    public static void resetClassVariables() {
        ZutiSupportMethods_NetSend.REQUEST_PARA_CAPTURING_HB = true;
        ZutiSupportMethods_NetSend.REQUEST_HB_AC_LIST = true;
        ZutiSupportMethods_NetSend.REQUEST_FRONT_MARKERS = true;
        ZutiSupportMethods_NetSend.REQUEST_AIRCRAFT_WITH_RELEASED_ORDINANCE = true;
    }

    /**
     * Send TEST message back to user
     *
     * @param netuser
     */
    public static void testMessage(NetUser netuser) {
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(TEST_MESSAGE);
            NetEnv.host().postTo(netuser.masterChannel(), netmsgguaranted);

            // System.out.println("ZutiNetSendMethods - testMessage executed for user >" + netuser.uniqueName() + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Player in question is not allowed to fly - is banned. Send server relevant data!
     *
     * @param netuser
     * @param penalty
     */
    public static void setUserTimePenalty(NetUser netuser, long penalty) {
        try {
            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.masterChannel() == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(USER_PENALIZED);
            netmsgguaranted.writeLong(penalty);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);

            // System.out.println("ZutiNetSendMethods - setUserTimePenalty executed for user >" + netuser.uniqueName() + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Plane that player selected is not available for various reasons.
     *
     * @param netuser
     * @param aircraftName
     */
    public static void aircraftNotAvailable(NetUser netuser) {
        try {
            if (netuser == null) return;

            String user = ((NetUser) NetEnv.host()).uniqueName();
            if (user.equals(netuser.uniqueName())) {
                // Message is meant for us
                ZutiSupportMethods_NetReceive.aircraftNotAvailable();
                return;
            }

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(AIRCRAFT_NOT_AVAILABLE);
            NetEnv.host().postTo(netuser.masterChannel(), netmsgguaranted);

            // System.out.println("ZutiNetSendMethods - aircraftNotAwailable executed for user >" + netuser.uniqueName() + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Inform users that target needs to be removed because it was completed.
     *
     * @param target
     */
    public static void removeTarget(Target target) {
        try {
            if (target == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(REMOVE_TARGET);
            netmsgguaranted.write255(target.zutiGetTargetDesignation());

            // System.out.println("ZutiNetSendMethods - removeTarget - notifying users to remove target >" + target.zutiGetTargetDesignation() + "<");

            // Send message to all users
            NetEnv.host().post(netmsgguaranted);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * User requested complete list of all completed targets. Send him that list.
     */
    public static void removeTargets(NetUser netuser) {
        if (netuser == null) return;

        // Send the sender info about inactive TInspect targets
        if (World.cur().targetsGuard == null || World.cur().targetsGuard.zutiGetTargets() != null) {
            List targets = World.cur().targetsGuard.zutiGetTargets();
            Target target = null;
            int size = targets.size();
            NetMsgGuaranted netmsgguaranted = null;

            for (int i = 0; i < size; i++)
                try {
                    target = (Target) targets.get(i);
                    // only targets that are deactivated should be reported!
                    if (target != null && (target.getDiedFlag() || target.isTaskComplete())) {
                        netmsgguaranted = new NetMsgGuaranted();
                        netmsgguaranted.writeByte(REMOVE_TARGET);
                        netmsgguaranted.write255(target.zutiGetTargetDesignation());

                        NetEnv.host().postTo(netuser.masterChannel(), netmsgguaranted);

                        // System.out.println("ZutiNetSendMethods - removeTargets executed for user >" + netuser.uniqueName() + "< and target >" + target.zutiGetTargetDesignation() + "<.");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
    }

    /**
     * Send user notification that he was the first to inspect designated area
     *
     * @param netuser
     */
    public static void awardedForInspectingArea(NetUser netuser) {
        try {
            if (netuser == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(AWARDED_TINSPECT);
            NetEnv.host().postTo(netuser.masterChannel(), netmsgguaranted);

            // System.out.println("ZutiNetSendMethods - awardedForInspectingArea executed for user >" + netuser.uniqueName() + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send information to user about which aircraft are not available for which born place.
     *
     * @param netuser
     */
    public static void forwardAircraftList(NetUser netuser) {
        try {
            if (netuser == null || World.cur() == null || World.cur().bornPlaces == null) return;

            ArrayList bornPlaces = World.cur().bornPlaces;
            BornPlace bp = null;
            NetMsgGuaranted netmsgguaranted = null;
            List aircraftLines = null;

            for (int i = 0; i < bornPlaces.size(); i++) {
                bp = (BornPlace) bornPlaces.get(i);
                if (bp == null) continue;

                aircraftLines = ZutiSupportMethods_Net.getAircraftList(bp);
                if (aircraftLines == null) continue;

//                System.out.println("forwardAircraftList aircraftLines.size()=" + aircraftLines.size());
                
                for (int j = 0; j < aircraftLines.size(); j++) {
                    // TODO: +++ Changed by SAS~Storebror: Compress excessively long aircraft lines.
                    String aircraftLine = (String) aircraftLines.get(j);
//                    System.out.println("BP " +bp.place.x+":" + bp.place.y + " aircraftLines(" + j + ")=" + aircraftLine);
                    boolean isLongLine = aircraftLine.length() > 230; 
                    netmsgguaranted = new NetMsgGuaranted();
                    netmsgguaranted.writeByte(isLongLine?AIRCRAFT_LIST_COMPRESSED:AIRCRAFT_LIST);
                    netmsgguaranted.writeDouble(bp.place.x);
                    netmsgguaranted.writeDouble(bp.place.y);
                    // System.out.println(" sending: " + (String) aircraftLines.get(j));
                    byte[] is = null;
                    if (isLongLine) {
                        is = aircraftLine.getBytes();
                        is = NetAircraft.compress(is);
                        netmsgguaranted.writeByte(is.length);
                        netmsgguaranted.write(is);
//                        System.out.println("Deflating aircraftLine len=" + aircraftLine.length() + " to " + is.length + " bytes: " + aircraftLine);
                    } else {
                        netmsgguaranted.write255(aircraftLine);
                    }
                    // TODO: --- Changed by SAS~Storebror: Compress excessively long aircraft lines.

                    try {
                        NetEnv.host().postTo(netuser.masterChannel(), netmsgguaranted);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error in forwardAircraftList, String is: " + aircraftLine + " (len=" + aircraftLine.length() + "), message length=" + netmsgguaranted.size() + (isLongLine?", compressed len=" + (is==null?"0":""+is.length):""));
                    }
                }
            }

//			System.out.println("ZSM_NS - forwardACList executed for user >" + netuser.uniqueName() + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Instruct connected users and server to eject player from his current position. After message is sent, neuser netplace is reset to -1 and his old connections to the plane he was in are severed.
     *
     * @param netuser : netuser object representing player that is being ejected
     */
    public static void ejectPlayer(NetUser netuser) {
        Aircraft playerAc = World.getPlayerAircraft();
        if (netuser == null || playerAc == null) return;

        String username = netuser.uniqueName();
        String acName = playerAc.name();

        if (username == null || acName == null) return;

        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(EJECT_CREW_MEMBER);
            netmsgguaranted.write255(username);
            // Notify server that user must be ejected
            NetEnv.host().post(netmsgguaranted);

            // If netuser that is to be ejected is us, eject us
            String currentUsername = ((NetUser) NetEnv.host()).uniqueName();
            if (currentUsername.equals(netuser.uniqueName())) ZutiSupportMethods_Multicrew.ejectPlayerGunner();

            NetGunner gunner = netuser.findGunner();
            if (gunner != null) gunner.zutiSetAircraftAndCockpitNum(null, -1);

            netuser.requestPlace(-1);

            ZutiAircraftCrew positions = ZutiAircraftCrewManagement.getAircraftCrew(acName);
            positions.setUserPosition(username, -1);

            System.out.println("ZutiNetSendMethods - ejectGunner >" + username + "< executed for user >" + username + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send user information about those home bases that were captured by paratroopers. These events can not be recreated by users that connect after those events because paratroopers despawn after some period of time.
     *
     * @param netuser
     */
    public static void paraCapturedHomeBases(NetUser netuser) {
        try {
            if (netuser == null || World.cur() == null || ZutiSupportMethods.ZUTI_PARA_CAPTURED_HOMEBASES == null) return;

            BornPlace bp = null;
            NetMsgGuaranted netmsgguaranted = null;

            for (int i = 0; i < ZutiSupportMethods.ZUTI_PARA_CAPTURED_HOMEBASES.size(); i++) {
                bp = (BornPlace) ZutiSupportMethods.ZUTI_PARA_CAPTURED_HOMEBASES.get(i);
                if (bp == null) continue;

                netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(PARA_CAPTURED_HB);

                netmsgguaranted.writeDouble(bp.place.x);
                netmsgguaranted.writeDouble(bp.place.y);
                netmsgguaranted.writeByte(bp.army);

                NetEnv.host().postTo(netuser.masterChannel(), netmsgguaranted);
            }

            // System.out.println("ZutiNetSendMethods - paraCapturedHomeBases executed for user >" + netuser.uniqueName() + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send information to users that home base was captured by paratroopers.
     *
     * @param bornplace
     */
    public static void paraCapturedHomeBase(BornPlace bornplace) {
        try {
            if (bornplace == null || World.cur() == null || ZutiSupportMethods.ZUTI_PARA_CAPTURED_HOMEBASES == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(PARA_CAPTURED_HB);

            netmsgguaranted.writeDouble(bornplace.place.x);
            netmsgguaranted.writeDouble(bornplace.place.y);
            netmsgguaranted.writeByte(bornplace.army);

            NetEnv.host().post(netmsgguaranted);

            // System.out.println("ZutiNetSendMethods - paraCapturedHomeBase executed for user >" + netuser.uniqueName() + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is executed by user and is sent to server/host. User is requesting TEST message acknowledgment.
     */
    public static void requestTestMessage() {
        try {
            NetUser netuser = (NetUser) NetEnv.host();

            if (netuser == null) netuser = (NetUser) NetEnv.host();

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(TEST_MESSAGE);
            netuser.post(netmsgguaranted);

            // System.out.println("ZutiNetSendMethods - requestTestMessage executed by user >" + netuser.uniqueName() + "<.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is executed by user and is sent to server/host. User is requesting aircraft unavailability list for home bases.
     */
    public static void requestAircraftList() {
        if (!ZutiSupportMethods_NetSend.REQUEST_HB_AC_LIST) return;

        try {
            NetUser netuser = (NetUser) NetEnv.host();

            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.masterChannel() == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(AIRCRAFT_LIST);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);

            System.out.println("ZSM_NS - requestAcList executed by user >" + netuser.uniqueName() + "<.");
            ZutiSupportMethods_NetSend.REQUEST_HB_AC_LIST = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is executed by user and is sent to server/host. User is requesting a list of already completed target objectives.
     */
    public static void requestDeactivatedTargetsList() {
        try {
            NetUser netuser = (NetUser) NetEnv.host();

            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.masterChannel() == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(REMOVE_TARGET);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);

            // System.out.println("ZutiNetSendMethods - requestCompledDeactivatedTargetsList executed by user >" + netuser.uniqueName() + "<.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is executed by user and is sent to server/host. User is requesting a list of those home bases that were captured by paratroopers. Only once, when mission is loaded after player joins.
     */
    public static void requestParaCapturedHbList() {
        if (!ZutiSupportMethods_NetSend.REQUEST_PARA_CAPTURING_HB) return;

        try {
            NetUser netuser = (NetUser) NetEnv.host();

            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.masterChannel() == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(PARA_CAPTURED_HB);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);

            ZutiSupportMethods_NetSend.REQUEST_PARA_CAPTURING_HB = false;

            // System.out.println("ZutiNetSendMethods - requestParaCapturedHbList executed by user >" + netuser.uniqueName() + "<.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send message out that certain AC dropped its bomb load.
     *
     * @param acName
     * @param bool
     * @param hasBombBayDoors
     */
    public static void bombardierReleasedOrdinance_ToServer(String acName, boolean bool, boolean hasBombBayDoors) {
        try {
            if (acName == null || Main.cur().netServerParams == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(BOMBARDIER_RELEASED_ORDINANCE);
            netmsgguaranted.write255(acName);
            netmsgguaranted.writeBoolean(bool);
            netmsgguaranted.writeBoolean(hasBombBayDoors);

            if (Main.cur().netServerParams.masterChannel() != null) NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            // System.out.println("ZutiNetSendMethods - bombardierReleasedOrdinance_ToServer executed for AC >" + acName + "<.");
            else if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend_ToClients.bombardierReleasedOrdinance_ToClient(acName, bool, hasBombBayDoors);
            // System.out.println("ZutiNetSendMethods - bombardierReleasedOrdinance_ToServer forwarded to ToClient method.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send message out that bomb bay doors status changed.
     *
     * @param status 1=opened, 0=closed
     * @param acName
     */
    public static void bombBayDoorsStatus(int status, String acName) {
        try {
            if (acName == null || Main.cur().netServerParams == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(BOMB_BAY_DOORS_STATUS);
            netmsgguaranted.write255(acName);
            netmsgguaranted.writeByte(status);

            if (Main.cur().netServerParams.masterChannel() != null) NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            // System.out.println("ZutiNetSendMethods - bombBayDoorsStatus executed for AC >" + acName + "<.");
            else if (Main.cur().netServerParams.isMaster()) ZutiSupportMethods_NetSend_ToClients.bombBayDoorsStatus_ToClient(acName, status);
            // System.out.println("ZutiNetSendMethods - bombBayDoorsStatus forwarded to ToClient method.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is executed by user and is sent to server/host. User is requesting a list of those aircraft that already released their bomb load.
     */
    public static void requestAircraftListWithReleasedOrdinance() {
        if (!ZutiSupportMethods_NetSend.REQUEST_AIRCRAFT_WITH_RELEASED_ORDINANCE) return;

        try {
            NetUser netuser = (NetUser) NetEnv.host();

            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.masterChannel() == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(AIRCRAFT_WITH_RELEASED_ORDINANCE);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);

            ZutiSupportMethods_NetSend.REQUEST_AIRCRAFT_WITH_RELEASED_ORDINANCE = false;

            // System.out.println("ZutiNetSendMethods - requestAircraftListWithReleasedOrdinance executed by user >" + netuser.uniqueName() + "<.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send information to user about which aircraft have already released their bombs.
     *
     * @param netuser
     */
    public static void aircraftWithReleasedOrdinance(NetUser netuser) {
        try {
            if (netuser == null) return;

            NetMsgGuaranted netmsgguaranted = null;
            ZutiAcWithReleasedOrdinance aircraft = null;
            Iterator iterator = ZutiAcWithReleasedOrdinance.AC_THAT_RELEASED_BOMBS.keySet().iterator();
            List weaponReleaseLines = null;

            while (iterator.hasNext()) {
                aircraft = (ZutiAcWithReleasedOrdinance) ZutiAcWithReleasedOrdinance.AC_THAT_RELEASED_BOMBS.get(iterator.next());
                if (aircraft == null) continue;

                weaponReleaseLines = aircraft.getReleasedOrdinancesList();
                for (int i = 0; i < weaponReleaseLines.size(); i++) {
                    netmsgguaranted = new NetMsgGuaranted();
                    netmsgguaranted.writeByte(AIRCRAFT_WITH_RELEASED_ORDINANCE);
                    netmsgguaranted.write255(aircraft.getAcName());
                    netmsgguaranted.write255((String) weaponReleaseLines.get(i));

                    NetEnv.host().postTo(netuser.masterChannel(), netmsgguaranted);
                    // System.out.println("ZutiNetSendMethods - aircraftWithReleasedOrdinance executed for user >" + netuser.uniqueName() + "< and AC >" + aircraft.getAcName() + "<. Line: " + (String)weaponReleaseLines.get(i) );
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send message to clients info about changes with bombardier instruments.
     *
     * @param acName
     * @param distance           : <0 = decrease, >0 = increase
     * @param sideslip           : <0 = decrease, >0 = increase
     * @param altitude           : <0 = decrease, >0 = increase
     * @param speed              : <0 = decrease, >0 = increase
     * @param gunsightAutomation : 1=toggle it, 0=do nothing
     */
    public static void bombardierInstrumentsChanged_ToServer(String acName, int distance, int sideslip, int altitude, int speed, int gunsightAutomation) {
        try {
            if (acName == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(BOMBARDIER_INSTRUMENTS_CHANGED);

            netmsgguaranted.write255(acName);
            netmsgguaranted.writeByte(distance);
            netmsgguaranted.writeByte(sideslip);
            netmsgguaranted.writeByte(altitude);
            netmsgguaranted.writeByte(speed);
            netmsgguaranted.writeByte(gunsightAutomation);

            // Send to server only!
            if (Main.cur().netServerParams != null && Main.cur().netServerParams.masterChannel() != null) NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            else ZutiSupportMethods_NetSend_ToClients.bombardierInstrumentsChanged_ToClient((NetUser) NetEnv.host(), acName, distance, sideslip, altitude, speed, gunsightAutomation);

            // System.out.println("ZutiNetSendMethods - bombardierInstrumentsChanged_Server executed for AC >" + acName + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Execute this method when a crew member destroys some actor that is valid for scoring.
     *
     * @param acName
     * @param actorName
     */
    public static void syncCrewScore_ToServer(String acName, String actorName, boolean damagedOnly, double explosionPower) {
        try {
            if (acName == null || actorName == null) return;

            // Report this ONLY if you did the damage!
            NetUser netuser = (NetUser) NetEnv.host();
            if (netuser != null) if (acName.equals(netuser.uniqueName() + "_0")) {
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(SYNC_CREW_SCORE);

                netmsgguaranted.write255(acName);
                netmsgguaranted.write255(actorName);
                netmsgguaranted.writeBoolean(damagedOnly);
                netmsgguaranted.writeDouble(explosionPower);

                // Send to server only!
                if (Main.cur().netServerParams != null && Main.cur().netServerParams.masterChannel() != null) netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
                else ZutiSupportMethods_NetSend_ToClients.syncCrewScore_ToClient(netuser, acName, actorName, damagedOnly, explosionPower);

                // System.out.println("ZSM_NS - syncCrewScore_Server executed for AC >" + acName + "< and actor >" + actorName + "<. Damage report: " +damagedOnly);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Instruct server to enable or disable controlling player AC to another user.
     *
     * @param username :
     */
    public static void transferControls_ToServer(String username) {
        Aircraft playerAc = World.getPlayerAircraft();
        // System.out.println("ZutiSupportMethods - canControlAcCockpit: trying to access cockpit: " + cockpitNr + " of ac: " + acName);
        if (username == null || playerAc == null || !(playerAc.FM instanceof RealFlightModel)) return;

        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(TRANSFER_CONTROLS);
            netmsgguaranted.write255(username);

            // Send to server only!
            if (Main.cur().netServerParams != null && Main.cur().netServerParams.masterChannel() != null) // System.out.println("Sending to SERVER!");
                NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            else // We are the host, forward to user that he has controls now.
                 // System.out.println("Sending to CLIENT!");
                ZutiSupportMethods_NetSend_ToClients.transferControls_ToClient((NetUser) NetEnv.host(), username);

            // RTSConf.cur.joy.setEnable( !RTSConf.cur.joy.isEnable() );

            // System.out.println("ZutiNetSendMethods - transferControls_Server sent to user >" + username + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send new controls settings to server. It then forwards them to original plane owner.
     *
     * @param controls
     */
    public static void aircraftControlsMoved_ToServer(int eventId, float controlState) {
        try {
            Aircraft playerAc = World.getPlayerAircraft();
            if (playerAc == null) return;

            String acName = playerAc.name();

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(AIRCRAFT_CONTROLS_MOVED);
            netmsgguaranted.write255(acName);
            netmsgguaranted.writeByte(eventId);
            netmsgguaranted.writeFloat(controlState);

            // Send to server only!
            if (Main.cur().netServerParams != null && Main.cur().netServerParams.masterChannel() != null) NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            else ZutiSupportMethods_NetSend_ToClients.aircraftControlsMoved_ToClient(acName, eventId, controlState);

            // System.out.println("ZutiNetSendMethods - aircraftControlsMove_Server sent to server!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send new controls settings to server. It then forwards them to original plane owner.
     *
     * @param controls
     */
    public static void aircraftControlsChanged_ToServer(String acName, int eventId, boolean weaponControl) {
        try {
            if (acName == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(AIRCRAFT_CONTROLS_CHANGED);
            netmsgguaranted.write255(acName);
            netmsgguaranted.writeByte(eventId);
            netmsgguaranted.writeBoolean(weaponControl);

            // Send to server only!
            if (Main.cur().netServerParams != null && Main.cur().netServerParams.masterChannel() != null) NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            else ZutiSupportMethods_NetSend_ToClients.aircraftControlsChanged_ToClient(acName, eventId, weaponControl);

            // System.out.println("ZutiNetSendMethods - aircraftControlsChanged_Server sent to server!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send request to server about possible cockpit change. If server grants out wish, it will send back required data to complete this "transaction".
     *
     * @param acName
     * @param desiredCockpit
     * @param isPilot
     */
    public static void requestNewCockpitPosition(String acName, int desiredCockpit, boolean isPilot) {
        if (acName == null) return;

        if (!ZutiSupportMethods_Multicrew.CAN_REQUEST_COCKPIT_CHANGE) {
            // System.out.println("ZutiSupportMethods - requestNewCockpitPosition: can not request new cockpit position. Old request still pending!");
            HUD.log("mds.netCommand.cockpitChange1");
            HUD.log("mds.netCommand.cockpitChange2");
            return;
        }
        try {
            NetUser netuser = (NetUser) NetEnv.host();

            if (netuser == null || Main.cur().netServerParams == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(COCKPIT_CHANGE);
            netmsgguaranted.write255(acName);
            netmsgguaranted.writeInt(desiredCockpit);
            netmsgguaranted.writeBoolean(isPilot);

            if (Main.cur().netServerParams.masterChannel() != null) {
                // Send request to server
                netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
                // Until we receive requested cockpit change, no more sending this request
                ZutiSupportMethods_Multicrew.CAN_REQUEST_COCKPIT_CHANGE = false;
            } else if (Main.cur().netServerParams.isMaster()) // We are the server/host. No need to sending message forward. Just execute.
                processCockpitChangeRequest((NetUser) NetEnv.host(), acName, desiredCockpit, isPilot);

            // System.out.println("ZutiNetSendMethods - requestNewCockpitPosition executed by user >" + netuser.uniqueName() + "<. Is pilot: " + isPilot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Server received user request to change cockpit. Check if desired cockpit is free on given aircraft and if it is, notify user that he is OK to switch this cockpit.
     *
     * @param sender
     * @param netmsginput
     */
    public static void processCockpitChangeRequest(NetUser sender, String acName, int requestedPosition, boolean isPilot) {
        try {
            if (sender == null || acName == null) // System.out.println("ZutiNetSendMethods - processCockpitChangeRequest: sender or acName are null, aborting method!");
                return;

            String username = sender.uniqueName();

            System.out.println("ZutiNetSendMethods - processCockpitChangeRequest: User >" + sender.uniqueName() + "< is requesting cockpit nr.: " + requestedPosition + " in AC: " + acName);
            ZutiAircraftCrew positions = ZutiAircraftCrewManagement.getAircraftCrew(acName);
            requestedPosition = positions.checkRequestedPosition(username, requestedPosition, isPilot);
            System.out.println("ZutiNetSendMethods - processCockpitChangeRequest: User >" + sender.uniqueName() + "< can get cockpit nr.: " + requestedPosition + " in AC: " + acName);
            NetMsgGuaranted netmsgguaranted = null;

            if (requestedPosition > -1) {
                NetUser netuser = null;
                List crew = positions.getCrewList();
                for (int i = 0; i < crew.size(); i++) {
                    netuser = ZutiSupportMethods.getNetUser((String) crew.get(i));
                    if (netuser == null) continue;

                    netmsgguaranted = new NetMsgGuaranted();
                    netmsgguaranted.writeByte(COCKPIT_CHANGE);
                    netmsgguaranted.write255(sender.uniqueName());
                    netmsgguaranted.writeByte(requestedPosition);
                    netmsgguaranted.writeBoolean(isPilot);

                    if (netuser.masterChannel() != null) NetEnv.host().postTo(netuser.masterChannel(), netmsgguaranted);
                    // System.out.println("ZutiNetSendMethods - processCockpitChangeRequest for user >" + sender.uniqueName() + "<, cockpit >" + requestedPosition + "< sent to >" + netuser.uniqueName() + "<.");
                    else if (netuser.uniqueName().equals(username)) // This will happen if net user is actually host! And host requested cockpit change
                        ZutiSupportMethods_NetReceive.changeCockpit(username, requestedPosition);
                }
            } else if (sender.masterChannel() != null) {
                // User can not change cockpit position, notify only him about that
                netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(COCKPIT_CHANGE);
                netmsgguaranted.write255(sender.uniqueName());
                netmsgguaranted.writeByte(requestedPosition);
                netmsgguaranted.writeBoolean(isPilot);

                NetEnv.host().postTo(sender.masterChannel(), netmsgguaranted);
                // System.out.println("ZutiNetSendMethods - processCockpitChangeRequest for user >" + sender.uniqueName() + "<, cockpit >" + requestedPosition + "< sent to >" + sender.uniqueName() + "<.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send request to server about your plane crew.
     *
     * @param acName
     */
    public static void requestAircraftCrew(String acName) {
        try {
            NetUser netuser = (NetUser) NetEnv.host();

            if (acName == null || netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.masterChannel() == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(AIRCRAFT_CREW);
            netmsgguaranted.write255(acName);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            // System.out.println("ZutiNetSendMethods - requestAircraftCrew executed by user >" + netuser.uniqueName() + "<.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Server received user request for his crew buddies. Send him that list.
     *
     * @param sender
     * @param netmsginput
     */
    public static void aircraftCrew(NetUser sender, NetMsgInput netmsginput) {
        try {
            if (sender == null || netmsginput == null) return;

            String acName = netmsginput.read255();

            NetMsgGuaranted netmsgguaranted = null;

            List crew = ZutiAircraftCrewManagement.getAircraftCrew(acName).getCrewLines();
            String crewLine = null;
            for (int i = 0; i < crew.size(); i++) {
                crewLine = (String) crew.get(i);
                // System.out.println(" ZutiSupportMethods - Crew line: " + crewLine);
                netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(AIRCRAFT_CREW);
                netmsgguaranted.write255(crewLine);

                NetEnv.host().postTo(sender.masterChannel(), netmsgguaranted);
            }

            // System.out.println("ZutiNetSendMethods - aircraftCrew(1) executed for user >" + sender.uniqueName() + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send receiver data about his crew.
     *
     * @param receiver
     * @param acName
     */
    public static void aircraftCrew(NetUser receiver, String acName) {
        try {
            if (acName == null || receiver == null || receiver.masterChannel() == null) return;

            NetMsgGuaranted netmsgguaranted = null;

            List crew = ZutiAircraftCrewManagement.getAircraftCrew(acName).getCrewLines();
            String crewLine = null;
            for (int i = 0; i < crew.size(); i++) {
                crewLine = (String) crew.get(i);
                // System.out.println(" Crew line: " + crewLine);
                netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(AIRCRAFT_CREW);
                netmsgguaranted.write255(crewLine);

                NetEnv.host().postTo(receiver.masterChannel(), netmsgguaranted);
            }

            // System.out.println("ZutiNetSendMethods - aircraftCrew(2) executed for user >" + receiver.uniqueName() + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send request to server for live craters
     */
    public static void requestCraters() {
        try {
            NetUser netuser = (NetUser) NetEnv.host();

            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.masterChannel() == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(CRATERS);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);

            // System.out.println("ZutiNetSendMethods - requestCraters executed by user >" + netuser.uniqueName() + "<.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Call this method from server to send user list of live craters
     *
     * @param netuser
     */
    public static void cratersList(NetUser netuser) {
        try {
            if (netuser == null || World.cur() == null) return;

            NetMsgGuaranted netmsgguaranted = null;
            List craters = ZutiSupportMethods_Effects.trimCratersList();
            ZutiSupportMethods_Effects.SyncingCrater crater = null;

            for (int i = 0; i < craters.size(); i++) {
                crater = (ZutiSupportMethods_Effects.SyncingCrater) craters.get(i);
                if (crater == null) continue;

                netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(CRATERS);

                netmsgguaranted.writeLong((crater.expirationTime - Time.current()) / 1000);
                netmsgguaranted.writeFloat(crater.size);
                netmsgguaranted.writeFloat(crater.x);
                netmsgguaranted.writeFloat(crater.y);
                netmsgguaranted.writeFloat(crater.z);

                NetEnv.host().postTo(netuser.masterChannel(), netmsgguaranted);
            }

            // System.out.println("ZutiNetSendMethods - cratersList executed for user >" + netuser.uniqueName() + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send server some message that all clients then log in event log.
     *
     * @param netuser
     * @param message
     */
    public static void logMessage(NetUser netuser, String message) {
        try {
            if (message == null || Main.cur().netServerParams == null) return;

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(LOG_MESSAGE);
            netmsgguaranted.write255(message);

            if (Main.cur().netServerParams.masterChannel() != null) // Send to server as we are not it.
                NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            else if (Main.cur().netServerParams.isMaster()) EventLog.type(message);
            // we are server, just forward the message to all clients, including sender.
            // logMessage_ToClient(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send request to server that we want to refuel specified amount of fuel.
     *
     * @param fuel
     */
    public static void requestFuel(float fuel) {
        try {
            if (Main.cur().netServerParams == null || Mission.isSingle()) {
                // Single player mode, RRR must also work!
                ZutiSupportMethods_NetSend_ToClients.refuelAircraft_ToClient(fuel, null);
                return;
            }

            String user = ((NetUser) NetEnv.host()).uniqueName();
            if (Main.cur().netServerParams.masterChannel() != null) {
                // Send to server
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(RRR_REFUEL);
                netmsgguaranted.writeFloat(fuel);
                netmsgguaranted.write255(user);

                NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            } else if (Main.cur().netServerParams.isMaster()) // We are server... calculate available fuel and execute.
                ZutiSupportMethods_NetSend_ToClients.refuelAircraft_ToClient(fuel, user);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Notify server that you did not use all approved resources.
     *
     * @param unusedFuel
     * @param pos
     */
    public static void returnRRRResources_Fuel(float unusedFuel, Point3d pos) {
        try {
            NetUser netuser = (NetUser) NetEnv.host();
            int bornPlayerId = -1;
            int army = -1;

            BornPlace bp = ZutiSupportMethods_Net.getNearestBornPlace_AnyArmy(pos.x, pos.y);
            if (Mission.MDS_VARIABLES().enabledResourcesManagement_BySide && ZutiSupportMethods.isOnZAP(pos) > -1) army = Front.army(pos.x, pos.y);

            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.isMaster() || Mission.isSingle()) {
                // We are the server or game is single player based, we update resources
                System.out.println("  Aircraft is returning unused fuel: " + unusedFuel);
                if (bp != null) {
                    if (bp.zutiEnableResourcesManagement) {
                        bornPlayerId = bp.zutiBpIndex;
                        bp.zutiFuelSupply += unusedFuel;

                        ZutiSupportMethods_ResourcesManagement.printOutResourcesForHomeBase(bp);
                    }
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
                return;
            }

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(RRR_RETURN_FUEL);
            netmsgguaranted.writeFloat(unusedFuel);
            netmsgguaranted.writeInt(bornPlayerId);
            netmsgguaranted.writeInt(army);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send request to server that we want to get specified amount of bullets.
     *
     * @param bullets
     */
    public static void requestBullets(long bullets) {
        try {
            if (Main.cur().netServerParams == null || Mission.isSingle()) {
                // Single player mode, RRR must also work!
                ZutiSupportMethods_NetSend_ToClients.reloadGuns_ToClient(bullets, null);
                return;
            }

            String user = ((NetUser) NetEnv.host()).uniqueName();
            if (Main.cur().netServerParams.masterChannel() != null) {
                // Send to server
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(RRR_BULLETS);
                netmsgguaranted.writeLong(bullets);
                netmsgguaranted.write255(user);

                NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            } else if (Main.cur().netServerParams.isMaster()) // We are server... calculate available fuel and execute.
                ZutiSupportMethods_NetSend_ToClients.reloadGuns_ToClient(bullets, user);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Notify server that you did not use all approved resources.
     *
     * @param unusedBullets
     * @param pos
     */
    public static void returnRRRResources_Bullets(long unusedBullets, Point3d pos) {
        try {
            NetUser netuser = (NetUser) NetEnv.host();
            int bornPlaceId = -1;
            int army = -1;

            BornPlace bp = ZutiSupportMethods_Net.getNearestBornPlace_AnyArmy(pos.x, pos.y);
            if (Mission.MDS_VARIABLES().enabledResourcesManagement_BySide && ZutiSupportMethods.isOnZAP(pos) > -1) army = Front.army(pos.x, pos.y);

            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.isMaster() || Mission.isSingle()) {
                // We are the server or game is single player based, we update resources
                System.out.println("  Aircraft is returning unused bullets: " + unusedBullets);
                if (bp != null) {
                    if (bp.zutiEnableResourcesManagement) {
                        bornPlaceId = bp.zutiBpIndex;
                        bp.zutiBulletsSupply += unusedBullets;

                        ZutiSupportMethods_ResourcesManagement.printOutResourcesForHomeBase(bp);
                    }
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
                return;
            }

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(RRR_RETURN_BULLETS);
            netmsgguaranted.writeLong(unusedBullets);
            netmsgguaranted.writeInt(bornPlaceId);
            netmsgguaranted.writeInt(army);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send request to server that we want to get specified amount of bullets.
     *
     * @param bullets
     */
    public static void requestBombs(int[] bombs) {
        try {
            if (Main.cur().netServerParams == null || Mission.isSingle()) {
                // Single player mode, RRR must also work!
                ZutiSupportMethods_NetSend_ToClients.reloadBombs_ToClient(bombs, null);
                return;
            }

            String user = ((NetUser) NetEnv.host()).uniqueName();
            if (Main.cur().netServerParams.masterChannel() != null) {
                // Send to server
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(RRR_BOMBS);
                netmsgguaranted.writeInt(bombs[0]);
                netmsgguaranted.writeInt(bombs[1]);
                netmsgguaranted.writeInt(bombs[2]);
                netmsgguaranted.writeInt(bombs[3]);
                netmsgguaranted.writeInt(bombs[4]);
                netmsgguaranted.writeInt(bombs[5]);
                netmsgguaranted.write255(user);

                NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            } else if (Main.cur().netServerParams.isMaster()) // We are server... calculate available fuel and execute.
                ZutiSupportMethods_NetSend_ToClients.reloadBombs_ToClient(bombs, user);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Notify server that you did not use all approved resources.
     *
     * @param unusedBullets
     * @param pos
     */
    public static void returnRRRResources_Bombs(int[] unusedBombs, Point3d pos) {
        try {
            NetUser netuser = (NetUser) NetEnv.host();
            int bornPlaceId = -1;
            int army = -1;

            BornPlace bp = ZutiSupportMethods_Net.getNearestBornPlace_AnyArmy(pos.x, pos.y);
            if (Mission.MDS_VARIABLES().enabledResourcesManagement_BySide && ZutiSupportMethods.isOnZAP(pos) > -1) army = Front.army(pos.x, pos.y);

            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.isMaster() || Mission.isSingle()) {
                // We are the server or game is single player based, we update resources
                System.out.println("  Aircraft is returning unused bombs...");
                if (bp != null) {
                    if (bp.zutiEnableResourcesManagement) {
                        bornPlaceId = bp.zutiBpIndex;
                        bp.zutiBombsSupply[0] += unusedBombs[0];
                        bp.zutiBombsSupply[1] += unusedBombs[1];
                        bp.zutiBombsSupply[2] += unusedBombs[2];
                        bp.zutiBombsSupply[3] += unusedBombs[3];
                        bp.zutiBombsSupply[4] += unusedBombs[4];
                        bp.zutiBombsSupply[5] += unusedBombs[5];

                        ZutiSupportMethods_ResourcesManagement.printOutResourcesForHomeBase(bp);
                    }
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
                return;
            }

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(RRR_RETURN_BOMBS);
            netmsgguaranted.writeInt(unusedBombs[0]);
            netmsgguaranted.writeInt(unusedBombs[1]);
            netmsgguaranted.writeInt(unusedBombs[2]);
            netmsgguaranted.writeInt(unusedBombs[3]);
            netmsgguaranted.writeInt(unusedBombs[4]);
            netmsgguaranted.writeInt(unusedBombs[5]);
            netmsgguaranted.writeInt(bornPlaceId);
            netmsgguaranted.writeInt(army);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send request to server that we want to get specified amount of rockets.
     *
     * @param bullets
     */
    public static void requestRockets(long rockets) {
        try {
            if (Main.cur().netServerParams == null || Mission.isSingle()) {
                // Single player mode, RRR must also work!
                ZutiSupportMethods_NetSend_ToClients.reloadRockets_ToClient(rockets, null);
                return;
            }

            String user = ((NetUser) NetEnv.host()).uniqueName();
            if (Main.cur().netServerParams.masterChannel() != null) {
                // Send to server
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(RRR_ROCKETS);
                netmsgguaranted.writeLong(rockets);
                netmsgguaranted.write255(user);

                NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            } else if (Main.cur().netServerParams.isMaster()) // We are server... calculate available fuel and execute.
                ZutiSupportMethods_NetSend_ToClients.reloadRockets_ToClient(rockets, user);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Notify server that you did not use all approved resources.
     *
     * @param unusedBullets
     * @param pos
     */
    public static void returnRRRResources_Rockets(long unusedRockets, Point3d pos) {
        try {
            NetUser netuser = (NetUser) NetEnv.host();
            int bornPlaceId = -1;
            int army = -1;

            BornPlace bp = ZutiSupportMethods_Net.getNearestBornPlace_AnyArmy(pos.x, pos.y);
            if (Mission.MDS_VARIABLES().enabledResourcesManagement_BySide && ZutiSupportMethods.isOnZAP(pos) > -1) army = Front.army(pos.x, pos.y);

            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.isMaster() || Mission.isSingle()) {
                // We are the server or game is single player based, we update resources
                System.out.println("  Aircraft is returning unused rockets: " + unusedRockets);
                if (bp != null) {
                    if (bp.zutiEnableResourcesManagement) {
                        bornPlaceId = bp.zutiBpIndex;
                        bp.zutiRocketsSupply += unusedRockets;

                        ZutiSupportMethods_ResourcesManagement.printOutResourcesForHomeBase(bp);
                    }
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
                return;
            }

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(RRR_RETURN_ROCKETS);
            netmsgguaranted.writeLong(unusedRockets);
            netmsgguaranted.writeInt(bornPlaceId);
            netmsgguaranted.writeInt(army);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send request to server that we want to get new engine.
     *
     * @param engines
     * @param engineId
     */
    public static void requestEngine(int engines, int engineId) {
        try {
            if (Main.cur().netServerParams == null || Mission.isSingle()) {
                // Single player mode, RRR must also work!
                ZutiSupportMethods_NetSend_ToClients.repairEngine_ToClient(engines, engineId, null);
                return;
            }

            String user = ((NetUser) NetEnv.host()).uniqueName();
            if (Main.cur().netServerParams.masterChannel() != null) {
                // Send to server
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(RRR_ENGINE);
                netmsgguaranted.writeInt(engines);
                netmsgguaranted.writeInt(engineId);
                netmsgguaranted.write255(user);

                NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            } else if (Main.cur().netServerParams.isMaster()) // We are server... calculate available fuel and execute.
                ZutiSupportMethods_NetSend_ToClients.repairEngine_ToClient(engines, engineId, user);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Notify server that you did not use all approved resources.
     *
     * @param unusedEngines
     * @param pos
     */
    public static void returnRRRResources_Engine(long unusedEngines, Point3d pos) {
        try {
            NetUser netuser = (NetUser) NetEnv.host();
            int bornPlaceId = -1;
            int army = -1;

            BornPlace bp = ZutiSupportMethods_Net.getNearestBornPlace_AnyArmy(pos.x, pos.y);
            if (Mission.MDS_VARIABLES().enabledResourcesManagement_BySide && ZutiSupportMethods.isOnZAP(pos) > -1) army = Front.army(pos.x, pos.y);

            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.isMaster() || Mission.isSingle()) {
                // We are the server or game is single player based, we update resources
                System.out.println("  Aircraft is returning unused engines: " + unusedEngines);
                if (bp != null) {
                    if (bp.zutiEnableResourcesManagement) {
                        bornPlaceId = bp.zutiBpIndex;
                        bp.zutiEnginesSupply += unusedEngines;

                        ZutiSupportMethods_ResourcesManagement.printOutResourcesForHomeBase(bp);
                    }
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
                return;
            }

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(RRR_RETURN_ENGINE);
            netmsgguaranted.writeLong(unusedEngines);
            netmsgguaranted.writeInt(bornPlaceId);
            netmsgguaranted.writeInt(army);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send request to server that we want to get one repair kit.
     *
     */
    public static void requestRepairKit(long requestedKits) {
        try {
            if (Main.cur().netServerParams == null || Mission.isSingle()) {
                // Single player mode, RRR must also work!
                ZutiSupportMethods_NetSend_ToClients.repairKit_ToClient(requestedKits, null);
                return;
            }

            String user = ((NetUser) NetEnv.host()).uniqueName();
            if (Main.cur().netServerParams.masterChannel() != null) {
                // Send to server
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(RRR_REPAIR_KIT);
                netmsgguaranted.writeLong(requestedKits);
                netmsgguaranted.write255(user);

                NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            } else if (Main.cur().netServerParams.isMaster()) // We are server... calculate available fuel and execute.
                ZutiSupportMethods_NetSend_ToClients.repairKit_ToClient(requestedKits, user);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Notify server that you did not use all approved resources.
     *
     * @param unusedEngines
     * @param pos
     */
    public static void returnRRRResources_RepairKit(long unusedRepairKits, Point3d pos) {
        try {
            NetUser netuser = (NetUser) NetEnv.host();
            int bornPlaceId = -1;
            int army = -1;

            BornPlace bp = ZutiSupportMethods_Net.getNearestBornPlace_AnyArmy(pos.x, pos.y);
            if (Mission.MDS_VARIABLES().enabledResourcesManagement_BySide && ZutiSupportMethods.isOnZAP(pos) > -1) army = Front.army(pos.x, pos.y);

            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.isMaster() || Mission.isSingle()) {
                // We are the server or game is single player based, we update resources
                System.out.println("  Aircraft is returning unused repair kits: " + unusedRepairKits);
                if (bp != null) {
                    if (bp.zutiEnableResourcesManagement) {
                        bornPlaceId = bp.zutiBpIndex;
                        bp.zutiRepairKitsSupply += unusedRepairKits;

                        ZutiSupportMethods_ResourcesManagement.printOutResourcesForHomeBase(bp);
                    }
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
                return;
            }

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(RRR_RETURN_REPAIR_KIT);
            netmsgguaranted.writeLong(unusedRepairKits);
            netmsgguaranted.writeInt(bornPlaceId);
            netmsgguaranted.writeInt(army);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Report user amount of resources that he can use on his spawn place.
     *
     * @param aircraft
     */
    public static void reportSpawnResources(Aircraft aircraft) {
        try {
            if (!ZutiSupportMethods.isResourcesManagementValid(aircraft.pos.getAbsPoint())) return;

            long bullets = ZutiSupportMethods_GameOrder.calculateBulletsToReload(aircraft);
            int[] bombs = ZutiSupportMethods_GameOrder.getBombsCount(aircraft);
            long rockets = ZutiSupportMethods_GameOrder.calculateRocketsToReload(aircraft);
            float fuel = aircraft.FM.M.fuel;
            long cargo = ZutiSupportMethods_GameOrder.getCargoCount(aircraft);

            if (Main.cur().netServerParams == null || Mission.isSingle()) {
                // Single player mode, RRR must also work!
                ZutiSupportMethods_NetSend_ToClients.spawnResources_ToClient(bullets, bombs, rockets, fuel, cargo, null);
                return;
            }

            if (Main.cur().netServerParams.isMaster()) {
                // We are server... calculate available fuel and send that info to client
                // User name is aircraft name - _0
                String user = ZutiSupportMethods.getUserNameFromAcName(aircraft.name());
                ZutiSupportMethods_NetSend_ToClients.spawnResources_ToClient(bullets, bombs, rockets, fuel, cargo, user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is executed by user and is sent to server/host. User is requesting aircraft at his selected home base.
     */
    public static void requestAircraft(String acName) {
        System.out.println("Requesting " + acName);
        try {
            NetUser netuser = (NetUser) NetEnv.host();

            if (netuser == null) return;

            if (Main.cur().netServerParams != null && Main.cur().netServerParams.masterChannel() != null) {
//				System.out.println("Sending to SERVER!");
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(AIRCRAFT_AVAILABILITY);
                netmsgguaranted.write255(acName);
                netmsgguaranted.writeInt(netuser.getBornPlace());

                NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            } else // We are the host, forward to user that he has controls now.
                   // System.out.println("Sending to CLIENT!");
                if (ZutiSupportMethods_Air.getAircraft(acName, netuser.getBornPlace()) != null) // Aircraft available, spawn!
                    ZutiSupportMethods_GUI.spawn_ServerGUI();
                else new GWindowMessageBox(Main3D.cur3D().guiManager.root, 20.0F, true, ZutiSupportMethods_GUI.GAME_STATE.i18n("mds.info.aircraftAvailability"), ZutiSupportMethods_GUI.GAME_STATE.i18n("mds.info.aircraftNotAvailable"), 3, 0.0F);

            // System.out.println("ZutiNetSendMethods - getUnavailablePlanesList executed by user >" + netuser.uniqueName() + "<.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Call this method to get carrier spawn place
     */
    public static void requestCarrierSpawnPlace() {
        try {
            NetUser netuser = (NetUser) NetEnv.host();
            if (netuser == null) return;

            BornPlace bp = (BornPlace) World.cur().bornPlaces.get(netuser.getBornPlace());
            // If home base is not positioned on a ship, don't execute this.
            if (!bp.zutiAlreadyAssigned || !bp.zutiEnableQueue) return;

            if (Main.cur().netServerParams.masterChannel() != null) {
                // Send to server
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(CARRIER_SPAWN_PLACE);

                NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            } else if (Main.cur().netServerParams.isMaster()) // We are server...
                ZutiSupportMethods_NetSend_ToClients.carrierStayPlace_ToClient((NetUser) NetEnv.host());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Call this method to release carrier spawn place
     */
    public static void releaseCarrierSpawnPlace(NetUser netUser) {
        try {
            if (netUser.getAirdromeStay() < 0) return;

            int userBornPlace = netUser.getBornPlace();

            // +++ MDS Hotfix by Storebror
            if (World.cur().bornPlaces != null) {
                if (userBornPlace >= World.cur().bornPlaces.size()) {
                    netUser.zutiSetAirdomeStay(-1);
                    return;
                }

                BornPlace bp = (BornPlace) World.cur().bornPlaces.get(userBornPlace);
                if (bp == null || bp.zutiAirspawnOnly || !bp.zutiAlreadyAssigned || !bp.zutiEnableQueue) return;
            }

            if (Main.cur().netServerParams.masterChannel() != null) {
                // Send to server
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(RELEASE_CARRIER_SPAWN_PLACE);
                netmsgguaranted.writeInt(userBornPlace);
                netmsgguaranted.writeInt(netUser.getAirdromeStay());

                NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            } else if (Main.cur().netServerParams.isMaster()) // We are server...
                ZutiSupportMethods_Net.releaseStayPlace(netUser.uniqueName(), userBornPlace, netUser.getAirdromeStay());

            // Just make sure that none is selected!
            netUser.zutiSetAirdomeStay(-1);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Call this method to eject complete AC crew. Call it only if you are owner of this AC.
     */
    public static void ejectAircraftCrew(Aircraft aircraft) {
        if (aircraft == null || !(aircraft.FM instanceof RealFlightModel)) return;

        try {
            if (Main.cur().netServerParams.masterChannel() != null) {
                // Send to server
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(EJECT_CREW);
                netmsgguaranted.write255(aircraft.name());

                NetEnv.host().postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
            } else if (Main.cur().netServerParams.isMaster()) // We are server...
                ZutiSupportMethods_Multicrew.ejectGunnersForAircraft(aircraft.name());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is executed by user and is sent to server/host. User is requesting others to update their net places. If they are a crew, that is.
     */
    public static void requestNetPlacesUpdate() {
        try {
            NetUser netuser = (NetUser) NetEnv.host();

            if (netuser == null) netuser = (NetUser) NetEnv.host();

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(UPDATE_NET_PLACE);
            netuser.post(netmsgguaranted);

            // System.out.println("ZutiNetSendMethods - requestTestMessage executed by user >" + netuser.uniqueName() + "<.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is executed by user and is sent to server/host. User is requesting a list of front markers so that the sync of the front line can be as accurate as possible.
     */
    public static void requestFrontMarkers() {
        if (!ZutiSupportMethods_NetSend.REQUEST_FRONT_MARKERS || Front.markers() == null || Front.markers().size() == 0) return;

        try {
            NetUser netuser = (NetUser) NetEnv.host();

            if (netuser == null || Main.cur().netServerParams == null || Main.cur().netServerParams.masterChannel() == null) return;

            if (Front.markers() != null) Front.markers().clear();

            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(ZutiSupportMethods_NetSend.FRONT_MARKER);
            // Send to server only!
            netuser.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);

            ZutiSupportMethods_NetSend.REQUEST_FRONT_MARKERS = false;

            System.out.println("ZutiNetSendMethods - requestParaCapturedHbList executed by user >" + netuser.uniqueName() + "<.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send user locations about front markers.
     *
     * @param netuser
     */
    public static void frontMarkers(NetUser netuser) {
        try {
            if (netuser == null || Front.markers() == null || Front.markers().size() == 0) return;

            Front.Marker marker = null;
            NetMsgGuaranted netmsgguaranted = null;

            for (int i = 0; i < Front.markers().size(); i++) {
                marker = (Front.Marker) Front.markers().get(i);
                if (marker == null) continue;

                netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(FRONT_MARKER);

                netmsgguaranted.writeDouble(marker.x);
                netmsgguaranted.writeDouble(marker.y);
                netmsgguaranted.writeByte(marker.army);
                netmsgguaranted.write255(marker.zutiMarkerCarrierName);

                NetEnv.host().postTo(netuser.masterChannel(), netmsgguaranted);
            }

            netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(FRONT_REFRESH);
            NetEnv.host().postTo(netuser.masterChannel(), netmsgguaranted);
//			System.out.println("ZutiNetSendMethods - frontMarkers sent to user >" + netuser.uniqueName() + "<.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}