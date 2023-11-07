/*4.10.1 class + CTO Mod*/
package com.maddox.il2.fm;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.rts.SectFile;

public class EnginesInterface extends FMMath {
    // TODO: CTO Mod
    // ----------------------------------------
    private boolean bCatapultArmed;
    private double  dCatapultForce;
    private long    lCatapultStartTime;
    // ----------------------------------------

    public Motor[]          engines;
    public boolean[]        bCurControl;
    private int             num       = 0;
    public Vector3d         producedF = new Vector3d();
    public Vector3d         producedM = new Vector3d();
    private FlightModel     reference = null;
    private static Vector3d tmpV3d    = new Vector3d();
    private static int      tmpI;

    public EnginesInterface() {
        // TODO: CTO Mod
        // ----------------------------------------
        this.bCatapultArmed = false;
        this.dCatapultForce = 0.0D;
        // ----------------------------------------

        this.num = 0;
        this.producedF = new Vector3d();
        this.producedM = new Vector3d();
        this.reference = null;
    }

    public void load(FlightModel flightmodel, SectFile sectfile) {
        this.reference = flightmodel;
        String string = "Engine";
        for (this.num = 0; sectfile.get(string, "Engine" + this.num + "Family") != null; this.num++) {
            /* empty */
        }
        this.engines = new Motor[this.num];
        for (tmpI = 0; tmpI < this.num; tmpI++)
            this.engines[tmpI] = new Motor();
        this.bCurControl = new boolean[this.num];
        Aircraft.debugprintln(this.reference.actor, "Loading " + this.num + " engine(s) from '" + sectfile.toString() + "....");
        for (tmpI = 0; tmpI < this.num; tmpI++) {
            String string_0_ = sectfile.get(string, "Engine" + tmpI + "Family");
            String string_1_ = sectfile.get(string, "Engine" + tmpI + "SubModel");
            Aircraft.debugprintln(this.reference.actor, "Loading engine model from '" + string_0_ + ".emd', submodel '" + string_1_ + "'....");
            this.engines[tmpI].load(flightmodel, "FlightModels/" + string_0_ + ".emd", string_1_, tmpI);
        }
        if (sectfile.get(string, "Position0x", -99999.0F) != -99999.0F) {
            Point3d point3d = new Point3d();
            Vector3f vector3f = new Vector3f();
            for (tmpI = 0; tmpI < this.num; tmpI++) {
                point3d.x = sectfile.get(string, "Position" + tmpI + "x", 0.0F);
                point3d.y = sectfile.get(string, "Position" + tmpI + "y", 0.0F);
                point3d.z = sectfile.get(string, "Position" + tmpI + "z", 0.0F);
                this.engines[tmpI].setPos(point3d);
                vector3f.x = sectfile.get(string, "Vector" + tmpI + "x", 0.0F);
                vector3f.y = sectfile.get(string, "Vector" + tmpI + "y", 0.0F);
                vector3f.z = sectfile.get(string, "Vector" + tmpI + "z", 0.0F);
                this.engines[tmpI].setVector(vector3f);
                point3d.x = sectfile.get(string, "PropPosition" + tmpI + "x", 0.0F);
                point3d.y = sectfile.get(string, "PropPosition" + tmpI + "y", 0.0F);
                point3d.z = sectfile.get(string, "PropPosition" + tmpI + "z", 0.0F);
                this.engines[tmpI].setPropPos(point3d);
            }
        }
        this.setCurControlAll(true);
    }

    public void setNotMirror(boolean bool) {
        for (int i = 0; i < this.getNum(); i++)
            this.engines[i].setMaster(bool);
    }

    public void set(Actor actor) {
        Point3d point3d = new Point3d(0.0, 0.0, 0.0);
        Loc loc = new Loc();
        if (this.num != 0 && !(this.engines[0].getPropPos().distanceSquared(new Point3f(0.0F, 0.0F, 0.0F)) > 0.0F)) {
            Vector3f vector3f = new Vector3f(1.0F, 0.0F, 0.0F);
            float[][] fs = new float[4][3];
            float[][] fs_2_ = new float[this.num][3];
            for (tmpI = 0; tmpI < 4; tmpI++) {
                Hook hook = actor.findHook("_Clip0" + tmpI);
                loc.set(0.0, 0.0, 0.0, 0.0F, 0.0F, 0.0F);
                hook.computePos(actor, actor.pos.getAbs(), loc);
                loc.get(point3d);
                actor.pos.getAbs().transformInv(point3d);
                fs[tmpI][0] = (float) point3d.x;
                fs[tmpI][1] = (float) point3d.y;
                fs[tmpI][2] = (float) point3d.z;
            }
            for (tmpI = 0; tmpI < this.num; tmpI++) {
                Hook hook = actor.findHook("_Engine" + (tmpI + 1) + "Smoke");
                loc.set(0.0, 0.0, 0.0, 0.0F, 0.0F, 0.0F);
                hook.computePos(actor, actor.pos.getAbs(), loc);
                loc.get(point3d);
                actor.pos.getAbs().transformInv(point3d);
                fs_2_[tmpI][0] = (float) point3d.x;
                fs_2_[tmpI][1] = (float) point3d.y;
                fs_2_[tmpI][2] = (float) point3d.z - 0.7F;
            }
            switch (this.reference.Scheme) {
                case 0:
                    point3d.set(0.0, 0.0, 0.0);
                    this.engines[0].setPos(point3d);
                    this.engines[0].setPropPos(point3d);
                    this.engines[0].setVector(vector3f);
                    break;
                case 1:
                    point3d.x = 0.25F * (fs[0][0] + fs[1][0] + fs[2][0] + fs[3][0]);
                    point3d.y = 0.0;
                    point3d.z = 0.25F * (fs[0][2] + fs[1][2] + fs[2][2] + fs[3][2]);
                    this.engines[0].setPropPos(point3d);
                    point3d.x = fs_2_[0][0];
                    point3d.y = 0.0;
                    point3d.z = fs_2_[0][2];
                    this.engines[0].setPos(point3d);
                    this.engines[0].setVector(vector3f);
                    break;
                case 2:
                case 3:
                    point3d.x = 0.25F * (fs[0][0] + fs[1][0] + fs[2][0] + fs[3][0]);
                    point3d.y = 0.5F * (fs[0][1] + fs[1][1]);
                    point3d.z = 0.25F * (fs[0][2] + fs[1][2] + fs[2][2] + fs[3][2]);
                    this.engines[0].setPropPos(point3d);
                    point3d.y = 0.5F * (fs[2][1] + fs[3][1]);
                    this.engines[1].setPropPos(point3d);
                    point3d.x = 0.5F * (fs_2_[0][0] + fs_2_[1][0]);
                    point3d.y = fs_2_[0][1];
                    point3d.z = 0.5F * (fs_2_[0][2] + fs_2_[1][2]);
                    this.engines[0].setPos(point3d);
                    point3d.y = fs_2_[1][1];
                    this.engines[1].setPos(point3d);
                    this.engines[0].setVector(vector3f);
                    this.engines[1].setVector(vector3f);
                    break;
                case 4:
                    point3d.x = 0.25F * (fs[0][0] + fs[1][0] + fs[2][0] + fs[3][0]);
                    point3d.y = fs[0][1];
                    point3d.z = 0.25F * (fs[0][2] + fs[1][2] + fs[2][2] + fs[3][2]);
                    this.engines[0].setPropPos(point3d);
                    point3d.y = fs[1][1];
                    this.engines[1].setPropPos(point3d);
                    point3d.y = fs[2][1];
                    this.engines[2].setPropPos(point3d);
                    point3d.y = fs[3][1];
                    this.engines[3].setPropPos(point3d);
                    point3d.x = 0.25F * (fs_2_[0][0] + fs_2_[1][0] + fs_2_[2][0] + fs_2_[3][0]);
                    point3d.y = fs_2_[0][1];
                    point3d.z = 0.25F * (fs_2_[0][2] + fs_2_[1][2] + fs_2_[2][2] + fs_2_[3][2]);
                    this.engines[0].setPos(point3d);
                    point3d.y = fs_2_[1][1];
                    this.engines[1].setPos(point3d);
                    point3d.y = fs_2_[2][1];
                    this.engines[2].setPos(point3d);
                    point3d.y = fs_2_[3][1];
                    this.engines[3].setPos(point3d);
                    this.engines[0].setVector(vector3f);
                    this.engines[1].setVector(vector3f);
                    this.engines[2].setVector(vector3f);
                    this.engines[3].setVector(vector3f);
                    break;
                case 5:
                    point3d.x = 0.25F * (fs[0][0] + fs[1][0] + fs[2][0] + fs[3][0]);
                    point3d.y = fs[0][1];
                    point3d.z = 0.25F * (fs[0][2] + fs[1][2] + fs[2][2] + fs[3][2]);
                    this.engines[0].setPropPos(point3d);
                    point3d.y = fs[1][1];
                    this.engines[1].setPropPos(point3d);
                    point3d.y = 0.0;
                    this.engines[2].setPropPos(point3d);
                    point3d.y = fs[2][1];
                    this.engines[3].setPropPos(point3d);
                    point3d.y = fs[3][1];
                    this.engines[4].setPropPos(point3d);
                    point3d.x = 0.2F * (fs_2_[0][0] + fs_2_[1][0] + fs_2_[2][0] + fs_2_[3][0] + fs_2_[4][0]);
                    point3d.y = fs_2_[0][1];
                    point3d.z = 0.2F * (fs_2_[0][2] + fs_2_[1][2] + fs_2_[2][2] + fs_2_[3][2] + fs_2_[4][2]);
                    this.engines[0].setPos(point3d);
                    point3d.y = fs_2_[1][1];
                    this.engines[1].setPos(point3d);
                    point3d.y = fs_2_[2][1];
                    this.engines[2].setPos(point3d);
                    point3d.y = fs_2_[3][1];
                    this.engines[3].setPos(point3d);
                    point3d.y = fs_2_[4][1];
                    this.engines[4].setPos(point3d);
                    this.engines[0].setVector(vector3f);
                    this.engines[1].setVector(vector3f);
                    this.engines[2].setVector(vector3f);
                    this.engines[3].setVector(vector3f);
                    this.engines[4].setVector(vector3f);
                    break;
                case 6:
                    point3d.x = 0.33333334F * (fs[0][0] + fs[1][0] + fs[2][0]);
                    point3d.y = fs[0][1];
                    point3d.z = 0.33333334F * (fs[0][2] + fs[1][2] + fs[2][2]);
                    this.engines[0].setPropPos(point3d);
                    point3d.y = fs[1][1];
                    this.engines[1].setPropPos(point3d);
                    point3d.y = fs[2][1];
                    this.engines[2].setPropPos(point3d);
                    point3d.x = 0.33333334F * (fs_2_[0][0] + fs_2_[1][0] + fs_2_[2][0]);
                    point3d.y = fs_2_[0][1];
                    point3d.z = 0.33333334F * (fs_2_[0][2] + fs_2_[1][2] + fs_2_[2][2]);
                    this.engines[0].setPos(point3d);
                    point3d.y = fs_2_[1][1];
                    this.engines[1].setPos(point3d);
                    point3d.y = fs_2_[2][1];
                    this.engines[2].setPos(point3d);
                    this.engines[0].setVector(vector3f);
                    this.engines[1].setVector(vector3f);
                    this.engines[2].setVector(vector3f);
                    break;
                case 7:
                    point3d.x = 0.25F * (fs[0][0] + fs[1][0] + fs[2][0] + fs[3][0]);
                    point3d.y = fs[0][1];
                    point3d.z = 0.25F * (fs[0][2] + fs[1][2] + fs[2][2] + fs[3][2]);
                    this.engines[0].setPropPos(point3d);
                    point3d.y = 0.0;
                    this.engines[1].setPropPos(point3d);
                    point3d.y = fs[1][1];
                    this.engines[2].setPropPos(point3d);
                    point3d.y = fs[2][1];
                    this.engines[3].setPropPos(point3d);
                    point3d.y = 0.0;
                    this.engines[4].setPropPos(point3d);
                    point3d.y = fs[3][1];
                    this.engines[5].setPropPos(point3d);
                    point3d.x = 0.16666667F * (fs_2_[0][0] + fs_2_[1][0] + fs_2_[2][0] + fs_2_[3][0] + fs_2_[4][0] + fs_2_[5][0]);
                    point3d.y = fs_2_[0][1];
                    point3d.z = 0.16666667F * (fs_2_[0][2] + fs_2_[1][2] + fs_2_[2][2] + fs_2_[3][2] + fs_2_[4][2] + fs_2_[5][2]);
                    this.engines[0].setPos(point3d);
                    point3d.y = fs_2_[1][1];
                    this.engines[1].setPos(point3d);
                    point3d.y = fs_2_[2][1];
                    this.engines[2].setPos(point3d);
                    point3d.y = fs_2_[3][1];
                    this.engines[3].setPos(point3d);
                    point3d.y = fs_2_[4][1];
                    this.engines[4].setPos(point3d);
                    point3d.y = fs_2_[5][1];
                    this.engines[5].setPos(point3d);
                    this.engines[0].setVector(vector3f);
                    this.engines[1].setVector(vector3f);
                    this.engines[2].setVector(vector3f);
                    this.engines[3].setVector(vector3f);
                    this.engines[4].setVector(vector3f);
                    this.engines[5].setVector(vector3f);
                    break;
                case 8:
                    point3d.x = 0.25F * (fs[0][0] + fs[1][0] + fs[2][0] + fs[3][0]);
                    point3d.y = fs[0][1];
                    point3d.z = 0.25F * (fs[0][2] + fs[1][2] + fs[2][2] + fs[3][2]);
                    engines[0].setPropPos(point3d);
                    point3d.y = 0.3333F * (2.0F * fs[0][1] + fs[1][1]);
                    engines[1].setPropPos(point3d);
                    point3d.y = 0.3333F * (fs[0][1] + 2.0F * fs[1][1]);
                    engines[2].setPropPos(point3d);
                    point3d.y = fs[1][1];
                    engines[3].setPropPos(point3d);
                    point3d.y = fs[2][1];
                    engines[4].setPropPos(point3d);
                    point3d.y = 0.3333F * (2.0F * fs[2][1] + fs[3][1]);
                    engines[5].setPropPos(point3d);
                    point3d.y = 0.3333F * (fs[2][1] + 2.0F * fs[3][1]);
                    engines[6].setPropPos(point3d);
                    point3d.y = fs[3][1];
                    engines[7].setPropPos(point3d);
                    point3d.x = 0.125F * (fs_2_[0][0] + fs_2_[1][0] + fs_2_[2][0] + fs_2_[3][0] + fs_2_[4][0] + fs_2_[5][0] + fs_2_[6][0] + fs_2_[7][0]);
                    point3d.y = fs_2_[0][1];
                    point3d.z = 0.125F * (fs_2_[0][2] + fs_2_[1][2] + fs_2_[2][2] + fs_2_[3][2] + fs_2_[4][2] + fs_2_[5][2] + fs_2_[6][2] + fs_2_[7][2]);
                    engines[0].setPos(point3d);
                    point3d.y = fs_2_[1][1];
                    engines[1].setPos(point3d);
                    point3d.y = fs_2_[2][1];
                    engines[2].setPos(point3d);
                    point3d.y = fs_2_[3][1];
                    engines[3].setPos(point3d);
                    point3d.y = fs_2_[4][1];
                    engines[4].setPos(point3d);
                    point3d.y = fs_2_[5][1];
                    engines[5].setPos(point3d);
                    point3d.y = fs_2_[6][1];
                    engines[6].setPos(point3d);
                    point3d.y = fs_2_[7][1];
                    engines[7].setPos(point3d);
                    engines[0].setVector(vector3f);
                    engines[1].setVector(vector3f);
                    engines[2].setVector(vector3f);
                    engines[3].setVector(vector3f);
                    engines[4].setVector(vector3f);
                    engines[5].setVector(vector3f);
                    engines[6].setVector(vector3f);
                    engines[7].setVector(vector3f);
                    break;

                case 10:
                    point3d.x = 0.25F * (fs[0][0] + fs[1][0] + fs[2][0] + fs[3][0]);
                    point3d.y = fs[0][1];
                    point3d.z = 0.25F * (fs[0][2] + fs[1][2] + fs[2][2] + fs[3][2]);
                    engines[0].setPropPos(point3d);
                    point3d.y = 0.25F * (3F * fs[0][1] + fs[1][1]);
                    engines[1].setPropPos(point3d);
                    point3d.y = 0.5F * (fs[0][1] + fs[1][1]);
                    engines[2].setPropPos(point3d);
                    point3d.y = 0.25F * (fs[0][1] + 3F * fs[1][1]);
                    engines[3].setPropPos(point3d);
                    point3d.y = fs[1][1];
                    engines[4].setPropPos(point3d);
                    point3d.y = fs[2][1];
                    engines[5].setPropPos(point3d);
                    point3d.y = 0.25F * (3F * fs[2][1] + fs[3][1]);
                    engines[6].setPropPos(point3d);
                    point3d.y = 0.5F * (fs[2][1] + fs[3][1]);
                    engines[7].setPropPos(point3d);
                    point3d.y = 0.25F * (fs[2][1] + 3F * fs[3][1]);
                    engines[8].setPropPos(point3d);
                    point3d.y = fs[3][1];
                    engines[9].setPropPos(point3d);
                    point3d.x = 0.1F * (fs_2_[0][0] + fs_2_[1][0] + fs_2_[2][0] + fs_2_[3][0] + fs_2_[4][0] + fs_2_[5][0] + fs_2_[6][0] + fs_2_[7][0] + fs_2_[8][0] + fs_2_[9][0]);
                    point3d.y = fs_2_[0][1];
                    point3d.z = 0.1F * (fs_2_[0][2] + fs_2_[1][2] + fs_2_[2][2] + fs_2_[3][2] + fs_2_[4][2] + fs_2_[5][2] + fs_2_[6][2] + fs_2_[7][2] + fs_2_[8][2] + fs_2_[9][2]);
                    engines[0].setPos(point3d);
                    point3d.y = fs_2_[1][1];
                    engines[1].setPos(point3d);
                    point3d.y = fs_2_[2][1];
                    engines[2].setPos(point3d);
                    point3d.y = fs_2_[3][1];
                    engines[3].setPos(point3d);
                    point3d.y = fs_2_[4][1];
                    engines[4].setPos(point3d);
                    point3d.y = fs_2_[5][1];
                    engines[5].setPos(point3d);
                    point3d.y = fs_2_[6][1];
                    engines[6].setPos(point3d);
                    point3d.y = fs_2_[7][1];
                    engines[7].setPos(point3d);
                    point3d.y = fs_2_[8][1];
                    engines[8].setPos(point3d);
                    point3d.y = fs_2_[9][1];
                    engines[9].setPos(point3d);
                    engines[0].setVector(vector3f);
                    engines[1].setVector(vector3f);
                    engines[2].setVector(vector3f);
                    engines[3].setVector(vector3f);
                    engines[4].setVector(vector3f);
                    engines[5].setVector(vector3f);
                    engines[6].setVector(vector3f);
                    engines[7].setVector(vector3f);
                    engines[8].setVector(vector3f);
                    engines[9].setVector(vector3f);
                    break;
                    
                default:
                    throw new RuntimeException("UNIDENTIFIED ENGINE DISTRIBUTION.");
            }
        }
    }

    public void update(float f) {
        // TODO: CTO Mod
        // ----------------------------------------
        if (this.bCatapultArmed && java.lang.System.currentTimeMillis() > this.lCatapultStartTime + 2500L / (long) com.maddox.rts.Time.speed()) {
            this.bCatapultArmed = false;
            this.dCatapultForce = 0.0D;
        }
        // ----------------------------------------

        this.producedF.set(0.0, 0.0, 0.0);
        this.producedM.set(0.0, 0.0, 0.0);
        for (int i = 0; i < this.num; i++) {
            this.engines[i].update(f);

            // TODO: Modified by CTO Mod
            // ---------------------------------------------------
            this.producedF.x += this.engines[i].getEngineForce().x + this.dCatapultForce;
            // ---------------------------------------------------

            this.producedF.y += this.engines[i].getEngineForce().y;
            this.producedF.z += this.engines[i].getEngineForce().z;
            this.producedM.x += this.engines[i].getEngineTorque().x;
            this.producedM.y += this.engines[i].getEngineTorque().y;
            this.producedM.z += this.engines[i].getEngineTorque().z;
        }
    }

    public void netupdate(float f, boolean bool) {
        for (int i = 0; i < this.num; i++)
            this.engines[i].netupdate(f, bool);
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int i) {
        this.num = i;
    }

    public void toggle() {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].toggle();
    }

    public void setCurControl(int i, boolean bool) {
        this.bCurControl[i] = bool;
    }

    public void setCurControlAll(boolean bool) {
        for (tmpI = 0; tmpI < this.num; tmpI++)
            this.bCurControl[tmpI] = bool;
    }

    public boolean getCurControl(int i) {
        // TODO: Modified by |ZUTI|: sometimes request is out of bounds...
        // --------------------------------
        if (i >= this.bCurControl.length) return false;
        // --------------------------------
        return this.bCurControl[i];
    }

    public Motor getFirstSelected() {
        for (int i = 0; i < this.num; i++)
            if (this.bCurControl[i]) return this.engines[i];
        return null;
    }

    public int getNumSelected() {
        int i = 0;
        for (int i_3_ = 0; i_3_ < this.num; i_3_++)
            if (this.bCurControl[i_3_]) i++;
        return i;
    }

    public float getPropDirSign() {
        float f = 0.0F;
        for (int i = 0; i < this.getNum(); i++)
            if (this.engines[i].getPropDir() == 0) f++;
            else f--;
        return f / this.getNum();
    }

    public float getRadiatorPos() {
        float f = 0.0F;
        for (int i = 0; i < this.getNum(); i++)
            f += this.engines[i].getControlRadiator();
        return f / this.getNum();
    }

    public int[] getSublist(int i, int i_4_) {
        int[] is = null;
        if (i_4_ == 1) switch (i) {
            case 2:
            case 3:
                is = new int[] { 0 };
                break;
            case 6:
                is = new int[] { 0 };
                break;
            case 4:
                is = new int[] { 0, 1 };
                break;
            case 5:
                is = new int[] { 0, 1 };
                break;
            case 7:
                is = new int[] { 0, 1, 2 };
                break;
        }
        else if (i_4_ == 2) switch (i) {
            case 2:
            case 3:
                is = new int[] { 1 };
                break;
            case 6:
                is = new int[] { 2 };
                break;
            case 4:
                is = new int[] { 2, 3 };
                break;
            case 5:
                is = new int[] { 3, 4 };
                break;
            case 7:
                is = new int[] { 3, 4, 5 };
                break;
        }
        return is;
    }

    public boolean isSelectionHasControlThrottle() {
        boolean bool = false;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) bool |= this.engines[tmpI].isHasControlThrottle();
        return bool;
    }

    public boolean isSelectionHasControlProp() {
        boolean bool = false;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) bool |= this.engines[tmpI].isHasControlProp();
        return bool;
    }

    public boolean isSelectionAllowsAutoProp() {
        FlightModel flightmodel = this.reference;
        World.cur();
        if (flightmodel != World.getPlayerFM()) return true;
        boolean bool = false;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) bool |= this.engines[tmpI].isAllowsAutoProp();
        return bool;
    }

    public boolean isSelectionHasControlMix() {
        boolean bool = false;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) bool |= this.engines[tmpI].isHasControlMix();
        return bool;
    }

    public boolean isSelectionHasControlMagnetos() {
        boolean bool = false;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) bool |= this.engines[tmpI].isHasControlMagnetos();
        return bool;
    }

    public boolean isSelectionHasControlCompressor() {
        boolean bool = false;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) bool |= this.engines[tmpI].isHasControlCompressor();
        return bool;
    }

    public boolean isSelectionHasControlFeather() {
        boolean bool = false;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) bool |= this.engines[tmpI].isHasControlFeather();
        return bool;
    }

    public boolean isSelectionHasControlExtinguisher() {
        boolean bool = false;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) bool = bool | this.engines[tmpI].getExtinguishers() > 0;
        return bool;
    }

    public boolean isSelectionHasControlAfterburner() {
        boolean bool = false;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) bool |= this.engines[tmpI].isHasControlAfterburner();
        return bool;
    }

    public boolean isSelectionAllowsAutoRadiator() {
        FlightModel flightmodel = this.reference;
        World.cur();
        if (flightmodel != World.getPlayerFM()) return true;
        boolean bool = false;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) bool |= this.engines[tmpI].isAllowsAutoRadiator();
        return bool;
    }

    public boolean isSelectionHasControlRadiator() {
        boolean bool = false;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) bool |= this.engines[tmpI].isHasControlRadiator();
        return bool;
    }

    public float getPowerOutput() {
        float f = 0.0F;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            f += this.engines[tmpI].getPowerOutput();
        return f / this.getNum();
    }

    public float getThrustOutput() {
        float f = 0.0F;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            f += this.engines[tmpI].getThrustOutput();
        return f / this.getNum();
    }

    public float getReadyness() {
        float f = 0.0F;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            f += this.engines[tmpI].getReadyness();
        return f / this.getNum();
    }

    public float getBoostFactor() {
        float f = 0.0F;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            f += this.engines[tmpI].getBoostFactor();
        return f / this.getNum();
    }

    public Vector3d getGyro() {
        tmpV3d.set(0.0, 0.0, 0.0);
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            tmpV3d.add(this.engines[tmpI].getEngineGyro());
        return tmpV3d;
    }

    public void setThrottle(float f) {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setControlThrottle(f);
    }

    public void setAfterburnerControl(boolean bool) {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setControlAfterburner(bool);
    }

    public void setProp(float f) {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setControlProp(f);
    }

    public void setPropDelta(int i) {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setControlPropDelta(i);
    }

    public void setPropAuto(boolean bool) {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setControlPropAuto(bool);
    }

    public void setFeather(int i) {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setControlFeather(i);
    }

    public void setMix(float f) {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setControlMix(f);
    }

    public void setMagnetos(int i) {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setControlMagneto(i);
    }

    public void setCompressorStep(int i) {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setControlCompressor(i);
    }

    public void setRadiator(float f) {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setControlRadiator(f);
    }

    public void updateRadiator(float f) {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            this.engines[tmpI].updateRadiator(f);
    }

    public void setEngineStops() {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setEngineStops(this.reference.actor);
    }

    public void setEngineRunning() {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setEngineRunning(this.reference.actor);
    }

    public float forcePropAOA(float f, float f_5_, float f_6_, boolean bool) {
        float f_7_ = 0.0F;
        for (int i = 0; i < this.getNum(); i++)
            f_7_ += this.engines[i].forcePropAOA(f, f_5_, f_6_, bool);
        Aircraft.debugprintln(this.reference.actor, "Computed thrust at " + f + " m/s and " + f_5_ + " m is " + f_7_ + " N..");
        return f_7_;
    }

    // TODO: CTO Mod
    // ----------------------------------------
    public void setCatapult(float f, boolean flag) {
        this.bCatapultArmed = true;
        double d = f;
        if (d > 8000D) d = 8000D;
        if (flag) this.dCatapultForce = d * 22D / this.num;
        else this.dCatapultForce = d * 15D / this.num;
        this.lCatapultStartTime = java.lang.System.currentTimeMillis();
    }

    public boolean getCatapult() {
        return this.bCatapultArmed;
    }

    public void resetCatapultTime() {
        this.lCatapultStartTime = java.lang.System.currentTimeMillis();
    }
    // ----------------------------------------

    // TODO: +++ TD AI code backport from 4.13 +++
    public void setManualAfterburnerControl(float f) {
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) this.engines[tmpI].setManualControlAfterburner(f);

    }

    public float getAIOverheatFactor() {
        float f = -1F;
        float f1 = -1F;
        for (int i = 0; i < this.getNum(); i++) {
            float f2 = this.engines[i].tOilOut / this.engines[i].tOilCritMax - 1.0F;
            f = Math.max(f, f2);
            f2 = this.engines[i].tWaterOut / this.engines[i].tWaterCritMax - 1.0F;
            f1 = Math.max(f1, f2);
        }

        return Math.max(f, f1);
    }

    public boolean isSelectionHasControlBoost() {
        boolean flag = false;
        for (tmpI = 0; tmpI < this.getNum(); tmpI++)
            if (this.bCurControl[tmpI]) flag |= this.engines[tmpI].isHasControlBoost();

        return flag;
    }

    public float getManifoldPressure() {
        float f = -1F;
        for (int i = 0; i < this.getNum(); i++)
            f = Math.max(f, this.engines[i].getManifoldPressure());

        return f;
    }
    // TODO: --- TD AI code backport from 4.13 ---

}
