package com.skytakeout;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;


@SpringBootTest
public class HttpClientTest {

    @Test
    public void httpGetTest() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8081/user/shop/status");

        CloseableHttpResponse executed = client.execute(httpGet);
        int statusCode = executed.getStatusLine().getStatusCode();
        System.out.println(statusCode);

        String body = EntityUtils.toString(executed.getEntity());
        System.out.println(body);

        executed.close();

        client.close();
    }
    @Test
    public void httpPostTest() throws IOException, JSONException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8081/admin/employee/login");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "admin");
        jsonObject.put("password", "123456");

        StringEntity entity = new StringEntity(jsonObject.toString());
        entity.setContentEncoding("utf-8");
        entity.setContentType("application/json");

        httpPost.setEntity(entity);
        CloseableHttpResponse response = client.execute(httpPost);

        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode);

        HttpEntity entityRes = response.getEntity();
        String string = EntityUtils.toString(entityRes);
        System.out.println(string);

        response.close();
        client.close();
    }
}
