package com.maddox.il2.ai;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.game.Mission;
import com.maddox.il2.objects.air.A6M;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.B5N;
import com.maddox.il2.objects.air.BF_109;
import com.maddox.il2.objects.air.BF_110;
import com.maddox.il2.objects.air.B_17;
import com.maddox.il2.objects.air.B_24;
import com.maddox.il2.objects.air.B_25;
import com.maddox.il2.objects.air.B_29;
import com.maddox.il2.objects.air.D3A;
import com.maddox.il2.objects.air.FW_190;
import com.maddox.il2.objects.air.G4M;
import com.maddox.il2.objects.air.H8K;
import com.maddox.il2.objects.air.HE_111H2;
import com.maddox.il2.objects.air.Hurricane;
import com.maddox.il2.objects.air.IL_2;
import com.maddox.il2.objects.air.JU_88;
import com.maddox.il2.objects.air.KI_43;
import com.maddox.il2.objects.air.KI_46;
import com.maddox.il2.objects.air.KI_61;
import com.maddox.il2.objects.air.KI_84;
import com.maddox.il2.objects.air.ME_323;
import com.maddox.il2.objects.air.N1K;
import com.maddox.il2.objects.air.PE_2;
import com.maddox.il2.objects.air.PE_8;
import com.maddox.il2.objects.air.PE_8xyz;
import com.maddox.il2.objects.air.P_38;
import com.maddox.il2.objects.air.P_47;
import com.maddox.il2.objects.air.P_51;
import com.maddox.il2.objects.air.SBD;
import com.maddox.il2.objects.air.SPITFIRE;
import com.maddox.il2.objects.air.TA_152H1;
import com.maddox.il2.objects.air.TBF;
import com.maddox.il2.objects.air.TB_3;
import com.maddox.il2.objects.air.TEMPEST;
import com.maddox.il2.objects.air.TU_2;
import com.maddox.il2.objects.air.YAK;
import com.maddox.rts.Property;

public class Formation
{

    public static final void generate(Aircraft aaircraft[])
    {
        gen(aaircraft, WR);
    }

    private static final float scaleCoeff(Aircraft aircraft)
    {
        // TODO: Changed by western0221, "Straight In Landing" space wider, other Landing a bit wide.
        float fsi = 1.0F;
        aircraft.FM.AP.way.next();
        if(aircraft.FM.AP.way.curr().Action == 2)
        {
            if(aircraft.FM.AP.way.curr().waypointType == 104) fsi = 2.5F;  // Straight In
            else if(aircraft.FM.EI.getNum() > 4) fsi = 1.8F;
            else if(aircraft.FM.EI.engines[0].getType() == 2) fsi = 1.4F;  // Jet engine
            else fsi = 1.2F;
        }
        aircraft.FM.AP.way.prev();
        
        // TODO: By SAS~Storebror: New Formation Parameters +++
        float newScaleCoeff = aircraft.FM.getFormationScaleCoeff();
        if (Float.isNaN(newScaleCoeff)) newScaleCoeff = Property.floatValue(aircraft.getClass(), "FormationScaleCoeff", Float.NaN);
        if (!Float.isNaN(newScaleCoeff))
            return newScaleCoeff * fsi;
        // TODO: By SAS~Storebror: New Formation Parameters ---
        
        if(aircraft instanceof ME_323)
            return 5F * fsi; // TODO: Added "spacing" parameter by western0221
        if((aircraft instanceof PE_8xyz) || (aircraft instanceof PE_8) || (aircraft instanceof TB_3) || (aircraft instanceof B_17) || (aircraft instanceof B_29))
            return 3.5F * fsi; // TODO: Added "spacing" parameter by western0221
        if(aircraft instanceof TA_152H1)
            return 2.0F * fsi; // TODO: Added "spacing" parameter by western0221
        if(aircraft instanceof SBD)
            return 1.8F * fsi; // TODO: Added "spacing" parameter by western0221
        if(aircraft instanceof TBF)
            return 1.9F * fsi; // TODO: Added "spacing" parameter by western0221
        if(aircraft instanceof FW_190)
            return 1.4F * fsi; // TODO: Added "spacing" parameter by western0221
        
        // TODO: Changed by SAS~Storebror, increase distance in formation depending on number of engines
//        return (aircraft instanceof Scheme1) ? 1.2F : 2.2F;
        //return 1.2F * aircraft.FM.EI.getNum();
        if(aircraft.FM.EI.getNum() == 0)
            return 2.2F * fsi; // TODO: Added "spacing" parameter by western0221
        if(aircraft.FM.EI.getNum() < 2)
            return 1.2F * fsi; // TODO: Added "spacing" parameter by western0221
        if(aircraft.FM.EI.getNum() < 4)
            return 1.1F * (float) aircraft.FM.EI.getNum() * fsi; // TODO: Added "spacing" parameter by western0221
        if(aircraft.FM.EI.getNum() < 8)
            return 1.0F * (float) aircraft.FM.EI.getNum() * fsi; // TODO: Added "spacing" parameter by western0221
        return 0.9F * (float) aircraft.FM.EI.getNum() * fsi; // TODO: Added "spacing" parameter by western0221
    }

    public static final void gather(FlightModel flightmodel, byte formationType)
    {
        gather(flightmodel, formationType, flightmodel.Offset);
    }

    public static final void gather(FlightModel flightmodel, byte formationType, Vector3d vector3d)
    {
        gather(flightmodel, formationType, vector3d, Mission.curYear());
    }

    public static final void gather(FlightModel flightmodel, byte formationType, Vector3d vector3d, int year)
    {
        Aircraft aircraft = (Aircraft)flightmodel.actor;
        int numInGroup = ((Maneuver)flightmodel).Group.numInGroup(aircraft);
        float formationScale = flightmodel.formationScale;
        switch(formationType)
        {
        default:
            break;

        case F_DEFAULT:
            // TODO: By SAS~Storebror: New Formation Parameters +++
            byte newFormationType = aircraft.FM.getFormationDefault();
            if (newFormationType == F_DEFAULT) newFormationType = (byte)Property.intValue(aircraft.getClass(), "FormationDefault", F_DEFAULT);
            if (newFormationType != F_DEFAULT) {
                gather(flightmodel, newFormationType, vector3d);
                return;
            }
            // TODO: By SAS~Storebror: New Formation Parameters ---
            
            if((aircraft instanceof BF_109) || (aircraft instanceof BF_110) || (aircraft instanceof FW_190) || (aircraft instanceof SPITFIRE) && year > 1941 || (aircraft instanceof Hurricane) && year > 1941 || (aircraft instanceof P_38) || (aircraft instanceof P_47) || (aircraft instanceof P_51) || (aircraft instanceof TEMPEST))
            {
                gather(flightmodel, F_FINGERFOUR, vector3d);
                return;
            }
            if((aircraft instanceof IL_2) || (aircraft instanceof YAK) || (aircraft instanceof PE_2) || (aircraft instanceof TU_2))
            {
                gather(flightmodel, F_VIC, vector3d);
                return;
            }
            if((aircraft instanceof A6M) || (aircraft instanceof B5N) || (aircraft instanceof D3A) || (aircraft instanceof G4M) || (aircraft instanceof KI_43) || (aircraft instanceof KI_46) || (aircraft instanceof KI_84) || (aircraft instanceof N1K) || (aircraft instanceof H8K) || (aircraft instanceof KI_61))
            {
                gather(flightmodel, F_VIC, vector3d);
                return;
            }
            if((aircraft instanceof JU_88) || (aircraft instanceof HE_111H2))
            {
                gather(flightmodel, F_DIAMOND, vector3d);
                return;
            }
            if(aircraft instanceof ME_323)
            {
                gather(flightmodel, F_DIAMOND, vector3d);
                return;
            }
            if((aircraft instanceof PE_8xyz) || (aircraft instanceof PE_8) || (aircraft instanceof B_17) || (aircraft instanceof B_25) || (aircraft instanceof B_29))
            {
                gather(flightmodel, F_VIC, vector3d);
                return;
            } else
            {
                gather(flightmodel, F_ECHELONRIGHT, vector3d);
                return;
            }

        case F_PREVIOUS:
            gather(flightmodel, flightmodel.formationType, vector3d);
            return;

        case F_ECHELONRIGHT:
            flightmodel.formationType = formationType;
            vector3d.set(25D, 25D, 0.0D);
            break;

        case F_ECHELONLEFT:
            flightmodel.formationType = formationType;
            vector3d.set(25D, -25D, 0.0D);
            break;

        case F_LINEABREAST:
            flightmodel.formationType = formationType;
            if(numInGroup == 0)
                vector3d.set(25D, 75D, 0.0D);
            else
                vector3d.set(1.0D, 33D, 0.0D);
            break;

        case F_LINEASTERN:
            flightmodel.formationType = formationType;
            if(numInGroup == 0)
                vector3d.set(120D, 0.0D, 15D);
            else
                vector3d.set(80D, 0.0D, 10D);
            vector3d.scale(formationScale);
            return;

        case F_VIC:
            flightmodel.formationType = formationType;
            switch(numInGroup)
            {
            case 0:
                vector3d.set(55D, 55D, 0.0D);
                break;

            case 1:
                vector3d.set(25D, 25D, 0.0D);
                break;

            case 2:
                vector3d.set(0.0D, -50D, 0.0D);
                break;

            case 3:
                vector3d.set(25D, -25D, 0.0D);
                break;
            }
            break;

        case F_FINGERFOUR:
            flightmodel.formationType = formationType;
            switch(numInGroup)
            {
            case 0:
                vector3d.set(25D, 25D, 0.0D);
                break;

            case 1:
                vector3d.set(15D, 30D, 0.0D);
                break;

            case 2:
                vector3d.set(25D, -60D, 0.0D);
                break;

            case 3:
                vector3d.set(15D, -20D, 0.0D);
                break;
            }
            break;

        case F_DIAMOND:
            flightmodel.formationType = formationType;
            switch(numInGroup)
            {
            case 0:
                vector3d.set(75D, 30D, 0.0D);
                break;

            case 1:
                vector3d.set(25D, 25D, 0.0D);
                break;

            case 2:
                vector3d.set(0.0D, -50D, 0.0D);
                break;

            case 3:
                vector3d.set(25D, 25D, 0.0D);
                break;
            }
            break;
        case F_JAVELIN:
            switch (numInGroup)
            {
            case 0: 
                vector3d.set(75.0D, 30.0D, 0.0D);
              break;
            case 1: 
                vector3d.set(25.0D, 25.0D, -10.0D);
              break;
            case 2: 
                vector3d.set(0.0D, -50.0D, 20.0D);
              break;
            case 3: 
                vector3d.set(25.0D, 25.0D, -10.0D);
            }
            break;
        case F_LINE:
            flightmodel.formationType = formationType;
            if(numInGroup == 0)
                vector3d.set(120D, 0.0D, 0.0D);
            else
                vector3d.set(80D, 0.0D, 0.0D);
            vector3d.scale(formationScale);
            return;
        case F_LINEASTERNLONG:
            flightmodel.formationType = formationType;
            switch(numInGroup)
            {
            case 0:
                vector3d.set(-360D, 0.0D, 0.0D);
                break;

            case 1:
                vector3d.set(240D, 0.0D, 0.0D);
                break;

            case 2:
                vector3d.set(120D, 0.0D, 0.0D);
                break;

            case 3:
                vector3d.set(0.0D, 0.0D, 0.0D);
                break;
            }
            vector3d.scale(formationScale);
            return;
        case F_LINEUPSTACKED:
            flightmodel.formationType = formationType;
            if(numInGroup == 0)
                vector3d.set(120D, 0.0D, -5D);
            else
                vector3d.set(280D, 0.0D, -20D);
            vector3d.scale(formationScale);
            return;
        }
        vector3d.scale(formationScale * scaleCoeff(aircraft));
    }

    public static final void leaderOffset(FlightModel flightmodel, byte formationType, Vector3d vector3d)
    {
        Aircraft aircraft = (Aircraft)flightmodel.actor;
        Wing wing = (Wing)flightmodel.actor.getOwner();
        int wingSquadIndex;
        if(wing != null)
            wingSquadIndex = wing.indexInSquadron();
        else
            wingSquadIndex = 0;
        
        // TODO: By SAS~Storebror: New Formation Parameters +++
        boolean offsetDone = false;
        if (aircraft.FM.isLeaderOffsetValid()) {
            vector3d.set(aircraft.FM.getLeaderOffset(wingSquadIndex));
            offsetDone = true;
        } else {
            do {
                try {
                    float leaderOffsetX = Property.floatValue(aircraft.getClass(), "FormationOffset" + (wingSquadIndex + 1) + "X", Float.NaN);
                    if (Float.isNaN(leaderOffsetX))
                        break;
                    float leaderOffsetY = Property.floatValue(aircraft.getClass(), "FormationOffset" + (wingSquadIndex + 1) + "Y", Float.NaN);
                    if (Float.isNaN(leaderOffsetY))
                        break;
                    float leaderOffsetZ = Property.floatValue(aircraft.getClass(), "FormationOffset" + (wingSquadIndex + 1) + "Z", Float.NaN);
                    if (Float.isNaN(leaderOffsetZ))
                        break;
                    vector3d.set(leaderOffsetX, leaderOffsetY, leaderOffsetZ);
                    offsetDone = true;
                } catch (Exception e) {
                }
            } while (false);
        }
        if (!offsetDone) {
        // TODO: By SAS~Storebror: New Formation Parameters ---
        
            if((aircraft instanceof B_17) || (aircraft instanceof B_24) || (aircraft instanceof B_29)) {
                switch(wingSquadIndex)
                {
                case 0:
                    vector3d.set(300D, -150D, 0.0D);
                    break;
    
                case 1:
                    vector3d.set(100D, -80D, -30D);
                    break;
    
                case 2:
                    vector3d.set(100D, 80D, 25D);
                    break;
    
                case 3:
                    vector3d.set(200D, 0.0D, 50D);
                    break;
                }
            } else {
                switch(wingSquadIndex)
                {
                default:
                    break;
    
                case 0:
                    vector3d.set(300D, -150D, 0.0D);
                    break;
    
                case 1:
                    if(formationType != 2)
                        vector3d.set(100D, 100D, 0.0D);
                    else
                        vector3d.set(200D, 200D, 0.0D);
                    break;
    
                case 2:
                    if(formationType != 3 && formationType != 6)
                        vector3d.set(150D, -150D, 0.0D);
                    else
                        vector3d.set(210D, -210D, 0.0D);
                    break;
    
                case 3:
                    if(formationType != 5)
                        vector3d.set(150D, 0.0D, 0.0D);
                    else
                        vector3d.set(300D, 0.0D, 0.0D);
                    break;
                }
            }
        }
        vector3d.scale(0.7D * (double)scaleCoeff(aircraft));
    }

    private static final void gen(Aircraft aaircraft[], Vector3f vector3f)
    {
        dd.set(vector3f);
        aaircraft[0].pos.getAbsOrient().transform(dd);
        int j = 0;
        for(int i = 1; i < aaircraft.length; i++)
            if(Actor.isValid(aaircraft[i]))
            {
                aaircraft[i].FM.Offset.set(vector3f);
                aaircraft[i].FM.Leader = aaircraft[j].FM;
                aaircraft[j].FM.Wingman = aaircraft[i].FM;
                aaircraft[j].pos.getAbs(Pd);
                Pd.sub(dd);
                aaircraft[i].pos.setAbs(Pd);
                j = i;
            }

    }

    public static final byte F_DEFAULT = 0;
    public static final byte F_PREVIOUS = 1;
    public static final byte F_ECHELONRIGHT = 2;
    public static final byte F_ECHELONLEFT = 3;
    public static final byte F_LINEABREAST = 4;
    public static final byte F_LINEASTERN = 5;
    public static final byte F_VIC = 6;
    public static final byte F_FINGERFOUR = 7;
    public static final byte F_DIAMOND = 8;
    public static final byte F_JAVELIN = 9;
    public static final byte F_LINE = 10;
    public static final byte F_LINEASTERNLONG = 11;
    public static final byte F_LINEUPSTACKED = 12;
    // TODO: 4.14 backport , idiotically they've thrown in F_JAVELIN at index 9 !!??!? +++
    // TODO: 4.14 backport ---
    private static final Vector3f WR = new Vector3f(100F, 100F, 0.0F);
    private static final Vector3d dd = new Vector3d();
    private static final Point3d Pd = new Point3d();

}
