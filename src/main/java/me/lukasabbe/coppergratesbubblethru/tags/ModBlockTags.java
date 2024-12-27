package me.lukasabbe.coppergratesbubblethru.tags;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;

import java.util.Arrays;
import java.util.List;

public class ModBlockTags {
    private final static List<Block> copperGrates = Arrays.asList(Blocks.COPPER_GRATE, Blocks.EXPOSED_COPPER_GRATE, Blocks.WAXED_COPPER_GRATE, Blocks.OXIDIZED_COPPER_GRATE, Blocks.WEATHERED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER_GRATE);
    public static boolean isAWaterLoggedCopperGrates(BlockState state){
        return copperGrates.stream().anyMatch(t -> t.getDefaultState().isOf(state.getBlock())) && state.contains(Properties.WATERLOGGED);
    }
}
