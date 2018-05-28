package com.elytradev.marsenal.math;

import net.minecraft.util.math.Vec3d;

public class ReferenceFrame {
	
	//The "forward matrix" to get coordinates from global-space into local-space
	protected double[] matrix = {
			1.0, 0.0, 0.0,
			0.0, 1.0, 0.0,
			0.0, 0.0, 1.0 };
	protected double[] inverse;
	
	
	protected Vec3d origin;
	
	protected Vec3d xAxis;
	protected Vec3d yAxis;
	protected Vec3d zAxis;
	
	/** The rotation of the frame around the global Y axis */
	protected double inclination;
	/** The rotation of the frame around the global Z axis */
	protected double azimuth;
	
	public Vec3d xAxis() {
		return xAxis;
	}
	
	public Vec3d yAxis() {
		return yAxis;
	}
	
	public Vec3d zAxis() {
		return zAxis;
	}
	
	public Vec3d pointAt(Vec3d vec) {
		double dx = vec.x - origin.x;
		double dy = vec.y - origin.y;
		double dz = vec.z - origin.z;
		
		
		double xzDistance = Math.sqrt(dx*dx + dz*dz);
		
		inclination = Math.atan2(dz, dx);
		azimuth = Math.atan2(dy, xzDistance);
		
		
		
		
		return vec; //TODO: Implement
	}
	
	/** Transform a direction vector into global space */
	//public Vec3d orient2Global(Vec3d vec) {
		
		
	//}
	
	public Vec3d global2Local(Vec3d vec) {
		return vec; //TODO: Implement
	}
}
