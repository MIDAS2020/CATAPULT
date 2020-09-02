# Midas
MIDAS: Towards Efficient and Effective Maintenance of Canned Patterns in Visual Graph Query Interfaces


This is our implementation for the paper:

MIDAS: Towards Efficient and Effective Maintenance of Canned Patterns in Visual Graph Query Interfaces


# Environments
Tensorflow (version: 1.0.1)
numpy
sklearn
Dataset
We use the same input format as the LibFM toolkit (http://www.libfm.org/). In this instruction, we use MovieLens. The MovieLens data has been used for personalized tag recommendation, which contains 668,953 tag applications of users on movies. We convert each tag application (user ID, movie ID and tag) to a feature vector using one-hot encoding and obtain 90,445 binary features. The following examples are based on this dataset and it will be referred as ml-tag wherever in the files' name or inside the code. When the dataset is ready, the current directory should be like this:
