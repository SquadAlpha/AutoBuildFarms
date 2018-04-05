copy ..\target\AutoBuildFarms-*-SNAPSHOT-shaded.jar .\plugins\

java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Xmx768M -Xms256M spigot-1.12.2.jar