package com.U100.foj.judge.codesandbox;

import com.U100.foj.judge.codesandbox.model.ExecuteCodeRequest;
import com.U100.foj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
