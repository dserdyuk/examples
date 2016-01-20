# examples

## Formula1 

Build - mvn clean install

Start default - mvn exec:exec

Start custorm - mvn -DteamNumber=5 -DtrackLen=1000 exec:exec

Note:

trackLen in meters


it will start default race with 10 teams and track length 100 km

Output contains:

Race time,

Winner,

Cars Table

## Hotel service

build - mvn  clean install

Start service on port 8090 - mvn exec:exec

Test request:

curl -H "Api-Key:test-1" -H "Content-Type:application/json" http://localhost:8090/hotels/Bangkok?sortAsc=true

