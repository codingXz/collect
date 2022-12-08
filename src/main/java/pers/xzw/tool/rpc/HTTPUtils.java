package pers.xzw.tool.rpc;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Http请求
 * @author mszhou
 *
 */
public class HTTPUtils {
    private static final int TIMEOUT = 45000;
    public static final String ENCODING = "UTF-8";

    /**
     * 创建HTTP连接
     *
     * @param url
     *            地址
     * @param method
     *            方法
     * @param headerParameters
     *            头信息
     * @param body
     *            请求内容
     * @return
     * @throws Exception
     */
    private static HttpURLConnection createConnection(String url,
                                                      String method, Map<String, String> headerParameters, String body)
            throws Exception {
        URL Url = new URL(url);
        trustAllHttpsCertificates();
        HttpURLConnection httpConnection = (HttpURLConnection) Url
                .openConnection();
        // 设置请求时间
        httpConnection.setConnectTimeout(TIMEOUT);
        // 设置 header
        if (headerParameters != null) {
            Iterator<String> iteratorHeader = headerParameters.keySet()
                    .iterator();
            while (iteratorHeader.hasNext()) {
                String key = iteratorHeader.next();
                httpConnection.setRequestProperty(key,
                        headerParameters.get(key));
            }
        }
        httpConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded;charset=" + ENCODING);

        // 设置请求方法
        httpConnection.setRequestMethod(method);
        httpConnection.setDoOutput(true);
        httpConnection.setDoInput(true);
        // 写query数据流
        if (!(body == null || body.trim().equals(""))) {
            OutputStream writer = httpConnection.getOutputStream();
            try {
                writer.write(body.getBytes(ENCODING));
            } finally {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
        }

        // 请求结果
        int responseCode = httpConnection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception(responseCode
                    + ":"
                    + inputStream2String(httpConnection.getErrorStream(),
                    ENCODING));
        }

        return httpConnection;
    }

    /**
     * POST请求
     * @param address 请求地址
     * @param headerParameters 参数
     * @param body
     * @return
     * @throws Exception
     */
    public static String post(String address,
                              Map<String, String> headerParameters, String body) throws Exception {

        return proxyHttpRequest(address, "POST", null,
                getRequestBody(headerParameters));
    }

    /**
     * GET请求
     * @param address
     * @param headerParameters
     * @param body
     * @return
     * @throws Exception
     */
    public static String get(String address,
                             Map<String, String> headerParameters, String body) throws Exception {

        return proxyHttpRequest(address + "?"
                + getRequestBody(headerParameters), "GET", null, null);
    }

    public static String get(String address,
                             Map<String, String> param, Map<String, String> header, String body) throws Exception {

        return proxyHttpRequest(address + "?"
                + getRequestBody(param), "GET", header, null);
    }

    /**
     * 读取网络文件
     * @param address
     * @param headerParameters
     * @param file
     * @return
     * @throws Exception
     */
    public static String getFile(String address,
                                 Map<String, String> headerParameters, File file) throws Exception {
        String result = "fail";

        HttpURLConnection httpConnection = null;
        try {
            httpConnection = createConnection(address, "POST", null,
                    getRequestBody(headerParameters));
            result = readInputStream(httpConnection.getInputStream(), file);

        } catch (Exception e) {
            throw e;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }

        }

        return result;
    }

    public static byte[] getFileByte(String address,
                                     Map<String, String> headerParameters) throws Exception {
        byte[] result = null;

        HttpURLConnection httpConnection = null;
        try {
            httpConnection = createConnection(address, "POST", null,
                    getRequestBody(headerParameters));
            result = readInputStreamToByte(httpConnection.getInputStream());

        } catch (Exception e) {
            throw e;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }

        }

        return result;
    }

    /**
     * 读取文件流
     * @param in
     * @return
     * @throws Exception
     */
    public static String readInputStream(InputStream in, File file)
            throws Exception {
        FileOutputStream out = null;
        ByteArrayOutputStream output = null;

        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }

            out = new FileOutputStream(file);
            out.write(output.toByteArray());

        } catch (Exception e) {
            throw e;
        } finally {
            if (output != null) {
                output.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return "success";
    }

    public static byte[] readInputStreamToByte(InputStream in) throws Exception {
        FileOutputStream out = null;
        ByteArrayOutputStream output = null;
        byte[] byteFile = null;

        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            byteFile = output.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            if (output != null) {
                output.close();
            }
            if (out != null) {
                out.close();
            }
        }

        return byteFile;
    }

    /**
     * HTTP请求
     *
     * @param address
     *            地址
     * @param method
     *            方法
     * @param headerParameters
     *            头信息
     * @param body
     *            请求内容
     * @return
     * @throws Exception
     */
    public static String proxyHttpRequest(String address, String method,
                                          Map<String, String> headerParameters, String body) throws Exception {
        String result = null;
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createConnection(address, method,
                    headerParameters, body);

            String encoding = "UTF-8";
            if (httpConnection.getContentType() != null
                    && httpConnection.getContentType().indexOf("charset=") >= 0) {
                encoding = httpConnection.getContentType()
                        .substring(
                                httpConnection.getContentType().indexOf(
                                        "charset=") + 8);
            }
            result = inputStream2String(httpConnection.getInputStream(),
                    encoding);
            // logger.info("HTTPproxy response: {},{}", address,
            // result.toString());

        } catch (Exception e) {
            // logger.info("HTTPproxy error: {}", e.getMessage());
            throw e;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return result;
    }

    /**
     * 将参数化为 body
     * @param params
     * @return
     */
    public static String getRequestBody(Map<String, String> params) {
        return getRequestBody(params, true);
    }

    /**
     * 将参数化为 body
     * @param params
     * @return
     */
    public static String getRequestBody(Map<String, String> params,
                                        boolean urlEncode) {
        StringBuilder body = new StringBuilder();

        Iterator<String> iteratorHeader = params.keySet().iterator();
        while (iteratorHeader.hasNext()) {
            String key = iteratorHeader.next();
            String value = params.get(key);

            if (urlEncode) {
                try {
                    body.append(key + "=" + URLEncoder.encode(value, ENCODING)
                            + "&");
                } catch (UnsupportedEncodingException e) {
                    // e.printStackTrace();
                }
            } else {
                body.append(key + "=" + value + "&");
            }
        }

        if (body.length() == 0) {
            return "";
        }
        return body.substring(0, body.length() - 1);
    }

    /**
     * 读取inputStream 到 string
     * @param input
     * @param encoding
     * @return
     * @throws IOException
     */
    private static String inputStream2String(InputStream input, String encoding)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input,
                encoding));
        StringBuilder result = new StringBuilder();
        String temp = null;
        while ((temp = reader.readLine()) != null) {
            result.append(temp);
        }

        return result.toString();

    }


    /**
     * 设置 https 请求
     * @throws Exception
     */
    private static void trustAllHttpsCertificates() throws Exception {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String str, SSLSession session) {
                return true;
            }
        });
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }


    //设置 https 请求证书
    static class miTM implements javax.net.ssl.TrustManager,javax.net.ssl.X509TrustManager {

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }


    }

    //====================================================================
    //============================= 测试调用   ============================
    //====================================================================
    public static void main(String[] args) {

        try {
            // 同一个诊所下同一个pid类型下的疫苗 在同一天内 mxid是没有发生变化的。 如何通过代码在拿到当天的疫苗的mxid （目前的方式是通过抓包在页面上点击提交后才生成）

            //String address = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm";
            String address ="https://cloud.cn2030.com/sc/wx/HandlerSubscribe.ashx?act=UserSubcribeList";
            String address2 = "https://cloud.cn2030.com/sc/wx/HandlerSubscribe.ashx?act=CustomerProduct&id=4219&lat=22.53332&lng=113.93041"; //查询当前诊所下可以接种哪些疫苗
            String addressQueryState = "https://cloud.cn2030.com/sc/wx/HandlerSubscribe.ashx?act=GetOrderStatus";
            String addressQueryAllDate = "https://cloud.cn2030.com/sc/wx/HandlerSubscribe.ashx?act=GetCustSubscribeDateAll&pid=35&id=4219&month=202110"; //查询当前诊所下当前疫苗可预约的天数 不受zftsl的影响

            String addressGetCaptcha = "https://cloud.cn2030.com/sc/wx/HandlerSubscribe.ashx?act=GetCaptcha&mxid=DurTAG96AABGZTQB";
            String submitUrl = "https://cloud.cn2030.com/sc/wx/HandlerSubscribe.ashx?act=Save20&birthday=1997-01-28&tel=17369397297&sex=2&cname=张婷玉&doctype=1&idcard=430426199701283485&mxid=DurTAG96AABGZTQB&date=2021-10-14&pid=35&Ftime=3&guid=";

            String test1 = "https://cloud.cn2030.com/sc/wx/HandlerSubscribe.ashx?act=GetCaptcha&mxid=DurTABN2AABKZTQB";
            String test2 = "https://cloud.cn2030.com/sc/wx/HandlerSubscribe.ashx?act=Save20&birthday=1997-01-28&tel=17369397297&sex=2&cname=张婷玉&doctype=1&idcard=430426199701283485&mxid=DurTABN2AABKZTQB&date=2021-10-18&pid=54&Ftime=1&guid=";
            Map<String, String> header = new HashMap<String, String>();
            header.put("Host", "cloud.cn2030.com");
            header.put("Connection", "keep-alive");

            header.put("Cookie", "ASP.NET_SessionId=hivxuuv3gl21icajyz312vu1");
            header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI MiniProgramEnv/Windows WindowsWechat");
            header.put("content-type", "application/json");
            //header.put("zftsl", "672f6f859b537f02b53c912267686943");
            header.put("Accept-Encoding", "gzip, deflate, br");
            header.put("Referer", "https://servicewechat.com/wx2c7f0f3c30d99445/86/page-frame.html");
            //header.put("zftsl", "45216917c1db2bbc1bd1b37699684058");
            header.put("zftsl", "00688567e1d352c610a3927b6338a9a1");


            //请求参数
           /* Map<String, String> params = new HashMap<String, String>();
            params.put("tel", "13777777777");//这是该接口需要的参数*/

            // 调用 get 请求
            String res = get(test2, new HashMap<>(), header, null);
            System.out.println(res);//打印返回参数

            /*res = res.substring(res.indexOf("{"));//截取
            JSONObject result = JSONObject.parseObject(res);//转JSON

            System.out.println(result.toString());//打印*/
            // 提前将校验的请求发送过去，在进行请求的提交就能成功
        } catch (Exception e) {
            // TODO 异常
            e.printStackTrace();
        }

    }

}