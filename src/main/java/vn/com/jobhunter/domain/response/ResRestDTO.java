package vn.com.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResRestDTO<T> {

    private int statusCode;
    private String error;

    // message can be string, or arrayList
    private Object message;
    private T data;
}
