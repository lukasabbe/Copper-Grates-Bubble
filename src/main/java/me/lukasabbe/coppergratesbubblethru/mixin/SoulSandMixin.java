package me.lukasabbe.coppergratesbubblethru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.lukasabbe.coppergratesbubblethru.tags.ModBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoulSandBlock.class)
public class SoulSandMixin extends Block {
    public SoulSandMixin(Settings settings) {
        super(settings);
    }

    @ModifyExpressionValue(method = "getStateForNeighborUpdate", at= @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    public boolean isOf(boolean original, @Local(ordinal = 1, argsOnly = true) BlockState neighborState){
        return original || (ModBlockTags.isACopperGrates(neighborState) && neighborState.contains(Properties.WATERLOGGED));
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
