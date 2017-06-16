LengthParameter thickness 		= new LengthParameter("Material Thickness",5.1,[25.4,1])

CSG corner =new Cylinder(2.5*25.4,2.5*25.4,thickness.getMM(),(int)30).toCSG() // a one line Cylinder
CSG shoulder = corner.toYMax().movey(15*25.4)
				.union(corner.toYMin().movey(-15*25.4))
CSG plate = corner
			.union(corner.movex(22*25.4))
			.union (shoulder.movex(17*25.4))
			.hull()

return plate