package com.connect.codeness.domain.file;

import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.entity.BaseEntity;
import com.connect.codeness.global.enums.FileCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "file")
public class ImageFile extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private String fileName;

	@Column(nullable = false)
	private String fileType;

	@Column(nullable = false)
	private Long fileSize;

	@Column(nullable = false)
	private String filePath;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private FileCategory category;

	public ImageFile() {
	}

	@Builder
	public ImageFile(User user, String fileName, String fileType, Long fileSize, String filePath,
		FileCategory category) {
		this.user = user;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileSize = fileSize;
		this.filePath = filePath;
		this.category = category;
	}
}