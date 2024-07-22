package com.viewmore.poksin.entity;

import com.viewmore.poksin.dto.UpdateUserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends MainUserEntity{

    // 전화번호
    private String phoneNum;
    // 긴급 연락처
    private String emergencyNum;
    // 주소
    private String address;
    // 전화번호 공개 비공개 여부
    private boolean phoneOpen;
    // 긴급 연락처 공개 비공개 여부
    private boolean emergencyOpen;
    // 주소 공개 비공개 여부
    private boolean addressOpen;
    // admin 계정인가?
    private String role;

    @Builder(builderMethodName = "userEntityBuilder")
    public UserEntity(String username, String password, String phoneNum, String emergencyNum, String address, boolean phoneOpen, boolean emergencyOpen, boolean addressOpen, String role) {
        super(username, password);
        this.phoneNum = phoneNum;
        this.emergencyNum = emergencyNum;
        this.address = address;
        this.phoneOpen = phoneOpen;
        this.emergencyOpen = emergencyOpen;
        this.addressOpen = addressOpen;
        this.role = role;
    }

    @Builder(builderMethodName = "signupBuilder")
    public UserEntity(String username, String password, String role) {
        super(username, password);
        this.role = role;
    }

    public boolean getphoneOpen() {
        return phoneOpen;
    }

    public boolean getEmergencyOpen() {
        return emergencyOpen;
    }

    public boolean getAddressOpen() {
        return addressOpen;
    }

    public void updateUser(UpdateUserDTO updateUserDTO) {
        this.address = updateUserDTO.getAddress() == null ? this.address : updateUserDTO.getAddress();
        this.emergencyNum = updateUserDTO.getEmergencyNum() == null ? this.emergencyNum : updateUserDTO.getEmergencyNum();
        this.phoneNum = updateUserDTO.getPhoneNum() == null ? this.phoneNum : updateUserDTO.getPhoneNum();
        this.phoneOpen = updateUserDTO.getphoneOpen();
        this.emergencyOpen = updateUserDTO.getEmergencyOpen();
        this.addressOpen = updateUserDTO.getAddressOpen();

    }

}
