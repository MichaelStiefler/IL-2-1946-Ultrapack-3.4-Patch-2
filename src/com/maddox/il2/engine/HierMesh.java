package com.maddox.il2.engine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import com.maddox.JGP.Matrix4d;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.RangeRandom;
import com.maddox.util.HashMapExt;

public class HierMesh extends Mesh {
    class ChunkState {

        public boolean bExist;
        public int     indx;
        public boolean bVisible;
        public float   x;
        public float   y;
        public float   z;
        public float   yaw;
        public float   pitch;
        public float   roll;

        public ChunkState(String s) {
            try {
//                indx = chunkFind(s);
                this.indx = HierMesh.this.chunkFindCheck(s); // TODO: Patch Pack 107 use chunkFindCheck to avoid internal errors flooding the log!
            } catch (Exception exception) {
                this.bExist = false;
                return;
            }
            // TODO: Patch Pack 107 check for negative index, because we might be using chunkFindCheck internally when calling chunkFind!
            if (this.indx < 0) {
                this.bExist = false;
                return;
            }
            // ---
            this.bExist = true;
            HierMesh.this.setCurChunk(this.indx);
            this.bVisible = HierMesh.this.isChunkVisible();
            HierMesh.this.getChunkLocObj(HierMesh._chunkLoc);
            this.x = (float) HierMesh._chunkLoc.getX();
            this.y = (float) HierMesh._chunkLoc.getY();
            this.z = (float) HierMesh._chunkLoc.getZ();
            this.yaw = HierMesh._chunkLoc.getOrient().getYaw();
            this.pitch = HierMesh._chunkLoc.getOrient().getPitch();
            this.roll = HierMesh._chunkLoc.getOrient().getRoll();
        }
    }

    public void setPos(Point3d point3d, Orient orient) {
        this.SetPosXYZATK(this.cppObj, point3d.x, point3d.y, point3d.z, orient.getAzimut(), orient.getTangage(), orient.getKren());
    }

    public void setPos(Loc loc) {
        Point3d point3d = loc.getPoint();
        Orient orient = loc.getOrient();
        this.SetPosXYZATK(this.cppObj, point3d.x, point3d.y, point3d.z, orient.getAzimut(), orient.getTangage(), orient.getKren());
    }

    public int preRender() {
        return this.PreRender(this.cppObj);
    }

    public void render() {
        this.Render(this.cppObj);
    }

    public void renderShadowProjective() {
        this.RenderShadowProjective(this.cppObj);
    }

    public void renderShadowVolume() {
        this.RenderShadowVolume(this.cppObj);
    }

    public void renderShadowVolumeHQ() {
        this.RenderShadowVolumeHQ(this.cppObj);
    }

    public void renderChunkMirror(double ad[], double ad1[], double ad2[]) {
        this.RenderChunkMirror(this.cppObj, ad, ad1, ad2);
    }

    public static void renderShadowPairs(ArrayList arraylist) {
        int i = arraylist.size();
        if (i == 0) return;
        if (shadowPairs.length < i) shadowPairs = new int[(i / 2 + 1) * 2];
        for (int j = 0; j < i; j++) {
            HierMesh hiermesh = (HierMesh) arraylist.get(j);
            shadowPairs[j] = hiermesh.cppObject();
        }

        RenderShadowPairs(i / 2, shadowPairs);
    }

    public int detectCollision(Loc loc, HierMesh hiermesh, Loc loc1) {
        loc.get(tmp);
        loc1.get(tmp2);
        return this.DetectCollision(this.cppObj, tmp, hiermesh.cppObj, tmp2);
    }

    public int detectCollision(Loc loc, Mesh mesh, Loc loc1) {
        loc.get(tmp);
        loc1.get(tmp2);
        return this.DetectCollisionMesh(this.cppObj, tmp, mesh.cppObj, tmp2);
    }

    public float detectCollisionLine(Loc loc, Point3d point3d, Point3d point3d1) {
        loc.get(tmp);
        point3d.get(ad);
        point3d1.get(tmp2);
        return this.DetectCollisionLine(this.cppObj, tmp, ad, tmp2);
    }

    public int detectCollisionLineMulti(Loc loc, Point3d point3d, Point3d point3d1) {
        loc.get(tmp);
        point3d.get(ad);
        point3d1.get(tmp2);
        return this.DetectCollisionLineMulti(this.cppObj, tmp, ad, tmp2);
    }

    public float detectCollisionQuad(Loc loc, Point3d point3d, Point3d point3d1, Point3d point3d2, Point3d point3d3) {
        loc.get(tmp);
        tmp2[0] = point3d.x;
        tmp2[1] = point3d.y;
        tmp2[2] = point3d.z;
        tmp2[3] = point3d1.x;
        tmp2[4] = point3d1.y;
        tmp2[5] = point3d1.z;
        tmp2[6] = point3d2.x;
        tmp2[7] = point3d2.y;
        tmp2[8] = point3d2.z;
        tmp2[9] = point3d3.x;
        tmp2[10] = point3d3.y;
        tmp2[11] = point3d3.z;
        return this.DetectCollisionQuad(this.cppObj, tmp, tmp2);
    }

    public int detectCollision_Quad_Multi(Loc loc, Point3d point3d, Point3d point3d1, Point3d point3d2, Point3d point3d3) {
        loc.get(tmp);
        tmp2[0] = point3d.x;
        tmp2[1] = point3d.y;
        tmp2[2] = point3d.z;
        tmp2[3] = point3d1.x;
        tmp2[4] = point3d1.y;
        tmp2[5] = point3d1.z;
        tmp2[6] = point3d2.x;
        tmp2[7] = point3d2.y;
        tmp2[8] = point3d2.z;
        tmp2[9] = point3d3.x;
        tmp2[10] = point3d3.y;
        tmp2[11] = point3d3.z;
        return this.DetectCollisionQuadMultiH(this.cppObj, tmp, tmp2);
    }

    public int detectCollisionPoint(Loc loc, Point3d point3d) {
        loc.get(tmp);
        point3d.get(ad);
        return this.DetectCollisionPoint(this.cppObj, tmp, ad);
    }

    public float detectCollisionPlane(Loc loc, Vector3d vector3d, double d) {
        loc.get(tmp);
        vector3d.get(tmp2);
        tmp2[3] = d;
        return this.DetectCollisionPlane(this.cppObj, tmp, tmp2);
    }

    public void setScale(float f) {
    }

    public float scale() {
        return 1.0F;
    }

    public void setScaleXYZ(float f, float f1, float f2) {
    }

    public void scaleXYZ(float af[]) {
        af[0] = af[1] = af[2] = 1.0F;
    }

    public float getUniformMaxDist() {
        return this.GetUniformMaxDist(this.cppObj);
    }

    public int chunks() {
        return this.Chunks(this.cppObj);
    }

    public boolean isChunkVisible(String s) {
        ChunkState chunkstate = this.chunkState(s);
        if (chunkstate == null) return false;
        else return chunkstate.bVisible;
    }

    public void chunkVisible(String s, boolean flag) {
        ChunkState chunkstate = this.chunkState(s);
        if (chunkstate == null) return;
        if (chunkstate.bVisible == flag) return;
        else {
            chunkstate.bVisible = flag;
            this.setCurChunk(chunkstate.indx);
            this.SetChunkVisibility(this.cppObj, flag ? 1 : 0);
            return;
        }
    }

    public void chunkSetAngles(String s, float af[]) {
        this.chunkSetAngles(s, af[0], af[1], af[2]);
    }

    public void chunkSetAngles(String s, float f, float f1, float f2) {
        ChunkState chunkstate = this.chunkState(s);
        if (chunkstate == null) return;
        if (chunkstate.yaw == f && chunkstate.pitch == f1 && chunkstate.roll == f2) return;
        else {
            _chunkAngles[0] = chunkstate.yaw = f;
            _chunkAngles[1] = chunkstate.pitch = f1;
            _chunkAngles[2] = chunkstate.roll = f2;
            this.setCurChunk(chunkstate.indx);
            this.SetChunkAngles(this.cppObj, _chunkAngles);
            return;
        }
    }

    public void chunkSetLocate(String s, float af[], float af1[]) {
        ChunkState chunkstate = this.chunkState(s);
        if (chunkstate == null) return;
        if (chunkstate.yaw == af1[0] && chunkstate.pitch == af1[1] && chunkstate.roll == af1[2] && chunkstate.x == af[0] && chunkstate.y == af[1] && chunkstate.z == af[2]) return;
        else {
            chunkstate.yaw = af1[0];
            chunkstate.pitch = af1[1];
            chunkstate.roll = af1[2];
            chunkstate.x = af[0];
            chunkstate.y = af[1];
            chunkstate.z = af[2];
            this.setCurChunk(chunkstate.indx);
            this.SetChunkLocate(this.cppObj, af, af1);
            return;
        }
    }

    private ChunkState chunkState(String s) {
        if (this.chunkMap == null) this.chunkMap = new HashMapExt();
        ChunkState chunkstate = (ChunkState) this.chunkMap.get(s);
        if (chunkstate == null) {
            chunkstate = new ChunkState(s);
            this.chunkMap.put(s, chunkstate);
        }
        if (chunkstate.bExist) return chunkstate;
        else {
            if (this.meshName != null) printDebugMessage(DEBUG_DETAILED, "HierMesh Debugger Error in chunkState(" + s + "): Chunk '" + s + "' does not exist in HierMesh '" + this.meshName + "'.");
            return null;
        }
    }

    public void setCurChunk(int i) {
        this.curchunk = i;
        this.SetCurChunk(this.cppObj, i);
    }

    public void setCurChunk(String s) {
        this.curchunk = this.SetCurChunkByName(this.cppObj, s);
        if (this.curchunk < 0 && this.meshName != null) printDebugMessage(DEBUG_DETAILED, "HierMesh Debugger Error in setCurChunk(" + s + "): Chunk '" + s + "' does not exist in HierMesh '" + this.meshName + "'.");
    }

    public int getCurChunk() {
        return this.curchunk;
    }

    public int chunkFind(String s) {
//        if (HierMesh.curDebugLevel() < DEBUG_NORMAL) { // Temporarily disabled workaround until further checks completed
//            return this.ChunkFindCheck(this.cppObj, s);
//        }
        int retVal = this.ChunkFind(this.cppObj, s);
//        System.out.println("chunkFind(" + s + ") = " + retVal);
        if (retVal < 0) printDebugMessage(DEBUG_DETAILED, "HierMesh Error in chunkFind(" + s + "): Chunk '" + s + "' does not exist in HierMesh '" + this.meshName + "'.");
        return retVal;
    }

    public int chunkFindCheck(String s) {
        int retVal = this.ChunkFindCheck(this.cppObj, s);
        if (retVal < 0 && this.meshName != null) printDebugMessage(DEBUG_EXTREME, "HierMesh Debugger Error in chunkFindCheck(" + s + "): Chunk '" + s + "' does not exist in HierMesh '" + this.meshName + "'.");
        return retVal;
    }

    public String chunkName() {
        return this.ChunkName(this.cppObj);
    }

    public float getChunkVisibilityR() {
        return this.GetChunkVisibilityR(this.cppObj);
    }

    public boolean isChunkVisible() {
        return this.GetChunkVisibility(this.cppObj) != 0;
    }

    public void chunkVisible(boolean flag) {
        this.SetChunkVisibility(this.cppObj, flag ? 1 : 0);
    }

    public void chunkSetAngles(float af[]) {
        this.SetChunkAngles(this.cppObj, af);
    }

    public void chunkSetLocate(float af[], float af1[]) {
        this.SetChunkLocate(this.cppObj, af, af1);
    }

    public void getChunkLTM(Matrix4d matrix4d) {
        this.GetChunkLTM(this.cppObj, tmp);
        matrix4d.set(tmp);
    }

    public void getChunkCurVisBoundBox(Point3f point3f, Point3f point3f1) {
        this.GetChunkCurVisBoundBox(this.cppObj, arrFloat6);
        point3f.set(arrFloat6[0], arrFloat6[1], arrFloat6[2]);
        point3f1.set(arrFloat6[3], arrFloat6[4], arrFloat6[5]);
    }

    public void getChunkLocObj(Loc loc) {
        this.getChunkLTM(m4);
        m4.getEulers(Eul);
        Eul[0] *= -57.299999237060547D;
        Eul[1] *= -57.299999237060547D;
        Eul[2] *= 57.299999237060547D;
        loc.set(m4.m03, m4.m13, m4.m23, (float) Eul[0], (float) Eul[1], (float) Eul[2]);
    }

    public float getChunkMass() {
        return 0.0F;
    }

    public int frames() {
        return this.Frames(this.cppObj);
    }

    public void setFrame(int i) {
        this.SetFrame(this.cppObj, i);
    }

    public void setFrame(int i, int j, float f) {
        this.SetFrame(this.cppObj, i, j, f);
    }

    public void setFrameFromRange(int i, int j, float f) {
        this.SetFrameFromRange(this.cppObj, i, j, f);
    }

    public int hooks() {
        return this.Hooks(this.cppObj);
    }

    public int hookFind(String s) {
        int retVal = this.HookFind(this.cppObj, s);
        if (retVal < 0 && this.meshName != null) printDebugMessage(DEBUG_EXTREME, "HierMesh Debugger Error in hookFind(" + s + "): Hook '" + s + "' does not exist in HierMesh '" + this.meshName + "'.");
        return retVal;
    }

    public int hookParentChunk(String s) {
        int retVal = this.HookFind(this.cppObj, s) >> 16;
        if (retVal <= 0) retVal = -1;
        else retVal--;
        if (retVal < 0 && this.meshName != null) printDebugMessage(DEBUG_EXTREME, "HierMesh Debugger Error in hookParentChunk(" + s + "): Hook '" + s + "' does not exist in HierMesh '" + this.meshName + "'.");
        return retVal;
    }

    public String hookName(int i) {
        return this.HookName(this.cppObj, i);
    }

    public void hookMatrix(int i, Matrix4d matrix4d) {
        this.HookMatrix(this.cppObj, i, tmp);
        matrix4d.set(tmp);
    }

    public int chunkByHookNamed(int i) {
        return this.ChunkByHookNamed(this.cppObj, i);
    }

    public int hookFaceCollisFind(Matrix4d matrix4d, int ai[], Matrix4d matrix4d1) {
        tmp[0] = matrix4d.m00;
        tmp[4] = matrix4d.m01;
        tmp[8] = matrix4d.m02;
        tmp[12] = matrix4d.m03;
        tmp[1] = matrix4d.m10;
        tmp[5] = matrix4d.m11;
        tmp[9] = matrix4d.m12;
        tmp[13] = matrix4d.m13;
        tmp[2] = matrix4d.m20;
        tmp[6] = matrix4d.m21;
        tmp[10] = matrix4d.m22;
        tmp[14] = matrix4d.m23;
        tmp[3] = matrix4d.m30;
        tmp[7] = matrix4d.m31;
        tmp[11] = matrix4d.m32;
        tmp[15] = matrix4d.m33;
        int i = this.HookFaceCollisFind(this.cppObj, tmp, ai, tmp2);
        if (i != -1) matrix4d1.set(tmp2);
        return i;
    }

    public int hookChunkFaceFind(Matrix4d matrix4d, Matrix4d matrix4d1) {
        tmp[0] = matrix4d.m00;
        tmp[4] = matrix4d.m01;
        tmp[8] = matrix4d.m02;
        tmp[12] = matrix4d.m03;
        tmp[1] = matrix4d.m10;
        tmp[5] = matrix4d.m11;
        tmp[9] = matrix4d.m12;
        tmp[13] = matrix4d.m13;
        tmp[2] = matrix4d.m20;
        tmp[6] = matrix4d.m21;
        tmp[10] = matrix4d.m22;
        tmp[14] = matrix4d.m23;
        tmp[3] = matrix4d.m30;
        tmp[7] = matrix4d.m31;
        tmp[11] = matrix4d.m32;
        tmp[15] = matrix4d.m33;
        int i = this.HookChunkFaceFind(this.cppObj, tmp, tmp2);
        if (i != -1) matrix4d1.set(tmp2);
        return i;
    }

    public int hookFaceFind(Matrix4d matrix4d, int ai[], Matrix4d matrix4d1) {
        tmp[0] = matrix4d.m00;
        tmp[4] = matrix4d.m01;
        tmp[8] = matrix4d.m02;
        tmp[12] = matrix4d.m03;
        tmp[1] = matrix4d.m10;
        tmp[5] = matrix4d.m11;
        tmp[9] = matrix4d.m12;
        tmp[13] = matrix4d.m13;
        tmp[2] = matrix4d.m20;
        tmp[6] = matrix4d.m21;
        tmp[10] = matrix4d.m22;
        tmp[14] = matrix4d.m23;
        tmp[3] = matrix4d.m30;
        tmp[7] = matrix4d.m31;
        tmp[11] = matrix4d.m32;
        tmp[15] = matrix4d.m33;
        int i = this.HookFaceFind(this.cppObj, tmp, ai, tmp2);
        if (i != -1) matrix4d1.set(tmp2);
        return i;
    }

    public void hookFaceMatrix(int i, int j, Matrix4d matrix4d) {
        this.HookFaceMatrix(this.cppObj, i, j, tmp);
        matrix4d.set(tmp);
    }

    public int materials() {
        return this.Materials(this.cppObj);
    }

    public int materialFind(String s, int i, int j) {
        int retVal = this.MaterialFind(this.cppObj, s, i, j);
        if (retVal < 0) printDebugMessage(DEBUG_DETAILED, "HierMesh Debugger Error in materialFind(" + s + ", " + i + ", " + j + "): Material '" + s + "' does not exist.");
        return retVal;
    }

    public int materialFind(String s, int i) {
        int retVal = this.MaterialFind(this.cppObj, s, i, -1);
        if (retVal < 0) printDebugMessage(DEBUG_DETAILED, "HierMesh Debugger Error in materialFind(" + s + ", " + i + "): Material '" + s + "' does not exist.");
        return retVal;
    }

    public int materialFind(String s) {
        int retVal = this.MaterialFind(this.cppObj, s, 0, -1);
        if (retVal < 0) printDebugMessage(DEBUG_DETAILED, "HierMesh Debugger Error in materialFind(" + s + "): Material '" + s + "' does not exist.");
        return retVal;
    }

    public int materialFindInChunk(String s, int i) {
        int retVal = this.MaterialFindInChunk(this.cppObj, s, i);
        if (retVal < 0) printDebugMessage(DEBUG_DETAILED, "HierMesh Debugger Error in materialFindInChunk(" + s + ", " + i + "): Material '" + s + "' does not exist.");
        return retVal;
    }

    public Mat material(int i) {
        return (Mat) this.Material(this.cppObj, i);
    }

    public void materialReplace(int i, Mat mat) {
        this.MaterialReplace(this.cppObj, i, mat.cppObject());
    }

    public void materialReplace(int i, String s) {
        this.MaterialReplace(this.cppObj, i, s);
    }

    public void materialReplace(String s, Mat mat) {
        try { // TODO: By SAS~Storebror: Avoid Runtime Exceptions in case of missing materials
            this.MaterialReplace(this.cppObj, s, mat.cppObject());
        } catch (Exception e) {
            System.out.println("Caught Exception in materialReplace(" + s + ", mat):");
            e.printStackTrace();
        }
    }

    public void materialReplaceToNull(String s) {
        try { // TODO: By SAS~Storebror: Avoid Runtime Exceptions in case of missing materials
            this.MaterialReplace(this.cppObj, s, 0);
        } catch (Exception e) {
            System.out.println("Caught Exception in materialReplaceToNull(" + s + "):");
            e.printStackTrace();
        }
    }

    public void materialReplace(String s, String s1) {
        try { // TODO: By SAS~Storebror: Avoid Runtime Exceptions in case of missing materials
            this.MaterialReplace(this.cppObj, s, s1);
        } catch (Exception e) {
            System.out.println("Caught Exception in materialReplace(" + s + ", " + s1 + "):");
            e.printStackTrace();
        }
    }

    public int[] hideSubTrees(String s) {
        this.chunkMap = null;
        return this.HideChunksInSubtrees(this.cppObj, s);
    }

    public int[] getSubTrees(String s) {
        this.chunkMap = null;
        return this.GetChunksInSubtrees(this.cppObj, s);
    }

    public int[] getSubTreesSpec(String s) {
        this.chunkMap = null;
        return this.GetChunksInSubtreesSpec(this.cppObj, s);
    }

    public float poseCRC() {
        return this.PoseCRC(this.cppObj);
    }

    public boolean isVisualRayHit(Point3d point3d, Vector3d vector3d, double d, double d1, Matrix4d matrix4d) {
        tmp[0] = matrix4d.m00;
        tmp[4] = matrix4d.m01;
        tmp[8] = matrix4d.m02;
        tmp[12] = matrix4d.m03;
        tmp[1] = matrix4d.m10;
        tmp[5] = matrix4d.m11;
        tmp[9] = matrix4d.m12;
        tmp[13] = matrix4d.m13;
        tmp[2] = matrix4d.m20;
        tmp[6] = matrix4d.m21;
        tmp[10] = matrix4d.m22;
        tmp[14] = matrix4d.m23;
        tmp[3] = matrix4d.m30;
        tmp[7] = matrix4d.m31;
        tmp[11] = matrix4d.m32;
        tmp[15] = matrix4d.m33;
        arrDouble6[0] = point3d.x;
        arrDouble6[1] = point3d.y;
        arrDouble6[2] = point3d.z;
        arrDouble6[3] = vector3d.x;
        arrDouble6[4] = vector3d.y;
        arrDouble6[5] = vector3d.z;
//        return this.isVisualRayHit(this.cppObj, arrDouble6, d, d1, tmp) != 0;
        try {
            return this.isVisualRayHit(this.cppObj, arrDouble6, d, d1, tmp) != 0;
        } catch (Throwable t) {
            return false;
        }
    }

    public int ApplyDecal_test(float f, float f1, boolean flag, Loc loc, int i) {
        loc.getMatrix(m4);
        tmp[0] = m4.m00;
        tmp[4] = m4.m01;
        tmp[8] = m4.m02;
        tmp[12] = m4.m03;
        tmp[1] = m4.m10;
        tmp[5] = m4.m11;
        tmp[9] = m4.m12;
        tmp[13] = m4.m13;
        tmp[2] = m4.m20;
        tmp[6] = m4.m21;
        tmp[10] = m4.m22;
        tmp[14] = m4.m23;
        tmp[3] = m4.m30;
        tmp[7] = m4.m31;
        tmp[11] = m4.m32;
        tmp[15] = m4.m33;
        float af[] = new float[3];
        af[0] = 0.0F;
        af[1] = 0.0F;
        af[2] = 0.0F;
        int ai[] = new int[this.chunks()];
        for (int j = 0; j < ai.length; j++)
            ai[j] = j;

        int k = this.ApplyDecal(this.cppObj, rnd.nextInt(0, 2), f, f1, flag ? 1 : 0, tmp, af, i, ai, ai.length);
        System.out.println("-- applyDec: " + k + "(chIdx:" + i + ")");
        return k;
    }

    public int grabDecalsFromChunk(int i) {
        if (i < 0) return 0;
        else return this.GrabDecalsFromChunk(this.cppObj, i);
    }

    public int applyGrabbedDecalsToChunk(int i) {
        if (i < 0) return 0;
        else return this.ApplyGrabbedDecalsToChunk(this.cppObj, i);
    }

    public HierMesh(String s) {
        super(0);
        this.meshName = s;
        this.chunkMap = null;
        this.curchunk = 0;
        if (s == null) throw new GObjException("Meshname is empty");
        this.cppObj = this.Load(s);
        if (this.cppObj == 0) throw new GObjException("HierMesh " + s + " not created");
        else {
            this.collisionR = this.CollisionR(this.cppObj);
            this.visibilityR = this.VisibilityR(this.cppObj);
            Pre.load(s);
            return;
        }
    }

    public HierMesh(HierMesh hiermesh, int i) {
        super(0);
        this.chunkMap = null;
        this.curchunk = 0;
        if (hiermesh == null) throw new GObjException("HierMesh is empty");
        hiermesh.setCurChunk(i);
        this.cppObj = this.LoadSubtree(hiermesh.cppObj);
        if (this.cppObj == 0) throw new GObjException("HierMesh (sub) not created");
        else {
            this.collisionR = this.CollisionR(this.cppObj);
            this.visibilityR = this.VisibilityR(this.cppObj);
            return;
        }
    }

    public HierMesh(HierMesh hiermesh, int i, Loc loc) {
        super(0);
        this.chunkMap = null;
        this.curchunk = 0;
        if (hiermesh == null) throw new GObjException("HierMesh is empty");
        hiermesh.setCurChunk(i);
        loc.getMatrix(m4);
        tmp[0] = m4.m00;
        tmp[4] = m4.m01;
        tmp[8] = m4.m02;
        tmp[12] = m4.m03;
        tmp[1] = m4.m10;
        tmp[5] = m4.m11;
        tmp[9] = m4.m12;
        tmp[13] = m4.m13;
        tmp[2] = m4.m20;
        tmp[6] = m4.m21;
        tmp[10] = m4.m22;
        tmp[14] = m4.m23;
        tmp[3] = m4.m30;
        tmp[7] = m4.m31;
        tmp[11] = m4.m32;
        tmp[15] = m4.m33;
        this.cppObj = this.LoadSubtreeLoc(hiermesh.cppObj, tmp);
        if (this.cppObj == 0) throw new GObjException("HierMesh (sub, loc) not created");
        else {
            this.collisionR = this.CollisionR(this.cppObj);
            this.visibilityR = this.VisibilityR(this.cppObj);
            return;
        }
    }

    private native void GetChunkCurVisBoundBox(int i, float af[]);

    private native int Load(String s);

    private native int LoadSubtree(int i);

    private native int LoadSubtreeLoc(int i, double ad[]);

    private native int[] HideChunksInSubtrees(int i, String s);

    private native int[] GetChunksInSubtrees(int i, String s);

    private native int[] GetChunksInSubtreesSpec(int i, String s);

    private native void SetPosXYZATK(int i, double d, double d1, double d2, float f, float f1, float f2);

    private native int PreRender(int i);

    private native void Render(int i);

    private native void RenderShadowProjective(int i);

    private native void RenderShadowVolume(int i);

    private native void RenderShadowVolumeHQ(int i);

    private native int DetectCollision(int i, double ad[], int j, double ad1[]);

    private native int DetectCollisionMesh(int i, double ad[], int j, double ad1[]);

    private native float DetectCollisionLine(int i, double ad[], double ad1[], double ad2[]);

    private native int DetectCollisionLineMulti(int i, double ad[], double ad1[], double ad2[]);

    private native float DetectCollisionQuad(int i, double ad[], double ad1[]);

    private native int DetectCollisionQuadMultiH(int i, double ad[], double ad1[]);

    private native int DetectCollisionPoint(int i, double ad[], double ad1[]);

    private native float DetectCollisionPlane(int i, double ad[], double ad1[]);

    private native float VisibilityR(int i);

    private native float CollisionR(int i);

    private native float GetUniformMaxDist(int i);

    private native int Chunks(int i);

    private native void SetCurChunk(int i, int j);

    private native int SetCurChunkByName(int i, String s);

    private native int ChunkFind(int i, String s);

    private native int ChunkFindCheck(int i, String s);

    private native String ChunkName(int i);

    private native float GetChunkVisibilityR(int i);

    private native int GetChunkVisibility(int i);

    private native void SetChunkVisibility(int i, int j);

    private native void SetChunkAngles(int i, float af[]);

    private native void SetChunkLocate(int i, float af[], float af1[]);

    private native int Frames(int i);

    private native void SetFrame(int i, int j);

    private native void SetFrame(int i, int j, int k, float f);

    private native void SetFrameFromRange(int i, int j, int k, float f);

    private native int Hooks(int i);

    private native int HookFind(int i, String s);

    private native String HookName(int i, int j);

    private native void HookMatrix(int i, int j, double ad[]);

    private native void GetChunkLTM(int i, double ad[]);

    private native int ChunkByHookNamed(int i, int j);

    private native int HookFaceCollisFind(int i, double ad[], int ai[], double ad1[]);

    private native int HookFaceFind(int i, double ad[], int ai[], double ad1[]);

    private native int HookChunkFaceFind(int i, double ad[], double ad1[]);

    private native void HookFaceMatrix(int i, int j, int k, double ad[]);

    private native int Materials(int i);

    private native int MaterialFind(int i, String s, int j, int k);

    private native int MaterialFindInChunk(int i, String s, int j);

    private native Object Material(int i, int j);

    private native void MaterialReplace(int i, int j, int k);

    private native void MaterialReplace(int i, int j, String s);

    private native void MaterialReplace(int i, String s, int j);

    private native void MaterialReplace(int i, String s, String s1);

    private native float PoseCRC(int i);

    private native int isVisualRayHit(int i, double ad[], double d, double d1, double ad1[]);

    private native int ApplyDecal(int i, int j, float f, float f1, int k, double ad[], float af[], int l, int ai[], int i1);

    private native int GrabDecalsFromChunk(int i, int j);

    private native int ApplyGrabbedDecalsToChunk(int i, int j);

    private native void RenderChunkMirror(int i, double ad[], double ad1[], double ad2[]);

    private static native void RenderShadowPairs(int i, int ai[]);

    private static Matrix4d    m4             = new Matrix4d();
    private static float       arrFloat6[]    = new float[6];
    private static double      arrDouble6[]   = new double[6];
    private static double      Eul[]          = new double[3];
    private static int         shadowPairs[]  = new int[32];
    private static float       _chunkAngles[] = new float[3];
    private HashMapExt         chunkMap;
    private static Loc         _chunkLoc      = new Loc();
    private int                curchunk;
    private static RangeRandom rnd            = new RangeRandom();

    // Patch Pack 107, additional HierMesh Debugging Settings
    private String              meshName;

    private static int          debugLevel       = Integer.MIN_VALUE;
    private static final int    DEBUG_NONE       = 0;
    private static final int    DEBUG_NORMAL     = 1;
    private static final int    DEBUG_DETAILED   = 2;
    private static final int    DEBUG_EXTREME    = 3;
    private static final int    DEBUG_DEFAULT    = DEBUG_NONE;

    private static ArrayList    stackTraceHashes = null;
    private static StringWriter stringWriter     = null;
    private static PrintWriter  printWriter      = null;

    private static int curDebugLevel() {
        if (debugLevel == Integer.MIN_VALUE) debugLevel = Config.cur.ini.get("Mods", "DEBUG_HIERMESH", DEBUG_DEFAULT);
        return debugLevel;
    }

    public static void printDebugMessage(int minLogLevel, String theMessage) {
        if (curDebugLevel() < minLogLevel) return;
        if (curDebugLevel() == DEBUG_EXTREME) {
            printDebug(theMessage);
            return;
        }
        System.out.println(theMessage);
    }
    
    public String getMeshName() {
        return meshName;
    }

    private static String getStackTraceAsString(Exception e) {
        if (stringWriter == null || printWriter == null) {
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
        }
        e.printStackTrace(printWriter);
        String retVal = stringWriter.getBuffer().toString();
        printWriter.flush();
        stringWriter.flush();
        stringWriter.getBuffer().setLength(0);
        return retVal;
    }

    public static void printDebug(String theMessage) {
        if (curDebugLevel() == 0) return;
        System.out.println(theMessage);
        if (debugLevel < DEBUG_EXTREME) return;
        if (stackTraceHashes == null) stackTraceHashes = new ArrayList();
        Exception e = new Exception("HierMesh Debugger Stacktrace");
        String logLine = theMessage + getStackTraceAsString(e);
        Integer stackTraceHash = new Integer(logLine.hashCode());
        if (stackTraceHashes.contains(stackTraceHash)) return;
        stackTraceHashes.add(stackTraceHash);
        System.out.println("Stacktrace follows:");
        e.printStackTrace();
    }
    // ---

    static {
        GObj.loadNative();
    }

}
