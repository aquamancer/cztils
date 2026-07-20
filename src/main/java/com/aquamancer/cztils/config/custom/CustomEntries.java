package com.aquamancer.cztils.config.custom;

import com.aquamancer.cztils.hud.AbilityIcon;
import com.aquamancer.cztils.hud.TextureInfo;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CustomEntries {
    public static AbstractConfigListEntry textureEntry(String name, TextureInfo info) {
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();
        List<AbstractConfigListEntry> entries = List.of(
                builder.startStrField(Text.literal("Identifier"), info.getIdentifier())
                        .setSaveConsumer(id -> info.setIdentifier(id)).build(),
                builder.startIntField(Text.literal("u"), info.getU())
                        .setSaveConsumer(v -> info.setU(v)).build(),
                builder.startIntField(Text.literal("v"), info.getV())
                        .setSaveConsumer(v -> info.setV(v)).build(),
                builder.startIntField(Text.literal("uw"), info.getUw())
                        .setSaveConsumer(v -> info.setUw(v)).build(),
                builder.startIntField(Text.literal("uh"), info.getUh())
                        .setSaveConsumer(v -> info.setUh(v)).build(),
                builder.startIntField(Text.literal("sourceWidth"), info.getSourceWidth())
                        .setSaveConsumer(v -> info.setSourceWidth(v)).build(),
                builder.startIntField(Text.literal("sourceHeight"), info.getSourceHeight())
                        .setSaveConsumer(v -> info.setSourceHeight(v)).build()
        );
        return builder.startSubCategory(Text.literal(name), entries).build();
    }

    public static AbstractConfigListEntry textureEntry(Enum<?> key, Map<Enum<?>, AbilityIcon.Type> config) {
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();

        AbstractConfigListEntry entry = builder.startEnumSelector(
                        Text.literal(key.name()),
                        AbilityIcon.Type.class,
                        config.get(key)
                )
                .setSaveConsumer(v -> config.put(key, v))
                .build();

        return entry;
    }
}
