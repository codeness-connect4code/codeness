package com.connect.codeness.domain.user;


import com.connect.codeness.domain.file.ImageFile;
import com.connect.codeness.domain.user.dto.UserUpdateRequestDto;
import com.connect.codeness.global.entity.BaseEntity;
import com.connect.codeness.global.enums.FieldType;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.enums.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity(name = "user")
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "email", unique = true, nullable = false)
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@Column(nullable = false)
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String name;

	@Column(nullable = false)
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String userNickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus userStatus;

	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String phoneNumber;

	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String region;

	private int career;

	@Size(max = 5, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String mbti;

	@Size(max = 100, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String site_link;

	//사용자 계좌
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String account;

	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String bankName;

	@Size(max = 100, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String firebaseUserId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FieldType field;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ImageFile> imageFiles = new ArrayList<>();

	@Builder
	public User(String email, String password,String name, String userNickname, String phoneNumber, FieldType field, UserRole role) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.userNickname = userNickname;
		this.phoneNumber = phoneNumber;
		this.field = field;
		this.role = role;
		this.userStatus = UserStatus.ACTIVE;
	}

	public User() {}

	public void update(UserUpdateRequestDto dto) {
		this.userNickname = dto.getNickname();
		this.phoneNumber = dto.getPhoneNumber();
		this.region = dto.getRegion();
		this.career = dto.getCareer();
		this.mbti = dto.getMbti();
		this.site_link = dto.getSiteLink();
		this.field = dto.getField();
	}

	public void setImageFiles(ImageFile imageFile) {
		this.imageFiles.add(imageFile);
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
