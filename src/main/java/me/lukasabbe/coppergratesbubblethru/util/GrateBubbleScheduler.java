package me.lukasabbe.coppergratesbubblethru.util;

import me.lukasabbe.coppergratesbubblethru.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public final class GrateBubbleScheduler {
    private GrateBubbleScheduler() {
    }

    /**
     * Schedules a block tick on the first non-grate block above {@code sourcePos},
     * walking through any waterlogged copper grate stack. Used when a bubble source
     * (magma / soul sand) is placed, removed, or replaced under grates.
     */
    public static void scheduleColumnUpdateAboveGrates(LevelAccessor level, BlockPos sourcePos) {
        BlockPos.MutableBlockPos above = sourcePos.mutable().move(Direction.UP);
        if (!ModBlockTags.isAWaterLoggedCopperGrates(level.getBlockState(above))) {
            return;
        }
        while (ModBlockTags.isAWaterLoggedCopperGrates(level.getBlockState(above))) {
            above.move(Direction.UP);
        }
        BlockState targetState = level.getBlockState(above);
        level.scheduleTick(above, targetState.getBlock(), 0);
    }
}
