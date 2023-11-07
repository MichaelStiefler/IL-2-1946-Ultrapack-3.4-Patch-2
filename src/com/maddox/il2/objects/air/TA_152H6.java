package com.maddox.il2.objects.air;

import com.maddox.il2.engine.Actor;
import com.maddox.rts.Property;

public class TA_152H6 extends TA_152NEW_WeaponLayout1 // TA_152H_Paulus
    implements TypeFighterAceMaker
{
    public void update(float f) {
        this.updateAfterburner();
        super.update(f);
    }

    protected void moveFan(float f)
    {
        hierMesh().chunkFind(Aircraft.Props[1][0]);
        SecondProp = 1;
        int i = 0;
        for(int j = 0; j < (SecondProp == 1 ? 2 : 1); j++)
        {
            if(oldProp[j] < 2)
            {
                i = Math.abs((int)(FM.EI.engines[0].getw() * 0.06F));
                if(i >= 1)
                    i = 1;
                if(i != oldProp[j] && hierMesh().isChunkVisible(Aircraft.Props[j][oldProp[j]]))
                {
                    hierMesh().chunkVisible(Aircraft.Props[j][oldProp[j]], false);
                    oldProp[j] = i;
                    hierMesh().chunkVisible(Aircraft.Props[j][i], true);
                }
            }
            if(i == 0)
            {
                propPos[j] = (propPos[j] + 57.3F * FM.EI.engines[0].getw() * f) % 360F;
            } else
            {
                float f1 = 57.3F * FM.EI.engines[0].getw();
                f1 %= 2880F;
                f1 /= 2880F;
                if(f1 <= 0.5F)
                    f1 *= 2.0F;
                else
                    f1 = f1 * 2.0F - 2.0F;
                f1 *= 1200F;
                propPos[j] = (propPos[j] + f1 * f) % 360F;
            }
            if(j == 0)
                hierMesh().chunkSetAngles(Aircraft.Props[j][i], 0.0F, propPos[j], 0.0F);
            else
                hierMesh().chunkSetAngles(Aircraft.Props[j][i], 0.0F, propPos[j] - 20F, 0.0F);
        }

    }

    public void hitProp(int i, int j, Actor actor)
    {
        if(i > FM.EI.getNum() - 1 || oldProp[i] == 2)
            return;
        if((isChunkAnyDamageVisible("Prop" + (i + 1)) || isChunkAnyDamageVisible("PropRot" + (i + 1))) && SecondProp == 1)
        {
            hierMesh().chunkVisible(Aircraft.Props[i + 1][0], false);
            hierMesh().chunkVisible(Aircraft.Props[i + 1][1], false);
            hierMesh().chunkVisible(Aircraft.Props[i + 1][2], true);
        }
        super.hitProp(i, j, actor);
    }

    protected int SecondProp;

    static 
    {
        Class class1 = TA_152H6.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Ta.152");
        Property.set(class1, "meshName", "3DO/Plane/Ta-152H-5/hierCRP.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1945.5F);
        Property.set(class1, "yearExpired", 1948F);
        Property.set(class1, "FlightModel", "FlightModels/Ta-152H-6.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitTA_152H6.class
        });
        Property.set(class1, "LOSElevation", 0.764106F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 1, 1, 1, 1, 9, 9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON03", "_CANNON04", "_CANNON05", "_CANNON06", "_ExternalDev01", "_ExternalDev02"
        });
    }
}
