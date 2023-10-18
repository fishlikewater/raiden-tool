package com.raiden.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileMode;
import com.raiden.tool.enitty.LoginBo;
import com.raiden.tool.enitty.LoginEntity;
import com.raiden.tool.enitty.ResponseEntity;
import com.raiden.tool.http.HttpBootStrap;
import com.raiden.tool.remote.HttpTest;
import com.raiden.tool.remote.JsonTest;
import com.raiden.tool.remote.TestRemote;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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
        HttpBootStrap.init("com.raiden.tool.remote");
        HttpBootStrap.registerHttpClient("third", HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build());
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
    public void test2() {
        final long t1 = System.currentTimeMillis();
        HttpBootStrap.getProxy(HttpTest.class).test1("1");
        final long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
    }

    @Test
    public void test3() {
        final long t1 = System.currentTimeMillis();
        HttpBootStrap.getProxy(HttpTest.class).test2("1");
        final long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
    }

    @Test
    public void testJson(){
        final LoginBo loginBo = new LoginBo();
        loginBo.setCode("111111");
        loginBo.setPhone("13002355860");
        final ResponseEntity<LoginEntity> login = HttpBootStrap.getProxy(JsonTest.class).login(loginBo);
        System.out.println(login.getResult());
    }


    @Test
    public void testFile() throws InterruptedException {
        HttpBootStrap.getProxy(TestRemote.class).download2().thenAccept(bytes -> {
            try {
                final File file = FileUtil.file("E:\\opt\\2.png");
                file.createNewFile();
                final RandomAccessFile randomAccessFile = FileUtil.createRandomAccessFile(file, FileMode.rw);
                randomAccessFile.write(bytes);
                randomAccessFile.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Thread.sleep(10*1000);
    }

}
