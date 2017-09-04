import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class SparkEvaluate {
  public static List<String> getIdList() throws FileNotFoundException {
    List<String> result = new ArrayList<>();
    Scanner sc = new Scanner(new File("weka/IDCatalog2.txt"));
    while (sc.hasNextLine()) {
      String id = sc.nextLine();
      result.add(id);
    }
    return result;
  }

  public static void main(String[] args) throws IOException {
    List<String> listId = getIdList();
    SparkConf conf = new SparkConf().setAppName("JavaKMeansExample").setMaster("local[2]").set("spark.executor.memory", "1g");
    JavaSparkContext jsc = new JavaSparkContext(conf);
    // Load and parse data
    //String path = "spark/sample.txt";
    String path = "spark/AP89Spark.txt";
    JavaRDD<String> data = jsc.textFile(path);
    JavaRDD<Vector> parsedData = data.map(s -> {
      String[] percentage = s.split(",");
      double[] values = new double[percentage.length];
      for (int i = 0; i < percentage.length; i++) {
        values[i] = Double.parseDouble(percentage[i]);
      }
      return Vectors.dense(values);
    });
    parsedData.cache();

    //Cluster the data into two classes using KMeans
    int numClusters = 25;
    int numIterations = 1000;
    KMeansModel clusters = KMeans.train(parsedData.rdd(), numClusters, numIterations);

    StringBuilder sb = new StringBuilder();
    List<Vector> vectors = parsedData.collect();
    int i = 0;
    for (Vector vector : vectors) {
      String docId = listId.get(i); // cast from double
      sb.append(docId).append(" Cluster ").append(clusters.predict(vector)).append("\n");
      System.out.printf("ID %s -> Cluster %d \n", docId, clusters.predict(vector));
      //System.out.println("cluster " + clusters.predict(vector) + " " + vector.toString());
      i++;
    }


    /*System.out.println("Cluster centers:");
    for (Vector center : clusters.clusterCenters()) {
      System.out.println(" " + center);
    }*/
    double cost = clusters.computeCost(parsedData.rdd());
    System.out.println("Cost: " + cost);

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    double WSSSE = clusters.computeCost(parsedData.rdd());
    System.out.println("Within Set Sum of Squared Errors = " + WSSSE);

    // Save and load model
    //clusters.save(jsc.sc(), "spark/KmeansModel");

    JavaRDD cluster_indices = clusters.predict(parsedData);

    System.out.println(cluster_indices.toString());
    jsc.stop();
    FileUtils.write(new File("spark/result.txt"), sb.toString(), "UTF-8");

  }
}
