package com.danikvitek.slimeinabukkit.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PluginConfig {
    public static final String CHUNK_STATUS_PLACEHOLDER = "<chunk-status>";

    // config start

    private static final String CMD_PATH = "custom-model-data";
    private static final List<String> CMD_COMMENTS = List.of(
        "Custom model data, used for Slime in a Bucket.",
        "You can use these values in resource packs to display custom textures for slimes in buckets."
    );

    private static final String CALM_SLIME_CMD_PATH = CMD_PATH + ".calm-slime";
    private static final int CALM_SLIME_CMD_DEFAULT = 404;
    private static final List<String> CALM_SLIME_CMD_COMMENTS = List.of("Default: " + CALM_SLIME_CMD_DEFAULT);

    private static final String ACTIVE_SLIME_CMD_PATH = CMD_PATH + ".active-slime";
    private static final int ACTIVE_SLIME_CMD_DEFAULT = 200;
    private static final List<String> ACTIVE_SLIME_CMD_COMMENTS = List.of("Default: " + ACTIVE_SLIME_CMD_DEFAULT);

    private static final String SLIME_CHUNK_MESSAGE_PATH = "slime-chunk-message";
    private static final String SLIME_CHUNK_MESSAGE_DEFAULT =
        "<gray>This chunk </gray>" + CHUNK_STATUS_PLACEHOLDER + "<gray> a Slime chunk";
    private static final List<String> SLIME_CHUNK_MESSAGE_COMMENTS = List.of(
        "Message used in /slime_chunk command.",
        "You can use the <chunk-status> placeholder in the message to display the status of the chunk.",
        "Default: " + SLIME_CHUNK_MESSAGE_DEFAULT
    );

    private static final String BUCKET_TITLE_PATH = "bucket-title";
    private static final String BUCKET_TITLE_DEFAULT = "<!italic><lang:item.slimeinabukkit.slime_bucket>";
    private static final List<String> BUCKET_TITLE_COMMENTS = List.of(
        "Title of the slime bucket item.",
        "Default: " + BUCKET_TITLE_DEFAULT
    );

    private static final String CHUNK_STATUS_PATH = "chunk-status";
    private static final List<String> CHUNK_STATUS_COMMENTS =
        List.of("Messages used in /slime_chunk command with <chunk-status> placeholder.");

    private static final String CHUNK_STATUS_TRUE_PATH = CHUNK_STATUS_PATH + ".true";
    private static final String CHUNK_STATUS_TRUE_DEFAULT = "<green>is";
    private static final List<String> CHUNK_STATUS_TRUE_COMMENTS = List.of("Default: " + CHUNK_STATUS_TRUE_DEFAULT);

    private static final String CHUNK_STATUS_FALSE_PATH = CHUNK_STATUS_PATH + ".false";
    private static final String CHUNK_STATUS_FALSE_DEFAULT = "<red>is not";
    private static final List<String> CHUNK_STATUS_FALSE_COMMENTS = List.of("Default: " + CHUNK_STATUS_FALSE_DEFAULT);

    private static final String CAN_PICKUP_SLIME_PATH = "can-pickup-slime";
    private static final boolean CAN_PICKUP_SLIME_DEFAULT = true;
    private static final List<String> CAN_PICKUP_SLIME_COMMENTS = List.of(
        "Whether players can pick up slimes in buckets.",
        "Default: " + CAN_PICKUP_SLIME_DEFAULT
    );

    private static final String DEBUG_PATH = "debug";
    private static final boolean DEBUG_DEFAULT = false;
    private static final List<String> DEBUG_COMMENTS = List.of(
        "Whether to enable debug logging.",
        "Default: " + DEBUG_DEFAULT
    );

    private static final String SOUND_PATH = "sound";

    private static final String SOUND_MODE_PATH = SOUND_PATH + ".mode";
    private static final SoundMode SOUND_MODE_DEFAULT = SoundMode.LOOP;
    private static final List<String> SOUND_MODE_COMMENTS = List.of(
        "Sound mode for slimes in buckets.",
        "Available modes: off, once, loop.",
        "Default: " + SOUND_MODE_DEFAULT
    );

    private static final String SOUND_VOLUME_PATH = SOUND_PATH + ".volume";
    private static final float SOUND_VOLUME_DEFAULT = .5f;
    private static final List<String> SOUND_VOLUME_COMMENTS = List.of(
        "Volume of the sound played by slimes in buckets.",
        "Default: " + SOUND_VOLUME_DEFAULT
    );

    private static final String SOUND_PITCH_PATH = SOUND_PATH + ".pitch";
    private static final float SOUND_PITCH_DEFAULT = 1f;
    private static final List<String> SOUND_PITCH_COMMENTS = List.of(
        "Pitch of the sound played by slimes in buckets.",
        "Default: " + SOUND_PITCH_DEFAULT
    );

    private static final String ANIMATION_PATH = SOUND_PATH + ".animation";
    private static final String ANIMATION_FRAMETIME_PATH = ANIMATION_PATH + ".frametime";
    private static final String ANIMATION_FRAMES_PATH = ANIMATION_PATH + ".frames";
    private static final int ANIMATION_FRAMETIME_DEFAULT = 2;
    private static final List<String> ANIMATION_FRAMETIME_COMMENTS = List.of(
        "Animation frame time for slimes in buckets.",
        "Default: " + ANIMATION_FRAMETIME_DEFAULT
    );
    private static final List<Optional<Sound>> ANIMATION_FRAMES_DEFAULT = List.of(
        Optional.of(Sound.ENTITY_SLIME_JUMP_SMALL),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.of(Sound.ENTITY_SLIME_SQUISH_SMALL)
    );
    private static final List<String> ANIMATION_FRAMES_COMMENTS = List.of(
        "List of animation frames for slimes in buckets.",
        "Each frame is a name of a sound to play. If the frame should not play a sound, use an empty string value.",
        ANIMATION_FRAMES_DEFAULT.stream()
            .map(o -> o.map(Enum::name).orElse("''"))
            .collect(Collectors.joining(", ", "Default: [", "]"))
    );

    // config end

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final @NotNull ConfigAccessor configAccessor;
    private int calmSlimeCmd;
    private int activeSlimeCmd;
    private Component slimeBucketTitle;
    private String slimeChunkMessage;
    private Component chunkStatusTrue;
    private Component chunkStatusFalse;
    private boolean canPickupSlime;
    private SoundMode soundMode;
    private float soundVolume;
    private float soundPitch;
    private int animationFrametime;
    private List<Optional<Sound>> animationFrames;
    private boolean debug;

    @Contract(pure = false)
    public PluginConfig(@NotNull ConfigAccessor configAccessor) {
        this.configAccessor = configAccessor;
        read();
        update();
    }

    @Contract(pure = false)
    public void read() {
        final var config = configAccessor.getConfig();

        calmSlimeCmd = config.getInt(CALM_SLIME_CMD_PATH, CALM_SLIME_CMD_DEFAULT);
        activeSlimeCmd = config.getInt(ACTIVE_SLIME_CMD_PATH, ACTIVE_SLIME_CMD_DEFAULT);
        slimeBucketTitle = MINI_MESSAGE.deserialize(config.getString(BUCKET_TITLE_PATH, BUCKET_TITLE_DEFAULT));
        slimeChunkMessage = config.getString(SLIME_CHUNK_MESSAGE_PATH, SLIME_CHUNK_MESSAGE_DEFAULT);
        chunkStatusTrue = MINI_MESSAGE.deserialize(config.getString(CHUNK_STATUS_TRUE_PATH, CHUNK_STATUS_TRUE_DEFAULT));
        chunkStatusFalse =
            MINI_MESSAGE.deserialize(config.getString(CHUNK_STATUS_FALSE_PATH, CHUNK_STATUS_FALSE_DEFAULT));
        canPickupSlime = config.getBoolean(CAN_PICKUP_SLIME_PATH, CAN_PICKUP_SLIME_DEFAULT);
        soundMode = SoundMode.fromString(config.getString(SOUND_MODE_PATH, SOUND_MODE_DEFAULT.toString()));
        soundVolume = (float) config.getDouble(SOUND_VOLUME_PATH, SOUND_VOLUME_DEFAULT);
        soundPitch = (float) config.getDouble(SOUND_PITCH_PATH, SOUND_PITCH_DEFAULT);
        animationFrametime = config.getInt(ANIMATION_FRAMETIME_PATH, ANIMATION_FRAMETIME_DEFAULT);
        animationFrames = config.getStringList(ANIMATION_FRAMES_PATH).stream()
            .map(s -> s.isBlank() ? Optional.<Sound>empty() : Optional.of(Sound.valueOf(s)))
            .collect(Collectors.toList());
        if (animationFrames.isEmpty()) {
            animationFrames = ANIMATION_FRAMES_DEFAULT;
        }
        debug = config.getBoolean(DEBUG_PATH, DEBUG_DEFAULT);
    }

    @Contract(pure = false)
    public void update() {
        final var config = configAccessor.getConfig();

        config.setComments(CMD_PATH, CMD_COMMENTS);

        config.set(CALM_SLIME_CMD_PATH, calmSlimeCmd);
        config.setComments(CALM_SLIME_CMD_PATH, CALM_SLIME_CMD_COMMENTS);

        config.set(ACTIVE_SLIME_CMD_PATH, activeSlimeCmd);
        config.setComments(ACTIVE_SLIME_CMD_PATH, ACTIVE_SLIME_CMD_COMMENTS);

        config.set(BUCKET_TITLE_PATH, MINI_MESSAGE.serialize(slimeBucketTitle));
        config.setComments(BUCKET_TITLE_PATH, BUCKET_TITLE_COMMENTS);

        config.set(SLIME_CHUNK_MESSAGE_PATH, slimeChunkMessage);
        config.setComments(SLIME_CHUNK_MESSAGE_PATH, SLIME_CHUNK_MESSAGE_COMMENTS);

        config.setComments(CHUNK_STATUS_PATH, CHUNK_STATUS_COMMENTS);

        config.set(CHUNK_STATUS_TRUE_PATH, MINI_MESSAGE.serialize(chunkStatusTrue));
        config.setComments(CHUNK_STATUS_TRUE_PATH, CHUNK_STATUS_TRUE_COMMENTS);

        config.set(CHUNK_STATUS_FALSE_PATH, MINI_MESSAGE.serialize(chunkStatusFalse));
        config.setComments(CHUNK_STATUS_FALSE_PATH, CHUNK_STATUS_FALSE_COMMENTS);

        config.set(CAN_PICKUP_SLIME_PATH, canPickupSlime);
        config.setComments(CAN_PICKUP_SLIME_PATH, CAN_PICKUP_SLIME_COMMENTS);

        config.set(SOUND_MODE_PATH, soundMode.toString());
        config.setComments(SOUND_MODE_PATH, SOUND_MODE_COMMENTS);

        config.set(SOUND_VOLUME_PATH, soundVolume);
        config.setComments(SOUND_VOLUME_PATH, SOUND_VOLUME_COMMENTS);

        config.set(SOUND_PITCH_PATH, soundPitch);
        config.setComments(SOUND_PITCH_PATH, SOUND_PITCH_COMMENTS);

        config.set(ANIMATION_FRAMETIME_PATH, animationFrametime);
        config.setComments(ANIMATION_FRAMETIME_PATH, ANIMATION_FRAMETIME_COMMENTS);

        config.set(
            ANIMATION_FRAMES_PATH, animationFrames.stream()
                .map(o -> o.map(Enum::name).orElse(""))
                .collect(Collectors.toList())
        );
        config.setComments(ANIMATION_FRAMES_PATH, ANIMATION_FRAMES_COMMENTS);

        config.set(DEBUG_PATH, debug);
        config.setComments(DEBUG_PATH, DEBUG_COMMENTS);

        configAccessor.saveConfig();
    }

    public int getCalmSlimeCmd() {
        return calmSlimeCmd;
    }

    public int getActiveSlimeCmd() {
        return activeSlimeCmd;
    }

    public Component getSlimeBucketTitle() {
        return slimeBucketTitle;
    }

    public boolean canPickupSlime() {
        return canPickupSlime;
    }

    public @NotNull String getSlimeChunkMessage() {
        return slimeChunkMessage;
    }

    public @NotNull Component getChunkStatusTrue() {
        return chunkStatusTrue;
    }

    public @NotNull Component getChunkStatusFalse() {
        return chunkStatusFalse;
    }

    public @NotNull SoundMode getSoundMode() {
        return soundMode;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public float getSoundPitch() {
        return soundPitch;
    }

    public int getAnimationFrametime() {
        return animationFrametime;
    }

    public @NotNull List<Optional<Sound>> getAnimationFrames() {
        return animationFrames;
    }

    public boolean isDebug() {
        return debug;
    }
}
