import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

/**
 * Build Matrix from qrel25
 * Query-docID BM25
 */
public class BuildMatrix {
  //[queryId -> docId -> score]
  private Map<Integer, Set<String>> topicSet;

  public BuildMatrix() throws IOException {
    topicSet = new TreeMap<>();
    readBM25("sophie_BM25.txt");
    readQrel("qrel25.txt");
    writeFile();
  }

  /**
   * Read and write the first 1000 from BM25 ranking. Record a set of docId. Append querl IDs whose
   * value is 1.
   */
  private void readBM25(String path) {
    try {
      Scanner sc = new Scanner(new FileReader(path));
      while (sc.hasNext()) {
        String line = sc.nextLine();
        String[] values = line.split(" ");
        int queryId = Integer.valueOf(values[0]);
        String docId = values[2];
        //store queryId and docId into map->set
        if (topicSet.containsKey(queryId)) {
          topicSet.get(queryId).add(docId);
        } else {
          Set<String> newSet = new HashSet<>();
          newSet.add(docId);
          topicSet.put(queryId, newSet);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void readQrel(String path) throws IOException {
    StringBuilder catalog = new StringBuilder();
    try {
      Scanner sc = new Scanner(new FileReader(path));
      while (sc.hasNext()) {
        String line = sc.nextLine();
        String[] values = line.split(" ");
        int queryId = Integer.valueOf(values[0]);
        String docId = values[2];
        int truth = Integer.valueOf(values[3]);
        if (truth == 1) {
          if (!topicSet.get(queryId).contains(docId)) {
            topicSet.get(queryId).add(docId);
            catalog.append(queryId + " " + docId + "\n");
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    FileUtils.write(new File("DocAppendCatalog.txt"), catalog.toString(), "UTF-8");
  }

  public void writeFile() throws IOException {
    StringBuilder sb = new StringBuilder();
    StringBuilder catalog = new StringBuilder();
    for (int i : topicSet.keySet()) {
      int count = 0;
      for (String docId : topicSet.get(i)) {
        count++;
        sb.append(String.valueOf(i)).append(" ").append(docId).append("\n");
      }
      catalog.append(String.valueOf(i)).append(" file count: ").append(count).append("\n");
    }
    FileUtils.write(new File("DocList.txt"), sb.toString(), "UTF-8");
    FileUtils.write(new File("DocListCatalog.txt"), catalog.toString(), "UTF-8");
  }


  public static void main(String[] st) throws IOException {
    BuildMatrix bm = new BuildMatrix();
  }

}
