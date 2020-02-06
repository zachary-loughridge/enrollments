package enrollments.main;

public class EnrollmentsProcessor {

	public static void main(String[] args) {
		//some basic validation checking
		if(args.length == 0 || args == null){
			System.out.println("Please speicfy a file");
		}else if(args[0].toLowerCase().endsWith(".edi")){
			System.out.println("Invalid file type, csv only");
		}else{
			//good passed, run the file
			System.out.println("Good file, continue processing");
			FileProcessor fp = new FileProcessor(args[0], args[1]);
			if(fp.runIt()){
				System.out.println("Successfully completed");
			}else{
				System.out.println("Error occured via processing");
			}
		}

	}

}
