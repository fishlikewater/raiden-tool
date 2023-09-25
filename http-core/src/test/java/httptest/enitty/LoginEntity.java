package httptest.enitty;

import lombok.Data;

/**
 * @author fishlikewater@126.com
 * @since 2023年09月25日 11:37
 **/
@Data
public class LoginEntity {

    private String haoPengUserId;

    private String token;

    private String refresh_token;

    private String phone;


}
