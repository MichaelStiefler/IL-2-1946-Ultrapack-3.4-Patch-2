package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Explosion;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.Wing;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;

public abstract class ME_155xyz extends Scheme1
    implements TypeFighter, TypeBNZFighter
{

    public ME_155xyz()
    {
        trimElevator = 0.0F;
        bHasElevatorControl = true;
        kangle = 0.0F;
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("mg151L", !thisWeaponsName.equals("none"));
        hierMesh.chunkVisible("mg151R", !thisWeaponsName.equals("none"));
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        ME_155xyz.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }
    
    protected void moveFlap(float f)
    {
        float f1 = -45F * f;
        hierMesh().chunkSetAngles("Flap01_D0", 0.0F, f1, 0.0F);
        hierMesh().chunkSetAngles("Flap02_D0", 0.0F, f1, 0.0F);
    }

    protected void moveRudder(float f)
    {
        hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -30F * f, 0.0F);
        hierMesh().chunkSetAngles("Flettner1_D0", 0.0F, -45F * f, 0.0F);
    }

    protected void moveElevator(float f)
    {
        f -= trimElevator;
        hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -30F * f, 0.0F);
        hierMesh().chunkSetAngles("VatorR_D0", 0.0F, -30F * f, 0.0F);
    }

    protected void moveAileron(float f)
    {
        if(f > 0.0F)
        {
            hierMesh().chunkSetAngles("AroneL_D0", 0.0F, -25F * f, 0.0F);
            hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -25F * f, 0.0F);
        } else
        {
            hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -25F * f, 0.0F);
            hierMesh().chunkSetAngles("AroneL_D0", 0.0F, -25F * f, 0.0F);
        }
    }

    public void update(float f)
    {
        if(this.FM.getSpeed() > 5F)
        {
            hierMesh().chunkSetAngles("SlatL_D0", 0.0F, Aircraft.cvt(this.FM.getAOA(), 6.8F, 11F, 0.0F, 1.5F), 0.0F);
            hierMesh().chunkSetAngles("SlatR_D0", 0.0F, Aircraft.cvt(this.FM.getAOA(), 6.8F, 11F, 0.0F, 1.5F), 0.0F);
        }
        hierMesh().chunkSetAngles("Flap01L_D0", 0.0F, -16F * kangle, 0.0F);
        hierMesh().chunkSetAngles("Flap01U_D0", 0.0F, 16F * kangle, 0.0F);
        hierMesh().chunkSetAngles("Flap02L_D0", 0.0F, -16F * kangle, 0.0F);
        hierMesh().chunkSetAngles("Flap02U_D0", 0.0F, 16F * kangle, 0.0F);
        kangle = 0.95F * kangle + 0.05F * this.FM.EI.engines[0].getControlRadiator();
        if(kangle > 1.0F)
            kangle = 1.0F;
        if(!getOp(31) || !getOp(32))
            this.FM.CT.trimAileron = ((this.FM.CT.ElevatorControl * (s32 - s31) + this.FM.CT.trimElevator * (s18 - s17)) * this.FM.SensPitch) / 3F;
        if(!bHasElevatorControl)
            this.FM.CT.ElevatorControl = 0.0F;
        if(trimElevator != this.FM.CT.trimElevator)
        {
            trimElevator = this.FM.CT.trimElevator;
            hierMesh().chunkSetAngles("StabL_D0", 0.0F, 0.0F, -16F * trimElevator);
            hierMesh().chunkSetAngles("StabR_D0", 0.0F, 0.0F, -16F * trimElevator);
        }
        super.update(f);
    }

    protected void nextDMGLevel(String s, int i, Actor actor)
    {
        super.nextDMGLevel(s, i, actor);
        if(this.FM.isPlayers())
            bChangedPit = true;
    }

    protected void nextCUTLevel(String s, int i, Actor actor)
    {
        super.nextCUTLevel(s, i, actor);
        if(this.FM.isPlayers())
            bChangedPit = true;
    }

    public static void moveGear(HierMesh hiermesh, float f)
    {
        float f1 = 0.8F;
        float f2 = -0.5F * (float)Math.cos((double)(f / f1) * Math.PI) + 0.5F;
        if(f <= f1 || f == 1.0F)
        {
            hiermesh.chunkSetAngles("GearL3_D0", 0.0F, 75.5F * f2, 0.0F);
            hiermesh.chunkSetAngles("GearL2_D0", 20.5F * f2, 0.0F, 0.0F);
        }
        f2 = -0.5F * (float)Math.cos((double)((f - (1.0F - f1)) / f1) * Math.PI) + 0.5F;
        if(f >= 1.0F - f1)
        {
            hiermesh.chunkSetAngles("GearR3_D0", 0.0F, -75.5F * f2, 0.0F);
            hiermesh.chunkSetAngles("GearR2_D0", -20.5F * f2, 0.0F, 0.0F);
        }
        f2 = Math.max(-f * 1500F, -90F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, f2, 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, -f2, 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 45F * f, 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearL99_D0", -9F * f, 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearR99_D0", 9F * f, 0.0F, 0.0F);
        if(f > 0.99F)
        {
            hiermesh.chunkSetAngles("GearL3_D0", 0.0F, 75.5F, 0.0F);
            hiermesh.chunkSetAngles("GearL2_D0", 20.5F, 0.0F, 0.0F);
            hiermesh.chunkSetAngles("GearR3_D0", 0.0F, -75.5F, 0.0F);
            hiermesh.chunkSetAngles("GearR2_D0", -20.5F, 0.0F, 0.0F);
        }
        if(f < 0.01F)
        {
            hiermesh.chunkSetAngles("GearL3_D0", 0.0F, 0.0F, 0.0F);
            hiermesh.chunkSetAngles("GearL2_D0", 0.0F, 0.0F, 0.0F);
            hiermesh.chunkSetAngles("GearR3_D0", 0.0F, 0.0F, 0.0F);
            hiermesh.chunkSetAngles("GearR2_D0", 0.0F, 0.0F, 0.0F);
        }
    }

    protected void moveGear(float f)
    {
        float f1 = 0.9F - (float)((Wing)getOwner()).aircIndex(this) * 0.1F;
        float f2 = -0.5F * (float)Math.cos((double)(f / f1) * Math.PI) + 0.5F;
        if(f <= f1 || f == 1.0F)
        {
            hierMesh().chunkSetAngles("GearL3_D0", 0.0F, 75.5F * f2, 0.0F);
            hierMesh().chunkSetAngles("GearL2_D0", 20.5F * f2, 0.0F, 0.0F);
        }
        f2 = -0.5F * (float)Math.cos((double)((f - (1.0F - f1)) / f1) * Math.PI) + 0.5F;
        if(f >= 1.0F - f1)
        {
            hierMesh().chunkSetAngles("GearR3_D0", 0.0F, -75.5F * f2, 0.0F);
            hierMesh().chunkSetAngles("GearR2_D0", -20.5F * f2, 0.0F, 0.0F);
        }
        f2 = Math.max(-f * 1500F, -90F);
        hierMesh().chunkSetAngles("GearL4_D0", 0.0F, f2, 0.0F);
        hierMesh().chunkSetAngles("GearR4_D0", 0.0F, -f2, 0.0F);
        hierMesh().chunkSetAngles("GearC2_D0", 45F * f, 0.0F, 0.0F);
        hierMesh().chunkSetAngles("GearL99_D0", -9F * f, 0.0F, 0.0F);
        hierMesh().chunkSetAngles("GearR99_D0", 9F * f, 0.0F, 0.0F);
        if(f > 0.99F)
        {
            hierMesh().chunkSetAngles("GearL3_D0", 0.0F, 75.5F, 0.0F);
            hierMesh().chunkSetAngles("GearL2_D0", 20.5F, 0.0F, 0.0F);
            hierMesh().chunkSetAngles("GearR3_D0", 0.0F, -75.5F, 0.0F);
            hierMesh().chunkSetAngles("GearR2_D0", -20.5F, 0.0F, 0.0F);
        }
    }

    public void moveSteering(float f)
    {
        if(this.FM.CT.getGear() < 0.98F)
        {
            return;
        } else
        {
            hierMesh().chunkSetAngles("GearC2_D0", 90F, -f, 0.0F);
            return;
        }
    }

    public void moveCockpitDoor(float f)
    {
        resetYPRmodifier();
        hierMesh().chunkSetAngles("Blister1_D0", 0.0F, 100F * f, 0.0F);
        if(Config.isUSE_RENDER())
        {
            if(Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null)
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            setDoorSnd(f);
        }
    }

    public void doKillPilot(int i)
    {
    }

    public void doMurderPilot(int i)
    {
        switch(i)
        {
        case 0:
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            hierMesh().chunkVisible("HMask1_D0", false);
            if(!this.FM.AS.bIsAboutToBailout)
            {
                if(hierMesh().isChunkVisible("Blister1_D0"))
                    hierMesh().chunkVisible("Gore1_D0", true);
                hierMesh().chunkVisible("Gore2_D0", true);
            }
            break;
        }
    }

    public boolean cut(String s)
    {
        if(s.startsWith("Tail1"))
            this.FM.AS.hitTank(this, 2, 100);
        return super.cut(s);
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(flag && this.FM.AS.astateTankStates[0] > 5)
            this.FM.AS.repairTank(0);
        if(this.FM.getAltitude() < 3000F)
            hierMesh().chunkVisible("HMask1_D0", false);
        else
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Head1_D0"));
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xx"))
        {
            if(s.startsWith("xxarmor"))
            {
                Aircraft.debugprintln(this, "*** Armor: Hit..");
                if(s.endsWith("p1"))
                {
                    getEnergyPastArmor(World.Rnd().nextFloat(20F, 30F), shot);
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
                    Aircraft.debugprintln(this, "*** Armor Glass: Hit..");
                    if(shot.power <= 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** Armor Glass: Bullet Stopped..");
                        if(World.Rnd().nextFloat() < 0.5F)
                            doRicochetBack(shot);
                    }
                } else
                if(s.endsWith("p2"))
                    getEnergyPastArmor(0.5F, shot);
                else
                if(s.endsWith("p3"))
                {
                    if(point3d.z < -0.27D)
                        getEnergyPastArmor(4.1D / (Math.abs(Aircraft.v1.z) + 0.00001D), shot);
                    else
                        getEnergyPastArmor(8.1D / (Math.abs(Aircraft.v1.x) + 0.00001D), shot);
                } else
                if(s.endsWith("p4"))
                    getEnergyPastArmor(9D / (Math.abs(Aircraft.v1.z) + 0.00001D), shot);
                else
                if(s.endsWith("p5"))
                    getEnergyPastArmor(9D / (Math.abs(Aircraft.v1.x) + 0.00001D), shot);
                else
                if(s.endsWith("p6"))
                    getEnergyPastArmor(9D / (Math.abs(Aircraft.v1.x) + 0.00001D), shot);
                else
                if(s.endsWith("a1"))
                {
                    if(World.Rnd().nextFloat() < 0.5F)
                        shot.powerType = 0;
                    getEnergyPastArmor(World.Rnd().nextFloat(1.0F, 3F), shot);
                }
            } else
            {
                if(s.startsWith("xxcontrols"))
                {
                    int i = s.charAt(10) - 48;
                    switch(i)
                    {
                    default:
                        break;

                    case 1:
                    case 4:
                        if(getEnergyPastArmor(0.175F, shot) > 0.0F)
                        {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                            Aircraft.debugprintln(this, "*** Aileron Controls: Control Crank Destroyed..");
                        }
                        break;

                    case 2:
                    case 3:
                        if(getEnergyPastArmor(0.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.1F)
                        {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                            Aircraft.debugprintln(this, "*** Aileron Controls: Disabled..");
                        }
                        break;

                    case 5:
                    case 6:
                        if(getEnergyPastArmor(0.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.1F)
                        {
                            bHasElevatorControl = false;
                            if(!this.FM.CT.bHasElevatorTrim)
                                this.FM.AS.setControlsDamage(shot.initiator, 1);
                            Aircraft.debugprintln(this, "*** Elevator Controls: Disabled / Strings Broken..");
                        }
                        if(getEnergyPastArmor(0.008F, shot) <= 0.0F || World.Rnd().nextFloat() >= 0.1F)
                            break;
                        this.FM.CT.bHasElevatorTrim = false;
                        if(!bHasElevatorControl)
                            this.FM.AS.setControlsDamage(shot.initiator, 1);
                        Aircraft.debugprintln(this, "*** Trimmer Controls: Disabled / Strings Broken..");
                        break;

                    case 7:
                        if(getEnergyPastArmor(2.25F, shot) > 0.0F && World.Rnd().nextFloat() < 0.2F)
                        {
                            this.FM.AS.setControlsDamage(shot.initiator, 2);
                            Aircraft.debugprintln(this, "*** Rudder Controls: Disabled..");
                        }
                        break;

                    case 8:
                        if(getEnergyPastArmor(4F, shot) > 0.0F)
                        {
                            Aircraft.debugprintln(this, "*** Control Column: Hit, Controls Destroyed..");
                            this.FM.AS.setControlsDamage(shot.initiator, 2);
                            this.FM.AS.setControlsDamage(shot.initiator, 1);
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                        }
                        break;

                    case 9:
                        if(getEnergyPastArmor(0.175F, shot) > 0.0F)
                        {
                            this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 6);
                            Aircraft.debugprintln(this, "*** Throttle Quadrant: Hit, Engine Controls Disabled..");
                        }
                        break;
                    }
                }
                if(s.startsWith("xxspar"))
                {
                    Aircraft.debugprintln(this, "*** Spar Construction: Hit..");
                    if(s.startsWith("xxspart") && chunkDamageVisible("Tail1") > 2 && getEnergyPastArmor(3.5F / (float)Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** Tail1 Spars Broken in Half..");
                        nextDMGLevels(1, 2, "Tail1_D3", shot.initiator);
                    }
                    if(s.startsWith("xxsparli") && chunkDamageVisible("WingLIn") > 2 && getEnergyPastArmor(9.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** WingLIn Spars Damaged..");
                        nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                    }
                    if(s.startsWith("xxsparri") && chunkDamageVisible("WingRIn") > 2 && getEnergyPastArmor(9.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** WingRIn Spars Damaged..");
                        nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                    }
                    if(s.startsWith("xxsparlm") && chunkDamageVisible("WingLMid") > 2 && getEnergyPastArmor(9.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** WingLMid Spars Damaged..");
                        nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                    }
                    if(s.startsWith("xxsparrm") && chunkDamageVisible("WingRMid") > 2 && getEnergyPastArmor(9.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** WingRMid Spars Damaged..");
                        nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                    }
                    if(s.startsWith("xxsparlo") && chunkDamageVisible("WingLOut") > 2 && getEnergyPastArmor(9.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** WingLOut Spars Damaged..");
                        nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                    }
                    if(s.startsWith("xxsparro") && chunkDamageVisible("WingROut") > 2 && getEnergyPastArmor(9.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** WingROut Spars Damaged..");
                        nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                    }
                }
                if(s.startsWith("xxwj") && getEnergyPastArmor(12.5F, shot) > 0.0F)
                    if(s.endsWith("l"))
                    {
                        Aircraft.debugprintln(this, "*** WingL Console Lock Destroyed..");
                        nextDMGLevels(4, 2, "WingLIn_D" + chunkDamageVisible("WingLIn"), shot.initiator);
                    } else
                    {
                        Aircraft.debugprintln(this, "*** WingR Console Lock Destroyed..");
                        nextDMGLevels(4, 2, "WingRIn_D" + chunkDamageVisible("WingRIn"), shot.initiator);
                    }
                if(s.startsWith("xxlock"))
                {
                    Aircraft.debugprintln(this, "*** Lock Construction: Hit..");
                    if(s.startsWith("xxlockr") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** Rudder Lock Shot Off..");
                        nextDMGLevels(3, 2, "Rudder1_D" + chunkDamageVisible("Rudder1"), shot.initiator);
                    }
                    if(s.startsWith("xxlockvl") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** VatorL Lock Shot Off..");
                        nextDMGLevels(3, 2, "VatorL_D" + chunkDamageVisible("VatorL"), shot.initiator);
                    }
                    if(s.startsWith("xxlockvr") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** VatorR Lock Shot Off..");
                        nextDMGLevels(3, 2, "VatorR_D" + chunkDamageVisible("VatorR"), shot.initiator);
                    }
                }
                if(s.startsWith("xxeng"))
                {
                    Aircraft.debugprintln(this, "*** Engine Module: Hit..");
                    if(s.endsWith("pipe"))
                    {
                        if(World.Rnd().nextFloat() < 0.3F && this.FM.CT.Weapons[1] != null && this.FM.CT.Weapons[1].length != 2)
                        {
                            this.FM.AS.setJamBullets(1, 0);
                            Aircraft.debugprintln(this, "*** Engine Module: Nose Nozzle Pipe Bent..");
                        }
                        getEnergyPastArmor(0.3F, shot);
                    } else
                    if(s.endsWith("prop"))
                    {
                        if(getEnergyPastArmor(0.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.8F)
                            if(World.Rnd().nextFloat() < 0.5F)
                            {
                                this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 3);
                                Aircraft.debugprintln(this, "*** Engine Module: Prop Governor Hit, Disabled..");
                            } else
                            {
                                this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 4);
                                Aircraft.debugprintln(this, "*** Engine Module: Prop Governor Hit, Damaged..");
                            }
                    } else
                    if(s.endsWith("gear"))
                    {
                        if(getEnergyPastArmor(5F, shot) > 0.0F)
                            if(World.Rnd().nextFloat() < 0.5F)
                            {
                                this.FM.EI.engines[0].setEngineStuck(shot.initiator);
                                Aircraft.debugprintln(this, "*** Engine Module: Bullet Jams Reductor Gear..");
                            } else
                            {
                                this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 3);
                                this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 4);
                                Aircraft.debugprintln(this, "*** Engine Module: Reductor Gear Damaged, Prop Governor Failed..");
                            }
                    } else
                    if(s.endsWith("supc"))
                    {
                        if(getEnergyPastArmor(0.1F, shot) > 0.0F)
                        {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 0);
                            Aircraft.debugprintln(this, "*** Engine Module: Supercharger Disabled..");
                        }
                    } else
                    if(s.endsWith("feed"))
                    {
                        if(getEnergyPastArmor(3.45F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F && this.FM.EI.engines[0].getPowerOutput() > 0.7F)
                        {
                            this.FM.AS.hitEngine(shot.initiator, 0, 100);
                            Aircraft.debugprintln(this, "*** Engine Module: Pressurized Fuel Line Pierced, Fuel Flamed..");
                        }
                    } else
                    if(s.endsWith("fuel"))
                    {
                        if(getEnergyPastArmor(1.1F, shot) > 0.0F)
                        {
                            this.FM.EI.engines[0].setEngineStops(shot.initiator);
                            Aircraft.debugprintln(this, "*** Engine Module: Fuel Line Stalled, Engine Stalled..");
                        }
                    } else
                    if(s.endsWith("case"))
                    {
                        if(getEnergyPastArmor(2.55F, shot) > 0.0F)
                        {
                            if(World.Rnd().nextFloat() < shot.power / 175000F)
                            {
                                this.FM.AS.setEngineStuck(shot.initiator, 0);
                                Aircraft.debugprintln(this, "*** Engine Module: Bullet Jams Crank Ball Bearing..");
                            }
                            if(World.Rnd().nextFloat() < shot.power / 50000F)
                            {
                                this.FM.AS.hitEngine(shot.initiator, 0, 2);
                                Aircraft.debugprintln(this, "*** Engine Module: Crank Case Hit, Readyness Reduced to " + this.FM.EI.engines[0].getReadyness() + "..");
                            }
                            this.FM.EI.engines[0].setReadyness(shot.initiator, this.FM.EI.engines[0].getReadyness() - World.Rnd().nextFloat(0.0F, shot.power / 48000F));
                            Aircraft.debugprintln(this, "*** Engine Module: Crank Case Hit, Readyness Reduced to " + this.FM.EI.engines[0].getReadyness() + "..");
                        }
                        getEnergyPastArmor(26F, shot);
                    } else
                    if(s.startsWith("xxeng1cyl"))
                    {
                        if(getEnergyPastArmor(0.125F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[0].getCylindersRatio() * 1.75F)
                        {
                            this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int)(shot.power / 4800F)));
                            Aircraft.debugprintln(this, "*** Engine Module: Cylinders Hit, " + this.FM.EI.engines[0].getCylindersOperable() + "/" + this.FM.EI.engines[0].getCylinders() + " Left..");
                            if(World.Rnd().nextFloat() < shot.power / 24000F)
                            {
                                this.FM.AS.hitEngine(shot.initiator, 0, 3);
                                Aircraft.debugprintln(this, "*** Engine Module: Cylinders Hit, Engine Fires..");
                            }
                            if(World.Rnd().nextFloat() < 0.01F)
                            {
                                this.FM.AS.setEngineStuck(shot.initiator, 0);
                                Aircraft.debugprintln(this, "*** Engine Module: Bullet Jams Piston Head..");
                            }
                            getEnergyPastArmor(26F, shot);
                        }
                    } else
                    if(s.startsWith("xxeng1mag"))
                    {
                        int j = s.charAt(9) - 49;
                        this.FM.EI.engines[0].setMagnetoKnockOut(shot.initiator, j);
                        Aircraft.debugprintln(this, "*** Engine Module: Magneto " + j + " Destroyed..");
                    } else
                    if(s.endsWith("sync"))
                    {
                        if(getEnergyPastArmor(2.25F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F)
                        {
                            Aircraft.debugprintln(this, "*** Engine Module: Gun Synchronized Hit, Nose Guns Lose Authority..");
                            this.FM.AS.setJamBullets(0, 0);
                            this.FM.AS.setJamBullets(0, 1);
                        }
                    } else
                    if(s.endsWith("oil1"))
                    {
                        this.FM.AS.hitOil(shot.initiator, 0);
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x80);
                        Aircraft.debugprintln(this, "*** Engine Module: Oil Radiator Hit..");
                    }
                }
                if(s.startsWith("xxoil"))
                {
                    this.FM.AS.hitOil(shot.initiator, 0);
                    getEnergyPastArmor(0.28F, shot);
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x80);
                    Aircraft.debugprintln(this, "*** Engine Module: Oil Tank Pierced..");
                }
                if(s.startsWith("xxtank"))
                {
                    int k = s.charAt(6) - 48;
                    switch(k)
                    {
                    default:
                        break;

                    case 1:
                    case 2:
                        if(getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat() < 0.45F)
                        {
                            if(this.FM.AS.astateTankStates[2] == 0)
                            {
                                Aircraft.debugprintln(this, "*** Fuel Tank: Pierced..");
                                this.FM.AS.hitTank(shot.initiator, 2, 1);
                                this.FM.AS.doSetTankState(shot.initiator, 2, 1);
                            }
                            if(World.Rnd().nextFloat() < 0.01F || shot.powerType == 3 && World.Rnd().nextFloat() < 0.25F)
                            {
                                this.FM.AS.hitTank(shot.initiator, 2, 2);
                                Aircraft.debugprintln(this, "*** Fuel Tank: Hit..");
                            }
                        }
                        break;

                    case 3:
                        if(World.Rnd().nextFloat() < 0.05F)
                        {
                            Aircraft.debugprintln(this, "*** MW50 Tank: Pierced..");
                            this.FM.AS.setInternalDamage(shot.initiator, 2);
                        }
                        break;

                    case 4:
                        if(getEnergyPastArmor(1.75F, shot) > 0.0F && (shot.powerType == 3 && World.Rnd().nextFloat() < 0.5F || World.Rnd().nextFloat() < 0.25F))
                        {
                            Aircraft.debugprintln(this, "*** Nitrogen Oxyde Tank: Pierced, Nitros Flamed..");
                            this.FM.AS.hitTank(shot.initiator, 0, 100);
                            this.FM.AS.hitTank(shot.initiator, 1, 100);
                            this.FM.AS.hitTank(shot.initiator, 2, 100);
                        }
                        break;
                    }
                }
                if(s.startsWith("xxmgun"))
                {
                    if(s.endsWith("01"))
                    {
                        Aircraft.debugprintln(this, "*** Cowling Gun: Disabled..");
                        this.FM.AS.setJamBullets(0, 0);
                    }
                    if(s.endsWith("02"))
                    {
                        Aircraft.debugprintln(this, "*** Cowling Gun: Disabled..");
                        this.FM.AS.setJamBullets(0, 1);
                    }
                    if(s.endsWith("l"))
                    {
                        Aircraft.debugprintln(this, "*** Wing Gun (L): Disabled..");
                        this.FM.AS.setJamBullets(1, 0);
                    }
                    if(s.endsWith("r"))
                    {
                        Aircraft.debugprintln(this, "*** Wing Gun (L): Disabled..");
                        this.FM.AS.setJamBullets(1, 1);
                    }
                    getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 12.96F), shot);
                }
                if(s.startsWith("xxcannon"))
                {
                    Aircraft.debugprintln(this, "*** Nose Cannon: Disabled..");
                    this.FM.AS.setJamBullets(1, 0);
                    getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 24.6F), shot);
                }
                if(s.startsWith("xxammo"))
                {
                    if(World.Rnd().nextFloat(3800F, 30000F) < shot.power)
                    {
                        if(s.endsWith("01"))
                        {
                            Aircraft.debugprintln(this, "*** Cowling Gun: Ammo Feed Chain Broken..");
                            this.FM.AS.setJamBullets(0, 0);
                        }
                        if(s.endsWith("02"))
                        {
                            Aircraft.debugprintln(this, "*** Cowling Gun: Ammo Feed Chain Broken..");
                            this.FM.AS.setJamBullets(0, 1);
                        }
                        if(s.endsWith("l"))
                        {
                            Aircraft.debugprintln(this, "*** Wing Gun (L): Ammo Feed Drum Damaged..");
                            this.FM.AS.setJamBullets(1, 0);
                        }
                        if(s.endsWith("r"))
                        {
                            Aircraft.debugprintln(this, "*** Wing Gun (R): Ammo Feed Drum Damaged..");
                            this.FM.AS.setJamBullets(1, 1);
                        }
                    }
                    getEnergyPastArmor(World.Rnd().nextFloat(0.0F, 28.33F), shot);
                }
            }
        } else
        if(s.startsWith("xcf") || s.startsWith("xcockpit"))
        {
            if(chunkDamageVisible("CF") < 3)
                hitChunk("CF", shot);
            if(s.startsWith("xcockpit"))
            {
                if(point3d.z > 0.4D)
                {
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
                    if(World.Rnd().nextFloat() < 0.1F)
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
                } else
                if(point3d.y > 0.0D)
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
                else
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
                if(point3d.x > 0.2D)
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
            }
        } else
        if(s.startsWith("xeng"))
        {
            if(chunkDamageVisible("Engine1") < 2)
                hitChunk("Engine1", shot);
        } else
        if(s.startsWith("xtail"))
        {
            if(chunkDamageVisible("Tail1") < 3)
                hitChunk("Tail1", shot);
        } else
        if(s.startsWith("xkeel"))
        {
            if(chunkDamageVisible("Keel1") < 2)
                hitChunk("Keel1", shot);
        } else
        if(s.startsWith("xrudder"))
        {
            if(chunkDamageVisible("Rudder1") < 1)
                hitChunk("Rudder1", shot);
        } else
        if(s.startsWith("xstab"))
        {
            if(s.startsWith("xstabl") && chunkDamageVisible("StabL") < 2)
                hitChunk("StabL", shot);
            if(s.startsWith("xstabr") && chunkDamageVisible("StabR") < 1)
                hitChunk("StabR", shot);
        } else
        if(s.startsWith("xvator"))
        {
            if(s.startsWith("xvatorl") && chunkDamageVisible("VatorL") < 1)
                hitChunk("VatorL", shot);
            if(s.startsWith("xvatorr") && chunkDamageVisible("VatorR") < 1)
                hitChunk("VatorR", shot);
        } else
        if(s.startsWith("xwing"))
        {
            if(s.startsWith("xwinglin") && chunkDamageVisible("WingLIn") < 3)
                hitChunk("WingLIn", shot);
            if(s.startsWith("xwingrin") && chunkDamageVisible("WingRIn") < 3)
                hitChunk("WingRIn", shot);
            if(s.startsWith("xwinglmid") && chunkDamageVisible("WingLMid") < 3)
                hitChunk("WingLMid", shot);
            if(s.startsWith("xwingrmid") && chunkDamageVisible("WingRMid") < 3)
                hitChunk("WingRMid", shot);
            if(s.startsWith("xwinglout") && chunkDamageVisible("WingLOut") < 3)
                hitChunk("WingLOut", shot);
            if(s.startsWith("xwingrout") && chunkDamageVisible("WingROut") < 3)
                hitChunk("WingROut", shot);
        } else
        if(s.startsWith("xarone"))
        {
            if(s.startsWith("xaronel"))
                hitChunk("AroneL", shot);
            if(s.startsWith("xaroner"))
                hitChunk("AroneR", shot);
        } else
        if(s.startsWith("xpilot") || s.startsWith("xhead"))
        {
            byte byte0 = 0;
            int l;
            if(s.endsWith("a"))
            {
                byte0 = 1;
                l = s.charAt(6) - 49;
            } else
            if(s.endsWith("b"))
            {
                byte0 = 2;
                l = s.charAt(6) - 49;
            } else
            {
                l = s.charAt(5) - 49;
            }
            hitFlesh(l, shot, byte0);
            if(this.FM.AS.getPilotHealth(0) >= 1.0F);
        }
    }

    private void cutOp(int i)
    {
        this.FM.Operate &= ~(1L << i);
    }

    protected boolean getOp(int i)
    {
        return (this.FM.Operate & 1L << i) != 0L;
    }

    private float Op(int i)
    {
        return getOp(i) ? 1.0F : 0.0F;
    }

    protected boolean cutFM(int i, int j, Actor actor)
    {
        if(!getOp(i))
            return false;
        switch(i)
        {
        case 17:
            cut("StabL");
            cutOp(17);
            this.FM.setCapableOfACM(false);
            if(World.Rnd().nextInt(-1, 8) < this.FM.Skill)
                this.FM.setReadyToReturn(true);
            if(World.Rnd().nextInt(-1, 16) < this.FM.Skill)
                this.FM.setReadyToDie(true);
            this.FM.Sq.liftStab *= 0.5F * Op(18) + 0.1F;
            this.FM.Sq.liftWingLIn *= 1.1F;
            this.FM.Sq.liftWingRIn *= 0.9F;
            this.FM.Sq.dragProducedCx -= 0.06F;
            if(Op(18) == 0.0F)
            {
                this.FM.SensPitch = 0.0F;
                this.FM.setGCenter(0.2F);
            } else
            {
                this.FM.setGCenter(0.1F);
                s17 = 0.0F;
                this.FM.SensPitch *= s17 + s18 + s31 + s32;
                X = 1.0F / (s17 + s18 + s31 + s32);
                s18 *= X;
                s31 *= X;
                s32 *= X;
            }
            // fall through

        case 31:
            if(Op(31) == 0.0F)
                return false;
            cut("VatorL");
            cutOp(31);
            if(Op(32) == 0.0F)
            {
                bHasElevatorControl = false;
                this.FM.setCapableOfACM(false);
                if(Op(18) == 0.0F)
                    this.FM.setReadyToDie(true);
            }
            this.FM.Sq.squareElevators *= 0.5F * Op(32);
            this.FM.Sq.dragProducedCx += 0.06F;
            s31 = 0.0F;
            this.FM.SensPitch *= s17 + s18 + s31 + s32;
            X = 1.0F / (s17 + s18 + s31 + s32);
            s17 *= X;
            s18 *= X;
            s32 *= X;
            return false;

        case 18:
            cut("StabR");
            cutOp(18);
            this.FM.setCapableOfACM(false);
            if(World.Rnd().nextInt(-1, 8) < this.FM.Skill)
                this.FM.setReadyToReturn(true);
            if(World.Rnd().nextInt(-1, 16) < this.FM.Skill)
                this.FM.setReadyToDie(true);
            this.FM.Sq.liftStab *= 0.5F * Op(17) + 0.1F;
            this.FM.Sq.liftWingLIn *= 0.9F;
            this.FM.Sq.liftWingRIn *= 1.1F;
            this.FM.Sq.dragProducedCx -= 0.06F;
            if(Op(17) == 0.0F)
            {
                this.FM.SensPitch = 0.0F;
                this.FM.setGCenter(0.2F);
            } else
            {
                this.FM.setGCenter(0.1F);
                s18 = 0.0F;
                this.FM.SensPitch *= s17 + s18 + s31 + s32;
                X = 1.0F / (s17 + s18 + s31 + s32);
                s17 *= X;
                s31 *= X;
                s32 *= X;
            }
            // fall through

        case 32:
            if(Op(32) == 0.0F)
                return false;
            cut("VatorR");
            cutOp(32);
            if(Op(31) == 0.0F)
            {
                bHasElevatorControl = false;
                this.FM.setCapableOfACM(false);
                if(Op(17) == 0.0F)
                    this.FM.setReadyToDie(true);
            }
            this.FM.Sq.squareElevators *= 0.5F * Op(31);
            this.FM.Sq.dragProducedCx += 0.06F;
            s32 = 0.0F;
            this.FM.SensPitch *= s17 + s18 + s31 + s32;
            X = 1.0F / (s17 + s18 + s31 + s32);
            s17 *= X;
            s18 *= X;
            s31 *= X;
            return false;

        default:
            return super.cutFM(i, j, actor);
        }
    }

    public void msgExplosion(Explosion explosion)
    {
        setExplosion(explosion);
        if(explosion.chunkName != null && explosion.power > 0.0F && explosion.chunkName.startsWith("Tail1"))
        {
            if(World.Rnd().nextFloat(0.0F, 0.038F) < explosion.power)
                this.FM.AS.setControlsDamage(explosion.initiator, 1);
            if(World.Rnd().nextFloat(0.0F, 0.042F) < explosion.power)
                this.FM.AS.setControlsDamage(explosion.initiator, 2);
        }
        super.msgExplosion(explosion);
    }

    private float trimElevator;
    private boolean bHasElevatorControl;
    private float X;
    private float s17;
    private float s18;
    private float s31;
    private float s32;
    private float kangle;
    public static boolean bChangedPit = false;

    static 
    {
        Class class1 = ME_155xyz.class;
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
    }
}
