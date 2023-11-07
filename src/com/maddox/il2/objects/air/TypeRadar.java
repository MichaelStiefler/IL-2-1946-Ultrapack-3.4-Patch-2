package com.maddox.il2.objects.air;

import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import java.io.IOException;

public interface TypeRadar
{

    public abstract boolean typeRadarToggleMode();

    public abstract void typeRadarRangePlus();

    public abstract void typeRadarRangeMinus();

    public abstract void typeRadarGainPlus();

    public abstract void typeRadarGainMinus();

    public abstract void typeRadarReplicateToNet(NetMsgGuaranted netmsgguaranted)
        throws IOException;

    public abstract void typeRadarReplicateFromNet(NetMsgInput netmsginput)
        throws IOException;
}
