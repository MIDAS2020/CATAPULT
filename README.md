# Midas 

This is our implementation for the paper:

MIDAS: Towards Efficient and Effective Maintenance of Canned Patterns in Visual Graph Query Interfaces

The project consists of Catapult++ and Midas. Catapult++ is  implemented with C++ (C++ 14) and  Midas is implemented with Java (JDK 1.8). 


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

There is an example to run the codes. Suppose the original database contains 25000 graphs in AIDS antiviral dataset, if 5000 graphs are added into the database, Catapult++ should be performed to maintain the  clusters generated from  the original database  and Midas is then to update original patterns based on the updated clusters. 

1. Run Catapult++: 

Step 1:  Import Catapult++ project into Visual Studio workspace.  

Step 2: Download Boost Graph Library (BGL) from https://www.boost.org/doc/libs/1_74_0/libs/graph/doc/ and compile BGL. Then configure it for Visual Studio (see https://www.youtube.com/watch?v=CH_YZ2bePPM ).

Step 3:  Open SmallGraphClustering.h, set  databasefilename = "AIDS40k",  initialsizeofgraph = 25000, addedsizeofgraph = 5000, initialclustername = "initialcluster.txt" and  updateclustername = "updatecluster.txt".   By doing this,  the input file is "AIDS40k".   The output file are  "initialcluster.txt"  and "updatecluster.txt" that record clusters for original database and updated database. 

Step 4:  Open the main class MidasMain.cpp, run it with Release Mode to obtain the output "initialcluster.txt" and "updatecluster.txt". 

2. Run Midas:

Step 1:  Import Midas project into Eclipse workspace.  

Step 2:  Open the class  src/main/patterngenerator.java,   set  readClusterFile("initialcluster.txt") in function generatePatterns(), set outputFilename = "InitialPatterns" in function PM_savePatternsToFile(),  run this class  to get the pattern set for original database.

Step 3:  Open the  class src/main/MIDASPatternMaintainer.java,  for function  UpdatedMidas(), set setDataBaseName("AIDS40k"), 
		setDbName("AIDS"),  setInitialPatternName("InitialPatterns"),  and setUpdateClusterName("UpdatePatterns") , run this class to get the pattern set for updated database. 
    
