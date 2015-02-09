SunGlass
-----------------
SunGlass is a light-weight maven repo proxy written in java utalizing the Spark framework. it is designed to be a light alternative to other maven repository software such as Artifactory, Sonatype Nexus, and Archiva.

**NOTE: JAVA 8 REQUIRED!** why? because Spark is java 8.

# Building
This project uses Gradle to build. It also rquires Java 8. To build, simply open a command line, and run ```gradlew distZip```. The output can be found in build/distributions. The zip there contains starter scripts as well as example configuration files.

# Usage
List the maven repositories you would like to proxy in your servers.txt and start SunGlass. It will check the repositories for any requested artifact in the order they are specified.

# ToDo list
[ ] Support snapshots and dynamic dependencies
[ ] Detect slow connections and spawn thread for artifact download
[ ] add admin pannel
[ ] support secured repositories with credentials
[ ] add https suppotr (Spark supports, but SunGlass doesnt yet)
[ ] stuff I havent thaught of yet
