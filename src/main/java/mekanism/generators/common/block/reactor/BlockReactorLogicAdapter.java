package mekanism.generators.common.block.reactor;

import buildcraft.api.tools.IToolWrench;
import javax.annotation.Nonnull;
import mekanism.common.Mekanism;
import mekanism.common.block.interfaces.IBlockDescriptive;
import mekanism.common.block.interfaces.IHasGui;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.tile.reactor.TileEntityReactorLogicAdapter;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReactorLogicAdapter extends Block implements IHasGui, IBlockDescriptive {

    private final String name;

    public BlockReactorLogicAdapter() {
        super(Material.IRON);
        setHardness(3.5F);
        setResistance(8F);
        setCreativeTab(Mekanism.tabMekanism);
        this.name = "reactor_logic_adapter";
        setTranslationKey(this.name);
        setRegistryName(new ResourceLocation(MekanismGenerators.MODID, this.name));
    }

    @Override
    public String getDescription() {
        return LangUtils.localize("tooltip.mekanism." + name);
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityBasicBlock) {
                ((TileEntityBasicBlock) tileEntity).onNeighborChange(neighborBlock);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityplayer, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        ItemStack stack = entityplayer.getHeldItem(hand);
        if (!stack.isEmpty()) {
            if (MekanismUtils.isBCWrench(stack.getItem()) && !stack.getTranslationKey().contains("omniwrench")) {
                if (entityplayer.isSneaking()) {
                    MekanismUtils.dismantleBlock(this, state, world, pos);
                    return true;
                }
                ((IToolWrench) stack.getItem()).wrenchUsed(entityplayer, hand, stack, new RayTraceResult(new Vec3d(hitX, hitY, hitZ), side, pos));
                return true;
            }
        }
        if (!entityplayer.isSneaking()) {
            entityplayer.openGui(MekanismGenerators.instance, getGuiID(), world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityReactorLogicAdapter();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    @Deprecated
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = MekanismUtils.getTileEntitySafe(world, pos);
        if (tile instanceof TileEntityReactorLogicAdapter) {
            return ((TileEntityReactorLogicAdapter) tile).checkMode() ? 15 : 0;
        }
        return 0;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public int getGuiID() {
        return 15;
    }
}