package com.maddox.il2.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Airport;
import com.maddox.il2.ai.Army;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.Chief;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.Front;
import com.maddox.il2.ai.Front.Marker;
import com.maddox.il2.ai.Regiment;
import com.maddox.il2.ai.Wing;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.ZutiTargetsSupportMethods;
import com.maddox.il2.ai.air.Point_Stay;
import com.maddox.il2.builder.ZutiSupportMethods_Builder;
import com.maddox.il2.builder.ZutiTypeFrontCarrier;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.IconDraw;
import com.maddox.il2.engine.ZutiSupportMethods_Engine;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.ZutiSupportMethods_FM;
import com.maddox.il2.gui.GUI;
import com.maddox.il2.gui.GUIBriefing.TargetPoint;
import com.maddox.il2.gui.GUIPad.AirDrome;
import com.maddox.il2.net.BornPlace;
import com.maddox.il2.net.NetUser;
import com.maddox.il2.net.ZutiSupportMethods_Net;
import com.maddox.il2.net.ZutiSupportMethods_NetSend;
import com.maddox.il2.objects.Statics.Block;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.air.NetAircraft.AircraftNet;
import com.maddox.il2.objects.air.Paratrooper;
import com.maddox.il2.objects.air.ZutiSupportMethods_Air;
import com.maddox.il2.objects.buildings.House;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.ShipGeneric;
import com.maddox.il2.objects.trains.Wagon;
import com.maddox.il2.objects.vehicles.artillery.ArtilleryGeneric;
import com.maddox.il2.objects.vehicles.cars.CarGeneric;
import com.maddox.il2.objects.vehicles.planes.PlaneGeneric;
import com.maddox.il2.objects.vehicles.stationary.StationaryGeneric;
import com.maddox.il2.objects.vehicles.tanks.TankGeneric;
import com.maddox.il2.objects.weapons.BombGun;
import com.maddox.il2.objects.weapons.Pylon;
import com.maddox.rts.LDRres;
import com.maddox.rts.NetEnv;
import com.maddox.rts.Property;
import com.maddox.rts.RTSConf;
import com.maddox.rts.SectFile;
import com.maddox.rts.Time;
import com.maddox.util.HashMapInt;
import com.maddox.util.HashMapIntEntry;
import com.maddox.util.NumberTokenizer;

public class ZutiSupportMethods {
    /**
     * Internal class that stores results for net places for users
     *
     * @author Zuti
     *
     */
    class ZutiNetPlaceSearchResult {
        public boolean wasNetPlaceFound = false;
        public int     foundNetPlace    = -1;

        public ZutiNetPlaceSearchResult(boolean found, int value) {
            this.wasNetPlaceFound = found;
            this.foundNetPlace = value;
        }
    }

    /**
     * Key = class name (only last bit, after last dot) Value = class i18n equivalent
     */
    private static Map AIRCRAFT_CLASS_FROM_I18N = null;
    /**
     * Key = class i18n equivalent Value = class name (only last bit, after last dot)
     */
    private static Map AIRCRAFT_I18N_FROM_CLASS = null;
//    private static boolean   AIRCRAFT_I18N_FROM_CLASS_INIT_DONE = false;

    private static String    ZUTI_LOADOUT_NONE         = "";
    private static String    ZUTI_LOADOUT_NULL         = "";
    public static int        ZUTI_KIA_COUNTER          = 0;
    public static boolean    ZUTI_KIA_DELAY_CLEARED    = false;
    public static List       ZUTI_PARA_CAPTURED_HOMEBASES;
    public static List       ZUTI_PENALIZED_USERS;
    private static BornPlace CURRENT_BORN_PLACE        = null;
    public static long       BASE_CAPRUTING_LAST_CHECK = 0;
    public static int        BASE_CAPTURING_INTERVAL   = 2000;
    public static boolean    IS_ACTING_INSTRUCTOR      = false;
    public static String[]   ZUTI_FUEL_TANK_OBJECTS    = { "Tank", "Barrel", "tank" };
    public static String[]   ZUTI_AMMO_BOX_OBJECTS     = { "Box" };
    public static String[]   ZUTI_WORKSHOP_OBJECTS     = { "Workshop", "HQ" };
    private static String[]  ZUTI_MOVING_RRR_OBJECTS   = { "Tanker",                  // Fuel cargo ship
            "Tramp",                                                                  // Dry cargo ship
            "Fuel", "fuel", "Cargo", "Column",                                        // used for identifying car columns
            "Equipment", "Train",                                                     // Trains in general...
    };

    /**
     * Key: acName, value: Paratrooper object
     */
    public static Map        AC_LAST_PARATROOPER       = new HashMap();

    /**
     * Reset class variables
     */
    public static void resetClassVariables() {
        ZutiSupportMethods.ZUTI_LOADOUT_NONE = "";
        ZutiSupportMethods.ZUTI_LOADOUT_NULL = "";
        ZutiSupportMethods.ZUTI_KIA_COUNTER = 0;
        ZutiSupportMethods.ZUTI_KIA_DELAY_CLEARED = false;
        ZutiSupportMethods.CURRENT_BORN_PLACE = null;
        ZutiSupportMethods.BASE_CAPRUTING_LAST_CHECK = 0;
        ZutiSupportMethods.BASE_CAPTURING_INTERVAL = 2000;
        ZutiSupportMethods.IS_ACTING_INSTRUCTOR = false;

        if (ZutiSupportMethods.ZUTI_PENALIZED_USERS == null) ZutiSupportMethods.ZUTI_PENALIZED_USERS = new ArrayList();
        ZutiSupportMethods.ZUTI_PENALIZED_USERS.clear();

        if (ZutiSupportMethods.ZUTI_PARA_CAPTURED_HOMEBASES == null) ZutiSupportMethods.ZUTI_PARA_CAPTURED_HOMEBASES = new ArrayList();
        ZutiSupportMethods.ZUTI_PARA_CAPTURED_HOMEBASES.clear();

        if (ZutiSupportMethods.AC_LAST_PARATROOPER != null) ZutiSupportMethods.AC_LAST_PARATROOPER.clear();
        ZutiSupportMethods.AC_LAST_PARATROOPER = new HashMap();
    }

    /**
     * Gets country from given net regiment string.
     *
     * @param netRegiment
     * @return
     */
    public static String getCountryFromNetRegiment(String netRegiment) {
        // System.out.println("Searching country for net regiment: " + netRegiment);
        java.util.ResourceBundle resCountry = java.util.ResourceBundle.getBundle("i18n/country", RTSConf.cur.locale, LDRres.loader());

        java.util.List list = Regiment.getAll();
        int k1 = list.size();
        for (int i = 0; i < k1; i++) {
            Regiment regiment = (Regiment) list.get(i);
            String branch = resCountry.getString(regiment.branch());
            if (regiment.name().equals(netRegiment)) return branch;
        }

        return "NONE";
    }

    /**
     * Gets user selected regiment from users config log.
     *
     * @param inRegiment
     * @return
     */
    public static String getUserCfgRegiment(String inRegiment) {
        java.util.ResourceBundle resCountry = java.util.ResourceBundle.getBundle("i18n/country", RTSConf.cur.locale, LDRres.loader());

        java.util.List list = Regiment.getAll();
        int k1 = list.size();
        for (int i = 0; i < k1; i++) {
            Regiment regiment = (Regiment) list.get(i);
            String branch = resCountry.getString(regiment.branch());
            if (branch.equals(inRegiment)) return regiment.name();
        }

        return "NoNe";
    }

    /**
     * Returns FIRST country that is specified for given born place
     *
     * @param bp
     * @return
     */
    public static String getHomeBaseFirstCountry(BornPlace bp) {
        ArrayList bpCountries = bp.zutiHomeBaseCountries;

        if (bpCountries == null || bpCountries.size() == 0) return "None";

        return (String) bpCountries.get(0);
    }

    /**
     * Returns true if given regiment is one of regiments for given born place.
     *
     * @param netRegiment
     * @param bp
     * @return
     */
    public static boolean isRegimentValidForSelectedHB(String netRegiment, BornPlace bp) {
        String currentCountry = getCountryFromNetRegiment(netRegiment);
        // System.out.println("GUIBriefing country for inserted regiment: " + currentCountry);

        if (bp.zutiHomeBaseCountries == null) return false;

        for (int i = 0; i < bp.zutiHomeBaseCountries.size(); i++) {
            String country = (String) bp.zutiHomeBaseCountries.get(i);
            if (country.equals(currentCountry)) return true;
        }

        return false;
    }

    /**
     * Returns player army based on different game modes!
     *
     * @return
     */
    public static int getPlayerArmy() {
        if (Mission.isDogfight()) return ((NetUser) NetEnv.host()).getArmy();
        else return World.getPlayerArmy();
    }

    /**
     * For given position this method returns closes target.
     *
     * @param playerPos
     * @param playerSameArmyAsTargets
     * @return
     */
    public static Point3d getNearestTarget(Point3d playerPos, boolean playerSameArmyAsTargets) {
        TargetPoint tp = null;
        Point3d outPoint3d = null;
        double min = 100000.0D;
        Iterator iterator = ZutiTargetsSupportMethods.ZUTI_TARGETPOINTS.iterator();
        while (iterator.hasNext()) {
            tp = (TargetPoint) iterator.next();

            if (playerSameArmyAsTargets && (tp.type == 0 || tp.type == 1 || tp.type == 2 || tp.type == 4)) {
                double tmpDistance = Math.sqrt(Math.pow(playerPos.x - tp.x, 2) + Math.pow(playerPos.y - tp.y, 2));

                if (tmpDistance < min) {
                    min = tmpDistance;
                    outPoint3d = new Point3d(tp.x, tp.y, 0.0D);
                }
            }

            if (!playerSameArmyAsTargets && (tp.type == 4 || tp.type == 5 || tp.type == 6 || tp.type == 7)) {
                double tmpDistance = Math.sqrt(Math.pow(playerPos.x - tp.x, 2) + Math.pow(playerPos.y - tp.y, 2));

                if (tmpDistance < min) {
                    min = tmpDistance;
                    outPoint3d = new Point3d(tp.x, tp.y, 0.0D);
                }
            }
        }

        return outPoint3d;
    }

    /**
     * Once home base is captured this methods loads new planeset for it.
     *
     * @param bp
     */
    public static void addNewPlanesToCapturedBase(BornPlace bp) {
        if (bp.army == 1 && bp.zutiCapturedAc_Red != null) {
            bp.airNames = ZutiSupportMethods.getAirNames(bp.zutiCapturedAc_Red);
            bp.zutiAircraft = bp.zutiCapturedAc_Red;

            // if no lies exist, don't allow new base to have all the planes, just add one, default plane for apropriate side
            if (bp.airNames.size() == 0) {
                bp.airNames.add(ZutiAircraft.DEFAULT_AIRCRAFT_RED_SIDE);

                if (bp.zutiAircraft == null) bp.zutiAircraft = new ArrayList();

                bp.zutiAircraft.add(ZutiSupportMethods_Air.createDefaultZutiAircraft(ZutiAircraft.DEFAULT_AIRCRAFT_RED_SIDE));
            }
        } else if (bp.army == 2 && bp.zutiCapturedAc_Blue != null) {
            bp.airNames = ZutiSupportMethods.getAirNames(bp.zutiCapturedAc_Blue);
            bp.zutiAircraft = bp.zutiCapturedAc_Blue;

            // if no lies exist, don't allow new base to have all the planes, just add one, default plane for apropriate side
            if (bp.airNames.size() == 0) {
                bp.airNames.add(ZutiAircraft.DEFAULT_AIRCRAFT_BLUE_SIDE);

                if (bp.zutiAircraft == null) bp.zutiAircraft = new ArrayList();

                bp.zutiAircraft.add(ZutiSupportMethods_Air.createDefaultZutiAircraft(ZutiAircraft.DEFAULT_AIRCRAFT_BLUE_SIDE));
            }
        }
    }

    /**
     * This method extracts aircraft names from objects in specified list
     *
     * @param zacList
     * @return
     */
    public static ArrayList getAirNames(ArrayList zacList) {
        ArrayList acNames = new ArrayList();

        if (zacList == null) return acNames;

        for (int i = 0; i < zacList.size(); i++) {
            ZutiAircraft zac = (ZutiAircraft) zacList.get(i);
            acNames.add(zac.getAcName());
        }

        return acNames;
    }

    /**
     * Get aircraft list from mission line
     *
     * @param line
     * @return
     */
    public static ArrayList parseCapturedBaseAircrafts(String line) {
        ArrayList list = new ArrayList();
        int securityBreak = 0;
        // System.out.println(line);

        if (line == null) return list;

        while (true) {
            securityBreak++;

            if (line.indexOf(" ") > 0) {
                String lineFirstPoint = line.substring(0, line.indexOf(" ")).trim();
                line = line.substring(line.indexOf(" ") + 1, line.length()).trim();

                if (lineFirstPoint.length() > 0) list.add(lineFirstPoint);
            } else {
                if (line.length() > 0) list.add(line);
                break;
            }

            // Just to make sure nothing goes wrong and we have an infinity loop on our hands :S
            if (securityBreak > 500) break;
        }

        return list;
    }

    /**
     * Returns true if given coordinate is on area that is under control by different army than the one that is specified as a parameter to this method.
     *
     * @param army
     * @param x
     * @param y
     * @return
     */
    public static boolean isCoordinateOnCapturedTerritory(int army, double x, double y) {
        // System.out.println("BP army: " + new Integer(i).toString() + ", location: " + new Double(d).toString() + ", " + new Double(d_1_).toString());
        if (army == 0) return false;

        int areaArmy = Front.army(x, y);

        if (areaArmy == 0) return false;
        if (army == areaArmy) return false;

        return true;
    }

    /**
     * This method assigns imputed marker to nearest ship around it. If marker was assigned to a ship, true is returned. Else false.
     *
     * @param marker
     * @return
     */
    public static boolean searchNearestFriendlyShip(Marker marker) {
        // System.out.println("Searching for nearest friendly ship to forward the front marker...");
        ArrayList actors = Main.cur().mission.actors;
        int size = actors.size();
        double min = ZutiSupportMethods_Builder.FRONT_MARKER_RADIUS * ZutiSupportMethods_Builder.FRONT_MARKER_RADIUS_SHIP_MULTI;
        boolean assigned = false;
        BigshipGeneric nearest = null;
        Actor actor = null;
        BigshipGeneric ship = null;
        Point3d point3d = null;

        try {
            for (int i = 0; i < size; i++) {
                actor = (Actor) actors.get(i);

                if (ZutiSupportMethods.isStaticActor(actor)) continue;

                if (actor instanceof BigshipGeneric && actor instanceof ZutiTypeFrontCarrier) {
                    if (actor.getArmy() == 2) continue;
                    ship = (BigshipGeneric) actor;
                    /*
                     * //If ship is dead or of different army, continue System.out.println("Ship: " + ship.toString()); System.out.println("Ship - isAlive " + ship.isAlive()); System.out.println("Ship - zutiHasFrontMarkerAssigned: " +
                     * ship.zutiHasFrontMarkerAssigned()); System.out.println("Ship - getArmy(): " + new Integer(ship.getArmy()).toString()); System.out.println("Marker - getArmy(): " + new Integer(marker.army).toString());
                     * System.out.println("Ship - zutiIsShipChief: " + ship.zutiIsShipChief()); System.out.println("====================================================================");
                     */

                    if (ship.zutiHasFrontMarkerAssigned() || ship.zutiGetDying() != 0 || ship.getArmy() != marker.army || !ship.zutiIsShipChief()) continue;

                    point3d = actor.pos.getAbsPoint();

                    double tmpDistance = Math.pow(marker.x - point3d.x, 2) + Math.pow(marker.y - point3d.y, 2);
                    // System.out.println("Distance: " + tmpDistance + " vs " + min);
                    if (tmpDistance < min) {
                        min = tmpDistance;
                        nearest = ship;
                    }
                }
            }

            if (nearest != null) {
                // System.out.println("Nearest front marker carrier found: " + nearest);
                marker.zutiMarkerCarrierName = nearest.name();
                nearest.zutiAddFrontMarker(marker);
                assigned = true;
            }

        } catch (Exception ex) {}

        // System.out.println("===============Assigned: " + assigned + "====================");
        return assigned;
    }

    /**
     * This method assigns imputed marker to nearest tank around it. If marker was assigned to a tank, true is returned. Else false.
     *
     * @param marker
     * @return
     */
    public static boolean searchNearestFriendlyTank(Marker marker) {
        ArrayList actors = Main.cur().mission.actors;
        int size = actors.size();
        double min = ZutiSupportMethods_Builder.FRONT_MARKER_RADIUS;
        boolean assigned = false;
        TankGeneric nearest = null;
        Actor actor = null;
        TankGeneric tank = null;
        Point3d point3d = null;

        try {
            for (int i = 0; i < size; i++) {
                actor = (Actor) actors.get(i);

                if (ZutiSupportMethods.isStaticActor(actor)) continue;

                if (actor instanceof TankGeneric) {
                    tank = (TankGeneric) actor;
                    // If tank is dead or of different army, continue
                    if (tank.zutiHasFrontMarkerAssigned() || tank.dying != 0 || tank.getArmy() != marker.army) continue;

                    point3d = actor.pos.getAbsPoint();

                    double tmpDistance = Math.pow(marker.x - point3d.x, 2) + Math.pow(marker.y - point3d.y, 2);
                    // System.out.println("Tank: " + tank.toString() + ", " + new Double(tmpDistance).toString() + " vs " + new Double(min).toString());
                    if (tmpDistance < min) {
                        min = tmpDistance;
                        nearest = tank;
                    }
                }
            }

            if (nearest != null) {
                marker.zutiMarkerCarrierName = nearest.name();
                nearest.zutiAddFrontMarker(marker);
                assigned = true;
            }
        } catch (Exception ex) {}

        return assigned;
    }

    /**
     * This method assigns imputed marker to nearest artillery around it. If marker was assigned to a artillery, true is returned. Else false.
     *
     * @param marker
     * @return
     */
    public static boolean searchNearestFriendlyArtillery(Marker marker) {
        ArrayList actors = Main.cur().mission.actors;
        int size = actors.size();
        double min = ZutiSupportMethods_Builder.FRONT_MARKER_RADIUS;
        boolean assigned = false;
        ArtilleryGeneric nearest = null;
        Actor actor = null;
        ArtilleryGeneric artillery = null;
        Point3d point3d = null;

        try {
            for (int i = 0; i < size; i++) {
                actor = (Actor) actors.get(i);

                if (ZutiSupportMethods.isStaticActor(actor)) continue;

                if (actor instanceof ArtilleryGeneric) {
                    artillery = (ArtilleryGeneric) actor;
                    // If tank is dead or of different army, continue
                    if (artillery.zutiHasFrontMarkerAssigned() || artillery.dying != 0 || artillery.getArmy() != marker.army) continue;

                    point3d = actor.pos.getAbsPoint();

                    double tmpDistance = Math.pow(marker.x - point3d.x, 2) + Math.pow(marker.y - point3d.y, 2);
                    // System.out.println("Tank: " + tank.toString() + ", " + new Double(tmpDistance).toString() + " vs " + new Double(min).toString());
                    if (tmpDistance < min) {
                        min = tmpDistance;
                        nearest = artillery;
                    }
                }
            }

            if (nearest != null) {
                marker.zutiMarkerCarrierName = nearest.name();
                nearest.zutiAddFrontMarker(marker);
                assigned = true;
            }
        } catch (Exception ex) {}

        return assigned;
    }

    /**
     * This method returns those Markers that are inside circle area defined by x and y and r coordinates.
     *
     * @param x     X location coordinate
     * @param y     Y location coordinate
     * @param r     Radius
     * @param color Army color of which markers we are trying to locate
     * @return
     */
    public static List getMarkersAtCoordinates(double x, double y, double r, int color) {
        List markers = new ArrayList();

        double minDistance = r * r;
        List frontMarkers = Front.markers();
        int sizeMarkers = frontMarkers.size();
        Marker marker = null;

        for (int i = 0; i < sizeMarkers; i++) {
            marker = (Marker) frontMarkers.get(i);
            // System.out.println("Markers comparison: " + marker.army + " vs " + bp.army);
            if (marker.army == color) {
                double tmpDistance = Math.pow(marker.x - x, 2) + Math.pow(marker.y - y, 2);
                if (tmpDistance < minDistance) // System.out.println("Adding marker army: " + marker.army + " to special array!");
                    markers.add(marker);
            }
        }

        return markers;
    }

    /**
     * This method returns those Markers that are inside circle area defined by x and y and r coordinates.
     *
     * @param x     X location coordinate
     * @param y     Y location coordinate
     * @param r     Radius
     * @param color Army color of which markers we do not want
     * @return
     */
    public static List getMarkersAtCoordinates_ExcludeArmy(double x, double y, double r, int color) {
        List markers = new ArrayList();

        double minDistance = r * r;
        List frontMarkers = Front.markers();
        int sizeMarkers = frontMarkers.size();
        Marker marker = null;

        for (int i = 0; i < sizeMarkers; i++) {
            marker = (Marker) frontMarkers.get(i);
            // System.out.println("Markers comparison: " + marker.army + " vs " + bp.army);
            if (marker.army != color) {
                double tmpDistance = Math.pow(marker.x - x, 2) + Math.pow(marker.y - y, 2);
                if (tmpDistance < minDistance) // System.out.println("Adding marker army: " + marker.army + " to special array!");
                    markers.add(marker);
            }
        }

        return markers;
    }

    /**
     * Method inserts new front market at selected coordinates with selected army
     *
     * @param x
     * @param y
     * @param army
     */
    public static void putMarkerAtCoordinates(double x, double y, int army) {
        Marker marker = new Marker();
        marker.army = army;
        marker.x = x;
        marker.y = y;

        Front.markers().add(marker);
    }

    /**
     * Remove markers that are in specified array.
     *
     * @param markers
     */
    public static void removeMarkers(List markers) {
        if (markers == null) return;

        try {
            for (int i = 0; i < markers.size(); i++)
                Front.markers().remove(markers.get(i));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method goes over all born places and checks if front line has moved over their center point and were captured by opposing army.
     */
    public static void checkIfAnyBornPlaceWasOverrun() {
        // System.out.println("Checking overruning... : " + Time.current());

        ArrayList bornPlaces = World.cur().bornPlaces;
        List frontMarkers = Front.markers();

        if (!ZutiMDSVariables.ZUTI_FRONT_ENABLE_HB_CAPTURING || bornPlaces == null || frontMarkers == null) return;

        BornPlace bp = null;
        Marker closestMarker = null;
        ArrayList bornPlaceMarkers = null;
        Marker marker = null;
        int size = bornPlaces.size();

        for (int i = 0; i < size; i++) {
            bp = (BornPlace) bornPlaces.get(i);

            // First, check if born place is colored different from Red or Blue. It it is, check if it is overrun
            if (bp.zutiCanThisHomeBaseBeCaptured && (bp.army <= 0 || bp.army > 2)) {
                // OK, we have such born place, check if any flag carrier from red/blue army is inside it's radius
                // if it is, overrun it!
                int sizeMarkers = frontMarkers.size();
                closestMarker = null;
                double minDistance = bp.r * bp.r;
                bornPlaceMarkers = new ArrayList();

                for (int x = 0; x < sizeMarkers; x++) {
                    marker = (Marker) frontMarkers.get(x);
                    // System.out.println("Markers comparison: " + marker.army + " vs " + bp.army);
                    if (closestMarker == null && marker.army == 1 || marker.army == 2) {
                        double tmpDistance = Math.pow(marker.x - bp.place.x, 2) + Math.pow(marker.y - bp.place.y, 2);
                        if (tmpDistance < minDistance) {
                            closestMarker = marker;
                            minDistance = tmpDistance;
                        }
                    } else if (marker.army == bp.army) {
                        double tmpDistance = Math.pow(marker.x - bp.place.x, 2) + Math.pow(marker.y - bp.place.y, 2);
                        if (tmpDistance < minDistance) // System.out.println("Adding marker army: " + marker.army + " to special array!");
                            bornPlaceMarkers.add(marker);
                    }
                }

                if (closestMarker != null) {
                    // Opposing army is inside born place, remove all markers inside that home base that are of different
                    // color than red or blue. By doing this, we will automatically overrun it. Also, change it's "no spawning"
                    // attribute to allow spawning from now on!
                    ZutiSupportMethods.removeMarkers(bornPlaceMarkers);

                    bp.zutiDisableSpawning = false;
                }
            }

            // Ignore born places that are positioned on carriers - zutiBpStayPoints are not null
            // only on carriers. Since we can't capture those, we still must count them as born place
            if (bp.zutiBpStayPoints != null) continue;

            // System.out.println("BP Army: " + new Integer(bp.army).toString());
            if (bp.zutiCanThisHomeBaseBeCaptured && ZutiSupportMethods.isCoordinateOnCapturedTerritory(bp.army, bp.place.x, bp.place.y)) executeCodeSequenceAfterBornPlaceWasOverrun(bp);
        }
    }

    /**
     * Method checks if there are any chiefs (tank, artillery, ship) inside home base that corresponds to specified army.
     *
     * @param x
     * @param y
     * @param R:        r*r
     * @param chiefArmy
     * @return
     */
    private static boolean isChiefInsideHomeBase(double x, double y, double R, int chiefArmy) {
        List actors = Mission.cur().actors;

        Actor actor = null;
        Point3d position = null;
        for (int i = 0; i < actors.size(); i++) {
            actor = (Actor) actors.get(i);

            if (!actor.isAlive()) continue;

            if (actor.getArmy() != chiefArmy) continue;

            if (ZutiSupportMethods.isStaticActor(actor)) continue;

            if (actor instanceof TankGeneric || actor instanceof ArtilleryGeneric || actor instanceof BigshipGeneric) {
                position = actor.pos.getAbsPoint();
                double tmpDistance = Math.pow(x - position.x, 2) + Math.pow(y - position.y, 2);
                if (tmpDistance <= R) {
                    System.out.println("Chief that is defending home base at T(" + Math.round(x) + ", " + Math.round(y) + ") is: " + actor.toString());
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Call this method once specified born place was identified as being overrun! After it completes method also executes isMDSObjectivesCompleted() method.
     *
     * @param bp
     */
    private static void executeCodeSequenceAfterBornPlaceWasOverrun(BornPlace bp) {
        int newArmy = Front.army(bp.place.x, bp.place.y);

        if (bp.zutiCaptureOnlyIfNoChiefPresent && isChiefInsideHomeBase(bp.place.x, bp.place.y, bp.r * bp.r, bp.army)) {
            System.out.println("There chiefs protecting home base... No capture possible!");
            return;
        }

        // Reset users that were stationed on captured home base
        ZutiSupportMethods.disconnectPilotsFromBornPlace(bp);
        // Set born place new army
        bp.army = newArmy;
        // Log that home base was captured
        EventLog.type(true, "Bornplace" + bp.zutiBpIndex + " overrun by army " + bp.army + " at " + bp.place.x + " " + bp.place.y);

        // Search for target areas in home base circle and convert them to new army
        ZutiTargetsSupportMethods.changeTargetSide(bp);

        // Change airdrome army and color so GUI pad will show them correctly
        ZutiSupportMethods.changeUnderlyingAirportsArmyAndColor(bp);

        // Add new planes to this captured base
        addNewPlanesToCapturedBase(bp);

        // Reset that born place country selection
        ZutiSupportMethods_Net.loadBornPlaceCapturedCountries(bp);

        // Convert all static actors near that bornPlace to it's new Army
        // if( (Mission.isSingle() || Mission.isServer()) && Main.cur().mission.actors != null )
        if (Main.cur().mission.actors != null) {
            int size = Main.cur().mission.actors.size();
            double d = bp.r * bp.r;
            Actor actor = null;
            Point3d point3d = null;
            for (int i = 0; i < size; i++) {
                actor = (Actor) Main.cur().mission.actors.get(i);

                if (actor == null || actor.getArmy() == bp.army) continue;

                // Changed that this will convert only static actors to appropriate army
                if (Actor.isValid(actor) && actor.pos != null && ZutiSupportMethods.isStaticActor(actor)) {
                    point3d = actor.pos.getAbsPoint();
                    double distance = (point3d.x - bp.place.x) * (point3d.x - bp.place.x) + (point3d.y - bp.place.y) * (point3d.y - bp.place.y);
                    if (distance <= d) // System.out.println("Actor army before: " + actor.getArmy());
                        actor.setArmy(bp.army);
                    // System.out.println("Actor that was converted: " + actor.toString() + ", army: " + actor.getArmy());
                }
            }
        }

        if (ZutiSupportMethods.isMDSObjectivesCompleted(1)) ZutiSupportMethods.doMissionComplete(1);
        else if (ZutiSupportMethods.isMDSObjectivesCompleted(2)) ZutiSupportMethods.doMissionComplete(2);

        Front.setMarkersChanged();
    }

    /**
     * Removes given front marker if it can not be reassigned to new unit.
     *
     * @param marker
     * @param ownerType: 0=ship, 1=tank, 2=artillery
     */
    public static void removeFrontMarker(Marker marker, int ownerType) {
        boolean markerReasigned = false;

        switch (ownerType) {
            case 0:
                // BigshipGeneric type
                markerReasigned = searchNearestFriendlyShip(marker);
                break;
            case 1:
                // TankGeneric type
                markerReasigned = searchNearestFriendlyTank(marker);
                if (!markerReasigned) markerReasigned = searchNearestFriendlyArtillery(marker);
                break;
            case 2:
                // Artillery type
                markerReasigned = searchNearestFriendlyArtillery(marker);
                if (!markerReasigned) markerReasigned = searchNearestFriendlyTank(marker);
                break;
            default:
                break;
        }

        // remove this front marker from global list
        if (!markerReasigned) {
            printDebugMessage("Markers before removing: " + Front.markers().size());
            Front.markers().remove(marker);
            printDebugMessage("Markers after removing: " + Front.markers().size());
            Front.setMarkersChanged();
            marker = null;
        }
    }

    /**
     * Method searches for airport at specified coordinates.
     *
     * @param x
     * @param y
     * @return
     */
    public static Airport getAirport(double x, double y) {
        ArrayList list = new ArrayList();
        World.getAirports(list);

        double minDistance = 1000000.0D;
        Airport result = null;

        try {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                Airport ap = (Airport) list.get(i);
                Point3d point3d = ap.pos.getAbsPoint();

                double tmpDistance = Math.sqrt(Math.pow(x - point3d.x, 2) + Math.pow(y - point3d.y, 2));
                if (tmpDistance < minDistance) {
                    minDistance = tmpDistance;
                    result = ap;
                }
            }
        } catch (Exception ex) {}

        return result;
    }

    /**
     * Returns player born place. If that one is not his anymore (overrun...), method searches for nearest one based on specified position and army.
     *
     * @param playerPos
     * @param army
     * @return
     */
    public static BornPlace getPlayerBornPlace(Point3d playerPos, int army) {
        NetUser netuser = (NetUser) NetEnv.host();
        int index = netuser.getBornPlace();
        BornPlace bp = (BornPlace) World.cur().bornPlaces.get(index);

        if (playerPos == null) return bp;

        if (index == -1) bp = ZutiSupportMethods_Net.getNearestBornPlace(playerPos.x, playerPos.y, army);

        return bp;
    }

    /**
     * Returns loadout name for specified aircraft and index of loaded loadout.
     *
     * @param zutiAircrafts
     * @param acName
     * @param loadout
     * @param I18NName
     * @return
     */
    public static String getACSelectedLoadoutName(List zutiAircrafts, String acName, int loadout, boolean I18NName) {
        try {
            if (zutiAircrafts == null) return ZUTI_LOADOUT_NONE;

            int size = zutiAircrafts.size();
            for (int i = 0; i < size; i++) {
                ZutiAircraft zac = (ZutiAircraft) zutiAircrafts.get(i);
                if (zac.getAcName().equals(acName)) {
                    String result = zac.getLoadoutById(loadout);
                    if (result != null) {
                        if (I18NName) return getWeaponI18NName(zac.getAcName(), result);
                        return result;
                    } else return ZUTI_LOADOUT_NULL;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ZUTI_LOADOUT_NONE;
    }

    /**
     * Method returns loadout name for specified aircraft based on born place that pilot landed on. Some born places might not offer same loadouts as pilot selected on his TO born place.
     *
     * @param bp
     * @param acName
     * @param posX
     * @param posY
     * @param loadoutId
     * @param pilotArmy
     * @param I18NName
     * @return
     */
    public static String getSelectedLoadoutForAircraft(BornPlace bp, String acName, double posX, double posY, int loadoutId, int pilotArmy, boolean I18NName) {
        String enemySide = "We, THE ENEMY, will only shoot you in the head!!!!";
        try {
            if (bp == null && Mission.MDS_VARIABLES().zutiReload_OnlyHomeBaseSpecificLoadouts) bp = ZutiSupportMethods_Net.getNearestBornPlace_AnyArmy(posX, posY);
            else if (bp != null && !bp.zutiOnlyHomeBaseSpecificLoadouts) {
                NetAircraft playerAc = World.getPlayerAircraft();
                bp = (BornPlace) World.cur().bornPlaces.get(((AircraftNet) playerAc.net).netUser.getBornPlace());
                /*
                 * NetUser netUser = ((AircraftNet) playerAc.net).netUser; System.out.println(((Aircraft)playerAc).getClass()); System.out.println("NetAircraft spawning player name : " + netUser.uniqueName());
                 * System.out.println("NetAircraft spawning player bp id: " + netUser.getBornPlace()); String currentAcName = Property.stringValue(((Aircraft)playerAc).getClass(), "keyName");
                 * System.out.println("NetAircraft spawning player plane: " + currentAcName); int bpId = netUser.getBornPlace(); System.out.println("BP ID = " + bpId);
                 *
                 * BornPlace zutiPilotsBornPlace = (BornPlace)World.cur().bornPlaces.get( bpId ); if( zutiPilotsBornPlace != null ) { System.out.println("Letali��e obstaja..."); }
                 */
            }

            if (bp != null) {
                // If pilot landed on enemy side of the lines... let him now that :)
                if (ZutiSupportMethods.isCoordinateOnCapturedTerritory(pilotArmy, posX, posY)) return enemySide;

                if (bp.army != pilotArmy) return enemySide;

                return ZutiSupportMethods.getACSelectedLoadoutName(bp.zutiAircraft, acName, loadoutId, I18NName);
            } else // Well, no airports/home bases are nearby. Check if he at least landed on a friendly side of the lines
                if (ZutiSupportMethods.isCoordinateOnCapturedTerritory(pilotArmy, posX, posY)) return enemySide;
                else return ZUTI_LOADOUT_NONE;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ZUTI_LOADOUT_NONE;
    }

    /**
     * Get net user by his user name. Null is returned if user is not found.
     *
     * @param username
     */
    public static NetUser getNetUser(String username) {
        List list = NetEnv.hosts();
        NetUser netuser = null;

        netuser = (NetUser) NetEnv.host();
        if (netuser.uniqueName().equals(username)) return netuser;

        for (int i = 0; i < list.size(); i++) {
            netuser = (NetUser) list.get(i);

            if (netuser.uniqueName().equals(username)) return netuser;
        }

        return null;
    }

    /**
     * Set current player re-fly duration.
     *
     * @param username
     * @param IP
     * @param penalty
     */
    public static void setUserTimePenalty(String username, String IP, long penalty) {
        ZutiBannedUser bannedUser = null;
        ZutiBannedUser tmpUsr = null;

        if (ZutiSupportMethods.ZUTI_PENALIZED_USERS != null) {
            int size = ZutiSupportMethods.ZUTI_PENALIZED_USERS.size();
            for (int i = 0; i < size; i++) {
                tmpUsr = (ZutiBannedUser) ZutiSupportMethods.ZUTI_PENALIZED_USERS.get(i);
                if (tmpUsr.isMatch(username, IP)) {
                    bannedUser = tmpUsr;
                    break;
                }
            }
        }

        penalty = Time.current() + penalty * 1000;

        if (bannedUser == null) {

            bannedUser = new ZutiBannedUser();
            bannedUser.setName(username);
            bannedUser.setIP(IP);
            bannedUser.setDuration(penalty);

            ZutiSupportMethods.ZUTI_PENALIZED_USERS.add(bannedUser);
        } else bannedUser.setDuration(penalty);

        // System.out.println("ZutiSupportMethods - setUserTimePenalty: IP=" + IP + ", name= " + username + ", time penalty=" + penalty);
    }

    /**
     * Check if user has any pending re-fly penalties or not.
     *
     * @param name User name.
     * @param IP   User IP
     * @return True if he is, false if he isn't.
     */
    public static boolean isUserPenalized(String name, String IP) {
        if (Main.cur().mission == null) return false;

        if (!Mission.MDS_VARIABLES().zutiMisc_EnableReflyOnlyIfBailedOrDied) return false;

        ZutiBannedUser bannedUser = null;
        if (ZutiSupportMethods.ZUTI_PENALIZED_USERS != null) {
            int size = ZutiSupportMethods.ZUTI_PENALIZED_USERS.size();
            for (int i = 0; i < size; i++) {
                ZutiBannedUser tmpUsr = (ZutiBannedUser) ZutiSupportMethods.ZUTI_PENALIZED_USERS.get(i);
                if (tmpUsr.isMatch(name, IP)) {
                    bannedUser = tmpUsr;
                    break;
                }
            }
        }
        // User exists, well well, do we allow him to join the game or not?
        if (bannedUser != null && bannedUser.isBanned()) return true;

        return false;
    }

    /**
     * Check status of objectives enforced by MDS. Do this each time when actor died.
     *
     * @param actor
     */
    public static void updateMDSObjectives(Actor actor) {
        // Attention: if object army is 1 this object is deducted from objects in army 2! Because
        // here we are setting "scores" regarding each army objectives. For instance: Blue team has to
        // destroy 3 stationary objects. That means that they have to destroy 3 RED stationary objects.
        // And when such object is destroyed, BLUE counter should be deducted for 1.
        if (actor instanceof NetAircraft) {
            // System.out.println("World - Live AC Destroyed: " + actor.name() + ", army: " + actor.getArmy());
            if (actor.getArmy() == 2) Mission.MDS_VARIABLES().zutiObjectives_redAircraftsM--;
            if (actor.getArmy() == 1) Mission.MDS_VARIABLES().zutiObjectives_blueAircraftsM--;

        }
        if (actor instanceof PlaneGeneric) {
            // System.out.println("World - Stationary AC Destroyed: " + actor.name() + ", army: " + actor.getArmy());
            if (actor.getArmy() == 2) Mission.MDS_VARIABLES().zutiObjectives_redAircraftsS--;
            if (actor.getArmy() == 1) Mission.MDS_VARIABLES().zutiObjectives_blueAircraftsS--;

        } else if (actor instanceof BigshipGeneric || actor instanceof ShipGeneric) {
            if (actor.name().indexOf("Static") > 0) {
                // System.out.println("World - Stationary Ship Destroyed: " + actor.name() + ", army: " + actor.getArmy() + ", " + actor.getClass().getSuperclass());

                if (actor.getArmy() == 2) Mission.MDS_VARIABLES().zutiObjectives_redShipsS--;
                if (actor.getArmy() == 1) Mission.MDS_VARIABLES().zutiObjectives_blueShipsS--;
            } else {
                // System.out.println("World - AI Ship Destroyed: " + actor.name() + ", army: " + actor.getArmy() + ", " + actor.getClass().getSuperclass());

                if (actor.getArmy() == 2) Mission.MDS_VARIABLES().zutiObjectives_redShipsM--;
                if (actor.getArmy() == 1) Mission.MDS_VARIABLES().zutiObjectives_blueShipsM--;
            }
        } else if (actor instanceof CarGeneric) {
            // System.out.println("World - Car Destroyed: " + actor.name() + ", army: " + actor.getArmy());

            if (actor.getArmy() == 2) Mission.MDS_VARIABLES().zutiObjectives_redVehicles--;
            if (actor.getArmy() == 1) Mission.MDS_VARIABLES().zutiObjectives_blueVehicles--;
        } else if (actor instanceof Wagon) {
            // System.out.println("World - Train or it's part Destroyed: " + actor.name() + ", army: " + actor.getArmy());

            if (actor.getArmy() == 2) Mission.MDS_VARIABLES().zutiObjectives_redTrains--;
            if (actor.getArmy() == 1) Mission.MDS_VARIABLES().zutiObjectives_blueTrains--;
        } else if (actor instanceof ArtilleryGeneric) {
            // System.out.println("World - Artillery Destroyed: " + actor.name() + ", army: " + actor.getArmy());

            if (actor.getArmy() == 2) Mission.MDS_VARIABLES().zutiObjectives_redArtillery--;
            if (actor.getArmy() == 1) Mission.MDS_VARIABLES().zutiObjectives_blueArtillery--;
        } else if (actor instanceof StationaryGeneric) {
            // System.out.println("World - Stationary Destroyed: " + actor.name() + ", army: " + actor.getArmy());

            if (actor.getArmy() == 2) Mission.MDS_VARIABLES().zutiObjectives_redStationary--;
            if (actor.getArmy() == 1) Mission.MDS_VARIABLES().zutiObjectives_blueStationary--;
        } else if (actor instanceof TankGeneric) {
            // System.out.println("World - Armor Destroyed: " + actor.name() + ", army: " + actor.getArmy());
            if (actor.toString().indexOf("Infantry") > 1) {
                if (actor.getArmy() == 2) Mission.MDS_VARIABLES().zutiObjectives_redInfantry--;
                if (actor.getArmy() == 1) Mission.MDS_VARIABLES().zutiObjectives_blueInfantry--;
            } else {
                if (actor.getArmy() == 2) Mission.MDS_VARIABLES().zutiObjectives_redArmor--;
                if (actor.getArmy() == 1) Mission.MDS_VARIABLES().zutiObjectives_blueArmor--;
            }
        } else {
            // System.out.println("World - Something destroyed...: " + actor.name() + ", army: " + actor.getArmy() + ", " + actor.getClass() + ", " + actor.getClass().getSuperclass());
        }
    }

    /**
     * Check if selected side completed MDS enforced objectives.
     *
     * @param army
     * @return
     */
    public static boolean isMDSObjectivesCompleted(int army) {
        // System.out.println("ZutiSupportMethods - isMDSObjectivesCompleted: " + Time.current());
        boolean result = false;

        // If MDS objectives are not enabled for given army, return false
        if (army == 1 && !Mission.MDS_VARIABLES().zutiObjectives_enableRed || army == 2 && !Mission.MDS_VARIABLES().zutiObjectives_enableBlue) return false;

        // army 1 won if army 2 units are 0 or less! :D Very important this logic is!
        switch (army) {
            case 1:

                if (Mission.MDS_VARIABLES().zutiObjectives_redShipsS <= 0 && Mission.MDS_VARIABLES().zutiObjectives_redShipsM <= 0 && Mission.MDS_VARIABLES().zutiObjectives_redArtillery <= 0 && Mission.MDS_VARIABLES().zutiObjectives_redStationary <= 0
                        && Mission.MDS_VARIABLES().zutiObjectives_redArmor <= 0 && Mission.MDS_VARIABLES().zutiObjectives_redVehicles <= 0 && Mission.MDS_VARIABLES().zutiObjectives_redTrains <= 0
                        && Mission.MDS_VARIABLES().zutiObjectives_redAircraftsS <= 0 && Mission.MDS_VARIABLES().zutiObjectives_redAircraftsM <= 0 && Mission.MDS_VARIABLES().zutiObjectives_redRockets <= 0
                        && Mission.MDS_VARIABLES().zutiObjectives_redInfantry <= 0 && Mission.MDS_VARIABLES().zutiObjectives_redHomeBases <= 0)
                    result = true;

                break;
            case 2:

                if (Mission.MDS_VARIABLES().zutiObjectives_blueShipsS <= 0 && Mission.MDS_VARIABLES().zutiObjectives_blueShipsM <= 0 && Mission.MDS_VARIABLES().zutiObjectives_blueArtillery <= 0 && Mission.MDS_VARIABLES().zutiObjectives_blueStationary <= 0
                        && Mission.MDS_VARIABLES().zutiObjectives_blueArmor <= 0 && Mission.MDS_VARIABLES().zutiObjectives_blueVehicles <= 0 && Mission.MDS_VARIABLES().zutiObjectives_blueTrains <= 0
                        && Mission.MDS_VARIABLES().zutiObjectives_blueAircraftsS <= 0 && Mission.MDS_VARIABLES().zutiObjectives_blueAircraftsM <= 0 && Mission.MDS_VARIABLES().zutiObjectives_blueRockets <= 0
                        && Mission.MDS_VARIABLES().zutiObjectives_blueInfantry <= 0 && Mission.MDS_VARIABLES().zutiObjectives_blueHomeBases <= 0)
                    result = true;
                break;
        }

        return result;
    }

    /**
     * Method returns number of objects found in area defined by given coordinates and radius. Objects are checked in map actors.static file, not mission file!
     *
     * @param inPoint
     * @param radius
     * @return
     */
    public static int countHousesInArea(Point3d inPoint, double radius) {
        int counter = 0;

        Block block = null;
        Actor actor = null;
        HashMapInt map = World.cur().statics.allBlocks();
        if (map == null) return counter;

        for (HashMapIntEntry hashmapintentry = map.nextEntry(null); hashmapintentry != null; hashmapintentry = map.nextEntry(hashmapintentry)) {
            block = (Block) hashmapintentry.getValue();
            if (block.actor == null) continue;

            for (int i = 0; i < block.actor.length; i++) {
                actor = block.actor[i];

                if (actor != null && actor instanceof House && Math.abs(inPoint.distance(actor.pos.getAbsPoint())) <= radius) // System.out.println(counter + " Target - Actor in area: " + actor.toString() + " is alive: " + Actor.isAlive(actor));
                    counter++;
            }
        }
        // System.out.println("------------------------------------------------------------------");

        return counter;
    }

    /**
     * Method returns number of dead objects found in area defined by given coordinates and radius. Objects are checked in map actors.static file, not mission file!
     *
     * @param inPoint
     * @param radius
     * @return
     */
    public static int countDeadHousesInArea(Point3d inPoint, double radius) {
        int counter = 0;

        Block block = null;
        Actor actor = null;
        HashMapInt map = World.cur().statics.allBlocks();
        if (map == null) return counter;

        for (HashMapIntEntry hashmapintentry = map.nextEntry(null); hashmapintentry != null; hashmapintentry = map.nextEntry(hashmapintentry)) {
            block = (Block) hashmapintentry.getValue();
            if (block.actor == null) continue;

            for (int i = 0; i < block.actor.length; i++) {
                actor = block.actor[i];

                if (actor != null && actor instanceof House && !actor.isAlive() && Math.abs(inPoint.distance(actor.pos.getAbsPoint())) <= radius) // System.out.println(counter + " Target - Actor in area: " + actor.toString() + " is alive: " +
                                                                                                                                                  // Actor.isAlive(actor));
                    counter++;
            }
        }
        // System.out.println("------------------------------------------------------------------");

        return counter;
    }

    /**
     * Call this method when one of the teams won.
     *
     * @param army
     */
    public static void doMissionComplete(int army) {
        if (Mission.isNet()) try {
            if (army == 1) {
                HUD.logCenter("mds.victory.redHBCondition");

                // Added by |ZUTI|: if mission has parameters about next mission, load it.
                int loadDelay = Mission.MDS_VARIABLES().zutiNextMission_LoadDelay;

                if (Mission.MDS_VARIABLES().zutiNextMission_Enable && Mission.MDS_VARIABLES().zutiNextMission_RedWon != null && Mission.MDS_VARIABLES().zutiNextMission_RedWon.trim().length() > 0) {
                    System.out.println("-=RED WON=-");
                    StringBuffer sb = new StringBuffer();
                    sb.append("Mission: RED WON [");
                    sb.append(Mission.MDS_VARIABLES().zutiNextMission_RedWon);
                    sb.append(",");
                    sb.append(Mission.MDS_VARIABLES().zutiNextMission_Difficulty);
                    sb.append(",");
                    sb.append(loadDelay);
                    sb.append("]");

                    EventLog.type(true, sb.toString());
                    System.out.println(sb.toString());

                    Mission.MDS_VARIABLES().zutiNextMission_Enable = false;
                }
            } else {
                HUD.logCenter("mds.victory.blueHBCondition");

                // Added by |ZUTI|: if mission has parameters about next mission, load it.
                int loadDelay = Mission.MDS_VARIABLES().zutiNextMission_LoadDelay;

                if (Mission.MDS_VARIABLES().zutiNextMission_Enable && Mission.MDS_VARIABLES().zutiNextMission_BlueWon != null && Mission.MDS_VARIABLES().zutiNextMission_BlueWon.trim().length() > 0) {
                    System.out.println("-=BLUE WON=-");
                    StringBuffer sb = new StringBuffer();
                    sb.append("Mission: BLUE WON [");
                    sb.append(Mission.MDS_VARIABLES().zutiNextMission_BlueWon);
                    sb.append(",");
                    sb.append(Mission.MDS_VARIABLES().zutiNextMission_Difficulty);
                    sb.append(",");
                    sb.append(loadDelay);
                    sb.append("]");

                    EventLog.type(true, sb.toString());
                    System.out.println(sb.toString());

                    Mission.MDS_VARIABLES().zutiNextMission_Enable = false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method checks if pilot landed on home base with its own specific RRR settings.
     *
     * @param FM
     * @return BornPlace object if such born place is found, else null value is returned.
     */
    public static BornPlace isPilotLandedOnBPWithOwnRRRSettings(FlightModel FM) {
        try {
            int pilotArmy = World.getPlayerArmy();
            Point3d playerPos = FM.actor.pos.getAbsPoint();

            BornPlace bp = ZutiSupportMethods_Net.getNearestBornPlace_AnyArmy(playerPos.x, playerPos.y);

            // RRR with HB specific settings if pilot landed inside friendly or neutral home base circle and if that HB supports it's specific settings
            if (bp != null && bp.zutiOverrideDefaultRRRSettings && (bp.army == 0 || bp.army == pilotArmy)) {
                double tmpDistance = Math.pow(playerPos.x - bp.place.x, 2) + Math.pow(playerPos.y - bp.place.y, 2);
                // is player in that home base circle?
                if (tmpDistance <= bp.r * bp.r) return bp;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Is performing RRR functions allowed? Conditions: plane must be on friction area and must not be moving. If the plane can set chocks they must be set else previous conditions apply (for float planes). If plane is on test runway or on a carrier plane
     * can also perform RRR functions.
     *
     * @param FM
     * @return
     */
    public static boolean allowRRR(Aircraft aircraft) {
        if (aircraft == null || !Actor.isValid(aircraft)) return false;
        
        if (Config.isUSE_RENDER() && Config.cur.ini.get("Mods", "offlineRRR", 0) == 1 && Mission.isSingle()) return true;

        boolean positionedOnRRRArea = false;
        if (ZutiSupportMethods_FM.isFMOnZAP(aircraft.FM) || ZutiAirfieldPoint.isLocatedTestRunway(aircraft.FM.Loc) || ZutiAirfieldPoint.isLocatedOnCarrier(aircraft.FM.Loc)) positionedOnRRRArea = true;

        if (!positionedOnRRRArea) return false;

        if (aircraft.FM.canChangeBrakeShoe && !aircraft.FM.brakeShoe) return false;

        if (!aircraft.FM.canChangeBrakeShoe && !ZutiSupportMethods.isPlaneStationary(aircraft.FM)) return false;

        return true;
    }

    /**
     * Check if entered flight model is moving or not.
     *
     * @param flightmodel
     * @return
     */
    public static boolean isPlaneStationary(FlightModel flightmodel) {
        if (flightmodel == null) return false;

        // Absurd, but I failed to get an affective method of determining if plane is on water or not. Method to determine if plane is stationary there!
        if (flightmodel.getSpeedKMH() < 10.0D && flightmodel.getVertSpeed() < 2.0D) return true;

        return false;
    }

    /**
     * Method searches all born places and returns that holds given coordinates and army.
     *
     * @param x
     * @param y
     * @param army
     * @return
     */
    public static BornPlace getParatrooperAreaBornPlace(double x, double y, int army) {
        if (!ZutiMDSVariables.ZUTI_FRONT_ENABLE_HB_CAPTURING || World.cur().bornPlaces == null || Front.markers() == null) return null;

        if (CURRENT_BORN_PLACE == null || CURRENT_BORN_PLACE.army == army) CURRENT_BORN_PLACE = ZutiSupportMethods_Net.getNearestBornPlace_ExcludeArmy(x, y, army, true);

        if (CURRENT_BORN_PLACE == null) // System.out.println("ZutiSupportMethods - 1 born place with army different than >" + army + "< was not found!");
            return null;

        double max = CURRENT_BORN_PLACE.r * CURRENT_BORN_PLACE.r;
        double distance = Math.pow(x - CURRENT_BORN_PLACE.place.x, 2) + Math.pow(y - CURRENT_BORN_PLACE.place.y, 2);
        if (distance > max) {
            // last saved/searched born place does not meet our conditions, get new one...
            CURRENT_BORN_PLACE = ZutiSupportMethods_Net.getNearestBornPlace_ExcludeArmy(x, y, army, true);

            if (CURRENT_BORN_PLACE == null) // System.out.println("ZutiSupportMethods - born place with army different than >" + army + "< was not found!");
                return null;
        }

        return CURRENT_BORN_PLACE;
    }

    /**
     * This method checks given born place and check if it was overrun by paratroopers. If it was, a net message is sent to all players.
     *
     * @param bp
     * @param army
     */
    public static void isBornPlaceOverrunByPara(BornPlace bp, int army) {
        if (bp.zutiParatroopersInsideHomeBaseArea >= bp.zutiCapturingRequiredParatroopers) {
            // Born place was overrun...
            ZutiSupportMethods.removeMarkers(ZutiSupportMethods.getMarkersAtCoordinates(bp.place.x, bp.place.y, bp.r, bp.army));
            ZutiSupportMethods.putMarkerAtCoordinates(bp.place.x, bp.place.y, army);

            // reset born place paratroopers counter so that old army can recapture the place
            bp.zutiParatroopersInsideHomeBaseArea = 0;

            executeCodeSequenceAfterBornPlaceWasOverrun(bp);

            ZUTI_PARA_CAPTURED_HOMEBASES.add(bp);

            ZutiSupportMethods_NetSend.paraCapturedHomeBase(bp);

            System.out.println("Home base x=" + bp.place.x + " and y=" + bp.place.y + " added to para captured bases list. Total: " + ZUTI_PARA_CAPTURED_HOMEBASES.size());
        }
    }

    /**
     * Is specified actor a static actor or not? Static actors = NStationary group.
     *
     * @param actor
     * @return
     */
    public static boolean isStaticActor(Actor actor) {
        if (actor == null) return false;

        if (actor.getArmy() == 0) return false;
        if (actor instanceof ShipGeneric) return ((ShipGeneric) actor).zutiIsStatic();
        if (actor instanceof BigshipGeneric) return ((BigshipGeneric) actor).zutiIsStatic();
        if (actor instanceof Aircraft) return false;
        if (actor instanceof Chief) return false;
        if (actor instanceof Wing) return false;
        return !Actor.isValid(actor.getOwner()) || !(actor.getOwner() instanceof Chief);
    }

    /**
     * Removes specified born place from global list.
     *
     * @param bp
     */
    public static void removeBornPlace(BornPlace bp) {
        // Set bornPlace to -1 for users that were stationed on removed bornPlace
        ZutiSupportMethods.disconnectPilotsFromBornPlace(bp);
        int size = bp.zutiBpStayPoints.size();
        ZutiStayPoint zutiSp = null;

        for (int i = 0; i < size; i++)
            try {
                zutiSp = (ZutiStayPoint) bp.zutiBpStayPoints.get(i);
                zutiSp.pointStay.set(-999999.99F, -999999.99F);
            } catch (Exception ex) {}
        World.cur().bornPlaces.remove(bp);

        bp = null;
    }

    /**
     * Disconnect pilots from given born place.
     *
     * @param bp
     */
    public static void disconnectPilotsFromBornPlace(BornPlace bp) {
        NetUser netuser = (NetUser) NetEnv.host();

        if (netuser == null) return;

        List bornplaces = World.cur().bornPlaces;
        int size = bornplaces.size();

        int bornPlaceId = netuser.getBornPlace();
        if (bornPlaceId > -1 && bornPlaceId < size && bp.equals(bornplaces.get(bornPlaceId))) netuser.setBornPlace(-1);

        List hosts = NetEnv.hosts();

        if (hosts == null) return;

        for (int i = 0; i < hosts.size(); i++) {
            netuser = (NetUser) hosts.get(i);
            bornPlaceId = netuser.getBornPlace();
            if (bornPlaceId > -1 && bornPlaceId < size && bp.equals(bornplaces.get(bornPlaceId))) netuser.setBornPlace(-1);
        }
    }

    /**
     * Call this method when you receive server message about para captured home base.
     *
     * @param bp
     * @param army
     */
    public static void paraCapturedBornPlace(BornPlace bp, int army) {
        if (bp == null) return;

        ZutiSupportMethods.removeMarkers(ZutiSupportMethods.getMarkersAtCoordinates_ExcludeArmy(bp.place.x, bp.place.y, bp.r, army));
        ZutiSupportMethods.putMarkerAtCoordinates(bp.place.x, bp.place.y, army);

        // reset born place paratroopers counter so that old army can recapture the place
        bp.zutiParatroopersInsideHomeBaseArea = 0;

        executeCodeSequenceAfterBornPlaceWasOverrun(bp);
    }

    /**
     * Call this method to check if any branch of existing regiments are missing in IL2 resources.
     */
    public static void checkForMissingBranches() {
        ResourceBundle resCountry = ResourceBundle.getBundle("i18n/country", RTSConf.cur.locale, LDRres.loader());
        Regiment regiment = null;
        StringBuffer sb = null;
        List list = Regiment.getAll();
        int k1 = list.size();

        System.out.println("Searching for missing resources...");

        for (int i = 0; i < k1; i++) {
            regiment = (Regiment) list.get(i);
            try {
                resCountry.getString(regiment.branch());
            } catch (Exception ex) {
                sb = new StringBuffer();
                sb.append("  branch >");
                sb.append(regiment.branch());
                sb.append("< of regiment >");
                sb.append(regiment.name());
                sb.append("< is missing in IL2 resources!");

                System.out.println(sb.toString());
            }
        }

        System.out.println("Done!");
    }

    /**
     * This method checks existing planes that were dropping paratroopers and checks that they can be safely created without tangeling their chutes amongst themselves. This is needed because when paratroopers are used as payload things go wrong in
     * dogfight environments because paratroopers just weren't created for such usage (they send netspawn commands each time they are created, no matter where they are created!
     *
     * @param x
     * @param y
     * @param z
     * @param paraOwner
     * @param ownerSpeed
     * @return
     */
    public static boolean canCreateParatrooper(double x, double y, double z, String paraOwner, double ownerSpeed) {
        Paratrooper oldPara = (Paratrooper) ZutiSupportMethods.AC_LAST_PARATROOPER.get(paraOwner);

        if (oldPara == null) return true;

        Point3d position = null;
        position = oldPara.pos.getAbsPoint();
        double distance = Math.pow(x - position.x, 2) + Math.pow(y - position.y, 2);
        double height = Math.abs(z - position.z);
        if (ownerSpeed < 2.0F) {
            // Plane is stationary, distance can be less
            if (distance < 4.0D) // System.out.println("Para distance difference of >" + distance + "< and height difference of >" + height + "< is not safe to jump another paratrooper!");
                return false;
        } else // Plane is flying, distance must be greater
               // Plane is stationary, distance can be less
            if (distance < 60.0D && height < 10) // System.out.println("Para distance difference of >" + distance + "< and height difference of >" + height + "< is not safe to jump another paratrooper!");
                return false;
        return true;
    }

    /**
     * Display (in console) what kind of ordinance AC carries.
     *
     * @param fm
     */
    public static void showWeapons(FlightModel fm) {
        for (int i = 0; i < fm.CT.Weapons.length; i++) {
            BulletEmitter[] bulletemitters = fm.CT.Weapons[i];
            if (bulletemitters != null) for (int i_164_ = 0; i_164_ < bulletemitters.length; i_164_++)
                if (bulletemitters[i_164_] instanceof BombGun) {
                    BombGun bomb = (BombGun) bulletemitters[i_164_];
                    System.out.println("Bomb: " + bomb.getHookName());
                } else if (bulletemitters[i_164_] instanceof Pylon) {
                    Pylon pylon = (Pylon) bulletemitters[i_164_];
                    System.out.println("Pylon: " + pylon.getHookName());
                }
        }
    }

    /**
     * This method returns a valid actor army different from specefied one. If no such actor can be found, original owner is returned instead.
     *
     * @param originalOwner
     * @param actorArmy
     * @return
     */
    public static Actor getValidActor(Actor originalOwner) {
        int ownerArmy = 0;

        if (originalOwner != null) ownerArmy = originalOwner.getArmy();

        Actor actor = null;
        List actors = Main.cur().mission.actors;
        for (int i = 0; i < actors.size(); i++) {
            actor = (Actor) actors.get(i);
            if (actor.getArmy() != ownerArmy && Actor.isValid(actor) && !actor.name().endsWith("_0")) // System.out.println("Found apropriate actor: " + actor.name() + ", army: " + actor.getArmy());
                return actor;
        }

        // If no actors were found of different army... search for actors of same army. AI naturally.
        for (int i = 0; i < actors.size(); i++) {
            actor = (Actor) actors.get(i);
            if (Actor.isValid(actor) && !actor.name().endsWith("_0")) // System.out.println("Found apropriate actor: " + actor.name() + ", army: " + actor.getArmy());
                return actor;
        }

        return originalOwner;
    }

    /**
     * Method determines if damager of some actor is this host or not.
     *
     * @param damager
     * @return
     */
    public static boolean isDamagerCurrentUser(Actor damager) {
        try {
            String name = damager.name();
            if (!name.endsWith("_0")) return false;

            name = name.substring(0, name.lastIndexOf("_0"));
            String hostName = ((NetUser) NetEnv.host()).uniqueName();
            // System.out.println("ZutiSupportMethods - isDamagerCurrentUser: damager=" + name + " vs host=" + hostName);

            if (hostName.equals(name)) return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * This method resolves AI AC names in more detail as original logging is very poor. You only get plane type. And that by itself means nothing since multiple wings can have same aircraft types so when one is destroyed, you have no way of telling to
     * which wing it belonged to nor to which army it belonged to. There just is no way of linking them to mission related data (wings). For other objects you can do that as they are identified correctly. Chiefs are reported with their mission names. So,
     * only AC pose a problem and only they are changed. Other names are returned as default (EventLog.name() method is called on them).
     *
     * @param actor
     * @return
     */
    public static String getAircraftCompleteName(Actor actor) {
        if (actor == null) return "";

        if (!(actor instanceof Aircraft) || actor.getOwner() == null) return EventLog.name(actor);

        StringBuffer sb = new StringBuffer();
        if (actor.getOwner() != null) {
            String properName = actor.name();
            if (!properName.endsWith("_0")) sb.append(actor.getOwner().name());
            else // Live player
                sb.append(properName.substring(0, properName.lastIndexOf("_0")));
            sb.append("{");
            sb.append(actor.name());
            sb.append(";");
            sb.append(EventLog.name(actor));
            sb.append(";");
            sb.append(actor.getArmy());
            sb.append("}");
        } else {
            sb.append("NoOwner{");
            sb.append(actor.name());
            sb.append(";");
            sb.append(EventLog.name(actor));
            sb.append(";");
            sb.append(actor.getArmy());
            sb.append("}");
        }

        return sb.toString();
    }

    /**
     * Convert boolean value to it's string representation. True = 1, false = 0
     *
     * @param value
     * @return
     */
    public static String boolToInt(boolean value) {
        if (value) return "1";

        return "0";
    }

    /**
     * Method searches markers and tries to attach them to appropriate/valid moving units.
     */
    public static void zutiAssignMarkersToActors() {
        List actors = Main.cur().mission.actors;
        if (actors == null) return;
        double min = ZutiSupportMethods_Builder.FRONT_MARKER_RADIUS * ZutiSupportMethods_Builder.FRONT_MARKER_RADIUS_SHIP_MULTI;
        List zutiMarkers = Front.markers();
        int size = zutiMarkers.size();
        Marker marker = null;
        Actor nearestActor = null;
        Actor actor = null;
        TankGeneric tank = null;
        ArtilleryGeneric artillery = null;
        BigshipGeneric ship = null;

        for (int i = 0; i < size; i++) {
            marker = (Marker) zutiMarkers.get(i);

            // System.out.println("Checking carrier for marker: x=" + marker.x + ", y=" + marker.y);

            nearestActor = null;
            int actorsSize = actors.size();
            for (int index = 0; index < actorsSize; index++) {
                actor = (Actor) actors.get(index);

                if (ZutiSupportMethods.isStaticActor(actor)) continue;

                if (actor instanceof TankGeneric) {
                    tank = (TankGeneric) actor;
                    if (tank.zutiHasFrontMarkerAssigned()) continue;

                    Point3d point3d = actor.pos.getAbsPoint();
                    double tmpDistance = Math.pow(marker.x - point3d.x, 2) + Math.pow(marker.y - point3d.y, 2);
                    // System.out.println("Tank: " + tank.toString() + ", " + new Double(tmpDistance).toString() + " vs " + new Double(min).toString());
                    if (tmpDistance < min) {
                        min = tmpDistance;
                        nearestActor = actor;
                    }
                } else if (actor instanceof ArtilleryGeneric) {
                    artillery = (ArtilleryGeneric) actor;
                    if (artillery.zutiHasFrontMarkerAssigned()) continue;

                    Point3d point3d = actor.pos.getAbsPoint();
                    double tmpDistance = Math.pow(marker.x - point3d.x, 2) + Math.pow(marker.y - point3d.y, 2);
                    // System.out.println("Artillery: " + artillery.toString() + ", " + new Double(tmpDistance).toString() + " vs " + new Double(min).toString());
                    if (tmpDistance < min) {
                        min = tmpDistance;
                        nearestActor = actor;
                    }
                } else if (actor instanceof BigshipGeneric && actor instanceof ZutiTypeFrontCarrier) {
                    ship = (BigshipGeneric) actor;
                    if (ship.zutiHasFrontMarkerAssigned()) continue;

                    Point3d point3d = actor.pos.getAbsPoint();
                    double tmpDistance = Math.pow(marker.x - point3d.x, 2) + Math.pow(marker.y - point3d.y, 2);
                    // System.out.println("Ship: " + ship.toString() + ", " + new Double(tmpDistance).toString() + " vs " + new Double(min).toString());
                    // System.out.println(" coordinates of the ship: x=" + ship.pos.getAbsPoint().x + ", y=" + ship.pos.getAbsPoint().y);
                    if (tmpDistance < min) {
                        min = tmpDistance;
                        nearestActor = actor;
                    }
                }
            }

            if (nearestActor instanceof TankGeneric) ((TankGeneric) nearestActor).zutiAddFrontMarker(marker);
            else if (nearestActor instanceof ArtilleryGeneric) ((ArtilleryGeneric) nearestActor).zutiAddFrontMarker(marker);
            else if (nearestActor instanceof BigshipGeneric) ((BigshipGeneric) nearestActor).zutiAddFrontMarker(marker);

            if (nearestActor != null) marker.zutiMarkerCarrierName = nearestActor.name();

            if (nearestActor != null) printDebugMessage("Marker >" + marker + "< assigned to >" + nearestActor.name() + "< - " + nearestActor);

            // TODO: Limit Log output // by Storebror
//			else
//				System.out.println("Marker >" + marker + "< not assigned");

            // Reset min distance
            min = ZutiSupportMethods_Builder.FRONT_MARKER_RADIUS * ZutiSupportMethods_Builder.FRONT_MARKER_RADIUS_SHIP_MULTI;
        }
    }

    /**
     * For specified born place this method searches list of allowed countries and loads them to it. Countries for this method to work are saved as i18n string representations.
     *
     * @param bornplace
     * @param sectfile
     */
    public static void zutiLoadBornPlaceCountries_oldMDS(BornPlace bornplace, SectFile sectfile) {
        if (bornplace == null) return;

        if (bornplace != null && bornplace.zutiHomeBaseCountries == null) bornplace.zutiHomeBaseCountries = new ArrayList();

        ZutiSupportMethods_Net.loadAllCountriesForBornPlace(bornplace);
        String country = null;

        int count = sectfile.sectionIndex("MDS_BornPlace_" + (int) bornplace.place.x + "_" + (int) bornplace.place.y + "_Countries");
        if (count >= 0) {
            bornplace.zutiHomeBaseCountries.clear();

            int i_23_ = sectfile.vars(count);
            for (int i_24_ = 0; i_24_ < i_23_; i_24_++)
                try {
                    country = sectfile.var(count, i_24_).replace('_', ' ');
                    if (!bornplace.zutiHomeBaseCountries.contains(country)) bornplace.zutiHomeBaseCountries.add(country);
                } catch (Exception ex) {}
            // TODO: +++ Add Default Country "None" Mod by SAS~Storebror +++
            if (Config.cur.bAddDefaultCountryNone) if (!bornplace.zutiHomeBaseCountries.contains("None")) // System.out.println("zutiHomeBaseCountries old add default");
                bornplace.zutiHomeBaseCountries.add("None");
        }

        count = sectfile.sectionIndex("MDS_BornPlace_" + (int) bornplace.place.x + "_" + (int) bornplace.place.y + "_CapturedCountries_Red");
        if (count >= 0) {
            if (bornplace.zutiHomeBaseCapturedRedCountries == null) bornplace.zutiHomeBaseCapturedRedCountries = new ArrayList();

            bornplace.zutiHomeBaseCapturedRedCountries.clear();

            int i_23_ = sectfile.vars(count);
            for (int i_24_ = 0; i_24_ < i_23_; i_24_++)
                try {
                    country = sectfile.var(count, i_24_).replace('_', ' ');
                    if (!bornplace.zutiHomeBaseCapturedRedCountries.contains(country)) bornplace.zutiHomeBaseCapturedRedCountries.add(country);
                } catch (Exception ex) {}
            // TODO: +++ Add Default Country "None" Mod by SAS~Storebror +++
            if (Config.cur.bAddDefaultCountryNone) if (!bornplace.zutiHomeBaseCapturedRedCountries.contains("None")) // System.out.println("zutiHomeBaseCapturedRedCountries old add default");
                bornplace.zutiHomeBaseCapturedRedCountries.add("None");
        }

        count = sectfile.sectionIndex("MDS_BornPlace_" + (int) bornplace.place.x + "_" + (int) bornplace.place.y + "_CapturedCountries_Blue");
        if (count >= 0) {
            if (bornplace.zutiHomeBaseCapturedBlueCountries == null) bornplace.zutiHomeBaseCapturedBlueCountries = new ArrayList();

            bornplace.zutiHomeBaseCapturedBlueCountries.clear();

            int i_23_ = sectfile.vars(count);
            for (int i_24_ = 0; i_24_ < i_23_; i_24_++)
                try {
                    country = sectfile.var(count, i_24_).replace('_', ' ');
                    if (!bornplace.zutiHomeBaseCapturedBlueCountries.contains(country)) bornplace.zutiHomeBaseCapturedBlueCountries.add(country);
                } catch (Exception ex) {}
            // TODO: +++ Add Default Country "None" Mod by SAS~Storebror +++
            if (Config.cur.bAddDefaultCountryNone) if (!bornplace.zutiHomeBaseCapturedBlueCountries.contains("None")) // System.out.println("zutiHomeBaseCapturedBlueCountries old add default");
                bornplace.zutiHomeBaseCapturedBlueCountries.add("None");
        }
    }

    /**
     * For specified born place this method searches list of allowed countries and loads them to it. Countries for this method to work are saved as non-i18n string representations.
     *
     * @param bornplace
     * @param sectfile
     */
    public static void zutiLoadBornPlaceCountries_newMDS(BornPlace bornplace, SectFile sectfile) {
        if (bornplace == null) return;

        if (bornplace != null && bornplace.zutiHomeBaseCountries == null) bornplace.zutiHomeBaseCountries = new ArrayList();

        ResourceBundle resourcebundle = ResourceBundle.getBundle("i18n/country", RTSConf.cur.locale, LDRres.loader());

        String i18nCountryName = null;
        String basicCountryName = null;

        int count = sectfile.sectionIndex("MDS_BornPlace_" + (int) bornplace.place.x + "_" + (int) bornplace.place.y + "_Countries");
        if (count >= 0) {
            int i_23_ = sectfile.vars(count);
            for (int i_24_ = 0; i_24_ < i_23_; i_24_++)
                try {
                    i18nCountryName = sectfile.var(count, i_24_);
                    basicCountryName = resourcebundle.getString(i18nCountryName);
                    if (basicCountryName != null && !bornplace.zutiHomeBaseCountries.contains(basicCountryName)) bornplace.zutiHomeBaseCountries.add(basicCountryName);
                    // System.out.println("Mission - loaded born place country: " + country);
                } catch (Exception ex) {}
            // TODO: +++ Add Default Country "None" Mod by SAS~Storebror +++
            if (Config.cur.bAddDefaultCountryNone) {
                i18nCountryName = resourcebundle.getString("nn");
//                System.out.println("zutiHomeBaseCountries add default 1, i18nCountryName=" + i18nCountryName);
                if (i18nCountryName != null && !bornplace.zutiHomeBaseCountries.contains(i18nCountryName)) // System.out.println("zutiHomeBaseCountries add default 2");
                    bornplace.zutiHomeBaseCountries.add(i18nCountryName);
            }
            // TODO: --- Add Default Country "None" Mod by SAS~Storebror ---
        }

        if (bornplace.zutiHomeBaseCountries.size() == 0) ZutiSupportMethods_Net.loadAllCountriesForBornPlace(bornplace);

        count = sectfile.sectionIndex("MDS_BornPlace_" + (int) bornplace.place.x + "_" + (int) bornplace.place.y + "_CapturedCountries_Red");
        if (count >= 0) {
            if (bornplace.zutiHomeBaseCapturedRedCountries == null) bornplace.zutiHomeBaseCapturedRedCountries = new ArrayList();

            int i_23_ = sectfile.vars(count);
            for (int i_24_ = 0; i_24_ < i_23_; i_24_++)
                try {
                    i18nCountryName = sectfile.var(count, i_24_);
                    basicCountryName = resourcebundle.getString(i18nCountryName);
                    if (basicCountryName != null && !bornplace.zutiHomeBaseCapturedRedCountries.contains(basicCountryName)) bornplace.zutiHomeBaseCapturedRedCountries.add(basicCountryName);
                } catch (Exception ex) {}
            // TODO: +++ Add Default Country "None" Mod by SAS~Storebror +++
            if (Config.cur.bAddDefaultCountryNone) {
                i18nCountryName = resourcebundle.getString("nn");
//                System.out.println("zutiHomeBaseCapturedRedCountries add default 1, i18nCountryName=" + i18nCountryName);
                if (i18nCountryName != null && !bornplace.zutiHomeBaseCapturedRedCountries.contains(i18nCountryName)) // System.out.println("zutiHomeBaseCapturedRedCountries add default 2");
                    bornplace.zutiHomeBaseCapturedRedCountries.add(i18nCountryName);
            }
            // TODO: --- Add Default Country "None" Mod by SAS~Storebror ---
        }

        count = sectfile.sectionIndex("MDS_BornPlace_" + (int) bornplace.place.x + "_" + (int) bornplace.place.y + "_CapturedCountries_Blue");
        if (count >= 0) {
            if (bornplace.zutiHomeBaseCapturedBlueCountries == null) bornplace.zutiHomeBaseCapturedBlueCountries = new ArrayList();

            int i_23_ = sectfile.vars(count);
            for (int i_24_ = 0; i_24_ < i_23_; i_24_++)
                try {
                    i18nCountryName = sectfile.var(count, i_24_);
                    basicCountryName = resourcebundle.getString(i18nCountryName);
                    if (basicCountryName != null && !bornplace.zutiHomeBaseCapturedBlueCountries.contains(basicCountryName)) bornplace.zutiHomeBaseCapturedBlueCountries.add(basicCountryName);
                } catch (Exception ex) {}
            // TODO: +++ Add Default Country "None" Mod by SAS~Storebror +++
            if (Config.cur.bAddDefaultCountryNone) {
                i18nCountryName = resourcebundle.getString("nn");
//                System.out.println("zutiHomeBaseCapturedBlueCountries add default 1, i18nCountryName=" + i18nCountryName);
                if (i18nCountryName != null && !bornplace.zutiHomeBaseCapturedBlueCountries.contains(i18nCountryName)) // System.out.println("zutiHomeBaseCapturedBlueCountries add default 2");
                    bornplace.zutiHomeBaseCapturedBlueCountries.add(i18nCountryName);
            }
            // TODO: --- Add Default Country "None" Mod by SAS~Storebror ---
        }
    }

    /**
     * As method name implies - are home bases also friction areas? If so, it ads them to complete list of friction areas.
     */
    public static void zutiAddHomeBasesAsFrictionDictators() {
        // System.out.println("----------------------------processing born places...");
        List zaps = ZutiSupportMethods_Engine.AIRFIELDS;
        ArrayList bornplaces = World.cur().bornPlaces;

        if (bornplaces == null) return;

        int size = bornplaces.size();

        for (int i = 0; i < size; i++) {
            BornPlace bp = (BornPlace) bornplaces.get(i);
            if (bp.zutiEnableFriction && !ZutiSupportMethods.zutiIsOnExistingFrictionArea(bp)) {
                double x1 = bp.place.x - bp.r;
                double y1 = bp.place.y + bp.r;
                double x2 = bp.place.x + bp.r;
                double y2 = bp.place.y - bp.r;
                double friction = bp.zutiFriction;

                if (zaps != null) {
                    zaps.add(new ZutiAirfieldPoint(x1, y1, x2, y2, friction));
                    printDebugMessage("BornPlace is also friction area: Location(" + x1 + ", " + y1 + "), T2(" + x2 + ", " + y2 + "), friction=" + friction);
                } else System.out.println("ZutiAirports table is null!");
            }
        }
    }

    private static boolean zutiIsOnExistingFrictionArea(BornPlace bp) {
        List zaps = ZutiSupportMethods_Engine.AIRFIELDS;
        int size = zaps.size();
        for (int i = 0; i < size; i++) {
            ZutiAirfieldPoint zap = (ZutiAirfieldPoint) zaps.get(i);
            if (zap.isInZAPArea(bp.place.x, bp.place.y) > 0) return true;
        }

        return false;
    }

    /**
     * Loads captured planes that were assigned to specified born place.
     *
     * @param bornplace
     * @param sectfile
     */
    public static void zutiLoadBornPlaceCapturedPlanes(BornPlace bornplace, SectFile sectfile) {
        // DS can not read this and does not need to read this!
        if (bornplace == null) return;

        boolean zutiMdsSectionIdExists = sectfile.sectionIndex("MDS") > -1;
        int i_22_ = sectfile.sectionIndex("MDS_BornPlace_" + (int) bornplace.place.x + "_" + (int) bornplace.place.y + "_CapturedAc_Red");
        if (i_22_ >= 0) {
            int i_23_ = sectfile.vars(i_22_);
            for (int i_24_ = 0; i_24_ < i_23_; i_24_++) {
                String readLine = sectfile.line(i_22_, i_24_);
                StringTokenizer stringtokenizer = new StringTokenizer(readLine);

                ZutiAircraft zac = new ZutiAircraft();
                ZutiSupportMethods.fillZutiAircraft(zac, stringtokenizer, zutiMdsSectionIdExists);
                String string = zac.getAcName();
                if (string != null) {
                    string = string.intern();
                    Class var_class = (Class) Property.value(string, "airClass", null);
                    // if (var_class != null && (Property.containsValue(var_class, "cockpitClass")))
                    if (var_class != null) {
                        // Add this ac to modified table for this home base
                        if (bornplace.zutiCapturedAc_Red == null) bornplace.zutiCapturedAc_Red = new ArrayList();

                        bornplace.zutiCapturedAc_Red.add(zac);
                        bornplace.zutiBaseCapturedRedPlanes += string + " ";
                    }
                }
            }
        }

        i_22_ = sectfile.sectionIndex("MDS_BornPlace_" + (int) bornplace.place.x + "_" + (int) bornplace.place.y + "_CapturedAc_Blue");
        if (i_22_ >= 0) {
            int i_23_ = sectfile.vars(i_22_);
            for (int i_24_ = 0; i_24_ < i_23_; i_24_++) {
                String readLine = sectfile.line(i_22_, i_24_);
                StringTokenizer stringtokenizer = new StringTokenizer(readLine);

                ZutiAircraft zac = new ZutiAircraft();
                ZutiSupportMethods.fillZutiAircraft(zac, stringtokenizer, zutiMdsSectionIdExists);
                String string = zac.getAcName();
                if (string != null) {
                    string = string.intern();
                    Class var_class = (Class) Property.value(string, "airClass", null);
                    // if (var_class != null && (Property.containsValue(var_class, "cockpitClass")))
                    if (var_class != null) {
                        if (bornplace.zutiCapturedAc_Blue == null) bornplace.zutiCapturedAc_Blue = new ArrayList();

                        bornplace.zutiCapturedAc_Blue.add(zac);
                        bornplace.zutiBaseCapturedBluePlanes += string + " ";
                    }
                }
            }
        }
    }

    /**
     * Loads specific RRR settings for specified born place. Only if such settings exist.
     *
     * @param bornplace
     * @param sectfile
     */
    public static void zutiLoadBornPlaceRRR(BornPlace bornplace, SectFile sectfile) {
        int sectionId = sectfile.sectionIndex("MDS_BornPlace_" + (int) bornplace.place.x + "_" + (int) bornplace.place.y + "_RRR");
        if (sectionId >= 0) {
            int lines = sectfile.vars(sectionId);
            if (lines > -1) try {
                NumberTokenizer numbertokenizer = new NumberTokenizer(sectfile.line(sectionId, 0));
                bornplace.zutiOverrideDefaultRRRSettings = false;
                if (numbertokenizer.next(0, 0, 1) == 1) bornplace.zutiOverrideDefaultRRRSettings = true;

                bornplace.zutiOneMgCannonRearmSecond = numbertokenizer.next(10, 0, 99999);
                bornplace.zutiOneBombFTankTorpedoeRearmSeconds = numbertokenizer.next(25, 0, 99999);
                bornplace.zutiOneRocketRearmSeconds = numbertokenizer.next(20, 0, 99999);
                bornplace.zutiLoadoutChangePenaltySeconds = numbertokenizer.next(30, 0, 99999);
                bornplace.zutiOnlyHomeBaseSpecificLoadouts = true;
                if (numbertokenizer.next(1, 0, 1) == 0) bornplace.zutiOnlyHomeBaseSpecificLoadouts = false;
                bornplace.zutiRearmOnlyIfAmmoBoxesExist = false;
                if (numbertokenizer.next(0, 0, 1) == 1) bornplace.zutiRearmOnlyIfAmmoBoxesExist = true;

                bornplace.zutiGallonsLitersPerSecond = numbertokenizer.next(3, 0, 99999);
                bornplace.zutiRefuelOnlyIfFuelTanksExist = false;
                if (numbertokenizer.next(0, 0, 1) == 1) bornplace.zutiRefuelOnlyIfFuelTanksExist = true;

                bornplace.zutiEngineRepairSeconds = numbertokenizer.next(90, 0, 99999);
                bornplace.zutiOneControlCableRepairSeconds = numbertokenizer.next(15, 0, 99999);
                bornplace.zutiFlapsRepairSeconds = numbertokenizer.next(30, 0, 99999);
                bornplace.zutiOneWeaponRepairSeconds = numbertokenizer.next(3, 0, 99999);
                bornplace.zutiCockpitRepairSeconds = numbertokenizer.next(30, 0, 99999);
                bornplace.zutiOneFuelOilTankRepairSeconds = numbertokenizer.next(20, 0, 99999);
                bornplace.zutiRepairOnlyIfWorkshopExist = false;
                if (numbertokenizer.next(0, 0, 1) == 1) bornplace.zutiRepairOnlyIfWorkshopExist = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Load aircraft for specified born place (parameter).
     *
     * @param parameter
     * @param sectfile
     * @return
     */
    public static String loadAircraftForBornPlace(String parameter, SectFile sectfile) {
        StringBuffer stringbuffer = new StringBuffer();

        for (int id = 0; id < 255; id++) {
            String index = "";
            if (id < 10) index = "00" + new Integer(id).toString();
            else if (id > 9 && id < 100) index = "0" + new Integer(id).toString();
            else if (id > 99) index = new Integer(id).toString();

            stringbuffer.append(sectfile.get("MDS", parameter + index, "").trim());
            stringbuffer.append(" ");
        }

        // System.out.println("Loaded for: " + parameter + " - " + stringbuffer.toString());

        return stringbuffer.toString().trim();
    }

    /**
     * Load aircraft list in case born place is captured.
     *
     * @param bornplace
     * @param sectfile
     */
    public static void loadCapturedPlanesList(BornPlace bornplace, SectFile sectfile) {
        try {
            String homeBaseId = (int) bornplace.place.x + "_" + (int) bornplace.place.y;
            String completeNameRed = "ZutiHB_" + homeBaseId + "_RPlanes";
            String completeNameBlue = "ZutiHB_" + homeBaseId + "_BPlanes";

            // System.out.println("Loading planes for: " + completeNameRed);
            // System.out.println("Loading planes for: " + completeNameBlue);

            bornplace.zutiBaseCapturedRedPlanes = ZutiSupportMethods.loadAircraftForBornPlace(completeNameRed, sectfile);
            bornplace.zutiBaseCapturedBluePlanes = ZutiSupportMethods.loadAircraftForBornPlace(completeNameBlue, sectfile);
        } catch (Exception ex) {
            System.out.println("PlMisBorn zutiLoadCapturedPlanesList error: " + ex.toString());
        }
    }

    /**
     * Method checks for existing airport for given born place coordinates and changes it's army designation and colour presentation.
     */
    private static void changeUnderlyingAirportsArmyAndColor(BornPlace bp) {
        if (bp == null || GUI.pad == null) return;

        double d = bp.r * bp.r;
        List airdrome = GUI.pad.zutiGetAirdromes();
        int size = airdrome.size();
        AirDrome aDrome = null;
        Point3d point3d = null;

        for (int i = 0; i < size; i++) {
            aDrome = (AirDrome) airdrome.get(i);
            if (aDrome.airport != null) {
                point3d = aDrome.airport.pos.getAbsPoint();
                double d_110_ = (point3d.x - bp.place.x) * (point3d.x - bp.place.x) + (point3d.y - bp.place.y) * (point3d.y - bp.place.y);
                if (d_110_ <= d) {
                    // airport is part of overrun home base, change that airports airdrome army and color
                    aDrome.army = bp.army;
                    aDrome.color = Army.color(aDrome.army);
                }
            }
        }
    }

    /**
     * Method sets airdrome army and color to army and color of born pace that was put on top of it.
     *
     * @param airdrome
     */
    public static void changeAirdromeArmyAndColorToBornPlaceArmyAndColor(AirDrome airdrome) {
        if (airdrome == null || World.cur().bornPlaces == null) return;

        ArrayList bornPlaces = World.cur().bornPlaces;
        int size = bornPlaces.size();
        Point3d point3d = null;
        BornPlace bp = null;
        for (int i = 0; i < size; i++) {
            bp = (BornPlace) bornPlaces.get(i);
            if (bp != null) {
                double d = bp.r * bp.r;

                point3d = airdrome.airport.pos.getAbsPoint();
                double d_110_ = (point3d.x - bp.place.x) * (point3d.x - bp.place.x) + (point3d.y - bp.place.y) * (point3d.y - bp.place.y);
                if (d_110_ <= d) {
                    airdrome.army = bp.army;
                    airdrome.color = Army.color(airdrome.army);
                }
            }
        }
    }

    /**
     * Method draws born places on your pad screen.
     */
    public static void drawBornPlaces() {
        if (!Mission.MDS_VARIABLES().zutiIcons_ShowNeutralHB || GUI.pad.iconBornPlace == null) return;

        if (GUI.pad.zutiNeutralHomeBases != null && GUI.pad.zutiNeutralHomeBases.size() != 0) {
            int size = GUI.pad.zutiNeutralHomeBases.size();
            BornPlace bornplace = null;

            for (int i = 0; i < size; i++) {
                bornplace = (BornPlace) GUI.pad.zutiNeutralHomeBases.get(i);

                // This can happen in case we are overrun neutral home base
                if (bornplace.army == 1 || bornplace.army == 2) {
                    GUI.pad.zutiNeutralHomeBases.remove(bornplace);
                    // Since we altered current zutiNeutralHomeBases array, break execution
                    break;
                }

                IconDraw.setColor(Army.color(bornplace.army));

                float x = (float) ((bornplace.place.x - GUI.pad.cameraMap2D.worldXOffset) * GUI.pad.cameraMap2D.worldScale);
                float y = (float) ((bornplace.place.y - GUI.pad.cameraMap2D.worldYOffset) * GUI.pad.cameraMap2D.worldScale);

                IconDraw.render(GUI.pad.iconBornPlace, x, y);
            }
        }
    }

    /**
     * This method serves as an means to add spawn points to global spawn points list.
     *
     * @param homeBaseSpawnPoints
     * @param standaloneHomebaseSpawnPoints
     * @return
     */
    public static Point_Stay[][] mergeArrays(Point_Stay[][] homeBaseSpawnPoints, List standaloneHomebaseSpawnPoints) {
//        System.out.println("ZutiSupportMethods.mergeArrays array1.length=" + homeBaseSpawnPoints.length + ", array2.size=" + standaloneHomebaseSpawnPoints.size());
        Point_Stay[][] mergedSpawnPoints = new Point_Stay[homeBaseSpawnPoints.length + standaloneHomebaseSpawnPoints.size()][];
        int homeBaseSpawnPointStartIndex = 0;
        for (int i = 0; i < standaloneHomebaseSpawnPoints.size(); i++) {
            mergedSpawnPoints[i] = (Point_Stay[]) standaloneHomebaseSpawnPoints.get(i);
            // TODO: Storebror: Carrier Spawnpoint Bugfix for empty [MDS_STDBornPlace_SpawnPlaces] section
//			homeBaseSpawnPointStartIndex = i;
            homeBaseSpawnPointStartIndex = i + 1;
            // ---

        }
        // TODO: Storebror: Carrier Spawnpoint Bugfix for empty [MDS_STDBornPlace_SpawnPlaces] section
//		homeBaseSpawnPointStartIndex++;
        // ---
        for (int i = 0; i < homeBaseSpawnPoints.length; i++)
            // System.out.println("startIndex=" + homeBaseSpawnPointStartIndex + ", i=" + i + " vs " + mergedSpawnPoints.length);
            mergedSpawnPoints[i + homeBaseSpawnPointStartIndex] = homeBaseSpawnPoints[i];
        /*
         * Point_Stay[][] result = new Point_Stay[array1.length + array2.size()][]; int startIndex = 0; for( int i=0; i<array1.length; i++ ) { //System.out.println("startIndex=" + startIndex + " vs " + result.length); result[i] = array1[i]; startIndex =
         * i; } startIndex++; for( int i=0; i<array2.size(); i++ ) { result[i+startIndex] = (Point_Stay[])array2.get(i); }
         */
        return mergedSpawnPoints;
    }

    /**
     * This method goes through all airports and for each checks complete list of house objects. If any house object is one of the RRR objects it is added to that airfield RRR objects list.
     */
    public static void appendRRRObjectsToAirfields() {
        List airfields = ZutiSupportMethods_Engine.AIRFIELDS;
        int size = airfields.size();
        if (World.cur().houseManager == null) return; // TODO: Added by SAS~Storebror to avoid deferencing possible null pointer
        House[] house = World.cur().houseManager.zutiGetHouses();
        for (int k = 0; k < size; k++) {
            ZutiAirfieldPoint zap = (ZutiAirfieldPoint) airfields.get(k);

            int length = house.length;
            for (int i = 0; i < length; i++)
                try {
                    House fuelTank = house[i];
                    if (fuelTank != null && fuelTank.name() != null && (isFuelTankObject(fuelTank.name()) || isAmmoBoxObject(fuelTank.name()) || isWorkshopObject(fuelTank.name()))) zap.addRRRObject(fuelTank);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        }
    }

    public static boolean isFuelTankObject(String name) {
        for (int i = 0; i < ZUTI_FUEL_TANK_OBJECTS.length; i++)
            if (name.indexOf(ZUTI_FUEL_TANK_OBJECTS[i]) > -1) return true;

        return false;
    }

    public static boolean isAmmoBoxObject(String name) {
        for (int i = 0; i < ZUTI_AMMO_BOX_OBJECTS.length; i++)
            if (name.indexOf(ZUTI_AMMO_BOX_OBJECTS[i]) > -1) return true;

        return false;
    }

    public static boolean isWorkshopObject(String name) {
        for (int i = 0; i < ZUTI_WORKSHOP_OBJECTS.length; i++)
            if (name.indexOf(ZUTI_WORKSHOP_OBJECTS[i]) > -1) return true;

        return false;
    }

    /**
     * Method returns true if object is identified as possible RRR object.
     *
     * @param name
     * @return
     */
    public static boolean isMovingRRRObject(String name, Actor actor) {
        for (int i = 0; i < ZUTI_MOVING_RRR_OBJECTS.length; i++)
            if (name.indexOf(ZUTI_MOVING_RRR_OBJECTS[i]) > -1) return true;

        if (actor == null && name != null) {
            actor = ZutiSupportMethods_Builder.getActorFromClassName(name);
            if (actor == null) return false;
        }

        if (actor instanceof ZutiTypeRRR) return true;

        return false;
    }

    /**
     * Method fills various data to zuti aircraft object from string tokenizer.
     *
     * @param zac
     * @param stringtokenizer
     * @param zutiMdsSectionIdExists
     * @return
     */
    public static ZutiAircraft fillZutiAircraft(ZutiAircraft zac, StringTokenizer stringtokenizer, boolean zutiMdsSectionIdExists) {
        String weaponLoadouts = "";
        int tokenCounter = 0;
        if (zutiMdsSectionIdExists) // Old AC line parsing that does not contain max fuel selection
            while (stringtokenizer.hasMoreTokens()) {
                switch (tokenCounter) {
                    case 0:
                        // Ac name
                        zac.setAcName(stringtokenizer.nextToken());
                        break;
                    case 1:
                        // max allowed here
                        zac.setNumberOfAircraft(Integer.valueOf(stringtokenizer.nextToken()).intValue());
                        break;
                    default:
                        // weapon loadouts here
                        weaponLoadouts += " " + stringtokenizer.nextToken();
                        break;
                }
                tokenCounter++;
            }
        else // New AC line parsing that also contains max fuel selection
            while (stringtokenizer.hasMoreTokens()) {
                switch (tokenCounter) {
                    case 0:
                        // Ac name
                        zac.setAcName(stringtokenizer.nextToken());
                        break;
                    case 1:
                        // max allowed here
                        zac.setNumberOfAircraft(Integer.valueOf(stringtokenizer.nextToken()).intValue());
                        break;
                    case 2:
                        zac.setMaxFuelSelection(Integer.valueOf(stringtokenizer.nextToken()).intValue());
                        break;
                    default:
                        // weapon loadouts here
                        weaponLoadouts += " " + stringtokenizer.nextToken();
                        break;
                }
                tokenCounter++;
            }

        zac.setLoadedWeapons(weaponLoadouts, false);

        return zac;
    }

    /**
     * Method returns player current location as a String. format: x y (space between).
     *
     * @return
     */
    public static String getPlayerLocation() {
        Aircraft ac = World.getPlayerAircraft();
        if (ac != null) {
            Point3d location = ac.pos.getAbsPoint();
            ZutiSupportMethods_Builder.DECIMAL_FORMAT.applyLocalizedPattern("#.##");
            return ZutiSupportMethods_Builder.DECIMAL_FORMAT.format(location.x) + " " + ZutiSupportMethods_Builder.DECIMAL_FORMAT.format(location.y);
        }

        return "0.0 0.0";
    }

    /**
     * Check if specified point is on any ZAP. If so, method will it's id in the array. Else -1 is returned.
     *
     * @param pos
     * @return
     */
    public static int isOnZAP(Point3d pos) {
        List airports = ZutiSupportMethods_Engine.AIRFIELDS;
        int size = airports.size();
        for (int z = 0; z < size; z++) {
            ZutiAirfieldPoint point = (ZutiAirfieldPoint) airports.get(z);
            {
                double result = point.isInZAPArea(pos.x, pos.y);
                if (result > -1) return z;
            }
        }

        return -1;
    }

    /**
     * Call this method if you want to be sure that Resources related operation is enabled for specified position.
     *
     * @param pos
     * @return
     */
    public static boolean isResourcesManagementValid(Point3d pos) {
        if (Mission.MDS_VARIABLES() == null) return false;

//        System.out.println("Checking position for resources management: " + pos.x + ", " + pos.y);
        BornPlace bp = ZutiSupportMethods_Net.getNearestBornPlace_AnyArmy(pos.x, pos.y);
        if (bp != null && bp.zutiEnableResourcesManagement) // System.out.println(" Resources management is ENABLED!");
            return true;

        int army = -1;
        if (Mission.MDS_VARIABLES().enabledResourcesManagement_BySide) {
            // It is. Increase resources for the side that pos in on.
            army = Front.army(pos.x, pos.y);
//            System.out.println("  Returned army: " + army);
            if (army == 1 || army == 2) // System.out.println(" Resources management is ENABLED!");
                return true;
        }

//        System.out.println("  Resources management is DISABLED!");
        return false;
    }

    public static int[] cloneIntegerArray(int[] inArray) {
        int[] array = new int[inArray.length];
        for (int i = 0; i < array.length; i++)
            array[i] = inArray[i];

        return array;
    }

    /**
     * This method will return you class name for specified i18n name. Name must then be appended to appropriate string as only the LAST BIT of class name is returned. Example: Input = A-20C, Output = A_20C.
     *
     * @param i18nName
     * @return
     */
    public static String getAircraftName_Class(String i18nName) {
        // TODO: Changed by SAS~Storebror to fix incorrect lazy initialization.
        checkInitAircraftClassFromI18N();
//        if (AIRCRAFT_CLASS_FROM_I18N == null) {
//            AIRCRAFT_CLASS_FROM_I18N = new HashMap();
//
//            String className = null;
//            ArrayList aircraftClasses = Main.cur().airClasses;
//            for (int i = 0; i < aircraftClasses.size(); i++) {
//                Class acClass = (Class) aircraftClasses.get(i);
//
//                className = acClass.getName();
//                className = className.substring(className.lastIndexOf(".") + 1, className.length());
//                // System.out.println(" getAircraftClassNameFromI18N - CLASS = " + className);
//                AIRCRAFT_CLASS_FROM_I18N.put(Property.stringValue(acClass, "keyName"), className);
//            }
//        }
        return (String) AIRCRAFT_CLASS_FROM_I18N.get(i18nName);
    }

    // TODO: Added by SAS~Storebror to fix incorrect lazy initialization.
    private synchronized static void checkInitAircraftClassFromI18N() {
        if (AIRCRAFT_CLASS_FROM_I18N != null) return;
        AIRCRAFT_CLASS_FROM_I18N = new HashMap();
        String className = null;
        ArrayList aircraftClasses = Main.cur().airClasses;
        for (int i = 0; i < aircraftClasses.size(); i++) {
            Class acClass = (Class) aircraftClasses.get(i);

            className = acClass.getName();
            className = className.substring(className.lastIndexOf(".") + 1, className.length());
            // System.out.println(" getAircraftClassNameFromI18N - CLASS = " + className);
            AIRCRAFT_CLASS_FROM_I18N.put(Property.stringValue(acClass, "keyName"), className);
        }
    }
    // ---

    /**
     * This method will return you i18n name for specified class name. Class name must be only the LAST BIT of class name. Example: Input = A-20C, Output = A_20C. A-20C is the last but from com.maddox.il2.objects.air.A_20C.
     *
     * @param i18nName
     * @return
     */
    public static String getAircraftName_I18N(String className) {
        if (className.indexOf(".") > -1) className = className.substring(className.lastIndexOf(".") + 1, className.length());
        // System.out.println(" getAircraftI18NFromClass - CLASS = " + className);

        // TODO: Changed by SAS~Storebror to fix incorrect lazy initialization.
        checkInitAircraftI18NFromClass();
//        if (AIRCRAFT_I18N_FROM_CLASS == null) {
//            AIRCRAFT_I18N_FROM_CLASS = new HashMap();
//            String acClassName = null;
//
//            ArrayList aircraftClasses = Main.cur().airClasses;
//            for (int i = 0; i < aircraftClasses.size(); i++) {
//                Class acClass = (Class) aircraftClasses.get(i);
//
//                acClassName = acClass.getName();
//                acClassName = acClassName.substring(acClassName.lastIndexOf(".") + 1, acClassName.length());
//
//                // System.out.println(" class = " + acClassName + ", i18n = " + Property.stringValue(acClass, "keyName"));
//
//                AIRCRAFT_I18N_FROM_CLASS.put(acClassName, Property.stringValue(acClass, "keyName"));
//            }
//        }

        // System.out.println(" Returning i18n for >" + className + "<");
        return (String) AIRCRAFT_I18N_FROM_CLASS.get(className);
    }

    // TODO: Added by SAS~Storebror to fix incorrect lazy initialization.
    private synchronized static void checkInitAircraftI18NFromClass() {
        if (AIRCRAFT_I18N_FROM_CLASS != null) return;
        AIRCRAFT_I18N_FROM_CLASS = new HashMap();
        String acClassName = null;

        ArrayList aircraftClasses = Main.cur().airClasses;
        for (int i = 0; i < aircraftClasses.size(); i++) {
            Class acClass = (Class) aircraftClasses.get(i);

            acClassName = acClass.getName();
            acClassName = acClassName.substring(acClassName.lastIndexOf(".") + 1, acClassName.length());

            // System.out.println(" class = " + acClassName + ", i18n = " + Property.stringValue(acClass, "keyName"));

            AIRCRAFT_I18N_FROM_CLASS.put(acClassName, Property.stringValue(acClass, "keyName"));
        }
    }
    // ---

    /**
     * Method returns null if ac name does not end with _0. Else user name is returned.
     *
     * @param acName
     * @return
     */
    public static String getUserNameFromAcName(String acName) {
        int index = acName.indexOf("_0");
        if (index < 0) return null;

        return acName.substring(0, index);
    }

    /**
     * Method returns weapon I18N name for specified aircraft.
     *
     * @param acName
     * @param name
     * @return
     */
    public static String getWeaponI18NName(String acName, String name) {
        return I18N.weapons(acName, name);
    }

    private static int       debugLevel    = Integer.MIN_VALUE;
    private static final int DEBUG_DEFAULT = 0;

    private static int curDebugLevel() {
        if (debugLevel == Integer.MIN_VALUE) debugLevel = Config.cur.ini.get("Mods", "DEBUG_ZSM", DEBUG_DEFAULT);
        return debugLevel;
    }

    public static void printDebugMessage(String theMessage) {
        if (curDebugLevel() == 0) return;
        System.out.println(theMessage);
    }
}