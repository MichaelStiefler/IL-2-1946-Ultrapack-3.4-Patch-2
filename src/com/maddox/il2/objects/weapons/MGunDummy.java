package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.BulletAimer;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorPosMove;
import com.maddox.il2.engine.BulletProperties;
import com.maddox.il2.engine.GunGeneric;
import com.maddox.il2.engine.GunProperties;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Loc;

public class MGunDummy extends Gun implements BulletEmitter, BulletAimer {

    public MGunDummy() {
        this.hasBullets = true;
        this.flags = this.flags | 0x4004;
        this.pos = new ActorPosMove(this);
        this._index = -1;
    }

    public void emptyGun() {
        this.hasBullets = false;
    }

    public BulletEmitter detach(HierMesh hiermesh, int i) {
        return this;
    }

    public void initRealisticGunnery(boolean flag1) {
        int i = super.prop.bullet.length;
        for (int j = 0; j < i; j++) {
            super.bulletAG[j] = 0.0F;
            super.bulletKV[j] = 0.0F;
        }
    }

    public boolean isEnablePause() {
        return false;
    }

    public float bulletMassa() {
        return 0.0F;
    }

    public float bulletSpeed() {
        return 0.0F;
    }

    public int countBullets() {
        return this.hasBullets ? 0x7fffffff : 0;
    }

    public boolean haveBullets() {
        return this.hasBullets;
    }

    public void loadBullets() {
        this.hasBullets = true;
    }

    public void loadBullets(int i) {
        super.loadBullets(i);
        this.resetGuard();
        this.hasBullets = true;
    }

    public Class bulletClass() {
        return null;
    }

    public void setBulletClass(Class class2) {
    }

    public boolean isShots() {
        return false;
    }

    public void shots(int j, float f1) {
    }

    public void shots(int j) {
    }

    public String getHookName() {
        return "Body";
    }

    public void set(Actor actor1, String s1, Loc loc1) {
    }

    public void set(Actor actor1, String s2, String s3) {
    }

    public void set(Actor actor1, String s1) {
    }

    public String getDayProperties(String s) {
        return null;
    }

    public void createdProperties() {
        super.createdProperties();
    }

    public String prop_fireMesh() {
        return null;
    }

    public String prop_fire() {
        return null;
    }

    public String prop_sprite() {
        return null;
    }

    public void setConvDistance(float f2, float f3) {
    }

    public void init() {
        int i = super.prop.bullet.length;
        super.bulletAG = new float[i];
        super.bulletKV = new float[i];
        this.initRealisticGunnery();
    }

    public Bullet createNextBullet(Vector3d vector3d, int i, GunGeneric gungeneric, Loc loc, Vector3d vector3d1, long l) {
        MGunDummy.bullet = new BulletAircraftGeneric(vector3d, i, gungeneric, loc, vector3d1, l);
        return MGunDummy.bullet;
    }

    public void _loadBullets(int i) {
        super._loadBullets(i);
        this.resetGuard();
    }

    private void resetGuard() {
        this.guardBullet = null;
    }

    public int nextIndexBulletType() {
        this._index++;
        if (this._index == super.prop.bullet.length) {
            this._index = 0;
        }
        return this._index;
    }

    protected void finalize() {
        if (!this.isDestroyed()) {
            this.destroy();
        }
        super.finalize();
    }

    public GunProperties createProperties() {
        GunProperties gunproperties = super.createProperties();
        gunproperties.bCannon = false;
        gunproperties.bUseHookAsRel = true;
        gunproperties.fireMesh = null;
        gunproperties.fire = null;
        gunproperties.sprite = null;
        gunproperties.smoke = null;
        gunproperties.shells = null;
        gunproperties.sound = null;
        gunproperties.emitColor = null;
        gunproperties.emitI = 0.0F;
        gunproperties.emitR = 0.0F;
        gunproperties.emitTime = 0.0F;
        gunproperties.aimMinDist = 0.0F;
        gunproperties.aimMaxDist = 0.0F;
        gunproperties.weaponType = 3;
        gunproperties.maxDeltaAngle = 0.0F;
        gunproperties.shotFreq = 0.0F;
        gunproperties.traceFreq = 0;
        gunproperties.bullets = 0x7fffffff;
        gunproperties.bulletsCluster = 1;
        gunproperties.bullet = new BulletProperties[] { new BulletProperties() };
        gunproperties.bullet[0].massa = 0.0F;
        gunproperties.bullet[0].kalibr = 0.0F;
        gunproperties.bullet[0].speed = 0.0F;
        gunproperties.bullet[0].power = 0.0F;
        gunproperties.bullet[0].powerType = 0;
        gunproperties.bullet[0].powerRadius = 0.0F;
        gunproperties.bullet[0].traceMesh = null;
        gunproperties.bullet[0].traceTrail = null;
        gunproperties.bullet[0].traceColor = 0;
        gunproperties.bullet[0].timeLife = 0.0F;
        return gunproperties;
    }

    private boolean hasBullets;
    protected com.maddox.il2.objects.weapons.BulletAircraftGeneric guardBullet;
    private static com.maddox.il2.objects.weapons.Bullet           bullet;
    private int                                                    _index;
}
