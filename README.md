**Remote Screen Sharing (Local Network)**

##How to Run  

* Clone the repository

  

```

$ git clone https://github.com/bdlogicalerror/remote-screen

```

  

 - Go to the bin folder

  

```

$ cd remote-screen/bin/

```

**2 ways to run application**  

* Interactive Application Mode

  

```

$ java ScreenShare

```

  

- For the server, type server followed by a port, (any integer between 1024 and 65534 should work)

  

- Add Monitor Number

```

localhost>>> server 10000 1

```

  

- For the client, type client followed by server address and server port

```

localhost>>> client 127.0.0.1 10000

```

 **you can pass params via command line arguments**
 

 - for run server [server] [port] [monitor_id]

 ```
 $ java ScreenShare server 10000 1
 ```

 - for run client [client] [port]

 ```
 $ java ScreenShare client 127.0.0.1 1000
 ```
