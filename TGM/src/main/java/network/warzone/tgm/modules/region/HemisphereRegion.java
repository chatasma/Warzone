package network.warzone.tgm.modules.region;

import com.google.gson.JsonElement;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class HemisphereRegion implements Region {
    private interface HemisphereDirectionEvaluation {
        boolean contains(Location focalPoint, Location loc);
    }
    enum HemisphereFace {
        POSITIVE_X((focalPoint, loc) -> loc.getX() >= focalPoint.getX()), NEGATIVE_X((focalPoint, loc) -> loc.getX() <= focalPoint.getX()),
        POSITIVE_Y((focalPoint, loc) -> loc.getY() >= focalPoint.getY()), NEGATIVE_Y((focalPoint, loc) -> loc.getY() <= focalPoint.getY()),
        POSITIVE_Z((focalPoint, loc) -> loc.getZ() >= focalPoint.getZ()), NEGATIVE_Z((focalPoint, loc) -> loc.getZ() <= focalPoint.getZ());

        @Getter HemisphereDirectionEvaluation hemisphereDirectionEvaluation;
        HemisphereFace(HemisphereDirectionEvaluation hemisphereDirectionEvaluation) {
            this.hemisphereDirectionEvaluation = hemisphereDirectionEvaluation;
        }
    }

    @Getter private final Location focalPoint;
    @Getter private final double radius;
    private final Location min;
    private final Location max;
    private HemisphereFace hemisphereFace;


    public HemisphereRegion(Location focalPoint, double radius, HemisphereFace hemisphereFace) {
       this.focalPoint = focalPoint;
       this.radius = radius;
       this.hemisphereFace = hemisphereFace;
       switch (hemisphereFace) {
           case POSITIVE_X:
               this.min = new Location(focalPoint.getWorld(), focalPoint.getX(), focalPoint.getY() - radius, focalPoint.getZ() - radius);
               this.max = new Location(focalPoint.getWorld(), focalPoint.getX() + radius, focalPoint.getY() + radius, focalPoint.getZ() + radius);
                break;
           case NEGATIVE_X:
               this.min = new Location(focalPoint.getWorld(), focalPoint.getX(), focalPoint.getY() - radius, focalPoint.getZ() - radius);
               this.max = new Location(focalPoint.getWorld(), focalPoint.getX() - radius, focalPoint.getY() + radius, focalPoint.getZ() + radius);
               break;
           case POSITIVE_Y:
               this.min = new Location(focalPoint.getWorld(), focalPoint.getX() - radius, focalPoint.getY(), focalPoint.getZ() - radius);
               this.max = new Location(focalPoint.getWorld(), focalPoint.getX() + radius, focalPoint.getY() + radius, focalPoint.getZ() + radius);
               break;
           case NEGATIVE_Y:
               this.min = new Location(focalPoint.getWorld(), focalPoint.getX() - radius, focalPoint.getY(), focalPoint.getZ() - radius);
               this.max = new Location(focalPoint.getWorld(), focalPoint.getX() + radius, focalPoint.getY() - radius, focalPoint.getZ() + radius);
               break;
           case POSITIVE_Z:
               this.min = new Location(focalPoint.getWorld(), focalPoint.getX() - radius, focalPoint.getY() - radius, focalPoint.getZ());
               this.max = new Location(focalPoint.getWorld(), focalPoint.getX() + radius, focalPoint.getY() + radius, focalPoint.getZ() + radius);
               break;
           case NEGATIVE_Z:
           default:
               this.min = new Location(focalPoint.getWorld(), focalPoint.getX() - radius, focalPoint.getY() - radius, focalPoint.getZ());
               this.max = new Location(focalPoint.getWorld(), focalPoint.getX() + radius, focalPoint.getY() + radius, focalPoint.getZ() - radius);
               break;
       }
    }
    @Override
    public boolean contains(Location location) {
        return hemisphereFace.hemisphereDirectionEvaluation.contains(focalPoint, location) && focalPoint.distance(location) <= radius;
    }

    @Override
    public boolean contains(Block block) {
        return contains(block.getLocation());
    }

    @Override
    public Location getCenter() {
        return this.focalPoint;
    }

    @Override
    public List<Block> getBlocks() {
        List<Block> results = new ArrayList<>();
        CuboidRegion bound = new CuboidRegion(this.min, this.max);
        for (Block block : bound.getBlocks()) {
            if (contains(new Location(focalPoint.getWorld(), block.getX(), block.getY(), block.getZ()))) results.add(block);
        }
        return results;
    }

    @Override
    public Location getMin() {
        return this.min;
    }

    @Override
    public Location getMax() {
        return this.max;
    }

    public static HemisphereFace parseHemisphereDirection(JsonElement hemisphereDirection) {
        String hemisphereDirRaw = hemisphereDirection.getAsString();
        return HemisphereFace.valueOf((hemisphereDirRaw.charAt(0) == '-' ? "NEGATIVE" : "POSITIVE") + "_" + Character.toString(hemisphereDirRaw.charAt(1)).toUpperCase());
    }
}
