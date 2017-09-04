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
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class EntireAP2Folder {

  List<String> docList;
  RestClient restClient;

  public EntireAP2Folder(RestClient restClient) throws IOException {
    docList = new LinkedList<>();
    this.restClient = restClient;
    read();
    getES();
  }
  //read bm25
  public void read() throws FileNotFoundException {
    Scanner sc = new Scanner(new File("doclist_new_0609.txt"));
    while (sc.hasNextLine()) {
      String[] vals = sc.nextLine().split("  ");
      String docNum = vals[1];
      docList.add(docNum);
    }
    System.out.println(docList.size() + " topics read");
  }
  //retrieve from elastic search
  public void getES() throws IOException {
      int count = 0;
      for (String docId : docList) {
        String text = getText(docId);
        count ++;
        FileUtils.write(new File("/Users/sophie/Desktop/AP89/" + docId + ".txt"), text, "UTF-8");
        if (count % 100 == 0) {
          System.out.println("wrote files: " + count);
        }
        break;
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
    EntireAP2Folder e2f = new EntireAP2Folder(restClient);
    restClient.close();
  }
}
