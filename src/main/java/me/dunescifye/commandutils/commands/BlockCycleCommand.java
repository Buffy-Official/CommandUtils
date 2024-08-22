package me.dunescifye.commandutils.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;

public class BlockCycleCommand extends Command implements Registerable {

    @SuppressWarnings("ConstantConditions")
    public void register() {
        if (!this.getEnabled()) return;

        LiteralArgument oxidizeArg = new LiteralArgument("oxidize");
        StringArgument worldArg = new StringArgument("World");
        LocationArgument locArg = new LocationArgument("Location", LocationType.BLOCK_POSITION);
        LiteralArgument waxArg = new LiteralArgument("wax");

        new CommandTree("blockcycle")
            .then(oxidizeArg
                .then(worldArg
                    .then(locArg
                        .executes((sender, args) -> {
                            Block b = Bukkit.getWorld(args.getByArgument(worldArg)).getBlockAt(args.getByArgument(locArg));
                            Material material = b.getType();
                            BlockData blockData = b.getBlockData();

                            if (blockData instanceof Stairs stairs) {
                                Stairs.Shape shape = stairs.getShape();
                                boolean waterlogged = stairs.isWaterlogged();
                                org.bukkit.block.BlockFace facing = stairs.getFacing();

                                switch (material) {
                                    case CUT_COPPER_STAIRS -> b.setType(Material.EXPOSED_CUT_COPPER_STAIRS);
                                    case EXPOSED_CUT_COPPER_STAIRS -> b.setType(Material.WEATHERED_CUT_COPPER_STAIRS);
                                    case WEATHERED_CUT_COPPER_STAIRS -> b.setType(Material.OXIDIZED_CUT_COPPER_STAIRS);
                                    case OXIDIZED_CUT_COPPER_STAIRS -> b.setType(Material.CUT_COPPER_STAIRS);
                                    case WAXED_CUT_COPPER_STAIRS -> b.setType(Material.WAXED_EXPOSED_CUT_COPPER_STAIRS);
                                    case WAXED_EXPOSED_CUT_COPPER_STAIRS -> b.setType(Material.WAXED_WEATHERED_CUT_COPPER_STAIRS);
                                    case WAXED_WEATHERED_CUT_COPPER_STAIRS -> b.setType(Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
                                    case WAXED_OXIDIZED_CUT_COPPER_STAIRS -> b.setType(Material.WAXED_CUT_COPPER_STAIRS);
                                }

                                // Get the new block data
                                BlockData newBlockData = b.getBlockData();

                                // Set the facing direction and other properties
                                if (newBlockData instanceof Stairs newStairs) {
                                    newStairs.setFacing(facing);
                                    newStairs.setShape(shape);
                                    newStairs.setWaterlogged(waterlogged);
                                    b.setBlockData(newStairs);
                                }
                            } else if (blockData instanceof Slab slab) {
                                Slab.Type type = slab.getType();
                                boolean waterlogged = slab.isWaterlogged();

                                switch (material) {
                                    case CUT_COPPER_SLAB -> b.setType(Material.EXPOSED_CUT_COPPER_SLAB);
                                    case EXPOSED_CUT_COPPER_SLAB -> b.setType(Material.WEATHERED_CUT_COPPER_SLAB);
                                    case WEATHERED_CUT_COPPER_SLAB -> b.setType(Material.OXIDIZED_CUT_COPPER_SLAB);
                                    case OXIDIZED_CUT_COPPER_SLAB -> b.setType(Material.CUT_COPPER_SLAB);
                                    case WAXED_CUT_COPPER_SLAB -> b.setType(Material.WAXED_EXPOSED_CUT_COPPER_SLAB);
                                    case WAXED_EXPOSED_CUT_COPPER_SLAB ->
                                        b.setType(Material.WAXED_WEATHERED_CUT_COPPER_SLAB);
                                    case WAXED_WEATHERED_CUT_COPPER_SLAB ->
                                        b.setType(Material.WAXED_OXIDIZED_CUT_COPPER_SLAB);
                                    case WAXED_OXIDIZED_CUT_COPPER_SLAB -> b.setType(Material.WAXED_CUT_COPPER_SLAB);
                                }

                                // Get the new block data
                                BlockData newBlockData = b.getBlockData();

                                // Set the slab type and waterlogged state
                                if (newBlockData instanceof Slab newSlab) {
                                    newSlab.setType(type);
                                    newSlab.setWaterlogged(waterlogged);
                                    b.setBlockData(newSlab);
                                }
                            } else {
                                switch (material) {
                                    case COPPER_BLOCK -> b.setType(Material.EXPOSED_COPPER);
                                    case EXPOSED_COPPER -> b.setType(Material.WEATHERED_COPPER);
                                    case WEATHERED_COPPER -> b.setType(Material.OXIDIZED_COPPER);
                                    case OXIDIZED_COPPER -> b.setType(Material.COPPER_BLOCK);
                                    case CUT_COPPER -> b.setType(Material.EXPOSED_CUT_COPPER);
                                    case EXPOSED_CUT_COPPER -> b.setType(Material.WEATHERED_CUT_COPPER);
                                    case WEATHERED_CUT_COPPER -> b.setType(Material.OXIDIZED_CUT_COPPER);
                                    case OXIDIZED_CUT_COPPER -> b.setType(Material.CUT_COPPER);
                                    case WAXED_COPPER_BLOCK -> b.setType(Material.WAXED_EXPOSED_COPPER);
                                    case WAXED_EXPOSED_COPPER -> b.setType(Material.WAXED_WEATHERED_COPPER);
                                    case WAXED_WEATHERED_COPPER -> b.setType(Material.WAXED_OXIDIZED_COPPER);
                                    case WAXED_OXIDIZED_COPPER -> b.setType(Material.WAXED_COPPER_BLOCK);
                                    case WAXED_CUT_COPPER -> b.setType(Material.WAXED_EXPOSED_CUT_COPPER);
                                    case WAXED_EXPOSED_CUT_COPPER -> b.setType(Material.WAXED_WEATHERED_CUT_COPPER);
                                    case WAXED_WEATHERED_CUT_COPPER -> b.setType(Material.WAXED_OXIDIZED_CUT_COPPER);
                                    case WAXED_OXIDIZED_CUT_COPPER -> b.setType(Material.WAXED_CUT_COPPER);
                                }
                            }

                        })
                    )
                )
                .then(locArg
                    .executes((sender, args) -> {
                        Block b = args.getByArgument(locArg).getBlock();
                        Material material = b.getType();
                        BlockData blockData = b.getBlockData();

                        if (blockData instanceof Stairs stairs) {
                            Stairs.Shape shape = stairs.getShape();
                            boolean waterlogged = stairs.isWaterlogged();
                            org.bukkit.block.BlockFace facing = stairs.getFacing();

                            switch (material) {
                                case CUT_COPPER_STAIRS -> b.setType(Material.EXPOSED_CUT_COPPER_STAIRS);
                                case EXPOSED_CUT_COPPER_STAIRS -> b.setType(Material.WEATHERED_CUT_COPPER_STAIRS);
                                case WEATHERED_CUT_COPPER_STAIRS -> b.setType(Material.OXIDIZED_CUT_COPPER_STAIRS);
                                case OXIDIZED_CUT_COPPER_STAIRS -> b.setType(Material.CUT_COPPER_STAIRS);
                                case WAXED_CUT_COPPER_STAIRS -> b.setType(Material.WAXED_EXPOSED_CUT_COPPER_STAIRS);
                                case WAXED_EXPOSED_CUT_COPPER_STAIRS -> b.setType(Material.WAXED_WEATHERED_CUT_COPPER_STAIRS);
                                case WAXED_WEATHERED_CUT_COPPER_STAIRS -> b.setType(Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
                                case WAXED_OXIDIZED_CUT_COPPER_STAIRS -> b.setType(Material.WAXED_CUT_COPPER_STAIRS);
                            }

                            // Get the new block data
                            BlockData newBlockData = b.getBlockData();

                            // Set the facing direction and other properties
                            if (newBlockData instanceof Stairs newStairs) {
                                newStairs.setFacing(facing);
                                newStairs.setShape(shape);
                                newStairs.setWaterlogged(waterlogged);
                                b.setBlockData(newStairs);
                            }
                        } else if (blockData instanceof Slab slab) {
                            Slab.Type type = slab.getType();
                            boolean waterlogged = slab.isWaterlogged();

                            switch (material) {
                                case CUT_COPPER_SLAB -> b.setType(Material.EXPOSED_CUT_COPPER_SLAB);
                                case EXPOSED_CUT_COPPER_SLAB -> b.setType(Material.WEATHERED_CUT_COPPER_SLAB);
                                case WEATHERED_CUT_COPPER_SLAB -> b.setType(Material.OXIDIZED_CUT_COPPER_SLAB);
                                case OXIDIZED_CUT_COPPER_SLAB -> b.setType(Material.CUT_COPPER_SLAB);
                                case WAXED_CUT_COPPER_SLAB -> b.setType(Material.WAXED_EXPOSED_CUT_COPPER_SLAB);
                                case WAXED_EXPOSED_CUT_COPPER_SLAB ->
                                    b.setType(Material.WAXED_WEATHERED_CUT_COPPER_SLAB);
                                case WAXED_WEATHERED_CUT_COPPER_SLAB ->
                                    b.setType(Material.WAXED_OXIDIZED_CUT_COPPER_SLAB);
                                case WAXED_OXIDIZED_CUT_COPPER_SLAB -> b.setType(Material.WAXED_CUT_COPPER_SLAB);
                            }

                            // Get the new block data
                            BlockData newBlockData = b.getBlockData();

                            // Set the slab type and waterlogged state
                            if (newBlockData instanceof Slab newSlab) {
                                newSlab.setType(type);
                                newSlab.setWaterlogged(waterlogged);
                                b.setBlockData(newSlab);
                            }
                        } else {
                            switch (material) {
                                case COPPER_BLOCK -> b.setType(Material.EXPOSED_COPPER);
                                case EXPOSED_COPPER -> b.setType(Material.WEATHERED_COPPER);
                                case WEATHERED_COPPER -> b.setType(Material.OXIDIZED_COPPER);
                                case OXIDIZED_COPPER -> b.setType(Material.COPPER_BLOCK);
                                case CUT_COPPER -> b.setType(Material.EXPOSED_CUT_COPPER);
                                case EXPOSED_CUT_COPPER -> b.setType(Material.WEATHERED_CUT_COPPER);
                                case WEATHERED_CUT_COPPER -> b.setType(Material.OXIDIZED_CUT_COPPER);
                                case OXIDIZED_CUT_COPPER -> b.setType(Material.CUT_COPPER);
                                case WAXED_COPPER_BLOCK -> b.setType(Material.WAXED_EXPOSED_COPPER);
                                case WAXED_EXPOSED_COPPER -> b.setType(Material.WAXED_WEATHERED_COPPER);
                                case WAXED_WEATHERED_COPPER -> b.setType(Material.WAXED_OXIDIZED_COPPER);
                                case WAXED_OXIDIZED_COPPER -> b.setType(Material.WAXED_COPPER_BLOCK);
                                case WAXED_CUT_COPPER -> b.setType(Material.WAXED_EXPOSED_CUT_COPPER);
                                case WAXED_EXPOSED_CUT_COPPER -> b.setType(Material.WAXED_WEATHERED_CUT_COPPER);
                                case WAXED_WEATHERED_CUT_COPPER -> b.setType(Material.WAXED_OXIDIZED_CUT_COPPER);
                                case WAXED_OXIDIZED_CUT_COPPER -> b.setType(Material.WAXED_CUT_COPPER);
                            }
                        }

                    })
                )
            )
            .then(waxArg
                .then(worldArg
                    .then(locArg
                        .executes((sender, args) -> {
                            Block b = Bukkit.getWorld(args.getByArgument(worldArg)).getBlockAt(args.getByArgument(locArg));
                            BlockData blockData = b.getBlockData();
                            String material = b.getType().toString();
                            b.setType(Material.valueOf(material.startsWith("WAXED_") ? material.substring(6) : "WAXED_" + material));
                            b.setBlockData(blockData);
                        })
                    )
                )
                .then(locArg
                    .executes((sender, args) -> {
                        Block b = args.getByArgument(locArg).getBlock();
                        BlockData blockData = b.getBlockData();
                        String material = b.getType().toString();
                        b.setType(Material.valueOf(material.startsWith("WAXED_") ? material.substring(6) : "WAXED_" + material));
                        b.setBlockData(blockData);
                    })
                )
            )
            .withPermission(this.getPermission())
            .withAliases(this.getCommandAliases())
            .register(this.getNamespace());
    }
}
