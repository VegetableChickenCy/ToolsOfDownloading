package DownloadFile;

import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.File;
import java.io.IOException;

public class text {
  public static void main(String[] args) {

    // 文件夹地址
    String fileAddress = "C:\\Users\\caoluca\\Desktop\\SpaceX\\eApp-PDF\\7.22-lucas-7";
    if (args.length>=1) {
      fileAddress = args[0];
    }
    System.out.println();
    System.out.println("Start read pages number in "+fileAddress);
    System.out.println();
    System.out.println();
    File files = new File(fileAddress);
    File[] fileList = files.listFiles();
    for (File file : fileList) {
      PDDocument load = null;
      try {
        load = PDDocument.load(file);
        int numberOfPages = load.getNumberOfPages();

        String str = String.format("%-40s", file.getName());

        System.out.println(str+":   "+numberOfPages);
      } catch (IOException e) {
        System.out.println(file.getName()+" is not correct");
        continue;
      } finally{
        try {
          load.close();
        } catch (IOException e) {
          continue;
        }
      }
    }
    System.out.println();
    System.out.println();
    System.out.println("End");
  }
}
