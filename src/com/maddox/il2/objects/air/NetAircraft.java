/* 4.10.1 class */
package com.maddox.il2.objects.air;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.Airport;
import com.maddox.il2.ai.AirportCarrier;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.Regiment;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.Way;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.Wing;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Point_Stay;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorFilter;
import com.maddox.il2.engine.ActorNet;
import com.maddox.il2.engine.ActorSpawn;
import com.maddox.il2.engine.ActorSpawnArg;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.FlightModelTrack;
import com.maddox.il2.fm.Turret;
import com.maddox.il2.fm.ZutiSupportMethods_FM;
import com.maddox.il2.game.DServer;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.ZutiSupportMethods_Multicrew;
import com.maddox.il2.net.BornPlace;
import com.maddox.il2.net.NetMissionTrack;
import com.maddox.il2.net.NetSquadron;
import com.maddox.il2.net.NetUser;
import com.maddox.il2.net.NetWing;
import com.maddox.il2.net.ZutiSupportMethods_NetSend;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.Ship.RwyTransp;
import com.maddox.il2.objects.ships.Ship.RwyTranspSqr;
import com.maddox.il2.objects.ships.Ship.RwyTranspWide;
import com.maddox.il2.objects.sounds.SndAircraft;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Message;
import com.maddox.rts.MsgAction;
import com.maddox.rts.MsgNet;
import com.maddox.rts.NetChannel;
import com.maddox.rts.NetChannelInStream;
import com.maddox.rts.NetChannelOutStream;
import com.maddox.rts.NetEnv;
import com.maddox.rts.NetMsgFiltered;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.NetMsgSpawn;
import com.maddox.rts.NetObj;
import com.maddox.rts.NetSpawn;
import com.maddox.rts.NetUpdate;
import com.maddox.rts.ObjIO;
import com.maddox.rts.Property;
import com.maddox.rts.Spawn;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.CommonTools;
import com.maddox.util.IntHashtable;

public abstract class NetAircraft extends SndAircraft {
    static long              timeOfPrevSpawn                   = -1L;

    public static final int  FM_AI                             = 0;
    public static final int  FM_REAL                           = 1;
    public static final int  FM_NET_CLIENT                     = 2;
    public static String     loadingCountry;
    public static boolean    loadingCoopPlane;
    protected String         thisWeaponsName                   = null;
    protected boolean        bPaintShemeNumberOn               = true;
    private boolean          bCoopPlane;
    private FlightModelTrack fmTrack;
    public static final int  NETG_ID_TOMASTER                  = 128;
    public static final int  NETG_ID_CODE_ASTATE               = 0;
    public static final int  NETG_ID_CODE_UPDATE_WAY           = 1;
    public static final int  NETG_ID_CODE_UPDATE_WEAPONS       = 2;
    public static final int  NETG_ID_CODE_GUNPODS_ON           = 3;
    public static final int  NETG_ID_CODE_GUNPODS_OFF          = 4;
    public static final int  NETG_ID_CODE_DROP_FUEL_TANKS      = 5;
    public static final int  NETG_ID_CODE_HIT                  = 6;
    public static final int  NETG_ID_CODE_HIT_PROP             = 7;
    public static final int  NETG_ID_CODE_CUT                  = 8;
    public static final int  NETG_ID_CODE_EXPLODE              = 9;
    public static final int  NETG_ID_CODE_DEAD                 = 10;
    public static final int  NETG_ID_CODE_FIRST_UPDATE         = 11;
    public static final int  NETG_ID_CODE_COCKPIT_ENTER        = 12;
    public static final int  NETG_ID_CODE_COCKPIT_AUTO         = 13;
    public static final int  NETG_ID_CODE_COCKPIT_DRIVER       = 14;
    public static final int  NETG_ID_CODE_DROP_EXTERNAL_STORES = 15;
    // TODO: +++ TD AI code backport from 4.13 +++
    public static final int  NETG_ID_CODE_GENERATE_WP          = 16;
    // TODO: --- TD AI code backport from 4.13 ---
    protected boolean        bGunPodsExist                     = false;
    protected boolean        bGunPodsOn                        = true;
    private int              netCockpitIndxPilot               = 0;
    private int              netCockpitWeaponControlNum        = 0;
    private int              netCockpitTuretNum                = -1;
    private boolean          netCockpitValid                   = false;
    private NetMsgGuaranted  netCockpitMsg                     = null;
    private boolean          bWeaponsEventLog                  = false;
    private Actor[]          netCockpitDrivers                 = null;
    private static Point3d   corn                              = new Point3d();
    private static Point3d   corn1                             = new Point3d();
    private static Point3d   pship                             = new Point3d();
    private static Loc       lCorn                             = new Loc();
    static ClipFilter        clipFilter                        = new ClipFilter();
    private ArrayList        damagers                          = new ArrayList();
    private Actor            damagerExclude                    = null;
    private Actor            damager_                          = null;

    // TODO: |ZUTI| variables
    public static boolean ZUTI_REFLY_OWERRIDE = false;

    static class DamagerItem {
        public Actor damager;
        public int   damage;
        public long  lastTime;

        public DamagerItem(Actor actor, int i) {
            this.damager = actor;
            this.damage = i;
            this.lastTime = Time.current();
        }
    }

    public static class SPAWN implements ActorSpawn, NetSpawn {
        public Class    cls;
        private NetUser _netUser;

        public SPAWN(Class var_class) {
            this.cls = var_class;
            Spawn.add(this.cls, this);
        }

        private Actor actorSpawnCoop(ActorSpawnArg actorspawnarg) {
            if (actorspawnarg.name == null) return null;
            String string = actorspawnarg.name.substring(3);
            boolean bool = false;
            NetAircraft netaircraft = (NetAircraft) Actor.getByName(string);
            if (netaircraft == null) {
                netaircraft = (NetAircraft) Actor.getByName(" " + string);
                if (netaircraft != null) bool = true;
            }
            if (netaircraft == null) return null;
            actorspawnarg.name = null;
            Wing wing = netaircraft.getWing();
            NetAircraft.loadingCountry = wing.regiment().country();
            NetAircraft netaircraft_0_;
            try {
                netaircraft_0_ = (NetAircraft) this.cls.newInstance();
            } catch (Exception exception) {
                NetAircraft.loadingCountry = null;
                printDebug(exception);
                return null;
            }
            netaircraft_0_.bCoopPlane = true;
            int i = netaircraft.aircIndex();
            if (!bool) {
                netaircraft.setName(" " + string);
                netaircraft.collide(false);
            }
            netaircraft_0_.setName(string);
            if (actorspawnarg.bPlayer && actorspawnarg.netChannel == null) {
                World.setPlayerAircraft((Aircraft) netaircraft_0_);
                netaircraft_0_.setFM(1, true);
                World.setPlayerFM();
            } else if (Mission.isServer()) netaircraft_0_.setFM(1, actorspawnarg.netChannel == null);
            else if (this._netUser != null && this._netUser.isTrackWriter()) {
                World.setPlayerAircraft((Aircraft) netaircraft_0_);
                netaircraft_0_.setFM(1, false);
                World.setPlayerFM();
            } else netaircraft_0_.setFM(2, actorspawnarg.netChannel == null);
            netaircraft_0_.FM.M.fuel = actorspawnarg.fuel * netaircraft_0_.FM.M.maxFuel;

            netaircraft_0_.bPaintShemeNumberOn = actorspawnarg.bNumberOn;
            netaircraft_0_.FM.AS.bIsEnableToBailout = netaircraft.FM.AS.bIsEnableToBailout;
            netaircraft_0_.createNetObject(actorspawnarg.netChannel, actorspawnarg.netIdRemote);
            ((AircraftNet) netaircraft_0_.net).netUser = this._netUser;
            ((AircraftNet) netaircraft_0_.net).netName = string;
            netaircraft_0_.FM.setSkill(netaircraft.FM.Skill);
            try {
                netaircraft_0_.weaponsLoad(actorspawnarg.weapons);
                netaircraft_0_.thisWeaponsName = actorspawnarg.weapons;

                // System.out.println("WEAPONS: " + actorspawnarg.weapons);
            } catch (Exception exception) {
                printDebug(exception);

                // TODO: Added by SAS~Storebror: In case of non-existing loadout name, try to load "default" loadout instead to avoid missing spawners
                try {
                    System.out.println("Weapon set '" + actorspawnarg.weapons + "' not registered in " + ObjIO.classGetName(netaircraft_0_.getClass()) + ", trying to load Default loadout instead!");
                    actorspawnarg.weapons = "default";
                    netaircraft_0_.weaponsLoad(actorspawnarg.weapons);
                    netaircraft_0_.thisWeaponsName = actorspawnarg.weapons;
                } catch (Exception exception2) {
                    printDebug(exception);
                }
            }
            if (this._netUser != null && (netaircraft_0_.net.isMaster() || this._netUser.isTrackWriter())) netaircraft_0_.createCockpits();
            netaircraft_0_.FM.AP.way = new Way(netaircraft.FM.AP.way);
            netaircraft_0_.onAircraftLoaded();
            wing.airc[i] = (Aircraft) netaircraft_0_;
            netaircraft_0_.setArmy(netaircraft.getArmy());
            netaircraft_0_.setOwner(wing);
            if (this._netUser != null && (netaircraft_0_.net.isMaster() || this._netUser.isTrackWriter())) World.setPlayerRegiment();
            if (Mission.isServer()) ((Maneuver) netaircraft.FM).Group.changeAircraft((Aircraft) netaircraft, (Aircraft) netaircraft_0_);
            netaircraft_0_.FM.CT.set(netaircraft.FM.CT);
            netaircraft_0_.FM.CT.forceGear(netaircraft_0_.FM.CT.GearControl);
            Aircraft.forceGear(netaircraft_0_.getClass(), netaircraft_0_.hierMesh(), netaircraft_0_.FM.CT.getGear());
            netaircraft_0_.pos.setAbs(netaircraft.pos.getAbs());
            netaircraft_0_.pos.reset();
            Vector3d vector3d = new Vector3d();
            netaircraft.getSpeed(vector3d);
            netaircraft_0_.setSpeed(vector3d);
            if (netaircraft.FM.brakeShoe) {
                ((Aircraft) netaircraft_0_).FM.AP.way.takeoffAirport = netaircraft.FM.AP.way.takeoffAirport;
                ((Aircraft) netaircraft_0_).FM.brakeShoe = true;
                ((Aircraft) netaircraft_0_).FM.turnOffCollisions = true;
                ((Aircraft) netaircraft_0_).FM.brakeShoeLoc.set(netaircraft.FM.brakeShoeLoc);
                ((Aircraft) netaircraft_0_).FM.brakeShoeLastCarrier = netaircraft.FM.brakeShoeLastCarrier;
                ((Aircraft) netaircraft_0_).FM.Gears.bFlatTopGearCheck = true;
                ((Aircraft) netaircraft_0_).makeMirrorCarrierRelPos();
            }
            if (netaircraft.FM.CT.bHasWingControl) {
                ((Aircraft) netaircraft_0_).FM.CT.wingControl = netaircraft.FM.CT.wingControl;
                ((Aircraft) netaircraft_0_).FM.CT.forceWing(netaircraft.FM.CT.wingControl);
            }
            netaircraft_0_.preparePaintScheme();
            netaircraft_0_.prepareCamouflage();
            NetAircraft.loadingCountry = null;
            if (this._netUser != null) {
                this._netUser.tryPrepareSkin(netaircraft_0_);
                this._netUser.tryPrepareNoseart(netaircraft_0_);
                this._netUser.tryPreparePilot(netaircraft_0_);
                this._netUser.setArmy(netaircraft_0_.getArmy());
            } else if (Config.isUSE_RENDER()) Mission.cur().prepareSkinAI((Aircraft) netaircraft_0_);
            netaircraft_0_.restoreLinksInCoopWing();
            if (netaircraft_0_.net.isMaster() && (!World.cur().diffCur.Takeoff_N_Landing || netaircraft.FM.AP.way.get(0).Action != 1 || !netaircraft.FM.isStationedOnGround())) {
                netaircraft_0_.FM.EI.setCurControlAll(true);
                netaircraft_0_.FM.EI.setEngineRunning();
                netaircraft_0_.FM.CT.setPowerControl(0.75F);
                netaircraft_0_.FM.setStationedOnGround(false);
                netaircraft_0_.FM.setWasAirborne(true);
            }

            return netaircraft_0_;
        }

        private void netSpawnCoop(int i, NetMsgInput netmsginput) {
            try {
                ActorSpawnArg actorspawnarg = new ActorSpawnArg();
                actorspawnarg.fuel = netmsginput.readFloat();
                actorspawnarg.bNumberOn = netmsginput.readBoolean();
                actorspawnarg.name = "net" + netmsginput.read255();
                actorspawnarg.weapons = netmsginput.read255();
                this._netUser = (NetUser) netmsginput.readNetObj();
                actorspawnarg.netChannel = netmsginput.channel();
                actorspawnarg.netIdRemote = i;
                NetAircraft netaircraft = (NetAircraft) this.actorSpawnCoop(actorspawnarg);
                netSpawnCommon(netmsginput, actorspawnarg);
                netaircraft.pos.setAbs(actorspawnarg.point, actorspawnarg.orient);
                netaircraft.pos.reset();
                netaircraft.setSpeed(actorspawnarg.speed);
                netSpawnCommon(netmsginput, actorspawnarg, netaircraft);
            } catch (Exception exception) {
                printDebug(exception);
            }
        }

        private Actor _actorSpawn(ActorSpawnArg actorspawnarg) {
            NetSquadron netsquadron = null;
            NetWing netwing = null;
            int i;
            NetAircraft netaircraft;
            try {
                int i_2_ = actorspawnarg.name.length();
                if (this._netUser != null) {
                    i = Integer.parseInt(actorspawnarg.name.substring(i_2_ - 2, i_2_));
                    int i_3_ = actorspawnarg.name.charAt(i_2_ - 3) - 48;
                    int i_4_ = actorspawnarg.name.charAt(i_2_ - 4) - 48;
                    Regiment regiment;
                    if (i_2_ == 4) regiment = this._netUser.netUserRegiment;
                    else {
                        String string = actorspawnarg.name.substring(0, i_2_ - 4);
                        regiment = (Regiment) Actor.getByName(string);
                    }
                    netsquadron = new NetSquadron(regiment, i_4_);
                    netwing = new NetWing(netsquadron, i_3_);
                } else {
                    i = Integer.parseInt(actorspawnarg.name.substring(i_2_ - 1, i_2_)) + 1;
                    int i_5_ = actorspawnarg.name.charAt(i_2_ - 2) - 48;
                    int i_6_ = actorspawnarg.name.charAt(i_2_ - 3) - 48;
                    i += i_6_ * 16 + i_5_ * 4;
                    String string = actorspawnarg.name.substring(0, i_2_ - 3);
                    Regiment regiment = (Regiment) Actor.getByName(string);
                    netsquadron = new NetSquadron(regiment, i_6_);
                    netwing = new NetWing(netsquadron, i_5_);
                }
                NetAircraft.loadingCountry = netsquadron.regiment().country();

                netaircraft = (NetAircraft) this.cls.newInstance();
            } catch (Exception exception) {
                if (netsquadron != null) netsquadron.destroy();
                if (netwing != null) netwing.destroy();
                NetAircraft.loadingCountry = null;
                printDebug(exception);
                return null;
            }
            netaircraft.bCoopPlane = false;
            netaircraft.createNetObject(actorspawnarg.netChannel, actorspawnarg.netIdRemote);
            ((AircraftNet) netaircraft.net).netUser = this._netUser;
            ((AircraftNet) netaircraft.net).netName = actorspawnarg.name;
            if (this._netUser != null) {
                actorspawnarg.name = null;
                this.makeName(netaircraft);
            }
            if (actorspawnarg.bPlayer && actorspawnarg.netChannel == null || this._netUser != null && this._netUser.isTrackWriter()) {
                World.cur().resetUser();
                World.setPlayerAircraft((Aircraft) netaircraft);
                netaircraft.setFM(1, actorspawnarg.netChannel == null);
                World.setPlayerFM();
                actorspawnarg.bPlayer = false;
            }
            // TODO: Added by |ZUTI|
            // ---------------------------------------------------------
            else if (Mission.isServer()) netaircraft.setFM(1, actorspawnarg.netChannel == null);
            // ---------------------------------------------------------
            else netaircraft.setFM(2, actorspawnarg.netChannel == null);
            netaircraft.FM.setSkill(3);
            netaircraft.FM.M.fuel = actorspawnarg.fuel * netaircraft.FM.M.maxFuel;

            netaircraft.bPaintShemeNumberOn = actorspawnarg.bNumberOn;

            // TODO: Added by |ZUTI|: adding info about multiCrew positions
            // -----------------------------------------------------------------------
            netaircraft.FM.AS.zutiSetMultiCrew(actorspawnarg.bZutiMultiCrew);
            netaircraft.FM.AS.zutiSetMultiCrewAnytime(actorspawnarg.bZutiMultiCrewAnytime);
            // -----------------------------------------------------------------------

            try {
                netaircraft.weaponsLoad(actorspawnarg.weapons);
                netaircraft.thisWeaponsName = actorspawnarg.weapons;
                // System.out.println("WEAPONS: " + actorspawnarg.weapons);
                // NetAircraft.zutiShowWeapons(netaircraft.FM.CT.Weapons);
            } catch (Exception exception) {
                printDebug(exception);
            }
            if (netaircraft.net.isMaster() || this._netUser != null && this._netUser.isTrackWriter()) netaircraft.createCockpits();
            netaircraft.onAircraftLoaded();
            NetAircraft.loadingCountry = null;
            boolean airStarting = false;
            BornPlace bornplace = null;
            if (actorspawnarg.bornPlaceExist) {
                bornplace = (BornPlace) World.cur().bornPlaces.get(actorspawnarg.bornPlace);
                // System.out.println("NetAircraft - spawn place designation: " +
                // actorspawnarg.stayPlace);
                Loc loc = bornplace.getAircraftPlace((Aircraft) netaircraft, actorspawnarg.stayPlace);
                actorspawnarg.point = loc.getPoint();
                actorspawnarg.orient = loc.getOrient();

                // TODO: Added by |ZUTI|: check if you are spawning on my own defined spawn
                // place
                // ---------------------------------------
                if (actorspawnarg.stayPlace > 0) {
                    Point_Stay[] zutiPss = World.cur().airdrome.stay[actorspawnarg.stayPlace];
                    Point_Stay zutiPs = zutiPss[0];
                    if (zutiPs.zutiLocation != null) {
                        actorspawnarg.point = zutiPs.zutiLocation.getPoint();
                        actorspawnarg.orient = zutiPs.zutiLocation.getOrient();
                        // actorspawnarg.point.z = 288F;

                        // System.out.println("SET!");
                    }
                }
                // ---------------------------------------

                actorspawnarg.armyExist = true;
                actorspawnarg.army = bornplace.army;
                actorspawnarg.speed = new Vector3d();

                // TODO: Edited by |ZUTI|: added my checks for air spawn occasions
                boolean carrierAirStart = actorspawnarg.stayPlace < 0 && bornplace.zutiAirspawnIfQueueFull;
                if (bornplace.zutiAirspawnOnly || !World.cur().diffCur.Takeoff_N_Landing || !bornplace.zutiIsStandAloneBornPlace && actorspawnarg.stayPlace >= World.cur().airdrome.stayHold.length && !netaircraft.FM.brakeShoe || carrierAirStart) {
                    // TODO: Set spawn point location in the air to home base location coordinates,
                    // IF it is placed on the carrier
                    if (carrierAirStart) {
                        actorspawnarg.point.x = bornplace.place.x;
                        actorspawnarg.point.y = bornplace.place.y;
                    }
                    // TODO: Edited by |ZUTI|
                    actorspawnarg.point.z = bornplace.zutiSpawnHeight;
                    // Need to divide this with 3 to get aprox speed at kmh
                    actorspawnarg.speed.x = bornplace.zutiSpawnSpeed / 3.6D;
                    actorspawnarg.orient = new Orient(bornplace.zutiSpawnOrient - 90F, 0F, 0F);
                    actorspawnarg.orient.transform(actorspawnarg.speed);

                    airStarting = true;
                } else {
                    netaircraft.FM.CT.setLanded();
                    Aircraft.forceGear(netaircraft.getClass(), netaircraft.hierMesh(), netaircraft.FM.CT.getGear());
                    AirportCarrier airportcarrier1 = (AirportCarrier) Airport.nearest(loc.getPoint(), -1, 4);
                    // TODO: Changed by SAS~Storebror, make planes spawn with wings folded if they are on a carrier!
//                    if (airportcarrier1 != null && airportcarrier1.ship() != null && (airportcarrier1.ship() instanceof RwyTransp || airportcarrier1.ship() instanceof RwyTranspWide || airportcarrier1.ship() instanceof RwyTranspSqr)
//                            && Engine.land().isWater(actorspawnarg.point.x, actorspawnarg.point.y))
//                        netaircraft.FM.brakeShoe = false;
                    if (airportcarrier1 != null && airportcarrier1.ship() != null && Engine.land().isWater(actorspawnarg.point.x, actorspawnarg.point.y)) {
                        if (airportcarrier1.ship() instanceof RwyTransp || airportcarrier1.ship() instanceof RwyTranspWide || airportcarrier1.ship() instanceof RwyTranspSqr) {
                            netaircraft.FM.brakeShoe = false;
                        } else if (netaircraft.isNetMaster()){
//                            System.out.println("### TEST 4 ###");
                            if (netaircraft.FM.CT.bHasWingControl) {
                                netaircraft.FM.CT.forceWing(1F); // TODO: Added by SAS~Storebror, forcibly fold wings on carrier ground Spawn!
                                netaircraft.FM.CT.wingControl = 1F;
                            }
                        }
                    }
                }
                netaircraft.FM.AS.bIsEnableToBailout = bornplace.bParachute;
            } else if (Mission.isDogfight() && World.cur().diffCur.Takeoff_N_Landing && Main.cur().netServerParams.isMaster()) {
                Loc loc = new Loc(actorspawnarg.point.x, actorspawnarg.point.y, 0.0D, 0.0F, 0.0F, 0.0F);
                AirportCarrier airportcarrier = (AirportCarrier) Airport.nearest(loc.getPoint(), -1, 4);
                if (airportcarrier != null && !NetAircraft.isOnCarrierDeck(airportcarrier, loc)) airportcarrier = null;
                if (airportcarrier != null) {
                    airportcarrier.setCellUsed((Aircraft) netaircraft);
                }
            }
            actorspawnarg.set(netaircraft);

            // TODO: Added by |ZUTI|
            netwing.setPlane(netaircraft, i);

            if (airStarting) {
                netaircraft.FM.EI.setCurControlAll(true);
                netaircraft.FM.EI.setEngineRunning();
                netaircraft.FM.CT.setPowerControl(0.75F);
                netaircraft.FM.setStationedOnGround(false);
                netaircraft.FM.setWasAirborne(true);
//                System.out.println("### TEST 1 ###");
                if (netaircraft.isNetMaster() && netaircraft.FM.CT.bHasCockpitDoorControl) { // TODO: Added by SAS~Storebror, forcibly close canopy on Spawn to avoid missing canopies on net clients / track playback!
//                    System.out.println("" + netaircraft.getClass().getName() + " airStarting=true, z=" + netaircraft.FM.Loc.z + ", Vwld.length()=" + netaircraft.FM.Vwld.length());
                    netaircraft.FM.CT.cockpitDoorControl = 0F;
                    netaircraft.FM.CT.forceCockpitDoor(0F);
                }
            } else {
//                System.out.println("" + netaircraft.getClass().getName() + " airStarting=false, netaircraft.isNetMaster()=" + netaircraft.isNetMaster() + ", Vwld.length()=" + netaircraft.FM.Vwld.length());
                if (netaircraft.isNetMaster() && CommonTools.equals(netaircraft.FM.Vwld.length(), 0D)) {
                    if (netaircraft instanceof Aircraft) {
                        Vector3d netaircraftSpeed = new Vector3d();
                        netaircraft.getSpeed(netaircraftSpeed);
                        ((Aircraft)netaircraft).setOnGround(netaircraft.pos.getAbsPoint(), netaircraft.pos.getAbsOrient(), netaircraftSpeed);
                    }
    //                System.out.println("### TEST 2 ###");
                    if (netaircraft.FM.CT.bHasCockpitDoorControl) { // TODO: Added by SAS~Storebror, forcibly open canopy on ground Spawn!
                        netaircraft.FM.CT.cockpitDoorControl = 1F;
                        netaircraft.FM.CT.forceCockpitDoor(1F);
                    }
                }
            }
            if (actorspawnarg.speed == null) netaircraft.setSpeed(new Vector3d());
            if (this._netUser != null) {
                this._netUser.tryPrepareSkin(netaircraft);
                this._netUser.tryPrepareNoseart(netaircraft);
                this._netUser.tryPreparePilot(netaircraft);
                this._netUser.setArmy(netaircraft.getArmy());
            } else if (Config.isUSE_RENDER()) Mission.cur().prepareSkinAI((Aircraft) netaircraft);
            if (netaircraft.net.isMaster() || this._netUser != null && this._netUser.isTrackWriter()) World.setPlayerRegiment();

            // TODO: Edit by |ZUTI|
            if (netaircraft != null) {
                // If we are not spawning on carrier, align us with ground
                if (netaircraft.FM.brakeShoeLastCarrier == null && !airStarting && bornplace != null && bornplace.zutiIsStandAloneBornPlace) ZutiSupportMethods_Air.alignAircraftToLandscape(netaircraft);
                // System.out.println("NetAircraft aligned to terrain.");
                // Report user resources that he can have for his AC
                if (Mission.isServer()) ZutiSupportMethods_NetSend.reportSpawnResources((Aircraft) netaircraft);

                // Disable chocks if air starting disables hanged up ac in the sky in some
                // cases!
                if (airStarting) World.getPlayerFM().brakeShoe = false;

                // Set up deck related limitations. Only for carrier based home bases.
                if (bornplace != null && bornplace.zutiAlreadyAssigned) {
                    ZutiSupportMethods_FM.UPDATE_DECK_TIMER = bornplace.zutiEnableQueue;
                    ZutiSupportMethods_FM.DECK_CLEAR_TIME = bornplace.zutiDeckClearTimeout * 1000;
                    ZutiSupportMethods_FM.DECK_LAST_REFRESH = Time.current();
                    ZutiSupportMethods_FM.UPDATE_VULNERABILITY_TIMER = bornplace.zutiPilotInVulnerableWhileOnTheDeck;

                    if (ZutiSupportMethods_FM.UPDATE_VULNERABILITY_TIMER && World.cur().diffCur.Vulnerability) {
                        ZutiSupportMethods_FM.VULNERABILITY_LAST_REFRESH = Time.current();
                        ZutiSupportMethods_FM.IS_PLAYER_VULNERABLE = false;
                        // System.out.println("Player is invulnerable!!!!");
                    }
                } else {
                    ZutiSupportMethods_FM.UPDATE_VULNERABILITY_TIMER = false;
                    ZutiSupportMethods_FM.IS_PLAYER_VULNERABLE = true;
                }
            }
            return netaircraft;
        }

        private void makeName(NetAircraft netaircraft) {
            String string = ((AircraftNet) netaircraft.net).netUser.uniqueName();
            int i;
            for (i = 0; Actor.getByName(string + "_" + i) != null; i++) {
            }
            netaircraft.setName(string + "_" + i);
        }

        private void _netSpawn(int i, NetMsgInput netmsginput) {
            try {
                ActorSpawnArg actorspawnarg = new ActorSpawnArg();
                actorspawnarg.army = netmsginput.readByte();
                actorspawnarg.armyExist = true;
                actorspawnarg.fuel = netmsginput.readFloat();
                actorspawnarg.bNumberOn = netmsginput.readBoolean();
                actorspawnarg.name = netmsginput.read255();
                actorspawnarg.weapons = netmsginput.read255();
                this._netUser = (NetUser) netmsginput.readNetObj();
                actorspawnarg.netChannel = netmsginput.channel();
                actorspawnarg.netIdRemote = i;
                netSpawnCommon(netmsginput, actorspawnarg);
                NetAircraft netaircraft = (NetAircraft) this._actorSpawn(actorspawnarg);
                if (netaircraft != null) netSpawnCommon(netmsginput, actorspawnarg, netaircraft);
            } catch (Exception exception) {
                printDebug(exception);
            }
        }

        public Actor actorSpawn(ActorSpawnArg actorspawnarg) {
            if (!Mission.isNet()) return null;
            if (Main.cur().netServerParams == null) return null;
            if (actorspawnarg.netChannel == null && actorspawnarg.bPlayer) this._netUser = (NetUser) NetEnv.host();
            Actor actor = null;
            if (Main.cur().netServerParams.isDogfight()) actor = this._actorSpawn(actorspawnarg);
            else if (Main.cur().netServerParams.isCoop()) actor = this.actorSpawnCoop(actorspawnarg);
            this._netUser = null;
            if (actor != null && actor == World.getPlayerAircraft() && NetMissionTrack.isRecording() && Main.cur().netServerParams.isDogfight()) try {
                NetMsgSpawn netmsgspawn = actor.netReplicate(NetMissionTrack.netChannelOut());
                actor.net.postTo(NetMissionTrack.netChannelOut(), netmsgspawn);
            } catch (Exception exception) {
                printDebug(exception);
            }
            return actor;
        }

        public void netSpawn(int i, NetMsgInput netmsginput) {
            if (Main.cur().netServerParams != null) {
                if (netmsginput.channel() instanceof NetChannelInStream && NetMissionTrack.playingVersion() == 100) {
                    if (Main.cur().netServerParams.isCoop()) this.netSpawnCoop(i, netmsginput);
                    else this._netSpawn(i, netmsginput);
                } else try {
                    byte i_8_ = netmsginput.readByte();
                    if ((i_8_ & 0x1) == 1) this.netSpawnCoop(i, netmsginput);
                    else this._netSpawn(i, netmsginput);
                } catch (Exception exception) {
                    printDebug(exception);
                    return;
                }
                this._netUser = null;
            }
        }
    }

    public class Mirror extends AircraftNet {
        NetMsgFiltered   out          = new NetMsgFiltered();
        private long     tupdate      = -1L;
        private boolean  ghostDetected= false;
        private long     _t;
        private long     tcur;
        private Point3f  _p           = new Point3f();
        private Vector3f _v           = new Vector3f();
        private Orient   _o           = new Orient();
        private Vector3f _w           = new Vector3f();
        private Vector3f TmpV         = new Vector3f();
        private Vector3d TmpVd        = new Vector3d();
        private float    save_dt      = 0.0010F;
        private float    saveCoeff    = 1.0F;
        private boolean  bGround      = false;
        private boolean  bUnderDeck   = false;
        private long     tint;
        private long     tlag;
        private boolean  bFirstUpdate = true;
        private Loc      _lRel        = new Loc();
        // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View
        // Replication
        private int      oldRudd      = -99999;
        private int      oldAil       = -99999;
        private int      oldElev      = -99999;

        // ---

        public void makeFirstUnderDeck() {
            if (NetAircraft.this.FM.brakeShoe) {
                NetAircraft.corn.set(NetAircraft.this.pos.getAbsPoint());
                NetAircraft.corn1.set(NetAircraft.this.pos.getAbsPoint());
                NetAircraft.corn1.z -= 20.0;
                Actor actor = Engine.collideEnv().getLine(NetAircraft.corn, NetAircraft.corn1, false, NetAircraft.clipFilter, NetAircraft.pship);
                if (!(actor instanceof BigshipGeneric) && Mission.isCoop() && Time.current() < 60000L) actor = NetAircraft.this.FM.brakeShoeLastCarrier;
                if (actor instanceof BigshipGeneric) {
                    this.bUnderDeck = true;
                    this._lRel.set(NetAircraft.this.pos.getAbs());
                    this._lRel.sub(actor.pos.getAbs());
                }
            }
        }

        public void netFirstUpdate(float f, float f_9_, float f_10_, float f_11_, float f_12_, float f_13_, float f_14_, float f_15_, float f_16_) {
            NetAircraft.this.FM.Vwld.set(f_14_, f_15_, f_16_);
            NetAircraft.this.FM.getAccel().set(0.0, 0.0, 0.0);
            this._t = this.tcur = this.tupdate = Time.current();
            this._p.set(f, f_9_, f_10_);
            this._v.set(NetAircraft.this.FM.Vwld);
            this._o.set(f_11_, f_12_, f_13_);
            this._w.set(0.0F, 0.0F, 0.0F);
            NetAircraft.this.FM.Loc.set(f, f_9_, f_10_);
            NetAircraft.this.FM.Or.set(f_11_, f_12_, f_13_);
            this.tint = this.tcur;
            this.tlag = 0L;
        }

        public boolean netInput(NetMsgInput netmsginput) throws IOException {
            if (netmsginput.isGuaranted()) return NetAircraft.this.netGetGMsg(netmsginput, false);
            if (Time.isPaused() && !NetMissionTrack.isPlaying()) return true;
            if (netmsginput.channel() != this.masterChannel()) {
                this.postRealTo(Message.currentTime(true), this.masterChannel(), new NetMsgFiltered(netmsginput, 0));
                return true;
            }
            if (this.isMirrored()) {
                this.out.unLockAndSet(netmsginput, 0);
                this.postReal(Message.currentTime(true), this.out);
            }
//            System.out.println("netInput(" + netmsginput.available() + "), NetAircraft=" + this.hashCode() + " (" + this.netName + "), Tick: " + Time.tick());
            int i = netmsginput.readByte();
            int i_17_ = netmsginput.readUnsignedByte();
            this.bGround = (i & 0x20) != 0;
            this.bUnderDeck = (i & 0x40) != 0;
            if (NetAircraft.this.isFMTrackMirror()) {
                // TODO: Storebror: +++ Bomb Release Bug hunting
                if (NetAircraft.this.iFMTrackMirror != 1) {
                    NetAircraft.this.iFMTrackMirror = 1;
                    printDebugMessage(NetAircraft.this, "isFMTrackMirror() = true");
                }
                // TODO: Storebror: --- Bomb Release Bug hunting
                netmsginput.readUnsignedByte();
                netmsginput.readUnsignedByte();
            } else {
                // TODO: Storebror: +++ Bomb Release Bug hunting
                if (NetAircraft.this.iFMTrackMirror != 0) {
                    NetAircraft.this.iFMTrackMirror = 0;
                    printDebugMessage(NetAircraft.this, "isFMTrackMirror() = false");
                }
                // TODO: Storebror: --- Bomb Release Bug hunting
                this.netControls(netmsginput.readUnsignedByte());
                this.netWeaponControl(netmsginput.readUnsignedByte());
                if (this.bFirstUpdate) {
                    Aircraft aircraft = (Aircraft) this.superObj();
                    aircraft.FM.CT.forceGear(aircraft.FM.CT.GearControl);
                    Aircraft.forceGear(aircraft.getClass(), aircraft.hierMesh(), aircraft.FM.CT.getGear());
                }
            }
            float f = netmsginput.readFloat();
            float f_18_ = netmsginput.readFloat();
            float f_19_ = netmsginput.readFloat();
            short i_20_ = netmsginput.readShort();
            short i_21_ = netmsginput.readShort();
            short i_22_ = netmsginput.readShort();
            float f_23_ = -(i_20_ * 180.0F / 32000.0F);
            float f_24_ = i_21_ * 90.0F / 32000.0F;
            float f_25_ = i_22_ * 180.0F / 32000.0F;
            short i_26_ = netmsginput.readShort();
            short i_27_ = netmsginput.readShort();
            short i_28_ = netmsginput.readShort();
            float f_29_ = i_26_ * 50.0F / 32000.0F;
            float f_30_ = i_27_ * 50.0F / 32000.0F;
            float f_31_ = i_28_ * 50.0F / 32000.0F;
            if (this.bUnderDeck) f_29_ = f_30_ = f_31_ = 0.0F;
            short i_32_ = netmsginput.readShort();
            short i_33_ = netmsginput.readShort();
            short i_34_ = netmsginput.readShort();
            float f_35_ = i_32_ * 400.0F / 32000.0F;
            float f_36_ = i_33_ * 400.0F / 32000.0F;
            float f_37_ = i_34_ * 400.0F / 32000.0F;
            if (this.bGround && !this.bUnderDeck) f_37_ = 0.0F;
            short i_38_ = netmsginput.readShort();
            short i_39_ = netmsginput.readShort();
            short i_40_ = netmsginput.readShort();
            float f_41_ = i_38_ * 2000.0F / 32000.0F;
            float f_42_ = i_39_ * 2000.0F / 32000.0F;
            float f_43_ = i_40_ * 2000.0F / 32000.0F;
            if (this.bGround || this.bUnderDeck) {
                f_41_ = 0.0F;
                f_42_ = 0.0F;
                f_43_ = 0.0F;
            }
            if (this.bUnderDeck) this._lRel.set(i_38_ * 200.0 / 32000.0, i_39_ * 200.0 / 32000.0, i_40_ * 200.0 / 32000.0, -(i_26_ * 180.0F / 32000.0F), i_27_ * 90.0F / 32000.0F, i_28_ * 180.0F / 32000.0F);
            long l = Message.currentTime(false) + i_17_;
            this._t = l;
            if (NetEnv.testLag) {
                long l_44_ = Time.tickNext() - l;
                if (l_44_ < 0L) l_44_ = 0L;
                if (this.bFirstUpdate || this.tlag >= l_44_) {
                    this.bFirstUpdate = false;
                    this.tlag = l_44_;
                } else if (l > this.tupdate) {
                    double d = (double) (l_44_ - (this.tcur - this.tint)) / (double) (l - this.tupdate);
                    if (d > 0.015) d = 0.015;
                    long l_45_ = (long) ((l - this.tupdate) * d);
                    if (l_45_ > Time.tickConstLen() / 2) l_45_ = Time.tickConstLen() / 2;
                    this.tlag = this.tcur - this.tint + l_45_;
                    if (this.tlag >= l_44_) this.tlag = l_44_;
                }
            } else this.bFirstUpdate = false;
            this.tupdate = this._t;
            NetAircraft.this.FM.Vwld.set(f_35_, f_36_, f_37_);
            NetAircraft.this.FM.getAccel().set(f_41_, f_42_, f_43_);
            this._p.set(f, f_18_, f_19_);
            this._v.set(NetAircraft.this.FM.Vwld);
            this._o.set(f_23_, f_24_, f_25_);
            this._o.transformInv(NetAircraft.this.FM.Vwld, NetAircraft.this.FM.getVflow());
            this._w.set(f_29_, f_30_, f_31_);
            NetAircraft.this.FM.getW().set(f_29_, f_30_, f_31_);
            int i_46_ = i & 0xf;
            if (i_46_ == 1) {
                float f_47_ = netmsginput.readUnsignedByte() / 255.0F * 640.0F;
                // +++ TODO: Storebror: Replicate high RPMs for Jet Aircraft
                if (NetAircraft.this.FM.EI.engines[0].wMaxAllowed > 600F)
                    f_47_ *= 10F;
                // ---
                float f_48_ = netmsginput.readUnsignedByte() / 255.0F * 1.6F;
                i_46_ = NetAircraft.this.FM.EI.getNum();
                for (int i_49_ = 0; i_49_ < i_46_; i_49_++)
                    if (!NetAircraft.this.isFMTrackMirror()) {
                        NetAircraft.this.FM.EI.engines[i_49_].setw(f_47_);
                        NetAircraft.this.FM.EI.engines[i_49_].setPropPhi(f_48_);
                    }
            } else for (int i_50_ = 0; i_50_ < i_46_; i_50_++) {
                float f_51_ = netmsginput.readUnsignedByte() / 255.0F * 640.0F;
                // +++ TODO: Storebror: Replicate high RPMs for Jet Aircraft
                if (NetAircraft.this.FM.EI.engines[0].wMaxAllowed > 600F)
                    f_51_ *= 10F;
                // ---
                float f_52_ = netmsginput.readUnsignedByte() / 255.0F * 1.6F;
                if (!NetAircraft.this.isFMTrackMirror()) {
                    NetAircraft.this.FM.EI.engines[i_50_].setw(f_51_);
                    NetAircraft.this.FM.EI.engines[i_50_].setPropPhi(f_52_);
                }
            }
            if ((i & 0x10) != 0 && NetAircraft.this.netCockpitTuretNum >= 0) {
                int i_53_ = netmsginput.readUnsignedShort();
                int i_54_ = netmsginput.readUnsignedShort();
                float f_55_ = this.unpackSY(i_53_);
                float f_56_ = this.unpackSP(i_54_ & 0x7fff);
                NetAircraft.this.FM.CT.WeaponControl[NetAircraft.this.netCockpitWeaponControlNum] = (i_54_ & 0x8000) != 0;
                if (this.superObj() == World.getPlayerAircraft()) {
                    Actor._tmpOrient.set(f_55_, f_56_, 0.0F);
                    ((CockpitGunner) Main3D.cur3D().cockpits[NetAircraft.this.netCockpitIndxPilot]).moveGun(Actor._tmpOrient);
                } else {
                    Turret turret = NetAircraft.this.FM.turret[NetAircraft.this.netCockpitTuretNum];
                    turret.tu[0] = f_55_;
                    turret.tu[1] = f_56_;
                }
            }

            // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View
            // Replication
            if (netmsginput.available() == 0) return true;
            int i11 = netmsginput.readUnsignedShort();
            i11 = i11 << 8 | netmsginput.readUnsignedByte();

            NetAircraft.this.viewTangage = this.unpackScale(i11 % 26, -23.5F, 3.153856F);
            i11 /= 26;
            NetAircraft.this.viewAzimut = this.unpackScale(i11 % 69, -108.5F, 3.144938F);
            i11 /= 69;
            float f18;
            if (this.oldRudd != i11 % 21) {
                f18 = this.unpackScale(i11 % 21, -1.05F, 0.10001F);
                ((Aircraft) NetAircraft.this).moveRudder(f18);
//                Reflection.setFloat(NetAircraft.this.FM.CT, "Rudder", f18);
                NetAircraft.this.FM.CT.Rudder = f18;
                NetAircraft.this.FM.CT.RudderControl = f18;
                this.oldRudd = i11 % 21;
            }
            i11 /= 21;

            if (this.oldElev != i11 % 21) {
                f18 = this.unpackScale(i11 % 21, -1.05F, 0.10001F);
                ((Aircraft) NetAircraft.this).moveElevator(f18);
//                Reflection.setFloat(NetAircraft.this.FM.CT, "Elevators", f18);
                NetAircraft.this.FM.CT.Elevators = f18;
                NetAircraft.this.FM.CT.ElevatorControl = f18;
                this.oldElev = i11 % 21;
            }
            i11 /= 21;

            if (this.oldAil != i11 % 21) {
                f18 = this.unpackScale(i11 % 21, -1.05F, 0.10001F);
                ((Aircraft) NetAircraft.this).moveAileron(f18);
//                Reflection.setFloat(NetAircraft.this.FM.CT, "Ailerons", f18);
                NetAircraft.this.FM.CT.Ailerons = f18;
                NetAircraft.this.FM.CT.AileronControl = f18;
                this.oldAil = i11 % 21;
            }
            // ---

//			// TODO: Storebror: Implement Guided Missile Lock State Replication
//			if (netmsginput.available() == 0) return true;
//			int i12 = netmsginput.readUnsignedByte();
//			if (this instanceof TypeGuidedMissileCarrier) ((TypeGuidedMissileCarrier)this).getGuidedMissileUtils().setMissileLockState(i12);
            return true;
        }

        float unpackSY(int i) {
            return (float) (i * 360.0 / 65000.0 - 180.0);
        }

        float unpackSP(int i) {
            return (float) (i * 360.0 / 32000.0 - 180.0);
        }

        // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View
        // Replication
        float unpackScale(float f1, float f2, float f3) {
            return (f1 + 0.5F) * f3 + f2;
        }

        // ---

        public void fmUpdate(float f) {
//            System.out.println("fmUpdate(" + f + "), NetAircraft=" + this.hashCode() + " (" + this.netName + "), Tick: " + Time.tick());
            if (this.tupdate < 0L) this.netFirstUpdate((float) NetAircraft.this.FM.Loc.x, (float) NetAircraft.this.FM.Loc.y, (float) NetAircraft.this.FM.Loc.z, NetAircraft.this.FM.Or.getAzimut(), NetAircraft.this.FM.Or.getTangage(),
                    NetAircraft.this.FM.Or.getKren(), (float) NetAircraft.this.FM.Vwld.x, (float) NetAircraft.this.FM.Vwld.y, (float) NetAircraft.this.FM.Vwld.z);
            f = (Time.tickNext() - this.tcur) * 0.0010F;
            if (!(f < 0.0010F)) {
                this.tcur = Time.tickNext();
                if (!this.ghostDetected && !(Main.cur() instanceof DServer) && !NetMissionTrack.isPlaying() && this.tupdate > 0L && this.tcur > this.tupdate && this.tcur - this.tupdate > 10000L) {
//                    this.tupdate = tcur;
                    this.ghostDetected = true;
                    if (Config.cur.ini.get("Mods", "destroyGhostPlanes", 1) != 0) {
                        System.out.println("Destroying Ghost Plane: " + this.hashCode() + " (" + this.netName + "), Tick Diff to last Update: " + (this.tcur - this.tupdate));
//                        System.out.println("tupdate=" + tupdate + ", tcur=" + tcur);
                        try {
                            NetAircraft.this.destroy();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    } else {
                        System.out.println("Possible Ghost Plane: " + this.hashCode() + " (" + this.netName + "), Tick Diff to last Update: " + (this.tcur - this.tupdate) + " - not destroyed because of conf.ini destroyGhostPlanes=0");
                    }
                    this.tupdate = tcur;
                }
                NetAircraft.this.FM.CT.update(f, 50.0F, NetAircraft.this.FM.EI, false, NetAircraft.this.isFMTrackMirror());
                NetAircraft.this.FM.Gears.ground(NetAircraft.this.FM, false, this.bGround);
                NetAircraft.this.FM.Gears.bFlatTopGearCheck = false;
                for (int i = 0; i < 3; i++) {
                    NetAircraft.this.FM.Gears.gWheelAngles[i] = (NetAircraft.this.FM.Gears.gWheelAngles[i] + (float) Math.toDegrees(Math.atan(NetAircraft.this.FM.Gears.gVelocity[i] * f / 0.375))) % 360.0F;
                    NetAircraft.this.FM.Gears.gVelocity[i] *= 0.95;
                }
                NetAircraft.this.hierMesh().chunkSetAngles("GearL1_D0", 0.0F, -NetAircraft.this.FM.Gears.gWheelAngles[0], 0.0F);
                NetAircraft.this.hierMesh().chunkSetAngles("GearR1_D0", 0.0F, -NetAircraft.this.FM.Gears.gWheelAngles[1], 0.0F);
                NetAircraft.this.hierMesh().chunkSetAngles("GearC1_D0", 0.0F, -NetAircraft.this.FM.Gears.gWheelAngles[2], 0.0F);
                float f_57_ = NetAircraft.this.FM.Gears.getSteeringAngle();
                NetAircraft.this.moveSteering(f_57_);
                if (NetAircraft.this.FM.Gears.nearGround()) NetAircraft.this.moveWheelSink();
                NetAircraft.this.FM.EI.netupdate(f, NetAircraft.this.isFMTrackMirror());
                NetAircraft.this.FM.FMupdate(f);
                long l;
                for (this.tint = this.tcur - this.tlag; this.tint > this._t; this._t += l) {
                    l = this.tint - this._t;
                    if (l > Time.tickConstLen()) l = Time.tickConstLen();
                    float f_58_ = l * 0.0010F;
                    this._p.x += this._v.x * f_58_;
                    this._p.y += this._v.y * f_58_;
                    this._p.z += this._v.z * f_58_;
                    this._v.x += NetAircraft.this.FM.getAccel().x * f_58_;
                    this._v.y += NetAircraft.this.FM.getAccel().y * f_58_;
                    this._v.z += NetAircraft.this.FM.getAccel().z * f_58_;
                    this.TmpV.scale(f_58_, this._w);
                    this._o.increment((float) -Math.toDegrees(this.TmpV.z), (float) -Math.toDegrees(this.TmpV.y), (float) Math.toDegrees(this.TmpV.x));
                }
                World.land();
                float f_59_ = Landscape.HQ(this._p.x, this._p.y);
                if (World.land().isWater(this._p.x, this._p.y)) {
                    if (this._p.z < f_59_ - 20.0F) this._p.z = f_59_ - 20.0F;
                } else if (this._p.z < f_59_ + 1.0F) this._p.z = f_59_ + 1.0F;
                this.TmpVd.set(this._p);
                this.save_dt = 0.98F * this.save_dt + 0.02F * ((this.tint - this.tupdate) * 0.0010F);
                f_57_ = 0.03F;
                if (this._v.length() > 0.0F) {
                    f_57_ = 1.08F - this.save_dt * 2.0F;
                    if (f_57_ > 1.0F) f_57_ = 1.0F;
                    if (f_57_ < 0.03F) f_57_ = 0.03F;
                }
                this.saveCoeff = 0.98F * this.saveCoeff + 0.02F * f_57_;
                NetAircraft.this.FM.Loc.interpolate(this.TmpVd, this.saveCoeff);
                float f_60_ = this.saveCoeff * 2.0F;
                if (NetMissionTrack.isPlaying()) f_60_ = this.saveCoeff / 4.0F;
                if (f_60_ > 1.0F) f_60_ = 1.0F;
                NetAircraft.this.FM.Or.interpolate(this._o, f_60_);
                if (this.bUnderDeck) {
                    NetAircraft.corn.set(NetAircraft.this.FM.Loc);
                    NetAircraft.corn1.set(NetAircraft.this.FM.Loc);
                    NetAircraft.corn1.z -= 20.0;
                    Actor actor = Engine.collideEnv().getLine(NetAircraft.corn, NetAircraft.corn1, false, NetAircraft.clipFilter, NetAircraft.pship);
                    if (!(actor instanceof BigshipGeneric) && Mission.isCoop() && Time.current() < 60000L) actor = NetAircraft.this.FM.brakeShoeLastCarrier;
                    if (actor instanceof BigshipGeneric) {
                        NetAircraft.lCorn.set(this._lRel);
                        NetAircraft.lCorn.add(actor.pos.getAbs());
                        NetAircraft.this.FM.Loc.set(NetAircraft.lCorn.getPoint());
                        NetAircraft.this.FM.Or.set(NetAircraft.lCorn.getOrient());
                        this.saveCoeff = 1.0F;
                        this._p.set(NetAircraft.this.FM.Loc);
                        this._o.set(NetAircraft.this.FM.Or);
                        actor.getSpeed(NetAircraft.this.FM.Vwld);
                        this._v.x = (float) NetAircraft.this.FM.Vwld.x;
                        this._v.y = (float) NetAircraft.this.FM.Vwld.y;
                        this._v.z = (float) NetAircraft.this.FM.Vwld.z;
                    }
                }
                if (NetAircraft.this.isFMTrackMirror()) NetAircraft.this.fmTrack.FMupdate(NetAircraft.this.FM);
                if (NetAircraft.this.FM.isTick(44, 0)) {
                    NetAircraft.this.FM.AS.update(f * 44.0F);
                    ((Aircraft) this.superObj()).rareAction(f * 44.0F, false);
                    if (NetAircraft.this.FM.Loc.z - Engine.land().HQ_Air(NetAircraft.this.FM.Loc.x, NetAircraft.this.FM.Loc.y) > 40.0) {
                        NetAircraft.this.FM.setWasAirborne(true);
                        NetAircraft.this.FM.setStationedOnGround(false);
                    } else if (NetAircraft.this.FM.Vwld.length() < 1.0) NetAircraft.this.FM.setStationedOnGround(true);
                }
                // TODO: Storebror: +++ Bomb Release Bug hunting
                NetAircraft.this.decUpdatePending(UPDATE_MIRROR);
                // TODO: Storebror: --- Bomb Release Bug hunting
            }
//            // TODO: Storebror: +++ Bomb Release Bug hunting
//            else {
//                NetAircraft.printDebugMessage(NetAircraft.this, "MIRROR: (Time.tickNext() - this.tcur) * 0.0010F < 0.0010F !!");
//            }
//            // TODO: Storebror: --- Bomb Release Bug hunting
        }

        public void netControls(int i) {
            NetAircraft.this.FM.CT.GearControl = (i & 0x1) != 0 ? 1.0F : 0.0F;
            NetAircraft.this.FM.CT.FlapsControl = (i & 0x2) != 0 ? 1.0F : 0.0F;
            NetAircraft.this.FM.CT.BrakeControl = (i & 0x4) != 0 ? 1.0F : 0.0F;
            NetAircraft.this.FM.CT.setRadiatorControl((i & 0x8) != 0 ? 1.0F : 0.0F);
            NetAircraft.this.FM.CT.BayDoorControl = (i & 0x10) != 0 ? 1.0F : 0.0F;
            NetAircraft.this.FM.CT.AirBrakeControl = (i & 0x20) != 0 ? 1.0F : 0.0F;
        }

        public void netWeaponControl(int netWeaponBits) {
            int wCTLlength = NetAircraft.this.FM.CT.WeaponControl.length;
            int wCTLindex = 0;
            for (int bitMask = 1; wCTLindex < wCTLlength && bitMask < 256; bitMask <<= 1) {
                NetAircraft.this.FM.CT.WeaponControl[wCTLindex] = (netWeaponBits & bitMask) != 0;
                // TODO: Storebror: +++ Bomb Release Bug hunting

                if (wCTLindex == 2 || wCTLindex == 3) if (NetAircraft.this.FM.CT.WeaponControl[wCTLindex] && NetAircraft.this.hasBulletsLeftOnTrigger(wCTLindex)) {
                    printDebugMessage(NetAircraft.this, "MIRROR: " + NetAircraft.TRIGGER_NAMES[wCTLindex - 2] + " Trigger pressed!");
                    NetAircraft.this.incUpdatePending(UPDATE_MIRROR);
                    NetAircraft.this.setTriggerActive(TRIGGER_MIRROR, wCTLindex - 2, true);
                } else if (NetAircraft.this.isTriggerActive(TRIGGER_MIRROR, wCTLindex - 2)) {
                    printDebugMessage(NetAircraft.this, "MIRROR: " + NetAircraft.TRIGGER_NAMES[wCTLindex - 2] + " Trigger released!");
                    if (NetAircraft.this.isUpdatePending(UPDATE_MIRROR) && NetAircraft.this.hasBulletsLeftOnTrigger(wCTLindex)) {
                        NetAircraft.this.FM.CT.WeaponControl[wCTLindex] = true;
                        printDebugMessage(NetAircraft.this, "MIRROR: " + NetAircraft.TRIGGER_NAMES[wCTLindex - 2] + " Trigger restored to \"pressed\" because fmUpdate was pending!");
                    } else NetAircraft.this.setTriggerActive(TRIGGER_MIRROR, wCTLindex - 2, false);
                }

                // TODO: Storebror: --- Bomb Release Bug hunting
                wCTLindex++;
            }
        }

        public Mirror(Actor actor, NetChannel netchannel, int i) {
            super(actor, netchannel, i);
            try {
                this.out.setIncludeTime(true);
                this.out.setFilterArg(actor);
            } catch (Exception exception) {}
        }
    }

    static class ClipFilter implements ActorFilter {
        public boolean isUse(Actor actor, double d) {
            return actor instanceof BigshipGeneric;
        }
    }

    class Master extends AircraftNet implements NetUpdate {
        NetMsgFiltered   out             = new NetMsgFiltered();

        public byte[]    weaponsBitStates;

        public boolean   weaponsIsEmpty  = false;

        public boolean   weaponsCheck    = false;

        public long      weaponsSyncTime = 0L;

        public int       curWayPoint     = 0;

        private Vector3f vec3f           = new Vector3f();

        private Point3d  p               = new Point3d();

        private Orient   o               = new Orient();

        private int      countUpdates    = 0;

        public boolean netInput(NetMsgInput netmsginput) throws IOException {
            if (netmsginput.isGuaranted()) return NetAircraft.this.netGetGMsg(netmsginput, true);
            if (!Config.isUSE_RENDER()) return true;
            int i = netmsginput.readByte();
            int i_64_ = netmsginput.readByte();
            int i_65_ = (i & 0x1) << 1 | i_64_ & 0x1;
            i &= ~0x1;
            i_64_ &= ~0x1;
            NetAircraft.this.msgSndShot(i_65_ * 0.05F + 0.01F, i * 0.25, i_64_ * 0.25, 0.0);
            return true;
        }

        public void netUpdate() {
            // TODO: Storebror: +++ Bomb Release Bug hunting
//            if (Time.tickCounter() <= 2)
//                return;
            if (Time.tickCounter() <= 2) if (NetAircraft.this.isUpdatePending(UPDATE_MASTER))
                printDebugMessage(NetAircraft.this, "Master netUpdate() Time.tickCounter()=" + Time.tickCounter() + ", enforcing Update since number of Master Updates pending is " + NetAircraft.this.getUpdatePending(UPDATE_MASTER));
            else return;
            // TODO: Storebror: --- Bomb Release Bug hunting
            if (this.netUser == null && NetAircraft.this.FM.brakeShoe) {
                int i = NetAircraft.this.FM.actor.hashCode() & 0xf;
                if ((this.countUpdates++ & 0xf) != i) // TODO: Storebror: +++ Bomb Release Bug hunting
                    if (NetAircraft.this.isUpdatePending(UPDATE_MASTER)) printDebugMessage(NetAircraft.this, "Master netUpdate() NetAircraft.this.FM.actor.hashCode() & 0xf=" + (NetAircraft.this.FM.actor.hashCode() & 0xf) + ", this.countUpdates="
                            + this.countUpdates + ", enforcing Update since number of Master Updates pending is " + NetAircraft.this.getUpdatePending(UPDATE_MASTER));
                    else // TODO: Storebror: --- Bomb Release Bug hunting
                        return;
            } else this.countUpdates = 0;
            // TODO: Storebror: +++ Bomb Release Bug hunting
            if (this.weaponsIsEmpty) {
                if (NetAircraft.this.FM.CT.WCT != 0) if (!NetAircraft.this.wctResetLogged) {
                    printDebugMessage(NetAircraft.this, "Master netUpdate() !!! WCT RESET TO ZERO !!! - this will be the only time logging this issue, but it might happen multiple times hereafter.");
                    NetAircraft.this.wctResetLogged = true;
                }
                NetAircraft.this.FM.CT.WCT = (byte) 0;
            }
//            if (this.weaponsIsEmpty)
//                NetAircraft.this.FM.CT.WCT = (byte) 0;
            // TODO: Storebror: --- Bomb Release Bug hunting
            boolean bool = (NetAircraft.this.FM.CT.WCT & 0xf) != 0;
            try {
                this.out.unLockAndClear();
                int i = 0;
                boolean engineWChangedFromLastNetUpdate = false;
//                boolean engineWMatches = false;
                boolean engineWMatches = true; // TODO: Storebror, Multi Engine Aircraft Net Replication Bugfix backport
                                               // from 4.13
                int lastEngineW = 0;
                for (int engineIndex = 0; engineIndex < NetAircraft.this.FM.EI.getNum(); engineIndex++) {
                    int curEngineW = (int) (NetAircraft.this.FM.EI.engines[engineIndex].getw() / 640.0F * 255.0F);
                    // +++ TODO: Storebror: Replicate high RPMs for Jet Aircraft
                    if (NetAircraft.this.FM.EI.engines[engineIndex].wMaxAllowed > 600F)
                        curEngineW = (int) (NetAircraft.this.FM.EI.engines[engineIndex].getw() / 6400.0F * 255.0F);
                    // ---
                    if (engineIndex == 0) lastEngineW = curEngineW;
                    else if (lastEngineW != curEngineW) // engineWMatches = true;
                        engineWMatches = false; // TODO: Storebror, Multi Engine Aircraft Net Replication Bugfix
                    // backport from 4.13
                    if (curEngineW != NetAircraft.this.FM.EI.engines[engineIndex].wNetPrev) {
                        engineWChangedFromLastNetUpdate = true;
                        NetAircraft.this.FM.EI.engines[engineIndex].wNetPrev = curEngineW;
                    }
                }
                if (engineWChangedFromLastNetUpdate) if (engineWMatches) i = 1;
                else {
                    i = NetAircraft.this.FM.EI.getNum();
                    if (i > 15) i = 15;
                }
                if (NetAircraft.this.netCockpitValid && NetAircraft.this.netCockpitTuretNum >= 0) i |= 0x10;
                if (NetAircraft.this.FM.Gears.onGround()) i |= 0x20;
                if (NetAircraft.this.FM.Gears.isUnderDeck() && NetAircraft.this.FM.Vrel.lengthSquared() < 2.0) {
                    NetAircraft.corn.set(NetAircraft.this.FM.Loc);
                    NetAircraft.corn1.set(NetAircraft.this.FM.Loc);
                    NetAircraft.corn1.z -= 20.0;
                    Actor actor = Engine.collideEnv().getLine(NetAircraft.corn, NetAircraft.corn1, false, NetAircraft.clipFilter, NetAircraft.pship);
                    if (!(actor instanceof BigshipGeneric) && Mission.isCoop() && Time.current() < 60000L) actor = NetAircraft.this.FM.brakeShoeLastCarrier;
                    if (actor instanceof BigshipGeneric) {
                        NetAircraft.lCorn.set(NetAircraft.this.pos.getAbs());
                        NetAircraft.lCorn.sub(actor.pos.getAbs());
                        if (Math.abs(NetAircraft.lCorn.getX()) < 200.0 && Math.abs(NetAircraft.lCorn.getY()) < 200.0 && Math.abs(NetAircraft.lCorn.getZ()) < 200.0) i |= 0x40;
                    }
                }
                this.out.writeByte(i);
                int i_71_ = (int) (Time.tickNext() - Time.current());
                if (i_71_ > 255) i_71_ = 255;
                this.out.writeByte(i_71_);
                this.out.writeByte(NetAircraft.this.FM.CT.CTL);

                this.out.writeByte(NetAircraft.this.FM.CT.WCT);

                // TODO: Storebror: +++ Bomb Release Bug hunting
                for (int wctlIndex = 2; wctlIndex <= 3; wctlIndex++) {
                    boolean triggerPressed = (NetAircraft.this.FM.CT.WCT & 1 << wctlIndex) != 0;
                    if (triggerPressed) {
                        if (NetAircraft.this.hasBulletsLeftOnTrigger(wctlIndex)) {
                            printDebugMessage(NetAircraft.this, "Master netUpdate(): " + NetAircraft.TRIGGER_NAMES[wctlIndex - 2] + " Trigger pressed!");
                            NetAircraft.this.setTriggerActive(TRIGGER_MASTER, wctlIndex - 2, true);
                            NetAircraft.this.setLastBulletLogged(wctlIndex - 2, false);
                        } else if (!NetAircraft.this.isLastBulletLogged(wctlIndex - 2)) {
                            printDebugMessage(NetAircraft.this, "Master netUpdate(): " + NetAircraft.TRIGGER_NAMES[wctlIndex - 2] + " Trigger pressed!");
                            NetAircraft.this.setTriggerActive(TRIGGER_MASTER, wctlIndex - 2, true);
                            NetAircraft.this.setLastBulletLogged(wctlIndex - 2, true);
                        }
                    } else if (NetAircraft.this.isTriggerActive(TRIGGER_MASTER, wctlIndex - 2)) {
                        printDebugMessage(NetAircraft.this, "Master netUpdate(): " + NetAircraft.TRIGGER_NAMES[wctlIndex - 2] + " Trigger released!");
                        NetAircraft.this.setTriggerActive(TRIGGER_MASTER, wctlIndex - 2, false);
                    }
                }
                NetAircraft.this.decUpdatePending(UPDATE_MASTER);
                // TODO: Storebror: --- Bomb Release Bug hunting

                NetAircraft.this.FM.CT.WCT &= 0x3;
                NetAircraft.this.pos.getAbs(this.p, this.o);
                this.out.writeFloat((float) this.p.x);
                this.out.writeFloat((float) this.p.y);
                this.out.writeFloat((float) this.p.z);
                this.o.wrap();
                int i_72_ = (int) (this.o.getYaw() * 32000.0F / 180.0F);
                lastEngineW = (int) (this.o.tangage() * 32000.0F / 90.0F);
                int i_73_ = (int) (this.o.kren() * 32000.0F / 180.0F);
                this.out.writeShort(i_72_);
                this.out.writeShort(lastEngineW);
                this.out.writeShort(i_73_);
                if ((i & 0x40) == 0) {
                    this.vec3f.set(NetAircraft.this.FM.getW());
                    int i_74_ = (int) (this.vec3f.x * 32000.0F / 50.0F);
                    int i_75_ = (int) (this.vec3f.y * 32000.0F / 50.0F);
                    int i_76_ = (int) (this.vec3f.z * 32000.0F / 50.0F);
                    this.out.writeShort(i_74_);
                    this.out.writeShort(i_75_);
                    this.out.writeShort(i_76_);
                } else {
                    NetAircraft.lCorn.get(this.o);
                    this.o.wrap();
                    i_72_ = (int) (this.o.getYaw() * 32000.0F / 180.0F);
                    lastEngineW = (int) (this.o.tangage() * 32000.0F / 90.0F);
                    i_73_ = (int) (this.o.kren() * 32000.0F / 180.0F);
                    this.out.writeShort(i_72_);
                    this.out.writeShort(lastEngineW);
                    this.out.writeShort(i_73_);
                }
                this.vec3f.set(NetAircraft.this.FM.Vwld);
                int i_77_ = (int) (this.vec3f.x * 32000.0F / 400.0F);
                int i_78_ = (int) (this.vec3f.y * 32000.0F / 400.0F);
                int i_79_ = (int) (this.vec3f.z * 32000.0F / 400.0F);
                this.out.writeShort(i_77_);
                this.out.writeShort(i_78_);
                this.out.writeShort(i_79_);
                if ((i & 0x40) == 0) {
                    this.vec3f.set(NetAircraft.this.FM.getAccel());
                    int i_80_ = (int) (this.vec3f.x * 32000.0F / 2000.0F);
                    int i_81_ = (int) (this.vec3f.y * 32000.0F / 2000.0F);
                    int i_82_ = (int) (this.vec3f.z * 32000.0F / 2000.0F);
                    this.out.writeShort(i_80_);
                    this.out.writeShort(i_81_);
                    this.out.writeShort(i_82_);
                } else {
                    int i_83_ = (int) (NetAircraft.lCorn.getX() * 32000.0 / 200.0);
                    int i_84_ = (int) (NetAircraft.lCorn.getY() * 32000.0 / 200.0);
                    int i_85_ = (int) (NetAircraft.lCorn.getZ() * 32000.0 / 200.0);
                    this.out.writeShort(i_83_);
                    this.out.writeShort(i_84_);
                    this.out.writeShort(i_85_);
                }
                for (int i_86_ = 0; i_86_ < (i & 0xf); i_86_++) {
                    // +++ TODO: Storebror: Replicate high RPMs for Jet Aircraft
                    if (NetAircraft.this.FM.EI.engines[i_86_].wMaxAllowed > 600F)
                        this.out.writeByte((byte) (int) (NetAircraft.this.FM.EI.engines[i_86_].getw() / 6400.0F * 255.0F));
                    else
                    // ---
                    this.out.writeByte((byte) (int) (NetAircraft.this.FM.EI.engines[i_86_].getw() / 640.0F * 255.0F));
                    this.out.writeByte((byte) (int) (NetAircraft.this.FM.EI.engines[i_86_].getPropPhi() / 1.6F * 255.0F));
                }
                if (NetAircraft.this.netCockpitValid && NetAircraft.this.netCockpitTuretNum >= 0) {
                    Turret turret = NetAircraft.this.FM.turret[NetAircraft.this.netCockpitTuretNum];
                    boolean bool_87_ = NetAircraft.this.FM.CT.WeaponControl[NetAircraft.this.netCockpitWeaponControlNum];
                    this.out.writeShort(this.packSY(turret.tu[0]));
                    this.out.writeShort(this.packSP(turret.tu[1]) | (bool_87_ ? 32768 : 0));
                }

                // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View
                // Replication
                int i7 = (((this.packScale(NetAircraft.this.FM.CT.getAileron(), -1.0F, 0.0952481F) * 21 + this.packScale(NetAircraft.this.FM.CT.getElevator(), -1.0F, 0.0952481F)) * 21
                        + this.packScale(NetAircraft.this.FM.CT.getRudder(), -1.0F, 0.0952481F)) * 69 + this.packScale(NetAircraft.this.viewAzimut, -107.0F, 3.101459F)) * 26 + this.packScale(NetAircraft.this.viewTangage, -22.0F, 3.038472F);

                this.out.writeShort(i7 >> 8 & 0xFFFF);
                this.out.writeByte(i7 & 0xFF);
                // ---

//				// TODO: Storebror: Implement Guided Missile Lock State Replication
//				if (this instanceof TypeGuidedMissileCarrier) {
//					int i8 = ((TypeGuidedMissileCarrier)this).getGuidedMissileUtils().getMissileLockState();
//					this.out.writeByte(i8 & 0xFF);
//				} else {
//					this.out.writeByte(0);
//				}

                this.post(Time.current(), this.out);
            } catch (Exception exception) {
                NetObj.printDebug(exception);
            }
            // TODO: Storebror: +++ Bomb Release Bug hunting
//            if (this.weaponsCheck && Time.current() > this.weaponsSyncTime) {
//                this.weaponsSyncTime = Time.current() + 5000L;
//                this.weaponsCheck = false;
//                if (NetAircraft.this.isWeaponsChanged(this.weaponsBitStates)) {
//                    this.weaponsBitStates = NetAircraft.this.getWeaponsBitStates(this.weaponsBitStates);
//                    NetAircraft.this.netPutWeaponsBitStates(this.weaponsBitStates);
//                    this.weaponsIsEmpty = NetAircraft.this.isWeaponsAllEmpty();
//                }
//            }
            if (this.weaponsCheck && Time.current() > this.weaponsSyncTime || NetAircraft.this.netPutWeaponsBitStatesPending) if (NetAircraft.this.isUpdatePending(NetAircraft.UPDATE_MASTER)) {
                NetAircraft.this.netPutWeaponsBitStatesPending = true;
                printDebugMessage(NetAircraft.this, "Master netUpdate() deferring weaponsBitStates replication because Master Update is pending!");
            } else {
                this.weaponsSyncTime = Time.current() + 5000L;
                this.weaponsCheck = false;
                if (NetAircraft.this.isWeaponsChanged(this.weaponsBitStates)) {
                    this.weaponsBitStates = NetAircraft.this.getWeaponsBitStates(this.weaponsBitStates);
                    NetAircraft.this.netPutWeaponsBitStates(this.weaponsBitStates);
                    this.weaponsIsEmpty = NetAircraft.this.isWeaponsAllEmpty();
                }
                NetAircraft.this.netPutWeaponsBitStatesPending = false;
            }
            // TODO: Storebror: --- Bomb Release Bug hunting
            if (bool) this.weaponsCheck = true;
        }

        int packSY(float f) {
            return 0xffff & (int) ((f % 360.0 + 180.0) * 65000.0 / 360.0);
        }

        int packSP(float f) {
            return 0x7fff & (int) ((f % 360.0 + 180.0) * 32000.0 / 360.0);
        }

        // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View
        // Replication
        int packScale(float paramFloat1, float paramFloat2, float paramFloat3) {
            return (int) ((paramFloat1 - paramFloat2) / paramFloat3);
        }

        // ---

        public Master(Actor actor) {
            super(actor);
            try {
                this.out.setIncludeTime(true);
                this.out.setFilterArg(actor);
            } catch (Exception exception) {
            }
        }
    }

    public class AircraftNet extends ActorNet {
        public NetUser      netUser;

        public String       netName;

        public IntHashtable filterTable;

        private void createFilterTable() {
            if (Main.cur().netServerParams != null && !Main.cur().netServerParams.isMirror()) this.filterTable = new IntHashtable();
        }

        public AircraftNet(Actor actor) {
            super(actor);
            this.createFilterTable();
        }

        public AircraftNet(Actor actor, NetChannel netchannel, int i) {
            super(actor, netchannel, i);
            this.createFilterTable();
        }
    }

    private boolean isCoopPlane() {
        return this.bCoopPlane;
    }

    protected static String[] partNames() {
        return Aircraft.partNames();
    }

    protected int curDMGLevel(int i) {
        return 0;
    }

    protected void nextCUTLevel(String string, int i, Actor actor) {
    }

    protected void nextDMGLevel(String string, int i, Actor actor) {
    }

    protected void netHits(int i, int i_88_, int i_89_, Actor actor) {
    }

    protected void doExplosion() {
    }

    public int curDMGProp(int i) {
        return 0;
    }

    protected void weaponsLoad(String string) throws Exception {
    }

    protected void createCockpits() {
    }

    public void setFM(int i, boolean bool) {
    }

    public void preparePaintScheme() {
    }

    public void prepareCamouflage() {
    }

    public int aircIndex() {
        return -1;
    }

    public Wing getWing() {
        return null;
    }

    public void onAircraftLoaded() {
    }

    public NetUser netUser() {
        if (!this.isNet()) return null;
        return ((AircraftNet) this.net).netUser;
    }

    public String netName() {
        if (!this.isNet()) return null;
        return ((AircraftNet) this.net).netName;
    }

    public boolean isNetPlayer() {
        if (!this.isNet()) return false;
        return ((AircraftNet) this.net).netUser != null;
    }

    public void moveSteering(float f) {
    }

    public void moveWheelSink() {
    }

    public void setFMTrack(FlightModelTrack flightmodeltrack) {
        this.fmTrack = flightmodeltrack;
    }

    public FlightModelTrack fmTrack() {
        return this.fmTrack;
    }

    public boolean isFMTrackMirror() {
        return this.fmTrack != null && this.fmTrack.isMirror();
    }

    public boolean netNewAState_isEnable(boolean bool) {
        if (!this.isNet()) return false;
        if (bool && this.net.isMaster()) return false;
        if (!bool && !this.net.isMirrored()) return false;
        if (bool && this.net.masterChannel() instanceof NetChannelInStream) return false;
        return true;
    }

    public NetMsgGuaranted netNewAStateMsg(boolean bool) throws IOException {
        if (!this.isNet()) return null;
        if (bool && this.net.isMaster()) return null;
        if (!bool && !this.net.isMirrored()) return null;
        if (bool && this.net.masterChannel() instanceof NetChannelInStream) return null;
        NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
        if (bool) netmsgguaranted.writeByte(128);
        else netmsgguaranted.writeByte(0);
        return netmsgguaranted;
    }

    public void netSendAStateMsg(boolean bool, NetMsgGuaranted netmsgguaranted) throws IOException {
        if (bool) this.net.postTo(this.net.masterChannel(), netmsgguaranted);
        else this.net.post(netmsgguaranted);
    }

    public void netUpdateWayPoint() {
        if (this.net != null && Main.cur().netServerParams != null && Main.cur().netServerParams.isCoop() && !Main.cur().netServerParams.isMaster() && this.net.isMaster() && this.net.isMirrored()) {
            Master master = (Master) this.net;
            if (master.curWayPoint != this.FM.AP.way.Cur()) {
                master.curWayPoint = this.FM.AP.way.Cur();
                try {
                    NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                    netmsgguaranted.writeByte(1);
                    netmsgguaranted.writeShort(master.curWayPoint);
                    master.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
                } catch (Exception exception) {
                    printDebug(exception);
                }
            }
        }
    }

    private boolean netGetUpdateWayPoint(NetMsgInput netmsginput, boolean bool, boolean bool_90_) throws IOException {
        if (bool) return false;
        int i = netmsginput.readUnsignedShort();
        if (Main.cur().netServerParams.isMaster()) {
            this.FM.AP.way.setCur(i);
            if (i == this.FM.AP.way.size() - 1) this.FM.AP.way.next();
        } else try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(1);
            netmsgguaranted.writeShort(i);
            this.net.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
        } catch (Exception exception) {
            printDebug(exception);
        }
        return true;
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    public void netSendNewWayPoint(WayPoint waypoint, int i) {
        if (this.net == null || Main.cur().netServerParams == null || !Main.cur().netServerParams.isCoop() || !Main.cur().netServerParams.isMaster() || !this.net.isMaster() || !this.net.isMirrored()) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(16);
            netmsgguaranted.writeShort(i);
            netmsgguaranted.writeFloat(waypoint.x());
            netmsgguaranted.writeFloat(waypoint.y());
            netmsgguaranted.writeFloat(waypoint.z());
            netmsgguaranted.writeFloat(waypoint.getV());
            netmsgguaranted.writeBoolean(waypoint.isRadioSilence());
            netmsgguaranted.writeInt(waypoint.waypointType);
            netmsgguaranted.writeInt(waypoint.targetTrigger);
            netmsgguaranted.writeInt(waypoint.delayTimer);
            this.net.post(netmsgguaranted);
        } catch (Exception exception) {
            printDebug(exception);
        }
    }

    private boolean netReceiveNewWayPoint(NetMsgInput netmsginput, boolean flag, boolean flag1) throws IOException {
        if (flag1) return false;
        if (flag) return false;
        else {
            WayPoint waypoint = new WayPoint();
            short word0 = netmsginput.readShort();
            float f = netmsginput.readFloat();
            float f1 = netmsginput.readFloat();
            float f2 = netmsginput.readFloat();
            waypoint.set(f, f1, f2);
            waypoint.set(netmsginput.readFloat());
            waypoint.setRadioSilence(netmsginput.readBoolean());
            waypoint.waypointType = netmsginput.readInt();
            waypoint.targetTrigger = netmsginput.readInt();
            waypoint.delayTimer = netmsginput.readInt();
            Way way = World.getPlayerFM().AP.way;
            way.insert(word0 + 1, waypoint);
            way.setCur(word0);
            way.curr().waypointType = 0;
            return true;
        }
    }
    // TODO: --- TD AI code backport from 4.13 ---

    private int getWeaponsAmount() {
        int i = this.FM.CT.Weapons.length;
        if (this.FM.CT.Weapons.length == 0) return 0;
        int retVal = 0;
        for (int triggerIndex = 0; triggerIndex < i; triggerIndex++) {
            BulletEmitter[] bulletemitters = this.FM.CT.Weapons[triggerIndex];
            if (bulletemitters != null) for (int emitterIndex = 0; emitterIndex < bulletemitters.length; emitterIndex++)
                if (bulletemitters[emitterIndex] != null) retVal++;
        }
        return retVal;
    }

    private boolean isWeaponsAllEmpty() {
        int i = this.FM.CT.Weapons.length;
        if (this.FM.CT.Weapons.length == 0) return true;
        for (int i_94_ = 0; i_94_ < i; i_94_++) {
            BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i_94_];
            if (bulletemitters != null) for (int i_95_ = 0; i_95_ < bulletemitters.length; i_95_++)
                if (bulletemitters[i_95_] != null && bulletemitters[i_95_].countBullets() != 0) return false;
        }
        return true;
    }

    private byte[] getWeaponsBitStatesBuf(byte[] is) {
        int i = this.getWeaponsAmount();
        if (i == 0) return null;
        int i_96_ = (i + 7) / 8;
        if (is == null || is.length != i_96_) is = new byte[i_96_];
        for (int i_97_ = 0; i_97_ < i_96_; i_97_++)
            is[i_97_] = (byte) 0;
        return is;
    }

    private byte[] getWeaponsBitStates(byte[] is) {
        is = this.getWeaponsBitStatesBuf(is);
        if (is == null) return null;
        int i = 0;
        int i_98_ = this.FM.CT.Weapons.length;
        for (int i_99_ = 0; i_99_ < i_98_; i_99_++) {
            BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i_99_];
            if (bulletemitters != null) for (int i_100_ = 0; i_100_ < bulletemitters.length; i_100_++)
                if (bulletemitters[i_100_] != null) {
                    if (bulletemitters[i_100_].countBullets() != 0) is[i / 8] |= 1 << i % 8;
                    i++;
                }
        }
        return is;
    }
    
    // TODO: Added by SAS~Storebror +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // In case of excessively long weapon slots (Fugaku anyone?), use deflated/inflated data instead.
    public static byte[] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
//        System.out.println("Original: " + data.length + " bytes");
//        System.out.println("Compressed: " + output.length + " bytes");
        return output;
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
//        System.out.println("NetAircraft advanced Data Compression for Net Replication, Original block size: " + data.length);
//        System.out.println("NetAircraft advanced Data Compression for Net Replication, Compressed block size: " + output.length);
        return output;
    }
    // TODO: Added by SAS~Storebror -----------------------------------------------------------------

    private void setWeaponsBitStates(byte[] is) {
        // TODO: Storebror: +++ Bomb Release Bug hunting
        printDebugMessage(NetAircraft.this, "NetAircraft setWeaponsBitStates(" + bytesToHex(is) + ")");
        this.dumpWeaponsBitStates(is);
        // TODO: Storebror: --- Bomb Release Bug hunting

        int i = 0;
        for (int weaponsIndex = 0; weaponsIndex < this.FM.CT.Weapons.length; weaponsIndex++) {
            BulletEmitter[] bulletemitters = this.FM.CT.Weapons[weaponsIndex];
            if (bulletemitters != null) for (int beIndex = 0; beIndex < bulletemitters.length; beIndex++)
                if (bulletemitters[beIndex] != null) {
                    if ((is[i / 8] & 1 << i % 8) == 0) // TODO: Storebror: +++ Bomb Release Bug hunting
                        do {
                            if (this.isNetMirror() && (weaponsIndex == 2 || weaponsIndex == 3) && !this.isNetSpawnSetWeaponsBitStates()) if (this.isUpdatePending(UPDATE_MIRROR)) {
                                printDebugMessage(NetAircraft.this,
                                        "MIRROR: Emptying Request for " + NetAircraft.TRIGGER_NAMES[weaponsIndex - 2] + " Emitter No." + beIndex + " (" + simpleClassName(bulletemitters[beIndex]) + ") skipped, fmUpdate is pending!");
                                break;
                            } else if (bulletemitters[beIndex].haveBullets()) {
                                int numBullets = bulletemitters[beIndex].countBullets();
                                printDebugMessage(NetAircraft.this, "MIRROR: Emptying Request for " + NetAircraft.TRIGGER_NAMES[weaponsIndex - 2] + " Emitter No." + beIndex + " (" + simpleClassName(bulletemitters[beIndex])
                                        + ") skipped, >>> releasing ordnance (" + numBullets + ") instead! <<<");
                                bulletemitters[beIndex].shots(numBullets);
                                break;
                            }
                            bulletemitters[beIndex]._loadBullets(0);
                        } while (false);
//                          bulletemitters[beIndex]._loadBullets(0);
                    // TODO: Storebror: --- Bomb Release Bug hunting
                    i++;
                }
        }

        // TODO: Storebror: +++ Bomb Release Bug hunting
        this.setNetSpawnSetWeaponsBitStates(false);
        // TODO: Storebror: --- Bomb Release Bug hunting
    }

    private boolean isWeaponsChanged(byte[] is) {
        if (this.getWeaponsAmount() == 0) return false;
        if (is == null) return true;
        int i = 0;
        int i_104_ = this.FM.CT.Weapons.length;
        for (int i_105_ = 0; i_105_ < i_104_; i_105_++) {
            BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i_105_];
            if (bulletemitters != null) for (int i_106_ = 0; i_106_ < bulletemitters.length; i_106_++)
                if (bulletemitters[i_106_] != null) {
                    if ((is[i / 8] & 1 << i % 8) == 0 != (bulletemitters[i_106_].countBullets() == 0)) return true;
                    i++;
                }
        }
        return false;
    }

    private void netPutWeaponsBitStates(byte[] is) {
        // TODO: Storebror: +++ Bomb Release Bug hunting
        NetAircraft.printDebugMessage(this, "NetAircraft netPutWeaponsBitStates this.isNet()=" + this.isNet() + ", this.net.countMirrors()=" + (this.isNet() ? this.net.countMirrors() : 0));
        this.dumpWeaponsBitStates(is);
        // TODO: Storebror: --- Bomb Release Bug hunting
        if (this.isNet() && this.net.countMirrors() != 0) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(2);
            netmsgguaranted.write(is);
            this.net.post(netmsgguaranted);
        } catch (Exception exception) {
            printDebug(exception);
        }
    }

    private boolean netGetWeaponsBitStates(NetMsgInput netmsginput, boolean bool, boolean bool_107_) throws IOException {
        if (bool || bool_107_) return false;
        byte[] is = this.getWeaponsBitStatesBuf(null);
        // TODO: Added by SAS~Storebror +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // In case of excessively long weapon slots (Fugaku anyone?), use deflated/inflated data instead.
        if (is.length > 80) {
            int compressedLength = netmsginput.readUnsignedByte();
            byte[] buf = new byte[compressedLength];
            for (int i = 0; i < buf.length; i++)
                buf[i] = (byte) netmsginput.readUnsignedByte();
            try { byte[] inflated = decompress(buf);
                for (int i = 0; i < is.length && i < inflated.length; i++) is[i] = inflated[i];
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
        } else
        // TODO: Added by SAS~Storebror -----------------------------------------------------------------
        for (int i = 0; i < is.length; i++)
            is[i] = (byte) netmsginput.readUnsignedByte();
        this.setWeaponsBitStates(is);
        this.netPutWeaponsBitStates(is);
        return true;
    }

    public boolean isGunPodsExist() {
        return this.bGunPodsExist;
    }

    public boolean isGunPodsOn() {
        return this.bGunPodsOn;
    }

    public void setGunPodsOn(boolean bool) {
        if (this.bGunPodsOn != bool) {
            for (int i = 0; i < this.FM.CT.Weapons.length; i++) {
                BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i];
                if (bulletemitters != null) for (int i_108_ = 0; i_108_ < bulletemitters.length; i_108_++)
                    if (bulletemitters[i_108_] != null) bulletemitters[i_108_].setPause(!bool);
            }
            this.bGunPodsOn = bool;
            if (this.isNet() && this.net.countMirrors() != 0) try {
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                if (bool) netmsgguaranted.writeByte(3);
                else netmsgguaranted.writeByte(4);
                this.net.post(netmsgguaranted);
            } catch (Exception exception) {
                printDebug(exception);
            }
        }
    }

    public void replicateDropFuelTanks() {
        if (this.isNet() && this.net.countMirrors() != 0) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(5);
            this.net.post(netmsgguaranted);
        } catch (Exception exception) {
            printDebug(exception);
        }
    }

    protected void netPutHits(boolean bool, NetChannel netchannel, int i, int i_109_, int i_110_, Actor actor) {
        if ((bool || this.net.countMirrors() != 0) && Actor.isValid(actor) && actor.isNet()) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            if (bool) netmsgguaranted.writeByte(134);
            else netmsgguaranted.writeByte(6);
            netmsgguaranted.writeByte(i & 0xf | i_109_ << 4 & 0xf0);
            netmsgguaranted.writeByte(i_110_);
            netmsgguaranted.writeNetObj(actor.net);
            if (bool) this.net.postTo(this.net.masterChannel(), netmsgguaranted);
            else this.net.postExclude(netchannel, netmsgguaranted);
        } catch (Exception exception) {
            printDebug(exception);
        }
    }

    private boolean netGetHits(NetMsgInput netmsginput, boolean bool, boolean bool_111_) throws IOException {
        if (bool && !bool_111_) return false;
        int i = netmsginput.readUnsignedByte();
        int i_112_ = i >> 4 & 0xf;
        i &= 0xf;
        int i_113_ = netmsginput.readUnsignedByte();
        if (i_113_ >= 44) return false;
        NetObj netobj = netmsginput.readNetObj();
        if (netobj == null) return true;
        Actor actor = (Actor) netobj.superObj();
        if (!Actor.isValid(actor)) return true;
        if (!bool && bool_111_) this.netPutHits(true, null, i, i_112_, i_113_, actor);
        if (this.net.countMirrors() > 1) this.netPutHits(false, netmsginput.channel(), i, i_112_, i_113_, actor);
        this.netHits(i, i_112_, i_113_, actor);
        return true;
    }

    public void hitProp(int i, int i_114_, Actor actor) {
        if (this.isNet() && this.net.isMirrored()) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(7);
            netmsgguaranted.writeByte(i);
            netmsgguaranted.writeByte(i_114_);
            netmsgguaranted.writeNetObj(actor != null ? actor.net : null);
            this.net.post(netmsgguaranted);
        } catch (Exception exception) {
            printDebug(exception);
        }
    }

    private boolean netGetHitProp(NetMsgInput netmsginput, boolean bool, boolean bool_115_) throws IOException {
        if (bool || bool_115_) return false;
        int i = netmsginput.readUnsignedByte();
        int i_116_ = netmsginput.readUnsignedByte();
        NetObj netobj = netmsginput.readNetObj();
        this.hitProp(i, i_116_, netobj != null ? (Actor) netobj.superObj() : null);
        return true;
    }

    protected void netPutCut(int i, int i_117_, Actor actor) {
        if (this.isNet() && this.net.countMirrors() != 0) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(8);
            netmsgguaranted.writeByte(i);
            netmsgguaranted.writeByte(i_117_);
            netmsgguaranted.writeNetObj(actor != null ? actor.net : null);
            this.net.post(netmsgguaranted);
        } catch (Exception exception) {
            printDebug(exception);
        }
    }

    private boolean netGetCut(NetMsgInput netmsginput, boolean bool, boolean bool_118_) throws IOException {
        if (bool || bool_118_) return false;
        int i = netmsginput.readUnsignedByte();
        if (i >= 44) return false;
        int i_119_ = netmsginput.readUnsignedByte();
        NetObj netobj = netmsginput.readNetObj();
        this.nextCUTLevel(partNames()[i] + "_D0", i_119_, netobj != null ? (Actor) netobj.superObj() : null);
        return true;
    }

    public void netExplode() {
        if (this.isNet() && this.net.countMirrors() != 0) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(9);
            this.net.post(netmsgguaranted);
        } catch (Exception exception) {
            printDebug(exception);
        }
    }

    private boolean netGetExplode(NetMsgInput netmsginput, boolean bool, boolean bool_120_) throws IOException {
        if (bool || bool_120_) return false;
        this.netExplode();
        this.doExplosion();
        return true;
    }

    public void setDiedFlag(boolean bool) {
        if (this.isAlive() && bool && this.isNet() && Actor.isValid(this.getDamager()) && this.getDamager().isNet() && this.net.countMirrors() > 0) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(10);
            netmsgguaranted.writeNetObj(this.getDamager().net);
            this.net.post(netmsgguaranted);
        } catch (Exception exception) {
            printDebug(exception);
        }
        super.setDiedFlag(bool);
    }

    private boolean netGetDead(NetMsgInput netmsginput, boolean bool, boolean bool_121_) throws IOException {
        NetObj netobj = netmsginput.readNetObj();
        if (netobj != null) {
            if (this.isAlive()) World.onActorDied(this, (Actor) netobj.superObj());
            if (this.net.countMirrors() > 0) try {
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(10);
                netmsgguaranted.writeNetObj(netobj);
                this.net.post(netmsgguaranted);
            } catch (Exception exception) {
                printDebug(exception);
            }
        }
        return true;
    }

    public void netFirstUpdate(NetChannel netchannel) throws IOException {
        // TODO: Changed by |ZUTI|: fixes propellers not spinning for DF at start,
        // folded wings etc.
        // if (!Mission.isDogfight() && (!Mission.isCoop() || !isNetPlayer()) &&
        // netchannel instanceof NetChannelOutStream)
        if ((!Mission.isCoop() || !this.isNetPlayer()) && (netchannel instanceof NetChannelOutStream || Mission.isDogfight())) {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(11);
//            System.out.println("netFirstUpdate 1 length=" + netmsgguaranted.dataLength());
            this.netReplicateFirstUpdate(netchannel, netmsgguaranted);
//            System.out.println("netFirstUpdate 2 length=" + netmsgguaranted.dataLength());
            this.net.postTo(netchannel, netmsgguaranted);
            if (Mission.isSingle() && World.getPlayerAircraft() == this) if (this.fmTrack() == null) {
                if (this.isNetMaster()) new MsgAction(true, this) {
                    public void doAction(Object object) {
                        new FlightModelTrack((Aircraft) object);
                    }
                };
            } else MsgNet.postRealNewChannel(this.fmTrack(), netchannel);
        }
        this.netCockpitFirstUpdate(this, netchannel);
    }

    private boolean netGetFirstUpdate(NetMsgInput netmsginput) throws IOException {
        ActorSpawnArg actorspawnarg = new ActorSpawnArg();
        try {
            netSpawnCommon(netmsginput, actorspawnarg);
            this.pos.setAbs(actorspawnarg.point, actorspawnarg.orient);
            this.pos.reset();
            this.setSpeed(actorspawnarg.speed);
            netSpawnCommon(netmsginput, actorspawnarg, this);
        } catch (Exception exception) {
            printDebug(exception);
        }
        return true;
    }

    public int netCockpitAstatePilotIndx(int i) {
        return netCockpitAstatePilotIndx(this.getClass(), i);
    }

    public static int netCockpitAstatePilotIndx(Class var_class, int i) {
        if (i < 0) return -1;
        Object object = Property.value(var_class, "cockpitClass");
        if (object == null) return -1;
        if (object instanceof Class) {
            if (i > 0) return -1;
            return Property.intValue((Class) object, "astatePilotIndx", 0);
        }
        Class[] var_classes = (Class[]) object;
        if (i >= var_classes.length) return -1;
        return Property.intValue(var_classes[i], "astatePilotIndx", 0);
    }

    public void netCockpitAuto(Actor actor, int i, boolean bool) {
        short[] is = null;
        Object object = Property.value(this.getClass(), "cockpitClass");
        if (object != null) {
            int i_124_;
            if (object instanceof Class) {
                if (i > 0) return;
                i_124_ = Property.intValue((Class) object, "weaponControlNum", 10);
            } else {
                Class[] var_classes = (Class[]) object;
                if (i >= var_classes.length) return;
                i_124_ = Property.intValue(var_classes[i], "weaponControlNum", 10);
            }
            if (World.cur().diffCur.Limited_Ammo) {
                BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i_124_];
                if (bulletemitters != null) {
                    is = new short[bulletemitters.length];
                    for (int i_125_ = 0; i_125_ < bulletemitters.length; i_125_++) {
                        int i_126_ = bulletemitters[i_125_].countBullets();
                        if (i_126_ < 0) is[i_125_] = (short) -1;
                        else is[i_125_] = (short) i_126_;
                    }
                }
            }
            this.netCockpitAuto(actor, i, bool, is, null);
        }
    }

    private void netCockpitAuto(Actor actor, int i, boolean bool, short[] is, NetChannel netchannel) {
        Object object = Property.value(this.getClass(), "cockpitClass");
        if (object != null) {
            Class var_class;
            if (object instanceof Class) {
                if (i > 0) return;
                var_class = (Class) object;
            } else {
                Class[] var_classes = (Class[]) object;
                if (i >= var_classes.length) return;
                var_class = var_classes[i];
            }
            if (!CockpitPilot.class.isAssignableFrom(var_class)) {
                int i_128_ = Property.intValue(var_class, "weaponControlNum", 10);
                int i_129_ = Property.intValue(var_class, "aiTuretNum", 0);
                if (this == World.getPlayerAircraft()) {
                    CockpitGunner cockpitgunner = (CockpitGunner) Main3D.cur3D().cockpits[i];
                    cockpitgunner.setRealMode(!bool);
                } else {
                    Turret turret = this.FM.turret[i_129_];
                    turret.bIsAIControlled = bool;
                }
                BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i_128_];
                if (bulletemitters != null) {
                    boolean bool_130_ = !actor.net.isMaster() || World.cur().diffCur.Realistic_Gunnery;
                    if (bool) bool_130_ = true;
                    for (int i_131_ = 0; i_131_ < bulletemitters.length; i_131_++) {
                        if (bulletemitters[i_131_] instanceof Actor) {
                            ((Actor) bulletemitters[i_131_]).setOwner(bool ? (Actor) this : actor);
                            if (bulletemitters[i_131_] instanceof Gun) ((Gun) bulletemitters[i_131_]).initRealisticGunnery(bool_130_);
                        }
                        if (is != null) {
                            short i_132_ = is[i_131_];
//                            if (i_132_ == 65535) i_132_ = (short) -1;
                            bulletemitters[i_131_]._loadBullets(i_132_);
                        } else if (!World.cur().diffCur.Limited_Ammo) bulletemitters[i_131_].loadBullets(-1);
                    }
                }
                if (actor instanceof NetGunner) {
                    ((NetGunner) actor).netCockpitTuretNum = bool ? -1 : i_129_;
                    ((NetGunner) actor).netCockpitWeaponControlNum = i_128_;
                } else {
                    this.netCockpitTuretNum = bool ? -1 : i_129_;
                    this.netCockpitWeaponControlNum = i_128_;
                }
            } else if (actor instanceof NetGunner) ((NetGunner) actor).netCockpitTuretNum = -1;
            else this.netCockpitTuretNum = -1;
            int i_133_ = this.net.countMirrors();
            if (this.net.isMirror()) i_133_++;
            if (netchannel != null) i_133_--;
            if (i_133_ > 0) {
                if (actor instanceof NetGunner) ((NetGunner) actor).netCockpitValid = false;
                else this.netCockpitValid = false;
                try {
                    NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted() {
                        public void unLocking() {
                            try {
                                Actor actor_135_ = (Actor) ((NetObj) this.objects().get(0)).superObj();
                                if (actor_135_ instanceof NetGunner) {
                                    if (((NetGunner) actor_135_).netCockpitMsg == this) ((NetGunner) actor_135_).netCockpitValid = true;
                                } else if (NetAircraft.this.netCockpitMsg == this) NetAircraft.this.netCockpitValid = true;
                            } catch (Exception exception) {
                                printDebug(exception);
                            }
                        }
                    };
                    if (actor instanceof NetGunner) ((NetGunner) actor).netCockpitMsg = netmsgguaranted;
                    else this.netCockpitMsg = netmsgguaranted;
                    netmsgguaranted.writeByte(13);
                    netmsgguaranted.writeNetObj(actor.net);
                    if (bool) i |= 0x80;
                    netmsgguaranted.writeByte(i);
                    if (is != null) for (int i_136_ = 0; i_136_ < is.length; i_136_++)
                        netmsgguaranted.writeShort(is[i_136_]);
                    this.net.postExclude(netchannel, netmsgguaranted);
                } catch (Exception exception) {
                    printDebug(exception);
                }
            } else if (actor instanceof NetGunner) ((NetGunner) actor).netCockpitValid = true;
            else this.netCockpitValid = true;
        }
    }

    private boolean netGetCockpitAuto(NetMsgInput netmsginput, boolean bool, boolean bool_137_) throws IOException {
        NetObj netobj = netmsginput.readNetObj();
        if (netobj == null) return false;
        Actor actor = (Actor) netobj.superObj();
        int i = netmsginput.readUnsignedByte();
        boolean bool_138_ = (i & 0x80) != 0;
        i &= ~0x80;
        short[] is = null;
        int i_139_ = netmsginput.available() / 2;
        if (i_139_ > 0) {
            is = new short[i_139_];
            for (int i_140_ = 0; i_140_ < is.length; i_140_++)
                is[i_140_] = (short) netmsginput.readUnsignedShort();
        }
        this.netCockpitAuto(actor, i, bool_138_, is, netmsginput.channel());
        return true;
    }

    public void netCockpitEnter(Actor actor, int i) {
        this.netCockpitEnter(actor, i, true);
    }

    public void netCockpitEnter(Actor actor, int i, boolean bool) {
        if (bool) if (actor instanceof NetGunner) EventLog.onOccupied((Aircraft) this, ((NetGunner) actor).getUser(), this.netCockpitAstatePilotIndx(i));
        else {
            EventLog.onOccupied((Aircraft) this, ((Aircraft) actor).netUser(), this.netCockpitAstatePilotIndx(i));
            if (actor == World.getPlayerAircraft() && actor.isNetMaster() && i == 0 && !this.bWeaponsEventLog) {
                this.bWeaponsEventLog = true;
                EventLog.onWeaponsLoad(actor, this.thisWeaponsName, (int) (this.FM.M.fuel * 100.0F / this.FM.M.maxFuel));
            }
        }
        this.netCockpitEnter(actor, i, (NetChannel) null);
    }

    private void netCockpitEnter(Actor actor, int i, NetChannel netchannel) {
        int i_141_ = this.netCockpitIndxPilot;
        if (actor instanceof NetGunner) i_141_ = ((NetGunner) actor).netCockpitIndxPilot;
        Object object_143_ = Property.value(this.getClass(), "cockpitClass");
        if (object_143_ != null) {
            Class var_class;
            Class var_class_144_;
            if (object_143_ instanceof Class) {
                if (i_141_ > 0 || i > 0) return;
                var_class = var_class_144_ = (Class) object_143_;
            } else {
                Class[] var_classes = (Class[]) object_143_;
                if (i_141_ >= var_classes.length || i >= var_classes.length) return;
                var_class = var_classes[i_141_];
                var_class_144_ = var_classes[i];
            }
            if (!CockpitPilot.class.isAssignableFrom(var_class)) {
                int i_145_ = Property.intValue(var_class, "aiTuretNum", 0);
                Turret turret = this.FM.turret[i_145_];
                turret.bIsNetMirror = false;
            }
            if (!CockpitPilot.class.isAssignableFrom(var_class_144_)) {
                int i_146_ = Property.intValue(var_class_144_, "aiTuretNum", 0);
                Turret turret = this.FM.turret[i_146_];
                turret.bIsNetMirror = actor.net.isMirror();
            }
            if (actor instanceof NetGunner) ((NetGunner) actor).netCockpitIndxPilot = i;
            else this.netCockpitIndxPilot = i;
            this.netCockpitDriverSet(actor, i);
            int i_147_ = 0;
            int i_148_ = -1;
            if (!CockpitPilot.class.isAssignableFrom(var_class_144_)) {
                i_148_ = Property.intValue(var_class_144_, "aiTuretNum", 0);
                Turret turret = this.FM.turret[i_148_];
                if (turret.bIsAIControlled) i_148_ = -1;
                else i_147_ = Property.intValue(var_class_144_, "weaponControlNum", 10);
            }
            if (actor instanceof NetGunner) {
                ((NetGunner) actor).netCockpitTuretNum = i_148_;
                ((NetGunner) actor).netCockpitWeaponControlNum = i_147_;
            } else {
                this.netCockpitTuretNum = i_148_;
                this.netCockpitWeaponControlNum = i_147_;
            }
            int i_149_ = this.net.countMirrors();
            if (this.net.isMirror()) i_149_++;
            if (netchannel != null) i_149_--;
            if (i_149_ > 0) try {
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(12);
                netmsgguaranted.writeNetObj(actor.net);
                netmsgguaranted.writeByte(i);
                this.net.postExclude(netchannel, netmsgguaranted);
            } catch (Exception exception) {
                printDebug(exception);
            }
        }
    }

    private boolean netGetCockpitEnter(NetMsgInput netmsginput, boolean bool, boolean bool_150_) throws IOException {
        NetObj netobj = netmsginput.readNetObj();
        if (netobj == null) return false;
        Actor actor = (Actor) netobj.superObj();
        int i = netmsginput.readUnsignedByte();
        this.netCockpitEnter(actor, i, netmsginput.channel());
        return true;
    }

    protected void netCockpitFirstUpdate(Actor actor, NetChannel netchannel) throws IOException {
        int i = this.netCockpitIndxPilot;
        int i_151_ = this.netCockpitTuretNum;
        if (actor instanceof NetGunner) {
            i = ((NetGunner) actor).netCockpitIndxPilot;
            i_151_ = ((NetGunner) actor).netCockpitTuretNum;
        }
        if (i != 0) {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(12);
            netmsgguaranted.writeNetObj(actor.net);
            netmsgguaranted.writeByte(i);
            this.net.postTo(netchannel, netmsgguaranted);
        }
        if (i_151_ >= 0) {
            short[] is = null;
            Object object = Property.value(this.getClass(), "cockpitClass");
            if (object != null) {
                Class var_class;
                if (object instanceof Class) {
                    if (i > 0) return;
                    var_class = (Class) object;
                } else {
                    Class[] var_classes = (Class[]) object;
                    if (i >= var_classes.length) return;
                    var_class = var_classes[i];
                }
                int i_153_ = Property.intValue(var_class, "weaponControlNum", 10);
                if (World.cur().diffCur.Limited_Ammo) {
                    BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i_153_];
                    if (bulletemitters != null) {
                        is = new short[bulletemitters.length];
                        for (int i_154_ = 0; i_154_ < bulletemitters.length; i_154_++) {
                            int i_155_ = bulletemitters[i_154_].countBullets();
                            if (i_155_ < 0) is[i_154_] = (short) -1;
                            else is[i_154_] = (short) i_155_;
                        }
                    }
                }
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted() {
                    public void unLocking() {
                        Actor actor_157_ = (Actor) ((NetObj) this.objects().get(0)).superObj();
                        if (actor_157_ instanceof NetGunner) {
                            if (((NetGunner) actor_157_).netCockpitMsg == this) ((NetGunner) actor_157_).netCockpitValid = true;
                        } else if (NetAircraft.this.netCockpitMsg == this) NetAircraft.this.netCockpitValid = true;
                    }
                };
                if (actor instanceof NetGunner) {
                    if (!((NetGunner) actor).netCockpitValid) ((NetGunner) actor).netCockpitMsg = netmsgguaranted;
                } else if (!this.netCockpitValid) this.netCockpitMsg = netmsgguaranted;
                netmsgguaranted.writeByte(13);
                netmsgguaranted.writeNetObj(actor.net);
                netmsgguaranted.writeByte(i);
                if (is != null) for (int i_158_ = 0; i_158_ < is.length; i_158_++)
                    netmsgguaranted.writeShort(is[i_158_]);
                this.net.postTo(netchannel, netmsgguaranted);
            }
        }
    }

    private boolean netCockpitCheckDrivers() {
        if (this.netCockpitDrivers != null) return true;
        Object object = Property.value(this.getClass(), "cockpitClass");
        if (object == null) return false;
        if (object instanceof Class) this.netCockpitDrivers = new Actor[1];
        else {
            Class[] var_classes = (Class[]) object;
            this.netCockpitDrivers = new Actor[var_classes.length];
        }
        return true;
    }

    public Actor netCockpitGetDriver(int i) {
        if (!this.netCockpitCheckDrivers()) return null;
        if (i < 0 || i >= this.netCockpitDrivers.length) return null;
        return this.netCockpitDrivers[i];
    }

    private void netCockpitDriverSet(Actor actor, int i) {
        if (this.netCockpitCheckDrivers()) {
            NetUser netuser = this.netUser();
            if (actor instanceof NetGunner) netuser = ((NetGunner) actor).getUser();
            if (netuser == null) netuser = (NetUser) NetEnv.host();
            for (int i_159_ = 0; i_159_ < this.netCockpitDrivers.length; i_159_++)
                if (this.netCockpitDrivers[i_159_] == actor) {
                    this.netCockpitDrivers[i_159_] = null;
                    netuser.tryPreparePilotDefaultSkin((Aircraft) this, this.netCockpitAstatePilotIndx(i_159_));
                }
            this.netCockpitDrivers[i] = actor;
            netuser.tryPreparePilotSkin(this, this.netCockpitAstatePilotIndx(i));
        }
    }

    public void netCockpitDriverRequest(Actor actor, int i) {
        if (this.netCockpitCheckDrivers() && i >= 0 && i < this.netCockpitDrivers.length) if (this.net.isMaster()) {
            if (this.netCockpitDrivers[i] == null) {
                this.netCockpitDriverSet(actor, i);
                Main3D.cur3D().aircraftHotKeys.netSwitchToCockpit(i);
            }
        } else try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(14);
            netmsgguaranted.writeNetObj(actor.net);
            netmsgguaranted.writeByte(i);
            this.net.postTo(this.net.masterChannel(), netmsgguaranted);
        } catch (Exception exception) {
            printDebug(exception);
        }
    }

    private boolean netGetCockpitDriver(NetMsgInput netmsginput, boolean bool, boolean bool_160_) throws IOException {
        NetObj netobj = netmsginput.readNetObj();
        if (netobj == null) return false;
        Actor actor = (Actor) netobj.superObj();
        int i = netmsginput.readUnsignedByte();
        if (!this.netCockpitCheckDrivers()) return false;
        if (i < 0 || i >= this.netCockpitDrivers.length) return true;
        if (bool) {
            if (this.netCockpitDrivers[i] != null) return true;
            this.netCockpitDriverSet(actor, i);
        } else if (bool_160_) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(14);
            netmsgguaranted.writeNetObj(actor.net);
            netmsgguaranted.writeByte(i);
            this.net.postTo(this.net.masterChannel(), netmsgguaranted);
        } catch (Exception exception) {
            printDebug(exception);
        }
        else {
            this.netCockpitDriverSet(actor, i);
            if (actor.net.isMaster()) Main3D.cur3D().aircraftHotKeys.netSwitchToCockpit(i);
        }
        if (this.net.countMirrors() > 0) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(14);
            netmsgguaranted.writeNetObj(actor.net);
            netmsgguaranted.writeByte(i);
            this.net.post(netmsgguaranted);
        } catch (Exception exception) {
            printDebug(exception);
        }
        return true;
    }

    boolean netGetGMsg(NetMsgInput netmsginput, boolean bool) throws IOException {
        int i = netmsginput.readUnsignedByte();

        // TODO: Added by |ZUTI|
        // -----------------------------------------------------------------
        if (ZutiSupportMethods_Air.processNetAircraftMirroredMessage((Aircraft) this, i, netmsginput)) return true;

        boolean bool2 = (i & 0x80) == 128;
        i &= ~0x80;
        switch (i) {
            case 0:
                this.FM.AS.netUpdate(bool, bool2, netmsginput);
                return true;
            case 1:
                return this.netGetUpdateWayPoint(netmsginput, bool, bool2);
            case 2:
                return this.netGetWeaponsBitStates(netmsginput, bool, bool2);
            case 3:
                this.setGunPodsOn(true);
                return true;
            case 4:
                this.setGunPodsOn(false);
                return true;
            case 5:
                this.FM.CT.dropFuelTanks();
                return true;
            case 6:
                return this.netGetHits(netmsginput, bool, bool2);
            case 7:
                return this.netGetHitProp(netmsginput, bool, bool2);
            case 8:
                return this.netGetCut(netmsginput, bool, bool2);
            case 9:
                return this.netGetExplode(netmsginput, bool, bool2);
            case 10:
                return this.netGetDead(netmsginput, bool, bool2);
            case 11:
                return this.netGetFirstUpdate(netmsginput);
            case 12:
                return this.netGetCockpitEnter(netmsginput, bool, bool2);
            case 13:
                return this.netGetCockpitAuto(netmsginput, bool, bool2);
            case 14:
                return this.netGetCockpitDriver(netmsginput, bool, bool2);
            case 15:
                this.FM.CT.dropExternalStores(false);
                return true;
            // TODO: +++ TD AI code backport from 4.13 +++
            case NETG_ID_CODE_GENERATE_WP:
                return this.netReceiveNewWayPoint(netmsginput, bool, bool2);
            // TODO: --- TD AI code backport from 4.13 ---
            default:
                return false;
        }
    }

    protected void sendMsgSndShot(Shot shot) {
        int i = shot.mass > 0.05F ? 1 : 0;
        Actor._tmpPoint.set(this.pos.getAbsPoint());
        Actor._tmpPoint.sub(shot.p);
        int i_162_ = (int) (Actor._tmpPoint.x / 0.25) & 0xfe;
        int i_163_ = (int) (Actor._tmpPoint.y / 0.25) & 0xfe;
        i &= 0x3;
        try {
            NetMsgFiltered netmsgfiltered = new NetMsgFiltered();
            netmsgfiltered.writeByte(i_162_ | i >> 1);
            netmsgfiltered.writeByte(i_163_ | i & 0x1);
            this.net.postTo(Time.current(), this.net.masterChannel(), netmsgfiltered);
        } catch (Exception exception) {
        }
    }

    protected void msgSndShot(float f, double d, double d_164_, double d_165_) {
    }

    public void makeMirrorCarrierRelPos() {
        if (this.net != null && !this.net.isMaster()) ((Mirror) this.net).makeFirstUnderDeck();
    }

    public boolean isMirrorUnderDeck() {
        if (this.net == null || this.net.isMaster()) return false;
        return ((Mirror) this.net).bUnderDeck;
    }

    public void destroy() {
        if (!this.isDestroyed()) {
            if (this.isNetMaster() && this.fmTrack != null && !this.fmTrack.isDestroyed()) this.fmTrack.destroy();
            this.fmTrack = null;

            // TODO: Added by |ZUTI| - eject all connected gunners
            // ------------------------------------------------------------------
            if (this.isNetMaster()) ZutiSupportMethods_Multicrew.ejectGunnersForAircraft(this.name());
            // ------------------------------------------------------------------
            super.destroy();
            this.damagers.clear();
        }
    }

    public void createNetObject(NetChannel netchannel, int i) {
        if (netchannel == null) this.net = new Master(this);
        else this.net = new Mirror(this, netchannel, i);
    }

    public void restoreLinksInCoopWing() {
        if (Main.cur().netServerParams != null && Main.cur().netServerParams.isCoop()) {
            Wing wing = this.getWing();
            Aircraft[] aircrafts = wing.airc;
            int i;
            for (i = 0; i < aircrafts.length; i++)
                if (Actor.isValid(aircrafts[i])) break;
            if (i != aircrafts.length) {
                aircrafts[i].FM.Leader = null;
                for (int i_166_ = i + 1; i_166_ < aircrafts.length; i_166_++)
                    if (Actor.isValid(aircrafts[i_166_])) {
                        aircrafts[i].FM.Wingman = aircrafts[i_166_].FM;
                        aircrafts[i_166_].FM.Leader = aircrafts[i].FM;
                        i = i_166_;
                    }
            }
        }
    }

    private static void netSpawnCommon(NetMsgInput netmsginput, ActorSpawnArg actorspawnarg) throws Exception {
        actorspawnarg.point = new Point3d(netmsginput.readFloat(), netmsginput.readFloat(), netmsginput.readFloat());
        actorspawnarg.orient = new Orient(netmsginput.readFloat(), netmsginput.readFloat(), netmsginput.readFloat());
        actorspawnarg.speed = new Vector3d(netmsginput.readFloat(), netmsginput.readFloat(), netmsginput.readFloat());
    }

    private static void netSpawnCommon(NetMsgInput netmsginput, ActorSpawnArg actorspawnarg, NetAircraft netaircraft) throws Exception {
        Mirror mirror = (Mirror) netaircraft.net;
        mirror.netFirstUpdate((float) actorspawnarg.point.x, (float) actorspawnarg.point.y, (float) actorspawnarg.point.z, actorspawnarg.orient.azimut(), actorspawnarg.orient.tangage(), actorspawnarg.orient.kren(), (float) actorspawnarg.speed.x,
                (float) actorspawnarg.speed.y, (float) actorspawnarg.speed.z);
        int i = 0;
        for (int i_167_ = 0; i_167_ < 44; i_167_++) {
            int i_168_;
            if ((i_167_ & 0x1) == 0) {
                i = netmsginput.readUnsignedByte();
                i_168_ = i & 0xff;
            } else i_168_ = i >> 8 & 0xff;
            while (i_168_-- > 0) {
                NetAircraft netaircraft_169_ = netaircraft;
                StringBuffer stringbuffer = new StringBuffer();
                netaircraft_169_.nextDMGLevel(stringbuffer.append(partNames()[i_167_]).append("_D0").toString(), 0, null);
            }
        }
        long l = netmsginput.readLong();
        if (l != netaircraft.FM.Operate) {
            int i_170_ = 0;
            long l_171_ = 1L;
            while (i_170_ < 44) {
                if ((l & l_171_) == 0L && (netaircraft.FM.Operate & l_171_) != 0L) {
                    NetAircraft netaircraft_172_ = netaircraft;
                    StringBuffer stringbuffer = new StringBuffer();
                    netaircraft_172_.nextCUTLevel(stringbuffer.append(partNames()[i_170_]).append("_D0").toString(), 0, null);
                }
                i_170_++;
                l_171_ <<= 1;
            }
        }
        int i_173_ = netmsginput.readByte();
        for (int i_174_ = 0; i_174_ < 4; i_174_++)
            if ((i_173_ & 1 << i_174_) != 0) netaircraft.hitProp(i_174_, 0, null);
        if ((i_173_ & 0x10) != 0) netaircraft.setGunPodsOn(false);
        byte[] is = netaircraft.getWeaponsBitStatesBuf(null);
        if (is != null) {
            for (int i_175_ = 0; i_175_ < is.length; i_175_++)
                is[i_175_] = (byte) netmsginput.readUnsignedByte();
            // TODO: Storebror: +++ Bomb Release Bug hunting
            netaircraft.setNetSpawnSetWeaponsBitStates(true);
            // TODO: Storebror: --- Bomb Release Bug hunting
            netaircraft.setWeaponsBitStates(is);
        }
        // TODO: Added by |ZUTI|: added try/catch block here to catch exception on
        // server side, if player takes on a plane that is not available
        // ---------------------------------------------------
        try {
            netaircraft.FM.AS.netFirstUpdate(netmsginput);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // ---------------------------------------------------
    }

    private void netReplicateCommon(NetChannel netchannel, NetMsgGuaranted netmsgguaranted) throws IOException {
        netmsgguaranted.writeFloat(this.FM.M.fuel / this.FM.M.maxFuel);
        netmsgguaranted.writeBoolean(this.bPaintShemeNumberOn);
        netmsgguaranted.write255(((AircraftNet) this.net).netName);
        netmsgguaranted.write255(this.thisWeaponsName);
        netmsgguaranted.writeNetObj(((AircraftNet) this.net).netUser);
    }

    private void netReplicateFirstUpdate(NetChannel netchannel, NetMsgGuaranted netmsgguaranted) throws IOException {
//        System.out.println("netFirstUpdate 3 length=" + netmsgguaranted.dataLength());
        Point3d point3d = this.pos.getAbsPoint();
        netmsgguaranted.writeFloat((float) point3d.x);
        netmsgguaranted.writeFloat((float) point3d.y);
        netmsgguaranted.writeFloat((float) point3d.z);
        Orient orient = this.pos.getAbsOrient();
        netmsgguaranted.writeFloat(orient.azimut());
        netmsgguaranted.writeFloat(orient.tangage());
        netmsgguaranted.writeFloat(orient.kren());
        Vector3d vector3d = new Vector3d();
        this.getSpeed(vector3d);
        netmsgguaranted.writeFloat((float) vector3d.x);
        netmsgguaranted.writeFloat((float) vector3d.y);
        netmsgguaranted.writeFloat((float) vector3d.z);
        int i1 = 0;
        int i2;
//        System.out.println("netFirstUpdate 4 length=" + netmsgguaranted.dataLength());
        for (i2 = 0; i2 < 44; i2++)
            if ((i2 & 0x1) == 0) i1 = this.curDMGLevel(i2) & 0xff;
            else {
                i1 |= (this.curDMGLevel(i2) & 0xff) << 8;
                netmsgguaranted.writeByte(i1);
            }
        if ((i2 & 0x1) == 1) netmsgguaranted.writeByte(i1);
//        System.out.println("netFirstUpdate 5 length=" + netmsgguaranted.dataLength());
        netmsgguaranted.writeLong(this.FM.Operate);
        int i3 = this.curDMGProp(0) | this.curDMGProp(1) << 1 | this.curDMGProp(2) << 2 | this.curDMGProp(3) << 3;
        if (!this.isGunPodsOn()) i3 |= 0x10;
        netmsgguaranted.writeByte(i3);
        byte[] is = this.getWeaponsBitStates(null);
        if (is != null) {
            // TODO: Added by SAS~Storebror +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // In case of excessively long weapon slots (Fugaku anyone?), use deflated/inflated data instead.
            if (is.length > 80) {
                is = compress(is); // FIXME: Welp... IL-2 can't replicate long weapons lists (Fugaku) correctly!
                netmsgguaranted.writeByte(is.length);
            }
            // TODO: Added by SAS~Storebror -----------------------------------------------------------------
            netmsgguaranted.write(is);
        }
//        System.out.println("netFirstUpdate 6 length=" + netmsgguaranted.dataLength());
        this.FM.AS.netReplicate(netmsgguaranted);
//        System.out.println("netFirstUpdate 7 length=" + netmsgguaranted.dataLength());
    }

    private NetMsgSpawn netReplicateCoop(NetChannel netchannel) throws IOException {
        NetMsgSpawn netmsgspawn = super.netReplicate(netchannel);
        netmsgspawn.writeByte(1);
        this.netReplicateCommon(netchannel, netmsgspawn);
        this.netReplicateFirstUpdate(netchannel, netmsgspawn);
        return netmsgspawn;
    }

    private NetMsgSpawn _netReplicate(NetChannel netchannel) throws IOException {
        NetMsgSpawn netmsgspawn = super.netReplicate(netchannel);
        netmsgspawn.writeByte(0);
        netmsgspawn.writeByte(this.getArmy());
        this.netReplicateCommon(netchannel, netmsgspawn);
        this.netReplicateFirstUpdate(netchannel, netmsgspawn);

        return netmsgspawn;
    }

    public NetMsgSpawn netReplicate(NetChannel netchannel) throws IOException {
        if (Main.cur().netServerParams == null) return null;
        if (netchannel.isMirrored(this.net)) return null;
        NetMsgSpawn netmsgspawn;
        if (this.isCoopPlane()) netmsgspawn = this.netReplicateCoop(netchannel);
        else netmsgspawn = this._netReplicate(netchannel);
        if (World.getPlayerAircraft() == this && netchannel instanceof NetChannelOutStream) if (this.fmTrack() == null) {
            if (this.isNetMaster()) new MsgAction(true, this) {
                public void doAction(Object object_180_) {
                    new FlightModelTrack((Aircraft) object_180_);
                }
            };
        } else MsgNet.postRealNewChannel(this.fmTrack(), netchannel);

        return netmsgspawn;
    }

    public NetAircraft() {
        this.bCoopPlane = loadingCoopPlane;
    }

    public void setDamagerExclude(Actor actor) {
        this.damagerExclude = actor;
        if (this.damager_ == actor) this.damager_ = null;
    }

    public void setDamager(Actor actor) {
        this.setDamager(actor, 1);
    }

    public void setDamager(Actor actor, int i) {
        if (Actor.isValid(actor) && this != actor && i > 0) {
            if (i > 4) i = 4;
            this.damager_ = null;
            int i_181_ = this.damagers.size();
            for (int i_182_ = 0; i_182_ < i_181_; i_182_++) {
                DamagerItem damageritem = (DamagerItem) this.damagers.get(i_182_);
                if (damageritem.damager == actor) {
                    damageritem.damage += i;
                    damageritem.lastTime = Time.current();
                    return;
                }
            }
            this.damagers.add(new DamagerItem(actor, i));
            if (World.cur().isDebugFM()) {
                Aircraft.debugprintln(this, "Printing Registered Damagers: *****");
                for (int i_183_ = 0; i_183_ < i_181_; i_183_++) {
                    DamagerItem damageritem = (DamagerItem) this.damagers.get(i_183_);
                    if (Actor.isValid(damageritem.damager)) Aircraft.debugprintln(damageritem.damager, "inflicted " + damageritem.damage + " puntos..");
                }
            }
        }
    }

    public Actor getDamager() {
        if (Actor.isValid(this.damager_)) return this.damager_;
        this.damager_ = null;
        long l = 0L;
        Actor actor = null;
        long l_184_ = 0L;
        Actor actor_185_ = null;
        Actor actor_186_ = null;
        int i = this.damagers.size();
        for (int i_187_ = 0; i_187_ < i; i_187_++) {
            DamagerItem damageritem = (DamagerItem) this.damagers.get(i_187_);
            if (damageritem.damager != this.damagerExclude && Actor.isValid(damageritem.damager)) if (damageritem.damager instanceof Aircraft) {
                if (damageritem.lastTime > l_184_) {
                    l_184_ = damageritem.lastTime;
                    actor_185_ = damageritem.damager;
                }
            } else if (damageritem.damager == Engine.actorLand()) actor_186_ = damageritem.damager;
            else if (damageritem.lastTime > l) {
                l = damageritem.lastTime;
                actor = damageritem.damager;
            }
        }
        if (actor_185_ != null) this.damager_ = actor_185_;
        else if (actor != null) this.damager_ = actor;
        else if (actor_186_ != null) this.damager_ = actor_186_;
        return this.damager_;
    }

    public boolean isDamagerExclusive() {
        int i = 0;
        for (int i_188_ = 0; i_188_ < this.damagers.size(); i_188_++)
            if (this.damagerExclude != this.damagers.get(i_188_)) i++;
        return i == 1;
    }

    protected static void printDebug(Exception exception) {
        if (curDebugLevel() == 0) return;
        System.out.println(exception.getMessage());
        exception.printStackTrace();
    }

    public int getPilotsCount() {
        return this.FM.crew;
    }

    public void replicateDropExternalStores() {
        if (this.isNet() && this.net.countMirrors() != 0) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(15);
            this.net.post(netmsgguaranted);
        } catch (java.lang.Exception exception) {
            NetAircraft.printDebug(exception);
        }
    }

    public static boolean isOnCarrierDeck(AirportCarrier airportcarrier, Loc loc) {
        return isOnCarrierDeck(airportcarrier, loc.getPoint());
    }

    public static boolean isOnCarrierDeck(AirportCarrier airportcarrier, Point3d point3d) {
        Point3d point3d1 = new Point3d();
        Point3d point3d2 = new Point3d();
        point3d1.set(point3d);
        point3d2.set(point3d);
        point3d1.z = Engine.cur.land.HQ(point3d.x, point3d.y);
        point3d2.z = point3d1.z + 40D;
        Actor actor = Engine.collideEnv().getLine(point3d1, point3d2, false, clipFilter, pship);
        return airportcarrier.ship() == actor;
    }

    // TODO: |ZUTI| methods
    // ------------------------------------------------------------------
    public void restoreLinksInDogWing() {
        if (Main.cur().netServerParams == null) return;
        Wing wing = this.getWing();
        Aircraft aaircraft[] = wing.airc;
        int i;
        for (i = 0; i < aaircraft.length && !Actor.isValid(aaircraft[i]); i++)
            ;
        if (i == aaircraft.length) return;
        aaircraft[i].FM.Leader = null;
        for (int j = i + 1; j < aaircraft.length; j++)
            if (Actor.isValid(aaircraft[j])) {
                aaircraft[i].FM.Wingman = aaircraft[j].FM;
                aaircraft[j].FM.Leader = aaircraft[i].FM;
                i = j;
            }

    }

    // -------------------------------------------------------------------------

    // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View
    // Replication
    float viewAzimut  = 0.0F;
    float viewTangage = 0.0F;

    public void setHeadAngles(float f, float f1) {
//        System.out.println("setHeadAngles(" + f + ", " + f1 + ")");
        this.viewAzimut = f % 360F;
        this.viewTangage = f1;
        if (this.viewAzimut > 180F) this.viewAzimut -= 360F;
        if (this.viewAzimut < -180F) this.viewAzimut += 360F;
        if (this.viewAzimut < -107F) this.viewAzimut = -107F;
        if (this.viewAzimut > 107F) this.viewAzimut = 107F;
        if (this.viewTangage > 57F) this.viewTangage = 57F;
        if (this.viewTangage < -22F) this.viewTangage = -22F;
    }

    // ---
    // TODO: Storebror: +++ Bomb Release Bug hunting
    int                          iFMTrackMirror                = -1;

    private boolean[][]          triggerActive                 = { { false, false }, { false, false } };
    private boolean[]            lastBulletLogged              = { false, false };
    public static final int      TRIGGER_ROCKET                = 0;
    public static final int      TRIGGER_BOMB                  = 1;
    public static final int      TRIGGER_MASTER                = 0;
    public static final int      TRIGGER_MIRROR                = 1;

    private int[]                updatePending                 = { 0, 0 };
    public static final int      UPDATE_MASTER                 = 0;
    public static final int      UPDATE_MIRROR                 = 1;

    public static final String[] TRIGGER_NAMES                 = { "ROCKET", "BOMB" };
    public static final String[] UPDATE_NAMES                  = { "MASTER", "MIRROR" };

    private boolean              netPutWeaponsBitStatesPending = false;
    private boolean              netSpawnSetWeaponsBitStates   = false;
    private boolean              wctResetLogged                = false;

    private static int           debugLevel                    = Integer.MIN_VALUE;
    private static final int     DEBUG_DEFAULT                 = 0;

    public void setLastBulletLogged(int triggerType, boolean isLastBulletLogged) {
        this.lastBulletLogged[triggerType] = isLastBulletLogged;
    }

    public boolean isLastBulletLogged(int triggerType) {
        return this.lastBulletLogged[triggerType];
    }

    public void setTriggerActive(int triggerSource, int triggerType, boolean isActive) {
        this.triggerActive[triggerSource][triggerType] = isActive;
    }

    public boolean isTriggerActive(int triggerSource, int triggerType) {
        return this.triggerActive[triggerSource][triggerType];
    }

    public void setUpdatePending(int updateSource, int numPending) {
        this.updatePending[updateSource] = numPending;
    }

    public void incUpdatePending(int updateSource) {
        this.updatePending[updateSource]++;
//        printDebugMessage(this, "Updates Pending for " + UPDATE_NAMES[updateSource] + " = " + this.updatePending[updateSource]);
    }

    public void decUpdatePending(int updateSource) {
        this.updatePending[updateSource]--;
        if (this.updatePending[updateSource] < 0) this.updatePending[updateSource] = 0;
//        else
//            printDebugMessage(this, "Updates Pending for " + UPDATE_NAMES[updateSource] + " = " + this.updatePending[updateSource]);
    }

    public int getUpdatePending(int updateSource) {
        return this.updatePending[updateSource];
    }

    public boolean isUpdatePending(int updateSource) {
        return this.updatePending[updateSource] > 0;
    }

    private boolean hasBulletsLeftOnTrigger(int Trigger) {
        BulletEmitter[] be = this.FM.CT.Weapons[Trigger];
        if (be == null) return false;
        for (int i = 0; i < be.length; i++)
            if (be[i] != null && be[i].haveBullets()) return true;
        return false;
    }

    public boolean isNetSpawnSetWeaponsBitStates() {
        return this.netSpawnSetWeaponsBitStates;
    }

    public void setNetSpawnSetWeaponsBitStates(boolean netSpawnSetWeaponsBitStates) {
        this.netSpawnSetWeaponsBitStates = netSpawnSetWeaponsBitStates;
    }

    private static int curDebugLevel() {
        if (debugLevel == Integer.MIN_VALUE) debugLevel = Config.cur.ini.get("Mods", "DEBUG_NETAIRCRAFT", DEBUG_DEFAULT);
        return debugLevel;
    }

    public static void printDebugMessage(Actor actor, String theMessage) {
        if (curDebugLevel() == 0) return;
        if (!(actor instanceof NetAircraft)) {
            System.out.println("[DEBUG_NETAIRCRAFT] " + actor.name() + " (" + simpleClassName(actor) + ") " + theMessage);
            return;
        }
        NetAircraft netAircraft = (NetAircraft) actor;
        if (netAircraft.netUser() != null) System.out.println("[DEBUG_NETAIRCRAFT] " + netAircraft.netUser().uniqueName() + " (" + simpleClassName(actor) + ") " + theMessage);
        else System.out.println("[DEBUG_NETAIRCRAFT] " + netAircraft.name() + " (" + simpleClassName(actor) + ") " + theMessage);

    }

    public static void printDebug(String message) {
        if (curDebugLevel() == 0) return;
        Exception exception = new Exception(message);
        System.out.println(exception.getMessage());
        exception.printStackTrace();
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String toBinary(byte[] bytes) {
        String retVal = "";
        for (int i = 0; i < 8 * bytes.length; i++)
            retVal += "" + ((bytes[i / 8] << i % 8 & 0x80) == 0 ? '0' : '1');
        return retVal;
    }

    public static String toBinaryLittleEndian(byte[] bytes) {
        String retVal = "";
        for (int i = 0; i < 8 * bytes.length; i++)
            retVal += "" + ((bytes[i / 8] >> i % 8 & 0x01) == 0 ? '0' : '1');
        return retVal;
    }

    public static String simpleClassName(Object o) {
        String fullClassName = o.getClass().getName();
        return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
    }

    private void dumpWeaponsBitStates(byte[] is) {
        printDebugMessage(NetAircraft.this, "NetAircraft setWeaponsBitStates(" + bytesToHex(is) + " = " + toBinaryLittleEndian(is) + ") :");
        int i = 0;
        String bulletEmitterStates = "";
        for (int weaponsIndex = 0; weaponsIndex < this.FM.CT.Weapons.length; weaponsIndex++) {
            BulletEmitter[] bulletemitters = this.FM.CT.Weapons[weaponsIndex];
            if (bulletemitters != null) for (int beIndex = 0; beIndex < bulletemitters.length; beIndex++)
                if (bulletemitters[beIndex] != null) {
                    if (i > 0) bulletEmitterStates += ", ";
                    bulletEmitterStates += simpleClassName(bulletemitters[beIndex]) + "=" + ((is[i / 8] & 1 << i % 8) != 0);
                    i++;
                }
        }
        printDebugMessage(NetAircraft.this, bulletEmitterStates);
    }

    // TODO: Storebror: --- Bomb Release Bug hunting

}
