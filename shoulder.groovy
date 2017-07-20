LengthParameter thickness 		= new LengthParameter("Material Thickness",5.1,[25.4,1])

CSG corner =new Cylinder(2.5*25.4,2.5*25.4,thickness.getMM(),(int)30).toCSG() // a one line Cylinder
double shoulderPos = 17*25.4
double soulderSeperation = 15*25.4
CSG shoulderA = corner.toYMax().movey(soulderSeperation)
CSG shoulderB =corner.toYMin().movey(-soulderSeperation)
CSG shoulder = shoulderA
				.union(shoulderB)
double shoulderInset = 40	
double chestDepth = 150		
CSG shoulderMountA = corner.rotx(90).movey(15*25.4-shoulderInset)
				.scalex(1.5)
				.scalez(0.9)
				.movex(shoulderPos)
				.movez(25)
				
CSG plate = corner
			.union(corner.movex(22*25.4))
			.union (shoulder.movex(shoulderPos))
			.hull()
CSG upper = new Cube(10000).toCSG().toZMin()
			.movez(plate.getMaxZ())
			
CSG clavicleA = corner.movey(soulderSeperation/3)
				.roty(90)				
				.toZMin()
				.movez(chestDepth)
				.movex(shoulderPos)
CSG clavicleB = corner.movey(-soulderSeperation/3)
				.roty(90)
				.toZMin()
				.movez(chestDepth)
				.movex(shoulderPos)	
CSG centerClavicle = 	clavicleA.union(clavicleB).hull()			

				
CSG chest =CSG.unionAll([
			shoulderA
				.roty(90)
				.movex(shoulderPos)
				.union(clavicleA)
				.hull(),
			shoulderB
				.roty(90)
				.movex(shoulderPos)
				.union(clavicleB)
				.hull(),
			centerClavicle
			]) 				
			.intersect(upper)
def intersectParts = [shoulderMountA.intersect(chest),shoulderMountA.intersect(plate)].collect{
	return it.movey(thickness.getMM())
		.union(it.movey(-thickness.getMM()))
		.hull()
}
shoulderMountA=shoulderMountA.difference(intersectParts)

shoulderMountB=shoulderMountA.toYMax().movey((-soulderSeperation+shoulderInset))

chest.setManufacturing({ toMfg ->
	return toMfg
			.roty(90)
			.toXMin()
			.toYMin()
			.toZMin()
})
shoulderMountB.setManufacturing({ toMfg ->
	return toMfg
			.rotx(90)
			.toXMin()
			.toYMin()
			.toZMin()
})
shoulderMountA.setManufacturing({ toMfg ->
	return toMfg
			.rotx(90)
			.toXMin()
			.toYMin()
			.toZMin()
})

return[ plate,chest,shoulderMountA,shoulderMountB]