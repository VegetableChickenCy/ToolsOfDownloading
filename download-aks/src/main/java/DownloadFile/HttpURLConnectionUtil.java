package DownloadFile;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpURLConnectionUtil {

    private static String token;
    private static String yfjToken;

    public static String doGet(String httpUrl,boolean retry){
        //链接
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer result = new StringBuffer();
        try {
            //创建连接
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("GET");
            //设置连接超时时间
            connection.setReadTimeout(15000);
            //token设置
            connection.setRequestProperty("Authorization",token);
            //开始连接
            connection.connect();
            //获取响应数据
            if (connection.getResponseCode() == 200) {
                //获取返回的数据
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
            } else if(connection.getResponseCode() == 401 && !retry) {
                refreshToken();
                return doGet(httpUrl,true);
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
            //关闭远程连接
            connection.disconnect();
        }
        return result.toString();
    }

    public static void download(String httpUrl,String fileName,String filePath, boolean retry){
        //链接
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        //输出流
        OutputStream out = null;
        try {
            //创建连接
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("GET");
            //设置连接超时时间
            connection.setReadTimeout(15000);
            //token设置
            connection.setRequestProperty("Authorization",yfjToken);
            //开始连接
            connection.connect();
            //获取响应数据
            if (connection.getResponseCode() == 200) {
                int fileLength = connection.getContentLength();
                //获取返回的数据
                is = connection.getInputStream();
                if (null != is) {
                    BufferedInputStream bin = new BufferedInputStream(is);
                    File file = null;
                    if (StringUtils.isNotEmpty(filePath)) {
                        if (fileName.contains("casedata")) {
                            file = new File(filePath+"\\"+fileName+".xml");
                        }else {
                            file = new File(filePath+"\\"+fileName+".pdf");
                        }
                    } else {
                        file = new File("C:\\Users\\caoluca\\Desktop\\"+fileName+".pdf");
                    }
                    int size = 0;
                    int len = 0;
                    byte[] buf = new byte[1024];
                    out = new FileOutputStream(file);
                    while ((size = bin.read(buf)) != -1) {
                    len += size;
                     out.write(buf, 0, size);
                     }
                }
                //关闭远程连接
                System.out.println("Download completed:"+filePath+"\\"+fileName+".pdf");
            } else if(connection.getResponseCode() == 401 && !retry) {
                refreshYfjToken();
                download(httpUrl,fileName,filePath,true);
            }
        } catch (IOException e) {
            System.out.println("Download failed:"+e.getMessage());
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

            connection.disconnect();
        }
    }


    public static void refreshToken() throws IOException {
        HashMap<String, String> bodyMap = new HashMap<>();
        bodyMap.put("client_id","af1cc3ab-7b4f-4a18-81dc-43b933122800");
        bodyMap.put("client_secret","Utx7Q~RTQdb9dF8HJpVEO9ugoEABDDWT53PMy");
        bodyMap.put("grant_type","client_credentials");
        bodyMap.put("scope","api://55c682d2-3e7d-4117-ac56-b1e1a379ee91/.default");
        String post = HttpURLConnectionUtil.post("https://login.microsoftonline.com/5d3e2773-e07f-4432-a630-1a0f68a28a05/oauth2/v2.0/token", null, bodyMap, null);
        JSONObject parse = (JSONObject) JSONObject.parse(post);
        String access_token = parse.get("access_token").toString();
        token = "Bearer " + access_token;
    }

    public static void refreshYfjToken() throws IOException {
        String post = HttpURLConnectionUtil.post("https://emm-prd-api.ap.manulife.com/int/pos-yfj-simulator-app-sgx/yfj/token", null, null, null);
        yfjToken = "Bearer " + post;
    }

    /**
     * Http请求之基于HttpUrlConnection，支持Header,Body传值，支持Multipart上传文件：
     * @return
     * @throws IOException
     */
    public static String post(String actionUrl, Map<String, String> headParams,
                              Map<String, String> params,
                              Map<String, File> files) throws IOException {

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";

        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(30 * 1000); // 缓存的最长时间
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                + ";boundary=" + BOUNDARY);
        if(headParams!=null){
            for(String key : headParams.keySet()){
                conn.setRequestProperty(key, headParams.get(key));
            }
        }
        StringBuilder sb = new StringBuilder();

        if (params!=null) {
            // 首先组拼文本类型的参数
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\""
                        + entry.getKey() + "\"" + LINEND);
                sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                sb.append(LINEND);
                sb.append(entry.getValue());
                sb.append(LINEND);
            }

        }

        DataOutputStream outStream = new DataOutputStream(
                conn.getOutputStream());
        if (!StringUtils.isEmpty(sb.toString())) {
            outStream.write(sb.toString().getBytes());
        }


        // 发送文件数据
        if (files != null)
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + file.getKey() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());

                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();

        // 得到响应码
        int res = conn.getResponseCode();
        InputStream in = conn.getInputStream();
        if (res == 200) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                buffer.append(line);
            }

//          int ch;
//          StringBuilder sb2 = new StringBuilder();
//          while ((ch = in.read()) != -1) {
//              sb2.append((char) ch);
//          }
            return buffer.toString();
        }
        outStream.close();
        conn.disconnect();
        return in.toString();

    }
}
