LengthParameter thickness 		= new LengthParameter("Material Thickness",5.1,[25.4,1])

CSG corner =new Cylinder(2.5*25.4,2.5*25.4,thickness.getMM(),(int)30).toCSG() // a one line Cylinder
double shoulderPos = 17*25.4
double soulderSeperation = 15*25.4
CSG shoulder = corner.toYMax().movey(soulderSeperation)
				.union(corner.toYMin().movey(-soulderSeperation))
double shoulderInset = 30				
CSG shoulderMountA = corner.rotx(90).movey(15*25.4-shoulderInset)
				.scalex(1.2)
				.scalez(0.9)
				.movex(shoulderPos)
				
CSG plate = corner
			.union(corner.movex(22*25.4))
			.union (shoulder.movex(shoulderPos))
			.hull()
CSG upper = new Cube(10000).toCSG().toZMin()
			.movez(plate.getMaxZ())
			
CSG clavicle = corner.movey(soulderSeperation/2)
				.roty(90)
				.movez(200)
				.movex(shoulderPos)
CSG chest = shoulder.roty(90)
			.movex(shoulderPos)
			.intersect(upper)

def intersectParts = [shoulderMountA.intersect(chest),shoulderMountA.intersect(plate)].collect{
	return it.movey(thickness.getMM())
		.union(it.movey(-thickness.getMM()))
		.hull()
}
shoulderMountA=shoulderMountA.difference(intersectParts)

shoulderMountB=shoulderMountA.toYMax().movey((-soulderSeperation+shoulderInset))
return[ plate,chest,shoulderMountA,shoulderMountB,clavicle]