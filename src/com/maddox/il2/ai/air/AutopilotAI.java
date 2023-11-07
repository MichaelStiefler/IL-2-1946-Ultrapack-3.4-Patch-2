/*4.10.1 class*/
package com.maddox.il2.ai.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Airport;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.Orientation;
import com.maddox.il2.fm.Autopilotage;
import com.maddox.il2.fm.Controls;
import com.maddox.il2.fm.FMMath;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Mission;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.TypeBomber;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.rts.Property;
import com.maddox.sas1946.il2.util.CommonTools;

public class AutopilotAI extends Autopilotage {
    public boolean             bWayPoint;
    public boolean             bStabAltitude;
    public boolean             bStabSpeed;
    public boolean             bStabDirection;
    protected double           StabAltitude;
    protected double           StabSpeed;
    protected double           StabDirection;
    protected Pilot            FM;
    protected WayPoint         WWPoint;
    protected Point3d          WPoint  = new Point3d();
    private static Point3d     P       = new Point3d();
    private static Point3d     PlLoc   = new Point3d();
    private static Orientation O       = new Orientation();
    protected Vector3d         courseV = new Vector3d();
    protected Vector3d         windV   = new Vector3d();
    private float              Ail;
    private float              Pw;
    private float              Ev;
    private float              SA;
    private Vector3d           Ve      = new Vector3d();
    
    // Fighting AI climbing above Waypoint altitude
    private float              aEARAF = 0F;
    private float              aEARAFFP = 0F;

    // TODO: Guided Missiles Update
    private boolean  overrideMissileControl = false;
    private Controls theMissileControls     = null;

    // TODO: +++ Backport from 4.13
    private float avoidance;
    // ---

    public AutopilotAI(FlightModel flightmodel) {
        this.FM = (Pilot) flightmodel;
    }

    public boolean getWayPoint() {
        return this.bWayPoint;
    }

    public boolean getStabAltitude() {
        return this.bStabAltitude;
    }

    public boolean getStabSpeed() {
        return this.bStabSpeed;
    }

    public boolean getStabDirection() {
        return this.bStabDirection;
    }

    public void setWayPoint(boolean bool) {
        this.bWayPoint = bool;
        if (bool) {
            this.bStabSpeed = false;
            this.bStabAltitude = false;
            this.bStabDirection = false;
            if (this.WWPoint != null) {
                this.WWPoint.getP(this.WPoint);
                this.StabSpeed = this.WWPoint.Speed;
                this.StabAltitude = this.WPoint.z;
            } else {
                this.StabAltitude = 1000.0;
                this.StabSpeed = 80.0;
            }
            this.StabDirection = O.getAzimut();
        }
    }

    public void setStabAltitude(boolean bool) {
        this.bStabAltitude = bool;
        if (bool) {
            this.bWayPoint = false;
            this.FM.getLoc(P);
            this.StabAltitude = P.z;
            if (!this.bStabSpeed) this.StabSpeed = this.FM.getSpeed();
            this.Pw = this.FM.CT.PowerControl;
        }
    }

    public void setStabAltitude(float f) {
        this.bStabAltitude = true;
        this.bWayPoint = false;
        this.FM.getLoc(P);
        this.StabAltitude = f;
        if (!this.bStabSpeed) this.StabSpeed = this.FM.getSpeed();
        this.Pw = this.FM.CT.PowerControl;
    }

    public void setStabSpeed(boolean bool) {
        this.bStabSpeed = bool;
        if (bool) {
            this.bWayPoint = false;
            this.StabSpeed = this.FM.getSpeed();
        }
    }

    public void setStabSpeed(float f) {
        this.bStabSpeed = true;
        this.bWayPoint = false;
        this.StabSpeed = f / 3.6F;
    }

    public void setStabDirection(boolean bool) {
        this.bStabDirection = bool;
        if (bool) {
            this.bWayPoint = false;
            O.set(this.FM.Or);
            this.StabDirection = O.getAzimut();
            this.Ail = this.FM.CT.AileronControl;
        }
    }

    public void setStabDirection(float f) {
        this.bStabDirection = true;
        this.bWayPoint = false;
        this.StabDirection = (f + 3600.0F) % 360.0F;
        this.Ail = this.FM.CT.AileronControl;
    }

    public void setStabAll(boolean bool) {
        this.bWayPoint = false;
        this.setStabDirection(bool);
        this.setStabAltitude(bool);
        this.setStabSpeed(bool);
        this.setStabDirection(bool);
    }

    public float getWayPointDistance() {
        if (this.WPoint == null) return 1000000.0F;
        this.way.curr().getP(P);
        P.sub(this.FM.Loc);
        return (float) Math.sqrt(P.x * P.x + P.y * P.y);
    }

    private void voiceCommand(Point3d point3d, Point3d point3d_0_) {
        this.Ve.sub(point3d_0_, point3d);
        float f = 57.32484F * (float) Math.atan2(this.Ve.x, this.Ve.y);
        int i = (int) f;
        i = (i + 180) % 360;
        Voice.speakHeading((Aircraft) this.FM.actor, i);
        Voice.speakAltitude((Aircraft) this.FM.actor, (int) point3d.z);
    }

    public void update(float f) {
        // TODO: Guided Missiles Update
        if (this.overrideMissileControl) this.theMissileControls.WeaponControl[this.theMissileControls.rocketHookSelected] = true;
        this.FM.getLoc(PlLoc);
        this.SA = (float) Math.max(this.StabAltitude, Engine.land().HQ_Air(PlLoc.x, PlLoc.y) + 5.0);
        // TODO: +++ Backport from 4.13
        if(FM.Group != null && FM.Group.getAaaNum() > 3F && ((Aircraft)FM.actor).aircIndex() == 0 && FM.isTick(165, 0))
            avoidance = (float)(1 - World.Rnd().nextInt(0, 1) * 2) * World.Rnd().nextFloat(15F, 30F);
        // ---
        do {
            if (this.bWayPoint) {
                if (this.WWPoint != this.way.auto(PlLoc) || this.way.isReached(PlLoc)) {
                    this.WWPoint = this.way.auto(PlLoc);
                    this.WWPoint.getP(this.WPoint);
                    // TODO: +++ Backport from 4.13
//                    if (((Aircraft) this.FM.actor).aircIndex() == 0 && !this.way.isLanding()) this.voiceCommand(this.WPoint, PlLoc);
                    if (((Aircraft) this.FM.actor).aircIndex() == 0 && !this.way.isLanding()) {
                        this.voiceCommand(this.WPoint, PlLoc);
                        byte formationType = FM.formationType = (byte)way.curr().formation;
                        if(FM.Group != null)
                            FM.Group.setFormationAndScale(formationType, 1.0F, true);
                    }
                    // ---
                    this.StabSpeed = this.WWPoint.Speed - 2.0F * ((Aircraft) this.FM.actor).aircIndex();
                    this.StabAltitude = this.WPoint.z;
                    if (this.WWPoint.Action == 3) {
                        Actor actor = this.WWPoint.getTarget();
                        if (actor != null) this.FM.target_ground = null;
                        else if ((Aircraft) this.FM.actor instanceof TypeBomber && this.FM.CT.Weapons[3] != null && this.FM.CT.Weapons[3][0] != null && this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1].haveBullets()) {
                            this.FM.CT.BayDoorControl = 1.0F;
                            Pilot pilot = this.FM;
                            while (pilot.Wingman != null) {
                                pilot = (Pilot) pilot.Wingman;
                                pilot.CT.BayDoorControl = 1.0F;
                            }
                        }
                    } else {
                        // TODO: +++ Backport from 4.13
//                        if ((Aircraft) this.FM.actor instanceof TypeBomber) {
                        if ((Aircraft) this.FM.actor instanceof TypeBomber || FM.CT.bHasBayDoorControl) {
                        // ---
                            this.FM.CT.BayDoorControl = 0.0F;
                            Pilot pilot = this.FM;
                            while (pilot.Wingman != null) {
                                pilot = (Pilot) pilot.Wingman;
                                pilot.CT.BayDoorControl = 0.0F;
                            }
                        }
                        Actor actor = this.WWPoint.getTarget();
                        if (actor instanceof Aircraft) if (actor.getArmy() == this.FM.actor.getArmy()) this.FM.airClient = ((Aircraft) actor).FM;
                        else this.FM.target = ((Aircraft) actor).FM;
                    }
                    if (this.way.isLanding()) {
                        this.FM.getLoc(P);
                        if (this.way.Cur() > 3 && P.z > this.WPoint.z + 500.0) this.way.setCur(1);
                        if (this.way.Cur() == 5) {
                            if (Main.cur().mission != null) {
                                /* empty */
                            }
                            // TODO: Altered by |ZUTI|: fixed variables references
                            if (!Mission.isDogfight() || !Mission.MDS_VARIABLES().zutiMisc_DisableAIRadioChatter) Voice.speakLanding((Aircraft) this.FM.actor);
                        }
                        if (this.way.Cur() == 6 || this.way.Cur() == 7) {
                            int i = 0;
                            if (Actor.isAlive(this.way.landingAirport)) i = this.way.landingAirport.landingFeedback(this.WPoint, (Aircraft) this.FM.actor);
                            if (i == 0) {
                                if (Main.cur().mission != null) {
                                    /* empty */
                                }
                                // TODO: Altered by |ZUTI|: fixed variables references
                                if (!Mission.isDogfight() || !Mission.MDS_VARIABLES().zutiMisc_DisableAIRadioChatter) Voice.speakLandingPermited((Aircraft) this.FM.actor);
                            }
                            if (i == 1) {
                                if (Main.cur().mission != null) {
                                    /* empty */
                                }
                                // TODO: Altered by |ZUTI|: fixed variables references
                                if (!Mission.isDogfight() || !Mission.MDS_VARIABLES().zutiMisc_DisableAIRadioChatter) Voice.speakLandingDenied((Aircraft) this.FM.actor);
                                this.way.first();
                                this.FM.push(2);
                                this.FM.push(2);
                                this.FM.push(2);
                                this.FM.push(2);
                                this.FM.pop();
                                if (Main.cur().mission != null) {
                                    /* empty */
                                }
                                // TODO: Altered by |ZUTI|: fixed variables references
                                if (!Mission.isDogfight() || !Mission.MDS_VARIABLES().zutiMisc_DisableAIRadioChatter) Voice.speakGoAround((Aircraft) this.FM.actor);
                                this.FM.CT.FlapsControl = 0.4F;
                                this.FM.CT.GearControl = 0.0F;
                                return;
                            }
                            if (i == 2) {
                                if (Main.cur().mission != null) {
                                    /* empty */
                                }
                                // TODO: Altered by |ZUTI|: fixed variables references
                                if (!Mission.isDogfight() || !Mission.MDS_VARIABLES().zutiMisc_DisableAIRadioChatter) Voice.speakWaveOff((Aircraft) this.FM.actor);
                                if (this.FM.isReadyToReturn()) {
                                    if (Main.cur().mission != null) {
                                        /* empty */
                                    }
                                    // TODO: Altered by |ZUTI|: fixed variables references
                                    if (!Mission.isDogfight() || !Mission.MDS_VARIABLES().zutiMisc_DisableAIRadioChatter) Voice.speakGoingIn((Aircraft) this.FM.actor);
                                    this.FM.AS.setCockpitDoor(this.FM.actor, 1);
                                    this.FM.CT.GearControl = 1.0F;
                                } else {
                                    this.way.first();
                                    this.FM.push(2);
                                    this.FM.push(2);
                                    this.FM.push(2);
                                    this.FM.push(2);
                                    this.FM.pop();
                                    this.FM.CT.FlapsControl = 0.4F;
                                    this.FM.CT.GearControl = 0.0F;
                                    Aircraft.debugprintln(this.FM.actor, "Going around!.");
                                    return;
                                }
                                return;
                            }
                            this.FM.CT.GearControl = 1.0F;
                        }
                    }
                }
                if (this.way.isLanding() && this.way.Cur() < 6 && this.way.getCurDist() < 800.0) this.way.next();
                if ((this.way.Cur() == this.way.size() - 1 && this.getWayPointDistance() < 2000.0F && this.way.curr().getTarget() == null && this.FM.M.fuel < 0.2F * this.FM.M.maxFuel || this.way.curr().Action == 2) && !this.way.isLanding()) {
                    Airport airport = Airport.makeLandWay(this.FM);
                    if (airport != null) {
                        this.WWPoint = null;
                        this.way.first();
                        this.update(f);
                        return;
                    }
                    this.FM.set_task(3);
                    this.FM.set_maneuver(49);
                    this.FM.setBusy(true);
                }
                if (World.cur().diffCur.Wind_N_Turbulence) {
                    World.cur();
                    if (!World.wind().noWind && this.FM.Skill > 0) {
                        World.cur();
                        World.wind().getVectorAI(this.WPoint, this.windV);
                        this.windV.scale(-1.0);
                        if (this.FM.Skill == 1) this.windV.scale(0.75);
                        this.courseV.set(this.WPoint.x - PlLoc.x, this.WPoint.y - PlLoc.y, 0.0);
                        this.courseV.normalize();
                        this.courseV.scale(this.FM.getSpeed());
                        this.courseV.add(this.windV);
                        this.StabDirection = -FMMath.RAD2DEG((float) Math.atan2(this.courseV.y, this.courseV.x));
                        break;
                    }
                }
                this.StabDirection = -FMMath.RAD2DEG((float) Math.atan2(this.WPoint.y - PlLoc.y, this.WPoint.x - PlLoc.x));
            }
        } while (false);
        if (this.bStabSpeed || this.bWayPoint) {
            this.Pw = 0.3F - 0.04F * (this.FM.getSpeed() - (float) this.StabSpeed);
            if (this.Pw > 1.0F) this.Pw = 1.0F;
            else if (this.Pw < 0.0F) this.Pw = 0.0F;
        }
        if (this.bStabAltitude || this.bWayPoint) {
            this.Ev = this.FM.CT.ElevatorControl;
            double d = this.SA - this.FM.getAltitude();
            double d_1_ = 0.0;
            double d_2_ = 0.0;
            float f_3_ = 0F;
            float f_4_ = 0F;
            
            // TODO: +++ Fighting AI climbing above Waypoint altitude
            if (this.FM.actor != null && aEARAFFP == 0F) {
                aEARAF = aEARAFFP = Property.floatValue(this.FM.actor.getClass(), "AutopilotElevatorAboveReferenceAltitudeFactor", 3.3E-4F);
            }
            if (aEARAFFP != 0F && d > 200D && this.FM.getVertSpeed() < 0F && aEARAF != aEARAFFP) {
                aEARAF = aEARAFFP;
//                if (this.FM.actor == World.getPlayerAircraft()) System.out.println("(0) AEARAF reset to " + aEARAF);
            }
            // ------------------------------------------------------

            if (d > -50.0) {
                f_3_ = 5.0F + 2.5E-4F * this.FM.getAltitude();
                f_3_ += 0.02 * (250.0 - this.FM.Vmax);
                if (f_3_ > 14.0F) f_3_ = 14.0F;
                d_1_ = Math.min(this.FM.getAOA() - f_3_, this.FM.Or.getTangage() - 1.0F) * 1.0F * f + 0.5F * this.FM.getForwAccel();
            }
            if (d < 50.0) {
                f_4_ = -15.0F + this.FM.M.mass * aEARAF;
                if (f_4_ < -4.0F) f_4_ = -4.0F;
                d_2_ = (this.FM.Or.getTangage() - f_4_) * 0.8F * f;
            }
            double d_5_ = 0.01 * (d + 50.0);
            if (d_5_ > 1.0) d_5_ = 1.0;
            if (d_5_ < 0.0) d_5_ = 0.0;
            this.Ev -= d_5_ * d_1_ + (1.0 - d_5_) * d_2_;
            this.Ev += 1.0 * this.FM.getW().y + 0.5 * this.FM.getAW().y;
            if (this.FM.getSpeed() < 1.3F * this.FM.VminFLAPS) this.Ev -= 0.0040F * f;
            float f_6_ = 9.0F * this.FM.getSpeed() / this.FM.VminFLAPS;
            if (this.FM.VminFLAPS < 28.0F) f_6_ = 10.0F;
            if (f_6_ > 25.0F) f_6_ = 25.0F;
            float f_7_ = (f_6_ - this.FM.Or.getTangage()) * 0.1F;
            float f_8_ = -15.0F + this.FM.M.mass * aEARAF;

            if (f_8_ < -4.0F) f_8_ = -4.0F;
            float f_9_ = (f_8_ - this.FM.Or.getTangage()) * 0.2F;
            if (this.Ev > f_7_) this.Ev = f_7_;
            if (this.Ev < f_9_) this.Ev = f_9_;
            this.FM.CT.ElevatorControl = 0.8F * this.FM.CT.ElevatorControl + 0.2F * this.Ev;
            
            // TODO: +++ Fighting AI climbing above Waypoint altitude
            if (d < -20D && this.FM.getVertSpeed() > 0F && this.Ev > 0F && aEARAF > 1E-5F) {
                aEARAF *= CommonTools.smoothCvt((float)d, -100F, -20F, 0.999F, 0.9999F);
//                if (this.FM.actor == World.getPlayerAircraft()) System.out.println("(-) AEARAF adjusted to " + aEARAF);
            } else if (d > 20D && this.FM.getVertSpeed() < 0F && this.Ev < 0F && aEARAF < 3.3E-4F) {
                aEARAF /= CommonTools.smoothCvt((float)d, 20F, 100F, 0.9999F, 0.9995F);
//                if (this.FM.actor == World.getPlayerAircraft()) System.out.println("(+) AEARAF adjusted to " + aEARAF);
            }
            // ------------------------------------------------------

//            if (this.FM.actor == World.getPlayerAircraft()) {
//                DecimalFormat df = new DecimalFormat("0.00");
//                HUD.training("EC:" + df.format(this.FM.CT.ElevatorControl) + 
//                        " VS:" + df.format(this.FM.getVertSpeed()) + 
//                        " ALT:" + df.format(this.FM.getAltitude()) + 
//                        " SA:" + df.format(this.SA) +
//                        " EV:" + df.format(this.Ev) +
//                        " AF:" + df.format(aEARAF / 3.3E-4F) +
//                        " M:" + df.format(this.FM.M.getFullMass()));
//            }
        }
        float f_10_ = 0.0F;
        if (this.bStabDirection || this.bWayPoint) {
            f_10_ = this.FM.Or.getAzimut();
            float f_11_ = this.FM.Or.getKren();
            // TODO: +++ Backport from 4.13
            if(FM.Group.getAaaNum() > 3F && ((Aircraft)FM.actor).aircIndex() == 0)
                f_10_ -= StabDirection + (double)avoidance;
            else
            // ---
            f_10_ -= this.StabDirection;
            f_10_ = (f_10_ + 3600.0F) % 360.0F;
            f_11_ = (f_11_ + 3600.0F) % 360.0F;
            if (f_10_ > 180.0F) f_10_ -= 360.0F;
            if (f_11_ > 180.0F) f_11_ -= 360.0F;
            float f_12_ = ((this.FM.getSpeed() - this.FM.VminFLAPS) * 3.6F + this.FM.getVertSpeed() * 40.0F) * 0.25F;
            if (this.way.isLanding()) f_12_ = 65.0F;
            if (f_12_ < 15.0F) f_12_ = 15.0F;
            else if (f_12_ > 65.0F) f_12_ = 65.0F;
            if (f_10_ < -f_12_) f_10_ = -f_12_;
            else if (f_10_ > f_12_) f_10_ = f_12_;
            this.Ail = -0.01F * (f_10_ + f_11_ + 3.0F * (float) this.FM.getW().x + 0.5F * (float) this.FM.getAW().x);
            if (this.Ail > 1.0F) this.Ail = 1.0F;
            else if (this.Ail < -1.0F) this.Ail = -1.0F;
            this.WPoint.get(this.Ve);
            this.Ve.sub(this.FM.Loc);
            this.FM.Or.transformInv(this.Ve);
            if (Math.abs(this.Ve.y) < 25.0 && Math.abs(this.Ve.x) < 150.0) this.FM.CT.AileronControl = -0.01F * this.FM.Or.getKren();
            else this.FM.CT.AileronControl = this.Ail;
            this.FM.CT.ElevatorControl += Math.abs(f_11_) * 0.0040F * f;
            this.FM.CT.RudderControl -= this.FM.getAOS() * 0.04F * f;
        }
        if (this.bWayPoint && this.way.isLanding()) {
            if (World.Rnd().nextFloat() < 0.01F) this.FM.doDumpBombsPassively();
            if (this.way.Cur() > 5) this.FM.set_maneuver(25);
            this.FM.CT.RudderControl -= f_10_ * 0.04F * f;
            this.landUpdate(f);
        }
    }

    private void landUpdate(float f) {
        if (this.FM.getAltitude() - 10.0F + this.FM.getVertSpeed() * 5.0F - this.SA > 0.0F) {
            if (this.FM.Vwld.z > -10.0) this.FM.Vwld.z -= 1.0F * f;
        } else if (this.FM.Vwld.z < 10.0) this.FM.Vwld.z += 1.0F * f;
        if (this.FM.getAOA() > 11.0F && this.FM.CT.ElevatorControl > -0.3F) this.FM.CT.ElevatorControl -= 0.3F * f;
    }

    // TODO: Guided Missiles Update
    public void setOverrideMissileControl(Controls theControls, boolean overrideMissile) {
        this.theMissileControls = theControls;
        this.overrideMissileControl = overrideMissile;
    }

}