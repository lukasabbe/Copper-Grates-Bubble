package me.lukasabbe.coppergratesbubblethru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.lukasabbe.coppergratesbubblethru.tags.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BubbleColumnBlock.class)
public abstract class BubbleColumnBlockMixin {

    @Shadow
    private static boolean isStillWater(BlockState state) {
        return false;
    }

    @Shadow
    private static BlockState getBubbleState(BlockState state) {
        return null;
    }

    @Shadow
    public static void update(WorldAccess world, BlockPos pos, BlockState bubbleSource) {
    }

    @Unique
    private static BlockState getSource(BlockPos.Mutable pos, WorldAccess world){
        BlockState water = world.getBlockState(pos);
        boolean isWaterLoggedGrate = ModBlockTags.isAWaterLoggedCopperGrates(water);
        while (isWaterLoggedGrate){
            pos.move(Direction.DOWN);
            water = world.getBlockState(pos);
            isWaterLoggedGrate = ModBlockTags.isAWaterLoggedCopperGrates(water);
        }
        return getBubbleState(water);
    }

    @Inject(method = "update(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V", at=@At("HEAD"), cancellable = true)
    private static void newUpdate(WorldAccess world, BlockPos pos, BlockState water, BlockState bubbleSource, CallbackInfo ci){
        boolean isWaterLogged = ModBlockTags.isAWaterLoggedCopperGrates(water);
        if(isStillWater(water) || isWaterLogged){
            BlockState waterState;
            BlockPos.Mutable waterPos;
            if(isWaterLogged){
                waterState = getSource(pos.mutableCopy(), world);
                waterPos = pos.mutableCopy().move(Direction.UP);
                while (ModBlockTags.isAWaterLoggedCopperGrates(world.getBlockState(waterPos))){
                    waterPos.move(Direction.UP);
                }
                if(!isStillWater(world.getBlockState(waterPos))) ci.cancel();
                world.setBlockState(waterPos, waterState, 2);
                waterPos.move(Direction.UP);

            }else{
                waterState = getSource(pos.down().mutableCopy(),world);
                waterPos = pos.mutableCopy().move(Direction.UP);
                world.setBlockState(pos, waterState, 2);
            }
            while(isStillWater(world.getBlockState(waterPos))) {
                world.setBlockState(waterPos, waterState, 2);
                waterPos.move(Direction.UP);
            }
            if(ModBlockTags.isAWaterLoggedCopperGrates(world.getBlockState(waterPos))){
                update(world,waterPos.up(),world.getBlockState(waterPos));
            }

        }
        ci.cancel();
    }

    @ModifyExpressionValue(
            method = "canPlaceAt",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 0)
    )
    public boolean canPlaceAt(boolean original, @Local(ordinal = 1) BlockState blockState){
        return original || ModBlockTags.isAWaterLoggedCopperGrates(blockState);
    }
    
}
