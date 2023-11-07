package com.maddox.il2.objects.air;

import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.rts.Property;

public class J4M2 extends J4Mx
    implements TypeStormovik
{

    public J4M2()
    {
        oldctl = -1F;
        curctl = -1F;
        bChangedPit = true;
        arrestor2 = 0.0F;
        arrestor = 0.0F;
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

    public void moveArrestorHook(float f)
    {
        resetYPRmodifier();
        Aircraft.xyz[0] = -1.545F * f;
        Aircraft.ypr[1] = -arrestor;
        hierMesh().chunkSetLocate("Hook1_D0", Aircraft.xyz, Aircraft.ypr);
    }

    public void update(float f)
    {
        super.update(f);
        float f1 = this.FM.CT.getArrestor();
        float f2 = 81F * f1 * f1 * f1 * f1 * f1 * f1 * f1;
        if(f1 > 0.01F)
            if(this.FM.Gears.arrestorVAngle != 0.0F)
            {
                arrestor = Aircraft.cvt(this.FM.Gears.arrestorVAngle, -f2, f2, -f2, f2);
                moveArrestorHook(f1);
                if(this.FM.Gears.arrestorVAngle >= -81F);
            } else
            {
                float f3 = 58F * this.FM.Gears.arrestorVSink;
                if(f3 > 0.0F && this.FM.getSpeedKMH() > 60F)
                    Eff3DActor.New(this, this.FM.Gears.arrestorHook, null, 1.0F, "3DO/Effects/Fireworks/04_Sparks.eff", 0.1F);
                arrestor += f3;
                if(arrestor > f2)
                    arrestor = f2;
                if(arrestor < -f2)
                    arrestor = -f2;
                moveArrestorHook(f1);
            }
        if(FM.AS.isMaster())
            if(curctl == -1F)
            {
                curctl = oldctl = FM.EI.engines[0].getControlThrottle();
            } else
            {
                curctl = FM.EI.engines[0].getControlThrottle();
                if((curctl - oldctl) / f > 3F && FM.EI.engines[0].getRPM() < 2400F && FM.EI.engines[0].getStage() == 6 && World.Rnd().nextFloat() < 0.25F)
                    FM.AS.hitEngine(this, 0, 100);
                if((curctl - oldctl) / f < -3F && FM.EI.engines[0].getRPM() < 2400F && FM.EI.engines[0].getStage() == 6)
                {
                    if(World.Rnd().nextFloat() < 0.25F && (FM instanceof RealFlightModel) && ((RealFlightModel)FM).isRealMode())
                        FM.EI.engines[0].setEngineStops(this);
                    if(World.Rnd().nextFloat() < 0.75F && (FM instanceof RealFlightModel) && ((RealFlightModel)FM).isRealMode())
                        FM.EI.engines[0].setKillCompressor(this);
                }
                oldctl = curctl;
            }
        if(Config.isUSE_RENDER() && FM.AS.isMaster())
            if(FM.EI.engines[0].getPowerOutput() > 0.8F && FM.EI.engines[0].getStage() == 6)
            {
                if(FM.EI.engines[0].getPowerOutput() > 0.95F)
                    FM.AS.setSootState(this, 0, 3);
                else
                    FM.AS.setSootState(this, 0, 2);
            } else
            {
                FM.AS.setSootState(this, 0, 0);
            }
        super.update(f);
    }

    public void doSetSootState(int i, int j)
    {
        for(int k = 0; k < 2; k++)
        {
            if(this.FM.AS.astateSootEffects[i][k] != null)
                Eff3DActor.finish(this.FM.AS.astateSootEffects[i][k]);
            this.FM.AS.astateSootEffects[i][k] = null;
        }

        switch(j)
        {
        case 1:
            this.FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "ES_01"), null, 1.8F, "3DO/Effects/Aircraft/BlackSmallTSPD.eff", -1F);
            this.FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "ES_02"), null, 1.8F, "3DO/Effects/Aircraft/BlackSmallTSPD.eff", -1F);
            break;

        case 3:
            this.FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "EF_01"), null, 1.8F, "3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1F);
            // fall through

        case 2:
            this.FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "EF_01"), null, 1.4F, "3DO/Effects/Aircraft/TurboZippo.eff", -1F);
            break;

        case 5:
            this.FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "EF_01"), null, 1.5F, "3DO/Effects/Aircraft/TurboJRD1100F.eff", -1F);
            // fall through

        case 4:
            this.FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "EF_01"), null, 1.0F, "3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1F);
            break;
        }
    }

    private float oldctl;
    private float curctl;
    private float arrestor;
    public boolean bChangedPit;
    protected float arrestor2;

    static 
    {
        Class class1 = J4M2.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "J4M");
        Property.set(class1, "meshName", "3DO/Plane/J4M2/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar01());
        Property.set(class1, "meshName_ja", "3DO/Plane/J4M2/hier.him");
        Property.set(class1, "PaintScheme_ja", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1945F);
        Property.set(class1, "yearExpired", 1947F);
        Property.set(class1, "FlightModel", "FlightModels/J4M2.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitJ4M2.class
        });
        Property.set(class1, "LOSElevation", 1.0151F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 1, 3, 3, 3, 3, 3, 3, 2, 
            2, 2, 2, 2, 2, 2, 2, 2, 2, 9, 
            9, 9, 9, 3, 3, 3, 3, 3, 3, 3, 
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
            3, 9, 9, 1, 1, 0, 0, 0, 0, 9, 
            9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_Cannon01", "_Cannon02", "_Cannon03", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06", "_ExternalRock07", 
            "_ExternalRock08", "_ExternalRock09", "_ExternalRock10", "_ExternalRock11", "_ExternalRock12", "_ExternalRock13", "_ExternalRock14", "_ExternalRock15", "_ExternalRock16", "_ExternalDev01", 
            "_ExternalDev02", "_ExternalDev11", "_ExternalDev12", "_ExternalBomb07", "_ExternalBomb08", "_ExternalBomb09", "_ExternalBomb10", "_ExternalBomb11", "_ExternalBomb12", "_ExternalBomb13", 
            "_ExternalBomb14", "_ExternalBomb15", "_ExternalBomb16", "_ExternalBomb17", "_ExternalBomb18", "_ExternalBomb19", "_ExternalBomb20", "_ExternalBomb21", "_ExternalBomb22", "_ExternalBomb23", 
            "_ExternalBomb24", "_ExternalDev03", "_ExternalDev04", "_Cannon04", "_Cannon05", "_MGun01", "_MGun02", "_MGun03", "_MGun04", "_ExternalDev05", 
            "_ExternalDev06"
        });
    }
}
