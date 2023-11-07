/* 4.10.1 class */
package com.maddox.il2.ai;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.air.Airdrome;
import com.maddox.il2.ai.air.NearestAircraft;
import com.maddox.il2.ai.ground.NearestEnemies;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorException;
import com.maddox.il2.engine.ActorFilter;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.engine.Sun;
import com.maddox.il2.engine.hotkey.HookView;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.fm.Wind;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.ZutiInterpolator;
import com.maddox.il2.game.ZutiSupportMethods;
import com.maddox.il2.game.ZutiTimer_Refly;
import com.maddox.il2.game.ZutiWeaponsManagement;
import com.maddox.il2.gui.GUINetMission;
import com.maddox.il2.net.BornPlace;
import com.maddox.il2.net.Chat;
import com.maddox.il2.net.NetMissionTrack;
import com.maddox.il2.net.NetUser;
import com.maddox.il2.net.ZutiSupportMethods_Net;
import com.maddox.il2.net.ZutiSupportMethods_NetSend;
import com.maddox.il2.objects.ActorViewPoint;
import com.maddox.il2.objects.ScoreRegister;
import com.maddox.il2.objects.Statics;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.air.NetGunner;
import com.maddox.il2.objects.air.Paratrooper;
import com.maddox.il2.objects.air.Runaway;
import com.maddox.il2.objects.air.ZutiSupportMethods_Air;
import com.maddox.il2.objects.buildings.HouseManager;
import com.maddox.il2.objects.effects.ForceFeedback;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.ZutiSupportMethods_Ships;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.il2.objects.vehicles.planes.PlaneGeneric;
import com.maddox.rts.CmdEnv;
import com.maddox.rts.NetEnv;
import com.maddox.rts.RTSConf;
import com.maddox.rts.Time;
import com.maddox.rts.TrackIRWin;
import com.maddox.sas1946.il2.util.Reflection;

public class World {
    public boolean            showMorseAsText         = false; // TODO: Fixed by SAS~Storebror: Unitinitalized instance variable fixed.
    public static final float NORD                    = 270.0F;
    public static final float PIXEL                   = 200.0F;
    public static float       MaxVisualDistance       = 5000.0F;
    public static float       MaxStaticVisualDistance = 4000.0F;
    public static float       MaxLongVisualDistance   = 10000.0F;
    public static float       MaxPlateVisualDistance  = 16000.0F;
    public boolean            blockMorseChat          = false;
    public boolean            smallMapWPLabels        = false;
    public RangeRandom        rnd                     = new RangeRandom();
    public int                camouflage              = 0;
    public static final int   CAMOUFLAGE_SUMMER       = 0;
    public static final int   CAMOUFLAGE_WINTER       = 1;
    public static final int   CAMOUFLAGE_DESERT       = 2;
    public static final int   CAMOUFLAGE_PACIFIC      = 3;
    public static final int   CAMOUFLAGE_ETO          = 4;
    public static final int   CAMOUFLAGE_MTO          = 5;
    public static final int   CAMOUFLAGE_CBI          = 6;
    public DifficultySettings diffCur                 = new DifficultySettings();
    public DifficultySettings diffUser                = new DifficultySettings();
    public UserCfg            userCfg;
    public float              userCoverMashineGun     = 500.0F;
    public float              userCoverCannon         = 500.0F;
    public float              userCoverRocket         = 500.0F;
    public float              userRocketDelay         = 10.0F;
    public float              userBombDelay           = 0.0F;
    // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
    public float              userBombFuze            = 0.0F;
    // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
    private boolean           bArcade                 = false;
    private boolean           bHighGore               = false;
    private boolean           bHakenAllowed           = false;
    private boolean           bDebugFM                = false;
    private boolean           bTimeOfDayConstant      = false;
    private boolean           bWeaponsConstant        = false;
    protected War             war;
    protected ArrayList       airports;
    public ArrayList          bornPlaces;
    public HouseManager       houseManager;
    public Runaway            runawayList;
    public Airdrome           airdrome;
    private int               missionArmy             = 1;
    private Aircraft          PlayerAircraft;
    private NetGunner         PlayerGunner;
    private int               PlayerArmy              = 1;
    private FlightModel       PlayerFM;
    private Regiment          PlayerRegiment;
    private String            PlayerLastCountry;
    private boolean           bPlayerParatrooper      = false;
    private boolean           bPlayerDead             = false;
    private boolean           bPlayerCaptured         = false;
    private boolean           bPlayerRemoved          = false;
    public static Actor       remover                 = new Remover();
    static ClipFilter         clipFilter              = new ClipFilter();
    public TargetsGuard       targetsGuard            = new TargetsGuard();
    // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
    public TriggersGuard      triggersGuard           = new TriggersGuard();
    // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
    public ScoreCounter       scoreCounter            = new ScoreCounter();
    private Wind              wind                    = new Wind();
    protected Front           front                   = new Front();
    public Statics            statics;
    private int               startTimeofDay          = 43200;
    public Atmosphere         Atm                     = new Atmosphere();
    public float[]            fogColor                = { 0.53F, 0.64F, 0.8F, 1.0F };
    public float[]            beachColor              = { 0.6F, 0.6F, 0.6F };
    public float[]            lodColor                = { 0.7F, 0.7F, 0.7F };
    public ChiefManager       ChiefMan                = new ChiefManager();
    private Sun               sun                     = new Sun();
    public Voice              voicebase               = new Voice();
    public boolean            useSmartAxis            = false;

    public static void showCallingLine(String message) {
        Exception ex = new Exception();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stacktrace = sw.getBuffer().toString();
        StringTokenizer stacktraceTokens = new StringTokenizer(stacktrace, "\n");
        boolean foundSource = false;
        int linesToSkip = 2;
        while (stacktraceTokens.hasMoreTokens()) {
            String stacktraceToken = stacktraceTokens.nextToken();
            while (stacktraceToken.startsWith("\t"))
                stacktraceToken = stacktraceToken.substring(1);
            if (!stacktraceToken.startsWith("at ")) continue;
            stacktraceToken = stacktraceToken.substring(3);
            if (linesToSkip-- > 0) continue;
            foundSource = true;
            System.out.println(message + stacktraceToken);
            break;
        }
        if (!foundSource) System.out.println(message + "unknown source");
    }

    static class ClipFilter implements ActorFilter {
        public boolean isUse(Actor actor, double d) {
            return actor instanceof BigshipGeneric;
        }
    }

    static class Remover extends Actor {
        protected void createActorHashCode() {
            this.makeActorRealHashCode();
        }
    }

    public static RangeRandom Rnd() {
        return cur().rnd;
    }

    public void setCamouflage(String string) {
        if ("SUMMER".equalsIgnoreCase(string)) this.camouflage = 0;
        else if ("WINTER".equalsIgnoreCase(string)) this.camouflage = 1;
        else if ("DESERT".equalsIgnoreCase(string)) this.camouflage = 2;
        else if ("PACIFIC".equalsIgnoreCase(string)) this.camouflage = 3;
        else if ("ETO".equalsIgnoreCase(string)) this.camouflage = 4;
        else if ("MTO".equalsIgnoreCase(string)) this.camouflage = 5;
        else if ("CBI".equalsIgnoreCase(string)) this.camouflage = 6;
        else this.camouflage = 0;
    }

    public void setUserCovers() {
        this.userCoverMashineGun = this.userCfg.coverMashineGun;
        this.userCoverCannon = this.userCfg.coverCannon;
        this.userCoverRocket = this.userCfg.coverRocket;
        this.userRocketDelay = this.userCfg.rocketDelay;
        this.userBombDelay = this.userCfg.bombDelay;
        // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
        this.userBombFuze = this.userCfg.bombFuze;
        // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
    }

    public boolean isArcade() {
        return Mission.isSingle() && this.bArcade && !NetMissionTrack.isPlaying();
    }

    public void setArcade(boolean bool) {
        this.bArcade = bool;
    }

    public boolean isHighGore() {
        return this.bHighGore;
    }

    public boolean isHakenAllowed() {
        return this.bHakenAllowed;
    }

    public boolean isDebugFM() {
        return this.bDebugFM;
    }

    public boolean isTimeOfDayConstant() {
        return this.bTimeOfDayConstant;
    }

    public void setTimeOfDayConstant(boolean bool) {
        this.bTimeOfDayConstant = bool;
        if (curDebugLevel() != 0) showCallingLine("setTimeOfDayConstant(" + bool + ") called from ");
    }

    public boolean isWeaponsConstant() {
        return this.bWeaponsConstant;
    }

    public void setWeaponsConstant(boolean bool) {
        this.bWeaponsConstant = bool;
    }

    public static void getAirports(List list) {
        if (cur().airports != null) list.addAll(cur().airports);
    }

    public static int getMissionArmy() {
        return cur().missionArmy;
    }

    public static void setMissionArmy(int i) {
        cur().missionArmy = i;
    }

    public static Aircraft getPlayerAircraft() {
        return cur().PlayerAircraft;
    }

    public static int getPlayerArmy() {
        return cur().PlayerArmy;
    }

    public static FlightModel getPlayerFM() {
        return cur().PlayerFM;
    }

    public static Regiment getPlayerRegiment() {
        return cur().PlayerRegiment;
    }

    public static String getPlayerLastCountry() {
        Regiment regiment = getPlayerRegiment();
        if (regiment != null) cur().PlayerLastCountry = regiment.country();
        return cur().PlayerLastCountry;
    }

    public static boolean isPlayerGunner() {
        // TODO: Added by |ZUTI|
        if (Actor.isValid(cur().PlayerGunner) && !(World.getPlayerFM() instanceof RealFlightModel)) return true;
        return false;
        // return Actor.isValid(cur().PlayerGunner);
    }

    public static NetGunner getPlayerGunner() {
        return cur().PlayerGunner;
    }

    public static boolean isPlayerParatrooper() {
        return cur().bPlayerParatrooper;
    }

    public static boolean isPlayerDead() {
        return cur().bPlayerDead;
    }

    public static boolean isPlayerCaptured() {
        return cur().bPlayerCaptured;
    }

    public static boolean isPlayerRemoved() {
        return cur().bPlayerRemoved;
    }

    public static void setPlayerAircraft(Aircraft aircraft) {
        cur().PlayerAircraft = aircraft;
        if (aircraft != null) {
            cur().PlayerFM = aircraft.FM;
            cur().scoreCounter.playerStartAir(aircraft);
        } else cur().PlayerFM = null;
    }

    public static void setPlayerFM() {
        if (Actor.isValid(cur().PlayerAircraft)) cur().PlayerFM = cur().PlayerAircraft.FM;
    }

    public static void setPlayerRegiment() {
        if (Actor.isValid(cur().PlayerAircraft)) {
            Aircraft aircraft = cur().PlayerAircraft;
            if (aircraft.getOwner() != null) cur().PlayerRegiment = ((Wing) aircraft.getOwner()).regiment();
            else cur().PlayerRegiment = null;
            cur().PlayerArmy = aircraft.getArmy();
            if (Mission.isSingle()) cur().missionArmy = cur().PlayerArmy;
        }
    }

    public static void doPlayerParatrooper(Paratrooper paratrooper) {
        // TODO: Added by |ZUTI|
        // ---------------------------------------------------------------------
        FlightModel fm = World.getPlayerFM();

        /*
         * System.out.println("WORLD ============================================================="); System.out.println(fm.Gears.zutiAreGearsOnZAP()); //true System.out.println(fm.Gears.isAnyDamaged()); //false System.out.println(fm.getSpeedKMH()); //-1
         * System.out.println(fm.isCapableOfBMP()); //true System.out.println(fm.isCapableOfTaxiing()); //true System.out.println(fm.isReadyToDie()); //true System.out.println(fm.isTakenMortalDamage()); //true
         * System.out.println(fm.isSentControlsOutNote()); //false System.out.println("=============================================================");
         */

        // if( fm.Gears.zutiAreGearsOnZAP() && ((fm.Gears.isAnyDamaged() && fm.getSpeedKMH() < 1) || !fm.isCapableOfBMP() || !fm.isCapableOfTaxiing() || fm.isReadyToDie() || fm.isTakenMortalDamage() || fm.isSentControlsOutNote()) )
        if (!ZutiSupportMethods_Air.isAircraftOnTheGroundAndUndamaged(fm)) {
            System.out.println("World - Landed damadged plane, no penalty for bail out!");

            if (Config.isUSE_RENDER()) RTSConf.cur.hotKeyEnvs.endAllActiveCmd(false);
            if (Config.isUSE_RENDER()) {
                if (Main3D.cur3D().viewActor() == getPlayerAircraft()) Main3D.cur3D().setViewFlow10(paratrooper, false);
                Main3D.cur3D().ordersTree.unactivate();
                ForceFeedback.stopMission();
            }
        }
        // ---------------------------------------------------------------------

        // Original code (if...)
        else if (!isPlayerParatrooper()) {
            if (Config.isUSE_RENDER()) RTSConf.cur.hotKeyEnvs.endAllActiveCmd(false);
            cur().bPlayerParatrooper = true;

            // TODO: Changed by |ZUTI|: added to be fair for score sharing for the crew
            // ---------------------------------------------
            if (!ZutiSupportMethods_Air.isBailedOnGroundOrDeck(World.getPlayerAircraft())) cur().scoreCounter.playerParatrooper();
            // ---------------------------------------------

            if (Config.isUSE_RENDER()) {
                if (Main3D.cur3D().viewActor() == getPlayerAircraft()) Main3D.cur3D().setViewFlow10(paratrooper, false);
                Main3D.cur3D().ordersTree.unactivate();
                ForceFeedback.stopMission();

//                System.out.println("World - player is paratrooper!");
            }
        }

        // TODO: Added by |ZUTI|
        // ---------------------------------------------------------------------
        if (Mission.MDS_VARIABLES().zutiMisc_EnableReflyOnlyIfBailedOrDied) {
            ZutiSupportMethods.ZUTI_KIA_DELAY_CLEARED = false;
            // Start re-fly counter
            float penalty = Mission.MDS_VARIABLES().zutiMisc_ReflyKIADelay + ZutiSupportMethods.ZUTI_KIA_COUNTER * Mission.MDS_VARIABLES().zutiMisc_ReflyKIADelayMultiplier;
            ZutiSupportMethods_NetSend.setUserTimePenalty((NetUser) NetEnv.host(), (long) penalty);
            new ZutiTimer_Refly(penalty);
        }

        // Releasing spawn place here because once player bails from his AC, the link is lost.
        Aircraft playerAc = World.getPlayerAircraft();
        if (!World.isPlayerGunner() && playerAc.FM instanceof RealFlightModel && NetEnv.host() != null)
            if (ZutiSupportMethods_Ships.isAircraftOnDeck(playerAc, 30D)) ZutiInterpolator.ZUTI_DELAYED_DESTROY_PLAYER_AC = Time.current() + ZutiSupportMethods_AI.AC_DESTROY_DELAY;
    }

    public static void doGunnerParatrooper(Paratrooper paratrooper) {
        if (!isPlayerParatrooper()) {
            if (Config.isUSE_RENDER()) RTSConf.cur.hotKeyEnvs.endAllActiveCmd(false);
            cur().bPlayerParatrooper = true;

            // TODO: Changed by |ZUTI|: added to be fair for score sharing for the crew
            // ---------------------------------------------
            if (!ZutiSupportMethods_Air.isBailedOnGroundOrDeck(World.getPlayerAircraft())) cur().scoreCounter.playerParatrooper();
            // ---------------------------------------------

            if (Config.isUSE_RENDER()) {
                if (Main3D.cur3D().viewActor() == getPlayerAircraft()) Main3D.cur3D().setViewFlow10(paratrooper, false);
                ForceFeedback.stopMission();

                if (Mission.MDS_VARIABLES().zutiMisc_EnableReflyOnlyIfBailedOrDied && Mission.isDogfight()) {
                    ZutiSupportMethods.ZUTI_KIA_DELAY_CLEARED = false;
                    GUINetMission.setPlayerParatrooper(paratrooper);
                }
            }
        }
    }

    public static void doPlayerUnderWater() {
        if (Config.isUSE_RENDER() && Main3D.cur3D().viewActor() == getPlayerAircraft() && !Main3D.cur3D().isViewOutside()) Main3D.cur3D().setViewFlow10(getPlayerAircraft(), false);
    }

    public static void setPlayerDead() {
        if (Config.isUSE_RENDER()) RTSConf.cur.hotKeyEnvs.endAllActiveCmd(false);

        cur().bPlayerDead = true;
        cur().scoreCounter.playerDead();

        // TODO: Added by |ZUTI|
        // -------------------------------------------------------------------
        System.out.println("World - player has died!");
        if (Mission.MDS_VARIABLES().zutiMisc_EnableReflyOnlyIfBailedOrDied) {
            if (getPlayerFM().Gears.nOfGearsOnGr < 3) // No gears on the ground or we have chocks engaged
                ZutiSupportMethods.ZUTI_KIA_COUNTER++;
            // Start refly counter
            ZutiSupportMethods.ZUTI_KIA_DELAY_CLEARED = false;
            float penalty = Mission.MDS_VARIABLES().zutiMisc_ReflyKIADelay + ZutiSupportMethods.ZUTI_KIA_COUNTER * Mission.MDS_VARIABLES().zutiMisc_ReflyKIADelayMultiplier;
            ZutiSupportMethods_NetSend.setUserTimePenalty((NetUser) NetEnv.host(), (long) penalty);
            new ZutiTimer_Refly(penalty);
            System.out.println(((NetUser) NetEnv.host()).uniqueName() + " has died for " + ZutiSupportMethods.ZUTI_KIA_COUNTER + " times. Refly penalty is " + penalty + "s.");
            EventLog.type(true, ((NetUser) NetEnv.host()).uniqueName() + " has died for " + ZutiSupportMethods.ZUTI_KIA_COUNTER + " times. Refly penalty is " + penalty + "s.");
        }
        // -------------------------------------------------------------------
    }

    public static void setPlayerCaptured() {
        cur().bPlayerCaptured = true;
        cur().scoreCounter.playerCaptured();
    }

    public static void setPlayerGunner(NetGunner netgunner) {
        cur().PlayerGunner = netgunner;
        cur().scoreCounter.playerStartGunner();
    }

    public static void onActorDied(Actor actor, Actor actor_0_) {
        onActorDied(actor, actor_0_, true);
    }

    public static void onActorDied(Actor actor, Actor actorRemover, boolean bool) {
        // TODO: Added by SAS~Storebror: Fix possible null pointer dereference
        if (actor == null) throw new ActorException("onActorDied --- actor is null!");

        if (actor.getDiedFlag()) throw new ActorException("actor " + actor.getClass() + ":" + actor.name() + " alredy dead");

        // TODO: Added by |ZUTI|
        // ---------------------------------------------------------------------
        ZutiSupportMethods.updateMDSObjectives(actor);

        if (actor instanceof PlaneGeneric) // System.out.println("Static plane destroyed: " + actor.toString());
            ZutiSupportMethods_Air.decreaseBornPlacePlaneCounter((PlaneGeneric) actor);
        if (actor instanceof NetAircraft && Main.cur().netServerParams != null) // && Main.cur().netServerParams.isMaster())
        {
            boolean isLivePlane = actor.name().endsWith("_0");
            // Added by |ZUTI|
            Point3d pos = actor.pos.getAbsPoint();
            BornPlace bp = ZutiSupportMethods_Net.getNearestBornPlace_AnyArmy(pos.x, pos.y);
            // If home base that is nearest has plane limitations enabled BUT does not have decreasing AC numbers enabled, release AC to
            // users home base of origin!
            if (isLivePlane && (bp == null || bp.zutiEnablePlaneLimits && !bp.zutiDecreasingNumberOfPlanes)) {
                List bornPlaces = World.cur().bornPlaces;
                if (bornPlaces != null) {
                    NetUser netUser = ZutiSupportMethods.getNetUser(actor.name().substring(0, actor.name().length() - 2));
                    if (netUser != null && netUser.getBornPlace() >= 0 && netUser.getBornPlace() < World.cur().bornPlaces.size()) // Patch Pack 107, change last comparison from <= to < because array indices are 0-based!
                        bp = (BornPlace) World.cur().bornPlaces.get(netUser.getBornPlace());
                }
            }
            // So, if final home base has at least plane limits enabled, add AC to it
            if (bp != null && bp.zutiEnablePlaneLimits) {
                boolean planeStationary = ZutiSupportMethods.isPlaneStationary(((NetAircraft) actor).FM);
                String acName = ZutiSupportMethods_Air.getAircraftI18NName(((Aircraft) actor).getClass());
                boolean acIsUsable = ZutiSupportMethods_Air.isAircraftUsable(((Aircraft) actor).FM);

                /*
                 * System.out.println( "World - ac name: " + acName ); System.out.println( "World - isLivePlane: " + isLivePlane ); System.out.println( "World - plane stationary: " + planeStationary ); System.out.println( "World - plane usable: " +
                 * acIsUsable );
                 */

                // Process AI aircraft only if decreasing aircraft numbers is supported.
                if (!isLivePlane && planeStationary && bp.zutiDecreasingNumberOfPlanes) {
                    // AI Aircraft was destroyed on the ground
                    if (!acIsUsable) {
                        // It is damaged beyond usability. Deduct it from "home base" because it was not deducted,
                        // when spawned as live ac are.
                        System.out.println("World - decreasing aircraft >" + acName + "< numbers because AI aircraft was damaged beyond usability.");
                        ZutiSupportMethods_Net.removeAircraftAtBornPlace(bp, acName);
                    } else {
                        System.out.println("___________________________________________________");
                        System.out.println("World - Returning (usable) AI aircraft >" + acName + "<!");
                        if (actor.isAlive()) ZutiWeaponsManagement.returnRemainingAircraftResources((Aircraft) actor, null);
                        System.out.println("___________________________________________________");

                        ZutiSupportMethods_Net.addAircraftToBornPlace(bp, acName);
                    }
                } else {
                    // Only add because AC was deducted when player spawned.

                    // A bit more advanced usability check. If player is paratrooper and ac is moving along... bailing in mid air, no go on AC
                    if (World.isPlayerParatrooper() && World.getPlayerFM().getSpeed() > 100F) acIsUsable = false;

                    Point3d position = new Point3d();
                    position.x = bp.place.x;
                    position.y = bp.place.y;
                    if (bp.zutiDecreasingNumberOfPlanes && acIsUsable) {
                        System.out.println("___________________________________________________");
                        System.out.println("World - Returning (usable) aircraft >" + acName + "<!");
                        if (actor.isAlive()) ZutiWeaponsManagement.returnRemainingAircraftResources((Aircraft) actor, position);
                        System.out.println("___________________________________________________");

                        // For this case we will RETURN the AC.
                        ZutiSupportMethods_Net.addAircraftToBornPlace(bp, acName);
                    } else if (!bp.zutiDecreasingNumberOfPlanes) {
                        System.out.println("___________________________________________________");
                        System.out.println("World - Returning damaged aircraft >" + acName + "<!");
                        if (actor.isAlive()) ZutiWeaponsManagement.returnRemainingAircraftResources((Aircraft) actor, position);
                        System.out.println("___________________________________________________");

                        // For this case we will RETURN the AC.
                        // TODO: Patch Pack 107, skip adding crashed aircraft to homebase which have not been available there before
                        ZutiSupportMethods_Net.addAircraftToBornPlace(bp, acName, true);
                    }
                }
            }
        }
        // ---------------------------------------------------------------------

        Voice.testTargDestr(actor, actorRemover == remover ? null : actorRemover);
        trySendChatMsgDied(actor, actorRemover == remover ? actor : actorRemover);
        actor.setDiedFlag(true);
        if (actorRemover == remover && actor == cur().PlayerAircraft) cur().bPlayerRemoved = true;
        if (bool) EventLog.onActorDied(actor, actorRemover);
        Engine.cur.world.war.onActorDied(actor, actorRemover == remover ? null : actorRemover);
        Engine.cur.world.targetsGuard.checkActorDied(actor);
        cur();

        // TODO: Changed by |ZUTI|: to allow multi crew score sharing
        // ------------------------------------------------------------------------------------------------
        Actor curPlayerAc = cur().PlayerAircraft;
        // if (actor.getArmy() != 0 && actor != actor_2_ && actor_2_ != null && actor_1_ == actor_2_)
        if (actor != null && curPlayerAc != null && actorRemover != null && actor.getArmy() != 0 && actor != curPlayerAc && curPlayerAc.name().indexOf(actorRemover.name()) > -1) if (actor.getArmy() != curPlayerAc.getArmy()) {
            // TODO: Added by |ZUTI|: recognize enemy killed for AC only if the AC was actually ruined!
            // ---------------------------------------------------
            if (actor instanceof Aircraft) {
                // Add to enemy destroyed list ONLY if the plane actually was destroyed!
                // For friendly kills, consider all!
                if (!ZutiSupportMethods_Air.isAircraftOnTheGroundAndUndamaged(((Aircraft) actor).FM)) cur().scoreCounter.enemyDestroyed(actor);
            }
            // ---------------------------------------------------
            else cur().scoreCounter.enemyDestroyed(actor);
        } else cur().scoreCounter.friendDestroyed(actor);

        if (actor == cur().PlayerAircraft) {
            cur().checkViewOnPlayerDied(actor);
            if (Config.isUSE_RENDER()) {
                CmdEnv.top().exec("music RAND music/crash");
                ForceFeedback.stopMission();
            }
            if (actorRemover != cur().PlayerAircraft) {
                cur();
                if (!isPlayerParatrooper()) cur().scoreCounter.playerDead();
            }
        }
    }

    public void checkViewOnPlayerDied(Actor actor) {
        Point3d point3d = new Point3d();
        Point3d point3d_6_ = new Point3d();
        Point3d point3d_7_ = new Point3d();
        point3d.set(actor.pos.getAbsPoint());
        point3d_6_.set(actor.pos.getAbsPoint());
        point3d.z -= 40.0;
        point3d_6_.z += 40.0;
        Actor actor_8_ = Engine.collideEnv().getLine(point3d_6_, point3d, false, clipFilter, point3d_7_);
        if (Actor.isValid(actor_8_)) {
            if (Config.isUSE_RENDER() && Main3D.cur3D().viewActor() == actor) Main3D.cur3D().setViewFlow10(actor_8_, false);
        } else {
            ActorViewPoint actorviewpoint = new ActorViewPoint();
            actorviewpoint.pos.setAbs(actor.pos.getAbs());
            actorviewpoint.pos.reset();
            actorviewpoint.dreamFire(true);
            if (Config.isUSE_RENDER() && Main3D.cur3D().viewActor() == actor) {
                HookView hookview = Main3D.cur3D().hookView;
                ActorViewPoint actorviewpoint_9_ = actorviewpoint;
                float f = 3.0F;
                if (Main3D.cur3D().hookView != null) {
                    /* empty */
                }
                hookview.set(actorviewpoint_9_, f * HookView.defaultLen(), 10.0F, -10.0F);
                Main3D.cur3D().setView(actorviewpoint, true);
            }
        }
    }

    public static void onTaskComplete(Actor actor) {
        if (!actor.isTaskComplete()) {
            actor.setTaskCompleteFlag(true);
            Engine.cur.world.targetsGuard.checkTaskComplete(actor);
            if (actor.isNetMaster()) ((NetUser) NetEnv.host()).postTaskComplete(actor);
        }
    }

    private static void trySendChatMsgDied(Actor actor, Actor actor_10_) {
        if (Actor.isValid(actor_10_) && !Mission.isSingle() && Main.cur().chat != null && actor instanceof Aircraft && actor.net != null && actor.net.isMaster()) {
            Aircraft aircraft = (Aircraft) actor;
            NetUser netuser = aircraft.netUser();
            if (netuser != null && actor != actor_10_) {
                int i = Engine.cur.world.scoreCounter.getRegisteredType(actor_10_);
                if (!aircraft.FM.isSentBuryNote()) {
                    aircraft.FM.setSentBuryNote(true);
                    switch (i) {
                        case 0:
                            if (actor_10_ instanceof Aircraft && ((Aircraft) actor_10_).netUser() != null) {
                                Chat.sendLog(1, "gore_kill" + Rnd().nextInt(1, 5), (Aircraft) actor_10_, aircraft);
                                if (!aircraft.FM.isWasAirborne() && aircraft.isDamagerExclusive()) Chat.sendLogRnd(2, "gore_vulcher", (Aircraft) actor_10_, null);
                            } else Chat.sendLogRnd(1, "gore_ai", aircraft, null);
                            break;
                        case 1:
                            Chat.sendLogRnd(2, "gore_tank", aircraft, null);
                            break;
                        case 2:
                            Chat.sendLogRnd(2, "gore_gun", aircraft, null);
                            break;
                        case 3:
                            Chat.sendLogRnd(2, "gore_gun", aircraft, null);
                            break;
                        case 4:
                            Chat.sendLogRnd(2, "gore_killaaa", aircraft, null);
                            break;
                        case 7:
                            Chat.sendLogRnd(2, "gore_ship", aircraft, null);
                            break;
                        case 6:
                            Chat.sendLogRnd(2, "gore_killaaa", aircraft, null);
                            break;
                        default:
                            Chat.sendLogRnd(1, "gore_crashes", aircraft, null);
                    }
                }
            }
        }
    }

    public static Landscape land() {
        return Engine.land();
    }

    public static Wind wind() {
        return cur().wind;
    }

    public static World cur() {
        return Engine.cur.world;
    }

    public void resetGameClear() {
        EventLog.resetGameClear();
        this.front.resetGameClear();
        this.war.resetGameClear();
        this.bTimeOfDayConstant = false;
        if (this.statics != null) this.statics.resetGame();
        if (this.airports != null) {
            for (int i = 0; i < this.airports.size(); i++)
                ((Airport) this.airports.get(i)).destroy();
            this.airports.clear();
            this.airports = null;
        }
        while (this.runawayList != null) {
            Runaway runaway = this.runawayList;
            this.runawayList = runaway.next();
            if (Actor.isValid(runaway)) runaway.destroy();
        }
        this.targetsGuard.resetGame();
        // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
        this.triggersGuard.resetGame();
        // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
        this.scoreCounter.resetGame();
        NearestEnemies.resetGameClear();
        NearestAircraft.resetGameClear();
        Aircraft.resetGameClear();
        MsgExplosion.resetGame();
        MsgShot.resetGame();
        Regiment.resetGame();
        this.bornPlaces = null;
        this.bPlayerParatrooper = false;
        this.bPlayerDead = false;
        this.bPlayerCaptured = false;
        this.bPlayerRemoved = false;
        if (Actor.isValid(this.houseManager)) this.houseManager.destroy();
        this.houseManager = null;
    }

    public void resetGameCreate() {
        if (this.statics == null) this.statics = new Statics();
        this.ChiefMan = new ChiefManager();
        setPlayerAircraft(null);
        this.PlayerArmy = 1;
        this.rnd = new RangeRandom();
        this.voicebase = new Voice();
        setTimeofDay(12.0F);
        this.airports = new ArrayList();
        this.war.resetGameCreate();
        this.front.resetGameCreate();
        EventLog.resetGameCreate();
    }

    public void resetUser() {
        this.bPlayerParatrooper = false;
        this.bPlayerDead = false;
        ZutiSupportMethods.ZUTI_KIA_DELAY_CLEARED = false;
    }

    public World() {
        this.war = new War();
        this.bArcade = Config.cur.ini.get("game", "Arcade", this.bArcade);
        if (Config.LOCALE.equals("RU")) {
            this.bHighGore = Config.cur.ini.get("game", "HighGore", this.bHighGore);
            this.bHakenAllowed = Config.cur.ini.get("game", "HakenAllowed", this.bHakenAllowed);
        }
        this.blockMorseChat = Config.cur.ini.get("game", "BlockMorseChat", this.blockMorseChat);
        this.smallMapWPLabels = Config.cur.ini.get("game", "SmallMapWPLabels", this.smallMapWPLabels);
        this.showMorseAsText = Config.cur.ini.get("game", "ShowMorseAsText", this.showMorseAsText);
        useSmartAxis = Config.cur.ini.get("rts", "UseSmartAxis", useSmartAxis);
       // TODO: Storebror: True Random Number Generation!
        // ------------------------------------
        long lTime = System.currentTimeMillis();
        SecureRandom secRandom = new SecureRandom();
        secRandom.setSeed(lTime);
        int saveCountAccess = this.rnd.countAccess();
        long seed = secRandom.nextLong();
        System.out.println("Initializing Random Number Generator, Seed=" + seed);
        this.rnd = new RangeRandom(seed);
        Reflection.setInt(this.rnd, "countAccess", saveCountAccess);
        // ------------------------------------
        // TODO: +++ 4.11+ TrackIR implementation by SAS~Storebror +++
        if (Config.isUSE_RENDER()) System.out.println("Using new TrackIR: " + TrackIRWin.isUseNewTrackIR());
        // TODO: --- 4.11+ TrackIR implementation by SAS~Storebror ---
        // FIXME: Storebror: +++ TEST Bomb/Rocket Fuze/Delay Replication
        else this.userBombDelay = 0.5F;
        // FIXME: Storebror: --- TEST Bomb/Rocket Fuze/Delay Replication
    }

    public static float getTimeofDay() {
        if (cur().bTimeOfDayConstant) return cur().startTimeofDay * 2.7777778E-4F % 24.0F;
        return (Time.current() / 1000L + cur().startTimeofDay) * 2.7777778E-4F % 24.0F;
    }

    public static void setTimeofDay(float f) {
        int i = (int) (f * 3600.0F % 86400.0F);
        if (cur().bTimeOfDayConstant) cur().startTimeofDay = i;
        else cur().startTimeofDay = i - (int) (Time.current() / 1000L);
        if (curDebugLevel() != 0) {
            showCallingLine("setTimeofDay(" + f + ") called from ");
            System.out.println("startTimeofDay = " + cur().startTimeofDay);
        }
    }

    public static float g() {
        return Atmosphere.g();
    }

    public static Sun Sun() {
        return cur().sun;
    }

    public Sun sun() {
        return this.sun;
    }

    static {
        ScoreRegister.load();
    }

    private static int       debugLevel    = Integer.MIN_VALUE;
    private static final int DEBUG_DEFAULT = 0;

    private static int curDebugLevel() {
        if (debugLevel == Integer.MIN_VALUE) debugLevel = Config.cur.ini.get("Mods", "DEBUG_TIMEOFDAY", DEBUG_DEFAULT);
        return debugLevel;
    }

    public static void printDebugMessage(String theMessage) {
        if (curDebugLevel() == 0) return;
        System.out.println(theMessage);
    }

    public void save()
    {
        Config.cur.ini.setValue("rts", "UseSmartAxis", useSmartAxis ? "1" : "0");
//        Config.cur.ini.setValue("game", "SkipParatrooperViews", noParaTrooperViews ? "1" : "0");
//        Config.cur.ini.setValue("game", "NoMissionInfoHud", noMissionInfoHud ? "1" : "0");
//        Config.cur.ini.setValue("game", "lowInfoHud", lowInfoHud ? "1" : "0");
//        Config.cur.ini.setValue("game", "noKillInfoHud", noKillInfoHud ? "1" : "0");
        Config.cur.ini.setValue("game", "BlockMorseChat", blockMorseChat ? "1" : "0");
        Config.cur.ini.setValue("game", "SmallMapWPLabels", smallMapWPLabels ? "1" : "0");
        Config.cur.ini.setValue("game", "ShowMorseAsText", showMorseAsText ? "1" : "0");
        Config.cur.ini.saveFile();
    }
}