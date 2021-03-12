package com.kcbs.webforum.event;

import com.kcbs.webforum.pojo.Result;

public class loginSuccessEvent {
    private Result result;

    public loginSuccessEvent(Result result){
        this.result = result;
    }

    public Result getResult() {
        return result;
    }
}
