package mekanism.common;

import java.util.ArrayList;
import java.util.Arrays;

import mekanism.api.IMechanicalPipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMechanicalPipe extends TileEntity implements IMechanicalPipe, ITankContainer, ITileNetwork
{
	public LiquidTank dummyTank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);
	
	public boolean isActive = false;
	
	@Override
	public boolean canTransferLiquids(TileEntity fromTile)
	{
		return worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) == 0;
	}
	
	@Override
	public void updateEntity()
	{
		if(!worldObj.isRemote && isActive)
		{
			ITankContainer[] connectedAcceptors = PipeUtils.getConnectedAcceptors(this);
			
			for(ITankContainer container : connectedAcceptors)
			{
				ForgeDirection side = ForgeDirection.getOrientation(Arrays.asList(connectedAcceptors).indexOf(container)).getOpposite();
				
				if(container != null)
				{
					LiquidStack received = container.drain(side, 100, false);
					
					if(received != null && received.amount != 0)
					{
						container.drain(side, new LiquidTransferProtocol(this, VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), side.getOpposite()), received).calculate(), true);
					}
				}
			}
		}
	}
	
	@Override
	public boolean canUpdate()
	{
		return isActive;
	}
	
	@Override
	public void validate()
	{
		super.validate();
		
		if(worldObj.isRemote)
		{
			PacketHandler.sendDataRequest(this);
		}
	}
	
	@Override
	public void handlePacketData(ByteArrayDataInput dataStream)
	{
		isActive = dataStream.readBoolean();
	}
	
	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		data.add(isActive);
		return data;
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbtTags)
    {
        super.readFromNBT(nbtTags);

        isActive = nbtTags.getBoolean("isActive");
    }

	@Override
    public void writeToNBT(NBTTagCompound nbtTags)
    {
        super.writeToNBT(nbtTags);
        
        nbtTags.setBoolean("isActive", isActive);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		return new LiquidTransferProtocol(this, VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), from), resource).calculate();
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) 
	{
		return new LiquidTransferProtocol(this, null, resource).calculate();
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) 
	{
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) 
	{
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) 
	{
		return new ILiquidTank[] {dummyTank};
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) 
	{
		return dummyTank;
	}
}
