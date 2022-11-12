# scoring function

In this part, **Knowledge Graph Embedding** and **Bert-based Sentence Embedding** methods are used to transform axioms into semantic vectors. Then we use our proposed scoring functions to score the maximum consistent subsets.



## Using Knowledge Graph Embedding

The complete experiment codes using KG Embedding are stored in the folder **./scoring_KGEmbedding/OpenKE** .

We use open-source toolkit **OpenKE** here.  See for detailed instructions: https://github.com/thunlp/OpenKE



### Steps to score the MCS

- **data process:**  See in the folder **./OpenKE/dataProcess**.  We first get the triple forms of data using NaturalOWL. The triple forms of data are stored in the folder **./dataProcess/tsvData**. Then we run the code **./dataProcess/2OpenkeFormat.py** to get the required data for training.
- **training :**  Take ontology UOBM-lite-10-35 and applying RESCAL as example. Directly run **train_rescal_UOBM35.py** .  Then UOBM-lite-10-35  is trained by RESCAL model, and the semantic vectors of axioms in UOBM-lite-10-35 ontology are stored in the folder **./embed/rescal_UOBM35_embed.vec**.
- **scoring**: Take ontology UOBM-lite-10-35 and applying RESCAL as example. Directly run **score_rescal_UOBM35.py** , then we can get the score of each MCS of the ontology UOBM-lite-10-35. 



## Using BERT-based Sentence Embedding

The complete experiment codes are stored in the folder **./scoring_SentenceEmbedding/sentence-transformers**.

We use open-source toolkit **sentence-transformers** here.   See for detailed instructions: https://github.com/UKPLab/sentence-transformers



### Steps to score the MCS

We use different versions of BERT and different models based on BERT to obtain semantic vectors of the axioms. All processed data are stored in the corresponding root folder: **./sentence-transformers** and are all in the folder **AUTOMCS**， **BioMCS** and **UOBM** . 

We can directly get the score of each MCS by running the corresponding scoring codes. Take ontology UOBM-lite-10-35 as example. Run **UOBM35_cosine_score.py** to get the scores of MCSs of the ontology UOBM-lite-10-35 applying ALBERT and cosine similarity. You can also try different models such as SBERT and RoBERTa (see codes and comments for details). 