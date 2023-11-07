package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.il2.engine.BulletProperties;
import com.maddox.il2.engine.GunProperties;

public class Type5M1ot extends MGunAircraftGeneric
{

    public GunProperties createProperties()
    {
        GunProperties gunproperties = super.createProperties();
        gunproperties.bCannon = true;
        gunproperties.bUseHookAsRel = true;
        gunproperties.fireMesh = null;
        gunproperties.fire = "3DO/Effects/GunFire/37mm/GunFire.eff";
        gunproperties.sprite = null;
        gunproperties.smoke = "effects/smokes/CannonTank.eff";
        gunproperties.shells = null;
        gunproperties.sound = "weapon.air_cannon_37";
        gunproperties.emitColor = new Color3f(1.0F, 1.0F, 0.0F);
        gunproperties.emitI = 10F;
        gunproperties.emitR = 3F;
        gunproperties.emitTime = 0.03F;
        gunproperties.aimMinDist = 10F;
        gunproperties.aimMaxDist = 2000F;
        gunproperties.weaponType = -1;
        gunproperties.maxDeltaAngle = 0.43F;
        gunproperties.shotFreq = 5.83F;
        gunproperties.traceFreq = 2;
        gunproperties.bullets = 60;
        gunproperties.bulletsCluster = 1;
        gunproperties.bullet = (new BulletProperties[] {
            new BulletProperties(), new BulletProperties(), new BulletProperties(), new BulletProperties(), new BulletProperties()
        });
        gunproperties.bullet[0].massa = 0.35F;
        gunproperties.bullet[0].kalibr = 0.000567F;
        gunproperties.bullet[0].speed = 750F;
        gunproperties.bullet[0].power = 0.056F;
        gunproperties.bullet[0].powerType = 0;
        gunproperties.bullet[0].powerRadius = 1.5F;
        gunproperties.bullet[0].traceMesh = "3do/effects/tracers/20mmYellow/mono.sim";
        gunproperties.bullet[0].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[0].traceColor = 0xd200ffff;
        gunproperties.bullet[0].timeLife = 3F;
        gunproperties.bullet[1].massa = 0.354F;
        gunproperties.bullet[1].kalibr = 0.000567F;
        gunproperties.bullet[1].speed = 750F;
        gunproperties.bullet[1].power = 0.01F;
        gunproperties.bullet[1].powerType = 0;
        gunproperties.bullet[1].powerRadius = 2.0F;
        gunproperties.bullet[1].traceMesh = "3do/effects/tracers/20mmYellow/mono.sim";
        gunproperties.bullet[1].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[1].traceColor = 0xd200ffff;
        gunproperties.bullet[1].timeLife = 3F;
        gunproperties.bullet[2].massa = 0.378F;
        gunproperties.bullet[2].kalibr = 0.000567F;
        gunproperties.bullet[2].speed = 750F;
        gunproperties.bullet[2].power = 0.05F;
        gunproperties.bullet[2].powerType = 0;
        gunproperties.bullet[2].powerRadius = 1.5F;
        gunproperties.bullet[2].traceMesh = "3do/effects/tracers/20mmYellow/mono.sim";
        gunproperties.bullet[2].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[2].traceColor = 0;
        gunproperties.bullet[2].timeLife = 2.0F;
        gunproperties.bullet[3].massa = 0.35F;
        gunproperties.bullet[3].kalibr = 0.000567F;
        gunproperties.bullet[3].speed = 750F;
        gunproperties.bullet[3].power = 0.001F;
        gunproperties.bullet[3].powerType = 0;
        gunproperties.bullet[3].powerRadius = 0.1F;
        gunproperties.bullet[3].traceMesh = "3do/effects/tracers/20mmYellow/mono.sim";
        gunproperties.bullet[3].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[3].traceColor = 0xd200ffff;
        gunproperties.bullet[3].timeLife = 3F;
        gunproperties.bullet[4].massa = 0.35F;
        gunproperties.bullet[4].kalibr = 0.000567F;
        gunproperties.bullet[4].speed = 750F;
        gunproperties.bullet[4].power = 0.064F;
        gunproperties.bullet[4].powerType = 0;
        gunproperties.bullet[4].powerRadius = 2.0F;
        gunproperties.bullet[4].traceMesh = "3do/effects/tracers/20mmYellow/mono.sim";
        gunproperties.bullet[4].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[4].traceColor = 0;
        gunproperties.bullet[4].timeLife = 3F;
        return gunproperties;
    }
}
