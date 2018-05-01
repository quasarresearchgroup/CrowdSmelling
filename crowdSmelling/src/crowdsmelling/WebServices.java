package crowdsmelling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.MessageDialog;
import org.json.simple.JSONObject;

public class WebServices {
	
	@SuppressWarnings("unchecked")
	public String  writePost2Mysql(String username,String csType,String model_ML, String project_cs,String package_cs,String class_cs,String method_cs,double LOC,double CYCLO,boolean codesmellDetected,boolean iscodesmell) {	
		String idRow = null;
		String url="http://crowdsmelling.com/webservices/ws.php";
		try {
			URL object=new URL(url);
	
			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestMethod("POST");
	
			JSONObject metrics   = new JSONObject();

			metrics.put("username", username);
			metrics.put("csType", csType);
			metrics.put("model_ML", model_ML);
			metrics.put("project", project_cs);
			metrics.put("package", package_cs);
			metrics.put("class",class_cs);
			metrics.put("method", method_cs);
			metrics.put("LOC", LOC);
			metrics.put("CYCLO", CYCLO);
			metrics.put("codesmellDetected", codesmellDetected);
			metrics.put("iscodesmell", iscodesmell);
	
			OutputStream os = con.getOutputStream();
			os.write(metrics.toString().getBytes("UTF-8"));
			os.close();
			
			//display what returns the POST request
	
			StringBuilder sb = new StringBuilder();  
			int HttpResult = con.getResponseCode(); 
			if (HttpResult == HttpURLConnection.HTTP_OK) {
			    BufferedReader br = new BufferedReader(
			            new InputStreamReader(con.getInputStream(), "utf-8"));
			    String line = null;  
			    while ((line = br.readLine()) != null) {  
			        sb.append(line + "\n");  
			    }
			    br.close();
			    idRow = sb.toString();//for method return
			   // MessageDialog.openInformation(null,"CrowdSmelling","" + sb.toString());    
			} else {
				MessageDialog.openInformation(null,"CrowdSmelling","" + con.getResponseMessage());
			}  
			 con.disconnect();	 
		} catch (MalformedURLException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return idRow;
	}

	
	@SuppressWarnings("unchecked")
	public  void writePut2Mysql(int id, byte iscodesmell) {	
		String url="http://crowdsmelling.com/webservices/ws.php/metrics/"+id;
		try {
			URL object=new URL(url);
	
			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestMethod("PUT");
	
			JSONObject metrics   = new JSONObject();
			
			//metrics.put("id", id);
			metrics.put("iscodesmell", iscodesmell);
			//MessageDialog.openInformation(null,"CrowdSmelling","" +id+" |"+iscodesmell + " | "+ metrics);	
			OutputStream os = con.getOutputStream();
			os.write(metrics.toString().getBytes("UTF-8"));
			os.close();
			
			//display what returns the POST request
	
			StringBuilder sb = new StringBuilder();  
			int HttpResult = con.getResponseCode(); 
			if (HttpResult == HttpURLConnection.HTTP_OK) {
			    BufferedReader br = new BufferedReader(
			            new InputStreamReader(con.getInputStream(), "utf-8"));
			    String line = null;  
			    while ((line = br.readLine()) != null) {  
			        sb.append(line + "\n");  
			    }
			    br.close();
			    //MessageDialog.openInformation(null,"CrowdSmelling","" + sb.toString());
			} else {
				MessageDialog.openInformation(null,"CrowdSmelling","" + con.getResponseMessage());
			}  
			 con.disconnect();
		
		} catch (MalformedURLException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
