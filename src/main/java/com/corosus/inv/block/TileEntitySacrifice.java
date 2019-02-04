package com.corosus.inv.block;

import CoroUtil.difficulty.DifficultyInfoPlayer;
import com.corosus.inv.InvasionManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class TileEntitySacrifice extends TileEntity implements ITickable
{

    private IInventory inventory;

    private DifficultyInfoPlayer difficultyInfoPlayer;

    public TileEntitySacrifice() {
        inventory = new InventoryBasic("Sacrifice Inventory", true, 9);
        difficultyInfoPlayer = new DifficultyInfoPlayer();
    }

	@Override
    public void update()
    {
    	
    	if (!world.isRemote) {
    		

    	}
    }

    public NBTTagCompound writeToNBT(NBTTagCompound var1)
    {
        return super.writeToNBT(var1);
    }

    public void readFromNBT(NBTTagCompound var1)
    {
        super.readFromNBT(var1);

    }

    public void onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (InvasionManager.skipNextInvasionForPlayer(playerIn)) {
            playerIn.sendMessage(new TextComponentString("Sacrifice received!"));
            if (!playerIn.isCreative()) {
                playerIn.attackEntityFrom(DamageSource.MAGIC, 12);
            }
        }

	}

    public IInventory getInventory() {
        return inventory;
    }

    public void setInventory(IInventory inventory) {
        this.inventory = inventory;
    }

    public DifficultyInfoPlayer getDifficultyInfoPlayer() {
        return difficultyInfoPlayer;
    }

    public void setDifficultyInfoPlayer(DifficultyInfoPlayer difficultyInfoPlayer) {
        this.difficultyInfoPlayer = difficultyInfoPlayer;
    }
}
