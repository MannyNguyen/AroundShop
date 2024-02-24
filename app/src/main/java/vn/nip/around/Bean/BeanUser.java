package vn.nip.around.Bean;

import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Helper.StringHelper;

/**
 * Created by viminh on 10/17/2016.
 */

public class BeanUser extends BeanBase {
    private String phone;
    private String deviceid;
    private String devicetoken;
    private String otp;
    private String fullname;
    private String avatar;
    private String token;
    private String version;
    private String password;
    private String countryCode;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getDevicetoken() {
        return devicetoken;
    }

    public void setDevicetoken(String devicetoken) {
        this.devicetoken = devicetoken;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getFullName() {
        return fullname;
    }

    public void setFullName(String fullName) {
        this.fullname = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPassword() {
        return password;
    }

    public String getMD5Password(){
        if(StringHelper.isNullOrEmpty(password)){
            return "";
        }
        return CmmFunc.MD5(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
