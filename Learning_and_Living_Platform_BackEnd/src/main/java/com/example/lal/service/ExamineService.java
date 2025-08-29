package com.example.lal.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 文本审核接口
 */
@Service
public class ExamineService {

    /**
     * 所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils
     */
    public static String TextCensor(String content) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined";
        try {
            String param = "text=" + content;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = "24.6a4672eb1a53b0521214de8e1bcf6a21.2592000.1688131613.282335-34229751";

            String result = HttpUtil.post(url, accessToken, param);
            // 使用Jackson库解析JSON字符串
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(result);

// 获取conclusion字段的值
            String conclusion = jsonNode.get("conclusion").asText();
            String reason = null;
            if(Objects.equals(conclusion, "不合规")){
                reason = jsonNode.get("data").get(0).get("msg").asText();
            }

// 打印conclusion值
//            System.out.println("Conclusion: " + conclusion);
//            System.out.println(reason);
            return reason;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) {
//        ExamineService.TextCensor();
//    }
}