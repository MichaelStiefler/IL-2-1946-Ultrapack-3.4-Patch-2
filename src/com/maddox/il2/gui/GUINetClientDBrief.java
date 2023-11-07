/* 4.10.1 class */
package com.maddox.il2.gui;

import java.util.ResourceBundle;

import com.maddox.gwindow.GWindowMessageBox;
import com.maddox.gwindow.GWindowRoot;
import com.maddox.il2.ai.AirportCarrier;
import com.maddox.il2.ai.Army;
import com.maddox.il2.ai.UserCfg;
import com.maddox.il2.ai.World;
import com.maddox.il2.game.GameState;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.net.BornPlace;
import com.maddox.il2.net.NetUser;
import com.maddox.il2.net.ZutiSupportMethods_NetSend;
import com.maddox.il2.objects.air.ZutiSupportMethods_Air;
import com.maddox.rts.CmdEnv;
import com.maddox.rts.NetEnv;
import com.maddox.rts.Property;
import com.maddox.rts.RTSConf;
import com.maddox.rts.Time;

public class GUINetClientDBrief extends GUIBriefing {
    public void enter(GameState gamestate) {
        super.enter(gamestate);
        if (gamestate != null && (gamestate.id() == 43 || gamestate.id() == 36) && this.briefSound != null) {
            String string = Main.cur().currentMissionFile.get("MAIN", "briefSound" + ((NetUser) NetEnv.host()).getArmy());
            if (string != null) this.briefSound = string;
            CmdEnv.top().exec("music PUSH");
            CmdEnv.top().exec("music LIST " + this.briefSound);
            CmdEnv.top().exec("music PLAY");
        }
    }

    public void leave(GameState gamestate) {
        if (gamestate != null && (gamestate.id() == 36 || gamestate.id() == 43) && this.briefSound != null) {
            CmdEnv.top().exec("music POP");
            CmdEnv.top().exec("music STOP");
            this.briefSound = null;
        }
        super.leave(gamestate);
    }

    public void leavePop(GameState gamestate) {
        if (gamestate != null && gamestate.id() == 2 && this.briefSound != null) {
            CmdEnv.top().exec("music POP");
            CmdEnv.top().exec("music PLAY");
        }
        super.leavePop(gamestate);
    }

    protected void fillTextDescription() {

    }

    public boolean isExistTextDescription() {
        return this.textDescription != null;
    }

    public void clearTextDescription() {
        this.textDescription = null;
    }

    public void setTextDescription(String string) {
        try {
            ResourceBundle resourcebundle = ResourceBundle.getBundle(string, RTSConf.cur.locale);
            this.textDescription = resourcebundle.getString("Description");
            // TODO: +++ Modified by SAS~Storebror: Save last Briefing Text (e.g. for Network use)
            GUIPad.setBriefingText(this.textDescription);
            // ---
            this.prepareTextDescription(Army.amountNet());
        } catch (Exception exception) {
            this.textDescription = null;
            this.textArmyDescription = null;
        }
        this.wScrollDescription.resized();
    }

    protected String textDescription() {
        if (this.textArmyDescription == null) return null;
        NetUser netuser = (NetUser) NetEnv.host();
        int i = netuser.getBornPlace();
        if (i < 0 || World.cur().bornPlaces == null || i >= World.cur().bornPlaces.size()) return this.textArmyDescription[0];
        BornPlace bornplace = (BornPlace) World.cur().bornPlaces.get(i);
        if (bornplace.army < 0 || bornplace.army >= this.textArmyDescription.length) return "";
        // TODO: +++ Modified by SAS~Storebror: Save last Briefing Text (e.g. for Network use)
        GUIPad.setBriefingText(this.textArmyDescription[bornplace.army]);
        // ---
        return this.textArmyDescription[bornplace.army];
    }

    private boolean isValidBornPlace() {
        NetUser netuser = (NetUser) NetEnv.host();
        int i = netuser.getBornPlace();
        if (i < 0 || i >= World.cur().bornPlaces.size()) {
            GUINetClientGuard guinetclientguard = (GUINetClientGuard) Main.cur().netChannelListener;
            guinetclientguard.curMessageBox = new GWindowMessageBox(Main3D.cur3D().guiManager.root, 20.0F, true, this.i18n("brief.BornPlace"), this.i18n("brief.BornPlaceSelect"), 3, 0.0F);
            return false;
        }
        int airdromeStay = netuser.getAirdromeStay();
        if (airdromeStay < 0) {
            // TODO: Added by |ZUTI|: since AirdromeStay is reset when user is on the carrier and hits refly,
            // his home base selection is still valid!
            // ------------------------------------------------------
            BornPlace bp = (BornPlace) World.cur().bornPlaces.get(netuser.getBornPlace());
            if (bp.zutiAlreadyAssigned) return true;
            // ------------------------------------------------------

            GUINetClientGuard guinetclientguard = (GUINetClientGuard) Main.cur().netChannelListener;
            guinetclientguard.curMessageBox = new GWindowMessageBox(Main3D.cur3D().guiManager.root, 20.0F, true, this.i18n("brief.StayPlace"), this.i18n("brief.StayPlaceWait"), 3, 0.0F);
            return false;
        }
        return true;
    }

    protected void doNext_410() {
        if (this.isValidBornPlace()) {
            int result = ZutiSupportMethods_GUI.isValidArming();
            if (result != -1) {
                String strReason = "";
                switch (result) {
                    case 0:
                        strReason = this.i18n("mds.info.various");
                        break;
                    case 1:
                        strReason = this.i18n("mds.info.fuel");
                        break;
                    case 2:
                        strReason = this.i18n("mds.info.weapons");
                        break;
                    case 3:
                        strReason = this.i18n("mds.info.country");
                        break;
                }
                Main.stateStack().push(55);
                new GWindowMessageBox(Main3D.cur3D().guiManager.root, 20.0F, true, this.i18n("mds.info.bornPlace"), this.i18n("mds.info.selectValidOptions") + " - " + strReason, 3, 0.0F);
                return;
            } else {
                UserCfg usercfg = World.cur().userCfg;
                AirportCarrier airportcarrier = this.getCarrier((NetUser) NetEnv.host());
                BornPlace bornplace = (BornPlace) World.cur().bornPlaces.get(((NetUser) NetEnv.host()).getBornPlace());
                if (airportcarrier != null && !bornplace.zutiAirspawnOnly && World.cur().diffCur.Takeoff_N_Landing) {
                    // TODO: Deleted some 410 stuff here. If spawning on ac carriers place needs to be reserved
                    Class var_class = (Class) Property.value(usercfg.netAirName, "airClass", null);
                    airportcarrier.setGuiCallback(this);
                    airportcarrier.ship().requestLocationOnCarrierDeck((NetUser) NetEnv.host(), var_class.getName());
                    GUINetClientGuard guinetclientguard = (GUINetClientGuard) Main.cur().netChannelListener;
                    guinetclientguard.curMessageBox = new GWindowMessageBox(Main3D.cur3D().guiManager.root, 20.0F, true, this.i18n("miss.ReFlyWait"), this.i18n("miss.ReFlyWait"), 4, 30.0F);
                }
                // TODO: Added by |ZUTI|
                // ---------------------------
                else {
                    String acName = ZutiSupportMethods_Air.getAircraftI18NName((Class) Property.value(usercfg.netAirName, "airClass", null));
                    ZutiSupportMethods_NetSend.requestAircraft(acName);
                }
                // ---------------------------
            }
        }
    }

    // TODO: Added by SAS~Storebror, avoid multiple klicks on "Fly" button!
    private static long       lastFly                 = 0L;
    private static final long TIME_BETWEEN_FLY_KLICKS = 10000L;

    protected void doNext() {
        // TODO: Added by SAS~Storebror, avoid multiple klicks on "Fly" button!
        if (Time.currentReal() - lastFly < TIME_BETWEEN_FLY_KLICKS) {
            long secondsLeft = (TIME_BETWEEN_FLY_KLICKS - Time.currentReal() + lastFly + 500L) / 1000L;
            GWindowMessageBox gwindowmessagebox = new GWindowMessageBox(Main3D.cur3D().guiManager.root, 20.0F, true, "Patience Please!",
                    "Minimum time between two\nclicks on \"Fly\" button\nis " + TIME_BETWEEN_FLY_KLICKS / 1000L + " seconds!\n\nPlease wait another " + secondsLeft + " second" + (secondsLeft > 1L ? "s." : "."), 3, 0.0F);
            GUINetClientGuard guinetclientguard = (GUINetClientGuard) Main.cur().netChannelListener;
            if (guinetclientguard != null) guinetclientguard.curMessageBox = gwindowmessagebox;
            return;
        }
        lastFly = Time.currentReal();

        // TODO: Modified by |ZUTI|: check if aircraft is available
        int result = ZutiSupportMethods_GUI.isValidArming();
        if (result != -1) {
            String strReason = "";
            switch (result) {
                case 0:
                    strReason = this.i18n("mds.info.various");
                    break;
                case 1:
                    strReason = this.i18n("mds.info.fuel");
                    break;
                case 2:
                    strReason = this.i18n("mds.info.weapons");
                    break;
                case 3:
                    strReason = this.i18n("mds.info.country");
                    break;
                case 4:
                    strReason = this.i18n("mds.info.homeBase");
                    break;
            }
            if (result != 4) Main.stateStack().push(55);
            new GWindowMessageBox(Main3D.cur3D().guiManager.root, 20.0F, true, this.i18n("mds.info.bornPlace"), this.i18n("mds.info.selectValidOptions") + " - " + strReason, 3, 0.0F);
            return;
        }

        // Request spawn place
        ZutiSupportMethods_NetSend.requestCarrierSpawnPlace();
        // Request aircraft
        UserCfg usercfg = World.cur().userCfg;
        String acName = ZutiSupportMethods_Air.getAircraftI18NName((Class) Property.value(usercfg.netAirName, "airClass", null));
        ZutiSupportMethods_NetSend.requestAircraft(acName);
    }

    public void flyFromCarrier(boolean bool) {
        // TODO: Deleted some 410 stuff here - property to air spawn if the deck is full
        GUINetClientGuard guinetclientguard = (GUINetClientGuard) Main.cur().netChannelListener;
        guinetclientguard.curMessageBox.close(false);
        if (bool) {
            // fly();
            // TODO: Added by |ZUTI|: OK, we are cleared to TO from carrier, request aircraft
            // ------------------------------------------------------------------------------
            UserCfg usercfg = World.cur().userCfg;
            String acName = ZutiSupportMethods_Air.getAircraftI18NName((Class) Property.value(usercfg.netAirName, "airClass", null));
            ZutiSupportMethods_NetSend.requestAircraft(acName);
            // ------------------------------------------------------------------------------
        } else guinetclientguard.curMessageBox = new GWindowMessageBox(Main3D.cur3D().guiManager.root, 20.0F, true, this.i18n("brief.CarrierDeckFull"), this.i18n("brief.CarrierDeckFullWait"), 3, 0.0F);
    }

    protected void doLoodout() {
        if (this.isValidBornPlace()) {
            GUIAirArming.stateId = 2;
            Main.stateStack().push(55);
        }
    }

    protected void doDiff() {
        Main.stateStack().push(41);
    }

    protected void doBack() {
        GUINetClientGuard guinetclientguard = (GUINetClientGuard) Main.cur().netChannelListener;
        guinetclientguard.dlgDestroy(new GUINetClientGuard.DestroyExec() {
            public void destroy(GUINetClientGuard guinetclientguard_9_) {
                guinetclientguard_9_.destroy(true);
            }
        });
    }

    protected void clientRender() {
        GUIBriefingGeneric.DialogClient dialogclient = this.dialogClient;
        GUIBriefingGeneric.DialogClient dialogclient_4_ = dialogclient;
        float f = dialogclient.x1024(5.0F);
        float f_5_ = dialogclient.y1024(633.0F);
        float f_6_ = dialogclient.x1024(160.0F);
        float f_7_ = dialogclient.y1024(48.0F);
        if (dialogclient != null) {
            /* empty */
        }
        dialogclient_4_.draw(f, f_5_, f_6_, f_7_, 1, this.i18n("brief.Disconnect"));
        GUIBriefingGeneric.DialogClient dialogclient_8_ = dialogclient;
        float f_9_ = dialogclient.x1024(194.0F);
        float f_10_ = dialogclient.y1024(633.0F);
        float f_11_ = dialogclient.x1024(208.0F);
        float f_12_ = dialogclient.y1024(48.0F);
        if (dialogclient != null) {
            /* empty */
        }
        dialogclient_8_.draw(f_9_, f_10_, f_11_, f_12_, 1, this.i18n("brief.Difficulty"));
        GUIBriefingGeneric.DialogClient dialogclient_13_ = dialogclient;
        float f_14_ = dialogclient.x1024(680.0F);
        float f_15_ = dialogclient.y1024(633.0F);
        float f_16_ = dialogclient.x1024(176.0F);
        float f_17_ = dialogclient.y1024(48.0F);
        if (dialogclient != null) {
            /* empty */
        }
        dialogclient_13_.draw(f_14_, f_15_, f_16_, f_17_, 1, this.i18n("brief.Arming"));
        super.clientRender();
    }

    protected void clientSetPosSize() {
        GUIBriefingGeneric.DialogClient dialogclient = this.dialogClient;
        this.bLoodout.setPosC(dialogclient.x1024(768.0F), dialogclient.y1024(689.0F));
    }

    public GUINetClientDBrief(GWindowRoot gwindowroot) {
        super(40);
        this.init(gwindowroot);
    }
}