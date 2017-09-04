# Clustering_and_Topic_Models

cluster documents, detect topics, and represent documents in topic space.

KeyWords: __Java__, __Python__, __Scikit Learn LDA__, __Mallet LDA__, __K-Means Clustering__

### Data
The Ap89 corpus (270MB, about 84000 files), BM25 ranked top 1000 document list for 25 queries, from previous project [Retrival Models](https://github.com/socrateszhang/InfoRetrivalModels).

### Topic Per Query
* Reading [Mallet](http://mallet.cs.umass.edu/) documentation, it is a Java-based package for statistical natural language processing,other machine learning applications to text.
* Retrieve all parsed doc content from ElasticSearch using REST api
* Write a **shell script** to run Mallet topic generation on every 1000 files, generate 10 topics each, compare it with the 25 queries. 

### LDA-topics and clustering
* Run [Mallet](http://mallet.cs.umass.edu/)  LDA on the entire AP89 collection, with about T=200 topics. Obtain a representation of all documents in these topics.
* Clustering : 
  * run clustering-partition algorithm on documents in this topic representation. partition means every document gets assigned to exactly one cluster, like with K-means. Target K=25 clusters. List each cluster with its documents IDs. Each document has a ElasticSearch index set up so one can verify documents by ID.
  * Using [weka's](http://www.cs.waikato.ac.nz/ml/weka/downloading.html) clustering algorithm, first put data into [.ARFF](https://weka.wikispaces.com/ARFF+%28book+version%29) file format, then run Simple Clustering assign documents into topic clusters.

### Improvement, Python Scikit, Scipy
The accuracy after topic generation with Mallet is about 70%, fairly low. 
 * I have tried different clustering algorithm with [appache spark](https://spark.apache.org/docs/latest/ml-clustering.html). Restul did not show improvement.

* Then, I used the python [Scikit Learn Latent Dirichlet Allocation (LDA) Library](http://scikit-learn.org/stable/modules/generated/sklearn.decomposition.LatentDirichletAllocation.html) to generate 200 topics.
* With this python library, using same clustering library as before, the accuracy improved to 87%! :+1:


### Evaluation, Result

There are about 1831 relevant documents in total. Consider all the pairs of two relevant documents, that is (1831 choose 2). For each pair, count a 1 in the appropriate cell of the following **confusion table**:


|ConfusionMatrix        |same cluster |different clusters|
|-----------------------|:-----------:|-----------------:|
|same query|
|different queries|


Accuracy  = (same query same cluster  + diff query diff cluster ) / (same query same cluster  + diff query diff cluster + same query diff cluster + diff query same cluster)
