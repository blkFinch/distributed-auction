# Distributed Auction

Group Project 4 for cs 351.
Group:
Galen Hutchison
Ashley Krattiger
Ryan Cooper

## Bank Server
This is the main server for the program. It houses the Database and accepts concurrent connections

### Usage
<b> Creating a New User </b>
When you connect to the bank you will be prompted to enter a user id or enter 0
to create a new user you will then be prompted to enter a string:

~~~
name:isAuction(y or n):starting balance:open port(0 for null)
~~~

For example:

~~~
fred:n:600:0
~~~
Will open a new account with Name: Fred and Balance:600
while:

~~~
DelewareBank:y:0:8004
~~~
Will open an auction house account with the available port 8004

### Setting up the Database
add sqlite-jdbc-3.21.0.21.jar to classpath available from intellj package manager 
or on [their repo](https://github.com/xerial/sqlite-jdbc)

The run the script provided in the Database package called dbinitializer

voila! Your database should be good to go.