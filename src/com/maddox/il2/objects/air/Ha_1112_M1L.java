package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Wing;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.Wreckage;
import com.maddox.rts.Property;

public class Ha_1112_M1L extends BF_109
{
    public Ha_1112_M1L()
    {
        cockpitDoor_ = 0.0F;
        fMaxKMHSpeedForOpenCanopy = 250F;
        kangle = 0.0F;
        bHasBlister = true;
        kl = 1.0F;
        kr = 1.0F;
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
        super.update(f);
        if((double)this.FM.CT.getCockpitDoor() > 0.2D && bHasBlister && this.FM.getSpeedKMH() > fMaxKMHSpeedForOpenCanopy && hierMesh().chunkFindCheck("Blister1_D0") != -1)
        {
            try
            {
                if(this == World.getPlayerAircraft())
                    ((CockpitBuchon)Main3D.cur3D().cockpitCur).removeCanopy();
            }
            catch(Exception exception) { }
            hierMesh().hideSubTrees("Blister1_D0");
            Wreckage wreckage = new Wreckage(this, hierMesh().chunkFind("Blister1_D0"));
            wreckage.collide(true);
            Vector3d vector3d = new Vector3d();
            vector3d.set(this.FM.Vwld);
            wreckage.setSpeed(vector3d);
            bHasBlister = false;
            this.FM.CT.bHasCockpitDoorControl = false;
            this.FM.setGCenter(-0.5F);
        }
        if(this.FM.isPlayers())
            if(!Main3D.cur3D().isViewOutside())
                hierMesh().chunkVisible("CF_D0", false);
            else
                hierMesh().chunkVisible("CF_D0", true);
        if(this.FM.isPlayers())
        {
            if(!Main3D.cur3D().isViewOutside())
                hierMesh().chunkVisible("CF_D1", false);
            hierMesh().chunkVisible("CF_D2", false);
            hierMesh().chunkVisible("CF_D3", false);
        }
        if(this.FM.isPlayers())
        {
            if(!Main3D.cur3D().isViewOutside())
                hierMesh().chunkVisible("Blister1_D0", false);
            else
            if(bHasBlister)
                hierMesh().chunkVisible("Blister1_D0", true);
            com.maddox.JGP.Point3d point3d = World.getPlayerAircraft().pos.getAbsPoint();
            if(point3d.z - World.land().HQ(point3d.x, point3d.y) < 0.01D)
                hierMesh().chunkVisible("CF_D0", true);
            if(this.FM.AS.bIsAboutToBailout)
                hierMesh().chunkVisible("Blister1_D0", false);
        }
    }

    public static void moveGear(HierMesh paramHierMesh, float paramFloat)
    {
        float f1 = 0.8F;
        float f2 = -0.5F * (float)Math.cos((double)(paramFloat / f1) * Math.PI) + 0.5F;
        if(paramFloat <= f1 || paramFloat == 1.0F)
        {
            paramHierMesh.chunkSetAngles("GearL3_D0", 0.0F, -77.5F * f2, 0.0F);
            paramHierMesh.chunkSetAngles("GearL2_D0", -33.5F * f2, 0.0F, 0.0F);
        }
        f2 = -0.5F * (float)Math.cos((double)((paramFloat - (1.0F - f1)) / f1) * Math.PI) + 0.5F;
        if(paramFloat >= 1.0F - f1)
        {
            paramHierMesh.chunkSetAngles("GearR3_D0", 0.0F, 77.5F * f2, 0.0F);
            paramHierMesh.chunkSetAngles("GearR2_D0", 33.5F * f2, 0.0F, 0.0F);
        }
        if(paramFloat > 0.99F)
        {
            paramHierMesh.chunkSetAngles("GearL3_D0", 0.0F, -77.5F, 0.0F);
            paramHierMesh.chunkSetAngles("GearL2_D0", -33.5F, 0.0F, 0.0F);
            paramHierMesh.chunkSetAngles("GearR3_D0", 0.0F, 77.5F, 0.0F);
            paramHierMesh.chunkSetAngles("GearR2_D0", 33.5F, 0.0F, 0.0F);
        }
        if(paramFloat < 0.01F)
        {
            paramHierMesh.chunkSetAngles("GearL3_D0", 0.0F, 0.0F, 0.0F);
            paramHierMesh.chunkSetAngles("GearL2_D0", 0.0F, 0.0F, 0.0F);
            paramHierMesh.chunkSetAngles("GearR3_D0", 0.0F, 0.0F, 0.0F);
            paramHierMesh.chunkSetAngles("GearR2_D0", 0.0F, 0.0F, 0.0F);
        }
    }

    protected void moveGear(float paramFloat)
    {
        if(this.FM.Gears.isHydroOperable())
        {
            kl = 1.0F;
            kr = 1.0F;
        }
        float f1 = 0.9F - (float)((Wing)getOwner()).aircIndex(this) * 0.1F;
        float f2 = -0.5F * (float)Math.cos((double)(paramFloat / f1) * Math.PI) * kl + 0.5F;
        if(paramFloat <= f1 || paramFloat == 1.0F)
        {
            hierMesh().chunkSetAngles("GearL3_D0", 0.0F, -77.5F * f2, 0.0F);
            hierMesh().chunkSetAngles("GearL2_D0", -33.5F * f2, 0.0F, 0.0F);
        }
        f2 = -0.5F * (float)Math.cos((double)((paramFloat - (1.0F - f1)) / f1) * Math.PI) * kr + 0.5F;
        if(paramFloat >= 1.0F - f1)
        {
            hierMesh().chunkSetAngles("GearR3_D0", 0.0F, 77.5F * f2, 0.0F);
            hierMesh().chunkSetAngles("GearR2_D0", 33.5F * f2, 0.0F, 0.0F);
        }
        if(paramFloat > 0.99F)
        {
            hierMesh().chunkSetAngles("GearL3_D0", 0.0F, -77.5F, 0.0F);
            hierMesh().chunkSetAngles("GearL2_D0", -33.5F, 0.0F, 0.0F);
            hierMesh().chunkSetAngles("GearR3_D0", 0.0F, 77.5F, 0.0F);
            hierMesh().chunkSetAngles("GearR2_D0", 33.5F, 0.0F, 0.0F);
        }
    }

    public void moveSteering(float paramFloat)
    {
        if(this.FM.CT.getGear() < 0.98F)
        {
            return;
        } else
        {
            hierMesh().chunkSetAngles("GearC2_D0", 0.0F, -paramFloat, 0.0F);
            return;
        }
    }

    public void moveCockpitDoor(float f)
    {
        if(bHasBlister)
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
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        this.FM.AS.wantBeaconsNet(true);
        Ha_1112_M1L.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {

        hierMesh.chunkVisible("RocketRail_R", thisWeaponsName.startsWith("8xrockets"));
        hierMesh.chunkVisible("RocketRail_R2", thisWeaponsName.startsWith("8xrockets"));
        hierMesh.chunkVisible("RocketRail_L", thisWeaponsName.startsWith("8xrockets"));
        hierMesh.chunkVisible("RocketRail_L2", thisWeaponsName.startsWith("8xrockets"));
        
        hierMesh.chunkVisible("MgFFR_D0", thisWeaponsName.startsWith("BoB"));
        hierMesh.chunkVisible("MgFFL_D0", thisWeaponsName.startsWith("BoB"));
        hierMesh.chunkVisible("StabStrutL_D0", thisWeaponsName.startsWith("BoB"));
        hierMesh.chunkVisible("StabStrutR_D0", thisWeaponsName.startsWith("BoB"));
        hierMesh.chunkVisible("antena", thisWeaponsName.startsWith("BoB"));
        
        hierMesh.chunkVisible("MGLeft", !thisWeaponsName.startsWith("BoB") && !thisWeaponsName.startsWith("none"));
        hierMesh.chunkVisible("MGRight", !thisWeaponsName.startsWith("BoB") && !thisWeaponsName.startsWith("none"));
        hierMesh.chunkVisible("Line01", !thisWeaponsName.startsWith("BoB") && !thisWeaponsName.startsWith("none"));
        hierMesh.chunkVisible("Line02", !thisWeaponsName.startsWith("BoB") && !thisWeaponsName.startsWith("none"));
    }

    private static float kl = 1.0F;
    private static float kr = 1.0F;
    public float cockpitDoor_;
    private float fMaxKMHSpeedForOpenCanopy;
    private float kangle;
    public boolean bHasBlister;

    static 
    {
        Class class1 = Ha_1112_M1L.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Bf109");
        Property.set(class1, "meshName", "3DO/Plane/Ha1112M/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar03());
        Property.set(class1, "yearService", 1954F);
        Property.set(class1, "yearExpired", 1969.5F);
        Property.set(class1, "FlightModel", "FlightModels/Buchon.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitBuchon.class
        });
        Property.set(class1, "LOSElevation", 0.7498F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 9, 9, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0 });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_MGUN01", "_MGUN02", "_ExternalDev01", "_ExternalDev02", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", 
            "_ExternalRock07", "_ExternalRock08", "_CANNON01", "_CANNON02"
        });
    }
}
