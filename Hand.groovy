

class handMaker{
	LengthParameter numberFingers 		= new LengthParameter("Number of Fingers",4,[8,1])
	LengthParameter numberThumbs 		= new LengthParameter("Number Thumbs",30,[130.0,2.0])
	StringParameter left 		= new StringParameter("Is Left hand","false",["false","true"])
	LengthParameter fingerWidth 		= new LengthParameter("Finger Width",30,[130.0,2.0])
	LengthParameter palmLength 		= new LengthParameter("Palm Lemgth",30,[130.0,2.0])
	LengthParameter fingerLength 		= new LengthParameter("Finger Length",30,[130.0,2.0])
	LengthParameter thumbLength 		= new LengthParameter("Thumb Length",30,[130.0,2.0])
	LengthParameter fingerDelta 		= new LengthParameter("Finger Delta",30,[130.0,2.0])

	
	Transform getFingerLocation(int index){
		
	}
	Transform getThumbLocation(int index){
		
	}
	
	ArrayList<CSG> makeThumb(){
		
	}
	ArrayList<CSG> makeFinger(){
		
	}

	ArrayList<CSG> makePalm(){
		
	}
	CSG makeMountLug(){
		
	}
	CSG makeLink(double length){
		
	}
	ArrayList<CSG> makeParts(){
		ArrayList<CSG> parts = []
		
		for (int i=0;i<numberFingers.getMM();i++){
			
		}
		for (int i=0;i<numberThumbs.getMM();i++){
			
		}
		
		return parts
	}
}
new handMaker().makeParts()