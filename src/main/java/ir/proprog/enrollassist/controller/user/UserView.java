package ir.proprog.enrollassist.controller.user;

import ir.proprog.enrollassist.domain.user.User;
import lombok.Getter;

@Getter
public class UserView {
    private Long id;
    private String userId;
    private String name;

    public UserView() {
    }

    public UserView(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.name = user.getName();
    }

}
