/* 4.10.1 class */
package com.maddox.il2.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.gwindow.GPoint;
import com.maddox.gwindow.GWindowMessageBox;
import com.maddox.il2.ai.Airport;
import com.maddox.il2.ai.AirportCarrier;
import com.maddox.il2.ai.Army;
import com.maddox.il2.ai.Front;
import com.maddox.il2.ai.UserCfg;
import com.maddox.il2.ai.Wing;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.CellAirField;
import com.maddox.il2.ai.air.CellAirPlane;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.CameraOrtho2D;
import com.maddox.il2.engine.GUIRenders;
import com.maddox.il2.engine.IconDraw;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.engine.Render;
import com.maddox.il2.engine.TTFont;
import com.maddox.il2.fm.ZutiSupportMethods_FM;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.I18N;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.ZutiPadObject;
import com.maddox.il2.game.ZutiRadarRefresh;
import com.maddox.il2.game.ZutiSupportMethods;
import com.maddox.il2.game.ZutiSupportMethods_Multicrew;
import com.maddox.il2.net.BornPlace;
import com.maddox.il2.net.NetUser;
import com.maddox.il2.net.ZutiSupportMethods_Net;
import com.maddox.il2.net.ZutiSupportMethods_NetSend;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.ships.TestRunway;
import com.maddox.il2.objects.vehicles.artillery.RocketryRocket;
import com.maddox.il2.objects.vehicles.radios.Beacon;
import com.maddox.il2.objects.vehicles.radios.Beacon.LorenzBLBeacon;
import com.maddox.il2.objects.vehicles.radios.Beacon.LorenzBLBeacon_AAIAS;
import com.maddox.il2.objects.vehicles.radios.Beacon.LorenzBLBeacon_LongRunway;
import com.maddox.il2.objects.vehicles.radios.Beacon.RadioBeacon;
import com.maddox.il2.objects.vehicles.radios.Beacon.RadioBeaconLowVis;
import com.maddox.il2.objects.vehicles.radios.Beacon.YGBeacon;
import com.maddox.il2.objects.vehicles.radios.TypeHasBeacon;
import com.maddox.il2.objects.vehicles.radios.TypeHasRadioStation;
import com.maddox.rts.CmdEnv;
import com.maddox.rts.NetEnv;
import com.maddox.rts.ObjIO;
import com.maddox.rts.Property;
import com.maddox.rts.SectFile;
import com.maddox.util.NumberTokenizer;

public class GUIBriefing extends GUIBriefingGeneric {
    protected String              playerName;
    protected ArrayList           playerPath  = new ArrayList();
    protected ArrayList           targets     = new ArrayList();
    protected Mat                 iconBornPlace;
    protected Mat                 iconPlayer;
    private static String[]       tip         = new String[3];
    private float[]               lineNXYZ    = new float[6];
    private int                   lineNCounter;
    protected boolean             bSelectBorn = false;

    protected java.util.ArrayList beacons;
    protected int playerArmy;

    // TODO: Variables by |ZUTI|
    // ------------------------------------------------------
    public static boolean ZUTI_IS_BRIEFING_ACTIVE = false;
    // ------------------------------------------------------

    public static class BeaconPoint {

        public float                     x;
        public float                     y;
        public Mat icon;
        public int                       army;
        public String          id;

        public BeaconPoint() {
        }
    }

    public static class TargetPoint {
        public float    x;
        public float    y;
        public float    z                    = 0.0F;
        public int      r;
        public int      type;
        public int      typeOArmy;
        public int      importance;
        public Mat      icon;
        public Mat      iconOArmy;
        public String   nameTarget;
        public String   nameTargetOrig;
        public Actor    actor;
        public boolean  isBaseActorWing      = false;
        public Wing     wing                 = null;

        private boolean visibleForPlayerArmy = false;
        private String  zutiDesignation      = null;

        public TargetPoint() {
        }

        public boolean isGroundUnit() {
            return ZutiPadObject.isGroundUnit(this.actor);
        }

        public boolean getIsAlive() {
            if (this.actor == null) return false;

            if (this.actor instanceof RocketryRocket) return !((RocketryRocket) this.actor).isDamaged();

            return !this.actor.getDiedFlag();
        }

        /**
         * Set targetpoint designation that correcponds to target designation.
         *
         * @return
         */
        public void zutiSetDesignation(String value) {
            this.zutiDesignation = value;
        }

        /**
         * Get targetpoint designation that correcponds to target designation.
         *
         * @return
         */
        public String zutiGetDesignation() {
            return this.zutiDesignation;
        }

        public boolean equals(Object o) {
            if (!(o instanceof TargetPoint)) return false;

            TargetPoint inKeyObject = (TargetPoint) o;

            if (this.actor != null) {
                if (this.actor.equals(inKeyObject.actor)) return true;
            } else if (this.nameTargetOrig.equals(inKeyObject.nameTargetOrig)) return true;

            return false;
        }

        public int hashCode() {
            if (this.actor != null) return this.actor.hashCode();
            else return this.nameTargetOrig.hashCode();
        }

        /**
         * This method refreshes object position if moving icons are supported, if player side has radars and if it is visible for at least one radar!
         */
        public void refreshPosition() {
            // If player has no radars, do not update target position!
            if (!Mission.MDS_VARIABLES().zutiIcons_MovingIcons || !Mission.MDS_VARIABLES().zutiRadar_PlayerSideHasRadars || !this.isVisibleForPlayerArmy()) return;

            // If this difficulty flag is NOT set, draw moving icons all the time
            if (this.actor != null && this.actor.pos != null) {
                Point3d position = this.actor.pos.getAbsPoint();
                // Moving targets icon location
                this.x = (float) position.x;
                this.y = (float) position.y;
                this.z = (float) position.z;
            }
        }

        public boolean isVisibleForPlayerArmy() {
            return this.visibleForPlayerArmy;
        }

        public void setVisibleForPlayerArmy(boolean value) {
            this.visibleForPlayerArmy = value;
        }
    }

    private static class PathPoint {
        public int   type;
        public float x;
        public float y;
        public float z;

        private PathPoint() {
        }
    }

    public void _enter() {
        this.playerPath.clear();
        this.playerName = null;
        super._enter();

        // TODO: Kill RRR timers
        ZutiSupportMethods_FM.resetRRRTimers();
        // Reset radars!
        ZutiRadarRefresh.findRadars(ZutiSupportMethods.getPlayerArmy());
        // Reset start timers
        ZutiRadarRefresh.resetStartTimes();
        // Set this variable to true
        GUIBriefing.ZUTI_IS_BRIEFING_ACTIVE = true;
        // Enable user to change cockpit again
        ZutiSupportMethods_Multicrew.CAN_REQUEST_COCKPIT_CHANGE = true;
        // Get paratroopers captured home bases
        ZutiSupportMethods_NetSend.requestFrontMarkers();
        // Get paratroopers captured home bases
        ZutiSupportMethods_NetSend.requestParaCapturedHbList();
        // Get unavailable planes
        ZutiSupportMethods_NetSend.requestAircraftList();
        // Get unavailable planes
        ZutiSupportMethods_NetSend.requestAircraftListWithReleasedOrdinance();
        // Get deactivated TInspect targets
        ZutiSupportMethods_NetSend.requestDeactivatedTargetsList();
        // Set this flag to false as soon as user enters this screen
        NetAircraft.ZUTI_REFLY_OWERRIDE = false;
        // Fill ground targets. Use different approach because Engine.targets actors
        // are without icons if actor is ground unit...
        ZutiSupportMethods_GUI.fillGroundChiefsArray(GUI.pad);
        // Sync craters data
        if (!ZutiSupportMethods_GUI.ZUTI_CRATERS_SYNCED) {
            ZutiSupportMethods_GUI.ZUTI_CRATERS_SYNCED = true;
            ZutiSupportMethods_NetSend.requestCraters();
        }
        // if( Main.cur().mission != null )
        // System.out.println("GUIBriefing: radar statistics. Red radars: " + Main.cur().mission.zutiRadar_CountRed + ", blue radars: " + Main.cur().mission.zutiRadar_CountBlue);

        // Check if BornPlaces were overrun by any chance
        Front.setMarkersChanged();
        ZutiSupportMethods.checkIfAnyBornPlaceWasOverrun();

        this.fillBeacons();
    }

    private void fillBeacons() {
        SectFile sectfile = Main.cur().currentMissionFile;
        int playerArmy = -1;
        if (Mission.isDogfight()) playerArmy = ((NetUser) NetEnv.host()).getArmy();
        else playerArmy = sectfile.get("MAIN", "army", 0);
        this.beacons.clear();
        int j = sectfile.sectionIndex("NStationary");
        if (j < 0) return;
        int k = sectfile.vars(j);
        for (int l = 0; l < k; l++) {
            NumberTokenizer numbertokenizer = new NumberTokenizer(sectfile.line(j, l));
            BeaconPoint beaconpoint = this.loadbeacon(playerArmy, numbertokenizer.next(""), numbertokenizer.next(""), numbertokenizer.next(0), numbertokenizer.next(0.0D), numbertokenizer.next(0.0D));
            if (beaconpoint != null) this.beacons.add(beaconpoint);
        }

    }

    private BeaconPoint loadbeacon(int i, String s, String s1, int j, double d, double d1) {
//        if (i != j) return null;
        Class class1 = null;
        try {
            class1 = ObjIO.classForName(s1);
        } catch (Exception exception) {
            System.out.println("Mission: class '" + s1 + "' not found");
            return null;
        }
        if((TypeHasRadioStation.class).isAssignableFrom(class1))
        {
            if(i != j && j != 0)
                return null;
        } else
        if(i != j)
            return null;
        if (TypeHasBeacon.class.isAssignableFrom(class1)) {
            BeaconPoint beaconpoint = new BeaconPoint();
            beaconpoint.x = (float) d;
            beaconpoint.y = (float) d1;
            beaconpoint.army = j;
            String s2 = Beacon.getBeaconID(this.beacons.size());
            beaconpoint.id = s2;
            if (RadioBeacon.class.isAssignableFrom(class1)) beaconpoint.icon = IconDraw.get("icons/beacon.mat");
            else if (RadioBeaconLowVis.class.isAssignableFrom(class1)) beaconpoint.icon = IconDraw.get("icons/beacon.mat");
            else if (YGBeacon.class.isAssignableFrom(class1)) beaconpoint.icon = IconDraw.get("icons/beaconYG.mat");
            else if (LorenzBLBeacon.class.isAssignableFrom(class1)) beaconpoint.icon = IconDraw.get("icons/ILS.mat");
            else if (LorenzBLBeacon_LongRunway.class.isAssignableFrom(class1)) beaconpoint.icon = IconDraw.get("icons/ILS.mat");
            else if (LorenzBLBeacon_AAIAS.class.isAssignableFrom(class1)) beaconpoint.icon = IconDraw.get("icons/ILS.mat");
            else if (TypeHasRadioStation.class.isAssignableFrom(class1))
            {
                beaconpoint.icon = IconDraw.get("icons/RadioStation.mat");
                beaconpoint.id = "";
            } else
            {
                return null;
            }
            return beaconpoint;
        } else return null;
    }

    public void drawBeacons(GUIRenders guirenders, TTFont ttfont, Mat mat, CameraOrtho2D cameraortho2d, java.util.ArrayList arraylist) {
        int i = arraylist.size();
        if (i == 0) return;
        for (int j = 0; j < i; j++) {
            BeaconPoint beaconpoint = (BeaconPoint) arraylist.get(j);
            int k = 0;
            if(beaconpoint.army == 0)
                k = Army.color(playerArmy);
            else
                k = Army.color(beaconpoint.army);
            IconDraw.setColor(k);
            if (beaconpoint.icon == null) continue;
            float f = (float) ((beaconpoint.x - cameraortho2d.worldXOffset) * cameraortho2d.worldScale);
            float f1 = (float) ((beaconpoint.y - cameraortho2d.worldYOffset) * cameraortho2d.worldScale);
            IconDraw.render(beaconpoint.icon, f, f1);
            if (cameraortho2d.worldScale > 0.02D) {
                float f2 = 20F;
                float f3 = 15F;
                this.gridFont.output(k, f + f2, f1 - f3, 0.0F, beaconpoint.id);
            }
        }

    }

    private void drawBeacons() {
        if (World.cur().diffCur.RealisticNavigationInstruments) this.drawBeacons(this.renders, this.gridFont, this.emptyMat, this.cameraMap2D, this.beacons);
    }

    // TODO: Added by |ZUTI|: override parent _leave() method
    // --------------------------------------------------
    public void _leave() {
        super._leave();

        GUIBriefing.ZUTI_IS_BRIEFING_ACTIVE = false;
    }

    // --------------------------------------------------

    private void drawBornPlaces() {
        if (this.iconBornPlace != null) {
            ArrayList arraylist = World.cur().bornPlaces;
            // System.out.println("Number of born places: " + new Integer(arraylist.size()).toString());

            if (arraylist != null && arraylist.size() != 0) {
                int i = arraylist.size();
                BornPlace bornplace = null;
                for (int i_0_ = 0; i_0_ < i; i_0_++) {
                    bornplace = (BornPlace) arraylist.get(i_0_);
                    bornplace.tmpForBrief = 0;
                }
                NetUser netuser = (NetUser) NetEnv.host();
                int i_1_ = netuser.getBornPlace();
                if (i_1_ >= 0 && i_1_ < i) {
                    bornplace = (BornPlace) arraylist.get(i_1_);
                    bornplace.tmpForBrief = 1;
                }
                List list = NetEnv.hosts();
                // TODO: Added by |ZUTI|: extracted size to separate variable
                int hostsSize = list.size();
                for (int i_2_ = 0; i_2_ < hostsSize; i_2_++) {
                    netuser = (NetUser) list.get(i_2_);
                    int i_4_ = netuser.getBornPlace();
                    if (i_4_ >= 0 && i_4_ < i) {
                        bornplace = (BornPlace) arraylist.get(i_4_);
                        bornplace.tmpForBrief++;
                    }
                }
                for (int i_5_ = 0; i_5_ < i; i_5_++) {
                    bornplace = (BornPlace) arraylist.get(i_5_);

                    if (bornplace.zutiDisableRendering) continue;

                    IconDraw.setColor(Army.color(bornplace.army));

                    float f = (float) ((bornplace.place.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                    float f_6_ = (float) ((bornplace.place.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);

                    // ZUTI: moving born places stay at the same location on the brief screen
                    if (bornplace.zutiStaticPositionOnly) {
                        f = (float) ((bornplace.zutiOriginalX - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                        f_6_ = (float) ((bornplace.zutiOriginalY - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
                    }

                    IconDraw.render(this.iconBornPlace, f, f_6_);
                    if (i_5_ == i_1_ && this.iconPlayer != null) Render.drawTile(f, f_6_, IconDraw.scrSizeX(), IconDraw.scrSizeY(), 0.0F, this.iconPlayer, Army.color(bornplace.army), 0.0F, 1.0F, 1.0F, -1.0F);

                    // TODO: Modified by |ZUTI|
                    // if (bornplace.tmpForBrief > 0)
                    if (bornplace.tmpForBrief > 0 && !Mission.MDS_VARIABLES().zutiMisc_HidePlayersCountOnHomeBase)
                        this.gridFont.output(Army.color(bornplace.army), (int) f + IconDraw.scrSizeX() / 2 + 2, (int) f_6_ - IconDraw.scrSizeY() / 2 - 2, 0.0F, "" + bornplace.tmpForBrief);
                }
            }
        }
    }

    public static void fillTargets(SectFile sectfile, ArrayList arraylist) {
        arraylist.clear();
        int i = sectfile.sectionIndex("Target");
        if (i >= 0) {
            int i_7_ = sectfile.vars(i);
            for (int i_8_ = 0; i_8_ < i_7_; i_8_++) {
                NumberTokenizer numbertokenizer = new NumberTokenizer(sectfile.line(i, i_8_));
                int i_9_ = numbertokenizer.next(0, 0, 7);
                int i_10_ = numbertokenizer.next(0, 0, 2);
                if (i_10_ != 2) {
                    TargetPoint targetpoint = new TargetPoint();
                    targetpoint.type = i_9_;
                    targetpoint.importance = i_10_;
                    numbertokenizer.next(0);
                    numbertokenizer.next(0, 0, 720);
                    numbertokenizer.next(0);
                    targetpoint.x = numbertokenizer.next(0);
                    targetpoint.y = numbertokenizer.next(0);
                    int i_13_ = numbertokenizer.next(0);
                    if (targetpoint.type == 3 || targetpoint.type == 6 || targetpoint.type == 1) {
                        if (i_13_ < 50) i_13_ = 50;
                        if (i_13_ > 3000) i_13_ = 3000;
                    }
                    targetpoint.r = i_13_;
                    numbertokenizer.next(0);
                    targetpoint.nameTarget = numbertokenizer.next((String) null);
                    if (targetpoint.nameTarget != null && targetpoint.nameTarget.startsWith("Bridge")) targetpoint.nameTarget = null;
                    int i_15_ = numbertokenizer.next(0);
                    int i_16_ = numbertokenizer.next(0);
                    if (i_15_ != 0 && i_16_ != 0) {
                        targetpoint.x = i_15_;
                        targetpoint.y = i_16_;
                    }

                    switch (targetpoint.type) {
                        case 0:
                            targetpoint.icon = IconDraw.get("icons/tdestroyair.mat");
                            if (targetpoint.nameTarget != null && sectfile.exist("Chiefs", targetpoint.nameTarget)) targetpoint.icon = IconDraw.get("icons/tdestroychief.mat");
                            break;
                        case 1:
                            targetpoint.icon = IconDraw.get("icons/tdestroyground.mat");
                            break;
                        case 2:
                            targetpoint.icon = IconDraw.get("icons/tdestroybridge.mat");
                            targetpoint.nameTarget = null;
                            break;
                        case 3:
                            targetpoint.icon = IconDraw.get("icons/tinspect.mat");
                            break;
                        case 4:
                            targetpoint.icon = IconDraw.get("icons/tescort.mat");
                            break;
                        case 5:
                            targetpoint.icon = IconDraw.get("icons/tdefence.mat");
                            break;
                        case 6:
                            targetpoint.icon = IconDraw.get("icons/tdefenceground.mat");
                            break;
                        case 7:
                            targetpoint.icon = IconDraw.get("icons/tdefencebridge.mat");
                            targetpoint.nameTarget = null;
                            break;
                    }
                    if (targetpoint.nameTarget != null) if (sectfile.exist("Chiefs", targetpoint.nameTarget)) try {
                        StringTokenizer stringtokenizer = new StringTokenizer(sectfile.get("Chiefs", targetpoint.nameTarget, (String) null));
                        String string = stringtokenizer.nextToken();
                        int i_17_ = string.indexOf(".");
                        targetpoint.nameTarget = I18N.technic(string.substring(0, i_17_)) + " " + I18N.technic(string.substring(i_17_ + 1));
                    } catch (Exception exception) {
                        targetpoint.nameTarget = null;
                    }
                    else if (sectfile.sectionIndex(targetpoint.nameTarget) >= 0) try {
                        String string = sectfile.get(targetpoint.nameTarget, "Class", (String) null);
                        Class var_class = ObjIO.classForName(string);
                        targetpoint.nameTarget = Property.stringValue((Object) var_class, "iconFar_shortClassName", null);
                    } catch (Exception exception) {
                        targetpoint.nameTarget = null;
                    }
                    else targetpoint.nameTarget = null;
                    arraylist.add(targetpoint);
                }
            }
        }
    }

    public static void drawTargets(GUIRenders guirenders, TTFont ttfont, Mat mat, CameraOrtho2D cameraortho2d, ArrayList arraylist) {
        int i = arraylist.size();
        if (i != 0) {
            GPoint gpoint = guirenders.getMouseXY();
            int i_18_ = -1;
            float f = gpoint.x;
            float f_19_ = guirenders.win.dy - 1.0F - gpoint.y;
            float f_20_ = IconDraw.scrSizeX() / 2;
            float f_21_ = f;
            float f_22_ = f_19_;
            IconDraw.setColor(-16711681);
            for (int i_23_ = 0; i_23_ < i; i_23_++) {
                TargetPoint targetpoint = (TargetPoint) arraylist.get(i_23_);
                if (targetpoint.icon != null) {
                    float f_24_ = (float) ((targetpoint.x - cameraortho2d.worldXOffset) * cameraortho2d.worldScale);
                    float f_25_ = (float) ((targetpoint.y - cameraortho2d.worldYOffset) * cameraortho2d.worldScale);
                    IconDraw.render(targetpoint.icon, f_24_, f_25_);
                    if (f_24_ >= f - f_20_ && f_24_ <= f + f_20_ && f_25_ >= f_19_ - f_20_ && f_25_ <= f_19_ + f_20_) {
                        i_18_ = i_23_;
                        f_21_ = f_24_;
                        f_22_ = f_25_;
                    }
                }
            }
            if (i_18_ != -1) {
                TargetPoint targetpoint = (TargetPoint) arraylist.get(i_18_);
                for (int i_26_ = 0; i_26_ < 3; i_26_++)
                    tip[i_26_] = null;
                if (targetpoint.importance == 0) tip[0] = I18N.gui("brief.Primary");
                else tip[0] = I18N.gui("brief.Secondary");
                switch (targetpoint.type) {
                    case 0:
                        tip[1] = I18N.gui("brief.Destroy");
                        break;
                    case 1:
                        tip[1] = I18N.gui("brief.DestroyGround");
                        break;
                    case 2:
                        tip[1] = I18N.gui("brief.DestroyBridge");
                        break;
                    case 3:
                        tip[1] = I18N.gui("brief.Inspect");
                        break;
                    case 4:
                        tip[1] = I18N.gui("brief.Escort");
                        break;
                    case 5:
                        tip[1] = I18N.gui("brief.Defence");
                        break;
                    case 6:
                        tip[1] = I18N.gui("brief.DefenceGround");
                        break;
                    case 7:
                        tip[1] = I18N.gui("brief.DefenceBridge");
                        break;
                }
                if (targetpoint.nameTarget != null) tip[2] = targetpoint.nameTarget;
                float f_27_ = ttfont.width(tip[0]);
                int i_28_ = 1;
                for (int i_29_ = 1; i_29_ < 3; i_29_++) {
                    if (tip[i_29_] == null) break;
                    i_28_ = i_29_;
                    float f_30_ = ttfont.width(tip[i_29_]);
                    if (f_27_ < f_30_) f_27_ = f_30_;
                }
                float f_31_ = -ttfont.descender();
                float f_32_ = ttfont.height() + f_31_;
                f_27_ += 2.0F * f_31_;
                float f_33_ = f_32_ * (i_28_ + 1) + 2.0F * f_31_;
                float f_34_ = f_21_ - f_27_ / 2.0F;
                float f_35_ = f_22_ + f_20_;
                if (f_34_ + f_27_ > guirenders.win.dx) f_34_ = guirenders.win.dx - f_27_;
                if (f_35_ + f_33_ > guirenders.win.dy) f_35_ = guirenders.win.dy - f_33_;
                if (f_34_ < 0.0F) f_34_ = 0.0F;
                if (f_35_ < 0.0F) f_35_ = 0.0F;
                Render.drawTile(f_34_, f_35_, f_27_, f_33_, 0.0F, mat, -813694977, 0.0F, 0.0F, 1.0F, 1.0F);
                Render.drawEnd();
                for (int i_36_ = 0; i_36_ <= i_28_; i_36_++)
                    ttfont.output(-16777216, f_34_ + f_31_, f_35_ + f_31_ + (i_28_ - i_36_) * f_32_ + f_31_, 0.0F, tip[i_36_]);
            }
        }
    }

    /*
     * private void drawTargets() { drawTargets(renders, gridFont, emptyMat, cameraMap2D, targets); }
     */

    private Mat getIconAir(int i) {
        String string;
        switch (i) {
            case 0:
                string = "normfly";
                break;
            case 1:
                string = "takeoff";
                break;
            case 2:
                string = "landing";
                break;
            case 3:
                string = "gattack";
                break;
            default:
                return null;
        }
        return IconDraw.get("icons/" + string + ".mat");
    }

    private void drawPlayerPath() {
        this.checkPlayerPath();
        int i = this.playerPath.size();
        if (i == 0) return;
        if (this.lineNXYZ.length / 3 <= i) this.lineNXYZ = new float[(i + 1) * 3];
        this.lineNCounter = 0;
        for (int j = 0; j < i; j++) {
            PathPoint pathpoint = (PathPoint) this.playerPath.get(j);
            this.lineNXYZ[this.lineNCounter * 3 + 0] = (float) ((pathpoint.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
            this.lineNXYZ[this.lineNCounter * 3 + 1] = (float) ((pathpoint.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
            this.lineNXYZ[this.lineNCounter * 3 + 2] = 0.0F;
            this.lineNCounter++;
        }

        Render.drawBeginLines(-1);
        Render.drawLines(this.lineNXYZ, this.lineNCounter, 2.0F, 0xff000000, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE | Mat.BLEND, 3);
        Render.drawEnd();
        IconDraw.setColor(0xff00ffff);
        float f = 0.0F;
        for (int k = 0; k < i; k++) {
            PathPoint pathpoint1 = (PathPoint) this.playerPath.get(k);
            float f1 = (float) ((pathpoint1.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
            float f2 = (float) ((pathpoint1.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
            IconDraw.render(this.getIconAir(pathpoint1.type), f1, f2);
//            if (k == i - 1) this.gridFont.output(0xff000000, (int) f1 + IconDraw.scrSizeX() / 2 + 2, (int) f2 - IconDraw.scrSizeY() / 2 - 2, 0.0F, "" + (k + 1));
            if(k == i - 1) waypointFont.output(0xff000000, (int)f1 + IconDraw.scrSizeX() / 2 + 2, (int)f2 - IconDraw.scrSizeY() / 2 - 2, 0.0F, (k + 1) + ".");
            float f3 = 1.0F - this.curScale / 7F;
            if (k >= i - 1) continue;
            int l = (int)pathpoint1.z;
            PathPoint pathpoint2 = (PathPoint) this.playerPath.get(k + 1);
            Point3f point3f = new Point3f(pathpoint1.x, pathpoint1.y, 0.0F);
            Point3f point3f1 = new Point3f(pathpoint2.x, pathpoint2.y, 0.0F);
            point3f.sub(point3f1);
            float f4 = 57.32484F * (float) Math.atan2(point3f.x, point3f.y);
            for (f4 = (f4 + 180F) % 360F; f4 < 0.0F; f4 += 360F)
                ;
            for (; f4 >= 360F; f4 -= 360F)
                ;
            f4 = Math.round(f4);
            float f5 = 0.0F;
            float f6 = 0.0F;
            if (f4 >= 0.0F && f4 < 90F) {
                f5 = 15F;
                f6 = -40F;
                if (f >= 270F && f <= 360F) {
//                    f5 = -70F;
//                    f6 = 60F;
                    f5 = -35F * bigFontMultip;
                    f6 = 40F * bigFontMultip;
                }
            } else if (f4 >= 90F && f4 < 180F) {
                f5 = 15F;
//                f6 = 60F;
                f6 = 40F * bigFontMultip;
                if (f >= 180F && f < 270F) {
//                    f5 = -70F;
                    f5 = -35F * bigFontMultip;
                    f6 = -15F;
                }
            } else if (f4 >= 180F && f4 < 270F) {
//                f5 = -70F;
//                f6 = 60F;
                f5 = -35F * bigFontMultip;
                f6 = 40F * bigFontMultip;
                if (f >= 90F && f < 180F) {
                    f5 = 15F;
//                    f6 = 60F;
                    f6 = 40F * bigFontMultip;
                }
            } else if (f4 >= 270F && f4 <= 360F) {
//                f5 = -70F;
                f5 = -35F * bigFontMultip;
                f6 = -40F;
                if (f >= 0.0F && f < 90F) {
                    f5 = 15F;
                    f6 = -40F;
                }
            }
            f5 *= f3;
            f6 *= f3;
            if (this.curScale >= 3) {
                if (f5 < 0.0F) f5 /= 2.0F;
                if (f6 > 0.0F) f6 /= 2.0F;
            }
            f = f4;
//            this.gridFont.output(0xff000000, f1 + f5, f2 + f6, 0.0F, "" + (k + 1));
            waypointFont.output(0xff000000, f1 + f5, f2 + f6, 0.0F, (k + 1) + ".");
            if (this.curScale >= 2) continue;
            double d = Math.sqrt(point3f.y * point3f.y + point3f.x * point3f.x) / 1000D;
            if (d < 0.5D) continue;
            String s = " km";
            String s1 = "m";
            if (HUD.drawSpeed() == 2 || HUD.drawSpeed() == 3) {
                d *= 0.53995698690414429D;
                s = " nm";
                l = (int)((double)l * 3.28084D);
                s1 = "ft";
            }
            String s2 = "" + d;
            s2 = s2.substring(0, s2.indexOf(".") + 2);
//            this.gridFont.output(0xff000000, f1 + f5, f2 + f6 - 22F, 0.0F, (int) f4 + "\260");
//            this.gridFont.output(0xff000000, f1 + f5, f2 + f6 - 44F, 0.0F, s2 + s);
            waypointFont.output(0xff000000, f1 + f5, (f2 + f6) - 12F * bigFontMultip, 0.0F, l + s1);
            waypointFont.output(0xff000000, f1 + f5, (f2 + f6) - 24F * bigFontMultip, 0.0F, (int)f4 + "\260");
            waypointFont.output(0xff000000, f1 + f5, (f2 + f6) - 36F * bigFontMultip, 0.0F, s2 + s);
        }

    }

    private void checkPlayerPath() {
        SectFile sectfile = Main.cur().currentMissionFile;
        String s;
        if (Mission.isCoop()) {
            s = GUINetAircraft.selectedWingName();
            if (s == null) s = sectfile.get("MAIN", "player", (String) null);
        } else s = sectfile.get("MAIN", "player", (String) null);
        if (s == null) {
            if (this.playerName != null) {
                this.playerPath.clear();
                this.playerName = null;
            }
        } else if (!s.equals(this.playerName)) {
            this.playerName = s;
            this.playerPath.clear();
            if (this.playerName != null) {
                int i = sectfile.sectionIndex(this.playerName + "_WAY");
                if (i >= 0) {
                    int j = sectfile.vars(i);
                    for (int k = 0; k < j; k++) {
                        PathPoint pathpoint = new PathPoint();
                        String s1 = sectfile.var(i, k);
//                        if ("NORMFLY".equals(s1)) pathpoint.type = 0;
//                        else if ("TAKEOFF".equals(s1)) pathpoint.type = 1;
//                        else if ("LANDING".equals(s1)) pathpoint.type = 2;
//                        else if ("GATTACK".equals(s1)) pathpoint.type = 3;
//                        else pathpoint.type = 0;
                        if(s1.startsWith("TRIGGERS")) continue;
                        if(s1.startsWith("NORMFLY")) pathpoint.type = 0;
                        else if(s1.startsWith("TAKEOFF")) {
                            if(s1.startsWith("TAKEOFF_004") || s1.startsWith("TAKEOFF_005")) continue;
                            pathpoint.type = 1;
                        } else if(s1.startsWith("LANDING")) pathpoint.type = 2;
                        else if(s1.startsWith("GATTACK")) pathpoint.type = 3;
                        else pathpoint.type = 0;
                        String s2 = sectfile.value(i, k);
                        if (s2 == null || s2.length() <= 0) pathpoint.x = pathpoint.y = 0.0F;
                        else {
                            NumberTokenizer numbertokenizer = new NumberTokenizer(s2);
                            pathpoint.x = numbertokenizer.next(-1.0E30F, -1.0E30F, 1.0E30F);
                            pathpoint.y = numbertokenizer.next(-1.0E30F, -1.0E30F, 1.0E30F);
                            pathpoint.z = numbertokenizer.next(-1E+030F, -1E+030F, 1E+030F);
                            numbertokenizer.next(0.0, 0.0, 10000.0);
                            numbertokenizer.next(0.0, 0.0, 1000.0);
                        }
                        this.playerPath.add(pathpoint);
                    }
                }
            }
        }
    }

    protected void doRenderMap2D() {
        // TODO: Added by |ZUTI|
        ZutiRadarRefresh.update();

//        int i = (int) Math.round(ZutiMDSVariables.ZUTI_ICON_SIZE * (double) this.client.root.win.dx / 1024.0);
        int i = (int)Math.round((32D * (double)client.root.win.dx) / 1024D);
        float f = client.root.win.dx / client.root.win.dy;
        float f1 = 1.333333F / f;
        i = (int)((float)i * f1);
        IconDraw.setScrSize(i, i);

        // TODO: Edited by |ZUTI|: Join rendering of briefing the same way for all game modes
        try {
            if (!Mission.MDS_VARIABLES().zutiIcons_MovingIcons || Mission.MDS_VARIABLES().zutiIcons_MovingIcons) ZutiSupportMethods_GUI.drawTargets(this.renders, this.gridFont, this.emptyMat, this.cameraMap2D);
            // Draw born places on top of target icons so it is more clear
            this.drawBornPlaces();

            // Draw player path
            this.drawPlayerPath();

            this.drawBeacons();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected int findBornPlace(float x, float y) {
        if (this.id() != 40 && this.id() != 39) return -1;

        ArrayList arraylist = World.cur().bornPlaces;
        if (arraylist == null || arraylist.size() == 0) return -1;

        int i = arraylist.size();
        double d = IconDraw.scrSizeX() / 2 / this.cameraMap2D.worldScale;
        d = 2.0 * d * d;

        // System.out.println("Mouse location: " + new Float(f).toString() + ", " + new Float(f_45_).toString());
        for (int index = 0; index < i; index++) {
            BornPlace bornplace = (BornPlace) arraylist.get(index);

            if (bornplace.zutiDisableSpawning) continue;

            // System.out.println("BornPlace name - " + bornplace.bpName + ", loc: " + new Double(bornplace.place.x).toString() + ", " + new Double(bornplace.place.y).toString());

            if ((bornplace.place.x - x) * (bornplace.place.x - x) + (bornplace.place.y - y) * (bornplace.place.y - y) < d && bornplace.army != 0) // System.out.println("Returning BP in array indexed - " + new Integer(i_46_).toString());
                return index;
        }
        return -1;
    }

    protected boolean isBornPlace(float f, float f1) {
        return this.findBornPlace(f, f1) >= 0;
    }

    protected void setBornPlace(float f, float f1) {
        int i = this.findBornPlace(f, f1);
        if (i >= 0) {
            // Selected HomeBase is valid, change country markings to default. If user changes those in Arming screen, everything is ok! If not... defaults for him!
            BornPlace bp = (BornPlace) World.cur().bornPlaces.get(i);

            // Can user even join this born place?If not, just return -1...
            if (!ZutiSupportMethods_Net.canUserJoinBornPlace(bp)) {
                new GWindowMessageBox(Main3D.cur3D().guiManager.root, 20.0F, true, this.i18n("mds.info.bornPlace"), this.i18n("mds.info.bornPlaceFull"), 3, 0.0F);
                return;
            }

            if (ZutiSupportMethods_Net.getBornPlaceAvailableAircraftList(bp).size() <= 0) {
                new GWindowMessageBox(Main3D.cur3D().guiManager.root, 20.0F, true, this.i18n("mds.info.bornPlace"), this.i18n("mds.info.bornPlaceNoAvailableAc"), 3, 0.0F);
                return;
            }
            /*
             * UserCfg usercfg = World.cur().userCfg; if( usercfg != null && !ZutiSupportMethods.isRegimentValidForSelectedHB (usercfg.netRegiment, bp) ) { String branch = ZutiSupportMethods.getHomeBaseFirstCountry(bp);
             * //System.out.println("GUIBriefing - returned branch 1: " + branch); branch = ZutiSupportMethods.getUserCfgRegiment( branch ); //System.out.println("GUIBriefing - returned branch 2: " + branch); //String countryReturned =
             * .Regiment.getCountryFromBranch ((branch.intern())); //System.out.println("GUIBriefing - returned country: " + countryReturned); usercfg.netRegiment = branch; usercfg.netSquadron = 0; }
             */
            NetUser netuser = (NetUser) NetEnv.host();
            int userArmy = netuser.getArmy();
            netuser.setBornPlace(i);
            wScrollDescription.resized();

            if (userArmy != netuser.getArmy() && this.briefSound != null) {
                String string = Main.cur().currentMissionFile.get("MAIN", "briefSound" + netuser.getArmy());
                if (string != null) {
                    this.briefSound = string;
                    CmdEnv.top().exec("music LIST " + this.briefSound);
                }
            }

            // TODO: Added by |ZUTI|: when player changes his army, reset radars!
            // --------------------------------------------------
            ZutiRadarRefresh.findRadars(ZutiSupportMethods.getPlayerArmy());

            // Since radars were refetched and player army might change,
            // we have to re-check also scout validity...
            ZutiSupportMethods_GUI.fillAirInterval(GUI.pad, true);
            // ----------------------------------------------------

            this.fillBeacons();
        }
    }

    protected void doMouseButton(int i, boolean bool, float f, float f1) {
        int i_51_ = i;
        if (this.renders != null) {
            /* empty */
        }
        if (i_51_ == 0) {
            this.bLPressed = bool;
            if (this.bSelectBorn) {
                if (this.bLPressed) {
                    float f_52_ = (float) (this.cameraMap2D.worldXOffset + f / this.cameraMap2D.worldScale);
                    float f_53_ = (float) (this.cameraMap2D.worldYOffset + (this.renders.win.dy - f1 - 1.0F) / this.cameraMap2D.worldScale);
                    this.setBornPlace(f_52_, f_53_);
                }
                return;
            }
        }
        super.doMouseButton(i, bool, f, f1);
    }

    protected void doMouseMove(float f, float f1) {
//        System.out.println("GUIBriefing doMouseMove(" + f + ", " + f1 + ")");
        if (this.bLPressed && !this.bSelectBorn) super.doMouseMove(f, f1);
        else {
            float f2 = (float) (this.cameraMap2D.worldXOffset + f / this.cameraMap2D.worldScale);
            float f3 = (float) (this.cameraMap2D.worldYOffset + (this.renders.win.dy - f1 - 1.0F) / this.cameraMap2D.worldScale);
            this.bSelectBorn = this.isBornPlace(f2, f3);
            this.renders.mouseCursor = this.bSelectBorn ? 2 : 3;
        }
    }

    protected void fillMap() throws Exception {
        super.fillMap();
        SectFile sectfile = Main.cur().currentMissionFile;

        // TODO: Comment by |ZUTI|: Join rendering of briefing the same way for all game modes
        try {
            this.iconBornPlace = IconDraw.get("icons/born.mat");
            this.iconPlayer = IconDraw.get("icons/player.mat");

            // TODO: Comment by |ZUTI|: targets were not yet drawn
            ZutiSupportMethods_GUI.setTargetsLoaded(false);
            // TODO: Comment by |ZUTI|: Fill targets from mission file but only if mission is NOT single player
            if (!(Mission.cur() == null)) ZutiSupportMethods_GUI.fillTargetPoints(sectfile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void clientRender() {
        GUIBriefingGeneric.DialogClient dialogclient = this.dialogClient;
        GUIBriefingGeneric.DialogClient dialogclient_81_ = dialogclient;
        float f = dialogclient.x1024(427.0F);
        float f_82_ = dialogclient.y1024(633.0F);
        float f_83_ = dialogclient.x1024(170.0F);
        float f_84_ = dialogclient.y1024(48.0F);

        dialogclient_81_.draw(f, f_82_, f_83_, f_84_, 1, this.i18n("brief.Fly"));
    }

//    protected void clientRender_old() {
//        GUIBriefingGeneric.DialogClient dialogclient = this.dialogClient;
//        GUIBriefingGeneric.DialogClient dialogclient_57_ = dialogclient;
//        float f = dialogclient.x1024(768.0F);
//        float f_58_ = dialogclient.y1024(656.0F);
//        float f_59_ = dialogclient.x1024(160.0F);
//        float f_60_ = dialogclient.y1024(48.0F);
//        dialogclient_57_.draw(f, f_58_, f_59_, f_60_, 2, this.i18n("brief.Fly"));
//    }

    protected String infoMenuInfo() {
        return this.i18n("brief.info");
    }

    public GUIBriefing(int i) {
        super(i);

        this.playerPath = new ArrayList();
        this.targets = new ArrayList();
        this.beacons = new ArrayList();
        playerArmy = 0;
        this.lineNXYZ = new float[6];
        this.bSelectBorn = false;
    }

    protected AirportCarrier getCarrier(NetUser netuser) {
        BornPlace bornplace = (BornPlace) World.cur().bornPlaces.get(netuser.getBornPlace());
        if (!World.land().isWater(bornplace.place.x, bornplace.place.y)) return null;
        else {
            Loc loc = new Loc(bornplace.place.x, bornplace.place.y, 0.0D, 0.0F, 0.0F, 0.0F);
            AirportCarrier airportcarrier = (AirportCarrier) Airport.nearest(loc.getPoint(), -1, 4);

            if (airportcarrier != null && airportcarrier.ship() instanceof TestRunway) return null;

            return airportcarrier;
        }
    }

    protected boolean isCarrierDeckFree(NetUser netuser) {
        try {
            AirportCarrier airportcarrier = null;
            BornPlace bornplace = (BornPlace) World.cur().bornPlaces.get(netuser.getBornPlace());
            if (bornplace.zutiAirspawnOnly || !World.cur().diffCur.Takeoff_N_Landing || !World.land().isWater(bornplace.place.x, bornplace.place.y)) return true;
            Loc loc = new Loc(bornplace.place.x, bornplace.place.y, 0.0D, 0.0F, 0.0F, 0.0F);
            airportcarrier = (AirportCarrier) Airport.nearest(loc.getPoint(), -1, 4);
            if (airportcarrier == null || !airportcarrier.ship().isAlive() || airportcarrier.ship().zutiIsStatic()) return false;
            UserCfg usercfg = World.cur().userCfg;
            Class class1 = (Class) Property.value(usercfg.netAirName, "airClass", null);
            NetAircraft netaircraft = (NetAircraft) class1.newInstance();
            CellAirField cellairfield = airportcarrier.ship().getCellTO();
            Aircraft aircraft = (Aircraft) netaircraft;
            if (aircraft.FM == null) return false;
            aircraft.setFM(1, false);
            CellAirPlane cellairplane = aircraft.getCellAirPlane();
            if (!cellairfield.findPlaceForAirPlane(cellairplane)) return false;

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }
}