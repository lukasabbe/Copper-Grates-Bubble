package me.lukasabbe.coppergratesbubblethru.tags;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Arrays;
import java.util.List;

public class ModBlockTags {
    private final static List<Block> copperGrates = Arrays.asList(Blocks.COPPER_GRATE, Blocks.EXPOSED_COPPER_GRATE, Blocks.WAXED_COPPER_GRATE, Blocks.OXIDIZED_COPPER_GRATE, Blocks.WEATHERED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER_GRATE);
    public static boolean isAWaterLoggedCopperGrates(BlockState state){
        return copperGrates.stream().anyMatch(t -> t.defaultBlockState().is(state.getBlock())) && state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED);
    }
}
