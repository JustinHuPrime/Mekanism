package mekanism.generators.common.block.reactor;

import buildcraft.api.tools.IToolWrench;
import javax.annotation.Nonnull;
import mekanism.common.Mekanism;
import mekanism.common.block.interfaces.IBlockDescriptive;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.IStateActive;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.tile.reactor.TileEntityReactorPort;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
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

public class BlockReactorPort extends Block implements IBlockDescriptive, IStateActive {

    private final String name;

    public BlockReactorPort() {
        super(Material.IRON);
        setHardness(3.5F);
        setResistance(8F);
        setCreativeTab(Mekanism.tabMekanism);
        this.name = "reactor_port";
        setTranslationKey(this.name);
        setRegistryName(new ResourceLocation(MekanismGenerators.MODID, this.name));
    }

    @Override
    public String getDescription() {
        return LangUtils.localize("tooltip.mekanism." + name);
    }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tile = MekanismUtils.getTileEntitySafe(worldIn, pos);
        if (tile instanceof TileEntityReactorPort) {
            state = state.withProperty(BlockStateHelper.activeProperty, ((TileEntityReactorPort) tile).fluidEject);
        }
        return state;
    }

    @Nonnull
    @Override
    public BlockStateContainer createBlockState() {
        return BlockStateHelper.getBlockState(this);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        //TODO
        return 0;
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
        return false;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityReactorPort();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
}