/*4.10.1 class*/
package com.maddox.il2.builder;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Map;

import com.maddox.JGP.Point2d;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3f;
import com.maddox.gwindow.GPoint;
import com.maddox.gwindow.GRegion;
import com.maddox.gwindow.GWindow;
import com.maddox.gwindow.GWindowDialogControl;
import com.maddox.gwindow.GWindowHScrollBar;
import com.maddox.gwindow.GWindowMenu;
import com.maddox.gwindow.GWindowMenuBarItem;
import com.maddox.gwindow.GWindowMenuItem;
import com.maddox.gwindow.GWindowMenuPopUp;
import com.maddox.gwindow.GWindowMessageBox;
import com.maddox.gwindow.GWindowRootMenu;
import com.maddox.gwindow.GWindowTabDialogClient.Tab;
import com.maddox.gwindow.GWindowVScrollBar;
import com.maddox.gwindow.GWindowVSliderInt;
import com.maddox.il2.ai.Army;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorFilter;
import com.maddox.il2.engine.ActorPosMove;
import com.maddox.il2.engine.ActorPosStatic;
import com.maddox.il2.engine.Camera3D;
import com.maddox.il2.engine.CameraOrtho2D;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.GUIWindowManager;
import com.maddox.il2.engine.IconDraw;
import com.maddox.il2.engine.Interpolate;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.engine.Render;
import com.maddox.il2.engine.TTFont;
import com.maddox.il2.engine.TextScr;
import com.maddox.il2.engine.hotkey.MouseXYZATK;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.gui.SquareLabels;
import com.maddox.il2.objects.ActorAlign;
import com.maddox.il2.objects.air.Runaway;
import com.maddox.il2.objects.bridges.Bridge;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.il2.objects.vehicles.planes.PlaneGeneric;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.HotKeyCmdEnv;
import com.maddox.rts.HotKeyEnv;
import com.maddox.rts.MsgTimerListener;
import com.maddox.rts.MsgTimerParam;
import com.maddox.rts.Property;
import com.maddox.rts.RTSConf;
import com.maddox.rts.SectFile;
import com.maddox.rts.SoftClass;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.CommonTools;
import com.maddox.util.HashMapExt;

public class Builder {
    private ArrayList          mis_cBoxes;
    private ArrayList          mis_properties;
    private ArrayList          mis_clipLoc;
    private Point3d            mis_clipP0;
    public static String       PLUGINS_SECTION             = "builder_plugins";
    public static String       envName;
    public static float        defaultAzimut               = 0.0F;
    public static final int    colorTargetPrimary          = -1;
    public static final int    colorTargetSecondary        = -16711936;
    public static final int    colorTargetSecret           = -8454144;
    public static float        MaxVisualDistance           = 16000.0F;
    public static float        saveMaxVisualDistance       = 5000.0F;
    public static float        saveMaxStaticVisualDistance = 3000.0F;
    public BldConfig           conf;
    public TTFont              smallFont;
    public static final double viewHLandMin                = 50.0;
    private Actor              selectedActor;
    private HashMapExt         selectedActors              = new HashMapExt();
    public Camera3D            camera;
    public CameraOrtho2D       camera2D;
    private MouseXYZATK        mouseXYZATK;
    private CursorMesh         cursor;
    public boolean             bMultiSelect                = false; // TODO: Fixed by SAS~Storebror: Unitinitalized instance variable fixed.
    public boolean             bSnap                       = false;
    public double              snapStep                    = 10.0;
    public Pathes              pathes;
    private boolean            bFreeView                   = false;
    private double             viewDistance                = 100000.0;
    private double             viewHMax                    = -1.0;
    private double             viewH                       = -1.0;
    private double             viewHLand                   = -1.0;
    private double             viewX                       = 1.0;
    private double             viewY                       = 1.0;
    private boolean            bView3D                     = false;
    private Point3d            _camPoint                   = new Point3d();
    private Orient             _camOrient                  = new Orient(-90.0F, -90.0F, 0.0F);
    private Point3d            __posScreenToLand           = new Point3d();
    private SelectFilter       _selectFilter               = new SelectFilter();
    private Point3d            __pi                        = new Point3d();
    private Orient             __oi                        = new Orient();
    ArrayList                  _deleted                    = new ArrayList();
    private Actor[]            _selectedActors;
    private boolean            _bSelect;
    private double             _selectX0;
    private double             _selectY0;
    private double             _selectX1;
    private double             _selectY1;
    private FilterSelect       filterSelect                = new FilterSelect();
    private int                saveMouseMode               = 2;
    private Loc                _savCameraNoFreeLoc         = new Loc();
    private Loc                _savCameraFreeLoc           = new Loc();
    public static final int    MOUSE_NONE                  = 0;
    public static final int    MOUSE_OBJECT_MOVE           = 1;
    public static final int    MOUSE_WORLD_ZOOM            = 2;
    public static final int    MOUSE_MULTISELECT           = 3;
    public static final int    MOUSE_SELECT_TARGET         = 4;
    public static final int    MOUSE_FILL                  = 5;
    public int                 mouseState                  = 0;
    int                        mouseFirstPosX              = 0;
    int                        mouseFirstPosY              = 0;
    int                        mousePosX                   = 0;
    int                        mousePosY                   = 0;
    boolean                    bMouseRenderRect            = false;
    Actor                      movedActor                  = null;
    Point3d                    movedActorPosSnap           = new Point3d();
    Point3d                    movedActorPosStepSave       = new Point3d();
    private Point3d            _objectMoveP                = new Point3d();
    protected Point3d          projectPos3d                = new Point3d();
    private float[]            line5XYZ                    = new float[15];
    private Mat                emptyMat;
    private float[]            line2XYZ                    = new float[6];
    protected TTFont           _gridFont;
    private int                _gridCount;
    private int[]              _gridX                      = new int[20];
    private int[]              _gridY                      = new int[20];
    private int[]              _gridVal                    = new int[20];
    protected Point2d          projectPos2d                = new Point2d();
    private float[]            lineNXYZ                    = new float[6];
    private int                lineNCounter;
    private Actor              overActor                   = null;
    private Mat                selectTargetMat;
    private GWindowMenuPopUp   popUpMenu;
    private GWindowMessageBox  deletingMessageBox;
    private boolean            bDeletingChangeSelection;
    private Actor              deletingActor;
    private boolean            bRotateObjects              = false;
    public ClientWindow        clientWindow;
    public ViewWindow          viewWindow;
    public XScrollBar          mapXscrollBar;
    public YScrollBar          mapYscrollBar;
    public ZSlider             mapZSlider;
    public WSelect             wSelect;
    public WViewLand           wViewLand;
    public WSnap               wSnap;
    public GWindowMenuBarItem  mFile;
    public GWindowMenuBarItem  mEdit;
    public GWindowMenuBarItem  mConfigure;
    public GWindowMenuBarItem  mView;
    public GWindowMenuItem     mSelectItem;
    public GWindowMenuItem     mViewLand;
    public GWindowMenuItem     mSnap;
    public GWindowMenuItem     mAlignItem;
    public GWindowMenuItem     mDisplayFilter;
    public GWindowMenuItem     mGrayScaleMap;
    public GWindowMenuItem     mIcon8;
    public GWindowMenuItem     mIcon16;
    public GWindowMenuItem     mIcon32;
    public GWindowMenuItem     mIcon64;
    private static int[]       _viewPort                   = new int[4];

    public class ClientWindow extends GWindow {
        public void resized() {
            GRegion gregion = this.parentWindow.getClientRegion();
            this.win.set(gregion);
            Builder.this.mapXscrollBar.setSize(this.win.dx, this.lookAndFeel().getHScrollBarH());
            Builder.this.mapXscrollBar.setPos(0.0F, this.win.dy - this.lookAndFeel().getHScrollBarH());
            Builder.this.mapYscrollBar.setSize(this.lookAndFeel().getVScrollBarW(), this.win.dy - this.lookAndFeel().getHScrollBarH());
            Builder.this.mapYscrollBar.setPos(this.win.dx - this.lookAndFeel().getVScrollBarW(), 0.0F);
            Builder.this.mapZSlider.setSize(this.lookAndFeel().getVSliderIntW(), this.win.dy - this.lookAndFeel().getHScrollBarH());
            Builder.this.mapZSlider.setPos(0.0F, 0.0F);
            Builder.this.viewWindow.setPos(this.lookAndFeel().getVSliderIntW(), 0.0F);
            Builder.this.viewWindow.setSize(this.win.dx - 2.0F * this.lookAndFeel().getVScrollBarW(), this.win.dy - this.lookAndFeel().getHScrollBarH());
            if (Builder.this.isLoadedLandscape()) Builder.this.computeViewMap2D(Builder.this.viewH, Builder.this.camera2D.worldXOffset + Builder.this.viewX / 2.0, Builder.this.camera2D.worldYOffset + Builder.this.viewY / 2.0);
        }

        public void resolutionChanged() {
            this.resized();
        }
    }

    public class ViewWindow extends GWindow {
        public void mouseMove(float f, float f_0_) {
            f_0_ = this.win.dy - f_0_ - 1.0F;
            Builder.this.doMouseAbsMove((int) f, (int) f_0_);
        }

        public void mouseRelMove(float f, float f1, float f2)
        {
            boolean flag = true;
            if((double)f2 < 0.001D && (double)f2 > -0.001D)
                return;
            if((double)f2 > 0.0D)
                flag = false;
            Point3d point3d = new Point3d();
            point3d.set(posScreenToLand(mousePosX, mousePosY, 0.0D, 0.1D));
            mapZSlider.setScrollPos(flag, true);
            Point3d point3d1 = new Point3d();
            point3d1.set(posScreenToLand(mousePosX, mousePosY, 0.0D, 0.1D));
            camera2D.worldXOffset += point3d.x - point3d1.x;
            camera2D.worldYOffset += point3d.y - point3d1.y;
            worldScrool(0.0D, 0.0D);
        }

        public void keyFocusEnter() {
            HotKeyCmdEnv.enable(Builder.envName, true);
        }

        public void keyFocusExit() {
            HotKeyCmdEnv.enable(Builder.envName, false);
        }
    }

    public class ZSlider extends GWindowVSliderInt {
        public boolean notify(int i, int i_1_) {
            if (i == 2) {
                if (Builder.this.isLoadedLandscape()) {
                    double d = this.pos() / 100.0;
                    double d_2_ = d * d;
                    Builder.this.computeViewMap2D(d_2_, Builder.this.camera2D.worldXOffset + Builder.this.viewX / 2.0, Builder.this.camera2D.worldYOffset + Builder.this.viewY / 2.0);
                }
                return true;
            }
            return false;
        }

        public void setScrollPos(boolean bool, boolean bool_3_) {
            int i = this.posCount / 64;
            if (i <= 0) i = 1;
            this.setPos(this.pos + (bool ? i : -i), bool_3_);
        }

        public ZSlider(GWindow gwindow) {
            super(gwindow);
        }
    }

    public class YScrollBar extends GWindowVScrollBar {
        public boolean notify(int i, int i_4_) {
            if (i == 2) {
                if (Builder.this.isLoadedLandscape()) Builder.this.worldScrool(0.0, Main3D.cur3D().land2D.mapSizeY() - Builder.this.viewY - this.pos() / 100.0F - Builder.this.camera2D.worldYOffset - Main3D.cur3D().land2D.worldOfsY());
                return true;
            }
            if (i == 17) return super.notify(i, i_4_);
            return false;
        }

        public YScrollBar(GWindow gwindow) {
            super(gwindow);
        }
    }

    public class XScrollBar extends GWindowHScrollBar {
        public boolean notify(int i, int i_5_) {
            if (i == 2) {
                if (Builder.this.isLoadedLandscape()) Builder.this.worldScrool(this.pos() / 100.0F - Builder.this.camera2D.worldXOffset - Main3D.cur3D().land2D.worldOfsX(), 0.0);
                return true;
            }
            return false;
        }

        public XScrollBar(GWindow gwindow) {
            super(gwindow);
        }
    }

    class AnimateView implements MsgTimerListener {
        Actor aView;
        Loc   from;
        Loc   to;
        Loc   cur = new Loc();

        public void msgTimer(MsgTimerParam msgtimerparam, int i, boolean bool, boolean bool_6_) {
            float f = (i + 1) / 100.0F;
            this.cur.interpolate(this.from, this.to, f);
            Builder.this.camera.pos.setAbs(this.cur);
            if (bool_6_) {
                if (this.aView != null) Builder.this.camera.pos.setBase(this.aView, Main3D.cur3D().hookView, false);
                else Builder.this.endClearActorView();
                HotKeyCmdEnv.enable(Builder.envName, true);
            }
        }

        public AnimateView(Actor actor, Loc loc, Loc loc_7_) {
            this.aView = actor;
            this.from = loc;
            this.to = loc_7_;
            if (actor != null) Builder.this.camera.pos.setBase(null, null, false);
            HotKeyCmdEnv.enable(Builder.envName, false);
            RTSConf.cur.realTimer.msgAddListener(this, new MsgTimerParam(0, 0, Time.currentReal(), 100, 10, true, true));
        }
    }

    class FilterSelect implements ActorFilter {
        public boolean isUse(Actor actor, double d) {
            if (!Builder.this.conf.bEnableSelect) return false;
            if (!Property.containsValue(actor, "builderSpawn")) return false;
            if (actor instanceof ActorLabel) return false;
            if (actor instanceof BridgeSegment) return false;
            if (actor instanceof Bridge) return false;
            Point3d point3d = actor.pos.getAbsPoint();
            if (point3d.x < Builder.this._selectX0 || point3d.x > Builder.this._selectX1) return false;
            if (point3d.y < Builder.this._selectY0 || point3d.y > Builder.this._selectY1) return false;
            if (Builder.this._bSelect) Builder.this.selectedActors.put(actor, null);
            else Builder.this.selectedActors.remove(actor);
            return true;
        }
    }

    class InterpolateOnLand extends Interpolate {
        public boolean tick() {
            Actor actor = Builder.this.selectedActor();
            if (!Actor.isValid(actor)) return true;
            if (Builder.this.isFreeView() && Builder.this.bMultiSelect) {
                actor.pos.getAbs(Builder.this.__pi, Builder.this.__oi);
                double d = Engine.land().HQ(Builder.this.__pi.x, Builder.this.__pi.y);
                double d_8_ = Builder.this.__pi.z - d;
                boolean bool = Engine.land().isWater(Builder.this.__pi.x, Builder.this.__pi.y);
                int i = (int) d_8_;
                if (d_8_ < 0.0) d_8_ = -d_8_;
                int i_9_ = (int) (d_8_ * 100.0) % 100;
                int i_10_ = TextScr.font().height() - TextScr.font().descender();
                int i_11_ = 5;
                int i_12_ = 5;
                StringBuffer stringbuffer = new StringBuffer().append("curPos: ").append((int) Builder.this.__pi.x).append(" ").append((int) Builder.this.__pi.y).append(" H= ").append(i).append(".").append(i_9_)
                        .append(bool ? " water HLand:" : " land HLand:").append((float) d).append(" type=");
                Engine.land();
                TextScr.output(i_11_, i_12_, stringbuffer.append(Landscape.getPixelMapT(Engine.land().WORLD2PIXX(Builder.this.__pi.x), Engine.land().WORLD2PIXY(Builder.this.__pi.y))).toString());
                TextScr.output(5, 5 + i_10_, "Orient: " + (int) Builder.this.__oi.azimut() + " " + (int) Builder.this.__oi.tangage() + " " + (int) Builder.this.__oi.kren());
                Point3d point3d = this.actor.pos.getAbsPoint();
                TextScr.output(5, 5 + 2 * i_10_, "Distance: " + (int) point3d.distance(Builder.this.__pi));
            }
            Builder.this.align(actor);
            return true;
        }
    }

    class SelectFilter implements ActorFilter {
        private Actor  _Actor = null;
        private double _Len2;
        private double _maxLen2;

        public void reset(double d) {
            this._Actor = null;
            this._maxLen2 = d;
        }

        public Actor get() {
            return this._Actor;
        }

        public boolean isUse(Actor actor, double d) {
            if (!Builder.this.conf.bEnableSelect) return true;
            if (d <= this._maxLen2) {
                if (actor instanceof BridgeSegment) if (Builder.this.conf.bViewBridge) actor = actor.getOwner();
                else return true;
                if (actor instanceof Bridge) {
                    if (!Builder.this.conf.bViewBridge) return true;
                } else if (actor instanceof PPoint) {
                    if (Builder.this.isFreeView()) return true;
                    Path path = (Path) actor.getOwner();
                    if (!path.isDrawing()) return true;
                } else if (!Property.containsValue(actor, "builderSpawn")) return true;
                if (this._Actor == null) {
                    this._Actor = actor;
                    this._Len2 = d;
                } else if (d < this._Len2) {
                    this._Actor = actor;
                    this._Len2 = d;
                }
            }
            return true;
        }
    }

    public static int armyAmount() {
        return Army.amountSingle();
    }

    public static int colorSelected() {
        long l = Time.currentReal();
        long l_13_ = 1000L;
        double d = 2.0 * (l % l_13_) / l_13_;
        if (d >= 1.0) d = 2.0 - d;
        int i = (int) (255.0 * d);
        return ~0xffffff | i << 16 | i << 8 | i;
    }

    public static int colorMultiSelected(int i) {
        if (i == -1) i = -16777216;
        long l = Time.currentReal();
        long l_14_ = 1000L;
        double d = 2.0 * (l % l_14_) / l_14_;
        if (d >= 1.0) d = 2.0 - d;
        int i_15_ = (int) (255.0 * d);
        return ~0xffffff | i_15_ << 16 | i_15_ << 8 | i_15_ | i;
    }

    public boolean isLoadedLandscape() {
        return PlMapLoad.getLandLoaded() != null;
    }

    public boolean isFreeView() {
        return this.bFreeView;
    }

    public boolean isView3D() {
        return this.bView3D;
    }

    public double viewDistance() {
        return this.viewDistance;
    }

    public void computeViewMap2D(double d, double d_16_, double d_17_) {
        if (this.isLoadedLandscape()) {
            int i = (int) this.viewWindow.win.dx;
            int i_18_ = (int) this.viewWindow.win.dy;
            double d_19_ = this.camera.FOV() * Math.PI / 360.0;
            double d_20_ = (double) i / (double) i_18_;
            if (d < 0.0) {
                this.viewHMax = Main3D.cur3D().land2D.mapSizeX() / 2.0 / Math.tan(d_19_);
                double d_21_ = Main3D.cur3D().land2D.mapSizeY() / 2.0 / Math.tan(d_19_) * d_20_;
                if (d_21_ < this.viewHMax) this.viewHMax = d_21_;
//                int i_22_ = (int) (Math.sqrt(1.0) * 100.0);
//                int i_23_ = (int) (Math.sqrt(this.viewHMax) * 100.0);
//                this.mapZSlider.setRange(i_22_, i_23_ - i_22_ + 1, i_22_);
                int k = (int)(Math.sqrt(viewHMax) * 100D);
                mapZSlider.setRange(100, (k - 100) + 1, 100);

                d = this.viewHMax;
                d_16_ = Main3D.cur3D().land2D.mapSizeX() / 2.0 - Main3D.cur3D().land2D.worldOfsX();
                d_17_ = Main3D.cur3D().land2D.mapSizeY() / 2.0 - Main3D.cur3D().land2D.worldOfsY();
            }
            double d_24_ = Engine.land().HQ(d_16_, d_17_);
            if (d < 50.0 + d_24_) d = 50.0 + d_24_;
            if (d > this.viewHMax) d = this.viewHMax;

            // TODO: Fixed by SAS~Storebror: Replace test for floating point equality by equality within a certain range, due to rounding errors in floating point calculations.
//            if (this.viewH != d) {
//            if (Math.abs(this.viewH - d) > .0000001D) {
            if (!CommonTools.equals(this.viewH, d)) {
                this.viewH = d;
                this.mapZSlider.setPos((int) (Math.sqrt(this.viewH) * 100.0), false);
            }
            this.viewHLand = this.viewH - d_24_;
            this.camera2D.worldScale = i / (2.0 * this.viewH * Math.tan(d_19_));
            boolean bool = (int) (Main3D.cur3D().land2D.mapSizeX() * this.camera2D.worldScale + 0.5) > i;
            boolean bool_25_ = (int) (Main3D.cur3D().land2D.mapSizeY() * this.camera2D.worldScale + 0.5) > i_18_;
            this.viewX = i / this.camera2D.worldScale;
            if (bool) {
                this.camera2D.worldXOffset = d_16_ - this.viewX / 2.0;
                if (this.camera2D.worldXOffset < -Main3D.cur3D().land2D.worldOfsX()) this.camera2D.worldXOffset = -Main3D.cur3D().land2D.worldOfsX();
                if (this.camera2D.worldXOffset > Main3D.cur3D().land2D.mapSizeX() - Main3D.cur3D().land2D.worldOfsX() - this.viewX) this.camera2D.worldXOffset = Main3D.cur3D().land2D.mapSizeX() - Main3D.cur3D().land2D.worldOfsX() - this.viewX;
                this.mapXscrollBar.setRange(0.0F, (int) (Main3D.cur3D().land2D.mapSizeX() * 100.0), (int) (this.viewX * 100.0), (int) (this.viewX * 100.0 / 64.0), (int) ((this.camera2D.worldXOffset + Main3D.cur3D().land2D.worldOfsX()) * 100.0));
            } else {
                this.camera2D.worldXOffset = -(this.viewX - Main3D.cur3D().land2D.mapSizeX()) / 2.0 - Main3D.cur3D().land2D.worldOfsX();
                this.mapXscrollBar.setRange(0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
            }
            this.viewY = i_18_ / this.camera2D.worldScale;
            if (bool_25_) {
                this.camera2D.worldYOffset = d_17_ - this.viewY / 2.0;
                if (this.camera2D.worldYOffset < -Main3D.cur3D().land2D.worldOfsY()) this.camera2D.worldYOffset = -Main3D.cur3D().land2D.worldOfsY();
                if (this.camera2D.worldYOffset > Main3D.cur3D().land2D.mapSizeY() - Main3D.cur3D().land2D.worldOfsY() - this.viewY) this.camera2D.worldYOffset = Main3D.cur3D().land2D.mapSizeY() - Main3D.cur3D().land2D.worldOfsY() - this.viewY;
                this.mapYscrollBar.setRange(0.0F, (int) (Main3D.cur3D().land2D.mapSizeY() * 100.0), (int) (this.viewY * 100.0), (int) (this.viewY * 100.0 / 64.0),
                        (int) ((Main3D.cur3D().land2D.mapSizeY() - this.viewY - this.camera2D.worldYOffset - Main3D.cur3D().land2D.worldOfsY()) * 100.0));
            } else {
                this.camera2D.worldYOffset = -(this.viewY - Main3D.cur3D().land2D.mapSizeY()) / 2.0 - Main3D.cur3D().land2D.worldOfsY();
                this.mapYscrollBar.setRange(0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
            }
            double d_26_ = Math.tan(d_19_) * this.viewH;
            double d_27_ = d_26_ / d_20_;
            double d_28_ = Math.sqrt(d_26_ * d_26_ + d_27_ * d_27_);
            this.viewDistance = Math.sqrt(this.viewH * this.viewH + d_28_ * d_28_);
            if (this.viewDistance > MaxVisualDistance) {
                this.bView3D = false;
                Main3D.cur3D().land2D.show(this.conf.bShowLandscape);
                Main3D.cur3D().renderMap2D.useClearColor(true);
                Main3D.cur3D().render3D0.setShow(false);
                Main3D.cur3D().render3D1.setShow(false);
                Main3D.cur3D().render2D.setShow(false);
            } else {
                this.bView3D = true;
                Main3D.cur3D().land2D.show(false);
                Main3D.cur3D().renderMap2D.useClearColor(false);
                Main3D.cur3D().render3D0.setShow(true);
                Main3D.cur3D().render3D1.setShow(true);
                Main3D.cur3D().render2D.setShow(true);
            }
            this.setPosCamera3D();
            this.repaint();
        }
    }

    private void setPosCamera3D() {
        this._camPoint.z = this.viewH;
        this._camPoint.x = this.camera2D.worldXOffset + (this.camera2D.right - this.camera2D.left) / this.camera2D.worldScale / 2.0;
        this._camPoint.y = this.camera2D.worldYOffset + (this.camera2D.top - this.camera2D.bottom) / this.camera2D.worldScale / 2.0;
        this.camera.pos.setAbs(this._camPoint, this._camOrient);
        this.camera.pos.reset();
    }

    public double posX2DtoWorld(int i) {
        return this.camera2D.worldXOffset + i / this.camera2D.worldScale;
    }

    public double posY2DtoWorld(int i) {
        return this.camera2D.worldYOffset + i / this.camera2D.worldScale;
    }

    public Point3d posScreenToLand(int i, int i_29_, double d, double d_30_) {
        Point3d point3d = this.__posScreenToLand;
        if (this.bView3D) {
            double d_31_ = this.camera2D.worldXOffset + (this.camera2D.right - this.camera2D.left) / this.camera2D.worldScale / 2.0;
            double d_32_ = this.camera2D.worldYOffset + (this.camera2D.top - this.camera2D.bottom) / this.camera2D.worldScale / 2.0;
            point3d.x = this.posX2DtoWorld(i);
            point3d.y = this.posY2DtoWorld(i_29_);
            point3d.z = Engine.land().HQ(point3d.x, point3d.y) + d;
            if (point3d.z > this.viewH) point3d.z = this.viewH;
            double d_33_ = (point3d.x - d_31_) / this.viewH;
            double d_34_ = (point3d.y - d_32_) / this.viewH;
            double d_35_ = 0.0;
            double d_36_ = (point3d.z - d_35_) * (point3d.z - d_35_);
            for (int i_37_ = 0; i_37_ < 8 && d_36_ > d_30_; i_37_++) {
                d_35_ = point3d.z;
                point3d.x = (this.viewH - d_35_) * d_33_ + d_31_;
                point3d.y = (this.viewH - d_35_) * d_34_ + d_32_;
                point3d.z = Engine.land().HQ(point3d.x, point3d.y) + d;
                if (point3d.z > this.viewH) point3d.z = this.viewH;
                d_36_ = (point3d.z - d_35_) * (point3d.z - d_35_);
            }
            for (int i_38_ = 0; i_38_ < 8 && d_36_ > d_30_; i_38_++) {
                d_35_ = point3d.z;
                point3d.x = ((this.viewH - d_35_) * d_33_ + d_31_ + point3d.x) / 2.0;
                point3d.y = ((this.viewH - d_35_) * d_34_ + d_32_ + point3d.y) / 2.0;
                point3d.z = Engine.land().HQ(point3d.x, point3d.y) + d;
                if (point3d.z > this.viewH) point3d.z = this.viewH;
                d_36_ = (point3d.z - d_35_) * (point3d.z - d_35_);
            }
        } else {
            point3d.x = this.posX2DtoWorld(i);
            point3d.y = this.posY2DtoWorld(i_29_);
            point3d.z = Engine.land().HQ(point3d.x, point3d.y);
        }
        return point3d;
    }

    public Point3d mouseWorldPos() {
        return this.posScreenToLand(this.mousePosX, this.mousePosY, 0.0, 0.1);
    }

    public Actor selectNear(Point3d point3d) {
        Actor actor = this.selectNear(point3d, 100.0);
        if (actor != null) return actor;
        return this.selectNear(point3d, 10000.0);
    }

    public Actor selectNearFull(int i, int i_39_) {
        if (i < 0 || i_39_ < 0) return null;
        Point3d point3d = this.posScreenToLand(i, i_39_, 0.0, 0.1);
        return this.selectNear(point3d);
    }

    public Actor selectNear(int i, int i_40_) {
        if (i < 0 || i_40_ < 0) return null;
        Point3d point3d = this.posScreenToLand(i, i_40_, 0.0, 0.1);
        double d = this.viewH - point3d.z;
        if (d < 0.0010) d = 0.0010;
        double d_41_ = this.conf.iconSize * d / this.viewH / this.camera2D.worldScale / 2.0;
        return this.selectNear(point3d, d_41_);
    }

    public Actor selectNear(Point3d point3d, double d) {
        this._selectFilter.reset(d * d);
        Engine.drawEnv().getFiltered((AbstractCollection) null, point3d.x - d, point3d.y - d, point3d.x + d, point3d.y + d, 15, this._selectFilter);
        return this._selectFilter.get();
    }

    private void worldScrool(double d, double d_42_) {
        double d_43_ = this.camera2D.worldXOffset + this.viewX / 2.0 + d;
        double d_44_ = this.camera2D.worldYOffset + this.viewY / 2.0 + d_42_;
        double d_45_ = Engine.land().HQ(d_43_, d_44_);
        double d_46_ = this.viewH;
        if (this.conf.bSaveViewHLand) d_46_ = d_45_ + this.viewHLand;
        this.computeViewMap2D(d_46_, d_43_, d_44_);
    }

    public void align(Actor actor) {
        if (actor instanceof ActorAlign) ((ActorAlign) actor).align();
        else {
            actor.pos.getAbs(this.__pi);
            double d = Engine.land().HQ(this.__pi.x, this.__pi.y) + 0.2;
            this.__pi.z = d;
            actor.pos.setAbs(this.__pi);
        }
    }

    public void deleteAll() {
        this.setSelected(null);
        Plugin.doDeleteAll();
        Plugin.doAfterDelete();
        this.selectedActorsValidate();
        this.pathes.clear();
    }

    public int countSelectedActors() {
        if (Actor.isValid(this.selectedActor)) {
            if (this.selectedActors.containsKey(this.selectedActor)) return this.selectedActors.size();
            return this.selectedActors.size() + 1;
        }
        return this.selectedActors.size();
    }

    public void selectActorsClear() {
        this.selectedActors.clear();
    }

    public void selectActorsAdd(Actor actor) {
        this.selectedActors.put(actor, null);
    }

    public Actor[] selectedActors() {
        int i = this.countSelectedActors();
        Actor[] actors = this._selectedActors(i > 0 ? i : 1);
        int i_47_ = 0;
        if (Actor.isValid(this.selectedActor)) actors[i_47_++] = this.selectedActor;
        if (this.selectedActors.size() > 0) for (Map.Entry entry = this.selectedActors.nextEntry(null); entry != null; entry = this.selectedActors.nextEntry(entry)) {
            Actor actor = (Actor) entry.getKey();
            if (Actor.isValid(actor) && actor != this.selectedActor) actors[i_47_++] = actor;
        }
        if (i_47_ == 0) actors[0] = null;
        else if (actors.length > i_47_) actors[i_47_] = null;
        return actors;
    }

    private Actor[] _selectedActors(int i) {
        if (this._selectedActors == null || this._selectedActors.length < i) this._selectedActors = new Actor[i];
        return this._selectedActors;
    }

    public void selectedActorsValidate() {
        Actor[] actors = this.selectedActors();
        for (int i = 0; i < actors.length; i++) {
            Actor actor = actors[i];
            if (actor == null) break;
            if (!Actor.isValid(actor) || !actor.isDrawing()) this.selectedActors.remove(actor);
        }
    }

    public void select(double d, double d_48_, double d_49_, double d_50_, boolean bool) {
        if (d_49_ > d) {
            this._selectX0 = d;
            this._selectX1 = d_49_;
        } else {
            this._selectX0 = d_49_;
            this._selectX1 = d;
        }
        if (d_50_ > d_48_) {
            this._selectY0 = d_48_;
            this._selectY1 = d_50_;
        } else {
            this._selectY0 = d_50_;
            this._selectY1 = d_48_;
        }
        this._bSelect = bool;
        Engine.drawEnv().getFiltered((AbstractCollection) null, this._selectX0, this._selectY0, this._selectX1, this._selectY1, 15, this.filterSelect);
    }

    public boolean isMiltiSelected(Actor actor) {
        return this.selectedActors.containsKey(actor);
    }

    public boolean isSelected(Actor actor) {
        if (actor == this.selectedActor) return true;
        return this.selectedActors.containsKey(actor);
    }

    public Actor selectedActor() {
        return this.selectedActor;
    }

    public PPoint selectedPoint() {
        if (this.pathes == null) return null;
        return this.pathes.currentPPoint;
    }

    // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
    public PPoint selectedFirstPoint()
    {
        Path path = selectedPath();
        if(path != null)
            return path.point(0);
        else
            return null;
    }
    // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---

    public Path selectedPath() {
        if (this.pathes == null) return null;
        if (!Actor.isValid(this.pathes.currentPPoint)) return null;
        return (Path) this.selectedPoint().getOwner();
    }

    public void setSelected(Actor actor) {
        // TODO: Added by |ZUTI|: do not select spawn place indicators
        // -----------------------------------------------------------
        if (!ZutiSupportMethods_Builder.canSelectActor(actor)) return;
        // -----------------------------------------------------------
        if (this.conf.bEnableSelect) {
            if (Actor.isValid(this.selectedActor)) {
                Plugin plugin = (Plugin) Property.value(this.selectedActor, "builderPlugin");
                if (plugin instanceof PlMisStatic) defaultAzimut = this.selectedActor.pos.getAbsOrient().azimut();
            }
            int i = this.wSelect.tabsClient.getCurrent();
            this.wSelect.clearExtendTabs();
            if (actor != null && actor instanceof PPoint) {
                this.pathes.currentPPoint = (PPoint) actor;
                this.selectedActor = null;
                Plugin plugin = (Plugin) Property.value(actor.getOwner(), "builderPlugin");
                plugin.syncSelector();
                if (actor instanceof PAir) this.tip(Plugin.i18n("Selected") + " " + ((PathAir) actor.getOwner()).typedName);
                else if (actor instanceof PNodes) this.tip(Plugin.i18n("Selected") + " " + Property.stringValue(actor.getOwner(), "i18nName", ""));
            } else {
                if (this.pathes != null) this.pathes.currentPPoint = null;
                this.selectedActor = actor;
                if (this.isFreeView()) this.setActorView(actor);
                if (actor != null) {
                    Plugin plugin = (Plugin) Property.value(actor, "builderPlugin");
                    if (plugin != null) {
                        plugin.syncSelector();
                        if (plugin instanceof PlMisStatic) defaultAzimut = actor.pos.getAbsOrient().azimut();
                    } else if (this.bMultiSelect) {
                        plugin = Plugin.getPlugin("MapActors");
                        plugin.syncSelector();
                    }
                    String string;
                    if (this.bMultiSelect) {
                        string = actor instanceof SoftClass ? ((SoftClass) actor).fullClassName() : actor.getClass().getName();
                        int i_51_ = string.lastIndexOf('.');
                        string = string.substring(i_51_ + 1);
                    } else string = Property.stringValue(actor.getClass(), "i18nName", "");
                    this.tip(Plugin.i18n("Selected") + " " + string);
                } else this.tip("");
            }
            if (i > 0 && i < this.wSelect.tabsClient.sizeTabs()) this.wSelect.tabsClient.setCurrent(i);
        }
    }

    public void doUpdateSelector() {
        if (Actor.isValid(this.pathes.currentPPoint)) {
            Plugin plugin = (Plugin) Property.value(this.pathes.currentPPoint.getOwner(), "builderPlugin");
            plugin.updateSelector();
        }
    }

    private void setActorView(Actor actor) {
        if (Actor.isValid(actor)) if (this.actorView() != actor) {
            if (actor.pos instanceof ActorPosStatic) {
                boolean bool = actor.isCollide();
                actor.collide(false);
                actor.drawing(false);
                actor.pos = new ActorPosMove(actor.pos);
                actor.drawing(true);
                if (bool) actor.collide(true);
            }
            this.mouseXYZATK.setTarget(actor);
            this.camera.pos.setBase(actor, Main3D.cur3D().hookView, false);
            this.camera.pos.reset();
            this.cursor.drawing(actor == this.cursor);
        }
    }

    private void setActorView() {
        this.pathes.currentPPoint = null;
        this.bFreeView = true;
        this.saveMouseMode = RTSConf.cur.getUseMouse();
        if (this.saveMouseMode != 2) RTSConf.cur.setUseMouse(2);
        this.camera.pos.getAbs(this._savCameraNoFreeLoc);
        Actor actor;
        if (Actor.isValid(this.selectedActor())) actor = this.selectedActor();
        else {
            actor = this.cursor;
            this.selectedActor = actor;
            Point3d point3d = new Point3d();
            this.camera.pos.getAbs(point3d);
            point3d.z = Engine.land().HQ(point3d.x, point3d.y) + 0.2;
            actor.pos.setAbs(point3d);
        }
        this.setActorView(actor);
        this.clientWindow.root.manager.activateMouse(false);
        this.clientWindow.root.manager.activateKeyboard(false);
        HotKeyCmdEnv.enable("HookView", true);
        HotKeyCmdEnv.enable("MouseXYZ", true);
        this.viewWindow.mouseCursor = 0;
        if (!this.bMultiSelect) {
            Main3D.cur3D().spritesFog.setShow(true);
            if (Main3D.cur3D().clouds != null) {
                Main3D.cur3D().bDrawClouds = true;
                Main3D.cur3D().clouds.setShow(true);
            }
            Main3D.cur3D().bEnableFog = true;
        }
        if (this.conf.bAnimateCamera) {
            this.camera.pos.getAbs(this._savCameraFreeLoc);
            new AnimateView(actor, this._savCameraNoFreeLoc, this._savCameraFreeLoc);
        }
    }

    private void clearActorView() {
        this.camera.pos.getAbs(this._savCameraFreeLoc);
        this.mouseXYZATK.setTarget(null);
        this.camera.pos.setBase(null, null, false);
        this.computeViewMap2D(this._savCameraNoFreeLoc.getZ(), this._savCameraFreeLoc.getX(), this._savCameraFreeLoc.getY());
        this.camera.pos.getAbs(this._savCameraNoFreeLoc);
        if (this.conf.bAnimateCamera) new AnimateView(null, this._savCameraFreeLoc, this._savCameraNoFreeLoc);
        else this.endClearActorView();
    }

    private void endClearActorView() {
        this.bFreeView = false;
        if (this.saveMouseMode != 2) RTSConf.cur.setUseMouse(this.saveMouseMode);
        this.cursor.drawing(false);
        if (this.selectedActor() == this.cursor) this.setSelected(null);
        this.selectedActorsValidate();
        this.clientWindow.root.manager.activateMouse(true);
        this.clientWindow.root.manager.activateKeyboard(true);
        HotKeyCmdEnv.enable("HookView", false);
        HotKeyCmdEnv.enable("MouseXYZ", false);
        this.viewWindow.mouseCursor = 1;
        PlMission.setChanged();
        if (!this.bMultiSelect) {
            Main3D.cur3D().spritesFog.setShow(false);
            if (Main3D.cur3D().clouds != null) Main3D.cur3D().clouds.setShow(false);
            Main3D.cur3D().bEnableFog = false;
        }
        this.repaint();
    }

    private Actor actorView() {
        return this.camera.pos.base();
    }

    public void doMouseAbsMove(int i, int i_52_) {
        if (this.isLoadedLandscape() && !this.isFreeView()) if (this.mousePosX == -1) {
            this.mousePosX = i;
            this.mousePosY = i_52_;
        } else {
            // TODO: Added by |ZUTI|: do not select spawn place indicators
            // -----------------------------------------------------------
            if (!ZutiSupportMethods_Builder.canSelectActor(this.selectNear(i, i_52_))) return;
            // -----------------------------------------------------------

            Point3d point3d = this.posScreenToLand(this.mousePosX, this.mousePosY, 0.0, 0.1);
            switch (this.mouseState) {
                case 1: {
                    double d = this.camera2D.worldScale;
                    if (this.bView3D) {
                        double d_53_ = this.viewH - point3d.z;
                        if (d_53_ < 0.0010) d_53_ = 0.0010;
                        d *= this.viewH / d_53_;
                    }
                    if (this.movedActor == null) this.worldScrool((this.mousePosX - i) / d, (this.mousePosY - i_52_) / d);
                    else {
                        double d_54_ = (i - this.mousePosX) / d;
                        double d_55_ = (i_52_ - this.mousePosY) / d;
                        if (this.bSnap) {
                            this.movedActorPosSnap.x += d_54_;
                            this.movedActorPosSnap.y += d_55_;
                            this.movedActor.pos.getAbs(this.movedActorPosStepSave);
                            this._objectMoveP.set(this.movedActorPosSnap);
                            this._objectMoveP.x = Math.round(this.movedActorPosSnap.x / this.snapStep) * this.snapStep;
                            this._objectMoveP.y = Math.round(this.movedActorPosSnap.y / this.snapStep) * this.snapStep;
                            this._objectMoveP.z = this.movedActorPosSnap.z;
                        } else {
                            this.movedActor.pos.getAbs(this._objectMoveP);
                            this._objectMoveP.x += d_54_;
                            this._objectMoveP.y += d_55_;
                        }
                        try {
                            if (this.movedActor instanceof PPoint) {
                                PPoint ppoint = (PPoint) this.movedActor;
                                ppoint.moveTo(this._objectMoveP);
                                ((Path) ppoint.getOwner()).pointMoved(ppoint);
                                PlMission.setChanged();
                            } else if (!(this.movedActor instanceof Bridge) && !(this.movedActor instanceof ActorLabel) && !(this.movedActor instanceof ActorBorn)) {
                                this.movedActor.pos.setAbs(this._objectMoveP);
                                this.movedActor.pos.reset();
                                this.align(this.movedActor);
                                PlMission.setChanged();
                            }
                            if (this.selectedActors.containsKey(this.movedActor)) {
                                Actor[] actors = this.selectedActors();
                                for (int i_56_ = 0; i_56_ < actors.length; i_56_++) {
                                    Actor actor = actors[i_56_];
                                    if (actor == null) break;
                                    if (Actor.isValid(actor) && actor != this.movedActor) {
                                        if (actor instanceof ActorAlign) {
                                            actor.pos.getAbs(this.__pi);
                                            if (this.bSnap) {
                                                this.__pi.x += this._objectMoveP.x - this.movedActorPosStepSave.x;
                                                this.__pi.y += this._objectMoveP.y - this.movedActorPosStepSave.y;
                                            } else {
                                                this.__pi.x += d_54_;
                                                this.__pi.y += d_55_;
                                            }
                                            actor.pos.setAbs(this.__pi);
                                            ((ActorAlign) actor).align();
                                        } else {
                                            actor.pos.getAbs(this.__pi);
                                            this.__pi.x += d_54_;
                                            this.__pi.y += d_55_;
                                            double d_57_ = Engine.land().HQ(this.__pi.x, this.__pi.y) + 0.2;
                                            this.__pi.z = d_57_;
                                            actor.pos.setAbs(this.__pi);
                                        }
                                        actor.pos.reset();
                                    }
                                }
                            }
                        } catch (Exception exception) {
                            this.mouseState = 0;
                            this.viewWindow.mouseCursor = 1;
                        }
                        this.repaint();
                    }
                    break;
                }
                case 0: {
                    Actor actor = this.selectNear(i, i_52_);
                    if (!this.bMultiSelect && actor != null && actor instanceof Bridge) {
                        if (this.movedActor != null) this.viewWindow.mouseCursor = 1;
                        this.movedActor = null;
                        this.setOverActor(actor);
                    } else {
                        if (actor != null) {
                            if (this.movedActor == null) this.viewWindow.mouseCursor = 7;
                            this.movedActor = actor;
                            this.movedActor.pos.getAbs(this.movedActorPosSnap);
                        } else {
                            if (this.movedActor != null) this.viewWindow.mouseCursor = 1;
                            this.movedActor = null;
                        }
                        this.setOverActor(this.movedActor);
                    }
                    break;
                }
                case 4:
                    this.movedActor = null;
                    this.setOverActor(this.selectNear(i, i_52_));
                    if (!Actor.isValid(this.selectedPoint()) && !(this.selectedActor() instanceof PlMisRocket.Rocket)) this.breakSelectTarget();
                    break;
                    
                    // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
                case 6:
                    movedActor = null;
                    setOverActor(selectNear(i, i_52_));
                    if(!Actor.isValid(selectedPoint()) && !(selectedActor() instanceof PlMisRocket.Rocket))
                        breakSelectSpawnPoint();
                    break;

                case 8:
                    movedActor = null;
                    setOverActor(selectNear(i, i_52_));
                    if(!(selectedActor() instanceof ActorTrigger))
                        breakSelectTrigger();
                    break;

                case 9:
                    movedActor = null;
                    setOverActor(selectNear(i, i_52_));
                    if(!(selectedActor() instanceof ActorTrigger))
                        breakSelectTriggerLink();
                    break;
                    // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
            }
            this.mousePosX = i;
            this.mousePosY = i_52_;
            if (this.mouseState == 5) Plugin.doFill(point3d);
            if (this.bMouseRenderRect) this.repaint();
        }
    }

    public void render3D() {
        if (this.isLoadedLandscape()) {
            Plugin.doRender3D();
            if (!this.isFreeView()) if (this.conf.bShowGrid && this.bView3D) this.drawGrid3D();
        }
    }

    public boolean project2d(Point3d point3d, Point2d point2d) {
        return this.project2d(point3d.x, point3d.y, point3d.z, point2d);
    }

    public boolean project2d(double d, double d_58_, double d_59_, Point2d point2d) {
        if (this.bView3D) {
            Main3D.cur3D().project2d(d, d_58_, d_59_, this.projectPos3d);
            point2d.x = this.projectPos3d.x - _viewPort[0];
            point2d.y = this.projectPos3d.y - _viewPort[1];
        } else {
            point2d.x = (d - this.camera2D.worldXOffset) * this.camera2D.worldScale;
            point2d.y = (d_58_ - this.camera2D.worldYOffset) * this.camera2D.worldScale;
        }
        if (point2d.x + this.conf.iconSize < 0.0) return false;
        if (point2d.y + this.conf.iconSize < 0.0) return false;
        if (point2d.x - this.conf.iconSize > this.camera2D.right - this.camera2D.left) return false;
        if (point2d.y - this.conf.iconSize > this.camera2D.top - this.camera2D.bottom) return false;
        return true;
    }

    public void preRenderMap2D() {
        if (this.isLoadedLandscape()) Plugin.doPreRenderMap2D();
    }

    public void renderMap2D() {
        if (this.isLoadedLandscape()) {
            if (!this.isFreeView() && this.conf.iLightLand != 255) {
                int i = 255 - this.conf.iLightLand << 24 | 0x3f3f3f;
                Render.drawTile(0.0F, 0.0F, this.viewWindow.win.dx, this.viewWindow.win.dy, 0.0F, this.emptyMat, i, 0.0F, 0.0F, 1.0F, 1.0F);
                Render.drawEnd();
            }
            Plugin.doRenderMap2DBefore();
            if (!this.isFreeView()) {
                this.pathes.renderMap2DTargetLines();
                // TODO: +++ Backport from 4.13
                pathes.renderMap2DSpawnLines();
                // ---
            }
            Plugin.doRenderMap2D();
            if (!this.isFreeView()) {
                if (this.conf.bShowGrid) {
                    if (!this.bView3D) this.drawGrid2D();
                    this.drawGridText();
                }
                this.pathes.renderMap2D(this.bView3D, this.conf.iconSize);
                Plugin.doRenderMap2DAfter();
                if (this.bMouseRenderRect && (this.mouseFirstPosX != this.mousePosX || this.mouseFirstPosY != this.mousePosY)) {
                    this.line5XYZ[0] = this.mouseFirstPosX;
                    this.line5XYZ[1] = this.mouseFirstPosY;
                    this.line5XYZ[2] = 0.0F;
                    this.line5XYZ[3] = this.mousePosX;
                    this.line5XYZ[4] = this.mouseFirstPosY;
                    this.line5XYZ[5] = 0.0F;
                    this.line5XYZ[6] = this.mousePosX;
                    this.line5XYZ[7] = this.mousePosY;
                    this.line5XYZ[8] = 0.0F;
                    this.line5XYZ[9] = this.mouseFirstPosX;
                    this.line5XYZ[10] = this.mousePosY;
                    this.line5XYZ[11] = 0.0F;
                    Render.drawBeginLines(-1);
                    Render.drawLines(this.line5XYZ, 4, 1.0F, colorSelected(), Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 4);
                    Render.drawEnd();
                }
                this.drawInfoOverActor();
                // TODO: +++ Backport from 4.13
//                this.drawSelectTarget();
                drawSelectTargetAndSpawnPoint();
                drawSelectedSpawnPoint();
                // ---
                if (Main3D.cur3D().land2DText != null) Main3D.cur3D().land2DText.render();
                if (this.conf.bShowGrid && !this.bView3D) SquareLabels.draw(this.camera2D, Main3D.cur3D().land2D.worldOfsX(), Main3D.cur3D().land2D.worldOfsX(), Main3D.cur3D().land2D.mapSizeX());
            }
        }
    }

    private int gridStep() {
        double d = this.viewX;
        if (this.viewY < this.viewX) d = this.viewY;
        d *= this.viewHLand / this.viewH;
        int i = 100000;
        for (int i_60_ = 0; i_60_ < 5 && !(i * 3 <= d); i_60_++)
            i /= 10;
        return i;
    }

    private void drawGrid2D() {
        int i = this.gridStep();
        int i_61_ = (int) ((this.camera2D.worldXOffset + Main3D.cur3D().land2D.worldOfsX()) / i);
        int i_62_ = (int) ((this.camera2D.worldYOffset + Main3D.cur3D().land2D.worldOfsY()) / i);
        int i_63_ = (int) (this.viewX / i) + 2;
        int i_64_ = (int) (this.viewY / i) + 2;
        float f = (float) ((i_61_ * i - this.camera2D.worldXOffset - Main3D.cur3D().land2D.worldOfsX()) * this.camera2D.worldScale + 0.5);
        float f_65_ = (float) ((i_62_ * i - this.camera2D.worldYOffset - Main3D.cur3D().land2D.worldOfsY()) * this.camera2D.worldScale + 0.5);
        float f_66_ = (float) (i_63_ * i * this.camera2D.worldScale);
        float f_67_ = (float) (i_64_ * i * this.camera2D.worldScale);
        float f_68_ = (float) (i * this.camera2D.worldScale);
        this._gridCount = 0;
        Render.drawBeginLines(-1);
        for (int i_69_ = 0; i_69_ <= i_64_; i_69_++) {
            float f_70_ = f_65_ + i_69_ * f_68_;
            int i_71_ = (i_69_ + i_62_) % 10 == 0 ? 192 : 127;
            this.line2XYZ[0] = f;
            this.line2XYZ[1] = f_70_;
            this.line2XYZ[2] = 0.0F;
            this.line2XYZ[3] = f + f_66_;
            this.line2XYZ[4] = f_70_;
            this.line2XYZ[5] = 0.0F;
            Render.drawLines(this.line2XYZ, 2, 1.0F, ~0xffffff | i_71_ << 16 | i_71_ << 8 | i_71_, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 0);
            if (i_71_ == 192) this.drawGridText(0, (int) f_70_, (i_62_ + i_69_) * i);
        }
        for (int i_72_ = 0; i_72_ <= i_63_; i_72_++) {
            float f_73_ = f + i_72_ * f_68_;
            int i_74_ = (i_72_ + i_61_) % 10 == 0 ? 192 : 127;
            this.line2XYZ[0] = f_73_;
            this.line2XYZ[1] = f_65_;
            this.line2XYZ[2] = 0.0F;
            this.line2XYZ[3] = f_73_;
            this.line2XYZ[4] = f_65_ + f_67_;
            this.line2XYZ[5] = 0.0F;
            Render.drawLines(this.line2XYZ, 2, 1.0F, ~0xffffff | i_74_ << 16 | i_74_ << 8 | i_74_, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 0);
            if (i_74_ == 192) this.drawGridText((int) f_73_, 0, (i_61_ + i_72_) * i);
        }
        Render.drawEnd();
    }

    private void drawGridText(int i, int i_75_, int i_76_) {
        if (i >= 0 && i_75_ >= 0 && i_76_ > 0 && this._gridCount != 20) {
            this._gridX[this._gridCount] = i;
            this._gridY[this._gridCount] = i_75_;
            this._gridVal[this._gridCount] = i_76_;
            this._gridCount++;
        }
    }

    private void drawGridText() {
        for (int i = 0; i < this._gridCount; i++)
            this._gridFont.output(-4144960, this._gridX[i] + 2, this._gridY[i] + 2, 0.0F, this._gridVal[i] / 1000 + "." + this._gridVal[i] % 1000 / 100);
        this._gridCount = 0;
    }

    private void drawGrid3D() {
        int i = this.gridStep();
        int i_77_ = (int) ((this.camera2D.worldXOffset + Main3D.cur3D().land2D.worldOfsX()) / i);
        int i_78_ = (int) ((this.camera2D.worldYOffset + Main3D.cur3D().land2D.worldOfsY()) / i);
        int i_79_ = (int) (this.viewX / i) + 2;
        int i_80_ = (int) (this.viewY / i) + 2;
        Render.drawBeginLines(0);
        for (int i_81_ = 0; i_81_ <= i_80_; i_81_++) {
            float f = (i_81_ + i_78_) * i;
            int i_82_ = 64;
            boolean bool = true;
            if ((i_81_ + i_78_) % 2 == 0) {
                i_82_ = 150;
                if ((i_81_ + i_78_) % 10 == 0) {
                    bool = false;
                    i_82_ = 240;
                }
            }
            double d = -1.0;
            double d_83_ = -1.0;
            if (this.lineNXYZ.length / 3 <= i_79_) this.lineNXYZ = new float[(i_79_ + 1) * 3];
            this.lineNCounter = 0;
            for (int i_84_ = 0; i_84_ <= i_79_; i_84_++) {
                float f_85_ = (i_84_ + i_77_) * i;
                Engine.land();
                float f_86_ = Landscape.HQ(f_85_ - (float) Main3D.cur3D().land2D.worldOfsX(), f - (float) Main3D.cur3D().land2D.worldOfsY());
                this.lineNXYZ[this.lineNCounter * 3 + 0] = f_85_ - (float) Main3D.cur3D().land2D.worldOfsX();
                this.lineNXYZ[this.lineNCounter * 3 + 1] = f - (float) Main3D.cur3D().land2D.worldOfsY();
                this.lineNXYZ[this.lineNCounter * 3 + 2] = f_86_;
                this.lineNCounter++;
                if (!bool) {
                    this.project2d(f_85_ - (float) Main3D.cur3D().land2D.worldOfsX(), f - (float) Main3D.cur3D().land2D.worldOfsY(), f_86_, this.projectPos2d);
                    if (this.projectPos2d.x > 0.0) {
                        if (i_84_ > 0) this.drawGridText(0, (int) (d_83_ - d / (this.projectPos2d.x - d) * (this.projectPos2d.y - d_83_)), (int) f);
                        else this.drawGridText(0, (int) this.projectPos2d.y, (int) f);
                        bool = true;
                    }
                    d = this.projectPos2d.x;
                    d_83_ = this.projectPos2d.y;
                }
            }
            Render.drawLines(this.lineNXYZ, this.lineNCounter, 1.0F, ~0xffffff | i_82_ << 16 | i_82_ << 8 | i_82_, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 2);
        }
        for (int i_87_ = 0; i_87_ <= i_79_; i_87_++) {
            float f = (i_87_ + i_77_) * i;
            int i_88_ = 64;
            boolean bool = true;
            if ((i_87_ + i_77_) % 2 == 0) {
                i_88_ = 150;
                if ((i_87_ + i_77_) % 10 == 0) {
                    bool = false;
                    i_88_ = 240;
                }
            }
            double d = -1.0;
            double d_89_ = -1.0;
            if (this.lineNXYZ.length / 3 <= i_80_) this.lineNXYZ = new float[(i_80_ + 1) * 3];
            this.lineNCounter = 0;
            for (int i_90_ = 0; i_90_ <= i_80_; i_90_++) {
                float f_91_ = (i_90_ + i_78_) * i;
                Engine.land();
                float f_92_ = Landscape.HQ(f - (float) Main3D.cur3D().land2D.worldOfsX(), f_91_ - (float) Main3D.cur3D().land2D.worldOfsY());
                this.lineNXYZ[this.lineNCounter * 3 + 0] = f - (float) Main3D.cur3D().land2D.worldOfsX();
                this.lineNXYZ[this.lineNCounter * 3 + 1] = f_91_ - (float) Main3D.cur3D().land2D.worldOfsY();
                this.lineNXYZ[this.lineNCounter * 3 + 2] = f_92_;
                this.lineNCounter++;
                if (!bool) {
                    this.project2d(f - (float) Main3D.cur3D().land2D.worldOfsX(), f_91_ - (float) Main3D.cur3D().land2D.worldOfsY(), f_92_, this.projectPos2d);
                    if (this.projectPos2d.y > 0.0) {
                        if (i_90_ > 0) this.drawGridText((int) (d - d_89_ / (this.projectPos2d.y - d_89_) * (this.projectPos2d.x - d)), 0, (int) f);
                        else this.drawGridText((int) this.projectPos2d.x, 0, (int) f);
                        bool = true;
                    }
                    d = this.projectPos2d.x;
                    d_89_ = this.projectPos2d.y;
                }
            }
            Render.drawLines(this.lineNXYZ, this.lineNCounter, 1.0F, ~0xffffff | i_88_ << 16 | i_88_ << 8 | i_88_, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 2);
        }
        Render.drawEnd();
    }

    public Actor getOverActor() {
        return this.overActor;
    }

    public void setOverActor(Actor actor) {
        this.overActor = actor;
    }

    private void drawInfoOverActor() {
        if (Actor.isValid(this.overActor)) {
            Plugin plugin = (Plugin) Property.value(this.overActor instanceof PPoint ? this.overActor.getOwner() : this.overActor, "builderPlugin");
            if (plugin != null) {
                String[] strings = plugin.actorInfo(this.overActor);
                if (strings != null) {
                    Point3d point3d = this.overActor.pos.getAbsPoint();
                    if (this.project2d(point3d, this.projectPos2d)) {
                        float f = 0.0F;
                        int i = 0;
                        for (int i_93_ = 0; i_93_ < strings.length; i_93_++) {
                            String string = strings[i_93_];
                            if (string == null) break;
                            float f_94_ = this.smallFont.width(string);
                            if (f_94_ > f) f = f_94_;
                            i++;
                        }
                        if (f != 0.0F) {
                            float f_95_ = -this.smallFont.descender();
                            float f_96_ = this.smallFont.height();
                            float f_97_ = i * (f_96_ + f_95_) + f_95_;
                            int i_98_ = Army.color(this.overActor instanceof PPoint ? this.overActor.getOwner().getArmy() : this.overActor.getArmy());
                            Render.drawTile((float) this.projectPos2d.x, (float) (this.projectPos2d.y + this.conf.iconSize / 2 + f_95_), f + 2.0F * f_95_, f_97_, 0.0F, this.emptyMat, i_98_ & 0x7fffffff, 0.0F, 0.0F, 1.0F, 1.0F);
                            Render.drawEnd();
                            for (int i_99_ = 0; i_99_ < strings.length; i_99_++) {
                                String string = strings[i_99_];
                                if (string == null) break;
                                this.smallFont.output(~0xffffff | i_98_ ^ 0xffffffff, (float) (this.projectPos2d.x + f_95_), (float) (this.projectPos2d.y + this.conf.iconSize / 2 + f_95_ + (i - i_99_ - 1) * (f_95_ + f_96_) + f_95_), 0.0F, string);
                            }
                        }
                    }
                }
            }
        }
    }

    // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
    /*private*/ void drawSelectTargetAndSpawnPoint()
    {
        if(mouseState != 4 && mouseState != 6 && mouseState != 8 && mouseState != 9)
            return;
        if(!viewWindow.isMouseOver())
            return;
        Actor actorSelect = null;
        if(mouseState != 8 && mouseState != 9)
        {
            if(mouseState == 6)
                actorSelect = (PAir)selectedFirstPoint();
            else
                actorSelect = (PAir)selectedPoint();
        } else
        {
            actorSelect = (ActorTrigger)selectedActor();
        }
        Point3d point3d;
        if(Actor.isValid(actorSelect))
        {
            point3d = actorSelect.pos.getAbsPoint();
        } else
        {
            Actor actor = selectedActor();
            if(!(actor instanceof PlMisRocket.Rocket))
                return;
            point3d = actor.pos.getAbsPoint();
        }
        project2d(point3d, projectPos2d);
        lineNXYZ[0] = (float)projectPos2d.x;
        lineNXYZ[1] = (float)projectPos2d.y;
        lineNXYZ[2] = 0.0F;
        lineNXYZ[3] = mousePosX;
        lineNXYZ[4] = mousePosY;
        lineNXYZ[5] = 0.0F;
        Render.drawBeginLines(-1);
        int i = 0xff00ff00;
        if(mouseState == 6 || mouseState == 9)
            i = 0xff00ffff;
        Render.drawLines(lineNXYZ, 2, 1.0F, i, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 2);
        Render.drawEnd();
        float f = conf.iconSize * 2;
        Render.drawTile((float)mousePosX - f / 2.0F, (float)mousePosY - f / 2.0F, f, f, 0.0F, selectTargetMat, -1, 0.0F, 0.0F, 1.0F, 1.0F);
    }
    // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---

    private void drawSelectedSpawnPoint()
    {
        if(!(selectedFirstPoint() instanceof PAir))
            return;
        PAir pair = (PAir)selectedFirstPoint();
        PathAir pathair = (PathAir)selectedPath();
        if(pathair == null || pair == null || !Actor.isValid(pair))
            return;
        PlMisAir plmisair = (PlMisAir)Plugin.getPlugin("MisAir");
        PlaneGeneric planegeneric = pathair.getSpawnPoint(plmisair.getSelectedPlaneIndex());
        if(planegeneric == null)
        {
            return;
        } else
        {
            Point3d point3d = pair.pos.getAbsPoint();
            Point3d point3d1 = planegeneric.pos.getAbsPoint();
            project2d(point3d, projectPos2d);
            lineNXYZ[0] = (float)projectPos2d.x;
            lineNXYZ[1] = (float)projectPos2d.y;
            lineNXYZ[2] = 0.0F;
            project2d(point3d1, projectPos2d);
            lineNXYZ[3] = (float)projectPos2d.x;
            lineNXYZ[4] = (float)projectPos2d.y;
            lineNXYZ[5] = 0.0F;
            Render.drawBeginLines(-1);
            Render.drawLines(lineNXYZ, 2, 2.0F, 0xff00ffff, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 2);
            Render.drawEnd();
            return;
        }
    }

    /*private*/ void drawSelectTarget() {
        if (this.mouseState == 4 && this.viewWindow.isMouseOver()) {
            PAir pair = (PAir) this.selectedPoint();
            Point3d point3d;
            if (Actor.isValid(pair)) point3d = pair.pos.getAbsPoint();
            else {
                Actor actor = this.selectedActor();
                if (!(actor instanceof PlMisRocket.Rocket)) return;
                point3d = actor.pos.getAbsPoint();
            }
            this.project2d(point3d, this.projectPos2d);
            this.lineNXYZ[0] = (float) this.projectPos2d.x;
            this.lineNXYZ[1] = (float) this.projectPos2d.y;
            this.lineNXYZ[2] = 0.0F;
            this.lineNXYZ[3] = this.mousePosX;
            this.lineNXYZ[4] = this.mousePosY;
            this.lineNXYZ[5] = 0.0F;
            Render.drawBeginLines(-1);
            Render.drawLines(this.lineNXYZ, 2, 1.0F, -16711936, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 2);
            Render.drawEnd();
            float f = this.conf.iconSize * 2;
            Render.drawTile(this.mousePosX - f / 2.0F, this.mousePosY - f / 2.0F, f, f, 0.0F, this.selectTargetMat, -1, 0.0F, 0.0F, 1.0F, 1.0F);
        }
    }

    public void beginSelectTarget() {
        Path path = this.selectedPath();
        if (path == null || !(path instanceof PathAir)) {
            Actor actor = this.selectedActor();
            if (!(actor instanceof PlMisRocket.Rocket)) return;
        }
        this.mouseState = 4;
        this.viewWindow.mouseCursor = 0;
    }

    public void endSelectTarget() {
        if (this.mouseState == 4) {
            this.mouseState = 0;
            this.viewWindow.mouseCursor = 1;
            Path path = this.selectedPath();
            if (path == null || !(path instanceof PathAir)) {
                PlMisRocket.Rocket rocket = (PlMisRocket.Rocket) this.selectedActor();
                Point3d point3d = this.mouseWorldPos();
                rocket.target = new Point2d(point3d.x, point3d.y);
            } else {
                Actor actor = this.getOverActor();
                if (!Actor.isValid(actor) || actor instanceof PPoint && actor.getOwner() == this.selectedPath()) return;
                PAir pair = (PAir) this.selectedPoint();
                pair.setTarget(actor);
                Plugin plugin = (Plugin) Property.value(pair.getOwner(), "builderPlugin");
                plugin.updateSelector();
            }
            PlMission.setChanged();
        }
    }

    public void breakSelectTarget() {
        if (this.mouseState == 4) {
            this.mouseState = 0;
            this.viewWindow.mouseCursor = 1;
        }
    }

    // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
  public void beginSelectSpawnPoint()
  {
      Path path = selectedPath();
      if(path == null || !(path instanceof PathAir))
      {
          Actor actor = selectedActor();
          if(!(actor instanceof PlMisRocket.Rocket))
              return;
      }
      mouseState = 6;
      viewWindow.mouseCursor = 1;
  }

  public void endSelectSpawnPoint()
  {
      if(mouseState != 6)
          return;
      mouseState = 0;
      viewWindow.mouseCursor = 1;
      Actor actor = getOverActor();
      PathAir pathair = (PathAir)selectedPath();
      if(!Actor.isValid(actor))
          return;
      if(actor instanceof PlaneGeneric)
      {
          PlaneGeneric planegeneric = (PlaneGeneric)actor;
          PlMisAir plmisair = (PlMisAir)Plugin.getPlugin("MisAir");
          int i = plmisair.getSelectedPlaneIndex();
          pathair.setSpawnPoint(i, planegeneric);
          ((GWindowDialogControl) (plmisair.wSpawnPointLabel[i])).cap.set(Plugin.i18n("IndicatedByTheYellowLine"));
      }
      Plugin plugin = (Plugin)Property.value(pathair, "builderPlugin");
      if(plugin != null)
          plugin.updateSelector();
      PlMission.setChanged();
  }

  public void breakSelectSpawnPoint()
  {
      if(mouseState != 6)
      {
          return;
      } else
      {
          mouseState = 0;
          viewWindow.mouseCursor = 1;
          return;
      }
  }

  public void beginSelectTrigger()
  {
      Actor actor = selectedActor();
      //if(actor != null)
      if(!(actor instanceof ActorTrigger))
      {
          // System.out.println("beginSelectTrigger !(actor instanceof ActorTrigger)");
          return;
      } else
      {
          // System.out.println("beginSelectTrigger return");
          mouseState = 8;
          viewWindow.mouseCursor = 1;
          return;
      }
  }

  public void endSelectTrigger()
  {
      // System.out.println("endSelectTrigger 1");
      if(mouseState != 8)
          return;
      // System.out.println("endSelectTrigger 2");
      mouseState = 0;
      viewWindow.mouseCursor = 1;
      Actor actor = getOverActor();
      // System.out.println("endSelectTrigger actor=" + actor.getClass().getName());
      ActorTrigger actortrigger = (ActorTrigger)selectedActor();
      // System.out.println("endSelectTrigger actortrigger=" + actortrigger.getClass().getName());
      if(!Actor.isValid(actor))
          return;
      // System.out.println("endSelectTrigger 3");
      if (!actortrigger.getTriggerActors().contains(actor)) actortrigger.addTriggerActor(actor);
      setSelected(actortrigger);
      Plugin plugin = (Plugin)Property.value(actortrigger, "builderPlugin");
      // System.out.println("endSelectTrigger plugin=" + plugin==null?"null":plugin.getClass().getName());
      if(plugin != null) {
          if (plugin instanceof PlMisTrigger) {
              // System.out.println("endSelectTrigger 4");
              ((PlMisTrigger)plugin).updateSelector(actortrigger, actor);
          } else {
              // System.out.println("endSelectTrigger 5");
              plugin.updateSelector();
          }
      }
      PlMission.setChanged();
  }

  public void breakSelectTrigger()
  {
      if(mouseState != 8)
      {
          return;
      } else
      {
          mouseState = 0;
          viewWindow.mouseCursor = 1;
          return;
      }
  }

  public void beginSelectTriggerLink()
  {
      Actor actor = selectedActor();
      if(!(actor instanceof ActorTrigger))
      {
          return;
      } else
      {
          mouseState = 9;
          viewWindow.mouseCursor = 1;
          return;
      }
  }

  public void endSelectTriggerLink()
  {
      if(mouseState != 9)
          return;
      mouseState = 0;
      viewWindow.mouseCursor = 1;
      Actor actor = getOverActor();
      ActorTrigger actortrigger = (ActorTrigger)selectedActor();
      if(!Actor.isValid(actor))
          return;
      actortrigger.setLinkActor(actor);
      setSelected(actortrigger);
      Plugin plugin = (Plugin)Property.value(actortrigger, "builderPlugin");
      if(plugin != null)
          plugin.updateSelector();
      PlMission.setChanged();
  }

  public void breakSelectTriggerLink()
  {
      if(mouseState != 9)
      {
          return;
      } else
      {
          mouseState = 0;
          viewWindow.mouseCursor = 1;
          return;
      }
  }
  // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---

    private void initHotKeys() {
        HotKeyCmdEnv.setCurrentEnv(envName);
        HotKeyEnv.fromIni(envName, Config.cur.ini, "HotKey " + envName);
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "toLand") {
            public void begin() {
                if (Actor.isValid(Builder.this.selectedActor())) {
                    Builder.this.align(Builder.this.selectedActor());
                    PlMission.setChanged();
                    if (!Builder.this.isFreeView()) Builder.this.repaint();
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "normalLand") {
            public void begin() {
                if (Actor.isValid(Builder.this.selectedActor())) {
                    Point3d point3d = new Point3d();
                    Orient orient = new Orient();
                    Builder.this.selectedActor().pos.getAbs(point3d, orient);
                    Vector3f vector3f = new Vector3f();
                    Engine.land().N(point3d.x, point3d.y, vector3f);
                    orient.orient(vector3f);
                    Builder.this.selectedActor().pos.setAbs(orient);
                    Builder.defaultAzimut = orient.azimut();
                    PlMission.setChanged();
                    if (!Builder.this.isFreeView()) Builder.this.repaint();
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "land") {
            public void begin() {
                Builder.this.changeViewLand();
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "stepAzimut-5") {
            public void begin() {
                Builder.this.stepAzimut(-5);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "stepAzimut-15") {
            public void begin() {
                Builder.this.stepAzimut(-15);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "stepAzimut-30") {
            public void begin() {
                Builder.this.stepAzimut(-30);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "stepAzimut5") {
            public void begin() {
                Builder.this.stepAzimut(5);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "stepAzimut15") {
            public void begin() {
                Builder.this.stepAzimut(15);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "stepAzimut30") {
            public void begin() {
                Builder.this.stepAzimut(30);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "resetAngles") {
            public void begin() {
                if (Actor.isValid(Builder.this.selectedActor())) {
                    Orient orient = new Orient();
                    orient.set(0.0F, 0.0F, 0.0F);
                    Builder.defaultAzimut = 0.0F;
                    Builder.this.selectedActor().pos.setAbs(orient);
                    PlMission.setChanged();
                    if (!Builder.this.isFreeView()) Builder.this.repaint();
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "resetTangage90") {
            public void begin() {
                if (Actor.isValid(Builder.this.selectedActor())) {
                    Orient orient = new Orient();
                    orient.set(0.0F, 90.0F, 0.0F);
                    Builder.defaultAzimut = 0.0F;
                    Builder.this.selectedActor().pos.setAbs(orient);
                    PlMission.setChanged();
                    if (!Builder.this.isFreeView()) Builder.this.repaint();
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "change+") {
            public void begin() {
                Builder.this.changeType(false, false);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "change++") {
            public void begin() {
                Builder.this.changeType(false, true);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "change-") {
            public void begin() {
                Builder.this.changeType(true, false);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "change--") {
            public void begin() {
                Builder.this.changeType(true, true);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "insert") {
            public void begin() {
                Builder.this.insert(false);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "insert+") {
            public void begin() {
                Builder.this.insert(true);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "delete") {
            public void begin() {
                Builder.this.delete(false);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "delete+") {
            public void begin() {
                Builder.this.delete(true);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "fill") {
            public void begin() {
                if (Builder.this.isLoadedLandscape() && !Builder.this.isFreeView() && (!Builder.this.bMultiSelect || Builder.this.bView3D) && Builder.this.mouseState == 0) {
                    Builder.this.mouseState = 5;
                    Point3d point3d = Builder.this.posScreenToLand(Builder.this.mousePosX, Builder.this.mousePosY, 0.0, 0.1);
                    Plugin.doBeginFill(point3d);
                }
            }

            public void end() {
                if (Builder.this.mouseState == 5) {
                    Builder.this.mouseState = 0;
                    Point3d point3d = Builder.this.posScreenToLand(Builder.this.mousePosX, Builder.this.mousePosY, 0.0, 0.1);
                    Plugin.doEndFill(point3d);
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cursor") {
            public void begin() {
                if (Builder.this.isLoadedLandscape() && Builder.this.isFreeView()) if (Builder.this.actorView() == Builder.this.cursor) {
                    Actor actor = Builder.this.selectNear(Builder.this.actorView().pos.getAbsPoint());
                    if (actor != null) {
                        Builder.this.cursor.drawing(false);
                        Builder.this.setSelected(actor);
                    }
                } else if (Actor.isValid(Builder.this.actorView())) {
                    Loc loc = Builder.this.actorView().pos.getAbs();
                    Builder.this.cursor.pos.setAbs(loc);
                    Builder.this.cursor.pos.reset();
                    Builder.this.cursor.drawing(true);
                    Builder.this.setSelected(Builder.this.cursor);
                }
            }
        });
        // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
        
//      HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "objectMove") {
//          public void begin() {
//              if (!Builder.this.isLoadedLandscape() || Builder.this.isFreeView()) return;
//             
//                  if (Builder.this.mouseState == 4) {
//                      Builder.this.endSelectTarget();
//                      return;
//                  }
//                  if(mouseState == 6)
//                  {
//                      endSelectSpawnPoint();
//                      return;
//                  }
//              if (Builder.this.mouseState != 0) return;
//                  Builder.this.mouseState = 1;
//                  Builder.this.viewWindow.mouseCursor = 7;
//                  Actor actor = Builder.this.selectNear(Builder.this.mousePosX, Builder.this.mousePosY);
//                  if (actor != null) {
//                      Builder.this.setSelected(actor);
//                      Builder.this.repaint();
//                  }
//                  Builder.this.setOverActor(null);
//          }
//
//          public void end() {
//              if (!Builder.this.isLoadedLandscape() || Builder.this.isFreeView() || Builder.this.mouseState != 1) return;
//                  Builder.this.mouseState = 0;
//                  Builder.this.viewWindow.mouseCursor = 1;
//          }
//      });
      
      HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "objectMove") {

          public void begin()
          {
              // System.out.println("Builder objectMove mouseState=" + mouseState);
              if(!isLoadedLandscape())
                  return;
              if(isFreeView())
                  return;
              if(mouseState == 4)
              {
                  endSelectTarget();
                  return;
              }
              if(mouseState == 6)
              {
                  endSelectSpawnPoint();
                  return;
              }
              if(mouseState == 8)
              {
                  endSelectTrigger();
                  return;
              }
              if(mouseState == 9)
              {
                  endSelectTriggerLink();
                  return;
              }
              if(mouseState != 0)
                  return;
              mouseState = 1;
              viewWindow.mouseCursor = 7;
              Actor actor = selectNear(mousePosX, mousePosY);
              if(actor != null)
              {
                  setSelected(actor);
                  repaint();
                   
                                                  
              }
              setOverActor(null);
          }

          public void end()
          {
              if(!isLoadedLandscape())
                  return;
              if(isFreeView())
                  return;
              if(mouseState != 1)
              {
                  return;
              } else
              {
                  mouseState = 0;
                  viewWindow.mouseCursor = 1;
                  return;
              }
          }

      }
);
      // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---

//      HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "objectMove") {
//            public void begin() {
//                if (Builder.this.isLoadedLandscape() && !Builder.this.isFreeView()) if (Builder.this.mouseState == 4) Builder.this.endSelectTarget();
//                else if (Builder.this.mouseState == 0) {
//                    Builder.this.mouseState = 1;
//                    Builder.this.viewWindow.mouseCursor = 7;
//                    Actor actor = Builder.this.selectNear(Builder.this.mousePosX, Builder.this.mousePosY);
//                    if (actor != null) {
//                        Builder.this.setSelected(actor);
//                        Builder.this.repaint();
//                    }
//                    Builder.this.setOverActor(null);
//                }
//            }
//
//            public void end() {
//                if (Builder.this.isLoadedLandscape() && !Builder.this.isFreeView() && Builder.this.mouseState == 1) {
//                    Builder.this.mouseState = 0;
//                    Builder.this.viewWindow.mouseCursor = 1;
//                }
//            }
//        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "worldZoom") {
            public void begin() {
                if (Builder.this.isLoadedLandscape() && !Builder.this.isFreeView() && Builder.this.mouseState == 0) {
                    Builder.this.mouseState = 2;
                    Builder.this.mouseFirstPosX = Builder.this.mousePosX;
                    Builder.this.mouseFirstPosY = Builder.this.mousePosY;
                    Builder.this.bMouseRenderRect = true;
                    Builder.this.viewWindow.mouseCursor = 2;
                    Builder.this.setOverActor(null);
                }
            }

            public void end() {
                if (Builder.this.isLoadedLandscape() && !Builder.this.isFreeView() && Builder.this.mouseState == 2) {
                    Builder.this.mouseState = 0;
                    Builder.this.bMouseRenderRect = false;
                    if (Builder.this.mouseFirstPosX == Builder.this.mousePosX) Builder.this.repaint();
                    else {
                        Point3d point3d = Builder.this.posScreenToLand(Builder.this.mouseFirstPosX, Builder.this.mouseFirstPosY, 0.0, 0.1);
                        double d = point3d.x;
                        double d_123_ = point3d.y;
                        point3d = Builder.this.posScreenToLand(Builder.this.mousePosX, Builder.this.mousePosY, 0.0, 0.1);
                        double d_124_ = Builder.this.camera.FOV() * 3.141592653589793 / 180.0 / 2.0;
                        double d_125_ = point3d.x - d;
                        if (d_125_ < 0.0) d_125_ = -d_125_;
                        double d_126_ = d_125_ / 2.0 / Math.tan(d_124_);
                        Builder.this.computeViewMap2D(d_126_, (d + point3d.x) / 2.0, (d_123_ + point3d.y) / 2.0);
                        Builder.this.viewWindow.mouseCursor = 1;
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "unselect") {
            public void begin() {
                if (Builder.this.isLoadedLandscape() && !Builder.this.isFreeView() && Builder.this.mouseState == 0) {
                    Builder.this.setSelected(null);
                    Builder.this.repaint();
                }
            }
        });
        if (this.bMultiSelect) {
            /* empty */
        }
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "select+") {
            public void begin() {
                if (Builder.this.isLoadedLandscape() && !Builder.this.isFreeView() && Builder.this.mouseState == 0) {
                    Builder.this.mouseState = 3;
                    Builder.this.mouseFirstPosX = Builder.this.mousePosX;
                    Builder.this.mouseFirstPosY = Builder.this.mousePosY;
                    Builder.this.bMouseRenderRect = true;
                }
            }

            public void end() {
                if (Builder.this.isLoadedLandscape() && !Builder.this.isFreeView() && Builder.this.mouseState == 3) {
                    Builder.this.mouseState = 0;
                    Builder.this.bMouseRenderRect = false;
                    if (Builder.this.mouseFirstPosX != Builder.this.mousePosX) {
                        Point3d point3d = Builder.this.posScreenToLand(Builder.this.mouseFirstPosX, Builder.this.mouseFirstPosY, 0.0, 0.1);
                        double d = point3d.x;
                        double d_129_ = point3d.y;
                        point3d = Builder.this.posScreenToLand(Builder.this.mousePosX, Builder.this.mousePosY, 0.0, 0.1);
                        Builder.this.select(d, d_129_, point3d.x, point3d.y, true);
                        Builder.this.setSelected(null);
                    }
                    Builder.this.repaint();
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "select-") {
            public void begin() {
                if (Builder.this.isLoadedLandscape() && !Builder.this.isFreeView() && Builder.this.mouseState == 0) {
                    Builder.this.mouseState = 3;
                    Builder.this.mouseFirstPosX = Builder.this.mousePosX;
                    Builder.this.mouseFirstPosY = Builder.this.mousePosY;
                    Builder.this.bMouseRenderRect = true;
                }
            }

            public void end() {
                if (Builder.this.isLoadedLandscape() && !Builder.this.isFreeView() && Builder.this.mouseState == 3) {
                    Builder.this.mouseState = 0;
                    Builder.this.bMouseRenderRect = false;
                    if (Builder.this.mouseFirstPosX != Builder.this.mousePosX) {
                        Point3d point3d = Builder.this.posScreenToLand(Builder.this.mouseFirstPosX, Builder.this.mouseFirstPosY, 0.0, 0.1);
                        double d = point3d.x;
                        double d_131_ = point3d.y;
                        point3d = Builder.this.posScreenToLand(Builder.this.mousePosX, Builder.this.mousePosY, 0.0, 0.1);
                        Builder.this.select(d, d_131_, point3d.x, point3d.y, false);
                        Builder.this.setSelected(null);
                    }
                    Builder.this.repaint();
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "beginSelectSpawnPoint") {

                public void end()
                {
                    if(!isLoadedLandscape())
                        return;
                    if(mouseState != 0)
                        return;
                    if(isFreeView())
                    {
                        return;
                    } else
                    {
                        beginSelectSpawnPoint();
                        return;
                    }
                }
    
            }
        );
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "freeView") {
            public void end() {
                if (Builder.this.isLoadedLandscape() && Builder.this.mouseState == 0) if (Builder.this.isFreeView()) Builder.this.clearActorView();
                else if (Builder.this.bView3D) Builder.this.setActorView();
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "break") {
            public void end() {
                if (Builder.this.isLoadedLandscape()) if (Builder.this.isFreeView()) Builder.this.clearActorView();
                else {
                    Builder.this.setOverActor(null);
                    Builder.this.breakSelectTarget();
                    // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
//                  Builder.this.breakSelectSpawnPoint();
                  Builder.this.breakSelectTrigger();
                  Builder.this.breakSelectTriggerLink();
                  // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
                  breakSelectSpawnPoint();
                    Builder.this.mouseState = 0;
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "popupmenu") {
            public void begin() {
                if (Builder.this.isLoadedLandscape() && Builder.this.mouseState == 0 && !Builder.this.isFreeView()) Builder.this.doPopUpMenu();
            }
        });
        if (!this.bMultiSelect) {
            HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "mis_cut") {
                public void begin() {
                    Builder.this.mis_cut();
                }
            });
            HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "mis_copy") {
                public void begin() {
                    Builder.this.mis_copy(true);
                }
            });
            HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "mis_paste") {
                public void begin() {
                    Builder.this.mis_paste();
                }
            });
        }
    }

    private void mis_cut() {
        if (!Plugin.builder.isFreeView()) {
            this.mis_copy(false);
            Actor[] actors = this.selectedActors();
            for (int i = 0; i < actors.length; i++) {
                Actor actor = actors[i];
                if (actor == null) break;
                if (Actor.isValid(actor) && this.isMiltiSelected(actor)) if (actor instanceof PAirdrome) {
                    PathAirdrome pathairdrome = (PathAirdrome) actor.getOwner();
                    if (pathairdrome.pointIndx((PAirdrome) actor) == 0) pathairdrome.destroy();
                } else actor.destroy();
            }
            this.selectedActorsValidate();
            PlMission.setChanged();
            this.repaint();
        }
    }

    private void mis_copy(boolean bool) {
        if (!Plugin.builder.isFreeView()) {
            this.mis_properties.clear();
            this.mis_cBoxes.clear();
            this.mis_clipLoc.clear();
            int i = 0;
            this.mis_clipP0.x = this.mis_clipP0.y = this.mis_clipP0.z = 0.0;
            Actor[] actors = this.selectedActors();
            for (int i_138_ = 0; i_138_ < actors.length; i_138_++) {
                Actor actor = actors[i_138_];
                if (actor == null) break;
                if (Actor.isValid(actor) && this.isMiltiSelected(actor)) {
                    Loc loc = new Loc();
                    actor.pos.getAbs(loc);
                    this.mis_clipLoc.add(loc);
                    this.mis_clipP0.add(loc.getPoint());
                    this.setSelected(actor);
                    this.mis_cBoxes.add("" + this.wSelect.comboBox1.getSelected());
                    this.mis_cBoxes.add("" + this.wSelect.comboBox2.getSelected());
                    this.mis_properties.add(Plugin.mis_doGetProperties(actor));
                    this.setSelected(null);
                    i++;
                }
            }
            if (i > 1) {
                this.mis_clipP0.x /= i;
                this.mis_clipP0.y /= i;
                this.mis_clipP0.z /= i;
            }
            if (bool) this.selectActorsClear();
            this.repaint();
        }
    }

    private void mis_paste() {
        if (!this.isFreeView()) {
            this.selectActorsClear();
            int i = this.mis_properties.size();
            if (i != 0) {
                Point3d point3d = this.mouseWorldPos();
                Loc loc = new Loc();
                Point3d point3d_139_ = new Point3d();
                for (int i_140_ = 0; i_140_ < i; i_140_++) {
                    Loc loc_141_ = (Loc) this.mis_clipLoc.get(i_140_);
                    point3d_139_.sub(loc_141_.getPoint(), this.mis_clipP0);
                    point3d_139_.add(point3d);
                    loc.set(point3d_139_, loc_141_.getOrient());
                    this.wSelect.comboBox1.setSelected(Integer.parseInt((String) this.mis_cBoxes.get(i_140_ * 2)), false, true);
                    this.wSelect.comboBox2.setSelected(Integer.parseInt((String) this.mis_cBoxes.get(i_140_ * 2 + 1)), false, true);
                    Actor actor = Plugin.mis_doInsert(loc, (String) this.mis_properties.get(i_140_));
                    this.selectActorsAdd(actor);
                    this.setSelected(null);
                }
                PlMission.setChanged();
                this.repaint();
            }
        }
    }

    public static final String getFullClassName(Actor actor) {
        return actor instanceof SoftClass ? ((SoftClass) actor).fullClassName() : actor.getClass().getName();
    }

    public void doPopUpMenu() {
        if (this.mousePosX != -1) {
            if (this.popUpMenu == null) this.popUpMenu = (GWindowMenuPopUp) this.viewWindow.create(new GWindowMenuPopUp());
            else {
                if (this.popUpMenu.isVisible()) return;
                this.popUpMenu.clearItems();
            }
            Point3d point3d = this.posScreenToLand(this.mousePosX, this.mousePosY, 0.0, 0.1);
            float f = this.viewWindow.win.dy - this.mousePosY - 1.0F;
            if (Actor.isValid(this.selectedPoint()) || Actor.isValid(this.selectedActor())) {
                this.popUpMenu.addItem(new GWindowMenuItem(this.popUpMenu, Plugin.i18n("&Unselect"), Plugin.i18n("TIPUnselect")) {
                    public void execute() {
                        Builder.this.setSelected(null);
                    }
                });
                this.popUpMenu.addItem(new GWindowMenuItem(this.popUpMenu, Plugin.i18n("&Delete"), Plugin.i18n("TIPDelete")) {
                    public void execute() {
                        Builder.this.delete(true);
                    }
                });
            }
            if (this.selectedActors.size() > 0) {
                this.popUpMenu.addItem(new GWindowMenuItem(this.popUpMenu, Plugin.i18n("Unselect&All"), Plugin.i18n("TIPUnselectAll")) {
                    public void execute() {
                        Builder.this.setSelected(null);
                        Builder.this.selectedActors.clear();
                    }
                });
                this.popUpMenu.addItem(new GWindowMenuItem(this.popUpMenu, Plugin.i18n("Cut"), Plugin.i18n("TIPCut")) {
                    public void execute() {
                        Builder.this.mis_cut();
                    }
                });
                this.popUpMenu.addItem(new GWindowMenuItem(this.popUpMenu, Plugin.i18n("Copy"), Plugin.i18n("TIPCopy")) {
                    public void execute() {
                        Builder.this.mis_copy(true);
                    }
                });
            }
            if (this.mis_properties.size() > 0) this.popUpMenu.addItem(new GWindowMenuItem(this.popUpMenu, Plugin.i18n("Paste"), Plugin.i18n("TIPPaste")) {
                public void execute() {
                    Builder.this.mis_paste();
                }
            });
            Plugin.doFillPopUpMenu(this.popUpMenu, point3d);
            if (this.popUpMenu.size() > 0) {
                this.popUpMenu.setPos(this.mousePosX, f);
                this.popUpMenu.showModal();
                this.movedActor = null;
            } else this.popUpMenu.hideWindow();
        }
    }

    public void setViewLand() {
        Main3D.cur3D().setDrawLand(this.conf.bShowLandscape);
        if (Main3D.cur3D().land2D != null) Main3D.cur3D().land2D.show(this.isView3D() ? false : this.conf.bShowLandscape);
    }

    public void changeViewLand() {
        this.conf.bShowLandscape = !this.conf.bShowLandscape;
        Main3D.cur3D().setDrawLand(this.conf.bShowLandscape);
        if (Main3D.cur3D().land2D != null) Main3D.cur3D().land2D.show(this.isView3D() ? false : this.conf.bShowLandscape);
        this.wViewLand.wShow.setChecked(this.conf.bShowLandscape, false);
        if (!this.isFreeView()) this.repaint();
    }

    public void changeType(boolean bool, boolean bool_154_) {
        if (this.isLoadedLandscape() && this.mouseState == 0 && Actor.isValid(this.selectedActor()) && this.selectedActor() != this.cursor) {
            Plugin.doChangeType(bool, bool_154_);
            if (!this.isFreeView()) this.repaint();
        }
    }

    private void insert(boolean bool) {
        if (this.isLoadedLandscape() && this.mouseState == 0) {
            Loc loc = new Loc();
            if (this.isFreeView()) {
                if (!Actor.isValid(this.actorView())) return;
                this.actorView().pos.getAbs(loc);
                if (this.actorView() != this.cursor) loc.add(new Point3d(1.0, 1.0, 0.0));
            } else loc.set(this.posScreenToLand(this.mousePosX, this.mousePosY, 0.0, 0.1), new Orient(defaultAzimut, 0.0F, 0.0F));
            Plugin.doInsert(loc, bool);
            if (!this.isFreeView()) this.repaint();
        }
    }

    private void delete(boolean bool) {
        if (this.isLoadedLandscape() && this.mouseState == 0) if (this.isFreeView()) {
            if (Actor.isValid(this.selectedActor()) && this.selectedActor() != this.cursor && !(this.selectedActor() instanceof Bridge)) {
                Loc loc = this.selectedActor().pos.getAbs();
                this.selectedActor().destroy();
                Plugin.doAfterDelete();
                PlMission.setChanged();
                Actor actor = null;
                if (bool) actor = this.selectNear(loc.getPoint());
                if (actor == null) {
                    actor = this.cursor;
                    this.cursor.pos.setAbs(loc);
                    this.cursor.pos.reset();
                    this.cursor.drawing(true);
                }
                this.setSelected(actor);
            }
        } else {
            if (Actor.isValid(this.selectedPoint())) {
                Path path = this.selectedPath();
                try {
                    PPoint ppoint = path.selectPrev(this.pathes.currentPPoint);
                    this.pathes.currentPPoint.destroy();
                    Plugin.doAfterDelete();
                    PlMission.setChanged();
                    if (ppoint == null) {
                        path.destroy();
                        Plugin.doAfterDelete();
                        this.setSelected(null);
                    } else this.setSelected(bool ? ppoint : null);
                } catch (Exception exception) {
                    /* empty */
                }
            } else {
                boolean bool_155_ = false;
                if (Actor.isValid(this.selectedActor()) && !this.selectedActors.containsKey(this.selectedActor())) {
                    if (this.bMultiSelect && this.selectedActor() instanceof Bridge) {
                        if (this.deletingMessageBox == null) {
                            this.deletingActor = this.selectedActor();
                            this.bDeletingChangeSelection = bool;
                            this.deletingMessageBox = new GWindowMessageBox(this.clientWindow, 20.0F, true, "Confirm DELETE", "Delete Bridge ?", 1, 0.0F) {
                                public void result(int i) {
                                    if (i == 3) {
                                        if (Builder.this.deletingActor == Builder.this.selectedActor()) Builder.this.delete(Builder.this.bDeletingChangeSelection);
                                    } else {
                                        Builder.this.deletingMessageBox = null;
                                        Builder.this.deletingActor = null;
                                        Builder.this.setSelected(null);
                                    }
                                }
                            };
                            return;
                        }
                        this.deletingMessageBox = null;
                        PlMapLoad.bridgeActors.remove(this.selectedActor());
                        this.selectedActor().destroy();
                        this.deletingActor = null;
                    } else {
                        Plugin plugin = (Plugin) Property.value(this.selectedActor(), "builderPlugin");
                        if (plugin != null) plugin.delete(this.selectedActor());
                    }
                    bool_155_ = true;
                } else {
                    Actor[] actors = this.selectedActors();
                    for (int i = 0; i < actors.length; i++) {
                        Actor actor = actors[i];
                        if (actor == null) break;
                        if (Actor.isValid(actor)) {
                            Plugin plugin = (Plugin) Property.value(actor, "builderPlugin");
                            plugin.delete(actor);
                            bool_155_ = true;
                        }
                    }
                }
                if (bool_155_) {
                    Plugin.doAfterDelete();
                    PlMission.setChanged();
                    this.selectedActorsValidate();
                    if (bool) this.setSelected(this.selectNearFull(this.mousePosX, this.mousePosY));
                    else this.setSelected(null);
                } else {
                    Loc loc = new Loc();
                    loc.set(this.posScreenToLand(this.mousePosX, this.mousePosY, 0.0, 0.1));
                    Plugin.doDelete(loc);
                }
            }
            this.repaint();
        }
    }

    private void stepAzimut(int i) {
        if (this.isLoadedLandscape()) if (Actor.isValid(this.selectedActor())) {
            if (!(this.selectedActor() instanceof Bridge)) {
                Orient orient = new Orient();
                this.selectedActor().pos.getAbs(orient);
                orient.wrap();
                orient.set(orient.azimut() + i, orient.tangage(), orient.kren());
                this.selectedActor().pos.setAbs(orient);
                defaultAzimut = orient.azimut();
                this.align(this.selectedActor());
                PlMission.setChanged();
                if (!this.isFreeView()) this.repaint();
            }
        } else {
            if (this.bMultiSelect) {
                /* empty */
            }
            if (!this.isFreeView() && this.countSelectedActors() > 0) {
                Point3d point3d = this.posScreenToLand(this.mousePosX, this.mousePosY, 0.0, 0.1);
                Point3d point3d_160_ = new Point3d();
                Orient orient = new Orient();
                Actor[] actors = this.selectedActors();
                double d = Math.sin(i * 3.141592653589793 / 180.0);
                double d_161_ = Math.cos(i * 3.141592653589793 / 180.0);
                for (int i_162_ = 0; i_162_ < actors.length; i_162_++) {
                    Actor actor = actors[i_162_];
                    if (actor == null) break;
                    if (Actor.isValid(actor)) {
                        Point3d point3d_163_ = actor.pos.getAbsPoint();
                        point3d_160_.x = point3d_163_.x - point3d.x;
                        point3d_160_.y = point3d_163_.y - point3d.y;
                        if (point3d_160_.x * point3d_160_.x + point3d_160_.y * point3d_160_.y > 1.0E-6) {
                            double d_164_ = point3d_160_.x * d_161_ + point3d_160_.y * d;
                            double d_165_ = point3d_160_.y * d_161_ - point3d_160_.x * d;
                            point3d_160_.x = d_164_ + point3d.x;
                            point3d_160_.y = d_165_ + point3d.y;
                        }
                        point3d_160_.z = point3d_163_.z;
                        if (this.bRotateObjects) {
                            actor.pos.getAbs(orient);
                            orient.wrap();
                            orient.set(orient.azimut() + i, orient.tangage(), orient.kren());
                            actor.pos.setAbs(point3d_160_, orient);
                        } else actor.pos.setAbs(point3d_160_);
                        this.align(actor);
                    }
                }
                this.repaint();
            }
        }
    }

    public void tipErr(String string) {
        System.err.println(string);
        this.clientWindow.toolTip(string);
    }

    public void tip(String string) {
        this.clientWindow.toolTip(string);
    }

    protected void doMenu_FileExit() {
        if (Plugin.doExitBuilder()) Main.stateStack().pop();
    }

    public void repaint() {
        /* empty */
    }

    public void enterRenders() {
        this.bView3D = false;
        Main3D.cur3D().renderMap2D.setShow(true);
        Main3D.cur3D().renderMap2D.useClearColor(true);
        Main3D.cur3D().render3D0.setShow(false);
        Main3D.cur3D().render3D1.setShow(false);
        Main3D.cur3D().render2D.setShow(false);
        _viewPort[2] = (int) this.viewWindow.win.dx;
        _viewPort[3] = (int) this.viewWindow.win.dy;
        GPoint gpoint = this.viewWindow.windowToGlobal(0.0F, 0.0F);
        _viewPort[0] = (int) gpoint.x + ((GUIWindowManager) this.viewWindow.root.manager).render.getViewPortX0();
        _viewPort[1] = (int) (this.viewWindow.root.win.dy - gpoint.y - this.viewWindow.win.dy) + ((GUIWindowManager) this.viewWindow.root.manager).render.getViewPortY0();
        Main3D.cur3D().renderMap2D.setViewPort(_viewPort);
        Main3D.cur3D().render3D0.setViewPort(_viewPort);
        Main3D.cur3D().render3D1.setViewPort(_viewPort);
        this.camera2D.set(0.0F, _viewPort[2], 0.0F, _viewPort[3]);
        Render render = (Render) Actor.getByName("renderConsoleGL0");
        if (render != null) {
            CameraOrtho2D cameraortho2d = (CameraOrtho2D) render.getCamera();
            render.setViewPort(_viewPort);
            cameraortho2d.set(0.0F, _viewPort[2], 0.0F, _viewPort[3]);
        }
        render = (Render) Actor.getByName("renderTextScr");
        if (render != null) {
            CameraOrtho2D cameraortho2d = (CameraOrtho2D) render.getCamera();
            render.setViewPort(_viewPort);
            cameraortho2d.set(0.0F, _viewPort[2], 0.0F, _viewPort[3]);
        }
    }

    public void leaveRenders() {
        Main3D.cur3D().renderMap2D.setShow(false);
        Main3D.cur3D().renderMap2D.useClearColor(false);
        Main3D.cur3D().render3D0.setShow(true);
        Main3D.cur3D().render3D1.setShow(true);
        Main3D.cur3D().render2D.setShow(true);
        this.leaveRender(Main3D.cur3D().renderMap2D);
        this.leaveRender(Main3D.cur3D().render3D0);
        this.leaveRender(Main3D.cur3D().render3D1);
        this.leaveRender(Main3D.cur3D().render2D);
        this.leaveRender((Render) Actor.getByName("renderConsoleGL0"));
        this.leaveRender((Render) Actor.getByName("renderTextScr"));
    }

    private void leaveRender(Render render) {
        if (render != null) render.contextResized();
    }

    public void mapLoaded() {
        this.enterRenders();
        if (!this.isLoadedLandscape()) {
            this.bView3D = false;
            Main3D.cur3D().renderMap2D.setShow(true);
            Main3D.cur3D().render3D0.setShow(false);
            Main3D.cur3D().render3D1.setShow(false);
            Main3D.cur3D().render2D.setShow(false);
            this.mapXscrollBar.setRange(0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
            this.mapYscrollBar.setRange(0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
            this.mapZSlider.setRange(0, 2, 0);
        } else {
            this.computeViewMap2D(-1.0, 0.0, 0.0);
            if (Main3D.cur3D().land2D != null) Main3D.cur3D().land2D.show(this.conf.bShowLandscape);
        }
    }

    public void enter() {
        Main3D.cur3D().resetGame();
        saveMaxVisualDistance = World.MaxVisualDistance;
        saveMaxStaticVisualDistance = World.MaxStaticVisualDistance;
        World.MaxVisualDistance = MaxVisualDistance;
        World.MaxStaticVisualDistance = MaxVisualDistance;
        this.enterRenders();
        this.setViewLand();
        Main3D.cur3D().camera3D.dreamFire(true);
        Main3D.cur3D().hookView.use(true);
        Main3D.cur3D().hookView.reset();
        Main3D.cur3D().bEnableFog = false;
        Runaway.bDrawing = this.bMultiSelect;
        this.camera.interpPut(new InterpolateOnLand(), "onLand", Time.currentReal(), null);
        this.viewWindow.mouseCursor = 1;
        this.pathes = new Pathes();
        mis_properties = new ArrayList();
        mis_cBoxes = new ArrayList();
        mis_clipLoc = new ArrayList();
        mis_clipP0 = new Point3d();
        PlMission.doMissionReload();
        this.cursor = new CursorMesh("3do/primitive/coord/mono.sim");
    }

    public void leave() {
        this.camera.interpEnd("onLand");
        PlMapLoad plmapload = (PlMapLoad) Plugin.getPlugin("MapLoad");
        plmapload.mapUnload();
        mis_properties = null;
        mis_cBoxes = null;
        mis_clipLoc = null;
        mis_clipP0 = null;
        selectedActors.clear();
        this.mouseState = 0;
        this.setSelected(null);
        if (this.wSelect.isVisible()) this.wSelect.hideWindow();
        if (this.wViewLand.isVisible()) this.wViewLand.hideWindow();
        if (this.wSnap.isVisible()) this.wSnap.hideWindow();
        Plugin.doFreeResources();
        this.leaveRenders();
        Main3D.cur3D().bEnableFog = true;
        Main3D.cur3D().camera3D.dreamFire(false);
        Runaway.bDrawing = false;
        this.pathes.destroy();
        this.pathes = null;
        this.cursor.destroy();
        this.cursor = null;
        this.mouseXYZATK.resetGame();
        Main3D.cur3D().resetGame();
        this.conf.save();
        World.MaxVisualDistance = saveMaxVisualDistance;
        World.MaxStaticVisualDistance = saveMaxVisualDistance;
    }

    public Builder(GWindowRootMenu gwindowrootmenu, String string) {
        envName = string;
        ((GUIWindowManager) gwindowrootmenu.manager).setUseGMeshs(true);
        this.mis_properties = new ArrayList();
        this.mis_cBoxes = new ArrayList();
        this.mis_clipLoc = new ArrayList();
        this.mis_clipP0 = new Point3d();
        this.camera = (Camera3D) Actor.getByName("camera");
        this.camera2D = (CameraOrtho2D) Actor.getByName("cameraMap2D");
        this.mouseXYZATK = new MouseXYZATK("MouseXYZ");
        this.mouseXYZATK.setCamera(this.camera);
        this.conf = new BldConfig();
        this.conf.load(new SectFile("bldconf.ini", 1), "builder config");
        Plugin.loadAll(new SectFile("bldconf.ini", 0), PLUGINS_SECTION, this);
        gwindowrootmenu.clientWindow = gwindowrootmenu.create(this.clientWindow = new ClientWindow());
        this.mapXscrollBar = new XScrollBar(this.clientWindow);
        this.mapYscrollBar = new YScrollBar(this.clientWindow);
        this.mapZSlider = new ZSlider(this.clientWindow);
        this.mapXscrollBar.setRange(0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
        this.mapYscrollBar.setRange(0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
        this.mapZSlider.setRange(0, 2, 0);
        this.mapZSlider.bSlidingNotify = true;
        this.clientWindow.create(this.viewWindow = new ViewWindow());
        this.clientWindow.resized();
        gwindowrootmenu.statusBar.defaultHelp = null;
        this.mFile = gwindowrootmenu.menuBar.addItem(Plugin.i18n("&File"), Plugin.i18n("Load/SaveMissionFiles"));
        this.mFile.subMenu = (GWindowMenu) this.mFile.create(new GWindowMenu());
        this.mFile.subMenu.close(false);
        this.mFile.subMenu.addItem("-", null);
        this.mFile.subMenu.addItem(new GWindowMenuItem(this.mFile.subMenu, Plugin.i18n("&Exit"), Plugin.i18n("ExitBuilder")) {
            public void execute() {
                Builder.this.doMenu_FileExit();
            }
        });
        this.mEdit = gwindowrootmenu.menuBar.addItem(Plugin.i18n("&Edit"), Plugin.i18n("TIPEdit"));
        this.mEdit.subMenu = (GWindowMenu) this.mEdit.create(new GWindowMenu());
        this.mEdit.subMenu.close(false);
        if (this.bMultiSelect) this.mEdit.subMenu.addItem(new GWindowMenuItem(this.mEdit.subMenu, Plugin.i18n("&Select_All"), null) {
            public void execute() {
                Plugin.doSelectAll();
            }
        });
        this.mEdit.subMenu.addItem(new GWindowMenuItem(this.mEdit.subMenu, Plugin.i18n("&Unselect_All"), null) {
            public void execute() {
                Builder.this.setSelected(null);
                Builder.this.selectedActors.clear();
            }
        });
        GWindowMenuItem gwindowmenuitem = this.mEdit.subMenu.addItem(new GWindowMenuItem(this.mEdit.subMenu, Plugin.i18n("&Enable_Select"), null) {
            public void execute() {
                Builder.this.setSelected(null);
                Builder.this.selectedActors.clear();
                Builder.this.conf.bEnableSelect = !Builder.this.conf.bEnableSelect;
                this.bChecked = Builder.this.conf.bEnableSelect;
            }
        });
        gwindowmenuitem.bChecked = this.conf.bEnableSelect;
        this.mEdit.subMenu.addItem("-", null);
        this.mEdit.subMenu.addItem(new GWindowMenuItem(this.mEdit.subMenu, Plugin.i18n("&DeleteAll"), Plugin.i18n("TIPDeleteAll")) {
            public void execute() {
                Builder.this.deleteAll();
            }
        });
        this.mEdit.subMenu.addItem("-", null);
        gwindowmenuitem = this.mEdit.subMenu.addItem(new GWindowMenuItem(this.mEdit.subMenu, Plugin.i18n("&Rotate_Objects"), null) {
            public void execute() {
                Builder.this.bRotateObjects = !Builder.this.bRotateObjects;
                this.bChecked = Builder.this.bRotateObjects;
            }
        });
        gwindowmenuitem.bChecked = this.bRotateObjects;
        this.mConfigure = gwindowrootmenu.menuBar.addItem(Plugin.i18n("&Configure"), Plugin.i18n("TIPConfigure"));
        this.mConfigure.subMenu = (GWindowMenu) this.mConfigure.create(new GWindowMenu());
        this.mConfigure.subMenu.close(false);
        this.mView = gwindowrootmenu.menuBar.addItem(Plugin.i18n("&View"), Plugin.i18n("TIPView"));
        this.mView.subMenu = (GWindowMenu) this.mView.create(new GWindowMenu());
        this.mView.subMenu.close(false);
        this.mSelectItem = this.mView.subMenu.addItem(new GWindowMenuItem(this.mView.subMenu, Plugin.i18n("&Object"), Plugin.i18n("TIPObject")) {
            public void execute() {
                if (Builder.this.wSelect.isVisible()) Builder.this.wSelect.hideWindow();
                else Builder.this.wSelect.showWindow();
            }
        });
        this.mViewLand = this.mView.subMenu.addItem(new GWindowMenuItem(this.mView.subMenu, Plugin.i18n("&Landscape"), Plugin.i18n("TIPLandscape")) {
            public void execute() {
                if (Builder.this.wViewLand.isVisible()) Builder.this.wViewLand.hideWindow();
                else Builder.this.wViewLand.showWindow();
            }
        });
        this.mSnap = this.mView.subMenu.addItem(new GWindowMenuItem(this.mView.subMenu, Plugin.i18n("&Snap"), null) {
            public void execute() {
                if (Builder.this.wSnap.isVisible()) Builder.this.wSnap.hideWindow();
                else Builder.this.wSnap.showWindow();
            }
        });
        this.mView.subMenu.addItem("-", null);
        this.mDisplayFilter = this.mView.subMenu.addItem(new GWindowMenuItem(this.mView.subMenu, Plugin.i18n("&DisplayFilter"), Plugin.i18n("TIPDisplayFilter")));
        this.mDisplayFilter.subMenu = (GWindowMenu) this.mDisplayFilter.create(new GWindowMenu());
        this.mDisplayFilter.subMenu.close(false);
        this.mView.subMenu.addItem("-", null);
        gwindowmenuitem = this.mView.subMenu.addItem(new GWindowMenuItem(this.mView.subMenu, Plugin.i18n("&IconSize"), Plugin.i18n("TIPIconSize")));
        gwindowmenuitem.subMenu = (GWindowMenu) gwindowmenuitem.create(new GWindowMenu());
        gwindowmenuitem.subMenu.close(false);
        this.mIcon8 = gwindowmenuitem.subMenu.addItem(new GWindowMenuItem(gwindowmenuitem.subMenu, "&8", null) {
            public void execute() {
                Builder.this.conf.iconSize = 8;
                IconDraw.setScrSize(Builder.this.conf.iconSize, Builder.this.conf.iconSize);
                Builder.this.mIcon8.bChecked = true;
                Builder.this.mIcon16.bChecked = Builder.this.mIcon32.bChecked = Builder.this.mIcon64.bChecked = false;
            }
        });
        this.mIcon16 = gwindowmenuitem.subMenu.addItem(new GWindowMenuItem(gwindowmenuitem.subMenu, "&16", null) {
            public void execute() {
                Builder.this.conf.iconSize = 16;
                IconDraw.setScrSize(Builder.this.conf.iconSize, Builder.this.conf.iconSize);
                Builder.this.mIcon16.bChecked = true;
                Builder.this.mIcon8.bChecked = Builder.this.mIcon32.bChecked = Builder.this.mIcon64.bChecked = false;
            }
        });
        this.mIcon32 = gwindowmenuitem.subMenu.addItem(new GWindowMenuItem(gwindowmenuitem.subMenu, "&32", null) {
            public void execute() {
                Builder.this.conf.iconSize = 32;
                IconDraw.setScrSize(Builder.this.conf.iconSize, Builder.this.conf.iconSize);
                Builder.this.mIcon32.bChecked = true;
                Builder.this.mIcon8.bChecked = Builder.this.mIcon16.bChecked = Builder.this.mIcon64.bChecked = false;
            }
        });
        this.mIcon64 = gwindowmenuitem.subMenu.addItem(new GWindowMenuItem(gwindowmenuitem.subMenu, "&64", null) {
            public void execute() {
                Builder.this.conf.iconSize = 64;
                IconDraw.setScrSize(Builder.this.conf.iconSize, Builder.this.conf.iconSize);
                Builder.this.mIcon64.bChecked = true;
                Builder.this.mIcon8.bChecked = Builder.this.mIcon16.bChecked = Builder.this.mIcon32.bChecked = false;
            }
        });
        switch (this.conf.iconSize) {
            case 8:
                this.mIcon8.bChecked = true;
                break;
            case 16:
                this.mIcon16.bChecked = true;
                break;
            case 32:
                this.mIcon32.bChecked = true;
                break;
            case 64:
                this.mIcon64.bChecked = true;
                break;
            default:
                this.conf.iconSize = 16;
                this.mIcon16.bChecked = true;
        }
        IconDraw.setScrSize(this.conf.iconSize, this.conf.iconSize);
        gwindowmenuitem = this.mView.subMenu.addItem(new GWindowMenuItem(this.mView.subMenu, Plugin.i18n("Save&ViewHLand"), Plugin.i18n("TIPSaveViewHLand")) {
            public void execute() {
                Builder.this.conf.bSaveViewHLand = !Builder.this.conf.bSaveViewHLand;
                this.bChecked = Builder.this.conf.bSaveViewHLand;
            }
        });
        gwindowmenuitem.bChecked = this.conf.bSaveViewHLand;
        gwindowmenuitem = this.mView.subMenu.addItem(new GWindowMenuItem(this.mView.subMenu, Plugin.i18n("Show&Grid"), Plugin.i18n("TIPShowGrid")) {
            public void execute() {
                Builder.this.conf.bShowGrid = !Builder.this.conf.bShowGrid;
                this.bChecked = Builder.this.conf.bShowGrid;
            }
        });
        gwindowmenuitem.bChecked = this.conf.bShowGrid;
        gwindowmenuitem = this.mView.subMenu.addItem(new GWindowMenuItem(this.mView.subMenu, Plugin.i18n("&AnimateCamera"), Plugin.i18n("TIPAnimateCamera")) {
            public void execute() {
                Builder.this.conf.bAnimateCamera = !Builder.this.conf.bAnimateCamera;
                this.bChecked = Builder.this.conf.bAnimateCamera;
            }
        });
        gwindowmenuitem.bChecked = this.conf.bAnimateCamera;
        this.wSelect = new WSelect(this, this.clientWindow) {
            // TODO: Added by |ZUTI|: tabs sizing based on tab name
            // ----------------------------------------------------
            public void doRender(boolean arg0) {
                Tab tab = this.tabsClient.getTab(this.tabsClient.current);
                if (tab != null && !Builder.zutiLastTabCaption.equals(tab.cap.caption)) {
                    if (tab.cap.caption.equals(Plugin.i18n("BornPlaceActor"))) Plugin.builder.wSelect.setMetricSize(40, 37);
                    else if (tab.cap.caption.equals(Plugin.i18n("bplace_aircraft"))) Plugin.builder.wSelect.setMetricSize(40, 30);
                    else if (tab.cap.caption.equals(Plugin.i18n("mds.tabSpawn"))) Plugin.builder.wSelect.setMetricSize(40, 40);
                    else if (tab.cap.caption.equals(Plugin.i18n("mds.tabCapturing"))) Plugin.builder.wSelect.setMetricSize(40, 24);
                    else if (tab.cap.caption.equals(Plugin.i18n("mds.tabRRR"))) Plugin.builder.wSelect.setMetricSize(40, 45);
                    else if (tab.cap.caption.equals(Plugin.i18n("Type"))) Plugin.builder.wSelect.setMetricSize(20, 25);
                    else if (tab.cap.caption.equals(Plugin.i18n("tTarget"))) Plugin.builder.wSelect.setMetricSize(36, 29);

                    // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
                    else if (tab.cap.caption.equals(Plugin.i18n("tTrigger"))) Plugin.builder.wSelect.setMetricSize(60, 38);
                    else if (tab.cap.caption.equals(Plugin.i18n("tTargets"))) Plugin.builder.wSelect.setMetricSize(37, 20);
                    else if (tab.cap.caption.equals(Plugin.i18n("tMessage"))) Plugin.builder.wSelect.setMetricSize(37, 20);
                    // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---

                    // TODO: +++ Make Waypoint Window Larger by SAS~Storebror +++
                    else if (tab.cap.caption.equals(Plugin.i18n("Waypoint"))) Plugin.builder.wSelect.setMetricSize(40, 37);
                    // TODO: --- Make Waypoint Window Larger by SAS~Storebror ---

                    Builder.zutiLastTabCaption = tab.cap.caption;
                }
                super.doRender(arg0);
            }
            // ----------------------------------------------------
        };
        this.wViewLand = new WViewLand(this, this.clientWindow);
        this.wSnap = new WSnap(this, this.clientWindow);

        // TODO: Added by |ZUTI|
        // ----------------------------------------------------------------------------------
        this.mZutiMds = gwindowrootmenu.menuBar.addItem(Plugin.i18n("mds.menu"), Plugin.i18n("Edit MDS properties"));
        this.mZutiMds.subMenu = (GWindowMenu) this.mZutiMds.create(new GWindowMenu());
        this.mZutiMds.subMenu.close(false);
        // ----------------------------------------------------------------------------------

        Plugin.doCreateGUI();
        this._gridFont = TTFont.font[1];
        this.smallFont = TTFont.font[0];
        this.emptyMat = Mat.New("icons/empty.mat");
        this.selectTargetMat = Mat.New("icons/selecttarget.mat");
        this.initHotKeys();
        Plugin.doStart();
        HotKeyCmdEnv.enable(envName, false);
        HotKeyCmdEnv.enable("MouseXYZ", false);
    }

    public String getCamoMesh(Class class1)
    {
        Object obj = Property.value(class1, "meshName", (Object)null);
        String as[] = new String[3];
        try
        {
            as = (String[])(String[])obj;
        }
        catch(ClassCastException classcastexception)
        {
            obj = null;
        }
        String s = null;
        if(obj == null)
            s = Property.stringValue(class1, "meshName", null);
        else
            switch(World.cur().camouflage)
            {
            case 1: // '\001'
                s = as[1];
                break;

            case 2: // '\002'
                s = as[2];
                break;

            default:
                s = as[0];
                break;
            }
        return s;
    }

    // TODO: Added by |ZUTI|
    // ---------------------------------------
    public GWindowMenuBarItem mZutiMds;
    private static String     zutiLastTabCaption = "";
    // ---------------------------------------
}
