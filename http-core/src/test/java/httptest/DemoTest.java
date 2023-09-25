package httptest;

import com.raiden.tool.http.HttpBootStrap;
import httptest.enitty.LoginBo;
import httptest.enitty.LoginEntity;
import httptest.enitty.ResponseEntity;
import httptest.remote.HttpTest;
import httptest.remote.JsonTest;
import org.junit.Before;
import org.junit.Test;

import java.net.http.HttpClient;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 18:44
 **/
public class DemoTest {


    @Before
    public void before() throws ClassNotFoundException {
        HttpBootStrap.setSelfManager(true);
        HttpBootStrap.init("httptest.remote");
        HttpBootStrap.registerHttpClient("third", HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build());
        HttpBootStrap.getLogConfig().setEnableLog(true);

    }

    @Test
    public void test() {

        final long t1 = System.currentTimeMillis();
        HttpBootStrap.getProxy(HttpTest.class).test();
        final long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);

        final long t3 = System.currentTimeMillis();
        HttpBootStrap.getProxy(HttpTest.class).test();
        final long t4 = System.currentTimeMillis();
        System.out.println(t4-t3);
    }

    @Test
    public void testJson(){
        final LoginBo loginBo = new LoginBo();
        loginBo.setCode("111111");
        loginBo.setPhone("13002355860");
        final ResponseEntity<LoginEntity> login = HttpBootStrap.getProxy(JsonTest.class).login(loginBo);
        System.out.println(login.getResult());
    }


}
