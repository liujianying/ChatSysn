package moe.chionlab.wechatmomentstat.Model;

import java.io.Serializable;
import java.util.HashSet;

/**
 * 上传微信朋友圈数据的结果类
 *
 * @author 杨情红
 */
public class UploadResult implements Serializable {

    /***
     * 上传成功的数据ID集合
     */
    public HashSet<String> success;
    /****
     * 上传失败的数据ID集合
     */
    public HashSet<String> failure;

    public UploadResult() {
    }

    public HashSet<String> getSuccess() {
        return success;
    }

    public void setSuccess(HashSet<String> success) {
        this.success = success;
    }

    public HashSet<String> getFailure() {
        return failure;
    }

    public void setFailure(HashSet<String> failure) {
        this.failure = failure;
    }

    @Override
    public String toString() {
        return "UploadResult{" +
                "success=" + success +
                ", failure=" + failure +
                '}';
    }
}
