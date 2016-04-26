package VCHK.VCheckerBwr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import model.art;
import model.artifactList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VCheckerBwr {
	public static void main(String[] args) {    	
    	String file="./"+args[0];
    	CheckVersions(file);    	
	}
	
	private static void CheckVersions(String file) {
		boolean check=false;
		ObjectMapper mapper = new ObjectMapper();			
		artifactList al=null;
		try {
			al = mapper.readValue(new File(file), artifactList.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//We construct an iterator with al to process all the artifacts contained in it
		Iterator<art> it=al.getArtifacts().iterator();
		
		while(it.hasNext()){
			art artefacto=it.next();
			if(artefacto.getServer().equals("bower.io")){
				check=CheckVersionBwr(artefacto);
			}
			if(!check){
				 System.out.println("The artifact "+artefacto.getArtifact()+", version "+artefacto.getVersion()
						 +" is notlocated in "+artefacto.getServer());
			}else{
				System.out.println("The artifact "+artefacto.getArtifact()+", version "+artefacto.getVersion()
						 +" is located in "+artefacto.getServer());
			}
		}
	}

	/**
	 * @param artefacto that is the artifact object which we'll check
	 * @return	boolean that is true if the version of the artifact is in the Archiva url repository
	 */
	private static boolean CheckVersionBwr(art artefacto) {
		boolean result=false;
		
		String cmd="bower info "+artefacto.getArtifact()+"#"+artefacto.getVersion();
		Process process=null;
		InputStream is=null;
		
	    try {
	    	process=Runtime.getRuntime().exec(cmd);	
	    	System.out.println("consulta ejecutada");
			is=process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder out = new StringBuilder();
		    String line;
		    String json="";
			while ((line = reader.readLine()) != null) {
			        out.append(line);			       
			}
			json=json+out.toString();
			if(json.equals(""))
				return false;
			
			System.out.println(json);  //Prints the string content read from input stream
		    reader.close();
		    ArrayList<String>palJson=ObtenerPalabrasJson(json);
		    if(artefacto.getArtifact().equals(palJson.get(palJson.indexOf("name:")+1))){		    	
		    	if(artefacto.getVersion().equals(palJson.get(palJson.indexOf("version:")+1))){
		    		return true;
		    	}		    		    	
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return result;
	}
	

	private static ArrayList<String> ObtenerPalabrasJson(String json) {
		ArrayList<String>result=new ArrayList<String>();
		//We have the information from npmjs.org in the string json
	    String delimiter=",{}'[]";
	    StringTokenizer st=new StringTokenizer(json,delimiter);
	    while(st.hasMoreTokens()){
	    	result.add(st.nextToken().trim());
	    }
		return result;
	}
}