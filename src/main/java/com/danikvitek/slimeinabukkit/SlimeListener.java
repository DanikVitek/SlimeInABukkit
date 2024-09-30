package com.danikvitek.slimeinabukkit;

import com.danikvitek.slimeinabukkit.config.PluginConfig;
import com.danikvitek.slimeinabukkit.persistence.PersistentContainerAccessor;
import com.danikvitek.slimeinabukkit.util.ISUtil;
import io.vavr.collection.Iterator;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.danikvitek.slimeinabukkit.SlimeInABukkitPlugin.SLIME_BUCKET_MATERIAL;

public class SlimeListener implements Listener {
    public static final String SLIME_INTERACT_PERMISSION = "slimeinabukkit.interact";
    public static final Random RANDOM = new Random();

    private static final Set<UUID> interactingPlayers = new HashSet<>();
    private static final Map<Item, Chunk> lastItemChunks = new ConcurrentHashMap<>();

    private final @NotNull PluginConfig config;
    private final @NotNull Consumer<String> debugLog;
    private final @NotNull Scheduler scheduler;
    private final @NotNull PersistentContainerAccessor persistentContainerAccessor;

    public SlimeListener(@NotNull PluginConfig config,
                         @NotNull Consumer<String> debugLog,
                         @NotNull Scheduler scheduler,
                         @NotNull PersistentContainerAccessor persistentContainerAccessor) {
        this.config = config;
        this.debugLog = debugLog;
        this.scheduler = scheduler;
        this.persistentContainerAccessor = persistentContainerAccessor;
    }

    @EventHandler
    public void onPlayerMove(final @NotNull PlayerMoveEvent event) {
        final var fromChunk = event.getFrom().getChunk();
        final var toChunk = event.getTo().getChunk();
        if (Objects.equals(fromChunk, toChunk)) return;
        debugLog.accept("PlayerMoveEvent was caught; Changing chunks");

        updateSlimes(event.getPlayer().getInventory(), toChunk.isSlimeChunk());
    }

    private void updateSlimes(final @NotNull PlayerInventory inventory, final boolean changeToActive) {
        debugLog.accept("updateSlimes: changeToActive = " + changeToActive);
        for (final var itemStack : inventory) updateSlime(itemStack, changeToActive);
    }

    private void updateSlime(final @Nullable ItemStack itemStack,
                             final boolean changeToActive) {
        if (itemStack == null || itemStack.getType() != SLIME_BUCKET_MATERIAL ||
            !itemStack.hasItemMeta() || !Objects.requireNonNull(itemStack.getItemMeta()).hasCustomModelData())
            return;

        final var itemMeta = itemStack.getItemMeta();
        debugLog.accept("updateSlimes: CMD = " + itemMeta.getCustomModelData());

        if (changeToActive && itemMeta.getCustomModelData() == config.getCalmSlimeCmd())
            itemMeta.setCustomModelData(config.getActiveSlimeCmd());
        else if (!changeToActive && itemMeta.getCustomModelData() == config.getActiveSlimeCmd())
            itemMeta.setCustomModelData(config.getCalmSlimeCmd());

        debugLog.accept("updateSlimes: new CMD = " + itemMeta.getCustomModelData());
        itemStack.setItemMeta(itemMeta);
    }

    @EventHandler
    public void onClickAtSlime(final @NotNull PlayerInteractEntityEvent event) {
        debugLog.accept("PlayerInteractEntityEvent was caught");

        final var player = event.getPlayer();
        if (checkCannotPickupSlime(player)) return;

        if (!(event.getRightClicked() instanceof Slime slime) || event.getRightClicked() instanceof MagmaCube) return;
        debugLog.accept("PlayerInteractEntityEvent: clicked at slime");

        if (slime.getSize() != 1) return;

        final var inventory = player.getInventory();

        final boolean isMainHand = event.getHand() == EquipmentSlot.HAND;
        if (!isMainHand && inventory.getItemInMainHand().getType() != Material.AIR)
            return;
        debugLog.accept("PlayerInteractEvent: Hand = " + event.getHand());

        final var itemStack = isMainHand
            ? inventory.getItemInMainHand()
            : inventory.getItemInOffHand();
        if (itemStack.getType() != Material.BUCKET) return;

        final var itemMeta = itemStack.hasItemMeta()
            ? itemStack.getItemMeta()
            : new ItemStack(SLIME_BUCKET_MATERIAL).getItemMeta();
        assert itemMeta != null;
        if (itemMeta.hasCustomModelData()) return;

        pickupSlime(slime, player, itemStack, itemMeta, event.getHand());
    }

    private void pickupSlime(final @NotNull Slime slime,
                             final @NotNull Player player,
                             final @NotNull ItemStack bucketStack,
                             final @NotNull ItemMeta slimeBucketMeta,
                             final @NotNull EquipmentSlot bucketStackSlot) {
        if (interactingPlayers.contains(player.getUniqueId())) return;
        interactingPlayers.add(player.getUniqueId());

        slime.remove();
        final var slimeBucketStack = bucketStack.clone();
        slimeBucketStack.setAmount(1);
        slimeBucketMeta.setCustomModelData(
            player.getLocation().getChunk().isSlimeChunk()
                ? config.getActiveSlimeCmd()
                : config.getCalmSlimeCmd()
        );
        if (slime.customName() != null) slimeBucketMeta.displayName(slime.customName());
        else slimeBucketMeta.displayName(
            slimeBucketMeta.hasDisplayName()
                ? slimeBucketMeta.displayName()
                : config.getSlimeBucketTitle()
        );

        slimeBucketStack.setItemMeta(slimeBucketMeta);
        slimeBucketStack.setType(SLIME_BUCKET_MATERIAL);
        persistentContainerAccessor.setSlimeBucketUUID(slimeBucketStack, slime.getUniqueId());
        if (bucketStack.getAmount() > 1) {
            bucketStack.setAmount(bucketStack.getAmount() - 1);
            final var notFittedItems = player.getInventory().addItem(slimeBucketStack);
            notFittedItems.forEach((index, notFittedSlimeBucket) -> {
                final var droppedItem = player.getWorld().dropItem(player.getEyeLocation(), notFittedSlimeBucket);
                droppedItem.setPickupDelay(40);
                droppedItem.setVelocity(player.getLocation().getDirection().clone().multiply(0.2));
            });
        } else player.getInventory().setItem(bucketStackSlot, slimeBucketStack);

        // play hand swing animation
        if (bucketStackSlot == EquipmentSlot.HAND) player.swingMainHand();
        else player.swingOffHand();

        scheduler.runTask(() -> interactingPlayers.remove(player.getUniqueId()));
    }

    @EventHandler
    public void onClickAtBlock(final @NotNull PlayerInteractEvent event) {
        debugLog.accept("PlayerInteractEvent was caught");

        final Player player = event.getPlayer();
        if (checkCannotPickupSlime(player)) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        debugLog.accept("PlayerInteractEvent: Action = " + event.getAction());

        if (event.getHand() == EquipmentSlot.OFF_HAND &&
            player.getInventory().getItemInMainHand().getType() != Material.AIR) return;
        debugLog.accept("PlayerInteractEvent: Hand = " + event.getHand());

        final ItemStack itemStack = event.getItem();
        if (itemStack == null || itemStack.getType() != SLIME_BUCKET_MATERIAL || !itemStack.hasItemMeta()) return;

        final ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        if (!itemMeta.hasCustomModelData() ||
            (itemMeta.getCustomModelData() != config.getCalmSlimeCmd() &&
             itemMeta.getCustomModelData() != config.getActiveSlimeCmd())) return;

        placeSlime(event, player, itemStack);
    }

    private boolean checkCannotPickupSlime(@NotNull Player player) {
        if (!player.hasPermission(SLIME_INTERACT_PERMISSION)) {
            debugLog.accept("no permission to interact with slime");
            return true;
        }
        if (!config.canPickupSlime()) {
            debugLog.accept("can-pickup-slime = false");
            return true;
        }
        debugLog.accept("can-pickup-slime = true");
        return false;
    }

    private void placeSlime(final @NotNull PlayerInteractEvent event,
                            final @NotNull Player player,
                            final @NotNull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) throw new AssertionError("ItemStack has no ItemMeta");
        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (interactingPlayers.contains(player.getUniqueId())) return;
        interactingPlayers.add(player.getUniqueId());

        event.setUseInteractedBlock(Event.Result.DENY);

        final Block block = event.getClickedBlock();
        assert block != null; // because the event.action is RIGHT_CLICK_BLOCK
        final BlockFace blockFace = event.getBlockFace();

        final Location slimeReleaseLocation = block.getLocation()
                                                   .add(0.5, 0d, 0.5)
                                                   .add(blockFace.getDirection());
        slimeReleaseLocation.setYaw(RANDOM.nextFloat() * 360f);

        player.getWorld().spawn(slimeReleaseLocation, Slime.class, slime -> {
            slime.setSize(1);
            final var serializer = PlainTextComponentSerializer.plainText();

            ISUtil.useDisplayName(itemMeta, displayName -> {
                if (!serializer.serialize(displayName)
                               .equals(serializer.serialize(config.getSlimeBucketTitle()))) {
                    slime.customName(itemMeta.displayName());
                }
            });
        });

        itemMeta.setCustomModelData(null);
        itemMeta.displayName(null);
        persistentContainerAccessor.removeSlimeBucketUUID(itemMeta);
        itemStack.setItemMeta(itemMeta);
        itemStack.setType(Material.BUCKET);

        if (event.getHand() == EquipmentSlot.HAND) player.swingMainHand();
        else player.swingOffHand();

        scheduler.runTask(() -> interactingPlayers.remove(player.getUniqueId()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraftWithSlimeBucket(final @NotNull CraftItemEvent e) {
        final int matrixSize = e.getInventory().getMatrix().length;
        final Int2ObjectMap<@NotNull ItemStack> slotsAndStacksToReplaceWithSlimeBucket =
            new Int2ObjectLinkedOpenHashMap<>(matrixSize);
        Iterator.of(e.getInventory().getMatrix())
                .zipWithIndex()
                .filter(pair -> {
                    final ItemStack itemStack = pair._1;
                    if (itemStack == null || itemStack.getType() != SLIME_BUCKET_MATERIAL || !itemStack.hasItemMeta()) {
                        return false;
                    }
                    final ItemMeta itemMeta = itemStack.getItemMeta();
                    assert itemMeta != null;
                    return itemMeta.hasCustomModelData() &&
                           (itemMeta.getCustomModelData() == config.getCalmSlimeCmd() ||
                            itemMeta.getCustomModelData() == config.getActiveSlimeCmd());
                })
                .forEach(pair -> {
                    final ItemStack itemStack = pair._1;
                    assert itemStack != null;
                    final int slot = pair._2;
                    slotsAndStacksToReplaceWithSlimeBucket.put(slot, itemStack.clone());
                });

        scheduler.runTaskLater(() -> {
            final ItemStack[] newMatrix = new ItemStack[matrixSize];
            slotsAndStacksToReplaceWithSlimeBucket.forEach((slot, clonedBucket) -> {
                clonedBucket.setType(Material.BUCKET);
                final ItemMeta clonedBucketMeta = clonedBucket.getItemMeta();
                assert clonedBucketMeta != null;
                clonedBucketMeta.setCustomModelData(null);
                clonedBucketMeta.displayName(null);
                persistentContainerAccessor.removeSlimeBucketUUID(clonedBucketMeta);
                clonedBucket.setItemMeta(clonedBucketMeta);
                newMatrix[slot] = clonedBucket;
            });

            Iterator.of(e.getInventory().getMatrix())
                    .zipWithIndex()
                    .filter(pair -> newMatrix[pair._2] == null)
                    .forEach(pair -> newMatrix[pair._2] = pair._1);
            e.getInventory().setMatrix(newMatrix);
        }, 0L);
    }

    @EventHandler
    public void onSlimeBucketDrop(final @NotNull PlayerDropItemEvent event) {
        final Item itemDrop = event.getItemDrop();
        final ItemStack itemStack = itemDrop.getItemStack();

        if (itemStack.getType() != SLIME_BUCKET_MATERIAL || !itemStack.hasItemMeta()) return;
        final ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;

        if (!itemMeta.hasCustomModelData()) return;
        final int cmd = itemMeta.getCustomModelData();

        if (cmd != config.getCalmSlimeCmd() && cmd != config.getActiveSlimeCmd()) return;

        lastItemChunks.put(itemDrop, itemDrop.getLocation().getChunk());

        scheduler.runTaskTimerAsynchronously(task -> {
            if (!itemDrop.isValid()) {
                lastItemChunks.remove(itemDrop);
                task.cancel();
            }

            final Chunk currentChunk = itemDrop.getLocation().getChunk();
            if (!Objects.equals(currentChunk, lastItemChunks.get(itemDrop))) {
                lastItemChunks.put(itemDrop, currentChunk);
                updateSlime(itemStack, currentChunk.isSlimeChunk());
                itemDrop.setItemStack(itemStack);
            }
        }, 0L, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        interactingPlayers.remove(event.getPlayer().getUniqueId());
    }
}
