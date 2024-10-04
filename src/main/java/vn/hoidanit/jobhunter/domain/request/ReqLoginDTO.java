package vn.hoidanit.jobhunter.domain.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class ReqLoginDTO {

    @NotBlank(message = "Tên tài khoản không được để trống")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

}
