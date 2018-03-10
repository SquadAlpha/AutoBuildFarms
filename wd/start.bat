copy ..\target\AutoBuildFarms-*-SNAPSHOT-shaded.jar .\plugins\

java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Xmx1G -Xms1G spigot-1.12.2.jar