package DownloadFile;



public class text {
  public static void main(String[] args) {
    String refnum = "909257772_INS-4000025_1";
    String agentId = "111039";
    String customerId = "G21199960B";
    String fileType = "bi";
    if (args.length >= 4) {
      refnum = args[0];
      agentId = args[1];
      customerId = args[2];
      fileType = args[3];
    } else {
      System.out.println("parameter wrong");
      Thread.currentThread().stop();
    }

    if (fileType.equalsIgnoreCase("eApp")) {
      fileType = "POLICY_APPLICATION_FORM";
    } else if (fileType.equalsIgnoreCase("bi")) {
      fileType = "PROPOSAL";
    } else {
      System.out.println("pls type eApp or bi");
      Thread.currentThread().stop();
    }

    String salt = "caaab29c-9ca6-4a8e-a0ab-5901f46d6efe";//生产
    if (args.length == 5 && !args[4].equalsIgnoreCase("prod")) {
      salt = "2afea7b4-baa9-406e-acfa-4ae75c1f189c";
    }
    StringBuilder stringToHash = new StringBuilder();
    stringToHash.append(agentId).append(":").append(customerId).append(":").append(refnum).append(":")
            .append(fileType).append(":").append(salt);
    String hashString = stringToHash.toString();
    String SHAnumber = SHAEncryptionUtility.generateSHA1Hash(hashString);
    System.out.println("id:"+SHAnumber);
  }
}