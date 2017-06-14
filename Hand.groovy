

class handMaker{
	LengthParameter numberFingers 	= new LengthParameter("Number of Fingers",4,[8,1])
	LengthParameter numberThumbs 		= new LengthParameter("Number Thumbs",1,[2,0])
	StringParameter left 			= new StringParameter("Is Left hand","false",["false","true"])
	LengthParameter PalmWidth 		= new LengthParameter("Palm Width",90,[130.0,2.0])
	LengthParameter palmLength 		= new LengthParameter("Palm Length",123,[130.0,2.0])
	LengthParameter fingerLength 		= new LengthParameter("Finger Length",106,[130.0,2.0])
	LengthParameter thumbLength 		= new LengthParameter("Thumb Length",77,[130.0,2.0])
	LengthParameter fingerDelta 		= new LengthParameter("Finger Delta",7,[50,0])
	LengthParameter thumbAngle 		= new LengthParameter("Thumb Angle",46,[130.0,2.0])
	LengthParameter fingerSpread 		= new LengthParameter("Finger Spread Angle",5,[20,0])
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.15,[25.4,1])
	CSG makeMountLugBoltsCache = null
	CSG makeMountLugCache = null
	
	Transform getFingerLocation(int index){
		
	}
	Transform getThumbLocation(int index){
		
	}
	
	ArrayList<CSG> makeThumb(int index){
		
	}
	ArrayList<CSG> makeFinger(int index){
		
	}

	ArrayList<CSG> makePalm(){
		ArrayList<Transform> corners = []
		
		for (int i=0;i<numberFingers.getMM();i++){
			corners.add(getFingerLocation(i))
		}
		for (int i=0;i<numberThumbs.getMM();i++){
			corners.add(getThumbLocation(i))
		}
		
	}
	CSG makeMountLug(){
		if(makeMountLugCache == null){
			
		}
		return makeMountLugCache
	}
	CSG makeMountLugBolts(){
		if(makeMountLugBoltsCache == null){
			
		}
		return makeMountLugBoltsCache
	}
	CSG makeLink(double length){
		
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
new handMaker().makeParts()