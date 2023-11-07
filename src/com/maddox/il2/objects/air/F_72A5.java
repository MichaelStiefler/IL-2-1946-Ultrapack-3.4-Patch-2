package com.maddox.il2.objects.air;

import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;

public class F_72A5 extends F_72xyz
{
    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        F_72A5.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }
    
    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        _WeaponSlot[] weaponSlotsRegistered = Aircraft.getWeaponSlotsRegistered(aircraftClass, thisWeaponsName);
        hierMesh.chunkVisible("MGL1", weaponSlotsRegistered[4] != null);
        hierMesh.chunkVisible("MGL2", weaponSlotsRegistered[2] != null);
        hierMesh.chunkVisible("MGL3", weaponSlotsRegistered[0] != null);
        hierMesh.chunkVisible("MGR1", weaponSlotsRegistered[5] != null);
        hierMesh.chunkVisible("MGR2", weaponSlotsRegistered[3] != null);
        hierMesh.chunkVisible("MGR3", weaponSlotsRegistered[1] != null);
        hierMesh.chunkVisible("CannonLin", weaponSlotsRegistered[8] != null);
        hierMesh.chunkVisible("CannonLout", weaponSlotsRegistered[6] != null);
        hierMesh.chunkVisible("CannonRin", weaponSlotsRegistered[9] != null);
        hierMesh.chunkVisible("CannonRout", weaponSlotsRegistered[7] != null);
        hierMesh.chunkVisible("37CannonLin", weaponSlotsRegistered[12] != null);
        hierMesh.chunkVisible("37CannonLout", weaponSlotsRegistered[10] != null);
        hierMesh.chunkVisible("37CannonRin", weaponSlotsRegistered[13] != null);
        hierMesh.chunkVisible("37CannonRout", weaponSlotsRegistered[11] != null);
        hierMesh.chunkVisible("RackL_D0", weaponSlotsRegistered[18] != null || weaponSlotsRegistered[20] != null);
        hierMesh.chunkVisible("RackR_D0", weaponSlotsRegistered[19] != null || weaponSlotsRegistered[21] != null);
    }

    protected void moveFan(float f)
    {
        hierMesh().chunkFind(Aircraft.Props[1][0]);
        int i = 0;
        for(int j = 0; j < 2; j++)
        {
            if(this.oldProp[j] < 2)
            {
                i = Math.abs((int)(this.FM.EI.engines[0].getw() * 0.06F));
                if(i >= 1)
                    i = 1;
                if(i != this.oldProp[j] && hierMesh().isChunkVisible(Aircraft.Props[j][this.oldProp[j]]))
                {
                    hierMesh().chunkVisible(Aircraft.Props[j][this.oldProp[j]], false);
                    this.oldProp[j] = i;
                    hierMesh().chunkVisible(Aircraft.Props[j][i], true);
                }
            }
            if(i == 0)
            {
                this.propPos[j] = (this.propPos[j] + 57.3F * this.FM.EI.engines[0].getw() * f) % 360F;
            } else
            {
                float f1 = 57.3F * this.FM.EI.engines[0].getw();
                f1 %= 2880F;
                f1 /= 2880F;
                if(f1 <= 0.5F)
                    f1 *= 2.0F;
                else
                    f1 = f1 * 2.0F - 2.0F;
                f1 *= 1200F;
                this.propPos[j] = (this.propPos[j] + f1 * f) % 360F;
            }
            if(j == 0)
                hierMesh().chunkSetAngles(Aircraft.Props[j][i], 0.0F, -this.propPos[j], 0.0F);
            if(j == 1)
                hierMesh().chunkSetAngles(Aircraft.Props[j][i], 0.0F, this.propPos[j], 0.0F);
        }

    }

    public void hitProp(int i, int j, Actor actor)
    {
        if(i > this.FM.EI.getNum() - 1 || this.oldProp[i] == 2)
            return;
        if(isChunkAnyDamageVisible("Prop" + (i + 1)) || isChunkAnyDamageVisible("PropRot" + (i + 1)))
        {
            hierMesh().chunkVisible(Aircraft.Props[i + 1][0], false);
            hierMesh().chunkVisible(Aircraft.Props[i + 1][1], false);
            hierMesh().chunkVisible(Aircraft.Props[i + 1][2], true);
            this.oldProp[i + 1] = 2;
        }
        super.hitProp(i, j, actor);
    }

    public void update(float f)
    {
        World.cur().diffCur.Torque_N_Gyro_Effects = false;
        hierMesh().chunkVisible(Aircraft.Props[1][0], this.oldProp[1] == 0);
        hierMesh().chunkVisible(Aircraft.Props[1][1], this.oldProp[1] == 1);
        hierMesh().chunkVisible(Aircraft.Props[1][2], this.oldProp[1] == 2);
        super.update(f);
    }

    public void moveCockpitDoor(float f)
    {
        resetYPRmodifier();
        Aircraft.xyz[2] = -Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.65F);
        hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if(Config.isUSE_RENDER())
        {
            if(Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null)
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            setDoorSnd(f);
        }
    }

    static 
    {
        Class class1 = F_72A5.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "SuperBolt");
        Property.set(class1, "meshName", "3DO/Plane/F-72A-5(multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(class1, "yearService", 1945F);
        Property.set(class1, "yearExpired", 1949F);
        Property.set(class1, "FlightModel", "FlightModels/F-72A5.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitF_72A5.class
        });
        Property.set(class1, "LOSElevation", 1.1058F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 3, 3, 3, 3, 9, 9, 
            9, 9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_MGUN05", "_MGUN06", "_MGUN07", "_MGUN08", "_MGUN09", "_MGUN10", 
            "_MGUN11", "_MGUN12", "_MGUN13", "_MGUN14", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalDev01", "_ExternalDev02", 
            "_ExternalDev03", "_ExternalDev04"
        });
    }
}
