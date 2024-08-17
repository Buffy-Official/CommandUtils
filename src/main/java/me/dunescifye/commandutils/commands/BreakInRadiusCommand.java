package me.dunescifye.commandutils.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import me.dunescifye.commandutils.files.Config;
import me.dunescifye.commandutils.CommandUtils;
import me.dunescifye.commandutils.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;

import static me.dunescifye.commandutils.utils.Utils.mergeSimilarItemStacks;
import static org.bukkit.Material.AIR;

public class BreakInRadiusCommand extends Command implements Registerable {

    @SuppressWarnings("ConstantConditions")
    public void register() {
        /*
        List<Predicate<Block>> whitelist = new ArrayList<>();
        for (Tag<Material> tag : Bukkit.getTags("blocks", Material.class)) {
            whitelist.add(block -> tag.isTagged(block.getType()));
        }

         */
        if (!this.getEnabled()) return;

        StringArgument whitelistedBlocksArgument = new StringArgument("Whitelisted Blocks");
        StringArgument worldArg = new StringArgument("World");
        LocationArgument locArg = new LocationArgument("Location");
        IntegerArgument radiusArg = new IntegerArgument("Radius", 0);
        PlayerArgument playerArg = new PlayerArgument("Player");
        ItemStackArgument dropArg = new ItemStackArgument("Drop");


        Collection<String> materials = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        for (Tag<Material> tag : Bukkit.getTags("blocks", Material.class)) {
            tags.add(tag.getKey().asString());
            tags.add("!" + tag.getKey().asString());
        }

        for (Material mat : Material.values()) {
            String name = mat.name();
            materials.add(name);
            materials.add("!" + name);
        }
        materials.add("");
        tags.add("");



        //With Griefprevention
        if (CommandUtils.griefPreventionEnabled) {
            new CommandTree("breakinradius")
                .then(locArg
                    .then(worldArg
                        .then(playerArg
                            .then(radiusArg
                                .executes((sender, args) -> {
                                    World world = Bukkit.getWorld(args.getByArgument(worldArg));
                                    Location location = args.getByArgument(locArg);
                                    Block block = world.getBlockAt(location);
                                    int radius = args.getByArgument(radiusArg);
                                    Player player = args.getByArgument(playerArg);
                                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                                    Collection<ItemStack> drops = new ArrayList<>();

                                    for (int x = -radius; x <= radius; x++) {
                                        for (int y = -radius; y <= radius; y++) {
                                            for (int z = -radius; z <= radius; z++) {
                                                Block b = block.getRelative(x, y, z);
                                                //Testing claim
                                                Location relativeLocation = b.getLocation();
                                                if (Utils.isInsideClaim(player, relativeLocation) || Utils.isWilderness(relativeLocation)) {
                                                    drops.addAll(b.getDrops(heldItem));
                                                    b.setType(AIR);
                                                }
                                            }
                                        }
                                    }

                                    for (ItemStack item : mergeSimilarItemStacks(drops)){
                                        world.dropItemNaturally(location, item);
                                    }
                                })
                                .then(whitelistedBlocksArgument
                                    .replaceSuggestions(ArgumentSuggestions.strings(Config.getWhitelistKeySet()))
                                    .executes((sender, args) -> {
                                        World world = Bukkit.getWorld(args.getByArgument(worldArg));
                                        Location location = args.getByArgument(locArg);
                                        int radius = args.getByArgument(radiusArg);
                                        Player player = args.getByArgument(playerArg);
                                        Block origin = world.getBlockAt(location);
                                        ItemStack heldItem = player.getInventory().getItemInMainHand();
                                        Collection<ItemStack> drops = new ArrayList<>();
                                        String whitelistedBlocks = args.getByArgument(whitelistedBlocksArgument);
                                        List<Predicate<Block>> whitelist = Config.getWhitelist(whitelistedBlocks), blacklist = Config.getBlacklist(whitelistedBlocks);

                                        for (int x = -radius; x <= radius; x++){
                                            for (int y = -radius; y <= radius; y++){
                                                block: for (int z = -radius; z <= radius; z++){
                                                    Block relative = origin.getRelative(x, y, z);
                                                    for (Predicate<Block> predicateWhitelist : whitelist) {
                                                        if (predicateWhitelist.test(relative)) {
                                                            for (Predicate<Block> predicateBlacklist : blacklist) {
                                                                if (predicateBlacklist.test(relative)) {
                                                                    continue block;
                                                                }
                                                            }
                                                            //Testing claim
                                                            Location relativeLocation = relative.getLocation();
                                                            if (Utils.isInsideClaim(player, relativeLocation) || Utils.isWilderness(relativeLocation)) {
                                                                drops.addAll(relative.getDrops(heldItem));
                                                                relative.setType(Material.AIR);
                                                            }
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        for (ItemStack item : mergeSimilarItemStacks(drops)){
                                            world.dropItemNaturally(location, item);
                                        }
                                    })
                                    .then(dropArg
                                        .executes((sender, args) -> {
                                            World world = Bukkit.getWorld(args.getByArgument(worldArg));
                                            Location location = args.getByArgument(locArg);
                                            int radius = args.getByArgument(radiusArg);
                                            Player player = args.getByArgument(playerArg);
                                            Block origin = world.getBlockAt(location);
                                            ItemStack drop = args.getByArgument(dropArg);
                                            String whitelistedBlocks = args.getByArgument(whitelistedBlocksArgument);
                                            List<Predicate<Block>> whitelist = Config.getWhitelist(whitelistedBlocks), blacklist = Config.getBlacklist(whitelistedBlocks);

                                            for (int x = -radius; x <= radius; x++){
                                                for (int y = -radius; y <= radius; y++){
                                                    block: for (int z = -radius; z <= radius; z++){
                                                        Block relative = origin.getRelative(x, y, z);
                                                        for (Predicate<Block> predicateWhitelist : whitelist) {
                                                            if (predicateWhitelist.test(relative)) {
                                                                for (Predicate<Block> predicateBlacklist : blacklist) {
                                                                    if (predicateBlacklist.test(relative)) {
                                                                        continue block;
                                                                    }
                                                                }
                                                                //Testing claim
                                                                Location relativeLocation = relative.getLocation();
                                                                if (Utils.isInsideClaim(player, relativeLocation) || Utils.isWilderness(relativeLocation)) {
                                                                    drop.setAmount(drop.getAmount() + 1);
                                                                    relative.setType(Material.AIR);
                                                                }
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            drop.setAmount(drop.getAmount() - 1);
                                            world.dropItemNaturally(location, drop);

                                        })
                                    )
                                )
                            )
                        )
                    )
                )
                .withPermission(this.getPermission())
                .withAliases(this.getCommandAliases())
                .register(this.getNamespace());
        //GriefPrevention disabled
        } else {
            new CommandTree("breakinradius")
                .then(locArg
                    .then(worldArg
                        .then(playerArg
                            .then(radiusArg
                                .executes((sender, args) -> {
                                    World world = Bukkit.getWorld(args.getByArgument(worldArg));
                                    Location location = args.getByArgument(locArg);
                                    Block block = world.getBlockAt(location);
                                    int radius = args.getByArgument(radiusArg);
                                    Player player = args.getByArgument(playerArg);
                                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                                    Collection<ItemStack> drops = new ArrayList<>();

                                    for (int x = -radius; x <= radius; x++) {
                                        for (int y = -radius; y <= radius; y++) {
                                            for (int z = -radius; z <= radius; z++) {
                                                Block b = block.getRelative(x, y, z);
                                                drops.addAll(b.getDrops(heldItem));
                                                b.setType(AIR);
                                            }
                                        }
                                    }

                                    for (ItemStack item : mergeSimilarItemStacks(drops)){
                                        world.dropItemNaturally(location, item);
                                    }
                                })
                                .then(whitelistedBlocksArgument
                                    .replaceSuggestions(ArgumentSuggestions.strings(Config.getWhitelistKeySet()))
                                    .executes((sender, args) -> {
                                        World world = Bukkit.getWorld(args.getByArgument(worldArg));
                                        Location location = args.getByArgument(locArg);
                                        int radius = args.getByArgument(radiusArg);
                                        Player player = args.getByArgument(playerArg);
                                        Block origin = world.getBlockAt(location);
                                        ItemStack heldItem = player.getInventory().getItemInMainHand();
                                        Collection<ItemStack> drops = new ArrayList<>();
                                        String whitelistedBlocks = args.getByArgument(whitelistedBlocksArgument);
                                        List<Predicate<Block>> whitelist = Config.getWhitelist(whitelistedBlocks), blacklist = Config.getBlacklist(whitelistedBlocks);

                                        for (int x = -radius; x <= radius; x++){
                                            for (int y = -radius; y <= radius; y++){
                                                block: for (int z = -radius; z <= radius; z++){
                                                    Block relative = origin.getRelative(x, y, z);
                                                    for (Predicate<Block> predicateWhitelist : whitelist) {
                                                        if (predicateWhitelist.test(relative)) {
                                                            for (Predicate<Block> predicateBlacklist : blacklist) {
                                                                if (predicateBlacklist.test(relative)) {
                                                                    continue block;
                                                                }
                                                            }
                                                            drops.addAll(relative.getDrops(heldItem));
                                                            relative.setType(Material.AIR);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        for (ItemStack item : mergeSimilarItemStacks(drops)){
                                            world.dropItemNaturally(location, item);
                                        }
                                    })
                                    .then(new ItemStackArgument("Drop")
                                        .executes((sender, args) -> {
                                            World world = Bukkit.getWorld(args.getByArgument(worldArg));
                                            Location location = args.getByArgument(locArg);
                                            int radius = args.getByArgument(radiusArg);
                                            Block origin = world.getBlockAt(location);
                                            ItemStack drop = args.getByArgument(dropArg);
                                            String whitelistedBlocks = args.getByArgument(whitelistedBlocksArgument);
                                            List<Predicate<Block>> whitelist = Config.getWhitelist(whitelistedBlocks), blacklist = Config.getBlacklist(whitelistedBlocks);

                                            for (int x = -radius; x <= radius; x++){
                                                for (int y = -radius; y <= radius; y++){
                                                    block: for (int z = -radius; z <= radius; z++){
                                                        Block relative = origin.getRelative(x, y, z);
                                                        for (Predicate<Block> predicateWhitelist : whitelist) {
                                                            if (predicateWhitelist.test(relative)) {
                                                                for (Predicate<Block> predicateBlacklist : blacklist) {
                                                                    if (predicateBlacklist.test(relative)) {
                                                                        continue block;
                                                                    }
                                                                }
                                                                drop.setAmount(drop.getAmount() + 1);
                                                                relative.setType(Material.AIR);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            drop.setAmount(drop.getAmount() - 1);
                                            world.dropItemNaturally(location, drop);

                                        })
                                    )
                                )
                            )
                        )
                    )
                )
                .withPermission(this.getPermission())
                .withAliases(this.getCommandAliases())
                .register(this.getNamespace());
        }

        LiteralArgument whitelistedBlocksArg = new LiteralArgument("whitelistedblocks");
        //Command for defining whitelists per command
        if (CommandUtils.griefPreventionEnabled) {
            new CommandTree("breakinradius")
                .then(locArg
                    .then(worldArg
                        .then(playerArg
                            .then(radiusArg
                                .then(whitelistedBlocksArg
                                    .then(new ListArgumentBuilder<String>("Whitelisted Blocks")
                                        .withList(materials)
                                        .withStringMapper()
                                        .buildText()
                                        .executes((sender, args) -> {
                                            List<Predicate<Block>> whitelist = new ArrayList<>(), blacklist = new ArrayList<>();
                                            Utils.stringListToPredicate(args.getUnchecked("Whitelisted Blocks"), whitelist, blacklist);

                                            World world = Bukkit.getWorld(args.getByArgument(worldArg));
                                            Location location = args.getByArgument(locArg);
                                            Block origin = world.getBlockAt(location);
                                            Player player = args.getByArgument(playerArg);
                                            ItemStack heldItem = player.getInventory().getItemInMainHand();
                                            int radius = args.getByArgument(radiusArg);
                                            Collection<ItemStack> drops = new ArrayList<>();

                                            for (int x = -radius; x <= radius; x++){
                                                for (int y = -radius; y <= radius; y++){
                                                    block: for (int z = -radius; z <= radius; z++){
                                                        Block relative = origin.getRelative(x, y, z);
                                                        for (Predicate<Block> predicateWhitelist : whitelist) {
                                                            if (predicateWhitelist.test(relative)) {
                                                                for (Predicate<Block> predicateBlacklist : blacklist) {
                                                                    if (predicateBlacklist.test(relative)) {
                                                                        continue block;
                                                                    }
                                                                }
                                                                //Testing claim
                                                                Location relativeLocation = relative.getLocation();
                                                                if (Utils.isInsideClaim(player, relativeLocation) || Utils.isWilderness(relativeLocation)) {
                                                                    drops.addAll(relative.getDrops(heldItem));
                                                                    relative.setType(Material.AIR);
                                                                }
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            for (ItemStack item : mergeSimilarItemStacks(drops)){
                                                world.dropItemNaturally(location, item);
                                            }
                                        })
                                        .then(new ItemStackArgument("Drop")
                                            .executes((sender, args) -> {
                                                List<Predicate<Block>> whitelist = new ArrayList<>(), blacklist = new ArrayList<>();
                                                Utils.stringListToPredicate(args.getUnchecked("Whitelisted Blocks"), whitelist, blacklist);

                                                World world = Bukkit.getWorld(args.getByArgument(worldArg));
                                                Location location = args.getByArgument(locArg);
                                                Block origin = world.getBlockAt(location);
                                                ItemStack drop = args.getByArgument(dropArg);
                                                Player player = args.getByArgument(playerArg);
                                                int radius = args.getByArgument(radiusArg);

                                                for (int x = -radius; x <= radius; x++){
                                                    for (int y = -radius; y <= radius; y++){
                                                        block: for (int z = -radius; z <= radius; z++){
                                                            Block relative = origin.getRelative(x, y, z);
                                                            for (Predicate<Block> predicateWhitelist : whitelist) {
                                                                if (predicateWhitelist.test(relative)) {
                                                                    for (Predicate<Block> predicateBlacklist : blacklist) {
                                                                        if (predicateBlacklist.test(relative)) {
                                                                            continue block;
                                                                        }
                                                                    }
                                                                    //Testing claim
                                                                    Location relativeLocation = relative.getLocation();
                                                                    if (Utils.isInsideClaim(player, relativeLocation) || Utils.isWilderness(relativeLocation)) {
                                                                        drop.setAmount(drop.getAmount() + 1);
                                                                        relative.setType(AIR);
                                                                    }
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                drop.setAmount(drop.getAmount() - 1);
                                                world.dropItemNaturally(location, drop);

                                            })
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
                .withPermission(this.getPermission())
                .withAliases(this.getCommandAliases())
                .register(this.getNamespace());
        //GriefPrevention disabled
        } else {
            new CommandTree("breakinradius")
                .then(locArg
                    .then(worldArg
                        .then(playerArg
                            .then(radiusArg
                                .then(whitelistedBlocksArg
                                    .then(new ListArgumentBuilder<String>("Whitelisted Blocks")
                                        .withList(materials)
                                        .withStringMapper()
                                        .buildText()
                                        .executes((sender, args) -> {
                                            EnumSet<Material> whitelistMaterials = EnumSet.noneOf(Material.class);
                                            EnumSet<Material> blacklistMaterials = EnumSet.noneOf(Material.class);

                                            List<String> inputList = args.getUnchecked("Whitelisted Blocks");

                                            for (String input : inputList) {
                                                if (input.startsWith("!")) {
                                                    input = input.substring(1);
                                                    Material material = Material.valueOf(input.toUpperCase());
                                                    blacklistMaterials.add(material);
                                                } else {
                                                    Material material = Material.valueOf(input.toUpperCase());
                                                    whitelistMaterials.add(material);
                                                }
                                            }

                                            World world = Bukkit.getWorld((String) args.get("World"));
                                            Location location = (Location) args.get("Location");
                                            Block origin = world.getBlockAt(location);
                                            Player player = (Player) args.get("Player");
                                            ItemStack heldItem = player.getInventory().getItemInMainHand();
                                            int radius = (int) args.getOrDefault("Radius", 0);
                                            Collection<ItemStack> drops = new ArrayList<>();

                                            //List<Predicate<Block>> whitelist = new ArrayList<>();

                                        /*
                                        //for (Tag<Material> tag : Bukkit.getTags("blocks", Material.class)) {
                                        for (String input : inputTags) {
                                            Tag<Material> tag = Bukkit.getServer().getTag("blocks", NamespacedKey.fromString(input), Material.class);
                                            whitelist.add(block -> tag.isTagged(block.getType()));
                                        }

                                         */

                                            if (whitelistMaterials.isEmpty()){
                                                for (int x = -radius; x <= radius; x++){
                                                    for (int y = -radius; y <= radius; y++){
                                                        for (int z = -radius; z <= radius; z++){
                                                            Block b = origin.getRelative(x, y, z);
                                                            Material blockType = b.getType();
                                                        /*
                                                        for (Predicate<Block> blockPredicate : whitelist) {
                                                            if (blockPredicate.test(b)) {
                                                                drops.addAll(b.getDrops(heldItem));
                                                                b.setType(AIR);
                                                                break;
                                                            }
                                                        }
                                                         */
                                                            if (!blacklistMaterials.contains(blockType)) {
                                                                //Testing claim
                                                                Location relativeLocation = b.getLocation();
                                                                if (Utils.isInsideClaim(player, relativeLocation) || Utils.isWilderness(relativeLocation)) {
                                                                    drops.addAll(b.getDrops(heldItem));
                                                                    b.setType(Material.AIR);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                for (int x = -radius; x <= radius; x++){
                                                    for (int y = -radius; y <= radius; y++){
                                                        for (int z = -radius; z <= radius; z++){
                                                            Block b = origin.getRelative(x, y, z);
                                                            Material blockType = b.getType();
                                                            if (whitelistMaterials.contains(blockType) && !blacklistMaterials.contains(blockType)){
                                                                drops.addAll(b.getDrops(heldItem));
                                                                b.setType(AIR);
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            for (ItemStack item : mergeSimilarItemStacks(drops)){
                                                world.dropItemNaturally(location, item);
                                            }
                                        })
                                        .then(new LiteralArgument("whitelistedtags")
                                            .then(new ListArgumentBuilder<String>("Whitelisted Tags")
                                                .withList(tags)
                                                .withStringMapper()
                                                .buildText()
                                                .executes((sender, args) -> {

                                                    Set<Tag<Material>> whitelistTags = new HashSet<>();
                                                    Set<Tag<Material>> blacklistTags = new HashSet<>();
                                                    EnumSet<Material> whitelistMaterials = EnumSet.noneOf(Material.class);
                                                    EnumSet<Material> blacklistMaterials = EnumSet.noneOf(Material.class);

                                                    List<String> inputTags = args.getUnchecked("Whitelisted Tags");
                                                    List<String> inputMaterials = args.getUnchecked("Whitelisted Blocks");

                                                    for (String input : inputTags) {
                                                        if (input.startsWith("!")) {
                                                            input = input.substring(1);
                                                            Tag<Material> tag = Bukkit.getServer().getTag("blocks", NamespacedKey.fromString(input), Material.class);
                                                            blacklistTags.add(tag);
                                                        } else {
                                                            Tag<Material> tag = Bukkit.getServer().getTag("blocks", NamespacedKey.fromString(input), Material.class);
                                                            whitelistTags.add(tag);
                                                        }
                                                    }


                                                    for (String input : inputMaterials) {
                                                        if (input.startsWith("!")) {
                                                            input = input.substring(1);
                                                            Material material = Material.valueOf(input.toUpperCase());
                                                            blacklistMaterials.add(material);
                                                        } else {
                                                            Material material = Material.valueOf(input.toUpperCase());
                                                            whitelistMaterials.add(material);
                                                        }
                                                    }

                                                    World world = Bukkit.getWorld((String) args.get("World"));
                                                    Location location = (Location) args.get("Location");
                                                    Block block = world.getBlockAt(location);
                                                    Player player = (Player) args.get("Player");
                                                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                                                    int radius = (int) args.getOrDefault("Radius", 0);
                                                    Collection<ItemStack> drops = new ArrayList<>();

                                                    if (whitelistTags.isEmpty()){
                                                        if (whitelistMaterials.isEmpty()){
                                                            for (int x = -radius; x <= radius; x++){
                                                                for (int y = -radius; y <= radius; y++){
                                                                    for (int z = -radius; z <= radius; z++){
                                                                        Block b = block.getRelative(x, y, z);
                                                                        Material blockType = b.getType();
                                                                        if (blacklistTags.stream().noneMatch(tag -> tag.isTagged(blockType)) && !blacklistMaterials.contains(blockType)){
                                                                            drops.addAll(b.getDrops(heldItem));
                                                                            b.setType(AIR);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            for (int x = -radius; x <= radius; x++){
                                                                for (int y = -radius; y <= radius; y++){
                                                                    for (int z = -radius; z <= radius; z++){
                                                                        Block b = block.getRelative(x, y, z);
                                                                        Material blockType = b.getType();
                                                                        if (blacklistTags.stream().noneMatch(tag -> tag.isTagged(blockType)) && whitelistMaterials.contains(blockType) && !blacklistMaterials.contains(blockType)){
                                                                            drops.addAll(b.getDrops(heldItem));
                                                                            b.setType(AIR);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {

                                                        if (whitelistMaterials.isEmpty()){
                                                            for (int x = -radius; x <= radius; x++){
                                                                for (int y = -radius; y <= radius; y++){
                                                                    for (int z = -radius; z <= radius; z++){
                                                                        Block b = block.getRelative(x, y, z);
                                                                        Material blockType = b.getType();
                                                                        if (whitelistTags.stream().anyMatch(tag -> tag.isTagged(blockType)) && blacklistTags.stream().noneMatch(tag -> tag.isTagged(blockType)) && !blacklistMaterials.contains(blockType)){
                                                                            drops.addAll(b.getDrops(heldItem));
                                                                            b.setType(AIR);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            for (int x = -radius; x <= radius; x++){
                                                                for (int y = -radius; y <= radius; y++){
                                                                    for (int z = -radius; z <= radius; z++){
                                                                        Block b = block.getRelative(x, y, z);
                                                                        Material blockType = b.getType();
                                                                        if ((whitelistTags.stream().anyMatch(tag -> tag.isTagged(blockType)) || whitelistMaterials.contains(blockType)) && blacklistTags.stream().noneMatch(tag -> tag.isTagged(blockType)) && !blacklistMaterials.contains(blockType)){
                                                                            drops.addAll(b.getDrops(heldItem));
                                                                            b.setType(AIR);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    for (ItemStack item : mergeSimilarItemStacks(drops)){
                                                        world.dropItemNaturally(location, item);
                                                    }
                                                })
                                                .then(new ItemStackArgument("Drop")
                                                    .executes((sender, args) -> {

                                                        Set<Tag<Material>> whitelistTags = new HashSet<>();
                                                        Set<Tag<Material>> blacklistTags = new HashSet<>();
                                                        EnumSet<Material> whitelistMaterials = EnumSet.noneOf(Material.class);
                                                        EnumSet<Material> blacklistMaterials = EnumSet.noneOf(Material.class);

                                                        List<String> inputTags = args.getUnchecked("Whitelisted Tags");
                                                        List<String> inputMaterials = args.getUnchecked("Whitelisted Blocks");

                                                        for (String input : inputTags) {
                                                            if (input.startsWith("!")) {
                                                                input = input.substring(1);
                                                                Tag<Material> tag = Bukkit.getServer().getTag("blocks", NamespacedKey.fromString(input), Material.class);
                                                                blacklistTags.add(tag);
                                                            } else {
                                                                Tag<Material> tag = Bukkit.getServer().getTag("blocks", NamespacedKey.fromString(input), Material.class);
                                                                whitelistTags.add(tag);
                                                            }
                                                        }


                                                        for (String input : inputMaterials) {
                                                            if (input.startsWith("!")) {
                                                                input = input.substring(1);
                                                                Material material = Material.valueOf(input.toUpperCase());
                                                                blacklistMaterials.add(material);
                                                            } else {
                                                                Material material = Material.valueOf(input.toUpperCase());
                                                                whitelistMaterials.add(material);
                                                            }
                                                        }

                                                        World world = Bukkit.getWorld((String) args.get("World"));
                                                        Location location = (Location) args.get("Location");
                                                        Block block = world.getBlockAt(location);
                                                        ItemStack drop = ((ItemStack) args.get("Drop"));

                                                        int radius = (int) args.getOrDefault("Radius", 0);

                                                        if (whitelistTags.isEmpty()){
                                                            if (whitelistMaterials.isEmpty()){
                                                                for (int x = -radius; x <= radius; x++){
                                                                    for (int y = -radius; y <= radius; y++){
                                                                        for (int z = -radius; z <= radius; z++){
                                                                            Block b = block.getRelative(x, y, z);
                                                                            Material blockType = b.getType();
                                                                            if (blacklistTags.stream().noneMatch(tag -> tag.isTagged(blockType)) && !blacklistMaterials.contains(blockType)){
                                                                                drop.setAmount(drop.getAmount() + 1);
                                                                                b.setType(AIR);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                for (int x = -radius; x <= radius; x++){
                                                                    for (int y = -radius; y <= radius; y++){
                                                                        for (int z = -radius; z <= radius; z++){
                                                                            Block b = block.getRelative(x, y, z);
                                                                            Material blockType = b.getType();
                                                                            if (blacklistTags.stream().noneMatch(tag -> tag.isTagged(blockType)) && whitelistMaterials.contains(blockType) && !blacklistMaterials.contains(blockType)){
                                                                                drop.setAmount(drop.getAmount() + 1);
                                                                                b.setType(AIR);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else {

                                                            if (whitelistMaterials.isEmpty()){
                                                                for (int x = -radius; x <= radius; x++){
                                                                    for (int y = -radius; y <= radius; y++){
                                                                        for (int z = -radius; z <= radius; z++){
                                                                            Block b = block.getRelative(x, y, z);
                                                                            Material blockType = b.getType();
                                                                            if (whitelistTags.stream().anyMatch(tag -> tag.isTagged(blockType)) && blacklistTags.stream().noneMatch(tag -> tag.isTagged(blockType)) && !blacklistMaterials.contains(blockType)){
                                                                                drop.setAmount(drop.getAmount() + 1);
                                                                                b.setType(AIR);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                for (int x = -radius; x <= radius; x++){
                                                                    for (int y = -radius; y <= radius; y++){
                                                                        for (int z = -radius; z <= radius; z++){
                                                                            Block b = block.getRelative(x, y, z);
                                                                            Material blockType = b.getType();
                                                                            if (whitelistTags.stream().anyMatch(tag -> tag.isTagged(blockType)) && blacklistTags.stream().noneMatch(tag -> tag.isTagged(blockType)) && whitelistMaterials.contains(blockType) && !blacklistMaterials.contains(blockType)){
                                                                                drop.setAmount(drop.getAmount() + 1);
                                                                                b.setType(AIR);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        drop.setAmount(drop.getAmount() - 1);
                                                        world.dropItemNaturally(location, drop);

                                                    })
                                                )
                                            )
                                        )
                                    )
                                )
                                .then(new LiteralArgument("whitelistedtags")
                                    .then(new ListArgumentBuilder<String>("Whitelisted Tags")
                                        .withList(tags)
                                        .withStringMapper()
                                        .buildText()
                                        .executes((sender, args) -> {

                                            Set<Tag<Material>> whitelistTags = new HashSet<>();
                                            Set<Tag<Material>> blacklistTags = new HashSet<>();

                                            List<String> inputTags = args.getUnchecked("Whitelisted Tags");

                                            for (String input : inputTags) {
                                                if (input.startsWith("!")) {
                                                    input = input.substring(1);
                                                    Tag<Material> tag = Bukkit.getServer().getTag("blocks", NamespacedKey.fromString(input), Material.class);
                                                    blacklistTags.add(tag);
                                                } else {
                                                    Tag<Material> tag = Bukkit.getServer().getTag("blocks", NamespacedKey.fromString(input), Material.class);
                                                    whitelistTags.add(tag);
                                                }
                                            }


                                            World world = Bukkit.getWorld((String) args.get("World"));
                                            Location location = (Location) args.get("Location");
                                            Block block = world.getBlockAt(location);
                                            Player player = (Player) args.get("Player");
                                            ItemStack heldItem = player.getInventory().getItemInMainHand();
                                            int radius = (int) args.getOrDefault("Radius", 0);
                                            Collection<ItemStack> drops = new ArrayList<>();

                                            if (whitelistTags.isEmpty()){
                                                for (int x = -radius; x <= radius; x++){
                                                    for (int y = -radius; y <= radius; y++){
                                                        for (int z = -radius; z <= radius; z++){
                                                            Block b = block.getRelative(x, y, z);
                                                            Material blockType = b.getType();
                                                            if (blacklistTags.stream().noneMatch(tag -> tag.isTagged(blockType))){
                                                                drops.addAll(b.getDrops(heldItem));
                                                                b.setType(AIR);
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                for (int x = -radius; x <= radius; x++){
                                                    for (int y = -radius; y <= radius; y++){
                                                        for (int z = -radius; z <= radius; z++){
                                                            Block b = block.getRelative(x, y, z);
                                                            Material blockType = b.getType();
                                                            if (whitelistTags.stream().anyMatch(tag -> tag.isTagged(blockType)) && blacklistTags.stream().noneMatch(tag -> tag.isTagged(blockType))){
                                                                drops.addAll(b.getDrops(heldItem));
                                                                b.setType(AIR);
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            for (ItemStack item : mergeSimilarItemStacks(drops)){
                                                world.dropItemNaturally(location, item);
                                            }
                                        })))
                            )
                        )
                    )
                )
                .withPermission(this.getPermission())
                .withAliases(this.getCommandAliases())
                .register(this.getNamespace());
        }
    }

}
