package de.inventivegames.ForceField;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public enum ParticleEffects {
	HUGE_EXPLOSION("hugeexplosion", 0), LARGE_EXPLODE("largeexplode", 1), FIREWORKS_SPARK(
			"fireworksSpark", 2), BUBBLE("bubble", 3), SUSPEND("suspend", 4), DEPTH_SUSPEND(
			"depthSuspend", 5), TOWN_AURA("townaura", 6), CRIT("crit", 7), MAGIC_CRIT(
			"magicCrit", 8), MOB_SPELL("mobSpell", 9), MOB_SPELL_AMBIENT(
			"mobSpellAmbient", 10), SPELL("spell", 11), INSTANT_SPELL(
			"instantSpell", 12), WITCH_MAGIC("witchMagic", 13), NOTE("note", 14), PORTAL(
			"portal", 15), ENCHANTMENT_TABLE("enchantmenttable", 16), EXPLODE(
			"explode", 17), FLAME("flame", 18), LAVA("lava", 19), FOOTSTEP(
			"footstep", 20), SPLASH("splash", 21), LARGE_SMOKE("largesmoke", 22), CLOUD(
			"cloud", 23), RED_DUST("reddust", 24), SNOWBALL_POOF(
			"snowballpoof", 25), DRIP_WATER("dripWater", 26), DRIP_LAVA(
			"dripLava", 27), SNOW_SHOVEL("snowshovel", 28), SLIME("slime", 29), HEART(
			"heart", 30), ANGRY_VILLAGER("angryVillager", 31), HAPPY_VILLAGER(
			"happyVillager", 32), ICONCRACK("iconcrack", 33), TILECRACK(
			"tilecrack", 34);

	private String										name;
	private int											id;
	private static final Map<String, ParticleEffects>	NAME_MAP;
	private static final Map<Integer, ParticleEffects>	ID_MAP;

	static {
		NAME_MAP = new HashMap<String, ParticleEffects>();
		ID_MAP = new HashMap<Integer, ParticleEffects>();

		for (ParticleEffects effect : values()) {
			NAME_MAP.put(effect.name, effect);
			ID_MAP.put(Integer.valueOf(effect.id), effect);
		}
	}

	private ParticleEffects(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public int getId() {
		return this.id;
	}

	@SuppressWarnings("rawtypes")
	public static ParticleEffects fromName(String name) {
		if (name == null) {
			return null;
		}
		for (Map.Entry e : NAME_MAP.entrySet()) {
			if (((String) e.getKey()).equalsIgnoreCase(name)) {
				return (ParticleEffects) e.getValue();
			}
		}
		return null;
	}

	public static ParticleEffects fromId(int id) {
		return ID_MAP.get(Integer.valueOf(id));
	}

	public void sendToPlayer(Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		Object packet = createPacket(this, location, offsetX, offsetY, offsetZ, speed, count);
		sendPacket(player, packet);
	}

	public static void sendToLocation(ParticleEffects effect, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		Object packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
		for (Player player : Bukkit.getOnlinePlayers()) {
			sendPacket(player, packet);
		}
	}

	public static void sendCrackToPlayer(boolean icon, int id, byte data, Player player, Location location, float offsetX, float offsetY, float offsetZ, int count) throws Exception {
		Object packet = createCrackPacket(icon, id, data, location, offsetX, offsetY, offsetZ, count);
		sendPacket(player, packet);
	}

	public static void sendCrackToLocation(boolean icon, int id, byte data, Location location, float offsetX, float offsetY, float offsetZ, int count) throws Exception {
		Object packet = createCrackPacket(icon, id, data, location, offsetX, offsetY, offsetZ, count);
		for (Player player : location.getWorld().getPlayers()) {
			sendPacket(player, packet);
		}
	}

	public static Object createPacket(ParticleEffects effect, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		if (count <= 0)
			count = 1;
		Object packet = getPacketPlayOutWorldParticles();
		setValue(packet, "a", effect.name);
		setValue(packet, "b", Float.valueOf((float) location.getX()));
		setValue(packet, "c", Float.valueOf((float) location.getY()));
		setValue(packet, "d", Float.valueOf((float) location.getZ()));
		setValue(packet, "e", Float.valueOf(offsetX));
		setValue(packet, "f", Float.valueOf(offsetY));
		setValue(packet, "g", Float.valueOf(offsetZ));
		setValue(packet, "h", Float.valueOf(speed));
		setValue(packet, "i", Integer.valueOf(count));
		return packet;
	}

	public static Object createCrackPacket(boolean icon, int id, byte data, Location location, float offsetX, float offsetY, float offsetZ, int count) throws Exception {
		if (count <= 0)
			count = 1;
		Object packet = getPacketPlayOutWorldParticles();
		String modifier = "iconcrack_" + id;
		if (!icon) {
			modifier = "tilecrack_" + id + "_" + data;
		}
		setValue(packet, "a", modifier);
		setValue(packet, "b", Float.valueOf((float) location.getX()));
		setValue(packet, "c", Float.valueOf((float) location.getY()));
		setValue(packet, "d", Float.valueOf((float) location.getZ()));
		setValue(packet, "e", Float.valueOf(offsetX));
		setValue(packet, "f", Float.valueOf(offsetY));
		setValue(packet, "g", Float.valueOf(offsetZ));
		setValue(packet, "h", Float.valueOf(0.1F));
		setValue(packet, "i", Integer.valueOf(count));
		return packet;
	}

	public static void fakeBlockCrack(Block block, short damage) {
		try {
			if ((damage > 7) || (damage < 0)) {
				throw new NumberFormatException("Damage needs to be between 0 and 7!");
			}

			Object packet = getPacketPlayInBlockDig();
			setValue(packet, "a", Integer.valueOf(0));
			setValue(packet, "b", Integer.valueOf(block.getX()));
			setValue(packet, "c", Integer.valueOf(block.getY()));
			setValue(packet, "d", Integer.valueOf(block.getZ()));
			setValue(packet, "e", Integer.valueOf(Short.toString(damage)));

			for (Player p : block.getWorld().getPlayers()) {
				sendPacket(p, packet);
			}

		} catch (Exception e) {
			Bukkit.getLogger().warning("Something went wrong while crafting the PacketPlayInBlockDig packet! (BLOCK_BREAK)");
			e.printStackTrace();
		}
	}

	private static void setValue(Object instance, String fieldName, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(instance, value);
	}

	private static Object getEntityPlayer(Player p) throws Exception {
		Method getHandle = p.getClass().getMethod("getHandle", new Class[0]);
		return getHandle.invoke(p, new Object[0]);
	}

	private static String getPackageName() {
		return "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	private static Object getPacketPlayOutWorldParticles() throws Exception {
		Class<?> packet = Class.forName(getPackageName() + ".PacketPlayOutWorldParticles");
		return packet.getConstructor(new Class[0]).newInstance(new Object[0]);
	}

	private static Object getPacketPlayInBlockDig() throws Exception {
		Class<?> packet = Class.forName(getPackageName() + ".PacketPlayInBlockDig");
		return packet.getConstructor(new Class[0]).newInstance(new Object[0]);
	}

	private static void sendPacket(Player p, Object packet) throws Exception {
		Object eplayer = getEntityPlayer(p);
		Field playerConnectionField = eplayer.getClass().getField("playerConnection");
		Object playerConnection = playerConnectionField.get(eplayer);
		for (Method m : playerConnection.getClass().getMethods()) {
			if (m.getName().equalsIgnoreCase("sendPacket")) {
				m.invoke(playerConnection, new Object[] { packet });
				return;
			}
		}
	}
}