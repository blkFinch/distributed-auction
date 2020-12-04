# Distributed Auction

Group Project 4 for cs 351.
Group:
Galen Hutchison
Ashley Krattiger
Ryan Cooper

## Bank Server
This is the main server for the program. It houses the Database and accepts concurrent connections

### Usage
Once connected, the bank accepts Messages which can be constructed using the shared Message class
Messages can be sent using the ObjectOutputStream:
~~~
ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
out.writeObject(message);
~~~
#### Commands
<b> Creating a New User </b>
  
command: OPENACCOUNT   
arguments: Name (the user name), <i>optional: "auction" "port number"</i>  
Example:  
open a new user account
~~~
Message openAccountRequest = new Message.Builder()
                                        .command(Message.Command.OPENACCOUNT)
                                        .accountName("Daisy")
                                        .nullId();
~~~

open a new AuctionHouse account

~~~
Message newAhRequest = new Message.Builder()
                .command(Message.Command.OPENACCOUNT)
                .accountName("AH-100")
                .arguments(new String[]{"auction","8000"})
                .nullId();
~~~

### Setting up the Database
add sqlite-jdbc-3.21.0.21.jar to classpath available from intellj package manager 
or on [their repo](https://github.com/xerial/sqlite-jdbc)

The run the script provided in the Database package called dbinitializer

voila! Your database should be good to go.