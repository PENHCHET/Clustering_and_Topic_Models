import scipy
import sklearn
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.datasets import fetch_20newsgroups
from sklearn.decomposition import LatentDirichletAllocation


def display_topics(model, feature_names, no_top_words):
    for topic_idx, topic in enumerate(model.components_):
        print("Topic %d:" % topic_idx)
        print(" ".join([feature_names[i]
                        for i in topic.argsort()[:-no_top_words - 1:-1]]))

pathFolder = "/Users/sophie/Desktop/data"

data = sklearn.datasets.load_files(pathFolder)
documents = data.data
# print(documents)
print("Value : %s" % data.keys())

no_features = 10000
no_topics = 200
no_top_words = 15
# LDA can only use raw term counts for LDA because it is a probabilistic graphical model
tf_vectorizer = CountVectorizer(max_df=0.95, min_df=2, max_features=no_features, stop_words='english')
tf = tf_vectorizer.fit_transform(documents)
tf_feature_names = tf_vectorizer.get_feature_names()

# RunLDA
lda = LatentDirichletAllocation(n_components=no_topics, max_iter=10, learning_method='online', learning_offset=50.,
                                random_state=0).fit(tf)
display_topics(lda, tf_feature_names, no_top_words)
count = 0

pre = "/Users/sophie/Desktop/data/"
end = ".txt"
filename = ""
matrix = lda.transform(tf)
names = data.filenames
f = open("ap89data.txt", "a+")
while count < len(names):
    filename = names[count].replace(pre, "").replace(end, "")
    f.write(str(count) + " " + filename)
    for topicnum in range(no_topics):
        f.write(" " + str(matrix[count][topicnum]))
    f.write("\n")
    count += 1
f.close()


