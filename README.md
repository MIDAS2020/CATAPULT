# Midas 

This is our implementation for the paper:

CATAPULT: Data-driven Selection of Canned Patterns for Efficient Visual Graph Query Formulation

The project consists of ClusterGeneration and CATAPULT. ClusterGeneration is  implemented with C++ (C++ 14) and  CATAPULT is implemented with Java (JDK 1.8). 


# Environments

C++ 14

JDK 1.8

Visual Studio 2019

Eclipse 2019  

Boost Graph Library (https://www.boost.org/doc/libs/1_74_0/libs/graph/doc/)

# Dataset

1) AIDS antiviral dataset

2) PubChem dataset

3) eMolecule dataset


# Example to run the codes

There is an example to run the codes. Suppose the original database contains 30000 graphs in AIDS antiviral dataset, ClusterGeneration should be performed to generate the  clusters  and CATAPULT is then to generate the pattern set. 

1. Run ClusterGeneration: 

Step 1:  Import ClusterGeneration project into Visual Studio workspace.  

Step 2: Download Boost Graph Library (BGL) from https://www.boost.org/doc/libs/1_74_0/libs/graph/doc/ and compile BGL. Then configure it for Visual Studio (see https://www.youtube.com/watch?v=CH_YZ2bePPM ).

Step 3:  Open SmallGraphClustering.h, set  databasefilename = "AIDS40k",  initialsizeofgraph = 30000, initialclustername = "clusters.txt".   By doing this,  the input file is "AIDS40k", the output file is  "clusters.txt"  that records the generated clusters for original database. 

Step 4:  Open the main class MidasMain.cpp, run it with Release Mode to obtain the output "clusters.txt". 

2. Run CATAPULT:

Step 1:  Import CATAPULT project into Eclipse workspace.  

Step 2:  Open the class  src/main/patterngenerator.java,   set  readClusterFile("clusters.txt") in function generatePatterns(),  run this class to obtain the updated pattern set "patterns/thumbnails/GUIPatterns.txt".

