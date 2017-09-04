import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Evaluating weka solution with matrix 1795 non duplicate total, 1832 docId for 25 topics
 */
public class WekaEvaluate {
  Map<Integer, List<String>> qrelIdDoc; //queryId -> docNum
  Map<String, Set<Integer>> docQueryMap; // docNum -> {queryID1, queryID2...}
  Map<String, Integer> docClusterMap; //docNum -> ClusterNum
  List<String> docList;
  StringBuilder sb;

  public WekaEvaluate() throws FileNotFoundException {
    qrelIdDoc = new HashMap<>();
    docClusterMap = new HashMap<>();
    docQueryMap = new HashMap<>();
    docList = new LinkedList<>();
    sb = new StringBuilder();
    getTopicMap();
    getQrelId();
  }

  private void getTopicMap() throws FileNotFoundException {
    Scanner sc = new Scanner(new File("weka/ap89cluster.txt"));
    while (sc.hasNextLine()) {
      String[] vals = sc.nextLine().split(" ");
      String docId = vals[0];
      docClusterMap.put(docId, Integer.valueOf(vals[2]));
    }
    sb.append( "total number in arff file s"+docClusterMap.keySet().size()).append("\n");
  }

  private void getQrelId() throws FileNotFoundException {
    Scanner sc = new Scanner(new File("qrel25.txt"));
    while (sc.hasNextLine()) {
      String[] vals = sc.nextLine().split(" ");
      String docId = vals[2];
      Integer queryId = Integer.valueOf(vals[0]);
      if (Integer.valueOf(vals[3]) == 1) {
        if (docQueryMap.containsKey(docId)) {
          docQueryMap.get(docId).add(queryId);
        } else {
          Set<Integer> newset = new HashSet<>();
          newset.add(queryId);
          docQueryMap.put(docId, newset);
          docList.add(docId);
          if (qrelIdDoc.containsKey(queryId)) {
            qrelIdDoc.get(queryId).add(vals[2]);
          } else {
            List<String> list = new ArrayList<>();
            list.add(vals[2]);
            qrelIdDoc.put(queryId, list);
          }
        }
      }
    }
    System.out.println("number of relevant " + docList.size());
    System.out.println("number of query " + qrelIdDoc.size());
    sb.append("number of query ").append(qrelIdDoc.size()).append("\n");
    sb.append("number of unique document in all qrel ").append(docQueryMap.size()).append("\n");
  }

  private void buildMatrix() throws IOException {
    int sameClusterSameQuery = 0;
    int diffClusterdiffQuery = 0;
    int sameClusterDiffQuery = 0;
    int diffClusterSameQuery = 0;
    boolean sameCluster = false;
    int len = docQueryMap.size();
    int countIter = 0;
    for (int i = 0; i < len; i++) {
      for (int j = i + 1; j < len; j++) {
          String docA = docList.get(i);
          String docB = docList.get(j);
          countIter++;
        if(docClusterMap.containsKey(docA) && docClusterMap.containsKey(docB)) {
          int clusterA = docClusterMap.get(docA);
          int clusterB = docClusterMap.get(docB);
          sameCluster = (clusterA == clusterB);

          Set<Integer> queryA = docQueryMap.get(docA);
          Set<Integer> queryB = docQueryMap.get(docB);
          boolean sameQuery = false;
          for (Integer queryId : queryA) {
            if (queryB.contains(queryId)){
              sameQuery = true;
              break;
            }
          }
          // fill matrix;
          if (sameCluster && sameQuery) {
            sameClusterSameQuery++;
          } else if (sameCluster && (!sameQuery)) {
            sameClusterDiffQuery++;
          } else if ((!sameCluster) && sameQuery) {
            diffClusterSameQuery++;
          } else if ((!sameCluster) && (!sameQuery)) {
            diffClusterdiffQuery++;
          }
        }
      }
    }

    sb.append("iteration: ").append(countIter).append("\n");
    int sum = sameClusterDiffQuery + sameClusterSameQuery
            + diffClusterdiffQuery + diffClusterSameQuery;
    int correct = sameClusterSameQuery + diffClusterdiffQuery;
    int falsePosNeg = sameClusterDiffQuery + diffClusterSameQuery;
    double acc = (double) correct / (double) sum;
    sb.append("total: ").append(sum).append("\n");
    sb.append("true pos + true neg = correct: ").append(correct).append("\n");
    sb.append("false pos + false negs: ").append(falsePosNeg).append("\n").append("\n");
    sb.append("correct/total: ").append(acc).append("\n");
    sb.append("          \tsame cluster \t diff clusters\n");
    sb.append("same query\t").append(sameClusterSameQuery).append("\t\t\t").append(diffClusterSameQuery).append("\n");
    sb.append("diff query\t").append(sameClusterDiffQuery).append("\t\t\t").append(diffClusterdiffQuery).append("\n");
    FileUtils.write(new File("weka/ap89matrix.txt"), sb.toString(), "UTF-8");
    //FileUtils.write(new File("spark/matrix.txt"), sb.toString(), "UTF-8");

  }

  public static void main(String[] args) throws IOException {
    WekaEvaluate we = new WekaEvaluate();
    we.buildMatrix();
    //we.getQrelId();
  }
}
