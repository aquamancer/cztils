package com.aquamancer.cztils;

import com.aquamancer.cztils.config.ModConfig;
import com.aquamancer.cztils.config.custom.CustomAnnots;
import com.aquamancer.cztils.config.custom.SpecConfig;
import com.aquamancer.cztils.hud.Hud;
import com.aquamancer.cztils.lifeline.AbilityPacketHandler;
import com.aquamancer.cztils.tooltip.TooltipHelper;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cztils implements ModInitializer {
	public static final String MOD_ID = "cztils";
	public static ModConfig config;
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Hud hud = new Hud();

	@Override
	public void onInitialize() {
		ConfigHolder<ModConfig> configHolder = AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = configHolder.getConfig();
		CustomAnnots.init();
		config.specConfigs.values().forEach(SpecConfig::updateEnumSets);
		configHolder.registerSaveListener((a, b) -> {
			hud.rebuild();
			return ActionResult.PASS;
		});

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			if (Cztils.config.tooltipsEnabled) {
				TooltipHelper.onTooltip(lines);
			}
		});
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}