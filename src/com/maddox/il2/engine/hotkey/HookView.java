package com.maddox.il2.engine.hotkey;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorFilter;
import com.maddox.il2.engine.ActorMesh;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookRender;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.engine.Orientation;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HookKeys;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.objects.ActorViewPoint;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.weapons.Bomb;
import com.maddox.il2.objects.weapons.Rocket;
import com.maddox.il2.objects.weapons.RocketBomb;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.HotKeyCmdEnv;
import com.maddox.rts.HotKeyCmdMouseMove;
import com.maddox.rts.HotKeyEnv;
import com.maddox.rts.RTSConf;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.CommonTools;

public class HookView extends HookRender {
    static class ClipFilter implements ActorFilter {

        public boolean isUse(Actor actor, double d) {
            return actor instanceof BigshipGeneric;
        }

        ClipFilter() {
        }
    }

    public boolean getFollow() {
        return this.bFollow;
    }

    public void setFollow(boolean flag) {
        // TODO: +++ Camera Mod 4.12 +++
        if (flag && Config.isUSE_RENDER()) {
            overrideActorChange = true;
            overrideActor = Main3D.cur3D().viewActor();
        } else overrideActorChange = false;

        if (this.bFollow == true && flag == true && this.sameActorSelected == true) {
            this.hookViewIndex++;
            this.setHookView();
        } else this.hookViewIndex = 0;
        this.sameActorSelected = false;
        // TODO: --- Camera Mod 4.12 ---
        this.bFollow = flag;
    }

    protected boolean isUseCommon() {
        return useFlags != 0;
    }

    protected void useCommon(int i, boolean flag) {
        if (this.bUse) useFlags |= i;
        else {
            useFlags &= ~i;
            // TODO: +++ Camera Mod 4.12 +++
            this.sameActorSelected = false;
            lastBaseActor = null;
            overrideActorChange = false;
            overrideActor = null;
            // TODO: --- Camera Mod 4.12 ---
        }
    }

    protected static float minLen() {
        if (_visibleR > 0.0F) return _visibleR + 1.5F;
        else return _minLen;
    }

    public static float defaultLen() {
        if (_visibleR > 0.0F) return _visibleR * 3F;
        else return _defaultLen;
    }

    protected static float maxLen() {
        return _maxLen;
    }

    public float len() {
        return len;
    }

    public void resetGame() {
        // TODO: +++ Camera Mod 4.12 +++
        overrideActorChange = false;
        // TODO: --- Camera Mod 4.12 ---
        lastBaseActor = null;
        _visibleR = -1F;
        _Azimut = Azimut = 0.0F;
        _Tangage = Tangage = 0.0F;
        this.timeViewSet = -2000L;
    }

    public void saveRecordedStates(PrintWriter printwriter) throws Exception {
        printwriter.println(len);
        printwriter.println(Azimut);
        printwriter.println(_Azimut);
        printwriter.println(Tangage);
        printwriter.println(_Tangage);
        printwriter.println(this.o.azimut());
        printwriter.println(this.o.tangage());
        printwriter.println(koofAzimut);
        printwriter.println(koofTangage);
        printwriter.println(koofLen);
        printwriter.println(_minLen);
        printwriter.println(_defaultLen);
        printwriter.println(_maxLen);
        printwriter.println(koofSpeed);
    }

    public void loadRecordedStates(BufferedReader bufferedreader) throws Exception {
        len = Float.parseFloat(bufferedreader.readLine());
        Azimut = Float.parseFloat(bufferedreader.readLine());
        _Azimut = Float.parseFloat(bufferedreader.readLine());
        Tangage = Float.parseFloat(bufferedreader.readLine());
        _Tangage = Float.parseFloat(bufferedreader.readLine());
        this.o.set(Float.parseFloat(bufferedreader.readLine()), Float.parseFloat(bufferedreader.readLine()), 0.0F);
        koofAzimut = Float.parseFloat(bufferedreader.readLine());
        koofTangage = Float.parseFloat(bufferedreader.readLine());
        koofLen = Float.parseFloat(bufferedreader.readLine());
        _minLen = Float.parseFloat(bufferedreader.readLine());
        _defaultLen = Float.parseFloat(bufferedreader.readLine());
        _maxLen = Float.parseFloat(bufferedreader.readLine());
        koofSpeed = Float.parseFloat(bufferedreader.readLine());
    }

    public void reset() {
        // TODO: +++ Camera Mod 4.12 +++
        overrideActorChange = false;
        // TODO: --- Camera Mod 4.12 ---
        this.timeViewSet = -2000L;
        if (AircraftHotKeys.bFirstHotCmd) return;
        else {
            this.set(lastBaseActor, defaultLen(), 0.0F, 0.0F);
            return;
        }
    }

    public void set(Actor actor, float f, float f1) {
        // TODO: +++ Camera Mod 4.12 +++
        if (overrideActorChange && Config.isUSE_RENDER()) {
            actor = overrideActor;
            if (Main3D.cur3D().viewActor() != actor) Main3D.cur3D().setView(actor);
        } else if (Config.isUSE_RENDER()) {
            this.overrideInertia = true;
        }

        if (lastBaseActor == actor && f == 10F && f1 == -10F) this.sameActorSelected = true;
        // TODO: --- Camera Mod 4.12 ---
        this.set(actor, defaultLen() > 5.0F ? defaultLen() : 5.0F, f, f1);
    }

    public void set(Actor actor, float f, float f1, float f2) {
        if (overrideActorChange && Config.isUSE_RENDER()) {
            actor = overrideActor;
            if (Main3D.cur3D().viewActor() != actor) Main3D.cur3D().setView(actor);
        } else if (Config.isUSE_RENDER()) {
            this.overrideInertia = true;
        }
        lastBaseActor = actor;
        _visibleR = -1F;
        if (Actor.isValid(actor) && actor instanceof ActorMesh) _visibleR = ((ActorMesh) actor).mesh().visibilityR();
        this.o.set(f1, f2, 0.0F);

        // TODO: +++ Camera Mod 4.12 +++
        this.oCam.set(0.0F, 0.0F, 0.0F);
        this.pCam.set(0.0F, 0.0F, 0.0F);
        this.oView.set(0.0F, 0.0F, 0.0F);
        this.pView.set(0.0F, 0.0F, 0.0F);
//        pExt.set(0.0D, 0.0D, 0.0D);
//        oExt.set(0.0F, 0.0F, 0.0F);
        // TODO: --- Camera Mod 4.12 ---

        _Azimut = Azimut = 0.0F;
        _Tangage = Tangage = 0.0F;
        prevTime = Time.real();
        len = f;
        if (this.camera != null) {
            Actor actor1 = this.camera.pos.base();
            if (actor1 != null) {
                actor1.pos.getAbs(this.o);
                if((Config.cur.inertiaFollowEnabled || Config.cur.inertiaExternalEnabled) && !bFollow) {
                    this.o.set(f1, f2, 0.0F);
                    this.o.set(this.o.azimut(), this.o.tangage(), 0.0F);
                    this.oCam.set(f1 + 5F, f2 - 5F, 0.0F);
                } else {
                    this.o.increment(f1, f2, 0.0F);
                    this.o.set(this.o.azimut(), this.o.tangage(), 0.0F);
                    this.oCam.set(0.0F, 0.0F, 0.0F);
                }
                this.pCam.set(0.0F, 0.0F, 0.0F);
            }
            if (this.bUse) this.camera.pos.inValidate(true);
        }
        this.o.wrap();
    }

    private float bvalue(float f, float f1, long l) {
        float f2 = koofSpeed * l / 30F;
        if (f == f1) return f;
        if (f > f1) if (f < f1 + f2) return f;
        else return f1 + f2;
        if (f > f1 - f2) return f;
        else return f1 - f2;
    }

    public boolean computeRenderPos(Actor actor, Loc loc, Loc loc1) {
        long l = Time.currentReal();
//        // TODO: +++ Camera Mod 4.12 +++
        if (Config.isUSE_RENDER() && RTSConf.cur.keyboard.isPressed(vkCapsLock)) {
            if (Main3D.cur3D().viewActor() instanceof Aircraft) {
                Aircraft viewAircraft = (Aircraft) Main3D.cur3D().viewActor();
                if (viewAircraft.FM.crew > 0) {
                    if (!viewAircraft.FM.AS.isPilotDead(0)) {
                        // We're watching an aircraft, it has a pilot and the pilot isn't dead.
                        int oldCurChunk = viewAircraft.hierMesh().getCurChunk();
                        viewAircraft.hierMesh().setCurChunk("Head1_D0");
                        viewAircraft.hierMesh().getChunkLocObj(this.headLoc);

                        Orient orientViewAircraft = new Orient();
                        orientViewAircraft.set(viewAircraft.pos.getAbsOrient());
                        Vector3d v3dHead = new Vector3d(this.headLoc.getPoint()); // take Head Offset into account
                        orientViewAircraft.transform(v3dHead); // take Head Offset into account
                        Point3d headPosAbs = new Point3d(viewAircraft.pos.getAbsPoint());
                        headPosAbs.add(v3dHead); // take Head Offset into account

                        Point3d p3dCam = new Point3d(this.pAbs);
                        p3dCam.add(this.pCam);
                        p3dCam.sub(headPosAbs);
                        viewAircraft.pos.getAbsOrient().transformInv(p3dCam);

                        float angleAzimuth = 0F;
                        float angleTangage = 0F;
                        if (p3dCam.x == 0D) {
                            if (p3dCam.y == 0D)
                                angleAzimuth = 0F;
                            else if (p3dCam.y > 0D)
                                angleAzimuth = 90F;
                            else
                                angleAzimuth = -90F;
                        } else if (p3dCam.x > 0D) {
                            angleAzimuth = (float) Math.toDegrees(Math.atan(p3dCam.y / p3dCam.x));
                        } else {
                            if (p3dCam.y < 0D)
                                angleAzimuth = -180F + (float) Math.toDegrees(Math.atan(p3dCam.y / p3dCam.x));
                            else
                                angleAzimuth = 180F + (float) Math.toDegrees(Math.atan(p3dCam.y / p3dCam.x));
                        }

                        double xyDistance = Math.sqrt(p3dCam.x * p3dCam.x + p3dCam.y * p3dCam.y);
                        angleTangage = (float) Math.toDegrees(Math.atan(p3dCam.z / xyDistance));

                        if (angleAzimuth < -150F)
                            angleAzimuth = -150F;
                        if (angleAzimuth > 150F)
                            angleAzimuth = 150F;
                        if (angleTangage < -70F)
                            angleTangage = -70F;
                        if (angleTangage > 85F)
                            angleTangage = 85F;

                        this.movePilotsHead(viewAircraft, angleAzimuth, angleTangage);
                        viewAircraft.hierMesh().setCurChunk(oldCurChunk);
                    }
                }
            }
        }
//        // TODO: --- Camera Mod 4.12 ---

        if (l != prevTime) {
            long l1 = l - prevTime;
            prevTime = l;
            // TODO: Fixed by SAS~Storebror: Replace test for floating point equality by equality within a certain range, due to rounding errors in floating point calculations.
//            if (_Azimut != Azimut || _Tangage != Tangage) {
//            if (Math.abs(_Azimut - Azimut) > .0000001F || Math.abs(_Tangage - Tangage) > .0000001F) {
            if (!CommonTools.equals(_Azimut, Azimut) || !CommonTools.equals(_Tangage, Tangage)) {
                Azimut = this.bvalue(_Azimut, Azimut, l1);
                Tangage = this.bvalue(_Tangage, Tangage, l1);
                loc.get(this.o);
                float f = this.o.azimut() + Azimut;
                this.o.set(f, Tangage, 0.0F);
                this.o.wrap360();
            }
            // TODO: Fixed by SAS~Storebror: Replace test for floating point equality by equality within a certain range, due to rounding errors in floating point calculations.
//            if (_Azimut == Azimut && (Azimut > 180F || Azimut < -180F)) {
//            if (Math.abs(_Azimut - Azimut) < .0000001F && (Azimut > 180F || Azimut < -180F)) {
            if (CommonTools.equals(_Azimut, Azimut) && (Azimut > 180F || Azimut < -180F)) {
                Azimut %= Circ;
                if (Azimut > 180F) Azimut -= Circ;
                else if (Azimut < -180F) Azimut += Circ;
                _Azimut = Azimut;
            }
            // TODO: Fixed by SAS~Storebror: Replace test for floating point equality by equality within a certain range, due to rounding errors in floating point calculations.
//            if (_Tangage == Tangage && (Tangage > 180F || Tangage < -180F)) {
//            if (Math.abs(_Tangage - Tangage) < .0000001F && (Tangage > 180F || Tangage < -180F)) {
            if (CommonTools.equals(_Tangage, Tangage) && (Tangage > 180F || Tangage < -180F)) {
                Tangage %= Circ;
                if (Tangage > 180F) Tangage -= Circ;
                else if (Tangage < -180F) Tangage += Circ;
                _Tangage = Tangage;
            }
        }
        this.computePos(actor, loc, loc1);
        return true;
    }

//    public void computePos(Actor actor, Loc loc, Loc loc1) {
//        // TODO: +++ Camera Mod 4.12 +++
//        if (this.hookViewIndex == 0) {
//            // TODO: --- Camera Mod 4.12 ---
//
//            loc.get(this.pAbs, this.oAbs);
//            if (this.bUse) {
//                if (lastBaseActor != actor) {
//                    lastBaseActor = actor;
//                    _visibleR = -1F;
//                    if (Actor.isValid(actor) && actor instanceof ActorMesh) _visibleR = ((ActorMesh) actor).mesh().visibilityR();
//                }
//                this.p.set(-len, 0.0D, 0.0D);
//                if (this.bFollow) this.o.set(this.oAbs.getAzimut(), this.o.getTangage(), this.o.getKren());
//                this.o.transform(this.p);
//
//                // TODO: +++ Camera Mod 4.12 +++
//                this.oView.set(this.o);
//                this.oView.increment(this.oCam);
//                // TODO: --- Camera Mod 4.12 ---
//
//                this.pAbs.add(this.p);
//                if (this.bClipOnLand) {
//                    double d = Engine.land().HQ_Air(this.pAbs.x, this.pAbs.y) + 2D;
//                    if (this.pAbs.z < d) this.pAbs.z = d;
//                    pClipZ1.set(this.pAbs);
//                    pClipZ2.set(this.pAbs);
//                    pClipZ1.z -= 2D;
//                    pClipZ2.z += 42D;
//                    Actor actor1 = Engine.collideEnv().getLine(pClipZ2, pClipZ1, false, clipFilter, pClipRes);
//                    if (Actor.isValid(actor1) && this.pAbs.z < pClipRes.z + 2D) this.pAbs.z = pClipRes.z + 2D;
//                }
//                // TODO: +++ Camera Mod 4.12 +++
//                this.pView.set(this.pAbs);
//                this.pView.add(this.pCam);
//                loc1.set(this.pView, this.oView);
//                // loc1.set(pAbs, o);
//                // TODO: --- Camera Mod 4.12 ---
//            } else loc1.set(this.pAbs, this.oAbs);
//
//            // TODO: +++ Camera Mod 4.12 +++
//        } else {
//            loc.get(this.pAbs, this.oAbs);
//            this.p.set(this.pExt);
//            this.oAbs.transform(this.p);
//            this.pAbs.add(this.p);
//            this.oAbs.increment(this.oExt);
//            this.oAbs.increment(this.oCam);
//            this.oView.set(this.oExt);
//            this.oView.increment(this.oCam);
//            loc1.set(this.pAbs, this.oAbs);
//        }
//        // TODO: --- Camera Mod 4.12 ---
//
//        // TODO: +++ Camera Mod 4.12 +++
//        if (Config.isUSE_RENDER() && RTSConf.cur.keyboard.isPressed(vkCapsLock)) if (Main3D.cur3D().viewActor() instanceof Aircraft) {
//            Aircraft viewAircraft = (Aircraft) Main3D.cur3D().viewActor();
//            if (viewAircraft.FM.crew > 0) if (!viewAircraft.FM.AS.isPilotDead(0)) {
//                // We're watching an aircraft, it has a pilot and the pilot isn't dead.
//                int oldCurChunk = viewAircraft.hierMesh().getCurChunk();
//                viewAircraft.hierMesh().setCurChunk("Head1_D0");
//                viewAircraft.hierMesh().getChunkLocObj(this.headLoc);
//
//                Orient orientViewAircraft = new Orient();
//                orientViewAircraft.set(viewAircraft.pos.getAbsOrient());
//                Vector3d v3dHead = new Vector3d(this.headLoc.getPoint()); // take Head Offset into account
//                orientViewAircraft.transform(v3dHead); // take Head Offset into account
//                Point3d headPosAbs = new Point3d(viewAircraft.pos.getAbsPoint());
//                headPosAbs.add(v3dHead); // take Head Offset into account
//
//                Point3d p3dCam = new Point3d(this.pAbs);
//                p3dCam.add(this.pCam);
//                p3dCam.sub(headPosAbs);
//                viewAircraft.pos.getAbsOrient().transformInv(p3dCam);
//
//                float angleAzimuth = 0F;
//                float angleTangage = 0F;
//                if (p3dCam.x == 0D) {
//                    if (p3dCam.y == 0D) angleAzimuth = 0F;
//                    else if (p3dCam.y > 0D) angleAzimuth = 90F;
//                    else angleAzimuth = -90F;
//                } else if (p3dCam.x > 0D) angleAzimuth = (float) Math.toDegrees(Math.atan(p3dCam.y / p3dCam.x));
//                else if (p3dCam.y < 0D) angleAzimuth = -180F + (float) Math.toDegrees(Math.atan(p3dCam.y / p3dCam.x));
//                else angleAzimuth = 180F + (float) Math.toDegrees(Math.atan(p3dCam.y / p3dCam.x));
//
//                double xyDistance = Math.sqrt(p3dCam.x * p3dCam.x + p3dCam.y * p3dCam.y);
//                angleTangage = (float) Math.toDegrees(Math.atan(p3dCam.z / xyDistance));
//
//                if (angleAzimuth < -150F) angleAzimuth = -150F;
//                if (angleAzimuth > 150F) angleAzimuth = 150F;
//                if (angleTangage < -70F) angleTangage = -70F;
//                if (angleTangage > 85F) angleTangage = 85F;
//
//                this.movePilotsHead(viewAircraft, angleAzimuth, angleTangage);
//                viewAircraft.hierMesh().setCurChunk(oldCurChunk);
//            }
//        }
//    }
    
    
    
    public void computePos(Actor actor, Loc loc, Loc loc1)
    {
//      System.out.println("" + actor.hashCode() + " loc x=" + loc.getX() + ", y=" + loc.getY() + ", z=" + loc.getZ() + ", a=" + loc.getAzimut() + ", t=" + loc.getTangage() + ", k=" + loc.getKren());
//      System.out.println("" + actor.hashCode() + " loc1 x=" + loc1.getX() + ", y=" + loc1.getY() + ", z=" + loc1.getZ() + ", a=" + loc1.getAzimut() + ", t=" + loc1.getTangage() + ", k=" + loc1.getKren());
//        bInertia = false;
//        if(HookKeys.current.isPanView() && (/*actor == myLatestReleasedOrdinance() || */(actor instanceof Aircraft) && !bFollow))
//        System.out.println("computePos isPanView=" + HookKeys.current.isPanView() + ", inertiaFollowEnabled=" + Config.cur.inertiaFollowEnabled + ", inertiaExternalEnabled=" + Config.cur.inertiaExternalEnabled + ", bFollow=" + bFollow);
        
        if(Mission.isPlaying())
        {
            if(!HotKeyEnv.isEnabled("SnapView"))
            {
                if(bFollow && (actor instanceof Aircraft))
                    computeFollowPos(actor, loc, loc1);
                else
                    computeDirectPos(actor, loc, loc1);
            } else
            {
                if(/*HookKeys.current.isPanView() && */(Config.cur.inertiaFollowEnabled || Config.cur.inertiaExternalEnabled) /*&& actor instanceof Aircraft*/ && !bFollow)
                    computeInertiaPos(actor, loc, loc1);
                else
                if((actor instanceof Aircraft) && bFollow)
                    computeFollowPos(actor, loc, loc1);
                else
                    computeDirectPos(actor, loc, loc1);
            }
        } else
        {
            computeDirectPos(actor, loc, loc1);
        }
    }

    public void computeFollowPos(Actor actor, Loc loc, Loc loc1)
    {
        if(hookViewIndex == 0)
        {
            loc.get(pAbs, oAbs);
            if(bUse)
            {
                if(lastBaseActor != actor)
                {
                    lastBaseActor = actor;
                    _visibleR = -1F;
                    if(Actor.isValid(actor) && (actor instanceof ActorMesh))
                        _visibleR = ((ActorMesh)actor).mesh().visibilityR();
                }
                p.set(-len, 0.0D, 0.0D);
                o.set(oAbs.getAzimut(), o.getTangage(), o.getKren());
                o.transform(p);
                oView.set(o);
                oView.increment(oCam);
                pAbs.add(p);
                if(bClipOnLand)
                {
                    double d = Engine.land().HQ_Air(pAbs.x, pAbs.y) + 2D;
                    if(pAbs.z < d)
                        pAbs.z = d;
                    pClipZ1.set(pAbs);
                    pClipZ2.set(pAbs);
                    pClipZ1.z -= 2D;
                    pClipZ2.z += 42D;
                    Actor actor1 = Engine.collideEnv().getLine(pClipZ2, pClipZ1, false, clipFilter, pClipRes);
                    if(Actor.isValid(actor1) && pAbs.z < pClipRes.z + 2D)
                        pAbs.z = pClipRes.z + 2D;
                }
                pView.set(pAbs);
                pView.add(pCam);
                loc1.set(pView, oView);
            } else
            {
                loc1.set(pAbs, oAbs);
            }
        } else
        {
            loc.get(pAbs, oAbs);
            p.set(pExt);
            oAbs.transform(p);
            pAbs.add(p);
            if(bClipOnLand)
            {
                double d1 = Engine.land().HQ_Air(pAbs.x, pAbs.y) + 2D;
                if(pAbs.z < d1)
                    pAbs.z = d1;
                pClipZ1.set(pAbs);
                pClipZ2.set(pAbs);
                pClipZ1.z -= 2D;
                pClipZ2.z += 42D;
                Actor actor2 = Engine.collideEnv().getLine(pClipZ2, pClipZ1, false, clipFilter, pClipRes);
                if(Actor.isValid(actor2) && pAbs.z < pClipRes.z + 2D)
                    pAbs.z = pClipRes.z + 2D;
            }
            oAbs.increment(oExt);
            oAbs.increment(oCam);
            oView.set(oExt);
            oView.increment(oCam);
            loc1.set(pAbs, oAbs);
        }
    }

    public void computeInertiaPos(Actor actor, Loc loc, Loc loc1)
    {
//        bInertia = true;
//        if (HookKeys.current.isPanView()) System.out.println("computeInertiaPos isPanView = true!");
        long l = Time.currentReal();
        float f = (float)(l - pTime) / 10F;
        pTime = l;
        loc.get(pAbs, oAbs);
        if(bUse)
        {
            if(lastBaseActor != actor)
            {
                lastBaseActor = actor;
                _visibleR = -1F;
                if(Actor.isValid(actor) && (actor instanceof ActorMesh))
                    _visibleR = ((ActorMesh)actor).mesh().visibilityR();
            }
            p.set(-len, 0.0D, 0.0D);
            if(Config.cur.inertiaExternalEnabled && Config.cur.inertiaExternalValue * f <= 1.0F && !this.overrideInertia)
                oIn.interpolate(oIn, o, Config.cur.inertiaExternalValue * f);
            else
                oIn.interpolate(oIn, o, 1F);
                //oIn.set(o);
            oIn.wrap360();
            oIn.transform(p);
            if(Time.isPaused())
                f *= 10F;
            if(actor != null)
                if(Actor.isValid(actor) && (actor instanceof ActorMesh))
                {
                    if(!actor.isAlive())
                        oAbs.set(oAbs.getAzimut(), 0.0F, 0.0F);
                    else
                    if(actor instanceof Aircraft) {
                        Aircraft aircraft = (Aircraft)actor;
                        if(aircraft.FM.Gears.getLandingState() > 0.0F)
                            oAbs.set(oAbs.getAzimut(), 0.0F, 0.0F);
                        else
                        if(aircraft.FM.getAOA() > aircraft.FM.AOA_Crit)
                            f *= 5F;
                    }
                } else
                {
                    oAbs.set(oAbs.getAzimut(), 0.0F, 0.0F);
                }
            if(Config.cur.inertiaFollowEnabled && Config.cur.inertiaFollowAngleValue * f <= 1.0F && !this.overrideInertia)
                oAbsIn.interpolate(oAbsIn, oAbs, Config.cur.inertiaFollowAngleValue * f);
            else
                oAbsIn.interpolate(oAbsIn, oAbs, 1F);
//                oAbsIn.set(oAbs);
            oAbsIn.wrap360();
            orv.set(oAbsIn);
            orv.increment(oIn);
            orv.increment(o6DOF);
            oAbs.wrap360();
//            if(oAdj || oAdjM)
//            {
//                oAbs.transform(p);
//                oAbsIn.set(oAbs);
//                lorv.set(oAbsIn);
//                lp.set(p);
//                oAdj = oAdjM = false;
//            } else
//            {
            oAbsIn.transform(p);

            // Reset roll when following land/sea actors

            if(!(actor instanceof Aircraft) && !(actor instanceof Bomb) && !(actor instanceof Rocket) && !(actor instanceof RocketBomb))
                orv.setRoll(0.0F);
            
            if(Config.cur.inertiaFollowEnabled && Config.cur.inertiaFollowAngleValue * f <= 1.0F && !this.overrideInertia)
                orv.interpolate(lorv, orv, Config.cur.inertiaFollowAngleValue * f);
            else
                orv.interpolate(lorv, orv, 1F);
//                orv.set(lorv);
            orv.wrap360();
            lorv.set(orv);
            oAbs.set(orv);
            if(Config.cur.inertiaFollowEnabled && Config.cur.inertiaFollowPositionValue * f <= 1.0F && !this.overrideInertia)
                lp.interpolate(lp, p, Config.cur.inertiaFollowPositionValue * f);
            else
                lp.interpolate(lp, p, 1F);
//                lp.set(p);
            p.set(lp);
//            }
            pAbs.add(p);
            if(bClipOnLand)
            {
                double d = Engine.land().HQ_Air(pAbs.x, pAbs.y) + 2D;
                if(pAbs.z < d)
                    pAbs.z = d;
                pClipZ1.set(pAbs);
                pClipZ2.set(pAbs);
                pClipZ1.z -= 2D;
                pClipZ2.z += 42D;
                Actor actor1 = Engine.collideEnv().getLine(pClipZ2, pClipZ1, false, clipFilter, pClipRes);
                if(Actor.isValid(actor1) && pAbs.z < pClipRes.z + 2D)
                    pAbs.z = pClipRes.z + 2D;
            }
            loc1.set(pAbs, oAbs);
            this.overrideInertia = false;
        } else
        {
            loc1.set(pAbs, oAbs);
        }
    }

    public void computeDirectPos(Actor actor, Loc loc, Loc loc1)
    {
        loc.get(pAbs, oAbs);
        if(bUse)
        {
            if(lastBaseActor != actor)
            {
                lastBaseActor = actor;
                _visibleR = -1F;
                if(Actor.isValid(actor) && (actor instanceof ActorMesh))
                    _visibleR = ((ActorMesh)actor).mesh().visibilityR();
            }
            p.set(-len, 0.0D, 0.0D);
            o.transform(p);
            oView.set(o);
            oView.increment(oCam);
            pAbs.add(p);
            if(bClipOnLand)
            {
                double d = Engine.land().HQ_Air(pAbs.x, pAbs.y) + 2D;
                if(pAbs.z < d)
                    pAbs.z = d;
                pClipZ1.set(pAbs);
                pClipZ2.set(pAbs);
                pClipZ1.z -= 2D;
                pClipZ2.z += 42D;
                Actor actor1 = Engine.collideEnv().getLine(pClipZ2, pClipZ1, false, clipFilter, pClipRes);
                if(Actor.isValid(actor1) && pAbs.z < pClipRes.z + 2D)
                    pAbs.z = pClipRes.z + 2D;
            }
            pView.set(pAbs);
            pView.add(pCam);
            loc1.set(pView, oView);
        } else
        {
            loc1.set(pAbs, oAbs);
        }
    }


    

    public void setCamera(Actor actor) {
        this.camera = actor;
    }

    public boolean isModMode() {
//        System.out.println("isModMode=" + (!(Config.cur.inertiaFollowEnabled || Config.cur.inertiaExternalEnabled) && HookKeys.current.isPanView() != this.normalPanViewState));
        return !(Config.cur.inertiaFollowEnabled || Config.cur.inertiaExternalEnabled) && HookKeys.current.isPanView() != this.normalPanViewState;
    }

    public void use(boolean flag) {
//        System.out.println("### use (" + flag + "), bUse=" + this.bUse + ", lastCockpitPanViewState=" + this.lastCockpitPanViewState + " ###");
//        HUD.log("### use (" + flag + ") ###");
        // TODO: When switching views (internal/external) default to "normal" view mode automatically.
        if (flag) {
            this.normalPanViewState = HookKeys.current == null ? false : HookKeys.current.isPanView();
            if (!this.bUse) this.lastCockpitPanViewState = this.normalPanViewState;
        } else if (this.bUse) HookKeys.current.setMode(this.lastCockpitPanViewState);

        this.bUse = flag;
        if (this.camera != null) this.camera.pos.inValidate(true);
        this.useCommon(1, this.bUse);
    }

    public boolean isUse() {
        return this.bUse;
    }

    public static void useMouse(boolean flag) {
        bUseMouse = flag;
    }

    public static boolean isUseMouse() {
        return bUseMouse;
    }

    public boolean clipOnLand(boolean flag) {
        boolean flag1 = this.bClipOnLand;
        this.bClipOnLand = flag;
        return flag1;
    }

    protected void clipLen(Actor actor) {
        if (len < minLen()) if (Actor.isValid(actor) && actor instanceof ActorViewPoint) {
            if (len < -maxLen()) len = -maxLen();
        } else len = minLen();
        if (len > maxLen()) len = maxLen();
    }

    protected void mouseMove(int i, int j, int k) {
        if (bUseMouse && this.isUseCommon()) {
            // TODO: +++ Camera Mod 4.12 +++

            if (this.isModMode() && this.bUse && !this.bFollow) {
                if (Time.real() < this.timeViewSet + 1000L) return;
                if (k != 0) {
                    if (k > 0 && mouseSpeedFactor < 100F) mouseSpeedFactor *= mouseSpeedFactorMultiplier;
                    if (k < 0 && mouseSpeedFactor > 0.1F) mouseSpeedFactor /= mouseSpeedFactorMultiplier;
                }

                if (RTSConf.cur.keyboard.isPressed(vkShift)) {
                    this.pCamOffset.set(0.0F, -i * mouseSpeedFactor * 0.1F, j * mouseSpeedFactor * 0.1F);
                    this.oView.transform(this.pCamOffset);
                    this.pCam.add(this.pCamOffset);
                } else if (RTSConf.cur.keyboard.isPressed(vkCtrl)) {
                    // Don't directly set Orient axis fields here, this might be misleading if Orient doesn't represent
                    // The standard orthogonal vector system but is twisted in any direction.
                    // Use the builtin "increment" method which perfectly respects any existing vector direction
                    this.oCam.increment(0F, 0F, i * koofKren);
                    this.oCam.wrap360();
                    this.pCamOffset.set(j * mouseSpeedFactor * 0.1F, 0.0F, 0.0F);
                    this.oView.transform(this.pCamOffset);
                    this.pCam.add(this.pCamOffset);
                } else if (RTSConf.cur.keyboard.isPressed(vkAlt)) {
                    if (bChangeLen) {
                        len += j * koofLen;
                        this.clipLen(lastBaseActor);
                    } else {
                        if (Time.real() < this.timeViewSet + 1000L) return;
                        if (this.bFollow) i = 0;
                        if (this.bUse) {
                            this.o.set(this.o.azimut() + i * koofAzimut, this.o.tangage() + j * koofTangage, this.o.kren());
                            this.o.wrap360();
                        }
                    }
                } else {
                    // Don't directly set Orient axis fields here, this might be misleading if Orient doesn't represent
                    // The standard orthogonal vector system but is twisted in any direction.
                    // Use the builtin "increment" method which perfectly respects any existing vector direction
                    this.oCam.increment(i * koofAzimut, j * koofTangage, 0F);
                    this.oCam.wrap360();
                }
            } else if (this.hookViewIndex > 0) {
                this.pCam.set(0.0D, 0.0D, 0.0D);
                if (bChangeLen) this.pCam.set(j * 0.01F, -(float) i * 0.01F, 0.0D);
                else this.pCam.set(0.0D, -(float) i * 0.01F, j * 0.01F);
                this.oView.transform(this.pCam);
                this.pExt.add(this.pCam);
            } else if (bChangeLen) {
                len += j * koofLen;
                this.clipLen(lastBaseActor);
            } else {
                if (Time.real() < this.timeViewSet + 1000L) return;
                if (this.bFollow) i = 0;
                if (this.bUse) {
                    this.o.set(this.o.azimut() + i * koofAzimut, this.o.tangage() + j * koofTangage, this.o.kren());
                    this.o.wrap360();
                }
            }
            // TODO: +++ Camera Mod 4.12 +++
            Orient test = new Orient();
            test.set(this.o);
            test.wrap();
//            DecimalFormat df = new DecimalFormat("0.#");
//            float azimuth = 90F - test.getYaw();
            // HUD.training("Azimuth:" + df.format(azimuth) + "�, Elevation:" + df.format(o.getTangage()) + "�");
            // TODO: --- Camera Mod 4.12 ---
            if (Actor.isValid(this.camera)) this.camera.pos.inValidate(true);
            Azimut = _Azimut;
            Tangage = _Tangage;
        }
    }

    // TODO: +++ Camera Mod 4.12 +++
    public void movePilotsHead(Aircraft aircraft, float azimuth, float tangage) {
        if (Config.isUSE_RENDER() && (aircraft.netUser() == null || this.headTp < tangage || this.headTm > tangage || this.headYp < azimuth || this.headYm > azimuth)) {
            float newAzimuth = azimuth + 360F;
            float newTangage = tangage + 360F;
            float oldAzimuth = this.oldHeadAzimuth + 360F;
            float oldTangage = this.oldHeadTangage + 360F;
            azimuth = this.oldHeadAzimuth = (oldAzimuth * (SMOOTH_FACTOR - 1) + newAzimuth) / SMOOTH_FACTOR - 360F;
            tangage = this.oldHeadTangage = (oldTangage * (SMOOTH_FACTOR - 1) + newTangage) / SMOOTH_FACTOR - 360F;

            if (Math.abs(this.lastHeadAzimuthShown - azimuth) < HEAD_MOVE_COARSE && Math.abs(this.lastHeadTangageShown - tangage) < HEAD_MOVE_COARSE) {
                azimuth = this.lastHeadAzimuthShown;
                tangage = this.lastHeadTangageShown;
            } else {
                this.lastHeadAzimuthShown = azimuth;
                this.lastHeadTangageShown = tangage;
            }

            this.headTp = tangage + 0.0005F;
            this.headTm = tangage - 0.0005F;
            this.headYp = azimuth + 0.0005F;
            this.headYm = azimuth - 0.0005F;
            tmpOrLH.setYPR(0.0F, 0.0F, 0.0F);
            tmpOrLH.increment(0.0F, azimuth, 0.0F);
            tmpOrLH.increment(tangage, 0.0F, 0.0F);
            tmpOrLH.increment(0.0F, 0.0F, -0.2F * tangage + 0.05F * tangage);

            this.headOr[0] = tmpOrLH.getYaw();
            this.headOr[1] = tmpOrLH.getPitch();
            this.headOr[2] = tmpOrLH.getRoll();

            this.headPos[0] = 0.0005F * Math.abs(azimuth);
            this.headPos[1] = -0.0001F * Math.abs(azimuth);
            this.headPos[2] = 0.0F;
            aircraft.hierMesh().chunkSetLocate("Head1_D0", this.headPos, this.headOr);
        }
    }
    // TODO: --- Camera Mod 4.12 ---

    public void viewSet(float f, float f1) {
        if (!bUseMouse && !this.isUseCommon()) return;
        if(!Config.cur.externalTrackIREnabled && isUseCommon())
            return;
        this.timeViewSet = Time.real();
        f %= Circ;
        if (f > 180F) f -= Circ;
        else if (f < -180F) f += Circ;
        f1 %= Circ;
        if (f1 > 180F) f1 -= Circ;
        else if (f1 < -180F) f1 += Circ;
        // TODO: +++ Camera Mod 4.12 +++
        if (this.bFollow && !this.isModMode()) {
//		if (bFollow) {
            // TODO: --- Camera Mod 4.12 ---
            this.o.set(this.o.azimut(), f1, 0.0F);
            this.o.wrap360();
        } else {
            Azimut = _Azimut = f;
            Tangage = _Tangage = f1;
            this.o.set(Azimut, Tangage, 0.0F);
        }
//        if(!Config.cur.bMouseI)
//            oAdjM = true;
        if (Actor.isValid(this.camera)) this.camera.pos.inValidate(true);
    }

    public void snapSet(float f, float f1) {
        if (!this.bUse || !bUseMouse || !this.isUseCommon()) return;
        // TODO: +++ Camera Mod 4.12 +++
        if (this.bFollow && !this.isModMode()) // FIXME: Needs further check, might mess up NTRK playback!
            // if (bFollow)
            // TODO: --- Camera Mod 4.12 ---
            f = 0.0F;
        _Azimut = 45F * f;
        _Tangage = 44F * f1;
        if (this.camera != null) {
            Actor actor = this.camera.pos.base();
            if (actor != null) {
                Azimut = (this.o.azimut() - actor.pos.getAbsOrient().azimut()) % Circ;
                if (Azimut > _Azimut) while (Math.abs(Azimut - _Azimut) > 180F)
                    Azimut -= Circ;
                else if (Azimut < _Azimut) while (Math.abs(Azimut - _Azimut) > 180F)
                    Azimut += Circ;
                Tangage = this.o.tangage() % Circ;
                if (Tangage > _Tangage) while (Math.abs(Tangage - _Tangage) > 180F)
                    Tangage -= Circ;
                else if (Tangage < _Tangage) while (Math.abs(Tangage - _Tangage) > 180F)
                    Tangage += Circ;
                this.camera.pos.inValidate(true);
            }
//            if(!Config.cur.bMouseI)
//                oAdj = true;
        }
    }

    public void panSet(int i, int j) {
        if (!this.bUse || !bUseMouse || !this.isUseCommon()) return;
        if (i == 0 && j == 0) {
            _Azimut = 0.0F;
            _Tangage = 0.0F;
        }
        _Azimut = i * stepAzimut + _Azimut;
        // TODO: +++ Camera Mod 4.12 +++
        if (this.bFollow && !this.isModMode()) // FIXME: Needs further check, might mess up NTRK playback!
            // if (bFollow)
            // TODO: --- Camera Mod 4.12 ---
            _Azimut = 0.0F;
        _Tangage = j * stepTangage + _Tangage;
        if (this.camera != null) {
            Actor actor = this.camera.pos.base();
            if (actor != null) {
                Azimut = (this.o.azimut() - actor.pos.getAbsOrient().azimut()) % Circ;
                if (Azimut > _Azimut) while (Math.abs(Azimut - _Azimut) > 180F)
                    Azimut -= Circ;
                else if (Azimut < _Azimut) while (Math.abs(Azimut - _Azimut) > 180F)
                    Azimut += Circ;
                Tangage = this.o.tangage() % Circ;
                if (Tangage > _Tangage) while (Math.abs(Tangage - _Tangage) > 180F)
                    Tangage -= Circ;
                else if (Tangage < _Tangage) while (Math.abs(Tangage - _Tangage) > 180F)
                    Tangage += Circ;
                this.camera.pos.inValidate(true);
            }
//            if(!Config.cur.bMouseI)
//                oAdj = true;
        }
    }

    private void initHotKeys(String s) {
//        System.out.println("initHotKeys (" + s + ")");
        HotKeyCmdEnv.addCmd(s, new HotKeyCmdMouseMove(true, "Move") {

            public void move(int i, int j, int k) {
                HookView.this.mouseMove(i, j, k);
            }

            public void created() {
                this.setRecordId(27);
            }

        });
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(true, "Reset") {

            public void begin() {
                if (HookView.this.isUseCommon()) HookView.this.reset();
            }

            public void created() {
                this.setRecordId(28);
            }

        });
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(true, "Len") {

            public void begin() {
                if (HookView.this.isUseCommon()) HookView.bChangeLen = true;
            }

            public void end() {
                if (HookView.this.isUseCommon()) HookView.bChangeLen = false;
            }

            public void created() {
                this.setRecordId(29);
            }

        });
//        GUIMenu.initHotKeys();
    }

    public static void loadConfig() {
        koofAzimut = Config.cur.ini.get(sectConf, "AzimutSpeed", koofAzimut, 0.01F, 1.0F);
        koofTangage = Config.cur.ini.get(sectConf, "TangageSpeed", koofTangage, 0.01F, 1.0F);
        koofLen = Config.cur.ini.get(sectConf, "LenSpeed", koofLen, 0.1F, 10F);
        _minLen = Config.cur.ini.get(sectConf, "MinLen", _minLen, 1.0F, 20F);
        _defaultLen = Config.cur.ini.get(sectConf, "DefaultLen", _defaultLen, 1.0F, 100F);
        _maxLen = Config.cur.ini.get(sectConf, "MaxLen", _maxLen, 1.0F, 50000F);
        if (_defaultLen < _minLen) _defaultLen = _minLen;
        if (_maxLen < _defaultLen) _maxLen = _defaultLen;
        koofSpeed = Config.cur.ini.get(sectConf, "Speed", koofSpeed, 0.1F, 100F);
        koofLeanF = Config.cur.ini.get(sectConf, "LeanF", koofLeanF, 0.01F, 1.0F) * 0.5F;
        koofLeanS = Config.cur.ini.get(sectConf, "LeanS", koofLeanS, 0.01F, 1.0F) * 0.5F;
        koofRaise = Config.cur.ini.get(sectConf, "Raise", koofRaise, 0.01F, 1.0F) * 0.5F;
        rubberBandStretch = Config.cur.ini.get(sectConf, "RubberBand", rubberBandStretch, 0.0F, 1.0F) * 0.08F;
    }

    public static void saveConfig() {
        Config.cur.ini.setValue(sectConf, "LeanF", Float.toString(koofLeanF * 2.0F));
        Config.cur.ini.setValue(sectConf, "LeanS", Float.toString(koofLeanS * 2.0F));
        Config.cur.ini.setValue(sectConf, "Raise", Float.toString(koofRaise * 2.0F));
        Config.cur.ini.setValue(sectConf, "RubberBand", Float.toString(rubberBandStretch * 12.5F));
        Config.cur.ini.saveFile();
    }

    public HookView(String s) {
        this.camera = null;
        this.bUse = false;
        this.bClipOnLand = true;
        this.o = new Orientation();
        this.p = new Point3d();
        this.pAbs = new Point3d();
        this.oAbs = new Orient();
        this.bFollow = false;
        this.timeViewSet = -2000L;
        HotKeyEnv.fromIni(s, Config.cur.ini, s);
        sectConf = s + " Config";
        loadConfig();
        this.initHotKeys(s);
        current = this;
        // TODO: +++ Camera Mod 4.12 +++
        this.oCam = new Orientation();
        this.pCam = new Point3d();
        this.oView = new Orientation();
        this.pView = new Point3d();
        this.pCamOffset = new Point3d();
        this.sameActorSelected = false;
        this.pExt = new Point3d();
        this.oExt = new Orient();
        // TODO: --- Camera Mod 4.12 ---
    }

    protected HookView() {
        this.camera = null;
        this.bUse = false;
        this.bClipOnLand = true;
        this.o = new Orientation();
        this.p = new Point3d();
        this.pAbs = new Point3d();
        this.oAbs = new Orient();
        this.bFollow = false;
        this.timeViewSet = -2000L;
        // TODO: +++ Camera Mod 4.12 +++
        this.oCam = new Orientation();
        this.pCam = new Point3d();
        this.oView = new Orientation();
        this.pView = new Point3d();
        this.pCamOffset = new Point3d();
        this.sameActorSelected = false;
        this.pExt = new Point3d();
        this.oExt = new Orient();
        // TODO: --- Camera Mod 4.12 ---
    }

    public static HookView cur() {
        return current;
    }

    // TODO: +++ Camera Mod 4.12 +++
    private boolean getHookByName(Aircraft aircraft, String s, Point3d point3d) {
        Orient orient = new Orient();
        Loc loc = new Loc();
        Loc loc1 = new Loc();
        Hook hook = aircraft.findHook(s);
        if (hook == null) return false;
        else {
            hook.computePos(aircraft, loc, loc1);
            loc1.get(point3d, orient);
            return true;
        }
    }

    public boolean setHookView() {
        Aircraft viewAircraft = null;

        if (Config.isUSE_RENDER()) if (Main3D.cur3D().viewActor() instanceof Aircraft) viewAircraft = (Aircraft) Main3D.cur3D().viewActor();

        if (viewAircraft == null) return false;
        this.set(viewAircraft, 10F, -10F);
        this.pExt.set(0.0D, 0.0D, 0.0D);
        this.oExt.set(0.0F, 0.0F, 0.0F);
        if (this.hookViewIndex > 9) this.hookViewIndex = 0;
        boolean retVal = true;
        switch (this.hookViewIndex) {
            default:
                break;

            case 1:
                this.pExt.set(15D, 0.0D, 0.0D);
                this.oExt.set(180F, 0.0F, 0.0F);
                break;

            case 2:
                try {
                    this.getHookByName(viewAircraft, "_WingTipL", this.pExt);
                    this.pExt.add(0.0D, 4D, 1.0D);
                    this.oExt.set(92F, -5F, 0.0F);
                } catch (Exception e) {
                    retVal = false;
                }
                break;

            case 3:
                try {
                    this.getHookByName(viewAircraft, "_WingTipR", this.pExt);
                    this.pExt.add(0.0D, -4D, 1.0D);
                    this.oExt.set(-92F, -5F, 0.0F);
                } catch (Exception e) {
                    retVal = false;
                }
                break;

            case 4:
                try {
                    this.getHookByName(viewAircraft, "_ClipLGear", this.pExt);
                    if (viewAircraft.FM.CT.getGear() > 0.99F) {
                        this.pExt.add(-2D, 0.7D, 0.5D);
                        this.oExt.set(0.0F, 5F, 0.0F);
                    } else {
                        this.pExt.add(-2D, 0.7D, -1D);
                        this.oExt.set(0.0F, 5F, 0.0F);
                    }
                } catch (Exception e) {
                    retVal = false;
                }
                break;

            case 5:
                try {
                    this.getHookByName(viewAircraft, "_ClipRGear", this.pExt);
                    if (viewAircraft.FM.CT.getGear() > 0.99F) {
                        this.pExt.add(-2D, -0.7D, 0.5D);
                        this.oExt.set(0.0F, 5F, 0.0F);
                    } else {
                        this.pExt.add(-2D, -0.7D, -1D);
                        this.oExt.set(0.0F, 5F, 0.0F);
                    }
                } catch (Exception e) {
                    retVal = false;
                }
                break;

            case 6:
                try {
                    this.getHookByName(viewAircraft, "_ClipCGear", this.pExt);
                    if (viewAircraft.FM.CT.getGear() > 0.99F) {
                        this.pExt.add(-2.5D, 0.0D, -0.2D);
                        this.oExt.set(0.0F, -5F, 0.0F);
                    } else {
                        this.pExt.add(-2.5D, 0.0D, -1D);
                        this.oExt.set(0.0F, -5F, 0.0F);
                    }
                } catch (Exception e) {
                    retVal = false;
                }
                break;

            case 7:
                try {
                    this.getHookByName(viewAircraft, "_NavLight4", this.pExt);
                    this.pExt.add(-1.5D, -0.6D, 0.7D);
                    this.oExt.set(0.0F, -10F, 0.0F);
                } catch (Exception e) {
                    retVal = false;
                }
                break;

            case 8:
                this.pExt.set(2.8D, -1.6D, 1.0D);
                this.oExt.set(200F, 0.0F, 0.0F);
                break;

            case 9:
                this.pExt.set(-2D, 0.0D, 15D);
                this.oExt.set(0.0F, -90F, 0.0F);
                break;
        }
        return retVal;
    }
    // TODO: --- Camera Mod 4.12 ---
    
    protected static Actor     lastBaseActor     = null;
    protected Actor            camera;
    protected boolean          bUse;
    protected boolean          bClipOnLand;
    protected static final int EXT               = 1;
    protected static boolean   bUseMouse         = true;
    protected static boolean   bChangeLen        = false;
    protected static int       useFlags          = 0;
    private static float       _minLen           = 2.0F;
    private static float       _defaultLen;
    private static float       _maxLen           = 500F;
    protected static float     _visibleR         = -1F;
    protected static float     len;
    protected static float     stepAzimut        = 45F;
    protected static float     stepTangage       = 30F;
    protected static float     maxAzimut         = 179F;
    protected static float     maxTangage        = 89F;
    protected static float     minTangage        = -89F;
    protected static float     Azimut            = 0.0F;
    protected static float     Tangage           = 0.0F;
    protected static float     _Azimut           = 0.0F;
    protected static float     _Tangage          = 0.0F;
    protected static long      prevTime          = 0L;
    protected static float     koofAzimut        = 0.1F;
    protected static float     koofTangage       = 0.1F;
    protected static float     koofLen           = 1.0F;
    protected static float     koofSpeed         = 6F;
    public static float        koofLeanF         = 0.4F;
    public static float        koofLeanS         = 0.4F;
    public static float        koofRaise         = 0.2F;
    public static float        rubberBandStretch = 0.5F;
    protected Orient           o;
    protected Point3d          p;
    protected Point3d          pAbs;
    protected Orient           oAbs;
    private boolean            bFollow;
    private static Point3d     pClipZ1           = new Point3d();
    private static Point3d     pClipZ2           = new Point3d();
    private static Point3d     pClipRes          = new Point3d();
    static ClipFilter          clipFilter        = new ClipFilter();
    private static final float Circ              = 360F;
    private long               timeViewSet;
    private static String      sectConf;
    public static HookView     current           = null;

    // TODO: +++ Camera Mod 4.12 +++
    private static float   mouseSpeedFactor           = 1.0F;
    private static float   mouseSpeedFactorMultiplier = 1.5F;
    private Orient         oCam;
    private Point3d        pCam;
    private Orient         oView;
    private Point3d        pView;
    private Point3d        pCamOffset;
    private static float   koofKren                   = 0.1F;
    private static int     vkShift                    = 16;
    private static int     vkCtrl                     = 17;
    private static int     vkAlt                      = 18;
    private static int     vkCapsLock                 = 20;
    private Loc            headLoc                    = new Loc();
    private float          headPos[]                  = { 0, 0, 0 };
    private float          headOr[]                   = { 0, 0, 0 };
    private static Orient  tmpOrLH                    = new Orient();
    private float          headYp;
    private float          headTp;
    private float          headYm;
    private float          headTm;
    private boolean        sameActorSelected          = false;
    private boolean        normalPanViewState         = false;
    private boolean        lastCockpitPanViewState    = false;
    private int            hookViewIndex              = 0;
    private Point3d        pExt;
    private Orient         oExt;
    private float          oldHeadAzimuth             = 0F;
    private float          oldHeadTangage             = 0F;
    private float          lastHeadAzimuthShown       = 0F;
    private float          lastHeadTangageShown       = 0F;
    private static int     SMOOTH_FACTOR              = 10;
    private static int     HEAD_MOVE_COARSE           = 2;
    private static boolean overrideActorChange        = false;
    private static Actor   overrideActor              = null;
    // TODO: --- Camera Mod 4.12 ---
    
//    protected static boolean bInertia = false;
//    private long hookViewIndexTime = Time.real();
    protected static long pTime = 0L;
    protected boolean bFirstPan = true;
//    protected boolean oAdj = false;
//    protected boolean oAdjM = false;
    protected Orient o6DOF = new Orient();
    protected Orient oIn = new Orient();
    protected Orient oAbsIn = new Orient();
    protected Orient lorv = new Orient();
    protected Orient orv = new Orient();
    protected Point3d lp = new Point3d();
//    public float inEI = 0.075F;
//    public float inA = 0.01F;
//    public double inF = 0.01D;
    private boolean overrideInertia = false;


    static {
        _defaultLen = 20F;
        len = _defaultLen;
    }
}
