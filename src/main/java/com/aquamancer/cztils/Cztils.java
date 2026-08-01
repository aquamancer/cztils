package com.aquamancer.cztils;

import com.aquamancer.cztils.config.ModConfig;
import com.aquamancer.cztils.config.custom.CustomAnnots;
import com.aquamancer.cztils.config.custom.SpecConfig;
import com.aquamancer.cztils.hud.Hud;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
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

		HudRenderCallback.EVENT.register(((context, tickDelta) -> {
			if (!Cztils.config.hudEnabled || Cztils.config.inGameRenderMode == ModConfig.InGameRenderMode.OFF) return;
			MinecraftClient client = MinecraftClient.getInstance();
			if (Cztils.config.renderInInventory && client.currentScreen instanceof HandledScreen<?>) return;
			if (Cztils.config.inGameRenderMode == ModConfig.InGameRenderMode.TABLIST && !client.options.playerListKey.isPressed()) return;
			hud.render(context);
		}));
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}