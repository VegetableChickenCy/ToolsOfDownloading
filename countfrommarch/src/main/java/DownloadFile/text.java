package DownloadFile;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class text {
  public static void main(String[] args) {

    ExecutorService executorService = Executors.newFixedThreadPool(3);

    Map<String, List<String>> mapList = new HashMap<String, List<String>>();
    FileSystemView fsv = FileSystemView.getFileSystemView();
    File com = fsv.getHomeDirectory();
    System.out.println(com.getPath());
    // 要查询的case单号
    String referenceNum = "";
    // policyNumber
    String policyNumber = "";
    referenceNum = text.getRefnumFromPolicyNumber(referenceNum, policyNumber);
    // 要下载的文件类型
    FileType fileType = FileType.bi;

    //    referenceNum = "";


//    String needFileString = "990811302_INS-30104169_1,990921001_INS-30912763_1,990892082_INS-30041103_1,990945921_INS-36168810_1,990951394_INS-30137864_1,991033867_INS-36168810_1,990861443_INS-36168810_1,991093524_INS-30046773_1,991123301_INS-36168810_1,991303473_INS-30912763_1,991417366_INS-36168810_1,991422055_INS-36168810_1,991428109_INS-30912763_1,991448823_INS-36168810_1,991602914_INS-36168810_1,991604768_INS-36168810_1,991613383_INS-36168810_1,991747230_INS-30912763_1,991874741_INS-36168810_1,991915002_INS-36168810_1,992032830_INS-36168810_1,992050248_INS-30912763_3,992050248_INS-30912763_4,992142838_INS-36168810_1,992150709_INS-36168810_1,991746125_INS-30912763_1,992243746_INS-30912763_2,992230702_INS-31761_1,992259437_INS-30137864_1,992325328_INS-30912763_1,992356191_INS-30137864_1,992581524_INS-36168810_1,992585227_INS-30912763_3,992585227_INS-30912763_4,992586790_INS-30912763_1,992583970_INS-36168810_1,992599529_INS-30137864_1,992631133_INS-36168810_1,992631133_INS-36168810_2,992702127_INS-36168810_1,992743795_INS-38588202_1,992768291_INS-36168810_1";
//    String[] split = needFileString.split(",");
//    List<String> strings1 = Arrays.asList(split);




    Enumeration<String> keys = ResourceBundleUtil.getBundle().getKeys();
    while (keys.hasMoreElements()) {
      String s = keys.nextElement();
        referenceNum += s;
        referenceNum += ",";
    }

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
    System.out.println("File type is '" + fileType + "'");
    System.out.println();
    List<String> doneList = new ArrayList<>();
/*    File folder = new File("C:\\Users\\caoluca\\Desktop\\0831");

    File[] files = folder.listFiles();
    for (File file : files) {
      String[] pis = file.getName().split("_PI");
      doneList.add(pis[0]);
    }*/

    for (String string : strings) {
      if (doneList.contains(string)) {
        continue;
      }

      fileType.setFileType(fileType);
      String format = String.format("%-27s", string);
      System.out.print(format);
      ArrayList<String> list = new ArrayList<>();
      mapList.put(string, list);
      list.add(string);
      FileType finalFileType = fileType;
      executorService.execute(
          new Runnable() {
            @Override
            public void run() {
              try {
                new text().download(string, finalFileType, mapList);
              } catch (Exception e) {
                System.out.println(":  failed");
                list.add("failed");
              }
            }
          });
    }
    System.out.println();
    System.out.println("start generate excel file to your desktop");
    executorService.shutdown();
    while (!executorService.isTerminated()) {
      int a = 1;
    }
    new text().exportLineById(com.getPath(), mapList);
    System.out.println("end");
  }

  public void download(String referenceNum, FileType fileType, Map<String, List<String>> mapList) {
    // 抓取payload信息
    /*    String getReturn =
        HttpURLConnectionUtil.doGet(
            "https://pos-data-sgp-service.apps.eas.pcf.manulife.com/v1/agents/readLog?reference="
                + referenceNum);
    JSONArray parse = (JSONArray) JSONObject.parse(getReturn);*/
    Map<String, String> map = new HashMap<>();
    // 由于customerId可能为空,所以需要遍历一下,直到找到不为空的customerId
    /*    for (Object r : parse) {
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
    }*/
    String agentId = map.get("agentId");
    String customerId = map.get("customerId");
    String reference = referenceNum;
    String docType = null;

    // 如果customerId依然为null，那我们需要用一个新的接口尝试去获取
    if (StringUtils.isEmpty(customerId)) {
      /*      if (null == agentId || null == reference) {
        throw new RuntimeException();
      }*/
      agentId = ResourceBundleUtil.getValue(reference);
      mapList.get(referenceNum).add(agentId);
      if (null == agentId) {
        System.out.println("Can't get agentId by " + reference);
      }
      // 抓取payload信息
      String getCustomer =
          HttpURLConnectionUtil.doGet(
              "https://pos-data-sgp-service.apps.eas.pcf.manulife.com/v1/agents/"
                  + agentId
                  + "/applications/"
                  + reference
                  + "?channel=DBS&systemId=SGX");
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
    HttpURLConnectionUtil.download(builder.toString(), mapList, referenceNum);
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
          System.out.println(string + ":" + " can't search");
          continue;
        }
        JSONObject r1 = (JSONObject) JSONObject.parse(s);

        String applicationId = r1.get("applicationId").toString();
        System.out.println(string + ":" + applicationId);
        refnum = refnum + applicationId + ",";
      }
    }
    return refnum;
  }

  public void exportLineById(String path, Map<String, List<String>> mapList) {
    String time = new SimpleDateFormat("MMddHHmmss").format(new Date());
    path += "\\" + time + ".xls";
    // 创建HSSFWorkbook
    HSSFWorkbook ws = ExcelUtil.setContent(mapList, time);
    File toFile = new File(path);
    try {
      OutputStream os = new FileOutputStream(toFile);
      System.out.println("Excel file name is " + toFile.getPath());
      ws.write(os);
      os.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void getFromResource() {
    ResourceBundle bundle = ResourceBundle.getBundle("caseinfo");
    System.out.println(1);
  }
}
