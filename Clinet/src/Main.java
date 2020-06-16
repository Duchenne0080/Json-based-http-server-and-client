import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.json.simple.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		// BufferedReader in = null;
		// BufferedWriter out = null;
		BufferedReader wt = null;
		String command = "";
		String host = "";
		int port = 0;
		String input = null;
		JSONObject json = new JSONObject();
		do 
		{
			Scanner scan = new Scanner(System.in);
			input = scan.nextLine();
		} 
		while (!validation(input));
		System.out.println("Connecting......");
		String[] a = input.split(" ");
		command = a[0];
		host = a[1];
		port = Integer.parseInt(a[2]);
		Socket socket = new Socket(host, port);
		System.out.println("Success!");
		DataInputStream in1 = new DataInputStream(socket.getInputStream());
		DataOutputStream out1 = new DataOutputStream(socket.getOutputStream());
		wt = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String string = wt.readLine();
			if (!string.equals("DISCONNECT")) {
				while (!test_command(string)) {
					string = wt.readLine();
				}
				json = Encode(string);
				out1.write((String.valueOf(json)+"\n").getBytes("UTF-8"));
				out1.flush();
				String REC = in1.readLine();
				decode(REC);
			} else {
				json = Encode(string);
				out1.write((String.valueOf(json)+"\n").getBytes("UTF-8"));
				out1.close();
				System.out.println("Disconnected!");
				in1.close();
				socket.close();
				break;
			}
		}
	}

	public static boolean validation(String test)
	// To test the command and port number
	{
		String[] a = test.split(" ");
		int port = 0;
		if (a.length != 3) {
			System.out.println("Wrong input");
			return false;
		}
		String com = a[0];
		String host = a[1];

		try {
			port = Integer.parseInt(a[2]);
		} catch (Exception e) {
			System.out.println("Wrong port");
			return false;
		}

		if (com.equals("connect") && port <= 65536 && ip_address(host)) {
			System.out.println("good command");
			return true;
		}
		System.out.println("Unknown error");
		return false;
	}

	public static boolean ip_address(String ip)
	// to validate the IPaddress
	{
		if (ip.length() < 7 || ip.length() > 15) {
			return false;
		}
		String[] arr = ip.split("\\.");
		if (arr.length != 4) {
			return false;
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < arr[i].length(); j++) {
				char temp = arr[i].charAt(j);
				if (!(temp > '0' && temp < '9')) {
					return false;
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			int temp = Integer.parseInt(arr[i]);
			if (temp < 0 || temp > 255) {
				return false;
			}
		}
		return true;
	}

	public static JSONObject Encode(String command) throws IOException
	// Encode your command into JSON message
	{
		JSONObject object = new JSONObject();
		if (command.equals("DISCONNECT")) 
		{
			object.put("message", "request");
			object.put("type", "DISCONNECT");
			StringWriter out = new StringWriter();
			object.writeJSONString(out);
			String jsonText = out.toString();
			System.out.println(jsonText);
			FileWriter fw = new FileWriter("msg.txt");
			fw.write(jsonText);
			fw.close();
			return object;

		}
		String[] Str = command.split(" ");

		if (Str[0].equals("PUT")) {
			object.put("message", "request");
			object.put("type", Str[0]);
			object.put("target", Str[2]);
			File file = new File(Str[1]);
			Reader input = new FileReader(file);
			BufferedReader bf = new BufferedReader(new FileReader(file));
			String con = "";
			StringBuilder sb = new StringBuilder();
			while (con != null) {
				con = bf.readLine();
				if (con == null) {
					break;
				}
				sb.append(con.trim());
			}
			bf.close();
			object.put("content", sb.toString());
			StringWriter out = new StringWriter();
			object.writeJSONString(out);
			String jsonText = out.toString();
			FileWriter fw = new FileWriter("msg.txt");
			fw.write(jsonText);
			fw.close();
			return object;
		} else {
			String com = Str[0];
			String path = Str[1];
			object.put("message", "request");
			object.put("type", com);
			object.put("target", path);
			StringWriter out = new StringWriter();
			object.writeJSONString(out);
			String jsonText = out.toString();
			FileWriter fw = new FileWriter("msg.txt");
			fw.write(jsonText);
			fw.close();
			return object;
		}
	}

	public static void decode(String code)
	// Decode the JSON message and print content
	{
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(code);
			JSONObject jsonObject = (JSONObject) obj;
			String message = (String) jsonObject.get("message");
			String statuscode = (String) jsonObject.get("code");
			String content = (String) jsonObject.get("content");
			System.out.println(content);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static boolean test_command(String test)
	// To test the command in GET/PUT/DELETE/DISCONNECT
	{
		String[] Str = test.split(" ");
		if (Str.length > 3) {
			System.out.println("illigal command");
			return false;
		}

		if (!Str[0].equals("GET") && !Str[0].equals("PUT") && !Str[0].equals("DELETE")
				&& !Str[0].equals("DISCONNECT")) {
			System.out.println(Str[0] + " illigal command");
			return false;
		} else {
			return true;
		}
	}
}
