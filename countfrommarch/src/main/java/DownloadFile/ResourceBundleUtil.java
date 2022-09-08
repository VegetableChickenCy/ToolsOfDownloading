package DownloadFile;

import java.util.ResourceBundle;

public class ResourceBundleUtil {

  private static ResourceBundle bundle;

  public static ResourceBundle getBundle() {
    if (bundle == null) {
      bundle = ResourceBundle.getBundle("caseinfo");
    }
    return bundle;
  }

  public static String getValue(String key){
    if (null == bundle) {
      getBundle();
    }
    return bundle.getString(key);
  }

}
