package com.aquamancer.cztils;

import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.cztils.config.ConfigDefaults;
import com.aquamancer.cztils.config.ModConfig;
import com.aquamancer.cztils.config.custom.CustomAnnots;
import com.aquamancer.cztils.config.custom.SpecConfig;
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

import org.joml.Vector2i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class Cztils implements ModInitializer {
	public static final String MOD_ID = "cztils";
	public static ModConfig config;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Hud hud = new Hud();

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

		Player player = new Player();
		hud.party.put("riot games", player);
		Set<Spec> playerSpecs = Set.of(Spec.FLAME, Spec.WIND, Spec.EARTH, Spec.SHADOW);
		player.setName("riot games");
		player.setSpec(Spec.DAWN);
		player.setHp(15);
		player.setHpMax(20).setGraveTimer(3.05);
		player.setActives(
				List.of(
						new Active(Actives.WINDSWEPT, AbilitySpec.WIND, Rarity.TWISTED),
						new Active(Actives.SOLAR_RAY, AbilitySpec.PRISMATIC, Rarity.COMMON),
						new Active(Actives.FLAMESTRIKE, AbilitySpec.FLAME, Rarity.RARE),
						new Active(Actives.IGNEOUS_RUNE, AbilitySpec.FLAME, Rarity.LEGENDARY),
						new Active(Actives.RAPID_FIRE, AbilitySpec.STEEL, Rarity.COMMON),
						new Active(Actives.EARTHQUAKE, AbilitySpec.EARTH, Rarity.UNCOMMON),
						new Active(Actives.STEEL_STALLION, AbilitySpec.STEEL, Rarity.COMMON),
						new Active(Actives.BLADE_FLURRY, AbilitySpec.SHADOW, Rarity.COMMON)
				),
				playerSpecs
		);
		player.setCurses(
				Set.of(Curse.DEATH, Curse.ARACHNOPHOBIA)
		);
		player.setPassives(
				List.of(
						new Passive(Passives.BULWARK, AbilitySpec.EARTH, Rarity.COMMON),
						new Passive(Passives.SHARPSHOOTER, AbilitySpec.STEEL, Rarity.LEGENDARY),
						new Passive(Passives.FROZEN_DOMAIN, AbilitySpec.FROST, Rarity.RARE),
						new Passive(Passives.AEROMANCY, AbilitySpec.WIND, Rarity.RARE),
						new Passive(Passives.PYROMANIA, AbilitySpec.FLAME, Rarity.RARE),
						new Passive(Passives.REBIRTH, AbilitySpec.PRISMATIC, Rarity.COMMON)
				),
				playerSpecs
		);
		player.setGifts(
				Set.of(
						new Gift(Spec.STEEL),
						new Gift(Gifts.CRACKED_IDOL)
				)
		);
		hud.party.put("copy of riot games", player);
		hud.sort();
////		AbilityIcon icon = new ItemAbilityIcon(
////				Identifier.of("minecraft", "textures/item/bell.png"),
////				Identifier.of("unofficial-monumenta-mod", "textures/abilities/dawnbringer/radiant_blessing.png"),
////				10, 10,
////				16, 16,
////				1,
////				ZenithTextures.getItem(Actives.Combo.SOOTHING).get()
////				new TextureInfo(
//////						"unofficial-monumenta-mod:textures/abilities/dawnbringer/radiant_blessing.png",
////						"minecraft:optifine/cit/monumenta/gui/depths/celestial_gifts/callicarpas_pointed_hat/callicarpas_pointed_hat.png",
////						0, 0, 16, 16, 16, 16
////				)
////		).setBorderColor(0xffb3b4bc).setGrayedOut(0x80000000).setBackgroundFill(0xFF000000);

		HudRenderCallback.EVENT.register(((context, tickDelta) -> {
			hud.render(context);
		}));
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
