# Distributed Auction

Group Project 4 for cs 351.
Group:
Galen Hutchison
Ashley Krattiger
Ryan Cooper

## Project Structure
  ![image](Resources/Bank.png)
## Message System
### Error Codes
Error codes can be found on a message with FAILURE response
The accountID field will be sent with the error code  

 - -999 => Invalid Lookup  
 - -888 => Insufficient Funds 

## Bank Server 
#### Galen Hutchison
<b>PORT: 6000</b>  
This is the main server for the program. It houses the Database and accepts concurrent connections

## Auction Server
#### Ryan Cooper
![image](Resources/AuctionDiagram.png)
Command line input int Auction Port, int Bank Port, String bankIp, String auction name
This is the main class fot the Auction server. 3 main threads. AuctionServer accepts input
from agents. BankActions & AgentAction are spawned from AuctionServer and perform bid and other 
actions for them. CountDown handles win conditions and auction list creation and maintenance.

## Agent Server
#### Ashley Krattiger
![image](Resources/AgentDiagram.png)
Agent takes no command line input. Agent interface works entirely through the GUI and prints
a log of the messages passed to and from the servers on the console. Agent will start by prompting
you to enter the Bank's IP and port into a pop up window, and to choose whether to open a new Bank 
account or log in with an existing account. Pressing either button changes the pop up to display
more fields to let you get started with your account. Once you are logged in, the Agent will open
connections to the Bank and, through connection requirements provided by the Bank, the open Auction
Houses. The starting screen of the main GUI window features buttons allowing you to check your Bank
balance and deposit funds into your account. Buttons will also appear in the center displaying the
names of each open Auction House on its own button. Pressing an Auction button takes you to a new
screen. On the second screen, there are buttons allowing you to check the bank balance, go back to
the main screen, and place a bid. In the center of the second screen, images will show up displaying
the items that are up for auction in that particular Auction House. If you click on a picture, it
will highlight in yellow and it counts as you having selected that item for bidding. You can only
bid on an item if you have one of the pictures selected. On both main screens, along the right side
there is a message log that displays all the messages from the server including updates on active
auctions you are participating in. To exit safely, close the program by pressing "X" on the GUI.

### Usage
Once connected, the bank accepts Messages which can be constructed using the shared Message class
Messages can be sent using the ObjectOutputStream:
~~~
ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
out.writeObject(message);
~~~
#### Commands

  
<b>OPENACCOUNT </b>     
arguments: Name (the user name), <i>optional: "auction" "port number"</i>  
Example:  
open a new user account
~~~
Message openAccountRequest = new Message.Builder()
                                        .command(Message.Command.OPENACCOUNT)
                                        .accountName("Daisy")
                                        .nullId();
~~~

<b>REGISTERHOUSE</b>  
open a new AuctionHouse account
requires: accountName, connectionReqs
Please generate a list with you ip and open port as the first element
~~~
Message newAhRequest = new Message.Builder()
                .command(Message.Command.REGISTERHOUSE)
                .accountName("AH-100")
                .connectionReqs({Your Connection Info})
                .nullId();
~~~

<b>LOGIN</b>   
requires: senderID
Use this command if you have already Created an account
and wish to use that entry in the DB
~~~
Message loginRequest = new Message.Builder()
                .command(Message.Command.LOGIN)
                .senderId(1);
~~~
RESPONSE: connectionReqs = connectionReqs of all active banks

<b>GETBALANCE</b>  
requires: accountId
returns the current balance of an active client. Failure response if client
is not in active session

RESPONSE: balance = requested balance, accountID = client id

<b>BLOCK</b>  
requires: accountId, balance
Will block the client of accountId to the amount of balance
return: SUCCESS if successful, FAILURE if client doesnt have enough funds
err= -888  
  
<b>DEPOSIT</b>
requires: accountId, balance

<b>TRANSFER</b>
requires: accountId, senderId, balance
transer balance from accountId to senderId. Will transer 0 if accountID does
not have funds

<b> DEREGISTER</b>    
Make sure to deregister before logging out. 
requires: senderID


 ## DatabaseServer
 <b>PORT: 6002 </b>
 Only the bank and Auction Houses should talk to Database
 It accepts DBMessage
 
 ### Commands
 
 <b> GET ITEM</b>  
 COMMAND: GET TABLE: ITEMS
 Requires: id

### Setting up the Database
add sqlite-jdbc-3.21.0.21.jar to classpath available from intellj package manager 
or on [their repo](https://github.com/xerial/sqlite-jdbc)

The run the script provided in the Database package called dbinitializer

voila! Your database should be good to go.

### Setting up on School computers
Computers
moons
trucks
gigs.cs.unm.edu
shuttle.cs.unm.edu
b146-[1-100]

use command:
~~~
dig +short myip.opendns.com @resolver1.opendns.com
~~~
to get ip address

### Known Issues
When multiple Agents are running, the Auction House will only send messages to one Agent