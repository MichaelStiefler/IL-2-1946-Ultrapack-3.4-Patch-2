/* 4.10.1 class */
package com.maddox.il2.ai;

import java.util.List;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.air.AirGroup;
import com.maddox.il2.ai.air.AirGroupList;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.NearestTargets;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.fm.AircraftState;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.Mission;
import com.maddox.il2.net.NetMissionTrack;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.TypeBomber;
import com.maddox.il2.objects.air.TypeFighter;
import com.maddox.il2.objects.air.TypeTransport;
import com.maddox.il2.objects.bridges.Bridge;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Time;

public class War {
    public static final int      TICK_DIV4  = 4;
    public static final int      TICK_DIV8  = 8;
    public static final int      TICK_DIV16 = 16;
    public static final int      TICK_DIV32 = 32;
    public static final int      ARMY_NUM   = 2;
    public static AirGroupList[] Groups     = new AirGroupList[2];
    private static int           curArmy    = 0;
    private static int           curGroup   = 0;
    private static Vector3d      tmpV       = new Vector3d();
    private static Vector3d      Ve         = new Vector3d();
    private static Vector3d      Vtarg      = new Vector3d();

    public static War cur() {
        return World.cur().war;
    }

    public War() {
        // TODO: Lutz mod
        ZutiSupportMethods_AI.bIniTakeOff = false;
    }

    public boolean isActive() {
        if (!Mission.isPlaying()) return false;
        if (NetMissionTrack.isPlaying()) return false;
        if (Mission.isSingle()) return true;

        // TODO: Edit by |ZUTI|
        // if (Mission.isServer() && Mission.isCoop())
        if (Mission.isServer() && (Mission.isCoop() || Mission.isDogfight())) return true;

        return false;
    }

    public void onActorDied(Actor actor, Actor actor_0_) {
        if (this.isActive()) if (actor instanceof Aircraft && ((Aircraft) actor).FM instanceof Maneuver) {
            Maneuver maneuver = (Maneuver) ((Aircraft) actor).FM;
            if (maneuver.Group != null) {
                maneuver.Group.delAircraft((Aircraft) actor);
                maneuver.Group = null;
            }
        }
    }

    public void missionLoaded() {
        /* empty */
    }

    public void resetGameCreate() {
        curArmy = 0;
        curGroup = 0;

        // TODO: Lutz Mod
        ZutiSupportMethods_AI.bIniTakeOff = false;
    }

    public void resetGameClear() {
        for (int i = 0; i < 2; i++)
            while (Groups[i] != null) {
                // TODO: Edit by |ZUTI|
                // Pinpointed to here. When errors are caught, all is fine!
                try {
                    Groups[i].G.release();
                } catch (Exception ex) {}
                try {
                    AirGroupList.delAirGroup(Groups, i, Groups[i].G);
                } catch (Exception ex) {}
            }

        // TODO: Lutz mod
        ZutiSupportMethods_AI.bIniTakeOff = false;
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    public void interpolateTickOld() {
        if (this.isActive()) try {
            if (Time.tickCounter() % 4 == 0) {
                this.checkCollisionForAircraft();
                if (Time.tickCounter() % 32 == 0) {
                    this.checkGroupsContact();
                    if (Time.tickCounter() % 64 == 0) this.delEmptyGroups();
                }
                this.upgradeGroups();
            }
            // TODO: Lutz mod
            // ---------------------------------------------------
            else if (com.maddox.rts.Time.tickCounter() % 81 == 0) ZutiSupportMethods_AI.checkGroupTakeOff(Groups);
            // ---------------------------------------------------

            this.formationUpdate();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

    public void interpolateTick() {
        if (!this.isActive()) return;
        try {
            // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
//          if(Time.tickCounter() % 128 == 0) // TODO: Changed by SAS~Storebror: All special trigger handling shifted to dedicated classes
              World.cur().triggersGuard.checkTask();
          // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
            if (Time.current() > 1000L) this.checkGroupsContact();
            this.checkCollisionForAircraft();
            if (Time.tickCounter() % 4 == 0) {
                if (Time.tickCounter() % 64 == 0) this.delEmptyGroups();
                this.upgradeGroups();
            }
            // TODO: Lutz mod
            // ---------------------------------------------------
            if (Time.tickCounter() % 81 == 0) ZutiSupportMethods_AI.checkGroupTakeOff(Groups);
            // ---------------------------------------------------
            this.formationUpdate();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }
    // TODO: --- TD AI code backport from 4.13 ---

    private void upgradeGroups() {
        int i = AirGroupList.length(Groups[curArmy]);
        if (i > curGroup) AirGroupList.getGroup(Groups[curArmy], curGroup).update();
        else {
            curArmy++;
            if (curArmy > 1) curArmy = 0;
            curGroup = 0;
            return;
        }
        curGroup++;
    }

    private void formationUpdate() {
        for (int i = 0; i < 2; i++)
            if (Groups[i] != null) {
                int i_1_ = AirGroupList.length(Groups[i]);
                for (int i_2_ = 0; i_2_ < i_1_; i_2_++)
                    AirGroupList.getGroup(Groups[i], i_2_).formationUpdate();
            }
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    public static boolean informOtherGroupsNearBy(AirGroup airgroup, AirGroupList airgrouplist, AirGroup airgroup1) {
        boolean flag = false;
        int i = AirGroupList.length(airgrouplist);
        for (int j = 0; j < i; j++) {
            AirGroup airgroup2 = AirGroupList.getGroup(airgrouplist, j);
            if (airgroup != airgroup2) {
                tmpV.sub(airgroup2.Pos, airgroup.Pos);
                double d = tmpV.length();
                if (airgroup2.clientGroup != null && airgroup == airgroup2.clientGroup) {
                    AirGroupList.addAirGroup(airgroup2.enemies, 0, airgroup1);
                    airgroup2.setEnemyFighters();
                    airgroup2.targetGroup = airgroup1;
                    airgroup2.bInitAttack = false;
                    flag = true;
                } else if (d < 12000D) {
                    AirGroupList.addAirGroup(airgroup2.enemies, 0, airgroup1);
                    airgroup2.setEnemyFighters();
                    flag = true;
                }
            }
        }

        return flag;
    }
    // TODO: --- TD AI code backport from 4.13 ---

    private void checkGroupsContact() {
        int i = AirGroupList.length(Groups[0]);
        int i_3_ = AirGroupList.length(Groups[1]);
        for (int i_4_ = 0; i_4_ < i; i_4_++) {
            AirGroup airgroup = AirGroupList.getGroup(Groups[0], i_4_);
            if (airgroup != null && airgroup.Pos != null) for (int i_5_ = 0; i_5_ < i_3_; i_5_++) {
                AirGroup airgroup1 = AirGroupList.getGroup(Groups[1], i_5_);
                if (airgroup1 != null && airgroup1.Pos != null) {
                    tmpV.sub(airgroup.Pos, airgroup1.Pos);
                    // TODO: +++ TD AI code backport from 4.13 +++
//						if (tmpV.lengthSquared() < 4.0E8 && airgroup.groupsInContact(airgroup1))
//						{
//							if (!AirGroupList.groupInList(airgroup.enemies[0], airgroup1))
//							{
//								AirGroupList.addAirGroup(airgroup.enemies, 0, airgroup1);
//								if (airgroup.airc[0] != null && airgroup1.airc[0] != null)
//									Voice.speakEnemyDetected(airgroup.airc[0], airgroup1.airc[0]);
//								airgroup.setEnemyFighters();
//							}
//							if (!AirGroupList.groupInList(airgroup1.enemies[0], airgroup))
//							{
//								AirGroupList.addAirGroup(airgroup1.enemies, 0, airgroup);
//								if (airgroup.airc[0] != null && airgroup1.airc[0] != null)
//									Voice.speakEnemyDetected(airgroup1.airc[0], airgroup.airc[0]);
//								airgroup1.setEnemyFighters();
//							}
//						}
//						else
//						{
//							if (AirGroupList.groupInList(airgroup.enemies[0], airgroup1))
//							{
//								AirGroupList.delAirGroup(airgroup.enemies, 0, airgroup1);
//								airgroup.setEnemyFighters();
//							}
//							if (AirGroupList.groupInList(airgroup1.enemies[0], airgroup))
//							{
//								AirGroupList.delAirGroup(airgroup1.enemies, 0, airgroup);
//								airgroup1.setEnemyFighters();
//							}
//						}

                    if (tmpV.lengthSquared() < 400000000D) {
                        if (!AirGroupList.groupInList(airgroup.enemies[0], airgroup1)) for (int i1 = 0; i1 < airgroup.nOfAirc; i1++) {
                            Aircraft aircraft = airgroup.airc[i1];
                            if (!aircraft.FM.isTick(32, 0)) continue;
                            int k1 = World.Rnd().nextInt(0, airgroup1.nOfAirc - 1);
                            VisCheck.checkGroup(aircraft, airgroup1.airc[k1], airgroup, airgroup1);
                            if (!airgroup.bSee) continue;
//                                    System.out.println("WAR setEnemyFighters 1");
                            informOtherGroupsNearBy(airgroup, Groups[0], airgroup1);
                            AirGroupList.addAirGroup(airgroup.enemies, 0, airgroup1);
                            if (airgroup.airc[0] != null && airgroup1.airc[k1] != null) Voice.speakEnemyDetected(airgroup.airc[0], airgroup1.airc[k1]);
                            airgroup.setEnemyFighters();
                            airgroup.bInitAttack = true;
                            airgroup.bSee = false;
                            break;
                        }
                        if (!AirGroupList.groupInList(airgroup1.enemies[0], airgroup)) for (int j1 = 0; j1 < airgroup1.nOfAirc; j1++) {
                            Aircraft aircraft1 = airgroup1.airc[j1];
                            if (!aircraft1.FM.isTick(32, 0)) continue;
                            int l1 = World.Rnd().nextInt(0, airgroup.nOfAirc - 1);
                            VisCheck.checkGroup(aircraft1, airgroup.airc[l1], airgroup1, airgroup);
                            if (!airgroup1.bSee) continue;
//                                    System.out.println("WAR setEnemyFighters 2");
                            informOtherGroupsNearBy(airgroup1, Groups[1], airgroup);
                            AirGroupList.addAirGroup(airgroup1.enemies, 0, airgroup);
                            if (airgroup.airc[0] != null && airgroup1.airc[l1] != null) Voice.speakEnemyDetected(airgroup1.airc[0], airgroup.airc[l1]);
                            airgroup1.setEnemyFighters();
                            airgroup1.bInitAttack = true;
                            airgroup1.bSee = false;
                            break;
                        }
                    } else {
                        if (AirGroupList.groupInList(airgroup.enemies[0], airgroup1)) {
                            AirGroupList.delAirGroup(airgroup.enemies, 0, airgroup1);
                            airgroup.setEnemyFighters();
                        }
                        if (AirGroupList.groupInList(airgroup1.enemies[0], airgroup)) {
                            AirGroupList.delAirGroup(airgroup1.enemies, 0, airgroup);
                            airgroup1.setEnemyFighters();
                        }
                    }

                }
                // TODO: --- TD AI code backport from 4.13 ---
            }
        }
    }

    private void delEmptyGroups() {
        int i = AirGroupList.length(Groups[0]);
        int i_7_ = AirGroupList.length(Groups[1]);
        for (int i_8_ = 0; i_8_ < i; i_8_++) {
            AirGroup airgroup = AirGroupList.getGroup(Groups[0], i_8_);
            if (airgroup != null && airgroup.nOfAirc == 0) {
                airgroup.release();
                AirGroupList.delAirGroup(Groups, 0, airgroup);
                // System.out.println("AirGroup released 0");
            }
        }
        for (int i_9_ = 0; i_9_ < i_7_; i_9_++) {
            AirGroup airgroup = AirGroupList.getGroup(Groups[1], i_9_);
            if (airgroup != null && airgroup.nOfAirc == 0) {
                airgroup.release();
                AirGroupList.delAirGroup(Groups, 1, airgroup);
                // System.out.println("AirGroup released 1");
            }
        }
    }

    private void checkCollisionForAircraft() {
        List list = Engine.targets();
        int i = list.size();
        for (int i_10_ = 0; i_10_ < i; i_10_++) {
            Actor actor = (Actor) list.get(i_10_);
            if (actor instanceof Aircraft) {
                FlightModel flightmodel = ((Aircraft) actor).FM;
                for (int i_11_ = i_10_ + 1; i_11_ < i; i_11_++) {
                    Actor actor_12_ = (Actor) list.get(i_11_);
                    if (i_10_ != i_11_ && actor_12_ instanceof Aircraft) {
                        FlightModel flightmodel_13_ = ((Aircraft) actor_12_).FM;
                        if (flightmodel instanceof Pilot && flightmodel_13_ instanceof Pilot) {
                            float f = (float) flightmodel.Loc.distanceSquared(flightmodel_13_.Loc);
                            if (!(f > 1.0E7F)) {
                                if (flightmodel.actor.getArmy() != flightmodel_13_.actor.getArmy()) {
                                    if (flightmodel instanceof RealFlightModel) testAsDanger(flightmodel, flightmodel_13_);
                                    if (flightmodel_13_ instanceof RealFlightModel) testAsDanger(flightmodel_13_, flightmodel);
                                }
                                Ve.sub(flightmodel.Loc, flightmodel_13_.Loc);
                                float f_14_ = (float) Ve.length();
                                Ve.normalize();
                                if (flightmodel.actor.getArmy() == flightmodel_13_.actor.getArmy()) {
                                    tmpV.set(Ve);
                                    flightmodel_13_.Or.transformInv(tmpV);
                                    if (tmpV.x > 0.0 && tmpV.y > -0.1 && tmpV.y < 0.1 && tmpV.z > -0.1 && tmpV.z < 0.1) ((Maneuver) flightmodel_13_).setShotAtFriend(f_14_);
                                    tmpV.set(Ve);
                                    tmpV.scale(-1.0);
                                    flightmodel.Or.transformInv(tmpV);
                                    if (tmpV.x > 0.0 && tmpV.y > -0.1 && tmpV.y < 0.1 && tmpV.z > -0.1 && tmpV.z < 0.1) ((Maneuver) flightmodel).setShotAtFriend(f_14_);
                                }
                                if (!(f > 20000.0F)) {
                                    float f_15_ = (flightmodel.actor.collisionR() + flightmodel_13_.actor.collisionR()) * 1.5F;
                                    f_14_ -= f_15_;
                                    Vtarg.sub(flightmodel_13_.Vwld, flightmodel.Vwld);
                                    Vtarg.scale(1.5);
                                    float f_16_ = (float) Vtarg.length();
                                    if (!(f_16_ < f_14_)) {
                                        Vtarg.normalize();
                                        Vtarg.scale(f_14_);
                                        Ve.scale(Vtarg.dot(Ve));
                                        Vtarg.sub(Ve);
                                        if (Vtarg.length() < f_15_ || f_14_ < 0.0F) {
                                            if (((Aircraft) actor).FM instanceof Pilot) ((Maneuver) ((Aircraft) actor).FM).setStrikeEmer(flightmodel_13_);
                                            if (((Aircraft) actor_12_).FM instanceof Pilot) ((Maneuver) ((Aircraft) actor_12_).FM).setStrikeEmer(flightmodel);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean isValidDanger(FlightModel fm) {
        if (fm.actor instanceof TypeTransport) {
            if (AircraftState.isCheaterFightAces(fm.actor)) return true;
            for (int trigger = 0; trigger < 2; trigger++)
                if (fm.CT.Weapons.length > trigger) {
                    BulletEmitter be[] = fm.CT.Weapons[trigger];
                    if (be != null) for (int gunindex = 0; gunindex < be.length; gunindex++)
                        if (be[gunindex] instanceof Gun) if (((Gun) be[gunindex]).haveBullets()) return true;
                }
            return false;
        }
        return true;
    }

    public static void testAsDanger(FlightModel fm1, FlightModel fm2) {
//        System.out.println("testAsDanger(" + fm1.actor.getClass().getName() + ", " + fm2.actor.getClass().getName() + ")");
//        if (fm1.actor instanceof TypeTransport) {
        if (isValidDanger(fm1)) {
            Ve.sub(fm2.Loc, fm1.Loc);
            fm1.Or.transformInv(Ve);
            if (Ve.x > 0.0) {
                float f = (float) Ve.length();
                Ve.normalize();
                ((Maneuver) fm2).incDangerAggressiveness(4, (float) Ve.x, f, fm1);
            }
        }
    }

    public static Aircraft getNearestFriend(Aircraft aircraft) {
        return getNearestFriend(aircraft, 10000.0F);
    }

    public static Aircraft getNearestFriend(Aircraft aircraft, float f) {
        Point3d point3d = aircraft.pos.getAbsPoint();
        double d = f * f;
        int i = aircraft.getArmy();
        Aircraft aircraft_18_ = null;
        List list = Engine.targets();
        int i_19_ = list.size();
        for (int i_20_ = 0; i_20_ < i_19_; i_20_++) {
            Actor actor = (Actor) list.get(i_20_);
            if (actor instanceof Aircraft && actor != aircraft && actor.getArmy() == i) {
                Point3d point3d_21_ = actor.pos.getAbsPoint();
                double d_22_ = (point3d.x - point3d_21_.x) * (point3d.x - point3d_21_.x) + (point3d.y - point3d_21_.y) * (point3d.y - point3d_21_.y) + (point3d.z - point3d_21_.z) * (point3d.z - point3d_21_.z);
                if (d_22_ < d) {
                    aircraft_18_ = (Aircraft) actor;
                    d = d_22_;
                }
            }
        }
        return aircraft_18_;
    }

    public static Aircraft getNearestFriendAtPoint(Point3d point3d, Aircraft aircraft, float f) {
        double d = f * f;
        int i = aircraft.getArmy();
        Aircraft aircraft_23_ = null;
        List list = Engine.targets();
        int i_24_ = list.size();
        for (int i_25_ = 0; i_25_ < i_24_; i_25_++) {
            Actor actor = (Actor) list.get(i_25_);
            if (actor instanceof Aircraft && actor.getArmy() == i) {
                Point3d point3d_26_ = actor.pos.getAbsPoint();
                double d_27_ = (point3d.x - point3d_26_.x) * (point3d.x - point3d_26_.x) + (point3d.y - point3d_26_.y) * (point3d.y - point3d_26_.y) + (point3d.z - point3d_26_.z) * (point3d.z - point3d_26_.z);
                if (d_27_ < d) {
                    aircraft_23_ = (Aircraft) actor;
                    d = d_27_;
                }
            }
        }
        return aircraft_23_;
    }

    public static Aircraft getNearestFriendlyFighter(Aircraft aircraft, float f) {
        double d = f * f;
        Point3d point3d = aircraft.pos.getAbsPoint();
        int i = aircraft.getArmy();
        Aircraft aircraft_28_ = null;
        List list = Engine.targets();
        int i_29_ = list.size();
        for (int i_30_ = 0; i_30_ < i_29_; i_30_++) {
            Actor actor = (Actor) list.get(i_30_);
            if (actor instanceof Aircraft) {
                Aircraft aircraft_31_ = (Aircraft) actor;
                if (aircraft_31_ != aircraft && aircraft_31_.getArmy() == i && aircraft_31_.getWing() != aircraft.getWing() && aircraft_31_ instanceof TypeFighter) {
                    Point3d point3d_32_ = aircraft_31_.pos.getAbsPoint();
                    double d_33_ = (point3d.x - point3d_32_.x) * (point3d.x - point3d_32_.x) + (point3d.y - point3d_32_.y) * (point3d.y - point3d_32_.y) + (point3d.z - point3d_32_.z) * (point3d.z - point3d_32_.z);
                    if (d_33_ < d) {
                        aircraft_28_ = aircraft_31_;
                        d = d_33_;
                    }
                }
            }
        }
        return aircraft_28_;
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    public static Aircraft getNearestEnemyOld(Aircraft aircraft, float f) {
        double d = f * f;
        Point3d point3d = aircraft.pos.getAbsPoint();
        int i = aircraft.getArmy();
        Aircraft aircraft_34_ = null;
        List list = Engine.targets();
        int i_35_ = list.size();
        for (int i_36_ = 0; i_36_ < i_35_; i_36_++) {
            Actor actor = (Actor) list.get(i_36_);
            if (actor instanceof Aircraft && actor.getArmy() != i) {
                Point3d point3d_37_ = actor.pos.getAbsPoint();
                double d_38_ = (point3d.x - point3d_37_.x) * (point3d.x - point3d_37_.x) + (point3d.y - point3d_37_.y) * (point3d.y - point3d_37_.y) + (point3d.z - point3d_37_.z) * (point3d.z - point3d_37_.z);
                if (d_38_ < d) {
                    aircraft_34_ = (Aircraft) actor;
                    d = d_38_;
                }
            }
        }
        return aircraft_34_;
    }

    public static Aircraft getNearestEnemy(Aircraft aircraft, float f) {
        double d = f * f;
        tmpPoint.set(aircraft.pos.getAbsPoint());
        int i = aircraft.getArmy();
        Aircraft aircraft1 = null;
        List list = Engine.targets();
        int j = list.size();
        for (int k = 0; k < j; k++) {
            Actor actor = (Actor) list.get(k);
            if (actor instanceof Aircraft && actor.getArmy() != i) {
                Point3d point3d = actor.pos.getAbsPoint();
                double d1 = (tmpPoint.x - point3d.x) * (tmpPoint.x - point3d.x) + (tmpPoint.y - point3d.y) * (tmpPoint.y - point3d.y) + (tmpPoint.z - point3d.z) * (tmpPoint.z - point3d.z);
                if (d1 < d) {
                    tmpPoint.z += 5D;
                    if (VisCheck.visCheck(tmpPoint, point3d, null, (Aircraft) actor)) {
                        aircraft1 = (Aircraft) actor;
                        d = d1;
                    }
                }
            }
        }

        return aircraft1;
    }

    // TODO: +++ Added by SAS~Storebror
    public static Aircraft getNearestEnemyBomber(Aircraft referenceAircraft, float maxDistance, int minEngines) {
        double squaredDistance = maxDistance * maxDistance;
        tmpPoint.set(referenceAircraft.pos.getAbsPoint());
        int army = referenceAircraft.getArmy();
        Aircraft foundBomber = null;
        List targets = Engine.targets();
        int targetsSize = targets.size();
        for (int targetsIndex = 0; targetsIndex < targetsSize; targetsIndex++) {
            Actor curTarget = (Actor) targets.get(targetsIndex);
            if (curTarget instanceof TypeBomber && curTarget.getArmy() != army) {
                if (((Aircraft)curTarget).FM.EI.engines.length < minEngines) continue;
                Point3d targetPos = curTarget.pos.getAbsPoint();
                double targetDistanceSquared = (tmpPoint.x - targetPos.x) * (tmpPoint.x - targetPos.x) + (tmpPoint.y - targetPos.y) * (tmpPoint.y - targetPos.y) + (tmpPoint.z - targetPos.z) * (tmpPoint.z - targetPos.z);
                if (targetDistanceSquared < squaredDistance) {
                    tmpPoint.z += 5D;
                    if (VisCheck.visCheck(tmpPoint, targetPos, null, (Aircraft) curTarget)) {
                        foundBomber = (Aircraft) curTarget;
                        squaredDistance = targetDistanceSquared;
                    }
                }
            }
        }
        return foundBomber;
    }
    // ---

    private static Point3d tmpPoint = new Point3d();
    // TODO: --- TD AI code backport from 4.13 ---

    public static Actor GetNearestEnemy(Actor actor, int i, float f) {
        return NearestTargets.getEnemy(0, i, actor.pos.getAbsPoint(), f, actor.getArmy());
    }

    public static Actor GetNearestEnemy(Actor actor, int i, float f, int i_39_) {
        return NearestTargets.getEnemy(i_39_, i, actor.pos.getAbsPoint(), f, actor.getArmy());
    }

    public static Actor GetNearestEnemy(Actor actor, int i, float f, Point3d point3d) {
        return NearestTargets.getEnemy(0, i, point3d, f, actor.getArmy());
    }

    public static Actor GetNearestEnemy(Actor actor, int i, Point3d point3d, float f) {
        return NearestTargets.getEnemy(i, 16, point3d, f, actor.getArmy());
    }

    public static Actor GetNearestEnemy(Actor actor, Point3d point3d, float f) {
        return NearestTargets.getEnemy(0, 16, point3d, f, actor.getArmy());
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    public static AirGroupList getEnemyGroups(Actor actor) {
        return actor.getArmy() != 1 ? Groups[0] : Groups[1];
    }

    public static AirGroupList getFriendlyGroups(Actor actor) {
        if (Groups.length < actor.getArmy()) return Groups[actor.getArmy()];
        else return null;
    }
    // TODO: --- TD AI code backport from 4.13 ---

    public static Actor GetNearestFromChief(Actor actor, Actor actor_40_) {
        if (!Actor.isAlive(actor_40_)) return null;
        Actor actor_41_ = null;
        if (actor_40_ instanceof Chief || actor_40_ instanceof Bridge) {
            int i = actor_40_.getOwnerAttachedCount();
            if (i < 1) return null;
            actor_41_ = (Actor) actor_40_.getOwnerAttached(0);
            double d = actor.pos.getAbsPoint().distance(actor_41_.pos.getAbsPoint());
            for (int i_42_ = 1; i_42_ < i; i_42_++) {
                Actor actor_43_ = (Actor) actor_40_.getOwnerAttached(i_42_);
                double d_44_ = actor.pos.getAbsPoint().distance(actor_43_.pos.getAbsPoint());
                if (d_44_ < d) {
                    d_44_ = d;
                    actor_41_ = actor_43_;
                }
            }
        }
        return actor_41_;
    }

    public static Actor GetRandomFromChief(Actor actor, Actor actor_45_) {
        if (!Actor.isAlive(actor_45_)) return null;
        if (actor_45_ instanceof Chief || actor_45_ instanceof Bridge) {
            int i = actor_45_.getOwnerAttachedCount();
            if (i < 1) return null;
            for (int i_46_ = 0; i_46_ < i; i_46_++) {
                Actor actor_47_ = (Actor) actor_45_.getOwnerAttached(World.Rnd().nextInt(0, i - 1));
                if (Actor.isValid(actor_47_) && actor_47_.isAlive()) return actor_47_;
            }
            for (int i_48_ = 0; i_48_ < i; i_48_++) {
                Actor actor_49_ = (Actor) actor_45_.getOwnerAttached(i_48_);
                if (Actor.isValid(actor_49_) && actor_49_.isAlive()) return actor_49_;
            }
        }
        return actor_45_;
    }

    public static Aircraft GetNearestEnemyAircraft(Actor actor, float f, int i) {

        Actor actor_50_ = GetNearestEnemy(actor, -1, f, i);
        if (actor_50_ != null) return (Aircraft) actor_50_;
        actor_50_ = GetNearestEnemy(actor, -1, f, 9);
        return (Aircraft) actor_50_;
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    public static Actor getNearestAnyFriendAtPoint(Point3d point3d, Aircraft aircraft, float f) {
        double d = f * f;
        int i = aircraft.getArmy();
        Actor actor = null;
        List list = Engine.targets();
        int j = list.size();
        for (int k = 0; k < j; k++) {
            Actor actor1 = (Actor) list.get(k);
            if (actor1.getArmy() == i) {
                Point3d point3d1 = actor1.pos.getAbsPoint();
                double d1 = (point3d.x - point3d1.x) * (point3d.x - point3d1.x) + (point3d.y - point3d1.y) * (point3d.y - point3d1.y) + (point3d.z - point3d1.z) * (point3d.z - point3d1.z);
                if (d1 < d) {
                    actor = actor1;
                    d = d1;
                }
            }
        }

        return actor;
    }
    // TODO: --- TD AI code backport from 4.13 ---

}