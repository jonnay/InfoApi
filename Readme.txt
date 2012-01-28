                                Readme
                                ======

Author: Jonathan Arkell
Date: 2012-01-28 15:43:46 MST


Table of Contents
=================
1 InfoApi V 4.0.0(JA)
2 Documentation
    2.1 Existing API Endpoints
    2.2 Coming Soon
    2.3 Larger Features Coming Soon
    2.4 Config File
    2.5 Installation 
        2.5.1 From the original doc:
    2.6 Communicating with the API
    2.7 Example Usage PHP:
3 Development
    3.1 What InfoApi Desperately needs that I cannot provide?
    3.2 What are .org files?
    3.3 Building
    3.4 Building your own plugin
        3.4.1 Resource file inside of jar called endpoint.info in main directory. contains:


1 InfoApi V 4.0.0(JA)
---------------------

2 Documentation
---------------


Info APi works alright, but there are some tweaks that could be done to improve it.  This is my attempt.

each request needs its secret key at the end like: 
[http://server:25577/version?secret]


2.1 Existing API Endpoints
==========================
   One day, an options request on / will return this info.  But not today.

   - GET /version - Mostly an example/test.  Spits out the version of the server.
   - OPTIONS /info - return server information.
     - GET /info/version
     - GET /info/ram
     - GET /info/onlinemode
     - GET /info/maxplayers
     - GET /info/plugins
   - POST /cmd - Execute a command on the server.  Right now it is POST /cmd/your/command/here like /give/jonnay23/diamond/512
   - OPTIONS /player 
     - GET /player/online - List all online player
     - GET /player/count - List the number of players
     - GET /player/count/<world> - List number of players in that world
     - GET /player/world/<world> - List all players in that world
     - GET /player/info/<playername> - Get player info for the player

2.2 Coming Soon
===============
   These endpoints are currently being worked on
   - POST /player/<playername>/<action> 
   - GET /world - List all available worlds
   - GET /world/<world>/feature/<feature> - get information about a particular feature in a world

2.3 Larger Features Coming Soon
===============================
   - Using request entities for POST/PUT/DELETE operations!
   - More and better information from an OPTIONS request!
   - different content types, with a confiurable default!  Output info in
     - plain text!
     - json!
     - yaml!
     - xml, if you're effing crazy!
   - better integration with other plugins!

2.4 Config File
===============


  # Horribly insecure password here.  FOR THE LOVE OF GOD, CHANGE THIS
  # Uncomment the next like and change "secret" to something that doesn't suck.
  # secretKey: secret
  secretKey: secret
  port: 25577
  npcSaveMode: true
  
  # Logging requests might not be the smartest thing in the world, because the secret is spit out in the log.
  # This will change in the future.  I hope.   In the meantime, if you're really worried, you probably
  # Want to uncomment the next line:
  #logRequests; false
  logRequests: true



2.5 Installation 
=================
   Put the main InfoApi.jar in your plugins directory.  Put the 'endpoints' directory inside of plugins/InfoApi/

2.5.1 From the original doc:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Please note: this Plugin will give an Error if you do /reload, because the API Server CAN'T get reloaded at the moment. Thats not really
an problem, but you should know. Also there can be an Error on Shutdown... im sorry about that

2.6 Communicating with the API
==============================
   My version of the InfoApi plugin is shaping up to be a restful API.  This means the following:
   - Any time the server runs into an internal error condition (exception thrown, etc.) it will return a HTTP 500 status code.
   - Any time the server can't find the entity/endpoint you are looking for, then it will throw a 404 instead
   - If you got the endpoint right, but it can't answer your request, it will spit out a 405 "Method Not Allowed" error. 
   - Any time the server found what you are looking for, but the response is empty, you will get an empty response with a 200 error code.
   - Each endpoint will provide a response to an HTTP OPTIONS request.  Right now it just provides its currently mounted location, and a
     small description of what it does. But this will be expanded later. 

2.7 Example Usage PHP:
======================
   Wildly untested code here.  But it should work. 


  $apiurl = "http://server:25577/info/version"
  $ch = curl_init();
  curl_setopt($ch, CURLOPT_URL, $apiurl);
  curl_setopt($ch, CURLOPT_HEADER, 1);
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
  curl_setopt($ch, CURLOPT_TIMEOUT, 1000);
  
  $version = curl_exec($ch);  // this is the text response from the server
  $http_status = curl_getinfo($ch, CURLINFO_HTTP_CODE);  // this is the http status code. 200 if hunky dory, >=400 if not.
  echo $version



3 Development
-------------

3.1 What InfoApi Desperately needs that I cannot provide?
=========================================================
   - A maven maven.  My pom.xml get the job done, but there is too much repetition--and rape-and-paste coding--going on there.
   - probably more, but this goes into the realm of unknown unknowns.  

3.2 What are .org files?
========================
   They are specialized emacs text files.  They actually rule quite a lot.  There are 2 in this project
   readme.org: The source of this file.
   Dev.org: My to do list, and build environment.  It actually can run my test instance of the server, and compiles everything for me.
                Org-Babel rules. It also has the set of development tasks I am working on.

   If you really want to know more, then check out emacs org mode. 

3.3 Building
============
   A [./pom.xml] is being worked on... but I am new to maven, so it probably sucks.  It sucks less then forcing you to use eclipse
   though.

3.4 Building your own plugin
============================

   Sorry for the spotty documentation... :/

3.4.1 Resource file inside of jar called endpoint.info in main directory. contains:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


  main-class: <fully-qualified-path-to-class-in-jar>




For example



  main-class: net.jonnay.infoapi.Info




Then you just need to build a class that extend InfoApiEndpoint, provide a method with the following signature:

public HttpResponse <lower-case-http-verb>Method(EndpointState s)

i.e.

public HttpResponse getMethod(EndpointState s) 

or

public HttpReponse postMethod(EndpointState s) 

The "version" and "info" projects will help you get started. 
