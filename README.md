ndsg-chupacabra
===============

Chupacabra: De Villa, Ongcol, Tan


commands

###### CREATING jars ####################

jar cf (name).jar *.class

###### VIEWING jars #####################

jar tf (name).jar

###### COMPILING CLASSES ################

javac -cp "jep-2.3.0.jar;djep-1.0.0.jar;peersim-1.0.5.jar" *.java

###### RUNNING A SIMULATION #############

java -cp "jep-2.3.0.jar;djep-1.0.0.jar;peersim-1.0.5.jar;(other jars)" peersim.Simulator (config).txt
