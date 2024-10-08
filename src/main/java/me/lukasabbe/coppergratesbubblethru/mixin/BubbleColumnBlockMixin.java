package me.lukasabbe.coppergratesbubblethru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.lukasabbe.coppergratesbubblethru.tags.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BubbleColumnBlock.class)
public class BubbleColumnBlockMixin {
    @ModifyExpressionValue(method = "isStillWater", at= @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",ordinal = 0))
    private static boolean isWaterOrGrates(boolean original, @Local(argsOnly = true) BlockState state){
        return original || (ModBlockTags.isACopperGrates(state) && state.contains(Properties.WATERLOGGED));
    }
    @WrapOperation(method = "update(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",ordinal = 0))
    private static boolean ifGrateSkip(WorldAccess instance, BlockPos blockPos, BlockState state, int i, Operation<Boolean> original){
        if(ModBlockTags.isACopperGrates(instance.getBlockState(blockPos)) && instance.getBlockState(blockPos).contains(Properties.WATERLOGGED)){
            return true;
        }else{
            return original.call(instance, blockPos, state, i);
        }
    }
    @WrapOperation(method = "update(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",ordinal = 1))
    private static boolean ifGrateSkipMutable(WorldAccess instance, BlockPos blockPos, BlockState state, int i, Operation<Boolean> original, @Local BlockPos.Mutable mutable){
        if(ModBlockTags.isACopperGrates(instance.getBlockState(mutable)) && instance.getBlockState(mutable).contains(Properties.WATERLOGGED)){
            return true;
        }else{
            return original.call(instance, blockPos, state, i);
        }
    }
    @ModifyExpressionValue(method = "canPlaceAt", at= @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",ordinal = 2))
    public boolean addGrates(boolean original, @Local(ordinal = 0, argsOnly = true) BlockState blockState){
        return original || (ModBlockTags.isACopperGrates(blockState) && blockState.contains(Properties.WATERLOGGED));
    }
    @WrapOperation(method = "update(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V", at= @At(value = "INVOKE", target = "Lnet/minecraft/block/BubbleColumnBlock;getBubbleState(Lnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockState;"))
    private static BlockState changeValueIfGrates(BlockState state, Operation<BlockState> original, @Local(argsOnly = true) WorldAccess worldAccess, @Local(argsOnly = true) BlockPos pos){
        final BlockState blockState = worldAccess.getBlockState(pos.down(2));
        if(ModBlockTags.isACopperGrates(state) && state.contains(Properties.WATERLOGGED)){
            return original.call(blockState);
        }else{
            return original.call(state);
        }
    }
}
