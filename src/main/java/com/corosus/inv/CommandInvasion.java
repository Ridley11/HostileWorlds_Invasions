package com.corosus.inv;

import CoroUtil.difficulty.DifficultyQueryContext;
import CoroUtil.difficulty.data.DifficultyDataReader;
import CoroUtil.difficulty.data.conditions.ConditionContext;
import CoroUtil.difficulty.data.spawns.DataMobSpawnsTemplate;
import CoroUtil.util.CoroUtilWorldTime;
import com.corosus.inv.capabilities.PlayerDataInstance;
import com.corosus.inv.config.ConfigInvasion;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import CoroUtil.util.BlockCoord;
import CoroUtil.util.CoroUtilMisc;
import CoroUtil.difficulty.DynamicDifficulty;

import CoroUtil.util.UtilMining;

public class CommandInvasion extends CommandBase {

	@Override
	public String getName() {
		return "hw_invasions";
	}

	@Override
	public String getUsage(ICommandSender icommandsender) {
		return "";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender var1, String[] var2) {
		
		/*if (!(var1 instanceof EntityPlayerMP)) {
			System.out.println("Works for actual players only");
			return;
		}*/

		EntityPlayer player = null;
		if (var1 instanceof EntityPlayer) {
			player = (EntityPlayer) var1;
		}
		World world = var1.getEntityWorld();
		int dimension = world.provider.getDimension();
		BlockPos posBlock = var1.getPosition();
		Vec3d posVec = var1.getPositionVector();
		
		try {
			
			world = DimensionManager.getWorld(0);
			long dayNumber = (world.getWorldTime() / CoroUtilWorldTime.getDayLength()) + 1;
			
			if (var2.length < 1 || var2[0].equalsIgnoreCase("difficulty"))
	        {
				if ((var1 instanceof EntityPlayerMP)) {
					EntityPlayerMP ent = (EntityPlayerMP) var1;
		    		//net.minecraft.util.Vec3 posVec = ent.getPosition(1F);
		    		/*net.minecraft.util.math.Vec3d */posVec = new net.minecraft.util.math.Vec3d(ent.posX, ent.posY + (ent.getEyeHeight() - ent.getDefaultEyeHeight()), ent.posZ);//player.getPosition(1F);
		    		BlockCoord pos = new BlockCoord(MathHelper.floor(posVec.x), MathHelper.floor(posVec.y), MathHelper.floor(posVec.z));
		    		//long dayNumber = (ent.worldObj.getWorldTime() / CoroUtilWorldTime.getDayLength()) + 1;
		    		CoroUtilMisc.sendCommandSenderMsg(ent, "day: " + dayNumber + ", average difficulty for this area: " + TextFormatting.GREEN + DynamicDifficulty.getDifficultyScaleAverage(ent.world, ent, pos));
					//DynamicDifficulty.msgDifficultyData((EntityPlayer) var1, pos);
				} else {
					var1.sendMessage(new TextComponentString("day: " + dayNumber));
		    		//CoroUtil.sendPlayerMsg(ent, "day: " + dayNumber + ", difficulty for this area: " + EventHandlerForge.getDifficultyScaleAverage(ent.worldObj, ent, pos));
				}
	        }
	        else
	        {

	        	if (var2[0].equalsIgnoreCase("canMine")) {
					if (var2.length <= 4) {
						int x = Integer.valueOf(var2[1]);
						int y = Integer.valueOf(var2[2]);
						int z = Integer.valueOf(var2[3]);

						BlockPos pos = new BlockPos(x, y, z);
						IBlockState state = world.getBlockState(pos);
						Block block = state.getBlock();
						boolean canMine = UtilMining.canMineBlockNew(world, pos);
						float blockStrength = state.getBlockHardness(world, pos);

						var1.sendMessage(new TextComponentString("can mine? "/* + x + ", " + y + ", " + z + "?: "*/ + canMine + ", hardness: " + blockStrength + ", block: " + block.getLocalizedName()));
					}

				} else if (var2[0].equalsIgnoreCase("skip")) {
					if (player != null) {
						InvasionManager.skipNextInvasionForPlayer(player);
					} else {
						var1.sendMessage(new TextComponentString("requires player reference"));
					}
				} else if (var2[0].equalsIgnoreCase("ti") || var2[0].equalsIgnoreCase("testInvasion")) {
					if (player != null) {

						BlockCoord pos = new BlockCoord(MathHelper.floor(posVec.x), MathHelper.floor(posVec.y), MathHelper.floor(posVec.z));
						double difficultyScale = DynamicDifficulty.getDifficultyScaleAverage(world, player, pos);
						if (var2.length >= 2) difficultyScale = Double.valueOf(var2[1]);
						int invasionNumber = 0;//InvasionManager.getInvasionNumber(world);
						if (ConfigInvasion.invasionCountingPerPlayer) {
							PlayerDataInstance storage = player.getCapability(Invasion.PLAYER_DATA_INSTANCE, null);
							invasionNumber = storage.lastWaveNumber;
						} else {
							invasionNumber = InvasionManager.getInvasionNumber(player.world);
						}
						if (var2.length >= 3) invasionNumber = Integer.valueOf(var2[2]);

						try {
							DifficultyDataReader.setDebugDifficulty(difficultyScale);

							DataMobSpawnsTemplate profile = InvasionManager.getInvasionTestData(player, new DifficultyQueryContext(ConditionContext.TYPE_INVASION, invasionNumber, (float) difficultyScale));

							var1.sendMessage(new TextComponentString(TextFormatting.GREEN + "Invasion template for difficulty: " + difficultyScale + ", invasion number: " + invasionNumber));
							if (profile != null) {

								String data = profile.toString();
								String[] list = data.split(" \\| ");
								for (String entry : list) {
									var1.sendMessage(new TextComponentString(entry));
								}
							} else {
								var1.sendMessage(new TextComponentString(TextFormatting.GREEN + "Could not find template for that scenario"));
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							DifficultyDataReader.setDebugDifficulty(-1);
						}

					}
				} else if (var2[0].equalsIgnoreCase("forceInvasion")) {
	        		int amount = (CoroUtilWorldTime.getDayLength() * 3) + (6000 * 2) + (600 * 3);
	        		world.getWorldInfo().setWorldTime(amount);

				} else if (var2[0].equalsIgnoreCase("resetPlayer")) {
					if (var2.length >= 3) {
						player = world.getPlayerEntityByName(var2[2]);
						if (player == null) {
							CoroUtilMisc.sendCommandSenderMsg(var1, "Couldnt find player by name: " + var2[1]);
						}
					}
					if (player != null) {
						player.getEntityData().setLong(DynamicDifficulty.dataPlayerServerTicks, 0);
						PlayerDataInstance storage = player.getCapability(Invasion.PLAYER_DATA_INSTANCE, null);
						storage.resetPersistentData();
						CoroUtilMisc.sendCommandSenderMsg(var1, "Reset persistent player invasion data for: " + player.getDisplayNameString());
					}
				} else if (var2[0].equalsIgnoreCase("setPlayerTime")) {
					int time = 0;
					if (var2.length >= 2) time = Integer.valueOf(var2[1]);
					if (var2.length >= 3) {
						player = world.getPlayerEntityByName(var2[2]);
						if (player == null) {
							CoroUtilMisc.sendCommandSenderMsg(var1, "Couldnt find player by name: " + var2[1]);
						}
					}
					if (player != null) {
						player.getEntityData().setLong(DynamicDifficulty.dataPlayerServerTicks, time);
						CoroUtilMisc.sendCommandSenderMsg(var1, "Set player time for: " + player.getDisplayNameString() + " to " + time);
					}
				} else if (var2[0].equalsIgnoreCase("setPlayerWave")) {
					int wave = 0;
					if (var2.length >= 2) wave = Integer.valueOf(var2[1]);
					if (var2.length >= 3) {
						player = world.getPlayerEntityByName(var2[2]);
						if (player == null) {
							CoroUtilMisc.sendCommandSenderMsg(var1, "Couldnt find player by name: " + var2[1]);
						}
					}
					if (player != null) {
						PlayerDataInstance storage = player.getCapability(Invasion.PLAYER_DATA_INSTANCE, null);
						storage.lastWaveNumber = wave;
						CoroUtilMisc.sendCommandSenderMsg(var1, "Set player last wave # for: " + player.getDisplayNameString() + " to " + wave);
					}
				} else if (var2[0].equalsIgnoreCase("playerTime")) {

					if (var2.length >= 2) {
						player = world.getPlayerEntityByName(var2[1]);
						if (player == null) {
							CoroUtilMisc.sendCommandSenderMsg(var1, "Couldnt find player by name: " + var2[1]);
						}
					}

					if (player != null) {
						long time = player.getEntityData().getLong(DynamicDifficulty.dataPlayerServerTicks);
						PlayerDataInstance storage = player.getCapability(Invasion.PLAYER_DATA_INSTANCE, null);
						CoroUtilMisc.sendCommandSenderMsg(var1, "Active tracked player time for: " + player.getDisplayNameString());
						CoroUtilMisc.sendCommandSenderMsg(var1, "Ticks: " + time);
						CoroUtilMisc.sendCommandSenderMsg(var1, "Days Played: " + (time / CoroUtilWorldTime.getDayLength()));
						if (ConfigInvasion.invasionCountingPerPlayer) {
							CoroUtilMisc.sendCommandSenderMsg(var1, "Days Needed: " + (ConfigInvasion.firstInvasionNight - 1) + " to " + ConfigInvasion.firstInvasionNight);
						} else {
							CoroUtilMisc.sendCommandSenderMsg(var1, "Server time in days needs: " + (ConfigInvasion.firstInvasionNight - 1) + " to " + ConfigInvasion.firstInvasionNight);
						}
						CoroUtilMisc.sendCommandSenderMsg(var1, "Last Wave #: " + storage.lastWaveNumber);
					}
				}
	        	
	        	
	        	
	        }
			
			
		} catch (Exception ex) {
			System.out.println("Caught HW_Invasion command crash!!!");
			ex.printStackTrace();
		}
	}
	
	/*@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return true;//par1ICommandSender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }*/
	
	/*@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender par1ICommandSender)
    {
		return true;
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}*/

	//require OP again

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender par1ICommandSender)
	{
		return par1ICommandSender.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
	}

}
