package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.Wreckage;
import com.maddox.rts.Property;

public class ME_209_IIA extends ME_155xyz
    implements TypeFighter, TypeBNZFighter, TypeStormovik
{

    public ME_209_IIA()
    {
        maxSpeedForOpeningCanopyRandomFactor = 1.0F;
        cockpitDoor_ = 0.0F;
        kangle = 0.0F;
        flapps = 0.0F;
        bHasBlister = true;
        maxSpeedForOpeningCanopyRandomFactor = World.Rnd().nextFloat(0.89F, 1.04F);
    }

    protected void moveRudder(float f)
    {
        hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -30F * f, 0.0F);
        hierMesh().chunkSetAngles("Flettner1_D0", 0.0F, -45F * f, 0.0F);
    }

    public void update(float f)
    {
        if(this.FM.getSpeed() > 5F)
        {
            hierMesh().chunkSetAngles("SlatL_D0", 0.0F, Aircraft.cvt(this.FM.getAOA(), 6.8F, 11F, 0.0F, 1.5F), 0.0F);
            hierMesh().chunkSetAngles("SlatR_D0", 0.0F, Aircraft.cvt(this.FM.getAOA(), 6.8F, 11F, 0.0F, 1.5F), 0.0F);
        }
        if(Math.abs(flapps - kangle) > 0.01F)
        {
            flapps = kangle;
            for(int i = 1; i < 16; i++)
            {
                String s = "Water" + i + "_D0";
                hierMesh().chunkSetAngles(s, 0.0F, 15F * kangle, 0.0F);
            }

        }
        kangle = 0.95F * kangle + 0.05F * this.FM.EI.engines[0].getControlRadiator();
        if(this.FM.Loc.z > 9000D)
        {
            if(!this.FM.EI.engines[0].getControlAfterburner())
                this.FM.EI.engines[0].setAfterburnerType(2);
        } else
        if(!this.FM.EI.engines[0].getControlAfterburner())
            this.FM.EI.engines[0].setAfterburnerType(9);
        super.update(f);
        if(this.FM.CT.getCockpitDoor() > 0.2F && bHasBlister && this.FM.getSpeedKMH() > MAX_SPEED_KMH_FOR_KEEPING_CANOPY * maxSpeedForOpeningCanopyRandomFactor && hierMesh().chunkFindCheck("Blister1_D0") != -1)
        {
            try
            {
                if(this == World.getPlayerAircraft())
                    ((CockpitMe_209II)Main3D.cur3D().cockpitCur).removeCanopy();
            }
            catch(Exception exception) { }
            playSound("aircraft.arrach", true);
            hierMesh().hideSubTrees("Blister1_D0");
            Wreckage wreckage = new Wreckage(this, hierMesh().chunkFind("Blister1_D0"));
            wreckage.collide(true);
            Vector3d vector3d = new Vector3d();
            vector3d.set(this.FM.Vwld);
            wreckage.setSpeed(vector3d);
            bHasBlister = false;
            this.FM.CT.bHasCockpitDoorControl = false;
            this.FM.Sq.dragProducedCx += 0.08F;
        }
        if(this.FM.getSpeedKMH() > MAX_SPEED_KMH_FOR_OPENING_CANOPY && bHasBlister && this.FM.CT.cockpitDoorControl > 0.1F && this.FM.CT.getCockpitDoor() < 0.99F)
        {
            this.FM.CT.cockpitDoorControl = 0.0F;
            this.FM.AS.setCockpitDoor(this.FM.actor, 0);
            float f1 = this.FM.CT.getCockpitDoor();
            if(Math.abs(cockpitDoor_ - f1) > 0.05F)
                moveCockpitDoor(cockpitDoor_ = f1);
            HUD.log("CockpitDoorCLS");
        }
    }

    public static void moveGear(HierMesh hiermesh, float f, float f1, float f2, boolean flag)
    {
        if(flag)
            hiermesh.chunkSetAngles("GearC2_D0", Aircraft.cvt(f2, 0.75F, 0.925F, 0.0F, 90F), 0.0F, 0.0F);
        else
            hiermesh.chunkSetAngles("GearC2_D0", Aircraft.cvt(f2, 0.01F, 0.175F, 0.0F, 90F), 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", Aircraft.cvt(f, 0.1F, 0.5F, 0.0F, -33.5F), 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, Aircraft.cvt(f, 0.1F, 0.5F, 0.0F, -77.5F), 0.0F);
        if(f < 0.5F)
            hiermesh.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.12F, 0.0F, 90F), 0.0F);
        else
            hiermesh.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(f, 0.48F, 0.6F, 90F, 0.0F), 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", Aircraft.cvt(f1, 0.4F, 0.8F, 0.0F, 33.5F), 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, Aircraft.cvt(f1, 0.4F, 0.8F, 0.0F, 77.5F), 0.0F);
        if(f1 < 0.5F)
            hiermesh.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(f1, 0.3F, 0.42F, 0.0F, -90F), 0.0F);
        else
            hiermesh.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(f1, 0.78F, 0.9F, -90F, 0.0F), 0.0F);
    }

    public static void moveGear(HierMesh hiermesh, float f, float f1, float f2)
    {
        moveGear(hiermesh, f, f1, f2, true);
    }

    protected void moveGear(float f, float f1, float f2)
    {
        moveGear(hierMesh(), f, f1, f2, this.FM.CT.GearControl > 0.5F);
    }

    public static void moveGear(HierMesh hiermesh, float f, boolean flag)
    {
        moveGear(hiermesh, f, f, f, flag);
    }

    public static void moveGear(HierMesh hiermesh, float f)
    {
        moveGear(hiermesh, f, f, f, true);
    }

    protected void moveGear(float f)
    {
        moveGear(hierMesh(), f, this.FM.CT.GearControl > 0.5F);
    }

    public void moveSteering(float f)
    {
        if(this.FM.CT.getGear() >= 0.98F)
            hierMesh().chunkSetAngles("GearC2_D0", 90F, -f, 0.0F);
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

    public float cockpitDoor_;
    private static final float MAX_SPEED_KMH_FOR_OPENING_CANOPY = 280F;
    private static final float MAX_SPEED_KMH_FOR_KEEPING_CANOPY = 330F;
    private float maxSpeedForOpeningCanopyRandomFactor;
    private float kangle;
    private float flapps;
    public boolean bHasBlister;

    static 
    {
        Class class1 = ME_209_IIA.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Me-209-IIA");
        Property.set(class1, "meshName", "3do/plane/Me-209-II/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(class1, "yearService", 1944F);
        Property.set(class1, "yearExpired", 1945F);
        Property.set(class1, "FlightModel", "FlightModels/Me-209-IIA.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitMe_209II.class
        });
        Property.set(class1, "LOSElevation", 0.7498F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 
            1, 3, 9, 9, 9, 9, 9, 9, 9, 9, 
            9, 9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_MGUN01", "_MGUN02", "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_CANNON05", "_CANNON06", "_CANNON07", "_CANNON08", 
            "_CANNON09", "_ExternalBomb01", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06", "_ExternalDev07", "_ExternalDev08", 
            "_ExternalDev09", "_ExternalDev10"
        });
    }
}
