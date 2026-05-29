package me.lukasabbe.coppergratesbubblethru.mixin;

import me.lukasabbe.coppergratesbubblethru.util.GrateBubbleScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin extends Block {
    public MagmaBlockMixin(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        GrateBubbleScheduler.scheduleColumnUpdateAboveGrates(level, pos);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, @NotNull BlockState state) {
        GrateBubbleScheduler.scheduleColumnUpdateAboveGrates(level, pos);
        super.destroy(level, pos, state);
    }
}
