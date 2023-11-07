package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class F9F6 extends F9F_Cougar
{
    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        if(this.FM.isPlayers())
        {
            this.FM.CT.bHasCockpitDoorControl = true;
            this.FM.CT.dvCockpitDoor = 0.5F;
        }
    }

    public void update(float f)
    {
        if(this.FM.CT.getWing() == 0.0F)
            if(this.FM.getSpeed() > 5F)
            {
                moveSlats(f);
                bSlatsOff = false;
            } else
            {
                slatsOff();
            }
        super.update(f);
    }

    protected void moveWingFold(HierMesh hiermesh, float f)
    {
        hiermesh.chunkSetAngles("WingLMid_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 70F), 0.0F);
        hiermesh.chunkSetAngles("WingRMid_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, -70F), 0.0F);
    }

    protected void moveSlats(float paramFloat)
    {
        resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.getAOA(), 6.8F, 15F, 0.0F, -0.04F);
        Aircraft.xyz[2] = Aircraft.cvt(this.FM.getAOA(), 6.8F, 15F, 0.0F, 0.045F);
        hierMesh().chunkSetAngles("WingSlatL", 0.0F, Aircraft.cvt(this.FM.getAOA(), 6.8F, 15F, 0.0F, 8.5F), 0.0F);
        hierMesh().chunkSetLocate("WingSlatL", Aircraft.xyz, Aircraft.ypr);
        hierMesh().chunkSetAngles("WingSlatR", 0.0F, Aircraft.cvt(this.FM.getAOA(), 6.8F, 15F, 0.0F, 8.5F), 0.0F);
        hierMesh().chunkSetLocate("WingSlatR", Aircraft.xyz, Aircraft.ypr);
    }

    protected void slatsOff()
    {
        if(!bSlatsOff)
        {
            resetYPRmodifier();
            Aircraft.xyz[1] = -0.04F;
            Aircraft.xyz[2] = 0.045F;
            hierMesh().chunkSetAngles("WingSlatL", 0.0F, 8.5F, 0.0F);
            hierMesh().chunkSetLocate("WingSlatL", Aircraft.xyz, Aircraft.ypr);
            hierMesh().chunkSetAngles("WingSlatR", 0.0F, 8.5F, 0.0F);
            hierMesh().chunkSetLocate("WingSlatR", Aircraft.xyz, Aircraft.ypr);
            bSlatsOff = true;
        }
    }

    protected boolean bSlatsOff;

    static 
    {
        Class class1 = F9F6.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "F9F6");
        Property.set(class1, "meshName", "3DO/Plane/F9F6/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFCSPar06());
        Property.set(class1, "yearService", 1946.9F);
        Property.set(class1, "yearExpired", 1955.3F);
        Property.set(class1, "FlightModel", "FlightModels/F9F6.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitF9F_Cougar.class
        });
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 0, 0, 9, 9, 9, 9, 9, 9, 
            9, 9, 3, 3, 3, 3, 3, 3, 9, 9, 
            2, 2, 2, 2, 2, 2, 2, 2, 9, 9, 
            9, 9, 2, 2, 2, 2, 2, 2, 2, 2, 
            3
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06", 
            "_ExternalDev07", "_ExternalDev08", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06", "_ExternalDev09", "_ExternalDev10", 
            "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalRock07", "_ExternalRock08", "_ExternalDev11", "_ExternalDev12", 
            "_ExternalDev13", "_ExternalDev14", "_ExternalRock09", "_ExternalRock09", "_ExternalRock10", "_ExternalRock10", "_ExternalRock11", "_ExternalRock11", "_ExternalRock12", "_ExternalRock12", 
            "_ExternalBomb07"
        });
    }
}
