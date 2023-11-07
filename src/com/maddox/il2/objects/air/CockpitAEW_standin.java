package com.maddox.il2.objects.air;

import java.util.ArrayList;
import java.util.List;

import com.maddox.JGP.Geom;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector2f;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorHMesh;
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

public class CockpitAEW_standin extends CockpitPilot {

    protected boolean doFocusEnter() {
        if (super.doFocusEnter()) {
            HookPilot hookpilot = HookPilot.current;
            if (hookpilot.isPadlock()) {
                hookpilot.stopPadlock();
            }
            hookpilot.reset();
            this.enter();
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
        CmdEnv.top().exec("fov 43.2");
        Main3D.cur3D().aircraftHotKeys.setEnableChangeFov(false);
        HotKeyEnv.enable("PanView", false);
        HotKeyEnv.enable("SnapView", false);
        this.updateTargetsList();
        this.bEntered = true;
    }

    private void leave() {
        if (this.bEntered) {
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
        }
    }

    public void destroy() {
        super.destroy();
        this.leave();
    }

    public boolean isToggleUp() {
        return false;
    }

    public boolean isToggleAim() {
        if (!this.isFocused()) {
            return false;
        }
        return this.radarShortRange;
    }

    public void doToggleAim(boolean flag) {
        if (!this.isFocused()) {
            return;
        }
        if (this.radarShortRange == flag) {
            return;
        }
        this.distanceMax = flag ? AR_234B3.RADAR_RANGE_SHORT : AR_234B3.RADAR_RANGE_NORMAL;
        this.beamTurnTime = flag ? AR_234B3.RADAR_ROTATION_SHORT : AR_234B3.RADAR_ROTATION_NORMAL;
        this.radarShortRange = flag;
        ((AR_234B3) this.aircraft()).setRadarShortRange(flag);
    }

    public CockpitAEW_standin() {
        super("3DO/Cockpit/AEW_standin/hier.him", "He111");
        this.distanceMin = 100;
        this.distanceMax = AR_234B3.RADAR_RANGE_NORMAL;
        this.radarShortRange = false;

        this.ViewX = 0.05F;
        this.oldBeamAngleInt = 0;
        this.lAngNoise = 0;
        this.radarPlane = new ArrayList();
        this.beamTurnTime = AR_234B3.RADAR_ROTATION_NORMAL;
        this.bEntered = false;
        this.bNeedSetUp = true;
        this.maxNumberOfContacts = 41;
        this.contacts = new Vector2f[4][this.maxNumberOfContacts];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < this.maxNumberOfContacts; j++) {
                this.contacts[i][j] = new Vector2f();
            }

        }

    }

    public void reflectWorldToInstruments(float f) {
        if (super.fm != null) {
            if (this.bNeedSetUp) {
                this.reflectPlaneMats();
                this.bNeedSetUp = false;
            }
            this.draw();
        }
    }

    protected void reflectPlaneMats() {
        HierMesh hiermesh = this.aircraft().hierMesh();
        Mat mat1 = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat1);
        mat1 = hiermesh.material(hiermesh.materialFind("Pilot1"));
        this.mesh.materialReplace("Pilot1", mat1);
    }

    private void updateTargetsList() {
        Aircraft playerAircraft = World.getPlayerAircraft();
        Point3d pointPlayerAircraft = playerAircraft.pos.getAbsPoint();
        Orient orientPlayerAircraft = playerAircraft.pos.getAbsOrient();
        this.radarPlane.clear();
        List targetList = Engine.targets();
        for (int targetNum = 0; targetNum < targetList.size(); targetNum++) {
            Actor targetActor = (Actor) targetList.get(targetNum);
            if (targetActor == World.getPlayerAircraft()) {
                continue; // don't track ourselves
            }
            if (!(targetActor instanceof Aircraft)) {
                continue; // track aircraft only
            }
            float scaleFactor = 1.0F;
            ActorHMesh ahm = (ActorHMesh) targetActor;
            if (ahm.mesh() != null) {
                try {
                    scaleFactor = ahm.mesh().collisionR() / 10F;
// System.out.println("targetActor " + targetActor.name() + " scaleFactor " + scaleFactor);
                } catch (Exception e) {
// e.printStackTrace();
                }
            }

            Point3d targetPointRelative = new Point3d();
            targetPointRelative.set(targetActor.pos.getAbsPoint());
            targetPointRelative.sub(pointPlayerAircraft);
            orientPlayerAircraft.transformInv(targetPointRelative);
            float distX = (float) Math.abs(targetPointRelative.x);
            float distY = (float) Math.abs(targetPointRelative.y);
            float distanceToTarget = (float) Math.sqrt((distX * distX) + (distY * distY));
            if ((distanceToTarget <= this.distanceMax) && (distanceToTarget >= this.distanceMin)) {
                targetPointRelative.z = scaleFactor;
                this.radarPlane.add(targetPointRelative);
            }
        }
    }

    public void draw() {
        Aircraft playerAircraft = World.getPlayerAircraft();
        if (Mission.isNet()) {
            return;
        }
        if (!Actor.isValid(playerAircraft)) {
            return;
        }
        if (!Actor.isAlive(playerAircraft)) {
            return;
        }
        int beamAngleInt = (int) ((Time.current() % this.beamTurnTime) / (this.beamTurnTime / 360F));
        if (this.oldBeamAngleInt > beamAngleInt) { // We crossed the 0-degree-mark, recalculate target positions.
            this.updateTargetsList();
        }

        if (beamAngleInt != this.oldBeamAngleInt) {
            for (int targetNumber = 0; targetNumber < MAX_TARGETS; targetNumber++) {
                boolean removeTarget = false;
                if (beamAngleInt > this.oldBeamAngleInt) {
                    if ((this.targetAngle[targetNumber] > this.oldBeamAngleInt) && (this.targetAngle[targetNumber] <= beamAngleInt)) {
                        removeTarget = true;
                    }
                } else {
                    if (this.targetAngle[targetNumber] > this.oldBeamAngleInt) {
                        removeTarget = true;
                    } else if (this.targetAngle[targetNumber] <= beamAngleInt) {
                        removeTarget = true;
                    }
                }
                if (removeTarget) {
                    String trackingMeshName = "R-Signal" + (targetNumber < 9 ? "0" : "") + "" + (targetNumber + 1);
                    if (this.mesh.isChunkVisible(trackingMeshName)) {
                        this.mesh.chunkVisible(trackingMeshName, false);
                    }
                }
            }

            for (int targetNumber = 0; targetNumber < this.radarPlane.size(); targetNumber++) {
                float distX = (float) ((Point3d) this.radarPlane.get(targetNumber)).x;
                float distY = (float) ((Point3d) this.radarPlane.get(targetNumber)).y;
                float plotFactor = (float) (Math.sqrt((distX * distX) + (distY * distY)) / Math.sqrt((distX * distX) + (distY * distY))) / this.distanceMax / screenPlotFactor;
                float plotX = distX * plotFactor;
                float plotY = distY * plotFactor;
                float degree = (float) Math.toDegrees(Math.atan2(distX, distY));
                while (degree < 0F) {
                    degree += 360F;
                }

                boolean addTarget = false;
                if (beamAngleInt > this.oldBeamAngleInt) {
                    if ((degree >= this.oldBeamAngleInt) && (degree <= beamAngleInt)) {
                        addTarget = true;
                    }
                } else {
                    if (degree >= this.oldBeamAngleInt) {
                        addTarget = true;
                    } else if (degree <= beamAngleInt) {
                        addTarget = true;
                    }
                }
                if (!addTarget) {
                    continue;
                }

                this.targetTrackingNumber++;
                if (this.targetTrackingNumber >= MAX_TARGETS) {
                    this.targetTrackingNumber = 0;
                }

                this.targetAngle[this.targetTrackingNumber] = beamAngleInt;
                String trackingMeshName = "R-Signal" + (this.targetTrackingNumber < 9 ? "0" : "") + "" + (this.targetTrackingNumber + 1);
                String trackingMeshMatName = "R-Signal_" + (this.targetTrackingNumber < 9 ? "0" : "") + "" + (this.targetTrackingNumber + 1);
                this.mesh.setCurChunk(trackingMeshName);
                this.resetYPRmodifier();
                Cockpit.xyz[0] = plotY;
                Cockpit.xyz[2] = plotX;
                this.mesh.chunkSetLocate(Cockpit.xyz, Cockpit.ypr);
                if (!this.mesh.isChunkVisible(trackingMeshName)) {
                    this.mesh.chunkVisible(trackingMeshName, true);
                    int scaleFactor = (int) (((Point3d) this.radarPlane.get(targetNumber)).z * 4D);
// System.out.println("Scaling Mesh " + trackingMeshName + " to " + scaleFactor);
                    this.mesh.setCurChunk(trackingMeshName);
                    switch (scaleFactor) {
                        case 0:
                            this.mesh.materialReplace(trackingMeshMatName, "R-Signal_025");
                            break;
                        case 1:
                            this.mesh.materialReplace(trackingMeshMatName, "R-Signal_050");
                            break;
                        case 2:
                            this.mesh.materialReplace(trackingMeshMatName, "R-Signal_075");
                            break;
                        default:
                            this.mesh.materialReplace(trackingMeshMatName, "R-Signal_100");
                            break;
                    }
                }
            }

            this.oldBeamAngleInt = beamAngleInt;
            this.mesh.chunkSetAngles("R-RadarBeam", 0.0F, beamAngleInt, 0.0F);
            this.mesh.chunkSetAngles("R-RadarScreen", 0.0F, beamAngleInt, 0.0F);
            for (int lAngNoisePlot = this.lAngNoise + 1; lAngNoisePlot <= (this.oldBeamAngleInt / 2); lAngNoisePlot++) {
                String noiseChunkName = "R-Noise" + lAngNoisePlot;
                float altitudeInMeters = (float) (playerAircraft.pos.getAbsPoint().z - World.land().HQ(playerAircraft.pos.getAbsPoint().x, playerAircraft.pos.getAbsPoint().y));
                if (World.cur().rnd.nextFloat() > (altitudeInMeters / 10000F)) {
                    this.mesh.setCurChunk(noiseChunkName);
                    this.resetYPRmodifier();
                    float rndDist = World.cur().rnd.nextFloat(0F, this.ViewX);
                    Cockpit.xyz[0] = rndDist * (float) Math.cos(Geom.DEG2RAD(lAngNoisePlot * 2F));
                    Cockpit.xyz[2] = rndDist * (float) Math.sin(Geom.DEG2RAD(lAngNoisePlot * 2F));
                    this.mesh.chunkSetLocate(Cockpit.xyz, Cockpit.ypr);
                    if (!this.mesh.isChunkVisible(noiseChunkName)) {
                        this.mesh.chunkVisible(noiseChunkName, true);
                    }
                } else {
                    if (this.mesh.isChunkVisible(noiseChunkName)) {
                        this.mesh.chunkVisible(noiseChunkName, false);
                    }
                }
            }
            this.lAngNoise = this.oldBeamAngleInt / 2;
        }
    }

    private boolean          bNeedSetUp;
    private float            saveFov;
    private boolean          bEntered;
    public boolean           bChanged;
    public int               x0;
    public int               y0;
    public Mat               mat;
    public int               ch;
    public int               mt;
    private static final int MAX_TARGETS          = 32;
    private int[]            targetAngle          = new int[MAX_TARGETS];
    private float            ViewX;
    private long             beamTurnTime;
    private int              maxNumberOfContacts;
    private Vector2f         contacts[][];
    private int              oldBeamAngleInt;
    private int              lAngNoise;
    private ArrayList        radarPlane;
    private boolean          radarShortRange;
    private static float     screenPlotFactor     = 25F;
    private int              targetTrackingNumber = 0;

    private float            distanceMin;
    private float            distanceMax;

    static {
        Property.set(CockpitAEW_standin.class, "astatePilotIndx", 0);
    }
}
