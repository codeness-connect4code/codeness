package com.connect.codeness.domain.file;


import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.io.IOException;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class FileServiceImpl implements FileService{

	private final S3Client s3Client;
	private final UserRepository userRepository;
	private final FileRepository fileRepository;

	@Autowired
	public FileServiceImpl(S3Client S3client, UserRepository userRepository, FileRepository fileRepository) {
		this.s3Client = S3client;
		this.userRepository = userRepository;
		this.fileRepository = fileRepository;
	}

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucketName;

	@Value("${spring.cloud.aws.region.static}")
	private String region;

	// 파일 업로드 메서드
	@Override
	public CommonResponseDto createFile(MultipartFile inputFile, Long userId, FileCategory category) throws IOException {

		User user = userRepository.findByIdOrElseThrow(userId);

		// 파일 존재 여부 검증
		if (inputFile == null || inputFile.isEmpty()) {
			throw new BusinessException(ExceptionType.NOT_FOUND_FILE);
		}

		// 파일 확장자 가져오기
		String fileExtension = getFileExtension(inputFile.getOriginalFilename());

		// 파일 확장자 지원 여부 검증
		checkExtension(fileExtension);

		// s3 저장 경로 및 파일 이름 설정
		String folderPath =category.getPath();

		String fileName = userId + "-" + category.getPath() + fileExtension;

		String s3Key = folderPath + "/" + fileName;

		// S3 스토리지에 데이터 업로드
		s3Client.putObject(
			PutObjectRequest.builder()
				.bucket(bucketName)
				.key(s3Key)
				.build(),
			RequestBody.fromInputStream(inputFile.getInputStream(), inputFile.getSize())
		);

		ImageFile imageFile = new ImageFile().builder()
			.user(user)
			.fileName(getFilename(inputFile.getOriginalFilename()))
			.fileType(fileExtension)
			.fileSize(inputFile.getSize())
			.category(category)
			.build();

		fileRepository.save(imageFile);

		// url 반환
		return CommonResponseDto.builder()
			.msg("사진 업로드가 완료되었습니다.")
			.data(getPublicUrl(s3Key))
			.build();
	}

	// 파일 확장자 가져오기
	private String getFileExtension(String originalFilename) {

		// 전체 이름 중 마지막 . 찾기 (확장자의 시작 부분)
		int dotIndex = originalFilename.lastIndexOf(".");

		// 확장자가 없을 경우 예외 발생 (.이 없을 경우)
		if (dotIndex == -1) {
			throw new BusinessException(ExceptionType.NOT_SUPPORT_EXTENSION);
		}

		// 확장자 반환 (.부터 반환)
		return originalFilename.substring(dotIndex);
	}

	// 파일 확장자 지원 여부 검증
	private void checkExtension(String fileExtension) {

		Set<String> supportedExtensions = Set.of(".jpg", ".png", ".jpeg");

		if (!supportedExtensions.contains(fileExtension)) {
			throw new BusinessException(ExceptionType.NOT_SUPPORT_EXTENSION);
		}
	}

	// 파일 확장자 가져오기
	private String getFilename(String originalFilename) {

		// 전체 이름 중 마지막 . 찾기 (확장자의 시작 부분)
		int dotIndex = originalFilename.lastIndexOf(".");

		// 확장자가 없을 경우 예외 발생 (.이 없을 경우)
		if (dotIndex == -1) {
			throw new BusinessException(ExceptionType.NOT_SUPPORT_EXTENSION);
		}

		// 파일이름 반환 (.앞까지 반환)
		return originalFilename.substring(0, dotIndex);
	}

	// url 받기
	private String getPublicUrl(String s3Key){
		return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3Key);
	}
}
