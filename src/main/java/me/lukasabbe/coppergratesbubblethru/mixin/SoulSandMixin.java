package me.lukasabbe.coppergratesbubblethru.mixin;

import me.lukasabbe.coppergratesbubblethru.util.GrateBubbleScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoulSandBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SoulSandBlock.class)
public class SoulSandMixin extends Block {
    public SoulSandMixin(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        GrateBubbleScheduler.scheduleColumnUpdateAboveGrates(level, pos);
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
        GrateBubbleScheduler.scheduleColumnUpdateAboveGrates(world, pos);
        super.destroy(world, pos, state);
    }
}
