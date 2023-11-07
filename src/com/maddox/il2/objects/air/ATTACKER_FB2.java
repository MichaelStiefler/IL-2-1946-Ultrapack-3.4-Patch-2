package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;

public class ATTACKER_FB2 extends Scheme1
    implements TypeFighter, TypeBNZFighter
{

    public ATTACKER_FB2()
    {
        arrestor = 0.0F;
    }

    protected void moveWingFold(HierMesh hiermesh, float f)
    {
        hiermesh.chunkSetAngles("WingLOut_D0", 0.0F, 110F * f, 0.0F);
        hiermesh.chunkSetAngles("WingROut_D0", 0.0F, -110F * f, 0.0F);
    }

    public void moveWingFold(float f)
    {
        moveWingFold(hierMesh(), f);
        this.FM.doRequestFMSFX(1, (int)Aircraft.cvt(f, 0.1F, 1.0F, 0.0F, 40F));
    }

    public static void moveGear(HierMesh hiermesh, float f)
    {
        float f1 = Math.max(-f * 800F, -100F);
        hiermesh.chunkSetAngles("GearL6_D0", 0.0F, f1, 0.0F);
        hiermesh.chunkSetAngles("GearR6_D0", 0.0F, -f1, 0.0F);
        hiermesh.chunkSetAngles("GearCL_D0", 0.0F, -f1, 0.0F);
        hiermesh.chunkSetAngles("GearCR_D0", 0.0F, f1, 0.0F);
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, 95F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, -95F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, 83F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, -83F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, 15F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, -165F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, -15F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, 165F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, 0.0F, 50F * f);
    }

    protected void moveGear(float f)
    {
        moveGear(hierMesh(), f);
    }

    protected void moveElevator(float f)
    {
        hierMesh().chunkSetAngles("VatorL_D0", 0.0F, 0.0F, -25F * f);
        hierMesh().chunkSetAngles("VatorR_D0", 0.0F, 0.0F, -25F * f);
    }

    protected void moveAileron(float f)
    {
        hierMesh().chunkSetAngles("AroneL1_D0", 0.0F, 0.0F, 25F * f);
        hierMesh().chunkSetAngles("AroneR1_D0", 0.0F, 0.0F, -25F * f);
        hierMesh().chunkSetAngles("AroneL2_D0", 0.0F, 0.0F, 25F * f);
        hierMesh().chunkSetAngles("AroneR2_D0", 0.0F, 0.0F, -25F * f);
    }

    protected void moveRudder(float f)
    {
        hierMesh().chunkSetAngles("Rudder1_D0", 30F * f, 0.0F, 0.0F);
    }

    protected void moveFlap(float f)
    {
        hierMesh().chunkSetAngles("Flap_WL", 0.0F, 0.0F, 40F * f);
        hierMesh().chunkSetAngles("Flap_WR", 0.0F, 0.0F, 40F * f);
        hierMesh().chunkSetAngles("Flap_CL", 0.0F, 0.0F, 45F * f);
        hierMesh().chunkSetAngles("Flap_CR", 0.0F, 0.0F, 45F * f);
    }

    public void doMurderPilot(int i)
    {
        switch(i)
        {
        case 0:
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("HMask1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            break;
        }
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        for(int i = 1; i < 2; i++)
            if(this.FM.getAltitude() < 3000F)
                hierMesh().chunkVisible("HMask" + i + "_D0", false);
            else
                hierMesh().chunkVisible("HMask" + i + "_D0", hierMesh().isChunkVisible("Pilot" + i + "_D0"));

    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xx"))
        {
            if(s.startsWith("xxarmor"))
            {
                if(s.startsWith("xxarmorg"))
                {
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
                    getEnergyPastArmor(56.259998321533203D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                    if(shot.power <= 0.0F)
                        if(Math.abs(Aircraft.v1.x) > 0.866D)
                            doRicochet(shot);
                        else
                            doRicochetBack(shot);
                }
                if(s.startsWith("xxarmorp"))
                    getEnergyPastArmor(12.88D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                return;
            }
            if(s.startsWith("xxeng"))
            {
                int i = s.charAt(5) - 49;
                if(point3d.x > 1.203D)
                {
                    if(getEnergyPastArmor(0.1F, shot) > 0.0F)
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, i, 0);
                } else
                {
                    if(getEnergyPastArmor(3.2F, shot) > 0.0F)
                        this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, 1);
                    if(World.Rnd().nextFloat(0.009F, 0.1357F) < shot.mass && this.FM.EI.engines[0].getStage() == 6)
                        this.FM.AS.hitEngine(shot.initiator, 0, 1);
                    getEnergyPastArmor(14.296F, shot);
                }
                return;
            }
            if(s.startsWith("xxtank"))
            {
                int j = s.charAt(6) - 48;
                switch(j)
                {
                case 1:
                    doHitMeATank(shot, 0);
                    break;

                case 2:
                    doHitMeATank(shot, 1);
                    break;

                case 3:
                    doHitMeATank(shot, 2);
                    break;

                case 4:
                    doHitMeATank(shot, 3);
                    break;

                case 5:
                    doHitMeATank(shot, 2);
                    break;

                case 6:
                    doHitMeATank(shot, 3);
                    break;

                case 7:
                    doHitMeATank(shot, 2);
                    break;

                case 8:
                    doHitMeATank(shot, 3);
                    break;
                }
                return;
            }
        }
        if(s.startsWith("xcf"))
        {
            if(chunkDamageVisible("CF") < 3)
                hitChunk("CF", shot);
            if(World.Rnd().nextFloat() < 0.32F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
            if(World.Rnd().nextFloat() < 0.32F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
        } else
        if(s.startsWith("xtail"))
        {
            if(chunkDamageVisible("Tail1") < 3)
                hitChunk("Tail1", shot);
        } else
        if(s.startsWith("xkeel1"))
        {
            if(chunkDamageVisible("Keel1") < 2)
                hitChunk("Keel1", shot);
        } else
        if(s.startsWith("xrudder"))
        {
            if(chunkDamageVisible("Rudder1") < 1)
                hitChunk("Rudder1", shot);
        } else
        if(s.startsWith("xstabl"))
        {
            if(chunkDamageVisible("StabL") < 2)
                hitChunk("StabL", shot);
        } else
        if(s.startsWith("xstabr"))
        {
            if(chunkDamageVisible("StabR") < 2)
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
            if(s.startsWith("xwinglout") && chunkDamageVisible("WingLOut") < 3)
                hitChunk("WingLOut", shot);
            if(s.startsWith("xwingrout") && chunkDamageVisible("WingROut") < 3)
                hitChunk("WingROut", shot);
        } else
        if(s.startsWith("xarone"))
        {
            if(s.startsWith("xaronel1") && chunkDamageVisible("AroneL1") < 1)
                hitChunk("AroneL1", shot);
            if(s.startsWith("xaroner1") && chunkDamageVisible("AroneR1") < 1)
                hitChunk("AroneR1", shot);
            if(s.startsWith("xaronel2") && chunkDamageVisible("AroneL2") < 1)
                hitChunk("AroneL2", shot);
            if(s.startsWith("xaroner2") && chunkDamageVisible("AroneR1") < 1)
                hitChunk("AroneR2", shot);
        } else
        if(s.startsWith("xgear"))
        {
            if(s.startsWith("xgear") && World.Rnd().nextFloat() < 0.1F && getEnergyPastArmor(World.Rnd().nextFloat(12.88F, 16.96F), shot) > 0.0F)
                this.FM.AS.setInternalDamage(shot.initiator, 3);
        } else
        if(s.startsWith("xpilot") || s.startsWith("xhead"))
        {
            byte byte0 = 0;
            int k;
            if(s.endsWith("a"))
            {
                byte0 = 1;
                k = s.charAt(6) - 49;
            } else
            if(s.endsWith("b"))
            {
                byte0 = 2;
                k = s.charAt(6) - 49;
            } else
            {
                k = s.charAt(5) - 49;
            }
            hitFlesh(k, shot, byte0);
        }
    }

    private final void doHitMeATank(Shot shot, int i)
    {
        if(getEnergyPastArmor(0.2F, shot) > 0.0F)
            if(shot.power < 14100F)
            {
                if(this.FM.AS.astateTankStates[i] == 0)
                {
                    this.FM.AS.hitTank(shot.initiator, i, 1);
                    this.FM.AS.doSetTankState(shot.initiator, i, 1);
                }
                if(this.FM.AS.astateTankStates[i] > 0 && (World.Rnd().nextFloat() < 0.02F || shot.powerType == 3 && World.Rnd().nextFloat() < 0.25F))
                    this.FM.AS.hitTank(shot.initiator, i, 1);
            } else
            {
                this.FM.AS.hitTank(shot.initiator, i, World.Rnd().nextInt(0, (int)(shot.power / 58899F)));
            }
    }

    public void update(float f)
    {
        super.update(f);
        if(this.FM.CT.getArrestor() > 0.001F)
        {
            if(this.FM.Gears.arrestorVAngle != 0.0F)
            {
                float f1 = Aircraft.cvt(this.FM.Gears.arrestorVAngle, -73.5F, 3.5F, 1.0F, 0.0F);
                arrestor = 0.8F * arrestor + 0.2F * f1;
                moveArrestorHook(arrestor);
            } else
            {
                float f2 = -1.224F * this.FM.Gears.arrestorVSink;
                if(f2 < 0.0F && this.FM.getSpeedKMH() > 60F)
                    Eff3DActor.New(this, this.FM.Gears.arrestorHook, null, 1.0F, "3DO/Effects/Fireworks/04_Sparks.eff", 0.1F);
                if(f2 > 0.2F)
                    f2 = 0.2F;
                if(f2 > 0.0F)
                    arrestor = 0.7F * arrestor + 0.3F * (arrestor + f2);
                else
                    arrestor = 0.3F * arrestor + 0.7F * (arrestor + f2);
                if(arrestor < 0.0F)
                    arrestor = 0.0F;
                else
                if(arrestor > this.FM.CT.getArrestor())
                    arrestor = this.FM.CT.getArrestor();
                moveArrestorHook(arrestor);
            }
        } else
        if(Config.isUSE_RENDER() && this.FM.AS.isMaster())
            if(this.FM.EI.engines[0].getPowerOutput() > 0.8F && this.FM.EI.engines[0].getStage() == 6)
            {
                if(this.FM.EI.engines[0].getPowerOutput() > 0.95F)
                    this.FM.AS.setSootState(this, 0, 3);
                else
                    this.FM.AS.setSootState(this, 0, 2);
            } else
            {
                this.FM.AS.setSootState(this, 0, 0);
            }
    }

    public void moveArrestorHook(float f)
    {
        hierMesh().chunkSetAngles("Hook_D0", 0.0F, 0.0F, 70F * arrestor);
    }

    public void moveCockpitDoor(float f)
    {
        resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.6F);
        hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if(Config.isUSE_RENDER())
        {
            if(Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null)
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            setDoorSnd(f);
        }
    }

    private float arrestor;

    static 
    {
        Class class1 = ATTACKER_FB2.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Attacker");
        Property.set(class1, "meshName", "3DO/Plane/ATTACKER-FB2/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar02());
        Property.set(class1, "yearService", 1945F);
        Property.set(class1, "yearExpired", 1954F);
        Property.set(class1, "FlightModel", "FlightModels/SA2J.fmd");
        Property.set(class1, "LOSElevation", 0.87195F);
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitATCK.class
        });
        Aircraft.weaponTriggersRegister(class1, new int[] {
            1, 1, 1, 1, 3, 3, 9, 9, 2, 2, 
            2, 2, 2, 2, 9, 9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalBomb01", "_ExternalBomb02", "_ExternalDev01", "_ExternalDev02", "_ExternalRock01", "_ExternalRock02", 
            "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalDev03", "_ExternalDev04"
        });
    }
}
