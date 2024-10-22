package me.lukasabbe.coppergratesbubblethru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.lukasabbe.coppergratesbubblethru.tags.ModBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.block.MagmaBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin extends Block {
    public MagmaBlockMixin(Settings settings) {
        super(settings);
    }
    @ModifyExpressionValue(method = "getStateForNeighborUpdate", at= @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    public boolean isOf(boolean original, @Local(ordinal = 1, argsOnly = true) BlockState neighborState){
        return original || (ModBlockTags.isACopperGrates(neighborState) && neighborState.contains(Properties.WATERLOGGED));
    }
    @Inject(method = "getStateForNeighborUpdate", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/ScheduledTickView;scheduleBlockTick(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V"))
    public void addMoreSchedules(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random, CallbackInfoReturnable<BlockState> cir){
        final BlockPos.Mutable movePos = pos.mutableCopy().move(Direction.UP).move(Direction.UP);
        tickView.scheduleBlockTick(movePos,world.getBlockState(movePos).getBlock(),20);
        BubbleColumnBlock.update((WorldAccess) world,movePos,world.getBlockState(pos));
        BubbleColumnBlock.update((WorldAccess) world,movePos,world.getBlockState(movePos));
    }
    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
        BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.UP);
        while (ModBlockTags.isACopperGrates(world.getBlockState(mutable))){
            mutable.move(Direction.UP);
            world.scheduleBlockTick(mutable,world.getBlockState(mutable).getBlock(),20);
        }
    }
}
