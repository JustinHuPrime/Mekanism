package mekanism.api;

import net.minecraft.tileentity.TileEntity;

/**
 * Implement this in your TileEntity class if the block can transfer liquid as a Mechanical Pipe.
 * @author AidanBrady
 *
 */
public interface IMechanicalPipe 
{
	/**
	 * Whether or not this pipe can transfer liquids.
	 * @return if the pipe can transfer liquids
	 */
	public boolean canTransferLiquids(TileEntity fromTile);
}
