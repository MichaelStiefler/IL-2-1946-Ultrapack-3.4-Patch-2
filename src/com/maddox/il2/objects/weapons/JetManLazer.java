package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.BulletProperties;
import com.maddox.il2.engine.GunProperties;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.objects.air.Aircraft;

public class JetManLazer extends MGunMK108s {

	public JetManLazer() {
	}

	// Never ending ammo
	public int nextIndexBulletType() {
		this.loadBullets();
		return super.nextIndexBulletType();
	}
	
	// No recoil for lazer gunz :)
	public void doStartBullet(double d) {
		Actor actor = getOwner();
		boolean modifyAM = false;
        if(actor == World.getPlayerAircraft())
        {
            if(World.cur().diffCur.Realistic_Gunnery && (((Aircraft)actor).FM instanceof RealFlightModel))
            {
            	modifyAM = true;
            }
        }
        if (modifyAM) {
        	this.vtemp.set(((RealFlightModel)((Aircraft)actor).FM).producedAM);
        	this.vwldtemp.set(((Aircraft)actor).FM.Vwld);
        	this.ftemp = ((RealFlightModel)((Aircraft)actor).FM).producedShakeLevel;
        }
		super.doStartBullet(d);
        if (modifyAM) {
        	((RealFlightModel)((Aircraft)actor).FM).producedAM.set(this.vtemp);
        	((Aircraft)actor).FM.Vwld.set(this.vwldtemp);
        	((RealFlightModel)((Aircraft)actor).FM).producedShakeLevel = this.ftemp;
        }
	}
	
	public GunProperties createProperties() {
		GunProperties gunproperties = super.createProperties();
		gunproperties.bCannon = true;
		gunproperties.bUseHookAsRel = true;
		gunproperties.fireMesh = "3DO/Effects/GunFire/7mm/mono.sim";
		gunproperties.fire = null;
		gunproperties.sprite = "3DO/Effects/GunFire/7mm/GunFlare.eff";
		gunproperties.smoke = null;
		gunproperties.shells = null;
		gunproperties.sound = "weapon.jetmanlazer";
		gunproperties.emitColor = new Color3f(1.0F, 1.0F, 0.0F);
		gunproperties.emitI = 2.5F;
		gunproperties.emitR = 1.0F;
		gunproperties.emitTime = 0.03F;
		gunproperties.aimMinDist = 10F;
		gunproperties.aimMaxDist = 1500F;
		gunproperties.weaponType = -1;
		gunproperties.maxDeltaAngle = 0.35F;
		gunproperties.shotFreq = 50F;
		gunproperties.traceFreq = 2;
		gunproperties.bullets = 50;
		gunproperties.bulletsCluster = 1;
		gunproperties.bullet = new BulletProperties[] { new BulletProperties(), new BulletProperties() };
		gunproperties.bullet[0].massa = 0.502F;
		gunproperties.bullet[0].kalibr = 0.000567F;
		gunproperties.bullet[0].speed = 900F;
		gunproperties.bullet[0].power = 0.0504F;
		gunproperties.bullet[0].powerType = 0;
		gunproperties.bullet[0].powerRadius = 1.5F;
		gunproperties.bullet[0].traceMesh = "3do/effects/tracers/20mmPink/mono.sim";
		gunproperties.bullet[0].traceTrail = null;
		gunproperties.bullet[0].traceColor = 0xd200ffff;
		gunproperties.bullet[0].timeLife = 4.0F;
		gunproperties.bullet[1].massa = 0.33F;
		gunproperties.bullet[1].kalibr = 0.000567F;
		gunproperties.bullet[1].speed = 900F;
		gunproperties.bullet[1].power = 0.054F;
		gunproperties.bullet[1].powerType = 0;
		gunproperties.bullet[1].powerRadius = 1.5F;
		gunproperties.bullet[1].traceMesh = null;
		gunproperties.bullet[1].traceTrail = null;
		gunproperties.bullet[1].traceColor = 0;
		gunproperties.bullet[1].timeLife = 3.0F;
		return gunproperties;
	}
	
    private Vector3d vtemp = new Vector3d();
    private Vector3d vwldtemp = new Vector3d();
    private float ftemp;

}
