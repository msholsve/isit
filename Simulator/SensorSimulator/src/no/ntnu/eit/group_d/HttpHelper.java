package no.ntnu.eit.group_d;

import java.awt.List;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public final class HttpHelper  {

	public static void postHttp(String request, String requestParameters) throws IOException {
		// http://stackoverflow.com/a/4206094 
		
		byte[] postData       = requestParameters.getBytes( StandardCharsets.UTF_8 );
		int    postDataLength = postData.length;
		URL    url            = new URL( request );
		HttpURLConnection conn= (HttpURLConnection) url.openConnection();      
		
		conn.setDoOutput( true );
		conn.setInstanceFollowRedirects( false );
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
		conn.setRequestProperty( "charset", "utf-8");
		conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
		conn.setUseCaches( false );
		
		try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
		   wr.write( postData );
		}
		
	}
	
	public static void patchHttp(String baseUrl, ByteArrayOutputStream data) {
		HttpURLConnection conn;
		
		try {						
			int    postDataLength = data.size();
			URL    url            = new URL(baseUrl);
			
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
			conn.setRequestMethod("POST");
			conn.setDoOutput( true );
			conn.setInstanceFollowRedirects( false );
			conn.setRequestProperty( "Content-Type", "application/json"); 
			conn.setRequestProperty( "charset", "utf-8");
			conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
			conn.setUseCaches( false );
			
			DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
			wr.write(data.toByteArray());
			
			DataInputStream is = new DataInputStream(conn.getInputStream());
			
			int count = is.available();
			byte[] returnBytes = new byte[count];
			is.read(returnBytes);
			
			String str = new String(returnBytes, StandardCharsets.UTF_8);

			str.length();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String getHttp(String request) throws IOException {
		// http://stackoverflow.com/a/4206094 
		
		StringBuilder result = new StringBuilder();
	      URL url = new URL(request);
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("GET");
	      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	      String line;
	      while ((line = rd.readLine()) != null) {
	         result.append(line);
	      }
	      rd.close();
	      return result.toString();
		
	}
	
}
