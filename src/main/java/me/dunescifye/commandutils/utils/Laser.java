package me.dunescifye.commandutils.utils;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Laser {


    protected final int distanceSquared;
    protected final int duration;
    protected boolean durationInTicks = false;
    protected Location start;
    protected Location end;

    protected Plugin plugin;
    protected BukkitRunnable main;

    protected BukkitTask startMove;
    protected BukkitTask endMove;

    protected Set<Player> show = ConcurrentHashMap.newKeySet();
    private Set<Player> seen = new HashSet<>();

    private List<Runnable> executeEnd = new ArrayList<>(1);


    protected Laser(Location start, Location end, int duration, int distance) {
        if (!Packets.enabled) throw new IllegalStateException("The Laser Beam API is disabled. An error has occured during initialization.");
        if (start.getWorld() != end.getWorld()) throw new IllegalArgumentException("Locations do not belong to the same worlds.");
        this.start = start.clone();
        this.end = end.clone();
        this.duration = duration;
        distanceSquared = distance < 0 ? -1 : distance * distance;
    }

    /**
     * Adds a runnable to execute when the laser reaches its final duration
     * @param runnable action to execute
     * @return this {@link Laser} instance
     */
    public Laser executeEnd(Runnable runnable) {
        executeEnd.add(runnable);
        return this;
    }

    /**
     * Makes the duration provided in the constructor passed as ticks and not seconds
     * @return this {@link Laser} instance
     */
    public Laser durationInTicks() {
        durationInTicks = true;
        return this;
    }

    /**
     * Starts this laser.
     * <p>
     * It will make the laser visible for nearby players and start the countdown to the final duration.
     * <p>
     * Once finished, it will destroy the laser and execute all runnables passed with {@link Laser#executeEnd}.
     * @param plugin plugin used to start the task
     */
    public void start(Plugin plugin) {
        if (main != null) throw new IllegalStateException("Task already started");
        this.plugin = plugin;
        main = new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                try {
                    if (time == duration) {
                        cancel();
                        return;
                    }
                    if (!durationInTicks || time % 20 == 0) {
                        for (Player p : start.getWorld().getPlayers()) {
                            if (isCloseEnough(p)) {
                                if (show.add(p)) {
                                    sendStartPackets(p, !seen.add(p));
                                }
                            }else if (show.remove(p)) {
                                sendDestroyPackets(p);
                            }
                        }
                    }
                    time++;
                }catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                super.cancel();
                main = null;
                try {
                    for (Player p : show) {
                        sendDestroyPackets(p);
                    }
                    show.clear();
                    executeEnd.forEach(Runnable::run);
                }catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        };
        main.runTaskTimerAsynchronously(plugin, 0L, durationInTicks ? 1L : 20L);
    }

    /**
     * Stops this laser.
     * <p>
     * This will destroy the laser for every player and start execute all runnables passed with {@link Laser#executeEnd}
     */
    public void stop() {
        if (main == null) throw new IllegalStateException("Task not started");
        main.cancel();
    }

    /**
     * Gets laser status.
     * @return	<code>true</code> if the laser is currently running
     * 			(i.e. {@link #start} has been called and the duration is not over)
     */
    public boolean isStarted() {
        return main != null;
    }

    /**
     * Gets laser type.
     * @return LaserType enum constant of this laser
     */
    public abstract LaserType getLaserType();

    /**
     * Instantly moves the start of the laser to the location provided.
     * @param location New start location
     * @throws ReflectiveOperationException if a reflection exception occurred during laser moving
     */
    public abstract void moveStart(Location location) throws ReflectiveOperationException;

    /**
     * Instantly moves the end of the laser to the location provided.
     * @param location New end location
     * @throws ReflectiveOperationException if a reflection exception occurred during laser moving
     */
    public abstract void moveEnd(Location location) throws ReflectiveOperationException;

    /**
     * Gets the start location of the laser.
     * @return where exactly is the start position of the laser located
     */
    public Location getStart() {
        return start.clone();
    }

    /**
     * Gets the end location of the laser.
     * @return where exactly is the end position of the laser located
     */
    public Location getEnd() {
        return end.clone();
    }

    /**
     * Moves the start of the laser smoothly to the new location, within a given time.
     * @param location New start location to go to
     * @param ticks Duration (in ticks) to make the move
     * @param callback {@link Runnable} to execute at the end of the move (nullable)
     */
    public void moveStart(Location location, int ticks, Runnable callback) {
        startMove = moveInternal(location, ticks, startMove, getStart(), this::moveStart, callback);
    }

    /**
     * Moves the end of the laser smoothly to the new location, within a given time.
     * @param location New end location to go to
     * @param ticks Duration (in ticks) to make the move
     * @param callback {@link Runnable} to execute at the end of the move (nullable)
     */
    public void moveEnd(Location location, int ticks, Runnable callback) {
        endMove = moveInternal(location, ticks, endMove, getEnd(), this::moveEnd, callback);
    }

    private BukkitTask moveInternal(Location location, int ticks, BukkitTask oldTask, Location from,
                                    ReflectiveConsumer<Location> moveConsumer, Runnable callback) {
        if (ticks <= 0)
            throw new IllegalArgumentException("Ticks must be a positive value");
        if (plugin == null)
            throw new IllegalStateException("The laser must have been started a least once");
        if (oldTask != null && !oldTask.isCancelled())
            oldTask.cancel();
        return new BukkitRunnable() {
            double xPerTick = (location.getX() - from.getX()) / ticks;
            double yPerTick = (location.getY() - from.getY()) / ticks;
            double zPerTick = (location.getZ() - from.getZ()) / ticks;
            Location loc = from.clone();
            int elapsed = 0;

            @Override
            public void run() {
                try {
                    loc.add(xPerTick, yPerTick, zPerTick);
                    moveConsumer.accept(loc);
                }catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                    cancel();
                    return;
                }

                if (++elapsed == ticks) {
                    cancel();
                    if (callback != null) callback.run();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    protected void moveFakeEntity(Location location, int entityId, Object fakeEntity) throws ReflectiveOperationException {
        if (fakeEntity != null) Packets.moveFakeEntity(fakeEntity, location);
        if (main == null) return;

        Object packet;
        if (fakeEntity == null) {
            packet = Packets.createPacketMoveEntity(location, entityId);
        }else {
            packet = Packets.createPacketMoveEntity(fakeEntity);
        }
        for (Player p : show) {
            Packets.sendPackets(p, packet);
        }
    }

    protected abstract void sendStartPackets(Player p, boolean hasSeen) throws ReflectiveOperationException;

    protected abstract void sendDestroyPackets(Player p) throws ReflectiveOperationException;

    protected boolean isCloseEnough(Player player) {
        if (distanceSquared == -1) return true;
        Location location = player.getLocation();
        return	getStart().distanceSquared(location) <= distanceSquared ||
            getEnd().distanceSquared(location) <= distanceSquared;
    }

    public static class GuardianLaser extends Laser {
        private static AtomicInteger teamID = new AtomicInteger(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));

        private Object createGuardianPacket;
        private Object createSquidPacket;
        private Object teamCreatePacket;
        private Object[] destroyPackets;
        private Object metadataPacketGuardian;
        private Object metadataPacketSquid;
        private Object fakeGuardianDataWatcher;

        private final UUID squidUUID = UUID.randomUUID();
        private final UUID guardianUUID = UUID.randomUUID();
        private final int squidID = Packets.generateEID();
        private final int guardianID = Packets.generateEID();
        private Object squid;
        private Object guardian;

        private int targetID;
        private UUID targetUUID;

        protected LivingEntity endEntity;

        private Location correctStart;
        private Location correctEnd;

        /**
         * Creates a new Guardian Laser instance
         * @param start Location where laser will starts
         * @param end Location where laser will ends
         * @param duration Duration of laser in seconds (<i>-1 if infinite</i>)
         * @param distance Distance where laser will be visible (<i>-1 if infinite</i>)
         * @throws ReflectiveOperationException if a reflection exception occurred during Laser creation
         * @see Laser#start(Plugin) to start the laser
         * @see #durationInTicks() to make the duration in ticks
         * @see #executeEnd(Runnable) to add Runnable-s to execute when the laser will stop
         * @see #GuardianLaser(Location, LivingEntity, int, int) to create a laser which follows an entity
         */
        public GuardianLaser(Location start, Location end, int duration, int distance) throws ReflectiveOperationException {
            super(start, end, duration, distance);

            initSquid();

            targetID = squidID;
            targetUUID = squidUUID;

            initLaser();
        }

        /**
         * Creates a new Guardian Laser instance
         * @param start Location where laser will starts
         * @param endEntity Entity who the laser will follow
         * @param duration Duration of laser in seconds (<i>-1 if infinite</i>)
         * @param distance Distance where laser will be visible (<i>-1 if infinite</i>)
         * @throws ReflectiveOperationException if a reflection exception occurred during Laser creation
         * @see Laser#start(Plugin) to start the laser
         * @see #durationInTicks() to make the duration in ticks
         * @see #executeEnd(Runnable) to add Runnable-s to execute when the laser will stop
         * @see #GuardianLaser(Location, Location, int, int) to create a laser with a specific end location
         */
        public GuardianLaser(Location start, LivingEntity endEntity, int duration, int distance) throws ReflectiveOperationException {
            super(start, endEntity.getLocation(), duration, distance);

            targetID = endEntity.getEntityId();
            targetUUID = endEntity.getUniqueId();

            initLaser();
        }

}
