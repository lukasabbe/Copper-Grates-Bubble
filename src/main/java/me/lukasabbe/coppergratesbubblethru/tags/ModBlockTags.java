package me.lukasabbe.coppergratesbubblethru.tags;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ModBlockTags {
    public static boolean isAWaterLoggedCopperGrates(BlockState state){
        return Blocks.COPPER_GRATE.asList().stream().anyMatch(t -> t.defaultBlockState().is(state.getBlock())) && state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED);
    }
}
