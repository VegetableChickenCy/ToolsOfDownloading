package DownloadFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.*;

public class text {
  public static void main(String[] args) {

    // 要查询的case单号
    String referenceNum = "993487621_INS-36168810_1,993604260_INS-30137864_1";
    // policyNumber
    String policyNumber = "";
    referenceNum = text.getRefnumFromPolicyNumber(referenceNum,policyNumber);
    // 要下载的文件类型
    FileType fileType = FileType.bi;
    // 下载地址(默认caoluca的桌面)
    String filePath = null;
    FileSystemView fsv = FileSystemView.getFileSystemView();
    File com = fsv.getHomeDirectory();
    filePath = com.getPath();

    if (args.length > 1) {
      referenceNum = args[0];
      try {
        fileType = FileType.valueOf(args[1]);
      } catch (IllegalArgumentException e) {
        System.out.println("The file type should be one of 'eApp','bi','casedata'");
        Thread.currentThread().stop();
      }
      if (args.length == 3) {
        filePath = args[2];
      }
    }
    List<String> strings = Arrays.asList(referenceNum.split(","));
    for (String string : strings) {
      fileType.setFileType(fileType);
      new text().download(string, fileType, filePath);
    }
  }

  public void download(String referenceNum, FileType fileType, String filePath) {
    // 抓取payload信息
/*

    String getReturn1 =
            HttpURLConnectionUtil.doGet("https://sea-emm-prd-api.ap.manulife.com/int/pos-apply-service-sgx/v1/sgp/agents/500464/customers/C22019020Y/applications/990934040_INS-20033904_1/documents/meta?channel=DBS&docType=PROPOSAL&docType=POLICY_APPLICATION_FORM&systemId=SGX",false);

    System.out.println(getReturn1);
*/

    String getReturn =
        HttpURLConnectionUtil.doGet(
            "https://sea-emm-prd-api.ap.manulife.com/int/pos-data-service-sgx/v1/agents/readLog?reference="
                + referenceNum,false);
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
//    String reference = map.get("reference");
    String docType = null;


    //如果customerId依然为null，那我们需要用一个新的接口尝试去获取
    if (StringUtils.isEmpty(customerId)) {
      if (null == agentId) {
        agentId = ResourceBundleUtil.getValue(referenceNum);
        if (null == agentId) {
          System.out.println(referenceNum+" Can't get necessary parameters");
          return;
        }
      }
      // 抓取payload信息
      String getCustomer =
              HttpURLConnectionUtil.doGet("https://sea-emm-prd-api.ap.manulife.com/int/pos-data-service-sgx/v1/agents/"+agentId+"/applications/"+referenceNum+"?channel=DBS&systemId=SGX",false);
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
        .append(referenceNum)
        .append(":")
        .append(docType)
        .append(":")
        .append(salt);
    String hashString = stringToHash.toString();
    // 计算下载文件接口所需要的ID
    String SHAnumber = SHAEncryptionUtility.generateSHA1Hash(hashString);

    // https://spacex-ms-yfj-simulator-app.apps.eas.pcf.manulife.com/yfjDocument/agents/471754/customers/K18N18183X/applications/992239176_INS-36168810_1/documents/eApp?id=6f185460c6661ae2819efd6e36e32e9d8c4906d2
    System.out.println();
    System.out.println("reference:" + referenceNum);
    System.out.println("agentId:" + agentId);
    System.out.println("customerId:" + customerId);
    System.out.println("id:" + SHAnumber);
    StringBuilder builder = new StringBuilder();
    // 构建下载接口参数
    //https://sea-emm-prd-api.ap.manulife.com/int/pos-yfj-simulator-app-sgx/agents/
    builder
        .append("https://sea-emm-prd-api.ap.manulife.com/int/pos-yfj-simulator-app-sgx/yfjDocument/agents/")
        .append(agentId)
        .append("/customers/")
        .append(customerId)
        .append("/applications/")
        .append(referenceNum)
        .append("/documents/")
        .append(fileType.toString())
        .append("?id=")
        .append(SHAnumber);
    // 下载文件
    HttpURLConnectionUtil.download(builder.toString(), referenceNum + "_" + fileType.getFileType(), filePath,false);
  }

  private static String getRefnumFromPolicyNumber(String refnum, String policyNumber) {
    if (!StringUtils.isEmpty(policyNumber)) {
      if (org.apache.commons.lang3.StringUtils.isNotEmpty(refnum)) {
        refnum += ",";
      }
      List<String> strings = Arrays.asList(policyNumber.split(","));
      for (String string : strings) {
        String s =
            HttpURLConnectionUtil.doGet(
                "https://sea-emm-prd-api.ap.manulife.com/int/pos-data-service-sgx/v1/applications/policyNumber/"
                    + string
                    + "?systemId=SGX",false);
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
}
