It is a json based http protocol
Server can run with command: javac -jar server -p [Port number]
When you run the client, you should first connect to the server with "connect [IP address] [Port Number]"
Client provide 4 function:
1.Get
Users can get a file from the server
i.e.
  GET test.txt
2.Put
Users can upload a file to the server
i.e.
  PUT test.txt a/b/c/test.txt
3.Delete
Users can delete a file in the server
i.e.
  Delete a/b/c/test.txt
4.Disconnect
Disconnect to the server
i.e.
  Disconnect
