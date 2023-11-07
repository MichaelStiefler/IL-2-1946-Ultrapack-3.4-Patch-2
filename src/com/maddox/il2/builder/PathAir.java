package com.maddox.il2.builder;

import com.maddox.il2.ai.Regiment;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.PaintScheme;
import com.maddox.il2.objects.vehicles.planes.PlaneGeneric;

public class PathAir extends Path
{

    public PlaneGeneric getSpawnPoint(int i)
    {
        if(i < 0 || i > 3)
            return null;
        if(spawnPointPlane[i] == null && spawnPointPlaneName[i] != null)
        {
            Actor actor = Actor.getByName(spawnPointPlaneName[i]);
            if(actor instanceof PlaneGeneric)
            {
                PlaneGeneric planegeneric = (PlaneGeneric)actor;
                spawnPointPlane[i] = planegeneric;
            }
        }
        return spawnPointPlane[i];
    }

    public void setSpawnPoint(int i, PlaneGeneric planegeneric)
    {
        spawnPointPlane[i] = planegeneric;
        if(planegeneric != null)
            spawnPointPlaneName[i] = planegeneric.name();
        else
            spawnPointPlaneName[i] = null;
    }

    public void setSpawnPointPlaneName(int i, String s)
    {
        spawnPointPlaneName[i] = s;
        if(s != null)
        {
            PlMisAir plmisair = (PlMisAir)Plugin.getPlugin("MisAir");
            plmisair.wSpawnPointLabel[i].cap.set(PlMisAir.i18n("IndicatedByTheYellowLine"));
        }
    }

    public void computeTimes()
    {
        computeTimes(true);
    }

    public void computeTimes(boolean flag)
    {
        int i = points();
        if(i == 0)
            return;
        PAir pair = (PAir)point(0);
        PlMisAir plmisair = (PlMisAir)Plugin.getPlugin("MisAir");
        for(int j = 1; j < i; j++)
        {
            PAir pair1 = (PAir)point(j);
            double d = pair1.speed;
            if(pair1.type() == 2)
                d = plmisair.type[_iType].item[_iItem].speedRunway;
            if(d == 0.0D)
                d = plmisair.type[_iType].item[_iItem].speedRunway;
            double d1 = pair.pos.getAbsPoint().distance(pair1.pos.getAbsPoint());
            d *= 0.27777777777777779D;
            double d2 = d1 / d;
            pair1.time = pair.time + d2;
            pair = pair1;
        }

        if(flag)
            Plugin.builder.doUpdateSelector();
    }

    public ResSquadron squadron()
    {
        return resSquadron;
    }

    public Regiment regiment()
    {
        return squadron().regiment();
    }

    public void setName(String s)
    {
        ResSquadron ressquadron = ResSquadron.New(s.substring(0, s.length() - 1));
        if(ressquadron != resSquadron)
        {
            if(resSquadron != null)
                resSquadron.detach(this);
            resSquadron = ressquadron;
            resSquadron.attach(this);
        }
        super.setName(s);
        updateTypedName();
    }

    public void updateTypedName()
    {
        PlMisAir plmisair = (PlMisAir)Plugin.getPlugin("MisAir");
        PaintScheme paintscheme = Aircraft.getPropertyPaintScheme(plmisair.type[_iType].item[_iItem].clazz, resSquadron.regiment().country());
        if(paintscheme != null)
            typedName = paintscheme.typedName(plmisair.type[_iType].item[_iItem].clazz, resSquadron.regiment(), iSquadron, iWing, 0);
        else
            typedName = "UNKNOWN";
    }

    public void destroy()
    {
        if(Actor.isValid(resSquadron))
            resSquadron.detach(this);
        super.destroy();
    }

    public PathAir(Pathes pathes, int i, int j)
    {
        super(pathes);
        typedName = "UNKNOWN";
        iSquadron = 0;
        iWing = 0;
        bOnlyAI = false;
        planes = 1;
        fuel = 100;
        skill = 1;
        skins = new String[4];
        noseart = new String[4];
        pilots = new String[4];
        weapons = "default";
        bParachute = true;
        spawnPointPlane = new PlaneGeneric[4];
        spawnPointPlaneName = new String[4];
        _iType = i;
        _iItem = j;
    }

    public String typedName;
    public String sRegiment;
    public int iRegiment;
    public String sCountry;
    public int iSquadron;
    public int iWing;
    public boolean bOnlyAI;
    public int planes;
    public int fuel;
    public int skill;
    public int skills[] = {
        1, 1, 1, 1
    };
    public String skins[];
    public String noseart[];
    public String pilots[];
    public boolean bNumberOn[] = {
        true, true, true, true
    };
    public String weapons;
    public boolean bParachute;
    public int _iType;
    public int _iItem;
    private PlaneGeneric spawnPointPlane[];
    private String spawnPointPlaneName[];
    private ResSquadron resSquadron;
}
