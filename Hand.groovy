
CSGDatabase.clear()
class handMaker{
	LengthParameter numberFingers 	= new LengthParameter("Number of Fingers",4,[8,1])
	LengthParameter numberThumbs 		= new LengthParameter("Number Thumbs",1,[3,0])
	StringParameter left 			= new StringParameter("Is Left hand","true",["false","true"])
	LengthParameter PalmWidth 		= new LengthParameter("Palm Width",90,[130.0,2.0])
	LengthParameter lowerPalmWidth 		= new LengthParameter("Lower Palm Width",80,[130.0,2.0])
	LengthParameter palmLength 		= new LengthParameter("Palm Length",123,[130.0,2.0])
	LengthParameter fingerLength 		= new LengthParameter("Finger Length",106,[130.0,2.0])
	LengthParameter thumbLength 		= new LengthParameter("Thumb Length",77,[130.0,2.0])
	LengthParameter fingerDelta 		= new LengthParameter("Finger Delta",15,[50,0])
	StringParameter boltSizeParam 			= new StringParameter("Bolt Size","M5",Vitamins.listVitaminSizes("capScrew"))
	LengthParameter fingerSpread 		= new LengthParameter("Finger Spread Angle",30,[20,0])
	LengthParameter thumbSpread 		= new LengthParameter("Thumb Spread Angle",46,[20,0])
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.15,[25.4,1])
	LengthParameter boltLength		= new LengthParameter("Bolt Length",25,[180,10])
	LengthParameter tendonOffset		= new LengthParameter("Tendon offset",20,[180,10])
	LengthParameter printerOffset 			= new LengthParameter("printerOffset",0.5,[1.2,0])
	HashMap<String, Object>  boltMeasurments = Vitamins.getConfiguration( "capScrew",boltSizeParam.getStrValue())
	CSG bolt = Vitamins.get( "capScrew",boltSizeParam.getStrValue())
				.movez(printerOffset.getMM())
	CSG makeMountLugBoltsCache = null
	CSG makeMountLugCache = null
	double lugRadius = (boltMeasurments.headDiameter*2+thickness.getMM())/2
	HashMap<Double,CSG> linkCache = new HashMap<>()
	Transform getFingerLocation(int index){
		Transform t= new Transform()
		double percent=0
		if(numberFingers.getMM()>1)
			percent= ((numberFingers.getMM()-index-1)/(numberFingers.getMM()-1))
		else
			percent=1.0
		double palmWithAmount = PalmWidth.getMM()*percent
		double delta = Math.sin(percent*Math.PI)*fingerDelta.getMM()
		println percent+" "+delta
		t.translateY(palmWithAmount)
		t.translateX(delta+palmLength.getMM())
		t.rotZ(fingerSpread.getMM()-180)
		t.rotZ(-fingerSpread.getMM()*2*percent)
		
		return t
					
	}
	Transform getThumbLocation(int index){
		Transform t= new Transform()
	
		double percent=0
		if(numberThumbs.getMM()>1)
			percent= ((numberThumbs.getMM()-index-1)/(numberThumbs.getMM()-1))
		else
			percent=1.0
		if(left.getStrValue().toLowerCase().contains("false")){
			percent=1-percent
			t.translateY(PalmWidth.getMM()-lowerPalmWidth.getMM())	
		}
			
		
		double palmWithAmount = lowerPalmWidth.getMM()*percent
		println "Thumb "  +index+" "+percent+" "+palmWithAmount
		t.translateY(palmWithAmount)
		t.rotZ(-thumbSpread.getMM())
		t.rotZ(thumbSpread.getMM()*2*percent)
		return t
					
	}
	
	ArrayList<CSG> makeThumb(int index){
		
	}
	ArrayList<CSG> makeFinger(int index){
		
	}

	ArrayList<CSG> makePalm(){
		ArrayList<Transform> corners = []
		CSG corner = makeMountBase()
		CSG lug = makeMountLug()
		for (int i=0;i<numberFingers.getMM();i++){
			corners.add(getFingerLocation(i))
		}
		for (int i=0;i<numberThumbs.getMM();i++){
			corners.add(getThumbLocation(i))
		}
		ArrayList<CSG> parts = corners.collect{
			return corner.transformed(it)
		}
		CSG boltOffset =makeMountLugBolts().toolOffset(printerOffset.getMM())
		ArrayList<CSG> lugs = corners.collect{
			return lug
					.difference(boltOffset)
					.transformed(it)
		}
		CSG plate =CSG
				.unionAll(parts)
				.hull() 
				.difference(corners.collect{
					return makeMountLugBolts().transformed(it)
				})
		lugs.add(plate)
		return lugs
		
	}
	
	CSG makeMountBase(){
		return  new Cube(lugRadius*2,lugRadius*2,thickness.getMM())
				.toCSG() // a one line Cylinder
				.toZMax()
				.movex(lugRadius*0.5)
	}
	CSG makeMountLug(){
		if(makeMountLugCache == null){
			CSG base = makeMountBase()
			makeMountLugCache=base.movez(thickness.getMM())
							.union(base.movez(boltMeasurments.headDiameter+tendonOffset.getMM()))
							.hull()
			makeMountLugCache=makeMountLugCache	
							.union(makeLink(0)
									.movez(makeMountLugCache.getMaxZ())
							)
		}
		return makeMountLugCache
	}
	CSG makeMountLugBolts(){
		if(makeMountLugBoltsCache == null){
			CSG lug = makeMountLug()
			double boltOffset = (boltMeasurments.headDiameter+thickness.getMM())/2
			makeMountLugBoltsCache=bolt
								.movey(boltOffset)
								.movex(boltMeasurments.headDiameter)
								.union(bolt
									.movey(-boltOffset)
									.movex(boltMeasurments.headDiameter)
								).union(bolt
									.rotx(90)
									.toZMax()
									.movey(lugRadius)
									.movez(-tendonOffset.getMM()/2)
								)
								.movez(lug.getMaxZ())
								
		}
		
		return makeMountLugBoltsCache
	}
	CSG makeLink(double length){
		if(length<boltMeasurments.headDiameter){
			length=boltMeasurments.headDiameter
		}
		if(linkCache.get(length)== null){
			double radOfNuckel = tendonOffset.getMM()/2
			CSG linknuckel =new Cylinder(radOfNuckel,radOfNuckel,thickness.getMM(),(int)10)
								.toCSG() 
								.rotx(90)
			
			CSG link = linknuckel
						.union(linknuckel
								.movex(-length))
						.movey(-thickness.getMM()/2)
			linkCache.put(length,link)
		}
		return linkCache.get(length)
	}
	ArrayList<CSG> makeParts(){
		ArrayList<CSG> parts = []
		
		for (int i=0;i<numberFingers.getMM();i++){
			for(CSG bit: makeFinger(i)){
				parts.add(bit)
			}
		}
		for (int i=0;i<numberThumbs.getMM();i++){
			for(CSG bit: makeThumb(i)){
				parts.add(bit)
			}
		}
		
		return parts
	}
}
new handMaker().makePalm()