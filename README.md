# serviceProject_news

This readme is a joke compared to any other proper readme, so let me be short and clear:

This project's scope is to make a service, capable of working in pair with gateway and other services. 
This specific one can get news from mostly any place in the planet thanks to good API I found. 

News are stored in MySQL database that is currently on docker container, there is also a bit of fucntionality so I can simulate
client knocking to gateway so our team can test my service. Ip addresses and ports need to be defined before executing service.

Main class to be executed is defined in manifest and pom, it is the MainRunner class. Whenever it is launched, it start to do 3 things:
  
  1. Registers in Gateway... does it recursively untill succeeds
  2. Starts a so called routine that will update common news topics once in a while
  3. Acts as a server that can handle requests and send responses.
  
  Registering part is pretty easy, it sends a post request to Gaetway and wants to see OK status in the response to know everything is fine.
  
  Routine works with scheduling and class Timer. It basically looks in db and checks wether or not news are outdated once in 12 hours. And updates
  news when needed.
  If we want to change common topics, we need to add or delete them in distinct table in DB.
  
  Server is the main part of this project. Server has a limited threadpool and can proccess incoming requests. If you want to complete a process, you will need
  to send at least 3 requests to the server. 
  
    1. Post request with function name and news topic in json formatted body.
    2. Put request with additionals search parameters, id returned by Post request also in json formatted body. 
       Available parameters are: date (last month, last week, today)
                                 language (en, ru, ro)
    2.1 Put request with "finalize":"true", so in get method service will now that it should compute and return needed info
                                 
       Additional parameters are available, but we didn't want to dig deeper into it.
    3. Get request with process id, in response client gets needed news.
    
    
    
