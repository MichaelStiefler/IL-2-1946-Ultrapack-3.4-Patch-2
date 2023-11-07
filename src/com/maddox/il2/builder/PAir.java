package com.maddox.il2.builder;

import com.maddox.JGP.Point3d;
import com.maddox.il2.engine.*;
import com.maddox.rts.Property;

public class PAir extends PPoint
{

    public void setType(int i)
    {
        type = i;
        setIcon();
    }

    public int type()
    {
        return type;
    }

    public Actor getTarget()
    {
        if(Actor.isValid(target))
            return target;
        if(sTarget != null)
        {
            Actor actor = Actor.getByName(sTarget);
            if(Actor.isValid(actor))
            {
                if(actor instanceof Path)
                {
                    Path path = (Path)actor;
                    if(iTarget >= path.points())
                        iTarget = path.points() - 1;
                    if(iTarget < 0)
                    {
                        iTarget = 0;
                        sTarget = null;
                        target = null;
                    } else
                    {
                        PPoint ppoint = path.point(iTarget);
                        target = ppoint;
                    }
                } else
                {
                    target = actor;
                }
                return target;
            }
            sTarget = null;
        }
        target = null;
        return null;
    }

    public void setTarget(Actor actor)
    {
        target = actor;
        if(target != null)
            if(type != 1 && type != 2)
            {
                if(target instanceof PAir)
                    setType(0);
                else
                    setType(3);
            } else
            if(target instanceof PPoint)
            {
                if(target.getOwner() instanceof PathChief)
                {
                    PathChief pathchief = (PathChief)target.getOwner();
                    if(!PlMisChief.isAirport(pathchief._iType, pathchief._iItem))
                        target = null;
                } else
                {
                    target = null;
                }
            } else
            if(!"true".equals(Property.stringValue(target.getClass(), "IsAirport", (String)null)))
                target = null;
    }

    public void destroy()
    {
        target = null;
        sTarget = null;
        Path path = (Path)getOwner();
        super.destroy();
        if(path != null)
            path.computeTimes();
    }

    public PAir(Path path, PPoint ppoint, Mat mat, Point3d point3d, int i, double d, 
            double d1, PAir pair)
    {
        super(path, ppoint, mat, point3d);
        target = null;
        sTarget = null;
        iTarget = 0;
        bRadioSilence = false;
        waypointType = 0;
        cycles = 0;
        orient = 0.0F;
        baseSize = 5;
        altDifference = 0;
        targetTrigger = 0;
        delayTimer = 0;
        takeoffSpacing = 20;
        ignoreAlt = 0;
        formation = 0;
        setType(i);
        height = d;
        speed = d1;
        resetPAirExtra();
        if(i == 1 && pair != null)
        {
            waypointType = 4;
            takeoffSpacing = pair.takeoffSpacing;
        }
        if(pair != null)
            formation = pair.formation;
        path.computeTimes();
    }

    public void resetPAirExtra()
    {
        waypointType = 0;
        cycles = 0;
        orient = 0.0F;
        delayTimer = 0;
        takeoffSpacing = 20;
        ignoreAlt = 0;
        baseSize = 5;
        altDifference = 0;
        formation = 0;
    }

    public PAir(Path path, PPoint ppoint, Point3d point3d, int i, double d, double d1, PAir pair)
    {
        this(path, ppoint, (Mat)null, point3d, i, d, d1, pair);
    }

    public PAir(Path path, PPoint ppoint, String s, Point3d point3d, int i, double d, 
            double d1)
    {
        this(path, ppoint, IconDraw.get(s), point3d, i, d, d1, null);
    }

    private void setIcon()
    {
        String s = null;
        switch(type)
        {
        case 0: // '\0'
            s = "normfly";
            break;

        case 1: // '\001'
            s = "takeoff";
            break;

        case 2: // '\002'
            s = "landing";
            break;

        case 3: // '\003'
            s = "gattack";
            break;

        default:
            return;
        }
        icon = IconDraw.get("icons/" + s + ".mat");
    }

    public static final int NORMFLY = 0;
    public static final int TAKEOFF = 1;
    public static final int LANDING = 2;
    public static final int GATTACK = 3;
    public static final String types[] = {
        "NORMFLY", "TAKEOFF", "LANDING", "GATTACK"
    };
    public static final String formations[] = {
        "Default", "EchelonRight", "EchelonLeft", "LineAbreast", "LineAstern", "Vic", "FingerFour", "Diamond", "Javelin", "Line", "LineAsternLong", "LineUpStacked"
    };
    private int type;
    public double height;
    public double speed;
    private Actor target;
    public String sTarget;
    public int iTarget;
    public boolean bRadioSilence;
    public int waypointType;
    public int cycles;
    public float orient;
    public int baseSize;
    public int altDifference;
    public int targetTrigger;
    public int delayTimer;
    public int takeoffSpacing;
    public int ignoreAlt;
    public int formation;
    public static final int WP_TAKEOFF_NORMAL_DELAY = 1;
    public static final int WP_TAKEOFF_PAIRS = 2;
    public static final int WP_TAKEOFF_LINE = 3;
    public static final int WP_TAKEOFF_TAXI = 4;
    public static final int WP_TAKEOFF_TAXI_FSP = 5;
    public static final int WP_LANDING_RIGHT = 101;
    public static final int WP_LANDING_SHORTLEFT = 102;
    public static final int WP_LANDING_SHORTRIGHT = 103;
    public static final int WP_LANDING_STRAIGHTIN = 104;
    public static final int WP_NORMFLY_CAP_TRIANGLE = 401;
    public static final int WP_NORMFLY_CAP_SQUARE = 402;
    public static final int WP_NORMFLY_CAP_PENTAGON = 403;
    public static final int WP_NORMFLY_CAP_HEXAGON = 404;
    public static final int WP_NORMFLY_CAP_RANDOM = 405;
    public static final int WP_NORMFLY_CAP_ONPATROL = 406;
    public static final int WP_NORMFLY_ART_SPOT = 407;

}
