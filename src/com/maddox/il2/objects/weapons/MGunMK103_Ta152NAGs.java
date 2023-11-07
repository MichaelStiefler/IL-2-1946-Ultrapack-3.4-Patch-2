package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.il2.engine.*;

public class MGunMK103_Ta152NAGs extends MGunAircraftGeneric
{
    public GunProperties createProperties()
    {
        GunProperties gunproperties = super.createProperties();
        gunproperties.bCannon = false;
        gunproperties.bUseHookAsRel = true;
        gunproperties.fireMesh = null;
        gunproperties.fire = "3DO/Effects/GunFire/30mm/GunFire.eff";
        gunproperties.sprite = "3DO/Effects/GunFire/30mm/GunFlare.eff";
        gunproperties.smoke = "effects/smokes/MachineGun.eff";
        gunproperties.shells = "3DO/Effects/GunShells/CannonShells.eff";
        gunproperties.sound = "weapon.MGun_MK103S";
        gunproperties.emitColor = new Color3f(1.0F, 1.0F, 0.0F);
        gunproperties.emitI = 10F;
        gunproperties.emitR = 3F;
        gunproperties.emitTime = 0.03F;
        gunproperties.aimMinDist = 10F;
        gunproperties.aimMaxDist = 1000F;
        gunproperties.weaponType = -1;
        gunproperties.maxDeltaAngle = 0.14F;
        gunproperties.shotFreq = 7.083333F;
        gunproperties.traceFreq = 3;
        gunproperties.bullets = 50;
        gunproperties.bulletsCluster = 1;
        gunproperties.bullet = (new BulletProperties[] {
            new BulletProperties(), new BulletProperties(), new BulletProperties()
        });
        gunproperties.bullet[0].massa = 0.33F;
        gunproperties.bullet[0].kalibr = 0.000408F;
        gunproperties.bullet[0].speed = 850F;
        gunproperties.bullet[0].power = 0.064F;
        gunproperties.bullet[0].powerType = 2;
        gunproperties.bullet[0].powerRadius = 1.5F;
        gunproperties.bullet[0].traceMesh = "3do/effects/tracers/20mmYellow/mono.sim";
        gunproperties.bullet[0].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[0].traceColor = 0xd200ffff;
        gunproperties.bullet[0].timeLife = 8F;
        gunproperties.bullet[1].massa = 0.33F;
        gunproperties.bullet[1].kalibr = 0.000408F;
        gunproperties.bullet[1].speed = 850F;
        gunproperties.bullet[1].power = 0.064F;
        gunproperties.bullet[1].powerType = 2;
        gunproperties.bullet[1].powerRadius = 1.5F;
        gunproperties.bullet[1].traceMesh = "3do/effects/tracers/20mmYellow/mono.sim";
        gunproperties.bullet[1].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[1].traceColor = 0xd200ffff;
        gunproperties.bullet[1].timeLife = 8F;
        gunproperties.bullet[2].massa = 0.455F;
        gunproperties.bullet[2].kalibr = 0.000408F;
        gunproperties.bullet[2].speed = 790F;
        gunproperties.bullet[2].power = 0.0766534F;
        gunproperties.bullet[2].powerType = 2;
        gunproperties.bullet[2].powerRadius = 0.0031F;
        gunproperties.bullet[2].traceMesh = "3do/effects/tracers/20mmWhite/mono.sim";
        gunproperties.bullet[2].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[2].traceColor = 0xd2ffffff;
        gunproperties.bullet[2].timeLife = 8F;
        return gunproperties;
    }
}
