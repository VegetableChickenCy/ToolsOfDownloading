package DownloadFile;

import org.apache.maven.shared.utils.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpURLConnectionUtil {


  public static String doGet(String httpUrl) {
    // 链接
    HttpURLConnection connection = null;
    InputStream is = null;
    BufferedReader br = null;
    StringBuffer result = new StringBuffer();
    try {
      // 创建连接
      URL url = new URL(httpUrl);
      connection = (HttpURLConnection) url.openConnection();
      // 设置请求方式
      connection.setRequestMethod("GET");
      // 设置连接超时时间
      connection.setReadTimeout(30000);
      // 开始连接
      connection.connect();
      // 获取响应数据
      if (connection.getResponseCode() == 200) {
        // 获取返回的数据
        is = connection.getInputStream();
        if (null != is) {
          br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
          String temp = null;
          while (null != (temp = br.readLine())) {
            result.append(temp);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (null != br) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (null != is) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      // 关闭远程连接
      connection.disconnect();
    }
    return result.toString();
  }

  public static void download(
      String httpUrl, Map<String, List<String>> mapList, String referenceNum) {

    // 链接
    HttpURLConnection connection = null;
    InputStream is = null;
    BufferedReader br = null;
    // 输出流
    OutputStream out = null;
    try {
      // 创建连接
      URL url = new URL(httpUrl);
      connection = (HttpURLConnection) url.openConnection();
      // 设置请求方式
      connection.setRequestMethod("GET");
      // 设置连接超时时间
      connection.setReadTimeout(30000);
      // 开始连接
      connection.connect();
      // 获取响应数据
      if (connection.getResponseCode() == 200) {
        int fileLength = connection.getContentLength();
        // 获取返回的数据
        is = connection.getInputStream();
        if (null != is) {

/*          PDDocument load = PDDocument.load(is);
          int numberOfPages = load.getNumberOfPages();
          mapList.get(referenceNum).add(String.valueOf(numberOfPages));
          System.out.println(referenceNum+" : "+numberOfPages);*/

          // 如果要下载就解开注释
          BufferedInputStream bin = new BufferedInputStream(is);
          File file = null;
          file = new File("C:\\Users\\caoluca\\Desktop\\0908\\" + referenceNum + "_Proposal.pdf");
          int size = 0;
          int len = 0;
          byte[] buf = new byte[1024];
          out = new FileOutputStream(file);
          while ((size = bin.read(buf)) != -1) {
            len += size;
            out.write(buf, 0, size);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (null != br) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (null != is) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (null != out) {
        try {
          out.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      // 关闭远程连接
      connection.disconnect();
    }
  }
}
