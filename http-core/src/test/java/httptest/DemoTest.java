package httptest;

import com.raiden.tool.http.HttpBootStrap;
import httptest.remote.HttpTest;

import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 18:44
 **/
public class DemoTest {


    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException {
        final HttpBootStrap httpBootStrap = HttpBootStrap.builder().build().setSelfManager(true);
        httpBootStrap.init("httptest.remote");
        final long t1 = System.currentTimeMillis();
        httpBootStrap.getProxy(HttpTest.class).test();
        final long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
        final long t3 = System.currentTimeMillis();
        httpBootStrap.getProxy(HttpTest.class).test();
        final long t4 = System.currentTimeMillis();
        System.out.println(t4-t3);
    }

}
