package com.maddox.il2.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import com.maddox.JGP.Color4f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3f;
import com.maddox.gwindow.GCaption;
import com.maddox.gwindow.GNotifyListener;
import com.maddox.gwindow.GWindow;
import com.maddox.gwindow.GWindowButton;
import com.maddox.gwindow.GWindowCheckBox;
import com.maddox.gwindow.GWindowComboControl;
import com.maddox.gwindow.GWindowDialogClient;
import com.maddox.gwindow.GWindowEditControl;
import com.maddox.gwindow.GWindowLabel;
import com.maddox.gwindow.GWindowMenu;
import com.maddox.gwindow.GWindowMenuItem;
import com.maddox.il2.ai.Airport;
import com.maddox.il2.ai.Army;
import com.maddox.il2.ai.Regiment;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.BmpUtils;
import com.maddox.il2.engine.Camera3D;
import com.maddox.il2.engine.GUIRenders;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.LightEnvXY;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.engine.Render;
import com.maddox.il2.engine.Renders;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.game.I18N;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.gui.GUIAirArming;
import com.maddox.il2.objects.ActorSimpleHMesh;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.PaintScheme;
import com.maddox.rts.Finger;
import com.maddox.rts.HomePath;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.HotKeyCmdEnv;
import com.maddox.rts.LDRres;
import com.maddox.rts.ObjIO;
import com.maddox.rts.Property;
import com.maddox.rts.RTSConf;
import com.maddox.rts.SectFile;
import com.maddox.util.HashMapInt;
import com.maddox.util.NumberTokenizer;

public class PlMisAir extends Plugin
{
    static class Country
    {

        public String name;
        public String i18nName;

        Country()
        {
        }
    }

    class _Render3D extends Render
    {

        public void preRender()
        {
            checkMesh(planeIndx);
            if(Actor.isValid(actorMesh[planeIndx]))
            {
                if(animateMeshA[planeIndx] != 0.0F || animateMeshT[planeIndx] != 0.0F)
                {
                    actorMesh[planeIndx].pos.getAbs(_orient);
                    _orient.set(_orient.azimut() + animateMeshA[planeIndx] * Main3D.cur3D().guiManager.root.deltaTimeSec, _orient.tangage() + animateMeshT[planeIndx] * Main3D.cur3D().guiManager.root.deltaTimeSec, 0.0F);
                    _orient.wrap360();
                    actorMesh[planeIndx].pos.setAbs(_orient);
                    actorMesh[planeIndx].pos.reset();
                }
                actorMesh[planeIndx].draw.preRender(actorMesh[planeIndx]);
            }
        }

        public void render()
        {
            if(Actor.isValid(actorMesh[planeIndx]))
            {
                Render.prepareStates();
                actorMesh[planeIndx].draw.render(actorMesh[planeIndx]);
            }
        }

        int planeIndx;

        public _Render3D(Renders renders1, float f)
        {
            super(renders1, f);
            planeIndx = _planeIndx;
//            setClearColor(GWindowLookAndFeel.getUIBackgroundColor());
            setClearColor(new Color4f(0.5F, 0.78F, 0.92F, 1.0F));
            useClearStencil(true);
        }
    }

    class ViewItem extends GWindowMenuItem
    {

        public void execute()
        {
            bChecked = !bChecked;
            viewType(indx);
        }

        int indx;

        public ViewItem(int i, GWindowMenu gwindowmenu, String s, String s1)
        {
            super(gwindowmenu, s, s1);
            indx = i;
        }
    }

    public static class DefaultArmy
    {

        public void save(PathAir pathair)
        {
            iRegiment = pathair.iRegiment;
            iSquadron = pathair.iSquadron;
            iWing = pathair.iWing;
            sCountry = pathair.sCountry;
        }

        public void load(PathAir pathair)
        {
            pathair.iRegiment = iRegiment;
            pathair.iSquadron = iSquadron;
            pathair.iWing = iWing;
            pathair.sCountry = sCountry;
        }

        public int iRegiment;
        public int iSquadron;
        public int iWing;
        public String sCountry;

        public DefaultArmy()
        {
        }
    }

    public static class Type
    {

        public String name;
        public Item item[];

        public Type(String s, Item aitem[])
        {
            name = s;
            item = aitem;
        }
    }

    public static class Item
    {

        public String name;
        public Class clazz;
        public int army;
        public boolean bEnablePlayer;
        public double speedMin;
        public double speedMax;
        public double speedRunway;

        public Item(String s, Class class1, int i)
        {
            speedMin = 200D;
            speedMax = 500D;
            speedRunway = 200D;
            name = s;
            clazz = class1;
            army = i;
            bEnablePlayer = Property.containsValue(class1, "cockpitClass");
            String s1 = Property.stringValue(class1, "FlightModel", null);
            if(s1 != null)
            {
                SectFile sectfile = FlightModelMain.sectFile(s1);
                speedMin = sectfile.get("Params", "Vmin", (float)speedMin);
                speedMax = sectfile.get("Params", "VmaxH", (float)speedMax);
                speedRunway = speedMin;
            }
        }
    }


    public PlMisAir()
    {
        defaultHeight = 500D;
        defaultSpeed = 300D;
        int i = Army.amountSingle();
        if(i < Army.amountNet())
            i = Army.amountNet();
        defaultArmy = new DefaultArmy[i];
        for(int j = 0; j < i; j++)
            defaultArmy[j] = new DefaultArmy();

        iArmyRegimentList = 0;
        sCountry = "";
        regimentList = new ArrayList();
        viewMap = new HashMapInt();
        _actorInfo = new String[2];
        tabSkin = new com.maddox.gwindow.GWindowTabDialogClient.Tab[4];
        _squads = new Object[4];
        _wings = new Object[4];
        wPlayer = new GWindowCheckBox[4];
        wSkills = new GWindowComboControl[4];
        wSkins = new GWindowComboControl[4];
        wNoseart = new GWindowComboControl[4];
        wPilots = new GWindowComboControl[4];
        wSpawnPointSet = new GWindowButton[4];
        wSpawnPointClear = new GWindowButton[4];
        wSpawnPointLabel = new GWindowLabel[4];
        renders = new GUIRenders[4];
        camera3D = new Camera3D[4];
        render3D = new _Render3D[4];
        meshName = null;
        actorMesh = new ActorSimpleHMesh[4];
        _orient = new Orient();
        bSpawnFromStationary = false;
    }

    private boolean makeRegimentList(int i, String s)
    {
        if(iArmyRegimentList == i && sCountry.equals(s))
            return false;
        initCountry();
        wCountry.clear(false);
        ArrayList arraylist = listCountry[i];
        for(int j = 0; j < arraylist.size(); j++)
        {
            Country country = (Country)arraylist.get(j);
            wCountry.add(country.i18nName);
        }

        if(s != null && !mapCountry[i].containsKey(s))
            s = null;
        if(s == null)
            switch(i)
            {
            case 0: // '\0'
                s = "nn";
                break;

            case 1: // '\001'
                s = "ru";
                break;

            case 2: // '\002'
                s = "de";
                break;
            }
        Integer integer = (Integer)mapCountry[i].get(s);
        wCountry.setSelected(integer.intValue(), true, false);
        sCountry = s;
        regimentList.clear();
        List list = Regiment.getAll();
        int k = list.size();
        for(int l = 0; l < k; l++)
        {
            Regiment regiment = (Regiment)list.get(l);
            if(regiment.getArmy() == i && regiment.branch().equals(s))
                regimentList.add(regiment);
        }

        iArmyRegimentList = i;
        wRegiment.clear(false);
        wRegiment.setSelected(-1, false, false);
        k = regimentList.size();
        if(wRegiment.posEnable == null || wRegiment.posEnable.length < k)
            wRegiment.posEnable = new boolean[k];
        for(int i1 = 0; i1 < k; i1++)
        {
            Regiment regiment1 = (Regiment)regimentList.get(i1);
            String s1 = I18N.regimentShort(regiment1.shortInfo());
            if(s1 != null && s1.length() > 0 && s1.charAt(0) == '<')
                s1 = I18N.regimentInfo(regiment1.info());
            wRegiment.add(s1);
            wRegiment.posEnable[i1] = true;
        }

        wRegiment.setSelected(0, true, false);
        return true;
    }

    public void load(SectFile sectfile)
    {
        int i = sectfile.sectionIndex("Wing");
        if(i < 0)
            return;
        int j = sectfile.vars(i);
        Point3d point3d = new Point3d();
        for(int k = 0; k < j; k++)
        {
            String s = sectfile.var(i, k);
            String s1 = s + "_Way";
            if(!sectfile.sectionExist(s))
            {
                Plugin.builder.tipErr("MissionLoad: Section '" + s + "' not found");
                continue;
            }
            int l = sectfile.sectionIndex(s1);
            if(l < 0)
            {
                Plugin.builder.tipErr("MissionLoad: Section '" + s1 + "' not found");
                continue;
            }
            int i1 = sectfile.vars(l);
            if(i1 == 0)
            {
                Plugin.builder.tipErr("MissionLoad: Section '" + s1 + "' is empty");
                continue;
            }
            String s2 = sectfile.get(s, "Class", (String)null);
            if(s2 == null)
            {
                Plugin.builder.tipErr("MissionLoad: In section '" + s + "' field 'Class' not present");
                continue;
            }
            Class class1 = null;
            try
            {
                class1 = ObjIO.classForName(s2);
            }
            catch(Exception exception)
            {
                Plugin.builder.tipErr("MissionLoad: In section '" + s + "' field 'Class' contains unknown class");
                continue;
            }
            int j1 = 0;
            int k1 = 0;
            for(j1 = 0; j1 < type.length; j1++)
            {
                for(k1 = 0; k1 < type[j1].item.length; k1++)
                    if(type[j1].item[k1].clazz == class1)
                        break;

                if(k1 < type[j1].item.length)
                    break;
            }

            if(j1 >= type.length)
            {
                Plugin.builder.tipErr("MissionLoad: In section '" + s + "' field 'Class' contains unknown class");
            } else
            {
                boolean flag = sectfile.get(s, "OnlyAI", 0, 0, 1) == 1;
                boolean flag1 = sectfile.get(s, "Parachute", 1, 0, 1) == 1;
                int l1 = sectfile.get(s, "Fuel", 100, 0, 100);
                int i2 = sectfile.get(s, "Planes", 1, 1, 4);
                int j2 = -1;
                if(sectfile.exist(s, "Skill"))
                    j2 = sectfile.get(s, "Skill", 1, 0, 3);
                int ai[] = new int[4];
                for(int k2 = 0; k2 < 4; k2++)
                    if(sectfile.exist(s, "Skill" + k2))
                        ai[k2] = sectfile.get(s, "Skill" + k2, 1, 0, 3);
                    else
                        ai[k2] = j2 != -1 ? j2 : 1;

                String as[] = new String[4];
                for(int l2 = 0; l2 < 4; l2++)
                    as[l2] = sectfile.get(s, "skin" + l2, (String)null);

                String as1[] = new String[4];
                for(int i3 = 0; i3 < 4; i3++)
                    as1[i3] = sectfile.get(s, "noseart" + i3, (String)null);

                String as2[] = new String[4];
                for(int j3 = 0; j3 < 4; j3++)
                    as2[j3] = sectfile.get(s, "pilot" + j3, (String)null);

                boolean aflag[] = new boolean[4];
                for(int k3 = 0; k3 < 4; k3++)
                    aflag[k3] = sectfile.get(s, "numberOn" + k3, 1, 0, 1) == 1;

                String s3 = s.substring(0, s.length() - 2);
                Regiment regiment = (Regiment)Actor.getByName(s3);
                int l3 = 0;
                int i4 = 0;
                int j4 = 0;
                if(regiment != null)
                {
                    makeRegimentList(regiment.getArmy(), regiment.branch());
                    l3 = regimentList.indexOf(regiment);
                    if(l3 >= 0)
                    {
                        j4 = Character.getNumericValue(s.charAt(s.length() - 1)) - Character.getNumericValue('0');
                        i4 = Character.getNumericValue(s.charAt(s.length() - 2)) - Character.getNumericValue('0');
                        if(j4 < 0)
                            j4 = 0;
                        if(j4 > 3)
                            j4 = 3;
                        if(i4 < 0)
                            i4 = 0;
                        if(i4 > 3)
                            i4 = 3;
                    } else
                    {
                        regiment = null;
                    }
                }
                if(regiment == null)
                {
                    int k4 = sectfile.get(s, "Army", 0);
                    if(k4 < 1 || k4 >= Builder.armyAmount())
                        k4 = 1;
                    makeRegimentList(k4, null);
                    regiment = (Regiment)regimentList.get(0);
                    s3 = regiment.name();
                }
                String s4 = sectfile.get(s, "weapons", (String)null);
                int l4 = sectfile.get(s, "StartTime", 0);
                if(l4 < 0)
                    l4 = 0;
                String as3[] = new String[4];
                for(int i5 = 0; i5 < 4; i5++)
                    as3[i5] = sectfile.get(s, "spawn" + i5, (String)null);

                PathAir pathair = new PathAir(Plugin.builder.pathes, j1, k1);
                pathair.setArmy(regiment.getArmy());
                pathair.sRegiment = s3;
                pathair.iRegiment = l3;
                pathair.sCountry = regiment.branch();
                pathair.iSquadron = i4;
                pathair.iWing = j4;
                pathair.fuel = l1;
                pathair.bOnlyAI = flag;
                pathair.bParachute = flag1;
                pathair.planes = i2;
                pathair.skill = j2;
                pathair.skills = ai;
                pathair.skins = as;
                pathair.noseart = as1;
                pathair.pilots = as2;
                pathair.bNumberOn = aflag;
                pathair.weapons = s4;
                pathair.setSpawnPointPlaneName(0, as3[0]);
                pathair.setSpawnPointPlaneName(1, as3[1]);
                pathair.setSpawnPointPlaneName(2, as3[2]);
                pathair.setSpawnPointPlaneName(3, as3[3]);
                if(!searchEnabledSlots(pathair))
                {
                    pathair.destroy();
                    Plugin.builder.tipErr("MissionLoad: Section '" + s + "', regiment table very small");
                } else
                {
                    pathair.setName(pathair.sRegiment + pathair.iSquadron + pathair.iWing);
                    Property.set(pathair, "builderPlugin", this);
                    pathair.drawing(viewMap.containsKey(j1));
                    PAir pair2 = null;
                    for(int j5 = 0; j5 < i1; j5++)
                    {
                        String s5 = sectfile.var(l, j5);
                        byte byte0 = -1;
                        int k5 = 0;
                        if(s5.startsWith("NORMFLY"))
                        {
                            byte0 = 0;
                            if(s5.endsWith("_401"))
                                k5 = 401;
                            else
                            if(s5.endsWith("_402"))
                                k5 = 402;
                            else
                            if(s5.endsWith("_403"))
                                k5 = 403;
                            else
                            if(s5.endsWith("_404"))
                                k5 = 404;
                            else
                            if(s5.endsWith("_405"))
                                k5 = 405;
                            else
                            if(s5.endsWith("_407"))
                                k5 = 407;
                        } else
                        if(s5.startsWith("TAKEOFF"))
                        {
                            byte0 = 1;
                            if(s5.endsWith("_001"))
                                k5 = 1;
                            else
                            if(s5.endsWith("_002"))
                                k5 = 2;
                            else
                            if(s5.endsWith("_003"))
                                k5 = 3;
                            else
                            if(s5.endsWith("_004"))
                                k5 = 4;
                            else
                            if(s5.endsWith("_005"))
                                k5 = 4;
                        } else
                        if(s5.startsWith("LANDING"))
                        {
                            byte0 = 2;
                            if(s5.endsWith("_101"))
                                k5 = 101;
                            else
                            if(s5.endsWith("_102"))
                                k5 = 102;
                            else
                            if(s5.endsWith("_103"))
                                k5 = 103;
                            else
                            if(s5.endsWith("_104"))
                                k5 = 104;
                        } else
                        if(s5.startsWith("GATTACK"))
                        {
                            byte0 = 3;
                        } else
                        {
                            Plugin.builder.tipErr("MissionLoad: Section '" + s1 + "' contains unknown type waypoint");
                            pathair.destroy();
                            continue;
                        }
                        String s6 = sectfile.value(l, j5);
                        if(s6 == null || s6.length() <= 0)
                        {
                            Plugin.builder.tipErr("MissionLoad: Section '" + s1 + "' type '" + s5 + "' is empty");
                            pathair.destroy();
                        } else
                        {
                            NumberTokenizer numbertokenizer = new NumberTokenizer(s6);
                            point3d.x = numbertokenizer.next(-1E+030D, -1E+030D, 1E+030D);
                            point3d.y = numbertokenizer.next(-1E+030D, -1E+030D, 1E+030D);
                            double d = numbertokenizer.next(0.0D, 0.0D, 10000D);
                            double d1 = numbertokenizer.next(0.0D, 0.0D, 1000D);
                            String s7 = null;
                            int l5 = 0;
                            String s8 = null;
                            s7 = numbertokenizer.next(null);
                            if(s7 != null)
                                if(s7.equals("&0"))
                                {
                                    s8 = s7;
                                    s7 = null;
                                } else
                                if(s7.equals("&1"))
                                {
                                    s8 = s7;
                                    s7 = null;
                                } else
                                {
                                    l5 = numbertokenizer.next(0);
                                    s8 = numbertokenizer.next(null);
                                }
                            String s9 = null;
                            s9 = numbertokenizer.next(null);
                            if(s9 != null && s9.startsWith("F"))
                                s9 = s9.substring(1);
                            switch(byte0)
                            {
                            default:
                                break;

                            case 0: // '\0'
                                byte0 = 0;
                                break;

                            case 3: // '\003'
                                if(s7 != null && s7.startsWith("Bridge"))
                                    s7 = " " + s7;
                                byte0 = 3;
                                break;

                            case 1: // '\001'
                                byte0 = 1;
                                break;

                            case 2: // '\002'
                                byte0 = 2;
                                break;
                            }
                            pair2 = new PAir(pathair, ((PPoint) (pair2)), point3d, byte0, d, d1, null);
                            pair2.waypointType = k5;
                            if(k5 > 0 && byte0 != 2)
                            {
                                String s10 = sectfile.value(l, j5 + 1);
                                NumberTokenizer numbertokenizer1 = new NumberTokenizer(s10);
                                switch(k5)
                                {
                                case 401: 
                                case 402: 
                                case 403: 
                                case 404: 
                                case 405: 
                                    pair2.cycles = numbertokenizer1.next(0);
                                    pair2.delayTimer = numbertokenizer1.next(0);
                                    pair2.orient = numbertokenizer1.next(0);
                                    pair2.baseSize = numbertokenizer1.next(0);
                                    pair2.altDifference = numbertokenizer1.next(0);
                                    break;

                                case 407: 
                                    pair2.cycles = numbertokenizer1.next(0);
                                    pair2.delayTimer = numbertokenizer1.next(0);
                                    pair2.orient = numbertokenizer1.next(0);
                                    pair2.baseSize = numbertokenizer1.next(0);
                                    break;

                                case 1: // '\001'
                                case 2: // '\002'
                                case 3: // '\003'
                                case 4: // '\004'
                                case 5: // '\005'
                                    pair2.targetTrigger = numbertokenizer1.next(0);
                                    pair2.delayTimer = numbertokenizer1.next(0);
                                    pair2.takeoffSpacing = numbertokenizer1.next(0);
                                    pair2.ignoreAlt = numbertokenizer1.next(0);
                                    break;
                                }
                                j5++;
                            }
                            if(s7 != null)
                            {
                                pair2.iTarget = l5;
                                pair2.sTarget = s7;
                            }
                            if(s8 != null && s8.equals("&1"))
                                pair2.bRadioSilence = true;
                            if(s9 != null)
                                pair2.formation = Integer.parseInt(s9);
                        }
                    }

                    if(l4 > 0)
                    {
                        PAir pair = (PAir)pathair.point(0);
                        pair.time = (double)l4 * 60D;
                        pathair.computeTimes(false);
                    }
                }
            }
        }

    }

    public boolean save(SectFile sectfile)
    {
        if(Plugin.builder.pathes == null)
            return true;
        int i = sectfile.sectionIndex("Wing");
        Object aobj[] = Plugin.builder.pathes.getOwnerAttached();
        for(int j = 0; j < aobj.length; j++)
        {
            Actor actor = (Actor)aobj[j];
            if(actor == null)
                break;
            if(actor instanceof PathAir)
            {
                PathAir pathair = (PathAir)actor;
                int k = pathair.points();
                if(i <= -1)
                    i = sectfile.sectionAdd("Wing");
                String s = pathair.sRegiment + pathair.iSquadron + pathair.iWing;
                String s1 = s + "_Way";
                sectfile.lineAdd(i, s, "");
                int l = sectfile.sectionAdd(s);
                sectfile.lineAdd(l, "Planes", "" + pathair.planes);
                if(pathair.bOnlyAI)
                    sectfile.lineAdd(l, "OnlyAI", "1");
                if(!pathair.bParachute)
                    sectfile.lineAdd(l, "Parachute", "0");
                if(pathair.skill != -1)
                    sectfile.lineAdd(l, "Skill", "" + pathair.skill);
                for(int i1 = 0; i1 < 4; i1++)
                    if(pathair.skill != pathair.skills[i1])
                        sectfile.lineAdd(l, "Skill" + i1, "" + pathair.skills[i1]);

                for(int j1 = 0; j1 < 4; j1++)
                    if(pathair.skins[j1] != null)
                        sectfile.lineAdd(l, "skin" + j1, pathair.skins[j1]);

                for(int k1 = 0; k1 < 4; k1++)
                    if(pathair.noseart[k1] != null)
                        sectfile.lineAdd(l, "noseart" + k1, pathair.noseart[k1]);

                for(int l1 = 0; l1 < 4; l1++)
                    if(pathair.pilots[l1] != null)
                        sectfile.lineAdd(l, "pilot" + l1, pathair.pilots[l1]);

                for(int i2 = 0; i2 < 4; i2++)
                    if(!pathair.bNumberOn[i2])
                        sectfile.lineAdd(l, "numberOn" + i2, "0");

                sectfile.lineAdd(l, "Class", ObjIO.classGetName(type[pathair._iType].item[pathair._iItem].clazz));
                sectfile.lineAdd(l, "Fuel", "" + pathair.fuel);
                if(pathair.weapons != null)
                    sectfile.lineAdd(l, "weapons", "" + pathair.weapons);
                else
                    sectfile.lineAdd(l, "weapons", "none");
                PAir pair = (PAir)pathair.point(0);
                if(pair.time > 0.0D)
                    sectfile.lineAdd(l, "StartTime", "" + (int)Math.round(pair.time / 60D));
                boolean flag = true;
                for(int j2 = 0; j2 < 4; j2++)
                    if(pathair.getSpawnPoint(j2) != null)
                        sectfile.lineAdd(l, "spawn" + j2, pathair.getSpawnPoint(j2).name());
                    else
                    if(j2 < pathair.planes)
                        flag = false;

                int k2 = sectfile.sectionAdd(s1);
                for(int l2 = 0; l2 < k; l2++)
                {
                    PAir pair1 = (PAir)pathair.point(l2);
                    Point3d point3d = pair1.pos.getAbsPoint();
                    switch(pair1.type())
                    {
                    default:
                        break;

                    case 0: // '\0'
                        String s2 = "";
                        if(pair1.waypointType > 0)
                            s2 = "_" + pair1.waypointType;
                        sectfile.lineAdd(k2, "NORMFLY" + s2, fmt(point3d.x) + " " + fmt(point3d.y) + " " + fmt(pair1.height) + " " + fmt(pair1.speed) + saveTarget(pair1) + saveRadioSilence(pair1) + saveFormation(pair1));
                        if(pair1.waypointType > 400)
                            sectfile.lineAdd(k2, "TRIGGERS", pair1.cycles + " " + pair1.delayTimer + " " + (int)pair1.orient + " " + pair1.baseSize + " " + pair1.altDifference);
                        break;

                    case 3: // '\003'
                        String s3 = "";
                        if(pair1.waypointType > 0)
                            s3 = "_" + pair1.waypointType;
                        sectfile.lineAdd(k2, "GATTACK" + s3, fmt(point3d.x) + " " + fmt(point3d.y) + " " + fmt(pair1.height) + " " + fmt(pair1.speed) + saveTarget(pair1) + saveRadioSilence(pair1) + saveFormation(pair1));
                        if(pair1.waypointType > 200)
                            sectfile.lineAdd(k2, "TRIGGERS", pair1.cycles + " " + pair1.delayTimer + " " + (int)pair1.orient + " " + pair1.baseSize + " " + pair1.targetTrigger + " " + pair1.altDifference);
                        break;

                    case 1: // '\001'
                        String s4 = "";
                        if(pair1.waypointType > 0)
                        {
                            int i3 = pair1.waypointType;
                            if(i3 == 4 && flag)
                                i3 = 5;
                            s4 = "_00" + i3;
                        }
                        sectfile.lineAdd(k2, "TAKEOFF" + s4, fmt(point3d.x) + " " + fmt(point3d.y) + " 0 0" + saveTarget(pair1) + saveRadioSilence(pair1));
                        if(pair1.waypointType > 0)
                            sectfile.lineAdd(k2, "TRIGGERS", pair1.targetTrigger + " " + pair1.delayTimer + " " + pair1.takeoffSpacing + " " + pair1.ignoreAlt);
                        break;

                    case 2: // '\002'
                        String s5 = "";
                        if(pair1.waypointType > 0)
                            s5 = "_" + pair1.waypointType;
                        sectfile.lineAdd(k2, "LANDING" + s5, fmt(point3d.x) + " " + fmt(point3d.y) + " 0 0" + saveTarget(pair1) + saveRadioSilence(pair1));
                        break;
                    }
                }

            }
        }

        return true;
    }

    private String fmt(double d)
    {
        boolean flag = d < 0.0D;
        if(flag)
            d = -d;
        double d1 = (d + 0.005D) - (double)(int)d;
        if(d1 >= 0.1D)
            return (flag ? "-" : "") + (int)d + "." + (int)(d1 * 100D);
        else
            return (flag ? "-" : "") + (int)d + ".0" + (int)(d1 * 100D);
    }

    private String saveTarget(PAir pair)
    {
        if(!Actor.isValid(pair.getTarget()))
            return "";
        if(pair.getTarget() instanceof PPoint)
        {
            PPoint ppoint = (PPoint)pair.getTarget();
            Path path = (Path)ppoint.getOwner();
            return " " + path.name() + " " + path.pointIndx(ppoint);
        } else
        {
            return " " + pair.getTarget().name() + " 0";
        }
    }

    private String saveRadioSilence(PAir pair)
    {
        return " " + (pair.bRadioSilence ? "&1" : "&0");
    }

    private String saveFormation(PAir pair)
    {
        if(pair.formation == 0)
            return "";
        else
            return " F" + pair.formation;
    }

    private void clampSpeed(PAir pair)
    {
        PathAir pathair = (PathAir)pair.getOwner();
        if(pair.type() == 0 || pair.type() == 3)
        {
            if(pair.speed < type[pathair._iType].item[pathair._iItem].speedMin)
                pair.speed = type[pathair._iType].item[pathair._iItem].speedMin;
            if(pair.speed > type[pathair._iType].item[pathair._iItem].speedMax)
                pair.speed = type[pathair._iType].item[pathair._iItem].speedMax;
        } else
        {
            pair.speed = 0.0D;
        }
    }

    private void clampSpeed(PathAir pathair)
    {
        int i = pathair.points();
        for(int j = 0; j < i; j++)
            clampSpeed((PAir)pathair.point(j));

    }

    public void insert(Loc loc, boolean flag)
    {
        PathAir pathair = null;
        try
        {
            Point3d point3d = loc.getPoint();
            int i = Plugin.builder.wSelect.comboBox1.getSelected();
            int j = Plugin.builder.wSelect.comboBox2.getSelected();
            if(Plugin.builder.selectedPath() != null)
            {
                Path path = Plugin.builder.selectedPath();
                if(!(path instanceof PathAir))
                    return;
                pathair = (PathAir)path;
                if(i - startComboBox1 != pathair._iType || j != pathair._iItem)
                    Plugin.builder.setSelected(null);
            }
            PAir pair;
            if(Plugin.builder.selectedPoint() != null)
            {
                PAir pair1 = (PAir)Plugin.builder.selectedPoint();
                if(pair1.type() == 2)
                    return;
                int k = 0;
                if(pair1.type() == 1 && pair1.waypointType == 4)
                    k = 1;
                pair = new PAir(Plugin.builder.selectedPath(), Plugin.builder.selectedPoint(), point3d, k, defaultHeight, defaultSpeed, pair1);
            } else
            {
                if(i < startComboBox1 || i >= startComboBox1 + type.length)
                    return;
                i -= startComboBox1;
                if(j < 0 || j >= type[i].item.length)
                    return;
                pathair = new PathAir(Plugin.builder.pathes, i, j);
                pathair.setArmy(type[i].item[j].army);
                defaultArmy[type[i].item[j].army].load(pathair);
                if(!searchEnabledSlots(pathair))
                {
                    pathair.destroy();
                    return;
                }
                pathair.setName(pathair.sRegiment + pathair.iSquadron + pathair.iWing);
                Property.set(pathair, "builderPlugin", this);
                pathair.drawing(viewMap.containsKey(i));
                pair = new PAir(pathair, null, point3d, 0, defaultHeight, defaultSpeed, null);
            }
            clampSpeed((PAir)pair);
            Plugin.builder.setSelected(pair);
            PlMission.setChanged();
        }
        catch(Exception exception)
        {
            if(pathair != null && pathair.points() == 0)
                pathair.destroy();
            System.out.println(exception);
        }
    }

    public void changeType()
    {
        int i = Plugin.builder.wSelect.comboBox1.getSelected() - startComboBox1;
        int j = Plugin.builder.wSelect.comboBox2.getSelected();
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        if(i != pathair._iType)
            return;
        pathair.skins = new String[4];
        pathair.noseart = new String[4];
        pathair._iItem = j;
        Class class1 = type[i].item[j].clazz;
        String as[] = Aircraft.getWeaponsRegistered(class1);
        if(as != null && as.length > 0)
        {
            int k;
            for(k = 0; k < as.length; k++)
            {
                String s = as[k];
                if(Aircraft.isWeaponDateOk(class1, as[k]) && s.equalsIgnoreCase(pathair.weapons))
                    break;
            }

            if(k == as.length)
                pathair.weapons = as[0];
        }
        clampSpeed(pathair);
        fillDialogWay();
        fillSkins();
        fillNoseart();
        syncSkins();
        syncNoseart();
        syncPilots();
        resetMesh();
        PlMission.setChanged();
    }

    public void configure()
    {
        if(Plugin.getPlugin("Mission") == null)
            throw new RuntimeException("PlMisAir: plugin 'Mission' not found");
        pluginMission = (PlMission)Plugin.getPlugin("Mission");
        if(sectFile == null)
            throw new RuntimeException("PlMisAir: field 'sectFile' not defined");
        SectFile sectfile = new SectFile(sectFile, 0);
        int i = sectfile.sections();
        if(i <= 0)
            throw new RuntimeException("PlMisAir: file '" + sectFile + "' is empty");
        type = new Type[i];
        for(int j = 0; j < i; j++)
        {
            String s = sectfile.sectionName(j);
            int k = sectfile.vars(j);
            Item aitem[] = new Item[k];
            for(int l = 0; l < k; l++)
            {
                String s1 = sectfile.var(j, l);
                NumberTokenizer numbertokenizer = new NumberTokenizer(sectfile.value(j, l));
                String s2 = numbertokenizer.next((String)null);
                int i1 = numbertokenizer.next(1, 1, Builder.armyAmount() - 1);
                Class class1 = null;
                try
                {
                    class1 = ObjIO.classForName(s2);
                }
                catch(Exception exception)
                {
                    throw new RuntimeException("PlMisAir: class '" + s2 + "' not found");
                }
                aitem[l] = new Item(s1, class1, i1);
            }

            type[j] = new Type(s, aitem);
        }

    }

    void viewUpdate()
    {
        if(Plugin.builder.pathes == null)
            return;
        Object aobj[] = Plugin.builder.pathes.getOwnerAttached();
        for(int i = 0; i < aobj.length; i++)
        {
            Actor actor = (Actor)aobj[i];
            if(actor == null)
                break;
            if(actor instanceof PathAir)
            {
                PathAir pathair = (PathAir)actor;
                pathair.drawing(viewMap.containsKey(pathair._iType));
            }
        }

        if(Plugin.builder.selectedPath() != null)
        {
            Path path = Plugin.builder.selectedPath();
            if(path instanceof PathAir)
            {
                PathAir pathair1 = (PathAir)path;
                if(!viewMap.containsKey(pathair1._iType))
                    Plugin.builder.setSelected(null);
            }
        }
        if(!Plugin.builder.isFreeView())
            Plugin.builder.repaint();
    }

    void viewType(int i, boolean flag)
    {
        if(flag)
            viewMap.put(i, null);
        else
            viewMap.remove(i);
        viewUpdate();
    }

    void viewType(int i)
    {
        viewType(i, viewType[i].bChecked);
    }

    public void viewTypeAll(boolean flag)
    {
        for(int i = 0; i < type.length; i++)
            if(viewType[i].bChecked != flag)
            {
                viewType[i].bChecked = flag;
                viewType(i, flag);
            }

    }

    private void fillComboBox1()
    {
        startComboBox1 = Plugin.builder.wSelect.comboBox1.size();
        for(int i = 0; i < type.length; i++)
            Plugin.builder.wSelect.comboBox1.add(I18N.technic(type[i].name));

        if(startComboBox1 == 0)
            Plugin.builder.wSelect.comboBox1.setSelected(0, true, false);
    }

    private void fillComboBox2(int i, int j)
    {
        if(i < startComboBox1 || i >= startComboBox1 + type.length)
            return;
        if(Plugin.builder.wSelect.curFilledType != i)
        {
            Plugin.builder.wSelect.curFilledType = i;
            Plugin.builder.wSelect.comboBox2.clear(false);
            for(int k = 0; k < type[i - startComboBox1].item.length; k++)
                Plugin.builder.wSelect.comboBox2.add(I18N.plane(type[i - startComboBox1].item[k].name));

            Plugin.builder.wSelect.comboBox1.setSelected(i, true, false);
        }
        Plugin.builder.wSelect.comboBox2.setSelected(j, true, false);
        Class class1 = type[i].item[j].clazz;
        Plugin.builder.wSelect.setMesh(Aircraft.getPropertyMesh(class1, currentCountry()), false);
    }

    public String[] actorInfo(Actor actor)
    {
        PathAir pathair = (PathAir)actor.getOwner();
        _actorInfo[0] = I18N.technic(type[pathair._iType].name) + "." + I18N.plane(type[pathair._iType].item[pathair._iItem].name) + ":" + pathair.typedName;
        PAir pair = (PAir)actor;
        int i = pathair.pointIndx(pair);
        _actorInfo[1] = "(" + i + ") " + Plugin.timeSecToString(pair.time + (double)(int)(World.getTimeofDay() * 60F * 60F));
        return _actorInfo;
    }

    public void syncSelector()
    {
        Plugin.builder.wSelect.tabsClient.addTab(1, tabActor);
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        fillComboBox2(pathair._iType + startComboBox1, pathair._iItem);
        Plugin.builder.wSelect.tabsClient.addTab(2, tabWay);
        Plugin.builder.wSelect.tabsClient.addTab(3, tabWay2);
        fillEditActor();
        fillDialogWay();
        PathAir pathair1 = (PathAir)Plugin.builder.selectedPath();
        for(int i = 0; i < pathair1.planes; i++)
        {
            Plugin.builder.wSelect.tabsClient.addTab(i + 4, tabSkin[i]);
            fillEditSkin(i);
        }

        fillSkins();
        fillNoseart();
        fillPilots();
        syncSkins();
        syncNoseart();
        syncPilots();
        resetMesh();
    }

    public void updateSelector()
    {
        fillDialogWay();
    }

    public void updateSelectorMesh()
    {
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        PaintScheme paintscheme = Aircraft.getPropertyPaintScheme(type[pathair._iType].item[pathair._iItem].clazz, pathair.regiment().country());
        if(paintscheme != null)
        {
            HierMesh hiermesh = Plugin.builder.wSelect.getHierMesh();
            if(hiermesh != null)
                paintscheme.prepare(type[pathair._iType].item[pathair._iItem].clazz, hiermesh, pathair.regiment(), pathair.iSquadron, pathair.iWing, 0);
        }
    }

    public void createGUI()
    {
        fillComboBox1();
        fillComboBox2(0, 0);
        Plugin.builder.wSelect.comboBox1.addNotifyListener(new GNotifyListener() {

            public boolean notify(GWindow gwindow, int k, int l)
            {
                int i1 = Plugin.builder.wSelect.comboBox1.getSelected();
                if(i1 >= 0 && k == 2)
                    fillComboBox2(i1, 0);
                return false;
            }

        }
);
        Plugin.builder.wSelect.comboBox2.addNotifyListener(new GNotifyListener() {

            public boolean notify(GWindow gwindow, int k, int l)
            {
                if(k != 2)
                    return false;
                int i1 = Plugin.builder.wSelect.comboBox1.getSelected();
                if(i1 < startComboBox1 || i1 >= startComboBox1 + type.length)
                    return false;
                int j1 = Plugin.builder.wSelect.comboBox2.getSelected();
                Class class1 = type[i1 - startComboBox1].item[j1].clazz;
                Plugin.builder.wSelect.setMesh(Aircraft.getPropertyMesh(class1, currentCountry()), false);
                resetMesh();
                fillSkins();
                fillNoseart();
                fillPilots();
                syncSkins();
                syncNoseart();
                syncPilots();
                PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                if(pathair != null)
                    pathair.updateTypedName();
                fillEditActor();
                return false;
            }

        }
);
        int i;
        for(i = Plugin.builder.mDisplayFilter.subMenu.size() - 1; i >= 0; i--)
            if(pluginMission.viewBridge == Plugin.builder.mDisplayFilter.subMenu.getItem(i))
                break;

        if(--i >= 0)
        {
            int j = i;
            i = type.length - 1;
            viewType = new ViewItem[type.length];
            for(; i >= 0; i--)
            {
                ViewItem viewitem = null;
                if("de".equals(RTSConf.cur.locale.getLanguage()))
                    viewitem = (ViewItem)Plugin.builder.mDisplayFilter.subMenu.addItem(j, new ViewItem(i, Plugin.builder.mDisplayFilter.subMenu, I18N.technic(type[i].name) + " " + Plugin.i18n("show"), null));
                else
                    viewitem = (ViewItem)Plugin.builder.mDisplayFilter.subMenu.addItem(j, new ViewItem(i, Plugin.builder.mDisplayFilter.subMenu, Plugin.i18n("show") + " " + I18N.technic(type[i].name), null));
                viewitem.bChecked = true;
                viewType[i] = viewitem;
                viewType(i, true);
            }

        }
        initEditActor();
        initEditWay();
        initEditWay2();
        initEditSkin();
    }

    public void start()
    {
        HotKeyCmdEnv.setCurrentEnv(Builder.envName);
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "beginSelectSpawnPoint") {

            public void end()
            {
                if(!Plugin.builder.isLoadedLandscape())
                    return;
                if(Plugin.builder.mouseState != 0)
                    return;
                if(Plugin.builder.isFreeView())
                {
                    return;
                } else
                {
                    Plugin.builder.beginSelectSpawnPoint();
                    return;
                }
            }

        }
);
    }

    private boolean searchEnabledSlots(PathAir pathair)
    {
        makeRegimentList(pathair.getArmy(), pathair.sCountry);
        int i = regimentList.size();
        if(pathair.iRegiment < 0)
            pathair.iRegiment = 0;
        if(pathair.iRegiment >= i)
            pathair.iRegiment = i - 1;
        for(int j = 0; j < i; j++)
        {
            Regiment regiment = (Regiment)regimentList.get(pathair.iRegiment);
            pathair.sRegiment = regiment.name();
            if(isEnabledRegiment(regiment))
            {
                for(int k = 0; k < 4; k++)
                {
                    ResSquadron ressquadron = (ResSquadron)Actor.getByName(regiment.name() + pathair.iSquadron);
                    if(ressquadron == null)
                    {
                        defaultArmy[pathair.getArmy()].save(pathair);
                        return true;
                    }
                    if(isEnabledSquad(ressquadron))
                    {
                        for(int l = 0; l < 4; l++)
                        {
                            if(Actor.getByName(regiment.name() + pathair.iSquadron + pathair.iWing) == null)
                            {
                                defaultArmy[pathair.getArmy()].save(pathair);
                                return true;
                            }
                            pathair.iWing = (pathair.iWing + 1) % 4;
                        }

                    }
                    pathair.iWing = 0;
                    pathair.iSquadron = (pathair.iSquadron + 1) % 4;
                }

            } else
            {
                pathair.iWing = 0;
            }
            pathair.iSquadron = 0;
            pathair.iRegiment = (pathair.iRegiment + 1) % i;
            pathair.sCountry = regiment.branch();
        }

        return false;
    }

    private void fillEnabledRegiments(int i)
    {
        int j = regimentList.size();
        for(int k = 0; k < j; k++)
            if(k == i)
            {
                wRegiment.posEnable[k] = true;
            } else
            {
                Regiment regiment = (Regiment)regimentList.get(k);
                wRegiment.posEnable[k] = isEnabledRegiment(regiment);
            }

    }

    private String currentCountry()
    {
        if(wRegiment == null)
            return "ru";
        int i = wRegiment.getSelected();
        if(i < 0)
            return "ru";
        else
            return ((Regiment)regimentList.get(i)).country();
    }

    private boolean isEnabledRegiment(Regiment regiment)
    {
        if(regiment.getOwnerAttachedCount() < 4)
            return true;
        _squads = regiment.getOwnerAttached(_squads);
        boolean flag = false;
        for(int i = 0; i < 4; i++)
        {
            ResSquadron ressquadron = (ResSquadron)_squads[i];
            _squads[i] = null;
            if(ressquadron == null || isEnabledSquad(ressquadron))
                flag = true;
        }

        return flag;
    }

    private void fillEnabledSquads(Regiment regiment, int i)
    {
        for(int j = 0; j < 4; j++)
            wSquadron.posEnable[j] = true;

        _squads = regiment.getOwnerAttached(_squads);
        for(int k = 0; k < 4; k++)
        {
            ResSquadron ressquadron = (ResSquadron)_squads[k];
            if(ressquadron == null)
                break;
            _squads[k] = null;
            if(ressquadron.index() != i)
                wSquadron.posEnable[ressquadron.index()] = isEnabledSquad(ressquadron);
        }

    }

    private boolean isEnabledSquad(ResSquadron ressquadron)
    {
        return ressquadron.getAttachedCount() < 4;
    }

    private void fillEnabledWings(ResSquadron ressquadron, int i)
    {
        for(int j = 0; j < 4; j++)
            wWing.posEnable[j] = true;

        _wings = ressquadron.getAttached(_wings);
        for(int k = 0; k < 4; k++)
        {
            PathAir pathair = (PathAir)_wings[k];
            if(pathair == null)
                break;
            _wings[k] = null;
            if(pathair.iWing != i)
                wWing.posEnable[pathair.iWing] = false;
        }

    }

    private void controlResized(GWindowDialogClient gwindowdialogclient, GWindow gwindow)
    {
        if(gwindow == null)
        {
            return;
        } else
        {
            gwindow.setSize(gwindowdialogclient.win.dx - gwindow.win.x - gwindowdialogclient.lAF().metric(1.0F), gwindow.win.dy);
            return;
        }
    }

    private void editResized(GWindowDialogClient gwindowdialogclient)
    {
        controlResized(gwindowdialogclient, wArmy);
        controlResized(gwindowdialogclient, wCountry);
        controlResized(gwindowdialogclient, wRegiment);
        controlResized(gwindowdialogclient, wSquadron);
        controlResized(gwindowdialogclient, wWing);
        controlResized(gwindowdialogclient, wWeapons);
        controlResized(gwindowdialogclient, wFuel);
        controlResized(gwindowdialogclient, wPlanes);
        controlResized(gwindowdialogclient, wSkill);
    }

    public void initEditActor()
    {
        GWindowDialogClient gwindowdialogclient = (GWindowDialogClient)Plugin.builder.wSelect.tabsClient.create(new GWindowDialogClient() {

            public void resized()
            {
                super.resized();
                editResized(this);
//                if(render3D != null)
//                {
//                    for(int i = 0; i < render3D.length; i++)
//                        render3D[i].setClearColor(GWindowLookAndFeel.getUIBackgroundColor());
//
//                }
            }

        }
);
        tabActor = Plugin.builder.wSelect.tabsClient.createTab(Plugin.i18n("AircraftActor"), gwindowdialogclient);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 1.0F, 7F, 1.3F, Plugin.i18n("Army"), null));
        gwindowdialogclient.addControl(wArmy = new GWindowComboControl(gwindowdialogclient, 9F, 1.0F, 7F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
                for(int i = 1; i < Builder.armyAmount(); i++)
                    add(I18N.army(Army.name(i)));

            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                    return false;
                PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                int k = getSelected() + 1;
                int l = pathair.getArmy();
                int i1 = pathair.iRegiment;
                int j1 = pathair.iSquadron;
                int k1 = pathair.iWing;
                String s = pathair.sCountry;
                pathair.setArmy(k);
                defaultArmy[k].load(pathair);
                if(!searchEnabledSlots(pathair))
                {
                    pathair.setArmy(l);
                    pathair.iRegiment = i1;
                    pathair.iSquadron = j1;
                    pathair.iWing = k1;
                    pathair.sCountry = s;
                    searchEnabledSlots(pathair);
                }
                pathair.setName(pathair.sRegiment + pathair.iSquadron + pathair.iWing);
                fillEditActor();
                fillNoseart();
                syncNoseart();
                Class class1 = type[pathair._iType].item[pathair._iItem].clazz;
                Plugin.builder.wSelect.setMesh(Aircraft.getPropertyMesh(class1, currentCountry()), false);
                resetMesh();
                if(Path.player == pathair)
                    PlMission.cur.missionArmy = k;
                PlMission.setChanged();
                PlMission.checkShowCurrentArmy();
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 3F, 7F, 1.3F, Plugin.i18n("Country"), null));
        gwindowdialogclient.addControl(wCountry = new GWindowComboControl(gwindowdialogclient, 9F, 3F, 7F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                    return false;
                int k = getSelected();
                Country country = (Country)listCountry[iArmyRegimentList].get(k);
                if(makeRegimentList(iArmyRegimentList, country.name))
                {
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    pathair.sCountry = country.name;
                    pathair.iRegiment = 0;
                    searchEnabledSlots(pathair);
                    pathair.setName(pathair.sRegiment + pathair.iSquadron + pathair.iWing);
                    defaultArmy[pathair.getArmy()].save(pathair);
                    fillEditActor();
                    fillNoseart();
                    syncNoseart();
                    Class class1 = type[pathair._iType].item[pathair._iItem].clazz;
                    Plugin.builder.wSelect.setMesh(Aircraft.getPropertyMesh(class1, currentCountry()), false);
                    resetMesh();
                    PlMission.setChanged();
                }
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 5F, 7F, 1.3F, Plugin.i18n("Regiment"), null));
        gwindowdialogclient.addControl(wRegiment = new GWindowComboControl(gwindowdialogclient, 9F, 5F, 7F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                {
                    return false;
                } else
                {
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    pathair.iRegiment = getSelected();
                    searchEnabledSlots(pathair);
                    pathair.setName(pathair.sRegiment + pathair.iSquadron + pathair.iWing);
                    defaultArmy[pathair.getArmy()].save(pathair);
                    fillEditActor();
                    fillNoseart();
                    syncNoseart();
                    Class class1 = type[pathair._iType].item[pathair._iItem].clazz;
                    Plugin.builder.wSelect.setMesh(Aircraft.getPropertyMesh(class1, currentCountry()), false);
                    resetMesh();
                    PlMission.setChanged();
                    return false;
                }
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 7F, 7F, 1.3F, Plugin.i18n("Squadron"), null));
        gwindowdialogclient.addControl(wSquadron = new GWindowComboControl(gwindowdialogclient, 9F, 7F, 7F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
                add("1");
                add("2");
                add("3");
                add("4");
                posEnable = new boolean[4];
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                {
                    return false;
                } else
                {
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    pathair.iSquadron = getSelected();
                    searchEnabledSlots(pathair);
                    pathair.setName(pathair.sRegiment + pathair.iSquadron + pathair.iWing);
                    defaultArmy[pathair.getArmy()].save(pathair);
                    fillEditActor();
                    resetMesh();
                    PlMission.setChanged();
                    return false;
                }
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 9F, 7F, 1.3F, Plugin.i18n("Wing"), null));
        gwindowdialogclient.addControl(wWing = new GWindowComboControl(gwindowdialogclient, 9F, 9F, 7F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
                add("1");
                add("2");
                add("3");
                add("4");
                posEnable = new boolean[4];
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                {
                    return false;
                } else
                {
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    pathair.iWing = getSelected();
                    pathair.setName(pathair.sRegiment + pathair.iSquadron + pathair.iWing);
                    defaultArmy[pathair.getArmy()].save(pathair);
                    fillEditActor();
                    resetMesh();
                    PlMission.setChanged();
                    return false;
                }
            }

        }
);
        lWeapons = new ArrayList();
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 11F, 7F, 1.3F, Plugin.i18n("Weapons"), null));
        gwindowdialogclient.addControl(wWeapons = new GWindowComboControl(gwindowdialogclient, 9F, 11F, 7F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                    return false;
                PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                int k = getSelected();
                if(k < 0)
                    pathair.weapons = null;
                else
                    pathair.weapons = (String)lWeapons.get(k);
                PlMission.setChanged();
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 13F, 7F, 1.3F, Plugin.i18n("Fuel"), null));
        gwindowdialogclient.addControl(wFuel = new GWindowEditControl(gwindowdialogclient, 9F, 13F, 7F, 1.3F, "") {

            public void afterCreated()
            {
                super.afterCreated();
                bDelayedNotify = true;
                bNumericOnly = true;
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                    return false;
                PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                String s = getValue();
                int k = 0;
                if(s != null)
                    try
                    {
                        k = Integer.parseInt(s);
                    }
                    catch(Exception exception)
                    {
                        setValue("" + ((PathAir)Plugin.builder.selectedPath()).fuel, false);
                        return false;
                    }
                if(k < 0)
                    k = 0;
                else
                if(k > 100)
                    k = 100;
                pathair.fuel = k;
                PlMission.setChanged();
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 15F, 7F, 1.3F, Plugin.i18n("Planes"), null));
        gwindowdialogclient.addControl(wPlanes = new GWindowComboControl(gwindowdialogclient, 9F, 15F, 7F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
                add("1");
                add("2");
                add("3");
                add("4");
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                {
                    return false;
                } else
                {
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    pathair.planes = getSelected() + 1;
                    checkEditSkinTabs();
                    checkEditSkinSkills();
                    PlMission.setChanged();
                    return false;
                }
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 17F, 7F, 1.3F, Plugin.i18n("Skill"), null));
        gwindowdialogclient.addControl(wSkill = new GWindowComboControl(gwindowdialogclient, 9F, 17F, 7F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
                add(Plugin.i18n("Rookie"));
                add(Plugin.i18n("Average"));
                add(Plugin.i18n("Veteran"));
                add(Plugin.i18n("Ace"));
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                    return false;
                PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                pathair.skill = getSelected();
                for(int k = 0; k < 4; k++)
                    pathair.skills[k] = pathair.skill;

                checkEditSkinSkills();
                PlMission.setChanged();
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 19F, 7F, 1.3F, Plugin.i18n("OnlyAI"), null));
        gwindowdialogclient.addControl(wOnlyAI = new GWindowCheckBox(gwindowdialogclient, 9F, 19F, null) {

            public void preRender()
            {
                super.preRender();
                PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                if(pathair == null)
                {
                    return;
                } else
                {
                    setChecked(pathair.bOnlyAI, false);
                    setEnable(type[pathair._iType].item[pathair._iItem].bEnablePlayer);
                    return;
                }
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                    return false;
                PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                pathair.bOnlyAI = isChecked();
                if(isChecked() && Path.player == pathair)
                {
                    Path.player = null;
                    Path.playerNum = 0;
                }
                PlMission.setChanged();
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 21F, 7F, 1.3F, Plugin.i18n("Parachute"), null));
        gwindowdialogclient.addControl(wParachute = new GWindowCheckBox(gwindowdialogclient, 9F, 21F, null) {

            public void preRender()
            {
                super.preRender();
                PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                if(pathair == null)
                {
                    return;
                } else
                {
                    setChecked(pathair.bParachute, false);
                    return;
                }
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                {
                    return false;
                } else
                {
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    pathair.bParachute = isChecked();
                    PlMission.setChanged();
                    return false;
                }
            }

        }
);
    }

    public void initEditWay2()
    {
        GWindowDialogClient gwindowdialogclient = (GWindowDialogClient)Plugin.builder.wSelect.tabsClient.create(new GWindowDialogClient() {

            public void resized()
            {
                super.resized();
                editResized(this);
            }

        }
);
        tabWay2 = Plugin.builder.wSelect.tabsClient.createTab(Plugin.i18n("WaypointOptions"), gwindowdialogclient);
        gwindowdialogclient.addLabel(wDelayTimerLabel = new GWindowLabel(gwindowdialogclient, 1.0F, 3F, 7F, 1.3F, Plugin.i18n("Delay"), null));
        gwindowdialogclient.addLabel(wDelayTimerLabel2 = new GWindowLabel(gwindowdialogclient, 15F, 3F, 4F, 1.3F, Plugin.i18n("min"), null));
        gwindowdialogclient.addControl(wDelayTimer = new GWindowEditControl(gwindowdialogclient, 9F, 3F, 5F, 1.3F, "") {

            public void afterCreated()
            {
                super.afterCreated();
                bNumericOnly = true;
                bDelayedNotify = true;
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                    return false;
                PAir pair = (PAir)Plugin.builder.selectedPoint();
                int k = Integer.parseInt(wDelayTimer.getValue());
                if(k < 0)
                    k = 0;
                if(k > 1800)
                    k = 1800;
                pair.delayTimer = k;
                if(k > 0 && wTakeOffType.getSelected() == 0)
                    pair.waypointType = 1;
                setValue("" + k, false);
                PlMission.setChanged();
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(wTakeOffTypeLabel = new GWindowLabel(gwindowdialogclient, 1.0F, 1.0F, 7F, 1.3F, Plugin.i18n("TakeOffType"), null));
        gwindowdialogclient.addControl(wTakeOffType = new GWindowComboControl(gwindowdialogclient, 9F, 1.0F, 9F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
                add(Plugin.i18n("TakeoffTNormal"));
                add(Plugin.i18n("TakeoffTPairs"));
                add(Plugin.i18n("TakeoffTLine"));
                add(Plugin.i18n("TakeoffTTaxi"));
                posEnable = new boolean[5];
                for(int i = 0; i < 5; i++)
                    posEnable[i] = true;

            }

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    pair.waypointType = wTakeOffType.getSelected();
                    if(pair.waypointType > 0)
                        pair.waypointType++;
                    else
                    if(pair.delayTimer > 0)
                        pair.waypointType++;
                    fillDialogWay2();
                    PlMission.setChanged();
                }
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(wTakeoffSpacingLabel = new GWindowLabel(gwindowdialogclient, 1.0F, 5F, 7F, 1.3F, Plugin.i18n("TakeOffSpacing"), null));
        gwindowdialogclient.addLabel(wTakeoffSpacingLabel2 = new GWindowLabel(gwindowdialogclient, 15F, 5F, 4F, 1.3F, Plugin.i18n("meters"), null));
        gwindowdialogclient.addControl(wTakeoffSpacing = new GWindowEditControl(gwindowdialogclient, 9F, 5F, 5F, 1.3F, "") {

            public void afterCreated()
            {
                super.afterCreated();
                bNumericOnly = false;
                bDelayedNotify = true;
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                    return false;
                PAir pair = (PAir)Plugin.builder.selectedPoint();
                int k = Integer.parseInt(wTakeoffSpacing.getValue());
                if(k < -1000)
                    k = -1000;
                if(k > 1000)
                    k = 1000;
                pair.takeoffSpacing = k;
                setValue("" + k, false);
                PlMission.setChanged();
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(wLandingTypeLabel = new GWindowLabel(gwindowdialogclient, 1.0F, 1.0F, 7F, 1.3F, Plugin.i18n("LandingType"), null));
        gwindowdialogclient.addControl(wLandingType = new GWindowComboControl(gwindowdialogclient, 9F, 1.0F, 9F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
                add(Plugin.i18n("LandingTypeLP"));
                add(Plugin.i18n("LandingTypeRP"));
                add(Plugin.i18n("LandingTypeSLP"));
                add(Plugin.i18n("LandingTypeSRP"));
                add(Plugin.i18n("LandingTypeStraightIn"));
            }

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    if(wLandingType.getSelected() > 0)
                        pair.waypointType = wLandingType.getSelected() + 100;
                    else
                        pair.waypointType = 0;
                    PlMission.setChanged();
                }
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(wNormFlyTypeLabel = new GWindowLabel(gwindowdialogclient, 1.0F, 1.0F, 7F, 1.3F, Plugin.i18n("NormflyType"), null));
        gwindowdialogclient.addControl(wNormFlyType = new GWindowComboControl(gwindowdialogclient, 9F, 1.0F, 9F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
                add(Plugin.i18n("NormflyTypeNormal"));
                add(Plugin.i18n("NormflyTypeCAP"));
                add(Plugin.i18n("ArtSpot"));
            }

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    if(wNormFlyType.getSelected() == 1)
                        pair.waypointType = 401;
                    else
                    if(wNormFlyType.getSelected() == 2)
                        pair.waypointType = 407;
                    else
                        pair.waypointType = 0;
                    fillDialogWay2();
                    PlMission.setChanged();
                }
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(wCAPTypeLabel = new GWindowLabel(gwindowdialogclient, 1.0F, 3F, 7F, 1.3F, Plugin.i18n("CAPType"), null));
        gwindowdialogclient.addControl(wCAPType = new GWindowComboControl(gwindowdialogclient, 9F, 3F, 9F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
                add(Plugin.i18n("CAPTypeTriangle"));
                add(Plugin.i18n("CAPTypeSquare"));
                add(Plugin.i18n("CAPTypePentagon"));
                add(Plugin.i18n("CAPTypeHexagon"));
                add(Plugin.i18n("CAPTypeRandom"));
            }

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    pair.waypointType = wCAPType.getSelected() + 401;
                    PlMission.setChanged();
                }
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(wCAPCyclesLabel = new GWindowLabel(gwindowdialogclient, 1.0F, 5F, 7F, 1.3F, Plugin.i18n("CAPCycles"), null));
        gwindowdialogclient.addControl(wCAPCycles = new GWindowEditControl(gwindowdialogclient, 9F, 5F, 5F, 1.3F, "") {

            public void afterCreated()
            {
                super.afterCreated();
                bNumericOnly = true;
                bDelayedNotify = true;
            }

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    int k = Integer.parseInt(wCAPCycles.getValue());
                    if(k < 0)
                        k = 0;
                    if(k > 1000)
                        k = 1000;
                    pair.cycles = k;
                    setValue("" + k);
                    PlMission.setChanged();
                }
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(wCAPTimerLabel = new GWindowLabel(gwindowdialogclient, 1.0F, 7F, 7F, 1.3F, Plugin.i18n("CAPTimer"), null));
        gwindowdialogclient.addLabel(wCAPTimerLabelMin = new GWindowLabel(gwindowdialogclient, 15F, 7F, 7F, 1.3F, Plugin.i18n("min"), null));
        gwindowdialogclient.addControl(wCAPTimer = new GWindowEditControl(gwindowdialogclient, 9F, 7F, 5F, 1.3F, "") {

            public void afterCreated()
            {
                super.afterCreated();
                bNumericOnly = true;
                bDelayedNotify = true;
            }

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    int k = Integer.parseInt(wCAPTimer.getValue());
                    if(k < 0)
                        k = 0;
                    if(k > 10000)
                        k = 10000;
                    pair.delayTimer = k;
                    setValue("" + k);
                    PlMission.setChanged();
                }
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(wCAPOrientLabel = new GWindowLabel(gwindowdialogclient, 1.0F, 9F, 7F, 1.3F, Plugin.i18n("CAPAngle"), null));
        gwindowdialogclient.addLabel(wCAPOrientLabelDeg = new GWindowLabel(gwindowdialogclient, 15F, 9F, 7F, 1.3F, Plugin.i18n("CAPdeg"), null));
        gwindowdialogclient.addControl(wCAPOrient = new GWindowEditControl(gwindowdialogclient, 9F, 9F, 5F, 1.3F, "") {

            public void afterCreated()
            {
                super.afterCreated();
                bNumericOnly = true;
                bDelayedNotify = true;
            }

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    int k = Integer.parseInt(wCAPOrient.getValue());
                    if(k < 0)
                        k = 0;
                    if(k > 360)
                        k = 360;
                    pair.orient = k;
                    PlMission.setChanged();
                }
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(wCAPBSizeLabel = new GWindowLabel(gwindowdialogclient, 1.0F, 11F, 7F, 1.3F, Plugin.i18n("CAPBaseSize"), null));
        gwindowdialogclient.addLabel(wCAPBSizeLabelKM = new GWindowLabel(gwindowdialogclient, 15F, 11F, 7F, 1.3F, Plugin.i18n("km"), null));
        gwindowdialogclient.addControl(wCAPBSize = new GWindowEditControl(gwindowdialogclient, 9F, 11F, 5F, 1.3F, "") {

            public void afterCreated()
            {
                super.afterCreated();
                bNumericOnly = true;
                bDelayedNotify = true;
            }

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    int k = Integer.parseInt(wCAPBSize.getValue());
                    if(k < 1)
                        k = 1;
                    if(k > 10000)
                        k = 10000;
                    pair.baseSize = k;
                    setValue("" + k);
                    PlMission.setChanged();
                }
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(wCAPAltDifferenceLabel = new GWindowLabel(gwindowdialogclient, 1.0F, 13F, 7F, 1.3F, Plugin.i18n("CAPAltDifference"), null));
        gwindowdialogclient.addLabel(wCAPAltDifferenceLabelM = new GWindowLabel(gwindowdialogclient, 15F, 13F, 7F, 1.3F, Plugin.i18n("meters"), null));
        gwindowdialogclient.addControl(wCAPAltDifference = new GWindowEditControl(gwindowdialogclient, 9F, 13F, 5F, 1.3F, "") {

            public void afterCreated()
            {
                super.afterCreated();
                bNumericOnly = true;
                bDelayedNotify = true;
            }

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    pair.altDifference = Integer.parseInt(wCAPAltDifference.getValue());
                    PlMission.setChanged();
                }
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(wGAttackNoOptionInfoLabel = new GWindowLabel(gwindowdialogclient, 1.0F, 1.0F, 19F, 1.3F, Plugin.i18n("GAttackNoOptions"), null));
    }

    private void showHideWay2()
    {
        PAir pair = (PAir)Plugin.builder.selectedPoint();
        wTakeOffType.hideWindow();
        wTakeOffTypeLabel.hideWindow();
        wTakeoffSpacing.hideWindow();
        wTakeoffSpacingLabel.hideWindow();
        wTakeoffSpacingLabel2.hideWindow();
        wDelayTimer.hideWindow();
        wDelayTimerLabel.hideWindow();
        wDelayTimerLabel2.hideWindow();
        wLandingType.hideWindow();
        wLandingTypeLabel.hideWindow();
        wNormFlyType.hideWindow();
        wNormFlyTypeLabel.hideWindow();
        wCAPType.hideWindow();
        wCAPTypeLabel.hideWindow();
        wCAPCycles.hideWindow();
        wCAPCyclesLabel.hideWindow();
        wCAPTimer.hideWindow();
        wCAPTimerLabel.hideWindow();
        wCAPTimerLabelMin.hideWindow();
        wCAPOrient.hideWindow();
        wCAPOrientLabel.hideWindow();
        wCAPOrientLabelDeg.hideWindow();
        wCAPBSize.hideWindow();
        wCAPBSizeLabel.hideWindow();
        wCAPBSizeLabelKM.hideWindow();
        wCAPAltDifference.hideWindow();
        wCAPAltDifferenceLabel.hideWindow();
        wCAPAltDifferenceLabelM.hideWindow();
        wGAttackNoOptionInfoLabel.hideWindow();
        switch(pair.type())
        {
        default:
            break;

        case 0: // '\0'
            wNormFlyType.showWindow();
            wNormFlyTypeLabel.showWindow();
            if(wNormFlyType.getSelected() == 1)
            {
                wCAPType.showWindow();
                wCAPTypeLabel.showWindow();
                wCAPCycles.showWindow();
                wCAPCyclesLabel.showWindow();
                wCAPTimer.showWindow();
                wCAPTimerLabel.showWindow();
                wCAPTimerLabelMin.showWindow();
                wCAPOrient.showWindow();
                wCAPOrientLabel.showWindow();
                wCAPOrientLabelDeg.showWindow();
                wCAPBSize.showWindow();
                wCAPBSizeLabel.showWindow();
                wCAPBSizeLabelKM.showWindow();
                wCAPAltDifference.showWindow();
                wCAPAltDifferenceLabel.showWindow();
                wCAPAltDifferenceLabelM.showWindow();
            }
            if(wNormFlyType.getSelected() == 2)
            {
                wCAPCycles.showWindow();
                wCAPCyclesLabel.showWindow();
                wCAPTimer.showWindow();
                wCAPTimerLabel.showWindow();
                wCAPTimerLabelMin.showWindow();
                wCAPOrient.showWindow();
                wCAPOrientLabel.showWindow();
                wCAPOrientLabelDeg.showWindow();
                wCAPBSize.showWindow();
                wCAPBSizeLabel.showWindow();
                wCAPBSizeLabelKM.showWindow();
            }
            break;

        case 1: // '\001'
            wTakeOffType.showWindow();
            wTakeOffTypeLabel.showWindow();
            wDelayTimer.showWindow();
            wDelayTimerLabel.showWindow();
            wDelayTimerLabel2.showWindow();
            if(wTakeOffType.getSelected() > 0)
            {
                wTakeoffSpacing.showWindow();
                wTakeoffSpacingLabel.showWindow();
                wTakeoffSpacingLabel2.showWindow();
            }
            break;

        case 2: // '\002'
            wLandingType.showWindow();
            wLandingTypeLabel.showWindow();
            break;

        case 3: // '\003'
            wGAttackNoOptionInfoLabel.showWindow();
            break;
        }
    }

    private void fillEditEnabled(PathAir pathair)
    {
        makeRegimentList(pathair.getArmy(), pathair.sCountry);
        fillEnabledRegiments(pathair.iRegiment);
        fillEnabledSquads(pathair.regiment(), pathair.iSquadron);
        fillEnabledWings(pathair.squadron(), pathair.iWing);
        defaultArmy[pathair.getArmy()].save(pathair);
    }

    public void fillEditActor()
    {
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        if(pathair == null)
            return;
        fillEditEnabled(pathair);
        wRegiment.setSelected(pathair.iRegiment, true, false);
        wSquadron.setSelected(pathair.iSquadron, true, false);
        wWing.setSelected(pathair.iWing, true, false);
        wArmy.setSelected(pathair.getArmy() - 1, true, false);
        wFuel.setValue("" + pathair.fuel, false);
        wPlanes.setSelected(pathair.planes - 1, true, false);
        if(pathair.skill != -1)
            wSkill.setSelected(pathair.skill, true, false);
        else
            wSkill.setValue(Plugin.i18n("Custom"));
        wWeapons.clear(false);
        lWeapons.clear();
        Class class1 = type[pathair._iType].item[pathair._iItem].clazz;
        String as[] = Aircraft.getWeaponsRegistered(class1);
        String s = type[pathair._iType].item[pathair._iItem].name;
        int i = Aircraft.getWeaponsRegistered(class1).length;
        wWeapons.posEnable = new boolean[i];
        if(as != null && as.length > 0)
        {
            int j = -1;
            for(int k = 0; k < as.length; k++)
            {
                String s1 = as[k];
                wWeapons.add(I18N.weapons(s, s1));
                lWeapons.add(s1);
                if(!Aircraft.isWeaponDateOk(class1, s1))
                    wWeapons.posEnable[k] = false;
                else
                    wWeapons.posEnable[k] = true;
                if(s1.equalsIgnoreCase(pathair.weapons))
                {
                    j = k;
                    if(!wWeapons.posEnable[k])
                        j = 0;
                }
            }

            if(j == -1)
                j = 0;
            wWeapons.setSelected(j, true, false);
        }
    }

    private void fillDialogWay()
    {
        PAir pair = (PAir)Plugin.builder.selectedPoint();
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        int i = pathair.pointIndx(pair);
        wPrev.setEnable(i > 0);
        wNext.setEnable(i < pathair.points() - 1);
        wCur.cap = new GCaption("" + i + "(" + pathair.points() + ")");
        wHeight.setValue("" + (int)pair.height, false);
        wSpeed.setValue("" + (int)pair.speed, false);
        wType.setSelected(pair.type(), true, false);
        fillDialogWay2();
        for(int j = 0; j < PAir.types.length; j++)
            wType.posEnable[j] = true;

        int k = pair.formation;
        if(k <= 0)
            k = 0;
        else
            k--;
        wFormations.setSelected(k, true, false);
        if(i > 0)
        {
            PAir pair1 = (PAir)pathair.point(i - 1);
            if(pair1.type() != 1)
                wType.posEnable[1] = false;
        }
        if(i < pathair.points() - 1)
            wType.posEnable[2] = false;
        _curPointType = pair.type();
        int l = (int)Math.round(pair.time / 60D + (double)(World.getTimeofDay() * 60F));
        wTimeH.setValue("" + (l / 60) % 24, false);
        wTimeM.setValue("" + l % 60, false);
        if(pair.getTarget() != null)
        {
            if(pair.getTarget() instanceof PPoint)
            {
                if(pair.getTarget() instanceof PAir)
                    wTarget.cap.set(((PathAir)pair.getTarget().getOwner()).typedName);
                else
                if(pair.getTarget() instanceof PNodes)
                    wTarget.cap.set(Property.stringValue(pair.getTarget().getOwner(), "i18nName", ""));
                else
                    wTarget.cap.set(pair.getTarget().getOwner().name());
            } else
            {
                wTarget.cap.set(Property.stringValue(pair.getTarget().getClass(), "i18nName", ""));
            }
        } else
        {
            wTarget.cap.set("");
        }
    }

    private void fillDialogWay2()
    {
        PAir pair = (PAir)Plugin.builder.selectedPoint();
        int i = pair.waypointType;
        switch(pair.type())
        {
        default:
            break;

        case 0: // '\0'
            if(i > 400 && i < 407)
            {
                wNormFlyType.setSelected(1, true, false);
                wCAPType.setSelected(i - 401, true, false);
                break;
            }
            if(i == 0)
            {
                wNormFlyType.setSelected(i, true, false);
                break;
            }
            if(i == 407)
                wNormFlyType.setSelected(2, true, false);
            break;

        case 1: // '\001'
            wTakeOffType.setSelected(i <= 0 ? 0 : i - 1, true, false);
            wTakeoffSpacing.setValue("" + pair.takeoffSpacing, false);
            break;

        case 2: // '\002'
            wLandingType.setSelected(i + (i <= 100 ? 0 : -100), true, false);
            break;

        case 3: // '\003'
            pair.waypointType = 0;
            break;
        }
        wDelayTimer.setValue("" + pair.delayTimer, false);
        wTakeoffSpacing.setValue("" + pair.takeoffSpacing, false);
        wCAPCycles.setValue("" + pair.cycles, false);
        wCAPTimer.setValue("" + pair.delayTimer, false);
        wCAPOrient.setValue("" + (int)pair.orient, false);
        wCAPBSize.setValue("" + pair.baseSize, false);
        wCAPAltDifference.setValue("" + pair.altDifference, false);
        showHideWay2();
    }

    public void initEditWay()
    {
        GWindowDialogClient gwindowdialogclient = (GWindowDialogClient)Plugin.builder.wSelect.tabsClient.create(new GWindowDialogClient());
        tabWay = Plugin.builder.wSelect.tabsClient.createTab(Plugin.i18n("Waypoint"), gwindowdialogclient);
        gwindowdialogclient.addControl(wPrev = new GWindowButton(gwindowdialogclient, 1.0F, 1.0F, 5F, 1.6F, Plugin.i18n("&Prev"), null) {

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    int k = pathair.pointIndx(pair);
                    if(k > 0)
                    {
                        Plugin.builder.setSelected(pathair.point(k - 1));
                        fillDialogWay();
                        Plugin.builder.repaint();
                    }
                    fillDialogWay2();
                    return true;
                } else
                {
                    return false;
                }
            }

        }
);
        gwindowdialogclient.addControl(wNext = new GWindowButton(gwindowdialogclient, 9F, 1.0F, 5F, 1.6F, Plugin.i18n("&Next"), null) {

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    int k = pathair.pointIndx(pair);
                    if(k < pathair.points() - 1)
                    {
                        Plugin.builder.setSelected(pathair.point(k + 1));
                        fillDialogWay();
                        Plugin.builder.repaint();
                    }
                    fillDialogWay2();
                    return true;
                } else
                {
                    return false;
                }
            }

        }
);
        gwindowdialogclient.addLabel(wCur = new GWindowLabel(gwindowdialogclient, 15F, 1.0F, 4F, 1.6F, "1(1)", null));
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 3F, 7F, 1.3F, Plugin.i18n("Height"), null));
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 15F, 3F, 4F, 1.3F, Plugin.i18n("[M]"), null));
        gwindowdialogclient.addControl(wHeight = new GWindowEditControl(gwindowdialogclient, 9F, 3F, 5F, 1.3F, "") {

            public void afterCreated()
            {
                super.afterCreated();
                bNumericOnly = true;
                bDelayedNotify = true;
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                    return false;
                PAir pair = (PAir)Plugin.builder.selectedPoint();
                String s = getValue();
                double d = 0.0D;
                try
                {
                    d = Double.parseDouble(s);
                }
                catch(Exception exception)
                {
                    setValue("" + ((PAir)Plugin.builder.selectedPoint()).height, false);
                    return false;
                }
                if(d < 0.0D)
                    d = 0.0D;
                else
                if(d > 12000D)
                    d = 12000D;
                pair.height = d;
                defaultHeight = d;
                PlMission.setChanged();
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 5F, 7F, 1.3F, Plugin.i18n("Speed"), null));
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 15F, 5F, 4F, 1.3F, Plugin.i18n("[kM/H]"), null));
        gwindowdialogclient.addControl(wSpeed = new GWindowEditControl(gwindowdialogclient, 9F, 5F, 5F, 1.3F, "") {

            public void afterCreated()
            {
                super.afterCreated();
                bNumericOnly = true;
                bDelayedNotify = true;
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                    return false;
                PAir pair = (PAir)Plugin.builder.selectedPoint();
                String s = getValue();
                double d = 0.0D;
                try
                {
                    d = Double.parseDouble(s);
                }
                catch(Exception exception)
                {
                    setValue("" + ((PAir)Plugin.builder.selectedPoint()).speed, false);
                    return false;
                }
                PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                if(pair.type() == 1 || pair.type() == 2)
                    d = 0.0D;
                else
                if(d < type[pathair._iType].item[pathair._iItem].speedMin)
                {
                    d = type[pathair._iType].item[pathair._iItem].speedMin;
                    defaultSpeed = d;
                } else
                if(d > type[pathair._iType].item[pathair._iItem].speedMax)
                {
                    d = type[pathair._iType].item[pathair._iItem].speedMax;
                    defaultSpeed = d;
                } else
                {
                    defaultSpeed = d;
                }
                pair.speed = d;
                pathair.computeTimes();
                PlMission.setChanged();
                return false;
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 7F, 7F, 1.3F, Plugin.i18n("Time"), null));
        gwindowdialogclient.addControl(wTimeH = new GWindowEditControl(gwindowdialogclient, 9F, 7F, 2.0F, 1.3F, "") {

            public void afterCreated()
            {
                super.afterCreated();
                bNumericOnly = true;
                bDelayedNotify = true;
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                {
                    return false;
                } else
                {
                    getTimeOut();
                    return false;
                }
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 11.2F, 7F, 1.0F, 1.3F, ":", null));
        gwindowdialogclient.addControl(wTimeM = new GWindowEditControl(gwindowdialogclient, 11.5F, 7F, 2.0F, 1.3F, "") {

            public void afterCreated()
            {
                super.afterCreated();
                bNumericOnly = true;
                bDelayedNotify = true;
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                {
                    return false;
                } else
                {
                    getTimeOut();
                    return false;
                }
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 9F, 7F, 1.3F, Plugin.i18n("lType"), null));
        gwindowdialogclient.addControl(wType = new GWindowComboControl(gwindowdialogclient, 9F, 9F, 7F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
                for(int i = 0; i < PAir.types.length; i++)
                    add(Plugin.i18n(PAir.types[i]));

                boolean aflag[] = new boolean[PAir.types.length];
                for(int j = 0; j < PAir.types.length; j++)
                    aflag[j] = true;

                posEnable = aflag;
            }

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    if(_curPointType != iSelected)
                    {
                        PAir pair = (PAir)Plugin.builder.selectedPoint();
                        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                        int k = pathair.pointIndx(pair);
                        boolean flag = true;
                        if(_curPointType == 1 || _curPointType == 2)
                            flag = false;
                        boolean flag1 = true;
                        if(iSelected == 1 || iSelected == 2)
                            flag1 = false;
                        if(flag != flag1)
                            if(flag1)
                            {
                                pair.height = defaultHeight;
                                pair.speed = defaultSpeed;
                                clampSpeed(pair);
                                wHeight.setValue("" + (int)defaultHeight, false);
                                wSpeed.setValue("" + (int)pair.speed, false);
                            } else
                            {
                                wHeight.setValue("0", false);
                                wSpeed.setValue("0", false);
                                pair.height = 0.0D;
                                pair.speed = 0.0D;
                            }
                        _curPointType = iSelected;
                        pair.setType(iSelected);
                        fillDialogWay2();
                        PlMission.setChanged();
                        if(iSelected == 1 && k == 0 || iSelected == 2)
                        {
                            Airport airport = Airport.nearest(pair.pos.getAbsPoint(), -1, 7);
                            if(airport != null)
                                if(airport.nearestRunway(pair.pos.getAbsPoint(), PlMisAir.nearestRunway))
                                    pair.pos.setAbs(PlMisAir.nearestRunway.getPoint());
                                else
                                    pair.pos.setAbs(airport.pos.getAbsPoint());
                            pair.setTarget(null);
                            pair.sTarget = null;
                            wTarget.cap.set("");
                        } else
                        {
                            Actor actor = pair.getTarget();
                            if(actor != null && (iSelected == 0 && !(actor instanceof PAir) || iSelected == 3 && (actor instanceof PAir)))
                            {
                                pair.setTarget(null);
                                pair.sTarget = null;
                                wTarget.cap.set("");
                            }
                        }
                        pathair = (PathAir)Plugin.builder.selectedPath();
                        pathair.computeTimes();
                    }
                    return true;
                } else
                {
                    return false;
                }
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 19F, 9F, 7F, 1.3F, Plugin.i18n("Formation"), null));
        gwindowdialogclient.addControl(wFormations = new GWindowComboControl(gwindowdialogclient, 25F, 9F, 12F) {

            public void afterCreated()
            {
                super.afterCreated();
                setEditable(false);
                boolean aflag[] = new boolean[PAir.formations.length];
                for(int i = 0; i < PAir.formations.length; i++)
                {
                    add(Plugin.i18n(PAir.formations[i]));
                    aflag[i] = true;
                }

                posEnable = aflag;
            }

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    int k = wFormations.getSelected();
                    if(k < 0)
                        k = 0;
                    if(k > PAir.formations.length - 1)
                        k = PAir.formations.length - 1;
                    if(k == 0)
                        pair.formation = k;
                    else
                        pair.formation = k + 1;
                    PlMission.setChanged();
                    return true;
                } else
                {
                    return false;
                }
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 11F, 7F, 1.3F, Plugin.i18n("RadioSilence"), null));
        gwindowdialogclient.addControl(wRadioSilence = new GWindowCheckBox(gwindowdialogclient, 9F, 11F, null) {

            public void preRender()
            {
                super.preRender();
                PAir pair = (PAir)Plugin.builder.selectedPoint();
                if(pair == null)
                {
                    return;
                } else
                {
                    setChecked(pair.bRadioSilence, false);
                    return;
                }
            }

            public boolean notify(int i, int j)
            {
                if(i != 2)
                {
                    return false;
                } else
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    pair.bRadioSilence = isChecked();
                    PlMission.setChanged();
                    return false;
                }
            }

        }
);
        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 13F, 7F, 1.3F, Plugin.i18n("Target"), null));
        gwindowdialogclient.addLabel(wTarget = new GWindowLabel(gwindowdialogclient, 9F, 13F, 7F, 1.3F, "", null));
        gwindowdialogclient.addControl(wTargetSet = new GWindowButton(gwindowdialogclient, 1.0F, 15F, 5F, 1.6F, Plugin.i18n("&Set"), null) {

            public boolean notify(int i, int j)
            {
                if(i == 2)
                    Plugin.builder.beginSelectTarget();
                return false;
            }

        }
);
        gwindowdialogclient.addControl(wTargetClear = new GWindowButton(gwindowdialogclient, 9F, 15F, 5F, 1.6F, Plugin.i18n("&Clear"), null) {

            public boolean notify(int i, int j)
            {
                if(i == 2)
                {
                    PAir pair = (PAir)Plugin.builder.selectedPoint();
                    pair.setTarget(null);
                    pair.sTarget = null;
                    wTarget.cap.set("");
                    PlMission.setChanged();
                }
                return false;
            }

        }
);
    }

    private void getTimeOut()
    {
        PAir pair = (PAir)Plugin.builder.selectedPoint();
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        String s = wTimeH.getValue();
        double d = 0.0D;
        try
        {
            d = Double.parseDouble(s);
        }
        catch(Exception exception) { }
        if(d < 0.0D)
            d = 0.0D;
        if(d > 23D)
            d = 23D;
        s = wTimeM.getValue();
        double d1 = 0.0D;
        try
        {
            d1 = Double.parseDouble(s);
        }
        catch(Exception exception1) { }
        if(d1 < 0.0D)
            d1 = 0.0D;
        if(d1 > 59D)
            d1 = 59D;
        double d2 = (d * 60D + d1) * 60D - (double)Math.round(World.getTimeofDay() * 60F * 60F);
        if(d2 < 0.0D)
            d2 = 0.0D;
        int i = pathair.pointIndx(pair);
        if(i == 0)
        {
            if(pathair == Path.player)
                pair.time = 0.0D;
            else
                pair.time = d2;
        } else
        if(pair.type() != 2)
        {
            PPoint ppoint = pathair.point(i - 1);
            double d3 = d2 - ppoint.time;
            double d4 = 0.0D;
            if(d3 <= 0.0D)
            {
                d4 = type[pathair._iType].item[pathair._iItem].speedMax;
            } else
            {
                double d5 = ppoint.pos.getAbsPoint().distance(pair.pos.getAbsPoint());
                d4 = ((d5 / d3) * 3600D) / 1000D;
                if(d4 > type[pathair._iType].item[pathair._iItem].speedMax)
                    d4 = type[pathair._iType].item[pathair._iItem].speedMax;
                if(d4 < type[pathair._iType].item[pathair._iItem].speedMin)
                    d4 = type[pathair._iType].item[pathair._iItem].speedMin;
            }
            pair.speed = d4;
        }
        pathair.computeTimes();
        PlMission.setChanged();
    }

    private void fillEditSkin(int i)
    {
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        if(pathair == null)
        {
            return;
        } else
        {
            wSkills[i].setSelected(pathair.skills[i], true, false);
            return;
        }
    }

    private void fillSkins()
    {
        for(int i = 0; i < 4; i++)
        {
            wSkins[i].clear(false);
            wSkins[i].add(Plugin.i18n("Default"));
        }

        try
        {
            String s = Main.cur().netFileServerSkin.primaryPath();
            int j = Plugin.builder.wSelect.comboBox1.getSelected();
            if(j < startComboBox1 || j >= startComboBox1 + type.length)
                return;
            j -= startComboBox1;
            int k = Plugin.builder.wSelect.comboBox2.getSelected();
            String s1 = GUIAirArming.validateFileName(type[j].item[k].name);
            File file = new File(HomePath.toFileSystemName(s + "/" + s1, 0));
            File afile[] = file.listFiles();
            if(afile != null)
            {
                for(int l = 0; l < afile.length; l++)
                {
                    File file1 = afile[l];
                    if(file1.isFile())
                    {
                        String s2 = file1.getName();
                        String s3 = s2.toLowerCase();
                        if(s3.endsWith(".bmp") && s3.length() + s1.length() <= 122)
                        {
//                            int i1 = BmpUtils._squareSizeBMP8Pal_(s + "/" + s1 + "/" + s2);
                            int i1 = BmpUtils.squareSizeBMP8Pal(s + "/" + s1 + "/" + s2);
                            if(i1 == 512 || i1 == 1024)
                            {
                                for(int j1 = 0; j1 < 4; j1++)
                                    wSkins[j1].add(s2);

                            } else
                            {
                                System.out.println("Skin " + s + "/" + s1 + "/" + s2 + " NOT loaded");
                            }
                        }
                    }
                }

            }
        }
        catch(Exception exception)
        {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

    private void syncSkins()
    {
        if(!(Plugin.builder.selectedPath() instanceof PathAir))
            return;
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        if(pathair == null)
            return;
        for(int i = 0; i < 4; i++)
            if(!syncComboControl(wSkins[i], pathair.skins[i]))
                pathair.skins[i] = null;

    }

    private void fillNoseart()
    {
        for(int i = 0; i < 4; i++)
        {
            wNoseart[i].clear(false);
            wNoseart[i].add(Plugin.i18n("None"));
        }

        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        if(pathair == null)
            return;
        Class class1 = type[pathair._iType].item[pathair._iItem].clazz;
        boolean flag = Property.intValue(class1, "noseart", 0) != 1;
        if(!flag)
        {
            int j = pathair.iRegiment;
            if(j < 0 && j >= regimentList.size())
            {
                flag = true;
            } else
            {
                Regiment regiment = (Regiment)regimentList.get(j);
                flag = !"us".equals(regiment.country());
            }
        }
        if(flag)
        {
            for(int k = 0; k < 4; k++)
                wNoseart[k].setEnable(false);

            return;
        }
        for(int l = 0; l < 4; l++)
            wNoseart[l].setEnable(true);

        try
        {
            String s = Main.cur().netFileServerNoseart.primaryPath();
            File file = new File(HomePath.toFileSystemName(s, 0));
            File afile[] = file.listFiles();
            if(afile != null)
            {
                for(int i1 = 0; i1 < afile.length; i1++)
                {
                    File file1 = afile[i1];
                    if(file1.isFile())
                    {
                        String s1 = file1.getName();
                        String s2 = s1.toLowerCase();
                        if(s2.endsWith(".bmp") && s2.length() <= 122)
                            if(BmpUtils.checkBMP8Pal(s + "/" + s1, 256, 512))
                            {
                                for(int j1 = 0; j1 < 4; j1++)
                                    wNoseart[j1].add(s1);

                            } else
                            {
                                System.out.println("Noseart " + s + "/" + s1 + " NOT loaded");
                            }
                    }
                }

            }
        }
        catch(Exception exception)
        {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

    private void syncNoseart()
    {
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        if(pathair == null)
            return;
        for(int i = 0; i < 4; i++)
            if(!syncComboControl(wNoseart[i], pathair.noseart[i]))
                pathair.noseart[i] = null;

    }

    private void fillPilots()
    {
        for(int i = 0; i < 4; i++)
        {
            wPilots[i].clear(false);
            wPilots[i].add(Plugin.i18n("Default"));
        }

        try
        {
            String s = Main.cur().netFileServerPilot.primaryPath();
            File file = new File(HomePath.toFileSystemName(s, 0));
            File afile[] = file.listFiles();
            if(afile != null)
            {
                for(int j = 0; j < afile.length; j++)
                {
                    File file1 = afile[j];
                    if(file1.isFile())
                    {
                        String s1 = file1.getName();
                        String s2 = s1.toLowerCase();
                        if(s2.endsWith(".bmp") && s2.length() <= 122)
                            if(BmpUtils.checkBMP8Pal(s + "/" + s1, 256, 256))
                            {
                                for(int k = 0; k < 4; k++)
                                    wPilots[k].add(s1);

                            } else
                            {
                                System.out.println("Pilot " + s + "/" + s1 + " NOT loaded");
                            }
                    }
                }

            }
        }
        catch(Exception exception)
        {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

    private void syncPilots()
    {
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        if(pathair == null)
            return;
        for(int i = 0; i < 4; i++)
            if(!syncComboControl(wPilots[i], pathair.pilots[i]))
                pathair.pilots[i] = null;

    }

    private boolean syncComboControl(GWindowComboControl gwindowcombocontrol, String s)
    {
        if(s == null)
        {
            gwindowcombocontrol.setSelected(0, true, false);
            return true;
        }
        int i = gwindowcombocontrol.size();
        for(int j = 1; j < i; j++)
        {
            String s1 = gwindowcombocontrol.get(j);
            if(s.equals(s1))
            {
                gwindowcombocontrol.setSelected(j, true, false);
                return true;
            }
        }

        gwindowcombocontrol.setSelected(0, true, false);
        return false;
    }

    private void checkEditSkinTabs()
    {
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        int i = Plugin.builder.wSelect.tabsClient.sizeTabs();
        if(pathair.planes == i - 4)
            return;
        if(pathair.planes > i - 4)
        {
            for(int j = i - 4; j < pathair.planes; j++)
            {
                Plugin.builder.wSelect.tabsClient.addTab(j + 4, tabSkin[j]);
                fillEditSkin(j);
            }

        } else
        {
            int k = Plugin.builder.wSelect.tabsClient.current;
            for(; Plugin.builder.wSelect.tabsClient.sizeTabs() - 4 > pathair.planes; Plugin.builder.wSelect.tabsClient.removeTab(Plugin.builder.wSelect.tabsClient.sizeTabs() - 1));
            Plugin.builder.wSelect.tabsClient.setCurrent(k, false);
            if(pathair == Path.player && pathair.planes >= Path.playerNum)
                Path.playerNum = 0;
        }
    }

    private void checkEditSkinSkills()
    {
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        if(pathair == null)
            return;
        int i = pathair.skills[0];
        wSkills[0].setSelected(pathair.skills[0], true, false);
        pathair.skill = -2;
        for(int j = 1; j < pathair.planes; j++)
        {
            if(pathair.skills[j] != i)
                pathair.skill = -1;
            wSkills[j].setSelected(pathair.skills[j], true, false);
        }

        if(pathair.skill == -1)
        {
            wSkill.setValue(Plugin.i18n("Custom"));
        } else
        {
            pathair.skill = i;
            wSkill.setSelected(pathair.skill, true, false);
        }
    }

    private void resetMesh()
    {
        for(int i = 0; i < 4; i++)
            resetMesh(i);

    }

    private void resetMesh(int i)
    {
        if(Actor.isValid(actorMesh[i]))
        {
            actorMesh[i].destroy();
            actorMesh[i] = null;
        }
    }

    private void checkMesh(int i)
    {
        if(Actor.isValid(actorMesh[i]))
            return;
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        if(pathair == null)
        {
            return;
        } else
        {
            Class class1 = type[pathair._iType].item[pathair._iItem].clazz;
            meshName = Aircraft.getPropertyMesh(class1, pathair.regiment().country());
            createMesh(i);
            PaintScheme paintscheme = Aircraft.getPropertyPaintScheme(class1, pathair.regiment().country());
            paintscheme.prepare(class1, actorMesh[i].hierMesh(), pathair.regiment(), pathair.iSquadron, pathair.iWing, i, pathair.bNumberOn[i]);
            prepareSkin(i, class1, pathair.skins[i]);
            prepareNoseart(i, pathair.noseart[i]);
            preparePilot(i, pathair.pilots[i]);
            return;
        }
    }

    private void createMesh(int i)
    {
        if(Actor.isValid(actorMesh[i]))
            return;
        if(meshName == null)
        {
            return;
        } else
        {
            double d = 20D;
            actorMesh[i] = new ActorSimpleHMesh(meshName);
            d = actorMesh[i].hierMesh().visibilityR();
            actorMesh[i].pos.setAbs(new Orient(90F, 0.0F, 0.0F));
            d *= Math.cos(0.26179938779914941D) / Math.sin(((double)camera3D[i].FOV() * 3.1415926535897931D) / 180D / 2D);
            camera3D[i].pos.setAbs(new Point3d(d, 0.0D, 0.0D), new Orient(180F, 0.0F, 0.0F));
            camera3D[i].pos.reset();
            return;
        }
    }

    private void prepareSkin(int i, Class class1, String s)
    {
        if(!Actor.isValid(actorMesh[i]))
            return;
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        if(s != null)
        {
            s = Main.cur().netFileServerSkin.primaryPath() + "/" + GUIAirArming.validateFileName(Property.stringValue(class1, "keyName", null)) + "/" + s;
            String s1 = "PaintSchemes/Cache/" + Finger.file(0L, s, -1);
            Aircraft.prepareMeshSkin(meshName, actorMesh[i].hierMesh(), s, s1, pathair.regiment());
        } else
        {
            Aircraft.prepareMeshCamouflage(meshName, actorMesh[i].hierMesh(), class1, pathair.regiment());
        }
    }

    private void prepareNoseart(int i, String s)
    {
        if(!Actor.isValid(actorMesh[i]))
            return;
        if(s != null)
        {
            String s1 = Main.cur().netFileServerNoseart.primaryPath() + "/" + s;
            String s2 = s.substring(0, s.length() - 4);
            String s3 = "PaintSchemes/Cache/Noseart0" + s2 + ".tga";
            String s4 = "PaintSchemes/Cache/Noseart0" + s2 + ".mat";
            String s5 = "PaintSchemes/Cache/Noseart1" + s2 + ".tga";
            String s6 = "PaintSchemes/Cache/Noseart1" + s2 + ".mat";
            if(BmpUtils.bmp8PalTo2TGA4(s1, s3, s5))
                Aircraft.prepareMeshNoseart(actorMesh[i].hierMesh(), s4, s6, s3, s5, null);
        }
    }

    private void preparePilot(int i, String s)
    {
        if(!Actor.isValid(actorMesh[i]))
            return;
        if(s != null)
        {
            String s1 = Main.cur().netFileServerPilot.primaryPath() + "/" + s;
            String s2 = s.substring(0, s.length() - 4);
            String s3 = "PaintSchemes/Cache/Pilot" + s2 + ".tga";
            String s4 = "PaintSchemes/Cache/Pilot" + s2 + ".mat";
            if(BmpUtils.bmp8PalToTGA3(s1, s3))
                Aircraft.prepareMeshPilot(actorMesh[i].hierMesh(), 0, s4, s3, null);
        }
    }

    public void dateChanged()
    {
        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
        if(pathair == null)
            return;
        for(int i = 0; i < 4; i++)
            resetMesh(i);

        fillEditActor();
        PlMission.setChanged();
    }

    private void initEditSkin()
    {
        for(_planeIndx = 0; _planeIndx < 4; _planeIndx++)
        {
            GWindowDialogClient gwindowdialogclient = (GWindowDialogClient)Plugin.builder.wSelect.tabsClient.create(new GWindowDialogClient() {

                public void resized()
                {
                    super.resized();
                    wSkills[planeIndx].setSize(win.dx - wSkills[planeIndx].win.x - lookAndFeel().metric(1.0F), wSkills[planeIndx].win.dy);
                    wSkins[planeIndx].setSize(win.dx - wSkins[planeIndx].win.x - lookAndFeel().metric(1.0F), wSkins[planeIndx].win.dy);
                    wNoseart[planeIndx].setSize(win.dx - wNoseart[planeIndx].win.x - lookAndFeel().metric(1.0F), wNoseart[planeIndx].win.dy);
                    wPilots[planeIndx].setSize(win.dx - wPilots[planeIndx].win.x - lookAndFeel().metric(1.0F), wPilots[planeIndx].win.dy);
                    renders[planeIndx].setSize(win.dx - renders[planeIndx].win.x - lookAndFeel().metric(1.0F), win.dy - renders[planeIndx].win.y - lookAndFeel().metric(1.0F));
                }

                int planeIndx;

            
            {
                planeIndx = _planeIndx;
            }
            }
);
            tabSkin[_planeIndx] = Plugin.builder.wSelect.tabsClient.createTab(Plugin.i18n("Plane" + (1 + _planeIndx)), gwindowdialogclient);
            gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 1.0F, 7F, 1.3F, Plugin.i18n("Player"), null));
            gwindowdialogclient.addControl(wPlayer[_planeIndx] = new GWindowCheckBox(gwindowdialogclient, 9F, 1.0F, null) {

                public void preRender()
                {
                    super.preRender();
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    if(pathair == null)
                        return;
                    setChecked(pathair == Path.player && planeIndx == Path.playerNum, false);
                    setEnable(type[pathair._iType].item[pathair._iItem].bEnablePlayer && !pathair.bOnlyAI);
                    if(!isEnable() && isChecked())
                    {
                        setChecked(false, false);
                        Path.player = null;
                        Path.playerNum = 0;
                        PlMission.setChanged();
                    }
                }

                public boolean notify(int i, int j)
                {
                    if(i != 2)
                        return false;
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    if(pathair == null)
                        return false;
                    if(isChecked())
                    {
                        Path.player = pathair;
                        Path.playerNum = planeIndx;
                        PAir pair = (PAir)pathair.point(0);
                        pair.time = 0.0D;
                        PlMission.cur.missionArmy = pathair.getArmy();
                        pathair.computeTimes();
                    } else
                    {
                        Path.player = null;
                        Path.playerNum = 0;
                    }
                    PlMission.setChanged();
                    return false;
                }

                int planeIndx;

            
            {
                planeIndx = _planeIndx;
            }
            }
);
            gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 3F, 7F, 1.3F, Plugin.i18n("Skill"), null));
            gwindowdialogclient.addControl(wSkills[_planeIndx] = new GWindowComboControl(gwindowdialogclient, 9F, 3F, 7F) {

                public void afterCreated()
                {
                    super.afterCreated();
                    setEditable(false);
                    add(Plugin.i18n("Rookie"));
                    add(Plugin.i18n("Average"));
                    add(Plugin.i18n("Veteran"));
                    add(Plugin.i18n("Ace"));
                }

                public boolean notify(int i, int j)
                {
                    if(i != 2)
                    {
                        return false;
                    } else
                    {
                        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                        pathair.skills[planeIndx] = getSelected();
                        checkEditSkinSkills();
                        PlMission.setChanged();
                        return false;
                    }
                }

                int planeIndx;

            
            {
                planeIndx = _planeIndx;
            }
            }
);
            gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 5F, 7F, 1.3F, Plugin.i18n("Skin"), null));
            gwindowdialogclient.addControl(wSkins[_planeIndx] = new GWindowComboControl(gwindowdialogclient, 9F, 5F, 7F) {

                public void afterCreated()
                {
                    super.afterCreated();
                    setEditable(false);
                    add(Plugin.i18n("Default"));
                }

                public boolean notify(int i, int j)
                {
                    if(i != 2)
                        return false;
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    if(getSelected() == 0)
                        pathair.skins[planeIndx] = null;
                    else
                        pathair.skins[planeIndx] = get(getSelected());
                    resetMesh(planeIndx);
                    PlMission.setChanged();
                    return false;
                }

                int planeIndx;

            
            {
                planeIndx = _planeIndx;
            }
            }
);
            gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 7F, 7F, 1.3F, Plugin.i18n("Noseart"), null));
            gwindowdialogclient.addControl(wNoseart[_planeIndx] = new GWindowComboControl(gwindowdialogclient, 9F, 7F, 7F) {

                public void afterCreated()
                {
                    super.afterCreated();
                    setEditable(false);
                    add(Plugin.i18n("None"));
                }

                public boolean notify(int i, int j)
                {
                    if(i != 2)
                        return false;
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    if(getSelected() == 0)
                        pathair.noseart[planeIndx] = null;
                    else
                        pathair.noseart[planeIndx] = get(getSelected());
                    resetMesh(planeIndx);
                    PlMission.setChanged();
                    return false;
                }

                int planeIndx;

            
            {
                planeIndx = _planeIndx;
            }
            }
);
            gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 9F, 7F, 1.3F, Plugin.i18n("Pilot"), null));
            gwindowdialogclient.addControl(wPilots[_planeIndx] = new GWindowComboControl(gwindowdialogclient, 9F, 9F, 7F) {

                public void afterCreated()
                {
                    super.afterCreated();
                    setEditable(false);
                    add(Plugin.i18n("Default"));
                }

                public boolean notify(int i, int j)
                {
                    if(i != 2)
                        return false;
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    if(getSelected() == 0)
                        pathair.pilots[planeIndx] = null;
                    else
                        pathair.pilots[planeIndx] = get(getSelected());
                    resetMesh(planeIndx);
                    PlMission.setChanged();
                    return false;
                }

                int planeIndx;

            
            {
                planeIndx = _planeIndx;
            }
            }
);
            gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 11F, 7F, 1.3F, Plugin.i18n("NumberOn"), null));
            gwindowdialogclient.addControl(wPlayer[_planeIndx] = new GWindowCheckBox(gwindowdialogclient, 9F, 11F, null) {

                public void preRender()
                {
                    super.preRender();
                    PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                    if(pathair == null)
                    {
                        return;
                    } else
                    {
                        setChecked(pathair.bNumberOn[planeIndx], false);
                        return;
                    }
                }

                public boolean notify(int i, int j)
                {
                    if(i != 2)
                    {
                        return false;
                    } else
                    {
                        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                        pathair.bNumberOn[planeIndx] = isChecked();
                        resetMesh(planeIndx);
                        PlMission.setChanged();
                        return false;
                    }
                }

                int planeIndx;

            
            {
                planeIndx = _planeIndx;
            }
            }
);
            gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 1.0F, 13F, 7F, 1.3F, Plugin.i18n("SpawnPoint"), null));
            gwindowdialogclient.addControl(wSpawnPointSet[_planeIndx] = new GWindowButton(gwindowdialogclient, 9F, 12.5F, 5F, 1.6F, Plugin.i18n("&Set"), null) {

                public boolean notify(int i, int j)
                {
                    if(i == 2)
                    {
                        Plugin.builder.beginSelectSpawnPoint();
                        bSpawnFromStationary = true;
                    }
                    return false;
                }

            }
);
            gwindowdialogclient.addControl(wSpawnPointClear[_planeIndx] = new GWindowButton(gwindowdialogclient, 15F, 12.5F, 5F, 1.6F, Plugin.i18n("&Clear"), null) {

                public boolean notify(int i, int j)
                {
                    if(i == 2)
                    {
                        PathAir pathair = (PathAir)Plugin.builder.selectedPath();
                        int k = getSelectedPlaneIndex();
                        pathair.setSpawnPoint(k, null);
                        bSpawnFromStationary = false;
                        for(int l = 0; l < 4; l++)
                        {
                            if(pathair.getSpawnPoint(l) == null)
                                continue;
                            bSpawnFromStationary = true;
                            break;
                        }

                        wSpawnPointLabel[k].cap.set(Plugin.i18n("NotSet"));
                        PlMission.setChanged();
                    }
                    return false;
                }

            }
);
            gwindowdialogclient.addLabel(wSpawnPointLabel[_planeIndx] = new GWindowLabel(gwindowdialogclient, 21F, 13F, 15F, 1.3F, Plugin.i18n("NotSet"), null));
            renders[_planeIndx] = new GUIRenders(gwindowdialogclient, 1.0F, 15F, 17F, 7F, true) {

                public void mouseButton(int i, boolean flag, float f, float f1)
                {
                    super.mouseButton(i, flag, f, f1);
                    if(!flag)
                        return;
                    if(i == 1)
                    {
                        animateMeshA[planeIndx] = animateMeshT[planeIndx] = 0.0F;
                        if(Actor.isValid(actorMesh[planeIndx]))
                            actorMesh[planeIndx].pos.setAbs(new Orient(90F, 0.0F, 0.0F));
                    } else
                    if(i == 0)
                    {
                        f -= win.dx / 2.0F;
                        if(Math.abs(f) < win.dx / 16F)
                            animateMeshA[planeIndx] = 0.0F;
                        else
                            animateMeshA[planeIndx] = (-128F * f) / win.dx;
                        f1 -= win.dy / 2.0F;
                        if(Math.abs(f1) < win.dy / 16F)
                            animateMeshT[planeIndx] = 0.0F;
                        else
                            animateMeshT[planeIndx] = (-128F * f1) / win.dy;
                    }
                }

                int planeIndx;

            
            {
                planeIndx = _planeIndx;
            }
            }
;
            camera3D[_planeIndx] = new Camera3D();
            camera3D[_planeIndx].set(50F, 1.0F, 800F);
            render3D[_planeIndx] = new _Render3D(renders[_planeIndx].renders, 1.0F);
            render3D[_planeIndx].setCamera(camera3D[_planeIndx]);
            LightEnvXY lightenvxy = new LightEnvXY();
            render3D[_planeIndx].setLightEnv(lightenvxy);
            lightenvxy.sun().setLight(0.5F, 0.5F, 1.0F, 1.0F, 1.0F, 0.8F);
            Vector3f vector3f = new Vector3f(-2F, 1.0F, -1F);
            vector3f.normalize();
            lightenvxy.sun().set(vector3f);
        }

    }

    public void freeResources()
    {
        resetMesh();
    }

    public int getSelectedPlaneIndex()
    {
        for(int i = 0; i < tabSkin.length; i++)
            if(tabSkin[i] != null && tabSkin[i].isCurrent())
                return i;

        return -1;
    }

    private void initCountry()
    {
        if(listCountry != null)
            return;
        listCountry = new ArrayList[3];
        mapCountry = new HashMap[3];
        for(int i = 0; i < 3; i++)
        {
            listCountry[i] = new ArrayList();
            mapCountry[i] = new HashMap();
        }

        ResourceBundle resourcebundle;
        try
        {
            resourcebundle = ResourceBundle.getBundle("i18n/country", RTSConf.cur.locale, LDRres.loader());
        }
        catch(Exception exception)
        {
            resourcebundle = null;
        }
        HashMap hashmap = new HashMap();
        List list = Regiment.getAll();
        for(int j = 0; j < list.size(); j++)
        {
            Regiment regiment = (Regiment)list.get(j);
            if(!hashmap.containsKey(regiment.branch()))
            {
                int k = regiment.getArmy();
                if(k >= 0 && k <= 2)
                {
                    hashmap.put(regiment.branch(), null);
                    Country country = new Country();
                    country.name = regiment.branch();
                    if(resourcebundle != null)
                        country.i18nName = resourcebundle.getString(country.name);
                    else
                        country.i18nName = country.name;
                    listCountry[k].add(country);
                    mapCountry[k].put(country.name, new Integer(listCountry[k].size() - 1));
                }
            }
        }

    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw new NoClassDefFoundError(classnotfoundexception.getMessage());
        }
    }

    protected Type type[];
    double defaultHeight;
    double defaultSpeed;
    private DefaultArmy defaultArmy[];
    private int iArmyRegimentList;
    private String sCountry;
    private ArrayList regimentList;
    private PlMission pluginMission;
    private int startComboBox1;
    ViewItem viewType[];
    HashMapInt viewMap;
    private String _actorInfo[];
    com.maddox.gwindow.GWindowTabDialogClient.Tab tabActor;
    com.maddox.gwindow.GWindowTabDialogClient.Tab tabWay;
    com.maddox.gwindow.GWindowTabDialogClient.Tab tabSkin[];
    GWindowComboControl wArmy;
    GWindowComboControl wCountry;
    GWindowComboControl wRegiment;
    GWindowComboControl wSquadron;
    GWindowComboControl wWing;
    GWindowComboControl wWeapons;
    ArrayList lWeapons;
    GWindowEditControl wFuel;
    GWindowComboControl wPlanes;
    GWindowComboControl wSkill;
    GWindowCheckBox wOnlyAI;
    GWindowCheckBox wParachute;
    private Object _squads[];
    private Object _wings[];
    GWindowButton wPrev;
    GWindowButton wNext;
    GWindowLabel wCur;
    GWindowEditControl wHeight;
    GWindowEditControl wSpeed;
    GWindowEditControl wTimeH;
    GWindowEditControl wTimeM;
    GWindowComboControl wType;
    GWindowCheckBox wRadioSilence;
    GWindowLabel wTarget;
    GWindowButton wTargetSet;
    GWindowButton wTargetClear;
    GWindowComboControl wFormations;
    private int _curPointType;
    private static Loc nearestRunway = new Loc();
    GWindowCheckBox wPlayer[];
    GWindowComboControl wSkills[];
    GWindowComboControl wSkins[];
    GWindowComboControl wNoseart[];
    GWindowComboControl wPilots[];
    GWindowButton wSpawnPointSet[];
    GWindowButton wSpawnPointClear[];
    GWindowLabel wSpawnPointLabel[];
    GUIRenders renders[];
    Camera3D camera3D[];
    _Render3D render3D[];
    String meshName;
    ActorSimpleHMesh actorMesh[];
    float animateMeshA[] = {
        0.0F, 0.0F, 0.0F, 0.0F
    };
    float animateMeshT[] = {
        0.0F, 0.0F, 0.0F, 0.0F
    };
    private Orient _orient;
    protected boolean bSpawnFromStationary;
    private int _planeIndx;
    private ArrayList listCountry[];
    private HashMap mapCountry[];
    com.maddox.gwindow.GWindowTabDialogClient.Tab tabWay2;
    GWindowComboControl wLandingType;
    GWindowLabel wLandingTypeLabel;
    GWindowComboControl wTakeOffType;
    GWindowLabel wTakeOffTypeLabel;
    GWindowEditControl wDelayTimer;
    GWindowLabel wDelayTimerLabel;
    GWindowLabel wDelayTimerLabel2;
    GWindowEditControl wTakeoffSpacing;
    GWindowLabel wTakeoffSpacingLabel;
    GWindowLabel wTakeoffSpacingLabel2;
    GWindowComboControl wNormFlyType;
    GWindowLabel wNormFlyTypeLabel;
    GWindowComboControl wCAPType;
    GWindowLabel wCAPTypeLabel;
    GWindowEditControl wCAPCycles;
    GWindowLabel wCAPCyclesLabel;
    GWindowEditControl wCAPTimer;
    GWindowLabel wCAPTimerLabel;
    GWindowLabel wCAPTimerLabelMin;
    GWindowEditControl wCAPOrient;
    GWindowLabel wCAPOrientLabel;
    GWindowLabel wCAPOrientLabelDeg;
    GWindowEditControl wCAPBSize;
    GWindowLabel wCAPBSizeLabel;
    GWindowLabel wCAPBSizeLabelKM;
    GWindowEditControl wCAPAltDifference;
    GWindowLabel wCAPAltDifferenceLabel;
    GWindowLabel wCAPAltDifferenceLabelM;
    GWindowLabel wGAttackNoOptionInfoLabel;

    static 
    {
        Property.set(com.maddox.il2.builder.PlMisAir.class, "name", "MisAir");
    }





























}
