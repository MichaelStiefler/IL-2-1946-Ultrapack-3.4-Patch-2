package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.Wreckage;
import com.maddox.rts.Property;

public class ME_509A2 extends ME_509zyx
    implements TypeFighter, TypeBNZFighter
{

    public ME_509A2()
    {
        trimElevator = 0.0F;
        bHasElevatorControl = true;
        maxSpeedForOpeningCanopyRandomFactor = 1.0F;
        cockpitDoor_ = 0.0F;
        bHasBlister = true;
        maxSpeedForOpeningCanopyRandomFactor = World.Rnd().nextFloat(0.89F, 1.04F);
    }

    protected void moveFlap(float f)
    {
        hierMesh().chunkSetAngles("Flap01_D0", 0.0F, -45F * f, 0.0F);
        hierMesh().chunkSetAngles("Flap02_D0", 0.0F, -45F * f, 0.0F);
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
        if(this.FM.Loc.z > 8800D)
        {
            if(!this.FM.EI.engines[0].getControlAfterburner())
                this.FM.EI.engines[0].setAfterburnerType(7);
        } else
        if(!this.FM.EI.engines[0].getControlAfterburner())
            this.FM.EI.engines[0].setAfterburnerType(9);
        super.update(f);
        if(this.FM.CT.getCockpitDoor() > 0.2F && bHasBlister && this.FM.getSpeedKMH() > MAX_SPEED_KMH_FOR_KEEPING_CANOPY * maxSpeedForOpeningCanopyRandomFactor && hierMesh().chunkFindCheck("Blister1_D0") != -1)
        {
            try
            {
                if(this == World.getPlayerAircraft())
                    ((CockpitME_509A2)Main3D.cur3D().cockpitCur).removeCanopy();
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

    public void moveCockpitDoor(float f)
    {
        resetYPRmodifier();
        hierMesh().chunkSetAngles("Blister1_D0", 0.0F, 0.0F, 100F * f);
        if(Config.isUSE_RENDER())
        {
            if(Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null)
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            setDoorSnd(f);
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

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("mgL", thisWeaponsName.equals("default") || thisWeaponsName.equals("2xdpt") || thisWeaponsName.equals("2xMK-108") || thisWeaponsName.equals("2xMK-108+2xdpt"));
        hierMesh.chunkVisible("mgR", thisWeaponsName.equals("default") || thisWeaponsName.equals("2xdpt") || thisWeaponsName.equals("2xMK-108") || thisWeaponsName.equals("2xMK-108+2xdpt"));
        hierMesh.chunkVisible("mg151L", thisWeaponsName.equals("default") || thisWeaponsName.equals("2xdpt"));
        hierMesh.chunkVisible("mg151R", thisWeaponsName.equals("default") || thisWeaponsName.equals("2xdpt"));
        hierMesh.chunkVisible("mk108L", thisWeaponsName.equals("2xMK-108") || thisWeaponsName.equals("2xMK-108+2xdpt"));
        hierMesh.chunkVisible("mk108R", thisWeaponsName.equals("2xMK-108") || thisWeaponsName.equals("2xMK-108+2xdpt"));
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        ME_509A2.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }
    
    public float cockpitDoor_;
    private static final float MAX_SPEED_KMH_FOR_OPENING_CANOPY = 280F;
    private static final float MAX_SPEED_KMH_FOR_KEEPING_CANOPY = 330F;
    private float maxSpeedForOpeningCanopyRandomFactor;
    private float trimElevator;
    private boolean bHasElevatorControl;
    private float X;
    private float s17;
    private float s18;
    private float s31;
    private float s32;
    public static boolean bChangedPit = false;
    public boolean bHasBlister;

    static 
    {
        Class class1 = ME_509A2.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Me-509A-2");
        Property.set(class1, "meshName", "3DO/Plane/Me-509A2(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1944.5F);
        Property.set(class1, "yearExpired", 1948.5F);
        Property.set(class1, "FlightModel", "FlightModels/Me-509A-2.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitME_509A2.class
        });
        Property.set(class1, "LOSElevation", 0.70305F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 1, 1, 1, 1, 3, 3, 3, 3, 
            9, 9, 9, 9, 9, 9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_MGUN01", "_MGUN02", "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", 
            "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06"
        });
    }
}
