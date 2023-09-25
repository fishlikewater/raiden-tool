package com.raiden.http.core.springboot.test;

import com.raiden.http.core.springboot.test.enitty.LoginBo;
import com.raiden.http.core.springboot.test.enitty.LoginEntity;
import com.raiden.http.core.springboot.test.enitty.ResponseEntity;
import com.raiden.http.core.springboot.test.remote.JsonTest;
import com.raiden.http.core.springboot.test.remote.TestRemote;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月22日 19:31
 **/
@Component
@RequiredArgsConstructor
public class TestDemo implements CommandLineRunner {

    private final TestRemote testRemote;
    private final JsonTest jsonTest;

    @Override
    public void run(String... args) {

        final LoginBo loginBo = new LoginBo();
        loginBo.setCode("111111");
        loginBo.setPhone("13002355860");

        final long l = System.currentTimeMillis();
        final String test = testRemote.test("1");
        final long l2 = System.currentTimeMillis();
        System.out.println(l2-l);

        final long l3 = System.currentTimeMillis();
        final String test2 = testRemote.test2("1", loginBo);
        final long l4 = System.currentTimeMillis();
        System.out.println(l4-l3);



        final ResponseEntity<LoginEntity> login = jsonTest.login(loginBo);
        System.out.println(login.getResult());
    }
}
