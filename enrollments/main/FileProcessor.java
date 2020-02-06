package enrollments.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import enrollments.pojos.InsuranceCompany;
import enrollments.pojos.User;

public class FileProcessor {
private String _fileName;
private String _baseOutputDirectory;
private final String _delimiter = ",";
private boolean _firstLine = true;

	FileProcessor(String fileName, String baseOutputDirectory){
		_fileName = fileName;
		_baseOutputDirectory = baseOutputDirectory;
	}
	
	public boolean runIt(){
		BufferedReader br = null;
		ArrayList<User> users = new ArrayList<User>();
		User userIndexes = new User();
		Collection<String> insuranceCompanies =  new HashSet<String>();
		
		try{
	        String line = "";
	        br = new BufferedReader(new FileReader(_fileName));
            while ((line = br.readLine()) != null) {
            	if(_firstLine){//set the position of data by given header line
            		String[] properties = line.split(_delimiter);
            		int index = 0;
            		for(String prop : properties){
            			/*
            			 * For later use, implement additional loop with fields to not hard code switch. Become dependent on class and file harmony only
            			 * ex: Field[] fields = User.class.getDeclaredFields();
            			 * 	   fields[0].getName().toString()
            			*/
	            		switch(prop){
	            		case "userId":
	            			userIndexes.userIdIndex = index;
	            			break;
	            		case "firstName":
	            			userIndexes.firstNameIndex = index;
	            			break;
		            	case "lastName":
		            		userIndexes.lastNameIndex = index;
		        			break;
			            case "version":
			            	userIndexes.versionIndex = index;
			    			break;
			            case "insuranceCompany":
			            	userIndexes.insuranceCompanyIndex = index;
			            	break;
	            		}
	            		index++;
            		}
            		_firstLine = false;
            	}else{
            		String[] userLine = line.split(_delimiter);
            		User tempUser = new User();
            		/*
            		 * Loop fields here again for dynamic
            		*/
            		tempUser.userId = userLine[userIndexes.userIdIndex];
            		tempUser.firstName = userLine[userIndexes.firstNameIndex];
            		tempUser.lastName = userLine[userIndexes.lastNameIndex];
            		tempUser.version = Integer.valueOf(userLine[userIndexes.versionIndex]);
            		tempUser.insuranceCompany = userLine[userIndexes.insuranceCompanyIndex];
            		users.add(tempUser);
            		
            		//save unique insurance companies
            		insuranceCompanies.add(userLine[userIndexes.insuranceCompanyIndex]);
            	}

            }
            
            
            //split up into multiple files
            ArrayList<InsuranceCompany> insuranceFiles = new ArrayList<InsuranceCompany>();
            for(String comp : insuranceCompanies){
            	InsuranceCompany company = new InsuranceCompany();
            	company.insuranceName = comp;
            	company.fileName = "insuranceUsers_"+comp;
            	
            	//grab the users of same company
            	for(User u : users){
            		if(comp.compareTo(u.insuranceCompany)==0){
            			company.users.add(u);
            		}
            	}
            	insuranceFiles.add(company);
            }
            
            //sort, remove duplicates and print to files
            for(InsuranceCompany comp : insuranceFiles){           	
            	Collections.sort(comp.users, new Comparator<User>() {
            	    @Override
            	    public int compare(User one, User two) {
            	        return one.lastName.compareTo(two.lastName)>0 ? 1 : one.lastName.compareTo(two.lastName)<0 ? -1 : 0;
            	    }
            	});
            	
            	//remove duplicate users, select highest version
            	User previousUser = comp.users.get(0);
            	ArrayList<User> tempUsers = comp.users;
            	if(comp.users.size()>1){
	            	for(int u=1;u<comp.users.size();u++){
	            		if(comp.users.get(u).userId.compareTo(previousUser.userId)==0){
	            			if(comp.users.get(u).version>previousUser.version)
	            				tempUsers.remove(previousUser);
	            			else
	            				tempUsers.remove(comp.users.get(u));
	            		}
	            		previousUser = comp.users.get(u);
	            	}
	            }
            	comp.users = tempUsers;
            	
            	PrintWriter writer = new PrintWriter(_baseOutputDirectory+comp.fileName+".csv", "UTF-8");
            	for(User u : comp.users){
            		writer.println(u.lastName+"\t"+u.firstName+"\t"+u.userId+"\t"+u.version);
            	}
            	writer.close();
            }
            
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		finally{
			try{
                br.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
		}
		return true;
	}
	
	
}
