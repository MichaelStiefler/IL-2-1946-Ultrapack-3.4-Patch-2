package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class YAK_17 extends YAK
{
    public float getEyeLevelCorrection()
    {
        return 0.1F;
    }

    public static void moveGear(HierMesh hiermesh, float f, float f1, float f2)
    {
        hiermesh.chunkSetAngles("GearC99_D0", 0.0F, -85F * f2, 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, Aircraft.cvt(f, 0.1F, 0.6F, 0.0F, -85F), 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(f, 0.1F, 0.6F, 0.0F, -83.5F), 0.0F);
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, Aircraft.cvt(f1, 0.4F, 0.9F, 0.0F, -85F), 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(f1, 0.4F, 0.9F, 0.0F, -83.5F), 0.0F);
    }

    protected void moveGear(float f, float f1, float f2)
    {
        moveGear(hierMesh(), f, f1, f2);
    }

    // ************************************************************************************************
    // Gear code for backward compatibility, older base game versions don't indepently move their gears
    public static void moveGear(HierMesh hiermesh, float gearPos) {
        YAK_17.moveGear(hiermesh, gearPos, gearPos, gearPos); // re-route old style function calls to new code
    }

    protected void moveGear(float gearPos) {
        YAK_17.moveGear(this.hierMesh(), gearPos, gearPos, gearPos);
    }
    // ************************************************************************************************

    public void moveSteering(float f)
    {
        hierMesh().chunkSetAngles("GearC2_D0", 0.0F, -f, 0.0F);
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xx"))
        {
            if(s.startsWith("xxCannon01"))
            {
                if(getEnergyPastArmor(9.8F, shot) > 0.0F)
                {
                    debuggunnery("Armament: Cannon (0) Disabled..");
                    this.FM.AS.setJamBullets(1, 0);
                    getEnergyPastArmor(World.Rnd().nextFloat(0.5F, 23.325F), shot);
                }
                return;
            }
            if(s.startsWith("xxCannon02"))
            {
                if(getEnergyPastArmor(9.8F, shot) > 0.0F)
                {
                    debuggunnery("Armament: Cannon (0) Disabled..");
                    this.FM.AS.setJamBullets(1, 1);
                    getEnergyPastArmor(World.Rnd().nextFloat(0.5F, 23.325F), shot);
                }
                return;
            }
        }
        super.hitBone(s, shot, point3d);
    }

    public void update(float f)
    {
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
        super.update(f);
    }

    static 
    {
        Class class1 = YAK_17.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Yak");
        Property.set(class1, "meshName", "3DO/Plane/Yak-17(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(class1, "meshName_ru", "3DO/Plane/Yak-17(Multi1)/hier.him");
        Property.set(class1, "PaintScheme_ru", new PaintSchemeFCSPar05());
        Property.set(class1, "yearService", 1946F);
        Property.set(class1, "yearExpired", 1948.5F);
        Property.set(class1, "FlightModel", "FlightModels/Yak-17.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitYAK_15.class
        });
        Property.set(class1, "LOSElevation", 1.0989F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            1, 1
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02"
        });
    }
}
