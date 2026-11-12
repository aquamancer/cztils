package com.aquamancer.cztils.mixin;

import com.aquamancer.czlib.api.PartyMember;
import com.aquamancer.czlib.api.ZenithApi;
import com.aquamancer.czlib.api.abils.AbilitySpec;
import com.aquamancer.czlib.api.abils.Passives;
import com.aquamancer.czlib.api.util.TextUtils;
import com.aquamancer.cztils.Cztils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(targets = "net.minecraft.network.packet.s2c.play.BossBarS2CPacket$UpdateNameAction")
class BossBarUpdateNameMixin {
    @Unique
    private static final Pattern GRAVE = Pattern.compile("(\\w+)'s Grave");

    @ModifyArg(method = "accept",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/BossBarS2CPacket$Consumer;updateName(Ljava/util/UUID;Lnet/minecraft/text/Text;)V"),
            index = 1)
    private Text replaceTitle(Text original) {
        if (!Cztils.config.rebirthBossBarIndicator) return original;
        if (ZenithApi.getInstance().getParty().isEmpty()) return original;
        String title = original.getString();
        Matcher matcher = GRAVE.matcher(title);
        if (!matcher.find()) return original;

        String name = matcher.group(1);
        PartyMember player = ZenithApi.getInstance().getPlayer(name).orElse(null);
        if (player == null || !player.getPassives().containsKey(Passives.REBIRTH)) return original;

        MutableText firstHalf = Text.literal(title.substring(0, matcher.end() - 5)).setStyle(original.getStyle());
        MutableText secondHalf = TextUtils.applyGradient("Rebirth" + title.substring(matcher.end()), original.getStyle(), AbilitySpec.PRISMATIC_GRADIENT);

        return Text.empty().append(firstHalf).append(secondHalf);
    }
}
