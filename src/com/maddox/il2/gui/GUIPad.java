/*
 * Modded by PAL, multiple styles of MapPads + Merged with MDS class by |ZUTI| +
 * added few things
 */
package com.maddox.il2.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.JGP.Tuple3d;
import com.maddox.JGP.Vector3f;
import com.maddox.gwindow.GMesh;
import com.maddox.gwindow.GPoint;
import com.maddox.gwindow.GRegion;
import com.maddox.gwindow.GWindowFramed;
import com.maddox.gwindow.GWindowMenuItem;
import com.maddox.gwindow.GWindowMenuPopUp;
import com.maddox.gwindow.GWindowRoot;
import com.maddox.il2.ai.Airport;
import com.maddox.il2.ai.AirportCarrier;
import com.maddox.il2.ai.Army;
import com.maddox.il2.ai.Chief;
import com.maddox.il2.ai.Front;
import com.maddox.il2.ai.ScoreCounter;
import com.maddox.il2.ai.ScoreItem;
import com.maddox.il2.ai.Target;
import com.maddox.il2.ai.Way;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.ground.TgtShip;
import com.maddox.il2.builder.Plugin;
import com.maddox.il2.engine.Accumulator;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.CameraOrtho2D;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.GUIRenders;
import com.maddox.il2.engine.IconDraw;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.engine.LightEnvXY;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.engine.Render;
import com.maddox.il2.engine.Renders;
import com.maddox.il2.engine.TTFont;
import com.maddox.il2.fm.Autopilotage;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.ZutiMDSVariables;
import com.maddox.il2.game.ZutiRadarRefresh;
import com.maddox.il2.game.ZutiSupportMethods;
import com.maddox.il2.net.BornPlace;
import com.maddox.il2.net.ZutiSupportMethods_Net;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.ShipGeneric;
import com.maddox.il2.objects.vehicles.artillery.AAA;
import com.maddox.il2.objects.vehicles.radios.Beacon;
import com.maddox.il2.objects.vehicles.radios.TypeHasBeacon;
import com.maddox.il2.objects.vehicles.radios.TypeHasLorenzBlindLanding;
import com.maddox.il2.objects.vehicles.radios.TypeHasRadioStation;
import com.maddox.rts.RTSConf;
import com.maddox.rts.Time;
import com.maddox.util.HashMapExt;

public class GUIPad {
    public TTFont waypointFont;
    public Mat    _iconILS;
    private float                       bigFontMultip;
    private Point3f      _wayP2;

    private float                       lineBeamXYZ[];
    // public GMesh meshbrief; //By PAL, new mesh for Frontal briefing view
    public TTFont                       smallFont;																							// By PAL, small font

    public static boolean               bStartMission = true;
    private Main3D                      main;
    public GUIClient                    client;
    public GWindowFramed                frame;
    public GRegion                      frameRegion;
    public float                        mapView[]     = {
            // 0.09F, 0.09F, 0.82F, 0.82F Original
            // 0.03F, 0.03F, 0.96F, 0.96F //By PAL, to extend visual area
            0.01F, 0.01F, 0.98F, 0.98F };
    public GUIRenders                   renders;
    public GUIRenders                   renders1;
    public RenderMap2D                  renderMap2D;
    public CameraOrtho2D                cameraMap2D;
    public RenderMap2D1                 renderMap2D1;
    public CameraOrtho2D                cameraMap2D1;
    public boolean                      savedUseMesh;
    public int                          saveIconDx;
    public int                          saveIconDy;
    protected ArrayList                 targets;
    public GMesh                        mesh;
    public GMesh                        meshradar;																							// By PAL, new mesh for radar view
    public GMesh                        meshradarF;																						// By PAL, new mesh for Frontal radar view
    public TTFont                       gridFont;
    public Mat                          emptyMat;
    public Mat                          _iconAir;
    public Mat                          _iconRadar;																						// By PAL, icon of Planes in the radar
    public Mat                          _iconShipRadar;																					// By PAL, icon of Ships planes in the radar
    public Mat                          _iconAirField;																						// By PAL, to show icons in navigation map
    private float                       scale[]       = { 0.064F, 0.032F, 0.016F, 0.008F, 0.004F, 0.002F, 0.001F, 0.0005F, 0.00025F };
    private int                         scales;

    // TODO: Changed by |ZUTI|: from private to public
    // --------------------------------------------------
    public int curScale;
    // --------------------------------------------------

    private int       curScaleDirect = 1;
    public boolean    bActive;
    private float     line2XYZ[];
    private int       _gridCount;
    private int       _gridX[];
    private int       _gridY[];
    private int       _gridVal[];
    private ArrayList airdrome;
    int               _army[];
    ArmyAccum         armyAccum;
    private ArrayList airs;
    private Point3f   _wayP;
    private float     lineNXYZ[];
    private int       lineNCounter;

    // TODO: Modified by |ZUTI|: changed from private to public
    // --------------------------------------------------
    public Point3d           OwnPos3d;																							// By PAL, to store position
    public double            OwnAzimut;
    public float             OwnAngle;
    public int               GUIPadMode;
    private GWindowMenuPopUp popUpMenu;
    public int               FrameOriginX, FrameOriginY, FrameGapX = 0;
    private int              to;																								// By PAL, to make update not permanent
    public String            textDescription;
    private ArrayList        radarPlane;
    private ArrayList        radarOther;

    // --------------------------------------------------
    
    // TODO: +++ Modified by SAS~Storebror: Save last Briefing Text (e.g. for Network use)
    private static String    briefingText;
    

    public static String getBriefingText() {
        return briefingText;
    }

    public static void setBriefingText(String briefingText) {
        GUIPad.briefingText = briefingText;
    }
    // ---

    public class RenderMap2D extends Render {
        public void preRender() {
            Front.preRender(false);
        }

        public void render() {
            // TODO: Added by |ZUTI|
            GUIPad.this.zutiRenderPad_MDSWay();
        }

        public String Target(int tNum) {
            String Descript = "";
            switch (tNum) {
                case 0:
                    Descript = "Destroy";
                    break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
                case 1:
                    Descript = "Destroy Ground";
                    break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
                case 2:
                    Descript = "Destroy Bridge";
                    break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
                case 3:
                    Descript = "Inspect";
                    break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
                case 4:
                    Descript = "Escort"; // Originally None
                    break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
                case 5:
                    Descript = "Defence"; // Originally Escort
                    break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
                case 6:
                    Descript = "Defence Ground";
                    break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
                case 7:
                    Descript = "Defence Bridge";
                    break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
            }
            return Descript;
        }

        public RenderMap2D(Renders renders2, float f) {
            super(renders2, f);
            this.useClearDepth(false);
            this.useClearColor(false);
        }
    }

    /*
     * public int drawText(float f, float f1, String s, int i, int j, float f2, float f3, int k) { GCanvas gcanvas = frame.root.C; gcanvas.cur.x = f + gcanvas.org.x; gcanvas.cur.y = f1 + gcanvas.org.y; GFont gfont = gcanvas.font; int l = 0; while (j > 0
     * && k != 0) { int i1 = j; int j1 = s.indexOf('\n', i); if (j1 >= 0) i1 = j1 - i; if (i1 > 0) { while (i1 > 0 && k != 0) { int k1 = gfont.len(s, i, i1, f2, true, true); if (k1 == 0) k1 = gfont.len(s, i, i1, f2, true, false); if (k1 == 0) return l;
     * gcanvas.draw(s, i, k1); gridFont.output(0xffffffff, f, l * f3, 0.0F, s); gcanvas.cur.y += f3; l++; j -= k1; i += k1; i1 -= k1; k--; for (; i1 > 0; i1--) { if (s.charAt(i) != ' ') break; i++; j--; } } if (j1 >= 0) { i++; j--; } } else {
     * gcanvas.cur.y += f3; l++; j--; i++; k--; } } return l; }
     */

    public class RenderMap2D1 extends Render {
        public void preRender() {
        }

        public void render() {
            if (GUIPad.this.GUIPadMode == 1 || GUIPad.this.GUIPadMode == 2 || GUIPad.this.GUIPadMode == 3) GUIPad.this.renders1.draw(0.0F, 0.0F, GUIPad.this.renders1.win.dx, GUIPad.this.renders1.win.dy, GUIPad.this.mesh);
            else if (GUIPad.this.GUIPadMode == 4) GUIPad.this.renders1.draw(0.0F, 0.0F, GUIPad.this.renders1.win.dx, GUIPad.this.renders1.win.dy, GUIPad.this.meshradar);
            if (GUIPad.this.GUIPadMode == 5) GUIPad.this.renders1.draw(0.0F, 0.0F, GUIPad.this.renders1.win.dx, GUIPad.this.renders1.win.dy, GUIPad.this.meshradarF);
        }

        public RenderMap2D1(Renders renders2, float f) {
            super(renders2, f);
            this.useClearDepth(false);
            this.useClearColor(false);
        }
    }

    class ArmyAccum implements Accumulator {
        public void clear() {
        }

        public boolean add(Actor actor, double d) {
            GUIPad.this._army[actor.getArmy()]++;
            return true;
        }

        ArmyAccum() {
        }
    }

    // TODO: Changed by |ZUTI|: added public access modifier to class and it's variables
    public class AirDrome {
        public Airport airport;
        public int     color;
        public int     army;

        // TODO: Added by |ZUTI|
        // -------------------------------------
        public boolean zutiIsOnShip       = false;
        public boolean zutiRenderAirdrome = true;
        // -------------------------------------
    }

    public boolean isActive() {
        return this.bActive;
    }

    public void enter() {
        if (this.bActive) return;

//        System.out.println("No_Fog_Of_War_Icons=" + World.cur().diffCur.No_Fog_Of_War_Icons +
//              ", No_Icons=" + World.cur().diffCur.No_Icons + 
//              ", No_Map_Icons=" + World.cur().diffCur.No_Map_Icons + 
//              ", No_Outside_Views=" + World.cur().diffCur.No_Outside_Views + 
//              ", No_Padlock=" + World.cur().diffCur.No_Padlock + 
//              ", No_Player_Icon=" + World.cur().diffCur.No_Player_Icon + 
//              ", NoMinimapPath=" + World.cur().diffCur.NoMinimapPath + 
//              ", RealisticNavigationInstruments=" + World.cur().diffCur.RealisticNavigationInstruments
//        );
                
        
        /*
         * for( int i=0; i<Mission.cur().actors.size(); i++ ) { System.out.println("ACTOR CLASS=" + Mission.cur().actors.get(i).getClass()); }
         */
        // World.cur().scoreCounter.zutiAddEnemyDestroyed();
        // ZutiWeaponsManagement.weaponPrintout(World.getPlayerAircraft());
        // ZutiSupportMethods_Air.emptyAircraft( World.getPlayerAircraft() );

        this.frameRegion.set(0.05F, 0.1F, 0.35F, 0.6F); // By PAL, to restore size to the normal
        this.bActive = true;
        GUI.activate(true, false);
        this.client.showWindow();
        float f = this.client.root.win.dx;
        float f1 = this.client.root.win.dy;
        this.GUIPadMode = Config.cur.ini.get("game", "mapPadMode", 1); // By PAL, store mode
        this.frameRegion.x = Config.cur.ini.get("game", "mapPadX", this.frameRegion.x);
        this.frameRegion.y = Config.cur.ini.get("game", "mapPadY", this.frameRegion.y);

        // TODO: Added by |ZUTI|
        // ------------------------------------------------------------
        this.zutiZoomingOldMode = Config.cur.ini.get("game", "mapZoomMode", 0) == 1 ? true : false;
        this.zutiMouseWheelDirection = Config.cur.ini.get("game", "mapWheelMode", 1);
        // ------------------------------------------------------------

        if (this.GUIPadMode == 1 || this.GUIPadMode == 2 || this.GUIPadMode == 6) // By PAL, normal or relative mode
        {
            if (this.FrameGapX > 0) {
                this.frameRegion.x += this.FrameGapX / f;
                this.FrameGapX = 0;
            }
            this.frame.setPosSize(this.frameRegion.x * f, this.frameRegion.y * f1, this.frameRegion.dx * f, this.frameRegion.dy * f1); // Rectangular for normal maps
        } else if (this.GUIPadMode == 3) if (this.frameRegion.x + this.frameRegion.dx * World.land().getSizeX() / World.land().getSizeY() <= 1) {
            this.FrameGapX = 0;
            this.frame.setPosSize(this.frameRegion.x * f, this.frameRegion.y * f1, this.frameRegion.dx * f * World.land().getSizeX() / World.land().getSizeY(), this.frameRegion.dx * f); // To make it squared for navigation
        } else {
            this.FrameGapX = (int) (this.frameRegion.x * f + this.frameRegion.dx * f * World.land().getSizeX() / World.land().getSizeY() - this.client.root.win.dx);
            this.frame.setPosSize(this.frameRegion.x * f - this.FrameGapX, this.frameRegion.y * f1, this.frameRegion.dx * f * World.land().getSizeX() / World.land().getSizeY(), this.frameRegion.dx * f); // To make it squared for navigation
        }
        if (this.GUIPadMode == 4 || this.GUIPadMode == 5) // By PAL => Radar
        {
            if (this.FrameGapX > 0) {
                this.frameRegion.x += this.FrameGapX / f;
                this.FrameGapX = 0;
            }
            this.frame.setPosSize(this.frameRegion.x * f, this.frameRegion.y * f1, this.frameRegion.dx * f, this.frameRegion.dx * f); // To make it squared for Radar
        }
        this.FrameOriginX = (int) (this.renderMap2D.getViewPortWidth() / 2 + 0.5F); // By PAL, to avoid problem when finishing
        this.FrameOriginY = (int) (this.renderMap2D.getViewPortHeight() / 2 + 0.5F);
        this.cameraMap2D.set(0.0F, this.renderMap2D.getViewPortWidth(), 0.0F, this.renderMap2D.getViewPortHeight());
        this.cameraMap2D1.set(0.0F, this.renderMap2D1.getViewPortWidth(), 0.0F, this.renderMap2D1.getViewPortHeight());
        this.computeScales();
        this.scaleCamera();
        if (bStartMission) {
            // TODO: Added by |ZTUI|
            // --------------------------------------------------------------
            // Reset radars!
            ZutiRadarRefresh.findRadars(ZutiSupportMethods.getPlayerArmy());
            this.targets.clear();
            // Reset start timers
            ZutiRadarRefresh.resetStartTimes();
            // Added by |ZUTI|
            ZutiSupportMethods_GUI.fillNeutralHomeBases(this.zutiNeutralHomeBases);
            // Load targets
            if (Mission.cur() != null) ZutiSupportMethods_GUI.fillTargetPoints(Mission.cur().sectFile());
            // Fill ground targets. Use different approach because Engine.targets actors
            // are without icons if actor is ground unit...
            ZutiSupportMethods_GUI.fillGroundChiefsArray(this);
            // --------------------------------------------------------------

            // Original lines
            /*
             * targets.clear(); if (Mission.isSingle() || Mission.isCoop()) GUIBriefing.fillTargets(Mission.cur().sectFile(), targets);
             */
        }
        if (!World.cur().diffCur.No_Map_Icons || bStartMission) {
            Actor actor = Actor.getByName("camera");
            float f2;
            float f3;
            if (Actor.isValid(actor)) {
                Point3d point3d = actor.pos.getAbsPoint();
                f2 = (float) point3d.x;
                f3 = (float) point3d.y;
            } else {
                f2 = World.land().getSizeX() / 2.0F;
                f3 = World.land().getSizeY() / 2.0F;
            }
            this.setPosCamera(f2, f3);
            bStartMission = false;
        }
        this.frame.activateWindow();
        this.savedUseMesh = this.main.guiManager.isUseGMeshs();
        this.saveIconDx = IconDraw.scrSizeX();
        this.saveIconDy = IconDraw.scrSizeY();
        this.main.guiManager.setUseGMeshs(false);
            String s = Main.cur().currentMissionFile.fileName(); // By PAL, to load only original name
            for (int j = s.length() - 1; j > 0; j--) {
                char c = s.charAt(j);
                if (c == '\\' || c == '/') break;
                if (c != '.') continue;
                s = s.substring(0, j);
                break;
            }

            // TODO: Added by |ZUTI|: try/catch block
            // --------------------------------------------------
            try {
                ResourceBundle resourcebundle = ResourceBundle.getBundle(s, RTSConf.cur.locale);
                this.textDescription = /*" Original Brief for the mission:\n\n" + */resourcebundle.getString("Description");
            } catch (Exception ex) {
                // TODO: +++ Modified by SAS~Storebror: Save last Briefing Text (e.g. for Network use)
                if (getBriefingText() != null && getBriefingText().length() > 1) this.textDescription = /*" Original Brief for the mission:\n\n" + */getBriefingText();
                // ---
            }
            // --------------------------------------------------
    }
    
    public void leave(boolean flag) {
        if (!this.bActive) return;
        this.bActive = false;
        float f = this.client.root.win.dx;
        float f1 = this.client.root.win.dy;
        this.frameRegion.x = this.frame.win.x / f;
        this.frameRegion.y = this.frame.win.y / f1;
        this.frameRegion.dx = this.frame.win.dx / f;
        this.frameRegion.dy = this.frame.win.dy / f1;

        if (this.FrameGapX > 0) // By PAL, to reposition the navigation map
        {
            this.frameRegion.x += this.FrameGapX / f;
            this.FrameGapX = 0;
        }

        Config.cur.ini.set("game", "mapPadX", this.frameRegion.x);
        Config.cur.ini.set("game", "mapPadY", this.frameRegion.y);
        Config.cur.ini.set("game", "mapPadMode", this.GUIPadMode);

        // TODO: Added by |ZUTI|
        // ------------------------------------------------------------
        Config.cur.ini.set("game", "mapZoomMode", ZutiSupportMethods.boolToInt(this.zutiZoomingOldMode));
        Config.cur.ini.set("game", "mapWheelMode", this.zutiMouseWheelDirection);
        // ------------------------------------------------------------

        this.client.hideWindow();
        Main3D.cur3D().guiManager.setUseGMeshs(this.savedUseMesh);
        IconDraw.setScrSize(this.saveIconDx, this.saveIconDy);
        if (!flag) GUI.unActivate();
    }

    private void setPosCamera(float f, float f1) {
        float f2 = (float) ((this.cameraMap2D.right - this.cameraMap2D.left) / this.cameraMap2D.worldScale);
        this.cameraMap2D.worldXOffset = f - f2 / 2.0F;
        float f3 = (float) ((this.cameraMap2D.top - this.cameraMap2D.bottom) / this.cameraMap2D.worldScale);
        this.cameraMap2D.worldYOffset = f1 - f3 / 2.0F;
        this.clipCamera();
    }

    private void scaleCamera() {
        this.cameraMap2D.worldScale = this.scale[this.curScale] * this.client.root.win.dx / 1024F;
    }

    private void clipCamera() {
        if (this.cameraMap2D.worldXOffset < -Main3D.cur3D().land2D.worldOfsX()) this.cameraMap2D.worldXOffset = -Main3D.cur3D().land2D.worldOfsX();
        float f = (float) ((this.cameraMap2D.right - this.cameraMap2D.left) / this.cameraMap2D.worldScale);
        if (this.cameraMap2D.worldXOffset > Main3D.cur3D().land2D.mapSizeX() - Main3D.cur3D().land2D.worldOfsX() - f) this.cameraMap2D.worldXOffset = Main3D.cur3D().land2D.mapSizeX() - Main3D.cur3D().land2D.worldOfsX() - f;
        if (this.cameraMap2D.worldYOffset < -Main3D.cur3D().land2D.worldOfsY()) this.cameraMap2D.worldYOffset = -Main3D.cur3D().land2D.worldOfsY();
        float f1 = (float) ((this.cameraMap2D.top - this.cameraMap2D.bottom) / this.cameraMap2D.worldScale);
        if (this.cameraMap2D.worldYOffset > Main3D.cur3D().land2D.mapSizeY() - Main3D.cur3D().land2D.worldOfsY() - f1) this.cameraMap2D.worldYOffset = Main3D.cur3D().land2D.mapSizeY() - Main3D.cur3D().land2D.worldOfsY() - f1;
    }

    private void computeScales() {
        float f = this.renders.win.dx * 1024F / this.client.root.win.dx;
        float f1 = this.renders.win.dy * 768F / this.client.root.win.dy;
        int i = 0;
        float f2 = 0.064F;
        for (; i < this.scale.length; i++) {
            this.scale[i] = f2;
            float f3 = (float) (Main3D.cur3D().land2D.mapSizeX() * f2);
            if (f3 < f) break;
            float f5 = (float) (Main3D.cur3D().land2D.mapSizeY() * f2);
            if (f5 < f1) break;
            f2 /= 2.0F;
        }

        this.scales = i;
        if (this.scales < this.scale.length) {
            float f4 = f / (float) Main3D.cur3D().land2D.mapSizeX();
            float f6 = f1 / (float) Main3D.cur3D().land2D.mapSizeY();
            this.scale[i] = f4;
            if (f6 > f4) this.scale[i] = f6;
            this.scales = i + 1;
        }
        if (this.curScale >= this.scales) this.curScale = this.scales - 1;
        if (this.curScale < 0) this.curScale = 0;
    }

    // TODO: Changed by |ZUTI|: from private to public
    public void drawGrid2DFixed() // By PAL, for Radar and relative mode
    {
        int i = this.gridStep();
        // int j = 0;//(int)((cameraMap2D.worldXOffset + Main3D.cur3D().land2D.worldOfsX()) / (double)i);
        // int k = 0;//(int)((cameraMap2D.worldYOffset + Main3D.cur3D().land2D.worldOfsY()) / (double)i);
        double d = (this.cameraMap2D.right - this.cameraMap2D.left) / this.cameraMap2D.worldScale;
        double d1 = (this.cameraMap2D.top - this.cameraMap2D.bottom) / this.cameraMap2D.worldScale;
        int l = (int) (d / i + 0.5F) + 2;
        int i1 = (int) (d1 / i + 0.5F) + 2;
        float f2 = (float) (l * i * this.cameraMap2D.worldScale);
        float f3 = (float) (i1 * i * this.cameraMap2D.worldScale);
        float f4 = (float) (i * this.cameraMap2D.worldScale);
        float f = this.renderMap2D.getViewPortWidth() / 2;// 0;//(float)(((double)(j * i) - cameraMap2D.worldXOffset - Main3D.cur3D().land2D.worldOfsX()) * cameraMap2D.worldScale + 0.5D);
        float f1 = this.renderMap2D.getViewPortHeight() / 2;// 0;//(float)(((double)(k * i) - cameraMap2D.worldYOffset - Main3D.cur3D().land2D.worldOfsY()) * cameraMap2D.worldScale + 0.5D);
        this._gridCount = 0;
        this.smallFont = TTFont.font[0];
        Render.drawBeginLines(-1);
        if (this.GUIPadMode == 4) { // By PAL, draw radial beam
            this.lineBeamXYZ[0] = this.lineBeamXYZ[6];
            this.lineBeamXYZ[1] = this.lineBeamXYZ[7];
            this.lineBeamXYZ[2] = 0.0F;
            this.lineBeamXYZ[3] = f;
            this.lineBeamXYZ[4] = f1;
            this.lineBeamXYZ[5] = 0.0F;
            this.lineBeamXYZ[6] = (float) (1 - Math.cos(Time.current() / 1000.0F)) * f;
            this.lineBeamXYZ[7] = (float) (1 + Math.sin(Time.current() / 1000.0F)) * f1;
            this.lineBeamXYZ[8] = 0.0F;
            Render.drawLines(this.lineBeamXYZ, 3, 2.0F /* 2.0F */, 0xffffffff, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 3);
        }
        // for(int j1 = 0; j1 <= i1; j1++)
        int numH = i1 / 2;
        for (int j1 = -numH; j1 <= numH; j1++) {
            float f5 = f1 + j1 * f4;
            char c = j1 % 10 != 0 ? '\177' : '\300';
            this.line2XYZ[0] = 0.0F; // f;
            this.line2XYZ[1] = f5;
            this.line2XYZ[2] = 0.0F;
            this.line2XYZ[3] = f2; // f + f2;
            this.line2XYZ[4] = f5;
            this.line2XYZ[5] = 0.0F;
            Render.drawLines(this.line2XYZ, 2, 1.0F, 0xff000000 | c << 16 | c << 8 | c, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 0);
            if (l >= 12 && j1 % 5 == 0 || l < 12) // if(j1 % (l /4 + 1) == 0) //drawGridText(0, (int)f5, (k + j1 + numH) * i); //numH by PAL
                this.smallFont.output(0xff000000, 25, (int) f5 + 2, 0.0F, "" + Math.abs(j1 * i / 1000));
        }
        // gridFont.output(0xffc0c0c0, 0, (int)f5 + 2, 0.0F, "x" + cameraMap2D.worldScale);//_gridVal[i] / 1000);

        // for(int k1 = 0; k1 <= l; k1++)
        int numV = l / 2;
        for (int k1 = -numV; k1 <= numV; k1++) {
            float f6 = f + k1 * f4;
            char c1 = k1 % 10 != 0 ? '\177' : '\300';
            this.line2XYZ[0] = f6;
            this.line2XYZ[1] = 0.0F; // f1;
            this.line2XYZ[2] = 0.0F;
            this.line2XYZ[3] = f6;
            this.line2XYZ[4] = f3; // f1 + f3;
            this.line2XYZ[5] = 0.0F;
            // Try to implement oval circles for radial radar view
            Render.drawLines(this.line2XYZ, 2, 1.0F, 0xff000000 | c1 << 16 | c1 << 8 | c1, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 0);
            if (l >= 12 && k1 % 5 == 0 || l < 12) // drawGridText((int)f6, 0, (j + k1 + numV) * i); //numV y PAL
                this.smallFont.output(0xff000000, (int) f6 + 2, 25, 0.0F, "" + Math.abs(k1 * i / 1000));
        }
        Render.drawEnd();
        this.gridFont.output(0xff000000, (int) f - 50, this.renderMap2D.getViewPortHeight() - 45, 0.0F, this.curScale > 2 ? "x1: 10km/div" : "x10: 1km/div"); // Fixed draw labels
        // gridFont.output(0xffc0c0c0, (int)f + 2, 25, 0.0F, ""+(int)Main3D.cur3D().land2D.mapSizeX());//(curScale > 2)? "x1" : "x10"); //Fixed draw labels
        // gridFont.output(0xffc0c0c0, 25, (int) f1 + 2, 0.0F, ""+(int)Main3D.cur3D().land2D.mapSizeY());//(curScale > 2)? "x1" : "x10");
        // drawGridText();
    }

    private void drawGrid2D() {
//        if (this.cameraMap2D == null || Main3D.cur3D() == null || Main3D.cur3D().land2D == null) {
//            System.out.println("GUI.pad.leave(false) - drawGrid2D");
////            this.leave(false); // TODO: By SAS~Storebror to avoid crashes when Map is still drawing after sortie end.
//        }
        try {
            int i = this.gridStep();
            int j = (int) ((this.cameraMap2D.worldXOffset + Main3D.cur3D().land2D.worldOfsX()) / i);
            int k = (int) ((this.cameraMap2D.worldYOffset + Main3D.cur3D().land2D.worldOfsY()) / i);
            double d = (this.cameraMap2D.right - this.cameraMap2D.left) / this.cameraMap2D.worldScale;
            double d1 = (this.cameraMap2D.top - this.cameraMap2D.bottom) / this.cameraMap2D.worldScale;
            int l = (int) (d / i) + 2;
            int i1 = (int) (d1 / i) + 2;
            float f = (float) ((j * i - this.cameraMap2D.worldXOffset - Main3D.cur3D().land2D.worldOfsX()) * this.cameraMap2D.worldScale + 0.5D);
            float f1 = (float) ((k * i - this.cameraMap2D.worldYOffset - Main3D.cur3D().land2D.worldOfsY()) * this.cameraMap2D.worldScale + 0.5D);
            float f2 = (float) (l * i * this.cameraMap2D.worldScale);
            float f3 = (float) (i1 * i * this.cameraMap2D.worldScale);
            float f4 = (float) (i * this.cameraMap2D.worldScale);
            this._gridCount = 0;
            Render.drawBeginLines(-1);
            for (int j1 = 0; j1 <= i1; j1++) {
                float f5 = f1 + j1 * f4;
                char c = (j1 + k) % 10 != 0 ? '\177' : '\300';
                this.line2XYZ[0] = f;
                this.line2XYZ[1] = f5;
                this.line2XYZ[2] = 0.0F;
                this.line2XYZ[3] = f + f2;
                this.line2XYZ[4] = f5;
                this.line2XYZ[5] = 0.0F;
                Render.drawLines(this.line2XYZ, 2, 1.0F, 0xff000000 | c << 16 | c << 8 | c, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 0);
                if (c == '\300') this.drawGridText(0, (int) f5, (k + j1) * i);
            }
    
            for (int k1 = 0; k1 <= l; k1++) {
                float f6 = f + k1 * f4;
                char c1 = (k1 + j) % 10 != 0 ? '\177' : '\300';
                this.line2XYZ[0] = f6;
                this.line2XYZ[1] = f1;
                this.line2XYZ[2] = 0.0F;
                this.line2XYZ[3] = f6;
                this.line2XYZ[4] = f1 + f3;
                this.line2XYZ[5] = 0.0F;
                Render.drawLines(this.line2XYZ, 2, 1.0F, 0xff000000 | c1 << 16 | c1 << 8 | c1, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 0);
                if (c1 == '\300') this.drawGridText((int) f6, 0, (j + k1) * i);
            }
    
            Render.drawEnd();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.drawGridText();
    }

    private int gridStep() {
        float f = this.cameraMap2D.right - this.cameraMap2D.left;
        float f1 = this.cameraMap2D.top - this.cameraMap2D.bottom;
        double d = f;
        if (f1 < f) d = f1;
        d /= this.cameraMap2D.worldScale;
        int i = 0x186a0;
        for (int j = 0; j < 5; j++) {
            if (i * 3 <= d) break;
            i /= 10;
        }

        return i;
    }

    private void drawGridText(int i, int j, int k) {
        if (i < 0 || j < 0 || k <= 0 || this._gridCount == 20) return;
        else {
            this._gridX[this._gridCount] = i;
            this._gridY[this._gridCount] = j;
            this._gridVal[this._gridCount] = k;
            this._gridCount++;
            return;
        }
    }

    private void drawGridText() {
        for (int i = 0; i < this._gridCount; i++)
            this.gridFont.output(0xffc0c0c0, this._gridX[i] + 2, this._gridY[i] + 2, 0.0F, this._gridVal[i] / 1000 + "." + this._gridVal[i] % 1000 / 100);

        this._gridCount = 0;
    }

    private void drawAirports() {
        Mission mission = Main.cur().mission;

        if (mission == null) return;

        int pilotArmy = World.getPlayerArmy();

        Mat mat = IconDraw.get("icons/runaway.mat");
        AirDrome airdrome = null;
        // Point3d point3d = null;

        for (int i = 0; i < this.airdrome.size(); i++) {
            airdrome = (AirDrome) this.airdrome.get(i);

            // TODO: Edited by |ZUTI|: color ours with their home base color, if they have home base attached. But do this only the first time minimap is displayed. Don't waste resourcer further.
            if (this.zutiColorAirfields) ZutiSupportMethods.changeAirdromeArmyAndColorToBornPlaceArmyAndColor(airdrome);

            // TODO: Added by |ZUTI|: disable of rendering white airports
            if (airdrome.army != pilotArmy && Mission.MDS_VARIABLES().zutiIcons_HideUnpopulatedAirstripsFromMinimap) continue;

            // TODO: Edited by |ZUTI|: enemy airfield will be colored white, like unpopulated ones. Friendly have their own color
            if (Actor.isValid(airdrome.airport) && Actor.isAlive(airdrome.airport))// && (airdrome.army == 0 || airdrome.army == World.getPlayerArmy()))
            {
                // point3d = airdrome.airport.pos.getAbsPoint();
                // float f = (float) ((point3d.x - cameraMap2D.worldXOffset) * cameraMap2D.worldScale);
                // float f_34_ = (float) ((point3d.y - cameraMap2D.worldYOffset) * cameraMap2D.worldScale);

                // Edit by |ZUTI|: since we are coloring the airfields with home base colors, this is not good since enemy can see which airfields have home bases. So, here we check if
                // airport is of different color than player army. And if so, just make it white, like unpopulated ones. Should hide populated ones nicely.
                if (airdrome.army != pilotArmy) {
                    // Do not show airports that are on carriers with white color. This reveals CV location to the enemy, even though it's white (it's moving :D).
                    if (airdrome.zutiIsOnShip) continue;

                    IconDraw.setColor(-1);
                } else IconDraw.setColor(airdrome.color);

                // Disabled |ZUTI| way for enabling Pablos way
                // IconDraw.render(mat, f, f_34_);
                this.pablo_drawAirports(airdrome.airport.pos.getAbsPoint(), airdrome.color, airdrome.airport.pos.getAbsOrient().azimut(), mat);
            }
        }

        Point3d bpPos = new Point3d(0D, 0D, 0D);
        for (int i = 0; i < ZutiSupportMethods_GUI.STD_HOME_BASE_AIRFIELDS.size(); i++) {
            BornPlace bp = (BornPlace) ZutiSupportMethods_GUI.STD_HOME_BASE_AIRFIELDS.get(i);

            // TODO: Added by |ZUTI|: disable of rendering white airports
            if (bp.army != pilotArmy && Mission.MDS_VARIABLES().zutiIcons_HideUnpopulatedAirstripsFromMinimap) continue;

            if (bp.army != pilotArmy) IconDraw.setColor(-1);
            else IconDraw.setColor(bp.zutiColor);

            bpPos.set(bp.place.x, bp.place.y, 0D);
            this.pablo_drawAirports(bpPos, bp.zutiColor, 0F, mat);
        }
        // TODO: Added by |ZUTI|: Airfields were colored for this mission session, disable it until new mission is loaded.
        this.zutiColorAirfields = false;
    }

    public void fillAirports() {
        // TODO: Added by |ZUTI|
        // ----------------------------------------
        Point3d zutiAirdromePoint = null;
        BornPlace zutiBornPlace = null;
        // ----------------------------------------

        this.airdrome.clear();
        ArrayList arraylist = new ArrayList();
        World.cur();
        World.getAirports(arraylist);
        for (int i = 0; i < arraylist.size(); i++) {
            Airport airport = (Airport) arraylist.get(i);
            AirDrome airdrome = new AirDrome();
            airdrome.airport = airport;
            if (airport instanceof AirportCarrier && Actor.isValid(((AirportCarrier) airport).ship())) {
                if (World.cur().diffCur.RealisticNavigationInstruments) continue;

                airdrome.army = ((AirportCarrier) airport).ship().getArmy();

                // TODO: Added by |ZUTI|
                // --------------------------------------------------
                airdrome.zutiIsOnShip = true;
                // --------------------------------------------------
            }

            // TODO: Added by |ZUTI|
            // --------------------------------------------------------------------
            if (airdrome.airport != null) {
                zutiAirdromePoint = airdrome.airport.pos.getAbsPoint();
                zutiBornPlace = ZutiSupportMethods_Net.getNearestBornPlace_AnyArmy(zutiAirdromePoint.x, zutiAirdromePoint.y);
                if (zutiBornPlace != null) {
                    // check if airdrome is actually inside born place circle...
                    double rr = zutiBornPlace.r * zutiBornPlace.r;
                    double tmpDistance = Math.pow(zutiAirdromePoint.x - zutiBornPlace.place.x, 2) + Math.pow(zutiAirdromePoint.y - zutiBornPlace.place.y, 2);
                    if (tmpDistance <= rr && zutiBornPlace.zutiDisableRendering) airdrome.zutiRenderAirdrome = false;
                }
            }

            if (airdrome.zutiRenderAirdrome) this.airdrome.add(airdrome);
            // --------------------------------------------------------------------
            // TODO: Comment by |ZUTI|: disabled original line
            // this.airdrome.add(airdrome);
        }
        Point3d point3d = new Point3d();
        for (int i = 0; i < this.airdrome.size(); i++) {
            AirDrome airdrome = (AirDrome) this.airdrome.get(i);
            Point3d point3d_35_ = airdrome.airport.pos.getAbsPoint();
            Point3d point3d_36_ = point3d;
            double d = point3d_35_.x;
            double d_37_ = point3d_35_.y;
            World.land();
            point3d_36_.set(d, d_37_, Landscape.HQ((float) point3d_35_.x, (float) point3d_35_.y));
            int i_38_ = 0;
            if (airdrome.army == 0) {
                Engine.collideEnv().getNearestEnemies(point3d, 2000.0, 0, this.armyAccum);
                int i_39_ = 0;
                for (int i_40_ = 1; i_40_ < this._army.length; i_40_++) {
                    if (i_39_ < this._army[i_40_]) {
                        i_39_ = this._army[i_40_];
                        i_38_ = i_40_;
                    }
                    this._army[i_40_] = 0;
                }
                airdrome.army = i_38_;
            }
            airdrome.color = Army.color(airdrome.army);
        }
    }

    private void drawRadioBeacons() {
        double d = this.cameraMap2D.worldXOffset;
        double d1 = this.cameraMap2D.worldYOffset;
        double d2 = this.cameraMap2D.worldXOffset + (this.cameraMap2D.right - this.cameraMap2D.left) / this.cameraMap2D.worldScale;
        double d3 = this.cameraMap2D.worldYOffset + (this.cameraMap2D.top - this.cameraMap2D.bottom) / this.cameraMap2D.worldScale;
        Aircraft aircraft = World.getPlayerAircraft();
        if (!Actor.isValid(aircraft)) return;
        java.util.ArrayList arraylist = Main.cur().mission.getBeacons(aircraft.getArmy());
        if (arraylist == null) return;
        int i = arraylist.size();
        for (int j = 0; j < i; j++) {
            Actor actor = (Actor) arraylist.get(j);
            if (!(actor instanceof TypeHasBeacon) || actor instanceof TgtShip || actor instanceof TypeHasRadioStation
                    || actor.getArmy() != World.getPlayerAircraft().getArmy())
                continue;
            Point3d point3d = actor.pos.getAbsPoint();
            if (point3d.x < d || point3d.x > d2 || point3d.y < d1 || point3d.y > d3) continue;
            float f = (float) ((point3d.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
            float f1 = (float) ((point3d.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
            int k = Army.color(actor.getArmy());
            if (actor instanceof TypeHasLorenzBlindLanding) {
                float f2 = actor.pos.getAbsOrient().azimut() + 90F;
                for (f2 = (f2 + 180F) % 360F; f2 < 0.0F; f2 += 360F)
                    ;
                for (; f2 >= 360F; f2 -= 360F)
                    ;
                IconDraw.setColor(-1);
                IconDraw.render(this._iconILS, f, f1, f2 - 90F);
                float f3 = 20F;
                java.lang.String s = Beacon.getBeaconID(j);
                this.gridFont.output(k, f + f3, f1 - f3, 0.0F, s);
                if (this.curScale < 2) {
                    int l = (int) actor.pos.getAbsPoint().z;
                    java.lang.String s1 = (int) f2 + "\260";
                    this.gridFont.output(k, f + f3, f1 - f3 - 20F, 0.0F, s1);
                    s1 = l + "m";
                    this.gridFont.output(k, f + f3, f1 - f3 - 40F, 0.0F, s1);
                }
                continue;
            }
            Mat mat = IconDraw.create(actor);
            if (mat != null) {
                IconDraw.setColor(k);
                IconDraw.render(mat, f, f1);
                float f4 = 20F;
                float f5 = 15F;
                java.lang.String s2 = Beacon.getBeaconID(j);
                this.gridFont.output(k, f + f4, f1 - f5, 0.0F, s2);
            }
        }
    }

    private void drawChiefs() {
        HashMapExt hashmapext = Engine.name2Actor();
        for (java.util.Map.Entry entry = hashmapext.nextEntry(null); entry != null; entry = hashmapext.nextEntry(entry)) {
            Actor actor = (Actor) entry.getValue();
            // TODO: +++ Hide dead ground objects from map! +++
            if (!actor.isAlive()) continue;
            // TODO: --- Hide dead ground objects from map! ---
            if (actor instanceof Chief) {
                Mat mat = actor.icon;
                if (mat != null) {
                    Point3d point3d = actor.pos.getAbsPoint();
                    if (this.GUIPadMode == 1 || this.GUIPadMode == 3) {
                        float f = (float) ((point3d.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                        float f1 = (float) ((point3d.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
                        IconDraw.setColor(Army.color(actor.getArmy()));
                        IconDraw.render(mat, f, f1);
                    } else if (this.GUIPadMode == 2) {
                        float NewY = (float) ((point3d.x - OwnPos3d.x) * Math.cos(this.OwnAzimut)) - (float) ((-point3d.y + OwnPos3d.y) * Math.sin(this.OwnAzimut));
                        float NewX = (float) ((-point3d.y + OwnPos3d.y) * Math.cos(this.OwnAzimut)) + (float) ((point3d.x - OwnPos3d.x) * Math.sin(this.OwnAzimut));
                        float f = this.FrameOriginX + (float) (NewX * this.cameraMap2D.worldScale);
                        float f1 = this.FrameOriginY + (float) (NewY * this.cameraMap2D.worldScale);
                        IconDraw.setColor(Army.color(actor.getArmy()));
                        IconDraw.render(mat, f, f1);
                    }
                }
            }
            // TODO: Fixed by SAS~Storebror, fixing repeated conditional test
//            if (actor instanceof BigshipGeneric || actor instanceof BigshipGeneric); // By PAL, to show Ships
            if (actor instanceof BigshipGeneric || actor instanceof ShipGeneric); // By PAL, to show Ships
            {
                Mat mat = actor.icon;
                if (mat != null) {
                    Point3d point3d = actor.pos.getAbsPoint();
                    if (this.GUIPadMode == 1 || this.GUIPadMode == 3) {
                        float f = (float) ((point3d.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                        float f1 = (float) ((point3d.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
                        IconDraw.setColor(Army.color(actor.getArmy()));
                        IconDraw.render(mat, f, f1);
                    } else if (this.GUIPadMode == 2) {
                        float NewY = (float) ((point3d.x - OwnPos3d.x) * Math.cos(this.OwnAzimut)) - (float) ((-point3d.y + OwnPos3d.y) * Math.sin(this.OwnAzimut));
                        float NewX = (float) ((-point3d.y + OwnPos3d.y) * Math.cos(this.OwnAzimut)) + (float) ((point3d.x - OwnPos3d.x) * Math.sin(this.OwnAzimut));
                        float f = this.FrameOriginX + (float) (NewX * this.cameraMap2D.worldScale);
                        float f1 = this.FrameOriginY + (float) (NewY * this.cameraMap2D.worldScale);
                        IconDraw.setColor(Army.color(actor.getArmy()));
                        IconDraw.render(mat, f, f1);
                    }
                }
            }
        }

    }

    private void drawAAAandFillAir() {
        double d = this.cameraMap2D.worldXOffset;
        double d1 = this.cameraMap2D.worldYOffset;
        double d2 = this.cameraMap2D.worldXOffset + (this.cameraMap2D.right - this.cameraMap2D.left) / this.cameraMap2D.worldScale;
        double d3 = this.cameraMap2D.worldYOffset + (this.cameraMap2D.top - this.cameraMap2D.bottom) / this.cameraMap2D.worldScale;
        List list = Engine.targets();
        int i = list.size();
        for (int j = 0; j < i; j++) {
            Actor actor = (Actor) list.get(j);
            if (actor instanceof Aircraft) {
                if (actor != World.getPlayerAircraft()) {
                    Point3d point3d = actor.pos.getAbsPoint();
                    if (this.GUIPadMode == 2 || this.GUIPadMode == 4 || this.GUIPadMode == 5) this.airs.add(actor); // By PAL, in relative I allways add actors
                    else if (point3d.x >= d && point3d.x <= d2 && point3d.y >= d1 && point3d.y <= d3) this.airs.add(actor);
                }
            } else if (actor instanceof AAA) {
                // TODO: +++ Hide dead ground objects from map! +++
                if (!actor.isAlive()) continue;
                // TODO: --- Hide dead ground objects from map! ---
                Point3d point3d1 = actor.pos.getAbsPoint();
                if (point3d1.x >= d && point3d1.x <= d2 && point3d1.y >= d1 && point3d1.y <= d3) {
                    Mat mat = IconDraw.create(actor);
                    if (mat != null) if (this.GUIPadMode == 1) // By PAL: normal view
                    {
                        float f = (float) ((point3d1.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                        float f1 = (float) ((point3d1.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
                        IconDraw.setColor(Army.color(actor.getArmy()));
                        IconDraw.render(mat, f, f1);
                    } else if (this.GUIPadMode == 2) // By PAL: relative view
                    {
                        float NewY = (float) ((point3d1.x - OwnPos3d.x) * Math.cos(this.OwnAzimut)) - (float) ((-point3d1.y + OwnPos3d.y) * Math.sin(this.OwnAzimut));
                        float NewX = (float) ((-point3d1.y + OwnPos3d.y) * Math.cos(this.OwnAzimut)) + (float) ((point3d1.x - OwnPos3d.x) * Math.sin(this.OwnAzimut));
                        float f = this.FrameOriginX + (float) (NewX * this.cameraMap2D.worldScale);
                        float f1 = this.FrameOriginY + (float) (NewY * this.cameraMap2D.worldScale);
                        IconDraw.setColor(Army.color(actor.getArmy()));
                        IconDraw.render(mat, f, f1);
                    }
                }
            }
        }
    }

    private void drawAir() {
        int i = this.airs.size();
        for (int j = 0; j < i; j++) {
            Actor actor = (Actor) this.airs.get(j);
            // TODO: +++ Hide dead ground objects from map! +++
            if (!actor.isAlive()) continue;
            // TODO: --- Hide dead ground objects from map! ---
            Point3d point3d = actor.pos.getAbsPoint();
            if (this.GUIPadMode == 1) {
                float f = (float) ((point3d.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                float f1 = (float) ((point3d.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
                IconDraw.setColor(Army.color(actor.getArmy()));
                IconDraw.render(this._iconAir, f, f1, actor.pos.getAbsOrient().azimut());
            } else if (this.GUIPadMode == 2) // By PAL, Relative mode
            {
                float NewY = (float) ((point3d.x - OwnPos3d.x) * Math.cos(this.OwnAzimut)) - (float) ((-point3d.y + OwnPos3d.y) * Math.sin(this.OwnAzimut));
                float NewX = (float) ((-point3d.y + OwnPos3d.y) * Math.cos(this.OwnAzimut)) + (float) ((point3d.x - OwnPos3d.x) * Math.sin(this.OwnAzimut));
                float f = this.FrameOriginX + (float) (NewX * this.cameraMap2D.worldScale);
                float f1 = this.FrameOriginY + (float) (NewY * this.cameraMap2D.worldScale);
                IconDraw.setColor(Army.color(actor.getArmy()));
                IconDraw.render(this._iconAir, f, f1, actor.pos.getAbsOrient().azimut() - this.OwnAngle - 90.0F);
            } else if (this.GUIPadMode == 4) {
                Aircraft aircraft = World.getPlayerAircraft();
                if (Actor.isValid(aircraft)) // Si el actor no era v�lido, no lo voy a dibujar
                {
                    Point3d pointAC = World.getPlayerAircraft().pos.getAbsPoint();
                    Orient orientAC = World.getPlayerAircraft().pos.getAbsOrient();
                    Point3d pointOrtho = new Point3d();
                    pointOrtho.set(actor.pos.getAbsPoint());

                    pointOrtho.sub(pointAC);
                    orientAC.transformInv(pointOrtho);

                    double x = pointOrtho.x;
                    double y = pointOrtho.y;
                    double z = pointOrtho.z;

                    double FOV = Math.sqrt(x * x + y * y + z * z) / Math.sqrt(x * x + y * y); // Math.abs(((Tuple3d)(pointOrtho)).z) / 5.0F;

                    double NewX = -y * FOV;
                    double NewY = x * FOV;

                    float f = this.FrameOriginX + (float) (NewX * this.cameraMap2D.worldScale);
                    float f1 = this.FrameOriginY + (float) (NewY * this.cameraMap2D.worldScale);

                    int size = (int) Math.round((10 - this.curScale) * 2D * this.client.root.win.dx / 1024D); // By PAL 32D
                    IconDraw.setScrSize(size, size);

                    IconDraw.setColor(0xff00ffff); // Green radar
                    IconDraw.render(this._iconRadar, f, f1, 0.0F);// actor.pos.getAbsOrient().azimut() - orientAC.azimut() - 90.0F);
                }
            } else if (this.GUIPadMode == 5) {
                Aircraft aircraft = World.getPlayerAircraft();
                if (Actor.isValid(aircraft)) // Si el actor no era v�lido, no lo voy a dibujar
                {
                    Point3d pointAC = World.getPlayerAircraft().pos.getAbsPoint();
                    Orient orientAC = World.getPlayerAircraft().pos.getAbsOrient();
                    Point3d pointOrtho = new Point3d();
                    pointOrtho.set(actor.pos.getAbsPoint());

                    pointOrtho.sub(pointAC);
                    orientAC.transformInv(pointOrtho);

                    double x = pointOrtho.x;
                    if (x > 5.0F) // Mayor que 5m, en frente del radar
                    {
                        double y = pointOrtho.y;
                        double z = pointOrtho.z;

                        double FOV = 1; // Math.abs(((Tuple3d)(pointOrtho)).x) / 5.0F;

                        double NewX = -y * FOV;
                        double NewY = z * FOV;

                        float f = this.FrameOriginX + (float) (NewX * this.cameraMap2D.worldScale);
                        float f1 = this.FrameOriginY + (float) (NewY * this.cameraMap2D.worldScale);

                        int size = (int) Math.round((20 - this.curScale) * 2D * this.client.root.win.dx / 1024D / (1 + x / 800)); // By PAL 32D
                        IconDraw.setScrSize(size, size);

                        IconDraw.setColor(0xff00ffff); // Green radar
                        IconDraw.render(this._iconRadar, f, f1, 0.0F);// actor.pos.getAbsOrient().kren() - orientAC.kren() - 360.0F);
                    }
                }
            }
        }
        // By PAL, I will clear the airs in FillAir!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        this.airs.clear();
    }

    private Mat getIconAir(int i) {
        String s = null;
        switch (i) {
            case 0: // '\0'
                s = "normfly";
                break;

            case 1: // '\001'
                s = "takeoff";
                break;

            case 2: // '\002'
                s = "landing";
                break;

            case 3: // '\003'
                s = "gattack";
                break;

            default:
                return null;
        }
        return IconDraw.get("icons/" + s + ".mat");
    }

    private void drawPlayerPath() {
        Autopilotage autopilotage = World.getPlayerFM().AP;
        if (autopilotage != null) {
            Way way = autopilotage.way;
            if (way != null) {
                int i = way.Cur();
                int i_62_ = way.size();
                if (i_62_ > 0 && i < i_62_) {
                    if (this.lineNXYZ.length / 3 <= i_62_) this.lineNXYZ = new float[(i_62_ + 1) * 3];
                    this.lineNCounter = 0;
                    int i_63_ = 0;
                    Render.drawBeginLines(-1);
                    while (i_63_ < i_62_) {
                        WayPoint waypoint = way.get(i_63_++);
                        waypoint.getP(this._wayP);
                        this.lineNXYZ[this.lineNCounter * 3 + 0] = (float) ((this._wayP.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                        this.lineNXYZ[this.lineNCounter * 3 + 1] = (float) ((this._wayP.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
                        this.lineNXYZ[this.lineNCounter * 3 + 2] = 0.0F;
                        this.lineNCounter++;
                    }
                    Render.drawLines(this.lineNXYZ, this.lineNCounter, 2.0F, -16777216, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE | Mat.BLEND, 3);
                    if (!World.cur().diffCur.No_Player_Icon && !World.cur().diffCur.RealisticNavigationInstruments) {
                        Point3d point3d = World.getPlayerAircraft().pos.getAbsPoint();
                        this.lineNXYZ[0] = (float) ((point3d.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                        this.lineNXYZ[1] = (float) ((point3d.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
                        this.lineNXYZ[2] = 0.0F;
                        WayPoint waypoint = way.get(i);
                        waypoint.getP(this._wayP);
                        this.lineNXYZ[3] = (float) ((this._wayP.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                        this.lineNXYZ[4] = (float) ((this._wayP.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
                        this.lineNXYZ[5] = 0.0F;
                        Render.drawLines(this.lineNXYZ, 2, 2.0F, -16777216, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE | Mat.BLEND, 3);
                    }
                    float f = 0.0F;
                    Render.drawEnd();
                    i_63_ = 0;
                    while (i_63_ < i_62_) {
                        WayPoint waypoint = way.get(i_63_++);
                        waypoint.getP(this._wayP);
                        float f_64_ = (float) ((this._wayP.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                        float f_65_ = (float) ((this._wayP.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
                        IconDraw.render(this.getIconAir(waypoint.Action), f_64_, f_65_);
                        if (this.curScale < (int) (4.0F - this.bigFontMultip) && i_63_ < i_62_) {
                            WayPoint waypoint_66_ = way.get(i_63_);
                            waypoint_66_.getP(this._wayP2);
                            this._wayP.sub(this._wayP2);
                            float f_67_ = 57.32484F * (float) Math.atan2(this._wayP.x, this._wayP.y);
                            for (f_67_ = (f_67_ + 180.0F) % 360.0F; f_67_ < 0.0F; f_67_ += 360.0F)
                                ;
                            for (; f_67_ >= 360.0F; f_67_ -= 360.0F)
                                ;
                            f_67_ = Math.round(f_67_);
                            double d = Math.sqrt(this._wayP.y * this._wayP.y + this._wayP.x * this._wayP.x) / 1000.0;
                            if (!(d < 1.0)) {
                                String string = "km";
                                if (HUD.drawSpeed() == 2 || HUD.drawSpeed() == 3) {
                                    d *= 0.5399569869041443;
                                    string = "nm";
                                }
                                d = Math.round(d);
                                float f_68_ = 0.0F;
                                float f_69_ = 0.0F;
                                if (f_67_ >= 0.0F && f_67_ < 90.0F) {
                                    f_68_ = 15.0F;
                                    f_69_ = -20.0F;
                                    if (f >= 270.0F && f <= 360.0F) {
                                        f_68_ = -35.0F * this.bigFontMultip;
                                        f_69_ = 30.0F * this.bigFontMultip;
                                    }
                                } else if (f_67_ >= 90.0F && f_67_ < 180.0F) {
                                    f_68_ = 15.0F;
                                    f_69_ = 30.0F * this.bigFontMultip;
                                    if (f >= 180.0F && f < 270.0F) {
                                        f_68_ = -35.0F * this.bigFontMultip;
                                        f_69_ = -20.0F;
                                    }
                                } else if (f_67_ >= 180.0F && f_67_ < 270.0F) {
                                    f_68_ = -35.0F * this.bigFontMultip;
                                    f_69_ = 30.0F * this.bigFontMultip;
                                    if (f >= 90.0F && f < 180.0F) {
                                        f_68_ = 15.0F;
                                        f_69_ = 30.0F * this.bigFontMultip;
                                    }
                                } else if (f_67_ >= 270.0F && f_67_ <= 360.0F) {
                                    f_68_ = -35.0F * this.bigFontMultip;
                                    f_69_ = -20.0F;
                                    if (f >= 0.0F && f < 90.0F) {
                                        f_68_ = 15.0F;
                                        f_69_ = -20.0F;
                                    }
                                }
                                f = f_67_;
                                this.waypointFont.output(-16777216, f_64_ + f_68_, f_65_ + f_69_ - 0.0F, 0.0F, "" + i_63_);
                                this.waypointFont.output(-16777216, f_64_ + f_68_, f_65_ + f_69_ - 12.0F * this.bigFontMultip, 0.0F, (int) f_67_ + "\u00b0");
                                this.waypointFont.output(-16777216, f_64_ + f_68_, f_65_ + f_69_ - 24.0F * this.bigFontMultip, 0.0F, (int) d + string);
                            }
                        }
                    }
                }
            }
        }
    }

    public GUIPad THIS() {
        return this;
    }

    public GUIPad(GWindowRoot gwindowroot) {
        // TODO: Added by |ZUTI|
        // ---------------------------------------------
        this.iconBornPlace = IconDraw.get("icons/born.mat");

        this.lineBeamXYZ = new float[9]; // By PAL, for radar Beam (2 lines)
        this.lineBeamXYZ[6] = 0.0F;
        this.lineBeamXYZ[7] = 0.0F;
        // ---------------------------------------------

        this.bigFontMultip = 1.0F;
        this._wayP2 = new Point3f();

        this.frameRegion = new GRegion(0.05F, 0.1F, 0.35F, 0.6F);
        this.targets = new ArrayList();
        this.scales = this.scale.length;
        this.curScale = 0;
        this.bActive = false;
        this.line2XYZ = new float[6];
        this._gridX = new int[20];
        this._gridY = new int[20];
        this._gridVal = new int[20];
        this.airdrome = new ArrayList();
        this._army = new int[Army.amountNet()];
        this.armyAccum = new ArmyAccum();
        this.airs = new ArrayList();
        this.radarPlane = new ArrayList();
        this.radarOther = new ArrayList();
        this._wayP = new Point3f();
        this.lineNXYZ = new float[6];
        this.client = (GUIClient) gwindowroot.create(new GUIClient() {

            public void render() {
                int i = GUIPad.this.client.root.C.alpha;
                GUIPad.this.client.root.C.alpha = 255;
                super.render();
                GUIPad.this.client.root.C.alpha = i;
            }

        });
        this.frame = (GWindowFramed) this.client.create(0.0F, 0.0F, 1.0F, 1.0F, false, new GWindowFramed() {

            public void resized() {
                super.resized();
                if (GUIPad.this.renders != null) GUIPad.this.renders.setPosSize(this.win.dx * GUIPad.this.mapView[0], this.win.dy * GUIPad.this.mapView[1], this.win.dx * GUIPad.this.mapView[2], this.win.dy * GUIPad.this.mapView[3]);
                if (GUIPad.this.renders1 != null) GUIPad.this.renders1.setPosSize(0.0F, 0.0F, this.win.dx, this.win.dy);
            }

            public void render() {
            }
        });
        this.frame.bSizable = false;
        this.renders = new GUIRenders(this.frame, 0.0F, 0.0F, 1.0F, 1.0F, false);
        this.renders1 = new GUIRenders(this.frame, 0.0F, 0.0F, 1.0F, 1.0F, false) {
            public void doPopUpMenu() {
                // if (popUpMenu == null)
                {
                    if (World.cur().diffCur.No_Map_Icons) {
                        GUIPad.this.popUpMenu = (GWindowMenuPopUp) this.create(new GWindowMenuPopUp());
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("Normal_Mode"), Plugin.i18n("Normal Mode")) {
                            public void execute() {
                                GUIPad.this.GUIPadMode = 1;
                                GUIPad.this.leave(false);
                                // frameRegion = new GRegion(0.05F, 0.1F, 0.35F, 0.6F);
                                GUIPad.this.enter();
                            }
                        });
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("Navigation_Map"), Plugin.i18n("Navigation Map")) {
                            public void execute() {
                                GUIPad.this.GUIPadMode = 3;
                                GUIPad.this.leave(false);
                                // frameRegion.set(0.05F, 0.1F, 0.6F, 0.6F);
                                // int Alpha = frame.root.C.alpha;
                                // renders1.root.C.alpha = 128; //By PAL, Make it semi transparent
                                GUIPad.this.enter();
                                // renders1.root.C.alpha = Alpha; //By PAL, return it to the original transparency level
                            }
                        });
                        GUIPad.this.popUpMenu.addItem("-", null);
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("Mission_Brief"), Plugin.i18n("Mission Brief")) {
                            public void execute() {
                                GUIPad.this.GUIPadMode = 6;
                                GUIPad.this.leave(false);
                                // frameRegion = new GRegion(0.05F, 0.1F, 0.35F, 0.6F);
                                GUIPad.this.enter();
                            }
                        });
                        GUIPad.this.popUpMenu.addItem("-", null);
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("Revert_zoom_axis"), Plugin.i18n("Revert mouse wheel/zoom axis")) {
                            public void execute() {
                                // TODO:
                                GUIPad.this.zutiMouseWheelDirection = GUIPad.this.zutiMouseWheelDirection * -1;
                                GUIPad.this.leave(false);
                                // frameRegion = new GRegion(0.05F, 0.1F, 0.35F, 0.6F);
                                GUIPad.this.enter();
                            }
                        });
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("IL2_zoom_mode"), Plugin.i18n("Old Zooming")) {
                            public void execute() {
                                GUIPad.this.zutiZoomingOldMode = !GUIPad.this.zutiZoomingOldMode;
                                GUIPad.this.leave(false);
                                // frameRegion = new GRegion(0.05F, 0.1F, 0.35F, 0.6F);
                                GUIPad.this.enter();
                            }
                        });

                    } else {
                        GUIPad.this.popUpMenu = (GWindowMenuPopUp) this.create(new GWindowMenuPopUp());
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("Normal_Mode"), Plugin.i18n("Normal Mode")) {
                            public void execute() {
                                GUIPad.this.GUIPadMode = 1;
                                GUIPad.this.leave(false);
                                // frameRegion = new GRegion(0.05F, 0.1F, 0.35F, 0.6F);
                                GUIPad.this.enter();
                            }
                        });
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("Relative_Mode"), Plugin.i18n("Relative Mode")) {
                            public void execute() {
                                GUIPad.this.GUIPadMode = 2;
                                GUIPad.this.leave(false);
                                // frameRegion = new GRegion(0.05F, 0.1F, 0.35F, 0.6F);
                                GUIPad.this.enter();
                            }
                        });
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("Navigation_Map"), Plugin.i18n("Navigation Map")) {
                            public void execute() {
                                GUIPad.this.GUIPadMode = 3;
                                GUIPad.this.leave(false);
                                // frameRegion.set(0.05F, 0.1F, 0.6F, 0.6F);
                                // int Alpha = frame.root.C.alpha;
                                // renders1.root.C.alpha = 128; //By PAL, Make it semi transparent
                                GUIPad.this.enter();
                                // renders1.root.C.alpha = Alpha; //By PAL, return it to the original transparency level
                            }
                        });
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("Radar_View_Radial"), Plugin.i18n("Radar View (Radial)")) {
                            public void execute() {
                                GUIPad.this.GUIPadMode = 4;
                                GUIPad.this.leave(false);
                                // frameRegion.set(0.05F, 0.1F, 0.35F, 0.42F);
                                GUIPad.this.enter();
                            }
                        });
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("Radar_View_Frontal"), Plugin.i18n("Radar View (Frontal)")) {
                            public void execute() {
                                GUIPad.this.GUIPadMode = 5;
                                GUIPad.this.leave(false);
                                // frameRegion.set(0.05F, 0.1F, 0.35F, 0.42F);
                                GUIPad.this.enter();
                            }
                        });
                        GUIPad.this.popUpMenu.addItem("-", null);
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("Mission_Brief"), Plugin.i18n("Mission Brief")) {
                            public void execute() {
                                GUIPad.this.GUIPadMode = 6;
                                GUIPad.this.leave(false);
                                // frameRegion = new GRegion(0.05F, 0.1F, 0.35F, 0.6F);
                                GUIPad.this.enter();
                            }
                        });
                        GUIPad.this.popUpMenu.addItem("-", null);
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("Revert_zoom_axis"), Plugin.i18n("Revert mouse wheel/zoom axis")) {
                            public void execute() {
                                // TODO:
                                GUIPad.this.zutiMouseWheelDirection = GUIPad.this.zutiMouseWheelDirection * -1;
                                GUIPad.this.leave(false);
                                // frameRegion = new GRegion(0.05F, 0.1F, 0.35F, 0.6F);
                                GUIPad.this.enter();
                            }
                        });
                        GUIPad.this.popUpMenu.addItem(new GWindowMenuItem(GUIPad.this.popUpMenu, Plugin.i18n("IL2_zoom_mode"), Plugin.i18n("Old Zooming")) {
                            public void execute() {
                                GUIPad.this.zutiZoomingOldMode = !GUIPad.this.zutiZoomingOldMode;
                                GUIPad.this.leave(false);
                                // frameRegion = new GRegion(0.05F, 0.1F, 0.35F, 0.6F);
                                GUIPad.this.enter();
                            }
                        });

                    }

                    Plugin.builder.mEdit.subMenu.addItem(2, "-", null);

                    GPoint MPos = this.getMouseXY();
                    GUIPad.this.popUpMenu.setPos(MPos.x, MPos.y);
                    for (int i = 1; i < GUIPad.this.popUpMenu.size(); i++)
                        GUIPad.this.popUpMenu.getItem(i).bChecked = false;

                    if (World.cur().diffCur.No_Map_Icons) switch (GUIPad.this.GUIPadMode) {
                        case 1:
                            GUIPad.this.popUpMenu.getItem(0).bChecked = true;
                            break;
                        case 3:
                            GUIPad.this.popUpMenu.getItem(1).bChecked = true;
                            break;
                        case 6:
                            GUIPad.this.popUpMenu.getItem(3).bChecked = true;
                            break;
                        default:
                            break;
                    }
                    else switch (GUIPad.this.GUIPadMode) {
                        case 1:
                            GUIPad.this.popUpMenu.getItem(0).bChecked = true;
                            break;
                        case 2:
                            GUIPad.this.popUpMenu.getItem(1).bChecked = true;
                            break;
                        case 3:
                            GUIPad.this.popUpMenu.getItem(2).bChecked = true;
                            break;
                        case 4:
                            GUIPad.this.popUpMenu.getItem(3).bChecked = true;
                            break;
                        case 5:
                            GUIPad.this.popUpMenu.getItem(4).bChecked = true;
                            break;
                        case 6:
                            GUIPad.this.popUpMenu.getItem(6).bChecked = true;
                            break;
                        default:
                            break;
                    }
                }
                // else
                {
                    if (GUIPad.this.popUpMenu.isVisible()) return;
                    // popUpMenu.clearItems();
                    if (GUIPad.this.popUpMenu.size() > 0) {
                        GPoint MPos = this.getMouseXY();
                        GUIPad.this.popUpMenu.setPos(MPos.x, MPos.y);
                        for (int i = 1; i < GUIPad.this.popUpMenu.size(); i++)
                            GUIPad.this.popUpMenu.getItem(i).bChecked = false;

                        if (World.cur().diffCur.No_Map_Icons) switch (GUIPad.this.GUIPadMode) {
                            case 1:
                                GUIPad.this.popUpMenu.getItem(0).bChecked = true;
                                break;
                            case 3:
                                GUIPad.this.popUpMenu.getItem(1).bChecked = true;
                                break;
                            case 6:
                                GUIPad.this.popUpMenu.getItem(3).bChecked = true;
                                break;
                            default:
                                break;
                        }
                        else switch (GUIPad.this.GUIPadMode) {
                            case 1:
                                GUIPad.this.popUpMenu.getItem(0).bChecked = true;
                                break;
                            case 2:
                                GUIPad.this.popUpMenu.getItem(1).bChecked = true;
                                break;
                            case 3:
                                GUIPad.this.popUpMenu.getItem(2).bChecked = true;
                                break;
                            case 4:
                                GUIPad.this.popUpMenu.getItem(3).bChecked = true;
                                break;
                            case 5:
                                GUIPad.this.popUpMenu.getItem(4).bChecked = true;
                                break;
                            case 6:
                                GUIPad.this.popUpMenu.getItem(6).bChecked = true;
                                break;
                            default:
                                break;
                        }
                        GUIPad.this.popUpMenu.showModal();
                    }
                }
            }

            public void mouseButton_oldWay(int i, boolean bool, float f, float f_58_) {
                if (i == 0) {
                    this.bLPressed = bool;
                    this.mouseCursor = this.bLPressed ? 3 : 1;
                } else if (i == 1 && GUIPad.this.scales > 1) {
                    this.bRPressed = bool;
                    if (this.bRPressed && !this.bLPressed) {
                        f -= GUIPad.this.THIS().renders.win.x;
                        f_58_ -= GUIPad.this.THIS().renders.win.y;
                        float f_59_ = (float) (GUIPad.this.cameraMap2D.worldXOffset + f / GUIPad.this.cameraMap2D.worldScale);
                        float f_60_ = (float) (GUIPad.this.cameraMap2D.worldYOffset + (GUIPad.this.THIS().renders.win.dy - f_58_ - 1.0F) / GUIPad.this.cameraMap2D.worldScale);
                        GUIPad.this.curScale += GUIPad.this.curScaleDirect;
                        if (GUIPad.this.curScaleDirect < 0) {
                            if (GUIPad.this.curScale < 0) {
                                GUIPad.this.curScale = 1;
                                GUIPad.this.curScaleDirect = 1;
                            }
                        } else if (GUIPad.this.curScale >= GUIPad.this.scales) {
                            GUIPad.this.curScale = GUIPad.this.scales - 2;
                            GUIPad.this.curScaleDirect = -1;
                        }
                        GUIPad.this.scaleCamera();
                        f_59_ -= (f - GUIPad.this.THIS().renders.win.dx / 2.0F) / GUIPad.this.cameraMap2D.worldScale;
                        f_60_ += (f_58_ - GUIPad.this.THIS().renders.win.dy / 2.0F) / GUIPad.this.cameraMap2D.worldScale;
                        GUIPad.this.setPosCamera(f_59_, f_60_);
                    }
                }
            }

            // By PAL, to allow operation with Mouse Wheel
            public void mouseButton(int i, boolean flag, float f, float f1) {
                if (i == 0) this.bLPressed = flag;
                else if (i == 1) {
                    this.bRPressed = flag;
                    if (!this.bMPressed && !this.bLPressed && !GUIPad.this.zutiZoomingOldMode) this.doPopUpMenu();
                    else this.mouseButton_oldWay(i, flag, f, f1);
                } else if (i == 2) this.bMPressed = flag;
                if (this.bLPressed && this.bRPressed || this.bMPressed) this.mouseCursor = 7;

                // TODO: Added by |ZUTI|: if both buttons are pressed and we have OLD way of zooming, enable drop down
                // -----------------------------------------------
                if (this.bLPressed && this.bRPressed) GUIPad.this.zutiZoomingOldMode = false;

                if (GUIPad.this.GUIPadMode == 1 || GUIPad.this.GUIPadMode == 4 || GUIPad.this.GUIPadMode == 5) if (this.bLPressed && !this.bRPressed && !this.bMPressed) this.mouseCursor = 3; // Only left mouse
                if (!this.bLPressed && !this.bMPressed && !this.bRPressed) this.mouseCursor = 1;
            }

            public void mouseRelMove(float f, float f1, float v) // By PAL
            {
                // bRPressed = flag;
                // if(bRPressed && !bLPressed)
                if (GUIPad.this.GUIPadMode == 6) return;
                if (this.bRPressed || this.bLPressed || this.bMPressed) return;
                GPoint MPos = this.getMouseXY();
                f = MPos.x;
                f1 = MPos.y;
                if (v != 0.0F) {
                    f -= GUIPad.this.THIS().renders.win.x;
                    f1 -= GUIPad.this.THIS().renders.win.y;
                    // if (f < 0) f = (THIS().renders).win.x;
                    // if (f1 < 0) f1 = (THIS().renders).win.y;
                    float f2 = (float) (GUIPad.this.cameraMap2D.worldXOffset + f / GUIPad.this.cameraMap2D.worldScale);
                    float f3 = (float) (GUIPad.this.cameraMap2D.worldYOffset + (GUIPad.this.THIS().renders.win.dy - f1 - 1.0F) / GUIPad.this.cameraMap2D.worldScale);

                    // TODO: Added by |ZUTI|: reverting mouse wheel axis
                    if (GUIPad.this.zutiMouseWheelDirection > 0) {
                        if (v > 0.0F) if (GUIPad.this.curScale < GUIPad.this.scales - 1) GUIPad.this.curScale++;
                        if (v < 0.0F) if (GUIPad.this.curScale > 0) GUIPad.this.curScale--;
                    } else {
                        if (v < 0.0F) if (GUIPad.this.curScale < GUIPad.this.scales - 1) GUIPad.this.curScale++;
                        if (v > 0.0F) if (GUIPad.this.curScale > 0) GUIPad.this.curScale--;
                    }

                    GUIPad.this.scaleCamera();
                    f2 = (float) (f2 - (f - GUIPad.this.THIS().renders.win.dx / 2.0F) / GUIPad.this.cameraMap2D.worldScale);
                    f3 = (float) (f3 + (f1 - GUIPad.this.THIS().renders.win.dy / 2.0F) / GUIPad.this.cameraMap2D.worldScale);
                    GUIPad.this.setPosCamera(f2, f3);
                }
            }

            public void mouseMove(float f, float f1) {
                if (this.bLPressed && this.bRPressed || this.bMPressed) GUIPad.this.frame.setPos(GUIPad.this.frame.win.x + this.root.mouseStep.dx, GUIPad.this.frame.win.y + this.root.mouseStep.dy);
                else if (this.bLPressed) if (GUIPad.this.GUIPadMode == 1 || GUIPad.this.GUIPadMode == 3) {
                    GUIPad.this.cameraMap2D.worldXOffset -= this.root.mouseStep.dx / GUIPad.this.cameraMap2D.worldScale;
                    GUIPad.this.cameraMap2D.worldYOffset += this.root.mouseStep.dy / GUIPad.this.cameraMap2D.worldScale;
                    GUIPad.this.clipCamera();
                }
            }

            // By PAL
            boolean bLPressed;
            boolean bRPressed;
            boolean bMPressed;
            {
                this.bLPressed = false;
                this.bRPressed = false;
                this.bMPressed = false;
            }
        };
        this.cameraMap2D = new CameraOrtho2D();
        this.cameraMap2D.worldScale = this.scale[this.curScale];
        this.renderMap2D = new RenderMap2D(this.renders.renders, 1.0F);
        this.renderMap2D.setCamera(this.cameraMap2D);
        this.renderMap2D.setShow(true);
        this.cameraMap2D1 = new CameraOrtho2D();
        this.renderMap2D1 = new RenderMap2D1(this.renders1.renders, 1.0F);
        this.renderMap2D1.setCamera(this.cameraMap2D1);
        this.renderMap2D1.setShow(true);
        LightEnvXY lightenvxy = new LightEnvXY();
        this.renderMap2D.setLightEnv(lightenvxy);
        this.renderMap2D1.setLightEnv(lightenvxy);
        lightenvxy.sun().setLight(0.5F, 0.5F, 1.0F, 1.0F, 1.0F, 0.8F);
        Vector3f vector3f = new Vector3f(1.0F, -2F, -1F);
        vector3f.normalize();
        lightenvxy.sun().set(vector3f);
        this.gridFont = TTFont.font[1];
        this.smallFont = TTFont.font[0]; // By PAL, small font for different purpuouses
        this.mesh = GMesh.New("gui/game/pad/mono.sim");
        this.meshradar = GMesh.New("gui/game/radar/mono.sim");
        this.meshradarF = GMesh.New("gui/game/radarF/mono.sim");
        this._iconAir = Mat.New("icons/plane.mat");
        // meshbrief = GMesh.New("gui/game/brief/mono.sim");
        this._iconRadar = Mat.New("icons/radarimage.mat"); // By PAL, icon of the radar
        this._iconShipRadar = Mat.New("icons/radarshipimage.mat"); // By PAL, icon of the radar
        this._iconAirField = Mat.New("icons/airfield.mat"); // By PAL, icon of the radar
        this.emptyMat = Mat.New("icons/empty.mat");

        if (World.cur().smallMapWPLabels) {
            this.waypointFont = TTFont.font[0];
            this.bigFontMultip = 1.0F;
        } else {
            this.waypointFont = TTFont.font[1];
            this.bigFontMultip = 2.0F;
        }
        this._iconILS = Mat.New("icons/ILS.mat");

        this.main = Main3D.cur3D();
        this.client.hideWindow();
    }

    // TODO: Methods and variables by |ZUTI|
    // ---------------------------------------------------------------------
    /**
     * Key=integer(ZutiPadObject hash code), value = ZutiPadObject
     */
    public Map       zutiPadObjects          = new HashMap();
    public boolean   zutiPlayeAcDrawn        = false;
    public ArrayList zutiNeutralHomeBases    = null;
    public Mat       iconBornPlace           = null;
    private boolean  zutiZoomingOldMode      = false;
    private int      zutiMouseWheelDirection = 1;

    private boolean  zutiColorAirfields      = true;

    /**
     * Reset class variables
     */
    public static void resetClassVariables() {
        if (GUI.pad != null) {
            GUI.pad.zutiPadObjects.clear();
            GUI.pad.zutiColorAirfields = true;
            GUI.pad.zutiPlayeAcDrawn = false;
            if (GUI.pad.zutiNeutralHomeBases != null) GUI.pad.zutiNeutralHomeBases.clear();
            GUI.pad.iconBornPlace = null;
        }
    }

    /**
     * Get a list containing all airports on the loaded map.
     *
     * @return
     */
    public List zutiGetAirdromes() {
        return this.airdrome;
    }

    private void pablo_drawAirports(Point3d position, int color, float azimut, Mat mat) {
        if (this.GUIPadMode == 1) {
            float f = (float) ((position.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
            float f1 = (float) ((position.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
            IconDraw.setColor(color);
            IconDraw.render(mat, f, f1);
        } else if (this.GUIPadMode == 2) {
            float NewY = (float) ((position.x - OwnPos3d.x) * Math.cos(this.OwnAzimut)) - (float) ((-position.y + OwnPos3d.y) * Math.sin(this.OwnAzimut));
            float NewX = (float) ((-position.y + OwnPos3d.y) * Math.cos(this.OwnAzimut)) + (float) ((position.x - OwnPos3d.x) * Math.sin(this.OwnAzimut));
            float f = this.FrameOriginX + (float) (NewX * this.cameraMap2D.worldScale);
            float f1 = this.FrameOriginY + (float) (NewY * this.cameraMap2D.worldScale);
            IconDraw.setColor(color);
            IconDraw.render(mat, f, f1);
        } else if (this.GUIPadMode == 3) {
            float f = (float) ((position.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
            float f1 = (float) ((position.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
            IconDraw.setColor(color);
            // IconDraw.render(mat, f, f1); //By PAL, to draw them twisted
            IconDraw.render(this._iconAirField, f, f1, azimut);
        }
    }

    private void zutiRenderPad_MDSWay() {
        // Added by |ZUTI|
        // -----------------------------
        if (this.GUIPadMode == 4 || this.GUIPadMode == 5 || this.GUIPadMode == 6 || !World.cur().diffCur.No_Map_Icons) {
            // If user selected any of the above pad modes, forward things to original Pablo methods.
            // Also, execute this if user allows rendering of map icons.
            this.zutiRenderPad_PabloWay();

            this.drawRadioBeacons();
            return;
        }
        // -----------------------------

        try {
            // Modified by |ZUTI|
            // int i = (int) Math.round(32.0 * (double) client.root.win.dx / 1024.0); //Original
            // int i = (int) Math.round(ZutiMDSVariables.ZUTI_ICON_SIZE * (double) client.root.win.dx / 1024.0); //MDS way
            // int iconsSizeMultiplier = (int) Math.round(((10 - curScale) * 3D * (double) client.root.win.dx) / 1024D); //By PAL 32D
            int iconsSizeMultiplier = (int) Math.round((ZutiMDSVariables.ZUTI_ICON_SIZE - this.curScale) * (double) this.client.root.win.dx / 1024D); // Hybrid - MDS and PAL - icon size depends olso on current scale
            Aircraft playerAc = World.getPlayerAircraft();
            // Do pad mode related stuff before you continue to rendering
            switch (this.GUIPadMode) {
                case 2: {
                    this.drawGrid2DFixed();
                    if (Actor.isValid(playerAc) && !World.cur().diffCur.No_Map_Icons) {
                        this.OwnPos3d = playerAc.pos.getAbsPoint();
                        this.OwnAzimut = Math.toRadians(360.0F - playerAc.pos.getAbsOrient().azimut());
                        this.OwnAngle = playerAc.pos.getAbsOrient().azimut();
                    } else // If player AC is not valid just return as all data
                           // for this mode relies on that!
                        return;

                    break;
                }
                default: {
                    // Mode 2 must not show ground/map texture in the background
                    if (this.main.land2D != null) this.main.land2D.render(0.9F, 0.9F, 0.9F, 1.0F);

                    if (this.main.land2DText != null) this.main.land2DText.render();

                    GUIPad.this.drawGrid2D();
                    break;
                }
            }

            // Update radar objects and their visibility
            ZutiRadarRefresh.update();
            this.zutiPlayeAcDrawn = false;
            // Draw Front lines
            Front.render(false);
            // Draw icons with predefined (mode dependent) size
            IconDraw.setScrSize(iconsSizeMultiplier, iconsSizeMultiplier);
            // Draw targets
            ZutiSupportMethods_GUI.drawTargets(GUIPad.this.renders, this.gridFont, this.emptyMat, this.cameraMap2D);
            // Draw airports
            GUIPad.this.drawAirports();
            // Draw born places
            ZutiSupportMethods.drawBornPlaces();

            if (!World.cur().diffCur.NoMinimapPath) if (Actor.isValid(playerAc)) {
                IconDraw.setColor(0xFF00FFFF);
                GUIPad.this.drawPlayerPath();
            }

            // Added by |ZUTI|: changed this with following logic:
            // If player side has any radar object alive, show these objects, else don't show them!
            // Draw only if player side has at least one radar alive! This is called here because in our new method we will also re-draw player icon with different color (color of his army)
            if (World.cur().diffCur.No_Map_Icons && Mission.MDS_VARIABLES().zutiRadar_PlayerSideHasRadars) ZutiSupportMethods_GUI.drawPadObjects();

            // Do this last, just in case we have not drawn player AC before
            if (!World.cur().diffCur.No_Map_Icons && !this.zutiPlayeAcDrawn) if (Actor.isValid(playerAc)) {
                Point3d point3d = playerAc.pos.getAbsPoint();
                float f = (float) ((point3d.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                float f_0_ = (float) ((point3d.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
                IconDraw.setColor(-1);
                IconDraw.render(this._iconAir, f, f_0_, playerAc.pos.getAbsOrient().azimut());
            }

            this.drawRadioBeacons();

            SquareLabels.draw(this.cameraMap2D, Main3D.cur3D().land2D.worldOfsX(), Main3D.cur3D().land2D.worldOfsX(), Main3D.cur3D().land2D.mapSizeX());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Default pad rendering defined by Pablo.
     */
    public void zutiRenderPad_PabloWay() {
//        if (this.cameraMap2D == null || Main3D.cur3D() == null || Main3D.cur3D().land2D == null) {
//            System.out.println("GUI.pad.leave(false) - zutiRenderPad_PabloWay");
////            this.leave(false); // TODO: By SAS~Storebror to avoid crashes when Map is still drawing after sortie end.
//        }
        if (this.GUIPadMode == 1) // By PAL, standard map, original routine
        {
            if (this.main.land2D != null) this.main.land2D.render(0.9F, 0.9F, 0.9F, 1.0F);
            if (this.main.land2DText != null) this.main.land2DText.render();
            this.drawGrid2D();
            Front.render(false);
            int i = (int) Math.round((10 - this.curScale) * 3D * this.client.root.win.dx / 1024D); // By PAL 32D
            IconDraw.setScrSize(i, i);
            this.drawAirports();
            if (!World.cur().diffCur.No_Map_Icons) {
                this.drawChiefs();
                this.drawAAAandFillAir();
                this.drawAir();
            }
            GUIBriefing.drawTargets(this.renders, this.gridFont, this.emptyMat, this.cameraMap2D, this.targets);
            if (!World.cur().diffCur.NoMinimapPath) {
                Aircraft aircraft = World.getPlayerAircraft();
                if (Actor.isValid(aircraft)) {
                    IconDraw.setColor(0xff00ffff);
                    this.drawPlayerPath();
                }
            }
            //if (!World.cur().diffCur.NoMinimapPath || !World.cur().diffCur.No_Map_Icons) {
            if (!World.cur().diffCur.No_Map_Icons && !World.cur().diffCur.No_Player_Icon) {
                Aircraft aircraft1 = World.getPlayerAircraft();
                if (Actor.isValid(aircraft1)) { // By PAL, draw absolute position for own plane
                    Point3d point3d = ((Actor) aircraft1).pos.getAbsPoint();
                    float f = (float) ((point3d.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                    float f1 = (float) ((point3d.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
                    IconDraw.setColor(-1);
                    IconDraw.render(this._iconAir, f, f1, ((Actor) aircraft1).pos.getAbsOrient().azimut());
                }
            }
            SquareLabels.draw(this.cameraMap2D, Main3D.cur3D().land2D.worldOfsX(), Main3D.cur3D().land2D.worldOfsX(), Main3D.cur3D().land2D.mapSizeX());
        } else if (this.GUIPadMode == 2) // By PAL, relative map
        {
            this.drawGrid2DFixed();
            // Front.renderRot(false, 0.0F);
            if (Actor.isValid(World.getPlayerAircraft()) && Actor.isAlive(World.getPlayerAircraft())) {
                this.OwnPos3d = World.getPlayerAircraft().pos.getAbsPoint();
                this.OwnAzimut = Math.toRadians(360.0F - World.getPlayerAircraft().pos.getAbsOrient().azimut());
                this.OwnAngle = World.getPlayerAircraft().pos.getAbsOrient().azimut();

                int i = (int) Math.round((10 - this.curScale) * 3D * this.client.root.win.dx / 1024D); // By PAL 32D
                IconDraw.setScrSize(i, i);
                this.drawAirports();
                if (!World.cur().diffCur.No_Map_Icons) {
                    this.drawChiefs();
                    this.drawAAAandFillAir();
                    this.drawAir();
                }
                // GUIBriefing.drawTargets(renders, gridFont, emptyMat, cameraMap2D, targets);
                //if (!World.cur().diffCur.NoMinimapPath || !World.cur().diffCur.No_Map_Icons) {
                if (!World.cur().diffCur.No_Map_Icons && !World.cur().diffCur.No_Player_Icon) {
                    Aircraft aircraft1 = World.getPlayerAircraft();
                    if (Actor.isValid(aircraft1)) { // By PAL, draw fixed central position for own plane
                        IconDraw.setColor(-1);
                        IconDraw.render(this._iconAir, this.FrameOriginX, this.FrameOriginY, -90.0F);
                    }
                }
            }
            // SquareLabels.draw(cameraMap2D, Main3D.cur3D().land2D.worldOfsX(), Main3D.cur3D().land2D.worldOfsX(), Main3D.cur3D().land2D.mapSizeX());
        } else if (this.GUIPadMode == 3) { // By PAL, navigation map
            if (this.main.land2D != null) this.main.land2D.render(0.9F, 0.9F, 0.9F, 1.0F);
            if (this.main.land2DText != null) this.main.land2DText.render();
            this.drawGrid2D();
            Front.render(false);
            int i = (int) Math.round(32D * this.client.root.win.dx / 1024D); // By PAL 32D
            IconDraw.setScrSize(i, i);
            this.drawAirports();

            GUIBriefing.drawTargets(this.renders, this.gridFont, this.emptyMat, this.cameraMap2D, this.targets);
            if (!World.cur().diffCur.NoMinimapPath) {
                Aircraft aircraft = World.getPlayerAircraft();
                if (Actor.isValid(aircraft)) {
                    IconDraw.setColor(0xff00ffff);
                    this.drawPlayerPath();
                }
            }
//            if (!World.cur().diffCur.NoMinimapPath || !World.cur().diffCur.No_Map_Icons) {
            if (!World.cur().diffCur.No_Map_Icons && !World.cur().diffCur.No_Player_Icon) {
                Aircraft aircraft1 = World.getPlayerAircraft();
                if (Actor.isValid(aircraft1)) { // By PAL, draw absolute position
                    Point3d point3d = ((Actor) aircraft1).pos.getAbsPoint();
                    float f = (float) ((point3d.x - this.cameraMap2D.worldXOffset) * this.cameraMap2D.worldScale);
                    float f1 = (float) ((point3d.y - this.cameraMap2D.worldYOffset) * this.cameraMap2D.worldScale);
                    IconDraw.setColor(-1);
                    IconDraw.render(this._iconAir, f, f1, ((Actor) aircraft1).pos.getAbsOrient().azimut());
                }
            }
            SquareLabels.draw(this.cameraMap2D, Main3D.cur3D().land2D.worldOfsX(), Main3D.cur3D().land2D.worldOfsX(), Main3D.cur3D().land2D.mapSizeX());
        } else if (this.GUIPadMode == 4) { // By PAL, radar views
            this.drawGrid2DFixed();
            Aircraft ownaircraft = World.getPlayerAircraft();
            if (!World.cur().diffCur.No_Map_Icons) if (Actor.isValid(ownaircraft) && Actor.isAlive(ownaircraft)) // Si el actor no era v�lido, no voy a dibujar nada
            {
                int ti = (int) (Time.current() % 1000 / 500);
                if (ti != this.to) {
                    this.to = ti;
                    Point3d pointAC = ownaircraft.pos.getAbsPoint();
                    Orient orientAC = ownaircraft.pos.getAbsOrient();
                    double d = (this.cameraMap2D.right - this.cameraMap2D.left) / this.cameraMap2D.worldScale; // By PAL, relative limits
                    this.radarPlane.clear(); // By PAL, cleans all the items for the radar
                    this.radarOther.clear();
                    List list = Engine.targets();
                    int i = list.size();
                    for (int j = 0; j < i; j++) {
                        Actor actor = (Actor) list.get(j);
                        if (actor instanceof Aircraft) {
                            if (actor != World.getPlayerAircraft()) {
                                Point3d pointOrtho = new Point3d();
                                pointOrtho.set(actor.pos.getAbsPoint());

                                pointOrtho.sub(pointAC);
                                orientAC.transformInv(pointOrtho);

                                double NewX = -pointOrtho.y;
                                double NewY = pointOrtho.x;

                                if (NewX >= -d && NewX <= d && NewY >= -d && NewY <= d) this.radarPlane.add(pointOrtho); // airs.add(actor);
                            }
                        } else if (actor instanceof ShipGeneric || actor instanceof BigshipGeneric) // Add the ships
                        {
                            Point3d pointOrtho = new Point3d();
                            pointOrtho.set(actor.pos.getAbsPoint());

                            pointOrtho.sub(pointAC);
                            orientAC.transformInv(pointOrtho);

                            double NewX = -pointOrtho.y;
                            double NewY = pointOrtho.x;

                            if (NewX >= -d && NewX <= d && NewY >= -d && NewY <= d) this.radarOther.add(pointOrtho); // airs.add(actor);
                        }
                    }
                }
                int i = this.radarPlane.size();
                for (int j = 0; j < i; j++) {
                    double x = ((Tuple3d) (Point3d) this.radarPlane.get(j)).x;
                    double y = ((Tuple3d) (Point3d) this.radarPlane.get(j)).y;
                    double z = ((Tuple3d) (Point3d) this.radarPlane.get(j)).z;

                    double FOV = Math.sqrt(x * x + y * y + z * z) / Math.sqrt(x * x + y * y); // Math.abs(((Tuple3d)(pointOrtho)).z) / 5.0F;

                    double NewX = -y * FOV;
                    double NewY = x * FOV;

                    float f = this.FrameOriginX + (float) (NewX * this.cameraMap2D.worldScale);
                    float f1 = this.FrameOriginY + (float) (NewY * this.cameraMap2D.worldScale);

                    int size = (int) Math.round((16 - this.curScale) * 2D * this.client.root.win.dx / 1024D); // By PAL 32D
                    IconDraw.setScrSize(size, size);

                    IconDraw.setColor(0xff00ffff); // Green radar
                    IconDraw.render(this._iconRadar, f, f1, 0.0F);// actor.pos.getAbsOrient().kren() - orientAC.kren() - 360.0F);
                }
                i = this.radarOther.size();
                for (int j = 0; j < i; j++) {
                    double x = ((Tuple3d) (Point3d) this.radarOther.get(j)).x;
                    double y = ((Tuple3d) (Point3d) this.radarOther.get(j)).y;
                    double z = ((Tuple3d) (Point3d) this.radarOther.get(j)).z;

                    double FOV = Math.sqrt(x * x + y * y + z * z) / Math.sqrt(x * x + y * y); // Math.abs(((Tuple3d)(pointOrtho)).z) / 5.0F;

                    double NewX = -y * FOV;
                    double NewY = x * FOV;

                    float f = this.FrameOriginX + (float) (NewX * this.cameraMap2D.worldScale);
                    float f1 = this.FrameOriginY + (float) (NewY * this.cameraMap2D.worldScale);

                    int size = (int) Math.round((16 - this.curScale) * 2D * this.client.root.win.dx / 1024D); // By PAL 32D
                    IconDraw.setScrSize(size, size);

                    IconDraw.setColor(0xff00ffff); // Green radar
                    IconDraw.render(this._iconShipRadar, f, f1, 0.0F);// actor.pos.getAbsOrient().kren() - orientAC.kren() - 360.0F);
                }
            }
        } else if (this.GUIPadMode == 5) {
            this.drawGrid2DFixed();
            Aircraft ownaircraft = World.getPlayerAircraft();
            if (!World.cur().diffCur.No_Map_Icons) if (Actor.isValid(ownaircraft) && Actor.isAlive(ownaircraft)) // Si el actor no era v�lido, no voy a dibujar nada
            {
                int ti = (int) (Time.current() % 1000 / 500);
                if (ti != this.to) {
                    this.to = ti;
                    Point3d pointAC = ownaircraft.pos.getAbsPoint();
                    Orient orientAC = ownaircraft.pos.getAbsOrient();
                    double d = (this.cameraMap2D.right - this.cameraMap2D.left) / this.cameraMap2D.worldScale; // By PAL, relative limits
                    this.radarPlane.clear(); // By PAL, cleans all the items for the radar
                    this.radarOther.clear();
                    List list = Engine.targets();
                    int i = list.size();
                    for (int j = 0; j < i; j++) {
                        Actor actor = (Actor) list.get(j);
                        if (actor instanceof Aircraft) {
                            if (actor != World.getPlayerAircraft()) {
                                Point3d pointOrtho = new Point3d();
                                pointOrtho.set(actor.pos.getAbsPoint());

                                pointOrtho.sub(pointAC);
                                orientAC.transformInv(pointOrtho);

                                double NewX = -pointOrtho.y;
                                double NewY = pointOrtho.z;

                                if (NewX >= -d && NewX <= d && NewY >= -d && NewY <= d) this.radarPlane.add(pointOrtho); // airs.add(actor);
                            }
                        } else if (actor instanceof ShipGeneric || actor instanceof BigshipGeneric) // Add the ships
                        {
                            Point3d pointOrtho = new Point3d();
                            pointOrtho.set(actor.pos.getAbsPoint());

                            pointOrtho.sub(pointAC);
                            orientAC.transformInv(pointOrtho);

                            double NewX = -pointOrtho.y;
                            double NewY = pointOrtho.z;

                            if (NewX >= -d && NewX <= d && NewY >= -d && NewY <= d) this.radarOther.add(pointOrtho); // airs.add(actor);
                        }
                    }
                }
                int i = this.radarPlane.size();
                for (int j = 0; j < i; j++) {
                    double x = ((Tuple3d) (Point3d) this.radarPlane.get(j)).x;
                    if (x > 5.0F) // Mayor que 5m, en frente del radar
                    {
                        double FOV = 1; // Math.abs(((Tuple3d)((Point3d)radarPlane.get(j))).x) / 5.0F;

                        double NewX = -((Tuple3d) (Point3d) this.radarPlane.get(j)).y * FOV;
                        double NewY = ((Tuple3d) (Point3d) this.radarPlane.get(j)).z * FOV;

                        float f = this.FrameOriginX + (float) (NewX * this.cameraMap2D.worldScale);
                        float f1 = this.FrameOriginY + (float) (NewY * this.cameraMap2D.worldScale);

                        int size = (int) Math.round((16 - this.curScale) * 3D * this.client.root.win.dx / 1024D / (1 + x / 800)); // By PAL 32D
                        IconDraw.setScrSize(size, size);

                        IconDraw.setColor(0xff00ffff); // Green radar
                        IconDraw.render(this._iconRadar, f, f1, 0.0F);// actor.pos.getAbsOrient().kren() - orientAC.kren() - 360.0F);
                    }
                }
                i = this.radarOther.size();
                for (int j = 0; j < i; j++) {
                    double x = ((Tuple3d) (Point3d) this.radarOther.get(j)).x;
                    if (x > 5.0F) // Mayor que 5m, en frente del radar
                    {
                        double FOV = 1; // Math.abs(((Tuple3d)((Point3d)radarPlane.get(j))).x) / 5.0F;

                        double NewX = -((Tuple3d) (Point3d) this.radarOther.get(j)).y * FOV;
                        double NewY = ((Tuple3d) (Point3d) this.radarOther.get(j)).z * FOV;

                        float f = this.FrameOriginX + (float) (NewX * this.cameraMap2D.worldScale);
                        float f1 = this.FrameOriginY + (float) (NewY * this.cameraMap2D.worldScale);

                        int size = (int) Math.round((16 - this.curScale) * 3D * this.client.root.win.dx / 1024D / (1 + x / 800)); // By PAL 32D
                        IconDraw.setScrSize(size, size);

                        IconDraw.setColor(0xff00ffff); // Green radar
                        IconDraw.render(this._iconShipRadar, f, f1, 0.0F);// actor.pos.getAbsOrient().kren() - orientAC.kren() - 360.0F);
                    }
                }
            }
        }
        if (this.GUIPadMode == 6) { // By PAL, Write Briefing
            int enemyAirKill = 0;
            int enemyGroundKill = 0;
            int friendlyKill = 0;

            ScoreCounter scorecounter = World.cur().scoreCounter;
            ArrayList arraylist = scorecounter.enemyItems;
            for (int i = 0; i < arraylist.size(); i++) {
                ScoreItem scoreitem = (ScoreItem) arraylist.get(i);
                if (scoreitem.type == 0) enemyAirKill++;
                else enemyGroundKill++;
            }
            ArrayList arraylist1 = scorecounter.friendItems;
            for (int k = 0; k < arraylist1.size(); k++)
                // ScoreItem scoreitem1 = (ScoreItem)arraylist1.get(k);
                friendlyKill++;

            List targets = World.cur().targetsGuard.zutiGetTargets(); // By PAL from Zuti, GetTargets() implemented in TargetsGuard
            Target target = null;
            int TargetPrimary = 0;
            int TargetSecondary = 0;
            int TargetSecret = 0;
            for (int i = 0; i < targets.size(); i++) {
                target = (Target) targets.get(i);
                if (target.getDiedFlag() || target.isTaskComplete()) continue;

                switch (target.importance()) {
                    case 0: {
                        TargetPrimary++;
                        break;
                    }
                    case 1: {
                        TargetSecondary++;
                        break;
                    }
                    case 2: {
                        TargetSecret++;
                        break;
                    }
                }
            }

            /*
             * arraylist = scorecounter.targetOnItems; for(int l = 0; l < arraylist.size(); l++) { ScoreItem scoreitem2 = (ScoreItem)arraylist.get(l); //Target target1 = (Target)arraylist.get(l); //if (scoreitem3.TARGET_PRIMARY > 0) TargetPrimary++; //if
             * (scoreitem3.TARGET_SECONDARY > 0) TargetSecondary++; //if (scoreitem3.TARGET_SECRET > 0) TargetSecret++; //if (target1.importance() == 0) TargetPrimary++; //if (target1.importance() == 1) TargetSecondary++; //if (target1.importance() == 2)
             * TargetSecret++; //World.cur().targetsGuard. if (scoreitem2.score == 100) TargetPrimary++; if (scoreitem2.score == 101) TargetSecondary++; if (scoreitem2.score == 102) TargetSecret++; World.cur().targetsGuard.isTaskComplete();
             * //Target.PRIMARY } arraylist = scorecounter.targetOffItems; for(int i1 = 0; i1 < arraylist.size(); i1++) { ScoreItem scoreitem3 = (ScoreItem)arraylist.get(i1); //Target target1 = (Target)arraylist.get(i1); //if (target1.importance() == 0)
             * TargetPrimary = 1; //if (target1.importance() == 1) TargetSecondary = 1; //if (target1.importance() == 2) TargetSecret = 1; //World.cur().targetsGuard. } /* if(bShowTime || isDemoPlaying()) { int i = TextScr.This().getViewPortWidth(); long
             * l = Time.current(); if(NetMissionTrack.isPlaying()) l -= NetMissionTrack.playingStartTime; int j = (int)((l / 1000L) % 60L); int k = (int)(l / 1000L / 60L); if(j > 9) TextScr.output(i - TextScr.font().height() * 3, 5, "" + k + ":" + j);
             * else TextScr.output(i - TextScr.font().height() * 3, 5, "" + k + ":0" + j); } public static final int PRIMARY = 0; public static final int SECONDARY = 1; public static final int SECRET = 2; public static final int DESTROY = 0; public static
             * final int DESTROY_GROUND = 1; public static final int DESTROY_BRIDGE = 2; public static final int INSPECT = 3; public static final int ESCORT = 4; public static final int DEFENCE = 5; public static final int DEFENCE_GROUND = 6; public
             * static final int DEFENCE_BRIDGE = 7;/* //if (target.PRIMARY > 0) //World.cur().scoreCounter.t /* int i = targets.size(); for(int j = 0; j < i; j++) { Target target = (Target)targets.get(j); if target. switch(checkType) { default: break;
             * case 0: // '\0' if(target.importance() != 0 || target.isTaskComplete()) break; if(!target.isAlive()) flag1 = true; flag = false; break; case 1: // '\001' if(target.importance() != 1 || target.isTaskComplete()) break; if(!target.isAlive())
             * flag1 = true; flag = false; break; case 2: // '\002' if(target.importance() != 2) break; if(target.isAlive()) { flag2 = false; break; } if(target.isTaskComplete()) flag = true; else flag1 = true; break; }
             */

            String s = this.textDescription();
            if (this.textDescription == null) s = "\n           No brief was found for this mission";
            else s = "\n" + s;

            {
                // renders1.setCanvasFont(0);
                // renders1.setCanvasColor(0xc0dd1010);

                long l = Time.current();
                // if(NetMissionTrack.isPlaying())
                // l -= NetMissionTrack.playingStartTime;
                int se = (int) (l / 1000L % 60L);
                int mi = (int) (l / 1000L / 60L);
                // long timeStart = Time.currentReal();
                float todStart = scorecounter.todStart; // World.getTimeofDay();
                int h = (int) todStart;
                int m = (int) ((todStart - h) * 60);

                int sp = (int) (this.smallFont.height() * 1.5) + this.smallFont.descender();
                int li = 5;
                int x = this.renderMap2D.getViewPortWidth();

                int y = this.renderMap2D.getViewPortHeight();
                int Border = (int) (x * 0.08) + 1;
                int col = 0xff000000;
                
                for (int bgx = -1; bgx < 2; bgx++) {
                    for (int bgy = -1; bgy < 2; bgy++) {
                        li = 5;
                        this.smallFont.output(col, bgx + Border, bgy + y - Border - sp * li++, 0.0F, "Elapsed time: " + (mi <= 9 ? "0" + mi + "m " : mi + "m ") + (se <= 9 ? "0" + se + "s" : se + "s"));
                        this.smallFont.output(col, bgx + Border, bgy + y - Border - sp * li++, 0.0F, "Mission started at: " + (h <= 9 ? "0" + h + ":" : h + ":") + (m <= 9 ? "0" + m : "" + m) + " hours");
                        this.smallFont.output(col, bgx + Border, bgy + y - Border - sp * li++, 0.0F, "Enemy air kills: " + (enemyAirKill == 0 ? "none" : "" + enemyAirKill));
                        this.smallFont.output(col, bgx + Border, bgy + y - Border - sp * li++, 0.0F, "Enemy ground kills: " + (enemyGroundKill == 0 ? "none" : "" + enemyGroundKill));
                        this.smallFont.output(col, bgx + Border, bgy + y - Border - sp * li++, 0.0F, "Friendly kills: " + (friendlyKill == 0 ? "none" : "" + friendlyKill));
                        this.smallFont.output(col, bgx + Border, bgy + y - Border - sp * li++, 0.0F, "Primary Targets pending: " + (TargetPrimary == 0 ? "none" : "" + TargetPrimary));
                        this.smallFont.output(col, bgx + Border, bgy + y - Border - sp * li++, 0.0F, "Secondary Targets pending:  " + (TargetSecondary == 0 ? "none" : "" + TargetSecondary));
                        this.smallFont.output(col, bgx + Border, bgy + y - Border - sp * li++, 0.0F, "Secret Targets pending: " + (TargetSecret == 0 ? "none" : "" + TargetSecret));
                        this.smallFont.output(col, bgx + Border, bgy + y - Border - sp * li++, 0.0F, "Original mission brief:");
                        this.writeLines(col, bgx + Border, bgy + y - Border - sp * li++, s, 0, s.length(), x, y, Border);
                    }
                }

                col = 0xff00ffff;
                li = 5;
                this.smallFont.output(col, Border, y - Border - sp * li++, 0.0F, "Elapsed time: " + (mi <= 9 ? "0" + mi + "m " : mi + "m ") + (se <= 9 ? "0" + se + "s" : se + "s"));
                this.smallFont.output(col, Border, y - Border - sp * li++, 0.0F, "Mission started at: " + (h <= 9 ? "0" + h + ":" : h + ":") + (m <= 9 ? "0" + m : "" + m) + " hours");
                this.smallFont.output(col, Border, y - Border - sp * li++, 0.0F, "Enemy air kills: " + (enemyAirKill == 0 ? "none" : "" + enemyAirKill));
                this.smallFont.output(col, Border, y - Border - sp * li++, 0.0F, "Enemy ground kills: " + (enemyGroundKill == 0 ? "none" : "" + enemyGroundKill));
                this.smallFont.output(col, Border, y - Border - sp * li++, 0.0F, "Friendly kills: " + (friendlyKill == 0 ? "none" : "" + friendlyKill));
                this.smallFont.output(col, Border, y - Border - sp * li++, 0.0F, "Primary Targets pending: " + (TargetPrimary == 0 ? "none" : "" + TargetPrimary));
                this.smallFont.output(col, Border, y - Border - sp * li++, 0.0F, "Secondary Targets pending:  " + (TargetSecondary == 0 ? "none" : "" + TargetSecondary));
                this.smallFont.output(col, Border, y - Border - sp * li++, 0.0F, "Secret Targets pending: " + (TargetSecret == 0 ? "none" : "" + TargetSecret));
                this.smallFont.output(col, Border, y - Border - sp * li++, 0.0F, "Original mission brief:");
                this.writeLines(col, Border, y - Border - sp * li++, s, 0, s.length(), x, y, Border);
            }
        }
    }
    
    protected String textDescription() {
        return this.textDescription;
    }

    public int writeLines(int c, int x, int y, String s, int i, int j, float f1, float f2, float f3)// ;, float f3, int k)
    {
        int sp = (int) (this.smallFont.height() * 1.5) + this.smallFont.descender();
        int l = 0; // Number of lines used
        int k = 20; // Maximum of lines wanted gridFont.height();
        while (j > 0 && k != 0) {
            int i1 = j;
            int j1 = s.indexOf('\n', i);
            if (j1 >= 0) i1 = j1 - i;
            if (i1 > 0) {
                while (i1 > 0 && k != 0)

                {
                    int k1 = this.smallFont.len(s, i, i1, f1 - 2 * f3, true);
                    if (k1 == 0) k1 = this.smallFont.len(s, i, i1, f1 - 2 * f3, false);
                    if (k1 == 0) return l;
                    // gcanvas.draw(s, i, k1);
                    // if (k1 <= s.length() && k1 >= i)
                    // gridFont.output(c, x, y - sp * l, 0.0F, s.substring(i, k1));
                    this.smallFont.outputClip(c, x + 2, y - sp * l, 0.0F, s, i, k1, f3, f3, f1 - 2 * f3, f2 - f3);
                    // gcanvas.cur.y += f3;
                    l++;
                    j -= k1;
                    i += k1;
                    i1 -= k1;
                    // k--;
                    for (; i1 > 0; i1--)

                    {
                        if (s.charAt(i) != ' ') break;
                        i++;
                        j--;
                    }

                }
                if (j1 >= 0) {
                    i++;
                    j--;

                }
            } else

            {
                // gcanvas.cur.y += f3;
                l++;
                j--;
                i++;
                // k--;
            }

        }
        return l;
    }

    public String Target(int tNum) {
        String Descript = "";
        switch (tNum) {
            case 0:
                Descript = "Destroy";
                break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
            case 1:
                Descript = "Destroy Ground";
                break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
            case 2:
                Descript = "Destroy Bridge";
                break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
            case 3:
                Descript = "Inspect";
                break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
            case 4:
                Descript = "Escort"; // Originally None
                break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
            case 5:
                Descript = "Defence"; // Originally Escort
                break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
            case 6:
                Descript = "Defence Ground";
                break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
            case 7:
                Descript = "Defence Bridge";
                break; // TODO: Fixed by SAS~Storebror: Fall through between switch cases fixed.
        }
        return Descript;
    }
    // ---------------------------------------------------------------------
}