package DownloadFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class text {
  public static void main(String[] args) {
    LinkedList<List<String>> listList = new LinkedList<>();
    FileSystemView fsv = FileSystemView.getFileSystemView();
    File com=fsv.getHomeDirectory();
    System.out.println(com.getPath());
    // 要查询的case单号
    String referenceNum = "993480867_INS-36168810_1,993468689_INS-20033904_1,992474061_INS-30137864_1,993456097_INS-38588202_1,993459525_INS-20033905_1";
    // policyNumber
    String policyNumber = "";
    referenceNum = text.getRefnumFromPolicyNumber(referenceNum,policyNumber);
    // 要下载的文件类型
    FileType fileType = FileType.eApp;


    if (args.length > 1) {
      referenceNum = args[0];
      try {
        fileType = FileType.valueOf(args[1]);
      } catch (IllegalArgumentException e) {
        System.out.println("The file type should be one of 'eApp','bi','casedata'");
        Thread.currentThread().stop();
      }

    }
    List<String> strings = Arrays.asList(referenceNum.split(","));
    System.out.println();
    System.out.println("File type is '"+fileType+"'");
    System.out.println();

    for (String string : strings) {
      fileType.setFileType(fileType);
      String format = String.format("%-27s", string);
      System.out.print(format);
      ArrayList<String> list = new ArrayList<>();
      listList.add(list);
      list.add(string);
      try {
        new text().download(string, fileType,listList);
      } catch (Exception e) {
        System.out.println(":  failed");
        list.add("failed");
        continue;
      }
    }
    System.out.println();
    System.out.println("start generate excel file to your desktop");
    new text().exportLineById(com.getPath(),listList);
    System.out.println("end");
  }

  public void download(String referenceNum, FileType fileType,LinkedList<List<String>> listList) {
    // 抓取payload信息
    String getReturn =
        HttpURLConnectionUtil.doGet(
            "https://pos-data-sgp-service.apps.eas.pcf.manulife.com/v1/agents/readLog?reference="
                + referenceNum);
    JSONArray parse = (JSONArray) JSONObject.parse(getReturn);
    Map<String, String> map = new HashMap<>();
    // 由于customerId可能为空,所以需要遍历一下,直到找到不为空的customerId
    for (Object r : parse) {
      JSONObject r1 = (JSONObject) r;
      String reference = r1.get("reference").toString();
      String agentId = //"470493";
                    r1.get("agentId").toString();
      String customerId = //"D18NA9058P";
                    r1.get("customerId").toString();
      if (!StringUtils.isEmpty(reference)) {
        map.put("reference", reference);
      }
      if (!StringUtils.isEmpty(agentId)) {
        map.put("agentId", agentId);
      }
      if (!StringUtils.isEmpty(customerId)) {
        map.put("customerId", customerId);
      }
      if (!StringUtils.isEmpty(map.get("customerId"))) {
        break;
      }
    }
    String agentId = map.get("agentId");
    String customerId = map.get("customerId");
    String reference = map.get("reference");
    String docType = null;

    //如果customerId依然为null，那我们需要用一个新的接口尝试去获取
    if (StringUtils.isEmpty(customerId)) {
      if (null == agentId || null == reference) {
        throw new RuntimeException();
      }
      // 抓取payload信息
      String getCustomer =
              HttpURLConnectionUtil.doGet("https://pos-data-sgp-service.apps.eas.pcf.manulife.com/v1/agents/"+agentId+"/applications/"+reference+"?channel=DBS&systemId=SGX");
      JSONObject parseCus = (JSONObject) JSONObject.parse(getCustomer);
      // 由于customerId可能为空,所以需要遍历一下,直到找到不为空的customerId
      if (parseCus.containsKey("customerId")) {
        Object customerObject = parseCus.get("customerId");
        if (null != customerObject) {
          String customer = customerObject.toString();
          if (!StringUtils.isEmpty(customer)) {
            map.put("customerId", customer);
          }
        }
      }
      customerId = map.get("customerId");
    }

    docType =
        fileType == FileType.bi
            ? "PROPOSAL"
            : fileType == FileType.eApp
                ? "POLICY_APPLICATION_FORM"
                : fileType == FileType.casedata ? "CASEDATA" : null;
    String salt = "caaab29c-9ca6-4a8e-a0ab-5901f46d6efe"; // 生产
    // QA		String salt = "2afea7b4-baa9-406e-acfa-4ae75c1f189c";
    StringBuilder stringToHash = new StringBuilder();
    stringToHash
        .append(agentId)
        .append(":")
        .append(customerId)
        .append(":")
        .append(reference)
        .append(":")
        .append(docType)
        .append(":")
        .append(salt);
    String hashString = stringToHash.toString();
    // 计算下载文件接口所需要的ID
    String SHAnumber = SHAEncryptionUtility.generateSHA1Hash(hashString);

    // https://spacex-ms-yfj-simulator-app.apps.eas.pcf.manulife.com/yfjDocument/agents/471754/customers/K18N18183X/applications/992239176_INS-36168810_1/documents/eApp?id=6f185460c6661ae2819efd6e36e32e9d8c4906d2
    StringBuilder builder = new StringBuilder();
    // 构建下载接口参数
    builder
        .append("https://spacex-ms-yfj-simulator-app.apps.eas.pcf.manulife.com/yfjDocument/agents/")
        .append(agentId)
        .append("/customers/")
        .append(customerId)
        .append("/applications/")
        .append(reference)
        .append("/documents/")
        .append(fileType.toString())
        .append("?id=")
        .append(SHAnumber);
    // 下载文件
    HttpURLConnectionUtil.download(builder.toString(),listList);
  }

  private static String getRefnumFromPolicyNumber(String refnum, String policyNumber) {
    if (!StringUtils.isEmpty(policyNumber)) {
      if (!StringUtils.isEmpty(refnum)) {
        refnum += ",";
      }
      List<String> strings = Arrays.asList(policyNumber.split(","));
      for (String string : strings) {
        String s =
            HttpURLConnectionUtil.doGet(
                "https://pos-data-sgp-service.apps.eas.pcf.manulife.com/v1/applications/policyNumber/"
                    + string
                    + "?systemId=SGX");
        if (StringUtils.isEmpty(s)) {
          System.out.println(string+":"+" can't search");
          continue;
        }
        JSONObject r1 = (JSONObject) JSONObject.parse(s);

          String applicationId = r1.get("applicationId").toString();
          System.out.println(string+":"+applicationId);
          refnum = refnum+applicationId+",";
      }
    }
    return refnum;
  }

  public void exportLineById(String path, LinkedList<List<String>> linkedList) {
    String time = new SimpleDateFormat("MMddHHmmss").format(new Date());
    path += "\\"+time+".xls";
            //创建HSSFWorkbook
    HSSFWorkbook ws = ExcelUtil.setContent(linkedList, time);
    File toFile = new File(path);
    try {
      OutputStream os = new FileOutputStream(toFile);
      ws.write(os);
      os.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
