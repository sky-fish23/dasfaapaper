# MCS generator

**MCS generator**  is used to generate the maximal consistent subsets of inconsistent ontology. 



## Runtime environment: 

JRE 1.8



## Datasets

See the folder **data** under MCS code file, which contains all the original datasets of our experiment. There are 6 inconsistent ontologies:

- AUTOMCSv2-cocus-edas.owl

- Bioportal-metadata.owl

- UOBM-lite-10-35.owl

- UOBM-lite-10-36.owl

- UOBM-lite-10-37.owl

- UOBM-lite-10-38.owl

There is an empty owl file, new.owl. 



## Running parameters

 Take  UOBM-lite-10-35.owl as an example:

​    -r 

​    data/new.owl 

​    data/UOBM-lite-10-35.owl



##  Results 

The code prints result of the complement set of each maximal consistent subset. 