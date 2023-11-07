/*
 * 4.13.1 Class
 * Backport by SAS~Storebror for TD AI code implementation
 */

package com.maddox.il2.ai;

import com.maddox.JGP.Matrix4d;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.air.AirGroup;
import com.maddox.il2.ai.air.AirGroupList;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.Turret;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.Selector;
import com.maddox.il2.game.VisibilityChecker;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.rts.Time;

public class VisCheck {

    public VisCheck() {
    }

    public static void checkGroup(Aircraft aircraft, Aircraft aircraft1, AirGroup airgroup, AirGroup airgroup1) {
        if (aircraft == null || aircraft1 == null || aircraft == aircraft1) return;
        int i = ((Maneuver) aircraft.FM).get_maneuver();
        float f = 1.0F;
        float f1 = 1.0F;
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 1.0F;
        float f5 = 0.2F;
        if (i == 88 || i == 89) f = 2.0F;
        for (int j = 0; j < aircraft.FM.turret.length; j++) {
            Turret turret = aircraft.FM.turret[j];
            if (turret.bIsAIControlled && turret.bIsOperable) switch (turret.obsDir) {
                default:
                    break;

                case 1:
                    if (f4 < 2.0F) f4 += f5;
                    break;

                case 2:
                    if (f < 2.0F) f += f5;
                    break;

                case 3:
                    if (f2 < 2.0F) f2 += f5;
                    break;

                case 4:
                    if (f3 < 2.0F) f3 += f5;
                    break;
            }
        }

        if (commonCheck(aircraft, aircraft1, f4, f, f2, f3, f1)) airgroup.bSee = true;
    }

    public static boolean seekCheck(Aircraft aircraft, Aircraft aircraft1) {
        return commonCheck(aircraft, aircraft1, 3F, 1.0F, 2.0F, 2.0F, 1.0F);
    }

    private static boolean commonCheck(Aircraft aircraft, Aircraft aircraft1, float f, float f1, float f2, float f3, float f4) {
        dist = (float) aircraft.pos.getAbsPoint().distance(aircraft1.pos.getAbsPoint());
        float f5 = aircraft1.FM.Wingspan / 2.0F;
        float f6 = (float) Math.atan2(f5, dist) / f5;
        if (f6 < 0.00015F) return false;
        float f7 = 1.0F;
        boolean flag = false;
        float f8 = f6 * 4000F;
        f8 = (float) Math.pow(f8, 5D);
        f8 *= World.Sun().lightAtAlt((float) aircraft1.pos.getAbsPoint().z);
        if (f8 > 50F) f8 = 50F;
        tmpVGr1.sub(aircraft1.pos.getAbsPoint(), aircraft.pos.getAbsPoint());
        tmpVGr2.set(tmpVGr1);
        tmpVGr2.normalize();
        tmpVGr2.scale(12000D);
        tmpp1.set(aircraft1.pos.getAbsPoint());
        tmpp1.add(tmpVGr2);
        if (Main.cur().clouds != null && Main.cur().clouds.getVisibility(tmpp1, aircraft1.pos.getAbsPoint()) < 1.0F) flag = true;
        tmpVGr2.set(tmpVGr1);
        tmpVGr2.normalize();
        int i = ((Maneuver) aircraft.FM).get_maneuver();
        if (i == 90 || i == 91) f4 = 3F;
        else aircraft.FM.Or.transformInv(tmpVGr1);
        tmpVGr1.normalize();
        float f9 = World.Rnd().nextFloat(0.0F, 100F);
        boolean flag1 = aircraft1.FM.AS.bLandingLightOn || aircraft1.FM.AS.bNavLightsOn || aircraft1.FM.AS.astateSootStates[0] != 0 || aircraft1.FM.AS.astateSootStates[1] != 0 || aircraft1.FM.AS.astateSootStates[2] != 0
                || aircraft1.FM.AS.astateSootStates[3] != 0 || aircraft1.FM.AS.astateTankStates[0] > 4 || aircraft1.FM.AS.astateTankStates[1] > 4 || aircraft1.FM.AS.astateTankStates[2] > 4 || aircraft1.FM.AS.astateTankStates[3] > 4
                || isShooting(aircraft1) || aircraft1.FM.AS.astateEngineStates[0] > 3 || aircraft1.FM.AS.astateEngineStates[1] > 3 || aircraft1.FM.AS.astateEngineStates[2] > 3 || aircraft1.FM.AS.astateEngineStates[3] > 3
                || aircraft1.tmSearchlighted != 0L && Time.current() - aircraft1.tmSearchlighted < 1000L /* || aircraft1.FM.AS.astateCondensateEffects[0] != null */;
        if (flag1) f8 *= 10F;
        float f10 = (float) (aircraft.pos.getAbsPoint().z * -1.296 - 005D);
        float f11 = 1.0F;
        if (tmpVGr2.z < -0.9D) {
            f11 = 0.1F;
            if (flag) f11 = 1.0F;
            f7 = f4;
        } else if (tmpVGr1.x > 0.9D) {
            f7 = f;
            if (tmpVGr2.z >= f10 || flag) f11 = 3F;
            else f11 = 1.2F;
        } else if (tmpVGr1.x > -0.5D) {
            if (tmpVGr1.y < 0.0D) f7 = f3;
            else f7 = f2;
            if (tmpVGr2.z >= f10 || flag) f11 = 1.3F;
            else f11 = 0.5F;
        } else {
            f7 = f1;
            if (tmpVGr2.z >= f10 || flag) f11 = 0.2F;
            else f11 = 0.05F;
        }
        float f12 = (aircraft.FM.sight + 1) * 0.5F;
        float f13 = aircraft.FM.sight * 0.3F * f11 * f7;
        float f14 = f12 * f11 * f8 * f7;
        float f15 = f13 + f14;
        if (f9 > f15) return false;
        if (!visCheck(aircraft.pos.getAbsPoint(), aircraft1.pos.getAbsPoint(), aircraft, aircraft1)) return false;
        if (checkIfOwnPlaneBlocksVisibility(aircraft, aircraft1, false)) return false;
        if (flag1) dist = (float) (dist * 0.5D);
        if (flag) envLighting = (float) (envLighting * 1.15D);

//        boolean retVal = envLighting > dist;
//        System.out.println("commonCheck(" + aircraft.name() + ", " + aircraft1.name() + ", " + f + ", " + f1 + ", " + f2 + ", " + f3 + ", " + f4 + ") = " + retVal);
//        Exception test = new Exception("TEST");
//        test.printStackTrace();

        return envLighting > dist;
    }

    private static boolean isShooting(Aircraft aircraft) {
        return aircraft.FM.CT.isShooting();
    }

    public static boolean visCheck(Point3d point3d, Point3d point3d1, Aircraft aircraft, Aircraft aircraft1) {
        if (Landscape.rayHitHQ(point3d, point3d1, tmpp1)) return false;
        tmpVGr1.sub(point3d1, point3d);
        dist = (float) tmpVGr1.length();
        if (Main.cur().clouds != null) {
            float f = Main.cur().clouds.getVisibility(point3d1, point3d);
            float f1 = aircraft1.FM.Wingspan / 2.0F;
            float f2 = 0.5F - (float) Math.atan2(f1, dist) * 100F;
            if (f2 < 0.0F) f2 = 0.0F;
//            System.out.println("visCheck=" + f + " f2=" + f2); //FIXME: TEST!
            if (f <= f2) return false;
        }
        return checkVisibilityFactors(point3d, point3d1, aircraft, aircraft1, 1.0F, false);
    }

    private static boolean checkVisibilityFactors(Point3d point3d, Point3d point3d1, Aircraft aircraft, Aircraft aircraft1, float f, boolean flag) {
        v_1.set(tmpVGr1);
        v_1.normalize();
        if (World.Sun().ToSun.z > 0.0F) {
            Vsun.set(World.Sun().ToSun);
            double d = v_1.dot(Vsun);
            d = Math.abs(d);
            if (d > 0.995D) return false;
        }
        return checkAmbientPos(point3d, point3d1, aircraft, aircraft1, f, flag);
    }

    private static boolean checkAmbientPos(Point3d point3d, Point3d point3d1, Aircraft aircraft, Aircraft aircraft1, float f, boolean flag) {
        envLighting = World.Sun().lightAtAlt((float) point3d1.z);
        float f1 = (float) (point3d.z * -1.296E-005D);
        tmpVGr2.set(tmpVGr1);
        tmpVGr2.normalize();
        if (tmpVGr2.z >= f1) envLighting += 0.05F;
        if (World.Sun().ToSun.z > 0.0F) {
            envLighting *= 0.7F * (float) Main.cur().dotRangeFoe.dot();
            return envLighting > tmpVGr1.length();
        }
        if (aircraft1.FM.AS.bLandingLightOn || aircraft1.FM.AS.bNavLightsOn || aircraft1.FM.AS.astateTankStates[0] > 4 || aircraft1.FM.AS.astateTankStates[1] > 4 || aircraft1.FM.AS.astateTankStates[2] > 4 || aircraft1.FM.AS.astateTankStates[3] > 4
                || isShooting(aircraft1) || aircraft1.FM.AS.astateEngineStates[0] > 3 || aircraft1.FM.AS.astateEngineStates[1] > 3 || aircraft1.FM.AS.astateEngineStates[2] > 3 || aircraft1.FM.AS.astateEngineStates[3] > 3
                || aircraft1.tmSearchlighted != 0L && Time.current() - aircraft1.tmSearchlighted < 1000L)
            envLighting = 6000F;
        else {
            Vmoon.set(World.Sun().ToMoon);
            Vlight.set(World.Sun().ToLight);
            double d = v_1.dot(Vmoon);
            double d1 = v_1.dot(Vlight);
            d = Math.abs(d);
            d1 = Math.abs(d1);
            envLighting *= 2500F;
            if (d > 0.966D || d1 > 0.966D) envLighting = 0.5F * (float) Main.cur().dotRangeFoe.dot();
        }
        envLighting += f * 150F;
        if (flag) {
            if (Mission.isSingle() && aircraft != null && aircraft1.FM instanceof Maneuver && aircraft.FM instanceof Maneuver) {
                AirGroup airgroup = ((Maneuver) aircraft1.FM).Group;
                AirGroup airgroup1 = ((Maneuver) aircraft.FM).Group;
                if (airgroup1 != null && airgroup != null && !AirGroupList.groupInList(airgroup1.enemies[0], airgroup)) envLighting *= 0.5F;
            }
            return World.Rnd().nextFloat(0.0F, envLighting) > tmpVGr1.length();
        } else return envLighting > tmpVGr1.length();
    }

    public static boolean visCheckTurret(Turret turret, Aircraft aircraft, Aircraft aircraft1, boolean flag) {
        return visCheckTurret(turret, aircraft, aircraft1, flag, true);
    }
    
    public static boolean visCheckTurret(Turret turret, Aircraft aircraft, Aircraft aircraft1, boolean flag, boolean checkTurretVisibilityPlaneBlocking) {
        aircraft.hierMesh().setCurChunk(turret.indexB);
        aircraft.hierMesh().getChunkLocObj(pilotHeadLoc);
        pilotHeadLoc.add(turretIncrement);
        pilotHeadLoc.add(aircraft.pos.getAbs());
        if (Main.cur().clouds != null && Main.cur().clouds.getVisibility(pilotHeadLoc.getPoint(), aircraft1.pos.getAbsPoint()) < 0.05F) return false;
        tmpVGr1.sub(aircraft1.pos.getAbsPoint(), pilotHeadLoc.getPoint());
        if (!checkVisibilityFactors(pilotHeadLoc.getPoint(), aircraft1.pos.getAbsPoint(), aircraft, aircraft1, turret.health * aircraft.FM.Skill, flag)) return false;
        return !checkTurretVisibilityPlaneBlocking || !checkPlaneBlocking(aircraft, aircraft1, false);
    }

    public static boolean checkDefense(Aircraft aircraft, Aircraft aircraft1) {
        if (aircraft == null || aircraft1 == null || aircraft.pos == null || aircraft1.pos == null) return false;
        if (checkIfOwnPlaneBlocksVisibility(aircraft, aircraft1, true)) return true;
        tmpVGr1.sub(aircraft1.pos.getAbsPoint(), aircraft.pos.getAbsPoint());
        v_1.set(tmpVGr1);
        v_1.normalize();
        if (World.Sun().ToSun.z > 0.0F) {
            Vsun.set(World.Sun().ToSun);
            double d = v_1.dot(Vsun);
            d = Math.abs(d);
            if (d > 0.995D) return true;
        }
        return false;
    }

    public static boolean checkLeadShotBlock(Aircraft aircraft, Aircraft aircraft1) {
        if (aircraft.hierMesh().chunkFindCheck("Head1_D0") == -1) return false;
        if (aircraft1 == null) return false;
        boolean flag = false;
        if (aircraft1.FM.EI.getNum() == 1 && aircraft1.hierMesh().chunkFindCheck("Prop1_D0") != -1) flag = true;
        aircraft.pos.getAbs(Plane2WorldLoc);
        Plane2WorldLoc.getMatrix(Plane2WorldTM);
        aircraft.hierMesh().setCurChunk("Head1_D0");
        aircraft.hierMesh().getChunkLocObj(pilotHeadLoc);
        float f = aircraft.getEyeLevelCorrection();
        if (f > 0.0F && aircraft.FM.Skill < 2) f = Aircraft.cvt(aircraft.FM.Skill, 0.0F, 3F, 0.0F, f);
        neckToEyeIncrement.set(0.20000000298023224D, 0.0D, 0.1F + aircraft.getEyeLevelCorrection(), 0.0F, 0.0F, 0.0F);
        pilotHeadLoc.add(neckToEyeIncrement);
        pilotHeadLoc.add(aircraft.pos.getAbs());
        if (flag) {
            aircraft1.hierMesh().setCurChunk("Prop1_D0");
            aircraft1.hierMesh().getChunkLocObj(tmpLoc);
        } else {
            float f1 = aircraft1.FM.Length / 2.0F;
            tmpLoc.set(f1, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        }
        tmpLoc.add(aircraft1.pos.getAbs());
        tmpV3d.sub(tmpLoc.getPoint(), pilotHeadLoc.getPoint());
        tmpV3d.normalize();
        tmpV3d.scale(1.0D);
        tmpp1.set(pilotHeadLoc.getPoint());
        tmpp1.add(tmpV3d);
        float f2 = aircraft.hierMesh().detectCollisionLine(aircraft.pos.getAbs(), tmpp1, tmpLoc.getPoint());
        return f2 != -1F;
    }

    private static boolean checkIfOwnPlaneBlocksVisibility(Aircraft aircraft, Aircraft aircraft1, boolean flag) {
        // +++ TODO: Storebror: null Checks added +++
        if (aircraft == null) return false;
        if (aircraft.hierMesh() == null) return false;
        // --- TODO: Storebror: null Checks added ---
        if (aircraft.hierMesh().chunkFindCheck("Head1_D0") == -1) return false;
        boolean flag1 = false;
        aircraft.pos.getAbs(Plane2WorldLoc);
        Plane2WorldLoc.getMatrix(Plane2WorldTM);
        aircraft.hierMesh().setCurChunk("Head1_D0");
        aircraft.hierMesh().getChunkLocObj(pilotHeadLoc);
        float f = 0.0F;
        if (World.Rnd().nextFloat() < (aircraft.FM.Skill + 2) / 10F) f = 0.1F + 0.05F * aircraft.FM.Skill;
        float f1 = World.Rnd().nextFloat(-f, f);
        neckToEyeIncrement.set(0.2D, f1, 0.1F + aircraft.getEyeLevelCorrection(), 0.0F, 0.0F, 0.0F);
        pilotHeadLoc.add(neckToEyeIncrement);
        pilotHeadLoc.add(aircraft.pos.getAbs());
        flag1 = checkPlaneBlocking(aircraft, aircraft1, flag);
        if (!flag1) return flag1;
        for (int i = 0; i < aircraft.FM.turret.length; i++) {
            Turret turret = aircraft.FM.turret[i];
            if (turret.bIsAIControlled && turret.bIsOperable && !turret.bIsShooting) {
                aircraft.hierMesh().setCurChunk("Turret" + (i + 1) + "B_D0");
                aircraft.hierMesh().getChunkLocObj(pilotHeadLoc);
                pilotHeadLoc.add(turretIncrement);
                pilotHeadLoc.add(aircraft.pos.getAbs());
                boolean flag2 = checkPlaneBlocking(aircraft, aircraft1, flag);
                if (!flag2) return flag2;
            }
        }

        return true;
    }

    private static boolean checkPlaneBlocking(Aircraft aircraft, Aircraft aircraft1, boolean flag) {
        // +++ TODO: Storebror: null Checks added +++
        if (aircraft1 == null) return false;
        if (aircraft1.pos == null) return false;
        // --- TODO: Storebror: null Checks added ---
        tmpV3d.sub(aircraft1.pos.getAbsPoint(), pilotHeadLoc.getPoint());
        double d = tmpV3d.length();
        tmpV3d.normalize();
        tmpV3d.scale(1.0D);
        tmpp1.set(pilotHeadLoc.getPoint());
        tmpp1.add(tmpV3d);
        if (!flag) {
            float f1 = aircraft.hierMesh().detectCollisionLine(aircraft.pos.getAbs(), tmpp1, aircraft1.pos.getAbsPoint());
            if (f1 == -1F) return false;
            boolean flag1 = aircraft1.FM.AS.astateSootStates[0] != 0 || aircraft1.FM.AS.astateSootStates[1] != 0 || aircraft1.FM.AS.astateSootStates[2] != 0 || aircraft1.FM.AS.astateSootStates[3] != 0 || aircraft1.FM.AS.astateTankStates[0] > 4
                    || aircraft1.FM.AS.astateTankStates[1] > 4 || aircraft1.FM.AS.astateTankStates[2] > 4 || aircraft1.FM.AS.astateTankStates[3] > 4 || aircraft1.FM.AS.astateEngineStates[0] > 3 || aircraft1.FM.AS.astateEngineStates[1] > 3
                    || aircraft1.FM.AS.astateEngineStates[2] > 3 || aircraft1.FM.AS.astateEngineStates[3] > 3 /* || aircraft1.FM.AS.astateCondensateEffects[0] != null */;
            if (flag1) {
                tmpLoc.set(-250D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
                tmpLoc.add(aircraft1.pos.getAbs());
                tmpV3d.sub(tmpLoc.getPoint(), pilotHeadLoc.getPoint());
                tmpV3d.normalize();
                tmpV3d.scale(1.0D);
                tmpp1.set(pilotHeadLoc.getPoint());
                tmpp1.add(tmpV3d);
                float f2 = aircraft.hierMesh().detectCollisionLine(aircraft.pos.getAbs(), tmpp1, tmpLoc.getPoint());
                if (f2 == -1F) return false;
            }
            boolean flag2 = aircraft1.FM.CT.WeaponControl[0] || aircraft1.FM.CT.WeaponControl[1];
            if (flag2) {
                tmpLoc.set(250D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
                tmpLoc.add(aircraft1.pos.getAbs());
                tmpV3d.sub(tmpLoc.getPoint(), pilotHeadLoc.getPoint());
                tmpV3d.normalize();
                tmpV3d.scale(1.0D);
                tmpp1.set(pilotHeadLoc.getPoint());
                tmpp1.add(tmpV3d);
                float f3 = aircraft.hierMesh().detectCollisionLine(aircraft.pos.getAbs(), tmpp1, tmpLoc.getPoint());
                if (f3 == -1F) return false;
            }
        }
        if (d < 2000D || flag) {
            float f5 = aircraft1.FM.Wingspan / 2.0F;
            tmpLoc.set(0.0D, 1.0D * f5, 0.0D, 0.0F, 0.0F, 0.0F);
            tmpLoc.add(aircraft1.pos.getAbs());
            tmpV3d.sub(tmpLoc.getPoint(), pilotHeadLoc.getPoint());
            tmpV3d.normalize();
            tmpV3d.scale(1.0D);
            tmpp1.set(pilotHeadLoc.getPoint());
            tmpp1.add(tmpV3d);
            float f4 = aircraft.hierMesh().detectCollisionLine(aircraft.pos.getAbs(), tmpp1, tmpLoc.getPoint());
            if (f4 == -1F) return false;
            tmpLoc.set(0.0D, -1D * f5, 0.0D, 0.0F, 0.0F, 0.0F);
            tmpLoc.add(aircraft1.pos.getAbs());
            tmpV3d.sub(tmpLoc.getPoint(), pilotHeadLoc.getPoint());
            tmpV3d.normalize();
            tmpV3d.scale(1.0D);
            tmpp1.set(pilotHeadLoc.getPoint());
            tmpp1.add(tmpV3d);
            f4 = aircraft.hierMesh().detectCollisionLine(aircraft.pos.getAbs(), tmpp1, tmpLoc.getPoint());
            if (f4 == -1F) return false;
        }
        return true;
    }

    public static boolean isVisibilityBlockedByClouds(FlightModel flightmodel, FlightModel flightmodel1, boolean flag) {
//        if (Main.cur().clouds == null) System.out.println("Main.cur().clouds == null");
//        if (flightmodel == null) System.out.println("flightmodel == null"); else if (flightmodel.actor == null) System.out.println("flightmodel.actor == null");
//        if (flightmodel1 == null) System.out.println("flightmodel1 == null"); else if (flightmodel1.actor == null) System.out.println("flightmodel1.actor == null");

        if (Main.cur().clouds == null || flightmodel == null || flightmodel1 == null || flightmodel.actor == null || flightmodel1.actor == null) return false;
        float f = Main.cur().clouds.getVisibility(flightmodel.actor.pos.getAbsPoint(), flightmodel1.actor.pos.getAbsPoint());
//        HUD.training("iVBBC=" + f); //FIXME: TEST!
//        System.out.println("isVisibilityBlockedByClouds=" + f); //FIXME: TEST!
        return f <= 0.0F;
    }

    public static boolean isVisibilityBlockedByDarkness(FlightModel flightmodel, FlightModel flightmodel1) {
        if (flightmodel == null || flightmodel1 == null || flightmodel.actor == null || flightmodel1.actor == null) return false;
        if (World.Sun().ToSun.z > 0.0F) return false;
        else {
            tmpVGr1.sub(flightmodel1.Loc, flightmodel.Loc);
            v_1.sub(flightmodel.Loc, flightmodel1.Loc);
            v_1.normalize();
            return !checkAmbientPos(flightmodel.Loc, flightmodel1.Loc, (Aircraft) flightmodel.actor, (Aircraft) flightmodel1.actor, flightmodel.Skill, false);
        }
    }

    public static Actor playerVisibilityCheck(Aircraft aircraft, boolean flag, float f) {
        if (Main.cur().netServerParams != null && Main.cur().netServerParams.isMaster() && aircraft != World.getPlayerAircraft() && !flag) {
            Actor actor = Selector.getTarget();
            if (actor != null && actor instanceof Aircraft) {
                Aircraft aircraft1 = (Aircraft) actor;
                if (aircraft1.getArmy() != aircraft.getArmy()) {
                    AirGroupList airgrouplist1 = War.getFriendlyGroups(aircraft);
                    AirGroup airgroup1 = ((Pilot) aircraft.FM).Group;
                    AirGroup airgroup2 = ((Maneuver) aircraft1.FM).Group;
                    War.informOtherGroupsNearBy(airgroup1, airgrouplist1, airgroup2);
                    AirGroupList.addAirGroup(airgroup1.enemies, 0, airgroup2);
                    airgroup1.setEnemyFighters();
                    airgroup1.bInitAttack = true;
//                    System.out.println("ARGH1!" + f); //FIXME: TEST!
                }
            }
            return actor;
        }
        if (Mission.isSingle() || Main.cur().netServerParams != null && Main.cur().netServerParams.isMaster()) {
            if (Config.isUSE_RENDER() && Main3D.cur3D().isViewOutside()) return null;
            AirGroupList airgrouplist = War.getEnemyGroups(aircraft);
            AirGroup airgroup = ((Pilot) aircraft.FM).Group;
            if (airgrouplist == null || airgroup == null) return null;
            int i = AirGroupList.length(airgrouplist);
            float f1 = 20F;
            for (int j = 0; j < i; j++) {
                AirGroup airgroup3 = AirGroupList.getGroup(airgrouplist, j);
                if (airgroup3 != null) {
                    tmpVGr1.sub(airgroup3.Pos, aircraft.pos.getAbsPoint());
                    if (tmpVGr1.length() < 10000F * f) for (int k = 0; k < airgroup3.nOfAirc; k++) {
                        Aircraft aircraft3 = airgroup3.airc[k];
                        if (aircraft3.getArmy() != aircraft.getArmy() && !isVisibilityBlockedByClouds(aircraft.FM, aircraft3.FM, true)) {
                            VisibilityChecker.checkLandObstacle = true;
                            VisibilityChecker.checkCabinObstacle = true;
                            VisibilityChecker.checkPlaneObstacle = true;
                            VisibilityChecker.checkObjObstacle = true;
                            float f2 = VisibilityChecker.computeVisibility(null, aircraft3);
                            if (f2 > 0.0F && VisibilityChecker.resultAng <= 15F * f && VisibilityChecker.resultAng <= f1) {
                                f1 = VisibilityChecker.resultAng;
                                if (checkVisibilityFactors(aircraft.pos.getAbsPoint(), aircraft3.pos.getAbsPoint(), aircraft, aircraft3, 3F, false)) {
                                    AirGroupList airgrouplist2 = War.getFriendlyGroups(aircraft);
                                    if (!AirGroupList.groupInList(airgroup.enemies[0], airgroup3)) AirGroupList.addAirGroup(airgroup.enemies, 0, airgroup3);
                                    boolean flag1 = War.informOtherGroupsNearBy(airgroup, airgrouplist2, airgroup3);
                                    airgroup.setEnemyFighters();
                                    airgroup.bInitAttack = true;
                                    if (flag && flag1 || airgroup.nOfAirc > 1) Voice.speakRooger(aircraft);
                                    return aircraft3;
                                }
                            }
                        }
                    }
                }
            }

        } else {
            if (Main3D.cur3D().isViewOutside()) return null;
            Actor actor1 = Selector.look(true, false, Main3D.cur3D().camera3D, aircraft.getArmy(), -1, aircraft, false);
            if (actor1 != null && actor1 instanceof Aircraft) {
                Aircraft aircraft2 = (Aircraft) actor1;
                if (isVisibilityBlockedByClouds(aircraft.FM, aircraft2.FM, true)) return null;
                if (VisibilityChecker.resultAng > 15F * f) return null;
                if (!checkVisibilityFactors(aircraft.pos.getAbsPoint(), aircraft2.pos.getAbsPoint(), aircraft, aircraft2, 3F, false)) return null;
                Selector.setTarget(aircraft2);
            }
        }
        return null;
    }

    private static Vector3d  Vsun               = new Vector3d();
    private static Vector3d  Vmoon              = new Vector3d();
    private static Vector3d  Vlight             = new Vector3d();
    private static Vector3d  tmpV3d             = new Vector3d();
    private static Point3d   tmpp1              = new Point3d();
    private static Vector3d  tmpVGr1            = new Vector3d();
    private static Vector3d  tmpVGr2            = new Vector3d();
    private static float     envLighting;
    private static float     dist;
    private static final Loc neckToEyeIncrement = new Loc(0.20000000298023224D, 0.0D, 0.10000000149011612D, 0.0F, 0.0F, 0.0F);
    private static final Loc turretIncrement    = new Loc(0.30000001192092896D, 0.0D, 0.20000000298023224D, 0.0F, 0.0F, 0.0F);
    private static Loc       pilotHeadLoc       = new Loc();
    private static Loc       Plane2WorldLoc     = new Loc();
    private static Matrix4d  Plane2WorldTM      = new Matrix4d();
    private static Loc       tmpLoc             = new Loc();
    private static Vector3d  v_1                = new Vector3d();
    static final float       pitClip            = 1F;

}
