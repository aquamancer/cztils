package com.aquamancer.cztils;

import com.aquamancer.czlib.api.abils.ActiveSlot;
import com.aquamancer.czlib.api.abils.Actives;
import com.aquamancer.cztils.config.ModConfig;
import com.aquamancer.cztils.config.custom.CustomAnnots;
import com.aquamancer.cztils.config.custom.SpecConfig;
import com.aquamancer.cztils.hud.Hud;
import com.aquamancer.cztils.tooltip.TooltipHelper;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.MutableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
			if (!Cztils.config.hudEnabled) return;
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.currentScreen instanceof HandledScreen<?>) return;

			if (client.options.playerListKey.isPressed()) {
				hud.render(context, Cztils.config.tabPressedRenderMode);
			} else {
				hud.render(context, Cztils.config.inGameRenderMode);
			}
		}));

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			TooltipHelper.onTooltip(stack, context, lines);
		});
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}