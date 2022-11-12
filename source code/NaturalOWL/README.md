# NaturalOWL tool

**NaturalOWL tool**  is used to translate the axioms into natural language sentences. 



## Runtime environment: 

JRE 1.8



## Datasets

See the  folder **data** under AXintoNL code file, which contains all the original datasets of our experiment. There are 6 inconsistent ontologies:

- AUTOMCSv2-cocus-edas.owl

- Bioportal-metadata.owl

- UOBM-lite-10-35.owl

- UOBM-lite-10-36.owl

- UOBM-lite-10-37.owl

- UOBM-lite-10-38.owl



## Setup: 

Modify the path under AXintoNL.java code file in src folder. 

OntoRoot is changed to the root directory of running data, and ontoName is changed to the name of running dataset. 

Take UOBM-lite-10-35.owl as example:

​	String ontoRoot = "data/"；

​   String ontoName = " UOBM-lite-10-35.owl；