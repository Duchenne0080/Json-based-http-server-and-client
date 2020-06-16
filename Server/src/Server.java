import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Server implements Runnable{

	public static void main(String[] args) throws Exception
	{
		CommandLineValues values = new CommandLineValues();
		CmdLineParser parser = new CmdLineParser(values);
		ServerSocket serverSocket = null;
		try{
			// parse the command line options with the args4j library
			parser.parseArgument(args);
			// print values of the command line options
			System.out.println(values.getPort());
			
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
			System.exit(-1);
		}
		try {
			//Server is listening on port 4444
			serverSocket = new ServerSocket(values.getPort());
			System.out.println("Server is listening...");
			//((((((( For the Assignment1 you should read and write your messages according to following rules: 
			//Messages are supposed to be finished by '\n' in the project
			//msg = in.readLine();
			//out.write((String.valueOf(obj)+"\n").getBytes("UTF-8"));      )))))))

			while (true) 
			{
				//Server waits for a new connection
				Socket socket = serverSocket.accept();
				// Java creates new socket object for each connection.
				
				System.out.println("Client Connected...");
				
				// A new thread is created per client
				Thread server = new Thread(server(socket));
				// It starts running the thread by calling run() method
				server.start();
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null)
				serverSocket.close();
		}
	}
	public static Runnable server(Socket socket) throws Exception{
        //BufferedReader in = null;
        //BufferedWriter out = null;
        BufferedReader br = null;
		DataInputStream in = null;
		DataOutputStream out = null;
        JSONParser parser = new JSONParser();
        String command = "";
        String path = "";
        String content = "";
        try {
           
			//ServerSocket server = new ServerSocket(8888);
            //System.out.println("Server is up!!!!!");
            //socket = server.accept();
        	rootfolder();
        	in = new DataInputStream(socket.getInputStream());
        	out = new DataOutputStream(socket.getOutputStream());
        	//in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //br = new BufferedReader(new InputStreamReader(System.in));
            while (true) 
            {
                String str = in.readLine();
                System.out.println("Rec:" + str);
                Object obj = parser.parse(str);
	    		JSONObject jsonObject = (JSONObject) obj;
                command = (String) jsonObject.get("type");
                path = (String) jsonObject.get("target");
                content = (String) jsonObject.get("content");
                JSONObject json = null;
                if(command.equals("GET"))
                {
                	json = get(path);
                	out.write((String.valueOf(json)+"\n").getBytes("UTF-8"));
                    out.flush();
                }
                if(command.equals("PUT"))
                {
                	json = put(path,content);
                	out.write((String.valueOf(json)+"\n").getBytes("UTF-8"));
                    out.flush();
                }
                if(command.equals("DELETE"))
                {
                	json = Delete(path);
                	out.write((String.valueOf(json)+"\n").getBytes("UTF-8"));
                    out.flush();
                }              
                if (command.equals("DISCONNECT"))
                {
                	//out.writeChars("Disconnected!"+"\n");
                    out.flush();
                	break;
                }
                if(!command.equals("DISCONNECT")&&!command.equals("GET")&&!command.equals("PUT")&&!command.equals("DELETE")&&command != null)
                {
                	json = Encode("401","Bad Request");
                	out.write((String.valueOf(json)+"\n").getBytes("UTF-8"));
                    out.flush();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		return null;
    }
	public static JSONObject Encode(String code,String c) throws IOException
	{

		JSONObject object = new JSONObject();
		object.put("message", "response");
		object.put("code",code);
		object.put("content",c);
		StringWriter out = new StringWriter();
		object.writeJSONString(out);
		String jsonText = out.toString();
		System.out.println(jsonText);
		FileWriter fw = new FileWriter("msg.txt");
		fw.write(jsonText);
		fw.close();
		return object;
	}
	public void decode() 
    {
   	 JSONParser parser = new JSONParser();
   	 try
   	 {
   		 Object obj = parser.parse(new FileReader("msg.txt"));
   		 JSONObject jsonObject = (JSONObject) obj;
   		 String message = (String) jsonObject.get("message");
   		 System.out.println(message);
   		 String type = (String) jsonObject.get("type");
   		 System.out.println(type); 
   		 String path = (String) jsonObject.get("target");
   		 System.out.println(path);
   		 String content = (String) jsonObject.get("content");
   		 System.out.println(content);
   	 } 
   	 catch (FileNotFoundException e) 
   	 {
   		 e.printStackTrace();
   	 } 
   	 catch (IOException e) 
   	 {
   		 e.printStackTrace();
   	 } 
   	 catch (ParseException e) 
   	 {
   		 e.printStackTrace();
   	 }
    }
    public static JSONObject get(String path) throws IOException
    {
    	File file = new File(path);
        if(file.exists())    
        {           
        	BufferedReader bf = new BufferedReader(new FileReader(file));
        	String con = "";
        	StringBuilder sb = new StringBuilder();
        	while(con != null)
        	{
        		con = bf.readLine(); 
        		if(con == null)
        		{
        			break;
        		}    
        		sb.append(con.trim());
        	}
        	bf.close();
        	return Encode("200",sb.toString());
       }
        else
        {
        	return Encode("404","NOT FOUND");
        }
    }

    
    public static JSONObject put(String path, String content) throws Exception
    {
    	File file = new File(path);
        if(!file.exists())
        {
        	file.createNewFile();
        	FileWriter writer = new FileWriter(file);
        	writer.write(content);
        	writer.flush();
            writer.close();
        	return Encode("201","OK");
        }
        else
        {
        	FileWriter writer = new FileWriter(file);
        	writer.write(content);
        	writer.flush();
            writer.close();
        	return Encode("202","Modifed");
        }
    }
    
    public static JSONObject Delete(String path) throws IOException
    {
    	File file = new File(path);
    	if(!file.exists())
    	{
    		return Encode("404","NOT FOUND");
    	}
    	else
    	{
    		file.delete();
    		return Encode("203","OK");
    	}
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	public static void rootfolder()
	{
		File file = new File("www");
		if(!file.exists())
        {
        	file.mkdir();
        }
	}
}