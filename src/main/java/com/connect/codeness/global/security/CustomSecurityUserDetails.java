package com.connect.codeness.global.security;

import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.enums.UserStatus;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

public class CustomSecurityUserDetails implements UserDetails {

	private final User user;

	public CustomSecurityUserDetails(User user) {
		this.user = user;
	}

	// user의 역할(role)을 반환 (ex. "ROLE_ADMIN" / "ROLE_USER" 등)
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// 사용자 역할을 기반으로 권한 반환
		return Collections.singletonList(() -> "ROLE_" + user.getRole().name());
	}

	// user의 비밀번호 반환
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	// user의 username 반환 (User 엔티티에서 name을 사용)
	@Override
	public String getUsername() {
		return user.getEmail();
	}

	public String getName(){
		return user.getName();
	}

	// 계정 만료 여부 (true이면 계정 만료되지 않음)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// 계정 잠금 여부 (true이면 계정 잠금되지 않음)
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// 자격 증명 만료 여부 (true이면 자격 증명 만료되지 않음)
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// 계정 활성화 여부 (true이면 계정 활성화 상태)
	@Override
	public boolean isEnabled() {
		return user.getUserStatus() == UserStatus.ACTIVE;  // ACTIVE 상태일 경우만 활성화
	}

	// 추가적인 사용자 정보를 필요한 경우 반환할 수 있는 메소드들 (예: phoneNumber, firebaseUserId 등)
	public String getPhoneNumber() {
		return user.getPhoneNumber();
	}

	public String getUserNickname() {
		return user.getUserNickname();
	}

	public String getRegion() {
		return user.getRegion();
	}
}
