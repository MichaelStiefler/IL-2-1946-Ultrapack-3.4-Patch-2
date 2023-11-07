package com.maddox.il2.objects.air;

import java.util.ArrayList;
import java.util.List;

import com.maddox.JGP.Geom;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector2f;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.engine.hotkey.HookPilot;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.rts.CmdEnv;
import com.maddox.rts.HotKeyEnv;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class CockpitAR_240Radar extends CockpitPilot
{

    protected boolean doFocusEnter()
    {
        if(super.doFocusEnter())
        {
            HookPilot hookpilot = HookPilot.current;
            if(hookpilot.isPadlock())
                hookpilot.stopPadlock();
            hookpilot.reset();
            enter();
            return true;
        } else
        {
            return false;
        }
    }

    protected void doFocusLeave()
    {
        if(!isFocused())
        {
            return;
        } else
        {
            leave();
            super.doFocusLeave();
            return;
        }
    }

    private void enter()
    {
        saveFov = Main3D.FOVX;
        CmdEnv.top().exec("fov 43.2");
        Main3D.cur3D().aircraftHotKeys.setEnableChangeFov(false);
        HotKeyEnv.enable("PanView", false);
        HotKeyEnv.enable("SnapView", false);
        bEntered = true;
    }

    private void leave()
    {
        if(bEntered)
        {
            Main3D.cur3D().aircraftHotKeys.setEnableChangeFov(true);
            CmdEnv.top().exec("fov " + saveFov);
            HookPilot hookpilot = HookPilot.current;
            hookpilot.doAim(false);
            hookpilot.setSimpleAimOrient(0.0F, 0.0F, 0.0F);
            hookpilot.setSimpleUse(false);
            boolean flag = HotKeyEnv.isEnabled("aircraftView");
            HotKeyEnv.enable("PanView", flag);
            HotKeyEnv.enable("SnapView", flag);
            bEntered = false;
        }
    }

    public void destroy()
    {
        super.destroy();
        leave();
    }

    public boolean isToggleUp()
    {
        return false;
    }

    public boolean isToggleAim()
    {
        if(!isFocused())
            return false;
        else
            return radarShortRange;
    }

    public void doToggleAim(boolean flag)
    {
        if(!isFocused())
            return;
        if(radarShortRange == flag)
        {
            return;
        } else
        {
            distanceMax = flag ? 2000F : 8000F;
            beamTurnTime = flag ? 1500L : 3000L;
            radarShortRange = flag;
            return;
        }
    }

    public CockpitAR_240Radar()
    {
        super("3DO/Cockpit/Arado240-Radar/hier.him", "He111");
        distanceMin = 100F;
        distanceMax = 8000F;
        heightMin = 200F;
        radarShortRange = false;
        nTgts = 32;
        ViewX = 0.05F;
        oldBeamAngleInt = 0;
        lAngNoise = 0;
        radarPlane = new ArrayList();
        beamTurnTime = 3000L;
        bEntered = false;
        bNeedSetUp = true;
        maxNumberOfContacts = 41;
        contacts = new Vector2f[4][maxNumberOfContacts];
        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < maxNumberOfContacts; j++)
                contacts[i][j] = new Vector2f();

        }

    }

    public void reflectWorldToInstruments(float f)
    {
        if(this.fm != null)
        {
            if(bNeedSetUp)
            {
                reflectPlaneMats();
                bNeedSetUp = false;
            }
            draw();
        }
    }

    protected void reflectPlaneMats()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        Mat mat1 = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat1);
        mat1 = hiermesh.material(hiermesh.materialFind("Pilot1"));
        this.mesh.materialReplace("Pilot1", mat1);
    }

    public void draw()
    {
        Aircraft aircraft = World.getPlayerAircraft();
        if(Mission.isNet())
            return;
        if(!Actor.isValid(aircraft))
            return;
        if(!Actor.isAlive(aircraft))
            return;
        int i = (int)((float)(Time.current() % beamTurnTime) / ((float)beamTurnTime / 360F));
        Point3d point3d = aircraft.pos.getAbsPoint();
        if(oldBeamAngleInt > i)
        {
            Orient orient = aircraft.pos.getAbsOrient();
            radarPlane.clear();
            List list = Engine.targets();
            for(int k = 0; k < list.size(); k++)
            {
                Actor actor = (Actor)list.get(k);
                if(actor != World.getPlayerAircraft() && (actor instanceof Aircraft))
                {
                    Point3d point3d1 = new Point3d();
                    point3d1.set(actor.pos.getAbsPoint());
                    float f2 = (float)(point3d1.z - World.land().HQ(point3d1.x, point3d1.y));
                    point3d1.sub(point3d);
                    orient.transformInv(point3d1);
                    float f4 = (float)Math.abs(point3d1.x);
                    float f6 = (float)Math.abs(point3d1.y);
                    float f8 = (float)Math.abs(point3d1.z);
                    float f10 = (float)Math.sqrt(f4 * f4 + f6 * f6 + f8 * f8);
                    if(f10 <= distanceMax && f10 >= distanceMin && f2 > heightMin)
                        radarPlane.add(point3d1);
                }
            }

            int l = 0;
            for(int i1 = 0; i1 < radarPlane.size(); i1++)
                if(l <= nTgts)
                {
                    float f3 = (float)((Point3d)radarPlane.get(i1)).x;
                    float f5 = (float)((Point3d)radarPlane.get(i1)).y;
                    float f7 = (float)((Point3d)radarPlane.get(i1)).z;
                    float f9 = (float)(Math.sqrt(f3 * f3 + f5 * f5 + f7 * f7) / Math.sqrt(f3 * f3 + f5 * f5)) / distanceMax / screenPlotFactor;
                    float f11 = f3 * f9;
                    float f12 = f5 * f9;
                    l++;
                    String s2 = "R-Signal" + l;
                    this.mesh.setCurChunk(s2);
                    resetYPRmodifier();
                    Cockpit.xyz[0] = f12;
                    Cockpit.xyz[2] = f11;
                    this.mesh.chunkSetLocate(Cockpit.xyz, Cockpit.ypr);
                    if(!this.mesh.isChunkVisible(s2))
                        this.mesh.chunkVisible(s2, true);
                }

            for(int j1 = l + 1; j1 <= nTgts; j1++)
            {
                String s1 = "R-Signal" + j1;
                if(this.mesh.isChunkVisible(s1))
                    this.mesh.chunkVisible(s1, false);
            }

        }
        if(i != oldBeamAngleInt)
        {
            oldBeamAngleInt = i;
            this.mesh.chunkSetAngles("R-RadarBeam", 0.0F, i, 0.0F);
            this.mesh.chunkSetAngles("R-RadarScreen", 0.0F, i, 0.0F);
            for(int j = lAngNoise + 1; j <= oldBeamAngleInt / 2; j++)
            {
                String s = "R-Noise" + j;
                float f = (float)(point3d.z - World.land().HQ(point3d.x, point3d.y));
                if(World.cur().rnd.nextFloat() > f / 10000F)
                {
                    this.mesh.setCurChunk(s);
                    resetYPRmodifier();
                    float f1 = World.cur().rnd.nextFloat(0.0F, ViewX);
                    Cockpit.xyz[0] = f1 * (float)Math.cos(Geom.DEG2RAD((float)j * 2.0F));
                    Cockpit.xyz[2] = f1 * (float)Math.sin(Geom.DEG2RAD((float)j * 2.0F));
                    this.mesh.chunkSetLocate(Cockpit.xyz, Cockpit.ypr);
                    if(!this.mesh.isChunkVisible(s))
                        this.mesh.chunkVisible(s, false);
                } else
                if(this.mesh.isChunkVisible(s))
                    this.mesh.chunkVisible(s, false);
            }

            lAngNoise = oldBeamAngleInt / 2;
        }
    }

    private boolean bNeedSetUp;
    private float saveFov;
    private boolean bEntered;
    public boolean bChanged;
    public int x0;
    public int y0;
    public Mat mat;
    public int ch;
    public int mt;
    private int nTgts;
    private float ViewX;
    private long beamTurnTime;
    private int maxNumberOfContacts;
    private Vector2f contacts[][];
    private int oldBeamAngleInt;
    private int lAngNoise;
    private ArrayList radarPlane;
    private boolean radarShortRange;
    private static float screenPlotFactor = 25F;
    private float distanceMin;
    private float distanceMax;
    private float heightMin;

    static 
    {
        Property.set(CockpitAR_240Radar.class, "astatePilotIndx", 1);
    }
}
