import org.apache.commons.io.FileUtils;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WekaCluster {
  StringBuilder wekaStr;
  StringBuilder catalog;
  List<String> idList;
  public WekaCluster() {
    wekaStr = new StringBuilder();
    catalog = new StringBuilder();
    idList = new ArrayList<>();
  }
  private void writeHeader() {
    wekaStr.append("@RELATION TopicDocID\n");
    for(int i = 0; i < 200; i++) {
      wekaStr.append("@ATTRIBUTE topic"+i+"\tREAL\n");
    }
    wekaStr.append("\n");
  }

  public void csb2arff() throws IOException {
    writeHeader();
    //Scanner  sc = new Scanner(new File("weka/topic_composition.csv"));
    //Scanner  sc = new Scanner(new File("weka/topic_compostion2.txt"));
    Scanner  sc = new Scanner(new File("weka/ap89data.txt"));

    wekaStr.append("@DATA\n");
    int count = 0;
    while (sc.hasNextLine()) {
      count++;

      String[] vals = sc.nextLine().split(" ");
      String docID = vals[1].substring(5).replaceAll(".txt", "");
      //String[] vals = sc.nextLine().split(" ");
      //String docID = vals[1];
      System.out.println(docID);
      idList.add(docID);
      catalog.append(docID).append("\n");
      for (int i = 2; i < vals.length; i++) {
        wekaStr.append(Double.valueOf(vals[i])).append(",");
      }
      wekaStr.append("\n");
      //heap out of memory, must write then append
      if (count % 100 == 0){
        System.out.println("read " + count + "files");
        FileUtils.write(new File("weka/ap89.arff"), wekaStr.toString(), "UTF-8", true);
        FileUtils.write(new File("weka/ap89catalog.txt"), catalog.toString(), "UTF-8", true);
        wekaStr = new StringBuilder();
        catalog = new StringBuilder();
      }
    }
    FileUtils.write(new File("weka/ap89.arff"), wekaStr.toString(), "UTF-8", true);
    FileUtils.write(new File("weka/ap89catalog.txt"), catalog.toString(), "UTF-8", true);
  }
  public List<String> getIdList() throws FileNotFoundException {
    List<String> result = new ArrayList<>();
    //Scanner sc = new Scanner(new File("weka/IDCatalog2.txt"));
    Scanner sc = new Scanner(new File ("weka/ap89catalog.txt"));
    while (sc.hasNextLine()) {
      String id = sc.nextLine();
      result.add(id);
    }
    return result;
  }

  public void Kmeans(String path) throws Exception{
    System.out.println("Clustering");
    Instances instances = new Instances(new FileReader(path));
    SimpleKMeans simpleKMeans = new SimpleKMeans();

    // build clusterer
    simpleKMeans.setPreserveInstancesOrder(true);
    simpleKMeans.setNumClusters(25);
    simpleKMeans.setSeed(1000);
    simpleKMeans.buildClusterer(instances);

    ClusterEvaluation eval = new ClusterEvaluation();
    eval.setClusterer(simpleKMeans);
    eval.evaluateClusterer(instances);

    System.out.println("Cluster Evaluation: "+eval.clusterResultsToString());

    // output docId -> cluster
    System.out.println("Writing cluster");
    List<String> listId = getIdList();
    StringBuilder result = new StringBuilder();
    int[] assignments = simpleKMeans.getAssignments();
    for (int i = 0; i < assignments.length; i++) {
      String docId = listId.get(i); // cast from double
      System.out.printf("ID %s -> Cluster %d \n", docId, assignments[i]);
      result.append(docId).append(" Cluster ").append(assignments[i]).append("\n");
    }
    FileUtils.write(new File("weka/ap89cluster.txt"), result.toString(), "UTF-8");
  }


  public static void main(String[] str) throws Exception {
    WekaCluster wekaCluster = new WekaCluster();
    //wekaCluster.csb2arff();
    //System.out.println("file:/Users/sophie/Desktop/mallet-2.0.8/AP89/".length());
    //System.out.println("file:/Users/sophie/Desktop/mallet-2.0.8/AP89/AP890101-0001.txt".substring(45).replaceAll(".txt", ""));
    wekaCluster.Kmeans("weka/ap89.arff");
  }
}
