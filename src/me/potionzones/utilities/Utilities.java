package me.potionzones.utilities;

import org.bukkit.Location;


public class Utilities {
	public static Location roundCenterTo0_5(Location center) {
		Location newCenter = new Location(center.getWorld(), 0, 0, 0);
		
		Double xEntero = center.getX() >= 0 ? Math.floor(center.getX()) : Math.ceil(center.getX());
		Double yEntero = Math.floor(center.getY());
		Double zEntero = center.getZ() >= 0 ? Math.floor(center.getZ()) : Math.ceil(center.getZ());
		
		newCenter.setX( xEntero >= 0 ? xEntero + 0.5 : xEntero - 0.5 );
		newCenter.setY( yEntero );
		newCenter.setZ( zEntero >= 0 ? zEntero + 0.5 : zEntero - 0.5 );

		return newCenter;
	}
	public static Location cubeStartLocation(Location center, int blocksFromCenterToBorder) {
		Location start = new Location(center.getWorld(), 0, 0, 0);
		
		Double x = Math.floor(center.getX());
		Double y = 0.0;
		Double z = Math.floor(center.getZ());
		
		start.setX(x - blocksFromCenterToBorder);
		start.setY(y);
		start.setZ(z - blocksFromCenterToBorder);
		
		return start;
	}
	public static Location cubeEndLocation(Location center, int blocksFromCenterToBorder) {
		Location end = new Location(center.getWorld(), 0, 0, 0);
		
		Double x = Math.ceil(center.getX());
		Double y = 256.0;
		Double z = Math.ceil(center.getZ());
		
		end.setX(x + blocksFromCenterToBorder);
		end.setY(y);
		end.setZ(z + blocksFromCenterToBorder);
		
		return end;
	}
}
