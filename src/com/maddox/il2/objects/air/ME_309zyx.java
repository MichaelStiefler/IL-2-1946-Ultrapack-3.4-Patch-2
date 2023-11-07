package com.maddox.il2.objects.air;

import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.HUD;
import com.maddox.rts.Property;

public abstract class ME_309zyx extends Scheme1
    implements TypeFighter, TypeBNZFighter
{

    public ME_309zyx()
    {
        fSteer = 0.0F;
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
        if(flag)
        {
            if(this.FM.AS.astateTankStates[0] > 4 && World.Rnd().nextFloat() < 0.07F)
                nextDMGLevel(this.FM.AS.astateEffectChunks[0] + "0", 0, this);
            if(this.FM.AS.astateTankStates[0] > 4 && World.Rnd().nextFloat() < 0.14F)
                this.FM.AS.hitTank(this, 1, 1);
            if(this.FM.AS.astateTankStates[0] > 4 && World.Rnd().nextFloat() < 0.14F)
                this.FM.AS.hitTank(this, 2, 1);
            if(this.FM.AS.astateTankStates[1] > 4 && World.Rnd().nextFloat() < 0.07F)
                nextDMGLevel(this.FM.AS.astateEffectChunks[1] + "0", 0, this);
            if(this.FM.AS.astateTankStates[1] > 4 && World.Rnd().nextFloat() < 0.14F)
                this.FM.AS.hitTank(this, 0, 1);
            if(this.FM.AS.astateTankStates[1] > 4 && World.Rnd().nextFloat() < 0.07F)
                nextDMGLevel(this.FM.AS.astateEffectChunks[2] + "0", 0, this);
            if(this.FM.AS.astateTankStates[1] > 4 && World.Rnd().nextFloat() < 0.14F)
                this.FM.AS.hitTank(this, 0, 1);
        }
        if(this.FM.getAltitude() < 3000F)
            hierMesh().chunkVisible("HMask1_D0", false);
        else
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Head1_D0"));
    }

    public static void moveGear(HierMesh hiermesh, float f)
    {
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, -75F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC2b", 0.0F, -85F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC6_D0", 0.0F, -90F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC7_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.12F, 0.0F, 36F), 0.0F);
        hiermesh.chunkSetAngles("GearC8_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.12F, 0.0F, -81F), 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, -85F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL7_D0", 0.0F, -90F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, -85F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR7_D0", 0.0F, -90F * f, 0.0F);
    }

    protected void moveGear(float f)
    {
        moveGear(hierMesh(), f);
    }

    public void moveWheelSink()
    {
        resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.24F, 0.0F, 0.2405F);
        Aircraft.ypr[1] = fSteer;
        hierMesh().chunkSetLocate("GearC3_D0", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.115F, 0.0F, 0.11675F);
        hierMesh().chunkSetLocate("GearL3_D0", Aircraft.xyz, Aircraft.ypr);
        hierMesh().chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.115F, 0.0F, -15F), 0.0F);
        hierMesh().chunkSetAngles("GearL5_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.115F, 0.0F, -27F), 0.0F);
        resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.115F, 0.0F, 0.11675F);
        hierMesh().chunkSetLocate("GearR3_D0", Aircraft.xyz, Aircraft.ypr);
        hierMesh().chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.115F, 0.0F, -15F), 0.0F);
        hierMesh().chunkSetAngles("GearR5_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.115F, 0.0F, -27F), 0.0F);
    }

    public void moveSteering(float f)
    {
    }

    protected void moveRudder(float f)
    {
        hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -31F * f, 0.0F);
        if(this.FM.CT.getGear() > 0.75F)
            hierMesh().chunkSetAngles("GearC2b", 0.0F, -90F + 40F * f, 0.0F);
    }

    public void msgShot(Shot shot)
    {
        setShot(shot);
        if(shot.chunkName.startsWith("Engine") && World.Rnd().nextFloat(0.0F, 1.0F) < shot.mass)
            this.FM.AS.hitEngine(shot.initiator, 0, 1);
        if(shot.chunkName.startsWith("WingLMid") && World.Rnd().nextFloat() < 0.25F && shot.power > 7650F && shot.powerType == 3)
            this.FM.AS.hitTank(shot.initiator, 0, World.Rnd().nextInt(1, (int)(shot.mass * 100F)));
        if(shot.chunkName.startsWith("WingLIn") && World.Rnd().nextFloat() < 0.25F && shot.power > 7650F && shot.powerType == 3)
            this.FM.AS.hitTank(shot.initiator, 1, World.Rnd().nextInt(1, (int)(shot.mass * 100F)));
        if(shot.chunkName.startsWith("WingRIn") && World.Rnd().nextFloat() < 0.25F && shot.power > 7650F && shot.powerType == 3)
            this.FM.AS.hitTank(shot.initiator, 2, World.Rnd().nextInt(1, (int)(shot.mass * 100F)));
        if(shot.chunkName.startsWith("WingRMid") && World.Rnd().nextFloat() < 0.25F && shot.power > 7650F && shot.powerType == 3)
            this.FM.AS.hitTank(shot.initiator, 3, World.Rnd().nextInt(1, (int)(shot.mass * 100F)));
        if(shot.chunkName.startsWith("CF"))
        {
            if(World.Rnd().nextFloat(0.0F, 1.0F) < 0.3F)
                this.FM.AS.hitEngine(shot.initiator, 0, 1);
            this.FM.AS.hitTank(shot.initiator, 0, 1);
            if(World.Rnd().nextFloat() < 0.03F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
            if(World.Rnd().nextFloat() < 0.03F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
            if(World.Rnd().nextFloat() < 0.03F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
            if(World.Rnd().nextFloat() < 0.03F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
            if(World.Rnd().nextFloat() < 0.03F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
            if(World.Rnd().nextFloat() < 0.03F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
            if(World.Rnd().nextFloat() < 0.03F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
        }
        if(shot.chunkName.startsWith("Engine1") && World.Rnd().nextFloat(0.0F, 1.0F) < 0.06F && Aircraft.Pd.z > 0.48D)
        {
            this.FM.AS.setJamBullets(0, 0);
            this.FM.AS.setJamBullets(0, 1);
        }
        if(shot.chunkName.startsWith("Oil") && World.Rnd().nextFloat(0.0F, 1.0F) < 0.5F)
        {
            this.FM.AS.hitOil(shot.initiator, 0);
            getEnergyPastArmor(0.25F, shot);
            Aircraft.debugprintln(this, "*** Engine Module: Oil Radiator Hit..");
        }
        if(shot.chunkName.startsWith("Pilot"))
        {
            if(Aircraft.v1.x > 0.86D)
            {
                if((double)shot.power * Aircraft.v1.x > 19200D)
                    killPilot(shot.initiator, 0);
            } else
            if(World.Rnd().nextFloat(0.0F, 4600F) < shot.power)
            {
                killPilot(shot.initiator, 0);
                if(Aircraft.Pd.z > 0.5D && shot.initiator == World.getPlayerAircraft() && World.cur().isArcade())
                    HUD.logCenter("H E A D S H O T");
            }
            return;
        } else
        {
            super.msgShot(shot);
            return;
        }
    }

    protected boolean cutFM(int i, int j, Actor actor)
    {
        switch(i)
        {
        case 33:
            return super.cutFM(34, j, actor);

        case 36:
            return super.cutFM(37, j, actor);
        }
        return super.cutFM(i, j, actor);
    }

    private float fSteer;

    static 
    {
        Class class1 = ME_309zyx.class;
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
    }
}
