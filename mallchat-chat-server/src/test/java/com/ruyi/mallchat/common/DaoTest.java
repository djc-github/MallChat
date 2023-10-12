package com.ruyi.mallchat.common;

import com.ruyi.mallchat.common.common.utils.JwtUtils;
import com.ruyi.mallchat.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @author <a href="https://github.com/djc-github">ruyi</a>
 * @since 2023-10-03
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class DaoTest {
    public static final long UID = 6L;
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private WxMpService wxMpService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private LoginService loginService;
//    @Autowired
//    private UserBackpackService iUserBackpackService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Test
    public void test0() throws WxErrorException {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(1, 100000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);
    }

    @Test
    public void jwt() {
        String token = loginService.login(UID);
        System.out.println(token);
    }

    @Test
    public void redis() {
        String s = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjExMDAyLCJjcmVhdGVUaW1lIjoxNjkzNjYzOTU1fQ.qISTe8UDzggilWqz0HKtGLrkgiG1IRGafS10qHih9iM";
        Long validUid = loginService.getValidUid(s);
        System.out.println(validUid);
    }

    @Test
    public void acquireItem() {
//        iUserBackpackService.acquireItem(UID, ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, UID + "");
    }

    @Test
    public void thread() throws InterruptedException {
        threadPoolTaskExecutor.execute(() -> {
            if (1 == 1) {
                log.error("123");
                throw new RuntimeException("1243");
            }
        });
        Thread.sleep(200);
    }

    @Test
    public void test() throws WxErrorException {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(1, 10000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);
    }
}
