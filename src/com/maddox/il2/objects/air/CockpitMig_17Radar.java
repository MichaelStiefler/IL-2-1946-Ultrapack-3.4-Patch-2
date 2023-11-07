package com.maddox.il2.objects.air;

import java.util.ArrayList;
import java.util.List;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.engine.hotkey.HookPilot;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.rts.CmdEnv;
import com.maddox.rts.HotKeyEnv;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class CockpitMig_17Radar extends CockpitPilot {
    class Interpolater extends InterpolateRef {

        public boolean tick() {
            if (CockpitMig_17Radar.this.fm != null) {
                long t = Time.current();
                long tB = (long) (t / CockpitMig_17Radar.this.BDiv);
                if (tB != CockpitMig_17Radar.this.tBOld) {
                    CockpitMig_17Radar.this.tBOld = tB;
                    CockpitMig_17Radar.this.resetYPRmodifier();
                    float x = (t % CockpitMig_17Radar.this.BRefresh) / CockpitMig_17Radar.this.BRefresh;
                    Cockpit.xyz[0] = ((2.0F * x) - 1.0F) * CockpitMig_17Radar.this.BRange;
                    CockpitMig_17Radar.this.mesh.chunkSetLocate("R-RadarBeam", Cockpit.xyz, Cockpit.ypr);
                }
            }
            return true;
        }

        Interpolater() {
        }
    }

    protected boolean doFocusEnter() {
        if (super.doFocusEnter()) {
            HookPilot hookpilot = HookPilot.current;
            if (hookpilot.isPadlock()) {
                hookpilot.stopPadlock();
            }
            hookpilot.reset();
            this.enter();
            this.go_top();
            return true;
        } else {
            return false;
        }
    }

    protected void doFocusLeave() {
        if (!this.isFocused()) {
            return;
        } else {
            this.leave();
            super.doFocusLeave();
            return;
        }
    }

    private void enter() {
        this.saveFov = Main3D.FOVX;
        Main3D.cur3D().aircraftHotKeys.setEnableChangeFov(false);
        HotKeyEnv.enable("PanView", false);
        HotKeyEnv.enable("SnapView", false);
        this.bEntered = true;
    }

    private void leave() {
        if (!this.bEntered) {
            return;
        } else {
            Main3D.cur3D().aircraftHotKeys.setEnableChangeFov(true);
            CmdEnv.top().exec("fov " + this.saveFov);
            HookPilot hookpilot = HookPilot.current;
            hookpilot.doAim(false);
            hookpilot.setSimpleAimOrient(0.0F, 0.0F, 0.0F);
            hookpilot.setSimpleUse(false);
            boolean flag = HotKeyEnv.isEnabled("aircraftView");
            HotKeyEnv.enable("PanView", flag);
            HotKeyEnv.enable("SnapView", flag);
            this.bEntered = false;
            return;
        }
    }

    private void go_top() {
        CmdEnv.top().exec("fov 43.2");
        HotKeyEnv.enable("PanView", false);
        HotKeyEnv.enable("SnapView", false);
    }

    public void destroy() {
        super.destroy();
        this.leave();
    }

    public void doToggleAim(boolean flag) {
        if (!this.isFocused()) {
            return;
        }
        if (this.isToggleAim() == flag) {
            return;
        } else {
            return;
        }
    }

    public CockpitMig_17Radar() {
        super("3DO/Cockpit/R-Izumrud/Izumrud.him", "He111");
        this.FOV = 1.0D;
        this.ScX = 0.01D;
        this.ScY = 0.0722D;//0.01D;
        this.FOrigX = 0.0F;
        this.FOrigY = -0.0264F;
        this.nTgts = 10;
        this.RRange = 14000F;
        this.RClose = 5F;
        this.BRange = 0.1F;
        this.BRefresh = 1300;
        this.BSteps = 12;
        this.BDiv = this.BRefresh / this.BSteps;
        this.tBOld = 0L;
        this.radarPlane = new ArrayList();
        this.bEntered = false;
        this.ElevationMaxPositive = 26.0F;
        this.ElevationMinNegative = -16.0F;
    }

    public void reflectWorldToInstruments(float f0) {
        this.draw();
    }

    public void draw() {
        try {
            Aircraft ownaircraft = World.getPlayerAircraft();
            if (!Mission.isNet() && Actor.isValid(ownaircraft) && Actor.isAlive(ownaircraft)) {
                long ti = (Time.current() % 1000L) / 500L;
                if (ti != this.to) {
                    this.to = ti;
                    Point3d pointAC = ownaircraft.pos.getAbsPoint();
                    Orient orientAC = ownaircraft.pos.getAbsOrient();
                    this.radarPlane.clear();
                    List list = Engine.targets();
                    int i = list.size();
                    for (int j = 0; j < i; j++) {
                        Actor actor = (Actor) list.get(j);
                        if (!(actor instanceof Aircraft) || (actor == World.getPlayerAircraft())) {
                            continue;
                        }
                        Point3d pointOrtho = new Point3d();
                        pointOrtho.set(actor.pos.getAbsPoint());
                        pointOrtho.sub(pointAC);
                        orientAC.transformInv(pointOrtho);
                        //pointOrtho.x = GuidedMissileUtils.distanceBetween(ownaircraft, actor);
//                        System.out.println("pointOrtho.x=" + pointOrtho.x);
                        if ((pointOrtho.x > this.RClose) && (pointOrtho.x < this.RRange)) {
                            this.radarPlane.add(pointOrtho);
                        }
                    }

                }
                int i = this.radarPlane.size();
                int nt = 0;
                for (int j = 0; j < i; j++) {
                    double x = ((Point3d) this.radarPlane.get(j)).x;
                    if ((x <= this.RClose) || (nt > this.nTgts)) {
                        continue;
                    }
                    this.FOV = 26D / x;
                    double NewZ = ((Point3d) this.radarPlane.get(j)).z * this.FOV;
                    double NewX = -((Point3d) this.radarPlane.get(j)).y * this.FOV;
                    double NewY = ((Point3d) this.radarPlane.get(j)).x / this.RRange;
//                    System.out.println("FOV=" + FOV + ", NewX=" + NewX + ", NewY=" + NewY + ", NewZ=" + NewZ);
                    if ((NewZ > this.ElevationMaxPositive) || (NewZ < this.ElevationMinNegative)) {
                        continue;
                    }
                    float f = this.FOrigX + (float) (NewX * this.ScX);
                    float f1 = this.FOrigY + (float) (NewY * this.ScY);
                    nt++;
                    String m = "R-Signal" + nt;
                    this.mesh.setCurChunk(m);
                    this.mesh.setScaleXYZ(0.5F, 0.5F, 0.5F);
                    this.resetYPRmodifier();
                    Cockpit.xyz[0] = -f;
                    Cockpit.xyz[2] = f1;
                    this.mesh.chunkSetLocate(Cockpit.xyz, Cockpit.ypr);
                    this.mesh.render();
                    if (!this.mesh.isChunkVisible(m)) {
                        this.mesh.chunkVisible(m, true);
                    }
                }

                for (int j = nt + 1; j <= this.nTgts; j++) {
                    String m = "R-Signal" + j;
                    if (this.mesh.isChunkVisible(m)) {
                        this.mesh.chunkVisible(m, false);
                    }
                }

            }
            
            
//            if (!Time.isPaused()) NewY -= 0.001D;
//            float f1 = this.FOrigY + (float) (NewY * this.ScY);
//            HUD.training("NewY=" + NewY);
//            String m = "R-Signal1";
//            this.mesh.setCurChunk(m);
//            this.mesh.setScaleXYZ(0.5F, 0.5F, 0.5F);
//            this.resetYPRmodifier();
//            Cockpit.xyz[0] = 0;
//            Cockpit.xyz[2] = f1;
//            this.mesh.chunkSetLocate(Cockpit.xyz, Cockpit.ypr);
//            this.mesh.render();
//            if (!this.mesh.isChunkVisible(m)) {
//                this.mesh.chunkVisible(m, true);
//            }

            
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private float     saveFov;
    private boolean   bEntered;
    private long      to;
    private double    FOV;
    private double    ScX;
    private double    ScY;
    private float     FOrigX;
    private float     FOrigY;
    private int       nTgts;
    private float     RRange;
    private float     RClose;
    private float     BRange;
    private int       BRefresh;
    private int       BSteps;
    private float     BDiv;
    private long      tBOld;
    private ArrayList radarPlane;
    private float     ElevationMaxPositive;
    private float     ElevationMinNegative;
    double NewY = 15D;

    static {
        Property.set(CockpitMig_17Radar.class, "astatePilotIndx", 0);
    }
}
