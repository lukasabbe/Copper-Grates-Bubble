package me.lukasabbe.coppergratesbubblethru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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

    @Unique
    private static BlockState getState(WorldAccess worldAccess, BlockPos pos, BlockState bubbleSource){
        if(ModBlockTags.isAWaterLoggedCopperGrates(bubbleSource)){
            return getState(worldAccess,pos.down(),worldAccess.getBlockState(pos.down()));
        }
        else{
            return getBubbleState(bubbleSource);
        }
    }

    @Inject(method = "update(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V", at=@At("HEAD"), cancellable = true)
    private static void newUpdate(WorldAccess world, BlockPos pos, BlockState water, BlockState bubbleSource, CallbackInfo ci){
        if(isStillWater(water) || ModBlockTags.isAWaterLoggedCopperGrates(bubbleSource)){
            BlockState blockState = getState(world,pos,bubbleSource);
            BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.UP);
            if(ModBlockTags.isAWaterLoggedCopperGrates(blockState)){
                mutable.move(Direction.UP);
                world.setBlockState(mutable, blockState,2);
            }else {
                world.setBlockState(pos, blockState, 2);
            }

            while (true){
                final BlockState waterState = world.getBlockState(mutable);
                if (!(isStillWater(waterState) || ModBlockTags.isAWaterLoggedCopperGrates(waterState))) break;
                if(!ModBlockTags.isAWaterLoggedCopperGrates(waterState)){
                    if (!world.setBlockState(mutable, blockState, 2)) {
                        return;
                    }
                    mutable.move(Direction.UP);
                }else {
                    mutable.move(Direction.UP);
                    mutable.move(Direction.UP);
                }

            }
        }
        ci.cancel();
    }
    
}
