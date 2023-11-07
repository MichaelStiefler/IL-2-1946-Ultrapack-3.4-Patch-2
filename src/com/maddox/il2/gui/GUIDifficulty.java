package com.maddox.il2.gui;

import com.maddox.gwindow.GColor;
import com.maddox.gwindow.GTexture;
import com.maddox.gwindow.GWindow;
import com.maddox.gwindow.GWindowRoot;
import com.maddox.il2.ai.DifficultySettings;
import com.maddox.il2.ai.World;
import com.maddox.il2.game.GameState;
import com.maddox.il2.game.Main;

public class GUIDifficulty extends GameState {
    public GUIClient     client;
    public DialogClient  dialogClient;
    public GUIInfoMenu   infoMenu;
    public GUIInfoName   infoName;
    public GUISwitchBox3 sWind_N_Turbulence;
    public GUISwitchBox3 sFlutter_Effect;
    public GUISwitchBox3 sStalls_N_Spins;
    public GUISwitchBox3 sBlackouts_N_Redouts;
    public GUISwitchBox3 sEngine_Overheat;
    public GUISwitchBox3 sTorque_N_Gyro_Effects;
    public GUISwitchBox3 sRealistic_Landings;
    public GUISwitchBox3 sTakeoff_N_Landing;
    public GUISwitchBox3 sCockpit_Always_On;
    public GUISwitchBox3 sNo_Outside_Views;
    public GUISwitchBox3 sHead_Shake;
    public GUISwitchBox3 sNo_Icons;
    public GUISwitchBox3 sRealistic_Gunnery;
    public GUISwitchBox3 sLimited_Ammo;
    public GUISwitchBox3 sLimited_Fuel;
    public GUISwitchBox3 sVulnerability;
    public GUISwitchBox3 sNo_Padlock;
    public GUISwitchBox3 sClouds;
    public GUISwitchBox3 sNo_Map_Icons;
    public GUISwitchBox3 sSeparateEStart;
    public GUISwitchBox3 sNoInstantSuccess;
    public GUISwitchBox3 sNoMinimapPath;
    public GUISwitchBox3 sNoSpeedBar;
    public GUISwitchBox3 sComplexEManagement;
    public GUISwitchBox3 sReliability;
    public GUISwitchBox3 sG_Limits;
    public GUISwitchBox3 sRealisticPilotVulnerability;
    public GUISwitchBox3 sRealisticNavigationInstruments;

    // TODO: Disabled by |ZUTI|
    // TODO: Re-enabled by SAS~Storebror
    // -------------------------------------
    public GUISwitchBox3 sNo_Player_Icon;
    public GUISwitchBox3 sNo_Fog_Of_War_Icons;
    // -------------------------------------

    public GUIButton     bExit;
    public GUIButton     bEasy;
    public GUIButton     bNormal;
    public GUIButton     bHard;
    public GUIButton     bNext;
    public boolean       bEnable = true;
    public boolean       bFirst  = true;
    public boolean       bSecond = false;
    public boolean       bThird  = false;

    public class DialogClient extends GUIDialogClient {
        public boolean notify(GWindow gwindow, int i, int i_0_) {
            if (i != 2) {
                return super.notify(gwindow, i, i_0_);
            }
            if (gwindow == GUIDifficulty.this.bExit) {
                if (GUIDifficulty.this.bFirst) {
                    Main.stateStack().pop();
                } else if (GUIDifficulty.this.bSecond) {
                    GUIDifficulty.this.bFirst = true;
                    GUIDifficulty.this.bSecond = false;
                    GUIDifficulty.this.bThird = false;
                    GUIDifficulty.this.showHide();
                } else {
                    GUIDifficulty.this.bSecond = true;
                    GUIDifficulty.this.bFirst = false;
                    GUIDifficulty.this.bThird = false;
                    GUIDifficulty.this.showHide();
                }
                return true;
            }
            if (gwindow == GUIDifficulty.this.bNext) {
                if (GUIDifficulty.this.bFirst) {
                    GUIDifficulty.this.bFirst = false;
                    GUIDifficulty.this.bSecond = true;
                    GUIDifficulty.this.bThird = false;
                    GUIDifficulty.this.showHide();
                } else if (GUIDifficulty.this.bSecond) {
                    GUIDifficulty.this.bFirst = false;
                    GUIDifficulty.this.bSecond = false;
                    GUIDifficulty.this.bThird = true;
                    GUIDifficulty.this.showHide();
                }
                return true;
            }
            if (gwindow == GUIDifficulty.this.bEasy) {
                GUIDifficulty.this.settings().setEasy();
                GUIDifficulty.this.reset();
                return true;
            }
            if (gwindow == GUIDifficulty.this.bNormal) {
                GUIDifficulty.this.settings().setNormal();
                GUIDifficulty.this.reset();
                return true;
            }
            if (gwindow == GUIDifficulty.this.bHard) {
                GUIDifficulty.this.settings().setRealistic();
                GUIDifficulty.this.reset();
                return true;
            }

            // TODO: Disabled by |ZUTI|
            // TODO: Re-enabled by SAS~Storebror
            // ---------------------------------------
            if (gwindow == GUIDifficulty.this.sNo_Map_Icons) {
                GUIDifficulty.this.sNo_Fog_Of_War_Icons.setEnable(GUIDifficulty.this.sNo_Map_Icons.isChecked());
                if (!GUIDifficulty.this.sNo_Map_Icons.isChecked()) {
                    GUIDifficulty.this.sNo_Fog_Of_War_Icons.setChecked(true, false);
                }
            }
            // ---------------------------------------
            return super.notify(gwindow, i, i_0_);
        }

        public void render() {
            super.render();
            GUISeparate.draw(this, GColor.Gray, this.x1024(32.0F), this.y1024(464.0F), this.x1024(768.0F), 2.0F);
            GUISeparate.draw(this, GColor.Gray, this.x1024(32.0F), this.y1024(544.0F), this.x1024(768.0F), 2.0F);
            this.setCanvasColor(GColor.Gray);
            this.setCanvasFont(0);
            this.draw(this.x1024(96.0F), this.y1024(577.0F), this.x1024(224.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Back"));
            if (GUIDifficulty.this.bEnable) {
                this.draw(this.x1024(224.0F), this.y1024(464.0F), this.x1024(128.0F), this.y1024(48.0F), 2, GUIDifficulty.this.i18n("diff.Easy"));
                this.draw(this.x1024(416.0F), this.y1024(464.0F), this.x1024(128.0F), this.y1024(48.0F), 2, GUIDifficulty.this.i18n("diff.Normal"));
                this.draw(this.x1024(608.0F), this.y1024(464.0F), this.x1024(128.0F), this.y1024(48.0F), 2, GUIDifficulty.this.i18n("diff.Hard"));
            }

            if (GUIDifficulty.this.bFirst || GUIDifficulty.this.bSecond) {
                this.draw(this.x1024(512.0F), this.y1024(577.0F), this.x1024(224.0F), this.y1024(48.0F), 2, GUIDifficulty.this.i18n("diff.Next"));
            }
            if (GUIDifficulty.this.bFirst) {
                this.draw(this.x1024(128.0F), this.y1024(32.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.SeparateEStart"));
                this.draw(this.x1024(128.0F), this.y1024(96.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.ComplexEManagement"));
                this.draw(this.x1024(128.0F), this.y1024(160.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Engine"));
                this.draw(this.x1024(128.0F), this.y1024(224.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Torque"));
                this.draw(this.x1024(128.0F), this.y1024(288.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Flutter"));
                this.draw(this.x1024(128.0F), this.y1024(352.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Wind"));
                this.draw(this.x1024(128.0F), this.y1024(416.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Reliability"));
                this.draw(this.x1024(528.0F), this.y1024(32.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Stalls"));
                this.draw(this.x1024(528.0F), this.y1024(96.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Vulnerability"));
                this.draw(this.x1024(528.0F), this.y1024(160.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Blackouts"));
                this.draw(this.x1024(528.0F), this.y1024(224.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.RealisticGun"));
                this.draw(this.x1024(528.0F), this.y1024(288.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.LimitedAmmo"));
                this.draw(this.x1024(528.0F), this.y1024(352.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.LimitedFuel"));
                this.draw(this.x1024(528.0F), this.y1024(416.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.G_Limits"));
            } else if (GUIDifficulty.this.bSecond) {
                this.draw(this.x1024(128.0F), this.y1024(32.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Cockpit"));
                this.draw(this.x1024(128.0F), this.y1024(96.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoOutside"));
                this.draw(this.x1024(128.0F), this.y1024(160.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Head"));
                this.draw(this.x1024(128.0F), this.y1024(224.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoIcons"));
                this.draw(this.x1024(128.0F), this.y1024(288.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoPadlock"));
                this.draw(this.x1024(128.0F), this.y1024(352.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Clouds"));
                this.draw(this.x1024(528.0F), this.y1024(32.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoInstantSuccess"));
                this.draw(this.x1024(528.0F), this.y1024(96.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.Takeoff"));
                this.draw(this.x1024(528.0F), this.y1024(160.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.RealisticLand"));
                this.draw(this.x1024(528.0F), this.y1024(224.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoSpeedBar"));
                this.draw(this.x1024(528.0F), this.y1024(288.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.RealisticNavInstr"));
                this.draw(this.x1024(528.0F), this.y1024(352.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.RealisticPilotVulnerability"));
            } else if (GUIDifficulty.this.bThird) {
                // TODO: Changed by |ZUTI|: disabled two options!
                // TODO: Re-enabled by SAS~Storebror
                this.draw(this.x1024(128.0F), this.y1024(32.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoMapIcons"));
                this.draw(this.x1024(128.0F), this.y1024(96.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoPlayerIcon"));
                this.draw(this.x1024(128.0F), this.y1024(160.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoFogOfWarIcons"));
                this.draw(this.x1024(128.0F), this.y1024(224.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoMinimapPath"));
                this.draw(this.x1024(128.0F), this.y1024(160.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoFogOfWarIcons"));
                this.draw(this.x1024(128.0F), this.y1024(224.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoMinimapPath"));

//                this.draw(this.x1024(128.0F), this.y1024(32.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoMapIcons"));
//                this.draw(this.x1024(128.0F), this.y1024(96.0F), this.x1024(272.0F), this.y1024(48.0F), 0, GUIDifficulty.this.i18n("diff.NoMinimapPath"));
            }
        }

        public void setPosSize() {
            this.set1024PosSize(92.0F, 72.0F, 832.0F, 656.0F);
            GUIDifficulty.this.sSeparateEStart.setPosC(this.x1024(88.0F), this.y1024(56.0F));
            GUIDifficulty.this.sComplexEManagement.setPosC(this.x1024(88.0F), this.y1024(120.0F));
            GUIDifficulty.this.sEngine_Overheat.setPosC(this.x1024(88.0F), this.y1024(184.0F));
            GUIDifficulty.this.sTorque_N_Gyro_Effects.setPosC(this.x1024(88.0F), this.y1024(248.0F));
            GUIDifficulty.this.sFlutter_Effect.setPosC(this.x1024(88.0F), this.y1024(312.0F));
            GUIDifficulty.this.sWind_N_Turbulence.setPosC(this.x1024(88.0F), this.y1024(376.0F));
            GUIDifficulty.this.sReliability.setPosC(this.x1024(88.0F), this.y1024(440.0F));
            GUIDifficulty.this.sStalls_N_Spins.setPosC(this.x1024(488.0F), this.y1024(56.0F));
            GUIDifficulty.this.sVulnerability.setPosC(this.x1024(488.0F), this.y1024(120.0F));
            GUIDifficulty.this.sBlackouts_N_Redouts.setPosC(this.x1024(488.0F), this.y1024(184.0F));
            GUIDifficulty.this.sRealistic_Gunnery.setPosC(this.x1024(488.0F), this.y1024(248.0F));
            GUIDifficulty.this.sLimited_Ammo.setPosC(this.x1024(488.0F), this.y1024(312.0F));
            GUIDifficulty.this.sLimited_Fuel.setPosC(this.x1024(488.0F), this.y1024(376.0F));
            GUIDifficulty.this.sG_Limits.setPosC(this.x1024(488.0F), this.y1024(440.0F));
            GUIDifficulty.this.sCockpit_Always_On.setPosC(this.x1024(88.0F), this.y1024(56.0F));
            GUIDifficulty.this.sNo_Outside_Views.setPosC(this.x1024(88.0F), this.y1024(120.0F));
            GUIDifficulty.this.sHead_Shake.setPosC(this.x1024(88.0F), this.y1024(184.0F));
            GUIDifficulty.this.sNo_Icons.setPosC(this.x1024(88.0F), this.y1024(248.0F));
            GUIDifficulty.this.sNo_Padlock.setPosC(this.x1024(88.0F), this.y1024(312.0F));
            GUIDifficulty.this.sClouds.setPosC(this.x1024(88.0F), this.y1024(376.0F));
            GUIDifficulty.this.sNoInstantSuccess.setPosC(this.x1024(488.0F), this.y1024(56.0F));
            GUIDifficulty.this.sTakeoff_N_Landing.setPosC(this.x1024(488.0F), this.y1024(120.0F));
            GUIDifficulty.this.sRealistic_Landings.setPosC(this.x1024(488.0F), this.y1024(184.0F));
            GUIDifficulty.this.sNoSpeedBar.setPosC(this.x1024(488.0F), this.y1024(248.0F));
            GUIDifficulty.this.sRealisticNavigationInstruments.setPosC(this.x1024(488.0F), this.y1024(312.0F));
            GUIDifficulty.this.sRealisticPilotVulnerability.setPosC(this.x1024(488.0F), this.y1024(376.0F));

            // TODO: Disabled two buttons by |ZUTI|
            // TODO: Re-enabled by SAS~Storebror
            // --------------------------------------------------
            GUIDifficulty.this.sNo_Map_Icons.setPosC(this.x1024(88.0F), this.y1024(56.0F));
            GUIDifficulty.this.sNo_Player_Icon.setPosC(this.x1024(88.0F), this.y1024(120.0F));
            GUIDifficulty.this.sNo_Fog_Of_War_Icons.setPosC(this.x1024(88.0F), this.y1024(184.0F));
            GUIDifficulty.this.sNoMinimapPath.setPosC(this.x1024(88.0F), this.y1024(248.0F));

//            GUIDifficulty.this.sNo_Map_Icons.setPosC(this.x1024(88.0F), this.y1024(56.0F));
//            GUIDifficulty.this.sNoMinimapPath.setPosC(this.x1024(88.0F), this.y1024(120.0F));
            // --------------------------------------------------

            GUIDifficulty.this.bExit.setPosC(this.x1024(56.0F), this.y1024(602.0F));
            GUIDifficulty.this.bEasy.setPosC(this.x1024(392.0F), this.y1024(488.0F));
            GUIDifficulty.this.bNormal.setPosC(this.x1024(584.0F), this.y1024(488.0F));
            GUIDifficulty.this.bHard.setPosC(this.x1024(776.0F), this.y1024(488.0F));
            GUIDifficulty.this.bNext.setPosC(this.x1024(776.0F), this.y1024(602.0F));
        }
    }

    public void enterPush(GameState gamestate) {
        if (gamestate.id() == 27) {
            this.bEnable = false;
        } else {
            this.bEnable = true;
        }
        this._enter();
    }

    protected DifficultySettings settings() {
        return World.cur().diffUser;
    }

    public void _enter() {
        this.reset();
        this.sReliability.setEnable(this.bEnable);
        this.sG_Limits.setEnable(this.bEnable);
        this.sRealisticPilotVulnerability.setEnable(this.bEnable);
        this.sRealisticNavigationInstruments.setEnable(this.bEnable);
        // TODO: Disabled by |ZUTI|
        // TODO: Re-enabled by SAS~Storebror
        // ---------------------------------------
        this.sNo_Player_Icon.setEnable(this.bEnable);
        // ---------------------------------------
        this.sWind_N_Turbulence.setEnable(this.bEnable);
        this.sFlutter_Effect.setEnable(this.bEnable);
        this.sStalls_N_Spins.setEnable(this.bEnable);
        this.sBlackouts_N_Redouts.setEnable(this.bEnable);
        this.sEngine_Overheat.setEnable(this.bEnable);
        this.sTorque_N_Gyro_Effects.setEnable(this.bEnable);
        this.sRealistic_Landings.setEnable(this.bEnable);
        this.sTakeoff_N_Landing.setEnable(this.bEnable);
        this.sCockpit_Always_On.setEnable(this.bEnable);
        this.sNo_Outside_Views.setEnable(this.bEnable);
        this.sHead_Shake.setEnable(this.bEnable);
        this.sNo_Icons.setEnable(this.bEnable);
        this.sNo_Map_Icons.setEnable(this.bEnable);
        this.sRealistic_Gunnery.setEnable(this.bEnable);
        this.sLimited_Ammo.setEnable(this.bEnable);
        this.sLimited_Fuel.setEnable(this.bEnable);
        this.sVulnerability.setEnable(this.bEnable);
        this.sNo_Padlock.setEnable(this.bEnable);
        this.sClouds.setEnable(this.bEnable);
        this.sSeparateEStart.setEnable(this.bEnable);
        this.sNoInstantSuccess.setEnable(this.bEnable);
        this.sNoMinimapPath.setEnable(this.bEnable);
        this.sNoSpeedBar.setEnable(this.bEnable);
        this.sComplexEManagement.setEnable(this.bEnable);
        this.setShow(this.bEnable, this.bEasy);
        this.setShow(this.bEnable, this.bNormal);
        this.setShow(this.bEnable, this.bHard);
        this.showHide();
        this.client.activateWindow();
    }

    private void setShow(boolean bool, GWindow gwindow) {
        if (bool) {
            gwindow.showWindow();
        } else {
            gwindow.hideWindow();
        }
    }

    private void showHide() {
        this.setShow(this.bFirst, this.sSeparateEStart);
        this.setShow(this.bFirst, this.sComplexEManagement);
        this.setShow(this.bFirst, this.sEngine_Overheat);
        this.setShow(this.bFirst, this.sTorque_N_Gyro_Effects);
        this.setShow(this.bFirst, this.sFlutter_Effect);
        this.setShow(this.bFirst, this.sWind_N_Turbulence);
        this.setShow(this.bFirst, this.sStalls_N_Spins);
        this.setShow(this.bFirst, this.sVulnerability);
        this.setShow(this.bFirst, this.sBlackouts_N_Redouts);
        this.setShow(this.bFirst, this.sRealistic_Gunnery);
        this.setShow(this.bFirst, this.sLimited_Ammo);
        this.setShow(this.bFirst, this.sLimited_Fuel);
        this.setShow(this.bFirst || this.bSecond, this.bNext);
        this.setShow(this.bFirst, this.sReliability);
        this.setShow(this.bFirst, this.sG_Limits);
        this.setShow(this.bSecond, this.sRealisticPilotVulnerability);
        this.setShow(this.bSecond, this.sRealisticNavigationInstruments);
        this.setShow(this.bSecond, this.sCockpit_Always_On);
        this.setShow(this.bSecond, this.sNo_Outside_Views);
        this.setShow(this.bSecond, this.sHead_Shake);
        this.setShow(this.bSecond, this.sNo_Icons);
        this.setShow(this.bSecond, this.sNo_Padlock);
        this.setShow(this.bSecond, this.sClouds);
        this.setShow(this.bSecond, this.sNoInstantSuccess);
        this.setShow(this.bSecond, this.sTakeoff_N_Landing);
        this.setShow(this.bSecond, this.sRealistic_Landings);
        this.setShow(this.bSecond, this.sNoSpeedBar);
        this.setShow(this.bThird, this.sNo_Map_Icons);
        // TODO: Disabled by |ZUTI|
        // TODO: Re-enabled by SAS~Storebror
        // ---------------------------------------
        this.setShow(this.bThird, this.sNo_Player_Icon);
        this.setShow(this.bThird, this.sNo_Fog_Of_War_Icons);
        // ---------------------------------------
        this.setShow(this.bThird, this.sNoMinimapPath);
        this.dialogClient.doResolutionChanged();
        this.dialogClient.setPosSize();
    }

    private void reset() {
        DifficultySettings difficultysettings = this.settings();
        this.sReliability.setChecked(difficultysettings.Reliability, false);
        this.sG_Limits.setChecked(difficultysettings.G_Limits, false);
        this.sRealisticPilotVulnerability.setChecked(difficultysettings.RealisticPilotVulnerability, false);
        this.sRealisticNavigationInstruments.setChecked(difficultysettings.RealisticNavigationInstruments, false);

        // TODO: Disabled by |ZUTI|
        // TODO: Re-enabled by SAS~Storebror
        // ---------------------------------------
        this.sNo_Player_Icon.setChecked(difficultysettings.No_Player_Icon, false);
        this.sNo_Fog_Of_War_Icons.setChecked(difficultysettings.No_Fog_Of_War_Icons, false);
        this.sNo_Fog_Of_War_Icons.setEnable(this.sNo_Map_Icons.isChecked() & this.bEnable);
        // ---------------------------------------

        this.sWind_N_Turbulence.setChecked(difficultysettings.Wind_N_Turbulence, false);
        this.sFlutter_Effect.setChecked(difficultysettings.Flutter_Effect, false);
        this.sStalls_N_Spins.setChecked(difficultysettings.Stalls_N_Spins, false);
        this.sBlackouts_N_Redouts.setChecked(difficultysettings.Blackouts_N_Redouts, false);
        this.sEngine_Overheat.setChecked(difficultysettings.Engine_Overheat, false);
        this.sTorque_N_Gyro_Effects.setChecked(difficultysettings.Torque_N_Gyro_Effects, false);
        this.sRealistic_Landings.setChecked(difficultysettings.Realistic_Landings, false);
        this.sTakeoff_N_Landing.setChecked(difficultysettings.Takeoff_N_Landing, false);
        this.sCockpit_Always_On.setChecked(difficultysettings.Cockpit_Always_On, false);
        this.sNo_Outside_Views.setChecked(difficultysettings.No_Outside_Views, false);
        this.sHead_Shake.setChecked(difficultysettings.Head_Shake, false);
        this.sNo_Icons.setChecked(difficultysettings.No_Icons, false);
        this.sNo_Map_Icons.setChecked(difficultysettings.No_Map_Icons, false);
        this.sRealistic_Gunnery.setChecked(difficultysettings.Realistic_Gunnery, false);
        this.sLimited_Ammo.setChecked(difficultysettings.Limited_Ammo, false);
        this.sLimited_Fuel.setChecked(difficultysettings.Limited_Fuel, false);
        this.sVulnerability.setChecked(difficultysettings.Vulnerability, false);
        this.sNo_Padlock.setChecked(difficultysettings.No_Padlock, false);
        this.sClouds.setChecked(difficultysettings.Clouds, false);
        this.sSeparateEStart.setChecked(difficultysettings.SeparateEStart, false);
        this.sNoInstantSuccess.setChecked(difficultysettings.NoInstantSuccess, false);
        this.sNoMinimapPath.setChecked(difficultysettings.NoMinimapPath, false);
        this.sNoSpeedBar.setChecked(difficultysettings.NoSpeedBar, false);
        this.sComplexEManagement.setChecked(difficultysettings.ComplexEManagement, false);
    }

    public void _leave() {
        if (this.bEnable) {
            DifficultySettings difficultysettings = this.settings();
            difficultysettings.Reliability = this.sReliability.isChecked();
            difficultysettings.RealisticNavigationInstruments = this.sRealisticNavigationInstruments.isChecked();
            difficultysettings.G_Limits = this.sG_Limits.isChecked();
            difficultysettings.RealisticPilotVulnerability = this.sRealisticPilotVulnerability.isChecked();
            // TODO: Disabled by |ZUTI|
            // TODO: Re-enabled by SAS~Storebror
            // ---------------------------------------
            difficultysettings.No_Player_Icon = this.sNo_Player_Icon.isChecked();
            difficultysettings.No_Fog_Of_War_Icons = this.sNo_Fog_Of_War_Icons.isChecked();
            // ---------------------------------------
            difficultysettings.Wind_N_Turbulence = this.sWind_N_Turbulence.isChecked();
            difficultysettings.Flutter_Effect = this.sFlutter_Effect.isChecked();
            difficultysettings.Stalls_N_Spins = this.sStalls_N_Spins.isChecked();
            difficultysettings.Blackouts_N_Redouts = this.sBlackouts_N_Redouts.isChecked();
            difficultysettings.Engine_Overheat = this.sEngine_Overheat.isChecked();
            difficultysettings.Torque_N_Gyro_Effects = this.sTorque_N_Gyro_Effects.isChecked();
            difficultysettings.Realistic_Landings = this.sRealistic_Landings.isChecked();
            difficultysettings.Takeoff_N_Landing = this.sTakeoff_N_Landing.isChecked();
            difficultysettings.Cockpit_Always_On = this.sCockpit_Always_On.isChecked();
            difficultysettings.No_Outside_Views = this.sNo_Outside_Views.isChecked();
            difficultysettings.Head_Shake = this.sHead_Shake.isChecked();
            difficultysettings.No_Icons = this.sNo_Icons.isChecked();
            difficultysettings.No_Map_Icons = this.sNo_Map_Icons.isChecked();
            difficultysettings.Realistic_Gunnery = this.sRealistic_Gunnery.isChecked();
            difficultysettings.Limited_Ammo = this.sLimited_Ammo.isChecked();
            difficultysettings.Limited_Fuel = this.sLimited_Fuel.isChecked();
            difficultysettings.Vulnerability = this.sVulnerability.isChecked();
            difficultysettings.No_Padlock = this.sNo_Padlock.isChecked();
            difficultysettings.Clouds = this.sClouds.isChecked();
            difficultysettings.SeparateEStart = this.sSeparateEStart.isChecked();
            difficultysettings.NoInstantSuccess = this.sNoInstantSuccess.isChecked();
            difficultysettings.NoMinimapPath = this.sNoMinimapPath.isChecked();
            difficultysettings.NoSpeedBar = this.sNoSpeedBar.isChecked();
            difficultysettings.ComplexEManagement = this.sComplexEManagement.isChecked();
        }
        this.client.hideWindow();
    }

    protected void clientInit(GWindowRoot gwindowroot) {
    }

    public GUIDifficulty(GWindowRoot gwindowroot) {
        this(gwindowroot, 17);
    }

    protected GUIDifficulty(GWindowRoot gwindowroot, int i) {
        super(i);
        this.client = (GUIClient) gwindowroot.create(new GUIClient());
        this.dialogClient = (DialogClient) this.client.create(new DialogClient());
        this.infoMenu = (GUIInfoMenu) this.client.create(new GUIInfoMenu());
        this.infoMenu.info = this.i18n("diff.info");
        this.infoName = (GUIInfoName) this.client.create(new GUIInfoName());
        GTexture gtexture = ((GUILookAndFeel) gwindowroot.lookAndFeel()).buttons2;
        this.bExit = (GUIButton) this.dialogClient.addEscape(new GUIButton(this.dialogClient, gtexture, 0.0F, 96.0F, 48.0F, 48.0F));
        this.bEasy = (GUIButton) this.dialogClient.addControl(new GUIButton(this.dialogClient, gtexture, 0.0F, 48.0F, 48.0F, 48.0F));
        this.bNormal = (GUIButton) this.dialogClient.addControl(new GUIButton(this.dialogClient, gtexture, 0.0F, 48.0F, 48.0F, 48.0F));
        this.bHard = (GUIButton) this.dialogClient.addControl(new GUIButton(this.dialogClient, gtexture, 0.0F, 48.0F, 48.0F, 48.0F));
        this.bNext = (GUIButton) this.dialogClient.addControl(new GUIButton(this.dialogClient, gtexture, 0.0F, 48.0F, 48.0F, 48.0F));
        this.sWind_N_Turbulence = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sFlutter_Effect = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sStalls_N_Spins = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sBlackouts_N_Redouts = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sEngine_Overheat = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sTorque_N_Gyro_Effects = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sRealistic_Landings = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sTakeoff_N_Landing = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sCockpit_Always_On = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sNo_Outside_Views = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sHead_Shake = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sNo_Icons = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sNo_Map_Icons = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sRealistic_Gunnery = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sLimited_Ammo = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sLimited_Fuel = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sVulnerability = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sNo_Padlock = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sClouds = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sSeparateEStart = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sNoInstantSuccess = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sNoMinimapPath = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sNoSpeedBar = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sComplexEManagement = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sReliability = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sRealisticNavigationInstruments = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sG_Limits = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        this.sRealisticPilotVulnerability = (GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient));
        // TODO: Disabled by |ZUTI|
        // TODO: Re-enabled by SAS~Storebror
        // ---------------------------------------
        this.sNo_Player_Icon = ((GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient)));
        this.sNo_Fog_Of_War_Icons = ((GUISwitchBox3) this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient)));
        // ---------------------------------------
        this.clientInit(gwindowroot);
        this.dialogClient.activateWindow();
        this.client.hideWindow();
    }
}
