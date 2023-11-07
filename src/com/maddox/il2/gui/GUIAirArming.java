/*4.10.1 class*/
package com.maddox.il2.gui;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.TreeMap;

import com.maddox.JGP.Color4f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3f;
import com.maddox.gwindow.GBevel;
import com.maddox.gwindow.GColor;
import com.maddox.gwindow.GWindow;
import com.maddox.gwindow.GWindowComboControl;
import com.maddox.gwindow.GWindowEditControl;
import com.maddox.gwindow.GWindowRoot;
import com.maddox.il2.ai.Regiment;
import com.maddox.il2.ai.UserCfg;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorHMesh;
import com.maddox.il2.engine.ActorMesh;
import com.maddox.il2.engine.BmpUtils;
import com.maddox.il2.engine.Camera3D;
import com.maddox.il2.engine.GUIRenders;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.LightEnvXY;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.engine.Render;
import com.maddox.il2.engine.Renders;
import com.maddox.il2.game.GameState;
import com.maddox.il2.game.I18N;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Mission;
import com.maddox.il2.net.BornPlace;
import com.maddox.il2.net.NetUser;
import com.maddox.il2.net.NetUserRegiment;
import com.maddox.il2.net.ZutiSupportMethods_Net;
import com.maddox.il2.objects.ActorSimpleHMesh;
import com.maddox.il2.objects.ActorSimpleMesh;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.PaintScheme;
import com.maddox.il2.objects.weapons.BombGun;
import com.maddox.rts.HomePath;
import com.maddox.rts.LDRres;
import com.maddox.rts.NetEnv;
import com.maddox.rts.ObjIO;
import com.maddox.rts.Property;
import com.maddox.rts.RTSConf;
import com.maddox.rts.SFSInputStream;
import com.maddox.rts.SectFile;
import com.maddox.sas1946.il2.util.FileTools;
import com.maddox.sas1946.il2.util.Reflection;
import com.maddox.util.HashMapExt;

public class GUIAirArming extends GameState {
    public GUIClient           client;
    public DialogClient        dialogClient;
    public GUIInfoMenu         infoMenu;
    public GUIInfoName         infoName;
    public GWindowEditControl  wMashineGun;
    public GWindowEditControl  wCannon;
    public GWindowEditControl  wRocket;
    public GWindowEditControl  wRocketDelay;
    public GWindowEditControl  wBombDelay;
    // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
    public GWindowEditControl  wBombFuze;
    // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
    public GWindowComboControl cFuel;
    public GWindowComboControl cAircraft;
    public GWindowComboControl cWeapon;
    public GWindowComboControl cCountry;
    public GWindowComboControl cRegiment;
    public GWindowComboControl cSkin;
    public GWindowComboControl cNoseart;
    public GWindowComboControl cPilot;
    public GWindowEditControl  wNumber;
    public GWindowComboControl cSquadron;
    public GUISwitchBox3       sNumberOn;
    public GWindowComboControl cPlane;
    public GUIButton           bBack;
    // MW Modified BEGIN: New Button declaration
    public GUIButton           bJoy;
    // MW Modified END
    public static final int    SINGLE         = 0;
    public static final int    CAMPAIGN       = 1;
    public static final int    DFIGHT         = 2;
    public static final int    COOP           = 3;
    public static final int    QUIK           = 4;
    public static int          stateId        = 2;
    public ArrayList           airNames;
    public ArrayList           weaponNames;
    public HashMapExt          regList;
    public HashMapExt          regHash;
    public ResourceBundle      resCountry;
    public ArrayList           countryLst;
    protected boolean          quikPlayer;
    protected int              quikArmy;
    protected int              quikPlanes;
    protected String           quikPlane;
    protected String           quikWeapon;
    protected int              quikCurPlane;
    protected String           quikRegiment;
    protected int              quikSquadron;
    protected int              quikWing;
    protected String           quikSkin[]     = { null, null, null, null };
    protected String           quikNoseart[]  = { null, null, null, null };
    protected String           quikPilot[]    = { null, null, null, null };
    protected boolean          quikNumberOn[] = { true, true, true, true };
    protected int              quikFuel;
    protected ArrayList        quikListPlane;

    // TODO: Modified by |ZUTI|: from private to protected
    // --------------------------
    protected String  planeName;
    protected boolean bEnableWeaponChange;
    // --------------------------

    private int       playerNum;
    public GUIRenders renders;
    public Camera3D   camera3D;
    public _Render3D  render3D;
    public ActorHMesh actorMesh;
    public ArrayList  weaponMeshs;
    public float      animateMeshA;
    public float      animateMeshT;
    private Orient    _orient;

    private boolean isNet() {
        return stateId == 2 || stateId == 3;
    }

    private String airName() {
        return (String) this.airNames.get(this.cAircraft.getSelected());
    }

    static class UserRegiment {

        protected String country;
        protected String branch;
        protected String fileName;
        protected char   id[];
        protected String shortInfo;
        protected int    gruppeNumber;

        public UserRegiment(String s) throws Exception {
            this.id = new char[2];
            this.gruppeNumber = 1;
            this.fileName = s;
            String s1 = Main.cur().netFileServerReg.primaryPath();
            PropertyResourceBundle propertyresourcebundle = new PropertyResourceBundle(new SFSInputStream(s1 + "/" + s));
            this.country = propertyresourcebundle.getString("country");
            this.country = this.country.toLowerCase().intern();
            this.branch = this.country;
            this.country = Regiment.getCountryFromBranch(this.branch);
            String s2 = propertyresourcebundle.getString("id");
            this.id[0] = s2.charAt(0);
            this.id[1] = s2.charAt(1);
            if ((this.id[0] < '0' || this.id[0] > '9') && (this.id[0] < 'A' || this.id[0] > 'Z')) throw new RuntimeException("Bad regiment id[0]");
            if ((this.id[1] < '0' || this.id[1] > '9') && (this.id[1] < 'A' || this.id[1] > 'Z')) throw new RuntimeException("Bad regiment id[1]");
            try {
                String s3 = propertyresourcebundle.getString("short");
                if (s3 == null || s3.length() == 0) s3 = s;
                this.shortInfo = s3;
            } catch (Exception exception) {
                this.shortInfo = s;
            }
            try {
                String s4 = propertyresourcebundle.getString("gruppeNumber");
                if (s4 != null) {
                    try {
                        this.gruppeNumber = Integer.parseInt(s4);
                    } catch (Exception exception2) {}
                    if (this.gruppeNumber < 1) this.gruppeNumber = 1;
                    if (this.gruppeNumber > 5) this.gruppeNumber = 5;
                }
            } catch (Exception exception1) {}
        }
    }

    public class DialogClient extends GUIDialogClient {
        public boolean notify(GWindow gwindow, int i, int j) {
            if (i != 2) return super.notify(gwindow, i, j);

            UserCfg usercfg = World.cur().userCfg;

            // TODO: Added by |ZUTI|: save sZutiMultiCrew status to usercfg
            // -------------------------------------------------------------------
            usercfg.bZutiMultiCrew = GUIAirArming.this.sZutiMultiCrew.isChecked();
            usercfg.bZutiMultiCrewAnytime = GUIAirArming.this.sZutiMultiCrewAnytime.isChecked();
            if (gwindow == GUIAirArming.this.sZutiMultiCrew) {
                if (!GUIAirArming.this.sZutiMultiCrew.isChecked()) GUIAirArming.this.sZutiMultiCrewAnytime.setChecked(false, false);

                GUIAirArming.this.sZutiMultiCrewAnytime.setEnable(GUIAirArming.this.sZutiMultiCrew.isChecked());
                return true;
            }
            // -------------------------------------------------------------------

            // MW Modified BEGIN: New button behaviour
            if (gwindow == GUIAirArming.this.bJoy) {
                Main.stateStack().push(53);
                return true;
            }
            // MW Modified END
            if (gwindow == GUIAirArming.this.cAircraft) {
                if (GUIAirArming.stateId != 2 && GUIAirArming.stateId != 4) return true;
                if (GUIAirArming.stateId == 2) usercfg.netAirName = GUIAirArming.this.airName();
                else if (GUIAirArming.stateId == 4) GUIAirArming.this.quikPlane = GUIAirArming.this.airName();

                // TODO: Added by |ZUTI|
                // -----------------------------------------------------------------------------------------------------
                if (GUIAirArming.this.zutiSelectedBornPlace != null && GUIAirArming.this.zutiSelectedBornPlace.zutiEnablePlaneLimits) ZutiSupportMethods_GUI.fillWeaponsListBasedOnBornPlace(GUIAirArming.this, GUIAirArming.this.zutiSelectedBornPlace);
                else GUIAirArming.this.fillWeapons();
                // System.out.println("GUIAirArming - Loading weapons 2");

                ZutiSupportMethods_GUI.setFuelSelectionForAircraft(GUIAirArming.this, GUIAirArming.this.zutiSelectedBornPlace, GUIAirArming.this.cFuel.getSelected());

                if (GUIAirArming.this.bEnableWeaponChange) if (GUIAirArming.this.isNet()) usercfg.fuel = (GUIAirArming.this.cFuel.getSelected() + 1) * 10;
                else if (GUIAirArming.stateId == 4) GUIAirArming.this.quikFuel = (GUIAirArming.this.cFuel.getSelected() + 1) * 10;
                else Main.cur().currentMissionFile.set(GUIAirArming.this.planeName, "Fuel", (GUIAirArming.this.cFuel.getSelected() + 1) * 10);

                GUIAirArming.this.selectWeapon();
                GUIAirArming.this.fillSkins();
                GUIAirArming.this.selectSkin();
                GUIAirArming.this.selectNoseart();
                GUIAirArming.this.setMesh();
                GUIAirArming.this.prepareMesh();
                GUIAirArming.this.prepareWeapons();
                GUIAirArming.this.prepareSkin();
                GUIAirArming.this.preparePilot();
                GUIAirArming.this.prepareNoseart();
                return true;
            }
            if (gwindow == GUIAirArming.this.cCountry) {
                if (GUIAirArming.stateId != 2 && GUIAirArming.stateId != 4) return true;
                GUIAirArming.this.fillRegiments();
                String s = (String) GUIAirArming.this.countryLst.get(GUIAirArming.this.cCountry.getSelected());
                ArrayList arraylist = (ArrayList) GUIAirArming.this.regList.get(s);
                Object obj = arraylist.get(GUIAirArming.this.cRegiment.getSelected());
                if (GUIAirArming.stateId == 2) {
                    if (obj instanceof Regiment) usercfg.netRegiment = ((Regiment) obj).name();
                    else usercfg.netRegiment = ((UserRegiment) obj).fileName;
                } else if (GUIAirArming.stateId == 4) GUIAirArming.this.quikRegiment = ((Regiment) obj).name();
                GUIAirArming.this.selectNoseart();
                GUIAirArming.this.setMesh();
                GUIAirArming.this.prepareMesh();
                GUIAirArming.this.prepareWeapons();
                GUIAirArming.this.prepareSkin();
                GUIAirArming.this.preparePilot();
                GUIAirArming.this.prepareNoseart();
                return true;
            }
            if (gwindow == GUIAirArming.this.cRegiment) {
                if (GUIAirArming.stateId != 2 && GUIAirArming.stateId != 4) return true;
                String s1 = (String) GUIAirArming.this.countryLst.get(GUIAirArming.this.cCountry.getSelected());
                ArrayList arraylist1 = (ArrayList) GUIAirArming.this.regList.get(s1);
                Object obj1 = arraylist1.get(GUIAirArming.this.cRegiment.getSelected());
                if (GUIAirArming.stateId == 2) {
                    if (obj1 instanceof Regiment) usercfg.netRegiment = ((Regiment) obj1).name();
                    else usercfg.netRegiment = ((UserRegiment) obj1).fileName;
                } else if (GUIAirArming.stateId == 4) GUIAirArming.this.quikRegiment = ((Regiment) obj1).name();
                GUIAirArming.this.prepareMesh();
                return true;
            }
            if (gwindow == GUIAirArming.this.cWeapon) {
                if (!GUIAirArming.this.bEnableWeaponChange) return true;
                if (GUIAirArming.this.isNet()) usercfg.setWeapon(GUIAirArming.this.airName(), (String) GUIAirArming.this.weaponNames.get(GUIAirArming.this.cWeapon.getSelected()));
                else if (GUIAirArming.stateId == 4) GUIAirArming.this.quikWeapon = (String) GUIAirArming.this.weaponNames.get(GUIAirArming.this.cWeapon.getSelected());
                else Main.cur().currentMissionFile.set(GUIAirArming.this.planeName, "weapons", (String) GUIAirArming.this.weaponNames.get(GUIAirArming.this.cWeapon.getSelected()));
                GUIAirArming.this.prepareWeapons();
                return true;
            }
            if (gwindow == GUIAirArming.this.cSkin) {
                int k = GUIAirArming.this.cSkin.getSelected();
                if (GUIAirArming.stateId == 4) {
                    if (k == 0) GUIAirArming.this.quikSkin[GUIAirArming.this.quikCurPlane] = null;
                    else GUIAirArming.this.quikSkin[GUIAirArming.this.quikCurPlane] = GUIAirArming.this.cSkin.get(k);
                } else if (k == 0) usercfg.setSkin(GUIAirArming.this.airName(), null);
                else usercfg.setSkin(GUIAirArming.this.airName(), GUIAirArming.this.cSkin.get(k));
                GUIAirArming.this.prepareSkin();
                return true;
            }
            if (gwindow == GUIAirArming.this.cNoseart) {
                int l = GUIAirArming.this.cNoseart.getSelected();
                if (GUIAirArming.stateId == 4) {
                    if (l == 0) GUIAirArming.this.quikNoseart[GUIAirArming.this.quikCurPlane] = null;
                    else GUIAirArming.this.quikNoseart[GUIAirArming.this.quikCurPlane] = GUIAirArming.this.cNoseart.get(l);
                } else if (l == 0) usercfg.setNoseart(GUIAirArming.this.airName(), null);
                else usercfg.setNoseart(GUIAirArming.this.airName(), GUIAirArming.this.cNoseart.get(l));
                if (l == 0) {
                    GUIAirArming.this.setMesh();
                    GUIAirArming.this.prepareMesh();
                    GUIAirArming.this.prepareWeapons();
                    GUIAirArming.this.prepareSkin();
                    GUIAirArming.this.preparePilot();
                }
                GUIAirArming.this.prepareNoseart();
                return true;
            }
            if (gwindow == GUIAirArming.this.cPilot) {
                if (GUIAirArming.stateId == 4) {
                    if (GUIAirArming.this.cPilot.getSelected() == 0) GUIAirArming.this.quikPilot[GUIAirArming.this.quikCurPlane] = null;
                    else GUIAirArming.this.quikPilot[GUIAirArming.this.quikCurPlane] = GUIAirArming.this.cPilot.getValue();
                } else if (GUIAirArming.this.cPilot.getSelected() == 0) usercfg.netPilot = null;
                else usercfg.netPilot = GUIAirArming.this.cPilot.getValue();
                GUIAirArming.this.preparePilot();
                return true;
            }
            if (gwindow == GUIAirArming.this.wMashineGun) {
                usercfg.coverMashineGun = GUIAirArming.this.clampValue(GUIAirArming.this.wMashineGun, usercfg.coverMashineGun, 100F, 1000F);
                return true;
            }
            if (gwindow == GUIAirArming.this.wCannon) {
                usercfg.coverCannon = GUIAirArming.this.clampValue(GUIAirArming.this.wCannon, usercfg.coverCannon, 100F, 1000F);
                return true;
            }
            if (gwindow == GUIAirArming.this.wRocket) {
                usercfg.coverRocket = GUIAirArming.this.clampValue(GUIAirArming.this.wRocket, usercfg.coverRocket, 100F, 1000F);
                return true;
            }
            if (gwindow == GUIAirArming.this.wRocketDelay) {
                usercfg.rocketDelay = GUIAirArming.this.clampValue(GUIAirArming.this.wRocketDelay, usercfg.rocketDelay, UserCfg.ROCKET_DELAY_MIN, UserCfg.ROCKET_DELAY_MAX);
                return true;
            }
            if (gwindow == GUIAirArming.this.wBombDelay) {
                usercfg.bombDelay = GUIAirArming.this.clampValue(GUIAirArming.this.wBombDelay, usercfg.bombDelay, UserCfg.BOMB_DELAY_MIN, UserCfg.BOMB_DELAY_MAX);
                return true;
            }
            // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
            if (gwindow == GUIAirArming.this.wBombFuze) {
                usercfg.bombFuze = GUIAirArming.this.clampValue(GUIAirArming.this.wBombFuze, usercfg.bombFuze, UserCfg.BOMB_FUZE_MIN, UserCfg.BOMB_FUZE_MAX);
                return true;
            }
            // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
            if (gwindow == GUIAirArming.this.cFuel) {
                if (GUIAirArming.this.bEnableWeaponChange) if (GUIAirArming.this.isNet()) usercfg.fuel = (GUIAirArming.this.cFuel.getSelected() + 1) * 10;
                else if (GUIAirArming.stateId == 4) GUIAirArming.this.quikFuel = (GUIAirArming.this.cFuel.getSelected() + 1) * 10;
                else Main.cur().currentMissionFile.set(GUIAirArming.this.planeName, "Fuel", (GUIAirArming.this.cFuel.getSelected() + 1) * 10);
            } else {
                if (gwindow == GUIAirArming.this.cSquadron) if (GUIAirArming.stateId != 2) return true;
                else {
                    usercfg.netSquadron = GUIAirArming.this.cSquadron.getSelected();
                    GUIAirArming.this.prepareMesh();
                    return true;
                }
                if (gwindow == GUIAirArming.this.wNumber) {
                    if (GUIAirArming.stateId != 2) return true;
                    String s2 = GUIAirArming.this.wNumber.getValue();
                    int i1 = usercfg.netTacticalNumber;
                    try {
                        i1 = Integer.parseInt(s2);
                    } catch (Exception exception) {}
                    if (i1 < 1) i1 = 1;
                    if (i1 > 99) i1 = 99;
                    GUIAirArming.this.wNumber.setValue("" + i1, false);
                    usercfg.netTacticalNumber = i1;
                    GUIAirArming.this.prepareMesh();
                    return true;
                }
                if (gwindow == GUIAirArming.this.sNumberOn) {
                    if (GUIAirArming.stateId == 4) GUIAirArming.this.quikNumberOn[GUIAirArming.this.quikCurPlane] = GUIAirArming.this.sNumberOn.isChecked();
                    else usercfg.netNumberOn = GUIAirArming.this.sNumberOn.isChecked();
                    GUIAirArming.this.prepareMesh();
                } else {
                    if (gwindow == GUIAirArming.this.cPlane) {
                        if (GUIAirArming.stateId != 4) return true;
                        GUIAirArming.this.quikCurPlane = GUIAirArming.this.cPlane.getSelected();
                        if (GUIAirArming.this.quikPlayer && GUIAirArming.this.quikCurPlane == 0) {
                            GUIAirArming.this.wMashineGun.showWindow();
                            GUIAirArming.this.wCannon.showWindow();
                            GUIAirArming.this.wRocket.showWindow();
                            GUIAirArming.this.wRocketDelay.showWindow();
                            GUIAirArming.this.wBombDelay.showWindow();
                            // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
                            GUIAirArming.this.wBombFuze.showWindow();
                            // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
                        } else {
                            GUIAirArming.this.wMashineGun.hideWindow();
                            GUIAirArming.this.wCannon.hideWindow();
                            GUIAirArming.this.wRocket.hideWindow();
                            GUIAirArming.this.wRocketDelay.hideWindow();
                            GUIAirArming.this.wBombDelay.hideWindow();
                            // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
                            GUIAirArming.this.wBombFuze.hideWindow();
                            // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
                        }
                        GUIAirArming.this.sNumberOn.setChecked(GUIAirArming.this.quikNumberOn[GUIAirArming.this.quikCurPlane], false);
                        GUIAirArming.this.fillSkins();
                        GUIAirArming.this.selectSkin();
                        GUIAirArming.this.selectNoseart();
                        GUIAirArming.this.fillPilots();
                        GUIAirArming.this.selectPilot();
                        GUIAirArming.this.setMesh();
                        GUIAirArming.this.prepareMesh();
                        GUIAirArming.this.prepareWeapons();
                        GUIAirArming.this.prepareSkin();
                        GUIAirArming.this.preparePilot();
                        GUIAirArming.this.prepareNoseart();
                        return true;
                    }
                    if (gwindow == GUIAirArming.this.bBack) switch (GUIAirArming.stateId) {
                        case 2: // '\002'
                            ((NetUser) NetEnv.host()).replicateNetUserRegiment();
                            // fall through

                        case 3: // '\003'
                            ((NetUser) NetEnv.host()).replicateSkin();
                            ((NetUser) NetEnv.host()).replicateNoseart();
                            ((NetUser) NetEnv.host()).replicatePilot();
                            // fall through

                        case 4: // '\004'
                        default:
                            usercfg.saveConf();
                            GUIAirArming.this.destroyMesh();
                            GUIAirArming.this.airNames.clear();
                            GUIAirArming.this.cAircraft.clear(false);
                            GUIAirArming.this.regList.clear();
                            GUIAirArming.this.regHash.clear();
                            GUIAirArming.this.cRegiment.clear(false);
                            GUIAirArming.this.countryLst.clear();
                            GUIAirArming.this.cCountry.clear(false);
                            GUIAirArming.this.cWeapon.clear();
                            GUIAirArming.this.weaponNames.clear();
                            GUIAirArming.this.cSkin.clear(false);
                            GUIAirArming.this.cPilot.clear(false);
                            GUIAirArming.this.cNoseart.clear(false);
                            Main.stateStack().pop();
                            return true;
                    }
                }
            }
            return super.notify(gwindow, i, j);
        }

        public void render() {
            super.render();
            // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
//			GUISeparate.draw(this, GColor.Gray, x1024(628F), y1024(176F), x1024(364F), 2.0F);
//			GUISeparate.draw(this, GColor.Gray, x1024(628F), y1024(320F), x1024(364F), 2.0F);
            GUISeparate.draw(this, GColor.Gray, this.x1024(624F), this.y1024(140F), this.x1024(372F), 2.0F);
            GUISeparate.draw(this, GColor.Gray, this.x1024(624F), this.y1024(264F), this.x1024(372F), 2.0F);
            GUISeparate.draw(this, GColor.Gray, this.x1024(624F), this.y1024(444F), this.x1024(372F), 2.0F);
            // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
            GUISeparate.draw(this, GColor.Gray, this.x1024(32F), this.y1024(640F), this.x1024(962F), 2.0F);
            this.setCanvasColor(GColor.Gray);
            this.setCanvasFont(0);
            // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
//			draw(x1024(644F), y1024(32F), x1024(332F), y1024(32F), 1, i18n("neta.Aircraft"));
//			draw(x1024(644F), y1024(96F), x1024(332F), y1024(32F), 1, i18n("neta.WeaponLoadout"));
//			draw(x1024(644F), y1024(176F), x1024(332F), y1024(32F), 1, i18n("neta.Country"));
//			draw(x1024(644F), y1024(240F), x1024(332F), y1024(32F), 1, i18n("neta.Regiment"));
//			draw(x1024(644F), y1024(320F), x1024(332F), y1024(32F), 1, i18n("neta.Skin"));
//			draw(x1024(644F), y1024(384F), x1024(332F), y1024(32F), 1, i18n("neta.Pilot"));
            if (GUIAirArming.stateId == 2) {
                this.draw(this.x1024(628F), this.y1024(448F), this.x1024(220F), this.y1024(32F), 0, GUIAirArming.this.i18n("neta.Number"));
                this.draw(this.x1024(864F), this.y1024(448F), this.x1024(128F), this.y1024(32F), 2, GUIAirArming.this.i18n("neta.Squadron"));
            } else this.draw(this.x1024(628F), this.y1024(480F), this.x1024(220F), this.y1024(32F), 0, GUIAirArming.this.i18n("neta.NumberOn"));
            this.draw(this.x1024(644F), this.y1024(20F), this.x1024(332F), this.y1024(32F), 1, GUIAirArming.this.i18n("neta.Aircraft"));
            this.draw(this.x1024(644F), this.y1024(76F), this.x1024(332F), this.y1024(32F), 1, GUIAirArming.this.i18n("neta.WeaponLoadout"));
            this.draw(this.x1024(644F), this.y1024(144F), this.x1024(332F), this.y1024(32F), 1, GUIAirArming.this.i18n("neta.Country"));
            this.draw(this.x1024(644F), this.y1024(200F), this.x1024(332F), this.y1024(32F), 1, GUIAirArming.this.i18n("neta.Regiment"));
            this.draw(this.x1024(644F), this.y1024(268F), this.x1024(332F), this.y1024(32F), 1, GUIAirArming.this.i18n("neta.Skin"));
            this.draw(this.x1024(644F), this.y1024(324F), this.x1024(332F), this.y1024(32F), 1, GUIAirArming.this.i18n("neta.Pilot"));
            // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
            GUILookAndFeel guilookandfeel = (GUILookAndFeel) this.lookAndFeel();
            if (GUIAirArming.stateId != 4 || GUIAirArming.this.quikPlayer && GUIAirArming.this.quikCurPlane == 0) {
                this.draw(this.x1024(80F), this.y1024(505F), this.x1024(576F), this.y1024(32F), 1, GUIAirArming.this.i18n("neta.WeaponConver"));
                this.draw(this.x1024(32F), this.y1024(544F), this.x1024(160F), this.y1024(32F), 2, GUIAirArming.this.i18n("neta.MachineGuns") + " ");
                this.draw(this.x1024(32F), this.y1024(592F), this.x1024(160F), this.y1024(32F), 2, GUIAirArming.this.i18n("neta.Cannons") + " ");
                this.draw(this.x1024(272F), this.y1024(544F), this.x1024(48F), this.y1024(32F), 0, " " + GUIAirArming.this.i18n("neta.m."));
                this.draw(this.x1024(272F), this.y1024(592F), this.x1024(48F), this.y1024(32F), 0, " " + GUIAirArming.this.i18n("neta.m."));
                this.draw(this.x1024(320F), this.y1024(544F), this.x1024(160F), this.y1024(32F), 2, GUIAirArming.this.i18n("neta.Rockets") + " ");
                this.draw(this.x1024(320F), this.y1024(592F), this.x1024(160F), this.y1024(32F), 2, GUIAirArming.this.i18n("neta.RocketDelay") + " ");
                this.draw(this.x1024(560F), this.y1024(544F), this.x1024(48F), this.y1024(32F), 0, " " + GUIAirArming.this.i18n("neta.m."));
                this.draw(this.x1024(560F), this.y1024(592F), this.x1024(48F), this.y1024(32F), 0, " " + GUIAirArming.this.i18n("neta.sec."));
                this.draw(this.x1024(608F), this.y1024(544F), this.x1024(224F), this.y1024(32F), 2, GUIAirArming.this.i18n("neta.BombDelay") + " ");
                this.draw(this.x1024(928F) - guilookandfeel.getVScrollBarW(), this.y1024(544F), this.x1024(48F), this.y1024(32F), 0, " " + GUIAirArming.this.i18n("neta.sec."));
                // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
                this.draw(this.x1024(608F), this.y1024(592F), this.x1024(224F), this.y1024(32F), 2, bombFuzeCaption + ": ");
                this.draw(this.x1024(928F) - guilookandfeel.getVScrollBarW(), this.y1024(592F), this.x1024(48F), this.y1024(32F), 0, " " + GUIAirArming.this.i18n("neta.sec."));
                // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
            }
            // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
//			draw(x1024(608F), y1024(592F), x1024(224F), y1024(32F), 2, i18n("neta.FuelQuantity") + " ");
//			draw(x1024(928F), y1024(592F), x1024(48F), y1024(32F), 0, " %");
            this.draw(this.x1024(608F), this.y1024(656F), this.x1024(224F), this.y1024(32F), 2, GUIAirArming.this.i18n("neta.FuelQuantity") + " ");
            this.draw(this.x1024(928F), this.y1024(656F), this.x1024(48F), this.y1024(32F), 0, " %");
            // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
            this.draw(this.x1024(96F), this.y1024(656F), this.x1024(320F), this.y1024(48F), 0, GUIAirArming.this.i18n("neta.Apply"));
            // MW Modified BEGIN: New button text position
            this.draw(this.x1024(396F), this.y1024(656F), this.x1024(620F), this.y1024(48F), 0, GUIAirArming.this.i18n("neta.Joystick"));
            // MW Modified END

            // TODO: Added by |ZUTI|: multicrew
            // -------------------------------------------------
            this.draw(this.x1024(40F), this.y1024(460F), this.x1024(220F), this.y1024(48F), 0, GUIAirArming.this.i18n("arming.multiCrew"));
            // draw(x1024(460F), y1024(656F), x1024(220F), y1024(48F), 0, i18n("Allow MultiCrew:"));
            this.draw(this.x1024(320F), this.y1024(460F), this.x1024(220F), this.y1024(48F), 0, GUIAirArming.this.i18n("arming.multiCrewAnytime"));
            // draw(x1024(700F), y1024(656F), x1024(220F), y1024(48F), 0, i18n("Crew Can Join Anytime:"));
            // -------------------------------------------------

            // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
//			if (cNoseart.isVisible())
            if (GUIAirArming.this.cNoseart.isEnable())
                // draw(x1024(292F), y1024(656F), x1024(320F), y1024(48F), 2, i18n("neta.Noseart"));
                this.draw(this.x1024(644F), this.y1024(380F), this.x1024(332F), this.y1024(32F), 1, GUIAirArming.this.i18n("neta.Noseart"));
            else this.draw(this.x1024(644F), this.y1024(380F), this.x1024(332F), this.y1024(32F), 1, GUIAirArming.this.i18n("neta.Noseart") + " (" + GUIAirArming.this.i18n("neta.None") + ")");
            // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
            this.setCanvasColorWHITE();
            guilookandfeel.drawBevel(this, this.x1024(32F), this.y1024(32F), this.x1024(564F), this.y1024(432F), guilookandfeel.bevelComboDown, guilookandfeel.basicelements);
        }

        public void setPosSize() {
            this.set1024PosSize(0.0F, 32F, 1024F, 736F);
            GUIAirArming.this.wMashineGun.set1024PosSize(192F, 544F, 80F, 32F);
            GUIAirArming.this.wCannon.set1024PosSize(192F, 592F, 80F, 32F);
            GUIAirArming.this.wRocket.set1024PosSize(480F, 544F, 80F, 32F);
            GUIAirArming.this.wRocketDelay.set1024PosSize(480F, 592F, 80F, 32F);
            GUILookAndFeel guilookandfeel = (GUILookAndFeel) this.lookAndFeel();
            GUIAirArming.this.wBombDelay.setPosSize(this.x1024(832F), this.y1024(544F), this.x1024(96F) - guilookandfeel.getVScrollBarW(), this.y1024(32F));
            // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
            GUIAirArming.this.wBombFuze.setPosSize(this.x1024(832F), this.y1024(592F), this.x1024(96F) - guilookandfeel.getVScrollBarW(), this.y1024(32F));
//			cFuel.set1024PosSize(832F, 592F, 96F, 32F);
            GUIAirArming.this.cFuel.set1024PosSize(832F, 656F, 96F, 32F);
            // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
            // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
//			if (GUIAirArming.stateId == 4)
//				cAircraft.set1024PosSize(628F, 64F, 298F, 32F);
//			else
//				cAircraft.set1024PosSize(628F, 64F, 364F, 32F);
//			cPlane.set1024PosSize(932F, 64F, 60F, 32F);
//			cWeapon.set1024PosSize(628F, 128F, 364F, 32F);
//			cCountry.set1024PosSize(628F, 208F, 364F, 32F);
//			cRegiment.set1024PosSize(628F, 272F, 364F, 32F);
//			cSkin.set1024PosSize(628F, 352F, 364F, 32F);
//			cPilot.set1024PosSize(628F, 416F, 364F, 32F);
            if (GUIAirArming.stateId == 4) GUIAirArming.this.cAircraft.set1024PosSize(628F, 46F, 298F, 32F);
            else GUIAirArming.this.cAircraft.set1024PosSize(628F, 46F, 364F, 32F);
            GUIAirArming.this.cPlane.set1024PosSize(932F, 46F, 60F, 32F);
            GUIAirArming.this.cWeapon.set1024PosSize(628F, 102F, 364F, 32F);
            GUIAirArming.this.cCountry.set1024PosSize(628F, 170F, 364F, 32F);
            GUIAirArming.this.cRegiment.set1024PosSize(628F, 226F, 364F, 32F);
            GUIAirArming.this.cSkin.set1024PosSize(628F, 294F, 364F, 32F);
            GUIAirArming.this.cPilot.set1024PosSize(628F, 350F, 364F, 32F);
            GUIAirArming.this.cNoseart.set1024PosSize(628F, 406F, 364F, 32F);
            // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
            GUIAirArming.this.wNumber.set1024PosSize(628F, 480F, 112F, 32F);
            GUIAirArming.this.cSquadron.set1024PosSize(896F, 480F, 96F, 32F);
            GUIAirArming.this.sNumberOn.setPosC(this.x1024(944F), this.y1024(496F));
            GUILookAndFeel guilookandfeel1 = (GUILookAndFeel) this.lookAndFeel();
            GBevel gbevel = guilookandfeel1.bevelComboDown;
            GUIAirArming.this.renders.setPosSize(this.x1024(32F) + gbevel.L.dx, this.y1024(32F) + gbevel.T.dy, this.x1024(564F) - gbevel.L.dx - gbevel.R.dx, this.y1024(432F) - gbevel.T.dy - gbevel.B.dy);
            GUIAirArming.this.bBack.setPosC(this.x1024(56F), this.y1024(680F));
            // MW Modified BEGIN: New button position
            GUIAirArming.this.bJoy.setPosC(this.x1024(356F), this.y1024(680F));
            // MW Modified END

            // TODO: Added by |ZUTI|
            // -------------------------------------------------
            GUIAirArming.this.sZutiMultiCrew.setPosC(this.x1024(230F), this.y1024(485F));
            GUIAirArming.this.sZutiMultiCrewAnytime.setPosC(this.x1024(580F), this.y1024(485F));
            // -------------------------------------------------
        }

        public DialogClient() {
        }
    }

    class _Render3D extends Render {

        public void preRender() {
            if (Actor.isValid(GUIAirArming.this.actorMesh)) {
                if (GUIAirArming.this.animateMeshA != 0.0F || GUIAirArming.this.animateMeshT != 0.0F) {
                    GUIAirArming.this.actorMesh.pos.getAbs(GUIAirArming.this._orient);
                    GUIAirArming.this._orient.set(GUIAirArming.this._orient.azimut() + GUIAirArming.this.animateMeshA * GUIAirArming.this.client.root.deltaTimeSec,
                            GUIAirArming.this._orient.tangage() + GUIAirArming.this.animateMeshT * GUIAirArming.this.client.root.deltaTimeSec, 0.0F);
                    // MW Modified BEGIN: Bug correction (Yaw forced to go back to [0:360] range)
                    float f = GUIAirArming.this._orient.getYaw();
                    while (f > 360F)
                        f = f - 360F;
                    while (f < 0F)
                        f = f + 360F;
                    GUIAirArming.this._orient.setYaw(f);
                    // MW Modified END
                    GUIAirArming.this.actorMesh.pos.setAbs(GUIAirArming.this._orient);
                    GUIAirArming.this.actorMesh.pos.reset();
                }
                GUIAirArming.this.actorMesh.draw.preRender(GUIAirArming.this.actorMesh);
                for (int i = 0; i < GUIAirArming.this.weaponMeshs.size(); i++) {
                    ActorMesh actormesh = (ActorMesh) GUIAirArming.this.weaponMeshs.get(i);
                    if (Actor.isValid(actormesh)) actormesh.draw.preRender(actormesh);
                }

            }
        }

        public void render() {
            if (Actor.isValid(GUIAirArming.this.actorMesh)) {
                Render.prepareStates();
                GUIAirArming.this.actorMesh.draw.render(GUIAirArming.this.actorMesh);
                for (int i = 0; i < GUIAirArming.this.weaponMeshs.size(); i++) {
                    ActorMesh actormesh = (ActorMesh) GUIAirArming.this.weaponMeshs.get(i);
                    if (Actor.isValid(actormesh)) actormesh.draw.render(actormesh);
                }
            }
        }

        public _Render3D(Renders renders1, float f) {
            super(renders1, f);
            this.setClearColor(new Color4f(0.5F, 0.78F, 0.92F, 1.0F));
            this.useClearStencil(true);
        }
    }

    public void _enter() {
        // TODO: Added by |ZUTI|
        this.zutiSelectedBornPlace = null;

        try {
            Object alQuickListPlane;
            Object localObject2;
            int k;
            Object lAllRegiments;
            Object treeMapRegiments;
            Object localObject5;
            int i2;
            Object localObject6;
            Object localObject8;
            Object cName;
            Object localObject10;
            Object localObject12;
            if (this.resCountry == null) this.resCountry = ResourceBundle.getBundle("i18n/country", RTSConf.cur.locale, LDRres.loader());
            this.bEnableWeaponChange = true;
            this.cFuel.setEnable(true);
            UserCfg localUserCfg = World.cur().userCfg;
            this.wMashineGun.setValue("" + localUserCfg.coverMashineGun, false);
            this.wCannon.setValue("" + localUserCfg.coverCannon, false);
            this.wRocket.setValue("" + localUserCfg.coverRocket, false);
            this.wRocketDelay.setValue("" + localUserCfg.rocketDelay, false);
            this.wBombDelay.setValue("" + localUserCfg.bombDelay, false);
            // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
            this.wBombFuze.setValue("" + localUserCfg.bombFuze, false);
        //    if ("ru".equalsIgnoreCase(RTSConf.cur.locale.getLanguage())) bombFuzeCaption = "\\u041F\\u0440\\u0435\\u0434\\u043E\\u0445\\u0440\\u0430\\u043D\\u0438\\u0442\\u0435\\u043B\\u044C \\u0431\\u043E\\u043C\\u0431\\u044B";
            if ("ru".equalsIgnoreCase(RTSConf.cur.locale.getLanguage())) bombFuzeCaption = "\u041F\u0440\u0435\u0434\u043E\u0445\u0440\u0430\u043D\u0438\u0442\u0435\u043B\u044C \u0431\u043E\u043C\u0431\u044B";
//            else if ("cs".equalsIgnoreCase(RTSConf.cur.locale.getLanguage())) bombFuzeCaption = "Bombov\\u00E1 pojistka";
            else if ("cs".equalsIgnoreCase(RTSConf.cur.locale.getLanguage())) bombFuzeCaption = "Bombov\u00E1 pojistka";
            else if ("de".equalsIgnoreCase(RTSConf.cur.locale.getLanguage())) bombFuzeCaption = "Bombensicherung";
//            else if ("fr".equalsIgnoreCase(RTSConf.cur.locale.getLanguage())) bombFuzeCaption = "Fus\\u00E9e de Bombe";
            else if ("fr".equalsIgnoreCase(RTSConf.cur.locale.getLanguage())) bombFuzeCaption = "Fus\u00E9e de Bombe";
//            else if ("hu".equalsIgnoreCase(RTSConf.cur.locale.getLanguage())) bombFuzeCaption = "Bomba biztos\\u00EDt\\u00E9k";
            else if ("hu".equalsIgnoreCase(RTSConf.cur.locale.getLanguage())) bombFuzeCaption = "Bomba biztos\u00EDt\u00E9k";
            else if ("pl".equalsIgnoreCase(RTSConf.cur.locale.getLanguage())) bombFuzeCaption = "Bezpiecznik Bomba";
            else bombFuzeCaption = "Bomb Fuze";

            SectFile localSectFile = Main.cur().currentMissionFile;
            if (localSectFile == null) World.cur().setWeaponsConstant(false);
            else {
                int j = localSectFile.get("MAIN", "WEAPONSCONSTANT", 0, 0, 1);
                World.cur().setWeaponsConstant(j == 1);
            }

            // TODO: Added by |ZUTI|: set sZutiMultiCrew from prew usercmd
            // ------------------------------------------------------------------------------
            this.sZutiMultiCrew.setChecked(localUserCfg.bZutiMultiCrew, false);
            this.sZutiMultiCrewAnytime.setChecked(localUserCfg.bZutiMultiCrewAnytime, false);
            if (!this.sZutiMultiCrew.isChecked()) this.sZutiMultiCrewAnytime.setChecked(false, false);

            this.sZutiMultiCrewAnytime.setEnable(this.sZutiMultiCrew.isChecked());
            // ------------------------------------------------------------------------------

            // System.out.println("State id: " + stateId);
            switch (stateId) {
                case 0:
                case 1:
                    this.wMashineGun.showWindow();
                    this.wCannon.showWindow();
                    this.wRocket.showWindow();
                    this.wRocketDelay.showWindow();
                    this.wBombDelay.showWindow();
                    // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
                    this.wBombFuze.showWindow();
                    // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
                    this.cPlane.hideWindow();
                    this.wNumber.hideWindow();
                    this.cSquadron.hideWindow();
                    this.sNumberOn.showWindow();
                    this.sNumberOn.setChecked(localUserCfg.netNumberOn, false);
                    localSectFile = Main.cur().currentMissionFile;
                    localObject2 = localSectFile.get("MAIN", "player", (String) null);
                    this.planeName = (String) localObject2;
                    lAllRegiments = ((String) localObject2).substring(0, ((String) localObject2).length() - 1);
                    treeMapRegiments = ((String) lAllRegiments).substring(0, ((String) lAllRegiments).length() - 1);
                    Regiment localRegiment = (Regiment) Actor.getByName((String) treeMapRegiments);
                    localObject5 = localSectFile.get(this.planeName, "Class", (String) null);
                    localObject6 = ObjIO.classForName((String) localObject5);
                    localObject8 = Property.stringValue((Class) localObject6, "keyName", null);
                    this.airNames.add(localObject8);
                    this.cAircraft.add(I18N.plane((String) localObject8));
                    this.cAircraft.setSelected(0, true, false);
                    this.countryLst.add(localRegiment.branch());
                    this.cCountry.add(this.resCountry.getString(localRegiment.branch()));
//                    System.out.println("Add (01) " + this.resCountry.getString(localRegiment.branch()));
                    this.cCountry.setSelected(0, true, false);
                    localObject10 = new ArrayList();
                    ((ArrayList) localObject10).add(localRegiment);
                    this.regList.put(localRegiment.branch(), localObject10);
                    this.cRegiment.add(localRegiment.shortInfo());
                    this.cRegiment.setSelected(0, true, false);
                    int i7 = localSectFile.get(this.planeName, "Fuel", 100, 0, 100);
                    // TODO: Edited by |ZUTI|: smaller fuel selections
                    // -----------------------------------------------------------------------------------------
                    int selection = i7 / 10 - 1;
                    if (selection < 0) selection = 0;

                    ZutiSupportMethods_GUI.setFuelSelectionForAircraft(GUIAirArming.this, this.zutiSelectedBornPlace, selection);
                    // -----------------------------------------------------------------------------------------
                    this.playerNum = localSectFile.get("Main", "playerNum", 0);
                    if (stateId == 1) this.bEnableWeaponChange = this.playerNum == 0 && !World.cur().isWeaponsConstant();
                    else this.bEnableWeaponChange = !World.cur().isWeaponsConstant();
                    this.cFuel.setEnable(this.bEnableWeaponChange);

                    break;
                case 3:
                    this.playerNum = -1;
                    this.wMashineGun.showWindow();
                    this.wCannon.showWindow();
                    this.wRocket.showWindow();
                    this.wRocketDelay.showWindow();
                    this.wBombDelay.showWindow();
                    // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
                    this.wBombFuze.showWindow();
                    // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
                    this.cPlane.hideWindow();
                    this.wNumber.hideWindow();
                    this.cSquadron.hideWindow();
                    this.sNumberOn.showWindow();
                    this.sNumberOn.setChecked(localUserCfg.netNumberOn, false);
                    this.planeName = GUINetAircraft.selectedWingName();
                    this.bEnableWeaponChange = !World.cur().isWeaponsConstant();
                    int i = (int) localUserCfg.fuel;
                    if (!this.bEnableWeaponChange) {
                        localObject2 = Main.cur().currentMissionFile;
                        i = ((SectFile) localObject2).get(this.planeName, "Fuel", 100, 0, 100);
                    }
                    // TODO: Edited by |ZUTI|: smaller fuel selections
                    // -----------------------------------------------------------------------------------------
                    selection = i / 10 - 1;
                    if (selection < 0) selection = 0;

                    ZutiSupportMethods_GUI.setFuelSelectionForAircraft(GUIAirArming.this, this.zutiSelectedBornPlace, selection);
                    // -----------------------------------------------------------------------------------------
                    this.cFuel.setEnable(this.bEnableWeaponChange);
                    this.airNames.add(GUINetAircraft.selectedAircraftKeyName());
                    this.cAircraft.add(GUINetAircraft.selectedAircraftName());
                    this.cAircraft.setSelected(0, true, false);
                    this.countryLst.add(GUINetAircraft.selectedRegiment().branch());
                    this.cCountry.add(this.resCountry.getString(GUINetAircraft.selectedRegiment().branch()));
//                    System.out.println("Add (02) " + this.resCountry.getString(GUINetAircraft.selectedRegiment().branch()));
                    this.cCountry.setSelected(0, true, false);
                    localObject2 = new ArrayList();
                    ((ArrayList) localObject2).add(GUINetAircraft.selectedRegiment());
                    this.regList.put(GUINetAircraft.selectedRegiment().branch(), localObject2);
                    this.cRegiment.add(GUINetAircraft.selectedRegiment().shortInfo());
                    this.cRegiment.setSelected(0, true, false);

                    break;
                case 2:
                    dfight: {
                        int l;
                        int i9;
                        ArrayList localArrayList2;
                        this.playerNum = -1;
                        this.wMashineGun.showWindow();
                        this.wCannon.showWindow();
                        this.wRocket.showWindow();
                        this.wRocketDelay.showWindow();
                        this.wBombDelay.showWindow();
                        // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
                        this.wBombFuze.showWindow();
                        // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
                        this.cPlane.hideWindow();
                        this.wNumber.showWindow();
                        this.cSquadron.showWindow();
                        this.sNumberOn.hideWindow();

                        // TODO: Edited by |ZUTI|: smaller fuel selections
                        // -----------------------------------------------------------------------------------------
                        selection = (int) localUserCfg.fuel / 10 - 1;
                        if (selection < 0) selection = 0;

                        ZutiSupportMethods_GUI.setFuelSelectionForAircraft(GUIAirArming.this, this.zutiSelectedBornPlace, selection);
                        // -----------------------------------------------------------------------------------------

                        alQuickListPlane = NetEnv.host();
                        k = ((NetUser) alQuickListPlane).getBornPlace();
                        lAllRegiments = World.cur().bornPlaces.get(k);

                        // TODO: Added by |ZUTI|
                        // ----------------------------------------------------------------------------------------------------------
                        this.zutiSelectedBornPlace = (BornPlace) lAllRegiments;
                        ArrayList zutiLimitedCountriesList = this.zutiSelectedBornPlace.zutiHomeBaseCountries;
                        // ----------------------------------------------------------------------------------------------------------

                        // TODO: Edited by |ZUTI|
                        // if (((BornPlace)localObject3).airNames != null) //Original line
                        ArrayList availablePlanesList = ZutiSupportMethods_Net.getBornPlaceAvailableAircraftList(this.zutiSelectedBornPlace);
                        if (availablePlanesList != null) // localObject4 = ((BornPlace)localObject3).airNames;
                            for (l = 0; l < availablePlanesList.size(); ++l) {
                                localObject5 = availablePlanesList.get(l);
                                localObject6 = Property.value(localObject5, "airClass", null);
                                if (localObject6 == null) continue;
                                this.airNames.add(localObject5);
                                this.cAircraft.add(I18N.plane((String) localObject5));
                            }
                        if (this.airNames.size() == 0 && !this.zutiSelectedBornPlace.zutiEnablePlaneLimits) {
                            treeMapRegiments = Main.cur().airClasses;
                            for (l = 0; l < ((ArrayList) treeMapRegiments).size(); ++l) {
                                localObject5 = ((ArrayList) treeMapRegiments).get(l);
                                localObject6 = Property.stringValue((Class) localObject5, "keyName");
                                if (!Property.containsValue((Class) localObject5, "cockpitClass")) continue;
                                this.airNames.add(localObject6);
                                this.cAircraft.add(I18N.plane((String) localObject6));
                            }
                        }

                        treeMapRegiments = Regiment.getAll();
                        TreeMap localTreeMap = new TreeMap();
                        i2 = ((List) treeMapRegiments).size();
                        for (int i3 = 0; i3 < i2; ++i3) {
                            localObject8 = ((List) treeMapRegiments).get(i3);
                            localObject10 = ((Regiment) localObject8).name();

                            if (!this.regHash.containsKey(localObject10)) {
                                this.regHash.put(localObject10, localObject8);
                                localObject12 = this.regList.get(((Regiment) localObject8).branch());
                                if (localObject12 == null) {
                                    String str1 = null;
                                    try {
                                        str1 = this.resCountry.getString(((Regiment) localObject8).branch());
                                    } catch (Exception localException6) {
                                        break dfight;
                                    }
                                    localObject12 = new ArrayList();
                                    this.regList.put(((Regiment) localObject8).branch(), localObject12);
                                    localTreeMap.put(str1, ((Regiment) localObject8).branch());
                                }
                                ((ArrayList) localObject12).add(localObject8);
                            }
                        }
                        try {
                            localObject8 = Main.cur().netFileServerReg.primaryPath();
                            localObject10 = new File(HomePath.toFileSystemName((String) localObject8, 0));
                            File[] afile = ((File) localObject10).listFiles();
                            if (afile != null) for (i9 = 0; i9 < afile.length; ++i9) {
                                File file = afile[i9];
                                if (!file.isFile()) continue;
                                String str2 = file.getName();
                                if (this.regHash.containsKey(str2)) continue;
                                String str3 = str2.toLowerCase();
                                if (!str3.endsWith(".bmp")) {
                                    if (str3.endsWith(".tga")) continue;
                                    if (str3.length() > 123) continue;
                                    int i10 = BmpUtils.squareSizeBMP8Pal((String) localObject8 + "/" + str3 + ".bmp");
                                    if (i10 != 64 && i10 != 128) System.out.println("File " + (String) localObject8 + "/" + str3 + ".bmp NOT loaded");
                                    else try {
                                        UserRegiment localUserRegiment = new UserRegiment(str2);

                                        this.regHash.put(str2, localUserRegiment);
                                        ArrayList localArrayList3 = (ArrayList) this.regList.get(localUserRegiment.branch);
                                        if (localArrayList3 == null) {
                                            String str4 = null;
                                            try {
                                                str4 = this.resCountry.getString(localUserRegiment.branch);
                                            } catch (Exception localException8) {
                                                break dfight;
                                            }
                                            localArrayList3 = new ArrayList();
                                            this.regList.put(localUserRegiment.branch, localArrayList3);
                                            localTreeMap.put(str4, localUserRegiment.branch);
                                        }
                                        localArrayList3.add(localUserRegiment);
                                    } catch (Exception localException7) {
                                        System.out.println(localException7.getMessage());
                                        System.out.println("Regiment " + str2 + " NOT loaded");
                                    }
                                }
                            }
                        } catch (Exception localException2) {
                            System.out.println(localException2.getMessage());
                            localException2.printStackTrace();
                        }

                        cName = localTreeMap.keySet().iterator();

                        while (((Iterator) cName).hasNext()) {
                            localObject10 = ((Iterator) cName).next();
                            // Modified by |ZUTI
                            // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                            if (zutiLimitedCountriesList == null || zutiLimitedCountriesList.size() <= 0) {
                                this.countryLst.add(localTreeMap.get(localObject10));
                                this.cCountry.add((String) localObject10);
//                                System.out.println("Add (03) " + (String) localObject10);
                            } else if (zutiLimitedCountriesList.contains(localObject10)) {
                                this.countryLst.add(localTreeMap.get(localObject10));
                                this.cCountry.add((String) localObject10);
//                                System.out.println("Add (04) " + (String) localObject10);
                            }
                            // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                        }
                        // TODO: Modified by |ZUTI|
                        // ---------------------------------------------------------
                        // Additional check: in unlucky event that there are NO valid countries available (it can happen that some mission
                        // has saved countries that are not available in specific installation), add "None" as country option.
                        if (this.countryLst.size() == 0 && this.cCountry.size() == 0) {
                            this.zutiSelectedBornPlace.zutiHomeBaseCountries.clear();
                            this.zutiSelectedBornPlace.zutiHomeBaseCountries.add("None");
                            this.countryLst.add(localTreeMap.get("None"));
                            this.cCountry.add("None");
                        }
                        // ---------------------------------------------------------
                        localTreeMap.clear();

                        this.cCountry.setSelected(0, true, false);
                        this.fillRegiments();
                        this.wNumber.setValue("" + localUserCfg.netTacticalNumber, false);
                        this.cSquadron.setSelected(localUserCfg.netSquadron, true, false);
                        if (localUserCfg.netRegiment != null) {
                            localObject10 = this.regHash.get(localUserCfg.netRegiment);
                            if (localObject10 != null) {
                                localObject12 = null;
                                if (localObject10 instanceof Regiment) localObject12 = ((Regiment) localObject10).branch();
                                else localObject12 = ((UserRegiment) localObject10).branch;
                                i9 = 0;
                                for (; i9 < this.countryLst.size(); ++i9)
                                    if (((String) localObject12).equals(this.countryLst.get(i9))) break;

                                if (i9 < this.countryLst.size()) {
                                    this.cCountry.setSelected(i9, true, false);
                                    this.fillRegiments();
                                    localArrayList2 = (ArrayList) this.regList.get(this.countryLst.get(i9));
                                    if (localArrayList2 != null) for (i9 = 0; i9 < localArrayList2.size(); ++i9)
                                        if (localObject10.equals(localArrayList2.get(i9))) {
                                            this.cRegiment.setSelected(i9, true, false);
                                            break;
                                        }
                                }
                            }

                        }

                        this.cAircraft.setSelected(-1, false, false);
                        try {
                            for (int i6 = 0; i6 < this.airNames.size(); ++i6)
                                if (localUserCfg.netAirName.equals(this.airNames.get(i6))) {
                                    this.cAircraft.setSelected(i6, true, false);
                                    break;
                                }
                        } catch (Exception localException4) {}
                        // TODO: Added by |ZUTI|: added this.cAircraft.size() > 0 condition
                        if (this.cAircraft.getSelected() < 0 && this.cAircraft.size() > 0) {
                            this.cAircraft.setSelected(0, true, false);
                            localUserCfg.netAirName = (String) this.airNames.get(0);
                        }
                        if (localUserCfg.netRegiment == null && this.cRegiment.size() > 0) {
                            this.cRegiment.setSelected(-1, false, false);
                            this.cRegiment.setSelected(0, true, true);
                        }

                        // TODO: Added by |ZUTI|
                        // ----------------------------------------------------------------
                        String s = (String) this.countryLst.get(this.cCountry.getSelected());
                        ArrayList arraylist = (ArrayList) this.regList.get(s);
                        Object obj = arraylist.get(this.cRegiment.getSelected());

                        try {
                            // In some cases regiments can fail (user defined ones, for one). Catch that class cast exception...
                            localUserCfg.netRegiment = ((Regiment) obj).name();
                        } catch (ClassCastException ex) {
                            // ... and load None as an valid option!
                            if (arraylist.size() <= 0) localUserCfg.netRegiment = "NoNe";
                            else {
                                obj = arraylist.get(0);
                                localUserCfg.netRegiment = ((Regiment) obj).name();
                            }
                        }

                        int currentFuelSelectionIndex = this.cFuel.getSelected();

                        ZutiSupportMethods_GUI.setFuelSelectionForAircraft(GUIAirArming.this, this.zutiSelectedBornPlace, currentFuelSelectionIndex);

                        if (this.bEnableWeaponChange) if (this.isNet()) localUserCfg.fuel = (this.cFuel.getSelected() + 1) * 10;
                        else if (GUIAirArming.stateId == 4) this.quikFuel = (this.cFuel.getSelected() + 1) * 10;
                        else Main.cur().currentMissionFile.set(this.planeName, "Fuel", (this.cFuel.getSelected() + 1) * 10);
                        // ----------------------------------------------------------------
                        break;
                    }
                case 4:
                    Object tmp;
                    if (this.quikPlayer) {
                        this.wMashineGun.showWindow();
                        this.wCannon.showWindow();
                        this.wRocket.showWindow();
                        this.wRocketDelay.showWindow();
                        this.wBombDelay.showWindow();
                        // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
                        this.wBombFuze.showWindow();
                        // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
                    } else {
                        this.wMashineGun.hideWindow();
                        this.wCannon.hideWindow();
                        this.wRocket.hideWindow();
                        this.wRocketDelay.hideWindow();
                        this.wBombDelay.hideWindow();
                        // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
                        this.wBombFuze.hideWindow();
                        // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
                    }
                    this.cPlane.showWindow();
                    this.wNumber.hideWindow();
                    this.cSquadron.hideWindow();
                    this.sNumberOn.showWindow();
                    this.quikCurPlane = 0;
                    this.sNumberOn.setChecked(this.quikNumberOn[this.quikCurPlane], false);

                    // TODO: Edited by |ZUTI|: smaller fuel selections
                    // -----------------------------------------------------------------------------------------
                    selection = this.quikFuel / 10 - 1;
                    if (selection < 0) selection = 0;

                    ZutiSupportMethods_GUI.setFuelSelectionForAircraft(GUIAirArming.this, this.zutiSelectedBornPlace, selection);
                    // -----------------------------------------------------------------------------------------

                    alQuickListPlane = this.quikListPlane;
                    for (k = 0; k < ((ArrayList) alQuickListPlane).size(); ++k) {
                        lAllRegiments = ((ArrayList) alQuickListPlane).get(k);
                        treeMapRegiments = Property.stringValue((Class) lAllRegiments, "keyName");
                        if (this.quikPlayer && !Property.containsValue((Class) lAllRegiments, "cockpitClass")) continue;
                        this.airNames.add(treeMapRegiments);
                        this.cAircraft.add(I18N.plane((String) treeMapRegiments));
                    }

                    lAllRegiments = Regiment.getAll();
                    treeMapRegiments = new TreeMap();
                    int allRegimentsSize = ((List) lAllRegiments).size();
                    for (i2 = 0; i2 < allRegimentsSize; ++i2) {
                        Regiment regimentMW = (Regiment) ((List) lAllRegiments).get(i2);
                        if (regimentMW.getArmy() != this.quikArmy) continue;
                        cName = regimentMW.name();

                        if (!this.regHash.containsKey(cName)) {
                            this.regHash.put(cName, regimentMW);
                            tmp = this.regList.get(regimentMW.branch());
                            if (tmp == null) {
                                localObject12 = null;
                                try {
                                    localObject12 = this.resCountry.getString(regimentMW.branch());
                                } catch (Exception localException5) {
                                    continue;
                                }
                                tmp = new ArrayList();
                                this.regList.put(regimentMW.branch(), tmp);
                                ((TreeMap) treeMapRegiments).put(localObject12, regimentMW.branch());
                            }
                            if (tmp != null) ((ArrayList) tmp).add(regimentMW);
                        }
                    }

                    Object localObject7 = ((TreeMap) treeMapRegiments).keySet().iterator();
                    while (((Iterator) localObject7).hasNext()) {
                        cName = ((Iterator) localObject7).next();
                        this.countryLst.add(((TreeMap) treeMapRegiments).get(cName));
                        this.cCountry.add((String) cName);
//                        System.out.println("Add (05) " + (String) cName);
                    }
                    ((TreeMap) treeMapRegiments).clear();

                    this.cCountry.setSelected(0, true, false);
                    this.fillRegiments();

                    if (this.quikRegiment != null) {
                        cName = this.regHash.get(this.quikRegiment);
                        if (cName != null) {
                            tmp = ((Regiment) cName).branch();
                            int i8 = 0;
                            for (; i8 < this.countryLst.size(); ++i8)
                                if (((String) tmp).equals(this.countryLst.get(i8))) break;

                            if (i8 < this.countryLst.size()) {
                                this.cCountry.setSelected(i8, true, false);
                                this.fillRegiments();
                                ArrayList localArrayList1 = (ArrayList) this.regList.get(this.countryLst.get(i8));
                                if (localArrayList1 != null) for (i8 = 0; i8 < localArrayList1.size(); ++i8)
                                    if (cName.equals(localArrayList1.get(i8))) {
                                        this.cRegiment.setSelected(i8, true, false);
                                        break;
                                    }
                            }
                        }

                    }

                    this.cAircraft.setSelected(-1, false, false);
                    try {
                        for (int i4 = 0; i4 < this.airNames.size(); ++i4)
                            if (this.quikPlane.equals(this.airNames.get(i4))) {
                                this.cAircraft.setSelected(i4, true, false);
                                break;
                            }
                    } catch (Exception localException3) {}
                    if (this.cAircraft.getSelected() < 0) {
                        this.cAircraft.setSelected(0, true, false);
                        this.quikPlane = (String) this.airNames.get(0);
                    }
                    if (this.quikRegiment == null && this.cRegiment.size() > 0) {
                        this.cRegiment.setSelected(-1, false, false);
                        this.cRegiment.setSelected(0, true, true);
                    }
                    this.cPlane.clear(false);
                    for (int i5 = 0; i5 < this.quikPlanes; ++i5)
                        this.cPlane.add(" " + (i5 + 1));
                    this.cPlane.setSelected(this.quikCurPlane, true, false);
            }

            // TODO: Added by |ZUTI|
            // -----------------------------------------------------------------------------------------------------
            if (this.zutiSelectedBornPlace != null && this.zutiSelectedBornPlace.zutiEnablePlaneLimits) ZutiSupportMethods_GUI.fillWeaponsListBasedOnBornPlace(this, this.zutiSelectedBornPlace);
            else this.fillWeapons();
            // System.out.println("GUIAirArming - Loading weapons 2");
            // -----------------------------------------------------------------------------------------------------
            this.selectWeapon();

            this.fillSkins();
            this.selectSkin();

            this.fillPilots();
            this.selectPilot();

            this.fillNoseart();
            this.selectNoseart();

            this.setMesh();
            this.prepareMesh();
            this.prepareWeapons();
            this.prepareSkin();
            this.preparePilot();
            this.prepareNoseart();
        } catch (Exception localException1) {
            System.out.println(localException1.getMessage());
            localException1.printStackTrace();
            Main.stateStack().pop();
            return;
        }

        this.dialogClient.setPosSize();
        this.client.activateWindow();
    }

    public void _leave() {
        World.cur().setUserCovers();
        this.client.hideWindow();
    }

    private void fillRegiments() {
        if (stateId != 2 && stateId != 4) return;
        this.cRegiment.clear();
        int i = this.cCountry.getSelected();
        if (i < 0) return;
        String s = (String) this.countryLst.get(i);
        ArrayList arraylist = (ArrayList) this.regList.get(s);
        if (arraylist.size() > 0) {
            for (int j = 0; j < arraylist.size(); j++) {
                Object obj = arraylist.get(j);
                if (obj instanceof Regiment) this.cRegiment.add(((Regiment) obj).shortInfo());
                else this.cRegiment.add(((UserRegiment) obj).shortInfo);
            }

            this.cRegiment.setSelected(0, true, false);
        }
    }

    // TODO: Modified by |ZUTI|: from private to protected
    protected void fillWeapons() {
        this.cWeapon.clear();
        this.weaponNames.clear();
        int i = this.cAircraft.getSelected();
        if (i < 0) return;
        Class class1 = (Class) Property.value(this.airNames.get(i), "airClass", null);
        String as[] = Aircraft.getWeaponsRegistered(class1);
        if (as != null && as.length > 0) {
            String s = (String) this.airNames.get(i);
            for (int j = 0; j < as.length; j++) {
                String s1 = as[j];
                if (!this.bEnableWeaponChange) {
                    String s2 = Main.cur().currentMissionFile.get(this.planeName, "weapons", (String) null);
                    if (!s1.equals(s2)) continue;
                }
                this.weaponNames.add(s1);
                // System.out.println("GUIAirArming - " + s1);
                this.cWeapon.add(I18N.weapons(s, s1));
            }

            if (this.weaponNames.size() == 0) {
                this.weaponNames.add(as[0]);
                this.cWeapon.add(I18N.weapons(s, as[0]));
            }
            this.cWeapon.setSelected(0, true, false);
        }
    }

    private void selectWeapon() {
        if (this.bEnableWeaponChange) {
            UserCfg usercfg = World.cur().userCfg;
            String s = null;
            if (this.isNet()) s = usercfg.getWeapon(this.airName());
            else if (stateId == 4) s = this.quikWeapon;
            else s = Main.cur().currentMissionFile.get(this.planeName, "weapons", (String) null);
            this.cWeapon.setSelected(-1, false, false);
            for (int i = 0; i < this.weaponNames.size(); i++) {
                String s1 = (String) this.weaponNames.get(i);
                if (!s1.equals(s)) continue;
                this.cWeapon.setSelected(i, true, false);
                break;
            }

            if (this.cWeapon.getSelected() < 0) {
                this.cWeapon.setSelected(0, true, false);
                if (this.isNet()) usercfg.setWeapon(this.airName(), (String) this.weaponNames.get(0));
                else if (stateId == 4) this.quikWeapon = (String) this.weaponNames.get(0);
                else Main.cur().currentMissionFile.set(this.planeName, "weapons", (String) this.weaponNames.get(0));
            }
        } else this.cWeapon.setSelected(0, true, false);
    }

    public static String validateFileName(String s) {
        if (s.indexOf('\\') >= 0) s = s.replace('\\', '_');
        if (s.indexOf('/') >= 0) s = s.replace('/', '_');
        if (s.indexOf('?') >= 0) s = s.replace('?', '_');
        return s;
    }

//    private void fillSkinsOld() {
//        this.cSkin.clear();
//        this.cSkin.add(this.i18n("neta.Default"));
//        try {
//            int i = this.cAircraft.getSelected();
//            String skinsPath = Main.cur().netFileServerSkin.primaryPath();
//            String aircraftName = validateFileName((String) this.airNames.get(i));
//            File file = new File(HomePath.toFileSystemName(skinsPath + "/" + aircraftName, 0));
//            File afile[] = file.listFiles();
//            boolean isOfflineMission = !Mission.isNet();
//            if (afile != null) for (int skinsListIndex = 0; skinsListIndex < afile.length; skinsListIndex++) {
//                File skinsFile = afile[skinsListIndex].getCanonicalFile();
//                long skinsFileSize = 0;
//                boolean isFile = false;
//                if (FileTools.isSymbolicLink(skinsFile.getPath())) {
////                    String resolvedPath = FileTools.resolveSymbolicLink(skinsFile.getPath());
////                    if (resolvedPath.startsWith("\\\\?\\")) {
////                        System.out.println("File " + skinsListIndex + " " + skinsFile.getPath() + " " + resolvedPath);
////                        resolvedPath = resolvedPath.substring(4);
////                    }
////                    File skinsFileResolved = new File(HomePath.toFileSystemName(resolvedPath, 0));
//                    File skinsFileResolved = new File(HomePath.toFileSystemName(FileTools.resolveSymbolicLink(skinsFile.getPath()), 0));
//                    isFile = skinsFileResolved.isFile();
//                    if (isFile) skinsFileSize = skinsFileResolved.length();
//                    System.out.println("File " + skinsListIndex + " " + skinsFile.getPath() + " " + skinsFileResolved.getPath() + " " + isFile + " " + skinsFileSize);
//                } else {
//                    isFile = skinsFile.isFile();
//                    if (isFile) skinsFileSize = skinsFile.length();
//                }
//                //System.out.println("File " + skinsListIndex + " " + skinsFile.getPath() + " " + FileTools.resolveSymbolicLink(skinsFile.getPath()) + " " + skinsFile.isFile() + " " + skinsFile.length() + " " + FileTools.isSymbolicLink(skinsFile.getPath()));
//                //if (skinsFile.isFile()) {
//                if (isFile) {
//                    String skinsFileName = skinsFile.getName();
//                    String skinsFileNameLowerCase = skinsFileName.toLowerCase();
//                    if (skinsFileNameLowerCase.endsWith(".bmp") && skinsFileNameLowerCase.length() + aircraftName.length() <= 122) {
//
//                        // TODO: By SAS~Storebror: Don't accept HD skins online!
//
////                        int k = BmpUtils.squareSizeBMP8Pal(skinsPath + "/" + aircraftName + "/" + skinsFileName);
////                        if (k == 512 || k == 1024) this.cSkin.add(skinsFileName);
////                        else System.out.println("Skin " + skinsPath + "/" + aircraftName + "/" + skinsFileName + " NOT loaded");
//
//                        // If this is an online mission, allow indexed 512x512 and 1024x1024 standard size skins only!
////                        long skinsFileSize = skinsFile.length(); // TODO: "getCanonicalFile()" is needed to resolve Symbolic Links properly.
//                        if (isOfflineMission || Math.abs(skinsFileSize-263036) < 200 || Math.abs(skinsFileSize-1049176) < 500) this.cSkin.add(skinsFileName);
//
//                        // ---
//                    }
//                }
//            }
//        } catch (Exception exception) {
//            System.out.println(exception.getMessage());
//            exception.printStackTrace();
//        }
//        this.cSkin.setSelected(0, true, false);
//    }

    private void fillSkins() {
        this.cSkin.clear();
        this.cSkin.add(this.i18n("neta.Default"));
        try {
            int i = this.cAircraft.getSelected();
            String skinsPath = Main.cur().netFileServerSkin.primaryPath();
            String aircraftName = validateFileName((String) this.airNames.get(i));
            File file = new File(HomePath.toFileSystemName(skinsPath + "/" + aircraftName, 0));
            File afile[] = file.listFiles();
            if (afile != null) for (int skinsListIndex = 0; skinsListIndex < afile.length; skinsListIndex++) {
                File skinsFile = afile[skinsListIndex].getCanonicalFile();
                if (skinsFile.isFile()) {
                    String skinsFileName = skinsFile.getName();
//                    System.out.println("Skin filename = " + skinsFileName);
                    if (FileTools.isSymbolicLink(skinsFile.getPath())) {
                        skinsFile = new File(HomePath.toFileSystemName(FileTools.resolveSymbolicLink(skinsFile.getPath()), 0));
//                        System.out.println("...is symbolic link, resolved filename = " + skinsFile.getName());
                    }
                    String skinsFileNameLowerCase = skinsFileName.toLowerCase();
                    if (skinsFileNameLowerCase.endsWith(".bmp") && skinsFileNameLowerCase.length() + aircraftName.length() <= 122) {
                        long skinsFileSize = skinsFile.length();
                        if (!Mission.isNet() || Math.abs(skinsFileSize - 263036) < 200 || Math.abs(skinsFileSize - 1049176) < 500) this.cSkin.add(skinsFileName);
                        else System.out.println("Skin not added to the list, skinsFileSize=" + skinsFileSize + " (should by < 200), Math.abs(skinsFileSize - 263036)=" + (Math.abs(skinsFileSize - 263036)) + ", Math.abs(skinsFileSize - 1049176)=" + (Math.abs(skinsFileSize - 1049176)) + " (should by < 500)");
                    } else {
//                        System.out.println("...skinsFileNameLowerCase.length() + aircraftName.length() = " + (skinsFileNameLowerCase.length() + aircraftName.length()));
                    }
                }
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        this.cSkin.setSelected(0, true, false);
    }

    private void selectSkin() {
        UserCfg usercfg = World.cur().userCfg;
        this.cSkin.setSelected(-1, false, false);
        String s = usercfg.getSkin(this.airName());
        if (stateId == 4) s = this.quikSkin[this.quikCurPlane];
        for (int i = 1; i < this.cSkin.size(); i++) {
            String s1 = this.cSkin.get(i);
            if (!s1.equals(s)) continue;
            this.cSkin.setSelected(i, true, false);
            break;
        }

        if (this.cSkin.getSelected() < 0) {
            this.cSkin.setSelected(0, true, false);
            if (stateId == 4) this.quikSkin[this.quikCurPlane] = null;
            else usercfg.setSkin(this.airName(), null);
        }
    }

    private void fillPilots() {
        this.cPilot.clear();
        this.cPilot.add(this.i18n("neta.Default"));
        try {
            String s = Main.cur().netFileServerPilot.primaryPath();
            File file = new File(HomePath.toFileSystemName(s, 0));
            File afile[] = file.listFiles();
            if (afile != null) for (int i = 0; i < afile.length; i++) {
                File file1 = afile[i];
                if (file1.isFile()) {
                    String s1 = file1.getName();
                    String s2 = s1.toLowerCase();
                    if (s2.endsWith(".bmp") && s2.length() <= 122) if (BmpUtils.checkBMP8Pal(s + "/" + s1, 256, 256)) this.cPilot.add(s1);
                    else System.out.println("Pilot " + s + "/" + s1 + " NOT loaded");
                }
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        this.cPilot.setSelected(0, true, false);
    }

    private void selectPilot() {
        UserCfg usercfg = World.cur().userCfg;
        this.cPilot.setSelected(-1, false, false);
        String s = usercfg.netPilot;
        if (stateId == 4) s = this.quikPilot[this.quikCurPlane];
        for (int i = 1; i < this.cPilot.size(); i++) {
            String s1 = this.cPilot.get(i);
            if (!s1.equals(s)) continue;
            this.cPilot.setSelected(i, true, false);
            break;
        }

        if (this.cPilot.getSelected() < 0) {
            this.cPilot.setSelected(0, true, false);
            if (stateId == 4) this.quikPilot[this.quikCurPlane] = null;
            else usercfg.netPilot = null;
        }
    }

    private void fillNoseart() {
        this.cNoseart.clear();
        this.cNoseart.add(this.i18n("neta.None"));
        try {
            String s = Main.cur().netFileServerNoseart.primaryPath();
            File file = new File(HomePath.toFileSystemName(s, 0));
            File afile[] = file.listFiles();
            if (afile != null) for (int i = 0; i < afile.length; i++) {
                File file1 = afile[i];
                if (file1.isFile()) {
                    String s1 = file1.getName();
                    String s2 = s1.toLowerCase();
                    if (s2.endsWith(".bmp") && s2.length() <= 122) if (BmpUtils.checkBMP8Pal(s + "/" + s1, 256, 512)) this.cNoseart.add(s1);
                    else System.out.println("Noseart " + s + "/" + s1 + " NOT loaded");
                }
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        this.cNoseart.setSelected(0, true, false);
    }

    private void selectNoseart() {
        UserCfg usercfg = World.cur().userCfg;
        this.cNoseart.setSelected(-1, false, false);
        boolean flag = true;
        int i = this.cAircraft.getSelected();
        if (i < 0) flag = false;
        else {
            Class class1 = (Class) Property.value(this.airNames.get(i), "airClass", null);
            flag = Property.intValue(class1, "noseart", 0) == 1;
            if (flag) {
                int j = this.cCountry.getSelected();
                if (j < 0) flag = false;
                else {
                    String s1 = (String) this.countryLst.get(j);
                    String s3 = Regiment.getCountryFromBranch(s1);
                    flag = "us".equals(s3);
                }
            }
        }
        if (flag) {
            String s = usercfg.getNoseart(this.airName());
            if (stateId == 4) s = this.quikNoseart[this.quikCurPlane];
            for (int k = 1; k < this.cNoseart.size(); k++) {
                String s2 = this.cNoseart.get(k);
                if (!s2.equals(s)) continue;
                this.cNoseart.setSelected(k, true, false);
                break;
            }

//			cNoseart.showWindow();
            this.cNoseart.setEnable(true);
        } else // cNoseart.hideWindow();
            this.cNoseart.setEnable(false);
        if (this.cNoseart.getSelected() < 0) {
            this.cNoseart.setSelected(0, true, false);
            if (stateId == 4) this.quikNoseart[this.quikCurPlane] = null;
            else usercfg.setNoseart(this.airName(), null);
        }
    }

    private void createRender() {
        this.renders = new GUIRenders(this.dialogClient) {

            public void mouseButton(int i, boolean flag, float f, float f1) {
                super.mouseButton(i, flag, f, f1);
                if (!flag) return;
                if (i == 1) {
                    GUIAirArming.this.animateMeshA = GUIAirArming.this.animateMeshT = 0.0F;
                    if (Actor.isValid(GUIAirArming.this.actorMesh)) GUIAirArming.this.actorMesh.pos.setAbs(new Orient(90F, 0.0F, 0.0F));
                } else if (i == 0) {
                    f -= this.win.dx / 2.0F;
                    if (Math.abs(f) < this.win.dx / 16F) GUIAirArming.this.animateMeshA = 0.0F;
                    else GUIAirArming.this.animateMeshA = -128F * f / this.win.dx;
                    f1 -= this.win.dy / 2.0F;
                    if (Math.abs(f1) < this.win.dy / 16F) GUIAirArming.this.animateMeshT = 0.0F;
                    else GUIAirArming.this.animateMeshT = -128F * f1 / this.win.dy;
                }
            }

        };
        this.camera3D = new Camera3D();
        this.camera3D.set(50F, 1.0F, 100F);
        this.render3D = new _Render3D(this.renders.renders, 1.0F);
        this.render3D.setCamera(this.camera3D);
        LightEnvXY lightenvxy = new LightEnvXY();
        this.render3D.setLightEnv(lightenvxy);
        lightenvxy.sun().setLight(0.5F, 0.5F, 1.0F, 1.0F, 1.0F, 0.8F);
        Vector3f vector3f = new Vector3f(-2F, 1.0F, -1F);
        vector3f.normalize();
        lightenvxy.sun().set(vector3f);
    }

    private void setMesh() {
        this.destroyMesh();
        int i = this.cAircraft.getSelected();
        if (i < 0) return;
        try {
            Class class1 = (Class) Property.value(this.airNames.get(i), "airClass", null);
            String s = (String) this.countryLst.get(this.cCountry.getSelected());
            String s1 = Regiment.getCountryFromBranch(s);
            String s2 = Aircraft.getPropertyMesh(class1, s1);
            this.actorMesh = new ActorSimpleHMesh(s2);
            double d = this.actorMesh.hierMesh().visibilityR();
            Aircraft.prepareMeshCamouflage(s2, this.actorMesh.hierMesh());
            this.actorMesh.pos.setAbs(new Orient(90F, 0.0F, 0.0F));
            this.actorMesh.pos.reset();
            d *= Math.cos(0.26179938779914941D) / Math.sin(this.camera3D.FOV() * 3.1415926535897931D / 180D / 2D);
            this.camera3D.pos.setAbs(new Point3d(d, 0.0D, 0.0D), new Orient(180F, 0.0F, 0.0F));
            this.camera3D.pos.reset();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

    private void destroyMesh() {
        if (Actor.isValid(this.actorMesh)) this.actorMesh.destroy();
        this.actorMesh = null;
        this.destroyWeaponMeshs();
    }

    private void destroyWeaponMeshs() {
        for (int i = 0; i < this.weaponMeshs.size(); i++) {
            ActorMesh actormesh = (ActorMesh) this.weaponMeshs.get(i);
            if (Actor.isValid(actormesh)) actormesh.destroy();
        }

        this.weaponMeshs.clear();
    }

    private void prepareMesh() {
        if (!Actor.isValid(this.actorMesh)) return;
        int i = this.cAircraft.getSelected();
        if (i < 0) return;
        switch (stateId) {
            default:
                break;

            case 3: // '\003'
                Class class1 = GUINetAircraft.selectedAircraftClass();
                Regiment regiment = GUINetAircraft.selectedRegiment();
                String s = regiment.country();
                String s2 = GUINetAircraft.selectedWingName();
                PaintScheme paintscheme1 = Aircraft.getPropertyPaintScheme(class1, s);
                if (paintscheme1 == null) return;
                int l = s2.charAt(s2.length() - 2) - 48;
                int i1 = s2.charAt(s2.length() - 1) - 48;
                int j1 = GUINetAircraft.selectedAircraftNumInWing();
                UserCfg usercfg1 = World.cur().userCfg;
                paintscheme1.prepare(class1, this.actorMesh.hierMesh(), regiment, l, i1, j1, usercfg1.netNumberOn);
                break;

            case 0: // '\0'
            case 1: // '\001'
            case 2: // '\002'
                Class class2 = (Class) Property.value(this.airNames.get(i), "airClass", null);
                PaintScheme paintscheme = Aircraft.getPropertyPaintScheme(class2, (String) this.countryLst.get(this.cCountry.getSelected()));
                if (paintscheme == null) return;
                int k = this.cCountry.getSelected();
                if (k < 0) return;
                String s3 = (String) this.countryLst.get(k);
                ArrayList arraylist1 = (ArrayList) this.regList.get(s3);
                if (arraylist1 == null) return;
                Object obj = arraylist1.get(this.cRegiment.getSelected());
                Object obj1 = null;
                if (obj instanceof Regiment) {
                    obj1 = obj;
                    NetUserRegiment netuserregiment = ((NetUser) NetEnv.host()).netUserRegiment;
                    ((NetUser) NetEnv.host()).setUserRegiment(netuserregiment.branch(), "", netuserregiment.aid(), netuserregiment.gruppeNumber());
                } else {
                    UserRegiment userregiment = (UserRegiment) obj;
                    ((NetUser) NetEnv.host()).setUserRegiment(userregiment.country, userregiment.fileName + ".bmp", userregiment.id, userregiment.gruppeNumber);
                    obj1 = ((NetUser) NetEnv.host()).netUserRegiment;
                }
                if (obj1 == null) return;
                if (this.isNet()) {
                    if (this.cSquadron.getSelected() < 0) return;
                    UserCfg usercfg = World.cur().userCfg;
                    boolean flag = usercfg.netNumberOn;
                    if (stateId == 2) flag = true;
                    paintscheme.prepareNum(class2, this.actorMesh.hierMesh(), (Regiment) obj1, this.cSquadron.getSelected(), 0, usercfg.netTacticalNumber, flag);
                } else {
                    int k1 = this.planeName.charAt(this.planeName.length() - 2) - 48;
                    int l1 = this.planeName.charAt(this.planeName.length() - 1) - 48;
                    int i2 = Main.cur().currentMissionFile.get("Main", "playerNum", 0);
                    UserCfg usercfg2 = World.cur().userCfg;
                    paintscheme.prepare(class2, this.actorMesh.hierMesh(), (Regiment) obj1, k1, l1, i2, usercfg2.netNumberOn);
                }
                break;

            case 4: // '\004'
                Class class3 = (Class) Property.value(this.airNames.get(i), "airClass", null);
                int j = this.cCountry.getSelected();
                if (j < 0) return;
                String s1 = (String) this.countryLst.get(j);
                ArrayList arraylist = (ArrayList) this.regList.get(s1);
                if (arraylist == null) return;
                Regiment regiment1 = (Regiment) arraylist.get(this.cRegiment.getSelected());
                if (regiment1 == null) return;
                PaintScheme paintscheme2 = Aircraft.getPropertyPaintScheme(class3, regiment1.country());
                if (paintscheme2 == null) return;
                paintscheme2.prepare(class3, this.actorMesh.hierMesh(), regiment1, this.quikSquadron, this.quikWing, this.quikCurPlane, this.quikNumberOn[this.quikCurPlane]);
                break;
        }
    }

    private void prepareWeapons() {
        this.destroyWeaponMeshs();
        if (!Actor.isValid(this.actorMesh)) return;
        int i = this.cAircraft.getSelected();
        if (i < 0) return;
        Class class1 = (Class) Property.value(this.airNames.get(i), "airClass", null);

        // TODO: Added by |ZUTI|
        // -----------------------------------------------------------------------------
        String as[] = ZutiSupportMethods_GUI.syncWeaponsLists(this, Aircraft.getWeaponsRegistered(class1));
        // -----------------------------------------------------------------------------

        // String as[] = Aircraft.getWeaponsRegistered(class1);
        if (as == null || as.length == 0) return;
        i = this.cWeapon.getSelected();
        if (i < 0 || i >= as.length) return;
        String as1[] = Aircraft.getWeaponHooksRegistered(class1);
        Aircraft._WeaponSlot a_lweaponslot[] = Aircraft.getWeaponSlotsRegistered(class1, as[i]);
        if (as1 == null || a_lweaponslot == null) return;
        
        // TODO: Added by SAS~Storebror: Dynamically update the plane's 3D according to the selected loadout
        // +++
        Class currentClass = class1;
        Method method = Reflection.getMethod(currentClass, "prepareWeapons", new Class[] {Class.class, HierMesh.class, String.class});
        while (method == null && currentClass != Object.class) {
            currentClass = currentClass.getSuperclass();
            method = Reflection.getMethod(currentClass, "prepareWeapons", new Class[] {Class.class, HierMesh.class, String.class});
        }
        if (method != null) {
            try {
                method.invoke(null, new Object[] {class1, this.actorMesh.hierMesh(), as[i]});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // ---
        
        for (int j = 0; j < as1.length; j++)
            if (as1[j] != null && a_lweaponslot[j] != null) {
                Class class2 = a_lweaponslot[j].clazz;
                if (!BombGun.class.isAssignableFrom(class2) || Property.containsValue(class2, "external")) {
                    String s = Property.stringValue(class2, "mesh", null);
                    if (s == null) {
                        Class class3 = (Class) Property.value(class2, "bulletClass", null);
                        if (class3 != null) s = Property.stringValue(class3, "mesh", null);
                    }
                    if (s != null) try {
                        ActorSimpleMesh actorsimplemesh = new ActorSimpleMesh(s);
                        actorsimplemesh.pos.setBase(this.actorMesh, new HookNamed(this.actorMesh, as1[j]), false);
                        float f = BombGun.getBombVShift(class1, class2);
                        if(f != 0.0F)
                        {
                            Point3d point3d = actorsimplemesh.pos.getRelPoint();
                            point3d.z += f;
                            actorsimplemesh.pos.setRel(point3d);
                        }
                        f = BombGun.getBombHShift(class1, class2);
                        if(f != 0.0F)
                        {
                            Point3d point3d1 = actorsimplemesh.pos.getRelPoint();
                            point3d1.x += f;
                            actorsimplemesh.pos.setRel(point3d1);
                        }
                        actorsimplemesh.pos.changeHookToRel();
                        actorsimplemesh.pos.resetAsBase();
                        this.weaponMeshs.add(actorsimplemesh);
                    } catch (Exception exception) {
                        System.out.println("Exception in GUIAirArming.prepareWeapons(). Aircraft class is " + class1.getName() + ".");
//                        System.out.println(exception.getMessage());
                        exception.printStackTrace();
                    }
                }
            }

    }

    private void prepareSkin() {
        int i = this.cSkin.getSelected();
        if (i < 0) return;
        Class class1 = (Class) Property.value(this.airNames.get(this.cAircraft.getSelected()), "airClass", null);
        String s = (String) this.countryLst.get(this.cCountry.getSelected());
        String s1 = Regiment.getCountryFromBranch(s);
        String s2 = Aircraft.getPropertyMesh(class1, s1);
        if (i == 0) {
            ((NetUser) NetEnv.host()).setSkin(null);
            Aircraft.prepareMeshCamouflage(s2, this.actorMesh.hierMesh());
        } else {
            String s3 = validateFileName(this.airName());
            String s4 = s3 + "/" + this.cSkin.get(i);
            String s5 = s2;
            int j = s5.lastIndexOf('/');
            if (j >= 0) s5 = s5.substring(0, j + 1) + "summer";
            else s5 = s5 + "summer";
            String s6 = "PaintSchemes/Cache/" + s3;
            try {
                File file = new File(HomePath.toFileSystemName(s6, 0));
                if (!file.isDirectory()) file.mkdir();
                else {
                    File afile[] = file.listFiles();
                    if (afile != null) for (int k = 0; k < afile.length; k++)
                        if (afile[k] != null) {
                            String s8 = afile[k].getName();
                            if (s8.regionMatches(true, s8.length() - 4, ".tg", 0, 3)) afile[k].delete();
                        }
                }
            } catch (Exception exception) {
                return;
            }
            String s7 = Main.cur().netFileServerSkin.primaryPath();
            if (!BmpUtils.bmp8PalTo4TGA4(s7 + "/" + s4, s5, s6)) return;
            Aircraft.prepareMeshCamouflage(s2, this.actorMesh.hierMesh(), s6);
            ((NetUser) NetEnv.host()).setSkin(s4);
        }
    }

    private void preparePilot() {
        int i = this.cPilot.getSelected();
        if (i < 0) return;
        if (i == 0) {
            Class class1 = (Class) Property.value(this.airNames.get(this.cAircraft.getSelected()), "airClass", null);
            String s1 = (String) this.countryLst.get(this.cCountry.getSelected());
            String s3 = Regiment.getCountryFromBranch(s1);
            String s5 = Aircraft.getPropertyMesh(class1, s3);
            String s7 = HomePath.concatNames(s5, "pilot1.mat");
            Aircraft.prepareMeshPilot(this.actorMesh.hierMesh(), 0, s7, "3do/plane/textures/pilot1.tga");
            ((NetUser) NetEnv.host()).setPilot(null);
        } else {
            String s = Main.cur().netFileServerPilot.primaryPath();
            String s2 = this.cPilot.get(i);
            String s4 = s2.substring(0, s2.length() - 4);
            String s6 = "PaintSchemes/Cache/Pilot" + s4 + ".mat";
            String s8 = "PaintSchemes/Cache/Pilot" + s4 + ".tga";
            if (!BmpUtils.bmp8PalToTGA3(s + "/" + s2, s8)) return;
            Aircraft.prepareMeshPilot(this.actorMesh.hierMesh(), 0, s6, s8);
            ((NetUser) NetEnv.host()).setPilot(s2);
        }
    }

    private void prepareNoseart() {
        int i = this.cNoseart.getSelected();
        if (i > 0) {
            String s = Main.cur().netFileServerNoseart.primaryPath();
            String s1 = this.cNoseart.get(i);
            String s2 = s1.substring(0, s1.length() - 4);
            String s3 = "PaintSchemes/Cache/Noseart0" + s2 + ".mat";
            String s4 = "PaintSchemes/Cache/Noseart0" + s2 + ".tga";
            String s5 = "PaintSchemes/Cache/Noseart1" + s2 + ".mat";
            String s6 = "PaintSchemes/Cache/Noseart1" + s2 + ".tga";
            if (!BmpUtils.bmp8PalTo2TGA4(s + "/" + s1, s4, s6)) return;
            Aircraft.prepareMeshNoseart(this.actorMesh.hierMesh(), s3, s5, s4, s6);
            ((NetUser) NetEnv.host()).setNoseart(s1);
        }
    }

    private float clampValue(GWindowEditControl gwindoweditcontrol, float f, float f1, float f2) {
        String s = gwindoweditcontrol.getValue();
        try {
            f = Float.parseFloat(s);
        } catch (Exception exception) {}
        if (f < f1) f = f1;
        if (f > f2) f = f2;
        gwindoweditcontrol.setValue("" + f, false);
        return f;
    }

    public GUIAirArming(GWindowRoot gwindowroot) {
        super(55);
        this.airNames = new ArrayList();
        this.weaponNames = new ArrayList();
        this.regList = new HashMapExt();
        this.regHash = new HashMapExt();
        this.countryLst = new ArrayList();
        this.bEnableWeaponChange = true;
        this.quikPlayer = true;
        this.quikArmy = 1;
        this.quikPlanes = 4;
        this.quikPlane = "Il-2_M3";
        this.quikWeapon = "default";
        this.quikCurPlane = 0;
        this.quikRegiment = "r01";
        this.quikSquadron = 0;
        this.quikWing = 0;
        this.quikFuel = 100;
        this.quikListPlane = new ArrayList();
        this.playerNum = -1;
        this.weaponMeshs = new ArrayList();
        this.animateMeshA = 0.0F;
        this.animateMeshT = 0.0F;
        this._orient = new Orient();
        this.client = (GUIClient) gwindowroot.create(new GUIClient());
        this.dialogClient = (DialogClient) this.client.create(new DialogClient());
        this.infoMenu = (GUIInfoMenu) this.client.create(new GUIInfoMenu());
        this.infoMenu.info = this.i18n("neta.info");
        this.infoName = (GUIInfoName) this.client.create(new GUIInfoName());
        com.maddox.gwindow.GTexture gtexture = ((GUILookAndFeel) gwindowroot.lookAndFeel()).buttons2;
        this.wMashineGun = (GWindowEditControl) this.dialogClient.addControl(new GWindowEditControl(this.dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null));
        this.wMashineGun.bNumericOnly = this.wMashineGun.bNumericFloat = true;
        this.wMashineGun.bDelayedNotify = true;
        this.wCannon = (GWindowEditControl) this.dialogClient.addControl(new GWindowEditControl(this.dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null));
        this.wCannon.bNumericOnly = this.wCannon.bNumericFloat = true;
        this.wCannon.bDelayedNotify = true;
        this.wRocket = (GWindowEditControl) this.dialogClient.addControl(new GWindowEditControl(this.dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null));
        this.wRocket.bNumericOnly = this.wRocket.bNumericFloat = true;
        this.wRocket.bDelayedNotify = true;
        this.wRocketDelay = (GWindowEditControl) this.dialogClient.addControl(new GWindowEditControl(this.dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null));
        this.wRocketDelay.bNumericOnly = this.wRocketDelay.bNumericFloat = true;
        this.wRocketDelay.bDelayedNotify = true;
        this.wBombDelay = (GWindowEditControl) this.dialogClient.addControl(new GWindowEditControl(this.dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null));
        this.wBombDelay.bNumericOnly = this.wBombDelay.bNumericFloat = true;
        this.wBombDelay.bDelayedNotify = true;
        // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
        this.wBombFuze = (GWindowEditControl) this.dialogClient.addControl(new GWindowEditControl(this.dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null));
        this.wBombFuze.bNumericOnly = this.wBombFuze.bNumericFloat = true;
        this.wBombFuze.bDelayedNotify = true;
        // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
        this.wNumber = (GWindowEditControl) this.dialogClient.addControl(new GWindowEditControl(this.dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null) {

            public void keyboardKey(int i, boolean flag) {
                super.keyboardKey(i, flag);
                if (i == 10 && flag) this.notify(2, 0);
            }

        });
        this.wNumber.bNumericOnly = true;
        this.wNumber.bDelayedNotify = true;
        this.wNumber.align = 1;
        this.sNumberOn = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.cFuel = (GWindowComboControl) this.dialogClient.addControl(new GWindowComboControl(this.dialogClient, 0.0F, 0.0F, 1.0F));
        this.cFuel.setEditable(false);
        this.cFuel.add("10");
        this.cFuel.add("20");
        this.cFuel.add("30");
        this.cFuel.add("40");
        this.cFuel.add("50");
        this.cFuel.add("60");
        this.cFuel.add("70");
        this.cFuel.add("80");
        this.cFuel.add("90");
        this.cFuel.add("100");
        this.cAircraft = (GWindowComboControl) this.dialogClient.addControl(new GWindowComboControl(this.dialogClient, 0.0F, 0.0F, 1.0F));
        this.cAircraft.setEditable(false);
        this.cAircraft.listVisibleLines = 24;// MW Increased the size of the drop down list
        this.cWeapon = (GWindowComboControl) this.dialogClient.addControl(new GWindowComboControl(this.dialogClient, 0.0F, 0.0F, 1.0F));
        this.cWeapon.setEditable(false);
        this.cCountry = (GWindowComboControl) this.dialogClient.addControl(new GWindowComboControl(this.dialogClient, 0.0F, 0.0F, 1.0F));
        this.cCountry.setEditable(false);
        this.cRegiment = (GWindowComboControl) this.dialogClient.addControl(new GWindowComboControl(this.dialogClient, 0.0F, 0.0F, 1.0F));
        this.cRegiment.setEditable(false);
        this.cSkin = (GWindowComboControl) this.dialogClient.addControl(new GWindowComboControl(this.dialogClient, 0.0F, 0.0F, 1.0F));
        this.cSkin.setEditable(false);
        this.cPilot = (GWindowComboControl) this.dialogClient.addControl(new GWindowComboControl(this.dialogClient, 0.0F, 0.0F, 1.0F));
        this.cPilot.setEditable(false);
        this.cSquadron = (GWindowComboControl) this.dialogClient.addControl(new GWindowComboControl(this.dialogClient, 0.0F, 0.0F, 1.0F));
        this.cSquadron.setEditable(false);
        this.cSquadron.editBox.align = this.cSquadron.align = 1;
        this.cSquadron.add("1");
        this.cSquadron.add("2");
        this.cSquadron.add("3");
        this.cSquadron.add("4");
        this.cPlane = (GWindowComboControl) this.dialogClient.addControl(new GWindowComboControl(this.dialogClient, 0.0F, 0.0F, 1.0F));
        this.cPlane.setEditable(false);
        this.cNoseart = (GWindowComboControl) this.dialogClient.addControl(new GWindowComboControl(this.dialogClient, 0.0F, 0.0F, 1.0F));
        this.cNoseart.setEditable(false);
        this.bBack = (GUIButton) this.dialogClient.addEscape(new GUIButton(this.dialogClient, gtexture, 0.0F, 96F, 48F, 48F));
        // MW Modified BEGIN: New button init
        this.bJoy = (GUIButton) this.dialogClient.addControl(new GUIButton(this.dialogClient, gtexture, 0.0F, 48F, 48F, 48F));
        // MW Modified END
        this.createRender();
        this.dialogClient.activateWindow();
        this.client.hideWindow();

        // TODO: Added by |ZUTI|
        this.sZutiMultiCrew = (com.maddox.il2.gui.GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sZutiMultiCrewAnytime = (com.maddox.il2.gui.GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
    }

    // TODO: |ZUTI| variables and methods
    // ------------------------------------------------------------------------
    private BornPlace     zutiSelectedBornPlace = null;
    public GUISwitchBox3  sZutiMultiCrew;
    public GUISwitchBox3  sZutiMultiCrewAnytime;
    // ------------------------------------------------------------------------
    // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
    private static String bombFuzeCaption       = "Bomb Fuze";
    // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
}