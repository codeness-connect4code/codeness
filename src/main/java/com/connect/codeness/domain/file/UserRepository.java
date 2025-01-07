package com.connect.codeness.domain.file;

import com.connect.codeness.domain.user.User;
import java.io.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

}
