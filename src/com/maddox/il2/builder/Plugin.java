/* 4.10.1 class */
package com.maddox.il2.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.maddox.JGP.Point3d;
import com.maddox.gwindow.GWindowMenuPopUp;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.IconDraw;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.game.I18N;
import com.maddox.rts.ObjIO;
import com.maddox.rts.Property;
import com.maddox.rts.SectFile;

public abstract class Plugin {
    public static Builder      builder;
    public static final String SELECT_ICON = "icons/SelectIcon.mat";
    public static final String TARGET_ICON = "icons/target.mat";
    public static final String PLAYER_ICON = "icons/player.mat";
    public static Mat          selectIcon;
    public static Mat          targetIcon;
    public static Mat          playerIcon;
    public String              sectFile;
    private static ArrayList   all;
    private static HashMap     mapNames;

    public final String name() {
        return Property.stringValue((Object) this.getClass(), "name", null);
    }

    public static String i18n(String string) {
        return I18N.bld(string);
    }

    // TODO: +++ Backport from 4.13
    public void dateChanged() { }
    // ---

    public void render3D() { }

    public void preRenderMap2D() { }

    public void renderMap2DBefore() { }

    public void renderMap2D() { }

    public void renderMap2DAfter() { }

    public void insert(Loc loc, boolean bool) { }

    public void beginFill(Point3d point3d) { }

    public void fill(Point3d point3d) { }

    public void endFill(Point3d point3d) { }

    public void delete(Loc loc) { }

    public void deleteAll() { }

    public void delete(Actor actor) {
        actor.destroy();
    }

    public void afterDelete() { }

    public void selectAll() { }

    public void changeType(boolean bool, boolean bool_0_) { }

    public void changeType() { }

    public void fillPopUpMenu(GWindowMenuPopUp gwindowmenupopup, Point3d point3d) { }

    public void syncSelector() { }

    public void updateSelector() { }

    public void updateSelectorMesh() { }

    public String[] actorInfo(Actor actor) {
        return null;
    }

    public static boolean isValidActorName(String string) {
        if (string == null) return false;
        if (string.length() == 0) return false;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.isWhitespace(c) || Character.isISOControl(c)) return false;
        }
        return true;
    }

    public void viewTypeAll(boolean bool) { }

    public void load(SectFile sectfile) { }

    public boolean save(SectFile sectfile) {
        return true;
    }

    public void mapLoaded() { }

    public boolean exitBuilder() {
        return true;
    }

    public void freeResources() { }

    public void configure() { }

    public void createGUI() { }

    public void start() { }

    protected static Plugin getPlugin(String string) {
        return (Plugin) mapNames.get(string);
    }

    protected static void loadAll(SectFile sectfile, String string, Builder builder) {
        Plugin.builder = builder;
        int i = sectfile.sectionIndex(string);
        if (i >= 0) {
            int i_1_ = sectfile.vars(i);
            if (i_1_ > 0) {
                for (int i_2_ = 0; i_2_ < i_1_; i_2_++) {
                    Object object = ObjIO.fromString(sectfile.line(i, i_2_));
                    if (object != null && object instanceof Plugin) {
                        Plugin plugin = (Plugin) object;
                        String string_3_ = plugin.name();
                        if (string_3_ != null && !mapNames.containsKey(string_3_)) {
                            all.add(object);
                            mapNames.put(string_3_, plugin);
                        }
                    }
                }

                // TODO: Added by |ZUTI|
                // ----------------------------------
                ZutiSupportMethods_Builder.createMDSPlugin();
                // ----------------------------------

                int i_4_ = all.size();
                for (int i_5_ = 0; i_5_ < i_4_; i_5_++)
                    ((Plugin) all.get(i_5_)).configure();
            }
        }
    }

    public static String timeSecToString(double d) {
        int i = (int) Math.round(d / 60.0);
        int i_6_ = i % 60;
        if (i_6_ < 10) return "" + i / 60 % 24 + ":0" + i_6_;
        return "" + i / 60 % 24 + ":" + i_6_;
    }

    // TODO: +++ Backport from 4.13
    protected static void doDateChanged()
    {
        int i = all.size();
        for(int j = 0; j < i; j++)
            ((Plugin)(Plugin)all.get(j)).dateChanged();

    }
    // ---

    protected static void doRender3D() {
        int i = all.size();
        for (int i_7_ = 0; i_7_ < i; i_7_++)
            ((Plugin) all.get(i_7_)).render3D();
    }

    protected static void doPreRenderMap2D() {
        int i = all.size();
        for (int i_8_ = 0; i_8_ < i; i_8_++)
            ((Plugin) all.get(i_8_)).preRenderMap2D();
    }

    protected static void doRenderMap2DBefore() {
        int i = all.size();
        for (int i_9_ = 0; i_9_ < i; i_9_++)
            ((Plugin) all.get(i_9_)).renderMap2DBefore();
    }

    protected static void doRenderMap2D() {
        int i = all.size();
        for (int i_10_ = 0; i_10_ < i; i_10_++)
            ((Plugin) all.get(i_10_)).renderMap2D();
    }

    protected static void doRenderMap2DAfter() {
        int i = all.size();
        for (int i_11_ = 0; i_11_ < i; i_11_++)
            ((Plugin) all.get(i_11_)).renderMap2DAfter();
    }

    protected static void doCreateGUI() {
        int i = all.size();
        for (int i_12_ = 0; i_12_ < i; i_12_++)
            ((Plugin) all.get(i_12_)).createGUI();
    }

    protected static void doStart() {
        selectIcon = IconDraw.get("icons/SelectIcon.mat");
        targetIcon = IconDraw.get("icons/target.mat");
        playerIcon = IconDraw.get("icons/player.mat");
        int i = all.size();
        for (int i_13_ = 0; i_13_ < i; i_13_++)
            ((Plugin) all.get(i_13_)).start();
    }

    protected static void doInsert(Loc loc, boolean bool) {
        int i = all.size();
        for (int i_14_ = 0; i_14_ < i; i_14_++)
            ((Plugin) all.get(i_14_)).insert(loc, bool);
    }

    protected static String mis_doGetProperties(Actor actor) {
        String string = "";
        for (int i = 0; i < all.size(); i++)
            string += ((Plugin) all.get(i)).mis_getProperties(actor);
        return string;
    }

    protected static Actor mis_doInsert(Loc loc, String string) {
        Actor actor = null;
        for (int i = 0; i < all.size(); i++) {
            actor = ((Plugin) all.get(i)).mis_insert(loc, string);
            if (actor != null) return actor;
        }
        return actor;
    }

    protected static boolean mis_doValidateSelected(int i, int i_15_) {
        boolean bool = false;
        for (int i_16_ = 0; i_16_ < all.size(); i_16_++)
            bool = bool || ((Plugin) all.get(i_16_)).mis_validateSelected(i, i_15_);
        return bool;
    }

    public String mis_getProperties(Actor actor) {
        return "";
    }

    public Actor mis_insert(Loc loc, String string) {
        return null;
    }

    public boolean mis_validateSelected(int i, int i_17_) {
        return false;
    }

    protected static void doFillPopUpMenu(GWindowMenuPopUp gwindowmenupopup, Point3d point3d) {
        int i = all.size();
        for (int i_18_ = 0; i_18_ < i; i_18_++)
            ((Plugin) all.get(i_18_)).fillPopUpMenu(gwindowmenupopup, point3d);
    }

    protected static void doBeginFill(Point3d point3d) {
        int i = all.size();
        for (int i_19_ = 0; i_19_ < i; i_19_++)
            ((Plugin) all.get(i_19_)).beginFill(point3d);
    }

    protected static void doFill(Point3d point3d) {
        int i = all.size();
        for (int i_20_ = 0; i_20_ < i; i_20_++)
            ((Plugin) all.get(i_20_)).fill(point3d);
    }

    protected static void doEndFill(Point3d point3d) {
        int i = all.size();
        for (int i_21_ = 0; i_21_ < i; i_21_++)
            ((Plugin) all.get(i_21_)).endFill(point3d);
    }

    protected static void doDelete(Loc loc) {
        int i = all.size();
        for (int i_22_ = 0; i_22_ < i; i_22_++)
            ((Plugin) all.get(i_22_)).delete(loc);
    }

    protected static void doDeleteAll() {
        int i = all.size();
        for (int i_23_ = 0; i_23_ < i; i_23_++)
            ((Plugin) all.get(i_23_)).deleteAll();
    }

    protected static void doAfterDelete() {
        int i = all.size();
        for (int i_24_ = 0; i_24_ < i; i_24_++)
            ((Plugin) all.get(i_24_)).afterDelete();
    }

    protected static void doSelectAll() {
        int i = all.size();
        for (int i_25_ = 0; i_25_ < i; i_25_++)
            ((Plugin) all.get(i_25_)).selectAll();
    }

    protected static void doChangeType(boolean bool, boolean bool_26_) {
        int i = all.size();
        for (int i_27_ = 0; i_27_ < i; i_27_++)
            ((Plugin) all.get(i_27_)).changeType(bool, bool_26_);
    }

    protected static void doViewTypeAll(boolean bool) {
        int i = all.size();
        for (int i_28_ = 0; i_28_ < i; i_28_++)
            ((Plugin) all.get(i_28_)).viewTypeAll(bool);
    }

    protected static void doLoad(SectFile sectfile) {
        int i = all.size();
        for (int i_29_ = 0; i_29_ < i; i_29_++)
            ((Plugin) all.get(i_29_)).load(sectfile);
    }

    protected static boolean doSave(SectFile sectfile) {
        int i = all.size();
        for (int i_30_ = 0; i_30_ < i; i_30_++)
            if (!((Plugin) all.get(i_30_)).save(sectfile)) return false;
        return true;
    }

    protected static void doMapLoaded() {
        builder.mapLoaded();
        int i = all.size();
        for (int i_31_ = 0; i_31_ < i; i_31_++)
            ((Plugin) all.get(i_31_)).mapLoaded();
    }

    protected static void doFreeResources() {
        int i = all.size();
        for (int i_32_ = 0; i_32_ < i; i_32_++)
            ((Plugin) all.get(i_32_)).freeResources();
    }

    protected static boolean doExitBuilder() {
        int i = all.size();
        for (int i_33_ = 0; i_33_ < i; i_33_++)
            if (!((Plugin) all.get(i_33_)).exitBuilder()) return false;
        return true;
    }

    static {
        ObjIO.field(Plugin.class, "sectFile");
        all = new ArrayList();
        mapNames = new HashMap();
    }

    // TODO: |ZUTI| methods
    // ---------------------------------------------------------------------
    /**
     * Method returns class all list.
     *
     * @return
     */
    public static ArrayList zutiGetAllActors() {
        return Plugin.all;
    }

    /**
     * Method returns class mapNames map.
     *
     * @return
     */
    public static Map zutiGetMapNames() {
        return Plugin.mapNames;
    }
    // ---------------------------------------------------------------------
}