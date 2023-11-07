package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.il2.engine.*;

public class MGunMG15120_Ta152NAGs extends MGunAircraftGeneric
{
    public GunProperties createProperties()
    {
        GunProperties gunproperties = super.createProperties();
        gunproperties.bCannon = false;
        gunproperties.bUseHookAsRel = true;
        gunproperties.fireMesh = "3DO/Effects/GunFire/20mm/mono.sim";
        gunproperties.fire = null;
        gunproperties.sprite = "3DO/Effects/GunFire/20mm/GunFlare.eff";
        gunproperties.smoke = "effects/smokes/MachineGun.eff";
        gunproperties.shells = "3DO/Effects/GunShells/GunShells.eff";
        gunproperties.sound = "weapon.MG15120NAG";
        gunproperties.emitColor = new Color3f(1.0F, 1.0F, 0.0F);
        gunproperties.emitI = 10F;
        gunproperties.emitR = 3F;
        gunproperties.emitTime = 0.03F;
        gunproperties.aimMinDist = 10F;
        gunproperties.aimMaxDist = 1000F;
        gunproperties.weaponType = 3;
        gunproperties.maxDeltaAngle = 0.21F;
        gunproperties.shotFreq = 10.8F;
        gunproperties.traceFreq = 3;
        gunproperties.bullets = 250;
        gunproperties.bulletsCluster = 1;
        gunproperties.bullet = (new BulletProperties[] {
            new BulletProperties(), new BulletProperties(), new BulletProperties()
        });
        gunproperties.bullet[0].massa = 0.092F;
        gunproperties.bullet[0].kalibr = 0.000404F;
        gunproperties.bullet[0].speed = 800F;
        gunproperties.bullet[0].power = 0.02F;
        gunproperties.bullet[0].powerType = 2;
        gunproperties.bullet[0].powerRadius = 0.8F;
        gunproperties.bullet[0].traceMesh = "3do/effects/tracers/20mmBlue/mono.sim";
        gunproperties.bullet[0].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[0].traceColor = 0xd2ff0000;
        gunproperties.bullet[0].timeLife = 8F;
        gunproperties.bullet[1].massa = 0.092F;
        gunproperties.bullet[1].kalibr = 0.000404F;
        gunproperties.bullet[1].speed = 800F;
        gunproperties.bullet[1].power = 0.02F;
        gunproperties.bullet[1].powerType = 2;
        gunproperties.bullet[1].powerRadius = 0.8F;
        gunproperties.bullet[1].traceMesh = "3do/effects/tracers/20mmBlue/mono.sim";
        gunproperties.bullet[1].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[1].traceColor = 0xd2ff0000;
        gunproperties.bullet[1].timeLife = 8F;
        gunproperties.bullet[2].massa = 0.092F;
        gunproperties.bullet[2].kalibr = 0.000404F;
        gunproperties.bullet[2].speed = 800F;
        gunproperties.bullet[2].power = 0.02F;
        gunproperties.bullet[2].powerType = 2;
        gunproperties.bullet[2].powerRadius = 0.8F;
        gunproperties.bullet[2].traceMesh = "3do/effects/tracers/20mmBlue/mono.sim";
        gunproperties.bullet[2].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[2].traceColor = 0xd2ff0000;
        gunproperties.bullet[2].timeLife = 8F;
        return gunproperties;
    }
}
