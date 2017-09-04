#!/bin/bash
echo $1

# create folder
# create mallet file for each topic
# bin/mallet import-dir --input sample-data/web/ --output web.mallet --keep-sequence --remove-stopwords

# train-topic and output
# bin/mallet train-topics  --input tutorial.mallet --num-topics 20 --output-state topic-state.gz --output-topic-keys tutorial/tutorial_keys.txt --output-doc-topics tutorial_compostion.txt 
mkdir hw8
list="54 56 57 58 59 60 61 62 63 64 68 71 77 80 85 87 89 91 93 94 95 97 98 99 100";
#list="54"
count=0;
for id in $list;
do
	mkdir hw8/$id
	bin/mallet import-dir --input topic/$id --output hw8/$id/model.mallet --keep-sequence --remove-stopwords
	echo "wrote mallet for: " $id
	bin/mallet train-topics  --input hw8/$id/model.mallet --num-topics 10 --output-state hw8/$id/topic-state.gz --output-topic-keys hw8/$id/topic_keys.txt  --output-doc-topics hw8/$id/topic_compostion.txt
	echo "wrote topics for: " $id
	((count++)) 
done
echo "Total number of topic: "  $count;
