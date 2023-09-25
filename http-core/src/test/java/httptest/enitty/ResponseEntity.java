package httptest.enitty;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一返回数据结构
 * @author fishlikewater@126.com
 * @version V1.0.0
 * @since 2021年12月12日 10:19
 */

@Data
public class ResponseEntity<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -6900139280435767733L;

    protected String code;

    protected String message;

    private T result;

    private String requestId;

}
