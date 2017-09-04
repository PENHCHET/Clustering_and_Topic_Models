import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

//read bm25, get data from es, save into folder per query
public class DataToFolder {
  RestClient restClient;
  Map<Integer, List<String>> queryMap; //queryID -> docId
  public DataToFolder(RestClient rs) throws IOException {
    queryMap = new HashMap<>();
    restClient = rs;
    read();
    getES();
  }
  //read bm25
  public void read() throws FileNotFoundException {
    Scanner sc = new Scanner(new File("DocList.txt"));
    while (sc.hasNextLine()) {
      String[] vals = sc.nextLine().split(" ");
      int queryId = Integer.valueOf(vals[0]);
      String docNum = vals[1];
      if (queryMap.containsKey(queryId)) {
        queryMap.get(queryId).add(docNum);
      } else {
        List<String> newList = new LinkedList<>();
        newList.add(docNum);
        queryMap.put(queryId, newList);
      }
    }
    System.out.println(queryMap.size() + "topics read");
  }
  //retrieve from elastic search
  public void getES() throws IOException {
    for (int i : queryMap.keySet()) {
      for (String docId : queryMap.get(i)) {
        String text = getText(docId);
        FileUtils.write(new File("topic/" + i + "/" + docId + ".txt"), text, "UTF-8");
      }
      System.out.println("wrote query " + i);
    }
  }
  public String getText(String docId) {
    String rst = "";
    try {
      Response response1 = restClient.performRequest("GET",
              "ap_dataset/document/" + docId + "/_source");
      String str = EntityUtils.toString(response1.getEntity());
      JsonParser parser = new JsonParser();
      JsonObject object = parser.parse(str).getAsJsonObject();
      rst = object.getAsJsonPrimitive("text").getAsString();
      //System.out.println(rst);
    }catch (IOException e) {
      e.printStackTrace();
    }
    return rst;
  }

  public static void main(String[] str) throws IOException {
    RestClient restClient = RestClient.builder(
            new HttpHost("localhost", 9200, "http")).build();
    DataToFolder df = new DataToFolder(restClient);
    //df.read();
    //df.getText("AP890308-0175");
    restClient.close();
  }


}
