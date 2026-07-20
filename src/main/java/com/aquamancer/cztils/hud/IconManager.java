package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.abils.ActiveType;
import com.aquamancer.czlib.api.abils.Actives;
import net.minecraft.util.Identifier;

import java.util.Set;

public class IconManager {
    public static Identifier getIdentifier(ActiveType active) {
        return Identifier.of("unofficial-monumenta-mod", "textures/abilities/flamecaller/flamestrike.png");
    }

    public static Set<ActiveType> alwaysShow() {
        return Set.of(Actives.Right.SIDEARM, Actives.Lifeline.STEEL_STALLION);
    }
}
