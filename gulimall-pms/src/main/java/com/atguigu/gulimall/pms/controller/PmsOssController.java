package com.atguigu.gulimall.pms.controller;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.atguigu.gulimall.commons.bean.Resp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * author:hcl
 */
@RequestMapping("/pms/oss")
@RestController
public class PmsOssController {
    String accessId = "LTAI1z6ayHpNWhSY";      // 请填写您的AccessKeyId。
    String accessKey = "d33P5nv07EJNsIj1eKby83yqj69Xd0"; // 请填写您的AccessKeySecret。
    String endpoint = "oss-cn-shanghai.aliyuncs.com"; // 请填写您的 endpoint。
    String bucket = "guliadmin";                    // 请填写您的 bucketname 。
    String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint


    @GetMapping("/policy")
    public Resp<Object> osspolicy() throws UnsupportedEncodingException {
        OSSClient client = new OSSClient(endpoint, accessId, accessKey);
        String dir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        long expireTime = 30;
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        Date expiration = new Date(expireEndTime);
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

        String postPolicy = client.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes("utf-8");
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = client.calculatePostSignature(postPolicy);

        Map<String, String> respMap = new LinkedHashMap<String, String>();
        respMap.put("accessid", accessId);
        respMap.put("policy", encodedPolicy);
        respMap.put("signature", postSignature);
        //给前端指定上传到哪个文件夹
        respMap.put("dir", dir);
        respMap.put("host", host);
        respMap.put("expire", String.valueOf(expireEndTime / 1000));
        // respMap.put("expire", formatISO8601Date(expiration));
        return Resp.ok(respMap);

    }
}
