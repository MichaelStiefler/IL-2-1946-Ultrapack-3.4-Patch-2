/* 4.10.1 class */
package com.maddox.il2.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import com.maddox.JGP.Point2d;
import com.maddox.JGP.Point3d;
import com.maddox.gwindow.GNotifyListener;
import com.maddox.gwindow.GWindow;
import com.maddox.gwindow.GWindowComboControl;
import com.maddox.gwindow.GWindowDialogClient;
import com.maddox.gwindow.GWindowEditControl;
import com.maddox.gwindow.GWindowLabel;
import com.maddox.gwindow.GWindowMenu;
import com.maddox.gwindow.GWindowMenuItem;
import com.maddox.gwindow.GWindowMessageBox;
import com.maddox.gwindow.GWindowTabDialogClient;
import com.maddox.il2.ai.Army;
import com.maddox.il2.ai.Chief;
import com.maddox.il2.ai.Regiment;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorSpawn;
import com.maddox.il2.engine.ActorSpawnArg;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.IconDraw;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.engine.Render;
import com.maddox.il2.game.I18N;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.PaintScheme;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.Ship.RwyTransp;
import com.maddox.il2.objects.ships.Ship.RwyTranspSqr;
import com.maddox.il2.objects.ships.Ship.RwyTranspWide;
import com.maddox.il2.objects.ships.ShipGeneric;
import com.maddox.il2.objects.vehicles.artillery.ArtilleryGeneric;
import com.maddox.il2.objects.vehicles.planes.Plane.SpawnplaceMarker;
import com.maddox.il2.objects.vehicles.planes.Plane.Spawnplaceholder;
import com.maddox.il2.objects.vehicles.planes.PlaneGeneric;
import com.maddox.il2.objects.vehicles.radios.LorenzBlindLandingBeacon;
import com.maddox.il2.objects.vehicles.stationary.DZGeneric;
import com.maddox.il2.objects.vehicles.stationary.SirenGeneric;
import com.maddox.il2.objects.vehicles.stationary.SmokeGeneric;
import com.maddox.rts.LDRres;
import com.maddox.rts.ObjIO;
import com.maddox.rts.Property;
import com.maddox.rts.RTSConf;
import com.maddox.rts.SectFile;
import com.maddox.rts.Spawn;
import com.maddox.util.NumberTokenizer;

public class PlMisStatic extends Plugin {
    private ArrayList[]        listCountry;
    private HashMap[]          mapCountry;
    protected ArrayList        allActors   = new ArrayList();
    Type[]                     type;
    private Point3d            p3d         = new Point3d();
    private Point2d            p2d         = new Point2d();
    private Point2d            p2dt        = new Point2d();
    private Point3d            p           = new Point3d();
    private Orient             o           = new Orient();
    private ActorSpawnArg      spawnArg    = new ActorSpawnArg();
    private PlMission          pluginMission;
    private int                startComboBox1;
    ViewItem[]                 viewType;
    HashMap                    viewClasses = new HashMap();
    private String[]           _actorInfo  = new String[2];
    GWindowTabDialogClient.Tab tabActor;
    GWindowLabel               wName;
    GWindowComboControl        wArmy;
    GWindowLabel               wLTimeOutH;
    GWindowEditControl         wTimeOutH;
    GWindowLabel               wLTimeOutM;
    GWindowEditControl         wTimeOutM;
    GWindowLabel               wLCountry;
    GWindowComboControl        wCountry;
    GWindowLabel               wLSleepM;
    GWindowLabel               wLSleepS;
    GWindowEditControl         wSleepM;
    GWindowEditControl         wRHide;
    GWindowLabel               wL1RHide;
    GWindowLabel               wL2RHide;
    GWindowEditControl         wSleepS;
    GWindowLabel               wLSkill;
    GWindowComboControl        wSkill;
    GWindowLabel               wLSlowfire;
    GWindowEditControl         wSlowfire;
    GWindowLabel               wLRadius;
    GWindowEditControl         wRadius;

    class ViewItem extends GWindowMenuItem {
        int indx;

        public void execute() {
            this.bChecked = !this.bChecked;
            PlMisStatic.this.viewType(this.indx);
        }

        public ViewItem(int i, GWindowMenu gwindowmenu, String string, String string_0_) {
            super(gwindowmenu, string, string_0_);
            this.indx = i;
        }
    }

    static class Type {
        public String name;
        public Item[] item;

        public Type(String string, Item[] items) {
            this.name = string;
            this.item = items;
        }
    }

    static class Item {
        public String     name;
        public Class      clazz;
        public int        army;
        public ActorSpawn spawn;
        public String     country;

        public Item(String string, Class var_class, int i) {
            this.name = string;
            this.clazz = var_class;
            this.army = i;
            if (var_class != null) {
                this.spawn = (ActorSpawn) Spawn.get(var_class.getName());
                String string_1_ = I18N.technic(string);
                if (string_1_.equals(string)) {
                    string_1_ = Property.stringValue(var_class, "i18nName", string_1_);
                    if ("Plane".equals(string_1_)) {
                        Class var_class_2_ = (Class) Property.value(var_class, "airClass", null);
                        if (var_class_2_ != null) {
                            String string_3_ = Property.stringValue(var_class_2_, "keyName", null);
                            if (string_3_ != null) string_1_ = I18N.plane(string_3_);
                        }
                    }
                }
                Property.set(var_class, "i18nName", string_1_);
            }
        }
    }

    static class Country {
        public String name;
        public String i18nName;
    }

    private void initCountry() {
        if (this.listCountry == null) {
            this.listCountry = new ArrayList[3];
            this.mapCountry = new HashMap[3];
            for (int i = 0; i < 3; i++) {
                this.listCountry[i] = new ArrayList();
                this.mapCountry[i] = new HashMap();
            }
            ResourceBundle resourcebundle;
            try {
                resourcebundle = ResourceBundle.getBundle("i18n/country", RTSConf.cur.locale, LDRres.loader());
            } catch (Exception exception) {
                resourcebundle = null;
            }
            HashMap hashmap = new HashMap();
            List list = Regiment.getAll();
            for (int i = 0; i < list.size(); i++) {
                Regiment regiment = (Regiment) list.get(i);
                if (!hashmap.containsKey(regiment.country())) {
                    int i_4_ = regiment.getArmy();
                    if (i_4_ >= 0 && i_4_ <= 2) {
                        hashmap.put(regiment.country(), null);
                        Country country = new Country();
                        country.name = regiment.country();

                        // TODO: Added by |ZUTI|: country name check
                        if (country.name != null) if (resourcebundle != null) // TODO: Added by |ZUTI|: added try/catch block
                            try {
                                country.i18nName = resourcebundle.getString(country.name);
                            } catch (Exception ex) {
                                country.i18nName = country.name;
                            }
                        else country.i18nName = country.name;

                        this.listCountry[i_4_].add(country);
                        this.mapCountry[i_4_].put(country.name, new Integer(this.listCountry[i_4_].size() - 1));
                    }
                }
            }
        }
    }

    public void mapLoaded() {
        this.deleteAll();
    }

    public void deleteAll() {
        for (int i = 0; i < this.allActors.size(); i++) {
            Actor actor = (Actor) this.allActors.get(i);
            if (Actor.isValid(actor)) actor.destroy();
        }
        this.allActors.clear();
    }

    public void delete(Actor actor) {
        this.allActors.remove(actor);
        actor.destroy();
    }

    public void renderMap2D() {
        if (!builder.isFreeView()) {
            Actor actor = builder.selectedActor();
            Render.prepareStates();
            for (int i = 0; i < this.allActors.size(); i++) {
                Actor actor_5_ = (Actor) this.allActors.get(i);
                if (Actor.isValid(actor_5_) && actor_5_.icon != null && this.viewClasses.containsKey(actor_5_.getClass())) {
                    this.p3d.set(actor_5_.pos.getAbsPoint());
                    if (actor_5_ instanceof SmokeGeneric) this.p3d.z = Engine.land().HQ(this.p3d.x, this.p3d.y);
                    if (builder.project2d(this.p3d, this.p2d)) {
                        int i_6_ = actor_5_.getArmy();
                        boolean bool = builder.conf.bShowArmy[i_6_];
                        if (bool) {
                            int i_7_;
                            if (builder.isMiltiSelected(actor_5_)) i_7_ = Builder.colorMultiSelected(Army.color(actor_5_.getArmy()));
                            else if (actor_5_ == actor) i_7_ = Builder.colorSelected();
                            else i_7_ = Army.color(actor_5_.getArmy());
                            IconDraw.setColor(i_7_);
                            IconDraw.render(actor_5_, this.p2d.x, this.p2d.y);
                            if (Plugin.builder.conf.bShowName) {
                                String string = Property.stringValue(actor_5_.getClass(), "i18nName", "");
                                Plugin.builder.smallFont.output(i_7_, (int) this.p2d.x + IconDraw.scrSizeX() / 2 + 2, (int) this.p2d.y + Plugin.builder.smallFont.height() - Plugin.builder.smallFont.descender() - IconDraw.scrSizeY() / 2 - 2, 0.0F, string);
                            }
                            if (actor_5_ instanceof DZGeneric) {
                                DZGeneric dzgeneric = (DZGeneric)actor_5_;
                                this.p3d.x += dzgeneric.getDzRadius();
                                if (Plugin.builder.project2d(this.p3d, this.p2dt)) {
                                    double d = this.p2dt.x - this.p2d.x;
                                    if (d > Plugin.builder.conf.iconSize / 3) this.drawCircle(this.p2d.x, this.p2d.y, d, i_7_);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void drawCircle(double x, double y, double radius, int armyColor) {
        int numElements = 48;
        double angleSegment = Math.PI * 2D / (double)numElements;
        double angleCurrent = 0.0;
        for (int elementIndex = 0; elementIndex < numElements; elementIndex++) {
            _circleXYZ[elementIndex * 3 + 0] = (float) (x + radius * Math.cos(angleCurrent));
            _circleXYZ[elementIndex * 3 + 1] = (float) (y + radius * Math.sin(angleCurrent));
            _circleXYZ[elementIndex * 3 + 2] = 0.0F;
            angleCurrent += angleSegment;
        }
        Render.drawBeginLines(-1);
        Render.drawLines(_circleXYZ, numElements, 1.0F, armyColor, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 4);
        Render.drawEnd();
    }
    private static float[]     _circleXYZ = new float[144];

    public void load(SectFile sectfile) {
        this.initCountry();
        int i = sectfile.sectionIndex("Stationary");
        if (i >= 0) {
            int i_8_ = sectfile.vars(i);
            for (int i_9_ = 0; i_9_ < i_8_; i_9_++) {
                NumberTokenizer numbertokenizer = new NumberTokenizer(sectfile.line(i, i_9_));
                this.insert(null, numbertokenizer.next(""), numbertokenizer.next(0), numbertokenizer.next(0.0), numbertokenizer.next(0.0), numbertokenizer.next(0.0F), numbertokenizer.next(0.0F), numbertokenizer.next((String) null),
                        numbertokenizer.next((String) null), numbertokenizer.next((String) null));
            }
        }
        i = sectfile.sectionIndex("NStationary");
        if (i >= 0) {
            int varsCount = sectfile.vars(i);
            for (int i_11_ = 0; i_11_ < varsCount; i_11_++) {
                NumberTokenizer numbertokenizer = new NumberTokenizer(sectfile.line(i, i_11_));
                this.insert(numbertokenizer.next(""), numbertokenizer.next(""), numbertokenizer.next(0), numbertokenizer.next(0.0), numbertokenizer.next(0.0), numbertokenizer.next(0.0F), numbertokenizer.next(0.0F), numbertokenizer.next((String) null),
                        numbertokenizer.next((String) null), numbertokenizer.next((String) null));
            }

            // TODO: Added by |ZUTI|: Load spawn place place holders as designated stationary aircraft
            // -------------------------------------------------------------
            List zutiSpawnPlacePlaceHolders = ZutiSupportMethods_Builder.getPreparedSpawnPlacePlaceHolders(sectfile, varsCount);
            for (int x = 0; x < zutiSpawnPlacePlaceHolders.size(); x++) {
                NumberTokenizer numbertokenizer = new NumberTokenizer((String) zutiSpawnPlacePlaceHolders.get(x));
                this.insert(numbertokenizer.next(""), numbertokenizer.next(""), numbertokenizer.next(0), numbertokenizer.next(0.0), numbertokenizer.next(0.0), numbertokenizer.next(0.0F), numbertokenizer.next(0.0F), numbertokenizer.next((String) null),
                        numbertokenizer.next((String) null), numbertokenizer.next((String) null));
            }
            // -------------------------------------------------------------
        }
        Engine.drawEnv().staticTrimToSize();
    }

    public boolean save(SectFile sectfile) {
        Orient orient = new Orient();
        int i = sectfile.sectionAdd("NStationary");
        for (int i_12_ = 0; i_12_ < this.allActors.size(); i_12_++) {
            Actor actor = (Actor) this.allActors.get(i_12_);
            if (Actor.isValid(actor) && Property.containsValue(actor, "builderSpawn")) {
                Point3d point3d = actor.pos.getAbsPoint();
                Orient orient_13_ = actor.pos.getAbsOrient();
                orient.set(orient_13_);
                orient.wrap360();
                float f = Property.floatValue(actor, "timeout", 0.0F);
                if (actor instanceof PlaneGeneric) {
                    // TODO: Added by |ZUTI|: save all but those that are serving as spawn place holders
                    // -----------------------------------------------------
                    if (actor instanceof Spawnplaceholder || actor instanceof SpawnplaceMarker)
                        // System.out.println("Skipping one for saving as NStationary: " + actor.toString());
                        continue;

                    String string = ((PlaneGeneric) actor).country;
                    sectfile.lineAdd(i, actor.name(), ObjIO.classGetName(actor.getClass()) + " " + actor.getArmy() + " " + this.formatPos(point3d.x, point3d.y, orient.azimut()) + " " + f + " " + string);
                } else if (actor instanceof ShipGeneric || actor instanceof BigshipGeneric) {
                    int i_14_ = Property.intValue(actor, "sleep", 0);
                    int i_15_ = Property.intValue(actor, "skill", 2);
                    float f_16_ = Property.floatValue(actor, "slowfire", 1.0F);
                    sectfile.lineAdd(i, actor.name(), ObjIO.classGetName(actor.getClass()) + " " + actor.getArmy() + " " + this.formatPos(point3d.x, point3d.y, orient.azimut()) + " " + f + " " + i_14_ + " " + i_15_ + " " + f_16_);
                } else if (actor instanceof ArtilleryGeneric) {
                    int i_17_ = Property.intValue(actor, "radius_hide", 0);
                    sectfile.lineAdd(i, actor.name(), ObjIO.classGetName(actor.getClass()) + " " + actor.getArmy() + " " + this.formatPos(point3d.x, point3d.y, orient.azimut()) + " " + f + " " + i_17_);
                } else if (actor instanceof SmokeGeneric) {
                    sectfile.lineAdd(i, actor.name(), ObjIO.classGetName(actor.getClass()) + " " + actor.getArmy() + " " + this.formatPos(point3d.x, point3d.y, orient.azimut()) + " " + this.formatValue(point3d.z));
                } else if (actor instanceof DZGeneric) {
                    sectfile.lineAdd(i, actor.name(), ObjIO.classGetName(actor.getClass()) + " " + actor.getArmy() + " " + this.formatPos(point3d.x, point3d.y, orient.azimut()) + " " + Property.intValue(actor, "radius", 1000));
                }
                else sectfile.lineAdd(i, actor.name(), ObjIO.classGetName(actor.getClass()) + " " + actor.getArmy() + " " + this.formatPos(point3d.x, point3d.y, orient.azimut()) + " " + f);
            }
        }
        return true;
    }

    private String formatPos(double d, double d_18_, float f) {
        return this.formatValue(d) + " " + this.formatValue(d_18_) + " " + this.formatValue(f);
    }

    private String formatValue(double d) {
        boolean bool = d < 0.0;
        if (bool) d = -d;
        double d_19_ = d + 0.0050 - (int) d;
        if (d_19_ >= 0.1) return (bool ? "-" : "") + (int) d + "." + (int) (d_19_ * 100.0);
        return (bool ? "-" : "") + (int) d + ".0" + (int) (d_19_ * 100.0);
    }

    private void makeName(Actor actor, String string) {
        if (string != null && Actor.getByName(string) == null) actor.setName(string);
        else {
            int i = 0;
            for (;;) {
                string = i + "_Static";
                if (Actor.getByName(string) == null) break;
                i++;
            }
            actor.setName(string);
        }
    }

    // TODO: Altered by |ZUTI| from private to public
    public Actor insert(String string, String string_20_, int i, double d, double d_21_, float f, float f_22_, String string_23_, String string_24_, String string_25_) {
        Class var_class;
        try {
            var_class = ObjIO.classForName(string_20_);
        } catch (Exception exception) {
            builder.tipErr("MissionLoad: class '" + string_20_ + "' not found");
            return null;
        }
        ActorSpawn actorspawn = (ActorSpawn) Spawn.get(var_class.getName(), false);
        if (actorspawn == null) {
            builder.tipErr("MissionLoad: ActorSpawn for '" + string_20_ + "' not found");
            return null;
        }
        this.spawnArg.clear();
        if (string != null) {
            if ("NONAME".equals(string)) string = null;
            if (Actor.getByName(string) != null) string = null;
        }
        if (i < 0 && i >= Builder.armyAmount()) {
            builder.tipErr("MissionLoad: Wrong actor army '" + i + "'");
            return null;
        }
        this.spawnArg.army = i;
        this.spawnArg.armyExist = true;
        this.spawnArg.country = string_23_;
        Chief.new_DELAY_WAKEUP = 0.0F;
        ArtilleryGeneric.new_RADIUS_HIDE = 0.0F;
        if (string_23_ != null) try {
            Chief.new_DELAY_WAKEUP = Integer.parseInt(string_23_);
            ArtilleryGeneric.new_RADIUS_HIDE = Chief.new_DELAY_WAKEUP;
        } catch (Exception exception) {}
        Chief.new_SKILL_IDX = 2;
        if (string_24_ != null) {
            try {
                Chief.new_SKILL_IDX = Integer.parseInt(string_24_);
            } catch (Exception exception) {}
            if (Chief.new_SKILL_IDX < 0 || Chief.new_SKILL_IDX > 3) {
                builder.tipErr("MissionLoad: Wrong actor skill '" + Chief.new_SKILL_IDX + "'");
                return null;
            }
        }
        Chief.new_SLOWFIRE_K = 1.0F;
        if (string_25_ != null) {
            try {
                Chief.new_SLOWFIRE_K = Float.parseFloat(string_25_);
            } catch (Exception exception) {}
            if (Chief.new_SLOWFIRE_K < 0.5F || Chief.new_SLOWFIRE_K > 100.0F) {
                builder.tipErr("MissionLoad: Wrong actor slowfire '" + Chief.new_SLOWFIRE_K + "'");
                return null;
            }
        }
        this.p.set(d, d_21_, 0.0);
        this.spawnArg.point = this.p;
        this.o.set(f, 0.0F, 0.0F);
        this.spawnArg.orient = this.o;
        Actor actor;
        try {
            Actor actor_26_ = actorspawn.actorSpawn(this.spawnArg);
            if (actor_26_ instanceof SirenGeneric) actor_26_.setArmy(i);
            if (actor_26_ instanceof SmokeGeneric) {
                this.p.z = f_22_;
                actor_26_.pos.setAbs(this.p);
                actor_26_.pos.reset();
            }
            builder.align(actor_26_);
            Property.set(actor_26_, "builderSpawn", "");
            Property.set(actor_26_, "builderPlugin", this);
            if (!actor_26_.isRealTimeFlag()) actor_26_.interpCancelAll();
            this.makeName(actor_26_, string);
            this.allActors.add(actor_26_);

            if (actor_26_ instanceof RwyTransp || actor_26_ instanceof RwyTranspWide || actor_26_ instanceof RwyTranspSqr) ((BigshipGeneric) actor_26_).showTransparentRunwayRed();
            if (actor_26_ instanceof LorenzBlindLandingBeacon) ((LorenzBlindLandingBeacon) actor_26_).showGuideArrows();

            if (actor_26_ instanceof ArtilleryGeneric) {
                Property.set(actor_26_, "timeout", f_22_);
                Property.set(actor_26_, "radius_hide", (int) ArtilleryGeneric.new_RADIUS_HIDE);
            }
            if (actor_26_ instanceof ShipGeneric || actor_26_ instanceof BigshipGeneric) {
                Property.set(actor_26_, "sleep", (int) Chief.new_DELAY_WAKEUP);
                Property.set(actor_26_, "skill", Chief.new_SKILL_IDX);
                Property.set(actor_26_, "slowfire", Chief.new_SLOWFIRE_K);
            }
            if (actor_26_ instanceof DZGeneric) {
                int radius = (int)f_22_;
                if (radius == 0) radius = 1000; 
                Property.set(actor_26_, "radius", radius);
                System.out.println("DZ Radius = " + radius);
            }
            actor = actor_26_;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return null;
        }
        return actor;
    }

    private Actor insert(ActorSpawn actorspawn, Loc loc, int i, boolean bool, String string) {
        this.spawnArg.clear();
        this.spawnArg.point = loc.getPoint();
        this.spawnArg.orient = loc.getOrient();
        this.spawnArg.army = i;
        this.spawnArg.armyExist = true;
        this.spawnArg.country = string;
        Actor actor;
        try {
            Actor actor_27_ = actorspawn.actorSpawn(this.spawnArg);

            // TODO: Added by |ZUTI|: Check if it is spawn place holder and if it is placed inside stand alone bp
            // ----------------------------------------------------------------
            if (actor_27_ instanceof Spawnplaceholder && !ZutiSupportMethods_Builder.isOnStandAloneBornPlace(loc.getX(), loc.getY())) {
                new GWindowMessageBox(builder.clientWindow.root, 20.0F, true, i18n("mds.spawnPlaceHolderInfo"), i18n("mds.spawnPlaceHolderNotPossible"), 3, 0.0F);
                actor_27_.destroy();
                actor_27_ = null;
                return null;
            }
            // ----------------------------------------------------------------

            if (actor_27_ instanceof RwyTransp || actor_27_ instanceof RwyTranspWide || actor_27_ instanceof RwyTranspSqr) ((BigshipGeneric) actor_27_).showTransparentRunwayRed();
            if (actor_27_ instanceof LorenzBlindLandingBeacon) ((LorenzBlindLandingBeacon) actor_27_).showGuideArrows();

            if (actor_27_ instanceof SirenGeneric) actor_27_.setArmy(i);
            builder.align(actor_27_);
            Property.set(actor_27_, "builderSpawn", "");
            Property.set(actor_27_, "builderPlugin", this);
            if (!actor_27_.isRealTimeFlag()) actor_27_.interpCancelAll();
            if (actor_27_ instanceof ArtilleryGeneric) {
                Property.set(actor_27_, "timeout", 0.0F);
                Property.set(actor_27_, "radius_hide", 0);
            }
            if (actor_27_ instanceof ShipGeneric || actor_27_ instanceof BigshipGeneric) {
                Property.set(actor_27_, "sleep", 0);
                Property.set(actor_27_, "skill", 2);
                Property.set(actor_27_, "slowfire", 1.0F);
            }
            this.makeName(actor_27_, null);
            this.allActors.add(actor_27_);
            if (bool) builder.setSelected(actor_27_);
            PlMission.setChanged();
            actor = actor_27_;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return null;
        }
        return actor;
    }

    public void insert(Loc loc, boolean bool) {
        int i = builder.wSelect.comboBox1.getSelected();
        int i_28_ = builder.wSelect.comboBox2.getSelected();
        if (i >= this.startComboBox1 && i < this.startComboBox1 + this.type.length) {
            i -= this.startComboBox1;
            if (i_28_ >= 0 && i_28_ < this.type[i].item.length) {
                ActorSpawn actorspawn = this.type[i].item[i_28_].spawn;
                this.insert(actorspawn, loc, this.type[i].item[i_28_].army, bool, this.type[i].item[i_28_].country);
            }
        }
    }

    public void changeType() {
        int i = builder.wSelect.comboBox1.getSelected() - this.startComboBox1;
        int i_29_ = builder.wSelect.comboBox2.getSelected();
        Actor actor = builder.selectedActor();
        Loc loc = actor.pos.getAbs();
        Actor actor_30_ = this.insert(this.type[i].item[i_29_].spawn, loc, this.type[i].item[i_29_].army, true, this.type[i].item[i_29_].country);
        if (builder.selectedActor() != actor) {
            this.allActors.remove(actor);
            String string = actor.name();
            actor.destroy();
            actor_30_.setName(string);
        }
    }

    public void configure() {
        if (getPlugin("Mission") == null) throw new RuntimeException("PlMisStatic: plugin 'Mission' not found");
        this.pluginMission = (PlMission) getPlugin("Mission");
        if (this.sectFile == null) throw new RuntimeException("PlMisStatic: field 'sectFile' not defined");
        SectFile sectfile = new SectFile(this.sectFile, 0);
        int i = sectfile.sections();
        if (i <= 0) throw new RuntimeException("PlMisStatic: file '" + this.sectFile + "' is empty");
        this.type = new Type[i];
        for (int i_31_ = 0; i_31_ < i; i_31_++) {
            String string = sectfile.sectionName(i_31_);
            int i_32_ = sectfile.vars(i_31_);
            Item[] items = new Item[i_32_];
            for (int i_33_ = 0; i_33_ < i_32_; i_33_++) {
                String string_34_ = sectfile.var(i_31_, i_33_);
                NumberTokenizer numbertokenizer = new NumberTokenizer(sectfile.value(i_31_, i_33_));
                String string_35_ = numbertokenizer.next((String) null);
                int i_36_ = numbertokenizer.next(0, 0, Builder.armyAmount() - 1);
                int i_37_ = string_35_.indexOf(' ');
                if (i_37_ > 0) string_35_ = string_35_.substring(0, i_37_);
                Class var_class;
                try {
                    var_class = ObjIO.classForName(string_35_);
                } catch (Exception exception) {
                    throw new RuntimeException("PlMisStatic: class '" + string_35_ + "' not found");
                }
                int i_38_ = string_35_.lastIndexOf('$');
                if (i_38_ >= 0) {
                    String string_39_ = string_35_.substring(0, i_38_);
                    try {
                        ObjIO.classForName(string_39_);
                    } catch (Exception exception) {
                        throw new RuntimeException("PlMisStatic: Outer class '" + string_39_ + "' not found");
                    }
                }
                items[i_33_] = new Item(string_34_, var_class, i_36_);
            }
            this.type[i_31_] = new Type(string, items);
        }
    }

    void viewUpdate() {
        for (int i = 0; i < this.allActors.size(); i++) {
            Actor actor = (Actor) this.allActors.get(i);
            if (Actor.isValid(actor) && Property.containsValue(actor, "builderSpawn")) actor.drawing(this.viewClasses.containsKey(actor.getClass()));
        }
        if (Actor.isValid(builder.selectedActor()) && !builder.selectedActor().isDrawing()) builder.setSelected(null);
        if (!builder.isFreeView()) builder.repaint();
    }

    void viewType(int i, boolean bool) {
        int i_40_ = this.type[i].item.length;
        for (int i_41_ = 0; i_41_ < i_40_; i_41_++)
            if (bool) this.viewClasses.put(this.type[i].item[i_41_].clazz, this.type[i].item[i_41_]);
            else this.viewClasses.remove(this.type[i].item[i_41_].clazz);
        this.viewUpdate();
    }

    void viewType(int i) {
        this.viewType(i, this.viewType[i].bChecked);
    }

    public void viewTypeAll(boolean bool) {
        for (int i = 0; i < this.type.length; i++)
            if (this.viewType[i].bChecked != bool) {
                this.viewType[i].bChecked = bool;
                this.viewType(i, bool);
            }
    }

    private void fillComboBox1() {
        this.startComboBox1 = builder.wSelect.comboBox1.size();
        for (int i = 0; i < this.type.length; i++)
            builder.wSelect.comboBox1.add(I18N.technic(this.type[i].name));
        if (this.startComboBox1 == 0) builder.wSelect.comboBox1.setSelected(0, true, false);
    }

    private void fillComboBox2(int i, int i_42_) {
        if (i >= this.startComboBox1 && i < this.startComboBox1 + this.type.length) {
            if (builder.wSelect.curFilledType != i) {
                builder.wSelect.curFilledType = i;
                builder.wSelect.comboBox2.clear(false);
                for (int i_43_ = 0; i_43_ < this.type[i - this.startComboBox1].item.length; i_43_++)
                    builder.wSelect.comboBox2.add(Property.stringValue(this.type[i - this.startComboBox1].item[i_43_].clazz, "i18nName", ""));
                builder.wSelect.comboBox1.setSelected(i, true, false);
            }
            builder.wSelect.comboBox2.setSelected(i_42_, true, false);
            this.fillComboBox2Render(i, i_42_);
        }
    }

    private void fillComboBox2Render(int i, int i_44_) {
        try {
            Class var_class = this.type[i - this.startComboBox1].item[i_44_].clazz;
            if (PlaneGeneric.class.isAssignableFrom(var_class)) {
                Class var_class_45_ = (Class) Property.value(var_class, "airClass", null);
                int i_46_ = this.type[i - this.startComboBox1].item[i_44_].army;
                String string = null;
                if (Actor.isValid(builder.selectedActor())) {
                    i_46_ = builder.selectedActor().getArmy();
                    string = ((PlaneGeneric) builder.selectedActor()).country;
                    this.type[i - this.startComboBox1].item[i_44_].country = string;
                    this.type[i - this.startComboBox1].item[i_44_].army = i_46_;
                }
                Regiment regiment = Regiment.findFirst(string, i_46_);
                String string_47_ = Aircraft.getPropertyMesh(var_class_45_, regiment.country());
                builder.wSelect.setMesh(string_47_, false);
                if (builder.wSelect.getHierMesh() != null) {
                    PaintScheme paintscheme = Aircraft.getPropertyPaintScheme(var_class_45_, regiment.country());
                    paintscheme.prepareNum(var_class_45_, builder.wSelect.getHierMesh(), regiment, (int) (Math.random() * 3.0), (int) (Math.random() * 3.0), (int) (Math.random() * 98.0 + 1.0));
                }
            } else {
                String string = Property.stringValue(var_class, "meshName", null);
                if (string == null) {
                    Method method = var_class.getMethod("getMeshNameForEditor", null);
                    string = (String) method.invoke(var_class, null);
                }
                builder.wSelect.setMesh(string, true);
            }
        } catch (RuntimeException e) {
            builder.wSelect.setMesh(null, true);
        } catch (Exception exception) {
            builder.wSelect.setMesh(null, true);
        }
    }

    public String[] actorInfo(Actor actor) {
        Class var_class = actor.getClass();
        for (int i = 0; i < this.type.length; i++)
            for (int i_48_ = 0; i_48_ < this.type[i].item.length; i_48_++)
                if (var_class == this.type[i].item[i_48_].clazz) {
                    this._actorInfo[0] = I18N.technic(this.type[i].name) + "." + Property.stringValue(this.type[i].item[i_48_].clazz, "i18nName", "");
                    float f = Property.floatValue(actor, "timeout", 0.0F);
                    if (f > 0.0F) this._actorInfo[1] = timeSecToString(f * 60.0F + (int) (World.getTimeofDay() * 60.0F * 60.0F));
                    else this._actorInfo[1] = null;
                    return this._actorInfo;
                }
        return null;
    }

    public void syncSelector() {
        Actor actor = builder.selectedActor();
        Class var_class = actor.getClass();
        for (int i = 0; i < this.type.length; i++)
            for (int i_49_ = 0; i_49_ < this.type[i].item.length; i_49_++)
                if (var_class == this.type[i].item[i_49_].clazz) {
                    this.fillComboBox2(i + this.startComboBox1, i_49_);
                    builder.wSelect.tabsClient.addTab(1, this.tabActor);
                    this.wName.cap.set(Property.stringValue(this.type[i].item[i_49_].clazz, "i18nName", ""));
                    this.wArmy.setSelected(actor.getArmy(), true, false);
                    if (actor instanceof ArtilleryGeneric) {
                        float f = Property.floatValue(actor, "timeout", 0.0F);
                        this.wTimeOutH.setValue("" + (int) (f / 60.0F % 24.0F), false);
                        this.wTimeOutM.setValue("" + (int) (f % 60.0F), false);
                        this.wLTimeOutH.showWindow();
                        this.wTimeOutH.showWindow();
                        this.wLTimeOutM.showWindow();
                        this.wTimeOutM.showWindow();
                        int i_50_ = Property.intValue(actor, "radius_hide", 0);
                        this.wL1RHide.showWindow();
                        this.wL2RHide.showWindow();
                        this.wRHide.showWindow();
                        this.wRHide.setValue("" + i_50_, false);
                    } else {
                        this.wLTimeOutH.hideWindow();
                        this.wTimeOutH.hideWindow();
                        this.wLTimeOutM.hideWindow();
                        this.wTimeOutM.hideWindow();
                        this.wL1RHide.hideWindow();
                        this.wL2RHide.hideWindow();
                        this.wRHide.hideWindow();
                    }
                    if (PlaneGeneric.class.isAssignableFrom(var_class)) {
                        PlaneGeneric planegeneric = (PlaneGeneric) actor;
                        this.fillCountry(actor.getArmy(), planegeneric.country);
                        this.wLCountry.showWindow();
                        this.wCountry.showWindow();
                    } else {
                        this.wLCountry.hideWindow();
                        this.wCountry.hideWindow();
                    }
                    if (actor instanceof ShipGeneric || actor instanceof BigshipGeneric) {
                        this.wLSleepM.showWindow();
                        this.wSleepM.showWindow();
                        this.wLSleepS.showWindow();
                        this.wSleepS.showWindow();
                        int i_51_ = Property.intValue(actor, "sleep", 0);
                        this.wSleepM.setValue("" + i_51_ / 60 % 99, false);
                        this.wSleepS.setValue("" + i_51_ % 60, false);
                        this.wLSkill.showWindow();
                        this.wSkill.showWindow();
                        int i_52_ = Property.intValue(actor, "skill", 2);
                        this.wSkill.setSelected(i_52_, true, false);
                        this.wLSlowfire.showWindow();
                        this.wSlowfire.showWindow();
                        float f = Property.floatValue(actor, "slowfire", 1.0F);
                        this.wSlowfire.setValue("" + f);
                    } else {
                        this.wLSleepM.hideWindow();
                        this.wSleepM.hideWindow();
                        this.wLSleepS.hideWindow();
                        this.wSleepS.hideWindow();
                        this.wLSkill.hideWindow();
                        this.wSkill.hideWindow();
                        this.wLSlowfire.hideWindow();
                        this.wSlowfire.hideWindow();
                    }
                    if (actor instanceof DZGeneric) {
                        this.wLRadius.showWindow();
                        this.wRadius.showWindow();
                        this.wRadius.setValue("" + Property.intValue(actor, "radius", 1000));
                    } else {
                        this.wLRadius.hideWindow();
                        this.wRadius.hideWindow();
                    }
                    return;
                }
    }

    private String fillCountry(int i, String string) {
        this.initCountry();
        this.wCountry.clear(false);
        ArrayList arraylist = this.listCountry[i];
        for (int i_53_ = 0; i_53_ < arraylist.size(); i_53_++) {
            Country country = (Country) arraylist.get(i_53_);
            this.wCountry.add(country.i18nName);
        }
        if (string != null && !this.mapCountry[i].containsKey(string)) string = null;
        if (string == null) switch (i) {
            case 0:
                string = "nn";
                break;
            case 1:
                string = "ru";
                break;
            case 2:
                string = "de";
                break;
        }
        Integer integer = (Integer) this.mapCountry[i].get(string);
        this.wCountry.setSelected(integer.intValue(), true, false);
        return string;
    }

    private void controlResized(GWindowDialogClient gwindowdialogclient, GWindow gwindow) {
        if (gwindow != null) gwindow.setSize(gwindowdialogclient.win.dx - gwindow.win.x - gwindowdialogclient.lAF().metric(1.0F), gwindow.win.dy);
    }

    private void editResized(GWindowDialogClient gwindowdialogclient) {
        this.controlResized(gwindowdialogclient, this.wName);
        this.controlResized(gwindowdialogclient, this.wArmy);
        this.controlResized(gwindowdialogclient, this.wCountry);
    }

    public void createGUI() {
        this.fillComboBox1();
        this.fillComboBox2(0, 0);
        builder.wSelect.comboBox1.addNotifyListener(new GNotifyListener() {
            public boolean notify(GWindow gwindow, int i, int i_55_) {
                int i_56_ = Plugin.builder.wSelect.comboBox1.getSelected();
                if (i_56_ >= 0 && i == 2) PlMisStatic.this.fillComboBox2(i_56_, 0);
                return false;
            }
        });
        builder.wSelect.comboBox2.addNotifyListener(new GNotifyListener() {
            public boolean notify(GWindow gwindow, int i, int i_58_) {
                if (i != 2) return false;
                int i_59_ = Plugin.builder.wSelect.comboBox1.getSelected();
                if (i_59_ < PlMisStatic.this.startComboBox1 || i_59_ >= PlMisStatic.this.startComboBox1 + PlMisStatic.this.type.length) return false;
                int i_60_ = Plugin.builder.wSelect.comboBox2.getSelected();
                PlMisStatic.this.fillComboBox2Render(i_59_, i_60_);
                return false;
            }
        });
        int i;
        for (i = builder.mDisplayFilter.subMenu.size() - 1; i >= 0 && this.pluginMission.viewBridge != builder.mDisplayFilter.subMenu.getItem(i); i--);
        if (--i >= 0) {
            int i_61_ = i;
            i = this.type.length - 1;
            this.viewType = new ViewItem[this.type.length];
            for (/**/; i >= 0; i--) {
                ViewItem viewitem;
                if ("de".equals(RTSConf.cur.locale.getLanguage())) viewitem = (ViewItem) builder.mDisplayFilter.subMenu.addItem(i_61_, new ViewItem(i, builder.mDisplayFilter.subMenu, I18N.technic(this.type[i].name) + " " + i18n("show"), null));
                else viewitem = (ViewItem) builder.mDisplayFilter.subMenu.addItem(i_61_, new ViewItem(i, builder.mDisplayFilter.subMenu, i18n("show") + " " + I18N.technic(this.type[i].name), null));
                viewitem.bChecked = true;
                this.viewType[i] = viewitem;
                this.viewType(i, true);
            }
        }
        GWindowDialogClient gwindowdialogclient = (GWindowDialogClient) builder.wSelect.tabsClient.create(new GWindowDialogClient() {
            public void resized() {
                super.resized();
                PlMisStatic.this.editResized(this);
            }
        });
        this.tabActor = builder.wSelect.tabsClient.createTab(i18n("StaticActor"), gwindowdialogclient);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 1.0F, 7.0F, 1.3F, i18n("Name"), null));
        gwindowdialogclient.addLabel(this.wName = new GWindowLabel(gwindowdialogclient, 9.0F, 1.0F, 7.0F, 1.3F, "", null));
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 3.0F, 7.0F, 1.3F, i18n("Army"), null));
        gwindowdialogclient.addControl(this.wArmy = new GWindowComboControl(gwindowdialogclient, 9.0F, 3.0F, 7.0F) {
            public void afterCreated() {
                super.afterCreated();
                this.setEditable(false);
                for (int i_66_ = 0; i_66_ < Builder.armyAmount(); i_66_++)
                    this.add(I18N.army(Army.name(i_66_)));
            }

            public boolean notify(int i_67_, int i_68_) {
                if (i_67_ != 2) return false;
                Actor actor = Plugin.builder.selectedActor();
                int i_69_ = this.getSelected();
                actor.setArmy(i_69_);
                PlMission.setChanged();
                PlMission.checkShowCurrentArmy();
                Class var_class = actor.getClass();
                if (PlaneGeneric.class.isAssignableFrom(var_class)) {
                    PlaneGeneric planegeneric = (PlaneGeneric) actor;
                    String string = PlMisStatic.this.fillCountry(i_69_, planegeneric.country);
                    planegeneric.country = string;
                    planegeneric.activateMesh(true);
                    for (int i_70_ = 0; i_70_ < PlMisStatic.this.type.length; i_70_++)
                        for (int i_71_ = 0; i_71_ < PlMisStatic.this.type[i_70_].item.length; i_71_++)
                            if (var_class == PlMisStatic.this.type[i_70_].item[i_71_].clazz) {
                                PlMisStatic.this.fillComboBox2Render(i_70_ + PlMisStatic.this.startComboBox1, i_71_);
                                break;
                            }
                }
                return false;
            }
        });
        gwindowdialogclient.addLabel(this.wLCountry = new GWindowLabel(gwindowdialogclient, 1.0F, 5.0F, 7.0F, 1.3F, I18N.gui("neta.Country"), null));
        gwindowdialogclient.addControl(this.wCountry = new GWindowComboControl(gwindowdialogclient, 9.0F, 5.0F, 7.0F) {
            public void afterCreated() {
                super.afterCreated();
                this.setEditable(false);
            }

            public boolean notify(int i_75_, int i_76_) {
                if (i_75_ != 2) return false;
                Actor actor = Plugin.builder.selectedActor();
                Class var_class = actor.getClass();
                if (!PlaneGeneric.class.isAssignableFrom(var_class)) return false;
                int i_77_ = this.getSelected();
                Country country = (Country) PlMisStatic.this.listCountry[actor.getArmy()].get(i_77_);
                PlaneGeneric planegeneric = (PlaneGeneric) actor;
                planegeneric.country = country.name;
                planegeneric.activateMesh(true);
                for (int i_78_ = 0; i_78_ < PlMisStatic.this.type.length; i_78_++)
                    for (int i_79_ = 0; i_79_ < PlMisStatic.this.type[i_78_].item.length; i_79_++)
                        if (var_class == PlMisStatic.this.type[i_78_].item[i_79_].clazz) {
                            PlMisStatic.this.fillComboBox2Render(i_78_ + PlMisStatic.this.startComboBox1, i_79_);
                            break;
                        }
                return false;
            }
        });
        GWindowDialogClient gwindowdialogclient_80_ = gwindowdialogclient;
        gwindowdialogclient_80_.addLabel(this.wLTimeOutH = new GWindowLabel(gwindowdialogclient_80_, 1.0F, 5.0F, 7.0F, 1.3F, i18n("TimeOut"), null));
        gwindowdialogclient_80_.addControl(this.wTimeOutH = new GWindowEditControl(gwindowdialogclient_80_, 9.0F, 5.0F, 2.0F, 1.3F, "") {
            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int i_85_, int i_86_) {
                if (i_85_ != 2) return false;
                PlMisStatic.this.getTimeOut();
                return false;
            }
        });
        gwindowdialogclient_80_.addLabel(this.wLTimeOutM = new GWindowLabel(gwindowdialogclient_80_, 11.2F, 5.0F, 1.0F, 1.3F, ":", null));
        gwindowdialogclient_80_.addControl(this.wTimeOutM = new GWindowEditControl(gwindowdialogclient_80_, 11.5F, 5.0F, 2.0F, 1.3F, "") {
            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int i_91_, int i_92_) {
                if (i_91_ != 2) return false;
                PlMisStatic.this.getTimeOut();
                return false;
            }
        });
        gwindowdialogclient_80_.addLabel(this.wL1RHide = new GWindowLabel(gwindowdialogclient_80_, 1.0F, 7.0F, 7.0F, 1.3F, i18n("RHide"), null));
        gwindowdialogclient_80_.addLabel(this.wL2RHide = new GWindowLabel(gwindowdialogclient_80_, 14.0F, 7.0F, 4.0F, 1.3F, i18n("[M]"), null));
        gwindowdialogclient_80_.addControl(this.wRHide = new GWindowEditControl(gwindowdialogclient_80_, 9.0F, 7.0F, 4.0F, 1.3F, "") {
            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int i_97_, int i_98_) {
                if (i_97_ != 2) return false;
                Actor actor = Plugin.builder.selectedActor();
                int i_99_ = Property.intValue(actor, "radius_hide", 0);
                String string = this.getValue();
                try {
                    i_99_ = (int) Double.parseDouble(string);
                    if (i_99_ < 0) {
                        i_99_ = 0;
                        this.setValue("" + i_99_, false);
                    }
                } catch (Exception exception) {
                    this.setValue("" + i_99_, false);
                    return false;
                }
                Property.set(actor, "radius_hide", i_99_);
                PlMission.setChanged();
                return false;
            }
        });
        gwindowdialogclient.addLabel(this.wLSleepM = new GWindowLabel(gwindowdialogclient, 1.0F, 5.0F, 7.0F, 1.3F, i18n("Sleep"), null));
        gwindowdialogclient.addControl(this.wSleepM = new GWindowEditControl(gwindowdialogclient, 9.0F, 5.0F, 2.0F, 1.3F, "") {
            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int i_104_, int i_105_) {
                if (i_104_ != 2) return false;
                PlMisStatic.this.getSleep();
                return false;
            }
        });
        gwindowdialogclient.addLabel(this.wLSleepS = new GWindowLabel(gwindowdialogclient, 11.2F, 5.0F, 1.0F, 1.3F, ":", null));
        gwindowdialogclient.addControl(this.wSleepS = new GWindowEditControl(gwindowdialogclient, 11.5F, 5.0F, 2.0F, 1.3F, "") {
            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int i_110_, int i_111_) {
                if (i_110_ != 2) return false;
                PlMisStatic.this.getSleep();
                return false;
            }
        });
        gwindowdialogclient.addLabel(this.wLSkill = new GWindowLabel(gwindowdialogclient, 1.0F, 7.0F, 7.0F, 1.3F, i18n("Skill"), null));
        gwindowdialogclient.addControl(this.wSkill = new GWindowComboControl(gwindowdialogclient, 9.0F, 7.0F, 7.0F) {
            public void afterCreated() {
                super.afterCreated();
                this.setEditable(false);
                this.add(Plugin.i18n("Rookie"));
                this.add(Plugin.i18n("Average"));
                this.add(Plugin.i18n("Veteran"));
                this.add(Plugin.i18n("Ace"));
            }

            public boolean notify(int i_115_, int i_116_) {
                if (i_115_ != 2) return false;
                Actor actor = Plugin.builder.selectedActor();
                Property.set(actor, "skill", this.getSelected());
                PlMission.setChanged();
                return false;
            }
        });
        gwindowdialogclient.addLabel(this.wLSlowfire = new GWindowLabel(gwindowdialogclient, 1.0F, 9.0F, 7.0F, 1.3F, i18n("Slowfire"), null));
        gwindowdialogclient.addControl(this.wSlowfire = new GWindowEditControl(gwindowdialogclient, 9.0F, 9.0F, 3.0F, 1.3F, "") {
            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = this.bNumericFloat = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int i_121_, int i_122_) {
                if (i_121_ != 2) return false;
                String string = this.getValue();
                float f = 1.0F;
                try {
                    f = Float.parseFloat(string);
                } catch (Exception exception) {}
                if (f < 0.5F) f = 0.5F;
                if (f > 100.0F) f = 100.0F;
                this.setValue("" + f, false);
                Actor actor = Plugin.builder.selectedActor();
                Property.set(actor, "slowfire", f);
                PlMission.setChanged();
                return false;
            }
        });

        gwindowdialogclient.addLabel(this.wLRadius = new GWindowLabel(gwindowdialogclient, 1.0F, 7.0F, 7.0F, 1.3F, i18n("mds.properties.radius"), null));
        gwindowdialogclient.addControl(this.wRadius = new GWindowEditControl(gwindowdialogclient, 9.0F, 7.0F, 5.0F, 1.3F, "") {
            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bNumericFloat = false;
                this.bDelayedNotify = true;
            }
    
            public boolean notify(int i, int j) {
                if (i != 2) return false;
                String string = this.getValue();
                int k = 1000;
                try {
                    k = Integer.parseInt(string);
                } catch (Exception exception) {}
                if (k < 1000) k = 1000;
                if (k > 100000) k = 100000;
                this.setValue("" + k, false);
                Actor actor = Plugin.builder.selectedActor();
                Property.set(actor, "radius", k);
                PlMission.setChanged();
                return false;
            }
        });
    }

    private void getTimeOut() {
        String string = this.wTimeOutH.getValue();
        double d = 0.0;
        try {
            d = Double.parseDouble(string);
        } catch (Exception exception) {}
        if (d < 0.0) d = 0.0;
        if (d > 12.0) d = 12.0;
        string = this.wTimeOutM.getValue();
        double d_123_ = 0.0;
        try {
            d_123_ = Double.parseDouble(string);
        } catch (Exception exception) {}
        if (d_123_ < 0.0) d_123_ = 0.0;
        if (d_123_ > 60.0) d_123_ = 60.0;
        float f = (float) (d * 60.0 + d_123_);
        Actor actor = builder.selectedActor();
        Property.set(actor, "timeout", f);
        PlMission.setChanged();
    }

    private void getSleep() {
        String string = this.wSleepM.getValue();
        double d = 0.0;
        try {
            d = Double.parseDouble(string);
        } catch (Exception exception) {}
        if (d < 0.0) d = 0.0;
        if (d > 99.0) d = 99.0;
        string = this.wSleepS.getValue();
        double d_124_ = 0.0;
        try {
            d_124_ = Double.parseDouble(string);
        } catch (Exception exception) {}
        if (d_124_ < 0.0) d_124_ = 0.0;
        if (d_124_ > 60.0) d_124_ = 60.0;
        Actor actor = builder.selectedActor();
        Property.set(actor, "sleep", (int) (d * 60.0 + d_124_));
        PlMission.setChanged();
    }

    public String mis_getProperties(Actor actor) {
        Orient orient = new Orient();
        String string = "";
        int i = builder.wSelect.comboBox1.getSelected();
        int i_125_ = builder.wSelect.comboBox2.getSelected();
        if (i < this.startComboBox1 || i >= this.startComboBox1 + this.type.length) return string;
        i -= this.startComboBox1;
        if (i_125_ < 0 || i_125_ >= this.type[i].item.length) return string;
        if (Actor.isValid(actor) && Property.containsValue(actor, "builderSpawn")) {
            Point3d point3d = actor.pos.getAbsPoint();
            Orient orient_126_ = actor.pos.getAbsOrient();
            orient.set(orient_126_);
            orient.wrap360();
            float f = Property.floatValue(actor, "timeout", 0.0F);
            if (actor instanceof PlaneGeneric) {
                String string_127_ = ((PlaneGeneric) actor).country;
                string = " 1_" + actor.name() + " " + ObjIO.classGetName(actor.getClass()) + " " + actor.getArmy() + " " + this.formatPos(point3d.x, point3d.y, orient.azimut()) + " " + f + " " + string_127_;
            } else if (actor instanceof ShipGeneric || actor instanceof BigshipGeneric) {
                int i_128_ = Property.intValue(actor, "sleep", 0);
                int i_129_ = Property.intValue(actor, "skill", 2);
                float f_130_ = Property.floatValue(actor, "slowfire", 1.0F);
                string = " 1_" + actor.name() + " " + ObjIO.classGetName(actor.getClass()) + " " + actor.getArmy() + " " + this.formatPos(point3d.x, point3d.y, orient.azimut()) + " " + f + " " + i_128_ + " " + i_129_ + " " + f_130_;
            } else if (actor instanceof ArtilleryGeneric) {
                int i_131_ = Property.intValue(actor, "radius_hide", 0);
                string = " 1_" + actor.name() + " " + ObjIO.classGetName(actor.getClass()) + " " + actor.getArmy() + " " + this.formatPos(point3d.x, point3d.y, orient.azimut()) + " " + f + " " + i_131_;
            } else if (actor instanceof SmokeGeneric) string = " 1_" + actor.name() + " " + ObjIO.classGetName(actor.getClass()) + " " + actor.getArmy() + " " + this.formatPos(point3d.x, point3d.y, orient.azimut()) + " " + this.formatValue(point3d.z);
            else string = " 1_" + actor.name() + " " + ObjIO.classGetName(actor.getClass()) + " " + actor.getArmy() + " " + this.formatPos(point3d.x, point3d.y, orient.azimut()) + " " + f;
        }
        return string;
    }

    public Actor mis_insert(Loc loc, String string) {
        int i = builder.wSelect.comboBox1.getSelected();
        int i_132_ = builder.wSelect.comboBox2.getSelected();
        if (i < this.startComboBox1 || i >= this.startComboBox1 + this.type.length) return null;
        i -= this.startComboBox1;
        if (i_132_ < 0 || i_132_ >= this.type[i].item.length) return null;
        NumberTokenizer numbertokenizer = new NumberTokenizer(string);
        numbertokenizer.next("");
        String string_134_ = numbertokenizer.next("");
        int i_135_ = numbertokenizer.next(0);
        double d = numbertokenizer.next(0.0);
        double d_136_ = numbertokenizer.next(0.0);
        d = loc.getPoint().x;
        d_136_ = loc.getPoint().y;
        Actor actor = this.insert(null, string_134_, i_135_, d, d_136_, numbertokenizer.next(0.0F), numbertokenizer.next(0.0F), numbertokenizer.next((String) null), numbertokenizer.next((String) null), numbertokenizer.next((String) null));
        return actor;
    }

    public boolean mis_validateSelected(int i, int i_137_) {
        if (i < this.startComboBox1 || i >= this.startComboBox1 + this.type.length) return false;
        i -= this.startComboBox1;
        if (i_137_ < 0 || i_137_ >= this.type[i].item.length) return false;
        return true;
    }

    static {
        Property.set(PlMisStatic.class, "name", "MisStatic");
    }
}
