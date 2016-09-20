# Introduction
The main purpose of this project is to test [google-maps-services](https://github.com/googlemaps/google-maps-services-java) library  
vs simple connection to google backend urls.

Google-maps-services allows to define maximum queries per second (QPS) to avoid google send the OVER_LIMIT_EXCEPTION. This mechanism has not been added to the simple URLLoader
## Configuration
You need to configure your google credentials to be able to call the geocoding library (google library and url)
**There are two main classes**
- ProcessOperations uses google library to geocode addresses
- URLLoader uses http connections to google backend

There are two scripts in /src/dist/bin that can be use to execute the applications. 
- runLibrary_example. This file contains configuration entries and is used to test google_maps_service library. Copy the file
to runLibrary and set your configuration
- runUrl_example. This file is normally used to launch the application that will use the file with urls that will be used 
for geocoding.

## Applications
There are two applications that will deal with the geocoding. There are two scripts used to launch these two applications in /src/dist/bin (runLibrary, runUrl)
- ProcessOperation uses the google-maps-service library
- URLLoader loads a file that contains the urls that will be used to retrieve geocoding data.
### ProcessOperation


**You need to define some environment entries to configure google account** 
* Google Account

        Development        
            - API_KEY (development key Aiza....)
        
        Production
            - CLIENT_ID
            - CLIENT_SECRET
* Queries per second 

        - QPS
ProcessOperations needs two parameters to work

        - batchSize Operations will be splitted in batches
        - filename Addresses to process
                
    Example: java com.test.main.ProcessOperations addresses.txt 30
### URLLoader
URLLoader only needs one parameter to work

        - filename URLS to process
        
    Example: java com.test.main.URLLoader geocodingRequests.txt

#### How to generate geocodingRequests.txt file
In order to generate the file geocodingRequests.txt used on the **URLLoader** you need to execute first ProcessOperations 
with your google account keys and then capture the urls printed to the output by the google-maps-service library. 
You can then copy the urls into a new file called geocodingRequests.txt
 
Alternatively you can just use the file __geocodingRequests.example__ and put your development key. 

**If you are using a paid google account, this will not work, as you will need to generate the signature for the request and this is based on your google account**

## Command line
to run from commandline

go to directory src/dist/bin

copy example files
runUrl_example to runUrl
runLibrary_example to runLibrary

set your API_KEY and update parameters if needed.
By default are used 90 records to geocode

run 
gradle clean installDist

the application is ready to use in gradle/installDist/performanceTest/bin

