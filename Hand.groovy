
CSGDatabase.clear()
class handMaker{
	LengthParameter numberFingers 	= new LengthParameter("Number of Fingers",4,[8,1])
	LengthParameter numberThumbs 		= new LengthParameter("Number Thumbs",1,[3,0])
	StringParameter left 			= new StringParameter("Is Left hand","true",["false","true"])
	LengthParameter PalmWidth 		= new LengthParameter("Palm Width",120,[130.0,2.0])
	LengthParameter lowerPalmWidth 		= new LengthParameter("Lower Palm Width",100,[130.0,2.0])
	LengthParameter palmLength 		= new LengthParameter("Palm Length",123,[130.0,2.0])
	LengthParameter fingerLength 		= new LengthParameter("Finger Length",200,[130.0,2.0])
	LengthParameter thumbLength 		= new LengthParameter("Thumb Length",400/3,[130.0,2.0])
	LengthParameter fingerDelta 		= new LengthParameter("Finger Delta",15,[50,0])
	StringParameter boltSizeParam 	= new StringParameter("Bolt Size","M5",Vitamins.listVitaminSizes("capScrew"))
	LengthParameter fingerSpread 		= new LengthParameter("Finger Spread Angle",10,[20,0])
	LengthParameter thumbSpread 		= new LengthParameter("Thumb Spread Angle",46,[20,0])
	LengthParameter thickness 		= new LengthParameter("Material Thickness",5.1,[25.4,1])
	LengthParameter boltLength		= new LengthParameter("Bolt Length",65,[180,10])
	LengthParameter tendonOffset		= new LengthParameter("Tendon offset",20,[180,10])
	LengthParameter printerOffset 			= new LengthParameter("printerOffset",0.5,[1.2,0])
	LengthParameter extentionLength 			= new LengthParameter("Extention Lngth",100,[500,0])
	HashMap<String, Object>  boltMeasurments = Vitamins.getConfiguration( "capScrew",boltSizeParam.getStrValue())
	CSG bolt = Vitamins.get( "capScrew",boltSizeParam.getStrValue())
				.movez(printerOffset.getMM())
	CSG makeMountLugBoltsCache = null
	CSG makeMountLugCache = null
	CSG cableLugCache = null
	double lugRadius = (boltMeasurments.headDiameter*2+thickness.getMM())/2
	double linkBoltCenter =boltMeasurments.headDiameter+tendonOffset.getMM() -tendonOffset.getMM()/2-thickness.getMM()
	double endOfBoard = 300
	double radOfNuckel = tendonOffset.getMM()/2
	double maxCurlAngle = 80
	HashMap<Double,CSG> linkCache = new HashMap<>()
	ArrayList<Transform> digitLugs(){
		ArrayList<Transform> lugs =[]
		double total = (numberThumbs.getMM()+numberFingers.getMM())
		for(int i=0;i<total;i++){
			Transform t= new Transform()
			double percent=0
			if(total>1)
				percent= ((total-i-1)/(total-1))
			else
				percent=1.0
			t.translateX(-extentionLength.getMM())
			t.translateY(((lugRadius*8+2)*percent) )
			lugs.add(t)
		}
		return lugs
	}
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
		int linksPer = 2
		double linkLen = thumbLength.getMM()/linksPer
		return makeDiget(linksPer,linkLen).collect{
			return it.transformed(getThumbLocation(index))
					.setManufacturing({ toMfg ->
								TransformNR step = com.neuronrobotics.bowlerstudio.physics.TransformFactory.csgToNR(getThumbLocation(index)).inverse()
								Transform move = com.neuronrobotics.bowlerstudio.physics.TransformFactory.nrToCSG(step)
								return toMfg
										.transformed(move)
										.rotx(90)
										.toXMin()
										.toZMin()
					})
		}
	}
	ArrayList<CSG> makeFinger(int index){
		int linksPer = 3
		double linkLen = fingerLength.getMM()/linksPer
		return makeDiget(linksPer,linkLen).collect{
			return it.transformed(getFingerLocation(index))
					.setManufacturing({ toMfg ->
						TransformNR step = com.neuronrobotics.bowlerstudio.physics.TransformFactory.csgToNR(getFingerLocation(index)).inverse()
						Transform move = com.neuronrobotics.bowlerstudio.physics.TransformFactory.nrToCSG(step)
						return toMfg
								.transformed(move)
								.rotx(90)
								.toXMin()
								.toZMin()
					})
		}
	}
	ArrayList<CSG> makeDiget(int numLinks,double linkLen){
		CSG midLinkHole = new Cylinder(0.7,0.7,thickness.getMM()*6,(int)10)
								.toCSG() 
								.movez(-thickness.getMM()*1.5)
								.rotx(90)
								.movex(-linkLen/2)
								.movez(radOfNuckel-3)
		midLinkHole=midLinkHole.union(midLinkHole.rotx(180))
		ArrayList<CSG> links = []
		for(int i=0;i<numLinks;i++){
			CSG link = makeLink(linkLen)
			Transform	location = new Transform()
			location.translateX(-linkLen*i)
			location.translateZ(	linkBoltCenter)	
			if(i%2!=0){
				
				link=makeFingerStop( link,midLinkHole,linkLen)
				//link=link.union(fingerStop)	
			}
			link=link
				.difference(midLinkHole)
				.transformed(location)
			links.add( link)
		}
		return links
	}

	CSG makeFingerStop(CSG link,CSG midLinkHole,double linkLen){
		double backstopOffset = 5
		CSG bolts = bolt.rotx(-90)
		bolts=bolts.union(bolts.movex(-linkLen))
		CSG linknuckel =new Cylinder(radOfNuckel+backstopOffset,radOfNuckel+backstopOffset,thickness.getMM()*2,(int)10)
								.toCSG() 
								.movez(-thickness.getMM()/2)
								
								.rotx(90)
								.movex(backstopOffset)
			
		CSG fingerStop = linknuckel
						.union(linknuckel.movex(linkLen))
						.hull()
			.toXMax()
			.movex(tendonOffset.getMM()/2+backstopOffset)

					
		CSG previous = link.rotz(180).roty(10)
				.union(link.rotz(180).roty(30))
				.union(link.rotz(180).roty(60))
				.union(link.rotz(180).roty(maxCurlAngle))
				.hull()
				//.movex(linkLen)
		CSG next = link.roty(-10)
				.union(link.roty(-30))
				.union(link.roty(-60))
				.union(link.roty(-maxCurlAngle))
				.hull()
				.movex(-linkLen)

		CSG cable =new Cylinder(0.8,0.8,linkLen+tendonOffset.getMM()*2,(int)6).toCSG() // a one line Cylinder
					.movez(-tendonOffset.getMM())
					.roty(90)
					.movez(-tendonOffset.getMM()/2-backstopOffset/3)
		cable=cable.union(cable.rotx(180))			
		fingerStop=fingerStop
				.difference(next)
				.difference(previous)
				.difference(midLinkHole)
				.difference(bolts)
				.difference(cable)
		return fingerStop
				
	}

	ArrayList<CSG> makePalm(){
		ArrayList<Transform> corners = []
		ArrayList<Transform> handLugs = digitLugs()
		
		//corners.addAll(handLugs)
		CSG corner = makeMountBase()
		CSG lug = makeMountLug()
		for (int i=0;i<numberFingers.getMM();i++){
			corners.add(getFingerLocation(i))
		}
		for (int i=0;i<numberThumbs.getMM();i++){
			corners.add(getThumbLocation(i))
		}
		for(Transform t:handLugs){
			corners.add(t)
		}
		ArrayList<CSG> parts = corners.collect{
			return corner.transformed(it)
		}
		parts.addAll(handLugs.collect{
			return corner.movex(-endOfBoard).transformed(it)
						
		})
		
		ArrayList<CSG> springBolts =handLugs.collect{
			return bolt.movex(-endOfBoard+220).transformed(it)
						
		}
		CSG boltOffset =makeMountLugBolts().toolOffset(printerOffset.getMM())
		CSG cutLug=lug
					.difference(boltOffset)
		ArrayList<CSG> lugs = corners.collect{
			return cutLug
					.transformed(it)
		}
		ArrayList<CSG> bolts=corners.collect{
					return makeMountLugBolts().transformed(it)
				}
		CSG strapSlot = new Cube(25.4,6.5,thickness.getMM()*4).toCSG()
						.movex(-extentionLength.getMM()-endOfBoard +10)
		CSG strapSlotBlank=	strapSlot.clone()
		for( int i=0;i<6;i++){
			strapSlot=strapSlot.union(	strapSlotBlank.movex(35*i))		
							
		}
		strapSlot=strapSlot.union(strapSlot.movey(lugRadius*8))
		
		CSG plate =CSG
				.unionAll(parts)
				.hull() 
				.difference(strapSlot)
		CSG intersectingParts = plate
				.intersect(bolts)
		plate=plate.difference(intersectingParts)
				.difference(springBolts)
				.movez(1)
				
		lugs.add(plate)
		return lugs
		
	}
	CSG makeCableLug(){
		if(cableLugCache==null){
			CSG cable =new Cylinder(0.8,0.8,80,(int)6).toCSG() // a one line Cylinder
			CSG housing =new Cylinder(2.8,2.8,20,(int)6).toCSG() // a one line Cylinder	
							.movez(-20)
			cable=cable.union(housing)
					
			cableLugCache=cable
		}
		return cableLugCache
	}
	
	CSG makeMountBase(){
		return  new Cube(lugRadius*2.5,lugRadius*2,thickness.getMM())
				//.cornerRadius(0.5)
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
							.difference(makeLink(0)
									.union(makeLink(0).roty(-30))
									.union(makeLink(0).roty(-60))
									.union(makeLink(0).roty(-maxCurlAngle))
									.hull()
									.toolOffset(printerOffset.getMM())
									//.movez(makeMountLugCache.getMaxZ())
									.movez(linkBoltCenter)
							)
							.difference(makeCableLug()
									.roty(90)
									.movez(linkBoltCenter+(tendonOffset.getMM()/2))
									.movex((tendonOffset.getMM()/2)+3)
									
							)
							.difference(makeCableLug()
									.roty(90)
									.movez(linkBoltCenter-(tendonOffset.getMM()/2))
									.movex((tendonOffset.getMM()/2)+3)
									
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
								.movez(lug.getMaxZ())
								.union(bolt
									.movey(-boltOffset)
									.movex(boltMeasurments.headDiameter)
									.movez(lug.getMaxZ())
								).union(bolt
									.rotx(90)
									.movey(lugRadius)
								)
								.movez(linkBoltCenter)
								
		}
		
		return makeMountLugBoltsCache
	}
	CSG makeLink(double length){
		if(length<boltMeasurments.headDiameter){
			length=boltMeasurments.headDiameter
		}
		if(linkCache.get(length)== null){
			
			
			CSG linknuckel =new Cylinder(radOfNuckel,radOfNuckel,thickness.getMM(),(int)10)
								.toCSG() 
								.rotx(90)
			
			CSG bolts = bolt.rotx(-90)
			bolts=bolts.union(bolts.movex(-length))
			CSG link = linknuckel
						.union(linknuckel
								.movex(-length))
						.hull()
						.difference(bolts)
						
						.movey(-thickness.getMM()/2)
			linkCache.put(length,link)
		}
		return linkCache.get(length)
	}
	ArrayList<CSG> makeParts(){
		ArrayList<CSG> parts = []
		for (int i=0;i<numberFingers.getMM();i++){
			parts.addAll(makeFinger(i))
		}
		for (int i=0;i<numberThumbs.getMM();i++){
			parts.addAll(makeThumb(i))
		}
		parts.addAll( makePalm())
		return parts
	}
}
new handMaker().makeParts()
//new handMaker().makeMountLug()