/* 410 class */
package com.maddox.il2.game;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Tuple3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Army;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.IconDraw;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.engine.Mesh;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.engine.Render;
import com.maddox.il2.engine.TTFont;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.game.order.Order;
import com.maddox.il2.game.order.OrderAnyone_Help_Me;
import com.maddox.il2.game.order.ZutiOrder_EjectGunner;
import com.maddox.il2.game.order.ZutiOrder_EngineRepair;
import com.maddox.il2.game.order.ZutiOrder_Loadout;
import com.maddox.il2.game.order.ZutiOrder_TransferControls;
import com.maddox.il2.net.NetUser;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.air.ZutiSupportMethods_Air;
import com.maddox.il2.objects.sounds.VoiceBase;
import com.maddox.il2.objects.weapons.Torpedo;
import com.maddox.il2.objects.weapons.TorpedoGun;
import com.maddox.rts.LDRres;
import com.maddox.rts.NetEnv;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.RTSConf;
import com.maddox.rts.Time;

public class HUD {
    private static final int  lenLogBuf             = 6;
    private static final long logTimeLife           = 10000L;
    private static final long logTimeFire           = 5000L;

    public boolean            bDrawAllMessages      = true;
    public boolean            bDrawVoiceMessages    = true;
    private boolean           bNoSubTitles          = false;
    private int               subTitlesLines        = 11;
    private boolean           bNoHudLog             = false;
    private Main3D            main3d;
    private int               viewDX;
    private int               viewDY;
    long                      timeLoadLimit         = 0L;
    int                       cnt;
    private String[][]        renderSpeedSubstrings = { { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null } };
    private int               iDrawSpeed            = 1;
    private int               lastDrawSpeed         = -1;
    private Order[]           order;
    private String[]          orderStr;
    public ResourceBundle     resOrder;
    public ResourceBundle     resMsg;
    private int               msgX0;
    private int               msgY0;
    private int               msgDX;
    private int               msgDY;
    private int               msgSIZE;
    private int               msgSpaceLen;
    private ArrayList         msgLines              = new ArrayList();
    private int[][]           msgColor              = { { -822083329, -822083329, -822083329, -822059105, -822067233, -822083329, -822083329, -822083329, -822083329 },
            { -805371904, -805371904, -805371904, -811639040, -807452928, -805371904, -805371904, -805371904, -805371904 } };
    private int               trainingX0;
    private int               trainingY0;
    private int               trainingDX;
    private int               trainingDY;
    private int               trainingSIZE;
    private int               trainingSpaceLen;
    private ArrayList         trainingLines         = new ArrayList();
    private static int        idLog                 = 1;
    public ResourceBundle     resLog;
    private String            logRightBottom;
    private long              logRightBottomTime;
    public static final long  logCenterTimeLife     = 5000L;
    private String            logCenter;
    private long              logCenterTime;
    // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
    private static long       logTriggerTime;
    private static ArrayList  msgWaitingList;
    // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
    private String            logIntro;
    private String            logIntroESC;
    private TTFont            fntCenter;
    private boolean           bCoopTimeStart        = false;
    private long              coopTimeStart;
    private String[]          logBuf                = new String[lenLogBuf];
    private String[]          logBufStr             = new String[lenLogBuf];
    private int[]             logBufId              = new int[lenLogBuf];
    private long[]            logTime               = new long[lenLogBuf];
    private int               logPtr                = 0;
    private int               logLen                = 0;
    private boolean           bDrawNetStat          = false;
    private int               pageNetStat           = 0;
    private int               pageSizeNetStat       = 0;
    ArrayList                 statUsers             = new ArrayList();
    private ArrayList         pointers              = new ArrayList();
    private int               nPointers             = 0;
    private Mat               spritePointer;
    public boolean            bDrawDashBoard        = true;
    private Point3d           _p                    = new Point3d();
    private Orient            _o                    = new Orient();
    private Orient            _o1                   = new Orient();
    private Orient            _oNull                = new Orient(0.0F, 0.0F, 0.0F);
    private Mat               spriteLeft;
    private Mat               spriteRight;
    // TODO: Disabled in 4.10.1
    // private Mat spriteG;
    private Mesh              meshNeedle1;
    private Mesh              meshNeedle2;
    private Mesh              meshNeedle3;
    private Mesh              meshNeedle4;
    private Mesh              meshNeedle5;
    private Mesh              meshNeedle6;
    private Mesh              meshNeedleMask;
    private TTFont            fntLcd;

    // TODO: +++ Additional Player Info Mod for Admins by SAS~Storebror +++
    private static boolean adminMode = false;

    public static boolean isAdminMode() {
        return adminMode;
    }

    public static void setAdminMode(boolean theAdminMode) {
        adminMode = theAdminMode;
    }
    // TODO: --- Additional Player Info Mod for Admins by SAS~Storebror ---

    // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
    static class MsgWaiting
    {

        int getLast()
        {
            return last;
        }

        String getMsg()
        {
            return msg;
        }

        String msg;
        int last;

        MsgWaiting(String s, int i)
        {
            msg = s;
            last = i;
        }
    }
    // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---

    static class Ptr {
        float x;
        float y;
        int   color;
        float alpha;
        float angle;

        public void set(float f, float f_0_, int i, float f_1_, float f_2_) {
            this.x = f;
            this.y = f_0_;
            this.color = i;
            this.alpha = f_1_;
            this.angle = (float) (f_2_ * 180.0F / Math.PI);
        }

        public Ptr(float f, float f_3_, int i, float f_4_, float f_5_) {
            this.set(f, f_3_, i, f_4_, f_5_);
        }
    }

    static class StatUser {
        NetUser  user;
        int      iNum;
        String   sNum;
        int      iPing;
        String   sPing;
        int      iScore;
        String   sScore;
        int      iArmy;
        String   sArmy;
        Aircraft aAircraft;
        String   sAircraft;
        String   sAircraftType;
    }

    static class MsgLine {
        String msg;
        int    iActor;
        int    army;
        long   time0;
        int    len;

        MsgLine(String string, int i, int i_6_, int i_7_, long l) {
            this.msg = string;
            this.len = i;
            this.iActor = i_6_;
            this.army = i_7_;
            this.time0 = l;
        }
    }

    public static int drawSpeed() {
        return Main3D.cur3D().hud.iDrawSpeed;
    }

    public static void setDrawSpeed(int i) {
        Main3D.cur3D().hud.iDrawSpeed = i;
    }

    private final void renderSpeed() {
        // TODO: +++ Additional Player Info Mod for Admins by SAS~Storebror +++
        TTFont ttfont = TTFont.font[1];
        int i = ttfont.height();
        int j = 0xc00000ff;
        // TODO: --- Additional Player Info Mod for Admins by SAS~Storebror ---
        if (Actor.isValid(World.getPlayerAircraft()) && this.iDrawSpeed != 0 && this.bDrawAllMessages && (Main.cur().netServerParams == null || Main.cur().netServerParams.isShowSpeedBar())) {
            // TODO: +++ Additional Player Info Mod for Admins by SAS~Storebror +++
//            TTFont ttfont = TTFont.font[1];
//            int i = ttfont.height();
//            int i_8_ = -1073741569;
            // TODO: --- Additional Player Info Mod for Admins by SAS~Storebror ---
            int i_9_ = (int) (World.getPlayerFM().Or.getYaw() + 0.5F);
            i_9_ = i_9_ > 90 ? 450 - i_9_ : 90 - i_9_;
            boolean bool = false;
            float f = World.getPlayerFM().getLoadDiff();
            if (f <= World.getPlayerFM().getLimitLoad() * 0.25F && f > World.getPlayerFM().getLimitLoad() * 0.1F) {
                bool = true;
                this.cnt = 0;
                this.timeLoadLimit = 0L;
            } else if (f <= World.getPlayerFM().getLimitLoad() * 0.1F && Time.current() < this.timeLoadLimit) bool = false;
            else if (f <= World.getPlayerFM().getLimitLoad() * 0.1F && Time.current() >= this.timeLoadLimit) {
                bool = true;
                this.cnt++;
                if (this.cnt == 22) {
                    this.timeLoadLimit = 125L + Time.current();
                    this.cnt = 0;
                }
            } else {
                this.cnt = 0;
                this.timeLoadLimit = 0L;
            }
            String string;
            int i_10_;
            int i_11_;
            switch (this.iDrawSpeed) {
                default:
                    string = ".si";
                    i_10_ = (int) (World.getPlayerFM().getAltitude() * 0.1F) * 10;
                    i_11_ = (int) (3.6F * Pitot.Indicator((float) World.getPlayerFM().Loc.z, World.getPlayerFM().getSpeed()) * 0.1F) * 10;
                    break;
                case 2:
                    string = ".gb";
                    i_10_ = (int) (3.28084F * World.getPlayerFM().getAltitude() * 0.02F) * 50;
                    i_11_ = (int) (1.943845F * Pitot.Indicator((float) World.getPlayerFM().Loc.z, World.getPlayerFM().getSpeed()) * 0.1F) * 10;
                    break;
                case 3:
                    string = ".us";
                    i_10_ = (int) (3.28084F * World.getPlayerFM().getAltitude() * 0.02F) * 50;
                    i_11_ = (int) (2.236936F * Pitot.Indicator((float) World.getPlayerFM().Loc.z, World.getPlayerFM().getSpeed()) * 0.1F) * 10;
            }
            if (this.iDrawSpeed != this.lastDrawSpeed) try {
                this.renderSpeedSubstrings[0][0] = this.resLog.getString("HDG");
                this.renderSpeedSubstrings[1][0] = this.resLog.getString("ALT");
                this.renderSpeedSubstrings[2][0] = this.resLog.getString("SPD");
                this.renderSpeedSubstrings[3][0] = this.resLog.getString("G");
                this.renderSpeedSubstrings[0][1] = this.resLog.getString("HDG" + string);
                this.renderSpeedSubstrings[1][1] = this.resLog.getString("ALT" + string);
                this.renderSpeedSubstrings[2][1] = this.resLog.getString("SPD" + string);
                this.renderSpeedSubstrings[3][1] = this.resLog.getString("Ga");
            } catch (Exception exception) {
                this.renderSpeedSubstrings[0][0] = "HDG";
                this.renderSpeedSubstrings[1][0] = "ALT";
                this.renderSpeedSubstrings[2][0] = "SPD";
                this.renderSpeedSubstrings[3][0] = "G";
                this.renderSpeedSubstrings[0][1] = "";
                this.renderSpeedSubstrings[1][1] = "";
                this.renderSpeedSubstrings[2][1] = "";
                this.renderSpeedSubstrings[3][1] = "";
            }
            ttfont.output(j, 5.0F, 5.0F, 0.0F, this.renderSpeedSubstrings[0][0] + " " + i_9_ + " " + this.renderSpeedSubstrings[0][1]);
            if (!World.cur().diffCur.NoSpeedBar) {
                ttfont.output(j, 5.0F, 5 + i, 0.0F, this.renderSpeedSubstrings[1][0] + " " + i_10_ + " " + this.renderSpeedSubstrings[1][1]);
                ttfont.output(j, 5.0F, 5 + i + i, 0.0F, this.renderSpeedSubstrings[2][0] + " " + i_11_ + " " + this.renderSpeedSubstrings[2][1]);
                if (bool) ttfont.output(j, 5.0F, 5 + i + i + i, 0.0F, this.renderSpeedSubstrings[3][0]);
            }
        }
        // TODO: +++ Additional Player Info Mod for Admins by SAS~Storebror +++
        Actor actorCur = Main3D.cur3D().camera3D.pos.base();
        if (this.iDrawSpeed != 0 && actorCur != null && actorCur instanceof Aircraft && isAdminMode()) {
            Aircraft aircraftCur = (Aircraft) actorCur;
            String eaInfo = "EA: ";
            NetUser netuser = ((NetAircraft) aircraftCur).netUser();
            if (netuser == null) eaInfo += "AI";
            else eaInfo += netuser.uniqueName();
            DecimalFormat dfDegrees = new DecimalFormat("000");
            DecimalFormat dfGs = new DecimalFormat("0.00");
            eaInfo += " A" + (int) aircraftCur.FM.getAltitude();
            eaInfo += " S" + (int) aircraftCur.FM.getSpeedKMH();
            int direction = (int) (aircraftCur.FM.Or.getYaw() + 0.5F);
            direction = direction > 90 ? 450 - direction : 90 - direction;
            eaInfo += " D" + dfDegrees.format(direction);

            Vector3d plAccel = new Vector3d();

            plAccel.set(aircraftCur.FM.getAccel());
            plAccel.scale(0.102D);
            plAccel.z += 0.5D;
            aircraftCur.FM.Or.transformInv(plAccel);
            float f1 = 0.5F + (float) ((Tuple3d) plAccel).z;
            eaInfo += " G" + dfGs.format(f1);
            ttfont.output(j, 5F, 5 + i + i + i + i, 0.0F, eaInfo);
        }
        // TODO: --- Additional Player Info Mod for Admins by SAS~Storebror ---
    }

    public void clearSpeed() {
        this.iDrawSpeed = 1;
    }

    private void initSpeed() {
        this.iDrawSpeed = 1;
    }

    public static void order(Order[] orders) {
        if (Config.isUSE_RENDER()) Main3D.cur3D().hud._order(orders);
    }

    public void _order(Order[] orders) {
        if (orders == null) this.order = null;
        else {
            this.order = new Order[orders.length];
            this.orderStr = new String[orders.length];
            int i = World.getPlayerArmy();

            // TODO: Added by |ZUTI|
            // ------------------------------------------------------------------------------------------
            Aircraft netAc = World.getPlayerAircraft();
            String currentAcName = Property.stringValue(netAc.getClass(), "keyName");
            int playerArmy = World.getPlayerArmy();
            int loadoutId = 0;
            int engineId = 1;
            int gunnerId = 0;
            int transferId = 0;
            Map crew = ZutiAircraftCrewManagement.getAircraftCrew(netAc.name()).getCrewMap();
            // System.out.println("HUD - crew size: " + crew.size());
            int playerPlaneEngines = ZutiSupportMethods_Air.getNuberOfAircraftEngines(netAc);
            ZutiOrder_EjectGunner ejectOrder = null;
            ZutiOrder_TransferControls transferOrder = null;
            // ------------------------------------------------------------------------------------------

            for (int i_12_ = 0; i_12_ < this.order.length; i_12_++) {
                this.order[i_12_] = orders[i_12_];
                if (orders[i_12_] != null && this.order[i_12_].name(i) != null) {
                    String string = this.order[i_12_].name(i);
                    String string_13_ = null;
                    String string_14_ = World.getPlayerLastCountry();

                    // TODO: Added by |ZUTI|
                    // ------------------------------------------------------------------------------------------
                    // System.out.println("HUD - " + string + ", " + string_14_);
                    // System.out.println("HUD - " + order[i_12_].toString());
                    // System.out.println("==================================");

                    if (this.order[i_12_] instanceof ZutiOrder_Loadout) {
                        // System.out.println("Populating loadout id: " + loadoutId);
                        string = ZutiSupportMethods.getSelectedLoadoutForAircraft(ZutiSupportMethods.isPilotLandedOnBPWithOwnRRRSettings(netAc.FM), currentAcName, netAc.FM.Loc.x, netAc.FM.Loc.y, loadoutId, playerArmy, true);
                        loadoutId++;
                    } else if (this.order[i_12_] instanceof ZutiOrder_EngineRepair) {
                        if (engineId > playerPlaneEngines) string = "";
                        else string = "Engine " + engineId;

                        engineId++;
                    } else if (this.order[i_12_] instanceof ZutiOrder_EjectGunner) {
                        if (crew != null) {
                            string = (String) crew.get(new Integer(gunnerId));
                            if (string == null) string = "";

                            // System.out.println("HUD - gunner at >" + gunnerId + "< is >" + string + "<.");
                            ejectOrder = (ZutiOrder_EjectGunner) this.order[i_12_];
                            ejectOrder.setName(string);
                        }
                        gunnerId++;
                    } else if (this.order[i_12_] instanceof ZutiOrder_TransferControls) {
                        transferOrder = (ZutiOrder_TransferControls) this.order[i_12_];

                        if (transferId < 8) {
                            if (crew != null) string = (String) crew.get(new Integer(transferId));

                            if (string == null) string = "";

                            transferOrder.setName(string);
                        } else transferOrder.setIsRetakeCommand(true);

                        transferId++;
                    }
                    // ------------------------------------------------------------------------------------------

                    if (string_14_ != null) try {
                        string_13_ = this.resOrder.getString(string + "_" + string_14_);
                    } catch (Exception exception) {
                        /* empty */
                    }
                    if (string_13_ == null) try {
                        string_13_ = this.resOrder.getString(string);
                    } catch (Exception exception) {
                        string_13_ = string;
                    }
                    this.orderStr[i_12_] = string_13_;
                }
            }
        }
    }

    private void renderOrder() {
        if (this.order != null && this.bDrawAllMessages) {
            TTFont ttfont = TTFont.font[1];
            int i = (int) (this.viewDX * 0.05);
            int i_15_ = ttfont.height();
            int i_16_ = this.viewDY - 2 * ttfont.height();
            String string = null;
            int i_17_ = i_16_;
            int i_18_ = 0;
            int i_19_ = 0;
            boolean bool = false;
            boolean grayedOut = false;
            for (int i_21_ = 0; i_21_ < this.order.length; i_21_++) {
                if (this.orderStr[i_21_] != null) {
                    if (this.order[i_21_] instanceof OrderAnyone_Help_Me) bool = true;
                    if (Main3D.cur3D().ordersTree.frequency() == null) grayedOut = true;
                    if (string != null) this.drawOrder(string, i, i_17_, i_19_ == 0 ? -1 : i_19_, i_18_, grayedOut);
                    i_19_ = i_21_;
                    string = this.orderStr[i_21_];
                    i_17_ = i_16_;
                    i_18_ = this.order[i_21_].attrib();

                    // TODO: Disabled by |ZUTI|: this makes vectoring options grayed out... why?
                    // --------------------------------------------------------------------------
                    /*
                     * if ((order[i_21_] instanceof OrderVector_To_Home_Base || order[i_21_] instanceof OrderVector_To_Target)) //&& Main.cur().mission.zutiRadar_DisableVectoring) grayedOut = true; else grayedOut = false;
                     */
                    // --------------------------------------------------------------------------
                }
                i_16_ -= i_15_;
            }
            if (Main3D.cur3D().ordersTree.frequency() == null) grayedOut = true;
            if (string != null) this.drawOrder(string, i, i_17_, 0, i_18_, grayedOut);
            if (bool) {
                String[] strings = Main3D.cur3D().ordersTree.getShipIDs();
                for (int i_22_ = 0; i_22_ < strings.length; i_22_++) {
                    if (i_22_ == 0 && strings[i_22_] != null) {
                        i_16_ -= i_15_;
                        i_16_ -= i_15_;
                        this.drawShipIDs(this.resOrder.getString("ShipIDs"), i, i_16_);
                        i_16_ -= i_15_;
                    }
                    if (strings[i_22_] != null) {
                        this.drawShipIDs(strings[i_22_], i, i_16_);
                        i_16_ -= i_15_;
                    }
                }
                this.drawTorpedoParameters(i, i_16_, i_15_);
            }
        }
    }

    // TODO: Storebror: Show Torpedo Drop Parameters
    private void drawTorpedoParameters(int i, int j, int k) {
        Aircraft theTorpedoCarrier = World.getPlayerAircraft();
        if (theTorpedoCarrier == null) return;
        j -= k;
        j -= k;
        Class theTorpedoClass = null;
        try {
            for (int l = 0; l < theTorpedoCarrier.FM.CT.Weapons.length; l++) {
                if (theTorpedoCarrier.FM.CT.Weapons[l] != null) for (int l1 = 0; l1 < theTorpedoCarrier.FM.CT.Weapons[l].length; l1++)
                    if (theTorpedoCarrier.FM.CT.Weapons[l][l1] != null && theTorpedoCarrier.FM.CT.Weapons[l][l1] instanceof TorpedoGun) {
                        Class theBulletClass = ((TorpedoGun) theTorpedoCarrier.FM.CT.Weapons[l][l1]).bulletClass();
                        if (Torpedo.class.isAssignableFrom(theBulletClass)) {
                            theTorpedoClass = theBulletClass;
                            break;
                        }
                    }
                if (theTorpedoClass != null) break;
            }
        } catch (Exception exception) {
            EventLog.type("Exception in drawTorpedoParameters: " + exception.getMessage());
        }
        if (theTorpedoClass == null) return;
        float dropAltitude = Property.floatValue(theTorpedoClass, "dropAltitude");
        float dropSpeed = Property.floatValue(theTorpedoClass, "dropSpeed");
        float impactSpeed = Property.floatValue(theTorpedoClass, "impactSpeed");
        float impactAngleMin = Property.floatValue(theTorpedoClass, "impactAngleMin");
        float impactAngleMax = Property.floatValue(theTorpedoClass, "impactAngleMax");

        String alt = "";
        String speed = "";

        switch (this.iDrawSpeed) {
            default:
                impactSpeed *= 3.6F;
                alt = "m";
                speed = "km/h";
                break;
            case 2:
                impactSpeed *= 1.943845F;
                dropSpeed *= 0.539957F;
                dropAltitude *= 3.28084F;
                alt = "ft";
                speed = "kts";
                break;
            case 3:
                impactSpeed *= 2.236936F;
                dropSpeed *= 0.621371F;
                dropAltitude *= 3.28084F;
                alt = "ft";
                speed = "mph";
                break;
        }
//        this.drawTorpedoParameter(
//                "h(drop)=" + (int) dropAltitude + alt + ", v(drop)=" + (int) dropSpeed + speed + ", v(impact)(max)=" + (int) impactSpeed + speed + ", impact\u00B7(min)=" + (int) impactAngleMin + "\u00B7, impact\u00B7(max)=" + (int) impactAngleMax + "\u00B7", i, j);
        this.drawTorpedoParameter(
                "h(drop)=" + (int) dropAltitude + alt + ", v(drop)=" + (int) dropSpeed + speed + ", v(impact)(max)=" + (int) impactSpeed + speed + ", impact°(min)=" + (int) impactAngleMin + "°, impact°(max)=" + (int) impactAngleMax + "°", i, j);
    }

    private void drawTorpedoParameter(String string, int i, int j) {
        int color = -16776961;
        TTFont ttfont = TTFont.font[1];
        ttfont.output(color, i, j, 0.0F, string);
    }

    public void logTorpedoImpact(float speedValue, float angle) {
        String speed = "";
        switch (this.iDrawSpeed) {
            default:
                speedValue *= 3.6F;
                speed = "km/h";
                break;
            case 2:
                speedValue *= 1.943845F;
                speed = "kts";
                break;
            case 3:
                speedValue *= 2.236936F;
                speed = "mph";
                break;
        }
        log("Torpedo impact: " + (int) speedValue + speed + ", " + (int) angle + "\u00B7");
    }

    private void drawShipIDs(String string, int i, int i_23_) {
        int i_24_ = -16776961;
        TTFont ttfont = TTFont.font[1];
        ttfont.output(i_24_, i, i_23_, 0.0F, string);
    }

    private void drawOrder(String string, int i, int i_25_, int i_26_, int i_27_, boolean grayedOut) {
        int color = -16776961;
        if ((i_27_ & 0x1) != 0) color = -16777089;
        else if ((i_27_ & 0x2) != 0) color = -16744449;
        TTFont ttfont = TTFont.font[1];
        if (grayedOut) color = 2139062143;

        if (i_26_ >= 0) ttfont.output(color, i, i_25_, 0.0F, "" + i_26_ + ". " + string);
        else ttfont.output(color, i, i_25_, 0.0F, string);
    }

    public void clearOrder() {
        this.order = null;
    }

    private void initOrder() {
        this.clearOrder();
        this.resOrder = ResourceBundle.getBundle("i18n/hud_order", RTSConf.cur.locale, LDRres.loader());
    }

    public static void message(int[] is, int i, int i_29_, boolean bool) {
        if (Config.isUSE_RENDER()) Main3D.cur3D().hud._message(is, i, i_29_, bool);
    }

    public void _message(int[] is, int i, int i_30_, boolean bool) {
        if (this.bDrawVoiceMessages && !this.bNoSubTitles && i >= 1 && i <= 9 && i_30_ >= 1 && i_30_ <= 2 && is != null) {
            TTFont ttfont = TTFont.font[1];
            for (int i_31_ = 0; i_31_ < is.length && is[i_31_] != 0; i_31_++) {
                String string = VoiceBase.vbStr[is[i_31_]];
                try {
                    String string_32_ = this.resMsg.getString(string);
                    if (string_32_ != null) string = string_32_;
                } catch (Exception exception) {
                    /* empty */
                }
                StringTokenizer stringtokenizer = new StringTokenizer(string);
                while (stringtokenizer.hasMoreTokens()) {
                    String string_33_ = stringtokenizer.nextToken();
                    int i_34_ = (int) ttfont.width(string_33_);
                    if (this.msgLines.size() == 0) this.msgLines.add(new MsgLine(string_33_, i_34_, i, i_30_, Time.current()));
                    else {
                        MsgLine msgline = (MsgLine) this.msgLines.get(this.msgLines.size() - 1);
                        if (msgline.iActor == i && msgline.army == i_30_ && !bool) {
                            int i_35_ = msgline.len + this.msgSpaceLen + i_34_;
                            if (i_35_ < this.msgDX) {
                                msgline.msg = msgline.msg + " " + string_33_;
                                msgline.len = i_35_;
                            } else this.msgLines.add(new MsgLine(string_33_, i_34_, i, i_30_, 0L));
                        } else this.msgLines.add(new MsgLine(string_33_, i_34_, i, i_30_, 0L));
                    }
                    bool = false;
                }
            }
            while (this.msgLines.size() > this.msgSIZE) {
                this.msgLines.remove(0);
                MsgLine msgline = (MsgLine) this.msgLines.get(0);
                msgline.time0 = Time.current();
            }
        }
    }

    private int msgColor(int i, int i_36_) {
        return this.msgColor[i_36_ - 1][i - 1];
    }

    private void renderMsg() {
        if (this.bDrawVoiceMessages && this.bDrawAllMessages) {
            int i = this.msgLines.size();
            if (i != 0) {
                MsgLine msgline = (MsgLine) this.msgLines.get(0);
                long l = msgline.time0 + msgline.msg.length() * 250;
                if (l < Time.current()) {
                    this.msgLines.remove(0);
                    if (--i == 0) return;
                    msgline = (MsgLine) this.msgLines.get(0);
                    msgline.time0 = Time.current();
                }
                TTFont ttfont = TTFont.font[1];
                int i_37_ = this.msgX0;
                int i_38_ = this.msgY0 + this.msgDY;
                for (int i_39_ = 0; i_39_ < i; i_39_++) {
                    msgline = (MsgLine) this.msgLines.get(i_39_);
                    ttfont.output(this.msgColor(msgline.iActor, msgline.army), i_37_, i_38_, 0.0F, msgline.msg);
                    i_38_ -= ttfont.height();
                }
            }
        }
    }

    public void clearMsg() {
        this.msgLines.clear();
    }

    public void resetMsgSizes() {
        this.clearMsg();
        TTFont ttfont = TTFont.font[1];
        this.msgX0 = (int) (this.viewDX * 0.3);
        this.msgDX = (int) (this.viewDX * 0.6);
        this.msgDY = ttfont.height() * this.subTitlesLines;
        if (this.msgDY > (int) (this.viewDY * 0.9)) this.msgDY = (int) (this.viewDY * 0.9);
        int i = this.msgDY / ttfont.height();
        if (i == 0) i = 1;
        this.msgDY = ttfont.height() * i;
        this.msgSIZE = i;
        this.msgY0 = (int) (this.viewDY * 0.95) - this.msgDY;
        this.msgSpaceLen = Math.round(ttfont.width(" "));
    }

    private void initMsg() {
        this.resetMsgSizes();
        this.resMsg = ResourceBundle.getBundle("i18n/hud_msg", RTSConf.cur.locale, LDRres.loader());
    }

    public static void training(String string) {
        if (Config.isUSE_RENDER()) Main3D.cur3D().hud._training(string);
    }

    public void _training(String string) {
        this.trainingLines.clear();
        if (string != null) {
            TTFont ttfont = TTFont.font[2];
            StringTokenizer stringtokenizer = new StringTokenizer(string);
            while (stringtokenizer.hasMoreTokens()) {
                String string_40_ = stringtokenizer.nextToken();
                int i = (int) ttfont.width(string_40_);
                if (this.trainingLines.size() == 0) this.trainingLines.add(string_40_);
                else {
                    String string_41_ = (String) this.trainingLines.get(this.trainingLines.size() - 1);
                    int i_42_ = (int) ttfont.width(string_41_);
                    int i_43_ = i_42_ + this.trainingSpaceLen + i;
                    if (i_43_ < this.trainingDX) this.trainingLines.set(this.trainingLines.size() - 1, string_41_ + " " + string_40_);
                    else {
                        if (this.trainingLines.size() >= this.trainingSIZE) break;
                        this.trainingLines.add(string_40_);
                    }
                }
            }
        }
    }

    private void renderTraining() {
        int i = this.trainingLines.size();
        if (i != 0) {
            TTFont ttfont = TTFont.font[2];
            int i_44_ = this.trainingX0;
            int i_45_ = this.trainingY0 + this.trainingDY;
            for (int i_46_ = 0; i_46_ < i; i_46_++) {
                String string = (String) this.trainingLines.get(i_46_);
                ttfont.output(-16776961, i_44_, i_45_, 0.0F, string);
                i_45_ -= ttfont.height();
            }
        }
    }

    public void clearTraining() {
        this.trainingLines.clear();
    }

    public void resetTrainingSizes() {
        this.clearTraining();
        TTFont ttfont = TTFont.font[2];
        this.trainingX0 = (int) (this.viewDX * 0.3);
        this.trainingDX = (int) (this.viewDX * 0.5);
        this.trainingY0 = (int) (this.viewDY * 0.5);
        this.trainingDY = (int) (this.viewDY * 0.45);
        int i = this.trainingDY / ttfont.height();
        if (i == 0) i = 1;
        this.trainingDY = ttfont.height() * i;
        this.trainingSIZE = i;
        this.trainingSpaceLen = Math.round(ttfont.width(" "));
    }

    private void initTraining() {
        this.resetTrainingSizes();
    }

    public static void intro(String string) {
        if (Config.isUSE_RENDER()) Main3D.cur3D().hud._intro(string);
    }

    public void _intro(String string) {
        if (string == null) this.logIntro = null;
        else this.logIntro = string;
    }

    public static void introESC(String string) {
        if (Config.isUSE_RENDER()) Main3D.cur3D().hud._introESC(string);
    }

    public void _introESC(String string) {
        if (string == null) this.logIntroESC = null;
        else this.logIntroESC = string;
    }

    public static int makeIdLog() {
        return idLog++;
    }

    public static void log(String string, Object[] objects) {
        if (Config.isUSE_RENDER()) log(0, string, objects);
    }

    public static void log(int i, String string, Object[] objects) {
        if (Config.isUSE_RENDER() && Main3D.cur3D().gameTrackPlay() == null) {
            if (Main3D.cur3D().gameTrackRecord() != null && objects != null && objects.length == 1 && objects[0] instanceof Integer) try {
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(0);
                netmsgguaranted.writeInt(i);
                netmsgguaranted.write255(string);
                netmsgguaranted.writeInt(((Integer) objects[0]).intValue());
                Main3D.cur3D().gameTrackRecord().postTo(Main3D.cur3D().gameTrackRecord().channel(), netmsgguaranted);
            } catch (Exception exception) {
                /* empty */
            }
            Main3D.cur3D().hud._log(i, string, objects);
        }
    }

    public void _log(int i, String string, Object[] objects) {
        if (!this.bNoHudLog) {
            int i_47_ = this.__log(i, string);
            String string_48_;
            try {
                string_48_ = this.resLog.getString(string);
            } catch (Exception exception) {
                string_48_ = string;
            }
            this.logBufStr[i_47_] = MessageFormat.format(string_48_, objects);
        }
    }

    public static void log(String string) {
        if (Config.isUSE_RENDER()) log(0, string);
    }

    public static void log(int i, String string) {
        log(i, string, true);
    }

    public static void log(int i, String string, boolean bool) {
        if (Config.isUSE_RENDER()) {
            if (bool) {
                if (Main3D.cur3D().gameTrackPlay() != null) return;
                if (Main3D.cur3D().gameTrackRecord() != null) try {
                    NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                    netmsgguaranted.writeByte(1);
                    netmsgguaranted.writeInt(i);
                    netmsgguaranted.write255(string);
                    Main3D.cur3D().gameTrackRecord().postTo(Main3D.cur3D().gameTrackRecord().channel(), netmsgguaranted);
                } catch (Exception exception) {
                    /* empty */
                }
            }
            Main3D.cur3D().hud._log(i, string);
        }
    }

    public void _log(int i, String string) {
        if (!this.bNoHudLog) {
            int i_49_ = this.__log(i, string);
            try {
                this.logBufStr[i_49_] = this.resLog.getString(string);
            } catch (Exception exception) {
                this.logBufStr[i_49_] = string;
            }
        }
    }

    public static void logRightBottom(String string) {
        if (Config.isUSE_RENDER() && Main3D.cur3D().gameTrackPlay() == null) {
            if (Main3D.cur3D().gameTrackRecord() != null) try {
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(2);
                netmsgguaranted.write255(string == null ? "" : string);
                Main3D.cur3D().gameTrackRecord().postTo(Main3D.cur3D().gameTrackRecord().channel(), netmsgguaranted);
            } catch (Exception exception) {
                /* empty */
            }
            Main3D.cur3D().hud._logRightBottom(string);
        }
    }

    public void _logRightBottom(String string) {
        if (!this.bNoHudLog) if (string == null) this.logRightBottom = null;
        else {
            try {
                this.logRightBottom = this.resLog.getString(string);
            } catch (Exception exception) {
                this.logRightBottom = string;
            }
            this.logRightBottomTime = Time.current();
        }
    }

    public static void logCenter(String string) {
        if (Config.isUSE_RENDER() && Main3D.cur3D().gameTrackPlay() == null) {
            if (Main3D.cur3D().gameTrackRecord() != null) try {
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(3);
                netmsgguaranted.write255(string == null ? "" : string);
                Main3D.cur3D().gameTrackRecord().postTo(Main3D.cur3D().gameTrackRecord().channel(), netmsgguaranted);
            } catch (Exception exception) {
                /* empty */
            }
            Main3D.cur3D().hud._logCenter(string);
        }
    }

    public void _logCenter(String string) {
        if (string == null) this.logCenter = null;
        else {
            try {
                this.logCenter = this.resLog.getString(string);
            } catch (Exception exception) {
                this.logCenter = string;
            }
            this.logCenterTime = Time.current();
        }
    }

    public static void logCoopTimeStart(long l) {
        if (Config.isUSE_RENDER()) Main3D.cur3D().hud._logCoopTimeStart(l);
    }

    public void _logCoopTimeStart(long l) {
        this.bCoopTimeStart = true;
        this.coopTimeStart = l;
    }

    public boolean netInputLog(int i, NetMsgInput netmsginput) throws IOException {
        switch (i) {
            case 0: {
                int i_50_ = netmsginput.readInt();
                String string = netmsginput.read255();
                Object[] objects = new Object[1];
                objects[0] = new Integer(netmsginput.readInt());
                this._log(i_50_, string, objects);
                break;
            }
            case 1: {
                int i_51_ = netmsginput.readInt();
                String string = netmsginput.read255();
                this._log(i_51_, string);
                break;
            }
            case 2: {
                String string = netmsginput.read255();
                if ("".equals(string)) string = null;
                this._logRightBottom(string);
                break;
            }
            case 3: {
                String string = netmsginput.read255();
                if ("".equals(string)) string = null;
                this._logCenter(string);
                break;
            }
        }
        return true;
    }

    public void clearLog() {
        this.logRightBottom = null;
        this.logPtr = 0;
        this.logLen = 0;
        this.logCenter = null;
        this.logIntro = null;
        this.logIntroESC = null;
        this.bCoopTimeStart = false;
    }

    private void initLog() {
        this.clearLog();
        this.resLog = ResourceBundle.getBundle("i18n/hud_log", RTSConf.cur.locale, LDRres.loader());
        this.fntCenter = TTFont.font[2];
    }

    private void renderLog() {
        long l = Time.current();
        if (this.bCoopTimeStart) {
            int i = (int) (this.coopTimeStart - Time.currentReal());
            if (i < 0) this.bCoopTimeStart = false;
            else if (this.bDrawAllMessages) {
                TTFont ttfont = this.fntCenter;
                String string = "" + (i + 500) / 1000;
                float f = ttfont.width(string);
                ttfont.output(0xff00ffff, (this.viewDX - f) / 2.0F, this.viewDY * 0.75F, 0.0F, string);
            }
        } else if (this.logIntro != null) {
            TTFont ttfont = this.fntCenter;
            float f = ttfont.width(this.logIntro);
            int i = 0xff000000;
            ttfont.output(i, (this.viewDX - f) / 2.0F, this.viewDY * 0.75F, 0.0F, this.logIntro);
        } else if (this.logCenter != null) if (l > this.logCenterTime + logCenterTimeLife) this.logCenter = null;
        else if (this.bDrawAllMessages) {
            TTFont ttfont = this.fntCenter;
            float f = ttfont.width(this.logCenter);
            int i = 0xff0000ff;
            int i_48_ = 255 - (int) ((l - this.logCenterTime) / 5000.0 * 255.0);
            i |= i_48_ << 8;
            ttfont.output(i, (this.viewDX - f) / 2.0F, this.viewDY * 0.75F, 0.0F, this.logCenter);
        }
        // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
        if(msgWaitingList.size() > 0) {
            TTFont ttfont1 = fntCenter;
            float f1 = ttfont1.width(((MsgWaiting)msgWaitingList.get(0)).getMsg());
            int j1 = 0xff0000ff;
            int i2 = 255 - (int)(((double)(l - logTriggerTime) / (double)((MsgWaiting)msgWaitingList.get(0)).getLast()) * 255D);
            j1 |= i2 << 8;
            float logPosX = ((float)viewDX - f1) / 2.0F;
            float logPosY = (float)viewDY * 0.75F - 3 * ttfont1.height();
            if(logTriggerTime != 0L && l > logTriggerTime + (long)((MsgWaiting)msgWaitingList.get(0)).getLast())
            {
//                System.out.println("HUD removing \"" + ((MsgWaiting)msgWaitingList.get(0)).getMsg() + "\" (" + ((MsgWaiting)msgWaitingList.get(0)).getLast() + "ms)");
                ttfont1.output(j1, logPosX, logPosY, 0.0F, "");
                msgWaitingList.remove(0);
                logTriggerTime = 0L;
            } else
            {
                if(logTriggerTime == 0L)
                    logTriggerTime = l;
                ttfont1.output(j1, logPosX , logPosY, 0.0F, ((MsgWaiting)msgWaitingList.get(0)).getMsg());
//                ttfont1.output(j1, logPosX , logPosY, 0.0F, ((MsgWaiting)msgWaitingList.get(0)).getMsg() + " " + logTriggerTime + "-" + (logTriggerTime + (long)((MsgWaiting)msgWaitingList.get(0)).getLast()));
            }
        }
        // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
        if (this.logIntroESC != null) {
            TTFont ttfont = TTFont.font[0];
            float f = ttfont.width(this.logIntroESC);
            int i = -1;
            ttfont.output(i, (this.viewDX - f) / 2.0F, this.viewDY * 0.05F, 0.0F, this.logIntroESC);
        }
        if (!Main3D.cur3D().aircraftHotKeys.isAfterburner()) this.logRightBottom = null;
        if (this.logRightBottom != null && this.bDrawAllMessages) {
            TTFont ttfont = TTFont.font[1];
            //int i = (int) (this.viewDX * 0.95);
            int i = this.viewDX - 5;
            int i_49_ = (int) ttfont.width(this.logRightBottom);
            //int i_50_ = (int) (this.viewDY * 0.45 - 3 * ttfont.height());
//            int i_50_ = (int) (this.viewDY * 0.22 - 3 * ttfont.height());
            int i_50_ = 5 + (lenLogBuf + 1) * ttfont.height();
            int i_51_ = 0xff0000ff;
            int i_52_ = (int) (510.0F * ((Time.current() - this.logRightBottomTime) % logTimeFire) / 5000.0F);
            i_52_ -= 255;
            if (i_52_ < 0) i_52_ = -i_52_;
            ttfont.output(i_51_ | i_52_ << 8, i - i_49_, i_50_, 0.0F, this.logRightBottom);
        }
        if (this.logLen != 0) {
            for (/**/; this.logLen > 0; this.logLen--) {
                if (l < this.logTime[this.logPtr] + logTimeLife) break;
                this.logPtr = (this.logPtr + 1) % lenLogBuf;
            }
            if (this.logLen != 0) {
                TTFont ttfont = TTFont.font[1];
                //int i = (int) (this.viewDX * 0.95);
                int i = this.viewDX - 5;
                int i_53_ = ttfont.height();
                //int i_54_ = (int) (this.viewDY * 0.45) - (lenLogBuf - this.logLen) * i_53_;
//                int i_54_ = (int) (this.viewDY * 0.12) - (lenLogBuf - this.logLen) * i_53_;
                int i_54_ = 5 + (this.logLen - 1) * i_53_;
                for (int i_55_ = 0; i_55_ < this.logLen; i_55_++) {
                    int i_56_ = (this.logPtr + i_55_) % lenLogBuf;
                    int i_57_ = 0xffff0000;
                    if (l < this.logTime[i_56_] + logCenterTimeLife) {
                        int i_58_ = (int) ((this.logTime[i_56_] + logCenterTimeLife - l) / 5000.0 * 255.0);
                        i_57_ |= i_58_ | i_58_ << 8;
                    }
                    float f = ttfont.width(this.logBufStr[i_56_]);
                    if (this.bDrawAllMessages) ttfont.output(i_57_, i - f, i_54_, 0.0F, this.logBufStr[i_56_]);
                    i_54_ -= i_53_;
                }
            }
        }
    }
//    ttfont.output(j, 5.0F, 5 + i, 0.0F, this.renderSpeedSubstrings[1][0] + " " + i_10_ + " " + this.renderSpeedSubstrings[1][1]);
//    ttfont.output(j, 5.0F, 5 + i + i, 0.0F, this.renderSpeedSubstrings[2][0] + " " + i_11_ + " " + this.renderSpeedSubstrings[2][1]);
//    if (bool) ttfont.output(j, 5.0F, 5 + i + i + i, 0.0F, this.renderSpeedSubstrings[3][0]);

    private int __log(int i, String string) {
        if (this.logLen > 0 && i != 0) {
            int i_59_ = (this.logPtr + this.logLen - 1) % lenLogBuf;
            if (this.logBufId[i_59_] == i) {
                this.logTime[i_59_] = Time.current();
                this.logBuf[i_59_] = string;
                return i_59_;
            }
        }
        if (this.logLen >= lenLogBuf) {
            this.logPtr = (this.logPtr + 1) % lenLogBuf;
            this.logLen = 2;
        }
        int i_60_ = (this.logPtr + this.logLen) % lenLogBuf;
        this.logBuf[i_60_] = string;
        this.logBufId[i_60_] = i;
        this.logTime[i_60_] = Time.current();
        this.logLen++;
        return i_60_;
    }

    private void syncStatUser(int i, NetUser netuser) {
        if (i == this.statUsers.size()) this.statUsers.add(new StatUser());
        StatUser statuser = (StatUser) this.statUsers.get(i);
        statuser.user = netuser;
        if (statuser.iNum != i + 1 || statuser.sNum == null) {
            statuser.iNum = i + 1;
            statuser.sNum = statuser.iNum + ".";
        }
        if (statuser.iPing != netuser.ping || statuser.sPing == null) {
            statuser.iPing = netuser.ping;
            statuser.sPing = "(" + statuser.iPing + ")";
        }
        int i_65_ = (int) netuser.stat().score;
        if (statuser.iScore != i_65_ || statuser.sScore == null) {
            statuser.iScore = i_65_;
            statuser.sScore = "" + statuser.iScore;
        }
        if (statuser.iArmy != netuser.getArmy() || statuser.sArmy == null) {
            statuser.iArmy = netuser.getArmy();
            statuser.sArmy = "(" + statuser.iArmy + ")" + I18N.army(Army.name(statuser.iArmy));
        }
        if (!Actor.isAlive(statuser.aAircraft) || statuser.aAircraft.netUser() != netuser || statuser.sAircraft == null) {
            Aircraft aircraft = netuser.findAircraft();
            statuser.aAircraft = aircraft;
            if (aircraft == null) {
                statuser.sAircraft = "";
                statuser.sAircraftType = "";
            } else {
                statuser.sAircraft = aircraft.typedName();
                statuser.sAircraftType = I18N.plane(Property.stringValue(aircraft.getClass(), "keyName"));
            }
        }
    }

    private void syncNetStat() {
        this.syncStatUser(0, (NetUser) NetEnv.host());
        for (int i = 0; i < NetEnv.hosts().size(); i++)
            this.syncStatUser(i + 1, (NetUser) NetEnv.hosts().get(i));
        while (this.statUsers.size() > NetEnv.hosts().size() + 1)
            this.statUsers.remove(this.statUsers.size() - 1);
    }

    private int x1024(float f) {
        return (int) (this.viewDX / 1024.0F * f);
    }

    private int y1024(float f) {
        return (int) (this.viewDY / 768.0F * f);
    }

    public void startNetStat() {
        if (!this.bDrawNetStat && Mission.isPlaying() && !Mission.isSingle()) {
            this.syncNetStat();
            TTFont ttfont = TTFont.font[1];
            int i = ttfont.height() - ttfont.descender();
            int i_66_ = this.y1024(740.0F);
            int i_67_ = 2 * i;
            this.pageSizeNetStat = (i_66_ - i_67_) / i;
            this.bDrawNetStat = true;
        }
    }

    public void stopNetStat() {
        if (this.bDrawNetStat) {
            this.statUsers.clear();
            this.bDrawNetStat = false;
            this.pageNetStat = 0;
        }
    }

    public boolean isDrawNetStat() {
        return this.bDrawNetStat;
    }

    public void pageNetStat() {
        if (this.bDrawNetStat) {
            this.pageNetStat++;
            if (this.pageSizeNetStat * this.pageNetStat > this.statUsers.size()) this.pageNetStat = 0;
        }
    }

    public void renderNetStat() {
        // TODO: Changed by |ZUTI|: added last condition
        if (this.bDrawNetStat && Mission.isPlaying() && !Mission.isSingle() && !Mission.MDS_VARIABLES().zutiHud_DisableHudStatistics) {
            TTFont ttfont = TTFont.font[1];
            TTFont ttfont_64_ = TTFont.font[3];
            int lineHeight = ttfont.height() - ttfont.descender();
            int statHeight = this.y1024(740.0F);
            int pilotNumWidth = 0;
            int pingWidth = 0;
            int uniqueNameWidth = 0;
            int scoreWidth = 0;
            int armyWidth = 0;
            int acDesignationWidth = 0;
            // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View Replication
            int patchLevelWidth = 0;
            // ---

            for (int statUserIndex = this.pageSizeNetStat * this.pageNetStat; statUserIndex < this.pageSizeNetStat * (this.pageNetStat + 1) && statUserIndex < this.statUsers.size(); statUserIndex++) {
                // TODO: Changed by |ZUTI|
                StatUser statuser = (StatUser) this.statUsers.get(statUserIndex);
                int statValueWidth = 0;

                if (Mission.MDS_VARIABLES().zutiHud_ShowPilotNumber) {
                    statValueWidth = (int) ttfont.width(statuser.sNum);
                    if (pilotNumWidth < statValueWidth) pilotNumWidth = statValueWidth;
                }
                if (Mission.MDS_VARIABLES().zutiHud_ShowPilotPing) {
                    statValueWidth = (int) ttfont_64_.width(statuser.sPing);
                    if (pingWidth < statValueWidth) pingWidth = statValueWidth;
                }
                if (Mission.MDS_VARIABLES().zutiHud_ShowPilotName) {
                    statValueWidth = (int) ttfont.width(statuser.user.uniqueName());
                    if (uniqueNameWidth < statValueWidth) uniqueNameWidth = statValueWidth;
                }
                if (Mission.MDS_VARIABLES().zutiHud_ShowPilotScore) {
                    statValueWidth = (int) ttfont.width(statuser.sScore);
                    if (scoreWidth < statValueWidth) scoreWidth = statValueWidth;
                }
                if (Mission.MDS_VARIABLES().zutiHud_ShowPilotArmy) {
                    statValueWidth = (int) ttfont.width(statuser.sArmy);
                    if (armyWidth < statValueWidth) armyWidth = statValueWidth;
                }
                if (Mission.MDS_VARIABLES().zutiHud_ShowPilotACDesignation) {
                    statValueWidth = (int) ttfont.width(statuser.sAircraft);
                    if (acDesignationWidth < statValueWidth) acDesignationWidth = statValueWidth;
                }
                // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View Replication
                statValueWidth = (int) ttfont.width("UP " + statuser.user.getUltrapackVersion());
                boolean hasPatch = true;
                String patchLevel = statuser.user.getPatchLevel();
                if (patchLevel.equalsIgnoreCase("none")) patchLevel = "0";
                if (patchLevel.equalsIgnoreCase("0")) hasPatch = false;
                boolean hasHotfix = true;
                String hotfixVersion = statuser.user.getHotfixVersion();
                if (hotfixVersion.equalsIgnoreCase("none")) hotfixVersion = "0";
                if (hotfixVersion.equalsIgnoreCase("0")) hasHotfix = false;
                if (hasPatch || hasHotfix)
                    statValueWidth += (int) ttfont.width("." + patchLevel);
                if (hasHotfix)
                    statValueWidth += (int) ttfont.width("." + hotfixVersion);
                String selectorVersion = statuser.user.getSelectorVersion();
                if (!selectorVersion.equalsIgnoreCase("unknown")) statValueWidth += (int) ttfont.width(" / Selector " + selectorVersion);
                if (patchLevelWidth < statValueWidth) patchLevelWidth = statValueWidth;
                // ---
            }

            int pilotNumX = this.x1024(40.0F) + pilotNumWidth;
            int pingX = pilotNumX + pingWidth + this.x1024(16.0F);
            int uniqueNameX = pingX + uniqueNameWidth + this.x1024(16.0F);
            int scoreX = uniqueNameX + scoreWidth + this.x1024(16.0F);
            if (Mission.isCoop()) scoreX = uniqueNameX;
            int armyX = scoreX + armyWidth + this.x1024(16.0F);
            int acDesignationX = armyX + acDesignationWidth + this.x1024(16.0F);
            // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View Replication
            int patchLevelX = acDesignationX + patchLevelWidth + this.x1024(192.0F);
            // ---
            int statRowY = statHeight;
            for (int statUserIndex = this.pageSizeNetStat * this.pageNetStat; statUserIndex < this.pageSizeNetStat * (this.pageNetStat + 1) && statUserIndex < this.statUsers.size(); statUserIndex++) {
                StatUser statuser = (StatUser) this.statUsers.get(statUserIndex);
                statRowY -= lineHeight;
                int statColor = Army.color(statuser.iArmy);
                if (Mission.MDS_VARIABLES().zutiHud_ShowPilotNumber) ttfont.output(statColor, pilotNumX - ttfont.width(statuser.sNum), statRowY, 0.0F, statuser.sNum);
                if (Mission.MDS_VARIABLES().zutiHud_ShowPilotPing) ttfont_64_.output(-1, pingX - ttfont_64_.width(statuser.sPing) - this.x1024(4.0F), statRowY, 0.0F, statuser.sPing);
                if (Mission.MDS_VARIABLES().zutiHud_ShowPilotName) ttfont.output(statColor, pingX, statRowY, 0.0F, statuser.user.uniqueName());
                if (!Mission.isCoop() && Mission.MDS_VARIABLES().zutiHud_ShowPilotScore) ttfont.output(statColor, uniqueNameX, statRowY, 0.0F, statuser.sScore);
                if (Mission.MDS_VARIABLES().zutiHud_ShowPilotArmy) ttfont.output(statColor, scoreX, statRowY, 0.0F, statuser.sArmy);
                if (Mission.MDS_VARIABLES().zutiHud_ShowPilotACDesignation) ttfont.output(statColor, armyX, statRowY, 0.0F, statuser.sAircraft);
                if (Mission.MDS_VARIABLES().zutiHud_ShowPilotACType) ttfont.output(statColor, acDesignationX, statRowY, 0.0F, statuser.sAircraftType);
                // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View Replication

                int patchLevelColor = 0xFF0000C0;
                if (statuser.user.getUltrapackVersion().equalsIgnoreCase(Config.getVersionNumber())) switch (NetUser.patchLevelState(statuser.user.getPatchLevel())) {
                    case 1:
                        patchLevelColor = 0xFF00C000;
                        break;
                    case 0:
                        patchLevelColor = 0xFF00C0C0;
                        break;
                    default:
                        patchLevelColor = 0xFF0000C0;
                        break;
                }
//	                for (int checkPatchLevelsY = 0; checkPatchLevelsY < NetUser.PATCHLEVEL_Y.length; checkPatchLevelsY++) {
//	                    if (statuser.user.getPatchLevel().toLowerCase().startsWith(NetUser.PATCHLEVEL_Y[checkPatchLevelsY].toLowerCase())) {
//	                        patchLevelColor = 0xFF00C0C0;
//	                        break;
//	                    }
//	                }
//	                for (int checkPatchLevelsG = 0; checkPatchLevelsG < NetUser.PATCHLEVEL_G.length; checkPatchLevelsG++) {
//	                    if (statuser.user.getPatchLevel().toLowerCase().startsWith(NetUser.PATCHLEVEL_G[checkPatchLevelsG].toLowerCase())) {
//	                        patchLevelColor = 0xFF00C000;
//	                        break;
//	                    }
//	                }
                String versionInfo = "UP " + statuser.user.getUltrapackVersion();
                boolean hasPatch = true;
                String patchLevel = statuser.user.getPatchLevel();
                if (patchLevel.equalsIgnoreCase("none")) patchLevel = "0";
                if (patchLevel.equalsIgnoreCase("0")) hasPatch = false;
                boolean hasHotfix = true;
                String hotfixVersion = statuser.user.getHotfixVersion();
                if (hotfixVersion.equalsIgnoreCase("none")) hotfixVersion = "0";
                if (hotfixVersion.equalsIgnoreCase("0")) hasHotfix = false;
                if (hasPatch || hasHotfix)
                    versionInfo += "." + patchLevel;
                if (hasHotfix)
                    versionInfo += "." + hotfixVersion;
                String selectorVersion = statuser.user.getSelectorVersion();
                if (!selectorVersion.equalsIgnoreCase("unknown")) versionInfo += " / Selector " + selectorVersion;
//                System.out.println("Rendering Patch Level " + statuser.user.getPatchLevel() + " for user " + statuser.user.uniqueName() + " with color " + patchLevelColor);
                ttfont.output(patchLevelColor, patchLevelX, statRowY, 0.0F, versionInfo);
                // ---
            }
        }
    }

    public static void addPointer(float f, float f_92_, int i, float f_93_, float f_94_) {
        Main3D.cur3D().hud._addPointer(f, f_92_, i, f_93_, f_94_);
    }

    private void _addPointer(float f, float f_95_, int i, float f_96_, float f_97_) {
        if (this.nPointers == this.pointers.size()) this.pointers.add(new Ptr(f, f_95_, i, f_96_, f_97_));
        else {
            Ptr ptr = (Ptr) this.pointers.get(this.nPointers);
            ptr.set(f, f_95_, i, f_96_, f_97_);
        }
        this.nPointers++;
    }

//    private void renderPointers() {
//        if (this.nPointers != 0) {
//            float f = this.viewDX / 1024.0F;
//            int i = IconDraw.scrSizeX();
//            int i_98_ = IconDraw.scrSizeY();
//            for (int i_99_ = 0; i_99_ < this.nPointers; i_99_++) {
//                Ptr ptr = (Ptr) this.pointers.get(i_99_);
//                int i_100_ = (int) (64.0F * f * ptr.alpha);
//                IconDraw.setScrSize(i_100_, i_100_);
//                IconDraw.setColor(ptr.color & 0xffffff | (int) (ptr.alpha * 255.0F) << 24);
//                IconDraw.render(this.spritePointer, ptr.x, ptr.y, 90.0F - ptr.angle);
//            }
//            IconDraw.setScrSize(i, i_98_);
//            this.nPointers = 0;
//        }
//    }

    private void renderPointers()
    {
        if(nPointers == 0)
            return;
        float f = (float)viewDX / 1024F;
        float f1 = (float)viewDX / (float)viewDY;
        float f2 = 1.333333F / f1;
        f *= f2;
        int i = IconDraw.scrSizeX();
        int j = IconDraw.scrSizeY();
        for(int k = 0; k < nPointers; k++)
        {
            Ptr ptr = (Ptr)pointers.get(k);
            int l = (int)(64F * f * ptr.alpha);
            IconDraw.setScrSize(l, l);
            IconDraw.setColor(ptr.color & 0xffffff | (int)(ptr.alpha * 255F) << 24);
            IconDraw.render(spritePointer, ptr.x, ptr.y, 90F - ptr.angle);
        }

        IconDraw.setScrSize(i, j);
        nPointers = 0;
    }

    public void clearPointers() {
        this.nPointers = 0;
    }

    private void initPointers() {
        this.spritePointer = Mat.New("gui/game/hud/pointer.mat");
    }

    private void preRenderDashBoard() {
        if (Actor.isValid(World.getPlayerAircraft()) && Actor.isValid(this.main3d.cockpitCur)) {
            if (this.main3d.isViewOutside()) {
                if (this.main3d.viewActor() != World.getPlayerAircraft() || !this.main3d.cockpitCur.isNullShow()) return;
            } else if (this.main3d.isViewInsideShow()) return;
            if (this.bDrawDashBoard) {
                this.spriteLeft.preRender();
                this.spriteRight.preRender();
                // TODO: Disabled in 4.10.1
                // spriteG.preRender();
                this.meshNeedle1.preRender();
                this.meshNeedle2.preRender();
                this.meshNeedle3.preRender();
                this.meshNeedle4.preRender();
                this.meshNeedle5.preRender();
                this.meshNeedle6.preRender();
                this.meshNeedleMask.preRender();
            }
        }
    }

//    private void renderDashBoard() {
//        if (Actor.isValid(World.getPlayerAircraft()) && Actor.isValid(this.main3d.cockpitCur)) {
//            if (this.main3d.isViewOutside()) {
//                if (this.main3d.viewActor() != World.getPlayerAircraft() || !this.main3d.cockpitCur.isNullShow()) return;
//            } else if (this.main3d.isViewInsideShow()) return;
//            if (this.bDrawDashBoard) {
//                float f = this.viewDX;
//                float f_101_ = this.viewDY;
//                float f_102_ = f / 1024.0F;
//                float f_103_ = f_101_ / 768.0F;
//                Render.drawTile(0.0F, 0.0F, 256.0F * f_102_, 256.0F * f_103_, 0.0F, this.spriteLeft, -1, 0.0F, 1.0F, 1.0F, -1.0F);
//                Render.drawTile(768.0F * f_102_, 0.0F, 256.0F * f_102_, 256.0F * f_103_, 0.0F, this.spriteRight, -1, 0.0F, 1.0F, 1.0F, -1.0F);
//                // TODO: Disabled in 4.10.1
//                // Render.drawTile(200.0F * f_102_, 168.0F * f_103_, 64.0F * f_102_, 64.0F * f_103_, 0.0F, spriteG, -1, 0.0F, 1.0F, 1.0F, -1.0F);
//                Point3d point3d = World.getPlayerAircraft().pos.getAbsPoint();
//                Orient orient = World.getPlayerAircraft().pos.getAbsOrient();
//                float f_104_ = (float) (point3d.z - World.land().HQ(point3d.x, point3d.y));
//                this._p.x = 172.0F * f_102_;
//                this._p.y = 84.0F * f_103_;
//                this._o.set(this.cvt(f_104_, 0.0F, 10000.0F, 0.0F, 3600.0F), 0.0F, 0.0F);
//                this.meshNeedle2.setPos(this._p, this._o);
//                this.meshNeedle2.render();
//                this._o.set(this.cvt(f_104_, 0.0F, 10000.0F, 0.0F, 360.0F), 0.0F, 0.0F);
//                this.meshNeedle1.setPos(this._p, this._o);
//                this.meshNeedle1.render();
//                String string = "" + (int) (f_104_ + 0.5);
//                float f_105_ = this.fntLcd.width(string);
//                this.fntLcd.output(-1, 208.0F * f_102_ - f_105_, 70.0F * f_103_, 0.0F, string);
//                if (f_104_ > 90.0F) this.meshNeedle5.setScale(90.0F * f_102_);
//                else this.meshNeedle5.setScaleXYZ(90.0F * f_102_, 90.0F * f_102_, this.cvt(f_104_, 0.0F, 90.0F, 13.0F, 90.0F) * f_102_);
//                f_104_ = (float) World.getPlayerAircraft().getSpeed(null);
//                f_104_ *= 3.6F;
//                this._o.set(this.cvt(f_104_, 0.0F, 900.0F, 0.0F, 270.0F) + 180.0F, 0.0F, 0.0F);
//                this._p.x = 83.0F * f_102_;
//                this._p.y = 167.0F * f_103_;
//                this.meshNeedle2.setPos(this._p, this._o);
//                this.meshNeedle2.render();
//                string = "" + (int) (f_104_ + 0.5);
//                f_105_ = this.fntLcd.width(string);
//                this.fntLcd.output(-1, 104.0F * f_102_ - f_105_, 135.0F * f_103_, 0.0F, string);
//                for (f_104_ = orient.azimut() + 90.0F; f_104_ < 0.0F; f_104_ += 360.0F) {
//                    /* empty */
//                }
//                f_104_ %= 360.0F;
//                this._o.set(f_104_, 0.0F, 0.0F);
//                this._p.x = 939.0F * f_102_;
//                this._p.y = 167.0F * f_103_;
//                this.meshNeedle3.setPos(this._p, this._o);
//                this.meshNeedle3.render();
//                string = "" + (int) (f_104_ + 0.5);
//                f_105_ = this.fntLcd.width(string);
//                this.fntLcd.output(-1, 960.0F * f_102_ - f_105_, 216.0F * f_103_, 0.0F, string);
//                Orient orient_106_ = this.main3d.camera3D.pos.getAbsOrient();
//                this._p.x = 511.0F * f_102_;
//                this._p.y = 96.0F * f_103_;
//                if (orient_106_.tangage() < 0.0F) {
//                    this._o1.set(orient_106_);
//                    this._o1.increment(0.0F, 0.0F, 90.0F);
//                    this._o1.increment(0.0F, 90.0F, 0.0F);
//                    this._o.sub(this._oNull, this._o1);
//                    this.meshNeedle5.setPos(this._p, this._o);
//                    this.meshNeedle5.render();
//                }
//                this._o1.set(orient_106_);
//                this._o1.increment(0.0F, 0.0F, 90.0F);
//                this._o1.increment(0.0F, 90.0F, 0.0F);
//                this._o.sub(orient, this._o1);
//                this.meshNeedle4.setPos(this._p, this._o);
//                this.meshNeedle4.render();
//                if (orient_106_.tangage() >= 0.0F) {
//                    this._o1.set(orient_106_);
//                    this._o1.increment(0.0F, 0.0F, 90.0F);
//                    this._o1.increment(0.0F, 90.0F, 0.0F);
//                    this._o.sub(this._oNull, this._o1);
//                    this.meshNeedle5.setPos(this._p, this._o);
//                    this.meshNeedle5.render();
//                }
//                this._p.x = 851.0F * f_102_;
//                this._p.y = 84.0F * f_103_;
//                this._o1.set(orient);
//                this._o1.set(0.0F, -this._o1.tangage(), this._o1.kren());
//                this._o1.increment(0.0F, 0.0F, 90.0F);
//                this._o1.increment(0.0F, 90.0F, 0.0F);
//                this._o.sub(this._oNull, this._o1);
//                this.meshNeedle6.setPos(this._p, this._o);
//                this.meshNeedle6.render();
//                this._o.set(0.0F, 0.0F, 0.0F);
//                this.meshNeedleMask.setPos(this._p, this._o);
//                this.meshNeedleMask.render();
//                int i = (int) (World.getPlayerFM().getOverload() * 10.0F);
//                float f_107_ = i / 10.0F;
//                String string_108_ = "" + f_107_;
//                float f_109_ = this.fntLcd.width(string_108_);
//                if (World.getPlayerFM().getLoadDiff() < World.getPlayerFM().getLimitLoad() * 0.25F) this.fntLcd.output(-16776961, 215.0F * f_102_ - f_109_, 182.0F * f_103_, 0.0F, string_108_);
//                else if (i < 0) this.fntLcd.output(-16777216, 215.0F * f_102_ - f_109_, 182.0F * f_103_, 0.0F, string_108_);
//                else this.fntLcd.output(-1, 215.0F * f_102_ - f_109_, 182.0F * f_103_, 0.0F, string_108_);
//            }
//        }
//    }

    private void renderDashBoard()
    {
        if(!Actor.isValid(World.getPlayerAircraft()))
            return;
        if(!Actor.isValid(main3d.cockpitCur))
            return;
        if(main3d.isViewOutside())
        {
            if(main3d.viewActor() != World.getPlayerAircraft())
                return;
            if(!main3d.cockpitCur.isNullShow())
                return;
        } else
        if(main3d.isViewInsideShow())
            return;
        if(!bDrawDashBoard)
            return;
        float f = viewDX;
        float f1 = viewDY;
        float f2 = f / 1024F;
        float f3 = 1.333333F / (f / f1);
        float f4 = f1 / 768F;
        if(f / f1 < 1.0F)
        {
            float f5 = 1.333333F;
            f2 = f / (768F * f5);
            f3 = 0.75F / (f / f1);
            f4 = f1 / (1024F * f5);
        }
        float f6 = ((1024F - 256F * f3) * f2 - 768F * f2) * 4F;
        Render.drawTile(0.0F, 0.0F, 256F * f2 * f3, 256F * f4, 0.0F, spriteLeft, -1, 0.0F, 1.0F, 1.0F, -1F);
        Render.drawTile((1024F - 256F * f3) * f2, 0.0F, 256F * f2 * f3, 256F * f4, 0.0F, spriteRight, -1, 0.0F, 1.0F, 1.0F, -1F);
        Point3d point3d = World.getPlayerAircraft().pos.getAbsPoint();
        Orient orient = World.getPlayerAircraft().pos.getAbsOrient();
        float f7 = (float)(point3d.z - World.land().HQ(point3d.x, point3d.y));
        _p.x = 172F * f2 * f3;
        _p.y = 84F * f4;
        _o.set(cvt(f7, 0.0F, 10000F, 0.0F, 3600F), 0.0F, 0.0F);
        meshNeedle2.setPos(_p, _o);
        meshNeedle2.render();
        _o.set(cvt(f7, 0.0F, 10000F, 0.0F, 360F), 0.0F, 0.0F);
        meshNeedle1.setPos(_p, _o);
        meshNeedle1.render();
        String s = "" + (int)((double)f7 + 0.5D);
        float f9 = fntLcd.width(s);
        fntLcd.output(-1, 208F * f2 * f3 - f9, 70F * f4, 0.0F, s);
        if(f7 > 90F)
            meshNeedle5.setScale(90F * f2);
        else
            meshNeedle5.setScaleXYZ(90F * f2, 90F * f2, cvt(f7, 0.0F, 90F, 13F, 90F) * f2);
        f7 = (float)World.getPlayerAircraft().getSpeed(null);
        f7 *= 3.6F;
        _o.set(cvt(f7, 0.0F, 900F, 0.0F, 270F) + 180F, 0.0F, 0.0F);
        _p.x = 83F * f2 * f3;
        _p.y = 167F * f4;
        meshNeedle2.setPos(_p, _o);
        meshNeedle2.render();
        s = "" + (int)((double)f7 + 0.5D);
        f9 = fntLcd.width(s);
        fntLcd.output(-1, 104F * f2 * f3 - f9, 135F * f4, 0.0F, s);
        for(f7 = orient.azimut() + 90F; f7 < 0.0F; f7 += 360F);
        f7 %= 360F;
        _o.set(f7, 0.0F, 0.0F);
        _p.x = 939F * f2 * f3 + f6;
        _p.y = 167F * f4;
        meshNeedle3.setPos(_p, _o);
        meshNeedle3.render();
        s = "" + (int)((double)f7 + 0.5D);
        f9 = fntLcd.width(s);
        fntLcd.output(-1, (960F * f2 * f3 - f9) + f6, 216F * f4, 0.0F, s);
        Orient orient1 = main3d.camera3D.pos.getAbsOrient();
        _p.x = 511F * f2;
        _p.y = 96F * f4;
        if(orient1.tangage() < 0.0F)
        {
            _o1.set(orient1);
            _o1.increment(0.0F, 0.0F, 90F);
            _o1.increment(0.0F, 90F, 0.0F);
            _o.sub(_oNull, _o1);
            meshNeedle5.setPos(_p, _o);
            meshNeedle5.render();
        }
        _o1.set(orient1);
        _o1.increment(0.0F, 0.0F, 90F);
        _o1.increment(0.0F, 90F, 0.0F);
        _o.sub(orient, _o1);
        meshNeedle4.setPos(_p, _o);
        meshNeedle4.render();
        if(orient1.tangage() >= 0.0F)
        {
            _o1.set(orient1);
            _o1.increment(0.0F, 0.0F, 90F);
            _o1.increment(0.0F, 90F, 0.0F);
            _o.sub(_oNull, _o1);
            meshNeedle5.setPos(_p, _o);
            meshNeedle5.render();
        }
        _p.x = 851F * f2 * f3 + f6;
        _p.y = 84F * f4;
        _o1.set(orient);
        _o1.set(0.0F, -_o1.tangage(), _o1.kren());
        _o1.increment(0.0F, 0.0F, 90F);
        _o1.increment(0.0F, 90F, 0.0F);
        _o.sub(_oNull, _o1);
        meshNeedle6.setPos(_p, _o);
        meshNeedle6.render();
        _o.set(0.0F, 0.0F, 0.0F);
        meshNeedleMask.setPos(_p, _o);
        meshNeedleMask.render();
        int iOverload = (int)(World.getPlayerFM().getOverload() * 10F);
        float f8 = (float)iOverload / 10F;
        String s1 = "" + f8;
        float f10 = fntLcd.width(s1);
        if(World.getPlayerFM().getLoadDiff() < World.getPlayerFM().getLimitLoad() * 0.25F)
            fntLcd.output(0xff0000ff, 215F * f2 * f3 - f10, 182F * f4, 0.0F, s1);
        else
        if(iOverload < 0)
            fntLcd.output(0xff000000, 215F * f2 * f3 - f10, 182F * f4, 0.0F, s1);
        else
            fntLcd.output(-1, 215F * f2 * f3 - f10, 182F * f4, 0.0F, s1);
    }

    private float cvt(float f, float f_110_, float f_111_, float f_112_, float f_113_) {
        f = Math.min(Math.max(f, f_110_), f_111_);
        return f_112_ + (f_113_ - f_112_) * (f - f_110_) / (f_111_ - f_110_);
    }

    private void initDashBoard() {
        this.spriteLeft = Mat.New("gui/game/hud/hudleft.mat");
        this.spriteRight = Mat.New("gui/game/hud/hudright.mat");
        this.meshNeedle1 = new Mesh("gui/game/hud/needle1/mono.sim");
        this.meshNeedle2 = new Mesh("gui/game/hud/needle2/mono.sim");
        this.meshNeedle3 = new Mesh("gui/game/hud/needle3/mono.sim");
        this.meshNeedle4 = new Mesh("gui/game/hud/needle4/mono.sim");
        this.meshNeedle5 = new Mesh("gui/game/hud/needle5/mono.sim");
        this.meshNeedle6 = new Mesh("gui/game/hud/needle6/mono.sim");
        this.meshNeedleMask = new Mesh("gui/game/hud/needlemask/mono.sim");
        // TODO: Disabled in 4.10.1
        // spriteG = Mat.New("gui/game/hud/hudg.mat");
        this.fntLcd = TTFont.get("lcdnova");
        this.setScales();
    }

//    private void setScales() {
//        float f = this.viewDX;
//        float f_114_ = f / 1024.0F;
//        this.meshNeedle1.setScale(140.0F * f_114_);
//        this.meshNeedle2.setScale(140.0F * f_114_);
//        this.meshNeedle3.setScale(75.0F * f_114_);
//        this.meshNeedle4.setScale(100.0F * f_114_);
//        this.meshNeedle5.setScale(90.0F * f_114_);
//        this.meshNeedle6.setScale(150.0F * f_114_);
//        this.meshNeedleMask.setScale(150.0F * f_114_);
//    }

    private void setScales()
    {
        float f = viewDX;
        float f1 = f / 1024F;
        float f2 = 1.333333F / (f / (float)viewDY);
        if(viewDX / viewDY < 1)
        {
            f1 *= 0.75F;
            f2 *= 0.75F;
        }
        meshNeedle1.setScale(140F * f1 * f2);
        meshNeedle2.setScale(140F * f1 * f2);
        meshNeedle3.setScale(75F * f1 * f2);
        meshNeedle4.setScale(100F * f1 * f2);
        meshNeedle5.setScale(90F * f1 * f2);
        meshNeedle6.setScale(150F * f1 * f2);
        meshNeedleMask.setScale(150F * f1 * f2);
    }

    public void render() {
        this.renderSpeed();
        this.renderOrder();
        this.renderMsg();
        this.renderTraining();
        this.renderLog();
        this.renderDashBoard();
        this.renderPointers();
        this.renderNetStat();
    }

    public void preRender() {
        this.preRenderDashBoard();
    }

    public void resetGame() {
        this.setScales();
        this.clearSpeed();
        this.clearOrder();
        this.clearMsg();
        this.clearTraining();
        this.clearLog();
        this.clearPointers();
        this.stopNetStat();
    }

    public void contextResize(int i, int i_115_) {
        this.viewDX = this.main3d.renderHUD.getViewPortWidth();
        this.viewDY = this.main3d.renderHUD.getViewPortHeight();
        this.setScales();
        this.resetMsgSizes();
        this.resetTrainingSizes();
    }

    public HUD() {
        this.main3d = Main3D.cur3D();
        this.viewDX = this.main3d.renderHUD.getViewPortWidth();
        this.viewDY = this.main3d.renderHUD.getViewPortHeight();
        this.initSpeed();
        this.initOrder();
        this.initMsg();
        this.initTraining();
        this.initLog();
        this.initDashBoard();
        this.initPointers();
        this.bNoSubTitles = Config.cur.ini.get("game", "NoSubTitles", this.bNoSubTitles);
        this.subTitlesLines = Config.cur.ini.get("game", "SubTitlesLines", this.subTitlesLines);
        if (this.subTitlesLines < 1) this.subTitlesLines = 1;
        this.bNoHudLog = Config.cur.ini.get("game", "NoHudLog", this.bNoHudLog);
        // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
        msgWaitingList = new ArrayList();
        logTriggerTime = 0L;
        // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
    }
    
    // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
    public static void addMsgToWaitingList(String s, float f)
    {
//        System.out.println("addMsgToWaitingList (" + s + ", " + i + ")");
        if (msgWaitingList == null) msgWaitingList = new ArrayList();
        msgWaitingList.add(new MsgWaiting(s, (int)(f * 1000F)));
    }
    public static void clearWaitingList()
    {
//        System.out.println("clearWaitingList");
        if (msgWaitingList == null) msgWaitingList = new ArrayList();
        msgWaitingList.clear();
        logTriggerTime = 0L;
    }
    // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
}
