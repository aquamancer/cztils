package com.aquamancer.cztils;

import com.aquamancer.czlib.api.abils.Actives;
import com.aquamancer.cztils.config.ModConfig;
import com.aquamancer.cztils.config.custom.CustomAnnots;
import com.aquamancer.cztils.hud.*;
import com.aquamancer.czlib.api.textures.ZenithTextures;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cztils implements ModInitializer {
	public static final String MOD_ID = "cztils";
	public static ModConfig config;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ConfigHolder<ModConfig> configHolder = AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = configHolder.getConfig();
		AutoConfig.getConfigHolder(ModConfig.class)
				.registerSaveListener((holder, data) -> {
					data.updateFlatMap();
					return ActionResult.PASS;
				});

		CustomAnnots.init();

		Nametag name = new Nametag(500, 500)
				.setName("aquablade").setSpec("Dawn").setHp(15).setHpMax(20).setGraveTimer(3.05);
		AbilityIcon icon = new ItemAbilityIcon(
//				Identifier.of("minecraft", "textures/item/bell.png"),
//				Identifier.of("unofficial-monumenta-mod", "textures/abilities/dawnbringer/radiant_blessing.png"),
				10, 10,
				16, 16,
				1,
				ZenithTextures.getItem(Actives.Combo.SOOTHING).get()
//				new TextureInfo(
////						"unofficial-monumenta-mod:textures/abilities/dawnbringer/radiant_blessing.png",
//						"minecraft:optifine/cit/monumenta/gui/depths/celestial_gifts/callicarpas_pointed_hat/callicarpas_pointed_hat.png",
//						0, 0, 16, 16, 16, 16
//				)
		).setBorderColor(0xffb3b4bc).setGrayedOut(0x80000000).setBackgroundFill(0xFF000000);

		HudRenderCallback.EVENT.register(((context, tickDelta) -> {
			name.render(context);
			icon.render(context);
		}));
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
