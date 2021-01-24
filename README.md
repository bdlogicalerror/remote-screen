##How to Run


* Clone the repository

```
$ git clone https://github.com/bdlogicalerror/remote-screen
```

* Go to the bin folder

```
$ cd ScreenSharer/bin/
```

* Run the Application

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