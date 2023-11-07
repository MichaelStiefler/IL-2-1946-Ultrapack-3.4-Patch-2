// 4.10.1 class - Here because of Lutz mod
package com.maddox.il2.fm;

import java.util.StringTokenizer;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.VisCheck;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.ground.ScareEnemies;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorFilter;
import com.maddox.il2.engine.ActorHMesh;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.ZutiMDSVariables;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.air.TypeDiveBomber;
import com.maddox.il2.objects.air.TypeFighter;
import com.maddox.il2.objects.air.TypeStormovik;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.TestRunway;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.SectFile;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.CommonTools;

public class FlightModel extends FlightModelMain {
    public FlightModel(String s) {
        super(s);
        this.turnOffCollisions = false;
        this.brakeShoe = false;
        this.brakeShoeLoc = new Loc();
        this.brakeShoeLastCarrier = null;
        this.canChangeBrakeShoe = false;
        this.dryFriction = 1.0F;
        this.timeToTakeOff = World.getTimeofDay();
        this.numberOfTrigger = 0;
        this.bstartmotor = true;
    }

    // TODO: +++ TD AI code backport from 4.13 +++

    static class ClipFilter implements ActorFilter {

        public boolean isUse(Actor actor, double d) {
            return actor != Engine.actorLand() && actor != this.target && actor != this.owner && !(actor instanceof TestRunway);
        }

        public void setTarget(Actor actor) {
            this.target = actor;
        }

        public void setOwner(Actor actor) {
            this.owner = actor;
        }

        Actor target;
        Actor owner;

        ClipFilter() {
            this.target = null;
            this.owner = null;
        }
    }

    public Polares getWing() {
        return this.Wing;
    }

    private native void nCreateSubSkills(int i);

    public native float nShootingPoint(int i);

    public native void nSmackMe(float f, float f1, float f2, int i, int j);

    public native void nShakeMe(int i, int j);

    public native void nLandAvoidance(float f, float f1, float f2, float f3, float f4, int i);

    public native void nFullPants(float f, int i);

    public native float nSD(int i, int j, int k, long l);

    public void shakeMe(float f, float f1, float f2, float f3, float f4, float f5) {
        this.producedAMM.x += f;
        this.producedAMM.y += f1;
        this.producedAMM.z += f2;
        this.producedAF.x += f3;
        this.producedAF.y += f4;
        this.producedAF.z += f5;
    }

    public void control(float f, float f1) {
        this.bankAngle = f;
        this.CT.AileronControl = f1;
    }

    public native float nDanCoeff(double d, float f, float f1);

    static ClipFilter       clipFilter = new ClipFilter();
    private static Vector3d Vg         = new Vector3d();
    // TODO: --- TD AI code backport from 4.13 ---

    public void setSkill(int newSkill) {
        this.setTrigger();
        if (newSkill < 0) newSkill = 0;
        if (newSkill > 3) newSkill = 3;
        // TODO: +++ UP3 Sniper Gunner Hotfix +++
        if (this.actor == World.getPlayerAircraft() || ((Aircraft) this.actor).isNetPlayer()) newSkill = 3;
        // TODO: --- UP3 Sniper Gunner Hotfix ---
        this.Skill = newSkill;
        // TODO: +++ TD AI code backport from 4.13 +++
        this.nCreateSubSkills(this.Skill);
        // TODO: --- TD AI code backport from 4.13 ---
        this.turretSkill = newSkill;
        for (int j = 0; j < this.turret.length; j++) {
            this.turret[j].gunnerSkill = this.setGunner();
            this.turret[j].gunnerSkill *= this.turretSkill;
            this.turret[j].igunnerSkill = Math.round(this.turret[j].gunnerSkill);
        }

        if (this.actor != World.getPlayerAircraft() && !((Aircraft) this.actor).isNetPlayer()) switch (newSkill) {
            // TODO: +++ UP3 Sniper Gunner Hotfix +++
            case TU_SKILL_ROOKIE:
                this.SensPitch *= 0.75F;
                this.SensRoll *= 0.5F;
                this.SensYaw *= 0.5F;

                break;
            case TU_SKILL_AVERAGE:
                this.SensRoll *= 0.7F;
                this.SensPitch *= 0.75F;
                this.SensYaw *= 0.7F;

                break;
            case TU_SKILL_VETERAN:
                this.SensRoll *= 0.88F;
                this.SensPitch *= 0.92F;
                this.SensYaw *= 0.9F;
                break;
            case TU_SKILL_ACE:
                this.SensPitch *= 1.1F;
                this.SensRoll *= 1.0F;
                this.SensYaw *= 1.0F;
                break;
            // TODO: --- UP3 Sniper Gunner Hotfix ---
        }
        else Aircraft.debugprintln(this.actor, "Skill adjustment rejected on the Player AI parameters..");
    }

    public void set(HierMesh hiermesh) {
        this.HM = hiermesh;
        this.am = (ActorHMesh) this.actor;
        int i;
        for (i = 1; i <= 9; i++)
            if (this.HM.chunkFindCheck("Turret" + i + "A_D0") < 0 || this.HM.chunkFindCheck("Turret" + i + "B_D0") < 0) break;

        i--;
        this.turret = new Turret[i];
        for (int j = 0; j < i; j++) {
            this.turret[j] = new Turret();
            this.turret[j].indexA = this.HM.chunkFind("Turret" + (j + 1) + "A_D0");
            this.turret[j].indexB = this.HM.chunkFind("Turret" + (j + 1) + "B_D0");
            tu[0] = tu[1] = tu[2] = 0.0F;
            this.HM.setCurChunk(this.turret[j].indexA);
            this.am.hierMesh().chunkSetAngles(tu);
            this.HM.setCurChunk(this.turret[j].indexB);
            this.am.hierMesh().chunkSetAngles(tu);
            this.am.getChunkLoc(this.turret[j].Lstart);
            // TODO: +++ TD AI code backport from 4.13 +++
            this.turret[j].setObservingDirection();
            // TODO: --- TD AI code backport from 4.13 ---
            // TODO: +++ Added by SAS~Storebror: Skip "Blocked by Plane" checks on remote turrets
            this.turret[j].initPlaneBlockingChecks(this.actor, j);
            // ---
        }

        this.Gears.set(hiermesh);
    }

    private void updateRotation(Turret theTurret, float tickLen) {
        tu[0] = theTurret.tuLim[0];
        tu[1] = theTurret.tuLim[1];
        // TODO: +++ UP3 Sniper Gunner Hotfix +++
        // float f1 = 10F * f;
        this.rotSpeed = (Math.abs(this.prev0 - tu[0]) + Math.abs(this.prev1 - tu[1])) * tickLen;
        this.prev0 = tu[0];
        this.prev1 = tu[1];
        float maxRotSpeed = 40F * tickLen;

        switch (this.Skill) {
            case TU_SKILL_ROOKIE:
                maxRotSpeed *= 0.8F;
                break;
            case TU_SKILL_AVERAGE:
            default:
                break;
            case TU_SKILL_VETERAN:
                maxRotSpeed *= 1.4F;
                break;
            case TU_SKILL_ACE:
                maxRotSpeed *= 2F;
                break;
        }
        // TODO: --- UP3 Sniper Gunner Hotfix ---
        float azimuth = tu[0] - theTurret.tu[0];
        if (azimuth < -maxRotSpeed) azimuth = -maxRotSpeed;
        else if (azimuth > maxRotSpeed) azimuth = maxRotSpeed;
        tu[0] = theTurret.tu[0] + azimuth;
        theTurret.tu[0] = tu[0];
        float elevation = tu[1] - theTurret.tu[1];
        if (elevation < -maxRotSpeed) elevation = -maxRotSpeed;
        else if (elevation > maxRotSpeed) elevation = maxRotSpeed;
        tu[1] = theTurret.tu[1] + elevation;
        theTurret.tu[1] = tu[1];
        if (azimuth == 0.0F && elevation == 0.0F) return;
        float fTemp = tu[0];
        tu[0] = 0.0F;
        this.HM.setCurChunk(theTurret.indexB);
        this.am.hierMesh().chunkSetAngles(tu);
        tu[1] = fTemp;
        this.HM.setCurChunk(theTurret.indexA);
        this.am.hierMesh().chunkSetAngles(tu);
        return;
    }

    private void updateTurret(Turret theTurret, int turretIndex, float tickLen) {
        if (!theTurret.bIsOperable) {
            this.CT.WeaponControl[turretIndex + 10] = false;
            return;
        }
        if (!theTurret.bIsAIControlled) {
            ((Aircraft) this.actor).setHumanControlledTurretAngels(theTurret, tu, this.HM, this.am);
            return;
        }
        if (theTurret.indexA == -1 || theTurret.indexB == -1) return;
        // TODO: Cheater Protection
        boolean fightingCheater = false;
        // ---

        this.am = (ActorHMesh) this.actor;
        float targetDistance = 0.0F;
        float gunnerSkill = (this.turretSkill + 1) * theTurret.health;
        if (this.W.lengthSquared() > 0.25D) gunnerSkill *= 1.0F - (float) Math.sqrt(this.W.length() - 0.5D);
        if (this.getOverload() > 1.0F) gunnerSkill *= Aircraft.cvt(this.getOverload(), 1.0F, 5F, 1.0F, 0.0F);
        else if (this.getOverload() < 0.0F) gunnerSkill *= Aircraft.cvt(this.getOverload(), -1.5F, 0.0F, 0.0F, 1.0F);
        if (theTurret.target != null && theTurret.target instanceof Aircraft && ((Aircraft) theTurret.target).FM.isTakenMortalDamage()) theTurret.target = null;
        if (theTurret.target == null) {
            if (theTurret.tMode != Turret.TU_MO_SLEEP) {
                theTurret.tMode = Turret.TU_MO_SLEEP;
                theTurret.timeNext = Time.current();
            }
        } else {
            // TODO: Cheater Protection
            if (AircraftState.isCheaterAdvancedAiTurrets(theTurret.target)) {
                fightingCheater = true;
                gunnerSkill = this.turretSkill + 1;
                if (this.CT.Weapons.length > 10 + turretIndex) {
                    BulletEmitter be[] = this.CT.Weapons[10 + turretIndex];
                    if (be != null) for (int gunindex = 0; gunindex < be.length; gunindex++)
                        if (be[gunindex] instanceof Gun) {
                            Gun gun = (Gun) be[gunindex];
                            if (!gun.haveBullets()) gun.loadBullets();
                        }
                }
            }
            // ---

            theTurret.target.pos.getAbs(Pt);
            theTurret.target.getSpeed(Vt);
            this.actor.getSpeed(Ve);
            Vt.sub(Ve);
            this.HM.setCurChunk(theTurret.indexA);
            this.am.getChunkLocAbs(Actor._tmpLoc);
            Ve.sub(Pt, Actor._tmpLoc.getPoint());
            targetDistance = (float) Ve.length();
            float f2 = (14F - 2.25F * gunnerSkill) * (float) Math.sin((Time.current() & 65535L) * 0.003F);
            Vt.scale((targetDistance + f2) * 0.001492537F);
            Ve.add(Vt);
            Vg.set(Ve);
            this.Or.transformInv(Ve);
            theTurret.Lstart.transformInv(Ve);
            Ve.y = -Ve.y;
            this.HM.setCurChunk(theTurret.indexB);
            theTurret.Lstart.get(Oo);
            Oo.setAT0(Ve);
            Oo.get(tu);
            this.Or.transformInv(Vt);
            theTurret.Lstart.transformInv(Vt);
            Vt.normalize();
            this.shoot = ((Aircraft) this.actor).turretAngles(turretIndex, tu);
        }

        // TODO: +++ UP3 Sniper Gunner Hotfix +++
        float randomWidth = 0.4F;
        float skillParam = 0.1F;
        float rotationDeviation = this.Skill == TU_SKILL_ACE ? 0F : this.rotSpeed * (6.0F - gunnerSkill);
        if ((this.actor == World.getPlayerAircraft() || ((Aircraft) this.actor).isNetPlayer()) && theTurret.target != null) {
            randomWidth = FlightModel.cvt(targetDistance, 1000F, 200F, 0.4F, 0.2F);
            skillParam = FlightModel.cvt(targetDistance, 1000F, 200F, 0.1F, 0.06F);
            rotationDeviation /= FlightModel.cvt(targetDistance, 1000F, 0F, 1F, 5F);
        }
        float skillDeviation = skillParam * gunnerSkill;
        // TODO: --- UP3 Sniper Gunner Hotfix ---
        
        switch (theTurret.tMode) {
            default:
                break;

            case Turret.TU_MO_SLEEP:
                theTurret.bIsShooting = false;
                float af[] = ((Aircraft) this.actor).getTurretRestOrient(turretIndex);
                theTurret.tuLim[0] = af[0];
                theTurret.tuLim[1] = af[1];
                if (Time.current() > theTurret.timeNext) {
                    theTurret.target = War.GetNearestEnemyAircraft(this.actor, 3619F, 9);
                    if (theTurret.target == null) {
                        theTurret.target = War.GetNearestEnemyAircraft(this.actor, 6822F, 9);
                        if (theTurret.target == null) theTurret.timeNext = Time.current() + World.Rnd().nextLong(1000L, 10000L);
                        else if (VisCheck.visCheckTurret(theTurret, (Aircraft) this.actor, (Aircraft) theTurret.target, true, theTurret.isCheckPlaneBlocking())) {
                            theTurret.tMode = Turret.TU_MO_TRACKING;
                            theTurret.timeNext = 0L;
                            if ((Aircraft) this.actor == World.getPlayerAircraft() && theTurret.health > 0.4F) Voice.speakEnemyDetected((Aircraft) this.actor, (Aircraft) theTurret.target);
                        } else theTurret.timeNext = Time.current() + World.Rnd().nextLong(100L, 3000L);
                    } else if (VisCheck.visCheckTurret(theTurret, (Aircraft) this.actor, (Aircraft) theTurret.target, true, theTurret.isCheckPlaneBlocking())) {
                        theTurret.tMode = Turret.TU_MO_TRACKING;
                        theTurret.timeNext = 0L;
                        if ((Aircraft) this.actor == World.getPlayerAircraft() && theTurret.health > 0.4F) Voice.speakEnemyDetected((Aircraft) this.actor, (Aircraft) theTurret.target);
                    } else theTurret.timeNext = Time.current() + World.Rnd().nextLong(100L, 3000L);
                }
                break;

            case Turret.TU_MO_TRACKING:
                theTurret.bIsShooting = false;
                theTurret.tuLim[0] = tu[0];
                theTurret.tuLim[1] = tu[1];
                if (!this.isTick(39, 16)) break;
                if (!this.shoot && targetDistance > 550F || World.Rnd().nextFloat() < 0.1F) {
                    theTurret.tMode = Turret.TU_MO_SLEEP;
                    theTurret.timeNext = Time.current();
                }
                if ((World.Rnd().nextInt() & 0xff) >= 32F * gunnerSkill && targetDistance >= 148F + 27F * (gunnerSkill - 1.0F)) break;
                if (targetDistance < 450F + 66.6F * (gunnerSkill - 1.0F)) {
                    if (gunnerSkill - 1.0F <= 0.0F) {
                        if (Vt.x < -0.96D) {
                            switch (World.Rnd().nextInt(1, 3)) {
                                case 1:
                                    theTurret.tMode = Turret.TU_MO_STOPPED;
                                    theTurret.timeNext = Time.current() + World.Rnd().nextLong(500L, 1200L);
                                    break;

                                case 2:
                                    theTurret.tuLim[0] += World.Rnd().nextFloat(-15F, 15F);
                                    theTurret.tuLim[1] += World.Rnd().nextFloat(-10F, 10F);
                                    // fall through

                                case 3:
                                    theTurret.tMode = Turret.TU_MO_FIRING_STOPPED;
                                    theTurret.timeNext = Time.current() + World.Rnd().nextLong(500L, 10000L);
                                    break;
                            }
                            break;
                        }
                        if (Vt.x < -0.33D) {
                            theTurret.tMode = Turret.TU_MO_FIRING_STOPPED;
                            theTurret.timeNext = Time.current() + World.Rnd().nextLong(1000L, 5000L);
                        }
                        break;
                    }
                    if (gunnerSkill - 1.0F <= 2.0F) {
                        if (Vt.x < -0.91D) {
                            if (World.Rnd().nextBoolean()) theTurret.tMode = Turret.TU_MO_FIRING_STOPPED;
                            else theTurret.tMode = Turret.TU_MO_FIRING_TRACKING;
                            theTurret.timeNext = Time.current() + World.Rnd().nextLong(500L, 2200L);
                            break;
                        }
                        if (World.Rnd().nextFloat() < 0.5F) theTurret.tMode = Turret.TU_MO_FIRING_TRACKING;
                        else theTurret.tMode = Turret.TU_MO_FIRING_STOPPED;
                        theTurret.timeNext = Time.current() + World.Rnd().nextLong(1500L, 7500L);
                    } else {
                        theTurret.tMode = Turret.TU_MO_FIRING_TRACKING;
                        theTurret.timeNext = Time.current() + World.Rnd().nextLong(500L, 7500L);
                    }
                    break;
                }
                if (targetDistance < 902F + 88F * (gunnerSkill - 1.0F)) {
                    theTurret.tMode = Turret.TU_MO_FIRING_STOPPED;
                    theTurret.timeNext = Time.current() + World.Rnd().nextLong(100L, 1000L);
                }
                break;

            case Turret.TU_MO_STOPPED:
                theTurret.bIsShooting = false;
                if (Time.current() > theTurret.timeNext) {
                    theTurret.tMode = Turret.TU_MO_SLEEP;
                    theTurret.timeNext = 0L;
                }
                break;

            case Turret.TU_MO_FIRING_STOPPED:
                theTurret.bIsShooting = true;
                // TODO: +++ UP3 Sniper Gunner Hotfix +++
//            theTurret.tuLim[0] = tu[0] * World.Rnd().nextFloat(0.76F + 0.06F * gunnerSkill, 1.24F - 0.06F * gunnerSkill) + World.Rnd().nextFloat((-10F + 2.5F * gunnerSkill) - this.rotSpeed * 5F, (10F - 2.5F * gunnerSkill) + this.rotSpeed * 5F);
//            theTurret.tuLim[1] = tu[1] * World.Rnd().nextFloat(0.76F + 0.06F * gunnerSkill, 1.24F - 0.06F * gunnerSkill) + World.Rnd().nextFloat((-10F + 2.5F * gunnerSkill) - this.rotSpeed * 5F, (10F - 2.5F * gunnerSkill) + this.rotSpeed * 5F);
                theTurret.tuLim[0] = tu[0] * World.Rnd().nextFloat_DomeInv(1.0F - randomWidth + skillDeviation, 1.0F + randomWidth - skillDeviation)
                        + World.Rnd().nextFloat(-this.tAcc2 + this.tAcc3 * gunnerSkill - rotationDeviation, this.tAcc2 - this.tAcc3 * gunnerSkill + rotationDeviation);
                theTurret.tuLim[1] = tu[1] * World.Rnd().nextFloat_DomeInv(1.0F - randomWidth + skillDeviation, 1.0F + randomWidth - skillDeviation)
                        + World.Rnd().nextFloat(-this.tAcc2 + this.tAcc3 * gunnerSkill - rotationDeviation, this.tAcc2 - this.tAcc3 * gunnerSkill + rotationDeviation);
                // TODO: --- UP3 Sniper Gunner Hotfix ---
                // TODO: Cheater Protection
                if (fightingCheater) {
                    theTurret.tuLim[0] = tu[0] * World.Rnd().nextFloat_Dome(0.99F, 1.01F) + World.Rnd().nextFloat_Dome(-0.1F, 0.1F);
                    theTurret.tuLim[1] = tu[1] * World.Rnd().nextFloat_Dome(0.99F, 1.01F) + World.Rnd().nextFloat_Dome(-0.1F, 0.1F);
                }
                // ---
                if (Time.current() > theTurret.timeNext) theTurret.tMode = Turret.TU_MO_TRACKING;
                break;

            case Turret.TU_MO_FIRING_TRACKING:
                theTurret.bIsShooting = true;
                // TODO: +++ UP3 Sniper Gunner Hotfix +++
//            theTurret.tuLim[0] = tu[0] * World.Rnd().nextFloat(0.76F + 0.06F * gunnerSkill, 1.24F - 0.06F * gunnerSkill) + World.Rnd().nextFloat((-10F + 2.5F * gunnerSkill) - this.rotSpeed * 4F, (10F - 2.5F * gunnerSkill) + this.rotSpeed * 4F);
//            theTurret.tuLim[1] = tu[1] * World.Rnd().nextFloat(0.76F + 0.06F * gunnerSkill, 1.24F - 0.06F * gunnerSkill) + World.Rnd().nextFloat((-10F + 2.5F * gunnerSkill) - this.rotSpeed * 4F, (10F - 2.5F * gunnerSkill) + this.rotSpeed * 4F);
                theTurret.tuLim[0] = tu[0] * World.Rnd().nextFloat_DomeInv(1.0F - randomWidth + skillDeviation, 1.0F + randomWidth - skillDeviation) + World.Rnd().nextFloat(-this.tAcc2 + this.tAcc3 * gunnerSkill, this.tAcc2 - this.tAcc3 * gunnerSkill);
                theTurret.tuLim[1] = tu[1] * World.Rnd().nextFloat_DomeInv(1.0F - randomWidth + skillDeviation, 1.0F + randomWidth - skillDeviation)
                        + World.Rnd().nextFloat(-this.tAcc2 + this.tAcc3 * gunnerSkill - rotationDeviation, this.tAcc2 - this.tAcc3 * gunnerSkill + rotationDeviation);
                // TODO: --- UP3 Sniper Gunner Hotfix ---
                // TODO: Cheater Protection
                if (fightingCheater) {
                    theTurret.tuLim[0] = tu[0] * World.Rnd().nextFloat_Dome(0.99F, 1.01F) + World.Rnd().nextFloat_Dome(-0.1F, 0.1F);
                    theTurret.tuLim[1] = tu[1] * World.Rnd().nextFloat_Dome(0.99F, 1.01F) + World.Rnd().nextFloat_Dome(-0.1F, 0.1F);
                }
                // ---
                if (Time.current() <= theTurret.timeNext) break;
                theTurret.tMode = Turret.TU_MO_TRACKING;
                if (gunnerSkill - 1.0F <= 1.0F) {
                    theTurret.tMode = Turret.TU_MO_SLEEP;
                    theTurret.timeNext = Time.current() + World.Rnd().nextLong(100L, (long) (gunnerSkill * 700F));
                }
                break;

            case Turret.TU_MO_PANIC:
                theTurret.bIsShooting = true;
                this.shoot = true;
                ((Aircraft) this.actor).turretAngles(turretIndex, theTurret.tuLim);
                if (this.isTick(20, 0)) {
                    theTurret.tuLim[0] += World.Rnd().nextFloat(-50F, 50F);
                    theTurret.tuLim[1] += World.Rnd().nextFloat(-50F, 50F);
                }
                if (Time.current() > theTurret.timeNext) {
                    theTurret.tMode = Turret.TU_MO_STOPPED;
                    theTurret.timeNext = Time.current() + World.Rnd().nextLong(100L, 1500L);
                }
                break;
        }
        this.shoot &= theTurret.bIsShooting;
        if (this.shoot && this.isTick(32, 0)) {
            // TODO: Cheater Protection
            if (!fightingCheater)
                // ---
                this.shoot = VisCheck.visCheckTurret(theTurret, (Aircraft) this.actor, (Aircraft) theTurret.target, false, theTurret.isCheckPlaneBlocking());

            if (!this.shoot) theTurret.tMode = Turret.TU_MO_SLEEP;
        }
        if (this.shoot) {
            this.HM.setCurChunk(theTurret.indexB);
            this.am.getChunkLocAbs(Actor._tmpLoc);
            Vg.normalize();
            Vg.scale(2D);
            p.set(Actor._tmpLoc.getPoint());
            p.add(Vg);
            Vg.normalize();
            Vg.scale(targetDistance + 300F);
            p2.set(Actor._tmpLoc.getPoint());
            p2.add(Vg);
            clipFilter.setTarget(theTurret.target);
            clipFilter.setOwner(this.actor);
            Actor actor = Engine.collideEnv().getLine(p, p2, true, clipFilter, null);
            if (actor != null && actor.getArmy() == this.actor.getArmy() && actor != this.actor && actor instanceof Aircraft) this.shoot = false;
            else {
                Vt.set(1.0D, 0.0D, 0.0D);
                Actor._tmpLoc.getOrient().transform(Vt);
                double d = Vg.angle(Vt);
                if (d > 0.1D) this.shoot = false;
            }
        }
        if (gunnerSkill <= 0.0F) this.shoot = false;
        this.CT.WeaponControl[turretIndex + 10] = this.shoot;
        this.updateRotation(theTurret, tickLen);
    }

    public void hit(int i) {
        if (!Actor.isValid(this.actor)) return;
        if (this.actor.isNetMirror()) return;
        super.hit(i);
    }

    public float getSpeed() {
        if (!Actor.isValid(this.actor)) return 0.0F;
        if (this.actor.isNetMirror()) return (float) this.Vwld.length();
        else return super.getSpeed();
    }

    public void update(float tickLen) {
        if (!this.bstartmotor) this.EI.setEngineStops();
        if (this.actor.isNetMirror()) ((NetAircraft.Mirror) this.actor.net).fmUpdate(tickLen);
        else this.FMupdate(tickLen);
    }

    public final void FMupdate(float tickLen) {
        if (this.turret != null) {
            int i = this.turret.length;
            for (int j = 0; j < i; j++)
                this.updateTurret(this.turret[j], j, tickLen);

        }
        super.update(tickLen);
    }

    protected void putScareShpere() {
        v1.set(1.0D, 0.0D, 0.0D);
        this.Or.transform(v1);
        v1.scale(2000D);
        p2.set(this.Loc);
        p2.add(v1);
        Engine.land();
        if (Landscape.rayHitHQ(this.Loc, p2, p)) {
            float f = (float) (this.getAltitude() - Engine.land().HQ_Air(this.Loc.x, this.Loc.y));
            if (this.actor instanceof TypeDiveBomber && f > 780F && this.Or.getTangage() < -70F) {
                ScareEnemies.set(16);
                Engine.collideEnv().getNearestEnemies(p, 75D, this.actor.getArmy(), ScareEnemies.enemies());
            }
            if (this.actor instanceof TypeStormovik) {
                if (f < 600F && this.Or.getTangage() < -15F) {
                    ScareEnemies.set(2);
                    Engine.collideEnv().getNearestEnemies(p, 45D, this.actor.getArmy(), ScareEnemies.enemies());
                }
            } else if (this.actor instanceof TypeFighter && f < 500F && this.Or.getTangage() < -15F) {
                ScareEnemies.set(2);
                Engine.collideEnv().getNearestEnemies(p, 45D, this.actor.getArmy(), ScareEnemies.enemies());
            }
        }
    }

    public void moveCarrier() {
        if (this.AP.way.isLandingOnShip()) {
            if (this.AP.way.landingAirport == null) {
                int i = this.AP.way.Cur();
                this.AP.way.last();
                if (this.AP.way.curr().Action == 2) {
                    Actor actor = this.AP.way.curr().getTarget();
                    if (actor != null && actor instanceof BigshipGeneric) this.AP.way.landingAirport = ((BigshipGeneric) actor).getAirport();
                }
                this.AP.way.setCur(i);
            }
            if (Actor.isAlive(this.AP.way.landingAirport) && !this.AP.way.isLanding()) this.AP.way.landingAirport.rebuildLastPoint(this);
        }
    }

    private void setTrigger() {
        this.timeToTakeOff = World.getTimeofDay();
        SectFile sectfile = Mission.cur().sectFile();
        String s = "AOC_Trigger";
        String s1 = this.actor.name();
        int i = s1.length();
        s1 = s1.substring(0, i - 1);
        String s2 = sectfile.get(s, s1);
        if (s2 != null) {
            s2 = s2.toLowerCase();
            this.initTrigger(s1, s2);
        }
    }

    private void initTrigger(String s, String s1) {
        ZutiMDSVariables variables = Mission.MDS_VARIABLES();

        StringTokenizer stringtokenizer = new StringTokenizer(s1, " ");
        this.numberOfTrigger = stringtokenizer.countTokens();
        this.typeOfTrigger = new int[this.numberOfTrigger];
        this.valueOfTrigger = new String[this.numberOfTrigger];
        for (int i = 0; i < this.numberOfTrigger; i++) {
            String s2 = stringtokenizer.nextToken();
            String s3 = s2.substring(0, 5);
            if (s3.equals("delay")) {
                this.typeOfTrigger[i] = -1;
                this.valueOfTrigger[i] = "delay";
                s3 = s2.substring(6);
                this.timeToTakeOff = World.getTimeofDay() + Float.parseFloat(s3) / 60F;
            }
            if (s3.equalsIgnoreCase("clone")) {
                this.typeOfTrigger[i] = -2;
                s3 = s2.substring(6);
                this.valueOfTrigger[i] = s3;
            }
            s3 = s2.substring(0, 4);
            if (s3.equals("zone")) {
                int k = s2.indexOf(':');
                String s4 = s2.substring(4, k);
                int l = Integer.parseInt(s4);
                World.cur();
                if (l < variables.getZone()) {
                    String s5 = s2.substring(k + 1);
                    this.typeOfTrigger[i] = l;
                    this.valueOfTrigger[i] = s5;
                } else this.numberOfTrigger--;
            }
        }

        if (this.numberOfTrigger > 0) this.bstartmotor = false;
    }

    private float setGunner() {
        SectFile sectfile = Mission.cur().sectFile();
        String s = "AOC_Gunner";
        String s1 = this.actor.name();
        int i = s1.length();
        s1 = s1.substring(0, i - 1);
        String s2 = sectfile.get(s, s1);
        if (s2 == null) {
            s2 = sectfile.get(s, this.actor.name());
            if (s2 == null) return 1.0F;
        }
        StringTokenizer stringtokenizer = new StringTokenizer(s2, " ");
        float af[] = new float[2];
        int j;
        for (j = 0; stringtokenizer.hasMoreElements(); j++)
            af[j] = Float.parseFloat(stringtokenizer.nextToken());

        if (j != 1) {
            float f = af[1] * (2.0F * (float) Math.random() - 1.0F);
            af[0] += f;
        }
        if (af[0] < 0.0F) af[0] = 0.0F;
        if (af[0] > 1.0F) af[0] = 1.0F;
        return af[0];
    }

    protected final static float cvt(float inputValue, float inMin, float inMax, float outMin, float outMax) {
        return CommonTools.cvt(inputValue, inMin, inMax, outMin, outMax);
//        inputValue = Math.min(Math.max(inputValue, inMin), inMax);
//        return outMin + (outMax - outMin) * (inputValue - inMin) / (inMax - inMin);
    }

    public Turret           turret[];
    protected HierMesh      HM;
    ActorHMesh              am;
    private boolean         shoot;
    public boolean          turnOffCollisions;
    public boolean          brakeShoe;
    public Loc              brakeShoeLoc;
    public Actor            brakeShoeLastCarrier;
    public boolean          canChangeBrakeShoe;
    public static final int _FIRST_TURRET = 10;
    private static Point3d  Pt            = new Point3d();
    private static Vector3d Ve            = new Vector3d();
    private static Vector3d Vt            = new Vector3d();
    private static Orient   Oo            = new Orient();
    private static float    tu[]          = new float[3];
    public float            dryFriction;
    static Point3d          p             = new Point3d();
    static Vector3d         v1            = new Vector3d();
    static Point3d          p2            = new Point3d();
    public float            timeToTakeOff;
    public int              numberOfTrigger;
    public int              typeOfTrigger[];
    public String           valueOfTrigger[];
    public boolean          bstartmotor;

    private final float     tAcc2         = 8F;
    private final float     tAcc3         = 2.666667F;

    // TODO: +++ UP3 Sniper Gunner Hotfix +++
    public static final int TU_SKILL_ROOKIE  = 0;
    public static final int TU_SKILL_AVERAGE = 1;
    public static final int TU_SKILL_VETERAN = 2;
    public static final int TU_SKILL_ACE     = 3;
    private float           rotSpeed         = 0.0F;
    private float           prev0            = 0.0F;
    private float           prev1            = 0.0F;
    // TODO: --- UP3 Sniper Gunner Hotfix ---

    // TODO: +++ TD AI code backport from 4.13 +++
    public int   gunnery;
    public int   sight;
    public int   courage;
    public int   flying;
    public int   subSkill;
    public float convAI;
    public float wanderRate;
    public float shootingPoint;
    public float bankAngle;
    // TODO: --- TD AI code backport from 4.13 ---

}
