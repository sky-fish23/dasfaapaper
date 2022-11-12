# MIS generator

**MIS generator**  is used to generate the minimal inconsistent subsets of inconsistent ontology. 



## Runtime environment: 

JRE 1.8



## Datasets

See the folder **data** under MIS code file, which contains all the original datasets of our experiment. There are 6 inconsistent ontologies:

- AUTOMCSv2-cocus-edas.owl

- Bioportal-metadata.owl

- UOBM-lite-10-35.owl

- UOBM-lite-10-36.owl

- UOBM-lite-10-37.owl

- UOBM-lite-10-38.owl




## Setup: 

Change the file path in the main function in the code file under src folder to the required path. Take generating all minimum inconsistent subsets of UOBM-lite-10-35 as example:

 Set ontoPath = "file:data/ UOBM-lite-10-35.owl"